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

/**
 * Classe responsavel por processar todos os eventos da tabela UBI_EVENTOS_ESOCIAL_STAGE que
 * estejam com status "A ASSINAR". Para cada registro retornado, sera invocado o web service
 * de assinatura de aventos do UBI.
 * 
 * <p>
 * <b>Empresa:</b> Acao Sistemas de Informatica Ltda.
 * </p>
 * 
 * @author Anderson Bestteti Santos
 *
 */
public class AssinarEventosStage {

	/**
	 * Recupera todos os registros tabela UBI_EVENTOS_ESOCIAL_STAGE cujo status seja
	 * igual a "A ASSINAR"
	 */
	public void lerRegistrosNaoAssinados() {
		ClienteWSAssinarEvento       clientWS             = new ClienteWSAssinarEvento();
		UBIEventosEsocialStageDAO    ubesDAO              = new UBIEventosEsocialStageDAO();
		List<UBIEventosEsocialStage> listaUbiEventosStage = new ArrayList<UBIEventosEsocialStage>();
		UBIEventosEsStageLog         ubel                 = new UBIEventosEsStageLog();
		
		listaUbiEventosStage = ubesDAO.listUBIEsocialEventosStage(StatusEsocialEventosStageEnum.A_ASSINAR);
				
		System.out.println("   Assinando dos XMLs da UBI_EVENTOS_ESOCIAL_STAGE...");
		
		for (UBIEventosEsocialStage ubesRow : listaUbiEventosStage) {
			
			System.out.println("     ".concat(new Timestamp(System.currentTimeMillis()).toString()));
			System.out.println("     Processando rowId: "+ubesRow.getRowId());
			System.out.println("     Data de movimentacao: "+ubesRow.getDtMov());
				
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
				ExceptionUtils.gravaExcecaoLog(ubesRow, e);
			} catch (SocketTimeoutException e) {
				// Caso a chamada do web service do correio retornar a excecao
				// IOException, faz a atualizacao do status com o
		        // valor apropriado
				ubesRow.setStatus(StatusEsocialEventosStageEnum.ERRO_ASSINATURA_IRRECUPERAVEL);
				ExceptionUtils.gravaExcecaoLog(ubesRow, e);
			} catch (IOException e) {
				// Caso a chamada do web service do correio retornar a excecao
				// IOException, faz a atualizacao do status com o
		        // valor apropriado
				ubesRow.setStatus(StatusEsocialEventosStageEnum.ERRO_ASSINATURA_IRRECUPERAVEL);
				ExceptionUtils.gravaExcecaoLog(ubesRow, e);
			}
		}
		
		ubesDAO.closeConnection();
		System.out.println("   Finalizado assinatura XMLs da UBI_EVENTOS_ESOCIAL_STAGE.");
	}
}
