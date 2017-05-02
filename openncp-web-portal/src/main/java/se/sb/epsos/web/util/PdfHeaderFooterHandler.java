package se.sb.epsos.web.util;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfHeaderFooterHandler extends PdfPageEventHelper {

	@Override
	public void onStartPage(PdfWriter pdfWriter, Document document) {
        PdfPTable headerTbl = new PdfPTable(2);
        headerTbl.setTotalWidth( document.getPageSize().getWidth() );
        headerTbl.setHorizontalAlignment( Element.ALIGN_CENTER );
        
        Chunk myHeaderLeft = new Chunk("Generated by epSOS-Web");
        
        PdfPCell headerCellLeft = new PdfPCell(new Phrase(myHeaderLeft));
        headerCellLeft.setBorder(0);
        headerCellLeft.setHorizontalAlignment( Element.ALIGN_LEFT );
        headerTbl.addCell( headerCellLeft );

        Chunk myHeaderRight = new Chunk("Page " + document.getPageNumber());
        
        PdfPCell headerCellRight = new PdfPCell(new Phrase(myHeaderRight));
        headerCellRight.setBorder(0);
        headerCellRight.setHorizontalAlignment( Element.ALIGN_RIGHT );
        headerTbl.addCell( headerCellRight );
        
        headerTbl.writeSelectedRows(0, -1, 0, document.getPageSize().getHeight(), pdfWriter.getDirectContent());
	}

	@Override
	public void onEndPage(PdfWriter pdfWriter, Document document) {
        PdfPTable footerTbl = new PdfPTable(1);
        footerTbl.setTotalWidth( document.getPageSize().getWidth() );
        footerTbl.setHorizontalAlignment( Element.ALIGN_CENTER );
        
        Chunk myFooter = new Chunk("Copyright © 2011 Apotekensservice AB");
        PdfPCell footer = new PdfPCell(new Phrase(myFooter));
        footer.setBorder(0);
        footer.setHorizontalAlignment( Element.ALIGN_CENTER );

        footerTbl.addCell( footer );

        footerTbl.writeSelectedRows(0, -1, 0, 20, pdfWriter.getDirectContent());
	}
	
}