package it.uniroma2.dicii.bd.progetto.repository;

import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import it.uniroma2.dicii.bd.progetto.errorLogic.BatchError;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.filament.BorderPoint;
import it.uniroma2.dicii.bd.progetto.filament.Filament;
import it.uniroma2.dicii.bd.progetto.satellite.InstrumentBean;


public class JDBCFilamentsDAO implements FilamentsRepository{
	
	private static final String QUERY_INSERT_FILAMENT = "INSERT INTO FILAMENTO VALUES (?,?,?,?,?,?)";
	private static final String QUERY_INSERT_BORDER_POINT_FILAMENT = "INSERT INTO PUNTOCONTORNOFILAMENTO VALUES (?,?,?,?)";
	private static final String QUERY_INSERT_BORDER_POINT = "INSERT INTO PUNTOCONTORNO VALUES (?,?,?)";
	private static final String QUERY_SEARCH_FILAMENT_BY_ID_AND_INSTRUMENT_1 = "SELECT * FROM FILAMENTO WHERE ID = ";
	private static final String QUERY_SEARCH_FILAMENT_BY_ID_AND_INSTRUMENT_2 = " AND (STRUMENTO = '";
	private static final String QUERY_SEARCH_FILAMENT_BY_ID_AND_INSTRUMENT_3 = " OR STRUMENTO = '";
	
	@Override
	public void insertAllFilaments(ArrayList<Filament> filaments) throws ConfigurationError, DataAccessError, BatchError {
		
		Connection connection = null;
		JDBCConnectionPool jdbcConnectionPool = null;
		
		try {
			
			//Si richiede una connessione al JDBCConnectionPool
			jdbcConnectionPool = JDBCConnectionPool.getInstance();
			connection = jdbcConnectionPool.getConnection();
			
			//Si imposta l'autocommit a false. Infatti se l'inserimento di una tupla nel database fallisce allora
			//e' neccessario effettuare un rollback
			connection.setAutoCommit(false);
			
			PreparedStatement statement = connection.prepareStatement(QUERY_INSERT_FILAMENT);
			
			//Per ogni filamento da memorizzare si genera una query per l'inserimento e si aggiunge al Batch
			for (Filament filament : filaments) {
				statement.setString(1, filament.getName());
				statement.setInt(2, filament.getNumber());
				statement.setDouble(3, filament.getEllipticity());
				statement.setDouble(4, filament.getContrast());
				statement.setInt(5, filament.getNumberOfSegments());
				statement.setString(6, filament.getInstrumentName());
				
				statement.addBatch();
			}
		
			//Si esegue il batch e se l'operazione ha successo si effettua il commit
			statement.executeBatch();
			connection.commit();
			
			
		} catch (IOException | ClassNotFoundException | NullPointerException e) {
			throw new ConfigurationError(e.getMessage(), e.getCause());
		} catch (BatchUpdateException e) {
			//Se una delle query nel batch fallisce si effettua il rollback e si solleva un eccezione ad hoc
			if (connection != null) {
				try {
					connection.rollback();
				} catch(SQLException e1) {
					throw new DataAccessError(e1.getMessage(), e1.getCause());
				}
			}
			throw new BatchError(e.getMessage(), e.getCause());
		} catch (SQLException e) {
			//Se viene sollevata una eccezione durante le operazioni sul database si effettua il rollback
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
		}
	}

