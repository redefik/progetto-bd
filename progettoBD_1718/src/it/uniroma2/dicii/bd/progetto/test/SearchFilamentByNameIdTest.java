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
import it.uniroma2.dicii.bd.progetto.filament.BorderPoint;
import it.uniroma2.dicii.bd.progetto.filament.Filament;
import it.uniroma2.dicii.bd.progetto.filament.FilamentBean;
import it.uniroma2.dicii.bd.progetto.filament.FilamentInfo;
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

public class SearchFilamentByNameIdTest {
	
	/*
	 * Il metodo crea nel db gli input per lo svolgimento del caso di test. In particolare, esso inserisce:
	 * Il satellite "satelliteTest" dell'agenzia "agencyTest"
	 * Lo strumento "instrumentTest" di "satelliteTest"
	 * Un filamento "filamentTest" misurato con "instrumentTest"
	 * 3 punti del contorno per "filamentTest": (0,0);(0,9);(9,0)
	 * 4 punti del segmento per "filamentTest": (3,3) sull'asse; (3,3) su ramo; (5,5) sull'asse e (2,5) su ramo (per un tot di 2 segmenti)
	 * Pertanto i valori attesi delle informazioni derivate sono:
	 * numero di segmenti = 2
	 * centroide = (3,3)
	 * estensione del contorno = 9 * sqrt(2)
	 * */
	@BeforeClass
	public static void setUp() throws ConfigurationError, DataAccessError, BatchError {
		SatellitesRepositoryFactory satellitesRepositoryFactory = SatellitesRepositoryFactory.getInstance();
		SatellitesRepository satelliteRepository = satellitesRepositoryFactory.createSatellitesRepository();
		
		Agency agency = new Agency("Agenzia");
		ArrayList<Agency> agenciesList = new ArrayList<Agency>();
		agenciesList.add(agency);
		
		Satellite satellite = new Satellite("satelliteTest", new Date(), new Date());
		satelliteRepository.persistSatellite(satellite, agenciesList);
		
		Instrument instrument = new Instrument("instrumentTest", "");
		satelliteRepository.persistInstrument(instrument, satellite.getName());
		
		FilamentsRepositoryFactory filamentsRepositoryFactory = FilamentsRepositoryFactory.getInstance();
		FilamentsRepository filamentsRepository = filamentsRepositoryFactory.createFilamentsRepository();
		Filament filament = new Filament();
		filament.setName("filamentTest");
		filament.setInstrumentName("instrumentTest");
		filament.setNumber(0);
		ArrayList<Filament> filamentList = new ArrayList<>();
		filamentList.add(filament);
		filamentsRepository.insertAllFilaments(filamentList);
		
		BorderPoint firstPoint = new BorderPoint();
		ArrayList<String> pointFilamentsList = new ArrayList<>();
		pointFilamentsList.add("filamentTest");
		firstPoint.setFilamentNames(pointFilamentsList);
		firstPoint.setLongitude(0);
		firstPoint.setLatitude(0);
		firstPoint.setSatellite("satelliteTest");
		
		BorderPoint secondPoint = new BorderPoint();
		secondPoint.setFilamentNames(pointFilamentsList);
		secondPoint.setLongitude(9);
		secondPoint.setLatitude(0);
		secondPoint.setSatellite("satelliteTest");
		
		BorderPoint thirdPoint = new BorderPoint();
		thirdPoint.setFilamentNames(pointFilamentsList);
		thirdPoint.setLongitude(0);
		thirdPoint.setLatitude(9);
		thirdPoint.setSatellite("satelliteTest");
		
		ArrayList<BorderPoint> borderPoints = new ArrayList<>();
		borderPoints.add(firstPoint);
		borderPoints.add(secondPoint);
		borderPoints.add(thirdPoint);
		
		filamentsRepository.insertAllBorderPoints(borderPoints);
		
		SegmentPointImported firstSegmentPoint = new SegmentPointImported();
		firstSegmentPoint.setFilamentId(0);
		firstSegmentPoint.setLongitude(3);
		firstSegmentPoint.setLatitude(3);
		firstSegmentPoint.setProgNumber(1);
		firstSegmentPoint.setSegmentId(1);
		firstSegmentPoint.setType('S');
		
		SegmentPointImported secondSegmentPoint = new SegmentPointImported();
		secondSegmentPoint.setFilamentId(0);
		secondSegmentPoint.setLongitude(3);
		secondSegmentPoint.setLatitude(3);
		secondSegmentPoint.setProgNumber(1);
		secondSegmentPoint.setSegmentId(2);
		secondSegmentPoint.setType('B');
		
		SegmentPointImported thirdSegmentPoint = new SegmentPointImported();
		thirdSegmentPoint.setFilamentId(0);
		thirdSegmentPoint.setLongitude(5);
		thirdSegmentPoint.setLatitude(5);
		thirdSegmentPoint.setProgNumber(2);
		thirdSegmentPoint.setSegmentId(1);
		thirdSegmentPoint.setType('S');
		
		SegmentPointImported fourthSegmentPoint = new SegmentPointImported();
		fourthSegmentPoint.setFilamentId(0);
		fourthSegmentPoint.setLongitude(2);
		fourthSegmentPoint.setLatitude(5);
		fourthSegmentPoint.setProgNumber(2);
		fourthSegmentPoint.setSegmentId(2);
		fourthSegmentPoint.setType('B');
		
		ArrayList<SegmentPointImported> segmentPoints = new ArrayList<>();
		
		segmentPoints.add(firstSegmentPoint);
		segmentPoints.add(secondSegmentPoint);
		segmentPoints.add(thirdSegmentPoint);
		segmentPoints.add(fourthSegmentPoint);
		
		filamentsRepository.insertAllSegmentPoints(segmentPoints, "satelliteTest");
		
	}
	
