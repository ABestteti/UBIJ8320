package br.com.acaosistemas.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import br.com.acaosistemas.main.Versao;
import br.com.acaosistemas.xml.retornoevento.jaxb.ESocial;
import br.com.acaosistemas.xml.retornoevento.jaxb.ESocial.RetornoEvento;
import br.com.acaosistemas.xml.retornoevento.jaxb.TDadosProcessamentoEvento;
import br.com.acaosistemas.xml.retornoevento.jaxb.TOcorrencias;
import br.com.acaosistemas.xml.retornoevento.jaxb.TOcorrencias.Ocorrencia;

/**
 * Classe responsavel por oferecer o servico de validacao de um XML contra o seu
 * respectivo XSD.
 * 
 * Caso o metodo {@link #hasErros()} retorne TRUE, significa que foram encontrados 
 * erros de validacao no XML. Nesse caso, utilize o metodo {@link #getMensagensDeValidacao()} 
 * ou {@link #getMensagensXmlFormat()} para obter as mensagens geradas por este validador.
 * <p>
 * Referencia: http://codippa.com/how-to-create-xml-from-java-objects-using-jaxb/
 * <p>
 * <p>
 * As classes dos packages br.com.acaosistemas.xml.retornoevento.jaxb e
 * br.com.acaosistemas.xml.retornoloteevento.jaxb sao geradas com a ferramenta xjc do Java.
 * As referencia acima apresenta um tutorial de como utilizar o xjc para criar as classes
 * Java a partir de um arquivo XSD.
 * <p>
 * A partir das classes geradas podemos criar uma representacao do XML em memoria para
 * depois grava-lo em arquivo ou em banco de dados. Essa transformacao chamase "marshalling"
 * <p>
 * <p>
 * <b>Empresa:</b> Acao Sistemas de Informatica Ltda.
 * </p>
 * 
 * @author Marcelo Leite
 * @author Anderson Bestteti Santos
 *
 */
public class XMLValidator {

	private static final int   CODIGO_PADRAO = 999;
	private static final short TIPO_ERRO     = 1;
	private MensagemDeValidacao[] mensagensDeValidacao;
	private String idESocial;
	
	public XMLValidator() {
	}
	
