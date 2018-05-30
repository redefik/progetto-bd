package it.uniroma2.dicii.bd.progetto.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import it.uniroma2.dicii.bd.progetto.administration.AdministrationSession;
import it.uniroma2.dicii.bd.progetto.administration.CSVFileParser;
import it.uniroma2.dicii.bd.progetto.administration.CSVFileParserFactory;
import it.uniroma2.dicii.bd.progetto.errorLogic.BatchError;
import it.uniroma2.dicii.bd.progetto.errorLogic.CSVFileParserException;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.filament.BorderPoint;
import it.uniroma2.dicii.bd.progetto.filament.BorderPointBean;
import it.uniroma2.dicii.bd.progetto.filament.Filament;
import it.uniroma2.dicii.bd.progetto.repository.FilamentsRepository;
import it.uniroma2.dicii.bd.progetto.repository.FilamentsRepositoryFactory;
import it.uniroma2.dicii.bd.progetto.repository.SatellitesRepository;
import it.uniroma2.dicii.bd.progetto.repository.SatellitesRepositoryFactory;
import it.uniroma2.dicii.bd.progetto.satellite.Agency;
import it.uniroma2.dicii.bd.progetto.satellite.Instrument;
import it.uniroma2.dicii.bd.progetto.satellite.InstrumentBean;
import it.uniroma2.dicii.bd.progetto.satellite.Satellite;
import it.uniroma2.dicii.bd.progetto.satellite.SatelliteBean;

public class TestBorderPointImport {
		
		/*
		 * Il successtest prevede che venga inserito all'interno del db un satellite e due filamenti. Successivamete si effettua
		 * l'import di un file relativo a punti del contorno. Il file contiene 4 punti:
		 * - (1,1) relativo a filamento1
		 * - (1,1) relativo a filamento2
		 * - (2,2) relativo a filamento2
		 * - (5,5) relativo a filamento3 (non esistente)
		 * Si effettua la ricerca del contorno i filamento1 e filamento2 e si verifica che:
		 * - Il contorno di filamento1 sia costituito soltanto dal punto (1,1)
		 * - Il contorno del filamento2 sia costituito soltanto dai punti (1,1) e (2,2)
		 * Il failuretest prevede che venga inserito all'interno del db un file contenente un punto con errore.
		 * Si verifica che venga sollevata un'opportuna eccezione in fase di parsing del file.
		 * L'ambiente viene ripulito dopo i test.
		 * */
		
		@BeforeClass
		public static void setUp() throws ConfigurationError, DataAccessError, BatchError {
			
			ArrayList<Agency> agencies = new ArrayList<Agency>();
			agencies.add(new Agency("Agenzia"));
			SatellitesRepository satellitesRepository = SatellitesRepositoryFactory.getInstance().createSatellitesRepository();
			satellitesRepository.persistSatellite(new Satellite("Satellite",  new Date(), null), agencies);
			satellitesRepository.persistInstrument(new Instrument("Strumento", ""), "Satellite");
			
			ArrayList<Filament> filaments = new ArrayList<Filament>();
			filaments.add(new Filament("Filamento1", 1, 1, 1, 1, "Strumento"));
			filaments.add(new Filament("Filamento2", 2, 1, 1, 1, "Strumento"));
			FilamentsRepository filamentsRepository = FilamentsRepositoryFactory.getInstance().createFilamentsRepository();
			filamentsRepository.insertAllFilaments(filaments);
		}
		
		@Test
		public void testSuccess() throws ConfigurationError, CSVFileParserException, DataAccessError, BatchError {
			CSVFileParser parser = CSVFileParserFactory.getInstance().createCSVFileParser();
			
			File file = new File(this.getClass().getResource("testImportBorderPoint.csv").getFile());
			InstrumentBean instrumentBean = new InstrumentBean("Strumento", "");
			ArrayList<InstrumentBean> instrumentBeans = new ArrayList<InstrumentBean>();
			instrumentBeans.add(instrumentBean);
			SatelliteBean satelliteBean = new SatelliteBean("Satellite", null, null);
			satelliteBean.setInstrumentBeans(instrumentBeans);
			
			ArrayList<BorderPointBean> borderPointBeans = parser.getBorderPointBeans(file, satelliteBean);
			AdministrationSession.getInstance().insertBorderPoints(borderPointBeans);
			
			FilamentsRepository filamentsRepository = FilamentsRepositoryFactory.getInstance().createFilamentsRepository();
			ArrayList<BorderPoint> borderFilament1 = filamentsRepository.findBorder("Filamento1");
			ArrayList<BorderPoint> borderFilament2 = filamentsRepository.findBorder("Filamento2");

			
			boolean condition1, condition2;
			
			condition1 = (borderFilament1.size() == 1) && (borderFilament1.get(0).getLatitude() == 1) && 
						 (borderFilament1.get(0).getLongitude() == 1);
			
			condition2 = (borderFilament2.size() == 2) && (
						  (((borderFilament2.get(0).getLatitude() == 1) && (borderFilament2.get(0).getLongitude() == 1)) && 
						   ((borderFilament2.get(1).getLatitude() == 2) && (borderFilament2.get(1).getLongitude() == 2))) || 
						  (((borderFilament2.get(0).getLatitude() == 2) && (borderFilament2.get(0).getLongitude() == 2)) && 
						   ((borderFilament2.get(1).getLatitude() == 1) && (borderFilament2.get(1).getLongitude() == 1))));
			
			assertTrue(condition1 && condition2);
		}
		
		@Test (expected = CSVFileParserException.class)
		public void failureTest() throws ConfigurationError, CSVFileParserException, DataAccessError, BatchError {
			CSVFileParser parser = CSVFileParserFactory.getInstance().createCSVFileParser();
			
			File file = new File(this.getClass().getResource("testFailureImportBorderPoint.csv").getFile());
			InstrumentBean instrumentBean = new InstrumentBean("Strumento", "");
			ArrayList<InstrumentBean> instrumentBeans = new ArrayList<InstrumentBean>();
			instrumentBeans.add(instrumentBean);
			SatelliteBean satelliteBean = new SatelliteBean("Satellite", null, null);
			satelliteBean.setInstrumentBeans(instrumentBeans);
			
			ArrayList<BorderPointBean> borderPointBeans = parser.getBorderPointBeans(file, satelliteBean);
			AdministrationSession.getInstance().insertBorderPoints(borderPointBeans);
		}
		
		@AfterClass
		public static void cleanUp() throws DataAccessError, ConfigurationError {
			
			FilamentsRepository filamentsRepository = FilamentsRepositoryFactory.getInstance().createFilamentsRepository();
			ArrayList<BorderPoint> borderPoints = new ArrayList<BorderPoint>();
			ArrayList<String> filamentName11 = new ArrayList<String>();
			filamentName11.add("Filamento1");
			filamentName11.add("Filamento2");
			ArrayList<String> filamentName22 = new ArrayList<String>();
		    filamentName22.add("Filamento2");
			borderPoints.add(new BorderPoint(1,1,"Satellite", filamentName11));
			borderPoints.add(new BorderPoint(2,2,"Satellite", filamentName22));
			filamentsRepository.deleteBorderPoints(borderPoints);
			filamentsRepository.deleteFilamentWithName("Filamento1");
			filamentsRepository.deleteFilamentWithName("Filamento2");
			SatellitesRepository satellitesRepository = SatellitesRepositoryFactory.getInstance().createSatellitesRepository();
			satellitesRepository.deleteInstrument("Strumento");
			satellitesRepository.deleteSatellite("Satellite");
		}
	
}
