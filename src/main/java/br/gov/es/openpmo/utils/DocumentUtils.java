package br.gov.es.openpmo.utils;

import java.nio.file.Path;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import br.gov.es.openpmo.exception.NegocioException;


public class DocumentUtils {
	
	public static Document convertToXMLDocument(Path path) 
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try
        {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(path.toUri().getRawPath());
            doc.getDocumentElement().normalize();
            return doc;
        } 
        catch (Exception e) {
            e.printStackTrace();
            throw new NegocioException(ApplicationMessage.REPORT_GENERATE_ERROR);
        }
    }
	
}
