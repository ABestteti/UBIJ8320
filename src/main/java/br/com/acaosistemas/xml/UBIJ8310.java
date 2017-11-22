package br.com.acaosistemas.xml;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import oracle.jdbc.OracleDriver;
import oracle.jdbc.driver.OracleConnection;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.CHAR;
import oracle.sql.CLOB;
import oracle.sql.Datum;
import oracle.sql.NUMBER;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;

/**
 * Realiza a validação de arquivos XML através de um XML Schema (XSD).
 * 
 * <p>
 * <b>Empresa:</b> <br>
 * Ação Sistemas de Informática LTDA.
 * </p>
 *
 * @author Marcelo Leite
 * 
 */
public class UBIJ8310 {

	/**
	 * Quantidade mínima de argumentos necessários para executar o programa.
	 */
	private static final int QUANTIDADE_MINIMA_DE_ARGUMENTOS = 5;

	/**
	 * Tipo de dado do banco de dados Oracle utilizado para armazenar o conteúdo
	 * dos documentos XSD que serão utilizados na validação.
	 */
	private static final String T_DOCUMENTOS_XSD_TAB = "T_DOCUMENTOS_XSD_TAB";

	/**
	 * Tipo de dado do banco de dados Oracle utilizado para armazenar as
	 * informações referente a uma mensagem de validação do documento XML.
	 */
	private static final String T_MENSAGEM_DE_VALIDACAO_REC = "T_MENSAGEM_DE_VALIDACAO_REC";

	/**
	 * Tipo de dado do banco de dados Oracle utilizado para armazenar as
	 * mensagens de validação de um documento XML.
	 */
	private static final String T_MENSAGENS_DE_VALIDACAO_TAB = "T_MENSAGENS_DE_VALIDACAO_TAB";

	/**
	 * Nome da classe utilizada para realizar a comunicação com o banco de
	 * dados.
	 */
	private static final String CLASSE_DRIVER_ORACLE = "oracle.jdbc.OracleDriver";

	/**
	 * Versão da classe.
	 */
	private static final String VERSAO = "27.07.2017";

	/**
	 * Conexão com o banco de dados Oracle.
	 */
	private OracleConnection oracleConnection;

	/**
	 * Retorna uma conexão com o banco de dados.
	 * 
	 */
	private OracleConnection getOracleConnection() {
		/* Se ainda não há uma conexão com o banco definida. */
		if (oracleConnection == null) {

			/* Tenta obter a conexão padrão com o banco */
			OracleDriver oracleDriver = new OracleDriver();
			try {
				oracleConnection = (OracleConnection) oracleDriver.defaultConnection();
				return oracleConnection;
			} catch (SQLException sqlException) {
				throw new RuntimeException("Erro ao obter a conexão default do oracle driver.");
			}
		} else {
			return oracleConnection;
		}
	}

	/**
	 * Valida um documento XML de acordo com os XML schemas informados.
	 * 
	 * @param documentoXml
	 *            O documento XML que será validado.
	 * @param arrayDocumentosXsd
	 *            Os XML schemas utilizados na validação do documento.
	 * @return As mensagens de erro retornadas pelo validador.
	 */
	private final ARRAY validarDocumento(CLOB documentoXml, ARRAY arrayDocumentosXsd) {

		if (documentoXml == null) {
			throw new IllegalArgumentException("O documento XML informado é nulo.");
		}

		if (arrayDocumentosXsd == null) {
			throw new IllegalArgumentException("O array de documentos XSD informado é nulo.");
		}

		/* Cria um validador com base nos arquivos XSD informados. */
		Validator validador = criarValidador(arrayDocumentosXsd);

		/*
		 * Cria o elemento que irá tratar os erros de validação do XML e o
		 * associa ao validador.
		 */
		ErrorHandlerValidacaoXml errorHandlerValidacaoXml = new ErrorHandlerValidacaoXml();
		validador.setErrorHandler(errorHandlerValidacaoXml);

		/* Cria um stream source para o arquivo XML. */
		StreamSource streamSourceDocumentoXml;
		try {
			streamSourceDocumentoXml = new StreamSource(documentoXml.getCharacterStream());
		} catch (SQLException sqlException) {
			throw new RuntimeException("Erro ao criar o stream source do documento XML.", sqlException);
		}

		/* Solicita a validação do arquivo XML. */
		try {
			validador.validate(streamSourceDocumentoXml);
		} catch (SAXException saxException) {
			throw new RuntimeException("Erro durante a validação do arquivo XML.", saxException);
		} catch (IOException ioException) {
			throw new RuntimeException("Erro durante a validação do arquivo XML.", ioException);
		}

		/* Obtém as mensagens de erro geradas pelo validador. */
		MensagemDeValidacao[] mensagensDeValidacao = errorHandlerValidacaoXml.getMensagensDeValidacao();

		return criarArrayDeMensagensDeValidacao(mensagensDeValidacao);
	}

