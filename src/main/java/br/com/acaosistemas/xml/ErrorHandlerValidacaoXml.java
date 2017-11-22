package br.com.acaosistemas.xml;

import java.util.ArrayList;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * Realiza o tratamento de erros durante a validação de um arquivo XML.
 * 
 * <p>
 * <b>Empresa:</b> Ação Sistemas de Informática LTDA.
 * </p>
 * 
 * @author Marcelo Leite
 */
public class ErrorHandlerValidacaoXml implements ErrorHandler {

	/**
	 * Lista de erros encontrados durante a validação.
	 */
	private MensagemDeValidacao[] mensagensDeValidacao;

	private int indice;

	/**
	 * Construtor da classe.
	 */
	public ErrorHandlerValidacaoXml() {
		mensagensDeValidacao = new MensagemDeValidacao[1024];
		indice = 0;
	}

	/**
	 * Elabora a mensagem de validação a partir da exceção obtida.
	 * 
	 * @param Exceção
	 *            ocorrida durante a validação.
	 */
	private MensagemDeValidacao elaborarMensagem(SAXParseException excecao) {
		MensagemDeValidacao resultado = new MensagemDeValidacao(new Integer(excecao.getLineNumber()),
				new Integer(excecao.getColumnNumber()), excecao.getLocalizedMessage());
		return resultado;
	}

	/**
	 * Tratamento realizado quando ocorre um erro durante a validação.
	 * 
	 * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
	 */
	public void error(SAXParseException excecao) {
		mensagensDeValidacao[indice] = elaborarMensagem(excecao);
		indice++;
	}

	/**
	 * Tratamento realizado quando ocorre um erro fatal durante a validação.
	 * 
	 * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
	 */
	public void fatalError(SAXParseException excecao) {
		mensagensDeValidacao[indice] = elaborarMensagem(excecao);
		indice++;
	}

	/**
	 * Tratamento realizado quando ocorre um aviso durante a validação.
	 * 
	 * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
	 */
	public void warning(SAXParseException excecao) {
		mensagensDeValidacao[indice] = elaborarMensagem(excecao);
		indice++;
	}

	/**
	 * Limpa as mensagens de erro de validação armazenados.
	 */
	public void limparErros() {
		indice = 0;
	}

	/**
	 * Retorna as mensagens obtidas durante o tratamento dos erros.
	 * 
	 * @return As mensagens obtidas durante o tratamento dos erros.
	 */
	public MensagemDeValidacao[] getMensagensDeValidacao() {
		ArrayList<MensagemDeValidacao> arrayDeMensagens = new ArrayList<MensagemDeValidacao>();

		for (int i = 0; i < indice; i++) {
			arrayDeMensagens.add(mensagensDeValidacao[i]);
		}

		MensagemDeValidacao[] mensagens = (MensagemDeValidacao[]) arrayDeMensagens
				.toArray(new MensagemDeValidacao[indice]);
		return mensagens;
	}

}
