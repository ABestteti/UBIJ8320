package br.com.acaosistemas.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.acaosistemas.db.dao.UBIEventosEsocialStageDAO;
import br.com.acaosistemas.db.dao.UBIXsdsDAO;
import br.com.acaosistemas.db.enumeration.StatusEsocialEventosStageEnum;
import br.com.acaosistemas.db.model.UBIEventosEsocialStage;
import br.com.acaosistemas.db.model.UBIXsds;
import br.com.acaosistemas.xml.XMLUtils;
import br.com.acaosistemas.xml.XMLValidator;

/**
 * Classe reposnsavel por validar os XML dos eventos, armazenados na tabela UBI_EVENTOS_ESOCIAL_STAGE,
 * contra os seu respectivo XSD.
 * 
 * <p>
 * <b>Empresa:</b> Acao Sistemas de Informatica Ltda.
 * <p>
 * Alterações:
 * <p>
 * 2018.03.08 - ABS - Adicionado sistema de log com a biblioteca log4j2.
 * 
 * 
 * @author Anderson Bestteti Santos
 *
 */
public class ValidarEventosStage {

	private static final Logger logger = LogManager.getLogger(ValidarEventosStage.class);
	
	/**
	 * Recupera todos os registros que estejom com status "A VALIDAR" na
	 * tabela UBI_EVENTOS_ESOCIAL_STAGE.
	 */
	public void lerRegistrosParaValidacao() {
		UBIEventosEsocialStageDAO    ubesDAO              = new UBIEventosEsocialStageDAO();
		List<UBIEventosEsocialStage> listaUbiEventosStage = new ArrayList<UBIEventosEsocialStage>();
		UBIXsdsDAO                   xsdsDAO              = new UBIXsdsDAO();
		
		XMLValidator                 xmlValidator         = new XMLValidator();
		List<StringBuffer>           xsdList              = new ArrayList<StringBuffer>();
		listaUbiEventosStage = ubesDAO.listUBIEsocialEventosStage(StatusEsocialEventosStageEnum.A_VALIDAR);		
		
		// Inicia a montagem da lista com os XSDs que serao usados para criar o validador do
		// XML do evento. O primeiro item da lista DEVE sempre ser o xmldsig-core-schema.xsd .
		try {
			File xmlDsigFile    = new File("resources/xmldsig-core-schema.xsd");
			Scanner xmlDsigScan = new Scanner(xmlDsigFile);
			
			xsdList.add(new StringBuffer(xmlDsigScan.useDelimiter("\\A").next()));
			
			xmlDsigScan.close();
		} catch (FileNotFoundException e) {
			logger.error(e);
		}

		logger.info("   Validando XMLs da UBI_EVENTOS_ESOCIAL_STAGE...");
		
		for (UBIEventosEsocialStage ubesRow : listaUbiEventosStage) {
			logger.info("     Processando rowId: "+ubesRow.getRowId());
			logger.info("     SeqReq...........: "+ubesRow.getSeqReg());
						
			StringBuffer xmlEvento = new StringBuffer();
			try {
				xmlEvento.append(ubesRow.getXml().getSubString(1, (int) ubesRow.getXml().length()));
			} catch (SQLException e) {
				logger.error(e);
			}
			
			// Recupera o namespace que referencia o XSD do atual XML do evento do eSocial
			// para buscar no banco o documento XSD.
			UBIXsds xsdRow = xsdsDAO.getUBIXsds(XMLUtils.getXMLNamespace(xmlEvento));
			
			// Adiciona o XSD localzado na lista de XSDs
			try {
				xsdList.add(new StringBuffer(xsdRow.getDocumento().getSubString(1, (int) xsdRow.getDocumento().length())));
			} catch (SQLException e) {
				logger.error(e);
			}

			xmlValidator.validate(ubesRow.getIdESocial(), xmlEvento, xsdList);
			
			if (xmlValidator.hasErros()) {
				// O XML apresenta erros de validacao contra o XSD.
				ubesRow.setXmlRetornoValidacao(xmlValidator.getMensagensXmlFormat());
				ubesRow.setStatus(StatusEsocialEventosStageEnum.ERRO_VALIDACAO);
				
				ubesDAO = new UBIEventosEsocialStageDAO();				
				ubesDAO.updateXmlRetornoValidacao(ubesRow);
			} else {
				// O XML passou com sucesso pela validacao com o XSD
				ubesRow.setStatus(StatusEsocialEventosStageEnum.VALIDADO_COM_SUCESSO);
				
				ubesDAO = new UBIEventosEsocialStageDAO();				
				ubesDAO.updateStatus(ubesRow);
			}
						
			// Remove o XSD do XML do evento da lista de XSDs
			xsdList.clear();
		}
		
		logger.info("   Finalizado validacao dos XMLs da UBI_EVENTOS_ESOCIAL_STAGE.");
	}
}
