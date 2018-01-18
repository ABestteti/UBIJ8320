package br.com.acaosistemas.xml.retornoloteevento;

import javax.xml.bind.annotation.XmlElement;

public class EsocialEvento {

    private EsocialRetornoEvento retornoEvento;
	
	public EsocialEvento() {
	}

	@XmlElement(name="retornoEvento")
	public EsocialRetornoEvento getRetornoEvento() {
		return retornoEvento;
	}

	public void setRetornoEvento(EsocialRetornoEvento retornoEvento) {
		this.retornoEvento = retornoEvento;
	}

}
