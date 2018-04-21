package it.uniroma2.dicii.bd.progetto.repository;

import java.util.ArrayList;

import it.uniroma2.dicii.bd.progetto.errorLogic.BatchError;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.filament.BorderPoint;
import it.uniroma2.dicii.bd.progetto.filament.Filament;
import it.uniroma2.dicii.bd.progetto.filament.SegmentPointImported;
import it.uniroma2.dicii.bd.progetto.satellite.InstrumentBean;

public interface FilamentsRepository {

	void insertAllFilaments(ArrayList<Filament> filaments) throws ConfigurationError, DataAccessError, BatchError;

	String searchFilamentByIdAndInstruments(int idFilament, ArrayList<InstrumentBean> instrumentBeans) 
			throws ConfigurationError, DataAccessError;

	void insertAllBorderPoints(ArrayList<BorderPoint> borderPoints) throws ConfigurationError, DataAccessError, BatchError;

	void insertAllSegmentPoints(ArrayList<SegmentPointImported> segmentPoints, String selectedSatellite) throws ConfigurationError, DataAccessError, BatchError;


}
