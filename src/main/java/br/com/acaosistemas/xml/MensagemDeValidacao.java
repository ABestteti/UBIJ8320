package br.com.acaosistemas.xml;

/**
 * Mensagens elaboradas durante a validacao do documento XML.
 * 
 * <p>
 * <b>Empresa:</b> Acao Sistemas de Informatica Ltda.
 * </p>
 * 
 * @author Marcelo Leite
 */
public class MensagemDeValidacao {

	/**
	 * Linha do documento XML a qual a mensagem se refere.
	 */
	private Integer linha;

	/**
	 * Coluna da linha do documento XML a qual a mensagem se refere.
	 */
	private Integer coluna;

	/**
	 * Mensagem de validacao armazenada.
	 */
	private String mensagem;

	/**
	 * Construtor da mensagem de validacao.
	 * 
	 * @param linha
	 *            Linha do documento XML a qual a mensagem se refere.
	 * @param coluna
	 *            Coluna da linha do documento XML a qual a mensagem se refere.
	 * @param mensagem
	 *            Mensagem a ser armazenada.
	 */
	public MensagemDeValidacao(Integer linha, Integer coluna, String mensagem) {
		this.linha = linha;
		this.coluna = coluna;
		this.mensagem = mensagem;
	}

	/**
	 * Construtor da mensagem de validacao.
	 * 
	 * @param mensagem
	 *            Mensagem a ser armazenada.
	 */
	public MensagemDeValidacao(String mensagem) {
		this.linha = null;
		this.coluna = null;
		this.mensagem = mensagem;
	}

	/**
	 * Retorna a linha do documento XML a qual a mensagem se refere.
	 * 
	 * @return A linha do documento XML a qual a mensagem se refere.
	 */
	public Integer getLinha() {
		return linha;
	}

	/**
	 * Retorna a coluna da linha do documento XML a qual a mensagem se refere.
	 * 
	 * @return A coluna da linha do documento XML a qual a mensagem se refere.
	 */
	public Integer getColuna() {
		return coluna;
	}

	/**
	 * Retorna o conteudo da mensagem de validacao.
	 * 
	 * @return O conteudo da mensagem de validacao.
	 */
	public String getMensagem() {
		return mensagem;
	}

	public String toString() {
		String resultado = "";
		if (linha != null && coluna != null) {
			resultado = "[Linha " + linha + "]: ";
		}

		resultado += mensagem;
		return resultado;
	}
}
