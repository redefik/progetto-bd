package it.uniroma2.dicii.bd.progetto.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import it.uniroma2.dicii.bd.progetto.administration.AdministrationSession;
import it.uniroma2.dicii.bd.progetto.administration.CSVFileParser;
import it.uniroma2.dicii.bd.progetto.administration.CSVFileParserFactory;
import it.uniroma2.dicii.bd.progetto.errorLogic.BatchError;
import it.uniroma2.dicii.bd.progetto.errorLogic.CSVFileParserException;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
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
import it.uniroma2.dicii.bd.progetto.satellite.SatelliteBean;



public class SegmentPointsImportTest {

	/*
	 * Il metodo inserisce nel database gli input necessari all'esecuzione del caso di test. Precisamente, inserisce:
	 * il satellite "satelliteTest"
	 * lo strumento "instrumentTest" di "satelliteTest"
	 * il filamento "filamentA" avente 1 segmento formato da 2 punti (misurato da "instrumentTest")
	 * i 2 punti del segmento di "filamentA"
	 * il filamento "filamentB" avente 1 segmento formato da 2 punti (misurato da "instrumentTest")
	 * i 2 punti del segmento di "filamentB"
	 * il filamento "filamentC" avente 3 punti del contorno (misurato da "instrumentTest")
	 * i 3 punti del contorno di "filamentC"
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
		
		Filament filamentA = new Filament();
		filamentA.setName("filamentA");
		filamentA.setNumber(1);
		filamentA.setInstrumentName("instrumentTest");
		
		SegmentPointImported filamentAFirstSegmentPoint = new SegmentPointImported();
		filamentAFirstSegmentPoint.setFilamentId(1);
		filamentAFirstSegmentPoint.setLongitude(1);
		filamentAFirstSegmentPoint.setLatitude(1);
		filamentAFirstSegmentPoint.setProgNumber(1);
		filamentAFirstSegmentPoint.setSegmentId(1);
		filamentAFirstSegmentPoint.setType('S');
		
		SegmentPointImported filamentASecondSegmentPoint = new SegmentPointImported();
		filamentASecondSegmentPoint.setFilamentId(1);
		filamentASecondSegmentPoint.setLongitude(2);
		filamentASecondSegmentPoint.setLatitude(2);
		filamentASecondSegmentPoint.setProgNumber(2);
		filamentASecondSegmentPoint.setSegmentId(1);
		filamentASecondSegmentPoint.setType('S');
		
		Filament filamentB = new Filament();
		filamentB.setName("filamentB");
		filamentB.setNumber(2);
		filamentB.setInstrumentName("instrumentTest");
		
		SegmentPointImported filamentBFirstSegmentPoint = new SegmentPointImported();
		filamentBFirstSegmentPoint.setFilamentId(2);
		filamentBFirstSegmentPoint.setLongitude(3);
		filamentBFirstSegmentPoint.setLatitude(3);
		filamentBFirstSegmentPoint.setProgNumber(1);
		filamentBFirstSegmentPoint.setSegmentId(1);
		filamentBFirstSegmentPoint.setType('S');
		
		SegmentPointImported filamentBSecondSegmentPoint = new SegmentPointImported();
		filamentBSecondSegmentPoint.setFilamentId(2);
		filamentBSecondSegmentPoint.setLongitude(4);
		filamentBSecondSegmentPoint.setLatitude(4);
		filamentBSecondSegmentPoint.setProgNumber(2);
		filamentBSecondSegmentPoint.setSegmentId(1);
		filamentBSecondSegmentPoint.setType('S');
		
		Filament filamentC = new Filament();
		filamentC.setName("filamentC");
		filamentC.setNumber(3);
		filamentC.setInstrumentName("instrumentTest");
		
		BorderPoint filamentCFirstPoint = new BorderPoint();
		ArrayList<String> pointFilamentsList = new ArrayList<>();
		pointFilamentsList.add("filamentC");
		filamentCFirstPoint.setFilamentNames(pointFilamentsList);
		filamentCFirstPoint.setLongitude(5);
		filamentCFirstPoint.setLatitude(5);
		filamentCFirstPoint.setSatellite("satelliteTest");
		
		BorderPoint filamentCSecondPoint = new BorderPoint();
		filamentCSecondPoint.setFilamentNames(pointFilamentsList);
		filamentCSecondPoint.setLongitude(6);
		filamentCSecondPoint.setLatitude(6);
		filamentCSecondPoint.setSatellite("satelliteTest");
		
		BorderPoint filamentCThirdPoint = new BorderPoint();
		filamentCThirdPoint.setFilamentNames(pointFilamentsList);
		filamentCThirdPoint.setLongitude(7);
		filamentCThirdPoint.setLatitude(7);
		filamentCThirdPoint.setSatellite("satelliteTest");
		
		ArrayList<Filament> filaments = new ArrayList<>();
		filaments.add(filamentA);
		filaments.add(filamentB);
		filaments.add(filamentC);
		
		FilamentsRepositoryFactory filamentsRepositoryFactory = FilamentsRepositoryFactory.getInstance();
		FilamentsRepository filamentsRepository = filamentsRepositoryFactory.createFilamentsRepository();
		filamentsRepository.insertAllFilaments(filaments);
		
		ArrayList<SegmentPointImported> segmentPoints = new ArrayList<>();
		segmentPoints.add(filamentAFirstSegmentPoint);
		segmentPoints.add(filamentASecondSegmentPoint);
		segmentPoints.add(filamentBFirstSegmentPoint);
		segmentPoints.add(filamentBSecondSegmentPoint);
		filamentsRepository.insertAllSegmentPoints(segmentPoints, "satelliteTest");
		
		ArrayList<BorderPoint> borderPoints = new ArrayList<>();
		borderPoints.add(filamentCFirstPoint);
		borderPoints.add(filamentCSecondPoint);
		borderPoints.add(filamentCThirdPoint);
		filamentsRepository.insertAllBorderPoints(borderPoints);
		
	}
	
	/*
	 * Il test utilizza un file contenente:
	 * 2 punti che costituiscono un nuovo segmento per il filamento "filamentA"
	 * un punto del segmento X appartenente ad un filamento inesistente
	 * un punto del segmento Y che appartiene a "filamentA" ma si sovrappone ad un punto del segmento di "filamentB"
	 * un punto del segmento Z che appartiene a "filamentC" ma si sovrappone ad un punto del contorno di "filamentC"
	 * Pertanto, le condizioni da verificare sono:
	 * 1-"filamentA" ha 2 segmenti (1 già presente più il nuovo)
	 * 2-la tabella puntosegmento contiene 6 punti del segmento (i 4 già presenti più i nuovi inseriti che sono solo 2 perchè X, Y e Z non vengono inseriti)
	 * NOTA: si assume che il satellite scelto sia "satelliteTest"
	 * */
	
