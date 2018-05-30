package it.uniroma2.dicii.bd.progetto.test;

import org.junit.Assert;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import it.uniroma2.dicii.bd.progetto.errorLogic.BatchError;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.filament.BorderPoint;
import it.uniroma2.dicii.bd.progetto.filament.Filament;
import it.uniroma2.dicii.bd.progetto.filament.FilamentBean;
import it.uniroma2.dicii.bd.progetto.repository.FilamentsRepository;
import it.uniroma2.dicii.bd.progetto.repository.FilamentsRepositoryFactory;
import it.uniroma2.dicii.bd.progetto.repository.SatellitesRepository;
import it.uniroma2.dicii.bd.progetto.repository.SatellitesRepositoryFactory;
import it.uniroma2.dicii.bd.progetto.satellite.Agency;
import it.uniroma2.dicii.bd.progetto.satellite.Instrument;
import it.uniroma2.dicii.bd.progetto.satellite.Satellite;
import it.uniroma2.dicii.bd.progetto.standardUser.StandardUserSession;

public class SearchFilamentByRegionTest {
	/*
	 * Il metodo inserisce nel database gli input necessari all'esecuzione del caso di test.
	 * Precisamente, 
	 * */	

	private static final String FILAMENT_A_NAME_TEST = "filamentTestIn";
	private static final String FILAMENT_B_NAME_TEST = "filamentTestPartiallyIn";
	private static final String FILAMENT_C_NAME_TEST = "filamentTestOut";

	private static final String SATELLITE_NAME_TEST = "satelliteTest";
	private static final String INSTRUMENT_NAME_TEST = "instrumentTest";
	private static final String AGENCY_NAME_TEST = "Agenzia";
	
	private static final double LONGITUDE_BORDER_POINT_1_FILAMENT_A = 2;
	private static final double LATITUDE_BORDER_POINT_1_FILAMENT_A = 1;

	private static final double LONGITUDE_BORDER_POINT_2_FILAMENT_A = 1;
	private static final double LATITUDE_BORDER_POINT_2_FILAMENT_A = 0;

	private static final double LONGITUDE_BORDER_POINT_3_FILAMENT_A = 2;
	private static final double LATITUDE_BORDER_POINT_3_FILAMENT_A = -2;

	private static final double LONGITUDE_BORDER_POINT_1_FILAMENT_B = -6;
	private static final double LATITUDE_BORDER_POINT_1_FILAMENT_B = 0;
	
	private static final double LONGITUDE_BORDER_POINT_2_FILAMENT_B = -3;
	private static final double LATITUDE_BORDER_POINT_2_FILAMENT_B = 4;
	
	private static final double LONGITUDE_BORDER_POINT_3_FILAMENT_B = -4;
	private static final double LATITUDE_BORDER_POINT_3_FILAMENT_B = 1;
	
	private static final double LONGITUDE_BORDER_POINT_1_FILAMENT_C = -6;
	private static final double LATITUDE_BORDER_POINT_1_FILAMENT_C = -6;

	private static final double LONGITUDE_BORDER_POINT_2_FILAMENT_C = -36;
	private static final double LATITUDE_BORDER_POINT_2_FILAMENT_C = -36;

	private static final double LONGITUDE_BORDER_POINT_3_FILAMENT_C = -30;
	private static final double LATITUDE_BORDER_POINT_3_FILAMENT_C = -36;
	
	private static final double LENGHT = 5.0;

