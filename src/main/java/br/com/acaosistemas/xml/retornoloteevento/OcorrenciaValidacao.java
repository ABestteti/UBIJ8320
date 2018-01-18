package br.com.acaosistemas.xml.retornoloteevento;

import javax.xml.bind.annotation.XmlElement;

/***
 * Classe para formatar a ramaficacao do XML do retorno do processamento do lote de
 * eventos do eSocial, com as mensagens de critica do XML do evento contra o seu respectivo XSD.
 * Referencia: http://codippa.com/how-to-create-xml-from-java-objects-using-jaxb/
 * 
 * Essa ramificacao esta documentada no Manual de Orientacao do Desenvolvedor do eSocial
 * na secao "Leiaute Mensagem de Retorno do Processamento do Lote", ramificacao "ocorrencia".
 *
 * <p>
 * <b>Empresa:</b> Acao Sistemas de Informatica Ltda.
 * </p>
 * 
 * @author Anderson Bestteti Santos
 *
 */

public class OcorrenciaValidacao {

	private static final int    CODIGO_OCORRENCIA_VALIDACAO_XML    = 1;
	private static final String DESCRICAO_OCORRENCIA_VALIDACAO_XML = "XML do evento foi rejeitado pela validacao do seu respectivo XSD.";
	private static final int    TIPO_OCORRENCIA_VALIDACAO_XML      = 1;
	
	int    codigo;
	String descricao;
	int    tipo;
	String localizacao;

	public OcorrenciaValidacao() {
		codigo    = CODIGO_OCORRENCIA_VALIDACAO_XML;
		descricao = DESCRICAO_OCORRENCIA_VALIDACAO_XML;
		tipo      = TIPO_OCORRENCIA_VALIDACAO_XML;
	}

	@XmlElement(name="codigo")
	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	@XmlElement(name="descricao")
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@XmlElement(name="tipo")
	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	@XmlElement(name="localizacao")
	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}

}
