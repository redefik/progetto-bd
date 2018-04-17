package it.uniroma2.dicii.bd.progetto.repository;

import java.util.ArrayList;

import it.uniroma2.dicii.bd.progetto.errorLogic.BatchError;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.filament.Filament;

public interface FilamentsRepository {

	void insertAllFilaments(ArrayList<Filament> filaments) throws ConfigurationError, DataAccessError, BatchError;

}
