/*
  logout function
*/

$(document).ready(function() {
	$("#logoutLink").on("click", function(e){
		//prevents default behaviour of the form
		e.preventDefault();
		
		//calls the logout form submit
		document.logoutForm.submit();
	});
	
	
	//method call
	customizeDropdownMenu();
});

//function to update the dropdown menu in the 
//page head menu 
function customizeDropdownMenu(){
	//slide down and slide up hover function on the drop down
	//on the displayed logged in user name in the menu. The purpose
	//of this function is show the logout button when you hover on the 
	//user name
	$(".navbar .dropdown").hover(
		function(){
			$(this).find('.dropdown-menu').first().stop(true,true).delay(250).slideDown();
		},
		function(){
			$(this).find('.dropdown-menu').first().stop(true,true).delay(100).slideUp();
		}
	);
	
	$(".dropdown > a").click(function(){
		location.href = this.href;
	});
}

