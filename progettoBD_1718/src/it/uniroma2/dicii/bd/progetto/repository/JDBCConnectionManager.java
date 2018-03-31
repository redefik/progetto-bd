package it.uniroma2.dicii.bd.progetto.repository;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;

// Classe Singleton che gestisce l'apertura e la chiusura delle connessioni con un database 
public class JDBCConnectionManager {
	
	private final static String INVALID_DB_TYPE = "Database non valido";
	private static final String CONFIGURATION_FILE = "/config.properties";
	private static final String DBDRIVER_KEY = "dbdriver";
	private static final String DBUSER_KEY = "dbuser";
	private static final String DBPASSWORD_KEY = "dbpassword";
	private static final String DB_URL_USER_KEY = "dburl_user";
	private static final String DB_URL_SPACE_KEY = "dburl_space";
	
	private static JDBCConnectionManager instance;
	private String dbUsername;
	private String dbPassword;
	
	protected JDBCConnectionManager () {}
	
	protected JDBCConnectionManager (String dbUsername, String dbPassword) {
		this.dbPassword = dbPassword;
		this.dbUsername = dbUsername;
	}

	
    public synchronized static JDBCConnectionManager getInstance() throws IOException, ClassNotFoundException {
        if (instance == null) {
        	// Legge da un file .properties i parametri di accesso al database 
        	Properties properties = new Properties();
            properties.load(JDBCConnectionManager.class.getResourceAsStream(CONFIGURATION_FILE));
            String driverClass = properties.getProperty(DBDRIVER_KEY);
            Class.forName(driverClass);
            String user = properties.getProperty(DBUSER_KEY);
            String password = properties.getProperty(DBPASSWORD_KEY);
            instance = new JDBCConnectionManager(user, password);
        }
        return instance;
    }
    
    
    public Connection openConnection (DBType type) throws IOException, SQLException, ConfigurationError {
    	
    	// Legge l'url del database corrispondente a type da un file .properties e stabilisce una connessione con esso
    	String dbUrlKey = null;
    	String dbUrl;
    	
    	switch (type) {
    		case DB_USER:
    			dbUrlKey = DB_URL_USER_KEY;
    			break;
    		case DB_SPACE:
    			dbUrlKey = DB_URL_SPACE_KEY;
    			break;
    		default:
    			throw new ConfigurationError(INVALID_DB_TYPE);
    	}
    	
    	Properties properties = new Properties();
        properties.load(JDBCConnectionManager.class.getResourceAsStream(CONFIGURATION_FILE));
        dbUrl = properties.getProperty(dbUrlKey);
        Connection connection = DriverManager.getConnection(dbUrl, this.dbUsername, this.dbPassword);
        return connection;
        
    }

	public void closeConnection(Connection connection) throws SQLException {
		connection.close();
	}
	

}
