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
import it.uniroma2.dicii.bd.progetto.star.StarsIntoRegion;

public class TestSearchStarByRegion {
	
	/*
	 * Il test prevede l'inserimento all'interno dello strato di persistenza di:
	 * - 3 filamenti a perimetro quadrato (uno totalmente interno alla regione di ricerca, uno parzialmente interno e uno esterno)
	 * - 3 stelle interne alla regione di ricerca (due interne ai filamenti di tipo diverso, e una esterna di una terza tipologia)
	 * - 1 stella esterna alla regione di ricerca
	 * Si effettua la ricerca di stelle per regione e si verifica che:
	 * 1) Vengono individuate all'interno della regione esattamente tre stelle, due interne ai filamenti e una esterna.
	 * 2) Le stelle interne alla regione e ai filamenti devono essere "StellaInternaFilamentoInterno" e 
	 *    "StellaInternaFilamentoSemiInterno".
	 * 3) La stella esterna alla regione deve essere necessariamente "StellaInternaNessunFilamento".
	 * 4) La percentuale per le stelle interne deve essere del 50% per la tipologia 1 e 2 e 0% per la tipologia 3
	 * 5) La percentuale per le stelle esterne deve essere il 100% per la tipologia 3 e 0% per la tipologia 1 e 2	 
	 */
	
	private static ArrayList<BorderPoint> contornoFilamentoInterno;
	private static ArrayList<BorderPoint> contornoFilamentoSemiInterno;
	private static ArrayList<BorderPoint> contornoFilamentoEsterno;
	
	@BeforeClass
	public static void setUp() throws ConfigurationError, DataAccessError, BatchError {
		
		ArrayList<Filament> filaments = new ArrayList<Filament>();
		filaments.add(new Filament("FilamentoInterno", 1, 1, 1, 1, "Strumento"));
		filaments.add(new Filament("FilamentoSemiInterno", 2, 1, 1, 1, "Strumento"));
		filaments.add(new Filament("FilamentoEsterno", 3, 1, 1, 1, "Strumento"));
		
		
		contornoFilamentoInterno = new ArrayList<BorderPoint>();
		contornoFilamentoSemiInterno = new ArrayList<BorderPoint>();
		contornoFilamentoEsterno = new ArrayList<BorderPoint>();
		
		ArrayList<String> internalFilamentName = new ArrayList<String>();
		internalFilamentName.add("FilamentoInterno");
		ArrayList<String> semiInternalFilamentName = new ArrayList<String>();
		semiInternalFilamentName.add("FilamentoSemiInterno");
		ArrayList<String> externalFilamentName = new ArrayList<String>();
		externalFilamentName.add("FilamentoEsterno");
		
		
		contornoFilamentoInterno.add(new BorderPoint(1 , 1, "Satellite", internalFilamentName));
		contornoFilamentoInterno.add(new BorderPoint(1 , 6, "Satellite", internalFilamentName));
		contornoFilamentoInterno.add(new BorderPoint(6 , 1, "Satellite", internalFilamentName));
		contornoFilamentoInterno.add(new BorderPoint(6 , 6, "Satellite", internalFilamentName));
		
		contornoFilamentoSemiInterno.add(new BorderPoint(4 , 196, "Satellite", semiInternalFilamentName));
		contornoFilamentoSemiInterno.add(new BorderPoint(5 , 201, "Satellite", semiInternalFilamentName));
		contornoFilamentoSemiInterno.add(new BorderPoint(-1 , 196, "Satellite", semiInternalFilamentName));
		contornoFilamentoSemiInterno.add(new BorderPoint(-1 , 201, "Satellite", semiInternalFilamentName));

		contornoFilamentoEsterno.add(new BorderPoint(101 , 0, "Satellite", externalFilamentName));
		contornoFilamentoEsterno.add(new BorderPoint(101 , 5, "Satellite", externalFilamentName));
		contornoFilamentoEsterno.add(new BorderPoint(106 , 0, "Satellite", externalFilamentName));
		contornoFilamentoEsterno.add(new BorderPoint(106 , 5, "Satellite", externalFilamentName));

		
		ArrayList<Star> stars = new ArrayList<Star>();
		stars.add(new Star("StellaInternaFilamentoInterno", 1, 2, 2, 1, "Tipologia1", "Satellite" ));
		stars.add(new Star("StellaInternaFilamentoSemiInterno", 2, 1, 199, 1, "Tipologia2", "Satellite" ));
		stars.add(new Star("StellaInternaNessunFilamento", 3, 90, 110, 1, "Tipologia3", "Satellite" ));
		stars.add(new Star("StellaEsterna", 4, 110, 110, 1, "Tipologia1", "Satellite" ));
		
		SatellitesRepository satellitesRepository = SatellitesRepositoryFactory.getInstance().createSatellitesRepository();
		FilamentsRepository filamentsRepository = FilamentsRepositoryFactory.getInstance().createFilamentsRepository();
		StarsRepository starsRepository = StarsRepositoryFactory.getInstance().createStarsRepository();

		ArrayList<Agency> agencies = new ArrayList<Agency>();
		agencies.add(new Agency("Agenzia"));
		satellitesRepository.persistSatellite(new Satellite("Satellite",  new Date(), null), agencies);
		satellitesRepository.persistInstrument(new Instrument("Strumento", ""), "Satellite");
		filamentsRepository.insertAllFilaments(filaments);
		filamentsRepository.insertAllBorderPoints(contornoFilamentoInterno);
		filamentsRepository.insertAllBorderPoints(contornoFilamentoSemiInterno);
		filamentsRepository.insertAllBorderPoints(contornoFilamentoEsterno);
		starsRepository.insertAllStars(stars);
	}
			
