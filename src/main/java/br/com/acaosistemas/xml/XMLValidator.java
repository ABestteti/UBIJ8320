package br.com.acaosistemas.xml;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import br.com.acaosistemas.main.Versao;

/**
 * Classe responsavel por oferecer o servico de validacao de um XML contra o seu
 * respectivo XSD.
 * 
 * Caso o metodo {@link #hasErros()} retorne TRUE, significa que foram encontrados 
 * erros de validacao no XML. Nesse caso, utilize o metodo {@link #getMensagensDeValidacao()} 
 * ou {@link #getMensagensXmlFormat()} para obter as mensagens geradas por este validador.
 * 
 * <p>
 * <b>Empresa:</b> Acao Sistemas de Informatica Ltda.
 * </p>
 * 
 * @author Marcelo Leite
 * @author Anderson Bestteti Santos
 *
 */
public class XMLValidator {

	private final int CODIGO_PADRAO = 999;
	private MensagemDeValidacao[] mensagensDeValidacao;
	
	public XMLValidator() {
	}
	
	/***
	 * @return Um StringBuffer com as mensagens de validacao no formato XML.
	 */
	public StringBuffer getMensagensXmlFormat() {
		
		StringWriter writer                            = new StringWriter();
		List<OcorrenciaValidacao> ocorrenciasValidacao = new ArrayList<OcorrenciaValidacao>();
		OcorrenciasValidacao ocorrenciaValidacaoXML    = new OcorrenciasValidacao();
		
		// Popula a lista de ocorrencias com as mensagens de erro do validor XML
		for (int i = 0; i < getMensagensDeValidacao().length; i++) {
			OcorrenciaValidacao occ = new OcorrenciaValidacao();
			occ.setCodigo(CODIGO_PADRAO);
			occ.setDescricao(
					Versao.getStringVersao() +
					" [Linha " + getMensagensDeValidacao()[i].getLinha() +
					";" +
					" Coluna " + getMensagensDeValidacao()[i].getColuna() +
					"]: " +
					getMensagensDeValidacao()[i].getMensagem());
			
			ocorrenciasValidacao.add(occ);
		}
		
		ocorrenciaValidacaoXML.setOcorrenciasValidacao(ocorrenciasValidacao);
		
		// Inicia a construcao do XML propriamente dito
		try {
			// Constroe o XML com base na hierarquia dos classes OcorrenciasValidacao e
		    // OcorrenciaValidacao. Para tanto, cria um contexto JAXB para criar o XML
			// onde o nodo raiz do XML e determinado pela classe OcorrenciasValidacao.class
			JAXBContext context = JAXBContext.newInstance(OcorrenciasValidacao.class);
			
			Marshaller mmarshaller = context.createMarshaller();
			
			// A propriedade "jaxb.fragment=TRUE" evita a geracao da linha
			// com de declaracao do XML: 
			//    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
			mmarshaller.setProperty("jaxb.fragment", Boolean.TRUE);
			
			// Ativa a formatacao do XML produzido.
			mmarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			// Cria o XML com base na hierarquia de classes
			mmarshaller.marshal(ocorrenciaValidacaoXML, writer);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		// Retorna o XML produzido que estar encapsulado no objeto write
		return writer.getBuffer();
	}

	/***
	 * 
	 * @return Um array MensagemDeValidacao com todas as mensagens geradas pelo
	 * validador de XML.
	 */
	public MensagemDeValidacao[] getMensagensDeValidacao() {
		return mensagensDeValidacao;
	}

	/***
	 * Recebe um array do tipo {@link MensgamDeValidacao} para armazenar as mensagens
	 * de validacao geradas pelo validador de XML.
	 * @param mensagensDeValidacao
	 */
	private void setMensagensDeValidacao(MensagemDeValidacao[] mensagensDeValidacao) {
		this.mensagensDeValidacao = mensagensDeValidacao;
	}

	/***
	 * 
	 * @return True se o XML tem erros de validacao. False, caso contrario.
	 */
	public boolean hasErros() {
		return (getMensagensDeValidacao().length > 0) ? true : false;
	}
	
	/**
	 * Valida um documento XML de acordo com os XSDs informados.
	 * 
	 * @param pXml
	 *            XML a ser validado.
	 * @param pXSDs
	 *            Lista de XSDs utilizados para a validacao do
	 *            XML.
	 */
	public void validateXML(StringBuffer pXml, List<StringBuffer> pXSDs) {

		// Cria um array de streamSource para passar como parametro para construcao do schema de
		// validacao do XML. Esse array vai combinar todos os XSDs necessarios para validar um XML.
		StreamSource[] streamSourceXSD = new StreamSource[pXSDs.size()];

		SchemaFactory factoryXSD = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schemaXSD;
		
		/*
		 * Cria o elemento que irá tratar os erros de validação do XML e o
		 * associa ao validador. Dessa forma, o validador de XML do Java
		 * fara a coleta de todas as inconsistencias encontradas ao inves
		 * de parar no primeiro erro encontrado.
		 */
		ErrorHandlerValidacaoXml errorHandlerValidacaoXml = new ErrorHandlerValidacaoXml();
		
		try {
			factoryXSD.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
		} catch (SAXNotRecognizedException e) {
			e.printStackTrace();
		} catch (SAXNotSupportedException e) {
			e.printStackTrace();
		}
		
        // Popula o array de streamSources com os XSDs necessarios para
		// validar um XML.
		int cont = 0;
		for (StringBuffer xsd : pXSDs) {
			streamSourceXSD[cont++] = new StreamSource(new StringReader(xsd.toString()));
		}
		
		try {
			schemaXSD = factoryXSD.newSchema(streamSourceXSD);
			Validator validatorXML = schemaXSD.newValidator();
			
			validatorXML.setErrorHandler(errorHandlerValidacaoXml);
			Source sourceXML = new StreamSource(new StringReader(pXml.toString()));

			try {
				validatorXML.validate(sourceXML);
				
				// Armazena as mensagens de validacao do XML para uso posterios
				setMensagensDeValidacao(errorHandlerValidacaoXml.getMensagensDeValidacao());	
				
			} catch (SAXException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} catch (SAXException ex) {
			ex.printStackTrace();
		}	
	}
}
