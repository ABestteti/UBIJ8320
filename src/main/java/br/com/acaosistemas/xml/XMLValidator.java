package br.com.acaosistemas.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

/**
 * Classe responsavel por oferecer o servico de validacao de um XML contra o seu
 * respectivo XSD.
 * 
 * @author Anderson Bestteti Santos
 *
 */
public class XMLValidator {

	private MensagemDeValidacao[] mensagensDeValidacao;
	
	public XMLValidator() {

	}
	
	public StringBuffer getMensagensXmlFormat() {
		
		StringWriter writer                            = new StringWriter();
		List<OcorrenciaValidacao> ocorrenciasValidacao = new ArrayList<OcorrenciaValidacao>();
		OcorrenciasValidacao ocorrenciaValidacaoXML    = new OcorrenciasValidacao();
		
		// Popula a lista de ocorrencias com as mensagens de erro do validor XML
		for (int i = 0; i < getMensagensDeValidacao().length; i++) {
			OcorrenciaValidacao occ = new OcorrenciaValidacao();
			occ.setCodigo(999);
			occ.setDescricao(
					"UBIJ8320\n" +
					getMensagensDeValidacao()[i].getLinha() +
					"; " +
					getMensagensDeValidacao()[i].getColuna() +
					": " +
					getMensagensDeValidacao()[i].getMensagem());
			
			ocorrenciasValidacao.add(occ);
		}
		
		ocorrenciaValidacaoXML.setOcorrenciasValidacao(ocorrenciasValidacao);
		
		// Inicia a construcao do XML propriamente dito
		try {
			// Constroe o XML com base na hierarquia dos classes OcorrenciasValidacao e
		    // OcorrenciaValidacao. Para tanto, cria um constexto JAXB para criar o XML
			// onde o nodo raiz do XML e determinado pela classe OcorrenciasValidacao.class
			JAXBContext context = JAXBContext.newInstance(OcorrenciasValidacao.class);
			
			Marshaller mmarshaller = context.createMarshaller();
			mmarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);			
			mmarshaller.marshal(ocorrenciaValidacaoXML, writer);
			
			System.out.println("XML:\n"+writer.getBuffer().toString());
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Retorna o XML produzido que estar encapsulado no objeto write
		return writer.getBuffer();
	}

	public MensagemDeValidacao[] getMensagensDeValidacao() {
		return mensagensDeValidacao;
	}

	private void setMensagensDeValidacao(MensagemDeValidacao[] mensagensDeValidacao) {
		this.mensagensDeValidacao = mensagensDeValidacao;
	}

	public boolean hasErros() {
		return (getMensagensDeValidacao().length > 0) ? true : false;
	}
	
	/**
	 * Valida um documento XML de acordo com os XSDs informados.
	 * 
	 * @param pXml
	 *            Caminho para o arquivo XML a ser validado.
	 * @param pXSDs
	 *            Caminho para os arquivos XSD utilizados para a validação do
	 *            XML.
	 * @return As mensagens retornadas pela validação do documento.
	 */
	public void validateXML(StringBuffer pXml, ArrayList<StringBuffer> pXSDs) {

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
		int contador = 0;
		for (StringBuffer xsd : pXSDs) {
			streamSourceXSD[contador++] = new StreamSource(new StringReader(xsd.toString()));
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
				
				// Se foram gerados erros de validacao, entao apresenta o resultado obtido
/*				if (errorHandlerValidacaoXml.getMensagensDeValidacao().length > 0) {
					MensagemDeValidacao[] mensagensDeValidacao = errorHandlerValidacaoXml.getMensagensDeValidacao();
					for (int i = 0; i < mensagensDeValidacao.length; i++) {
						System.out.println("Linha...: [" + i + "] " + mensagensDeValidacao[i].getLinha());
						System.out.println("Coluna..: [" + i + "] " + mensagensDeValidacao[i].getColuna());
						System.out.println("Mensagem: [" + i + "] " + mensagensDeValidacao[i].getMensagem());
						System.out.println("========================================================");
					}
				}*/
			} catch (SAXException ex) {
				System.out.println(ex.getMessage());
				/* Obtém as mensagens de erro geradas pelo validador. */
			} catch (IOException ex) {
				System.out.println(ex.getMessage());
			}
		} catch (SAXException ex) {
			System.out.println(ex.getMessage());
		}	
	}


	
	public static void main(String[] args) {
		StringBuffer vXml;
		ArrayList<StringBuffer> vXSDs = new ArrayList<StringBuffer>();
		
		try {
			vXml = new StringBuffer(
					new Scanner(new File("files/ID1001956380001252017072614095503085.xml")).useDelimiter("\\A").next());
			
			vXSDs.add(new StringBuffer(new Scanner(new File("files/xmldsig-core-schema.xsd")).useDelimiter("\\A").next()));
			vXSDs.add(new StringBuffer(new Scanner(new File("files/evtInfoEmpregador.xsd")).useDelimiter("\\A").next()));

			XMLValidator vValidator = new XMLValidator();
			vValidator.validateXML(vXml, vXSDs);
			
			StringBuffer testSb = vValidator.getMensagensXmlFormat();
			
			if (vValidator.hasErros()) {
				System.out.println("XML com erros");
			}
			else {
				System.out.println("XML sem erros");
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}