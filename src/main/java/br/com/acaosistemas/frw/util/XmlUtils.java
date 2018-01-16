package br.com.acaosistemas.frw.util;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/***
 * Classe utilitaria para manipulacao de XML
 * 
 * @author Anderson Bestteti
 *
 */
public final class XmlUtils {

	private static final String XMLNAMESPACE = "xmlns";
	
	/***
	 * Recupera o namespace do nodo root de um XML
	 * @param pXml
	 * XML que sera analizado para retuperar o namespace no elemento root
	 * @return
	 * Uma string que representa a URI do namespace
	 */
	public static String getXsdNameSpace(StringBuffer pXmlDoc) {
		String xsdNameSpace = "";
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
		DocumentBuilder builder = null;
		Document document = null;
		
		try {  
		    builder = factory.newDocumentBuilder(); 
		    document = builder.parse(new InputSource(new StringReader(pXmlDoc.toString())));
		} catch (Exception e) {  
		    e.printStackTrace();  
		} 
		
		xsdNameSpace = document.getElementsByTagName("eSocial").item(1).getNamespaceURI();
		return xsdNameSpace;
	}

}
