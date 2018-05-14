package it.uniroma2.dicii.bd.progetto.repository;

import java.util.ArrayList;

import it.uniroma2.dicii.bd.progetto.errorLogic.BatchError;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.filament.BorderPoint;
import it.uniroma2.dicii.bd.progetto.filament.BorderPointFilament;
import it.uniroma2.dicii.bd.progetto.filament.Filament;
import it.uniroma2.dicii.bd.progetto.filament.FilamentWithBorderPoints;
import it.uniroma2.dicii.bd.progetto.filament.SegmentPoint;
import it.uniroma2.dicii.bd.progetto.filament.SegmentPointImported;
import it.uniroma2.dicii.bd.progetto.satellite.InstrumentBean;

public interface FilamentsRepository {

	void insertAllFilaments(ArrayList<Filament> filaments) throws ConfigurationError, DataAccessError, BatchError;

	String searchFilamentByIdAndInstruments(int idFilament, ArrayList<InstrumentBean> instrumentBeans) 
			throws ConfigurationError, DataAccessError;

	void insertAllBorderPoints(ArrayList<BorderPoint> borderPoints) throws ConfigurationError, DataAccessError, BatchError;

	void insertAllSegmentPoints(ArrayList<SegmentPointImported> segmentPoints, String selectedSatellite) throws ConfigurationError, DataAccessError, BatchError;

	boolean existFilamentWithName(String name) throws ConfigurationError, DataAccessError;

	ArrayList<BorderPoint> findBorder(String filament) throws ConfigurationError, DataAccessError;

	ArrayList<String> findAllFilamentPartiallyIntoRegion(double latitude, double longitude, double width, double heigth) throws ConfigurationError, DataAccessError;

	Filament findFilamentByName(String name) throws ConfigurationError, DataAccessError;

	Filament findFilamentByIdAndInstrument(int filamentId, String instrumentName)
			throws ConfigurationError, DataAccessError;

	ArrayList<BorderPointFilament> findBorderPointsOfFilament(Filament filament)
			throws ConfigurationError, DataAccessError;

	long getFilamentsCount() throws ConfigurationError, DataAccessError;

	ArrayList<Filament> findFilamentsByContrastAndEllipticity(double minContrast, double minEllipticity,
			double maxEllipticity) throws ConfigurationError, DataAccessError;

	ArrayList<Filament> findFilamentByNumOfSegments(int minNum, int maxNum) throws ConfigurationError, DataAccessError;

	ArrayList<FilamentWithBorderPoints> findFilamentsWithBorderPointsInSquare(double x0, double x1, double y0,
			double y1) throws ConfigurationError, DataAccessError;

	Filament findFilament(FilamentWithBorderPoints filamentWithBorderPoints) throws DataAccessError, ConfigurationError;

	ArrayList<SegmentPoint> findSegment(String filamentName, int idSegment) throws DataAccessError, ConfigurationError;

	ArrayList<FilamentWithBorderPoints> findBorder(String filamentName, String satelliteName)
			throws ConfigurationError, DataAccessError;


}
