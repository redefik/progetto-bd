package it.uniroma2.dicii.bd.progetto.test;

import static org.junit.Assert.assertTrue;

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
import it.uniroma2.dicii.bd.progetto.repository.SatellitesRepository;
import it.uniroma2.dicii.bd.progetto.repository.SatellitesRepositoryFactory;
import it.uniroma2.dicii.bd.progetto.repository.StarsRepository;
import it.uniroma2.dicii.bd.progetto.repository.StarsRepositoryFactory;
import it.uniroma2.dicii.bd.progetto.satellite.Agency;
import it.uniroma2.dicii.bd.progetto.satellite.Satellite;
import it.uniroma2.dicii.bd.progetto.star.Star;
import it.uniroma2.dicii.bd.progetto.star.StarBean;

public class TestStarImport {
	
	/*
	 * Il test prevede che venga inserito un satellite all'interno del db e che successivamente venga effettuato 
	 * l'import di due file. Nel primo caso (testSuccess) il file non contiene chiavi duplicate per cui l'inserimento 
	 * deve andare a buon fine: si controlla che alla fine siano presenti nello strato di peristenza tutte e sole le
	 * stelle relative a righe del file csv. Nel secondo caso (testFailure) il file contiene una chiave 
	 * duplicata e si verifica che venga sollevata l'opportuna eccezione.
	 * */
	
	@BeforeClass
	public static void setUp() throws ConfigurationError, DataAccessError {
		ArrayList<Agency> agencies = new ArrayList<Agency>();
		agencies.add(new Agency("Agenzia"));
		SatellitesRepository satellitesRepository = SatellitesRepositoryFactory.getInstance().createSatellitesRepository();
		satellitesRepository.persistSatellite(new Satellite("Satellite",  new Date(), null), agencies);
	}
	
	@Test
	public void testSuccess() throws ConfigurationError, CSVFileParserException, DataAccessError, BatchError {
		CSVFileParser parser = CSVFileParserFactory.getInstance().createCSVFileParser();
		File file = new File(this.getClass().getResource("testSuccessImportStar.csv").getFile());
		ArrayList<StarBean> starBeans = parser.getStarBeans(file, "Satellite");
		AdministrationSession.getInstance().insertStars(starBeans);
		
		StarsRepository starsRepository = StarsRepositoryFactory.getInstance().createStarsRepository();
		ArrayList<Star> stars = starsRepository.findAllStars();
		
		boolean condition1, condition2;
		
		condition1 = (stars.size() == 2);
		
		String star1 = stars.get(0).getName();
		String star2 = stars.get(1).getName();
		
		condition2 = ((star1.equals("Stella1")) && (star2.equals("Stella2"))) ||
					 ((star1.equals("Stella2")) && (star2.equals("Stella1")));
		
		assertTrue(condition1 && condition2);
	}
	
	@Test (expected = BatchError.class)
	public void testFailure() throws ConfigurationError, CSVFileParserException, DataAccessError, BatchError {
		CSVFileParser parser = CSVFileParserFactory.getInstance().createCSVFileParser();
		File file = new File(this.getClass().getResource("testFailureImportStar.csv").getFile());
		ArrayList<StarBean> starBeans = parser.getStarBeans(file, "Satellite");
		AdministrationSession.getInstance().insertStars(starBeans);
	}
	
	@AfterClass
	public static void cleanUp() throws DataAccessError, ConfigurationError {
		StarsRepository starsRepository = StarsRepositoryFactory.getInstance().createStarsRepository();
		starsRepository.deleteStar("Stella1");
		starsRepository.deleteStar("Stella2");
		SatellitesRepository satellitesRepository = SatellitesRepositoryFactory.getInstance().createSatellitesRepository();
		satellitesRepository.deleteSatellite("Satellite");
	}
}
