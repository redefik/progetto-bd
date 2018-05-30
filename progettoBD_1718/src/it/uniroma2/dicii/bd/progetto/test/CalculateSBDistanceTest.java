package it.uniroma2.dicii.bd.progetto.test;

import java.util.Date;
import java.util.HashMap;
import java.text.ParseException;
import java.util.ArrayList;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import it.uniroma2.dicii.bd.progetto.errorLogic.BatchError;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.errorLogic.NotFoundBorderError;
import it.uniroma2.dicii.bd.progetto.errorLogic.NotFoundSegmentPointError;
import it.uniroma2.dicii.bd.progetto.filament.BorderPoint;
import it.uniroma2.dicii.bd.progetto.filament.Filament;
import it.uniroma2.dicii.bd.progetto.filament.SegmentPoint;
import it.uniroma2.dicii.bd.progetto.filament.SegmentPointImported;
import it.uniroma2.dicii.bd.progetto.repository.FilamentsRepository;
import it.uniroma2.dicii.bd.progetto.repository.FilamentsRepositoryFactory;
import it.uniroma2.dicii.bd.progetto.repository.SatellitesRepository;
import it.uniroma2.dicii.bd.progetto.repository.SatellitesRepositoryFactory;
import it.uniroma2.dicii.bd.progetto.satellite.Agency;
import it.uniroma2.dicii.bd.progetto.satellite.Instrument;
import it.uniroma2.dicii.bd.progetto.satellite.Satellite;
import it.uniroma2.dicii.bd.progetto.standardUser.StandardUserSession;


public class CalculateSBDistanceTest {

	/*
	 * Il metodo inserisce nel database gli input necessari all'esecuzione del caso di test.
	 * Precisamente, crea un filamento con tre punti del contorno e due punti del segmento, ne calcola la distanza minima
	 * ed elimina il filamento ed i punti che lo compongono
	 * */

	private static final String FILAMENT_NAME_TEST = "filamentTest";
	private static final String SATELLITE_NAME_TEST = "satelliteTest";
	private static final String INSTRUMENT_NAME_TEST = "instrumentTest";
	private static final String AGENCY_NAME_TEST = "Agenzia";
			
	private static final double LONGITUDE_BORDER_POINT_1 = 0;
	private static final double LATITUDE_BORDER_POINT_1 = 0;
	
	private static final double LONGITUDE_BORDER_POINT_2 = 10;
	private static final double LATITUDE_BORDER_POINT_2 = 0;

	private static final double LONGITUDE_BORDER_POINT_3 = 5;
	private static final double LATITUDE_BORDER_POINT_3 = 10;
	
	private static final double LONGITUDE_SEGMETN_POINT_1 = 5;
	private static final double LATITUDE_SEGMENT_POINT_1 = 3;

	private static final double LONGITUDE_SEGMETN_POINT_2 = 5;
	private static final double LATITUDE_SEGMENT_POINT_2 = 1;


