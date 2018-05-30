package it.uniroma2.dicii.bd.progetto.repository;

import java.util.ArrayList;

import it.uniroma2.dicii.bd.progetto.errorLogic.BatchError;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.filament.BorderPoint;
import it.uniroma2.dicii.bd.progetto.filament.BorderPointFilament;
import it.uniroma2.dicii.bd.progetto.filament.Filament;
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

	ArrayList<String> findFilamentsWithBorderPointsInSquare(double left, double right, double down, double up) throws ConfigurationError, DataAccessError;

	ArrayList<SegmentPoint> findSegmentBySatelliteNameAndId(String filamentName, int idSegment) throws DataAccessError, ConfigurationError;

	ArrayList<BorderPointFilament> findFilamentBorder(String filamentName, String satelliteName)
			throws ConfigurationError, DataAccessError;

	Filament findFilamentByBorderPoints(BorderPointFilament filamentWithBorderPoints) throws DataAccessError, ConfigurationError;

	void deleteFilamentWithName(String name) throws ConfigurationError, DataAccessError;

	void deleteBorderPoints(ArrayList<BorderPoint> borderPoints) throws ConfigurationError, DataAccessError;

	void deleteSegmentPoints(ArrayList<SegmentPoint> segmentPoints) throws ConfigurationError, DataAccessError;

	ArrayList<SegmentPoint> getBackBone(Filament filament) throws DataAccessError, ConfigurationError;

	ArrayList<SegmentPoint> findAllSegmentPoints() throws ConfigurationError, DataAccessError;

	ArrayList<String> findFilamentsWithBorderPointsInCircle(double centreX, double centreY, double powRadius)
			throws DataAccessError, ConfigurationError;
}	
