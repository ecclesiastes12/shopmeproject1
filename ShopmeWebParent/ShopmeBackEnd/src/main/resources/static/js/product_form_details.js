$(document).ready(function(){
	$("a[name='linkRemoveDetail']").each(function(index){
		$(this).click(function(){
			removeDetailSectionByIndex(index);
		});
	});
});
function addNextDetailSection(){
	
	// add all div details
	allDivDetails = $("[id^='divDetail']");
	
	//gets the number of div details section on the form
	divDetailsCount = allDivDetails.length;
	
	
	//<input type="text" name="detailIDs" value="0">  the value is always zero to indicate that it's a new detail section'
	htmlDetailSection = `
		<div class="form-inline" id="divDetail${divDetailsCount}">
		<input type="hidden" name="detailIDs" value="0"> 
		<label class="m-3">Name:</label>
		<input type="text" class="form-control w-25" name="detailNames" maxlength="255">
		<label class="m-3">Value:</label>
		<input type="text" class="form-control w-25" name="detailValues" maxlength="255">
	    </div>
	
	`;
	
	
	$("#divProductDetails").append(htmlDetailSection);
	
	//get the previous div detail section
	previousDivDetailSection = allDivDetails.last();
	
	//get id of the previous details section
	previousDivDetailID = previousDivDetailSection.attr("id");
	
	
	htmlLinkRemove = `
		<a class="fas fa-times-circle fa-2x icon-dark m-2" 
		href="javascript:removeDetailSectionById('${previousDivDetailID}')"
		title="Remove this detail"></a>
	
	`;
	
	previousDivDetailSection.append(htmlLinkRemove);
	
	//add focus to the added textfield
	$("input[name='detailNames']").last().focus();
}

//function to remove extra images
 	function removeDetailSectionById(id){
		$("#" + id).remove();
		//extraImagesCount--;
	}
 	
   //function to remove product details text field when the 
   // remove icon is clicked. thus when the form is in the edit mode
   function	removeDetailSectionByIndex(index){
	$("#divDetail" + index).remove();
}