	/***
	 * @return Um StringBuffer com as mensagens de validacao no formato XML.
	 */
	public StringBuffer getMensagensXmlFormat() {
		
		StringWriter retornoEvento                  = new StringWriter();
		StringWriter retornoProcessamentoLoteEvento = new StringWriter();
		TOcorrencias ocorrenciasJaxB                = new TOcorrencias();
				
		// Popula a lista de ocorrencias com as mensagens de erro do validor XML
		for (int i = 0; i < getMensagensDeValidacao().length; i++) {
			Ocorrencia ocorrenciaJaxB = new Ocorrencia();
			
			ocorrenciaJaxB.setTipo(TIPO_ERRO);
			ocorrenciaJaxB.setCodigo(CODIGO_PADRAO);
			ocorrenciaJaxB.setDescricao(
					Versao.getStringVersao() +
					" [Linha " + getMensagensDeValidacao()[i].getLinha() +
					";" +
					" Coluna " + getMensagensDeValidacao()[i].getColuna() +
					"]: " +
					getMensagensDeValidacao()[i].getMensagem());
			
			ocorrenciasJaxB.getOcorrencia().add(ocorrenciaJaxB);
		}
		
		TDadosProcessamentoEvento processamentoEventoJaxB = new TDadosProcessamentoEvento();
		processamentoEventoJaxB.setCdResposta(CODIGO_PADRAO);
		processamentoEventoJaxB.setDescResposta("Erro na validaÁ„o do xml do evento contra o seu xsd.");
		processamentoEventoJaxB.setOcorrencias(ocorrenciasJaxB);
		processamentoEventoJaxB.setVersaoAppProcessamento(Versao.getStringVersao());

		try {
			processamentoEventoJaxB.setDhProcessamento(
					DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
		} catch (DatatypeConfigurationException e1) {
			e1.printStackTrace();
		}

		// Construcao da ramificacao <retornoEvento>, definida no
		// XML de processamento do lote de eventos.
		RetornoEvento retornoEventoJaxB = new RetornoEvento();
		
		// Adiciona as ocorrencias de validacao do XML do evento na ramificacao
		// <retornoEvento>
		retornoEventoJaxB.setProcessamento(processamentoEventoJaxB);
		
		// Adiciona o atributo Id da tag <retornoEvento>
		retornoEventoJaxB.setId(idESocial);
		
		// Prepara a criacao da tag <recepcao> no XML do evento.
		br.com.acaosistemas.xml.retornoevento.jaxb.TDadosRecepcao 

		// Cria a tag <dhRecepcao> na ramifacacao <recepcao>
		dadosRecepcao = new br.com.acaosistemas.xml.retornoevento.jaxb.TDadosRecepcao();
		dadosRecepcao.setDhRecepcao(processamentoEventoJaxB.getDhProcessamento());
		
		// Adiciona a ramificacao <recepcao> no XML de processamento do
		// evento.
		retornoEventoJaxB.setRecepcao(dadosRecepcao);
		
		// Preparacao do XML do evento, o qual armazena as ocorrencias de validacao.
		ESocial esocialEventoJaxB = new ESocial();
		esocialEventoJaxB.setRetornoEvento(retornoEventoJaxB);
		
		// Construcao do XML (marshalling) da validacao do evento propriamente dito.
		// TAG de referencia: <retornoEvento>
		try {
			// Constroe o XML com base na hierarquia dos classes OcorrenciasValidacao e
		    // OcorrenciaValidacao. Para tanto, cria um contexto JAXB para criar o XML
			// onde o nodo raiz do XML e determinado pela classe ESocial.class
			// do package br.com.acaosistemas.xml.retornoevento.jaxb.
			JAXBContext context = JAXBContext.newInstance(
					br.com.acaosistemas.xml.retornoevento.jaxb.ESocial.class);
			
			Marshaller mmarshaller = context.createMarshaller();
			
			// A propriedade "jaxb.fragment=TRUE" evita a geracao da linha
			// com de declaracao do XML: 
			//    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
			mmarshaller.setProperty("jaxb.fragment", Boolean.TRUE);
			
			// Desativa a formatacao do XML produzido.
			mmarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
			
			// Cria o XML com base na hierarquia de classes da secao de processamento
			// do evento, onde sao registradas as mensagens de validacao do XML.
			mmarshaller.marshal(esocialEventoJaxB, retornoEvento);
			
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		// Construcao da representacao do XML das ocorrencias do evento no formato
		// DOM para permitir adicionar esse XML no XML do processamento do lote de
		// eventos. O objeto docXmlOcorrenciasEvento e passado como parametro para
		// metodo setAny da classe TArquivoEsocial.
		DocumentBuilderFactory dbf       = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder       = null;
		Document docXmlOcorrenciasEvento = null;

		try {
			docBuilder = dbf.newDocumentBuilder();
			docXmlOcorrenciasEvento = docBuilder.parse(
					new ByteArrayInputStream(
							retornoEvento.toString().getBytes(
									StandardCharsets.UTF_8)));
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}		
		
		// Construcao da ramificacao <retornoEvento> para encampsular o XML das
		// ocorrencias de validacao do XML do evento, que estao armazenadas no objeto
		// docXmlOcorrenciasEvento.
		br.com.acaosistemas.xml.retornoloteevento.jaxb.TArquivoEsocial 
		   esocialOcorrenciasEventoJaxB = new br.com.acaosistemas.xml.retornoloteevento.jaxb.TArquivoEsocial();
		
		// Prepara as ocorrencias da validacao para adicionar na ramificacao <evento>
		// do XML do processamento do lote de eventos.
		// TAG de referencia: <retornoEvento>
		esocialOcorrenciasEventoJaxB.setAny(docXmlOcorrenciasEvento.getDocumentElement());

		// Adiciona as ocorrencias da validacao do XML do evento na ramificacao <evento>
		// TAG de referencia: <evento>
		br.com.acaosistemas.xml.retornoloteevento.jaxb.ESocial.RetornoProcessamentoLoteEventos.RetornoEventos.Evento 
		   esocialLoteEventoJaxB = new br.com.acaosistemas.xml.retornoloteevento.jaxb.ObjectFactory().createESocialRetornoProcessamentoLoteEventosRetornoEventosEvento();
		esocialLoteEventoJaxB.setRetornoEvento(esocialOcorrenciasEventoJaxB);
		esocialLoteEventoJaxB.setId(idESocial);
		
		// Prepara a criacao da ramificacao <retornoEventos>
		// TAG de referencia: <retornoEventos>
		br.com.acaosistemas.xml.retornoloteevento.jaxb.ESocial.RetornoProcessamentoLoteEventos.RetornoEventos
		   esocialLoteRetornoEventosJaxB = new br.com.acaosistemas.xml.retornoloteevento.jaxb.ObjectFactory().createESocialRetornoProcessamentoLoteEventosRetornoEventos();
		esocialLoteRetornoEventosJaxB.getEvento().add(esocialLoteEventoJaxB);
		
		// Prepara a criacao da ramificacao <retornoProcessamentoLoteEventos>
		// TAG de referencia: <retornoProcessamentoLoteEventos>
		br.com.acaosistemas.xml.retornoloteevento.jaxb.ESocial.RetornoProcessamentoLoteEventos
		   esocialRetornoProcessamentoLoteEventosJaxB = new br.com.acaosistemas.xml.retornoloteevento.jaxb.ObjectFactory().createESocialRetornoProcessamentoLoteEventos();
		
        // Adiciona a ramificacao <retornoEventos> na ramificacao <retornoProcessamentoLoteEventos>
		// TAG de referencia: <retornoProcessamentoLoteEventos>
		esocialRetornoProcessamentoLoteEventosJaxB.setRetornoEventos(esocialLoteRetornoEventosJaxB);

		// Prepara a criacao da tag de grupo <status> do XML de processamento do lote
		// de eventos.
		// TAG de referencia: <status>
		br.com.acaosistemas.xml.retornoloteevento.jaxb.TStatus
		   esocialStatusLoteEventosJaxB = new br.com.acaosistemas.xml.retornoloteevento.jaxb.ObjectFactory().createTStatus();
		esocialStatusLoteEventosJaxB.setCdResposta(CODIGO_PADRAO);
		esocialStatusLoteEventosJaxB.setDescResposta(
				"Lote com erros de validaÁ„o no XML do evento contra o seu respectivo XSD.");

		// Adiciona a tag de grupo <status> na ramificacao <retornoProcessamentoLoteEventos>
		// TAG de referencia: <status>
		esocialRetornoProcessamentoLoteEventosJaxB.setStatus(esocialStatusLoteEventosJaxB);
		
		// Prepara a criacao do XML de retorno do processamento do lote de eventos
		// TAG de referencia: <eSocial>
		br.com.acaosistemas.xml.retornoloteevento.jaxb.ESocial
		   esocialRetornoProcessamentoLoteJaxB = new br.com.acaosistemas.xml.retornoloteevento.jaxb.ObjectFactory().createESocial();
		
		// Adiciona a ramificacao <retornoProcessamentoLoteEventos> na ramificacao raiz <eSocial>
		// TAG de referencia: <eSocial>
		esocialRetornoProcessamentoLoteJaxB.setRetornoProcessamentoLoteEventos(esocialRetornoProcessamentoLoteEventosJaxB);

		// Cria o XML (marshalling) do processamento do lote de eventos a partir da hierarquia de classes
		// escpasuladas no objeto esocialRetornoProcessamentoLoteJaxB.
		try {
			// Constroe o XML com base na hierarquia de classes ESocial.
			// Para tanto, cria um contexto JAXB para criar o XML
			// onde o nodo raiz do XML e determinado pela classe ESocial.class
			// do package br.com.acaosistemas.xml.retornoloteevento.jaxb.
			JAXBContext context = JAXBContext.newInstance(
					br.com.acaosistemas.xml.retornoloteevento.jaxb.ESocial.class);
			
			Marshaller mmarshaller = context.createMarshaller();
						
			// Ativa a formatacao do XML produzido.
			mmarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			// Cria o XML com base na hierarquia de classes encapsuladas no objeto
			// esocialRetornoProcessamentoLoteJaxB.
			mmarshaller.marshal(
					esocialRetornoProcessamentoLoteJaxB,
					retornoProcessamentoLoteEvento);			
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		
		// Retorna o XML produzido
		return retornoProcessamentoLoteEvento.getBuffer();
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
	 * @param pIdESocial 
	 *            O Id Esocial do XML do evento que sera validado. Essa informacao sera usada
	 *            na construcao do XML do retorno do processamento do lote de ventos, gerado
	 *            pelo validador de XML do eSocial.
	 * @param pXml
	 *            XML a ser validado.
	 * @param pXSDs
	 *            Lista de XSDs utilizados para a validacao do
	 *            XML.
	 */
	public void validate(String pIdESocial, StringBuffer pXml, List<StringBuffer> pXSDs) {

		// Cria um array de streamSource para passar como parametro para construcao do schema de
		// validacao do XML. Esse array vai combinar todos os XSDs necessarios para validar um XML.
		StreamSource[] streamSourceXSD = new StreamSource[pXSDs.size()];

		SchemaFactory factoryXSD = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schemaXSD;
		// Salva o ID do evento
		this.idESocial = pIdESocial;
		/*
		 * Cria o elemento que ir√° tratar os erros de valida√ß√£o do XML e o
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
