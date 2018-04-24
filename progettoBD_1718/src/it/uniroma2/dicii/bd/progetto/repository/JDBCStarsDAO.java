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
import it.uniroma2.dicii.bd.progetto.star.Star;

public class JDBCStarsDAO implements StarsRepository {
	
	private static final String INSERT_STAR_QUERY = "INSERT INTO STELLA VALUES (?,?,?,?,?,?,?)";


	@Override
	public void insertAllStars(ArrayList<Star> stars) throws ConfigurationError, DataAccessError, BatchError {
		
		Connection connection = null;
		JDBCConnectionPool jdbcConnectionPool = null;
		
		try {
			
			//Si richiede una connessione al JDBCConnectionPool
			jdbcConnectionPool = JDBCConnectionPool.getInstance();
			connection = jdbcConnectionPool.getConnection();
			
			//Si imposta l'autocommit a false. Infatti se l'inserimento di una tupla nel database fallisce allora
			//e' neccessario effettuare un rollback
			connection.setAutoCommit(false);
			
			PreparedStatement statement = connection.prepareStatement(INSERT_STAR_QUERY);
			
			//Per ogni stella da memorizzare si genera una query per l'inserimento e si aggiunge al Batch
			for (Star star : stars) {

				statement.setString(1,star.getName());
				statement.setInt(2,star.getId());
				statement.setDouble(3,star.getLatitude());
				statement.setDouble(4,star.getLongitude());
				statement.setDouble(5,star.getFlow());
				statement.setString(6,star.getClassification());
				statement.setString(7,star.getSatellite());

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
