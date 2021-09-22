package com.shopme.admin.product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.brand.BrandService;
import com.shopme.admin.category.CategoryService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.ProductImage;



@Controller
@Transactional
public class ProductController {

	private static final Logger  LOGGER= LoggerFactory.getLogger(ProductController.class);
	
	@Autowired
	ProductService productService;
	
	@Autowired
	BrandService brandService;
	
	@Autowired
	CategoryService categoryService;

	
	/*
	 * @GetMapping("/products") public String listAll(Model model) {
	 * 
	 * List<Product> listProducts = productService.listAll();
	 * 
	 * model.addAttribute("listProducts", listProducts);
	 * 
	 * return "products/products"; }
	 */
	
	@GetMapping("/products")
	public String listByFirstPage(Model model) {
		
		return listByPage(1, model, "name", "asc", null,0);	
	}
	
	@GetMapping("/products/page/{pageNum}")
	public String listByPage(@PathVariable(name="pageNum") int pageNum, Model model,
			@Param("sortField") String sortField,
			@Param("sortDir") String sortDir,
			@Param("keyword") String keyword,
			@Param("categoryId") Integer categoryId //categoryId refers to the name="categoryId" in product.html
			){
		
		
		//updated with categoryId for dropdown search
		Page<Product> page = productService.listByPage(pageNum, sortField, sortDir,keyword,categoryId);
		
		List<Product> listProducts = page.getContent();
		
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();
		
		// page counter
		long startCount = (pageNum - 1) * ProductService.PRODUCT_PER_PAGE + 1;
		long endCount = startCount + ProductService.PRODUCT_PER_PAGE - 1;
		
		//gets the last page number
		if(endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		//reverse sorting
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
				
		//set category id if it is not null
		if(categoryId != null) model.addAttribute("categoryId", categoryId);
		
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("listCategories", listCategories);
		
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword); //display the keyword 
		
		return "products/products";
	}

	
	
	@GetMapping("/products/new")
	public String newProduct(Model model) {
		
		List<Brand> listBrands = brandService.listAll();
		
		
		Product product = new Product();
		
		//sets enable and instock to true for default value
		product.setEnabled(true);
		product.setInStock(true);
		
		model.addAttribute("product", product);
		model.addAttribute("listBrands", listBrands);
		
		model.addAttribute("numberOfExistingExtraImages", 0);
		
		model.addAttribute("pageTitle", "Create New Product");
		
		
		return "products/product_form";
	}
	
	@PostMapping("/products/save")
	public String saveProduct(Product product, 
			RedirectAttributes ra,
			@RequestParam("fileImage") MultipartFile mainImageMultiparts,
			@RequestParam("extraImage") MultipartFile[] extraImageMultiparts,
			@RequestParam(name = "detailNames", required = false) String[] detailNames,
			@RequestParam(name = "detailIDs", required = false) String[] detailIDs,
			@RequestParam(name = "detailValues", required = false) String[] detailValues,
			@RequestParam(name = "imageIDs", required = false) String[] imageIDs,
			@RequestParam(name = "imageNames", required = false) String[] imageNames
			) throws IOException {
		
		setMainImageName(mainImageMultiparts, product); //method call
		setExistingExtraImageNames(imageIDs, imageNames, product);
		setNewExtraImageNames(extraImageMultiparts, product); //method call
		setProductDetails(detailIDs, detailNames,detailValues,product);
		
		
		
		Product savedProduct = productService.save(product);
		
		//method call that save the uploaded images
		saveUploadedImages(mainImageMultiparts,extraImageMultiparts,savedProduct);
		
		//delete extra images that were removed on form
		deleteExtraImagesWereRemovedOnForm(product);
			
		ra.addFlashAttribute("message", "The product has been saved successfully.");
		
		return "redirect:/products";
	}
	
	//function to remove extra images removed on form. That is when user change 
	// or remove an existing image
	private void deleteExtraImagesWereRemovedOnForm(Product product) {
		// directory path
		String extraImageDir = "../product-images/"+ product.getId() + "/extras";
		Path dirPath = Paths.get(extraImageDir);
		
		try {
			// loops through each extra image file
			Files.list(dirPath).forEach(file -> {
				//gets file name
				String filename = file.toFile().getName();
				
				//checks if product does not contains the file name
				if(!product.containsImageName(filename)) {
					try {
						Files.delete(file);
						LOGGER.info("Delete extra image: " + filename);
						
					} catch (Exception e) {
						LOGGER.error("Could not delete extra image" + filename);
					}
				}
			});
		} catch (Exception e) {
			LOGGER.error("Could not list directory" + dirPath);
		}
		
		
	}

	//method to set id's and names of existing extra images
	private void setExistingExtraImageNames(String[] imageIDs, String[] imageNames, 
			Product product) {
		//checks if the array of imageIDs is null or length of the array is zero
		if(imageIDs == null || imageIDs.length == 0) return;
		
		Set<ProductImage> images = new HashSet<>();
		
		//loops throught the array of images
		for(int count = 0; count < imageIDs.length; count++) {
			//grabs  the id of the array of images and convert it into integer
			Integer id = Integer.parseInt(imageIDs[count]);
			
			//grabs the image name from the array at index count
			String name = imageNames[count];
			
			images.add(new ProductImage(id, name, product));
		}
		
		product.setImages(images);
	}

