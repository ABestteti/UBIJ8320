package br.com.acaosistemas.main;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import br.com.acaosistemas.db.dao.UBIEventosEsocialStageDAO;
import br.com.acaosistemas.db.dao.UBIEventosEsStageLogDAO;
import br.com.acaosistemas.db.enumeration.StatusEsocialEventosStageEnum;
import br.com.acaosistemas.db.model.UBIEventosEsocialStage;
import br.com.acaosistemas.db.model.UBIEventosEsStageLog;
import br.com.acaosistemas.frw.util.ExceptionUtils;
import br.com.acaosistemas.wsclientes.ClienteWSAssinarEvento;

public class ProcessarEventosStage {

	public ProcessarEventosStage() {
	}

	public void lerRegistrosNaoProcessados() {
		ClienteWSAssinarEvento       clientWS             = new ClienteWSAssinarEvento();
		UBIEventosEsocialStageDAO    ubesDAO              = new UBIEventosEsocialStageDAO();
		List<UBIEventosEsocialStage> listaUbiEventosStage = new ArrayList<UBIEventosEsocialStage>();
		UBIEventosEsStageLog    ubel                 = new UBIEventosEsStageLog();
		
		listaUbiEventosStage = ubesDAO.listUBIEsocialEventosStage();
				
		System.out.println("   Processando registros da UBI_ESOCIAL_EVENTOS_STAGE...");
		
		for (UBIEventosEsocialStage ubesRow : listaUbiEventosStage) {
			
			System.out.println("     Processando rowId: "+ubesRow.getRowId());
				
			try {
				clientWS.execWebService(ubesRow);
				
				// Atualiza o status da tabela UBI_EVENTOS_ESOCIAL_STAGE para
				// ASSINADO_COM_SUCESSO (298)
				ubesRow.setStatus(StatusEsocialEventosStageEnum.ASSINADO_COM_SUCESSO);
				ubesDAO.updateStatus(ubesRow);
				
				// Insere no log o resultado da chamada do web service
				ubel.setUbesDtMov(ubesRow.getDtMov());
				ubel.setDtMov(new Timestamp(System.currentTimeMillis()));
				ubel.setMensagem(StatusEsocialEventosStageEnum.ASSINADO_COM_SUCESSO.getDescricao());
				ubel.setStatus(StatusEsocialEventosStageEnum.ASSINADO_COM_SUCESSO);
				ubel.setNumErro(0L);
				
				UBIEventosEsStageLogDAO ubelDAO = new UBIEventosEsStageLogDAO();				
				ubelDAO.insert(ubel);
				ubelDAO.closeConnection();
				
			} catch (MalformedURLException e) {
				// Caso a chamada do web service do correio retornar a excecao
				// MalformedURLException, faz a atualizacao do status com o
		        // valor apropriado.
				ubesRow.setStatus(StatusEsocialEventosStageEnum.ERRO_ASSINATURA_IRRECUPERAVEL);
				gravaExcecaoLog(ubesRow, e);
			} catch (SocketTimeoutException e) {
				// Caso a chamada do web service do correio retornar a excecao
				// IOException, faz a atualizacao do status com o
		        // valor apropriado
				ubesRow.setStatus(StatusEsocialEventosStageEnum.ERRO_ASSINATURA_IRRECUPERAVEL);
				gravaExcecaoLog(ubesRow, e);
			} catch (IOException e) {
				// Caso a chamada do web service do correio retornar a excecao
				// IOException, faz a atualizacao do status com o
		        // valor apropriado
				ubesRow.setStatus(StatusEsocialEventosStageEnum.ERRO_ASSINATURA_IRRECUPERAVEL);
				gravaExcecaoLog(ubesRow, e);
			}
		}
		
		ubesDAO.closeConnection();
		System.out.println("   Finalizado processomento da UBI_ESOCIAL_EVENTOS_STAGE.");
	}
	
	private void gravaExcecaoLog(UBIEventosEsocialStage pUbesRow, Exception pException) {
		UBIEventosEsocialStageDAO ubpxDAO = new UBIEventosEsocialStageDAO();
		
		ubpxDAO.updateStatus(pUbesRow);
		
		// Grava na tabela UBI_EVENTOS_ESOCIAL_STAGE_LOGS a string com a mensagem de
		// erro completa				
		UBIEventosEsStageLogDAO ubelDAO = new UBIEventosEsStageLogDAO();
		UBIEventosEsStageLog    ubel    = new UBIEventosEsStageLog();
		
		ubel.setUbesDtMov(pUbesRow.getDtMov());
		ubel.setDtMov(new Timestamp(System.currentTimeMillis()));
		ubel.setStatus(pUbesRow.getStatus());
		ubel.setMensagem(pUbesRow.getStatus().getDescricao() +
				        "\n"                                 +
				        pException.getMessage()              +
				        "\n"                                 +
				        ExceptionUtils.stringStackTrace(pException));
		ubel.setNumErro(new Long(pUbesRow.getStatus().getId()));
		
		ubelDAO.insert(ubel);
		ubelDAO.closeConnection();		
	}
}
