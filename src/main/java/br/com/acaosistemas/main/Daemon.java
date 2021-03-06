package br.com.acaosistemas.main;

import java.sql.CallableStatement;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.acaosistemas.db.connection.ConnectionFactory;
import br.com.acaosistemas.db.connection.DBConnectionInfo;
import br.com.acaosistemas.db.dao.UBIRuntimesDAO;
import br.com.acaosistemas.frw.util.ResetPipe;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleTypes;

/**
 * Classe reponsavel por ler o pipe de comunicacao do banco.
 * <p>
 * <b>Empresa:</b> Acao Sistemas de Informatica Ltda.
 * <p>
 * Altera��es:
 * <p>
 * 2018.03.07 - ABS - Altera��o da PK das tabelas UBI_EVENTOS_ES_STAGE_LOGS 
 *                    e UBI_EVENTOS_ESOCIAL_STAGE, conforme SA 20330.
 *                  - Adicionado sistema de log com a biblioteca log4j2.
 *                  
 * @author Anderson Bestteti
 * <p>
 * Referencias:<br>
 *  https://stackoverflow.com/questions/19333011/how-to-call-a-stored-function-from-jdbc<br>
 *  http://docs.oracle.com/cd/A84870_01/doc/java.816/a81354/samapp2.htm<br>
 *  https://docs.oracle.com/cd/B19306_01/java.102/b14355/toc.htm<br>
 */
public class Daemon {
	
	private static final Logger logger = LogManager.getLogger(Daemon.class);	
	
	private static final int STOP_DAEMON             = 1;
	private static final int CONSULTAR_STATUS        = 2;
	private static final int CONSULTAR_VERSAO_DAEMON = 3;
	
	private static final int DEAMON_ALIVE     = 1;

	private OracleConnection  conn;
	private CallableStatement stmt;
		
	public static void main(String[] args) {

		if (args.length != 3) {
			System.out.println(Versao.ver()+"\n");
			System.out.println("Quantidade de parametros insuficientes.");
			System.out.println("Utilize o comando abaixo para executar a aplicacao, utilizando o Java 1.8 ou superior:");
			System.out.println("java -jar UBIJ8320.jar usuarioDB senhaDB servidorDB:portaListner:instanciaDB");
			System.exit(1);
		}
		
		logger.info("\n".concat(Versao.ver()));
		
		Daemon procPoboxXml = new Daemon();
		
		String dbUserName = args[0];
		String dbPassWord = args[1];
		String dbStrConn  = args[2];

		
		// Salva em memoria as informacoes de conexao com o banco
		// de dados para posterior uso pela classes DAO.
		DBConnectionInfo.setDbUserName(dbUserName);
		DBConnectionInfo.setDbPassWord(dbPassWord);
		DBConnectionInfo.setDbStrConnect(dbStrConn);
		
		// Entra no loop de leitura da tabela UBI_POBOX_XML
		procPoboxXml.lerPipeDB();

	}
	
