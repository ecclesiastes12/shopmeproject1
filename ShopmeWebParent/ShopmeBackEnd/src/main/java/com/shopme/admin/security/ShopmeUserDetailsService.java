package com.shopme.admin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.shopme.admin.user.UserRepository;
import com.shopme.common.entity.User;

public class ShopmeUserDetailsService implements UserDetailsService {

	@Autowired
	UserRepository userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		//method call from user repository
		User user = userRepo.getUserEmail(email);
		
		//checks if user is not null
		if(user != null) {
			return new ShopmeUserDetails(user);
		}
		//throws error message if user email is not found
		throw new UsernameNotFoundException("Could not find user with email: " + email);
	}

}
