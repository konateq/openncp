package com.gnomon.epsos.service;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.property.TextAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

public class HtmlToPdfConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(EpsosHelperService.class);

    public static ByteArrayOutputStream createPdf(String html) throws IOException {
        ByteArrayOutputStream baos = null;
        html = replaceStylesheet(html);
        try {
            baos = new ByteArrayOutputStream();
            WriterProperties writerProperties = new WriterProperties();
            //Add metadata
            writerProperties.addXmpMetadata();
            PdfWriter pdfWriter = new PdfWriter(baos, writerProperties);

            PdfDocument pdfDoc = new PdfDocument(pdfWriter);
            PageSize pageSize = PageSize.A4;
            pdfDoc.setDefaultPageSize(pageSize);
            pdfDoc.getCatalog().setLang(new PdfString("en-US"));
            //Set the document to be tagged
            pdfDoc.setTagged();
            PdfViewerPreferences pdfViewerPreferences = new PdfViewerPreferences();
            pdfViewerPreferences.setDisplayDocTitle(true);
            pdfDoc.getCatalog().setViewerPreferences(pdfViewerPreferences);

            //Create event-handlers
            String header = "© 2018 Generated by OpenNCP Portal";
            Header headerHandler = new Header(header);
            PageXofY footerHandler = new PageXofY(pdfDoc);

            //Assign event-handlers
            pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE,headerHandler);
            pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE,footerHandler);

            // pdf conversion
            ConverterProperties props = new ConverterProperties();
            FontProvider dfp = new DefaultFontProvider(true, false, false);

            props.setFontProvider(dfp);

            HtmlConverter.convertToDocument(html, pdfDoc, props);
            footerHandler.writeTotal(pdfDoc);
            pdfDoc.close();
        } catch (Exception e) {
            LOGGER.error("Error occurred when converting HTML to PDF", e);
        }
        return baos;
    }

    private static String replaceStylesheet(String html) throws IOException {
        URL url = Resources.getResource("css/pdfstyle.css");
        String css = Resources.toString(url, Charsets.UTF_8);
        return html.replaceFirst("(?s)<style[^>]*>.*?</style>",
                "<style type=\"text/css\">" + css + "</style>");
    }

    //Header event handler
    protected static class Header implements IEventHandler {
        String header;
        public Header(String header) {
            this.header = header;
        }
        @Override
        public void handleEvent(Event event) {
            //Retrieve document and
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdf = docEvent.getDocument();
            PdfPage page = docEvent.getPage();
            Rectangle pageSize = page.getPageSize();
            PdfCanvas pdfCanvas = new PdfCanvas(
                    page.getLastContentStream(), page.getResources(), pdf);
            Canvas canvas = new Canvas(pdfCanvas, pdf, pageSize);
            canvas.setFontSize(8f);
            //Write text at position
            canvas.showTextAligned(header,
                    pageSize.getWidth() - 100,
                    pageSize.getTop() - 30, TextAlignment.CENTER);
        }
    }

    //page X of Y
    protected static class PageXofY implements IEventHandler {
        protected PdfFormXObject placeholder;
        protected float side = 20;
        protected float x = 300;
        protected float y = 25;
        protected float space = 4.5f;
        protected float descent = 3;
        public PageXofY(PdfDocument pdf) {
            placeholder =
                    new PdfFormXObject(new Rectangle(0, 0, side, side));
        }
        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdf = docEvent.getDocument();
            PdfPage page = docEvent.getPage();
            int pageNumber = pdf.getPageNumber(page);
            Rectangle pageSize = page.getPageSize();
            PdfCanvas pdfCanvas = new PdfCanvas(
                    page.getLastContentStream(), page.getResources(), pdf);
            Canvas canvas = new Canvas(pdfCanvas, pdf, pageSize);
            canvas.setFontSize(8f);
            Paragraph p = new Paragraph()
                    .add("Page ").add(String.valueOf(pageNumber)).add(" of");
            canvas.showTextAligned(p, x, y, TextAlignment.RIGHT);
            pdfCanvas.addXObject(placeholder, x + space, y - descent);
            pdfCanvas.release();
        }
        public void writeTotal(PdfDocument pdf) {
            Canvas canvas = new Canvas(placeholder, pdf);
            canvas.setFontSize(8f);
            canvas.showTextAligned(String.valueOf(pdf.getNumberOfPages()),
                    0, descent, TextAlignment.LEFT);
        }
    }
}
