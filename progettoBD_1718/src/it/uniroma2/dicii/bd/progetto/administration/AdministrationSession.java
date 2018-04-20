package it.uniroma2.dicii.bd.progetto.administration;

import it.uniroma2.dicii.bd.progetto.errorLogic.BatchError;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.filament.BorderPoint;
import it.uniroma2.dicii.bd.progetto.filament.BorderPointBean;
import it.uniroma2.dicii.bd.progetto.filament.Filament;
import it.uniroma2.dicii.bd.progetto.filament.FilamentBean;
import it.uniroma2.dicii.bd.progetto.repository.FilamentsRepository;
import it.uniroma2.dicii.bd.progetto.repository.FilamentsRepositoryFactory;
import it.uniroma2.dicii.bd.progetto.repository.SatellitesRepository;
import it.uniroma2.dicii.bd.progetto.repository.SatellitesRepositoryFactory;
import it.uniroma2.dicii.bd.progetto.repository.UsersRepository;
import it.uniroma2.dicii.bd.progetto.repository.UsersRepositoryFactory;
import it.uniroma2.dicii.bd.progetto.satellite.Agency;
import it.uniroma2.dicii.bd.progetto.satellite.AgencyBean;
import it.uniroma2.dicii.bd.progetto.satellite.Instrument;
import it.uniroma2.dicii.bd.progetto.satellite.InstrumentBean;
import it.uniroma2.dicii.bd.progetto.satellite.Satellite;
import it.uniroma2.dicii.bd.progetto.satellite.SatelliteBean;
import it.uniroma2.dicii.bd.progetto.satellite.SelectableAgencyBean;
import it.uniroma2.dicii.bd.progetto.user.User;
import it.uniroma2.dicii.bd.progetto.user.UserBean;
import java.util.ArrayList;

// Classe singleton
public class AdministrationSession {
		
		private static AdministrationSession instance;
		
		protected AdministrationSession() {}

	    public synchronized static AdministrationSession getInstance() {
	        if (instance == null) {
	            instance = new AdministrationSession();
	        }
	        return instance;
	    }
	    
	    // Il metodo verifica l'assenza di un utente con lo username specificato
	   public boolean isAvailableUsername(String username) throws ConfigurationError, DataAccessError {
		   
		   UsersRepositoryFactory usersRepositoryFactory = UsersRepositoryFactory.getInstance();
		   UsersRepository usersRepository = usersRepositoryFactory.createUsersRepository();
		   return (!(usersRepository.existsUserWithUsername(username)));
		   
	   }
	   
	   // Il metodo delega a un oggetto userRepository l'inserimento di un nuovo utente 
	   public void registerUser(UserBean userBean) throws ConfigurationError, DataAccessError {
		
		   User user = new User(userBean);
		   UsersRepositoryFactory usersRepositoryFactory = UsersRepositoryFactory.getInstance();
		   UsersRepository usersRepository = usersRepositoryFactory.createUsersRepository();
		   usersRepository.persist(user);
	   }
	   
	   //Il metodo si serve di oggetto satellitesRepository per ottenere la lista di tutte le agenzie spaziali presenti in persistenza
	   public ArrayList<AgencyBean> findAllAgencies() throws ConfigurationError, DataAccessError {
		   
		   SatellitesRepositoryFactory satellitesRepositoryFactory = SatellitesRepositoryFactory.getInstance();
		   SatellitesRepository satellitesRepository = satellitesRepositoryFactory.createSatellitesRepository();
		   
		   ArrayList<Agency> agencies = satellitesRepository.findAllAgencies();
		   
		   ArrayList<AgencyBean> agencyBeans = new ArrayList<>();
		   
		   for (Agency agency : agencies) {
			   agencyBeans.add(new AgencyBean(agency));
		   }
		   
		   return agencyBeans;
	   }

