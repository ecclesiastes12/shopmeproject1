package com.shopme.admin.brand;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.admin.category.CategoryService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@RestController
public class BrandRestController {

	@Autowired
	BrandService service;

	@PostMapping("/brands/check_unique")
	public String checkUnique(@Param("id") Integer id, @Param("name") String name) {
		return service.checkUnique(id, name);
	}

	// purpose of this restcontroller is to call the brand with
	// its associated category on the product form

	@GetMapping("/brands/{id}/categories")
	public List<CategoryDTO> listCategoriesByBrand(@PathVariable(name = "id") Integer brandId)
			throws BrandNotFoundRestException {

		// return list of category object
		List<CategoryDTO> listCategories = new ArrayList<>();

		try {
			// grabs brands id

			Brand brand = service.get(brandId);

			// sets categories for brands used in product
			Set<Category> categories = brand.getCategories();

			// iterate through each categories associated with the brand
			for (Category category : categories) {

				// create new CategoryDTO object that takes category id and category name
				// as parameter
				CategoryDTO dto = new CategoryDTO(category.getId(), category.getName());

				// adds the dto object to the list of categories
				listCategories.add(dto);
			}

			return listCategories;

		} catch (BrandNotFoundException e) {
			throw new BrandNotFoundRestException();
		}
	}

}
