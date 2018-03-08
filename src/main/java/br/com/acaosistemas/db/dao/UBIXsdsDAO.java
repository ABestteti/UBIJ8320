package br.com.acaosistemas.db.dao;

import oracle.jdbc.OracleConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.acaosistemas.db.connection.ConnectionFactory;
import br.com.acaosistemas.db.model.UBIXsds;

/**
 * DAO para recuperar do banco os valores dos XSDs de eventos armazenados na
 * tabela UBI_XSDS.
 * <p>
 * <b>Empresa:</b> Acao Sistemas de Informatica Ltda.
 * <p>
 * Alterações:
 * <p>
 * 2018.03.08 - ABS - Adicionado sistema de log com a biblioteca log4j2.
 * 
 * @author Anderson Bestteti Santos
 *
 */
public class UBIXsdsDAO {

	private static final Logger logger = LogManager.getLogger(UBIXsdsDAO.class);
	
	private OracleConnection conn;
	private UBIXsds          xsds;
	
	public UBIXsdsDAO() {
		conn = new ConnectionFactory().getConnection();
	}
	
	public UBIXsds getUBIXsds(String pNameSpace) {
		xsds = new UBIXsds();
		
		PreparedStatement stmt = null;
		
		try {
			stmt = conn.prepareStatement(
					  "SELECT "
					+ "   uxsd.codigo, "
					+ "   uxsd.documento, "
					+ "   uxsd.rowid "
					+ "FROM "
					+ "   ubi_xsds uxsd "
					+ "WHERE "
					+ "   uxsd.target_namespace = ?");
			
			stmt.setString(1, pNameSpace);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				xsds.setCodigo(rs.getInt("codigo"));
				xsds.setDocumento(rs.getNClob("documento"));
				xsds.setRowId(rs.getString("rowid"));
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
		return xsds;
	}
}
