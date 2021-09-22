package com.shopme.admin.brand;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;


import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class BrandRepositoryTest {

	@Autowired
	BrandRepository brandRepo;

	@Test
	public void testCreateBrand1() {
		
		
		Category laptops = new Category(6);
		
		Brand acer = new Brand("Acer");
		
		acer.getCategories().add(laptops);
		Brand saveBrand = brandRepo.save(acer);
		
		assertThat(saveBrand).isNotNull();
		assertThat(saveBrand.getId()).isGreaterThan(0);
		
	}
	
	
	  @Test public void testCreateBrand2() {
	  
	  Category cellPhone = new Category(24);
	  Category tablets = new Category(29);
	  Brand Sumsung = new Brand("Sumsung"); 
	  
	  Sumsung.getCategories().add(cellPhone);
	  Sumsung.getCategories().add(tablets);
	  
	  Brand saveBrand = brandRepo.save(Sumsung);
	  
	  assertThat(saveBrand).isNotNull();
	  assertThat(saveBrand.getId()).isGreaterThan(0);
	  }
	 
	@Test
	public void testFindAll() {
		Iterable<Brand> brands = brandRepo.findAll();
		brands.forEach(System.out :: println);
		assertThat(brands).isNotEmpty();
	}
	
	@Test
	public void testGetById() {
		Brand brand = brandRepo.findById(1).get();
		
		assertThat(brand.getName()).isEqualTo("Acer");
	}
	
	@Test
	public void testUpdateBrand() {
		String newName = "Samsung Electronics";
		Brand samsung = brandRepo.findById(3).get();
		
		samsung.setName(newName);
		Brand savedBrand = brandRepo.save(samsung);
		assertThat(savedBrand.getName()).isEqualTo(newName);
	}
	
	
	@Test
	public void testDeleteBrand() {
		
		Integer apple = 2;
		brandRepo.deleteById(apple);
		
		Optional<Brand> result = brandRepo.findById(apple);
		assertThat(result.isEmpty());
		
	}
}