	@BeforeClass
	public static void setUp() throws ConfigurationError, DataAccessError, BatchError, ParseException {
		SatellitesRepositoryFactory satellitesRepositoryFactory = SatellitesRepositoryFactory.getInstance();
		SatellitesRepository satelliteRepository = satellitesRepositoryFactory.createSatellitesRepository();
		
		Agency agency = new Agency(AGENCY_NAME_TEST);
		ArrayList<Agency> agenciesList = new ArrayList<Agency>();
		agenciesList.add(agency);
		
		Satellite satellite = new Satellite(SATELLITE_NAME_TEST, new Date(), new Date());
		satelliteRepository.persistSatellite(satellite, agenciesList);
		
		Instrument instrument = new Instrument(INSTRUMENT_NAME_TEST, "");
		satelliteRepository.persistInstrument(instrument, SATELLITE_NAME_TEST);
		
		Filament filamentA = new Filament();
		filamentA.setName(FILAMENT_NAME_TEST);
		filamentA.setNumber(1);
		filamentA.setInstrumentName(INSTRUMENT_NAME_TEST);
		
		SegmentPointImported filamentFirstSegmentPoint = new SegmentPointImported();
		filamentFirstSegmentPoint.setFilamentId(1);
		filamentFirstSegmentPoint.setLongitude(LONGITUDE_SEGMETN_POINT_1);
		filamentFirstSegmentPoint.setLatitude(LATITUDE_SEGMENT_POINT_1);
		filamentFirstSegmentPoint.setProgNumber(1);
		filamentFirstSegmentPoint.setSegmentId(1);
		filamentFirstSegmentPoint.setType('S');
		
		SegmentPointImported filamentSecondSegmentPoint = new SegmentPointImported();
		filamentSecondSegmentPoint.setFilamentId(1);
		filamentSecondSegmentPoint.setLongitude(LONGITUDE_SEGMETN_POINT_2);
		filamentSecondSegmentPoint.setLatitude(LATITUDE_SEGMENT_POINT_2);
		filamentSecondSegmentPoint.setProgNumber(2);
		filamentSecondSegmentPoint.setSegmentId(1);
		filamentSecondSegmentPoint.setType('S');
		
		ArrayList<String> pointFilamentsList = new ArrayList<>();
		pointFilamentsList.add(FILAMENT_NAME_TEST);
		
		BorderPoint filamentFirstPoint = new BorderPoint();
		filamentFirstPoint.setFilamentNames(pointFilamentsList);
		filamentFirstPoint.setLongitude(LONGITUDE_BORDER_POINT_1);
		filamentFirstPoint.setLatitude(LATITUDE_BORDER_POINT_1);
		filamentFirstPoint.setSatellite(SATELLITE_NAME_TEST);
		
		BorderPoint filamentSecondPoint = new BorderPoint();
		filamentSecondPoint.setFilamentNames(pointFilamentsList);
		filamentSecondPoint.setLongitude(LONGITUDE_BORDER_POINT_2);
		filamentSecondPoint.setLatitude(LATITUDE_BORDER_POINT_2);
		filamentSecondPoint.setSatellite(SATELLITE_NAME_TEST);
		
		BorderPoint filamentThirdPoint = new BorderPoint();
		filamentThirdPoint.setFilamentNames(pointFilamentsList);
		filamentThirdPoint.setLongitude(LONGITUDE_BORDER_POINT_3);
		filamentThirdPoint.setLatitude(LATITUDE_BORDER_POINT_3);
		filamentThirdPoint.setSatellite(SATELLITE_NAME_TEST);
		
		ArrayList<Filament> filaments = new ArrayList<>();
		filaments.add(filamentA);
		
		FilamentsRepositoryFactory filamentsRepositoryFactory = FilamentsRepositoryFactory.getInstance();
		FilamentsRepository filamentsRepository = filamentsRepositoryFactory.createFilamentsRepository();
		filamentsRepository.insertAllFilaments(filaments);
		
		ArrayList<SegmentPointImported> segmentPoints = new ArrayList<>();
		segmentPoints.add(filamentFirstSegmentPoint);
		segmentPoints.add(filamentSecondSegmentPoint);
		filamentsRepository.insertAllSegmentPoints(segmentPoints, SATELLITE_NAME_TEST);
		
		ArrayList<BorderPoint> borderPoints = new ArrayList<>();
		borderPoints.add(filamentFirstPoint);
		borderPoints.add(filamentSecondPoint);
		borderPoints.add(filamentThirdPoint);
		filamentsRepository.insertAllBorderPoints(borderPoints);
	}

	@Test
	public void testCalculateMinDistanceFromSegmentToBorder() throws NotFoundSegmentPointError, ConfigurationError, DataAccessError, NotFoundBorderError { 

		HashMap<String,Double> distances = new HashMap<>();
		
		// inizializzo i risultati attesi
		double firstResultExpected = 5.831; 
		double secondResultExpected = 5.099; 
		
		// inizializzo gli input
		String satellite = SATELLITE_NAME_TEST;
		String filament = FILAMENT_NAME_TEST;
		int segmentId = 1;
		
		// metodo dello standardUserSession
		distances = StandardUserSession.getInstance().calculateMinDistanceFromSegmentToBorder(filament, segmentId, satellite);
		
		// ottengo i risulati e li verifico
		double firstResult = distances.get("firstMinDinstance");
		double secondResult = distances.get("secondMinDistance");
		
		boolean firstCondition = Math.abs(firstResult - firstResultExpected) < 0.001;
		boolean secondCondition = Math.abs(secondResult - secondResultExpected) < 0.001;
		
		Assert.assertTrue(firstCondition);
		Assert.assertTrue(secondCondition);
	}