	/**
	 * Abre uma conexão com o banco de dados Oracle.
	 * 
	 * @param usuario
	 *            Usuário do banco de dados.
	 * @param senha
	 *            Senha do banco de dados.
	 * @param endereco
	 *            Endereço de acesso ao banco de dados.
	 */
	private final void conectarNoBanco(String usuario, String senha, String endereco) {

		try {
			Class.forName(CLASSE_DRIVER_ORACLE);
		} catch (ClassNotFoundException classNotFoundException) {
			throw new RuntimeException("Não foi possível encontrar a classe \"" + CLASSE_DRIVER_ORACLE + "\".",
					classNotFoundException);
		}

		/*
		 * Abre uma conexão com o banco de dados através das informações
		 * fornecidas.
		 */
		try {
			String urlDoBanco = "jdbc:oracle:thin:@" + endereco;
			oracleConnection = (OracleConnection) DriverManager.getConnection(urlDoBanco, usuario, senha);
		} catch (SQLException sqlException) {
			throw new RuntimeException("Erro ao conectar no banco de dados.", sqlException);
		}
	}

	/**
	 * Cria uma estrutura de dados reconhecido pelo banco de dados Oracle
	 * contendo as informações do array de mensagens de validação.
	 * 
	 * @param mensagensDeValidacao
	 *            Array de mensagens de validação com a qual a estrutura de
	 *            dados será preenchida.
	 * @return Uma estrutura de dados reconhecido pelo banco de dados Oracle
	 *         contendo as informações do array de mensagens de validação.
	 */
	private final ARRAY criarArrayDeMensagensDeValidacao(MensagemDeValidacao[] mensagensDeValidacao) {

		Vector<STRUCT> vetorMensagensDeValidacao = new Vector<STRUCT>();

		StructDescriptor structDescriptor;
		try {
			structDescriptor = new StructDescriptor(T_MENSAGEM_DE_VALIDACAO_REC, getOracleConnection());
		} catch (SQLException sqlException) {
			throw new RuntimeException(
					"Erro ao criar o struct descriptor para o tipo \"" + T_MENSAGEM_DE_VALIDACAO_REC + "\".",
					sqlException);
		}

		for (int contador = 0; contador < mensagensDeValidacao.length; contador++) {
			Vector<Object> vetorMensagemDeValidacao = new Vector<Object>();
			vetorMensagemDeValidacao.addElement(mensagensDeValidacao[contador].getLinha());
			vetorMensagemDeValidacao.addElement(mensagensDeValidacao[contador].getColuna());
			vetorMensagemDeValidacao.addElement(mensagensDeValidacao[contador].getMensagem());

			try {
				vetorMensagensDeValidacao.addElement(
						new STRUCT(structDescriptor, getOracleConnection(), vetorMensagemDeValidacao.toArray()));
			} catch (SQLException sqlException) {
				throw new RuntimeException("Erro ao inserir uma mensagem de validação no vetor.");
			}
		}

		ArrayDescriptor arrayDescriptorMensagensDeValidacaoTab;
		try {
			arrayDescriptorMensagensDeValidacaoTab = ArrayDescriptor.createDescriptor(T_MENSAGENS_DE_VALIDACAO_TAB,
					getOracleConnection());
		} catch (SQLException sqlException) {
			throw new RuntimeException(
					"Erro ao criar o struct descriptor para o tipo \"" + T_MENSAGENS_DE_VALIDACAO_TAB + "\".",
					sqlException);
		}

		ARRAY arrayMensagensDeValidacao = null;
		try {
			arrayMensagensDeValidacao = new ARRAY(arrayDescriptorMensagensDeValidacaoTab, getOracleConnection(),
					vetorMensagensDeValidacao.toArray());
		} catch (SQLException sqlException) {
			throw new RuntimeException("Erro ao criar o array de mensagens de validação.", sqlException);
		}
		return arrayMensagensDeValidacao;
	}

