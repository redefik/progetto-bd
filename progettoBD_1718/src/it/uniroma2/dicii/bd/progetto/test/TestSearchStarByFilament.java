package it.uniroma2.dicii.bd.progetto.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import it.uniroma2.dicii.bd.progetto.errorLogic.BatchError;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.filament.BorderPoint;
import it.uniroma2.dicii.bd.progetto.filament.Filament;
import it.uniroma2.dicii.bd.progetto.repository.FilamentsRepository;
import it.uniroma2.dicii.bd.progetto.repository.FilamentsRepositoryFactory;
import it.uniroma2.dicii.bd.progetto.repository.SatellitesRepository;
import it.uniroma2.dicii.bd.progetto.repository.SatellitesRepositoryFactory;
import it.uniroma2.dicii.bd.progetto.repository.StarsRepository;
import it.uniroma2.dicii.bd.progetto.repository.StarsRepositoryFactory;
import it.uniroma2.dicii.bd.progetto.satellite.Agency;
import it.uniroma2.dicii.bd.progetto.satellite.Instrument;
import it.uniroma2.dicii.bd.progetto.satellite.Satellite;
import it.uniroma2.dicii.bd.progetto.standardUser.StandardUserSession;
import it.uniroma2.dicii.bd.progetto.star.Star;
import it.uniroma2.dicii.bd.progetto.star.StarBean;

public class TestSearchStarByFilament {
	
	/*
	 * Il test prevede l'inserimento di un filamento (costituito da 4 punti del contorno a formare un quadrato) e tre stelle (due interne 
	 * di tipologie diverse e una esterna al filamento). Si effettua la ricerca di stelle per filamento e si verifica che: 
	 * 1) Nel filamento vengono individuate esattamente due stelle
	 * 2) Tutte le stelle individuate sono effettivamente quelle interne al filamento
	 * 3) Le percentuali per ogni tipo di stella sono corrette
	 * L'ambiente viene ripulito dopo il test.                                                                             
	 */
	
	private static ArrayList<BorderPoint> borderPoints;
	
	@BeforeClass
	public static void setUp() throws ConfigurationError, DataAccessError, BatchError {
		
		ArrayList<Filament> filaments = new ArrayList<Filament>();
		filaments.add(new Filament("Filamento", 1, 1, 1, 1, "Strumento"));
		
		ArrayList<String> filamentNames = new ArrayList<String>();
		filamentNames.add("Filamento");
		
		borderPoints = new ArrayList<BorderPoint>();
		borderPoints.add(new BorderPoint(0 , 0, "Satellite", filamentNames));
		borderPoints.add(new BorderPoint(5 , 0, "Satellite", filamentNames));
		borderPoints.add(new BorderPoint(0 , 5, "Satellite", filamentNames));
		borderPoints.add(new BorderPoint(5 , 5, "Satellite", filamentNames));
		
		ArrayList<Star> stars = new ArrayList<Star>();
		stars.add(new Star("StellaInterna1", 1, 3, 3, 1, "Tipologia1", "Satellite" ));
		stars.add(new Star("StellaInterna2", 2, 2, 2, 1, "Tipologia2", "Satellite" ));
		stars.add(new Star("StellaEsterna", 3, 20, 20, 1, "Tipologia3", "Satellite" ));
		
		SatellitesRepository satellitesRepository = SatellitesRepositoryFactory.getInstance().createSatellitesRepository();
		FilamentsRepository filamentsRepository = FilamentsRepositoryFactory.getInstance().createFilamentsRepository();
		StarsRepository starsRepository = StarsRepositoryFactory.getInstance().createStarsRepository();

		ArrayList<Agency> agencies = new ArrayList<Agency>();
		agencies.add(new Agency("Agenzia"));
		satellitesRepository.persistSatellite(new Satellite("Satellite",  new Date(), null), agencies);
		satellitesRepository.persistInstrument(new Instrument("Strumento", ""), "Satellite");
		filamentsRepository.insertAllFilaments(filaments);
		filamentsRepository.insertAllBorderPoints(borderPoints);
		starsRepository.insertAllStars(stars);
	}
			
	@Test
	public void test() throws ConfigurationError, DataAccessError{
		
		StandardUserSession standardUserSession = StandardUserSession.getInstance();
		ArrayList<StarBean> stars = standardUserSession.searchStarsIntoFilament("Filamento");
		Map<String, Double> percentageStarType = standardUserSession.getPercentageStarTypes(stars);
		
		boolean condition1, condition2, condition3;
		
		condition1 = (stars.size() == 2);
		
		condition2 = (stars.get(0).getName().equals("StellaInterna1") && stars.get(1).getName().equals("StellaInterna2")) ||
					 (stars.get(0).getName().equals("StellaInterna2") && stars.get(1).getName().equals("StellaInterna1"));
		
		condition3 = (percentageStarType.get("Tipologia1") == 50.0) && (percentageStarType.get("Tipologia2") == 50.0) && 
					 (!(percentageStarType.containsKey("Tipologia3")));
		
		Assert.assertTrue(condition1 && condition2 && condition3);
		
		
	}
	
	@AfterClass
	public static void cleanUp() throws ConfigurationError, DataAccessError {
		
		SatellitesRepository satellitesRepository = SatellitesRepositoryFactory.getInstance().createSatellitesRepository();
		FilamentsRepository filamentsRepository = FilamentsRepositoryFactory.getInstance().createFilamentsRepository();
		StarsRepository starsRepository = StarsRepositoryFactory.getInstance().createStarsRepository();
		
		starsRepository.deleteStar("StellaInterna1");
		starsRepository.deleteStar("StellaInterna2");
		starsRepository.deleteStar("StellaEsterna");
		filamentsRepository.deleteBorderPoints(borderPoints);
		filamentsRepository.deleteFilamentWithName("Filamento");
		satellitesRepository.deleteInstrument("Strumento");
		satellitesRepository.deleteSatellite("Satellite");
	}
}
