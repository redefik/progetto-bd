package it.uniroma2.dicii.bd.progetto.repository;

import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import it.uniroma2.dicii.bd.progetto.errorLogic.BatchError;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.filament.Filament;


public class JDBCFilamentsDAO implements FilamentsRepository{
	
	private static final String INSERT_FILAMENT_QUERY = "INSERT INTO FILAMENTO VALUES (?,?,?,?,?,?)";


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
			
			PreparedStatement statement = connection.prepareStatement(INSERT_FILAMENT_QUERY);
			
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
	
}
