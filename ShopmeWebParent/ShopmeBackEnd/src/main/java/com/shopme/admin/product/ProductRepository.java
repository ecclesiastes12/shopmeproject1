package com.shopme.admin.product;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Product;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer>{

	Product findByName(String name);
	Product findByAlias(String alias);	
	
	@Query("UPDATE Product p SET p.enabled = ?2 WHERE p.id = ?1")
	@Modifying
	public void statusEnabled(Integer id, boolean enabled);
	
	Long countById(Integer id);
	
	//search query
	@Query("SELECT p FROM Product p WHERE p.name LIKE %?1% "
			+ "OR p.shortDescription LIKE %?1% "
			+ "OR p.fullDescription LIKE %?1% "
			+ "OR p.brand.name LIKE %?1% "
			+ "OR p.category.name LIKE %?1%")
	public Page<Product> findAll(String keyword, Pageable pageable);
	
	//category search without keyword
	@Query("SELECT p FROM Product p WHERE p.category.id = ?1 "
			+ "OR p.category.allParentIDs LIKE %?2%")
	public Page<Product> findAllInCategory(Integer categoryId, String categoryIdMatch,
			Pageable pageable);
	
	//category search with keyword
	@Query("SELECT p FROM Product p WHERE (p.category.id = ?1 "
			+ "OR p.category.allParentIDs LIKE %?2%) AND "
			+ "(p.name LIKE %?3% "
			+ "OR p.shortDescription LIKE %?3% "
			+ "OR p.fullDescription LIKE %?3% "
			+ "OR p.brand.name LIKE %?3% "
			+ "OR p.category.name LIKE %?3%)")
	public Page<Product> searchInCategory(Integer categoryId,String categoryIdMatch,
			String keyword, Pageable pageable);
}