	//method that set product details
	private void setProductDetails(String[] detailIDs, String[] detailNames, String[] detailValues, Product product) {
		// check if there is no null elements in the array of product details
		//or the length of the array is not equal to zero
		if(detailNames == null || detailNames.length == 0) return ;
		
		//iterate through each detailNames in the array
		for(int count = 0; count < detailNames.length; count++) {
			
			//gets the names and values in the array at index count
			String name = detailNames[count];
			String value = detailValues[count];
			Integer id = Integer.parseInt(detailIDs[count]);
			
			//checks if id is not zero or id exist
			if(id != 0) {
				product.addDetail(id,name,value);
			}else if(!name.isEmpty() && !value.isEmpty()) {//checks if name and value is not empty
				//adds the product details to the products
				product.addDetail(name, value);
			}
			
		}
		
		
	}

	//method that save main images
	private void saveUploadedImages(MultipartFile mainImageMultipart, MultipartFile[] extraImageMultiparts,
			Product savedProduct) throws IOException {
		//check if imagepath is not empty. if not empty it means the form is in the edit mode
		if(!mainImageMultipart.isEmpty()) {
			
			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
			
			String uploadDir = "../product-images/" + savedProduct.getId();
			
			FileUploadUtil.cleanDir(uploadDir);
			
			FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);
		}
		
		// save extra images
		//loops through the extraimage if the length is more than o
		if(extraImageMultiparts.length > 0) {
			
			//create directory for extra images
			String uploadDir = "../product-images/" + savedProduct.getId() + "/extras";
			for(MultipartFile multipartFile : extraImageMultiparts) {
				//checks if multipartFile is empty
				if(multipartFile.isEmpty()) {
					// continue keyword used here causes the loop to jump to the next 
					//iteration if multipartFile is empty
					continue; 
				}
				
				//grabs the file name
				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
				
				//save the upload file to the image directory
				FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			}
		}
		
	}

	//method to set main image name
	private void setMainImageName(MultipartFile mainImageMultiparts, Product product) {
		if(!mainImageMultiparts.isEmpty()) {
			
			String fileName = StringUtils.cleanPath(mainImageMultiparts.getOriginalFilename());
			
			product.setMainImage(fileName);
		}
	}
	
	//method to set extra image names
	private void setNewExtraImageNames(MultipartFile[] extraImageMultiparts, Product product) {
		//loops through the extraimage if the length is more than o
		if(extraImageMultiparts.length > 0) {
			for(MultipartFile multipartFile : extraImageMultiparts) {
				//checks if multipartFile is not empty
				if(!multipartFile.isEmpty()) {
					//grabs the file name
					String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
					
					//checks if product does not contain imageName
					if(!product.containsImageName(fileName)) {
						// adds the file name
						product.addExtraImage(fileName);
					}
				}
			}
		}
	}
	@GetMapping("/products/{id}/enabled/{status}")
	public String updateProductEnabledStatus(@PathVariable(name = "id") Integer id, 
		   @PathVariable(name = "status") boolean enabled,
		   RedirectAttributes redirectAttributes) {
		
		String status = "";
		productService.updateProductEnabledStatus(id, enabled);
		
		if(enabled) { status += "enabled"; }else { status += "disabled"; }
		
		redirectAttributes.addFlashAttribute("message", "The product with ID " + id + " is " + status + " successfully");
		
		return "redirect:/products";
	}
	
	@GetMapping("/products/delete/{id}")
	public String delete(@PathVariable(name = "id") Integer id,RedirectAttributes ra) {
	
		try {
			productService.deleteProduct(id);
			/*
			 * String brandDir = "../brand-logos/" + id; FileUploadUtil.removeDir(brandDir);
			 */
			
			String productExtraImagesDir = "../product-images/" + id + "extras";
			String productImagesDir = "../product-images/" + id;
			
			
			FileUploadUtil.removeDir(productExtraImagesDir);
			FileUploadUtil.removeDir(productImagesDir);
			
			ra.addFlashAttribute("message", "The product with ID: (" + id + ") has been deleted succesffully");
		} catch (ProductNotFoundException e) {
			ra.addFlashAttribute("message", e.getMessage());
		}
		
		return "redirect:/products";
	}
	
	
	@GetMapping("/products/edit/{id}")
	public String editProduct(@PathVariable("id") Integer id,
			Model model,
			RedirectAttributes ra) {
		try {
			Product product = productService.get(id);
			List<Brand> listBrands = brandService.listAll();
			Integer numberOfExistingExtraImages = product.getImages().size();
			
			model.addAttribute("product", product);
			model.addAttribute("listBrands", listBrands);
			model.addAttribute("pageTitle", "Edit Product (ID: " + id + ")");
			model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);
			
			return "products/product_form";
			
		} catch (ProductNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			
			return "redirect:/products";
		}
	}
	
	
	@GetMapping("/products/detail/{id}")
	public String viewProductDetails(@PathVariable("id") Integer id,
			Model model,
			RedirectAttributes ra) {
		try {
			Product product = productService.get(id);
			
			model.addAttribute("product", product);
		
			return "products/product_detail_modal";
			
		} catch (ProductNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			
			return "redirect:/products";
		}
	}
	
}
