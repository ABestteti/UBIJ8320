package br.com.acaosistemas.db.model;

import java.sql.Date;
import java.sql.NClob;

import br.com.acaosistemas.db.enumeration.SimNaoEnum;
import br.com.acaosistemas.db.enumeration.StatusEsocialEventosStageEnum;

/**
 * Entidade representando tabela UBI_EVENTOS_ESOCIAL_STAGE
 * 
 * <p>
 * <b>Empresa:</b> Acao Sistemas de Informatica Ltda.
 * </p>
 * <p>
 * Alterações:
 * <p>
 * 2018.03.07 - ABS - Alteração da PK da tabela UBI_EVENTOS_ESOCIAL_STAGE, 
 *                    conforme SA 20330.
 *
 * @author Anderson Bestteti Santos
 * 
 * 
 */
public class UBIEventosEsocialStage {
	
	private Long                          seqReg;
	private Date                          dtMov;
    private SimNaoEnum                    xmlAssinado;
    private StatusEsocialEventosStageEnum status;
    private StringBuffer                  xmlRetornoValidacao;
    private NClob                         xml;
    private String                        idESocial;
    private String                        rowId;
    
    public Long getSeqReg() {
		return seqReg;
	}

	public void setSeqReg(Long seqReg) {
		this.seqReg = seqReg;
	}

    public Date getDtMov() {
		return dtMov;
	}
	
	public void setDtMov(Date dtMov) {
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

	public String getIdESocial() {
		return idESocial;
	}

	public void setIdESocial(String idESocial) {
		this.idESocial = idESocial;
	}
}
