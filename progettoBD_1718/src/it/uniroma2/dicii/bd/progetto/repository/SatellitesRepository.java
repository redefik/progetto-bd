package it.uniroma2.dicii.bd.progetto.repository;

import java.util.ArrayList;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.satellite.Agency;
import it.uniroma2.dicii.bd.progetto.satellite.Instrument;
import it.uniroma2.dicii.bd.progetto.satellite.Satellite;

public interface SatellitesRepository {

	ArrayList<Agency> findAllAgencies() throws ConfigurationError, DataAccessError;

	void persistSatellite(Satellite satellite, ArrayList<Agency> agencies) throws ConfigurationError, DataAccessError;

	boolean existsSatelliteWithName(String name) throws ConfigurationError, DataAccessError;

	ArrayList<Satellite> findAllSatellites() throws ConfigurationError, DataAccessError;
	
	ArrayList<Instrument> findAllInstrumenOfSatellite(Satellite satellite) throws ConfigurationError, DataAccessError;

	void persistInstrument(Instrument instrument, String satellite) throws ConfigurationError, DataAccessError;

	ArrayList<Instrument> findAllInstruments() throws ConfigurationError, DataAccessError;
	
}
