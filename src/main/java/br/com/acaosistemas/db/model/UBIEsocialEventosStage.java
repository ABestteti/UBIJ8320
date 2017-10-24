package br.com.acaosistemas.db.model;

import java.sql.Timestamp;

import br.com.acaosistemas.db.enumeration.SimNaoEnum;
import br.com.acaosistemas.db.enumeration.StatusEsocialEventosStageEnum;

/**
 * Entidade representando tabela UBI_ESOCIAL_EVENTOS_STAGE
 *
 * @author Anderson Bestteti Santos
 */
public class UBIEsocialEventosStage {
	
    private Timestamp  dtMov;
    private SimNaoEnum xmlAssinado;
    private StatusEsocialEventosStageEnum status;
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

	public String getRowId() {
		return rowId;
	}

	public void setRowId(String rowId) {
		this.rowId = rowId;
	}

	public StatusEsocialEventosStageEnum getStatus() {
		return status;
	}

	public void setStatus(StatusEsocialEventosStageEnum status) {
		this.status = status;
	}
}
