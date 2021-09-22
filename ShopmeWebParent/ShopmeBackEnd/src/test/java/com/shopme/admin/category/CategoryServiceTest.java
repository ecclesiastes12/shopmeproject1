package com.shopme.admin.category;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import com.shopme.common.entity.Category;

/*
 * NB Mockito is used to test the service class layer 
 */

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class CategoryServiceTest {

	@MockBean  //create fake object of the repository
	private CategoryRepository repo;
	
	@InjectMocks   //injects the service class
	private CategoryService service;
	
	@Test
	public void testCheckUniqueInNewModeReturnDuplicateName() {
		//specify paramter values for the method checkUnique
		Integer id = null;
		String name = "Computers";
		String alias = "abc";
		
		Category category = new Category(id, name, alias);
		
		Mockito.when(repo.findByName(name)).thenReturn(category);//when category of that name exist in the db
		Mockito.when(repo.findByAlias(alias)).thenReturn( null); //when no alias of that name exist in the db
		
		String result = service.checkUnique(id, name, alias);
		
		assertThat(result).isEqualTo("Duplicate Name");
		
		
	}
	
	@Test
	public void testCheckUniqueInNewModeReturnDuplicateAlias() {
		//specify paramter values for the method checkUnique
		Integer id = null;
		String name = "NameABC";
		String alias = "computers";
		
		Category category = new Category(id, name, alias);
		
		Mockito.when(repo.findByName(name)).thenReturn(null); //when no category of that name exist in the db
		Mockito.when(repo.findByAlias(alias)).thenReturn( category); //when alias of that name exist in the db
		
		String result = service.checkUnique(id, name, alias);
		
		assertThat(result).isEqualTo("Duplicate Alias");
		
		
	}
	
	@Test
	public void testCheckUniqueInNewModeReturnOK() {
		//specify paramter values for the method checkUnique
		Integer id = null;
		String name = "Computers2";
		String alias = "abc3";
		
		Category category = new Category(id, name, alias);
		
		Mockito.when(repo.findByName(name)).thenReturn(null); //when no category name of that name exist in the db
		Mockito.when(repo.findByAlias(alias)).thenReturn( null); //when no alias of that name exist in the db
		
		String result = service.checkUnique(id, name, alias);
		
		assertThat(result).isEqualTo("OK");
		
		
	}
	
	@Test
	public void testCheckUniqueInEditModeReturnDuplicateName() {
		//specify paramter values for the method checkUnique
		Integer id = 1;
		String name = "Computers2";
		String alias = "abc3";
		
		Category category = new Category(2, name, alias);
		
		Mockito.when(repo.findByName(name)).thenReturn(category); //when category name of that name exist in the db
		Mockito.when(repo.findByAlias(alias)).thenReturn( null); //when no alias of that name exist in the db
		
		String result = service.checkUnique(id, name, alias);
		
		assertThat(result).isEqualTo("Duplicated Name");
		
		
	}
	
	@Test
	public void testCheckUniqueInEditModeReturnDuplicateAlias() {
		//specify paramter values for the method checkUnique
		Integer id = 1;
		String name = "Computers2";
		String alias = "abc3";
		
		Category category = new Category(2, name, alias);
		
		Mockito.when(repo.findByName(name)).thenReturn(null); //when no category name of that name exist in the db
		Mockito.when(repo.findByAlias(alias)).thenReturn( category); //when alias of that name exist in the db
		
		String result = service.checkUnique(id, name, alias);
		
		assertThat(result).isEqualTo("Duplicate Alias");
		
		
	}
	
	@Test
	public void testCheckUniqueInEditModeReturnOK() {
		//specify paramter values for the method checkUnique
		Integer id = 1;
		String name = "Computers2";
		String alias = "abc3";
		
		Category category = new Category(id, name, alias);
		
		Mockito.when(repo.findByName(name)).thenReturn(category); //when no category name of that name exist in the db
		Mockito.when(repo.findByAlias(alias)).thenReturn( category); //when no alias of that name exist in the db
		
		String result = service.checkUnique(id, name, alias);
		
		assertThat(result).isEqualTo("OK");
		
		
	}
}
