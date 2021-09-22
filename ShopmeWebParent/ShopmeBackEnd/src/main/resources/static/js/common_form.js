/* close the create new user page*/
 	$(document).ready(function(){
 		$("#buttonCancel").on("click", function(){
		/*
			NB this is a static url referece in javascript
			use this type of code when you write your js in the 
			same page
			
			window.location="[[@{/users}]]";
		*/
		
		// Dynamic url reference
		window.location = moduleURL; 
 			
 		});
 		
 		
 		/*for images preview*/
 		$("#fileImage").change(function(){
 		
 	//	/* checks image file size is more than 1MB*/
 	//	fileSize = this.files[0].size;
 		
 	//	//alert("File Size: " + fileSize);
 		
 	//	if(fileSize > MAX_FILE_SIZE){
 	//		this.setCustomValidity("You must an image less than " + MAX_FILE_SIZE + "bytes!");
 	//		this.reportValidity();
 	//	}else{
 	//		this.setCustomValidity("");
 	//		showImageThumbnail(this);
 	//	}
 	
 		if(!checkFileSize(this)){
	
			return true;
		}
 			
 			showImageThumbnail(this);
 			
 		});
 		
 	});
 	
 	function showImageThumbnail(fileInput){
 		var file = fileInput.files[0];
 		var reader = new FileReader();
 		reader.onload = function(e){
 			$("#thumbnail").attr("src", e.target.result);
 		}
 		
 		reader.readAsDataURL(file);
 	}
 	
 		/* function to check image file size */
 	function checkFileSize(fileInput){
	
	/* checks image file size is more than 1MB*/
 		//fileSize = this.files[0].size;
 		fileSize = fileInput.files[0].size;
 		
 		//alert("File Size: " + fileSize);
 		
 		if(fileSize > MAX_FILE_SIZE){
 		//	this.setCustomValidity("You must an image less than " + MAX_FILE_SIZE + "bytes!");
 		//	this.reportValidity();
 		
 		fileInput.setCustomValidity("You must an image less than " + MAX_FILE_SIZE + "bytes!");
 			fileInput.reportValidity();
 			
 			return false;
 		}else{
 		//	this.setCustomValidity("");
 		//	showImageThumbnail(this);
 		
 		fileInput.setCustomValidity("");
 			return true
 		
 		}
}
 	
 	/* function to show modal dialog with warning message*/
 	function showModalDialog(title,message){
 	$("#modalTitle").text(title);
 	$("#modalBody").text(message);
 	$("#modalDialog").modal();
 	}
 	
 	
 	/*functon for showing error message of the modal dialog*/
 	function showErrorModal(message){
 	showModalDialog("Error", message);
 	}
 	
 	/*functon for showing warning message of the modal dialog*/
 	function showWarningModal(message){
 	showModalDialog("Warning", message);
 	}