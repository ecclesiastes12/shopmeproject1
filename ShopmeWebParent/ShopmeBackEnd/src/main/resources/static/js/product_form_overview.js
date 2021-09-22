
 
 
 dropdownBrands = $("#brand");
 dropdownCategories = $("#category");
 
 $(document).ready(function(){
  
  //$("#shortDescription").richText(); add the richtext features to description textarea
 	$("#shortDescription").richText();
 	$("#fullDescription").richText();
 
 	//change event handler for brand dropdown list
 	dropdownBrands.change(function(){
 	
 		dropdownCategories.empty();
 	
 		//method called
 		getCategories()
 		
 	});
 
 	
 	getCategoriesForNewForm();
 	
	 });

	function getCategoriesForNewForm(){
	
		//gets the category id for the hidden field on the form
		catIdField = $("#categoryId");
		
		//check if the form is in the edit mode
		editMode = false;
	
		//check the presents of the input feild category id
		if(catIdField.length){
			//set edit mode to true
			editMode = true;
		}
		
		//shows the first category item in the list of categories associated with 
		//list of brands in the form, if only the form is in the new mode thus when edit mode is false
		if(!editMode) getCategories(); 
	}
 
 	
 	function getCategories(){
 	brandId = dropdownBrands.val();
 	
 	// expose the url in the restcontroller ("/brands/{id}/categories")
 	url = brandModuleURL + "/" + brandId + "/categories";
 		
 	//use json to get response from the server
 	$.get(url, function(responseJson){
 		
 		//iterate through each object element of categories in json
 		$.each(responseJson, function(index, category){
 			
 			//adds the categories objects associated with brand to the the dropdown
 			$("<option>")
 				.val(category.id)
 					.text(category.name)
 						.appendTo(dropdownCategories);
 		});
 	});
 	
 	}
 	
 	//function to check product uniqueness
 	function checkUnique(form){
	
 		productId = $("#id").val();
 		productName = $("#name").val();
 		
 	
 		csrfValue = $("input[name='_csrf']").val();
 	
 		/* the value of the url is the path in the ProductRestController*/
 		
 		
 		
 		params = {id: productId, name: productName, _csrf: csrfValue};
 		
 		/*sends post request to the server with the given url and params*/
 		
 		$.post(checkUniqueUrl, params, function(response){
 			
 			if (response == "OK"){
 				form.submit();
 			}else if(response == "Duplicate"){
 				showWarningModal("There is another product having the same name " + productName);
 			}else{
 				showErrorModal("Unknown response from server");
 			}
 			
 		}).fail(function(){
 			showErrorModal("Could not connect to the server");
 		});
 	
 	 return false;
 	} 	
 	