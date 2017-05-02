/***    Copyright 2011-2013 Apotekens Service AB <epsos@apotekensservice.se>
 *
 *    This file is part of epSOS-WEB.
 *
 *    epSOS-WEB is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *    epSOS-WEB is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License along with epSOS-WEB. If not, see http://www.gnu.org/licenses/.
 **/
package se.sb.epsos.web.model;

import hl7OrgV3.ClinicalDocumentDocument1;
import org.apache.xmlbeans.XmlException;
import se.sb.epsos.shelob.ws.client.jaxws.EpsosDocument;
import se.sb.epsos.web.service.MetaDocument;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

/**
 *
 */
public class PdfDocument extends CdaDocument {

    private static final long serialVersionUID = -84248995964958326L;
    private ClinicalDocumentDocument1 doc;

    /**
     * @param metaDoc
     * @param bytes
     * @param epsosDocument
     * @throws XmlException
     */
    public PdfDocument(MetaDocument metaDoc, byte[] bytes, EpsosDocument epsosDocument) throws XmlException {
        super(metaDoc, bytes, epsosDocument);
        try {

            this.doc = ClinicalDocumentDocument1.Factory.parse(new ByteArrayInputStream(bytes));
        } catch (IOException ex) {
            throw new XmlException("Failed to parse CDA", ex);
        }
    }

    /**
     * @return
     */
    public byte[] getPdf() {

        return Base64.getDecoder().decode(doc.getClinicalDocument().getComponent().getNonXMLBody().getText().newCursor().getTextValue());
    }
}