package com.shopme.admin.category;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Category;

@Service
@Transactional
public class CategoryService {

	//static variable to category per page
	public static final int ROOT_CATEGORY_PER_PAGE = 4;
	
	@Autowired
	CategoryRepository repo;
	
	//method that returns list of categories
	/*
	 * public List<Category> listAll() { //return repo.findAll(); return
	 * (List<Category>) repo.findAll(); }
	 */
	/*
	 *
	 * //method that returns list of categories modified to list parent(root)
	 * categories public List<Category> listAll() { List<Category> rootCategories =
	 * repo.findRootCategories(); return listHierarchicalCategories(rootCategories);
	 * }
	 * 
	 */
	
//	//method that returns list of categories modified to list parent(root) categories and sorted in ascending order
//		public List<Category> listAll() {	
//			List<Category> rootCategories = repo.findRootCategories(Sort.by("name").ascending());
//			return listHierarchicalCategories(rootCategories);
//		}
	
	
	//method that returns list of categories modified to list parent(root) categories and sorted in ascending order
	//modified with sort paramter
//	
//			public List<Category> listAll(String sortDir) {	
//				
//				Sort sort = Sort.by("name");
//				
//				if(sortDir.equals("asc")) {
//					sort = sort.ascending();
//				}else if (sortDir.equals("desc")) {
//					sort = sort.descending();
//				}
//				
//				List<Category> rootCategories = repo.findRootCategories(sort);
//				
//				return listHierarchicalCategories(rootCategories, sortDir);
//			}
//	
	
//	
//	//listAll method  changed to listByPage and modified
//	//for the purpose of paging and sorting
//	public List<Category> listByPage(CategoryPageInfo pageInfo, int pageNum, String sortDir) {	
//		
//		Sort sort = Sort.by("name");
//		
//		if(sortDir.equals("asc")) {
//			sort = sort.ascending();
//		}else if (sortDir.equals("desc")) {
//			sort = sort.descending();
//		}
//		
//		//creates pageable object
//		Pageable pageable = PageRequest.of(pageNum - 1, ROOT_CATEGORY_PER_PAGE, sort);
//		
//		//List<Category> rootCategories = repo.findRootCategories(sort);
//		
//		//pass the pageable to pageCategories to sort only the parent or root categories
//		Page<Category> pageCategories = repo.findRootCategories(pageable);
//		
//		List<Category> rootCategories = pageCategories.getContent();
//		
//		//sets page info
//		pageInfo.setTotalElements(pageCategories.getTotalElements());
//		pageInfo.setTotalPages(pageCategories.getTotalPages());
//		
//		return listHierarchicalCategories(rootCategories, sortDir);
//	}
//
//	
	
	
	

