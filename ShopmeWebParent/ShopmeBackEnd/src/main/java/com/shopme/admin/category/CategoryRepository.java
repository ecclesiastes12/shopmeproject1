package com.shopme.admin.category;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.shopme.common.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

//	//Query for listing parent(root) categories
//	@Query("SELECT c FROM Category c WHERE c.parent.id is NULL ")
//	public List<Category> findRootCategories();
	
	//Query for listing and sorting parent(root) categories 
	@Query("SELECT c FROM Category c WHERE c.parent.id is NULL ")
	public List<Category> findRootCategories(Sort sort);
	 
	//Query for listing and sorting parent(root) categories by pages
	@Query("SELECT c FROM Category c WHERE c.parent.id is NULL ")
	public Page<Category> findRootCategories(Pageable pageable);
		
	//query for search
	@Query("SELECT c FROM Category c WHERE c.name LIKE %?1%")
	public Page<Category> search(String keyword, Pageable page);
	
	public Category findByName(String name);
	
	public Category findByAlias(String alias);
	
	//method to update category status
	@Query("UPDATE Category c SET c.enabled = ?2 where c.id = ?1")
	@Modifying
	public void updateCategoryEnabledStatus(Integer id, boolean enabled);
	
	//for delete query
	//method to be used to check the existence of  category before deletion
	public Long countById(Integer id);
}
