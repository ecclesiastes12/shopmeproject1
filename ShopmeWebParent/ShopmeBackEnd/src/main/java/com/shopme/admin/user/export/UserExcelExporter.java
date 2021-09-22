package com.shopme.admin.user.export;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.shopme.common.entity.User;

public class UserExcelExporter extends AbstractExporter{


	XSSFWorkbook workbook;
	XSSFSheet sheet;

	public UserExcelExporter() {
		//create excel workbook object
		workbook = new XSSFWorkbook();
	}
	
	//method to write the header lines thus column header in excel file
	private void writeHeaderLine() {
		//Users is the name given to the excel sheet
				//create excel sheet
		sheet = workbook.createSheet("Users");
		//creates new row from sheet with index 0
				XSSFRow row = sheet.createRow(0);
				
				//creates new excel cell style object
				XSSFCellStyle cellStyle = workbook.createCellStyle();
				
				//creates new excel font style
				XSSFFont font = workbook.createFont();
				font.setBold(true);
				font.setFontHeight(14); //font height
				//set font for the cellStyle
				cellStyle.setFont(font);
				
				//method call
				createCell(row, 0, "User ID", cellStyle);
				createCell(row, 1, "E-mail", cellStyle);
				createCell(row, 2, "First Name", cellStyle);
				createCell(row, 3, "Last Name", cellStyle);
				createCell(row, 4, "Roles", cellStyle);
				createCell(row, 5, "Enabled", cellStyle);
				
	}
	
	//method for creating excel cell
	private void createCell(XSSFRow row, int columnIndex, Object value, CellStyle style) {
		//create new cell with index 0
				XSSFCell cell = row.createCell(columnIndex);
				
				//makes the values auto-fit the width of the column
				sheet.autoSizeColumn(columnIndex);
				
				//checks the dataType of the setCellValue(value)
				if(value instanceof Integer) {
					//set value for the cell
					cell.setCellValue((Integer) value);
				}else if(value instanceof Boolean) {
					//set value for the cell
					cell.setCellValue((Boolean) value);
				}else if(value instanceof String) {
					//set value for the cell
					cell.setCellValue((String) value);
				}
				
				//set cellStyle for cell
				cell.setCellStyle(style);
	}
	
	
	
	// method for writing excel file
	public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
		
		super.setResponseHeader(response, "application/octet-stream", ".xlsx","users_");
//
//		//create excel workbook object
//		XSSFWorkbook workbook = new XSSFWorkbook();
//		
//		//Users is the name given to the excel sheet
//		//create excel sheet
//		XSSFSheet sheet = workbook.createSheet("Users");
//		
//		//creates new row from sheet with index 0
//		XSSFRow row = sheet.createRow(0);
//		
//		//creates new excel cell style object
//		XSSFCellStyle cellStyle = workbook.createCellStyle();
//		
//		//creates new excel font style
//		XSSFFont font = workbook.createFont();
//		font.setBold(true);
//		font.setFontHeight(16); //font height
//		//set font for the cellStyle
//		cellStyle.setFont(font);
//		
//		
//		//create new cell with index 0
//		XSSFCell cell = row.createCell(0);
//		
//		//set value for the cell
//		cell.setCellValue("User ID");
//		//set cellStyle for cell
//		cell.setCellStyle(cellStyle);
//		
		writeHeaderLine();
		writeDataLines(listUsers);
		
		//outputStream from the ServletResponse
		ServletOutputStream outputStream = response.getOutputStream();
		
		//write workbook to outputStream
		workbook.write(outputStream);
		
		//close workbook
		workbook.close();
		
		//close outputStream
		outputStream.close();
	}

	private void writeDataLines(List<User> listUsers) {
		//NB the actual data starts with row index 1 (cell 1) 
		// because index 0 (cell 0) is occupied by column headers (writeHeaderLine)
		int rowIndex = 1;
		
		//creates new excel cell style object
		XSSFCellStyle cellStyle = workbook.createCellStyle();
		
		//creates new excel font style
		XSSFFont font = workbook.createFont();
		font.setBold(false);
		font.setFontHeight(12); //font height
		//set font for the cellStyle
		cellStyle.setFont(font);
		
		for(User user : listUsers) {
			XSSFRow row = sheet.createRow(rowIndex++);
			int columnIndex = 0;
			
			createCell(row, columnIndex++, user.getId(),cellStyle );
			createCell(row, columnIndex++, user.getEmail(),cellStyle );
			createCell(row, columnIndex++, user.getFirstName(),cellStyle );
			createCell(row, columnIndex++, user.getLastName(),cellStyle );
			createCell(row, columnIndex++, user.getRoles().toString(),cellStyle );
			createCell(row, columnIndex++, user.isEnabled(),cellStyle );
		}
	}
}
