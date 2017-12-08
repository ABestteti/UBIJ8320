package br.com.acaosistemas.db.model;

import java.sql.NClob;

/**
 * Entidade representando tabela UBI_XSDS
 *
 * @author Anderson Bestteti Santos
 */
public class UBIXsds {
	Integer codigo;
	String  targetNameSpace;
	NClob   documento;
	String  rowId;
	
	public Integer getCodigo() {
		return codigo;
	}
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	public String getTargetNameSpace() {
		return targetNameSpace;
	}
	public void setTargetNameSpace(String targetNameSpace) {
		this.targetNameSpace = targetNameSpace;
	}
	public NClob getDocumento() {
		return documento;
	}
	public void setDocumento(NClob documento) {
		this.documento = documento;
	}
	public String getRowId() {
		return rowId;
	}
	public void setRowId(String rowId) {
		this.rowId = rowId;
	}
}
