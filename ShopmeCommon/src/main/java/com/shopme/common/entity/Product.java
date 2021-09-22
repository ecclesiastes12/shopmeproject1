package com.shopme.common.entity;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "products")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;
	
	@Column(unique = true, length = 256, nullable = false)
	String name;
	
	@Column(unique = true, length = 256, nullable = false)
	String alias;
	
	@Column(name = "short_description", length = 512, nullable = false)
	String shortDescription;
	
	@Column(name = "full_description", length = 4096, nullable = false)
	String fullDescription;
	
	@Column(name = "created_time")
	Date createdTime;
	
	@Column(name = "updated_time")
	Date updatedTime;
	
	boolean enabled;
	
	@Column(name = "in_stock")
	boolean inStock;
	
	float cost;
	float price;
	
	@Column(name = "discount_percent")
	float discountPercent;
	
	float length;
	float width;
	float height;
	float weight;
	
	@Column(name = "main_image", nullable = false)
	String mainImage;
	
	@ManyToOne
	@JoinColumn(name = "category_id")
	Category category;
	
	@ManyToOne
	@JoinColumn(name = "brand_id")
	Brand brand;

	//NB product used in mappedBy = "product" is the name of the 
	//viariable product decleared in ProductImage class
	/**
	 * orphanRemoval is an entirely ORM-specific thing. 
	 * It marks "child" entity to be removed when it's no 
	 * longer referenced from the "parent" entity, 
	 * e.g. when you remove the child entity from the corresponding 
	 * collection of the parent entity.
	 * 
	 * In this case, if the user change an existing extra image,
	 * the new image will be save the the existing one will be 
	 * removed from the database because it's no longer referenced from the parent
	 * 
	 * 
	 */
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	Set<ProductImage> images = new HashSet<>(); //for extra images
	
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	List<ProductDetail> details = new ArrayList<>();
	
	public Product() {}
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

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getFullDescription() {
		return fullDescription;
	}

	public void setFullDescription(String fullDescription) {
		this.fullDescription = fullDescription;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isInStock() {
		return inStock;
	}

	public void setInStock(boolean inStock) {
		this.inStock = inStock;
	}

	public float getCost() {
		return cost;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public float getDiscountPercent() {
		return discountPercent;
	}

	public void setDiscountPercent(float discountPercent) {
		this.discountPercent = discountPercent;
	}

	public float getLength() {
		return length;
	}

	public void setLength(float length) {
		this.length = length;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}
	
	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + "]";
	}
	public String getMainImage() {
		return mainImage;
	}
	public void setMainImage(String mainImage) {
		this.mainImage = mainImage;
	}
	public Set<ProductImage> getImages() {
		return images;
	}
	public void setImages(Set<ProductImage> images) {
		this.images = images;
	}
	
	//method for adding extra images
	public void addExtraImage(String imageName) {
		//"this" used in the parameter refers to the parent object thus product
		this.images.add(new ProductImage(imageName, this));
	}
	
	@Transient
	public String getMainImagePath() {
		if(id == null || mainImage == null) return "/images/image-thumbnail.png";
		
		//NB this.mainImage will create a sub-directory in product image
		return "/product-images/" + this.id + "/" + this.mainImage;
	}
	
	public List<ProductDetail> getDetails() {
		return details;
	}
	public void setDetails(List<ProductDetail> details) {
		this.details = details;
	}
	public void addDetail(String name, String value) {
		this.details.add(new ProductDetail(name,value,this));
	}
	
	public void addDetail(Integer id, String name, String value) {
		this.details.add(new ProductDetail(id,name,value,this));
		
	}
	public boolean containsImageName(String imageName) {
		// iterate through the set images
		Iterator<ProductImage> iterator = images.iterator();
		
		while (iterator.hasNext()) {
			ProductImage image = iterator.next();
			if(image.getName().equals(imageName)) {
				return true;
			}
			
		}
		return false;
	}
	
	
	//method to shorten the name if the name is too long. That is 
	//when it exceeds certain number of characters
	@Transient
	public String getShortName() {
		if(name.length() > 70) {
			return name.substring(0, 70).concat("...");
		}
		
		return name;
	}
}