	/**
	 * Cria um elemento CLOB a partir do conteúdo de um arquivo do sistema
	 * operacional.
	 * 
	 * @param caminhoDoArquivo
	 *            Caminho do arquivo a ser lido.
	 * @return Um elemento CLOB com o conteúdo do arquivo.
	 */
	private CLOB criarClobDoArquivo(String caminhoDoArquivo) {
		char[] buffer = new char[1024];
		int totalLido;

		oracle.sql.CLOB clob;
		try {
			clob = CLOB.createTemporary(getOracleConnection(), false, CLOB.DURATION_SESSION);
			clob.open(CLOB.MODE_READWRITE);
			Writer clobWriter = clob.setCharacterStream(1);

			FileReader fileReader = new FileReader(caminhoDoArquivo);
			while (((totalLido = fileReader.read(buffer, 0, 1024)) != -1)) {
				clobWriter.write(buffer, 0, totalLido);
			}
			fileReader.close();

			clobWriter.flush();
			clobWriter.close();
			clob.close();

		} catch (SQLException sqlException) {
			throw new RuntimeException("Erro ao criar o elemento CLOB para o arquivo \"" + caminhoDoArquivo + "\".",
					sqlException);
		} catch (IOException ioException) {
			throw new RuntimeException("Erro ao criar o elemento CLOB para o arquivo \"" + caminhoDoArquivo + "\".",
					ioException);
		}

		return clob;
	}

	/**
	 * Cria o validador que será utilizado para a análise do documento XML.
	 * 
	 * @param arrayDocumentosXsd
	 *            Array de documentos XSD utilizados para elaborar o schema de
	 *            validação.
	 * @return O schema que deve ser utilizado para validar o documento XML.
	 */
	private final Validator criarValidador(ARRAY arrayDocumentosXsd) {
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		try {
			schemaFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
		} catch (SAXNotRecognizedException excecao) {
			throw new RuntimeException("Erro ao criar o validador do XML.", excecao);
		} catch (SAXNotSupportedException excecao) {
			throw new RuntimeException("Erro ao criar o validador do XML.", excecao);
		}

		CLOB[] clobsDocumentosXsd;
		try {
			clobsDocumentosXsd = (CLOB[]) arrayDocumentosXsd.getArray();
		} catch (SQLException sqlException) {
			throw new RuntimeException("Erro ao obter o vetor de CLOBs dos documentos XSD.", sqlException);
		}

		StreamSource[] streamSourceDocumentosXsd = new StreamSource[clobsDocumentosXsd.length];
		for (int contador = 0; contador < clobsDocumentosXsd.length; contador++) {
			Reader readerDocumentoXsd;
			CLOB clobDocumentoXsd = clobsDocumentosXsd[contador];

			try {
				readerDocumentoXsd = clobDocumentoXsd.getCharacterStream();
			} catch (SQLException sqlException) {
				throw new RuntimeException("Erro ao criar obter o character stream do documento XSD.", sqlException);
			}

			streamSourceDocumentosXsd[contador] = new StreamSource(readerDocumentoXsd);
		}

		Schema schema = null;
		try {
			schema = schemaFactory.newSchema(streamSourceDocumentosXsd);
		} catch (org.xml.sax.SAXException excecao) {
			throw new RuntimeException("Erro ao criar o schema para a validação do documento.", excecao);
		}

		Validator validador = schema.newValidator();

		return validador;
	}

	/**
	 * Desconecta do banco de dados.
	 */
	private final void desconectarDoBanco() {
		if (oracleConnection != null) {
			try {
				oracleConnection.close();
			} catch (SQLException sqlException) {
				throw new RuntimeException("Erro ao desconectar do banco.", sqlException);
			}
		}
	}

