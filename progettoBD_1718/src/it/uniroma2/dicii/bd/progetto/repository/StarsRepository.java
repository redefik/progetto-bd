package it.uniroma2.dicii.bd.progetto.repository;

import java.util.ArrayList;
import it.uniroma2.dicii.bd.progetto.errorLogic.BatchError;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.star.Star;


public interface StarsRepository {
	void insertAllStars(ArrayList<Star> stars) throws ConfigurationError, DataAccessError, BatchError;
}