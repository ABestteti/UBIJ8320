package br.com.acaosistemas.xml;

/**
 * Mensagens elaboradas durante a validação do documento XML.
 * 
 * <p>
 * <b>Empresa:</b> Ação Sistemas de Informática LTDA.
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
	 * Mensagem de validação armazenada.
	 */
	private String mensagem;

	/**
	 * Construtor da mensagem de validação.
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
	 * Construtor da mensagem de validação.
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
	 * Retorna o conteúdo da mensagem de validação.
	 * 
	 * @return O conteúdo da mensagem de validação.
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