	   // Il metodo delega a un oggetto satellitesRepository l'inserimento di un nuovo satellite 
	   public void registerSatellite(SatelliteBean satelliteBean, ArrayList<SelectableAgencyBean> selectedAgencies) throws ConfigurationError, DataAccessError {
			Satellite satellite = new Satellite(satelliteBean);
			ArrayList<Agency> agencies = new ArrayList<Agency>();
			for (SelectableAgencyBean elem : selectedAgencies) {
				agencies.add(new Agency(elem.getName()));
			}
			
			 SatellitesRepositoryFactory satellitesRepositoryFactory = SatellitesRepositoryFactory.getInstance();
			 SatellitesRepository satellitesRepository = satellitesRepositoryFactory.createSatellitesRepository();
			 satellitesRepository.persistSatellite(satellite, agencies);
		}

	   //Il metodo controlla che il satellite con il nome specificato non sia gia' presente
		public boolean isAvailableName(String name) throws ConfigurationError, DataAccessError {
			SatellitesRepositoryFactory satellitesRepositoryFactory = SatellitesRepositoryFactory.getInstance();
			SatellitesRepository satellitesRepository = satellitesRepositoryFactory.createSatellitesRepository();
			return satellitesRepository.existsSatelliteWithName(name);
		}
	
		//Il metodo si serve di oggetto satellitesRepository per ottenere la lista di tutte i satelliti presenti in persistenza
		public ArrayList<SatelliteBean> findAllSatellites() throws ConfigurationError, DataAccessError {
			
			SatellitesRepositoryFactory satellitesRepositoryFactory = SatellitesRepositoryFactory.getInstance();
			SatellitesRepository satellitesRepository = satellitesRepositoryFactory.createSatellitesRepository();
	
			ArrayList<Satellite> satellites = satellitesRepository.findAllSatellites();
			   
			ArrayList<SatelliteBean> satelliteBeans = new ArrayList<>();
			   
			   for (Satellite satellite : satellites) {
				   satelliteBeans.add(new SatelliteBean(satellite));
			   }
			   
			   return satelliteBeans;
		}
	
		// Il metodo delega a un oggetto satellitesRepository l'inserimento di un nuovo strumento relativo a un satellite 
		public void registerInstrument(SatelliteBean satelliteBean, InstrumentBean instrumentBean) throws ConfigurationError, DataAccessError {
			
			Instrument instrument = new Instrument (instrumentBean);
			SatellitesRepositoryFactory satellitesRepositoryFactory = SatellitesRepositoryFactory.getInstance();
			SatellitesRepository satellitesRepository = satellitesRepositoryFactory.createSatellitesRepository();
			satellitesRepository.persistInstrument(instrument, satelliteBean.getName());
	
		}

		// Il metodo delega a un oggetto filamentsRepository l'inserimento di una lista di filamenti in persistenza
		public void insertFilaments(ArrayList<FilamentBean> filamentBeans) throws ConfigurationError, DataAccessError, BatchError {
			
			ArrayList<Filament> filaments = new ArrayList<Filament>();
			Filament filament;
			
			for (FilamentBean filamentBean : filamentBeans) {
				filament = new Filament(filamentBean);
				filaments.add(filament);
			}
			
			FilamentsRepositoryFactory filamentsRepositoryFactory = FilamentsRepositoryFactory.getInstance();
			FilamentsRepository filamentsRepository = filamentsRepositoryFactory.createFilamentsRepository();
			
			filamentsRepository.insertAllFilaments(filaments);
		}

		public void insertBorderPoints(ArrayList<BorderPointBean> borderPointBeans) throws ConfigurationError, DataAccessError, BatchError {
			
			ArrayList<BorderPoint> borderPoints = new ArrayList<BorderPoint>();
			BorderPoint borderPoint;
			
			for (BorderPointBean borderPointBean : borderPointBeans) {
				borderPoint = new BorderPoint(borderPointBean);
				borderPoints.add(borderPoint);
			}
			
			FilamentsRepositoryFactory filamentsRepositoryFactory = FilamentsRepositoryFactory.getInstance();
			FilamentsRepository filamentsRepository = filamentsRepositoryFactory.createFilamentsRepository();
			
			filamentsRepository.insertAllBorderPoints(borderPoints);
		} 
			    
	    
	    
}
