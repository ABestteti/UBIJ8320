package br.com.acaosistemas.xml.retornoloteevento;

import javax.xml.bind.annotation.XmlElement;

public class EsocialRetornoEvento {

	EsocialRetornoEventoProcessamento processamento;
	
	public EsocialRetornoEvento() {
	}

	@XmlElement(name="processamento")
	public EsocialRetornoEventoProcessamento getProcessamento() {
		return processamento;
	}

	public void setProcessamento(EsocialRetornoEventoProcessamento processamento) {
		this.processamento = processamento;
	}

}