	@Override
	public String searchFilamentByIdAndInstruments(int idFilament, ArrayList<InstrumentBean> instruments) throws ConfigurationError, DataAccessError {
		
		//Se la lista di strumenti � vuota si ritorna il valore null a significare che il filamento non � stato trovato
		if (instruments.get(0)== null) {
			return null;
		}
		
		Connection connection = null;
		JDBCConnectionPool jdbcConnectionPool = null;
		
		try {
			
			//Si richiede una connessione al JDBCConnectionPool
			jdbcConnectionPool = JDBCConnectionPool.getInstance();
			connection = jdbcConnectionPool.getConnection();
			
			//La query cerca il filamento avente id specificato e rilevato con uno degli strumenti indicati
 			String query = QUERY_SEARCH_FILAMENT_BY_ID_AND_INSTRUMENT_1 + idFilament + 
					QUERY_SEARCH_FILAMENT_BY_ID_AND_INSTRUMENT_2 + instruments.get(0).getName() +"' ";
			for (int i = 1; i < instruments.size(); i++) {
				query = query + QUERY_SEARCH_FILAMENT_BY_ID_AND_INSTRUMENT_3 + instruments.get(i).getName() +"' ";
			}
			query = query + ")";
			
			ResultSet resultSet = connection.createStatement().executeQuery(query);
		
			//Se viene trovato il filamento si restuisce il nome, altrimenti si torna null
			if (resultSet.next()) {
				return resultSet.getString("nome");
			}
			return null;
			
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
	public void insertAllBorderPoints(ArrayList<BorderPoint> borderPoints) throws ConfigurationError, DataAccessError, BatchError {
		
		Connection connection = null;
		JDBCConnectionPool jdbcConnectionPool = null;
		
		try {
			
			//Si richiede una connessione al JDBCConnectionPool
			jdbcConnectionPool = JDBCConnectionPool.getInstance();
			connection = jdbcConnectionPool.getConnection();
			
			//Si imposta l'autocommit a false. Infatti se l'inserimento di una tupla nel database fallisce allora
			//e' neccessario effettuare un rollback
			connection.setAutoCommit(false);
			
			PreparedStatement statementInsertBorderPoint = connection.prepareStatement(QUERY_INSERT_BORDER_POINT);
			PreparedStatement statementInsertBorderPointFilament = connection.prepareStatement(QUERY_INSERT_BORDER_POINT_FILAMENT);
			
			//Per ogni punto del contorno da memorizzare si genera una query per l'inserimento e si aggiunge al Batch
			for (BorderPoint borderPoint : borderPoints) {
				statementInsertBorderPoint.setDouble(1, borderPoint.getLatitude());
				statementInsertBorderPoint.setDouble(2, borderPoint.getLongitude());
				statementInsertBorderPoint.setString(3, borderPoint.getSatellite());
				
				statementInsertBorderPoint.addBatch();
				
				//Per ogni filamento a cui appartiene il punto del contorno si genera una query per l'inserimento di una
				//tupla nella relazione punto contorno - filamento e si aggiunge al batch
				for (String filamentName : borderPoint.getFilamentNames()) {
					statementInsertBorderPointFilament.setDouble(1, borderPoint.getLatitude());
					statementInsertBorderPointFilament.setDouble(2, borderPoint.getLongitude());
					statementInsertBorderPointFilament.setString(3, borderPoint.getSatellite());
					statementInsertBorderPointFilament.setString(4, filamentName);
					
					statementInsertBorderPointFilament.addBatch();
				}
			}
		
			//Si esegue il batch e se l'operazione ha successo si effettua il commit
			statementInsertBorderPoint.executeBatch();
			statementInsertBorderPointFilament.executeBatch();
			connection.commit();
			
			
		} catch (IOException | ClassNotFoundException | NullPointerException e) {
			throw new ConfigurationError(e.getMessage(), e.getCause());
		} catch (BatchUpdateException e) {
			//Se una delle query nel batch fallisce si effettua il rollback e si solleva un eccezione ad hoc
			if (connection != null) {
				try {
					connection.rollback();
				} catch(SQLException e1) {
					throw new DataAccessError(e1.getMessage(), e1.getCause());
				}
			}
			throw new BatchError(e.getMessage(), e.getCause());
		} catch (SQLException e) {
			//Se viene sollevata una eccezione durante le operazioni sul database si effettua il rollback
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
		}
	}
	
}
