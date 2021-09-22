package com.shopme.admin.user.export;

import java.io.IOException;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.shopme.common.entity.User;

public class UserCsvExporter extends AbstractExporter{

	//method for writing csv file
	public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
		/*
		 * NB commented code moved to setResponseHeader method in AbstractExporter class and modified
		 * 
		 * // the code below is for generating the file name for the csv file //using
		 * time stamp
		 * 
		 * //creates date format object DateFormat dateFormatter = new
		 * SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		 * 
		 * //creates time stamp that format the current date and time String timestamp =
		 * dateFormatter.format(new Date());
		 * 
		 * 
		 * // users_ can be any name of your choice //creates file name. String fileName
		 * = "users_" + timestamp + ".csv";
		 * 
		 * //set information for HttpServletResponse for the browser to download the
		 * file // as csv file response.setContentType("text/csv");
		 * 
		 * //header for the file to be downloaded. String headerKey =
		 * "Content-Disposition"; String headerValue = "attachment; filename=" +
		 * fileName; response.setHeader(headerKey, headerValue);
		 * 
		 */
		super.setResponseHeader(response,"text/csv",".csv","users_");
		
		//creates csv writer from csv library
		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),
				CsvPreference.STANDARD_PREFERENCE);
		
		//header line of the csv file
		String[] csvHeader = {"User ID","E-mail","First Name","Last Name","Role","Enabled"};
		
		//map fields for the actual data to be exported
		//this field mapping must correspond with the class entity variable names
		String[] fieldMapping = {"id","email","firstName","lastName","roles","enabled"};
		
		//writes the header
		csvWriter.writeHeader(csvHeader);
		
		//iterate through each of the list of users
		for(User user : listUsers) {
			csvWriter.write(user, fieldMapping);
		}
		
		//close the writer
		csvWriter.close();
		
	}
}
