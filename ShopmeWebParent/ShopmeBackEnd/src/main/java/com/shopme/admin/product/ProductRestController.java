package com.shopme.admin.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.common.entity.Product;

@RestController
public class ProductRestController {

@Autowired
ProductService productService;
	
	@PostMapping("/products/check_unique")
	public String checkUnique(@Param("id") Integer id,
			@Param("name") String name,@Param("alias") String alias) {
		
		return productService.checkUniqueProduct(id, name);
	}
}
