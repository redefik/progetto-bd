package it.uniroma2.dicii.bd.progetto.test;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import it.uniroma2.dicii.bd.progetto.administration.AdministrationSession;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.repository.SatellitesRepository;
import it.uniroma2.dicii.bd.progetto.repository.SatellitesRepositoryFactory;
import it.uniroma2.dicii.bd.progetto.satellite.Agency;
import it.uniroma2.dicii.bd.progetto.satellite.Instrument;
import it.uniroma2.dicii.bd.progetto.satellite.InstrumentBean;
import it.uniroma2.dicii.bd.progetto.satellite.Satellite;
import it.uniroma2.dicii.bd.progetto.satellite.SatelliteBean;

public class TestInstrumentImport {
	private static String SATELLITE_NAME = "Satellite";
	private static String INSTRUMENT_NAME = "Instrument";
	private static String AGENCY_NAME = "Agenzia";
	private static String LIST_BANDS = "";
	private static SatelliteBean satelliteBean;
	private static Satellite satellite;
	private static InstrumentBean instrumentBean;
 
	@BeforeClass
	public static void setUp() throws ConfigurationError, DataAccessError {
		// inserisco nel database un satellite a cui dovrò associare poi lo strumento e la sua agenzia
		ArrayList<Agency> agencies = new ArrayList<Agency>();
		agencies.add(new Agency(AGENCY_NAME));
		SatellitesRepository satellitesRepository = SatellitesRepositoryFactory.getInstance().createSatellitesRepository();
		satellite = new Satellite(SATELLITE_NAME,  new Date(), null);
		satelliteBean = new SatelliteBean(satellite);
		satellitesRepository.persistSatellite(satellite, agencies);
	}
	
	@Test
	public void testRegisterInstrument() throws ConfigurationError, DataAccessError {
		// inserisco uno strumento associato al satellite creato da me
		instrumentBean = new InstrumentBean(INSTRUMENT_NAME, LIST_BANDS);
		AdministrationSession.getInstance().registerInstrument(satelliteBean, instrumentBean);
		
		// ricerco tutti gli strumenti relativi al mio satellite, ovvero solo quello del test
		SatellitesRepository satellitesRepository = SatellitesRepositoryFactory.getInstance().createSatellitesRepository();
		ArrayList<Instrument> instruments = satellitesRepository.findAllInstrumenOfSatellite(satellite);
		
		boolean firstCondition;
		boolean secondCondition;
		
		// verifico che lo strumento venga trovato
		if(instruments.size()==1) { // e confronto i valori
			firstCondition = instruments.get(0).getName().equals(INSTRUMENT_NAME);
			secondCondition = instruments.get(0).getListBands().equals(LIST_BANDS);

		} else {
			firstCondition = false;
			secondCondition = false;
		}
		
		assertTrue(firstCondition && secondCondition);
	}
	
	@AfterClass
	public static void cleanUp() throws ConfigurationError, DataAccessError {
		SatellitesRepository satellitesRepository = SatellitesRepositoryFactory.getInstance().createSatellitesRepository();
		satellitesRepository.deleteInstrument(INSTRUMENT_NAME);
		satellitesRepository.deleteSatellite(SATELLITE_NAME);
	}
}
