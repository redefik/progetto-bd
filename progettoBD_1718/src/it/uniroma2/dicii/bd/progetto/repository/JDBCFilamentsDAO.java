package it.uniroma2.dicii.bd.progetto.repository;

import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.uniroma2.dicii.bd.progetto.errorLogic.BatchError;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.filament.BorderPoint;
import it.uniroma2.dicii.bd.progetto.filament.BorderPointFilament;
import it.uniroma2.dicii.bd.progetto.filament.Filament;
import it.uniroma2.dicii.bd.progetto.filament.FilamentWithSegments;
import it.uniroma2.dicii.bd.progetto.filament.SegmentPoint;
import it.uniroma2.dicii.bd.progetto.filament.SegmentPointImported;
import it.uniroma2.dicii.bd.progetto.satellite.InstrumentBean;


public class JDBCFilamentsDAO implements FilamentsRepository{
	
	private static final String QUERY_INSERT_FILAMENT = "INSERT INTO FILAMENTO VALUES (?,?,?,?,?,?)";
	private static final String QUERY_INSERT_BORDER_POINT_FILAMENT = "INSERT INTO PUNTOCONTORNOFILAMENTO VALUES (?,?,?,?)";
	private static final String QUERY_INSERT_BORDER_POINT = "INSERT INTO PUNTOCONTORNO VALUES (?,?,?)";
	private static final String QUERY_SEARCH_FILAMENT_BY_NAME = "SELECT * FROM FILAMENTO WHERE NOME = ?";
	private static final String QUERY_SEARCH_FILAMENT_BY_ID_AND_INSTRUMENT_1 = "SELECT * FROM FILAMENTO WHERE ID = ";
	private static final String QUERY_SEARCH_FILAMENT_BY_ID_AND_INSTRUMENT_2 = " AND (STRUMENTO = '";
	private static final String QUERY_SEARCH_FILAMENT_BY_ID_AND_INSTRUMENT_3 = " OR STRUMENTO = '";
	private static final String QUERY_SEARCH_FILAMENT_NAME = 
								"SELECT F.NOME, F.ID, F.ELLITTICITA, F.CONTRASTO, F.NUMEROSEGMENTI, F.STRUMENTO"
								+ " FROM FILAMENTO F JOIN STRUMENTO S ON F.STRUMENTO = S.NOME WHERE F.ID = ? AND S.Satellite = ?";
	private static final String QUERY_INSERT_SEGMENT_POINTS = "INSERT INTO PUNTOSEGMENTO VALUES(";
	private static final String QUERY_UPDATE_SEGMENT_NUMBER = "UPDATE FILAMENTO SET NUMEROSEGMENTI = ";
	private static final String QUERY_SEARCH_BORDER = "SELECT LATITUDINE, LONGITUDINE FROM PUNTOCONTORNOFILAMENTO WHERE FILAMENTO = ? "
								+ "ORDER BY LATITUDINE, LONGITUDINE";
	private static final String QUERY_SEARCH_FILAMENT_PARTIALLY_INTO_REGION = "SELECT DISTINCT FILAMENTO FROM PUNTOCONTORNOFILAMENTO WHERE "
								+ "LATITUDINE <= ? AND LATITUDINE >= ? AND LONGITUDINE <= ? AND LONGITUDINE >= ?";
	private static final String QUERY_FIND_FILAMENT_BY_NAME = "SELECT * FROM FILAMENTO WHERE NOME = ?";
	private static final String QUERY_FIND_FILAMENT_BY_ID_AND_INSTRUMENT = "SELECT * FROM FILAMENTO WHERE ID = ? AND STRUMENTO = ?";
	private static final String QUERY_FIND_BORDER_POINTS_FILAMENT = "SELECT * FROM PUNTOCONTORNOFILAMENTO WHERE FILAMENTO = ?";
	private static final String QUERY_COUNT_FILAMENTS = "SELECT COUNT(*) FROM FILAMENTO;";
	private static final String QUERY_FIND_FILAMENT_BY_ELLIPTICITY_AND_CONTRAST = 
			"SELECT * FROM FILAMENTO WHERE ELLITTICITA >= ? AND ELLITTICITA <= ? AND CONTRASTO >= ?";
	private static final String QUERY_FIND_FILAMENT_BY_NUM_OF_SEGMENTS = 
			"SELECT * FROM FILAMENTO WHERE NUMEROSEGMENTI >= ? AND NUMEROSEGMENTI <= ?";
	private static final String QUERY_FIND_BORDER_POINT_IN_THE_AREA = 
			"SELECT * FROM PUNTOCONTORNOFILAMENTO "
			+ "EXCEPT SELECT * FROM PUNTOCONTORNOFILAMENTO WHERE ?>LATITUDINE OR LATITUDINE>? OR ?>LONGITUDINE OR LONGITUDINE>?";
	private static final String QUERY_FIND_SEGMENT_BY_ID_AND_SATELLITE_NAME = "SELECT * FROM PUNTOSEGMENTO WHERE FILAMENTO = ? AND IDSEGMENTO = ?";
	private static final String QUERY_FIND_FILAMENT_BORDER = 
			"SELECT * FROM PUNTOCONTORNOFILAMENTO WHERE FILAMENTO = ? AND SATELLITE = ?";
	
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
		