	/**
	 * Retorna o array Java de mensagens de validação obtido com base no array
	 * Oracle de mensagens de validação.
	 * 
	 * @param arrayMensagensDeValidacao
	 *            Mensagens obtidas durante a validação do documento XML.
	 */
	private MensagemDeValidacao[] obterArrayDeMensagensDeValidacao(ARRAY arrayMensagensDeValidacao) {

		/*
		 * Obtém a estrutura (Oracle table) que armazena as mensagens de
		 * validação.
		 */
		Datum[] datumMensagensDeValidacao;
		try {
			datumMensagensDeValidacao = arrayMensagensDeValidacao.getOracleArray();
		} catch (SQLException sqlException) {
			throw new RuntimeException("Erro ao obter as estruturas de mensagens de validação.", sqlException);
		}

		NUMBER numberLinha;
		NUMBER numberColuna;
		CHAR charMensagem;

		Integer linha;
		Integer coluna;
		String mensagem;

		MensagemDeValidacao[] mensagensDeValidacao = new MensagemDeValidacao[datumMensagensDeValidacao.length];

		/* Obtém as mensagens de validação armazenadas na tabela. */
		for (int contador = 0; contador < datumMensagensDeValidacao.length; contador++) {
			STRUCT structMensagemDeValidacao = (STRUCT) datumMensagensDeValidacao[contador];

			Datum[] elementosDoObjectMensagem;
			try {
				elementosDoObjectMensagem = structMensagemDeValidacao.getOracleAttributes();

				numberLinha = (NUMBER) elementosDoObjectMensagem[0];
				numberColuna = (NUMBER) elementosDoObjectMensagem[1];
				charMensagem = (CHAR) elementosDoObjectMensagem[2];

				linha = numberLinha.intValue();
				coluna = numberColuna.intValue();
				mensagem = charMensagem.getString();

				mensagensDeValidacao[contador] = new MensagemDeValidacao(linha, coluna, mensagem);

			} catch (SQLException sqlException) {
				throw new RuntimeException("Erro ao obter os elementos que compõem a mensagem de validação.",
						sqlException);
			}
		}

		return mensagensDeValidacao;
	}

	/**
	 * Valida um documento XML de acordo com os XML schemas informados.
	 * 
	 * @param caminhoArquivoXml
	 *            Caminho para o arquivo XML a ser validado.
	 * @param caminhosArquivosXsd
	 *            Caminho para os arquivos XSD utilizados para a validação do
	 *            XML.
	 * @return As mensagens retornadas pela validação do documento.
	 */
	private final MensagemDeValidacao[] validarDocumento(String caminhoArquivoXml, String[] caminhosArquivosXsd) {

		/* Cria os CLOBs dos arquivos XSD. */
		CLOB[] clobsDocumentosXsd = new CLOB[caminhosArquivosXsd.length];
		for (int contador = 0; contador < caminhosArquivosXsd.length; contador++) {
			CLOB clobDocumentoXsd = criarClobDoArquivo(caminhosArquivosXsd[contador]);
			clobsDocumentosXsd[contador] = clobDocumentoXsd;
		}

		/*
		 * Cria o descritor do tipo de dado Oracle que armazena os CLOBs dos
		 * arquivos XSD.
		 */
		ArrayDescriptor arrayDescriptorDocumentosXsd;
		try {
			arrayDescriptorDocumentosXsd = ArrayDescriptor.createDescriptor(T_DOCUMENTOS_XSD_TAB, oracleConnection);
		} catch (SQLException sqlException) {
			throw new RuntimeException("Erro ao criar o descritor para o tipo \"" + T_DOCUMENTOS_XSD_TAB + "\".",
					sqlException);
		}

		/* Cria o elemento Oracle que armazena os CLOBs dos arquivos XSD. */
		ARRAY arrayDocumentosXsd;
		try {
			arrayDocumentosXsd = new ARRAY(arrayDescriptorDocumentosXsd, oracleConnection, clobsDocumentosXsd);
		} catch (SQLException sqlException) {
			throw new RuntimeException("Erro ao criar o array para armazenar os documentos XSD.", sqlException);
		}

		/* Cria o CLOB do documento XML. */
		CLOB documentoXml = criarClobDoArquivo(caminhoArquivoXml);

		/*
		 * Solicita a validação do documento utilizando os tipos de dados
		 * Oracle.
		 */
		ARRAY arrayMensagensDeValidacao = validarDocumento(documentoXml, arrayDocumentosXsd);

		/*
		 * Obtém o array de mensagens de validação a partir do resultado da
		 * validação.
		 */
		MensagemDeValidacao[] mensagensDeValidacao = obterArrayDeMensagensDeValidacao(arrayMensagensDeValidacao);

		return mensagensDeValidacao;
	}

