package br.com.acaosistemas.db.model;

import java.sql.Date;

import br.com.acaosistemas.db.enumeration.StatusEsocialEventosStageEnum;

/**
 * Entidade representando tabela UBI_EVENTOS_ES_STAGE_LOGS
 * 
 * <p>
 * <b>Empresa:</b> Acao Sistemas de Informatica Ltda.
 * </p>
 * <p>
 * Alterações:
 * <p>
 * 2018.03.07 - ABS - Alteração da PK da tabela UBI_EVENTOS_ES_STAGE_LOGS, 
 *                    conforme SA 20330.
 *
 * @author Anderson Bestteti Santos
 * 
 * 
 */
public class UBIEventosEsStageLog {

	private Long                          ubesSeqReg;
	private Long                          seqReg;
	private Date                          dtMov;
	private String                        mensagem;
	private StatusEsocialEventosStageEnum status;
	private Long                          numErro;
	
	public Long getUbesSeqReg() {
		return ubesSeqReg;
	}
	public void setUbesSeqReg(Long ubesSeqReg) {
		this.ubesSeqReg = ubesSeqReg;
	}
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
	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	public StatusEsocialEventosStageEnum getStatus() {
		return status;
	}
	public void setStatus(StatusEsocialEventosStageEnum status) {
		this.status = status;
	}
	public Long getNumErro() {
		return numErro;
	}
	public void setNumErro(Long numErro) {
		this.numErro = numErro;
	}
}