	@Test
	public void test() throws ConfigurationError, DataAccessError{
		
		StandardUserSession standardUserSession = StandardUserSession.getInstance();
		StarsIntoRegion starsIntoRegion = standardUserSession.searchStarsIntoRegion(50, 100, 200, 100);
		
		Map<String, Double> percentageInternalStarType, percentageExternalStarType;
		percentageInternalStarType = standardUserSession.getPercentageStarTypes(starsIntoRegion.getInternalStars());
		percentageExternalStarType = standardUserSession.getPercentageStarTypes(starsIntoRegion.getExternalStars());
		
		boolean condition1, condition2, condition3, condition4, condition5;
		
		condition1 = ((starsIntoRegion.getInternalStars().size() == 2) && (starsIntoRegion.getExternalStars().size() == 1));
		
		StarBean internalStar1 = starsIntoRegion.getInternalStars().get(0);
		StarBean internalStar2 = starsIntoRegion.getInternalStars().get(1);
		
		condition2 = ((internalStar1.getName().equals("StellaInternaFilamentoInterno") &&
					   internalStar2.getName().equals("StellaInternaFilamentoSemiInterno")) || 
					  (internalStar1.getName().equals("StellaInternaFilamentoSemiInterno") &&
					   internalStar2.getName().equals("StellaInternaFilamentoInterno")));
		
		condition3 = (starsIntoRegion.getExternalStars().get(0).getName().equals("StellaInternaNessunFilamento"));
		
		condition4 = ((percentageInternalStarType.get("Tipologia1") == 50.0) && 
				      (percentageInternalStarType.get("Tipologia2") == 50.0) && 
				      (!(percentageInternalStarType.containsKey("Tipologia3"))));
		
		condition5 = ((!(percentageExternalStarType.containsKey("Tipologia1"))) &&
					  (!(percentageExternalStarType.containsKey("Tipologia2"))) &&
					  (percentageExternalStarType.get("Tipologia3") == 100.0));
		
		Assert.assertTrue(condition1 && condition2 && condition3 && condition4 && condition5);
		
		
	}
	
	@AfterClass
	public static void cleanUp() throws ConfigurationError, DataAccessError {
		
		SatellitesRepository satellitesRepository = SatellitesRepositoryFactory.getInstance().createSatellitesRepository();
		FilamentsRepository filamentsRepository = FilamentsRepositoryFactory.getInstance().createFilamentsRepository();
		StarsRepository starsRepository = StarsRepositoryFactory.getInstance().createStarsRepository();
		
		starsRepository.deleteStar("StellaInternaFilamentoInterno");
		starsRepository.deleteStar("StellaInternaFilamentoSemiInterno");
		starsRepository.deleteStar("StellaInternaNessunFilamento");
		starsRepository.deleteStar("StellaEsterna");
		filamentsRepository.deleteBorderPoints(contornoFilamentoInterno);
		filamentsRepository.deleteBorderPoints(contornoFilamentoSemiInterno);
		filamentsRepository.deleteBorderPoints(contornoFilamentoEsterno);
		filamentsRepository.deleteFilamentWithName("FilamentoInterno");
		filamentsRepository.deleteFilamentWithName("FilamentoSemiInterno");
		filamentsRepository.deleteFilamentWithName("FilamentoEsterno");
		satellitesRepository.deleteInstrument("Strumento");
		satellitesRepository.deleteSatellite("Satellite");
	}
}
