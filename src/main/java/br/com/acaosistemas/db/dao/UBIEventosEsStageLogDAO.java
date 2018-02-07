package br.com.acaosistemas.db.dao;

import oracle.jdbc.OracleConnection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import br.com.acaosistemas.db.connection.ConnectionFactory;
import br.com.acaosistemas.db.model.UBIEventosEsStageLog;
import br.com.acaosistemas.main.Versao;

/**
 * DAO para manipulacao da tabela UBI_EVENTOS_ES_STAGE_LOGS
 * 
 * @author Anderson Bestteti Santos
 *
 */
public class UBIEventosEsStageLogDAO {

	private OracleConnection conn;
	
	public UBIEventosEsStageLogDAO() {
		conn = new ConnectionFactory().getConnection();
	}

//	public void closeConnection () {
//		try {
//			conn.close();
//		} catch (SQLException e) {
//			throw new RuntimeException(e);
//		}
//	}
	
	public void insert(UBIEventosEsStageLog pUbelRow) {
		final String      ORA_DUP_VAL_ON_INDEX_ERROR = "ORA-00001"; // CHAVE DUPLICADA
		final int         RETRIES                    = 3;
		PreparedStatement stmt					     = null;

		// Laço para tratar erro ORA_DUP_VAL_ON_INDEX_ERROR. Se não ocorrer o erro na primeira iteração,
		// então o laço será interrompido. Caso, contrário serão feitas mais duas tentativas de inserção
		// com a atualização de pUbelRow com novo Timestamp.		
		for (int tentativa = 1; tentativa <= RETRIES; tentativa++) {		
			try {
				stmt = conn.prepareStatement(
						"INSERT INTO ubi_eventos_es_stage_logs (ubes_dt_mov,dt_mov,mensagem,status,num_erro) VALUES (?,?,?,?,?)");
			
				stmt.setTimestamp(1, pUbelRow.getUbesDtMov());
				stmt.setTimestamp(2, pUbelRow.getDtMov());
				stmt.setString(3, Versao.getStringVersao() + "\n" + pUbelRow.getMensagem());
				stmt.setInt(4, pUbelRow.getStatus().getId());
				stmt.setLong(5, pUbelRow.getNumErro());
				
				stmt.execute();
				stmt.close();
				break; // cai fora do laço caso a inserção ocorra sem problema.
			} catch (SQLException e) {
				if (e.getMessage().contains(ORA_DUP_VAL_ON_INDEX_ERROR)) {
					if (tentativa < RETRIES) {
						try {
							
							// Aguarda 250 milisegundos para atualizar o TimeStamp de
							// pUbelRow.setDtMov.
							Thread.sleep(250);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}

						// Atualiza pUbelRow.setDtMov com o novo TimeStamp para tentar nova
						// inserção na tabela UBI_EVENTOS_ES_STAGE_LOGS.
						pUbelRow.setDtMov(new Timestamp(System.currentTimeMillis()));

					} else {
						System.out.println(
								RETRIES +
								" tentativas de inclusão do log sem êxito.");
						e.printStackTrace();
					}
				} else {
					e.printStackTrace();
				}				
			}
			finally { 
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}			
		}
	}
}