		//Se la lista di strumenti è vuota si ritorna il valore null a significare che il filamento non è stato trovato
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
	
	
	private ArrayList<SegmentPoint> getValidSegmentPoints(ArrayList<SegmentPointImported> importedPoints, 
			String selectedSatellite) throws ConfigurationError, DataAccessError {
		Connection connection = null;
		JDBCConnectionPool jdbcConnectionPool = null;
		
		try {
			jdbcConnectionPool = JDBCConnectionPool.getInstance();
			connection = jdbcConnectionPool.getConnection();
			
			PreparedStatement stmt = connection.prepareStatement(QUERY_SEARCH_FILAMENT_NAME);
			stmt.setString(2, selectedSatellite);
			
			Map<Integer, Filament> filIdfilMap = new HashMap<Integer, Filament>();
			ArrayList<SegmentPoint> segmentPoints = new ArrayList<>();
			
			for (SegmentPointImported imported : importedPoints) {
				int filamentId = imported.getFilamentId();
				Filament filament = filIdfilMap.get(filamentId);
				// il filamento corrispondente ad un dato id viene ricercato una volta sola e poi inserito nella mappa
				if (filament == null) {
					stmt.setInt(1, filamentId);
					ResultSet rs = stmt.executeQuery();
					// questo controllo e' necessario perche' i punti importati potrebbero far riferimento a filamenti non presenti nel db
					if (rs.next()) {
						filament = new Filament();
						filament.setName(rs.getString(1));
						filament.setNumber(rs.getInt(2));
						filament.setEllipticity(rs.getDouble(3));
						filament.setContrast(rs.getDouble(4));
						filament.setNumberOfSegments(rs.getInt(5));
						filament.setInstrumentName(rs.getString(6));
						filIdfilMap.put(filamentId, filament);
					}
				}
				// i punti che fanno riferimento a filamenti presenti nel db vengono ignorati
				if (filament != null) {
					SegmentPoint segmentPoint = new SegmentPoint();
					segmentPoint.setFilament(filament);
					segmentPoint.setLatitude(imported.getLatitude());
					segmentPoint.setLongitude(imported.getLongitude());
					segmentPoint.setProgNumber(imported.getProgNumber());
					segmentPoint.setSegmentId(imported.getSegmentId());
					segmentPoint.setType(imported.getType());
					segmentPoints.add(segmentPoint);
				}
			}
			return segmentPoints;
		} catch (ClassNotFoundException | IOException e) {
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
	public void insertAllSegmentPoints(ArrayList<SegmentPointImported> segmentPoints, String selectedSatellite) 
			throws ConfigurationError, DataAccessError, BatchError {
		
		Connection connection = null;
		JDBCConnectionPool jdbcConnectionPool = null;
		
		try {
		
			// si ricavano innanzitutto le entry corrispondenti ai punti importati rimuovendo dalla lista quelli che si riferiscono
			// a filamenti inesistenti
			ArrayList<SegmentPoint> segmentPointsToInsert = getValidSegmentPoints(segmentPoints, selectedSatellite);
			
			jdbcConnectionPool = JDBCConnectionPool.getInstance();
			connection = jdbcConnectionPool.getConnection();
			
			connection.setAutoCommit(false);
			Statement statement = connection.createStatement();
			
			Map<String, FilamentWithSegments> filamentSegmentsMap = new HashMap<String, FilamentWithSegments>();
			
			for (SegmentPoint point : segmentPointsToInsert) {
				Filament filament = point.getFilament();
				String filamentName = filament.getName();
				int filamentCurrentNumberOfSegments = filament.getNumberOfSegments();
				int segmentId = point.getSegmentId();
				// si costruisce il comando SQL per inserire PuntoSegmento e lo si aggiunge al batchUpdate
				StringBuilder queryBuilder = new StringBuilder(QUERY_INSERT_SEGMENT_POINTS);
				queryBuilder.append(point.getLatitude());
				queryBuilder.append("," + point.getLongitude());
				queryBuilder.append(",'" + filamentName + "'");
				queryBuilder.append("," + segmentId);
				queryBuilder.append("," + point.getProgNumber());
				queryBuilder.append(",'" + String.valueOf(point.getType()) + "')");
				statement.addBatch(queryBuilder.toString());
				// contestualmente, si aggiorna il numero di segmenti del filamento a cui appartiene il punto
				FilamentWithSegments filamentWithSegments = filamentSegmentsMap.get(filamentName);
				if (filamentWithSegments == null) { // se il filamento non e' ancora presente nella mappa lo si aggiunge
					filamentWithSegments = new FilamentWithSegments(filamentName, filamentCurrentNumberOfSegments);
				}
				filamentWithSegments.updateSegments(segmentId);
				filamentSegmentsMap.put(filamentName, filamentWithSegments);
			}
			
			// si aggiorna il valore dell'attributo NumeroSegmenti per tutti i filamenti coinvolti nell'inserimento dei punti
			for (Map.Entry<String, FilamentWithSegments> entry : filamentSegmentsMap.entrySet()) {
				FilamentWithSegments filament = entry.getValue();
				String filamentName = filament.getName();
				int segmentsNumber = filament.getNumberOfSegments();
				StringBuilder queryBuilder = new StringBuilder(QUERY_UPDATE_SEGMENT_NUMBER);
				queryBuilder.append(segmentsNumber);
				queryBuilder.append(" WHERE NOME = '" + filamentName + "'");
				statement.addBatch(queryBuilder.toString());
			}
			
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
	public boolean existFilamentWithName(String name) throws ConfigurationError, DataAccessError {
		
		Connection connection = null;
		JDBCConnectionPool jdbcConnectionPool = null;
				
		try {
					
			//Si richiede una connessione al JDBCConnectionPool
			jdbcConnectionPool = JDBCConnectionPool.getInstance();
			connection = jdbcConnectionPool.getConnection();
					
			PreparedStatement statement = connection.prepareStatement(QUERY_SEARCH_FILAMENT_BY_NAME);
			statement.setString(1, name);
			ResultSet resultSet = statement.executeQuery();
			
			return (resultSet.next());
					
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
	public ArrayList<BorderPoint> findBorder(String filament) throws ConfigurationError, DataAccessError {
		Connection connection = null;
		JDBCConnectionPool jdbcConnectionPool = null;
		
		try {
			
			//Si richiede una connessione al JDBCConnectionPool
			jdbcConnectionPool = JDBCConnectionPool.getInstance();
			connection = jdbcConnectionPool.getConnection();
			
			PreparedStatement statement = connection.prepareStatement(QUERY_SEARCH_BORDER);
			statement.setString(1, filament);
			ResultSet resultSet = statement.executeQuery();
			
			ArrayList<BorderPoint> border = new ArrayList<BorderPoint>();
			
			//Per ogni punto del contorno trovato si crea un oggetto BorderPoint e si aggiunge a border
			while (resultSet.next()) {
				
				BorderPoint borderPoint = new BorderPoint();
				borderPoint.setLatitude(resultSet.getDouble("latitudine"));
				borderPoint.setLongitude(resultSet.getDouble("longitudine"));
				borderPoint.setFilamentNames(null);
				borderPoint.setSatellite(null);
				
				border.add(borderPoint);
			}
		
			return border;
			
			
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
	public ArrayList<String> findAllFilamentPartiallyIntoRegion(double latitude, double longitude, double width, double heigth)
			throws ConfigurationError, DataAccessError {
		
		Connection connection = null;
		JDBCConnectionPool jdbcConnectionPool = null;
		
		try {
			
			//Si richiede una connessione al JDBCConnectionPool
			jdbcConnectionPool = JDBCConnectionPool.getInstance();
			connection = jdbcConnectionPool.getConnection();
			
			PreparedStatement statement = connection.prepareStatement(QUERY_SEARCH_FILAMENT_PARTIALLY_INTO_REGION);
			statement.setDouble(1, latitude + (heigth/2));
			statement.setDouble(2, latitude - (heigth/2));
			statement.setDouble(3, longitude + (width/2));
			statement.setDouble(4, longitude - (width/2));
			
			ResultSet resultSet = statement.executeQuery();
			
			ArrayList<String> filaments = new ArrayList<String>();
			
			//Per ogni filamento trovato, il suo nome viene aggiunto alla lista che sarà restituita
			while (resultSet.next()) {
				
				filaments.add(resultSet.getString("filamento"));
			}
		
			return filaments;
			
			
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
	public Filament findFilamentByName(String name) throws ConfigurationError, DataAccessError {
		Connection connection = null;
		JDBCConnectionPool jdbcConnectionPool = null;
		
		try {
			jdbcConnectionPool = JDBCConnectionPool.getInstance();
			connection = jdbcConnectionPool.getConnection();
			PreparedStatement statement = connection.prepareStatement(QUERY_FIND_FILAMENT_BY_NAME);
			statement.setString(1, name);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				Filament filament = new Filament();
				filament.setName(resultSet.getString(1));
				filament.setNumber(resultSet.getInt(2));
				filament.setEllipticity(resultSet.getDouble(3));
				filament.setContrast(resultSet.getDouble(4));
				filament.setNumberOfSegments(resultSet.getInt(5));
				filament.setInstrumentName(resultSet.getString(6));
				return filament;
			} else {
				return null;
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

	@Override
	public Filament findFilamentByIdAndInstrument(int filamentId, String instrumentName) throws ConfigurationError, DataAccessError {
		Connection connection = null;
		JDBCConnectionPool jdbcConnectionPool = null;
		
		try {
			jdbcConnectionPool = JDBCConnectionPool.getInstance();
			connection = jdbcConnectionPool.getConnection();
			PreparedStatement statement = connection.prepareStatement(QUERY_FIND_FILAMENT_BY_ID_AND_INSTRUMENT);
			statement.setInt(1, filamentId);
			statement.setString(2, instrumentName);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				Filament filament = new Filament();
				filament.setName(resultSet.getString(1));
				filament.setNumber(resultSet.getInt(2));
				filament.setEllipticity(resultSet.getDouble(3));
				filament.setContrast(resultSet.getDouble(4));
				filament.setNumberOfSegments(resultSet.getInt(5));
				filament.setInstrumentName(resultSet.getString(6));
				return filament;
			} else {
				return null;
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

	@Override
	public ArrayList<BorderPointFilament> findBorderPointsOfFilament(Filament filament)
			throws ConfigurationError, DataAccessError {
		Connection connection = null;
		JDBCConnectionPool jdbcConnectionPool = null;
		
		try {
			jdbcConnectionPool = JDBCConnectionPool.getInstance();
			connection = jdbcConnectionPool.getConnection();
			PreparedStatement statement = connection.prepareStatement(QUERY_FIND_BORDER_POINTS_FILAMENT);
			statement.setString(1, filament.getName());
			ResultSet resultSet = statement.executeQuery();
			ArrayList<BorderPointFilament> borderPoints = new ArrayList<>();
			while (resultSet.next()) {
				double pointLatitude = resultSet.getDouble(1);
				double pointLongitude = resultSet.getDouble(2);
				String filamentName = resultSet.getString(4);
				String satelliteName = resultSet.getString(3);
				BorderPointFilament borderPoint = new BorderPointFilament(pointLatitude, pointLongitude, filamentName, satelliteName);
				borderPoints.add(borderPoint);
			}
			return borderPoints;
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
	public long getFilamentsCount() throws ConfigurationError, DataAccessError {
		Connection connection = null;
		JDBCConnectionPool jdbcConnectionPool = null;
		
		try {
			jdbcConnectionPool = JDBCConnectionPool.getInstance();
			connection = jdbcConnectionPool.getConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(QUERY_COUNT_FILAMENTS);
			resultSet.next();
			return resultSet.getLong(1);
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
	public ArrayList<Filament> findFilamentsByContrastAndEllipticity(double minContrast, double minEllipticity,
			double maxEllipticity) throws ConfigurationError, DataAccessError {
		Connection connection = null;
		JDBCConnectionPool jdbcConnectionPool = null;
		
		try {
			jdbcConnectionPool = JDBCConnectionPool.getInstance();
			connection = jdbcConnectionPool.getConnection();
			PreparedStatement statement = connection.prepareStatement(QUERY_FIND_FILAMENT_BY_ELLIPTICITY_AND_CONTRAST);
			statement.setDouble(1, minEllipticity);
			statement.setDouble(2, maxEllipticity);
			statement.setDouble(3, minContrast);
			ResultSet resultSet = statement.executeQuery();
			ArrayList<Filament> filaments = new ArrayList<>();
			while (resultSet.next()) {
				Filament filament = new Filament();
				filament.setName(resultSet.getString(1));
				filament.setNumber(resultSet.getInt(2));
				filament.setEllipticity(resultSet.getDouble(3));
				filament.setContrast(resultSet.getDouble(4));
				filament.setNumberOfSegments(resultSet.getInt(5));
				filament.setInstrumentName(resultSet.getString(6));
				filaments.add(filament);
			}
			return filaments;
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
	public ArrayList<Filament> findFilamentByNumOfSegments(int minNum, int maxNum)
			throws ConfigurationError, DataAccessError {
		Connection connection = null;
		JDBCConnectionPool jdbcConnectionPool = null;
		
		try {
			jdbcConnectionPool = JDBCConnectionPool.getInstance();
			connection = jdbcConnectionPool.getConnection();
			PreparedStatement statement = connection.prepareStatement(QUERY_FIND_FILAMENT_BY_NUM_OF_SEGMENTS);
			statement.setInt(1, minNum);
			statement.setInt(2, maxNum);
			ResultSet resultSet = statement.executeQuery();
			ArrayList<Filament> filaments = new ArrayList<>();
			while (resultSet.next()) {
				Filament filament = new Filament();
				filament.setName(resultSet.getString(1));
				filament.setNumber(resultSet.getInt(2));
				filament.setEllipticity(resultSet.getDouble(3));
				filament.setContrast(resultSet.getDouble(4));
				filament.setNumberOfSegments(resultSet.getInt(5));
				filament.setInstrumentName(resultSet.getString(6));
				filaments.add(filament);
			}
			return filaments;
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
	
	//_________________________________________________________________________________________
	
	@Override
	public ArrayList<BorderPointFilament> findFilamentsWithBorderPointsInSquare(double x0, double x1, double y0, double y1) throws ConfigurationError, DataAccessError {
		
		ArrayList<BorderPointFilament> filamentsWithBorderPointsInArea = new ArrayList<>();
		
		Connection connection = null;
		JDBCConnectionPool jdbcConnectionPool = null;
		try {
			//Si richiede una connessione al JDBCConnectionPool
			jdbcConnectionPool = JDBCConnectionPool.getInstance();
			connection = jdbcConnectionPool.getConnection();
			
			//Si imposta l'autocommit a false. Infatti se l'inserimento di una tupla nel database fallisce allora
			//e' neccessario effettuare un rollback
			connection.setAutoCommit(false);
			
			//Trovo tutti i filamenti presenti nell'area selezionata
			PreparedStatement statement = connection.prepareStatement(QUERY_FIND_BORDER_POINT_IN_THE_AREA);
			statement.setDouble(1, x0);
			statement.setDouble(2, x1);
			statement.setDouble(3, y0);
			statement.setDouble(4, y1);
			ResultSet resultSet = statement.executeQuery();
			while(resultSet.next()) { // prendo i punti del contorno che rispettano le caratteristiche
				BorderPointFilament filamentWithBorder = new BorderPointFilament();
				filamentWithBorder.setPointLatitude(resultSet.getDouble("latitudine"));
				filamentWithBorder.setPointLongitude(resultSet.getDouble("longitudine"));
				filamentWithBorder.setSatelliteName(resultSet.getString("satellite"));
				filamentWithBorder.setFilamentName(resultSet.getString("filamento"));
				filamentsWithBorderPointsInArea.add(filamentWithBorder);				
			}
			connection.commit();
			return filamentsWithBorderPointsInArea;
				
			} catch (IOException | ClassNotFoundException | NullPointerException e) {
				throw new ConfigurationError(e.getMessage(), e.getCause());
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
	public Filament findFilamentByBorderPoints(BorderPointFilament filamentWithBorderPoints) throws DataAccessError, ConfigurationError {
		Connection connection = null;
		JDBCConnectionPool jdbcConnectionPool = null;
		Filament filament = new Filament();

		try {
			//Si richiede una connessione al JDBCConnectionPool
			jdbcConnectionPool = JDBCConnectionPool.getInstance();
			connection = jdbcConnectionPool.getConnection();
			PreparedStatement statement = connection.prepareStatement(QUERY_FIND_FILAMENT_BY_NAME);
			statement.setString(1, filamentWithBorderPoints.getFilamentName());
				
			ResultSet resultSet = statement.executeQuery();
			resultSet.next();
			filament.setName(filamentWithBorderPoints.getFilamentName());
			filament.setNumber(resultSet.getInt("id"));
			filament.setNumberOfSegments(resultSet.getInt("numerosegmenti"));
			filament.setEllipticity(resultSet.getDouble("ellitticita"));
			filament.setContrast(resultSet.getDouble("contrasto"));
			filament.setInstrumentName(resultSet.getString("strumento"));
			
			return filament;
			
		}catch (IOException | ClassNotFoundException | NullPointerException e) {
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
	
	// La funzione cerca tutti i punti del segmento che formano il segmento con dato id e nome satellite
	@Override
	public ArrayList<SegmentPoint> findSegmentBySatelliteNameAndId(String filamentName, int idSegment) throws DataAccessError, ConfigurationError {
		ArrayList<SegmentPoint> segment = new ArrayList<SegmentPoint>();

		Connection connection = null;
		JDBCConnectionPool jdbcConnectionPool = null;

		try {
			//Si richiede una connessione al JDBCConnectionPool
			jdbcConnectionPool = JDBCConnectionPool.getInstance();
			connection = jdbcConnectionPool.getConnection();
			PreparedStatement statement = connection.prepareStatement(QUERY_FIND_SEGMENT_BY_ID_AND_SATELLITE_NAME);
			statement.setString(1, filamentName);
			statement.setInt(2,idSegment);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				SegmentPoint segmentPoint = new SegmentPoint();
				
				segmentPoint.setFilament(new Filament(filamentName));
				segmentPoint.setSegmentId(idSegment);
				segmentPoint.setLatitude(resultSet.getDouble("latitudine"));
				segmentPoint.setLongitude(resultSet.getDouble("longitudine"));
				segmentPoint.setProgNumber(resultSet.getInt("numeroprogressivo"));
				segmentPoint.setType(resultSet.getString("tipo").charAt(0));

				segment.add(segmentPoint);
			}
			return segment;
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
	public ArrayList<BorderPointFilament> findFilamentBorder(String filamentName, String satelliteName) throws ConfigurationError, DataAccessError {
		
		Connection connection = null;
		JDBCConnectionPool jdbcConnectionPool = null;
		ArrayList<BorderPointFilament> border = new ArrayList<>();
		try {
			//Si richiede una connessione al JDBCConnectionPool
			jdbcConnectionPool = JDBCConnectionPool.getInstance();
			connection = jdbcConnectionPool.getConnection();
			
			//Si imposta l'autocommit a false. Infatti se l'inserimento di una tupla nel database fallisce allora
			//e' neccessario effettuare un rollback
			connection.setAutoCommit(false);
			
			//cerco tutti i punti del bordo del filamento
			PreparedStatement statement = connection.prepareStatement(QUERY_FIND_FILAMENT_BORDER);
			statement.setString(1, filamentName);
			statement.setString(2, satelliteName);
			ResultSet resultSet = statement.executeQuery();
			while(resultSet.next()) {
				BorderPointFilament filamentsWithBorderPoints = new BorderPointFilament();
				filamentsWithBorderPoints.setFilamentName(resultSet.getString("filamento"));
				filamentsWithBorderPoints.setSatelliteName(resultSet.getString("satellite"));
				filamentsWithBorderPoints.setPointLatitude(resultSet.getDouble("latitudine"));
				filamentsWithBorderPoints.setPointLongitude(resultSet.getDouble("longitudine"));
				border.add(filamentsWithBorderPoints);
			}
			
			connection.commit();
			return border;
			
		} catch (IOException | ClassNotFoundException | NullPointerException e) {
			throw new ConfigurationError(e.getMessage(), e.getCause());
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
