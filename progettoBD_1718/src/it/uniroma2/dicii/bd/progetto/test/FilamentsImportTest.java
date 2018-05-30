package it.uniroma2.dicii.bd.progetto.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import it.uniroma2.dicii.bd.progetto.administration.AdministrationSession;
import it.uniroma2.dicii.bd.progetto.administration.CSVFileParser;
import it.uniroma2.dicii.bd.progetto.administration.CSVFileParserFactory;
import it.uniroma2.dicii.bd.progetto.errorLogic.BatchError;
import it.uniroma2.dicii.bd.progetto.errorLogic.CSVFileParserException;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.filament.Filament;
import it.uniroma2.dicii.bd.progetto.filament.FilamentBean;
import it.uniroma2.dicii.bd.progetto.repository.FilamentsRepository;
import it.uniroma2.dicii.bd.progetto.repository.FilamentsRepositoryFactory;
import it.uniroma2.dicii.bd.progetto.repository.SatellitesRepository;
import it.uniroma2.dicii.bd.progetto.repository.SatellitesRepositoryFactory;
import it.uniroma2.dicii.bd.progetto.satellite.Agency;
import it.uniroma2.dicii.bd.progetto.satellite.Instrument;
import it.uniroma2.dicii.bd.progetto.satellite.Satellite;
import org.junit.Assert;

public class FilamentsImportTest {

	/*
	 * Il metodo inserisce nel database gli input necessari allo svolgimento del test. Precisamente inserisce:
	 * un satellite "satelliteTest"
	 * uno strumento "instrumentTest" appartenente a "satelliteTest"
	 * un filamento "filamentTest" misurato con "instrumentTest"
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
		
	}
	
	/*
	 * Il test utilizza un file .csv contenente due filamenti: filamentA e filamentB.
	 * Si verifica che dopo l'import il numero di filamenti presenti sia pari a 3.
	 * */
	@Test
	public void testImportFilamentsSuccess() throws ConfigurationError, CSVFileParserException, DataAccessError, BatchError {
		File testFile = new File("src/it/uniroma2/dicii/bd/progetto/test/filamentsImportSuccessFileTest.csv");
		CSVFileParserFactory parserFactory = CSVFileParserFactory.getInstance();
		CSVFileParser fileParser = parserFactory.createCSVFileParser();
		ArrayList<FilamentBean> filamentBeans = fileParser.getFilamentBeans(testFile);
		AdministrationSession administrationSession = AdministrationSession.getInstance();
		administrationSession.insertFilaments(filamentBeans);
		FilamentsRepositoryFactory filamentsRepositoryFactory = FilamentsRepositoryFactory.getInstance();
		FilamentsRepository filamentsRepository = filamentsRepositoryFactory.createFilamentsRepository();
		boolean conditionToVerify = filamentsRepository.getFilamentsCount() == 3;
		Assert.assertTrue(conditionToVerify);
	}
	
	/*
	 * Il test utilizza un file .csv contenente un filamento di nome "filamentTest".
	 * Un filamento così nominato è già presente, dunque si verifica che venga sollevata l'eccezione BatchError.
	 * */
	@Test(expected = BatchError.class)
	public void testImportFilamentsFail() throws ConfigurationError, CSVFileParserException, DataAccessError, BatchError {
		File testFile = new File("src/it/uniroma2/dicii/bd/progetto/test/filamentsImportFailFileTest.csv");
		CSVFileParserFactory parserFactory = CSVFileParserFactory.getInstance();
		CSVFileParser fileParser = parserFactory.createCSVFileParser();
		ArrayList<FilamentBean> filamentBeans = fileParser.getFilamentBeans(testFile);
		AdministrationSession administrationSession = AdministrationSession.getInstance();
		administrationSession.insertFilaments(filamentBeans);
	}
	
	/*
	 * Il test utilizza un file .csv che non è nel formato corretto.
	 * Pertanto si verifica che venga sollevata l'eccezione CSVFileParserException
	 * */
	@Test(expected = CSVFileParserException.class)
	public void testImportInvalidFile() throws ConfigurationError, CSVFileParserException, DataAccessError, BatchError {
		File testFile = new File("src/it/uniroma2/dicii/bd/progetto/test/invalidFilamentsFile.csv");
		CSVFileParserFactory parserFactory = CSVFileParserFactory.getInstance();
		CSVFileParser fileParser = parserFactory.createCSVFileParser();
		ArrayList<FilamentBean> filamentBeans = fileParser.getFilamentBeans(testFile);
		AdministrationSession administrationSession = AdministrationSession.getInstance();
		administrationSession.insertFilaments(filamentBeans);
	}
	
	
	/*
	 * Il metodo rimuove i dati inseriti prima e durante l'esecuzione del caso di test
	 * */
	@AfterClass
	public static void cleanUp() throws ConfigurationError, DataAccessError {
		FilamentsRepositoryFactory filamentsRepositoryFactory = FilamentsRepositoryFactory.getInstance();
		FilamentsRepository filamentsRepository = filamentsRepositoryFactory.createFilamentsRepository();
		filamentsRepository.deleteFilamentWithName("filamentTest");
		filamentsRepository.deleteFilamentWithName("filamentA");
		filamentsRepository.deleteFilamentWithName("filamentB");
		
		SatellitesRepositoryFactory satellitesRepositoryFactory = SatellitesRepositoryFactory.getInstance();
		SatellitesRepository satellitesRepository = satellitesRepositoryFactory.createSatellitesRepository();
		
		satellitesRepository.deleteInstrument("instrumentTest");
		satellitesRepository.deleteSatellite("satelliteTest");
	}
	
}