	@Test
	public void testSegmentPointsImport() throws ConfigurationError, DataAccessError, BatchError, CSVFileParserException {
		File testFile = new File("src/it/uniroma2/dicii/bd/progetto/test/segmentPointsImportFileTest.csv");
		CSVFileParserFactory parserFactory = CSVFileParserFactory.getInstance();
		CSVFileParser fileParser = parserFactory.createCSVFileParser();
		ArrayList<SegmentPointImported> segmentPoints = fileParser.getSegmentPoints(testFile);
		AdministrationSession administrationSession = AdministrationSession.getInstance();
		SatelliteBean satelliteBean = new SatelliteBean("satelliteTest", new Date(), new Date());
		administrationSession.insertSegmentPoints(segmentPoints, satelliteBean);
		FilamentsRepositoryFactory filamentsRepositoryFactory = FilamentsRepositoryFactory.getInstance();
		FilamentsRepository filamentsRepository = filamentsRepositoryFactory.createFilamentsRepository();
		ArrayList<SegmentPoint> actualSegmentPoints = filamentsRepository.findAllSegmentPoints();
		Filament filamentA = filamentsRepository.findFilamentByName("filamentA");
		boolean firstCondition = actualSegmentPoints.size() == 6;
		boolean secondCondition = filamentA.getNumberOfSegments() == 2;
		boolean conditionToVerify = firstCondition && secondCondition;
		Assert.assertTrue(conditionToVerify);	
	}
	
