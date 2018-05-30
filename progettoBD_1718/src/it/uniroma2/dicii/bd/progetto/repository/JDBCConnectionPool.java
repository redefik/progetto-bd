 package it.uniroma2.dicii.bd.progetto.repository;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Vector;

public class JDBCConnectionPool {
	
	private static final String CONFIGURATION_FILE = "/config.properties";
	private static final String DBDRIVER_KEY = "dbdriver";
	private static final String DBUSER_KEY = "dbuser";
	private static final String DBPASSWORD_KEY = "dbpassword";
	private static final String DB_URL_SPACE_KEY = "dburl_space";
	
	//Il numero di connessioni che si vogliono mantenere attive contemporaneamente. Se necessario vengono create altre 
	//connessioni, quelle in eccesso vengono chiuse quando rilasciate.
	private static final int NUMBER_OF_CONNECTION = 3;
	
	private static JDBCConnectionPool instance;
	private Vector<Connection> connections;
	private String dbUsername;
	private String dbPassword;
	private String dbUrl;
		
	private JDBCConnectionPool() {}
	
	private JDBCConnectionPool (String dbUsername, String dbPassword, String dbUrl) throws IOException, SQLException {

		this.connections = new Vector<Connection>();
		this.dbUsername = dbUsername;
		this.dbPassword = dbPassword;
		this.dbUrl = dbUrl;
        
        int i;
        Connection connection;
        
        for (i = 0; i <= NUMBER_OF_CONNECTION; i++ ) {
        	connection = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.dbPassword);
        	connections.addElement(connection);
        }
	}
	
	public synchronized static JDBCConnectionPool getInstance() throws IOException, ClassNotFoundException, SQLException {
	    	
		if (instance == null) {
	        	
	       	Properties properties = new Properties();
	       	properties.load(JDBCConnectionPool.class.getResourceAsStream(CONFIGURATION_FILE));
	       	String driverClass = properties.getProperty(DBDRIVER_KEY);
	       	Class.forName(driverClass);
	       	String username = properties.getProperty(DBUSER_KEY);
	       	String password = properties.getProperty(DBPASSWORD_KEY);
	       	String url = properties.getProperty(DB_URL_SPACE_KEY); 
	       	instance = new JDBCConnectionPool(username, password, url);
	    }
	    return instance;
	}
	    
	public synchronized Connection getConnection() throws SQLException{
		Connection connection;
			      
		if (this.connections.size() > 0) {      
			connection = this.connections.firstElement();
			connections.removeElementAt(0);
			
			//Se la connessione prelevata non è più valida, si richiama ricorsivamente la funzione getConnetion
			if(connection.isClosed()) {          
			   connection = getConnection();
			}
			
		} else { 
			connection = DriverManager.getConnection(this.dbUrl, this.dbUsername, this.dbPassword);
		}
		
		return connection;
		
	} 
			   
	public synchronized void releaseConnection(Connection connection) throws SQLException {

		if (this.connections.size() > NUMBER_OF_CONNECTION) {
			connection.close();
		} else {
			//Prima di reinserire una connessione nel pool viene impostato l'autocommit a true nel caso in cui qualche
			//utilizzatore avesse sfruttato la modalita' di commit manuale
			connection.setAutoCommit(true);
			this.connections.add(connection);
		}
	}
	
}
