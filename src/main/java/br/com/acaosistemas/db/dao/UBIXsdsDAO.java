package br.com.acaosistemas.db.dao;

import oracle.jdbc.OracleConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import br.com.acaosistemas.db.connection.ConnectionFactory;
import br.com.acaosistemas.db.model.UBIXsds;

public class UBIXsdsDAO {

	private OracleConnection conn;
	private UBIXsds          xsds;
	
	public UBIXsdsDAO() {
		conn = new ConnectionFactory().getConnection();
	}
	
//	public void closeConnection () {
//		try {
//			conn.close();
//		} catch (SQLException e) {
//			throw new RuntimeException(e);
//		}
//	}
	
	public UBIXsds getUBIXsds(String pNameSpace) {
		xsds = new UBIXsds();
		
		PreparedStatement stmt = null;
		
		try {
			stmt = conn.prepareStatement(
					"SELECT uxsd.codigo, uxsd.documento, uxsd.rowid FROM ubi_xsds uxsd WHERE uxsd.target_namespace = ?");
			
			stmt.setString(1, pNameSpace);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				xsds.setCodigo(rs.getInt("codigo"));
				xsds.setDocumento(rs.getNClob("documento"));
				xsds.setRowId(rs.getString("rowid"));
			}
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}		
		return xsds;
	}
}
