package it.uniroma2.dicii.bd.progetto.repository;

import java.util.ArrayList;
import it.uniroma2.dicii.bd.progetto.errorLogic.BatchError;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.star.Star;


public interface StarsRepository {
	void insertAllStars(ArrayList<Star> stars) throws ConfigurationError, DataAccessError, BatchError;

	ArrayList<Star> findAllStars() throws ConfigurationError, DataAccessError;

	ArrayList<Star> findAllStarIntoRegion(double latitude, double longitude, double width, double heigth) throws ConfigurationError, DataAccessError;

	void deleteStar(String starName) throws DataAccessError, ConfigurationError;
}