package br.com.acaosistemas.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/***
 * Classe armazenar cada uma das ocorrencias da critica do XML do evento contra o seu
 * respectivo arquivo XSD.
 * A partir dessa classe será construido o XML com as ocorrencias encontradas.
 * 
 * Referencia: http://codippa.com/how-to-create-xml-from-java-objects-using-jaxb/
 * 
 * Essa ramificacao esta documentada no Manual de Orientacao do Desenvolvedor do eSocial
 * na secao "Leiaute Mensagem de Retorno do Processamento do Lote", ramificacao "ocorrencias".
 * 
 * @author Anderson Bestteti Santos
 *
 */

@XmlRootElement(name="ocorrencias")
public class OcorrenciasValidacao {

	private static final int CODIGO_VALIDACAO_XML = 999;
	private static final int TIPO_ERRO_XML        = 1;
	
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
