package com.shopme.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{

	//creates bean for UserDetailsService
	@Bean
	public UserDetailsService userDetailsService() {
		return new ShopmeUserDetailsService();
	}
	
	
	//creates bean for password encoder
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	//method that configures the authentication provider
	//DaoAuthenticationProvider tell spring security that
	//authentication will be base on the database
	public DaoAuthenticationProvider authenticationProvider() {
		
		//creates new DaoAuthenticationProvider object
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		
		//sets userDetailsService for DaoAuthenticationProvider
		authProvider.setUserDetailsService(userDetailsService());
		
		//sets passwordEncoder for DaoAuthenticationProvider
		authProvider.setPasswordEncoder(passwordEncoder());
		
		return authProvider;
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}


//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		http.authorizeRequests()
//		.antMatchers("/users/**","/categories/**","/brands/**","/products/**","/questions/**",
//				"/reviews/**","/customers/**","/shipping/**","/orders/**","/reports/**","/articles/**",
//				"/menu/**","/settings/**").hasAuthority("Admin") // this menu is only visible to Admin users
//		.antMatchers("/categories/**","/brands/**").hasAnyAuthority("Admin","Editor")
//		.antMatchers("/products/**").hasAnyAuthority("Admin","Salesperson","Editor","Shipper")
//		.antMatchers("/questions/**","/reviews/**").hasAnyAuthority("Admin","Assistance")
//		.antMatchers("/customes/**","/shipping/**","/orders/**","/reports/**").hasAnyAuthority("Admin","Salesperson")
//		.antMatchers("/products/**","/orders/**").hasAnyAuthority("Admin","Shipper","Assistant")
//		.antMatchers("/articles/**","/menu/**").hasAnyAuthority("Admin","Editor")
//			.anyRequest().authenticated() 
//			.and()
//			.formLogin()
//				.loginPage("/login")
//				//spring security by default will use username for that parameter
//				//since we use email for the login paramter we have to change customer
//				//parameter name to email 
//				.usernameParameter("email") 
//				.permitAll()
//		.and().logout().permitAll()
//		//for remember me 
//		//the purpose of using key is make the token survive even if
//		//the application is restarted else the user will have to 
//		//login again 
//		.and()
//			.rememberMe()
//				.key("abcdefghijklmnopqrs_1234567890")
//				//expiration time for the token thus 1 week
//				.tokenValiditySeconds(7 * 24 * 60 * 60);
//		
//		
//			
//	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		.antMatchers("/users/**").hasAuthority("Admin") // this menu is only visible to Admin users
		.antMatchers("/categories/**","/brands/**").hasAnyAuthority("Admin","Editor")
		
		.antMatchers("/products/new","/products/delete/**") //url for creating new product and deleting product
			.hasAnyAuthority("Admin","Editor")
			
		.antMatchers("/products/edit/**","/products/save","/products/check_unique") //url for editing,saving and checking product uniqueness
			.hasAnyAuthority("Admin","Editor","Salesperson")
			
		.antMatchers("/products","/products/","/products/detail/**","/products/page/**") //url's for the various pages under products
			.hasAnyAuthority("Admin","Editor","Salesperson","Shipper")
			
		.antMatchers("/products/**").hasAnyAuthority("Admin","Editor")
				/*
				 * .antMatchers("/questions/**","/reviews/**").hasAnyAuthority("Admin",
				 * "Assistance")
				 * .antMatchers("/customes/**","/shipping/**","/orders/**","/reports/**").
				 * hasAnyAuthority("Admin","Salesperson")
				 * .antMatchers("/products/**","/orders/**").hasAnyAuthority("Admin","Shipper",
				 * "Assistant")
				 * .antMatchers("/articles/**","/menu/**").hasAnyAuthority("Admin","Editor")
				 */
			.anyRequest().authenticated() 
			.and()
			.formLogin()
				.loginPage("/login")
				//spring security by default will use username for that parameter
				//since we use email for the login paramter we have to change customer
				//parameter name to email 
				.usernameParameter("email") 
				.permitAll()
		.and().logout().permitAll()
		//for remember me 
		//the purpose of using key is make the token survive even if
		//the application is restarted else the user will have to 
		//login again 
		.and()
			.rememberMe()
				.key("abcdefghijklmnopqrs_1234567890")
				//expiration time for the token thus 1 week
				.tokenValiditySeconds(7 * 24 * 60 * 60);
		
		
			
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		// ignores the authentication of static resources
		web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
	}

	
	
}
