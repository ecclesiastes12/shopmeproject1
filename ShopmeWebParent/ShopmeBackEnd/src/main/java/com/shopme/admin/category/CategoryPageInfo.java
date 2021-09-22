package com.shopme.admin.category;

/*
 * this class is created to get page info. because of the 
 * hierarchy code structure of the page
 */
public class CategoryPageInfo {
 
	int totalPages;
	long totalElements;
	
	public CategoryPageInfo() {}
	
	public int getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	public long getTotalElements() {
		return totalElements;
	}
	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}
	
	
	
}
