package br.com.acaosistemas.wsclientes;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import br.com.acaosistemas.db.dao.UBIEsocialEventosStageDAO;
import br.com.acaosistemas.db.dao.UBIRuntimesDAO;
import br.com.acaosistemas.db.model.UBIEsocialEventosStage;
import br.com.acaosistemas.frw.util.ExceptionUtils;
import br.com.acaosistemas.frw.util.HttpUtils;

/**
 * @author Anderson Bestteti Santos
 *
 * Classe cliente do web service de assinatura de evento do eSocial
 */

public class ClienteWSAssinarEvento {

	/**
	 * Construtor default da classe
	 */
	public ClienteWSAssinarEvento() {
		// TODO Auto-generated constructor stub
	}
	
	public void execWebService(UBIEsocialEventosStage pUbesRow) throws MalformedURLException, IOException {
		String parametros = new String();
		String wsEndPoint;
		
		UBIRuntimesDAO runtimeDAO = new UBIRuntimesDAO();
		
		// Recupera do banco de dados a informacao do runtime UBIWSASSINAEVT
		wsEndPoint = runtimeDAO.getRuntimeValue("UBIWSASSINAEVT");
		
		// Fecha a conexao com o banco de dados
		runtimeDAO.closeConnection();

		// Monta o parametro de chamada do web service
		// O formato da data deve ser o seguinte: YYYY-MM-DD/HH24:MI:SS.FF
		parametros  = pUbesRow.getDtMov().toString().replaceAll(" ", "/");
		
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
				    throw new MalformedURLException("Código HTTP retornado: " + 
			                                        request.getResponseCode() + 
			                                        " [" + wsEndPoint + "]\n" +
			                                        "Parâmetros: "            + 
			                                        parametros);
			    }
			    else {
			    	    throw new IOException("Código HTTP retornado: "     + 
			                              request.getResponseCode() + 
			                              " [" + wsEndPoint + "]\n" +
			                              "Parâmetros: "            +
			                              parametros);
			    }
			}
			else {
				System.out.println("HTTP code .....: " + request.getResponseMessage());
				System.err.println("Message from ws: " + HttpUtils.readResponse(request) + " [" + wsEndPoint + "]");
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
