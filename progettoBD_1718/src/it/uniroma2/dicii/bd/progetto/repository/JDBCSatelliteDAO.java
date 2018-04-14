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
import it.uniroma2.dicii.bd.progetto.satellite.Instrument;
import it.uniroma2.dicii.bd.progetto.satellite.Satellite;

// Per questioni di ottimizzazione sulla apertura e chiusura di connessioni la classe si serve di un oggetto JDBCConnectionPool
// che mantiene attive un certo numero di connessioni per passarle velocemente alle classi che ne fanno uso
public class JDBCSatelliteDAO implements SatellitesRepository {
	
	private static final String QUERY_FIND_ALL_AGENCIES = "SELECT * FROM AGENZIA";
	private static final String QUERY_FIND_ALL_SATELLITES = "SELECT * FROM SATELLITE";
	private static final String QUERY_FIND_ALL_INSTRUMENTS = "SELECT * FROM STRUMENTO WHERE SATELLITE = ?";
	private static final String QUERY_INSERT_SATELLITE = "INSERT INTO SATELLITE VALUES (?,?,?)";
	private static final String QUERY_INSERT_AGENCY_SATELLITE = "INSERT INTO AGENZIASATELLITE VALUES (?,?)";
	private static final String QUERY_SEARCH_SATELLITE = "SELECT * FROM SATELLITE WHERE NOME = ?";
	private static final String QUERY_INSERT_INSTRUMENT = "INSERT INTO STRUMENTO VALUES(?,?,?)";
	
	public ArrayList<Agency> findAllAgencies() throws ConfigurationError, DataAccessError{
		
		Connection connection = null;
		JDBCConnectionPool jdbcConnectionPool = null;
		
		try {
			
			Agency agency = null;
			ArrayList<Agency> agencies = new ArrayList<Agency>();
			
			//Si richiede una connessione al JDBCConnectionPool
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
			// Si restituisce la connessione al JDBCConnectionPool
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
			//Si richiede una connessione al JDBCConnectionPool

			jdbcConnectionPool = JDBCConnectionPool.getInstance();
			connection = jdbcConnectionPool.getConnection();
			
			//Si crea una transazione per gestire in maniera coerente l'inserimento di un satellite e di una tupla all'interno
			//della relazione satellite-agenzia per ogni agenzia coinvolta nella missione relativa al satellite

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
				//Se l'inserimento del satellite fallisce si effettua il rollback
				connection.rollback();
				throw new DataAccessError(DataAccessError.INSERT_FAILED);
			}
			
			statement = connection.prepareStatement(QUERY_INSERT_AGENCY_SATELLITE);
			statement.setString(2, satellite.getName());
			
			for (Agency agency : agencies) {
				statement.setString(1, agency.getName());
				
				if (statement.executeUpdate() != 1) {
					//Se l'inserimento della tupla satellite-agenzia fallisce si effettua il rollback
					connection.rollback();
					throw new DataAccessError(DataAccessError.INSERT_FAILED);
				}
			}
			
			//Soltanto nel caso in cui prima il satellite e poi e coppie satellite-agenzia sono state inserite 
			// correttamente si effettua il commit
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
			// Si restituisce la connessione al JDBCConnectionPool

			if (jdbcConnectionPool != null) {
				try {
					jdbcConnectionPool.releaseConnection(connection);
				} catch (SQLException e) {
					throw new DataAccessError(e.getMessage(), e.getCause());
				}
			}
			
			// Poichè la connessione non viene necessariamente chiusa ma puo' essere riusata c'e' necessita
			// di reimpostare l'autocommit
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

	@Override
	public ArrayList<Satellite> findAllSatellites() throws ConfigurationError, DataAccessError {
		Connection connection = null;
		JDBCConnectionPool jdbcConnectionPool = null;
		
		try {
			//Si richiede una connessione al JDBCConnectionPool
			Satellite satellite = null;
			ArrayList<Satellite> satellites = new ArrayList<Satellite>();
			
			jdbcConnectionPool = JDBCConnectionPool.getInstance();
			connection = jdbcConnectionPool.getConnection();
			PreparedStatement statement = connection.prepareStatement(QUERY_FIND_ALL_SATELLITES);
			ResultSet resultSet = statement.executeQuery();
			
			while (resultSet.next()) {
				satellite = new Satellite (
						resultSet.getString("nome"), resultSet.getDate("inizioattivita"), resultSet.getDate("fineattivita"));
				satellite.setInstruments(findAllInstrumenOfSatellite(satellite));
				satellites.add(satellite);
			}
			
			return satellites;
			
		} catch (IOException | ClassNotFoundException | NullPointerException e) {
			throw new ConfigurationError(e.getMessage(), e.getCause());
		} catch (SQLException e) {
			throw new DataAccessError(e.getMessage(), e.getCause());
		} finally {
			// Si restituisce la connessione al JDBCConnectionPool
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
	public ArrayList<Instrument> findAllInstrumenOfSatellite(Satellite satellite) throws ConfigurationError, DataAccessError{
		Connection connection = null;
		JDBCConnectionPool jdbcConnectionPool = null;
		
		try {
			
			Instrument instrument = null;
			ArrayList<Instrument> instruments = new ArrayList<Instrument>();
			
			//Si richiede una connessione al JDBCConnectionPool
			
			jdbcConnectionPool = JDBCConnectionPool.getInstance();
			connection = jdbcConnectionPool.getConnection();
			PreparedStatement statement = connection.prepareStatement(QUERY_FIND_ALL_INSTRUMENTS);
			statement.setString(1, satellite.getName());
			ResultSet resultSet = statement.executeQuery();
			
			while (resultSet.next()) {
				instrument = new Instrument (resultSet.getString("nome"), resultSet.getString("elencobande"));
				instruments.add(instrument);
			}
			
			return instruments;
			
		} catch (IOException | ClassNotFoundException | NullPointerException e) {
			throw new ConfigurationError(e.getMessage(), e.getCause());
		} catch (SQLException e) {
			throw new DataAccessError(e.getMessage(), e.getCause());
		} finally {
			// Si restituisce la connessione al JDBCConnectionPool
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
	public void persistInstrument(Instrument instrument, String satellite) throws ConfigurationError, DataAccessError {
		Connection connection = null;
		JDBCConnectionPool jdbcConnectionPool = null;
		
		try {
			//Si richiede una connessione al JDBCConnectionPool
			jdbcConnectionPool = JDBCConnectionPool.getInstance();
			connection = jdbcConnectionPool.getConnection();
			
			PreparedStatement statement = connection.prepareStatement(QUERY_INSERT_INSTRUMENT);
			statement.setString(1, instrument.getName());
			statement.setString(2, instrument.getListBands());
			statement.setString(3, satellite);
	
			//Se l'inserimento fallisce viene sollevata un eccezione
			if (statement.executeUpdate() != 1) {
				throw new DataAccessError(DataAccessError.INSERT_FAILED);
			}
			
		} catch (IOException | ClassNotFoundException | NullPointerException e) {
			throw new ConfigurationError(e.getMessage(), e.getCause());
		} catch (SQLException e) {
			throw new DataAccessError(e.getMessage(), e.getCause());
		} finally {
			// Si restituisce la connessione al JDBCConnectionPool
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