	@BeforeClass
	public static void setUp() throws ConfigurationError, DataAccessError, BatchError {
		// inizializzo un satellite e uno strumento
		SatellitesRepositoryFactory satellitesRepositoryFactory = SatellitesRepositoryFactory.getInstance();
		SatellitesRepository satelliteRepository = satellitesRepositoryFactory.createSatellitesRepository();

		Agency agency = new Agency(AGENCY_NAME_TEST);
		ArrayList<Agency> agenciesList = new ArrayList<Agency>();
		agenciesList.add(agency);

		Satellite satellite = new Satellite(SATELLITE_NAME_TEST, new Date(), new Date());
		satelliteRepository.persistSatellite(satellite, agenciesList);

		Instrument instrument = new Instrument(INSTRUMENT_NAME_TEST, "");
		satelliteRepository.persistInstrument(instrument, SATELLITE_NAME_TEST);

		// inizializzo tre filamenti con tre punti del contorno
		// inizializzo il primo filamento
		Filament filamentATest = new Filament();
		filamentATest.setName(FILAMENT_A_NAME_TEST);
		filamentATest.setNumber(1);
		filamentATest.setInstrumentName(INSTRUMENT_NAME_TEST);
		
		ArrayList<String> pointFilamentsAList = new ArrayList<>();
		pointFilamentsAList.add(FILAMENT_A_NAME_TEST);
		
		// inizializzo tre punti del contorno
		// inizializzo il primo punto del contorno
		BorderPoint filamentAFirstPoint = new BorderPoint();
		filamentAFirstPoint.setFilamentNames(pointFilamentsAList);
		filamentAFirstPoint.setLongitude(LONGITUDE_BORDER_POINT_1_FILAMENT_A);
		filamentAFirstPoint.setLatitude(LATITUDE_BORDER_POINT_1_FILAMENT_A);
		filamentAFirstPoint.setSatellite(SATELLITE_NAME_TEST);
			
		// inizializzo il secondo punto del contorno
		BorderPoint filamentASecondPoint = new BorderPoint();
		filamentASecondPoint.setFilamentNames(pointFilamentsAList);
		filamentASecondPoint.setLongitude(LONGITUDE_BORDER_POINT_2_FILAMENT_A);
		filamentASecondPoint.setLatitude(LATITUDE_BORDER_POINT_2_FILAMENT_A);
		filamentASecondPoint.setSatellite(SATELLITE_NAME_TEST);
				
		// inizializzo il terzo punto del contorno
		BorderPoint filamentAThirdPoint = new BorderPoint();
		filamentAThirdPoint.setFilamentNames(pointFilamentsAList);
		filamentAThirdPoint.setLongitude(LONGITUDE_BORDER_POINT_3_FILAMENT_A);
		filamentAThirdPoint.setLatitude(LATITUDE_BORDER_POINT_3_FILAMENT_A);
		filamentAThirdPoint.setSatellite(SATELLITE_NAME_TEST);
		
		// inizializzo il secondo filamento
		Filament filamentBTest = new Filament();
		filamentBTest.setName(FILAMENT_B_NAME_TEST);
		filamentBTest.setNumber(2);
		filamentBTest.setInstrumentName(INSTRUMENT_NAME_TEST);
		
		ArrayList<String> pointFilamentsBList = new ArrayList<>();
		pointFilamentsBList.add(FILAMENT_B_NAME_TEST);

		// inizializzo tre punti del contorno
		// inizializzo il primo punto del contorno
		BorderPoint filamentBFirstPoint = new BorderPoint();
		filamentBFirstPoint.setFilamentNames(pointFilamentsBList);
		filamentBFirstPoint.setLongitude(LONGITUDE_BORDER_POINT_1_FILAMENT_B);
		filamentBFirstPoint.setLatitude(LATITUDE_BORDER_POINT_1_FILAMENT_B);
		filamentBFirstPoint.setSatellite(SATELLITE_NAME_TEST);
				
		// inizializzo il secondo punto del contorno
		BorderPoint filamentBSecondPoint = new BorderPoint();
		filamentBSecondPoint.setFilamentNames(pointFilamentsBList);
		filamentBSecondPoint.setLongitude(LONGITUDE_BORDER_POINT_2_FILAMENT_B);
		filamentBSecondPoint.setLatitude(LATITUDE_BORDER_POINT_2_FILAMENT_B);
		filamentBSecondPoint.setSatellite(SATELLITE_NAME_TEST);
				
		// inizializzo il terzo punto del contorno
		BorderPoint filamentBThirdPoint = new BorderPoint();
		filamentBThirdPoint.setFilamentNames(pointFilamentsBList);
		filamentBThirdPoint.setLongitude(LONGITUDE_BORDER_POINT_3_FILAMENT_B);
		filamentBThirdPoint.setLatitude(LATITUDE_BORDER_POINT_3_FILAMENT_B);
		filamentBThirdPoint.setSatellite(SATELLITE_NAME_TEST);
		
		// inizializzo il terzo filamento
		Filament filamentCTest = new Filament();
		filamentCTest.setName(FILAMENT_C_NAME_TEST);
		filamentCTest.setNumber(3);
		filamentCTest.setInstrumentName(INSTRUMENT_NAME_TEST);
		
		ArrayList<String> pointFilamentsCList = new ArrayList<>();
		pointFilamentsCList.add(FILAMENT_C_NAME_TEST);

		// inizializzo tre punti del contorno
		// inizializzo il primo punto del contorno
		BorderPoint filamentCFirstPoint = new BorderPoint();
		filamentCFirstPoint.setFilamentNames(pointFilamentsCList);
		filamentCFirstPoint.setLongitude(LONGITUDE_BORDER_POINT_1_FILAMENT_C);
		filamentCFirstPoint.setLatitude(LATITUDE_BORDER_POINT_1_FILAMENT_C);
		filamentCFirstPoint.setSatellite(SATELLITE_NAME_TEST);
						
		// inizializzo il secondo punto del contorno
		BorderPoint filamentCSecondPoint = new BorderPoint();
		filamentCSecondPoint.setFilamentNames(pointFilamentsCList);
		filamentCSecondPoint.setLongitude(LONGITUDE_BORDER_POINT_2_FILAMENT_C);
		filamentCSecondPoint.setLatitude(LATITUDE_BORDER_POINT_2_FILAMENT_C);
		filamentCSecondPoint.setSatellite(SATELLITE_NAME_TEST);
		
		// inizializzo il terzo punto del contorno
		BorderPoint filamentCThirdPoint = new BorderPoint();
		filamentCThirdPoint.setFilamentNames(pointFilamentsCList);
		filamentCThirdPoint.setLongitude(LONGITUDE_BORDER_POINT_3_FILAMENT_C);
		filamentCThirdPoint.setLatitude(LATITUDE_BORDER_POINT_3_FILAMENT_C);
		filamentCThirdPoint.setSatellite(SATELLITE_NAME_TEST);
		
		ArrayList<Filament> filaments = new ArrayList<>();
		filaments.add(filamentATest);
		filaments.add(filamentBTest);
		filaments.add(filamentCTest);
		
		FilamentsRepositoryFactory filamentsRepositoryFactory = FilamentsRepositoryFactory.getInstance();
		FilamentsRepository filamentsRepository = filamentsRepositoryFactory.createFilamentsRepository();
		filamentsRepository.insertAllFilaments(filaments);
		
		ArrayList<BorderPoint> borderPoints = new ArrayList<>();
		borderPoints.add(filamentAFirstPoint);
		borderPoints.add(filamentASecondPoint);
		borderPoints.add(filamentAThirdPoint);
		borderPoints.add(filamentBFirstPoint);
		borderPoints.add(filamentBSecondPoint);
		borderPoints.add(filamentBThirdPoint);
		borderPoints.add(filamentCFirstPoint);
		borderPoints.add(filamentCSecondPoint);
		borderPoints.add(filamentCThirdPoint);
		
		// inserisco i punti del contorno nel database
		filamentsRepository.insertAllBorderPoints(borderPoints);
		
	}

