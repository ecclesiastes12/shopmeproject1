
 var extraImagesCount = 0; //for counting the number of extra images
 
 
 
 $(document).ready(function(){
  
 	
 	/*for images preview*/
 	//	$("#extraImage1").change(function(){
 		
 	//	if(!checkFileSize(this)){
	
	//		return true;
	//	}
 			
 	//		showExtraImageThumbnail(this);		
 	//	});
 	
 	/** 
 		NB extraImage used here refers to the name value attribute
 		of the input tag in addNextExtraImageSection()
 		
 		index used here refers to the id of extraThumbnail
 	*/
 	$("input[name='extraImage']").each(function(index){
		extraImagesCount++;
		$(this).change(function(){
		if(!checkFileSize(this)){
	
			return true;
		}
			showExtraImageThumbnail(this, index);
		});
	
	});
 	
 	//removes extra images
 	//loops through each extra image
 	//index used here is for the index of the hyper link
 	$("a[name='linkRemoveExtraImage']").each(function(index){
		//when the remove button is clicked
		$(this).click(function(){
			removeExtraImage(index);
		})
	});
 	
	 });
	 
	 
 
 	/** function to show extra images of product 
 	
 	index used here refers to the id of extraThumbnail
 	*/
 	function showExtraImageThumbnail(fileInput, index){
	
		var file = fileInput.files[0];
		
			//change the extra image name in case the user
			//change the existing extra image
			fileName = file.name;
			
			//check the existence or presence of the image name by using 
			//the id 
			imageNameHiddenField = $("#imageName" + index);
			if(imageNameHiddenField.length){
				//replace the file name for the hidden field thus existing extra image
				imageNameHiddenField.val(fileName);
			}
		
		
	 		var reader = new FileReader();
	 		reader.onload = function(e){
	 			$("#extraThumbnail" + index).attr("src", e.target.result);
	 		}
 		
 		reader.readAsDataURL(file);
 		
 		//checks if the current index is the last extra images index
 		if(index >= extraImagesCount -1){
			
			//function call
 		addNextExtraImageSection(index + 1);
		}
 		
 		
	}
	
	/** function to add addtional images to the extra images */
 	function addNextExtraImageSection(index){
	
	htmlExtraImage = `
		<div class="col border m-3 p-2" id="divExtraImage${index}">
			<div id="extraImageHeader${index}">
			<label>Extra Image #${index + 1}:</label>
		</div>
		<div class="m-2">
			<img id="extraThumbnail${index}" alt="Extra image #{index + 1} preview" class="img-fluid"
			src="${defaultImageThumbnailSrc}" />
		</div>
		<div>
			<input type="file"  name="extraImage"
			onchange="showExtraImageThumbnail(this, ${index})"
			accept="image/png, image/jpeg">
		</div>
		</div>
	
	
	`;
	
	//${index - 1 indicates that it's the previous section
	htmlLinkRemove = `
		<a class=" fas fa-times-circle fa-2x icon-dark float-right" 
		href="javascript:removeExtraImage(${index - 1})" 
		title="Remove this image"></a>
	
	`;
	
	/** append extral image to the html */
	$("#divProductImages").append(htmlExtraImage);
	
	/** append the  htmlLinkRemove to the preview extra image header*/
	$("#extraImageHeader" + (index - 1)).append(htmlLinkRemove);
	
	extraImagesCount++;
}
 	
 	
 	//function to remove extra images
 	function removeExtraImage(index){
		$("#divExtraImage" + index).remove();
		//extraImagesCount--;
	}
 	
 	