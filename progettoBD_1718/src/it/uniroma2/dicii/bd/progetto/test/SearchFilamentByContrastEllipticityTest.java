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
import it.uniroma2.dicii.bd.progetto.errorLogic.InvalidBrightnessError;
import it.uniroma2.dicii.bd.progetto.errorLogic.InvalidEllipticityError;
import it.uniroma2.dicii.bd.progetto.filament.ContrastEllipticityResearchResult;
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

public class SearchFilamentByContrastEllipticityTest {
	
	/*
	 * Il metodo inserisce nel database gli input per lo svolgimento del caso di test. Precisamente, inserisce:
	 * un filamento con brillanza 2(quindi contrasto 1.02) ed ellitticità 3
	 * un filamento con brillanza 7(quindi contrasto 1.07) ed ellitticità 8
	 * un filamento con brillanza 3(quindi contrasto 1.03) ed ellitticità 6
	 * Pertanto la ricerca di un filamento con brillanza maggiore di 2 ed ellitticità compresa tra 6 e 7 dovrà restituire il solo
	 * filamento con brillanza 3 ed ellitticità 6
	 * Per soddisfare i vincoli di FK vengono anche inseriti un satellite e uno strumento di riferimento
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
		
		Filament firstFilament = new Filament();
		firstFilament.setName("filamentTest1");
		firstFilament.setNumber(0);
		firstFilament.setContrast(1.02);
		firstFilament.setEllipticity(3);
		firstFilament.setInstrumentName("instrumentTest");
		Filament secondFilament = new Filament();
		secondFilament.setName("filamentTest2");
		secondFilament.setNumber(1);
		secondFilament.setContrast(1.07);
		secondFilament.setEllipticity(8);
		secondFilament.setInstrumentName("instrumentTest");
		Filament thirdFilament = new Filament();
		thirdFilament.setName("filamentTest3");
		thirdFilament.setNumber(2);
		thirdFilament.setContrast(1.03);
		thirdFilament.setEllipticity(6);
		thirdFilament.setInstrumentName("instrumentTest");
		
		ArrayList<Filament> filaments = new ArrayList<>();
		filaments.add(firstFilament);
		filaments.add(secondFilament);
		filaments.add(thirdFilament);
		filamentsRepository.insertAllFilaments(filaments);
	}
	
	@Test
	public void testSearchFilamentByContrastAndEllipticity() 
			throws InvalidBrightnessError, InvalidEllipticityError, ConfigurationError, DataAccessError {
		StandardUserSession standardUserSession = StandardUserSession.getInstance();
		ContrastEllipticityResearchResult researchResult = 
				standardUserSession.findFilamentsByContrastAndEllipticity(2, 6, 7);
		ArrayList<FilamentBean> suitableFilaments = researchResult.getSuitableFilaments();
		boolean conditionToVerify = suitableFilaments.size() == 1 && suitableFilaments.get(0).getName().equals("filamentTest3");
		Assert.assertTrue(conditionToVerify);
	}
	
	
	/*
	 * Il metodo rimuove i dati inseriti nel database per eseguire la classe di test
	 * */
	@AfterClass
	public static void cleanUp() throws ConfigurationError, DataAccessError {
		
		FilamentsRepositoryFactory filamentsRepositoryFactory = FilamentsRepositoryFactory.getInstance();
		FilamentsRepository filamentsRepository = filamentsRepositoryFactory.createFilamentsRepository();
		
		filamentsRepository.deleteFilamentWithName("filamentTest1");
		filamentsRepository.deleteFilamentWithName("filamentTest2");
		filamentsRepository.deleteFilamentWithName("filamentTest3");
		
		SatellitesRepositoryFactory satellitesRepositoryFactory = SatellitesRepositoryFactory.getInstance();
		SatellitesRepository satellitesRepository = satellitesRepositoryFactory.createSatellitesRepository();
		
		satellitesRepository.deleteInstrument("instrumentTest");
		satellitesRepository.deleteSatellite("satelliteTest");
	}

}
