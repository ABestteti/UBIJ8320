package br.com.acaosistemas.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import br.com.acaosistemas.db.dao.UBIEventosEsocialStageDAO;
import br.com.acaosistemas.db.dao.UBIXsdsDAO;
import br.com.acaosistemas.db.enumeration.StatusEsocialEventosStageEnum;
import br.com.acaosistemas.db.model.UBIEventosEsStageLog;
import br.com.acaosistemas.db.model.UBIEventosEsocialStage;
import br.com.acaosistemas.db.model.UBIXsds;
import br.com.acaosistemas.xml.XMLUtils;
import br.com.acaosistemas.xml.XMLValidator;

/**
 * Classe reposnsavel por validar os XML dos eventos, armazenados na tabela UBI_EVENTOS_ESOCIAL_STAGE,
 * contra os seu respectivo XSD.
 * 
 * @author Anderson Bestteti Santos
 *
 */
public class ValidarEventosStage {

	/**
	 * Recupera todos os registros que estejom com status "A VALIDAR" na
	 * tabela UBI_EVENTOS_ESOCIAL_STAGE.
	 */
	public void lerRegistrosParaValidacao() {
		UBIEventosEsocialStageDAO    ubesDAO              = new UBIEventosEsocialStageDAO();
		List<UBIEventosEsocialStage> listaUbiEventosStage = new ArrayList<UBIEventosEsocialStage>();
		UBIEventosEsStageLog         ubel                 = new UBIEventosEsStageLog();
		UBIXsdsDAO                   xsdsDAO              = new UBIXsdsDAO();
		
		XMLValidator                 xmlValidator         = new XMLValidator();
		List<StringBuffer>           xsdList              = new ArrayList<StringBuffer>();
		String                       xsdNameSpace;
		
		listaUbiEventosStage = ubesDAO.listUBIEsocialEventosStage(StatusEsocialEventosStageEnum.A_VALIDAR);
				
		// Inicia a montagem da lista com os XSDs que serao usados para criar o validador do
		// XML do evento. O primeiro item da lista DEVE sempre ser o xmldsig-core-schema.xsd .
		try {
			xsdList.add(new StringBuffer(new Scanner(new File("resource/xmldsig-core-schema.xsd")).useDelimiter("\\A").next()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		System.out.println("   Validando XMLs da UBI_EVENTOS_ESOCIAL_STAGE...");
		
		for (UBIEventosEsocialStage ubesRow : listaUbiEventosStage) {
			System.out.println("     Processando rowId: "+ubesRow.getRowId());
						
			StringBuffer xmlEvento = new StringBuffer();
			try {
				xmlEvento.append(ubesRow.getXml().getSubString(1, (int) ubesRow.getXml().length()));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			// Recupera o namespace que referencia o XSD do atual XML do evento do eSocial
			// para buscar no banco o documento XSD.
			UBIXsds xsdRow = xsdsDAO.getUBIXsds(XMLUtils.getXMLNamespace(xmlEvento));
			
			// Adiciona o XSD localzado na lista de XSDs
			xsdList.add(new StringBuffer(xsdRow.getDocumento().toString()));

			xmlValidator.validateXML(xmlEvento, xsdList);	
			
			// Remove o XSD do XML do evento da lista de XSDs
			xsdList.remove(2);
		}
	}
}
