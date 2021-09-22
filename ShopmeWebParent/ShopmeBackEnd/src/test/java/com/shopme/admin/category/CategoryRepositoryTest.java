package com.shopme.admin.category;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;


import com.shopme.common.entity.Category;
//showSql = false will disable printing of sql statements in the console
@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTest {

	@Autowired
	CategoryRepository repo;
	
	
	@Test
	public void testCreateRootCategory() {
		Category category = new Category("Electronics");
		Category saveCategory = repo.save(category);
		
		assertThat(saveCategory.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateSubCategory() {
		//gets the id of the parent(root) category
		Category parent = new Category(7);
		Category subCategory = new Category("iPhone",parent);
		//Category components = new Category("Smartphones",parent);
		Category saveCategory = repo.save(subCategory);
		// repo.saveAll(List.of(subCategory,components));
		assertThat(saveCategory.getId()).isGreaterThan(0);
		
	}
	
	//Test method to get a category name thus first level category and its sub-category
	@Test
	public void testGetCategory() {
		Category category = repo.findById(1).get();
		System.out.println(category.getName());
		
		Set<Category> children = category.getChildren();
		
		//iterate through the list of sub-category
		for(Category subCategory : children) {
			System.out.println(subCategory.getName());
		}
		
		assertThat(children.size()).isGreaterThan(0);
	}
	
	//Test method to print all categories in a hierarchy form
	//prints top level category
	/*
	 * @Test public void testPrintHierarchicalCategories() { // retrieves all
	 * categories from the db by iteration Iterable<Category> categories =
	 * repo.findAll();
	 * 
	 * // iterates through each child category for (Category category : categories)
	 * { if (category.getParent() == null) { Set<Category> children =
	 * category.getChildren(); for (Category subCategory : children) { // prints the
	 * child categories System.out.println("--" + subCategory.getName()); } } } }
	 */
	
//	//Test method to print all categories in a hierarchy form
//	@Test
//	public void testPrintHierarchicalCategories() {
//		//retrieves all categories from the db by iteration
//		Iterable<Category> categories = repo.findAll();
//		
//		//iterates through each parent category
//		for(Category category : categories) {
//			//if category equls to null it means the category
//			//is a root level(top level) category
//			if(category.getParent() == null) {
//				System.out.println(category.getName());
//				
//				//gets the children of the category which is a set of collection of objects
//				//of type category
//				Set<Category> children = category.getChildren();
//				
//				//iterates through each child category
//				for(Category subCategory : children) {
//					//prints the child categories
//					System.out.println("--" + subCategory.getName());
//				}
//			}
//		}
//	}
	
	
	//Test method to print all categories and sub-categories in a hierarchy form
		@Test
		public void testPrintHierarchicalCategories() {
			//retrieves all categories from the db by iteration
			Iterable<Category> categories = repo.findAll();
			
			//iterates through each parent category
			for(Category category : categories) {
				//if category equls to null it means the category
				//is a root level(top level) category
				if(category.getParent() == null) {
					System.out.println(category.getName());
					
					//gets the children of the category which is a set of collection of objects
					//of type category
					Set<Category> children = category.getChildren();
					
					//iterates through each child category
					for(Category subCategory : children) {
						//prints the child categories
						System.out.println("--" + subCategory.getName());
						
						//method call
						printChildren(subCategory,1);
					}
				}
			}
		}
	
		
		//method to print the children of each sub-category
		private void printChildren(Category parent,int subLevel) {
			//increase subLevel by one
			int newSubLevel = subLevel + 1;
			
			Set<Category> children = parent.getChildren();
			//print the name of each subCategory under each parent category
			for(Category subCategory : children) {
				for(int i = 0; i < newSubLevel; i++) {
					System.out.print("--"); // for indicating the subLevel
				}
				
				//prints the name of each subCategory
				System.out.println( subCategory.getName());
				
				printChildren(subCategory, newSubLevel);
			}
		}
		
//		//test   list parent(root) categories
//		@Test
//		public void testListRootCategories() {
//			List<Category> rootCategories = repo.findRootCategories();
//			rootCategories.forEach(cat -> System.out.println(cat.getName()));
//		}
//		
		//test  sorted list parent(root) categories
		@Test
		public void testListRootCategories() {
			List<Category> rootCategories = repo.findRootCategories(Sort.by("name").ascending());
			rootCategories.forEach(cat -> System.out.println(cat.getName()));
		}
		
		@Test
		public void testFindByName() {
			String name = "Computers";
			Category category = repo.findByName(name);
			
			assertThat(category).isNotNull();
			assertThat(category.getName()).isEqualTo(name);
		}
		
		@Test
		public void testFindByAlias() {
			String alias = "computer";
			Category category = repo.findByAlias(alias);
			
			assertThat(category).isNotNull();
			assertThat(category).isEqualTo(alias);
		}
}