	public static ARRAY validarDocumentoXml(CLOB documentoXML, ARRAY arrayDocumentosXSD) {

		UBIJ8310 ubij8050 = new UBIJ8310();

		ARRAY arrayMensagensDeValidacao = ubij8050.validarDocumento(documentoXML, arrayDocumentosXSD);

		return arrayMensagensDeValidacao;
	}

	/**
	 * Retorna a versão do programa.
	 * 
	 * @return A versão do programa.
	 */
	public static String getVersao() {
		return VERSAO;
	}

	/**
	 * Entrada do programa.
	 * 
	 * @param args
	 *            Argumentos para a execução do programa.
	 */
	public static void main(String[] args) {

		String usuario = null;
		String senha = null;
		String endereco = null;
		String caminhoArquivoXml = null;
		String[] caminhosArquivosXsd = null;

		if (args.length < QUANTIDADE_MINIMA_DE_ARGUMENTOS) {
			System.err.println(obterUtilizacaoDoPrograma());
			System.exit(1);
		} else {
			usuario = args[0];
			senha = args[1];
			endereco = args[2];
			caminhoArquivoXml = args[3];

			caminhosArquivosXsd = new String[args.length - (QUANTIDADE_MINIMA_DE_ARGUMENTOS - 1)];

			for (int contador = (QUANTIDADE_MINIMA_DE_ARGUMENTOS - 1); contador < args.length; contador++) {
				caminhosArquivosXsd[contador - (QUANTIDADE_MINIMA_DE_ARGUMENTOS - 1)] = args[contador];
			}
		}

		System.out.println(UBIJ8310.class.getSimpleName() + " - Versão: " + getVersao());

		UBIJ8310 ubij8050 = new UBIJ8310();

		ubij8050.conectarNoBanco(usuario, senha, endereco);

		MensagemDeValidacao[] mensagensDeValidacao = ubij8050.validarDocumento(caminhoArquivoXml, caminhosArquivosXsd);

		ubij8050.desconectarDoBanco();

		if (mensagensDeValidacao.length > 0) {
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append(mensagensDeValidacao.length + " ");
			stringBuffer.append(mensagensDeValidacao.length == 1 ? "erro encontrado " : "erros encontrado ");
			stringBuffer.append("no arquivo \"" + caminhoArquivoXml + "\".");
			System.out.println(stringBuffer);
			for (MensagemDeValidacao mensagemDeValidacao : mensagensDeValidacao) {
				System.out.println(mensagemDeValidacao);
			}
		} else {
			System.out.println("Nenhum erro encontrado no arquivo \"" + caminhoArquivoXml + "\".");
		}
	}

	/**
	 * Retorna a mensagem de utilização do programa.
	 * 
	 * @return A mensagem de utilização do programa.
	 */
	private static final String obterUtilizacaoDoPrograma() {
		StringBuffer stringBuffer = new StringBuffer();

		stringBuffer.append("UBIJ8050 - Validador de arquivos XML versão " + getVersao() + ".\n\n");
		stringBuffer.append("Utilização do programa:\n");
		stringBuffer.append(UBIJ8310.class.getSimpleName()
				+ " {usuario} {senha} {enderecoDoBanco} {arquivoXML} {arquivosXSD...}\n\n");
		stringBuffer.append("Onde:\n");
		stringBuffer.append("\tusuario         - Nome do usuário no banco de dados.\n");
		stringBuffer.append("\tsenha           - Senha do usuário.\n");
		stringBuffer.append("\tenderecoDoBanco - Endereço de conexão com o banco de dados.\n");
		stringBuffer.append("\tarquivoXML      - Caminho para o arquivo XML a ser validado.\n");
		stringBuffer.append("\tarquivosXSD     - Caminho para os arquivos XSDs com os quais o XML será validado.\n");

		return stringBuffer.toString();
	}

}
