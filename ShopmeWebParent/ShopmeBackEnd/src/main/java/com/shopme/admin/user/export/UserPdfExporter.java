package com.shopme.admin.user.export;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.shopme.common.entity.User;

public class UserPdfExporter extends AbstractExporter{

	public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "application/pdf", ".pdf","users_" );
		
		//creates document object with A4 page size
		Document document = new Document(PageSize.A4);
		
		//writes document content to the response for browser 
		//to be able to download the file
		PdfWriter.getInstance(document, response.getOutputStream());
		
		//open the document
		document.open();
		
		//creates font 
		Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		font.setSize(18);
		font.setColor(Color.BLUE);
		
		//creates paragraph for document title
		Paragraph paragraph = new Paragraph("List of Users",font);
		
		//align the text to the center
		paragraph.setAlignment(Paragraph.ALIGN_CENTER);
		
		document.add(paragraph);
		
		//creates PDF table with 6 columns
		PdfPTable table = new PdfPTable(6);
		
		//set width percentage for the table. thus the table
		//should cover the entire width of the A4 sheet
		table.setWidthPercentage(100f);
		
		//set width of columns in the table
		table.setWidths(new float[] {1.2f, 3.5f, 3.0f, 3.0f, 3.0f, 1.7f});
		
		//creates space between the paragraph and the column header
		table.setSpacingBefore(10);
		
		//method call 
		writeTableHeader(table);
		writeTableData(table,listUsers);
		
		//adds table to the document
		document.add(table);
		
		//close the document
		document.close();
	}

	//method that writes the actual table data
	private void writeTableData(PdfPTable table, List<User> listUsers) {
		
		for(User user : listUsers) {
			table.addCell(String.valueOf(user.getId()));
			table.addCell(String.valueOf(user.getEmail()));
			table.addCell(String.valueOf(user.getFirstName()));
			table.addCell(String.valueOf(user.getLastName()));
			table.addCell(String.valueOf(user.getRoles()));
			table.addCell(String.valueOf(user.isEnabled()));
		}
	}

	//method that write the table header
	private void writeTableHeader(PdfPTable table) {
		// create pdf cell object
		PdfPCell cell = new PdfPCell();
		
		//sets cell background
		cell.setBackgroundColor(Color.BLUE);
		 
		//sets cell padding
		cell.setPadding(5);
		
		//set font size and font color
		Font font = FontFactory.getFont(FontFactory.HELVETICA);
		
		font.setColor(Color.WHITE);
		
		//sets phrase for the cell thus the first column text
		cell.setPhrase(new Phrase("ID",font));
		
		//adds cell to the table
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("E-mail",font));
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("First Name",font));
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("Last Name",font));
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("Roles",font));
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("Enabled",font));
		table.addCell(cell);
		
		
		
	}

}
