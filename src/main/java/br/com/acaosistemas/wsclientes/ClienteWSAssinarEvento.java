package br.com.acaosistemas.wsclientes;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.acaosistemas.db.dao.UBIRuntimesDAO;
import br.com.acaosistemas.db.model.UBIEventosEsocialStage;
import br.com.acaosistemas.frw.util.ExceptionUtils;
import br.com.acaosistemas.frw.util.HttpUtils;
import br.com.acaosistemas.main.Daemon;

/**
 * Classe cliente do web service de assinatura de evento do eSocial
 * </p>
 * <p>
 * Alterações:
 * <p>
 * 2018.03.08 - ABS - Alteracao do parametro do web service de assinatura do evento
 *                    de "dtmov" para "seqreg".
 *                  - Adicionado sistema de log com a biblioteca log4j2.
 *                    
 * @author Anderson Bestteti Santos
 */

public class ClienteWSAssinarEvento {
	
	private static final Logger logger = LogManager.getLogger(ClienteWSAssinarEvento.class);
	
	/**
	 * Construtor default da classe
	 */
	public ClienteWSAssinarEvento() {
	}
	
	public void execWebService(UBIEventosEsocialStage pUbesRow) throws MalformedURLException, IOException {
		String parametros = new String();
		String wsEndPoint;
		
		UBIRuntimesDAO runtimeDAO = new UBIRuntimesDAO();
		
		// Recupera do banco de dados a informacao do runtime UBIWSASSINAEVT
		wsEndPoint = runtimeDAO.getRuntimeValue("UBIWSASSINAEVT");
		
		// Fecha a conexao com o banco de dados
		//runtimeDAO.closeConnection();

		// Monta o parametro de chamada do web service
		parametros  = pUbesRow.getSeqReg().toString();
		
		try {
			URL url = new URL(wsEndPoint+parametros);
			
			HttpURLConnection request = (HttpURLConnection) url.openConnection();			

			// Define que a requisicao pode obter informacoes de retorno.
		    request.setDoOutput(true);
						
			// Define o metodo da requisicao
			request.setRequestMethod("GET");
			
			// Conecta na URL
			request.connect();
			
			if (request.getResponseCode() != HttpURLConnection.HTTP_OK) {
			    if (request.getResponseCode() == HttpURLConnection.HTTP_INTERNAL_ERROR) {
				    throw new MalformedURLException("Codigo HTTP retornado: " + 
			                                        request.getResponseCode() + 
			                                        " [" + wsEndPoint + "]\n" +
			                                        "Parametros: "            + 
			                                        parametros);
			    }
			    else {
			    	    throw new IOException("Codigo HTTP retornado: "     + 
			                              request.getResponseCode() + 
			                              " [" + wsEndPoint + "]\n" +
			                              "Parametros: "            +
			                              parametros);
			    }
			}
			else {
				logger.info("HTTP code .....: " + request.getResponseMessage());
				logger.info("Message from ws: " + HttpUtils.readResponse(request) + " [" + wsEndPoint + "]");
			}
						
		} catch (MalformedURLException e) {
			throw new MalformedURLException(e.getMessage()+":\n"+ExceptionUtils.stringStackTrace(e));
		} catch (SocketTimeoutException e) {
			throw new SocketTimeoutException(e.getMessage()+":\n"+ExceptionUtils.stringStackTrace(e));
		} catch (IOException e) {
			throw new IOException(e.getMessage()+":\n"+ExceptionUtils.stringStackTrace(e));
		}
	}
}
