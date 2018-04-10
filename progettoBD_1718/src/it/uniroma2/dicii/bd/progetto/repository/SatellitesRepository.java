package it.uniroma2.dicii.bd.progetto.repository;

import java.util.ArrayList;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.satellite.Agency;

public interface SatellitesRepository {

	ArrayList<Agency> findAllAgencies() throws ConfigurationError, DataAccessError;
	
}
