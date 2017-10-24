package br.com.acaosistemas.db.model;

import java.sql.Timestamp;

import br.com.acaosistemas.db.enumeration.StatusEsocialEventosStageEnum;

public class UBIEsocialEventosStageLog {

	private Timestamp dtMov;
	private Timestamp ubesDtMov;
	private String    mensagem;
	private StatusEsocialEventosStageEnum status;
	private Long      numErro;
	
	public Timestamp getDtMov() {
		return dtMov;
	}
	public void setDtMov(Timestamp dtMov) {
		this.dtMov = dtMov;
	}
	public Timestamp getUbesDtMov() {
		return ubesDtMov;
	}
	public void setUbesDtMov(Timestamp ubesDtMov) {
		this.ubesDtMov = ubesDtMov;
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
