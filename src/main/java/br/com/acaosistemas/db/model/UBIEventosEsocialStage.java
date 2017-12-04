package br.com.acaosistemas.db.model;

import java.sql.NClob;
import java.sql.Timestamp;

import br.com.acaosistemas.db.enumeration.SimNaoEnum;
import br.com.acaosistemas.db.enumeration.StatusEsocialEventosStageEnum;

/**
 * Entidade representando tabela UBI_EVENTOS_ESOCIAL_STAGE
 *
 * @author Anderson Bestteti Santos
 */
public class UBIEventosEsocialStage {
	
    private Timestamp  dtMov;
    private SimNaoEnum xmlAssinado;
    private StatusEsocialEventosStageEnum status;
    private StringBuffer xmlRetornoValidacao;
    private NClob xml;
    private String rowId;
    
	public Timestamp getDtMov() {
		return dtMov;
	}
	
	public void setDtMov(Timestamp dtMov) {
		this.dtMov = dtMov;
	}

	public SimNaoEnum getXmlAssinado() {
		return xmlAssinado;
	}

	public void setXmlAssinado(SimNaoEnum xmlAssinado) {
		this.xmlAssinado = xmlAssinado;
	}

	public StatusEsocialEventosStageEnum getStatus() {
		return status;
	}

	public void setStatus(StatusEsocialEventosStageEnum status) {
		this.status = status;
	}

	public StringBuffer getXmlRetornoValidacao() {
		return xmlRetornoValidacao;
	}

	public void setXmlRetornoValidacao(StringBuffer xmlRetornoValidacao) {
		this.xmlRetornoValidacao = xmlRetornoValidacao;
	}

	public NClob getXml() {
		return xml;
	}

	public void setXml(NClob xml) {
		this.xml = xml;
	}
	
	public String getRowId() {
		return rowId;
	}

	public void setRowId(String rowId) {
		this.rowId = rowId;
	}
}