	/*
	 * Il metodo rimuove gli input inseriti nel database per eseguire la classe di test
	 * */
	@AfterClass
	public static void cleanUp() throws ConfigurationError, DataAccessError {
		
		Filament filamentA = new Filament();
		filamentA.setName("filamentA");
		filamentA.setNumber(1);
		filamentA.setInstrumentName("instrumentTest");
		
		Filament filamentB = new Filament();
		filamentB.setName("filamentB");
		filamentB.setNumber(2);
		filamentB.setInstrumentName("instrumentTest");
		
		Filament filamentC = new Filament();
		filamentC.setName("filamentC");
		filamentC.setNumber(3);
		filamentC.setInstrumentName("instrumentTest");
		
		FilamentsRepositoryFactory filamentsRepositoryFactory = FilamentsRepositoryFactory.getInstance();
		FilamentsRepository filamentsRepository = filamentsRepositoryFactory.createFilamentsRepository();
		
		SegmentPoint filamentAFirstSegmentPoint = new SegmentPoint();
		filamentAFirstSegmentPoint.setFilament(filamentA);
		filamentAFirstSegmentPoint.setLongitude(1);
		filamentAFirstSegmentPoint.setLatitude(1);
		filamentAFirstSegmentPoint.setProgNumber(1);
		filamentAFirstSegmentPoint.setSegmentId(1);
		filamentAFirstSegmentPoint.setType('S');
		
		SegmentPoint filamentASecondSegmentPoint = new SegmentPoint();
		filamentASecondSegmentPoint.setFilament(filamentA);
		filamentASecondSegmentPoint.setLongitude(2);
		filamentASecondSegmentPoint.setLatitude(2);
		filamentASecondSegmentPoint.setProgNumber(2);
		filamentASecondSegmentPoint.setSegmentId(1);
		filamentASecondSegmentPoint.setType('S');
		
		SegmentPoint filamentBFirstSegmentPoint = new SegmentPoint();
		filamentBFirstSegmentPoint.setFilament(filamentB);
		filamentBFirstSegmentPoint.setLongitude(3);
		filamentBFirstSegmentPoint.setLatitude(3);
		filamentBFirstSegmentPoint.setProgNumber(1);
		filamentBFirstSegmentPoint.setSegmentId(1);
		filamentBFirstSegmentPoint.setType('S');
		
		SegmentPoint filamentBSecondSegmentPoint = new SegmentPoint();
		filamentBSecondSegmentPoint.setFilament(filamentB);
		filamentBSecondSegmentPoint.setLongitude(4);
		filamentBSecondSegmentPoint.setLatitude(4);
		filamentBSecondSegmentPoint.setProgNumber(2);
		filamentBSecondSegmentPoint.setSegmentId(1);
		filamentBSecondSegmentPoint.setType('S');
		
		//seguono i punti del segmento inseriti eseguendo il metodo @Test
		
		SegmentPoint newSegmentPointFirst = new SegmentPoint();
		newSegmentPointFirst.setFilament(filamentA);
		newSegmentPointFirst.setLongitude(1);
		newSegmentPointFirst.setLatitude(1);
		newSegmentPointFirst.setProgNumber(1);
		newSegmentPointFirst.setSegmentId(2);
		newSegmentPointFirst.setType('B');
		
		SegmentPoint newSegmentPointSecond = new SegmentPoint();
		newSegmentPointSecond.setFilament(filamentA);
		newSegmentPointSecond.setLongitude(1.5);
		newSegmentPointSecond.setLatitude(1.5);
		newSegmentPointSecond.setProgNumber(2);
		newSegmentPointSecond.setSegmentId(2);
		newSegmentPointSecond.setType('B');
		
		ArrayList<SegmentPoint> segmentPoints = new ArrayList<>();
		segmentPoints.add(filamentAFirstSegmentPoint);
		segmentPoints.add(filamentASecondSegmentPoint);
		segmentPoints.add(filamentBFirstSegmentPoint);
		segmentPoints.add(filamentBSecondSegmentPoint);
		segmentPoints.add(newSegmentPointFirst);
		segmentPoints.add(newSegmentPointSecond);
		filamentsRepository.deleteSegmentPoints(segmentPoints);
		
		BorderPoint filamentCFirstPoint = new BorderPoint();
		ArrayList<String> pointFilamentsList = new ArrayList<>();
		pointFilamentsList.add("filamentC");
		filamentCFirstPoint.setFilamentNames(pointFilamentsList);
		filamentCFirstPoint.setLongitude(5);
		filamentCFirstPoint.setLatitude(5);
		filamentCFirstPoint.setSatellite("satelliteTest");
		
		BorderPoint filamentCSecondPoint = new BorderPoint();
		filamentCSecondPoint.setFilamentNames(pointFilamentsList);
		filamentCSecondPoint.setLongitude(6);
		filamentCSecondPoint.setLatitude(6);
		filamentCSecondPoint.setSatellite("satelliteTest");
		
		BorderPoint filamentCThirdPoint = new BorderPoint();
		filamentCThirdPoint.setFilamentNames(pointFilamentsList);
		filamentCThirdPoint.setLongitude(7);
		filamentCThirdPoint.setLatitude(7);
		filamentCThirdPoint.setSatellite("satelliteTest");
		
		ArrayList<BorderPoint> borderPoints = new ArrayList<>();
		borderPoints.add(filamentCFirstPoint);
		borderPoints.add(filamentCSecondPoint);
		borderPoints.add(filamentCThirdPoint);
		filamentsRepository.deleteBorderPoints(borderPoints);
		
		filamentsRepository.deleteFilamentWithName("filamentA");
		filamentsRepository.deleteFilamentWithName("filamentB");
		filamentsRepository.deleteFilamentWithName("filamentC");
		
		SatellitesRepositoryFactory satellitesRepositoryFactory = SatellitesRepositoryFactory.getInstance();
		SatellitesRepository satellitesRepository = satellitesRepositoryFactory.createSatellitesRepository();
		
		satellitesRepository.deleteInstrument("instrumentTest");
		satellitesRepository.deleteSatellite("satelliteTest");
		
	}

}
