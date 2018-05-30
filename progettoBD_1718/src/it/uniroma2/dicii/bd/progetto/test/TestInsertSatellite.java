package it.uniroma2.dicii.bd.progetto.test;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import org.junit.AfterClass;
import org.junit.Test;

import it.uniroma2.dicii.bd.progetto.administration.AdministrationSession;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.repository.SatellitesRepository;
import it.uniroma2.dicii.bd.progetto.repository.SatellitesRepositoryFactory;
import it.uniroma2.dicii.bd.progetto.satellite.AgencyBean;
import it.uniroma2.dicii.bd.progetto.satellite.SatelliteBean;

public class TestInsertSatellite {
	
	/*
	 * Il test prevede l'inserimento di due satelliti. Il primo associato a una agenzia effettivamente presente
	 * nello strato di persistenza, il secondo associato a un'agenzia non presente.
	 * Si verifica che nel primo caso (successTest) l'inserimento viene effettuato correttamente mentre nel secondo 
	 * caso (failureTest) viene sollevata un'eccezione che manifesta un problema nell'inserimento.
	 * L'ambiente viene ripulito dal primo satellite dopo l'esecuzione del test.
	 */

	
	@Test
	public void successTest() throws ConfigurationError, DataAccessError {
		
		Date startActivity = new Date();
		SatelliteBean satelliteBean = new SatelliteBean("Satellite", startActivity, null);
		ArrayList<AgencyBean> agencies = new ArrayList<AgencyBean>();
		AgencyBean agency = new AgencyBean("Agenzia");
		agencies.add(agency);
		AdministrationSession.getInstance().registerSatellite(satelliteBean, agencies);
		
		SatellitesRepository satellitesRepository = SatellitesRepositoryFactory.getInstance().createSatellitesRepository();
		assertTrue(satellitesRepository.existsSatelliteWithName("Satellite"));
	}
	
	@Test (expected = DataAccessError.class)
	public void failureTest() throws ConfigurationError, DataAccessError {
		
		SatelliteBean satelliteBean = new SatelliteBean("SatelliteDaNonInserire", new Date(), null);
		ArrayList<AgencyBean> agencies = new ArrayList<AgencyBean>();
		AgencyBean agency = new AgencyBean("AgenziaNonEsistente");
		agencies.add(agency);
		AdministrationSession.getInstance().registerSatellite(satelliteBean, agencies);
	}
	
	@AfterClass
	public static void cleanUp() throws DataAccessError, ConfigurationError {
		SatellitesRepository satellitesRepository = SatellitesRepositoryFactory.getInstance().createSatellitesRepository();
		satellitesRepository.deleteSatellite("Satellite");
	}
}