	@AfterClass
	public static void cleanUp() throws ConfigurationError, DataAccessError, ParseException {
		Filament filamentTest = new Filament();
		filamentTest.setName(FILAMENT_NAME_TEST);
		filamentTest.setNumber(1);
		filamentTest.setInstrumentName(INSTRUMENT_NAME_TEST);
		
		FilamentsRepositoryFactory filamentsRepositoryFactory = FilamentsRepositoryFactory.getInstance();
		FilamentsRepository filamentsRepository = filamentsRepositoryFactory.createFilamentsRepository();
		
		SegmentPoint filamentFirstSegmentPoint = new SegmentPoint();
		filamentFirstSegmentPoint.setFilament(filamentTest);
		filamentFirstSegmentPoint.setLongitude(LONGITUDE_SEGMETN_POINT_1);
		filamentFirstSegmentPoint.setLatitude(LATITUDE_SEGMENT_POINT_1);
		filamentFirstSegmentPoint.setProgNumber(1);
		filamentFirstSegmentPoint.setSegmentId(1);
		filamentFirstSegmentPoint.setType('S');
		
		SegmentPoint filamentSecondSegmentPoint = new SegmentPoint();
		filamentSecondSegmentPoint.setFilament(filamentTest);
		filamentSecondSegmentPoint.setLongitude(LONGITUDE_SEGMETN_POINT_2);
		filamentSecondSegmentPoint.setLatitude(LATITUDE_SEGMENT_POINT_2);
		filamentSecondSegmentPoint.setProgNumber(2);
		filamentSecondSegmentPoint.setSegmentId(1);
		filamentSecondSegmentPoint.setType('S');
		
		ArrayList<SegmentPoint> segmentPoints = new ArrayList<>();
		segmentPoints.add(filamentFirstSegmentPoint);
		segmentPoints.add(filamentSecondSegmentPoint);
		
		filamentsRepository.deleteSegmentPoints(segmentPoints);
		
		ArrayList<String> pointFilamentsList = new ArrayList<>();
		pointFilamentsList.add(FILAMENT_NAME_TEST);
		
		BorderPoint filamentFirstBorderPoint = new BorderPoint();
		filamentFirstBorderPoint.setFilamentNames(pointFilamentsList);
		filamentFirstBorderPoint.setLongitude(LONGITUDE_BORDER_POINT_1);
		filamentFirstBorderPoint.setLatitude(LATITUDE_BORDER_POINT_1);
		filamentFirstBorderPoint.setSatellite(SATELLITE_NAME_TEST);
		
		BorderPoint filamentSecondBorderPoint = new BorderPoint();
		filamentSecondBorderPoint.setFilamentNames(pointFilamentsList);
		filamentSecondBorderPoint.setLongitude(LONGITUDE_BORDER_POINT_2);
		filamentSecondBorderPoint.setLatitude(LATITUDE_BORDER_POINT_2);
		filamentSecondBorderPoint.setSatellite(SATELLITE_NAME_TEST);
		
		BorderPoint filamentThirdBorderPoint = new BorderPoint();
		filamentThirdBorderPoint.setFilamentNames(pointFilamentsList);
		filamentThirdBorderPoint.setLongitude(LONGITUDE_BORDER_POINT_3);
		filamentThirdBorderPoint.setLatitude(LATITUDE_BORDER_POINT_3);
		filamentThirdBorderPoint.setSatellite(SATELLITE_NAME_TEST);
		
		ArrayList<BorderPoint> borderPoints = new ArrayList<>();
		borderPoints.add(filamentFirstBorderPoint);
		borderPoints.add(filamentSecondBorderPoint);
		borderPoints.add(filamentThirdBorderPoint);
		
		filamentsRepository.deleteBorderPoints(borderPoints);
		filamentsRepository.deleteFilamentWithName(FILAMENT_NAME_TEST);
		
		SatellitesRepositoryFactory satellitesRepositoryFactory = SatellitesRepositoryFactory.getInstance();
		SatellitesRepository satellitesRepository = satellitesRepositoryFactory.createSatellitesRepository();
		
		satellitesRepository.deleteInstrument(INSTRUMENT_NAME_TEST);
		satellitesRepository.deleteSatellite(SATELLITE_NAME_TEST);		
		
	}
}
