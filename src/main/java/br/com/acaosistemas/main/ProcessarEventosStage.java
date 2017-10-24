package br.com.acaosistemas.main;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import br.com.acaosistemas.db.dao.UBIEsocialEventosStageDAO;
import br.com.acaosistemas.db.dao.UBIEsocialEventosStageLogDAO;
import br.com.acaosistemas.db.enumeration.StatusEsocialEventosStageEnum;
import br.com.acaosistemas.db.model.UBIEsocialEventosStage;
import br.com.acaosistemas.db.model.UBIEsocialEventosStageLog;
import br.com.acaosistemas.frw.util.ExceptionUtils;
import br.com.acaosistemas.wsclientes.ClienteWSAssinarEvento;

public class ProcessarEventosStage {

	public ProcessarEventosStage() {
	}

	public void lerRegistrosNaoProcessados() {
		ClienteWSAssinarEvento       clientWS             = new ClienteWSAssinarEvento();
		UBIEsocialEventosStageDAO    ubesDAO              = new UBIEsocialEventosStageDAO();
		List<UBIEsocialEventosStage> listaUbiEventosStage = new ArrayList<UBIEsocialEventosStage>();
		UBIEsocialEventosStageLog    ubel                 = new UBIEsocialEventosStageLog();
		
		listaUbiEventosStage = ubesDAO.listUBIEsocialEventosStage();
				
		System.out.println("   Processando registros da UBI_ESOCIAL_EVENTOS_STAGE...");
		
		for (UBIEsocialEventosStage ubesRow : listaUbiEventosStage) {
			
			System.out.println("     Processando rowId: "+ubesRow.getRowId());
				
			try {
				clientWS.execWebService(ubesRow);
				
				// Atualiza o status da tabela UBI_POBOX_XML para
				// PROCESSAMENTO_COM_SUCESSO (198)
				ubesRow.setStatus(StatusEsocialEventosStageEnum.ASSINADO_COM_SUCESSO);
				ubesDAO.updateStatus(ubesRow);
				
				// Insere no log o resultado da chamada do web service
				ubel.setUbesDtMov(ubesRow.getDtMov());
				ubel.setDtMov(new Timestamp(System.currentTimeMillis()));
				ubel.setMensagem(Versao.getStringVersao() +
						         "\n"                     +
						         StatusEsocialEventosStageEnum.ASSINADO_COM_SUCESSO.getDescricao());
				ubel.setStatus(StatusEsocialEventosStageEnum.ASSINADO_COM_SUCESSO);
				ubel.setNumErro(0L);
				
				UBIEsocialEventosStageLogDAO ubelDAO = new UBIEsocialEventosStageLogDAO();				
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
	
	private void gravaExcecaoLog(UBIEsocialEventosStage pUbesRow, Exception pException) {
		UBIEsocialEventosStageDAO ubpxDAO = new UBIEsocialEventosStageDAO();
		
		ubpxDAO.updateStatus(pUbesRow);
		
		// Grava na tabela UBI_POBOX_XML_LOG a string com a mensagem de
		// erro completa				
		UBIEsocialEventosStageLogDAO ubelDAO = new UBIEsocialEventosStageLogDAO();
		UBIEsocialEventosStageLog    ubel    = new UBIEsocialEventosStageLog();
		
		ubel.setDtMov(pUbesRow.getDtMov());
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
