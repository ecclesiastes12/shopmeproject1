package com.shopme.admin.brand;

import java.io.IOException;
import java.util.List;

import javax.persistence.Id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
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
import com.shopme.admin.category.CategoryService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@Controller
public class BrandController {

	@Autowired
	BrandService service;
	
	@Autowired
	CategoryService categoryService;
	
//	@GetMapping("/brands")
//	public String listAllBrands(Model model) {
//		List<Brand> listBrands = service.listAll();
//		model.addAttribute("listBrands", listBrands);
//		
//		return "brands/brands";
//	}
//	

	//listAllBrands change to listFirstPage and modified
	@GetMapping("/brands")
	public String listAllBrands(Model model) {
		
		//method call
		
		return listByPage(1, model, "name", "asc", null); //sorted in ascending order
		
	}
	
	
//	
//	@GetMapping("/brands/page/{pageNum}")
//	public String listByPage(@PathVariable(name="pageNum") int pageNum, Model model) {
//		
//		Page<Brand> page = service.listByPage(pageNum);
//		
//		List<Brand> ListBrands = page.getContent();
//		
//		// page counter
//		long startCount = (pageNum - 1) * BrandService.BRANDS_PER_PAGE + 1;
//		long endCount = startCount + BrandService.BRANDS_PER_PAGE - 1;
//		
//		//gets the last page number
//		if(endCount > page.getTotalElements()) {
//			endCount = page.getTotalElements();
//		}
//		
//		model.addAttribute("listBrands", ListBrands);
//		
//		model.addAttribute("currentPage", pageNum);
//		model.addAttribute("totalPages", page.getTotalPages());
//		model.addAttribute("totalItems", page.getTotalElements());
//		model.addAttribute("startCount", startCount);
//		model.addAttribute("endCount", endCount);
//		
//		return "brands/brands";
//	}
	
	
	/*
	 * //method modified with sort field and sort direction
	 * 
	 * @GetMapping("/brands/page/{pageNum}") public String
	 * listByPage(@PathVariable(name="pageNum") int pageNum, Model model,
	 * 
	 * @Param("sortField") String sortField,
	 * 
	 * @Param("sortDir") String sortDir) {
	 * 
	 * Page<Brand> page = service.listByPage(pageNum, sortField, sortDir);
	 * 
	 * List<Brand> ListBrands = page.getContent();
	 * 
	 * // page counter long startCount = (pageNum - 1) *
	 * BrandService.BRANDS_PER_PAGE + 1; long endCount = startCount +
	 * BrandService.BRANDS_PER_PAGE - 1;
	 * 
	 * //gets the last page number if(endCount > page.getTotalElements()) { endCount
	 * = page.getTotalElements(); }
	 * 
	 * //reverse sorting String reverseSortDir = sortDir.equals("asc") ? "desc" :
	 * "asc";
	 * 
	 * model.addAttribute("listBrands", ListBrands);
	 * 
	 * model.addAttribute("currentPage", pageNum); model.addAttribute("totalPages",
	 * page.getTotalPages()); model.addAttribute("totalItems",
	 * page.getTotalElements()); model.addAttribute("startCount", startCount);
	 * model.addAttribute("endCount", endCount);
	 * 
	 * model.addAttribute("sortField", sortField); model.addAttribute("sortDir",
	 * sortDir); model.addAttribute("reverseSortDir", reverseSortDir);
	 * 
	 * return "brands/brands"; }
	 */
	
	
	//method modified with sort field and sort direction
	//modified with keyword
	@GetMapping("/brands/page/{pageNum}")
	public String listByPage(@PathVariable(name="pageNum") int pageNum, Model model,
			@Param("sortField") String sortField,
			@Param("sortDir") String sortDir,
			@Param("keyword") String keyword) {
		
		Page<Brand> page = service.listByPage(pageNum, sortField, sortDir,keyword);
		
		List<Brand> ListBrands = page.getContent();
		
		// page counter
		long startCount = (pageNum - 1) * BrandService.BRANDS_PER_PAGE + 1;
		long endCount = startCount + BrandService.BRANDS_PER_PAGE - 1;
		
		//gets the last page number
		if(endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		//reverse sorting
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
				
		model.addAttribute("listBrands", ListBrands);
		
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword); //display the keyword 
		
		return "brands/brands";
	}


	
	
	/*
	 * //listAllBrands modified with sortDir as a paramter
	 * 
	 * @GetMapping("/brands") public String listAllBrands(Model model,
	 * 
	 * @Param("sortField") String sortField,
	 * 
	 * @Param("sortDir") String sortDir) {
	 * 
	 * 
	 * List<Brand> listBrands = service.listAll("name","asc");
	 * 
	 * //reverse sorting //String reverseSortDir = sortDir.equals("asc") ? "desc" :
	 * "asc";
	 * 
	 * model.addAttribute("listBrands", listBrands);
	 * 
	 * 
	 * 
	 * model.addAttribute("sortField", sortField); model.addAttribute("sortDir",
	 * sortDir); //model.addAttribute("reverseSortDir", reverseSortDir);
	 * 
	 * return "brands/brands"; }
	 */
	
	@GetMapping("/brands/new")
	public String newBrand(Model model) {
		Brand brand = new Brand();
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("brand", brand);	
		model.addAttribute("pageTitle", "Create New Brand");	
		return "brands/brand_form";
	}
	
	
	@PostMapping("/brands/save")
	public String saveBrand(Brand brand,
			RedirectAttributes redirectAttributes,
			@RequestParam("image") MultipartFile multipartFile) throws IOException {
		
		//check if imagepath is not empty. if not empty it means the form is in the edit mode
		if(!multipartFile.isEmpty()) {
			
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			
			brand.setLogo(fileName);
			
			Brand savedBrand = service.saveBrad(brand);
			
			String uploadDir = "../brand-logos/" + savedBrand.getId();
			
			FileUploadUtil.cleanDir(uploadDir);
			
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			
		}else {
			
			service.saveBrad(brand);
			
		}
		
		redirectAttributes.addFlashAttribute("message","The Brand has been saved successfully");
		return "redirect:/brands";
		
	}
	
	@GetMapping("/brands/edit/{id}")
	public String editBrand(@PathVariable(name = "id") Integer id,Model model,
			RedirectAttributes redirectAttributes) {
		
		try {
			Brand brand = service.get(id);
		
			List<Category> listCategories = categoryService.listCategoriesUsedInForm();
			model.addAttribute("listCategories", listCategories);
			model.addAttribute("brand", brand);
	
			model.addAttribute("pageTitle", "Edit Brand ID: (" + id + ")");
		
			return "brands/brand_form";
		
			
		} catch (BrandNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/brands";
		}
		
		
	}
	
	
	@GetMapping("/brands/delete/{id}")
	public String deleteBrand(@PathVariable(name = "id") Integer id,RedirectAttributes ra) {
		
		try {
			service.delete(id);
			String brandDir = "../brand-logos/" + id;
			FileUploadUtil.removeDir(brandDir);
			
			ra.addFlashAttribute("message", "The brand with ID: (" + id + ") has been deleted succesffully");
		} catch (BrandNotFoundException e) {
			ra.addFlashAttribute("message", e.getMessage());
		}
		
		return "redirect:/brands";
	}
	
}
