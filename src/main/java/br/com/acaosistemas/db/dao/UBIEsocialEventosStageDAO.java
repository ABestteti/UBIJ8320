package br.com.acaosistemas.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.acaosistemas.db.connection.ConnectionFactory;
import br.com.acaosistemas.db.enumeration.SimNaoEnum;
import br.com.acaosistemas.db.enumeration.StatusEsocialEventosStageEnum;
import br.com.acaosistemas.db.model.UBIEsocialEventosStage;

public class UBIEsocialEventosStageDAO {

	private Connection             conn;
	private UBIEsocialEventosStage ubes;
	
	public UBIEsocialEventosStageDAO() {
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
	
	public UBIEsocialEventosStage getUBIEsocialEventosStage(String pRowID) {
		ubes = new UBIEsocialEventosStage();
		
		PreparedStatement stmt = null;
		
		try {
			stmt = conn.prepareStatement(
					"SELECT ubes.dt_mov FROM ubi_eventos_esocial_stage ubes WHERE ubes.rowid = ?");
			
			stmt.setString(1, pRowID);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				ubes.setDtMov(rs.getTimestamp("dt_mov"));
			}
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return ubes;
	}
	
	public List<UBIEsocialEventosStage> listUBIEsocialEventosStage() {
		PreparedStatement stmt             = null;
		List<UBIEsocialEventosStage> listaUBIEsocialEventosStage = new ArrayList<UBIEsocialEventosStage>();
	
		try {
			stmt = conn.prepareStatement(
					"SELECT ubes.dt_mov, ubes.status, ubes.xml_assinado, ubes.rowid FROM ubi_eventos_esocial_stage ubes WHERE ubes.status = ?");
			
			stmt.setInt(1, StatusEsocialEventosStageEnum.A_ASSINAR.getId());
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				UBIEsocialEventosStage ubes = new UBIEsocialEventosStage();
				
				ubes.setRowId(rs.getString("rowId"));
				ubes.setDtMov(rs.getTimestamp("dt_mov"));
				ubes.setStatus(StatusEsocialEventosStageEnum.getById(rs.getInt("status")));
				ubes.setXmlAssinado(SimNaoEnum.getById(rs.getString("xml_assinado")));
				
				listaUBIEsocialEventosStage.add(ubes);
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return listaUBIEsocialEventosStage;
	}
	
	public void updateStatus(UBIEsocialEventosStage pUbesRow) {
		PreparedStatement stmt = null;
		
		try {
			stmt = conn.prepareStatement(
					"UPDATE ubi_eventos_esocial_stage ubes SET ubes.status = ? WHERE ubes.rowid = ?");
		
			stmt.setInt(1, pUbesRow.getStatus().getId());
			stmt.setString(2, pUbesRow.getRowId());
			
			stmt.execute();
			stmt.close();
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}		
	}
}