	@Test
	public void testFindFilamentBySquare() throws ConfigurationError, DataAccessError {
		
		ArrayList<FilamentBean> filaments = new ArrayList<>();
		
		double centreX = 0;
		double centreY = 0;
		double side = LENGHT*2.0;
		// in questo modola distanza dal lato al filamento è uguale al raggio e posso riusare i filamenti con il cerchio
		String chosenShape = "QUADRATO";
		
		// String expectedFilamentName = "filamentTestIn";
		String expectedFilamentName = FILAMENT_A_NAME_TEST;
		
		filaments = StandardUserSession.getInstance().findFilamentsInARegion(centreX, centreY, side, chosenShape);
		
		Assert.assertTrue(filaments.get(0).getName().equals(expectedFilamentName));
		Assert.assertTrue(filaments.size()==1);
		assertTrue(true);
	}
	
	@Test
	public void testFindFilamentByCircle() throws ConfigurationError, DataAccessError {
		
		ArrayList<FilamentBean> filaments = new ArrayList<>();
		
		double centreX = 0;
		double centreY = 0;
		double radius = LENGHT;
		String chosenShape = "CERCHIO";

		// String expectedFilamentName = "filamentTestIn";
		String expectedFilamentName = FILAMENT_A_NAME_TEST;
		
		filaments = StandardUserSession.getInstance().findFilamentsInARegion(centreX, centreY, radius, chosenShape);
		
		Assert.assertTrue(filaments.get(0).getName().equals(expectedFilamentName));
		Assert.assertTrue(filaments.size()==1);
	}
	
