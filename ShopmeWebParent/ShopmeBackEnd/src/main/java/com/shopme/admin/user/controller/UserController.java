package com.shopme.admin.user.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

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
import com.shopme.admin.user.UserNotFoundException;
import com.shopme.admin.user.UserService;
import com.shopme.admin.user.export.UserCsvExporter;
import com.shopme.admin.user.export.UserExcelExporter;
import com.shopme.admin.user.export.UserPdfExporter;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Controller
public class UserController {

	@Autowired
	UserService service;

	//method modified
	
	/*
	 * @GetMapping("/users") public String listAll(Model model) { List<User>
	 * listUsers = service.listAll(); model.addAttribute("listUsers", listUsers);
	 * 
	 * return "users"; }
	 */

	//public String listAll(Model model) changed to listFirstPage and modified 
	//to display list of users in pages
	@GetMapping("/users")
	public String listFirstPage(Model model) {
		
		/**
		 * NB firstName used here is the field name of an entity class in the user class
		 * 
		 * null keyword is pass to the parameter here
		 */
		return listByPage(1,model, "firstName","asc",null);
	}
	
	/**
	 * list user by pagination
	 *@PathVariable is used here to map the page number in the url
	 *@Param is used here to map the values of the parameter in the url thus sortfield and sortdir
	 *
	 *	method updated for search purpose
	 */
	
	@GetMapping("/users/page/{pageNum}")
	public String listByPage(@PathVariable(name ="pageNum") int pageNum ,Model model,
			@Param("sortField") String sortField,
			@Param("sortDir") String sortDir,
			@Param("keyword") String keyword) {
		
		System.out.println("Sort Field: " + sortField);
		System.out.println("Sort Oder: " + sortDir);
		
		Page<User> page = service.listByPage(pageNum,sortField,sortDir,keyword);
		
		//get the content to page
		List<User> listUsers = page.getContent();
		
		/*
		 * System.out.println("pageNum =" + pageNum);
		 * System.out.println("Total elements = " + page.getTotalElements());
		 * System.out.println("Total Pages = " + page.getTotalPages());
		 */
		
		//count pages
		long startCount = (pageNum - 1) * UserService.USERS_PER_PAGE + 1;
		long endCount = startCount + UserService.USERS_PER_PAGE - 1;
		
		//gets the last page number
		if(endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		//sorting
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		
		model.addAttribute("listUsers", listUsers);
		
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword); //display the keyword
		
		return "users/users";
	}
	
	
	@GetMapping("/users/new")
	public String newUser(Model model) {
		List<Role> listRoles = service.listRoles();

		User user = new User();
		user.setEnabled(true);
		model.addAttribute("user", user);
		model.addAttribute("listRoles", listRoles);
		model.addAttribute("pageTitle", "Create New User");

		return "users/user_form";
	}

	//the parameter value of RequestParam used here is the name attribute
	//used in the input tag in the user_form.html
	
	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes, Model model,
			@RequestParam("image") MultipartFile multipartFile) throws IOException {
		
		//check if the form has an upload file or not. thus if multipart is not empty
		//then the form has an upload file
		if(!multipartFile.isEmpty()) {
			//gets the file name
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			
			//sets the file name
			user.setPhotos(fileName);
			
			//saves the data with the name of the image file
			User savedUser = service.save(user);
			
			// directory name with saved user id
			String uploadDir = "user-photos/" + savedUser.getId(); 
			
			//method call. clean the directory before saving the file
			FileUploadUtil.cleanDir(uploadDir);
			
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			
		}else {

			//set photos to null if user photo is empty
			if(user.getPhotos().isEmpty()) user.setPhotos(null);
			
			service.save(user);

		}
		
		
		// success message
		redirectAttributes.addFlashAttribute("message", "The user has been saved succesfully.");

		// redirects to users page
		//return "redirect:/users"; 

		return getRedirectURLtoAffectedUser(user);
	}

	// method to show the edited user on the first page
	private String getRedirectURLtoAffectedUser(User user) {
		String firtsPartOfEmail = user.getEmail().split("@")[0];
		return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firtsPartOfEmail;
	}

	// NB @PathVariable is used here because the id from the url
	@GetMapping("/users/edit/{id}")
	public String editUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		// retrieve the user object by id
		// method call from the service class
		try {
			User user = service.get(id);
			List<Role> listRoles = service.listRoles();
			model.addAttribute("user", user);
			// both works
			// model.addAttribute("user",service.get(id));

			model.addAttribute("pageTitle", "Edit User (ID: " + id + ")");
			model.addAttribute("listRoles", listRoles);
			return "users/user_form";

		} catch (UserNotFoundException ex) {
			// redirectAttributes is for displaying the error message or custome exception
			// created in the UserService class thus UserNotFoundException in the get method
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/users";
	}

	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		// retrieve the user object by id
		// method call from the service class
		try {
			service.delete(id);

			redirectAttributes.addFlashAttribute("message", "The user ID " + id + " has been deleted succesfully");

		} catch (UserNotFoundException ex) {
			// redirectAttributes is for displaying the error message or custome exception
			// created in the UserService class thus UserNotFoundException in the get method
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/users";
	}

	/**
	 * mapping of the id and stutus with the path variables
	 * 
	 * {id} must match with @PathVariable("id")
	 * {status} must match with @PathVariable("status")
	 */
	
	
	@GetMapping("/users/{id}/enabled/{status}")
	public String updateUserEnabledStatus(@PathVariable("id") Integer id,
			@PathVariable("status") boolean enabled,
			RedirectAttributes redirectAttributes) {
		
		service.updateUserEnabledStatus(id, enabled);
		
		String status = enabled ? "enabled" : "disabled";
			
			String message = "The user ID " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		
		return "redirect:/users";
	}

	//for csv
	@GetMapping("/users/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		
		List<User> listUsers = service.listAll();
		
		UserCsvExporter exporter = new UserCsvExporter();
		
		exporter.export(listUsers, response);
		
	}
	
	//for excel
	@GetMapping("/users/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		
		List<User> listUsers = service.listAll();
		
		UserExcelExporter exporter = new UserExcelExporter();
		
		exporter.export(listUsers, response);
		
	}
	
	//for pdf
		@GetMapping("/users/export/pdf")
		public void exportToPdf(HttpServletResponse response) throws IOException {
			
			List<User> listUsers = service.listAll();
			
			UserPdfExporter exporter = new UserPdfExporter();
			
			exporter.export(listUsers, response);
			
		}
	
}
