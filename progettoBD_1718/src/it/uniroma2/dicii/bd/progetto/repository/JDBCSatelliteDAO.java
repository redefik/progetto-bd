package it.uniroma2.dicii.bd.progetto.repository;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.satellite.Agency;
import it.uniroma2.dicii.bd.progetto.satellite.Satellite;

public class JDBCSatelliteDAO implements SatellitesRepository {
	
	private static final String QUERY_FIND_ALL_AGENCIES = "SELECT * FROM AGENZIA";
	private static final String QUERY_INSERT_SATELLITE = "INSERT INTO SATELLITE VALUES (?,?,?)";
	private static final String QUERY_INSERT_AGENCY_SATELLITE = "INSERT INTO AGENZIASATELLITE VALUES (?,?)";
	private static final String QUERY_SEARCH_SATELLITE = "SELECT * FROM SATELLITE WHERE NOME = ?";

	public ArrayList<Agency> findAllAgencies() throws ConfigurationError, DataAccessError{
		
		Connection connection = null;
		JDBCConnectionPool jdbcConnectionPool = null;
		
		try {
			
			Agency agency = null;
			ArrayList<Agency> agencies = new ArrayList<Agency>();
			
			jdbcConnectionPool = JDBCConnectionPool.getInstance();
			connection = jdbcConnectionPool.getConnection();
			PreparedStatement statement = connection.prepareStatement(QUERY_FIND_ALL_AGENCIES);
			ResultSet resultSet = statement.executeQuery();
			
			while (resultSet.next()) {
				agency = new Agency (resultSet.getString("nome"));
				agencies.add(agency);
			}
			
			return agencies;
			
		} catch (IOException | ClassNotFoundException | NullPointerException e) {
			throw new ConfigurationError(e.getMessage(), e.getCause());
		} catch (SQLException e) {
			throw new DataAccessError(e.getMessage(), e.getCause());
		} finally {
			if (jdbcConnectionPool != null) {
				try {
					jdbcConnectionPool.releaseConnection(connection);
				} catch (SQLException e) {
					throw new DataAccessError(e.getMessage(), e.getCause());
				}
			}
		}
	
	}

	@Override
	public void persistSatellite(Satellite satellite, ArrayList<Agency> agencies) throws ConfigurationError, DataAccessError {
		Connection connection = null;
		JDBCConnectionPool jdbcConnectionPool = null;
		
		try {
			
			jdbcConnectionPool = JDBCConnectionPool.getInstance();
			connection = jdbcConnectionPool.getConnection();
			connection.setAutoCommit(false);
			PreparedStatement statement = connection.prepareStatement(QUERY_INSERT_SATELLITE);
			statement.setString(1, satellite.getName());
			statement.setDate(2, new Date(satellite.getBeginDate().getTime()));
			
			if (satellite.getEndDate() == null) {
				statement.setDate(3, null);
			} else {
				statement.setDate(3, new Date(satellite.getEndDate().getTime()));
			}
			if (statement.executeUpdate() != 1) {
				connection.rollback();
				throw new DataAccessError(DataAccessError.INSERT_FAILED);
			}
			
			statement = connection.prepareStatement(QUERY_INSERT_AGENCY_SATELLITE);
			statement.setString(2, satellite.getName());
			
			for (Agency agency : agencies) {
				statement.setString(1, agency.getName());
				
				if (statement.executeUpdate() != 1) {
					connection.rollback();
					throw new DataAccessError(DataAccessError.INSERT_FAILED);
				}
			}
			
			connection.commit();
			
		} catch (IOException | ClassNotFoundException | NullPointerException e) {
			throw new ConfigurationError(e.getMessage(), e.getCause());
		} catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch(SQLException e1) {
					throw new DataAccessError(e1.getMessage(), e1.getCause());
				}
			}
			throw new DataAccessError(e.getMessage(), e.getCause());
		} finally {
			if (jdbcConnectionPool != null) {
				try {
					jdbcConnectionPool.releaseConnection(connection);
				} catch (SQLException e) {
					throw new DataAccessError(e.getMessage(), e.getCause());
				}
			}
			if (connection != null) {
				try {
					connection.setAutoCommit(true);
				} catch(SQLException e) {
					throw new DataAccessError(e.getMessage(), e.getCause());
				}
			}
		}
	
	}

	@Override
	public boolean existsSatelliteWithName(String name) throws ConfigurationError, DataAccessError {
		Connection connection = null;
		JDBCConnectionPool jdbcConnectionPool = null;
		
		try {
			
			jdbcConnectionPool = JDBCConnectionPool.getInstance();
			connection = jdbcConnectionPool.getConnection();
			PreparedStatement statement = connection.prepareStatement(QUERY_SEARCH_SATELLITE);
			statement.setString(1, name);

			ResultSet resultSet = statement.executeQuery();
			
			return resultSet.next();
			
		} catch (IOException | ClassNotFoundException | NullPointerException e) {
			throw new ConfigurationError(e.getMessage(), e.getCause());
		} catch (SQLException e) {
			throw new DataAccessError(e.getMessage(), e.getCause());
		} finally {
			if (jdbcConnectionPool != null) {
				try {
					jdbcConnectionPool.releaseConnection(connection);
				} catch (SQLException e) {
					throw new DataAccessError(e.getMessage(), e.getCause());
				}
			}
		}
	}
}