	/*
	 * Il metodo verifica la correttezza della ricerca di un filamento per nome
	 * */
	@Test
	public void testSearchFilamentByName() throws ConfigurationError, DataAccessError {
		StandardUserSession session = StandardUserSession.getInstance();
		FilamentBean filamentFound = session.findFilamentByName("filamentTest");
		Assert.assertTrue(filamentFound.getName().equals("filamentTest"));
	}
	
	/*
	 * Il metodo verifica la correttezza della ricerca di un filamento per id e strumento
	 * */
	@Test
	public void testSearchFilamentByIdAndInstrument() throws ConfigurationError, DataAccessError {
		StandardUserSession session = StandardUserSession.getInstance();
		FilamentBean filamentFound = session.findFilamentByIdAndInstrument(0, "instrumentTest");
		Assert.assertTrue(filamentFound.getName().equals("filamentTest"));
	}
	
	/*
	 * Il metodo verifica che il calcolo delle informazioni derivate del filamento avvenga in modo corretto
	 * */
	@Test
	public void testGetFilamentDerivatedInfo() throws ConfigurationError, DataAccessError {
		StandardUserSession session = StandardUserSession.getInstance();
		FilamentBean filamentBean = session.findFilamentByName("filamentTest");
		FilamentInfo filamentInfo = session.getFilamentInfo(filamentBean);
		int numOfSegments = filamentInfo.getNumberOfSegments();
		double centroidLatitude = filamentInfo.getCentroidLatitude();
		double centroidLongitude = filamentInfo.getCentroidLongitude();
		double borderLength = filamentInfo.getBorderLength();
		boolean firstCondition = (centroidLatitude == 3 && centroidLongitude == 3);
		//La condizione sull'estensione del contorno tiene conto della precisione dei numeri in virgola mobile
		boolean secondCondition = Math.abs(borderLength - 9 * Math.sqrt(2)) < 0.00001;
		boolean thirdCondition = (numOfSegments == 2);
		boolean conditionToVerify = firstCondition && secondCondition && thirdCondition;
		Assert.assertTrue(conditionToVerify);
	}
	
