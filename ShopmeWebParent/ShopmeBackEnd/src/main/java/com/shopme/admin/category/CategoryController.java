package com.shopme.admin.category;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.shopme.common.entity.Category;

@Controller
public class CategoryController {

	@Autowired
	CategoryService service;
	

	
//	@GetMapping("/categories")
//	public String listAll(Model model) {
//		
//		List<Category> listCategories = service.listAll();
//		model.addAttribute("listCategories", listCategories);
//		
//		return "categories/categories";
//	}
	
	//listAll method modified with sorted paramter
//	@GetMapping("/categories")
//	public String listAll(@Param("sortDir") String sortDir,Model model) {
//		
//		List<Category> listCategories = service.listAll();
//		model.addAttribute("listCategories", listCategories);
//		
//		return "categories/categories";
//	}
	
	//method to show first page by default
	@GetMapping("/categories")
	public String listFirstPage(@Param("sortDir") String sortDir,Model model) {
		
		return listByPage(1, sortDir, null, model); //method call
	}
	
	//listAll method changed to listByPage and modified
	
	//method to show page by page number
	@GetMapping("/categories/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum,
			@Param("sortDir") String sortDir, 
			@Param("keyword") String keyword,Model model) {
		
		//default sort if the sortDir is equal to null or empty
				if(sortDir == null || sortDir.isEmpty()) {
					sortDir = "asc";
				}
				
				//List<Category> listCategories = service.listAll(sortDir);
				
				//creates CategoryPageInfo object
				CategoryPageInfo pageInfo = new CategoryPageInfo();
				List<Category> listCategories = service.listByPage(pageInfo, pageNum, sortDir, keyword);
				
				long startCount = (pageNum - 1) * CategoryService.ROOT_CATEGORY_PER_PAGE + 1;
				long endCount = startCount + CategoryService.ROOT_CATEGORY_PER_PAGE - 1;
				
				if(endCount > pageInfo.getTotalElements()) {
					endCount = pageInfo.getTotalElements();
				}
				
				//reverse sort
				String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
				
				model.addAttribute("totalPages", pageInfo.totalPages);
				model.addAttribute("totalItems", pageInfo.getTotalElements());
				model.addAttribute("currentPage", pageNum);
				
				model.addAttribute("sortField", "name");
				model.addAttribute("sortDir", sortDir);
				
				model.addAttribute("keyword", keyword);
				
				model.addAttribute("startCount", startCount);
				model.addAttribute("endCount", endCount);
				
				model.addAttribute("listCategories", listCategories);
				model.addAttribute("reverseSortDir", reverseSortDir);
				
				return "categories/categories";
		
	}
	
	
	//method that creates new category
	@GetMapping("/categories/new")
	public String newCategory(Model model) {
		List<Category> listCategories = service.listCategoriesUsedInForm();
		model.addAttribute("category", new Category());
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("pageTitle", "Creates New Category");//page title
		return "categories/category_form";
	}
	
	//the parameter attribute of @RequestParam("fileImage") must always correspond 
	//with the value to the attribute name of the input tag
	@PostMapping("/categories/save")
	public String saveCategory(Category category,
			@RequestParam("fileImage") MultipartFile multipartFile,
			RedirectAttributes ra) throws IOException {
		
		if (!multipartFile.isEmpty()) {
			//grabs the file name of the muiltipart file object
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			//sets the image file name
			category.setImage(fileName);
			//save Category
			Category savedCategory = service.save(category);
			//create image upload directory (folder) by using the 
			//ID of the saved category
			//../category-images/ will be created outside project directory
			String uploadDir = "../category-images/" + savedCategory.getId();
			
			// clean image directory
			FileUploadUtil.cleanDir(uploadDir);
			
			//save the image file
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			
			
		}else {
			service.save(category);
		}
		
		ra.addFlashAttribute("message", "The category has been saved successfully.");
		
		return "redirect:/categories";
	}
	
	@GetMapping("/categories/edit/{id}")
	public String editCategory(@PathVariable(name = "id") Integer id, 
			Model model, RedirectAttributes redirectAttributes) {
		
		try {
			Category category = service.get(id);
			List<Category> listCategories= service.listCategoriesUsedInForm(); 
			model.addAttribute("category", category);
			model.addAttribute("listCategories", listCategories);
			model.addAttribute("pageTitle", "Edit Category ID (" + id + ")");
		
			return "categories/category_form";
			
		} catch (CategoryNotFoundException ex) {
			// redirectAttributes is for displaying the error message or custome exception
			// created in the CategoryService class thus CategoryNotFoundException in the get method
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/categories";
		}
		
		
	}
	
	@GetMapping("/categories/{id}/enabled/{status}")
	public String updateCategoryEnabledStatus(@PathVariable(name = "id") Integer id,
			@PathVariable(name = "status") boolean enabled,
			RedirectAttributes redirectAttributes) {
		
		service.updateCategoryEnabledStatus(id, enabled);
		/*
		 * String status = "";
		 * 
		 * if(enabled) { status += "enabled"; }else { status += "disabled"; }
		 */
		
		//better way of writing the above commented out code
		
		String status = enabled ? "enabled" : "disabled";
		
		redirectAttributes.addFlashAttribute("message","Category with ID " + id + " is " + status + " succussfully");
		
		return "redirect:/categories";
	}
	
	//delete category
	@GetMapping("/categories/delete/{id}")
	public String deteleCategory(@PathVariable(name = "id") Integer id,
			RedirectAttributes redirectAttributes) {
		
		try {
			service.delete(id);
			String categoryDir = "../category-images/" + id; //directory folder path
			FileUploadUtil.removeDir(categoryDir);//delete image directory  
			
			redirectAttributes.addFlashAttribute("message", "The category ID " + id + " has been deleted succesfully");
		} catch (CategoryNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		
		return "redirect:/categories";
	}
	
	
	@GetMapping("/categories/export/csv")
	public void exportToCsv(HttpServletResponse response) throws IOException {
		
		List<Category> listCategories = service.listCategoriesUsedInForm();
		
		CategoryCSVExporter exporter = new CategoryCSVExporter();
		exporter.export(listCategories, response);
	}
}
