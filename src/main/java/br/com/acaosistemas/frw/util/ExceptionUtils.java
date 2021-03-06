package br.com.acaosistemas.frw.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import br.com.acaosistemas.db.dao.UBIEventosEsStageLogDAO;
import br.com.acaosistemas.db.dao.UBIEventosEsocialStageDAO;
import br.com.acaosistemas.db.model.UBIEventosEsStageLog;
import br.com.acaosistemas.db.model.UBIEventosEsocialStage;

/**
 * Classe utilitaria manipular informacoes de excecoes
 * <p>
 * <b>Empresa:</b> Acao Sistemas de Informatica Ltda.
 * <p>
 * Alterações:
 * <p>
 * 2018.03.08 - ABS - Adicionado JavaDoc.
 * 
 * @author Anderson Bestteti Santos
 *
 */
public final class ExceptionUtils {

	/***
	 * Retorna o stack trace da excecao como uma String.
	 * @param pException
	 * @return String contendo o stack trace
	 */
	public static String stringStackTrace(Exception pException) {
		StringWriter sw = new StringWriter();
		
		// Transforma o stack trace em uma string para
		// sava-la no log
		pException.printStackTrace(new PrintWriter(sw));
		
		return sw.toString();
	}
	
	/***
	 * Grava na tabela UBI_EVENTOS_ESOCIAL_STAGE_LOG o stack trace da excecao.
	 * 
	 * @param pUbesRow
	 *    Uma linha da tabela UBI_EVENTOS_ESOCIAL_STAGE.
	 * @param pException
	 *    A excecao que ocorreu.
	 */
	public static void gravaExcecaoLog(UBIEventosEsocialStage pUbesRow, Exception pException) {
		UBIEventosEsocialStageDAO ubpxDAO = new UBIEventosEsocialStageDAO();
		
		ubpxDAO.updateStatus(pUbesRow);
		
		// Grava na tabela UBI_EVENTOS_ESOCIAL_STAGE_LOGS a string com a mensagem de
		// erro completa				
		UBIEventosEsStageLogDAO ubelDAO = new UBIEventosEsStageLogDAO();
		UBIEventosEsStageLog    ubel    = new UBIEventosEsStageLog();
		
		ubel.setUbesSeqReg(pUbesRow.getSeqReg());
		ubel.setDtMov(new java.sql.Date(new java.util.Date().getTime()));
		ubel.setStatus(pUbesRow.getStatus());
		ubel.setMensagem(pUbesRow.getStatus().getDescricao() +
				        "\n"                                 +
				        pException.getMessage()              +
				        "\n"                                 +
				        stringStackTrace(pException));
		ubel.setNumErro(new Long(pUbesRow.getStatus().getId()));
		
		ubelDAO.insert(ubel);
	}
}
