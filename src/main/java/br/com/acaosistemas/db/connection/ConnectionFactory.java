package br.com.acaosistemas.db.connection;

import java.sql.SQLException;

import oracle.jdbc.OracleConnection;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

public class ConnectionFactory {

	// Get the PoolDataSource for UCP
	static PoolDataSource   pds    = null;
	static OracleConnection connDB = null;
	
	public OracleConnection getConnection() {
	        try {	
	        	if (pds == null) {
	        		System.out.print("Conectando no banco de dados: ");

	        		pds = PoolDataSourceFactory.getPoolDataSource();

	        		// Prepara a conexao com o banco Oracle
	        		pds.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");	        
	        		pds.setURL("jdbc:oracle:thin:@"+DBConnectionInfo.getDbStrConnect());
	        		pds.setUser(DBConnectionInfo.getDbUserName());
	        		pds.setPassword(DBConnectionInfo.getDbPassWord());

	        		// Define propriedades do pool de conexao do banco Oracle
	        		pds.setConnectionPoolName("JDBC_UCP_POOL");
	        		pds.setInitialPoolSize(1);
	        		pds.setMinPoolSize(1);
	        		pds.setMaxPoolSize(5);
	        		
		           	// Cria uma conexao com o banco Oracle
	        		connDB = (OracleConnection) pds.getConnection();
	        		
	        		System.out.println("Ok");
	        	}
	        	
	        	return connDB;
	        } catch (SQLException e) {
	            System.err.println("Erro durante a conexo com o banco de dados.");
	            System.err.println("Revise se os parametros usuario, senha e string"); 
	            System.err.println("de conexao estao corretos, ou se existe algum problema com a rede.");
	            throw new RuntimeException(e);
	        }
		}
	}