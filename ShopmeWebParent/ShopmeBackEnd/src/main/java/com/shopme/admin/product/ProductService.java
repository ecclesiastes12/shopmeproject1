package com.shopme.admin.product;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.bouncycastle.asn1.gnu.GNUObjectIdentifiers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Product;

@Service
public class ProductService {

	public static final int PRODUCT_PER_PAGE = 5;
	
	@Autowired
	ProductRepository repo;
	
	//method that list all products
	public List<Product> listAll(){
		return (List<Product>) repo.findAll();
	}
	
//	public Page<Product> listByPage(int pageNum, String sortField, String sortDir,String keyword){
//		
//		Sort sort = Sort.by(sortField);
//		
//		//sort in ascending or descending order
//		if(sortDir.equals("asc")) {
//			sort = sort.ascending();
//		}else {
//			sort = sort.descending();
//		}
//		
//		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCT_PER_PAGE, sort);
//		
//		//checks if keyword is not empty or not null
//		if (keyword != null) {
//			return repo.findAll(keyword, pageable); //returns the search results if keyword is not null
//		}
//		
//		return repo.findAll(pageable);
//	}
//	
	
	//method that list product by page updated with categoryId for search purpose
	public Page<Product> listByPage(int pageNum, String sortField, String sortDir,
		String keyword, Integer categoryId){
		
		Sort sort = Sort.by(sortField);
		
		//sort in ascending or descending order
		/*
		 * if(sortDir.equals("asc")) { sort = sort.ascending(); }else { sort =
		 * sort.descending(); }
		 */
		
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCT_PER_PAGE, sort);
//		
//		//checks if keyword is not empty or not null
//		if (keyword != null && !keyword.isEmpty()) {
//			return repo.findAll(keyword, pageable); //returns the search results if keyword is not null
//		}
		

		//category search with keyword
		//checks if keyword is not empty or not null
		if (keyword != null && !keyword.isEmpty()) {
			
			//checks if categoryId is not null or greater than zero
			if(categoryId != null && categoryId > 0) {
				//create categoryIdMatch
				String categoryMatch = "-" + String.valueOf(categoryId) + "-";
				return repo.searchInCategory(categoryId, categoryMatch, keyword,pageable);
			}
			
			return repo.findAll(keyword, pageable); //returns the search results if keyword is not null
		}
		
		
		//category search without keyword
		//checks if categoryId is not null or greater than zero
		if(categoryId != null && categoryId > 0) {
			//create categoryIdMatch
			String categoryMatch = "-" + String.valueOf(categoryId) + "-";
			return repo.findAllInCategory(categoryId, categoryMatch, pageable);
		}
		
		return repo.findAll(pageable);
	}
	
	
	//method that save product
	public Product save(Product product) {
		//set created time if product id is null
		if(product.getId() == null) {
			product.setCreatedTime(new Date());
		}
		
		//set default alias if alias is null or empty
		if(product.getAlias() == null || product.getAlias().isEmpty()) {
			
			String defaultAlias = product.getName().replaceAll(" ", "-");
			product.setAlias(defaultAlias);
			
		}else {
			product.setAlias(product.getAlias().replaceAll(" ", "-"));
		}
		
		product.setUpdatedTime(new Date());
		
		return repo.save(product);
		
	}
	
	//method that check product uniqueness
	public String checkUniqueProduct(Integer id, String name) {
		
		boolean isCreatingNew = (id == null || id == 0 );
		
		Product productName = repo.findByName(name);
		
		
		if(isCreatingNew) {
			if(productName != null) {
				return "Duplicate";
			}
		}else {
			
			if(productName != null && productName.getId() != id) {
				return "Duplicate";
			}
		}
		
		return "OK";
		
	}
	
	
	//method that enabled and disabled product status
	public void updateProductEnabledStatus(Integer id, boolean enabled) {
		
		 repo.statusEnabled(id, enabled);
	}
	
	
	//method that delete product
	public void deleteProduct(Integer id) throws ProductNotFoundException {
        Long countById = repo.countById(id);
		
		if(countById == null || countById == 0) {
			throw new ProductNotFoundException("Could not find any product with ID: " + id);
		}
		
		repo.deleteById(id);
	}
	
	//method to find product by id
	public Product get(Integer id) throws ProductNotFoundException{
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new ProductNotFoundException("Could not find any product with ID " + id);
		}
	}
}
