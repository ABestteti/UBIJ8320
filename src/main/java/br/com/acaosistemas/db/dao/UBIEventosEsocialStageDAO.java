package br.com.acaosistemas.db.dao;

import oracle.jdbc.OracleConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.acaosistemas.db.connection.ConnectionFactory;
import br.com.acaosistemas.db.enumeration.SimNaoEnum;
import br.com.acaosistemas.db.enumeration.StatusEsocialEventosStageEnum;
import br.com.acaosistemas.db.model.UBIEventosEsocialStage;

/**
 * DAO para manipulacao da tabela UBI_EVENTOS_ESOCIAL_STAGE
 * <p>
 * <b>Empresa:</b> Acao Sistemas de Informatica Ltda.
 * <p>
 * Alterações:
 * <p>
 * 2018.03.07 - ABS - Alteração da PK da tabela UBI_EVENTOS_ESOCIAL_STAGE, 
 *                    conforme SA 20330.
 *                  - Adicionado sistema de log com a biblioteca log4j2.
 * @author Anderson Bestteti Santos
 *
 */
public class UBIEventosEsocialStageDAO {

	private static final Logger logger = LogManager.getLogger(UBIEventosEsocialStageDAO.class);
	
	private OracleConnection       conn;
	private UBIEventosEsocialStage ubes;
	
	public UBIEventosEsocialStageDAO() {
		conn = new ConnectionFactory().getConnection();
	}

	public UBIEventosEsocialStage getUBIEsocialEventosStage(String pRowID) {
		ubes = new UBIEventosEsocialStage();
		
		PreparedStatement stmt = null;
		
		try {
			stmt = conn.prepareStatement(
					"SELECT "
					+ "   ubes.dt_mov, "
					+ "   ubes.seq_reg "
					+ "FROM "
					+ "   ubi_eventos_esocial_stage ubes "
					+ "WHERE "
					+ "   ubes.rowid = ?");
			
			stmt.setString(1, pRowID);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				ubes.setSeqReg(rs.getLong("seq_reg"));
				ubes.setDtMov(rs.getDate("dt_mov"));
			}
			
		} catch (SQLException e) {
			logger.error(e);
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				logger.error(e);
			}
		}
		return ubes;
	}
	
	public List<UBIEventosEsocialStage> listUBIEsocialEventosStage(StatusEsocialEventosStageEnum pStatus) {
		PreparedStatement stmt                                   = null;
		List<UBIEventosEsocialStage> listaUBIEsocialEventosStage = new ArrayList<UBIEventosEsocialStage>();
	
		try {
			stmt = conn.prepareStatement(
					  "SELECT "
					+ "   ubes.seq_reg, "
				    + "   ubes.dt_mov,"
					+ "   ubes.status, "
					+ "   ubes.xml_assinado, "
					+ "   ubes.xml, "
					+ "   ubes.id_esocial, "
					+ "   ubes.rowid "
					+ "FROM "
					+ "   ubi_eventos_esocial_stage ubes "
					+ "WHERE "
					+ "   ubes.status = ?");
			
			stmt.setInt(1, pStatus.getId());
			stmt.setFetchSize(100);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				UBIEventosEsocialStage ubes = new UBIEventosEsocialStage();
				
				ubes.setRowId(rs.getString("rowId"));
				ubes.setSeqReg(rs.getLong("seq_reg"));
				ubes.setDtMov(rs.getDate("dt_mov"));
				ubes.setStatus(StatusEsocialEventosStageEnum.getById(rs.getInt("status")));
				ubes.setXmlAssinado(SimNaoEnum.getById(rs.getString("xml_assinado")));
				ubes.setXml(rs.getNClob("xml"));
				ubes.setIdESocial(rs.getString("id_esocial"));
				
				listaUBIEsocialEventosStage.add(ubes);
			}

		} catch (SQLException e) {
			logger.error(e);
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				logger.error(e);
			}
		}
		return listaUBIEsocialEventosStage;
	}
	
	public void updateStatus(UBIEventosEsocialStage pUbesRow) {
		PreparedStatement stmt = null;
		
		try {
			stmt = conn.prepareStatement(
					"UPDATE ubi_eventos_esocial_stage ubes SET ubes.status = ? WHERE ubes.rowid = ?");
		
			stmt.setInt(1, pUbesRow.getStatus().getId());
			stmt.setString(2, pUbesRow.getRowId());
			
			stmt.execute();
			stmt.close();
			
		} catch (SQLException e) {
			logger.error(e);
		}		
	}

	public void updateXmlRetornoValidacao(UBIEventosEsocialStage pUbesRow) {
		PreparedStatement stmt = null;
		
		try {
			stmt = conn.prepareStatement(
					"UPDATE ubi_eventos_esocial_stage ubes SET ubes.xml_retorno_validacao = ?, ubes.status = ? WHERE ubes.rowid = ?");
		
			stmt.setString(1, pUbesRow.getXmlRetornoValidacao().toString());
			stmt.setInt(2, pUbesRow.getStatus().getId());
			stmt.setString(3, pUbesRow.getRowId());
			
			stmt.execute();
			stmt.close();
			
		} catch (SQLException e) {
			logger.error(e);
		}				
	}
}
