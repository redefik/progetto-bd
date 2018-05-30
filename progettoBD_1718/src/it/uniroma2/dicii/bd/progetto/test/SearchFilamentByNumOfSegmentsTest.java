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
import it.uniroma2.dicii.bd.progetto.errorLogic.InvalidNumOfSegmentsError;
import it.uniroma2.dicii.bd.progetto.filament.Filament;
import it.uniroma2.dicii.bd.progetto.filament.FilamentBean;
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

public class SearchFilamentByNumOfSegmentsTest {

	/*
	*Il metodo inserisce nel database i dati di input per l'esecuzione del caso di test. Precisamente, inserisce:
	*un filamento con 0 segmenti
	*un filamento con 2 segmenti
	*un filamento con 4 segmenti
	*In tal modo la ricerca di filamenti con numero di segmenti compreso tra 0 e 3 dovrà restituire solamente i primi 2 segmenti
	*Per soddisfare i vincoli di FK vengono anche inseriti un satellite e uno strumento di riferimento  
	*/
	
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
		
		Filament firstFilament = new Filament();
		firstFilament.setName("filamentTest1");
		firstFilament.setInstrumentName("instrumentTest");
		firstFilament.setNumber(0);
		
		Filament secondFilament = new Filament();
		secondFilament.setName("filamentTest2");
		secondFilament.setInstrumentName("instrumentTest");
		secondFilament.setNumber(1);
		
		Filament thirdFilament = new Filament();
		thirdFilament.setName("filamentTest3");
		thirdFilament.setInstrumentName("instrumentTest");
		thirdFilament.setNumber(2);
		
		ArrayList<Filament> filaments = new ArrayList<>();
		filaments.add(firstFilament);
		filaments.add(secondFilament);
		filaments.add(thirdFilament);
		
		filamentsRepository.insertAllFilaments(filaments);
		
		//si aggiungono i punti del segmento del filamento con 2 segmenti
		SegmentPointImported firstSegmentPoint = new SegmentPointImported();
		firstSegmentPoint.setFilamentId(1);
		firstSegmentPoint.setLongitude(3);
		firstSegmentPoint.setLatitude(3);
		firstSegmentPoint.setProgNumber(1);
		firstSegmentPoint.setSegmentId(1);
		firstSegmentPoint.setType('S');
		
		SegmentPointImported secondSegmentPoint = new SegmentPointImported();
		secondSegmentPoint.setFilamentId(1);
		secondSegmentPoint.setLongitude(3);
		secondSegmentPoint.setLatitude(3);
		secondSegmentPoint.setProgNumber(1);
		secondSegmentPoint.setSegmentId(2);
		secondSegmentPoint.setType('B');
		
		SegmentPointImported thirdSegmentPoint = new SegmentPointImported();
		thirdSegmentPoint.setFilamentId(1);
		thirdSegmentPoint.setLongitude(5);
		thirdSegmentPoint.setLatitude(5);
		thirdSegmentPoint.setProgNumber(2);
		thirdSegmentPoint.setSegmentId(1);
		thirdSegmentPoint.setType('S');
		
		SegmentPointImported fourthSegmentPoint = new SegmentPointImported();
		fourthSegmentPoint.setFilamentId(1);
		fourthSegmentPoint.setLongitude(2);
		fourthSegmentPoint.setLatitude(5);
		fourthSegmentPoint.setProgNumber(2);
		fourthSegmentPoint.setSegmentId(2);
		fourthSegmentPoint.setType('B');
		
		ArrayList<SegmentPointImported> segmentPointsSecondFilament = new ArrayList<>();
		
		segmentPointsSecondFilament.add(firstSegmentPoint);
		segmentPointsSecondFilament.add(secondSegmentPoint);
		segmentPointsSecondFilament.add(thirdSegmentPoint);
		segmentPointsSecondFilament.add(fourthSegmentPoint);
		
		filamentsRepository.insertAllSegmentPoints(segmentPointsSecondFilament, "satelliteTest");
		