	//listAll method  changed to listByPage and modified
	//for the purpose of paging and sorting
	//modified with search keyword
	public List<Category> listByPage(CategoryPageInfo pageInfo, int pageNum, String sortDir,
			String keyword) {	
		
		Sort sort = Sort.by("name");
		
		if(sortDir.equals("asc")) {
			sort = sort.ascending();
		}else if (sortDir.equals("desc")) {
			sort = sort.descending();
		}
		
		//creates pageable object
		Pageable pageable = PageRequest.of(pageNum - 1, ROOT_CATEGORY_PER_PAGE, sort);
		
		//List<Category> rootCategories = repo.findRootCategories(sort);
		
		//pass the pageable to pageCategories to sort only the parent or root categories
		Page<Category> pageCategories = null;
		
		//check the existence of search keyword
		if(keyword != null && !keyword.isEmpty()) {
			pageCategories = repo.search(keyword,pageable);
		}else {
			pageCategories = repo.findRootCategories(pageable);
		}
		
		
		List<Category> rootCategories = pageCategories.getContent();
		
		//sets page info
		pageInfo.setTotalElements(pageCategories.getTotalElements());
		pageInfo.setTotalPages(pageCategories.getTotalPages());
		
		
		if(keyword != null && !keyword.isEmpty()) {
			List<Category> searchResult = pageCategories.getContent();
			for(Category category : searchResult) {
				category.setHasChildren(category.getChildren().size() > 0);
			}
			
			return searchResult;
		}else {
			return listHierarchicalCategories(rootCategories, sortDir);
		}
		
	}

	
	
	
		
//	//method to list categories in a hierarchy format
//	private List<Category> listHierarchicalCategories(List<Category> rootCategories ){
//		//creates array list of hierarchical categories object
//		List<Category> hierarchicalCategories = new ArrayList<>();
//		
//		//iterate through each root categories
//		for(Category rootCategory : rootCategories) {
//			
//			//adds the parent(root) categories to hierarchical array collection
//			hierarchicalCategories.add(Category.copyFull(rootCategory));
//			
//			//grabs the children of root category
//			Set<Category> children = rootCategory.getChildren();
//			
//			//iterate through the children category
//			for(Category subCategory : children) {
//				
//				//appends -- to the child(subCategory)
//				String name = "--" + subCategory.getName();
//				
//				//adds the subCategory(children categories) to hierarchical array collection
//				hierarchicalCategories.add(Category.copyFull(subCategory, name));
//				
//				//method call
//				listSubHierarchicalCategories(hierarchicalCategories,subCategory,1);
//			}
//			
//		}
//		
//		return hierarchicalCategories;
//	}
//	
		
		
//		//method to list categories in a hierarchy format with sorted subcategories
//		private List<Category> listHierarchicalCategories(List<Category> rootCategories ){
//			//creates array list of hierarchical categories object
//			List<Category> hierarchicalCategories = new ArrayList<>();
//			
//			//iterate through each root categories
//			for(Category rootCategory : rootCategories) {
//				
//				//adds the parent(root) categories to hierarchical array collection
//				hierarchicalCategories.add(Category.copyFull(rootCategory));
//				
//				//grabs the children of root category (unsorted)
//				//Set<Category> children = rootCategory.getChildren();
//				
//				//sorted children(sub-categories) of parent categories
//				Set<Category> children = sortSubCategories(rootCategory.getChildren());
//				
//				//iterate through the children category
//				for(Category subCategory : children) {
//					
//					//appends -- to the child(subCategory)
//					String name = "--" + subCategory.getName();
//					
//					//adds the subCategory(children categories) to hierarchical array collection
//					hierarchicalCategories.add(Category.copyFull(subCategory, name));
//					
//					//method call
//					listSubHierarchicalCategories(hierarchicalCategories,subCategory,1);
//				}
//				
//			}
//			
//			return hierarchicalCategories;
//		}
//		
		
			
			//method to list categories in a hierarchy format with sorted subcategories
			//modified with sorted paramter
			private List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortDir) {
				//creates array list of hierarchical categories object
				List<Category> hierarchicalCategories = new ArrayList<>();
				
				//iterate through each root categories
				for(Category rootCategory : rootCategories) {
					
					//adds the parent(root) categories to hierarchical array collection
					hierarchicalCategories.add(Category.copyFull(rootCategory));
					
					//grabs the children of root category (unsorted)
					//Set<Category> children = rootCategory.getChildren();
					
					//sorted children(sub-categories) of parent categories
					//modified with sorted parameter
					Set<Category> children = sortSubCategories(rootCategory.getChildren(), sortDir);
					
					//iterate through the children category
					for (Category subCategory : children) {
						
						//appends -- to the child(subCategory)
						String name = "--" + subCategory.getName();
						
						//adds the subCategory(children categories) to hierarchical array collection
						hierarchicalCategories.add(Category.copyFull(subCategory, name));
						
						//method call
						listSubHierarchicalCategories(hierarchicalCategories,subCategory,1,sortDir);
						
					}
					
				}
				
				return hierarchicalCategories;
			}
			
		
		
		
