 package com.shopme.admin.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.shopme.common.entity.User;
/**
 *By default PagingAndSortingRepository extends CrudRepository
 */
public interface UserRepository extends PagingAndSortingRepository<User, Integer>{

	/*NB :email refers to the parameter value in @Param("email") */
	
	@Query("SELECT u FROM User u WHERE u.email = :email")
	public User getUserEmail(@Param("email") String email);
	
	//method to be used to check the existence of  user before deletion
	public Long countById(Integer id);
	
	/**
	 * ?1 means parameter 1
	 * ?2 means parameter 2
	 */
	
	@Query("UPDATE User u SET u.enabled = ?2 WHERE u.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id, boolean enabled );
	
	//method to implement searching functionality on a page thus pagination
	//1. write a method to return the page of the user
	//2. write the search query on the return page
	
	/*
	 * @Query("SELECT u FROM User u WHERE u.firstName LIKE %?1% OR u.lastName LIKE %?1%"
	 * + " OR u.email LIKE %?1%")
	 */
	//better approach
	@Query("SELECT u FROM User u WHERE CONCAT(u.id,' ', u.email, ' ', u.firstName, ' ', u.lastName) LIKE %?1%")
	public Page<User> findAll(String keyword, Pageable pageable);
}
