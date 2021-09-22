package com.shopme.admin.user.export;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;



public class AbstractExporter {

	// method for setting reponseHeader for excel and csv file
		public void setResponseHeader(HttpServletResponse response,
				String contentType, String extension, String prefix) throws IOException {
			// the code below is for generating the file name for the csv file
			// using time stamp

			// creates date format object
			DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

			// creates time stamp that format the current date and time
			String timestamp = dateFormatter.format(new Date());

			//users_ can be any name of your choice
			// creates file name
			//String fileName = "users_" + timestamp + extension;

			String fileName = prefix + timestamp + extension;
			
			// set information for HttpServletResponse for the browser to download the file
			// as csv file
			response.setContentType(contentType);

			// header for the file to be downloaded
			String headerKey = "Content-Disposition";
			String headerValue = "attachment; filename=" + fileName;
			response.setHeader(headerKey, headerValue);

		}
}
