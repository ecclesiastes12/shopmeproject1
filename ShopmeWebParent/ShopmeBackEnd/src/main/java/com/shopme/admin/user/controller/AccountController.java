package com.shopme.admin.user.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.admin.user.UserService;
import com.shopme.common.entity.User;

@Controller
public class AccountController {

	@Autowired
	UserService service;
	
	/**
	 * NB Before the user can update his or her account you first
	 * has to obtain the object of the current logged in user.
	 * This is can be done via @AuthenticationPrincipal followed by
	 * the type of class that implements the UserDetails Interface.
	 * Followed by any object name of your choice. eg loggedUser
	 * Here ShopmeUserDetails is the class that implements UserDetails Interface.
	 */
	
	@GetMapping("/account")
	public String viewDetails(@AuthenticationPrincipal ShopmeUserDetails loggedUser,
			Model model) {
		
		String email = loggedUser.getUsername(); // returns the email. check ShopmeUserDetails class
		User user = service.getByEmail(email);//retrieve email of current logged in user from the db
		model.addAttribute("user", user);
		
		return "users/account_form";
	}
	
	/**
	 * NB @AuthenticationPrincipal ShopmeUserDetails loggedUser
	 * is used here because the user name that is displayed in the 
	 * menu bar also have to be update else the displayed logged in user name
	 * wont update
	 */
	@PostMapping("/account/update")
	public String saveUser(User user, RedirectAttributes redirectAttributes, Model model,
			@AuthenticationPrincipal ShopmeUserDetails loggedUser,
			@RequestParam("image") MultipartFile multipartFile) throws IOException {
		
		//check if the form has an upload file or not. thus if multipart is not empty
		//then the form has an upload file
		if(!multipartFile.isEmpty()) {
			//gets the file name
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			
			//sets the file name
			user.setPhotos(fileName);
			
			//saves the data with the name of the image file
			User savedUser = service.updateAccount(user);
			
			// directory name with saved user id
			String uploadDir = "user-photos/" + savedUser.getId(); 
			
			//method call. clean the directory before saving the file
			FileUploadUtil.cleanDir(uploadDir);
			
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			
		}else {

			//set photos to null if user photo is empty
			if(user.getPhotos().isEmpty()) user.setPhotos(null);
			
			service.updateAccount(user);

		}
		
		//without this code the user name the menu bar will not 
		//update
		loggedUser.setFirstName(user.getFirstName());
		loggedUser.setLastName(user.getLastName());

		// success message
		redirectAttributes.addFlashAttribute("message", "Your account details has been updated");
		return "redirect:/account";
	}
}