		// si aggiungono i punti del segmento del filamento con 4 segmenti
		SegmentPointImported fifthSegmentPoint = new SegmentPointImported();
		fifthSegmentPoint.setFilamentId(2);
		fifthSegmentPoint.setLongitude(10);
		fifthSegmentPoint.setLatitude(10);
		fifthSegmentPoint.setProgNumber(1);
		fifthSegmentPoint.setSegmentId(1);
		fifthSegmentPoint.setType('S');
		
		SegmentPointImported sixthSegmentPoint = new SegmentPointImported();
		sixthSegmentPoint.setFilamentId(2);
		sixthSegmentPoint.setLongitude(10);
		sixthSegmentPoint.setLatitude(10);
		sixthSegmentPoint.setProgNumber(1);
		sixthSegmentPoint.setSegmentId(2);
		sixthSegmentPoint.setType('B');
		
		SegmentPointImported seventhSegmentPoint = new SegmentPointImported();
		seventhSegmentPoint.setFilamentId(2);
		seventhSegmentPoint.setLongitude(10);
		seventhSegmentPoint.setLatitude(12);
		seventhSegmentPoint.setProgNumber(2);
		seventhSegmentPoint.setSegmentId(2);
		seventhSegmentPoint.setType('B');
		
		SegmentPointImported eigthSegmentPoint = new SegmentPointImported();
		eigthSegmentPoint.setFilamentId(2);
		eigthSegmentPoint.setLongitude(12);
		eigthSegmentPoint.setLatitude(12);
		eigthSegmentPoint.setProgNumber(2);
		eigthSegmentPoint.setSegmentId(1);
		eigthSegmentPoint.setType('S');
		
		SegmentPointImported ninthSegmentPoint = new SegmentPointImported();
		ninthSegmentPoint.setFilamentId(2);
		ninthSegmentPoint.setLongitude(12);
		ninthSegmentPoint.setLatitude(12);
		ninthSegmentPoint.setProgNumber(1);
		ninthSegmentPoint.setSegmentId(3);
		ninthSegmentPoint.setType('B');
		
		SegmentPointImported tenthSegmentPoint = new SegmentPointImported();
		tenthSegmentPoint.setFilamentId(2);
		tenthSegmentPoint.setLongitude(12);
		tenthSegmentPoint.setLatitude(13);
		tenthSegmentPoint.setProgNumber(2);
		tenthSegmentPoint.setSegmentId(3);
		tenthSegmentPoint.setType('B');
		
		SegmentPointImported eleventhSegmentPoint = new SegmentPointImported();
		eleventhSegmentPoint.setFilamentId(2);
		eleventhSegmentPoint.setLongitude(12);
		eleventhSegmentPoint.setLatitude(12);
		eleventhSegmentPoint.setProgNumber(1);
		eleventhSegmentPoint.setSegmentId(4);
		eleventhSegmentPoint.setType('B');
		
		SegmentPointImported twelvethSegmentPoint = new SegmentPointImported();
		twelvethSegmentPoint.setFilamentId(2);
		twelvethSegmentPoint.setLongitude(14);
		twelvethSegmentPoint.setLatitude(12);
		twelvethSegmentPoint.setProgNumber(2);
		twelvethSegmentPoint.setSegmentId(4);
		twelvethSegmentPoint.setType('B');
		
		ArrayList<SegmentPointImported> segmentPointsThirdFilament = new ArrayList<>();
		
		segmentPointsThirdFilament.add(fifthSegmentPoint);
		segmentPointsThirdFilament.add(sixthSegmentPoint);
		segmentPointsThirdFilament.add(seventhSegmentPoint);
		segmentPointsThirdFilament.add(eigthSegmentPoint);
		segmentPointsThirdFilament.add(ninthSegmentPoint);
		segmentPointsThirdFilament.add(tenthSegmentPoint);
		segmentPointsThirdFilament.add(eleventhSegmentPoint);
		segmentPointsThirdFilament.add(twelvethSegmentPoint);
	
