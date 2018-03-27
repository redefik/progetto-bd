package it.uniroma2.dicii.bd.progetto.repository;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCConnectionManager {
	
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
        	Properties properties = new Properties();
            InputStream in = JDBCConnectionManager.class.getResourceAsStream("config.properties");
            properties.load(in);
            String driverClass = properties.getProperty("dbdriver");
            Class.forName(driverClass);
            String user = properties.getProperty("dbuser");
            String password = properties.getProperty("dbpassword");
            instance = new JDBCConnectionManager(user, password);
        }
        return instance;
    }
    
    public Connection openConnection (DBType type) throws Exception {
    	
    	String dbUrlKey;
    	String dbUrl;
    	
    	switch (type) {
    		case DB_USER:
    			dbUrlKey = "dburl_user";
    			break;
    		case DB_SPACE:
    			dbUrlKey = "dburl_space";
    			break;
    		default:
    			throw new Exception();
    	}
    	
    	Properties properties = new Properties();
        InputStream in = JDBCConnectionManager.class.getResourceAsStream("config.properties");
        properties.load(in);
        dbUrl = properties.getProperty(dbUrlKey);
        Connection connection = DriverManager.getConnection(dbUrl, this.dbUsername, this.dbPassword);
        return connection;
        
    }

	public void closeConnection(Connection connection) throws SQLException {
		connection.close();
	}
	

}
