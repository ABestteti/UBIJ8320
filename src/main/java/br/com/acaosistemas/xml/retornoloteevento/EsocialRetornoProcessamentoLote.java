package br.com.acaosistemas.xml.retornoloteevento;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/***
 * Classe para construcao do XML do retorno do processamento do lote de
 * eventos do eSocial, com as mensagens de critica do XML do evento contra o seu respectivo XSD.
 * Referencia: http://codippa.com/how-to-create-xml-from-java-objects-using-jaxb/
 * 
 * Este XML esta descrito no Manual de Orientacao do Desenvolvedor do eSocial
 * na secao "Leiaute Mensagem de Retorno do Processamento do Lote", ramificacao "ocorrencia".
 *
 * <p>
 * <b>Empresa:</b> Acao Sistemas de Informatica Ltda.
 * </p>
 * 
 * @author Anderson Bestteti Santos
 *
 */

@XmlRootElement(name="eSocial")
public class EsocialRetornoProcessamentoLote {

	private EsocialRetornoProcessamentoLoteEventos retornoProcessamentoLoteEventos;
	
	public EsocialRetornoProcessamentoLote() {
	}

	@XmlElement(name="retornoProcessamentoLoteEventos")
	public EsocialRetornoProcessamentoLoteEventos getRetornoProcessamentoLoteEventos() {
		return retornoProcessamentoLoteEventos;
	}

	public void setRetornoProcessamentoLoteEventos(EsocialRetornoProcessamentoLoteEventos retornoProcessamentoLoteEventos) {
		this.retornoProcessamentoLoteEventos = retornoProcessamentoLoteEventos;
	}
}