		filamentsRepository.insertAllSegmentPoints(segmentPointsThirdFilament, "satelliteTest");
		
	}
	
	@Test
	public void testSearchFilamentByNumOfSegments() throws InvalidNumOfSegmentsError, ConfigurationError, DataAccessError {
		StandardUserSession session = StandardUserSession.getInstance();
		ArrayList<FilamentBean> filamentsFound = session.findFilamentsByNumOfSegments(0, 3);
		boolean firstCondition = filamentsFound.size() == 2;
		boolean secondCondition = 
				filamentsFound.get(0).getName().equals("filamentTest1") && filamentsFound.get(1).getName().equals("filamentTest2");
		boolean thirdCondition = 
				filamentsFound.get(1).getName().equals("filamentTest1") && filamentsFound.get(0).getName().equals("filamentTest2");
		boolean conditionToVerify = firstCondition && (secondCondition || thirdCondition);
		Assert.assertTrue(conditionToVerify);
	}
	
	@AfterClass
	public static void cleanUp() throws ConfigurationError, DataAccessError {
		
		FilamentsRepositoryFactory filamentsRepositoryFactory = FilamentsRepositoryFactory.getInstance();
		FilamentsRepository filamentsRepository = filamentsRepositoryFactory.createFilamentsRepository();
		
		Filament firstFilament = new Filament();
		firstFilament.setName("filamentTest1");
		firstFilament.setInstrumentName("instrumentTest");
		firstFilament.setNumber(0);
		
		Filament secondFilament = new Filament();
		secondFilament.setName("filamentTest2");
		secondFilament.setInstrumentName("instrumentTest");
		secondFilament.setNumber(1);
		
		Filament thirdFilament = new Filament();
		thirdFilament.setName("filamentTest3");
		thirdFilament.setInstrumentName("instrumentTest");
		thirdFilament.setNumber(2);
		
		SegmentPoint firstSegmentPoint = new SegmentPoint();
		firstSegmentPoint.setFilament(secondFilament);
		firstSegmentPoint.setLongitude(3);
		firstSegmentPoint.setLatitude(3);
		firstSegmentPoint.setProgNumber(1);
		firstSegmentPoint.setSegmentId(1);
		firstSegmentPoint.setType('S');
		
		SegmentPoint secondSegmentPoint = new SegmentPoint();
		secondSegmentPoint.setFilament(secondFilament);
		secondSegmentPoint.setLongitude(3);
		secondSegmentPoint.setLatitude(3);
		secondSegmentPoint.setProgNumber(1);
		secondSegmentPoint.setSegmentId(2);
		secondSegmentPoint.setType('B');
		
		SegmentPoint thirdSegmentPoint = new SegmentPoint();
		thirdSegmentPoint.setFilament(secondFilament);
		thirdSegmentPoint.setLongitude(5);
		thirdSegmentPoint.setLatitude(5);
		thirdSegmentPoint.setProgNumber(2);
		thirdSegmentPoint.setSegmentId(1);
		thirdSegmentPoint.setType('S');
		
		SegmentPoint fourthSegmentPoint = new SegmentPoint();
		fourthSegmentPoint.setFilament(secondFilament);
		fourthSegmentPoint.setLongitude(2);
		fourthSegmentPoint.setLatitude(5);
		fourthSegmentPoint.setProgNumber(2);
		fourthSegmentPoint.setSegmentId(2);
		fourthSegmentPoint.setType('B');
		
		SegmentPoint fifthSegmentPoint = new SegmentPoint();
		fifthSegmentPoint.setFilament(thirdFilament);
		fifthSegmentPoint.setLongitude(10);
		fifthSegmentPoint.setLatitude(10);
		fifthSegmentPoint.setProgNumber(1);
		fifthSegmentPoint.setSegmentId(1);
		fifthSegmentPoint.setType('S');
		
		SegmentPoint sixthSegmentPoint = new SegmentPoint();
		sixthSegmentPoint.setFilament(thirdFilament);
		sixthSegmentPoint.setLongitude(10);
		sixthSegmentPoint.setLatitude(10);
		sixthSegmentPoint.setProgNumber(1);
		sixthSegmentPoint.setSegmentId(2);
		sixthSegmentPoint.setType('B');
		
		SegmentPoint seventhSegmentPoint = new SegmentPoint();
		seventhSegmentPoint.setFilament(thirdFilament);
		seventhSegmentPoint.setLongitude(10);
		seventhSegmentPoint.setLatitude(12);
		seventhSegmentPoint.setProgNumber(2);
		seventhSegmentPoint.setSegmentId(2);
		seventhSegmentPoint.setType('B');
		
		SegmentPoint eigthSegmentPoint = new SegmentPoint();
		eigthSegmentPoint.setFilament(thirdFilament);
		eigthSegmentPoint.setLongitude(12);
		eigthSegmentPoint.setLatitude(12);
		eigthSegmentPoint.setProgNumber(2);
		eigthSegmentPoint.setSegmentId(1);
		eigthSegmentPoint.setType('S');
		
		SegmentPoint ninthSegmentPoint = new SegmentPoint();
		ninthSegmentPoint.setFilament(thirdFilament);
		ninthSegmentPoint.setLongitude(12);
		ninthSegmentPoint.setLatitude(12);
		ninthSegmentPoint.setProgNumber(1);
		ninthSegmentPoint.setSegmentId(3);
		ninthSegmentPoint.setType('B');
		
		SegmentPoint tenthSegmentPoint = new SegmentPoint();
		tenthSegmentPoint.setFilament(thirdFilament);
		tenthSegmentPoint.setLongitude(12);
		tenthSegmentPoint.setLatitude(13);
		tenthSegmentPoint.setProgNumber(2);
		tenthSegmentPoint.setSegmentId(3);
		tenthSegmentPoint.setType('B');
		
		SegmentPoint eleventhSegmentPoint = new SegmentPoint();
		eleventhSegmentPoint.setFilament(thirdFilament);
		eleventhSegmentPoint.setLongitude(12);
		eleventhSegmentPoint.setLatitude(12);
		eleventhSegmentPoint.setProgNumber(1);
		eleventhSegmentPoint.setSegmentId(4);
		eleventhSegmentPoint.setType('B');
		
		SegmentPoint twelvethSegmentPoint = new SegmentPoint();
		twelvethSegmentPoint.setFilament(thirdFilament);
		twelvethSegmentPoint.setLongitude(14);
		twelvethSegmentPoint.setLatitude(12);
		twelvethSegmentPoint.setProgNumber(2);
		twelvethSegmentPoint.setSegmentId(4);
		twelvethSegmentPoint.setType('B');
		
		ArrayList<SegmentPoint> segmentPoints = new ArrayList<>();
		segmentPoints.add(firstSegmentPoint);
		segmentPoints.add(secondSegmentPoint);
		segmentPoints.add(thirdSegmentPoint);
		segmentPoints.add(fourthSegmentPoint);
		segmentPoints.add(fifthSegmentPoint);
		segmentPoints.add(sixthSegmentPoint);
		segmentPoints.add(seventhSegmentPoint);
		segmentPoints.add(eigthSegmentPoint);
		segmentPoints.add(ninthSegmentPoint);
		segmentPoints.add(tenthSegmentPoint);
		segmentPoints.add(eleventhSegmentPoint);
		segmentPoints.add(twelvethSegmentPoint);
		
		filamentsRepository.deleteSegmentPoints(segmentPoints);
		filamentsRepository.deleteFilamentWithName("filamentTest1");
		filamentsRepository.deleteFilamentWithName("filamentTest2");
		filamentsRepository.deleteFilamentWithName("filamentTest3");
		
		SatellitesRepositoryFactory satellitesRepositoryFactory = SatellitesRepositoryFactory.getInstance();
		SatellitesRepository satellitesRepository = satellitesRepositoryFactory.createSatellitesRepository();
		
		satellitesRepository.deleteInstrument("instrumentTest");
		satellitesRepository.deleteSatellite("satelliteTest");

	}
	
}
