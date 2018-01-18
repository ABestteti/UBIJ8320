package br.com.acaosistemas.xml.retornoloteevento;

import javax.xml.bind.annotation.XmlElement;

public class EsocialRetornoEventoProcessamento {

	public static final int ERRO_CONTEUDO_EVENTO = 401;
	
	private int cdResposta;
	private OcorrenciasValidacao ocorrencias;
	
	public EsocialRetornoEventoProcessamento() {
		cdResposta = ERRO_CONTEUDO_EVENTO;
	}

	@XmlElement(name="cdResposta")
	public int getCdResposta() {
		return cdResposta;
	}

	public void setCdResposta(int cdResposta) {
		this.cdResposta = cdResposta;
	}

	@XmlElement(name="ocorrencias")
	public OcorrenciasValidacao getOcorrencias() {
		return ocorrencias;
	}

	public void setOcorrencias(OcorrenciasValidacao ocorrencias) {
		this.ocorrencias = ocorrencias;
	}

}