//	//method that list subcategory in a hierarchy format
//	private void listSubHierarchicalCategories(List<Category> hierarchicalCategories,
//			Category parent, int subLevel) {
//		//unsorted sub-categories
//		//Set<Category> children = parent.getChildren();
//		
//		//sorted sub-categories
//		Set<Category> children =sortSubCategories(parent.getChildren());
//		
//		int newSubLevel = subLevel + 1;//increase sub level by 1
//		
//		//iterate through each sub category
//		for(Category subCategory : children) {
//			String name = ""; //create empty string
//			for(int i = 0; i < newSubLevel; i++) {
//				name += "--"; //appends -- to the empty string
//			}
//			name += subCategory.getName();//appends -- to the subcategory 
//			
//			//adds a copy of sub categories details to hierarchical array collection 
//			hierarchicalCategories.add(Category.copyFull(subCategory, name));
//			
//			listSubCategoriesUsedInForm(hierarchicalCategories, subCategory, newSubLevel);
//		}
//	}
//	
			
			//method that list subcategory in a hierarchy format
			//modified with sorted parameter
			private void listSubHierarchicalCategories(List<Category> hierarchicalCategories,
					Category parent, int subLevel, String sortDir) {
				//unsorted sub-categories
				//Set<Category> children = parent.getChildren();
				
				//sorted sub-categories with sorted parameter
				Set<Category> children = sortSubCategories(parent.getChildren(), sortDir);
				
				int newSubLevel = subLevel + 1;//increase sub level by 1
				
				//iterate through each sub category
				for (Category subCategory : children) {
					String name = ""; //create empty string
					for (int i = 0; i < newSubLevel; i++) {		
						
						name += "--"; //appends -- to the empty string
					}
					name += subCategory.getName();//appends -- to the subcategory 
					
					//adds a copy of sub categories details to hierarchical array collection 
					hierarchicalCategories.add(Category.copyFull(subCategory, name));
					
					//listSubCategoriesUsedInForm(hierarchicalCategories, subCategory, newSubLevel);
					
					listSubHierarchicalCategories(hierarchicalCategories, subCategory, newSubLevel, sortDir);
				}
			}
			
			
			
	//method that save category
	public Category save(Category category) {
		
		
		  //grabs parent categories 
		  Category parent = category.getParent();
		  
		  //checks if parent id is not null 
		  if(parent != null) { //get all parent ids
		  // of parent and if parent id is null "-" will be assigned to the parent id 
		  //and if parent id is not null return parent id 
		  String allParentsIds = parent.getAllParentIDs() == null ? "-" : parent.getAllParentIDs();
		  
		  //concatenate allParantIDs with the value of parentid and "-" 
		  allParentsIds += String.valueOf(parent.getId()) + "-";
		  
		  category.setAllParentIDs(allParentsIds);//set allParantIDs 
		  }
		 
		
		/*
		 * Category parent = category.getParent(); if (parent != null) { String
		 * allParentIds = parent.getAllParentIDs() == null ? "-" :
		 * parent.getAllParentIDs(); allParentIds += String.valueOf(parent.getId()) +
		 * "-"; category.setAllParentIDs(allParentIds); }
		 */
		
		return repo.save(category);
	}
	
	
	//method that return categories used in  the form. thus drop down
//	public List<Category> listCategoriesUsedInForm(){
//		return repo.findAll();
//	}
	
//	
//	//method that return categories used in  the form. thus drop down
//	//in a hierarchical format
//	public List<Category> listCategoriesUsedInForm(){
//		//categories used in form
//		List<Category> categoriesUsedInForm =  new ArrayList<>();
//		//retrieves all categories from the db
//		Iterable<Category> categoriesInDB = repo.findAll();
//		
//		for(Category category : categoriesInDB) {
//			//if category equls to null it means the category
//			//is a root level(top level) category
//			if(category.getParent() == null) {
//				//adds the name of the categories used in the form (drop down)
//				//categoriesUsedInForm.add(new Category(category.getName()));
//				categoriesUsedInForm.add(Category.copyIdAndName(category));
//				//gets the children of the category which is a set of collection of objects
//				//of type category
//				Set<Category> children = category.getChildren();
//				
//				//iterates through each child category
//				for(Category subCategory : children) {
//					// child categories with -- indicating the level
//					String name= "--" + subCategory.getName();
//					
//					//adds the name of the sub Category used in the form (drop down)
//					//categoriesUsedInForm.add(new Category(name));
//					categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
//					
//					//method call
//					listSubCategoriesUsedInForm(categoriesUsedInForm,subCategory,1);
//				}
//			}
//		}
//		
//		return categoriesUsedInForm;
//	}
//	
	
	

	//method that return categories used in  the form. thus drop down
	//in a hierarchical format and modified to list parent(root) in a sorted order
	
	public List<Category> listCategoriesUsedInForm(){
		//categories used in form
		List<Category> categoriesUsedInForm =  new ArrayList<>();
		//retrieves all categories from the db. modified to list parent categories
		//Iterable<Category> categoriesInDB = repo.findAll();
		Iterable<Category> categoriesInDB = repo.findRootCategories(Sort.by("name").ascending());
		
		for(Category category : categoriesInDB) {
			//if category equls to null it means the category
			//is a root level(top level) category
	//		if(category.getParent() == null) {
				//adds the name of the categories used in the form (drop down)
				//categoriesUsedInForm.add(new Category(category.getName()));
				categoriesUsedInForm.add(Category.copyIdAndName(category));
				//gets the children of the category which is a set of collection of objects
				//of type category
			//	Set<Category> children = category.getChildren();
				
				Set<Category> children = sortSubCategories(category.getChildren());
				
				//iterates through each child category
				for(Category subCategory : children) {
					// child categories with -- indicating the level
					String name = "--" + subCategory.getName();
					
					//adds the name of the sub Category used in the form (drop down)
					//categoriesUsedInForm.add(new Category(name));
					categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
					
					//method call
					listSubCategoriesUsedInForm(categoriesUsedInForm,subCategory,1);
				}
		//	}
		}
		
		return categoriesUsedInForm;
	}
	
