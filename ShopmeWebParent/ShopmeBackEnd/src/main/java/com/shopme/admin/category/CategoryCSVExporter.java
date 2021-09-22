package com.shopme.admin.category;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.shopme.admin.user.export.AbstractExporter;
import com.shopme.common.entity.Category;

public class CategoryCSVExporter extends AbstractExporter{

	//method for writing csv file
	public void export(List<Category> listCategories, HttpServletResponse response) throws IOException {
		
//		//creates date object 
//		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
//		
//		//creates timestamp for the current date and time
//		String timeStamp = dateFormatter.format(new Date());
//		
//		//creates file name
//		String fileName = "categories_" + timeStamp + ".csv";
//		
//		//sets the content type for the file to be exported eg. text/csv, text/pdf
//		response.setContentType("text/csv");
//		
//		//header for the file to be downloaded
//		String headerKey = "Content-Disposition";
//		String headerValue = "attachment; fileName=" + fileName;
//		
//		//sets the header
//		response.setHeader(headerKey, headerValue);
		
		
		super.setResponseHeader(response, "text/csv", ".csv", "categories_");
		
		//creates csv writer from csv library
		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), 
				CsvPreference.STANDARD_PREFERENCE);
		
		//header line of the csv file
		String[] csvHeader = {"Category ID","Category Name"};
		
		//map fields for the actual data to be exported
		//this field mapping must correspond with the class entity variable names
		String[] fieldMapping = {"id", "name"}; 
		
		//writes the csv header
		csvWriter.writeHeader(csvHeader);
		
		//iterate through each of the list of categories
		for(Category category : listCategories) {
			
			category.setName(category.getName().replace("--"," "));
			
			csvWriter.write(category, fieldMapping);
		}
		
		//close the writer
		csvWriter.close();
	}
}