    private void lerPipeDB() {
		// Rowid de uma linha da table UBI_POBOX_XML
		String pipeConteudo  = "";
		
		// Variaveis para trabalhar com o pipe de banco
		String pipeName   = null;
		int    pipeCmd    = -1;
		int    pipeStatus = -1;
		
		// Controla o loop de leitura do PIPE
		boolean stopDaemon = false;
		
		// Objects de acesso as tabelas do banco de dados
		UBIRuntimesDAO runtimeDAO = new UBIRuntimesDAO();
		
		pipeName = runtimeDAO.getRuntimeValue("PIPEUBIASSEVT");
		//runtimeDAO.closeConnection();
		
		if (pipeName == null) {
			throw new RuntimeException("O runtime PIPEUBIASSEVT nao esta cadastrado no UBI.");
		}
		// Abre conexao com o banco para leitura do pipe do
		// banco de dados.
		conn = new ConnectionFactory().getConnection();
		
		// Remove todas as mensagens do pipe indicado em "pipeName"
		ResetPipe.reset(conn, pipeName);

		logger.info("Processando registros da area de stage...");
		
		// Loop para leitura constante do pipe de comunicacao
		// do deamon e por procura de registros com status 0 (nao processado)
		// na tabela UBI_POBOX_XML
		while (!stopDaemon) {
			
			// Pausa a execucao da thread principal por 5 segundos
			// Com iso, e liberado o lock da dbms_pipe, permitindo que a 
			// consiliacao de usuario possa conceder grant da package para
			// o usuario que esta sem conciliado.
            try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				logger.error(e1);
				throw new RuntimeException(e1);
			}
            
			// Prepara a chamada da funcao no banco de dados
			try {
				stmt = conn.prepareCall("{? = call dbms_pipe.receive_message(?,1)}");

				// Define que o tipo de retorno da funcao sera um NUMBER
				stmt.registerOutParameter(1, OracleTypes.NUMBER);

				// Define o nome do pipe que sera lido do banco.
				stmt.setString(2, pipeName);

				// Executa a funcao do banco
				stmt.execute();

				// Recupera o status da leitura do pipe do banco
				pipeStatus = stmt.getInt(1);

			} catch (SQLException e) {
				logger.error(e);
				throw new RuntimeException(e);
			}
			
			// Se o retorno do pipe foi obtido com sucesso (valor 0),
			// busca o comando enviado.
			if (pipeStatus == 0) {

				try {
					
					stmt = conn.prepareCall("BEGIN dbms_pipe.unpack_message(?); dbms_pipe.unpack_message(?); END;");

					// Define que o parametro e do tipo OUT, retornando um NUMBER
					// e um VARCHAR, respectivamente.
					stmt.registerOutParameter(1, OracleTypes.NUMBER);
					stmt.registerOutParameter(2, OracleTypes.VARCHAR);

					// Executa a funcao do banco
					stmt.execute();

					// Recupera os valores retornados do pipe
					pipeCmd = stmt.getInt(1);
					pipeConteudo = stmt.getString(2);
							;
				} catch (SQLException e) {
					logger.error(e);
					throw new RuntimeException(e);
				}

				switch (pipeCmd) {
				case CONSULTAR_STATUS:
					logger.info("Recebido comando status do servico!");
					
					// Nesse caso o objeto pipeConteudo armazena o nome do
					// pipe de retorno que sera usado para enviar o status
					// de volta para o PL/SQL, sinalizando que o daemon esta
					// rodando.
					statusDaemon(pipeConteudo);
			     	break;
				case CONSULTAR_VERSAO_DAEMON:
					logger.info("Recebido comando versao do servico!");
					
					// Nesse caso o objeto pipeConteudo armazena o nome do
					// pipe de retorno que sera usado para enviar a versao
					// deste servico de volta para o PL/SQL.
					versaoDaemon(pipeConteudo);
			     	break;	
				case STOP_DAEMON:
					logger.info("Recebido comando stop do servico!");
					stopDaemon = true;
					break;
				}
			}
			
			try {
				if (!stmt.isClosed()) {
				   stmt.close();
				}
			} catch (SQLException e) {
				logger.error(e);
				throw new RuntimeException(e) ;
			}
			
			if (!stopDaemon) {
				// Inicia o processo de leitura dos registros da tabela de stage
				// cujo status seja A_ASSINAR (101)
				new AssinarEventosStage().lerRegistrosNaoAssinados();
				
				// Inicia o processo de validacao dos registros da tabela de stage
				// cujo status seja A_VALIDAR (201)
				new ValidarEventosStage().lerRegistrosParaValidacao();
			}
		}
		
		try {
			stmt.close();
	        conn.close();
		} catch (SQLException e) {
			logger.error(e);
			throw new RuntimeException(e) ;
		}
		
		logger.info("Servico encerrado por requisicao do usuario.");
		System.exit(0);
	}
	
	private void statusDaemon(String pPipeReturn) {
		try {
			if (!stmt.isClosed()) {
				stmt.close();
			}
			
			// Retorna o status do deamon, informando
			// que ele esta ativo: DEAMON_ALIVE
			//dbms_pipe.pack_message(pipe_name);
			stmt = conn.prepareCall("BEGIN dbms_pipe.pack_message(?); ? := dbms_pipe.send_message(?,2); END;");
			
			// Manda para o pipe o status que representa que o
			// o deamon esta rodando.
			stmt.setInt(1, DEAMON_ALIVE);
			stmt.registerOutParameter(2, OracleTypes.NUMBER);			
			stmt.setString(3, pPipeReturn);
			
			stmt.execute();
			
			stmt.close();
			
		} catch (SQLException e) {
			logger.error(e);
			throw new RuntimeException(e);
		}	    
	}
	
	private void versaoDaemon(String pPipeReturn) {
		try {
			if (!stmt.isClosed()) {
				stmt.close();
			}
			
			// Retorna o status do deamon, informando
			// que ele esta ativo: DEAMON_ALIVE
			//dbms_pipe.pack_message(pipe_name);
			stmt = conn.prepareCall("BEGIN dbms_pipe.pack_message(?); ? := dbms_pipe.send_message(?,2); END;");
			
			// Manda para o pipe o status que representa que o
			// o deamon esta rodando.
			stmt.setString(1, Versao.getStringVersao());
			stmt.registerOutParameter(2, OracleTypes.NUMBER);			
			stmt.setString(3, pPipeReturn);
			
			stmt.execute();
			stmt.close();
			
		} catch (SQLException e) {
			logger.error(e);
			throw new RuntimeException(e);
		}	    
	}
}
