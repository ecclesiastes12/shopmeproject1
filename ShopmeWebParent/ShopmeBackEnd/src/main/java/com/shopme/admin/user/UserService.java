package com.shopme.admin.user;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Service
@Transactional
public class UserService {

	//constant variable for page size thus total number of list per page
	public static final int USERS_PER_PAGE = 4;
	
	@Autowired
	UserRepository userRepo;

	@Autowired
	RoleRepository roleRepo;

	@Autowired
	PasswordEncoder passwordEncoder;
	
	//method that return user by email
	public User getByEmail(String email) {
		return userRepo.getUserEmail(email);
	}

	// list all users
	public List<User> listAll() {
		//return (List<User>) userRepo.findAll();
		
		//for data export. data will be sorted in ascending order when exported
		return (List<User>) userRepo.findAll(Sort.by("firstName").ascending());
	}

	//method that list users by pages
	/*
	 * public Page<User> listByPage(int pageNum){ Pageable pageable =
	 * PageRequest.of(pageNum - 1, USER_PER_PAGE); return
	 * userRepo.findAll(pageable); }
	 */
	
	/**
	 * method updated for sorting purpose
	 *
	 * method updated with the search keyword. check userService2.java to see the changes
	 */
	public Page<User> listByPage(int pageNum, String sortField, String sortDir,String keyword){
		//sort by field name
		Sort sort = Sort.by(sortField);
		
		//sort in ascending or descending order
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);
		
		//checks if the keyword is not null thus search parameter
		if(keyword != null) {
			return userRepo.findAll(keyword,pageable);
		}
		
		return userRepo.findAll(pageable);
	}
	
	
	// list all roles
	public List<Role> listRoles() {
		return (List<Role>) roleRepo.findAll();
	}

	// saves user
	public User save(User user) {
		// checks if the form is in the edit or new user mode before saving the data
		boolean isUpdatingUser = (user.getId() != null);

		if (isUpdatingUser) {
			//retrieves user details from the database
			User existingUser = userRepo.findById(user.getId()).get();

			//checks if password is empty
			//NB if the password is empty it means user want to maintain the old password
			if(user.getPassword().isEmpty()) {
				//reads password from the database and set it to user object
				user.setPassword(existingUser.getPassword());
			}else {
				//encode the new password if the password field is not empty
				encodePassword(user);
			}
			
		} else {
			encodePassword(user); // method call
		}

		return userRepo.save(user);
	}
	
	//method that update the account of currently logged in user
	public User updateAccount(User userInForm) {
		//returns the user object in the database
		User userInDB = userRepo.findById(userInForm.getId()).get();
		
		//checks if password field on the form is not empty
		if(!userInForm.getPassword().isEmpty()) {
			//sets new password
			userInDB.setPassword(userInForm.getPassword());
			//encode the new password
			encodePassword(userInDB);
			
		}
		
		//checks the value of the photos in the form is not null
		if(userInForm.getPhotos() != null) {
			userInDB.setPhotos(userInForm.getPhotos());
		}
		
		//gets the values of first name and last name of the user in the form
		userInDB.setFirstName(userInForm.getFirstName());
		userInDB.setLastName(userInForm.getLastName());
		
		return userRepo.save(userInDB);
	}

	// encrypts the password
	public void encodePassword(User user) {
		String encodePassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
	}

	// checks if email is unique
	public boolean isEmailUnique(Integer id, String email) {
		User userByEmail = userRepo.getUserEmail(email);

		if (userByEmail == null)
			return true; // if it returns null meaning email is unique in the db

		// if id is null meaning the form is in the new mode
		// if id is not null meaning the form is in the edit mode
		boolean isCreatingNew = (id == null);

		if (isCreatingNew) {
			// if email is not null meaning the email exist in the db
			// therefore the email is not unique. New email must be provided
			if (userByEmail != null)
				return false;
		} else {
			// if the ID of the user find by email is different from the ID
			// of the user being edited then email is not unique
			if (userByEmail.getId() != id) {
				return false;
			}
		}

		return true;
	}

	// method to generate custom error message
	public User get(Integer id) throws UserNotFoundException {
		try {
			return userRepo.findById(id).get();
		} catch (NoSuchElementException ex) {
			// custom exception
			throw new UserNotFoundException("Could not find the user with ID" + id);
		}

	}
	
	//method to delete user
	public void delete(Integer id) throws UserNotFoundException {
		
		Long countById = userRepo.countById(id); //method call
		
		//throws exception if count by Id is null or equal to 0
		// thus user with the given Id does not exist in the db
		if(countById == null || countById == 0) {
			throw new UserNotFoundException("Could not find the user with ID" + id);
		}
		userRepo.deleteById(id);
	}
	
	//method to enabled or disabled user status
	public void updateUserEnabledStatus(Integer id, boolean enabled) {
		userRepo.updateEnabledStatus(id, enabled);
	}
}
