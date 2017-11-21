/**
 * Copyright (c) 2000-2007 Liferay, Inc. All rights reserved.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.gnomon.xslt;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class EpsosXSLTransformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EpsosXSLTransformer.class);

    private static final String MAIN_XSLT = "displaytool/cda.xsl";

    private static String[] filesNeeded = {"epSOSDisplayLabels.xml", "NullFlavor.xml", "SNOMEDCT.xml", "UCUMUnifiedCodeforUnitsofMeasure.xml"};

    private String xmlResourcePath;
    private Transformer transformer;

    public EpsosXSLTransformer(String xmlResourcePath) throws TransformerConfigurationException {

        this.xmlResourcePath = xmlResourcePath;
        URL xslUrl = this.getClass().getClassLoader().getResource(MAIN_XSLT);
        InputStream xslStream = this.getClass().getClassLoader().getResourceAsStream(MAIN_XSLT);
        assert xslUrl != null;
        String systemId = xslUrl.toExternalForm();
        StreamSource xslSource = new StreamSource(xslStream);
        xslSource.setSystemId(systemId);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformer = transformerFactory.newTransformer(xslSource);
        checkLanguageFiles();
    }

    public static String readFile(String file) throws IOException {

        try (FileInputStream fileInputStream = new FileInputStream(file)) {

            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            String ls = System.getProperty("line.separator");
            while ((line = reader.readLine()) != null) {
                // remove a strange character in the beginning of the file
                if (line.length() > 0 && line.codePointAt(0) == 65279)
                    line = line.substring(1);
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
            return stringBuilder.toString();
        }
    }

    public static String readFile(File file) throws IOException {

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            String ls = System.getProperty("line.separator");
            while ((line = reader.readLine()) != null) {
                // remove a strange character in the beginning of the file
                if (line.length() > 0 && line.codePointAt(0) == 65279) {
                    line = line.substring(1);
                }
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
            return stringBuilder.toString();
        }
    }

    /**
     * @param xml        the source cda xml file
     * @param lang       the language you want the labels, value set to be displayed
     * @param actionpath the url that you want to post the dispensation form
     * @return the cda document in html format
     * @throws IOException
     * @throws TransformerException
     */
    public String transform(String xml, String lang, String actionpath, String stylesheetPath)
            throws IOException, TransformerException {

        String output = "";
        StreamSource xmlSource = new StreamSource(new StringReader(xml));
        File resultFile = File.createTempFile("Streams", ".html");
        Result result = new StreamResult(resultFile);
        transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());
        transformer.setParameter("epsosLangDir", xmlResourcePath);
        transformer.setParameter("userLang", lang);
        transformer.setParameter("stylesheet", stylesheetPath);
        if (StringUtils.isNotBlank(actionpath)) {
            transformer.setParameter("actionpath", actionpath);
        }
        transformer.transform(xmlSource, result);
        output = readFile(resultFile.getAbsolutePath());
        boolean fileDeleted = resultFile.delete();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Transformed file has been deleted: '{}'", fileDeleted);
        }
        return output;
    }

    public boolean checkLanguageFiles() {

        boolean filesFound = true;
        File dirFile = new File(this.xmlResourcePath);
        if (!dirFile.exists())
            filesFound = false;
        else {
            for (String aFilesNeeded : filesNeeded) {
                File newFile = new File(this.xmlResourcePath + "/" + aFilesNeeded);
                if (!newFile.exists()) {
                    filesFound = false;
                    break;
                }
            }
        }
        return filesFound;
    }
}
