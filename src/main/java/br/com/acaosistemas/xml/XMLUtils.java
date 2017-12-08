package br.com.acaosistemas.xml;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public final class XMLUtils {

	/**
	 * Recupera do XML o seu namespace
	 * @param xmlText
	 *     Um XML do evento do eSocial
	 * @return
	 *     Uma string que representa o namespace do XML do evento do eSocial
	 */
	public static String getXMLNamespace(StringBuffer xmlText) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        DocumentBuilder builder; 
        Document doc;
        
        Element root;
        String xsdNameSpace = null;
        
        // Habilita a busca de namespaces no XML
        factory.setNamespaceAware(true);
        
        try  
        {  
        	    // Cria um novo documento DOM
            builder = factory.newDocumentBuilder();
            
            // Cria a representacao DOM do XML recebido
            doc = builder.parse(new InputSource(new StringReader(xmlText.toString())));
            
            // Obtem o elemento root do dcoumento XML
            root = doc.getDocumentElement();
            
            // Obtem namespace do elemento root do XML, que nesse caso espera-se que seja
            // o elemento eSocial.
            xsdNameSpace = root.getNamespaceURI();
            
        } catch (Exception e) {  
            e.printStackTrace();  
        }            
		return xsdNameSpace;
	}

}