//	
//	//method to list the children of each sub-category
//	private void listSubCategoriesUsedInForm(List<Category> categoriesUsedInForm,Category parent,int subLevel) {
//		//increase subLevel by one
//		int newSubLevel = subLevel + 1;
//		
//		Set<Category> children = parent.getChildren();
//		//print the name of each subCategory under each parent category
//		for(Category subCategory : children) {
//			
//			String name = "";
//			for(int i = 0; i < newSubLevel; i++) {
//			//	System.out.print("--"); // for indicating the subLevel
//				name += "--";
//			}
//			
//			//adds -- to the subcategory names
//			name += subCategory.getName();
//			
//			//adds the copied id and names of sub-categories to the form
//			categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
//			
//			//method calling itself (recursion)
//			listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, newSubLevel);
//		}
//	}
//			
	
	

	//method to list the children of each sub-category in a sorted order
	private void listSubCategoriesUsedInForm(List<Category> categoriesUsedInForm,
			Category parent,int subLevel) {
		//increase subLevel by one
		int newSubLevel = subLevel + 1;
		
		//unsorted sub categories (children)
	//	Set<Category> children = parent.getChildren();
		
		//sorted sub categories
		Set<Category> children = sortSubCategories(parent.getChildren());
		
		//print the name of each subCategory under each parent category
		for(Category subCategory : children) {
			
			String name = "";
			for(int i = 0; i < newSubLevel; i++) {
			//	System.out.print("--"); // for indicating the subLevel
				name += "--";
			}
			
			//adds -- to the subcategory names
			name += subCategory.getName();
			
			//adds the copied id and names of sub-categories to the form
			categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
			
			//method calling itself (recursion)
			listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, newSubLevel);
		}
	}
		
	
	
	
	//method that return error message if category id is not found in the database
	public Category get(Integer id) throws CategoryNotFoundException{
		try {
			return repo.findById(id).get();
		} catch (Exception e) {
			throw new NoSuchElementException("Could not find the user with ID " + id);
		}
	}
	
	//method that check the uniqueness of a category
	public String checkUnique(Integer id, String name, String alias) {
		
		//if id is null it means its creating new category
		boolean isCreatingNew = (id == null || id == 0);
		
		//find category by name
		Category categoryByName = repo.findByName(name);
		
		//find category by alias
		Category categoryByAlias = repo.findByAlias(alias);
		
		if(isCreatingNew) {
			//return duplicate name if category by name is not null when form is in new mode
			if(categoryByName != null) {
				return "DuplicateName";
			}else { 
				//return duplicate alias if category by alias is not null when form is in new mode
				if(categoryByAlias != null) {
				return "DuplicateAlias";
				}
			}
		}else {
			if(categoryByName != null && categoryByName.getId() != id) {
				return "DuplicateName";
			}
			
			if(categoryByAlias != null && categoryByAlias.getId() != id) {
				return "DuplicateAlias";
			}
		}
		
		return "OK";
	}
	
	
//	//method to sort sub-category in a specify order
//	//without this method the sub-category will be sorted in a random order
//	private SortedSet<Category> sortSubCategories(Set<Category> children){
//		
//		SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {
//
//			@Override
//			public int compare(Category cat1, Category cat2) {
//				//campares category 1 to category 2
//				return cat1.getName().compareTo(cat2.getName());
//			}
//			
//		});
//		//adds all elements of children to sorted children
//		sortedChildren.addAll(children);
//		
//		return sortedChildren;
//	}
	
	
	//method used by list of categories used in form
	private SortedSet<Category> sortSubCategories(Set<Category> children){
		return sortSubCategories(children, "asc");
	}
	
	
	//method to sort sub-category in a specify order
		//without this method the sub-category will be sorted in a random order
	//modified based on sort direction thus ascending or descending order
		private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir){
			
			SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {

				@Override
				public int compare(Category cat1, Category cat2) {
					
					//sorted in ascending or descending order
					if(sortDir.equals("asc")) {
						//campares category 1 to category 2
						return cat1.getName().compareTo(cat2.getName());
					}else {
						//campares category 2 to category 1
						return cat2.getName().compareTo(cat1.getName());
					}
				}
				
			});
			//adds all elements of children to sorted children
			sortedChildren.addAll(children);
			
			return sortedChildren;
		}
		
		//NB without the annotation @Transactional on top of the class this method will fail to work
		//method to enabled or disabled category
		public void updateCategoryEnabledStatus(Integer id, boolean enabled) {
			repo.updateCategoryEnabledStatus(id, enabled);
		}
		
		//method to delete category
		public void delete(Integer id) throws CategoryNotFoundException {
			
			Long countById = repo.countById(id);
			//if(countById.equals(null) || countById.equals(0)) {
			if (countById == null || countById == 0) {
				throw new CategoryNotFoundException("Could not find any category with ID " + id);
			}
			repo.deleteById(id);
		}
}