	@AfterClass
	public static void cleanUp() throws ConfigurationError, DataAccessError {
		// inizializzo tre filamenti con tre punti del contorno
		// inizializzo il primo filamento
		Filament filamentATest = new Filament();
		filamentATest.setName(FILAMENT_A_NAME_TEST);
		filamentATest.setNumber(1);
		filamentATest.setInstrumentName(INSTRUMENT_NAME_TEST);
		
		ArrayList<String> pointFilamentsAList = new ArrayList<>();
		pointFilamentsAList.add(FILAMENT_A_NAME_TEST);
				
		// inizializzo tre punti del contorno
		// inizializzo il primo punto del contorno
		BorderPoint filamentAFirstPoint = new BorderPoint();
		filamentAFirstPoint.setFilamentNames(pointFilamentsAList);
		filamentAFirstPoint.setLongitude(LONGITUDE_BORDER_POINT_1_FILAMENT_A);
		filamentAFirstPoint.setLatitude(LATITUDE_BORDER_POINT_1_FILAMENT_A);
		filamentAFirstPoint.setSatellite(SATELLITE_NAME_TEST);
					
		// inizializzo il secondo punto del contorno
		BorderPoint filamentASecondPoint = new BorderPoint();
		filamentASecondPoint.setFilamentNames(pointFilamentsAList);
		filamentASecondPoint.setLongitude(LONGITUDE_BORDER_POINT_2_FILAMENT_A);
		filamentASecondPoint.setLatitude(LATITUDE_BORDER_POINT_2_FILAMENT_A);
		filamentASecondPoint.setSatellite(SATELLITE_NAME_TEST);
				
		// inizializzo il terzo punto del contorno
		BorderPoint filamentAThirdPoint = new BorderPoint();
		filamentAThirdPoint.setFilamentNames(pointFilamentsAList);
		filamentAThirdPoint.setLongitude(LONGITUDE_BORDER_POINT_3_FILAMENT_A);
		filamentAThirdPoint.setLatitude(LATITUDE_BORDER_POINT_3_FILAMENT_A);
		filamentAThirdPoint.setSatellite(SATELLITE_NAME_TEST);
		
		// inizializzo il secondo filamento
		Filament filamentBTest = new Filament();
		filamentBTest.setName(FILAMENT_B_NAME_TEST);
		filamentBTest.setNumber(2);
		filamentBTest.setInstrumentName(INSTRUMENT_NAME_TEST);
			
		ArrayList<String> pointFilamentsBList = new ArrayList<>();
		pointFilamentsBList.add(FILAMENT_B_NAME_TEST);
		
		// inizializzo tre punti del contorno
		// inizializzo il primo punto del contorno
		BorderPoint filamentBFirstPoint = new BorderPoint();
		filamentBFirstPoint.setFilamentNames(pointFilamentsBList);
		filamentBFirstPoint.setLongitude(LONGITUDE_BORDER_POINT_1_FILAMENT_B);
		filamentBFirstPoint.setLatitude(LATITUDE_BORDER_POINT_1_FILAMENT_B);
		filamentBFirstPoint.setSatellite(SATELLITE_NAME_TEST);
						
		// inizializzo il secondo punto del contorno
		BorderPoint filamentBSecondPoint = new BorderPoint();
		filamentBSecondPoint.setFilamentNames(pointFilamentsBList);
		filamentBSecondPoint.setLongitude(LONGITUDE_BORDER_POINT_2_FILAMENT_B);
		filamentBSecondPoint.setLatitude(LATITUDE_BORDER_POINT_2_FILAMENT_B);
		filamentBSecondPoint.setSatellite(SATELLITE_NAME_TEST);
				
		// inizializzo il terzo punto del contorno
		BorderPoint filamentBThirdPoint = new BorderPoint();
		filamentBThirdPoint.setFilamentNames(pointFilamentsBList);
		filamentBThirdPoint.setLongitude(LONGITUDE_BORDER_POINT_3_FILAMENT_B);
		filamentBThirdPoint.setLatitude(LATITUDE_BORDER_POINT_3_FILAMENT_B);
		filamentBThirdPoint.setSatellite(SATELLITE_NAME_TEST);
			
		// inizializzo il terzo filamento
		Filament filamentCTest = new Filament();
		filamentCTest.setName(FILAMENT_C_NAME_TEST);
		filamentCTest.setNumber(3);
		filamentCTest.setInstrumentName(INSTRUMENT_NAME_TEST);
				
		ArrayList<String> pointFilamentsCList = new ArrayList<>();
		pointFilamentsCList.add(FILAMENT_C_NAME_TEST);

		// inizializzo tre punti del contorno
		// inizializzo il primo punto del contorno
		BorderPoint filamentCFirstPoint = new BorderPoint();
		filamentCFirstPoint.setFilamentNames(pointFilamentsCList);
		filamentCFirstPoint.setLongitude(LONGITUDE_BORDER_POINT_1_FILAMENT_C);
		filamentCFirstPoint.setLatitude(LATITUDE_BORDER_POINT_1_FILAMENT_C);
		filamentCFirstPoint.setSatellite(SATELLITE_NAME_TEST);
						
		// inizializzo il secondo punto del contorno
		BorderPoint filamentCSecondPoint = new BorderPoint();
		filamentCSecondPoint.setFilamentNames(pointFilamentsCList);
		filamentCSecondPoint.setLongitude(LONGITUDE_BORDER_POINT_2_FILAMENT_C);
		filamentCSecondPoint.setLatitude(LATITUDE_BORDER_POINT_2_FILAMENT_C);
		filamentCSecondPoint.setSatellite(SATELLITE_NAME_TEST);
		
		// inizializzo il terzo punto del contorno
		BorderPoint filamentCThirdPoint = new BorderPoint();
		filamentCThirdPoint.setFilamentNames(pointFilamentsCList);
		filamentCThirdPoint.setLongitude(LONGITUDE_BORDER_POINT_3_FILAMENT_C);
		filamentCThirdPoint.setLatitude(LATITUDE_BORDER_POINT_3_FILAMENT_C);
		filamentCThirdPoint.setSatellite(SATELLITE_NAME_TEST);
		
		ArrayList<BorderPoint> borderPoints = new ArrayList<>();
		borderPoints.add(filamentAFirstPoint);
		borderPoints.add(filamentASecondPoint);
		borderPoints.add(filamentAThirdPoint);
		borderPoints.add(filamentBFirstPoint);
		borderPoints.add(filamentBSecondPoint);
		borderPoints.add(filamentBThirdPoint);
		borderPoints.add(filamentCFirstPoint);
		borderPoints.add(filamentCSecondPoint);
		borderPoints.add(filamentCThirdPoint);
		
		FilamentsRepositoryFactory filamentsRepositoryFactory = FilamentsRepositoryFactory.getInstance();
		FilamentsRepository filamentsRepository = filamentsRepositoryFactory.createFilamentsRepository();

		// cancello i punti del contorno e cancello un filamento
		filamentsRepository.deleteBorderPoints(borderPoints);
		filamentsRepository.deleteFilamentWithName(FILAMENT_A_NAME_TEST);
		filamentsRepository.deleteFilamentWithName(FILAMENT_B_NAME_TEST);
		filamentsRepository.deleteFilamentWithName(FILAMENT_C_NAME_TEST);
		
		// cancello un satellite e uno strumento
		SatellitesRepositoryFactory satellitesRepositoryFactory = SatellitesRepositoryFactory.getInstance();
		SatellitesRepository satellitesRepository = satellitesRepositoryFactory.createSatellitesRepository();
		
		satellitesRepository.deleteInstrument(INSTRUMENT_NAME_TEST);
		satellitesRepository.deleteSatellite(SATELLITE_NAME_TEST);	
		
	}

}
