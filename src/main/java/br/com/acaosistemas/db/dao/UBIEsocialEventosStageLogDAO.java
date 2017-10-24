package br.com.acaosistemas.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import br.com.acaosistemas.db.connection.ConnectionFactory;
import br.com.acaosistemas.db.model.UBIEsocialEventosStageLog;
import br.com.acaosistemas.main.Versao;

public class UBIEsocialEventosStageLogDAO {

	private Connection                conn;
	private UBIEsocialEventosStageLog ubel;
	
	public UBIEsocialEventosStageLogDAO() {
		conn = new ConnectionFactory().getConnection();
	}

	public void closeConnection () {
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
	
	public void insert(UBIEsocialEventosStageLog pUbelRow) {
		PreparedStatement stmt = null;
		
		try {
			stmt = conn.prepareStatement(
					"INSERT INTO ubi_eventos_esocial_stage_logs (dt_mov,ubes_dt_mov,mensagem,status,num_erro) VALUES (?,?,?,?,?)");
		
			stmt.setTimestamp(1, pUbelRow.getUbesDtMov());
			stmt.setTimestamp(2, pUbelRow.getDtMov());
			stmt.setString(3, Versao.getStringVersao() + "\n" + pUbelRow.getMensagem());
			stmt.setInt(4, pUbelRow.getStatus().getId());
			stmt.setLong(5, pUbelRow.getNumErro());
			
			stmt.execute();
			stmt.close();

			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
