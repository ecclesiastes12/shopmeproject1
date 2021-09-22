package com.shopme.admin.brand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;
import static org.hamcrest.CoreMatchers.nullValue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.shopme.common.entity.Brand;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class BrandServiceTest {

	@Mock
	BrandRepository repository;
	
	@InjectMocks
	BrandService service;
	
	@Test
	public void testCheckUniqueInNewModeReturnDuplicate() {
		Integer id = null;
		String name = "Acer";
		
		Brand brand = new Brand(id,name); 
		
		
		Mockito.when(repository.findByName(name)).thenReturn(brand);
		
		String result = service.checkUnique(id, name);
		
		assertThat(result).isEqualTo("Duplicate");
	}
	
	@Test
	public void testCheckUniqueInNewModeReturnOK() {
		Integer id = null;
		String name = "AMD";
		
		Brand brand = new Brand(id,name); 
		
		
		Mockito.when(repository.findByName(name)).thenReturn(null);
		
		String result = service.checkUnique(id, name);
		
		assertThat(result).isEqualTo("OK");
	}
	
	
	@Test
	public void testCheckUniqueInEditModeReturnDuplicate() {
		Integer id = 1;
		String name = "Acer";
		
		Brand brand = new Brand(2,name); 
		
		
		Mockito.when(repository.findByName(name)).thenReturn(brand);
		
		String result = service.checkUnique(5, name);
		
		assertThat(result).isEqualTo("Duplicate");
	}
	
	
	@Test
	public void testCheckUniqueInEditModeReturnOK() {
		Integer id = 1;
		String name = "Acer";
		
		Brand brand = new Brand(id,name); 
		
		
		Mockito.when(repository.findByName(name)).thenReturn(brand);
		
		String result = service.checkUnique(id, name);
		
		assertThat(result).isEqualTo("OK");
	}
	
}
