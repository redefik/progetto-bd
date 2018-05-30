package it.uniroma2.dicii.bd.progetto.test;

import java.util.ArrayList;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import it.uniroma2.dicii.bd.progetto.errorLogic.BatchError;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.errorLogic.FilamentWithoutStarsError;
import it.uniroma2.dicii.bd.progetto.errorLogic.NotFoundBackBoneError;
import it.uniroma2.dicii.bd.progetto.errorLogic.NotFoundFilamentError;
import it.uniroma2.dicii.bd.progetto.filament.BorderPoint;
import it.uniroma2.dicii.bd.progetto.filament.Filament;
import it.uniroma2.dicii.bd.progetto.filament.SegmentPoint;
import it.uniroma2.dicii.bd.progetto.filament.SegmentPointImported;
import it.uniroma2.dicii.bd.progetto.repository.FilamentsRepository;
import it.uniroma2.dicii.bd.progetto.repository.FilamentsRepositoryFactory;
import it.uniroma2.dicii.bd.progetto.repository.SatellitesRepository;
import it.uniroma2.dicii.bd.progetto.repository.SatellitesRepositoryFactory;
import it.uniroma2.dicii.bd.progetto.repository.StarsRepository;
import it.uniroma2.dicii.bd.progetto.repository.StarsRepositoryFactory;
import it.uniroma2.dicii.bd.progetto.satellite.Agency;
import it.uniroma2.dicii.bd.progetto.satellite.Instrument;
import it.uniroma2.dicii.bd.progetto.satellite.Satellite;
import it.uniroma2.dicii.bd.progetto.standardUser.StandardUserSession;
import it.uniroma2.dicii.bd.progetto.star.Star;
import it.uniroma2.dicii.bd.progetto.star.StarBeanWithMinDistance;

public class CalculateSFDistanceTest {
	/*
	 * Il metodo inserisce nel database gli input necessari all'esecuzione del caso di test.
	 * Precisamente, crea una stella dentro un filamento e ne verifica la distanza minima
	 * */

	private static final String FILAMENT_NAME_TEST = "filamentTest";
	private static final String SATELLITE_NAME_TEST = "satelliteTest";
	private static final String INSTRUMENT_NAME_TEST = "instrumentTest";
	private static final String AGENCY_NAME_TEST = "Agenzia";
	private static final String STAR_NAME_TEST = "starTest";
	private static final String STAR_TYPE_TEST = "starTypeTest";
	
	private static final double STAR_FLOW = 2;
	private static final double STAR_LATITUDE = 0;
	private static final double STAR_LONGITUDE = 0;
	
	private static final double LATITUDE_BORDER_POINT_1 = -10;
	private static final double LATITUDE_BORDER_POINT_2 = -10;
	private static final double LATITUDE_BORDER_POINT_3 = 10;
	
	private static final double LONGITUDE_BORDER_POINT_1 = -10;
	private static final double LONGITUDE_BORDER_POINT_2 = 10;
	private static final double LONGITUDE_BORDER_POINT_3 = 0;
	
	private static final double LATITUDE_SEGMENT_POINT_1 = 3;
	private static final double LATITUDE_SEGMENT_POINT_2 = 4;
	
	private static final double LONGITUDE_SEGMETN_POINT_1 = 4;
	private static final double LONGITUDE_SEGMETN_POINT_2 = 4;
	
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

		// inizializzo un filamento
		Filament filamentTest = new Filament();
		filamentTest.setName(FILAMENT_NAME_TEST);
		filamentTest.setNumber(1);
		filamentTest.setInstrumentName(INSTRUMENT_NAME_TEST);

		// inizializzo due punti del segmento
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

		// inizializzo tre punti del contorno
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
		filaments.add(filamentTest);

		FilamentsRepositoryFactory filamentsRepositoryFactory = FilamentsRepositoryFactory.getInstance();
		FilamentsRepository filamentsRepository = filamentsRepositoryFactory.createFilamentsRepository();
		filamentsRepository.insertAllFilaments(filaments);

		ArrayList<SegmentPointImported> segmentPoints = new ArrayList<>();
		segmentPoints.add(filamentFirstSegmentPoint);
		segmentPoints.add(filamentSecondSegmentPoint);
		
