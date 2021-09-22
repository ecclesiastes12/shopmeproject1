package com.shopme.admin.brand;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.Brand;

@Repository
public interface BrandRepository extends PagingAndSortingRepository<Brand, Integer> {

	Brand findByName(String name);
	
	Long countById(Integer id);
	
	@Query("SELECT b FROM Brand b WHERE b.name like %?1%")
	Page<Brand> findAll(String keyword, Pageable pageable);
	
	/*
	 * Custom query to select brand id and brand name
	 * 
	 * Brand(b.id, b.name) refers to the constructor in the Brand class
	 * that takes to parameters thus id and name.
	 * This style is called projection in JPA
	 */
	@Query("SELECT NEW Brand(b.id, b.name) FROM Brand b ORDER BY b.name ASC")
	List<Brand> findAll();
	
}
