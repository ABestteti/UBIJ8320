package br.com.acaosistemas.db.dao;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.acaosistemas.db.connection.ConnectionFactory;
import br.com.acaosistemas.db.model.UBIEventosEsStageLog;
import br.com.acaosistemas.main.Versao;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleTypes;

/**
 * DAO para manipulacao da tabela UBI_EVENTOS_ES_STAGE_LOGS
 * <p>
 * <b>Empresa:</b> Acao Sistemas de Informatica Ltda.
 * <p>
 * Altera��es:
 * <p>
 * 2018.03.07 - ABS - Altera��o da PK da tabela UBI_EVENTOS_ES_STAGE_LOGS, 
 *                    conforme SA 20330.
 *                  - Adicionado sistema de log com a biblioteca log4j2.
 * @author Anderson Bestteti Santos
 *
 */
public class UBIEventosEsStageLogDAO {

	private static final Logger logger = LogManager.getLogger(UBIEventosEsStageLogDAO.class);
	
	private OracleConnection conn;
	
	public UBIEventosEsStageLogDAO() {
		conn = new ConnectionFactory().getConnection();
	}

	public void insert(UBIEventosEsStageLog pUbelRow) {
		PreparedStatement stmt = null;

		try {
			stmt = conn.prepareStatement(
					"INSERT INTO ubi_eventos_es_stage_logs "
				  + "  (ubes_seq_reg,"
				  + "   seq_reg,"
				  + "   dt_mov," 
				  + "   mensagem," 
				  + "   status," 
				  + "   num_erro) " 
				  + "VALUES "
				  + "  (?," 
				  + "   ?," 
				  + "   ?," 
				  + "   ?," 
				  + "   ?," 
				  + "   ?)");

			stmt.setLong(1, pUbelRow.getUbesSeqReg());
			stmt.setLong(2, getNextSeqReg());
			stmt.setDate(3, new java.sql.Date(new java.util.Date().getTime()));
			stmt.setString(4, Versao.getStringVersao() + "\n" + pUbelRow.getMensagem());
			stmt.setInt(5, pUbelRow.getStatus().getId());
			stmt.setLong(6, pUbelRow.getNumErro());

			stmt.execute();
		} catch (SQLException e) {
			logger.error(e);
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				logger.error(e);
			}
		}
	}
	
	/***
	 * Retorna a sequencia gerada pelo package de banco ubip8100.gera_seq_chave.
	 * @return Um numero que representa a sequencia gerada pela funcao de banco.
	 * 
	 */
	private Long getNextSeqReg() {
		Long              nextVal = 0L;
		CallableStatement    stmt = null;

		try {
			// Executa a funcao gera_seq_chave do package ubip8100. 
			stmt = conn.prepareCall("{? = call ubip8100.gera_seq_chave}");
			
			// Define que o tipo de retorno da funcao sera um NUMBER
			stmt.registerOutParameter(1, OracleTypes.NUMBER);
			stmt.execute();
			
			nextVal = stmt.getLong(1);
		} catch (SQLException e) {
			logger.error(e);
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				logger.error(e);
			}			
		}
		return nextVal;
	}	
}