		// inserisco i punti del segmento nel database
		filamentsRepository.insertAllSegmentPoints(segmentPoints, SATELLITE_NAME_TEST);
		
		ArrayList<BorderPoint> borderPoints = new ArrayList<>();
		borderPoints.add(filamentFirstPoint);
		borderPoints.add(filamentSecondPoint);
		borderPoints.add(filamentThirdPoint);
		
		// inserisco i punti del contorno nel database
		filamentsRepository.insertAllBorderPoints(borderPoints);
		
		// inizializzo una stella
		Star star = new Star();
		star.setName(STAR_NAME_TEST);
		star.setFlow(STAR_FLOW);
		star.setLatitude(STAR_LATITUDE);
		star.setLongitude(STAR_LONGITUDE);
		star.setClassification(STAR_TYPE_TEST);
		star.setSatellite(SATELLITE_NAME_TEST);
		
		ArrayList<Star> stars = new ArrayList<>();
		stars.add(star);
		
		StarsRepositoryFactory starsRepositoryFactory = StarsRepositoryFactory.getInstance();
		StarsRepository starsRepository = starsRepositoryFactory.createStarsRepository();
		
		// inserisco la stella nel database
		starsRepository.insertAllStars(stars);
	}

	@Test
	public void testCalculateSFDistance() throws ConfigurationError, DataAccessError, NotFoundFilamentError, NotFoundBackBoneError, FilamentWithoutStarsError {
		
		ArrayList<StarBeanWithMinDistance> starsWithDistance = new ArrayList<>();
		
		// inizializzo i valori attesi
		double flowExpected = STAR_FLOW;
		double distanceExpected = 5;
		
		// inizializzo il nome del filamento da cercare
		String filamentName = FILAMENT_NAME_TEST;
		
		// cerco la distanza tra la stella e lo scheletro
		starsWithDistance = StandardUserSession.getInstance().calculateSFDistance(filamentName);
		
		// verifico con i risultati attesi
		boolean firstCondition = Math.abs(flowExpected - starsWithDistance.get(0).getFlow()) < 0.001;
		boolean secondCondition = Math.abs(distanceExpected - starsWithDistance.get(0).getMinDistanceFromBackBone()) < 0.001;
		
		Assert.assertTrue(firstCondition);
		Assert.assertTrue(secondCondition);
	}
	
	@AfterClass
	public static void cleanUp() throws ConfigurationError, DataAccessError {
		// inizializzo un filamento
		Filament filamentTest = new Filament();
		filamentTest.setName(FILAMENT_NAME_TEST);
		filamentTest.setNumber(1);
		filamentTest.setInstrumentName(INSTRUMENT_NAME_TEST);
		
		FilamentsRepositoryFactory filamentsRepositoryFactory = FilamentsRepositoryFactory.getInstance();
		FilamentsRepository filamentsRepository = filamentsRepositoryFactory.createFilamentsRepository();
		
		// inizializzo due punti del segmento
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
		
		// canello i punti del segmento
		filamentsRepository.deleteSegmentPoints(segmentPoints);
		
		ArrayList<String> pointFilamentsList = new ArrayList<>();
		pointFilamentsList.add(FILAMENT_NAME_TEST);
		
		// inizializzo tre punti del contorno
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
		
		// cancello i punti del contorno e cancello un filamento
		filamentsRepository.deleteBorderPoints(borderPoints);
		filamentsRepository.deleteFilamentWithName(FILAMENT_NAME_TEST);
		
		// cancello una stella
		StarsRepositoryFactory starsRepositoryFactory = StarsRepositoryFactory.getInstance();
		StarsRepository starsRepository = starsRepositoryFactory.createStarsRepository();
				
		starsRepository.deleteStar(STAR_NAME_TEST);
		
		// cancello un satellite e uno strumento
		SatellitesRepositoryFactory satellitesRepositoryFactory = SatellitesRepositoryFactory.getInstance();
		SatellitesRepository satellitesRepository = satellitesRepositoryFactory.createSatellitesRepository();
		
		satellitesRepository.deleteInstrument(INSTRUMENT_NAME_TEST);
		satellitesRepository.deleteSatellite(SATELLITE_NAME_TEST);		
		
	}

}
