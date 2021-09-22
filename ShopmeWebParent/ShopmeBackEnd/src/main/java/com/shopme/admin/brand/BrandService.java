package com.shopme.admin.brand;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.AbstractBeanDefinitionReader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import com.shopme.common.entity.Brand;

@Service
public class BrandService {

	@Autowired
	BrandRepository repo;
	
	public static final int BRANDS_PER_PAGE = 10;
	
	//method that list all brands
	public List<Brand> listAll(){
		
		return (List<Brand>) repo.findAll();					
	}
	
//	
	//method that list all brands modified to list brands in by name in ascending order
//	public List<Brand> listAll(){
//		
//		Sort sort = Sort.by("name").ascending();
//		return (List<Brand>) repo.findAll(sort);			
//	}
	
//	
//	//method that list brand by Pages 
//	public Page<Brand> listByPage(int pageNum){
//		Pageable pageable = PageRequest.of(pageNum - 1, BRANDS_PER_PAGE  );
//		return repo.findAll(pageable);
//	}
//	
	
	/*
	 * //method that list brand by Pages //modified with sorting field and sorting
	 * direction public Page<Brand> listByPage(int pageNum, String sortField, String
	 * sortDir){
	 * 
	 * Sort sort = Sort.by(sortField);
	 * 
	 * //sort in ascending or descending order if(sortDir == "asc") { sort =
	 * sort.ascending(); }else { sort = sort.descending(); }
	 * 
	 * Pageable pageable = PageRequest.of(pageNum - 1, BRANDS_PER_PAGE ); return
	 * repo.findAll(pageable); }
	 */


	
	//method that list brand by Pages 
	//modified with sorting field and sorting direction
	//modified with search keyword
	public Page<Brand> listByPage(int pageNum, String sortField, String sortDir,String keyword){
		
		Sort sort = Sort.by(sortField);
		
		//sort in ascending or descending order
		if(sortDir.equals("asc")) {
			sort = sort.ascending();
		}else {
			sort = sort.descending();
		}
		
		Pageable pageable = PageRequest.of(pageNum - 1, BRANDS_PER_PAGE, sort);
		
		//checks if keyword is not empty or not null
		if (keyword != null) {
			return repo.findAll(keyword, pageable); //returns the search results if keyword is not null
		}
		
		return repo.findAll(pageable);
	}

	
	
	/*
	 * 
	 * //method that list all brands modified to list brands in by name in ascending
	 * order //modified again with a sortDir as a paramter
	 * 
	 * public List<Brand> listAll(String sortDir){
	 * 
	 * Sort sort = Sort.by("name");
	 * 
	 * if(sortDir == "asc") { sort = sort.ascending(); }else if (sortDir == "desc")
	 * { sort = sort.descending(); } return (List<Brand>) repo.findAll(sort); }
	 * 
	 */	

	
	/*
	 * //method that list all brands modified to list brands in by name in ascending
	 * order //modified again with sortField and sortDir as paramters
	 * 
	 * public List<Brand> listAll(String sortField, String sortDir) {
	 * 
	 * Sort sort = Sort.by(sortField);
	 * 
	 * if (sortDir == "asc") { sort = sort.ascending(); } else { sort =
	 * sort.descending(); } return (List<Brand>) repo.findAll(sort); }
	 */
		
	//method that creates new brand
	public Brand saveBrad(Brand brand) {
		
		return repo.save(brand);
	}
	
	public String checkUnique(Integer id, String name) {
		boolean isCreatingNew = (id == null || id == 0 );
		
		Brand brandName = repo.findByName(name); //finds brand by name
		
		if(isCreatingNew) {
			if(brandName != null) {
				return "Duplicate";
			}
		}else {
		//checks if brandName contains a value and brand ID is not the same as the id of the form
			//being edited
			if(brandName != null && brandName.getId() != id) {
				
				return "Duplicate";
			}
		}
		
		return "OK";
	}
	
	
	//method that gets the id
	public Brand get(Integer id) throws BrandNotFoundException{
		
		try {
			return repo.findById(id).get();
		} catch (Exception e) {
			throw new BrandNotFoundException("Could not find any brand with ID: " + id);
		} 
	}
	
	
	public void delete(Integer id) throws BrandNotFoundException{
		
		Long countById = repo.countById(id);
		
		if(countById == null || countById == 0) {
			throw new BrandNotFoundException("Could not find any brand with ID: " + id);
		}
		
		repo.deleteById(id);
	}
	
}
