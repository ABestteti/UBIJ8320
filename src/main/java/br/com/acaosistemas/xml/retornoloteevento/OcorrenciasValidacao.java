package br.com.acaosistemas.xml.retornoloteevento;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/***
 * Classe armazenar cada uma das ocorrencias da critica do XML do evento contra o seu
 * respectivo arquivo XSD.
 * A partir dessa classe sera construido o XML com as ocorrencias encontradas.
 * 
 * Referencia: http://codippa.com/how-to-create-xml-from-java-objects-using-jaxb/
 * 
 * Essa ramificacao esta documentada no Manual de Orientacao do Desenvolvedor do eSocial
 * na secao "Leiaute Mensagem de Retorno do Processamento do Lote", ramificacao "ocorrencias".
 * 
 * <p>
 * <b>Empresa:</b> Acao Sistemas de Informatica Ltda.
 * </p>
 * 
 * @author Anderson Bestteti Santos
 *
 */

@XmlRootElement(name="ocorrencias")
public class OcorrenciasValidacao {
	
	private List<OcorrenciaValidacao> ocorrenciasValidacao;
	
	// Indicacao da existencia ou nao de erros de validacao
	// do XML com o seu respectivo XSD.
	private boolean hasErros;
	
	public OcorrenciasValidacao() {
		hasErros = false;
	}

	public boolean hasErros() {
		return hasErros;
	}

	public void setHasErros(boolean hasErros) {
		this.hasErros = hasErros;
	}

	@XmlElement(name="ocorrencia")
	public List<OcorrenciaValidacao> getOcorrenciasValidacao() {
		return ocorrenciasValidacao;
	}

	public void setOcorrenciasValidacao(List<OcorrenciaValidacao> listaOcorrencias) {
		ocorrenciasValidacao = listaOcorrencias;
	}
}
