package com.shopme.common.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "categories")
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;
	
	@Column(length = 128, nullable = false, unique = true)
	String name;
	
	@Column(length = 64, nullable = false, unique = true)
	String alias;
	
	@Column(length = 128, nullable = false)
	String image;
	
	boolean enabled;
	
	//for search query in products form
	//nullable = true because root category has no parent
	@Column(name = "all_parent_ids", length = 256, nullable = true)
	String allParentIDs;
	
	@OneToOne
	@JoinColumn(name = "parent_id")
	Category parent; //self referenced foreign key
	
	@OneToMany(mappedBy = "parent") // mappedBy = "parent" refers to Category parent
	Set<Category> children = new HashSet();

	public Category() {
		
	}
	public Category(Integer id) {
		this.id = id;
	}

	

	//The purpose of this method is to get id and names of parent
	//categories  used in the form thus the dropdown
	//category in the form.
	public static Category copyIdAndName(Category category) {
		Category copyCategory = new Category();
		copyCategory.setId(category.getId());
		copyCategory.setName(category.getName());
		return copyCategory;
	}
	
	//for id and names of sub-category in the form 
	public static Category copyIdAndName(Integer id, String name) {
		Category copyCategory = new Category();
		copyCategory.setId(id);
		copyCategory.setName(name);
		return copyCategory;
	}
	
	//copys full details of category
	public static Category copyFull(Category category) {
		Category copyCategory = new Category();
		copyCategory.setId(category.getId());
		copyCategory.setName(category.getName());
		copyCategory.setImage(category.getImage());
		copyCategory.setAlias(category.getAlias());
		copyCategory.setEnabled(category.isEnabled());
		
		//for subcategory deletion
		copyCategory.setHasChildren(category.getChildren().size() > 0);
		
		return copyCategory;
	}
	
	//for updating the name of subCategory with "--"
	public static Category copyFull(Category category, String name) {
		Category copyCategory = Category.copyFull(category);//category full details
		copyCategory.setName(name); //set the name which will append "--"
		
		return copyCategory;
	}
	
	//third constructor
	public Category(String name) {
		
		this.name = name;
		this.alias = name; //NB alias is set as name; meaning every alias will be
							// the same as name
		this.image = "default.png";
	}

	
	//constructor for subcategory
	public Category(String name, Category parent) {

		this(name); // third constructor call
		this.parent = parent;
	}
	
	

	public Category(Integer id, String name, String alias) {
		
		this.id = id;
		this.name = name;
		this.alias = alias;
	}
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Category getParent() {
		return parent;
	}

	public void setParent(Category parent) {
		this.parent = parent;
	}

	public Set<Category> getChildren() {
		return children;
	}

	public void setChildren(Set<Category> children) {
		this.children = children;
	}
		
	public String getAllParentIDs() {
		return allParentIDs;
	}
	
	public void setAllParentIDs(String allParentIDs) {
		this.allParentIDs = allParentIDs;
	}
	
	//returns image path of the category
	@Transient
	public String getImagePath() {
		if(this.id == null || this.image == null) return "/images/image-thumbnail.png";
		return "/category-images/" + this.id + "/" + this.image;
	}
	
	//for subcategory deletion
	public boolean isHasChildren() {
		return hasChildren;
	}
	
	//for subcategory deletion
	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}
	
	//for subcategory deletion
	@Transient
	private boolean hasChildren;

	@Override
	public String toString() {
		return "Category [name=" + name + "]";
	}
	
	
	
	
}