	/*
	 * Il metodo rimuove dal database i dati usati nell'esecuzione della classe di test
	 * */
	@AfterClass
	public static void cleanUp() throws ConfigurationError, DataAccessError {
		FilamentsRepositoryFactory filamentsRepositoryFactory = FilamentsRepositoryFactory.getInstance();
		FilamentsRepository filamentsRepository = filamentsRepositoryFactory.createFilamentsRepository();
		
		Filament filament = new Filament();
		filament.setName("filamentTest");
		
		SegmentPoint firstSegmentPoint = new SegmentPoint();
		firstSegmentPoint.setFilament(filament);
		firstSegmentPoint.setLongitude(3);
		firstSegmentPoint.setLatitude(3);
		firstSegmentPoint.setProgNumber(1);
		firstSegmentPoint.setSegmentId(1);
		firstSegmentPoint.setType('S');
		
		SegmentPoint secondSegmentPoint = new SegmentPoint();
		secondSegmentPoint.setFilament(filament);
		secondSegmentPoint.setLongitude(3);
		secondSegmentPoint.setLatitude(3);
		secondSegmentPoint.setProgNumber(1);
		secondSegmentPoint.setSegmentId(2);
		secondSegmentPoint.setType('B');
		
		SegmentPoint thirdSegmentPoint = new SegmentPoint();
		thirdSegmentPoint.setFilament(filament);
		thirdSegmentPoint.setLongitude(5);
		thirdSegmentPoint.setLatitude(5);
		thirdSegmentPoint.setProgNumber(2);
		thirdSegmentPoint.setSegmentId(1);
		thirdSegmentPoint.setType('S');
		
		SegmentPoint fourthSegmentPoint = new SegmentPoint();
		fourthSegmentPoint.setFilament(filament);
		fourthSegmentPoint.setLongitude(2);
		fourthSegmentPoint.setLatitude(5);
		fourthSegmentPoint.setProgNumber(2);
		fourthSegmentPoint.setSegmentId(2);
		fourthSegmentPoint.setType('B');
		
		ArrayList<SegmentPoint> segmentPoints = new ArrayList<>();
		segmentPoints.add(firstSegmentPoint);
		segmentPoints.add(secondSegmentPoint);
		segmentPoints.add(thirdSegmentPoint);
		segmentPoints.add(fourthSegmentPoint);
		
		filamentsRepository.deleteSegmentPoints(segmentPoints);
		
		BorderPoint firstPoint = new BorderPoint();
		ArrayList<String> pointFilamentsList = new ArrayList<>();
		pointFilamentsList.add("filamentTest");
		firstPoint.setFilamentNames(pointFilamentsList);
		firstPoint.setLongitude(0);
		firstPoint.setLatitude(0);
		firstPoint.setSatellite("satelliteTest");
		
		BorderPoint secondPoint = new BorderPoint();
		secondPoint.setFilamentNames(pointFilamentsList);
		secondPoint.setLongitude(9);
		secondPoint.setLatitude(0);
		secondPoint.setSatellite("satelliteTest");
		
		BorderPoint thirdPoint = new BorderPoint();
		thirdPoint.setFilamentNames(pointFilamentsList);
		thirdPoint.setLongitude(0);
		thirdPoint.setLatitude(9);
		thirdPoint.setSatellite("satelliteTest");
		
		ArrayList<BorderPoint> borderPoints = new ArrayList<>();
		borderPoints.add(firstPoint);
		borderPoints.add(secondPoint);
		borderPoints.add(thirdPoint);
		
		filamentsRepository.deleteBorderPoints(borderPoints);
		
		filamentsRepository.deleteFilamentWithName("filamentTest");
		
		SatellitesRepositoryFactory satellitesRepositoryFactory = SatellitesRepositoryFactory.getInstance();
		SatellitesRepository satellitesRepository = satellitesRepositoryFactory.createSatellitesRepository();
		
		satellitesRepository.deleteInstrument("instrumentTest");
		satellitesRepository.deleteSatellite("satelliteTest");
		
	}

}
