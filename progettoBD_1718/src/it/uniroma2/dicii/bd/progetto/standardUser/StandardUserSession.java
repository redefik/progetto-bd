package it.uniroma2.dicii.bd.progetto.standardUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.lang.Math;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.errorLogic.FilamentWithoutStarsError;
import it.uniroma2.dicii.bd.progetto.errorLogic.InvalidBrightnessError;
import it.uniroma2.dicii.bd.progetto.errorLogic.InvalidEllipticityError;
import it.uniroma2.dicii.bd.progetto.errorLogic.InvalidNumOfSegmentsError;
import it.uniroma2.dicii.bd.progetto.errorLogic.NotFoundBackBoneError;
import it.uniroma2.dicii.bd.progetto.errorLogic.NotFoundBorderError;
import it.uniroma2.dicii.bd.progetto.errorLogic.NotFoundFilamentError;
import it.uniroma2.dicii.bd.progetto.errorLogic.NotFoundSegmentPointError;
import it.uniroma2.dicii.bd.progetto.filament.BorderPoint;
import it.uniroma2.dicii.bd.progetto.filament.BorderPointFilament;
import it.uniroma2.dicii.bd.progetto.filament.ContrastEllipticityResearchResult;
import it.uniroma2.dicii.bd.progetto.filament.Filament;
import it.uniroma2.dicii.bd.progetto.filament.FilamentBean;
import it.uniroma2.dicii.bd.progetto.filament.FilamentInfo;
import it.uniroma2.dicii.bd.progetto.filament.SegmentPoint;
import it.uniroma2.dicii.bd.progetto.repository.FilamentsRepository;
import it.uniroma2.dicii.bd.progetto.repository.FilamentsRepositoryFactory;
import it.uniroma2.dicii.bd.progetto.repository.SatellitesRepository;
import it.uniroma2.dicii.bd.progetto.repository.SatellitesRepositoryFactory;
import it.uniroma2.dicii.bd.progetto.repository.StarsRepository;
import it.uniroma2.dicii.bd.progetto.repository.StarsRepositoryFactory;
import it.uniroma2.dicii.bd.progetto.satellite.Instrument;
import it.uniroma2.dicii.bd.progetto.satellite.InstrumentBean;
import it.uniroma2.dicii.bd.progetto.star.Star;
import it.uniroma2.dicii.bd.progetto.star.StarBean;
import it.uniroma2.dicii.bd.progetto.star.StarBeanWithMinDistance;
import it.uniroma2.dicii.bd.progetto.star.StarsIntoRegion;

public class StandardUserSession {
	
	private static StandardUserSession instance;
	
	protected StandardUserSession() {}

    public synchronized static StandardUserSession getInstance() {
        if (instance == null) {
            instance = new StandardUserSession();
        }
        return instance;
    }
    
//_______________________________________________________________________________________________________
    
    //La funzione ritorna true se il filamento che prende come parametro esiste in persistenza, false altrimenti.
	public boolean isValidFilamentName(String filament) throws ConfigurationError, DataAccessError {
		
		FilamentsRepositoryFactory filamentsRepositoryFactory = FilamentsRepositoryFactory.getInstance();
		FilamentsRepository filamentsRepository = filamentsRepositoryFactory.createFilamentsRepository();
		
		return filamentsRepository.existFilamentWithName(filament);
	}
	
	//La funzione ritorna vero se la stella è contenuta nel contorno del filamento passato come argomento
	public boolean isStarInFilament(Star star, ArrayList<BorderPoint> border) throws ConfigurationError, DataAccessError {

		
		int borderSize = border.size();
		
		double Slong, Slat, BPlong1, BPlong2, BPlat1, BPlat2;
		double result;
		
		result = 0;
		Slong = star.getLongitude();
		Slat = star.getLatitude();
		
		for ( int i = 0; i < borderSize - 1; i++ ) {
			BPlong1 = border.get(i).getLongitude();
			BPlong2 = border.get(i+1).getLongitude();
			BPlat1 = border.get(i).getLatitude();
			BPlat2 = border.get(i+1).getLatitude();
			
			result += Math.atan(((((BPlong1 - Slong)*(BPlat2 - Slat))-((BPlat1 - Slat)*(BPlong2 - Slong)))/
								 (((BPlong1 - Slong)*(BPlong2 - Slong))+((BPlat1 - Slat)*(BPlat2 - Slat)))));
		}
	
		
		result = Math.toRadians(result);
		result = Math.abs(result);
		
		if (result >= 0.01) {
			return true;
		}else {
			return false;
		}
	}


	
	//La funziona ritorna una lista di StarBean contenute all'interno del filamento specificato
	public ArrayList<StarBean> searchStarsIntoFilament(String filament) throws ConfigurationError, DataAccessError {
		
		//Tramite un oggetto StarsRepository si ottengono tutte le stelle presenti in persistenza
		StarsRepositoryFactory starsRepositoryFactory = StarsRepositoryFactory.getInstance();
		StarsRepository starsRepository = starsRepositoryFactory.createStarsRepository();
		
		ArrayList<Star> stars = starsRepository.findAllStars();
		
		//Tramite un oggetto FilamentRepository si ottiene l'insieme dei punti che costituiscono il contorno del filamento
		FilamentsRepositoryFactory filamentsRepositoryFactory = FilamentsRepositoryFactory.getInstance();
		FilamentsRepository filamentsRepository = filamentsRepositoryFactory.createFilamentsRepository();

		ArrayList<BorderPoint> border = filamentsRepository.findBorder(filament);
		
		ArrayList<StarBean> starBeans = new ArrayList<StarBean>();
		
		//Per ogni stella, si verifica l'appartenenza al filamento, se la stella appartiene al filamento viene inserita
		//nella lista che sarà ritornata dalla funzione
		for (Star star : stars) {
			
			if (isStarInFilament(star, border)) {
				starBeans.add(new StarBean(star));
			}
		}

		return starBeans;

	}

	//La funzione calcola la percentuale per ogni tipo di stella contenuta all'interno di una lista di StarBean
	public Map<String, Double> getPercentageStarTypes(ArrayList<StarBean> starBeans) {
		
		
		int starNumber = starBeans.size();
		Map<String, Double> percentageStarTypes = new HashMap<String, Double>();
		
		if (starNumber == 0) {
			return percentageStarTypes;
		}

		
		//Per ogni classificazione di stelle si contano il numero di stelle di quel tipo
		for (StarBean starBean : starBeans) {
			
			String classification = starBean.getClassification();
			
			if (percentageStarTypes.containsKey(classification)) {
				percentageStarTypes.put(classification, percentageStarTypes.get(classification) + 1.0);
			} else {
				percentageStarTypes.put(classification, 1.0);
			}
		}
		
		//Per ogni classificazione di stelle si calcola la percentuale di stelle di quel tipo
		for (String key : percentageStarTypes.keySet()) {
			percentageStarTypes.put(key, ((percentageStarTypes.get(key) * 100.0) / starNumber));
		}
		
		return percentageStarTypes;
	}
	
	
	//La funzione ritorna un oggetto di tipo StarsIntoRegion costituito da due campi: una lista di stelle nella regione
	//esterne ai filamenti e una lista di stelle interne alla regione e ad almeno un filamento.
	public StarsIntoRegion searchStarsIntoRegion(double latitude, double longitude, double width, double heigth) 
			throws ConfigurationError, DataAccessError {

		StarsIntoRegion starsIntoRegion = new StarsIntoRegion(new ArrayList<StarBean>(), new ArrayList<StarBean>());
		
		//Tramite un oggetto StarsRepository si ottengono tutte le stelle presenti in persistenza contenute nella regione
		StarsRepositoryFactory starsRepositoryFactory = StarsRepositoryFactory.getInstance();
		StarsRepository starsRepository = starsRepositoryFactory.createStarsRepository();
		
		ArrayList<Star> stars = starsRepository.findAllStarIntoRegion(latitude, longitude, width, heigth);
		
		
		//Tramite un oggetto FilamentRepository si ottiene l'insieme dei filamenti aventi almeno un punto del contorno 
		//interno alla regione specificata.
		FilamentsRepositoryFactory filamentsRepositoryFactory = FilamentsRepositoryFactory.getInstance();
		FilamentsRepository filamentsRepository = filamentsRepositoryFactory.createFilamentsRepository();
		
		ArrayList<String> filaments = filamentsRepository.findAllFilamentPartiallyIntoRegion(latitude, longitude, width, heigth);
	
		
		Map<String, ArrayList<BorderPoint>> filamentToBorder = new HashMap<String, ArrayList<BorderPoint>>();
		ArrayList<BorderPoint> border;
		
		for (Star star : stars) {
			
				boolean isInternal = false;
		
				for (String filament : filaments) {
					
					//Se il contorno del filamento è già stato calcolato si preleva dall'hashmap
					if (filamentToBorder.containsKey(filament)) {
						border = filamentToBorder.get(filament);
					//Altrimenti si calcola e si inserisce nell'hashmap
					} else {
						border = filamentsRepository.findBorder(filament);
						filamentToBorder.put(filament, border);
					}
					
					//Si verifica se la stella è contenuta nel filamento. In caso affermativo si inserisce la stella 
					//nell'elenco di stelle interne ai filamenti e si passa alla prossima stella. In caso negativo 
					//si ripete la verifica per il filamento successivo.
					if (isStarInFilament(star, border)) {
						starsIntoRegion.getInternalStars().add(new StarBean(star));
						isInternal = true;
						break;
					}
				}
				
				if (isInternal == false) {
					//Se la stella non è interna ad alcun filamento allora si inserisce nell'elenco di stelle esterne
					starsIntoRegion.getExternalStars().add(new StarBean(star));
				}
			}

		return starsIntoRegion;
	}
	
//_______________________________________________________________________________________________________
	
		public ArrayList<InstrumentBean> findAllInstruments() throws ConfigurationError, DataAccessError {
			
			SatellitesRepositoryFactory satellitesRepositoryFactory = SatellitesRepositoryFactory.getInstance();
			SatellitesRepository satellitesRepository = satellitesRepositoryFactory.createSatellitesRepository();
			ArrayList<Instrument> instruments = satellitesRepository.findAllInstruments();
			ArrayList<InstrumentBean> instrumentBeans = new ArrayList<>();
			for (Instrument instrument : instruments) {
				InstrumentBean instrumentBean = new InstrumentBean(instrument);
				instrumentBeans.add(instrumentBean);
			}
			return instrumentBeans;
		}
		
		// il metodo delega ad un filamentRepository la ricerca di un filamento avente un certo nome
		public FilamentBean findFilamentByName(String name) throws ConfigurationError, DataAccessError {
			FilamentsRepositoryFactory filamentsRepositoryFactory = FilamentsRepositoryFactory.getInstance();
			FilamentsRepository filamentsRepository = filamentsRepositoryFactory.createFilamentsRepository();
			Filament filament = filamentsRepository.findFilamentByName(name);
			if (filament != null) {
				return new FilamentBean(filament);
			} else {
				return null;
			}
		}
		
		// il metodo delega ad un filamentRepository la ricerca di un filamento avente un dato id e misurato da un dato strumento
		public FilamentBean findFilamentByIdAndInstrument(int filamentId, String instrumentName) 
				throws ConfigurationError, DataAccessError {
			FilamentsRepositoryFactory filamentsRepositoryFactory = FilamentsRepositoryFactory.getInstance();
			FilamentsRepository filamentsRepository = filamentsRepositoryFactory.createFilamentsRepository();
			Filament filament = filamentsRepository.findFilamentByIdAndInstrument(filamentId, instrumentName);
			if (filament != null) {
				return new FilamentBean(filament);
			} else {
				return null;
			}
		}
		
		public FilamentInfo getFilamentInfo(FilamentBean filamentBean) throws ConfigurationError, DataAccessError {
			FilamentsRepositoryFactory filamentsRepositoryFactory = FilamentsRepositoryFactory.getInstance();
			FilamentsRepository filamentsRepository = filamentsRepositoryFactory.createFilamentsRepository();
			Filament filament = new Filament(filamentBean);
			// innanzitutto, si ricavano i punti del contorno del filamento
			ArrayList<BorderPointFilament> borderPoints = filamentsRepository.findBorderPointsOfFilament(filament);
			// se il filamento non è presente oppure non sono memorizzati i suoi punti del contorno si ritorna null
			if (borderPoints.isEmpty()) {
				return null;
			}
			// se la ricerca ha avuto buon fine, vengono calcolate le informazioni derivate e restituito l'oggetto che le incapsula
			FilamentInfo filamentInfo = new FilamentInfo();
			filamentInfo.setName(filamentBean.getName());
			filamentInfo.setNumberOfSegments(filamentBean.getNumberOfSegments());
			filamentInfo.setCentroid(borderPoints);
			filamentInfo.setBorderLength(borderPoints);
			return filamentInfo;
		}
		
		public ContrastEllipticityResearchResult findFilamentsByContrastAndEllipticity
		(double minBrightness, double minEllipticity, double maxEllipticity) 
				throws InvalidBrightnessError, InvalidEllipticityError, ConfigurationError, DataAccessError {
			// il session realizza i controlli semantici sulle specifiche di ricerca fornite dall'utente
			// in caso di valore non valido viene sollevata un'eccezione ad hoc
			if (minBrightness < 0) {
				throw new InvalidBrightnessError();
			}
			if (minEllipticity <= 1 || minEllipticity > maxEllipticity || maxEllipticity >= 10) {
				throw new InvalidEllipticityError();
			}
			FilamentsRepositoryFactory filamentsRepositoryFactory = FilamentsRepositoryFactory.getInstance();
			FilamentsRepository filamentsRepository = filamentsRepositoryFactory.createFilamentsRepository();
			// si richiede il numero totale di filamenti nel catalogo all'oggetto FilamentsRepository
			long numOfFilaments = filamentsRepository.getFilamentsCount();
			// si richiede la lista dei filamenti compatibili con le richieste dell'utente all'oggetto FilamentsRepository
			double minContrast = 1 + minBrightness/100;
			ArrayList<Filament> suitableFilaments = filamentsRepository.findFilamentsByContrastAndEllipticity(minContrast, minEllipticity, maxEllipticity);
			ArrayList<FilamentBean> suitableFilamentsBean = new ArrayList<>();
			for (Filament filament : suitableFilaments) {
				FilamentBean filamentBean = new FilamentBean(filament);
				suitableFilamentsBean.add(filamentBean);
			}
			// si ritorna al controller l'oggetto che incapsula il risultato della ricerca
			return new ContrastEllipticityResearchResult(numOfFilaments, minBrightness, minEllipticity, maxEllipticity, suitableFilamentsBean);
		}
		
		public ArrayList<FilamentBean> findFilamentsByNumOfSegments(int minNum, int maxNum) 
				throws InvalidNumOfSegmentsError, ConfigurationError, DataAccessError {
			// il metodo effettua innanzitutto un controllo semantico sugli argomenti interi sollevando un'eccezione ad hoc nel caso in cui
			// il controllo fallisca.
			if (minNum < 0 || minNum > maxNum || (maxNum - minNum <= 2)) {
				throw new InvalidNumOfSegmentsError();
			}
			FilamentsRepositoryFactory filamentsRepositoryFactory = FilamentsRepositoryFactory.getInstance();
			FilamentsRepository filamentsRepository = filamentsRepositoryFactory.createFilamentsRepository();
			ArrayList<Filament> suitableFilaments = filamentsRepository.findFilamentByNumOfSegments(minNum, maxNum);
			ArrayList<FilamentBean> suitableFilamentsBean = new ArrayList<>();
			for (Filament filament : suitableFilaments) {
				FilamentBean filamentBean = new FilamentBean(filament);
				suitableFilamentsBean.add(filamentBean);
			}
			return suitableFilamentsBean;
		}
		
//_______________________________________________________________________________________________________

		public ArrayList<FilamentBean> findFilamentsInARegion(double galacticLongitude, double galacticLatitude, double lenght, String chosenShape) throws ConfigurationError, DataAccessError {
			ArrayList<FilamentBean> filamentBeans = new ArrayList<>();
			if(chosenShape =="CERCHIO") {
				filamentBeans = findFilamentByCircle(galacticLongitude,galacticLatitude,lenght);
			} else if(chosenShape =="QUADRATO") {
				filamentBeans = findFilamentBySquare(galacticLongitude,galacticLatitude,lenght);
			}
			return filamentBeans;
		}
		
		
		
		// La funzione restituisce i filamenti in un quadrato
		private ArrayList<FilamentBean> findFilamentBySquare(double centreX, double centreY, double side) throws ConfigurationError, DataAccessError {
			
			// definisco i quattro vertici del quadrato partendo dalle coordinate del centroide e dalla lunghezza del lato
			double halfSide = side/2.0;
			double left = centreX - halfSide;
			double right = centreX + halfSide;
			double down = centreY - halfSide;
			double up = centreY + halfSide;
			ArrayList<FilamentBean> filamentBeans = new ArrayList<>();
			ArrayList<String> filamentNamesInTheSquare = new ArrayList<>();
			ArrayList<Filament> filaments = new ArrayList<>();
			
			FilamentsRepositoryFactory filamentsRepositoryFactory = FilamentsRepositoryFactory.getInstance();
			FilamentsRepository filamentsRepository = filamentsRepositoryFactory.createFilamentsRepository();
			
			// cerco i BorderPointFilament nel quadrato
			filamentNamesInTheSquare = filamentsRepository.findFilamentsWithBorderPointsInSquare(left, right, down, up);
			
			// dato ogni BorderPointFilament ricavo un Filament
			for(String filamentName : filamentNamesInTheSquare) {
				Filament filament = new Filament();
				filament = filamentsRepository.findFilamentByName(filamentName);
				filaments.add(filament);
			}
			
			// dato ogni Filament ricavo un FilamentBean
			for(Filament filament : filaments) {
				FilamentBean filamentBean = new FilamentBean(filament);
				filamentBeans.add(filamentBean);
			}
			
			return filamentBeans;
		}
		
		// La funzione restituisce i filamenti in un cerchio
		private ArrayList<FilamentBean> findFilamentByCircle(double centreX, double centreY, double radius) throws ConfigurationError, DataAccessError {
			
			ArrayList<FilamentBean> filamentBeans = new ArrayList<>();
			ArrayList<String> filamentNames = new ArrayList<>();
			ArrayList<Filament> filaments = new ArrayList<>();
			double powRadius = Math.pow(radius, 2);
			
			FilamentsRepositoryFactory filamentsRepositoryFactory = FilamentsRepositoryFactory.getInstance();
			FilamentsRepository filamentsRepository = filamentsRepositoryFactory.createFilamentsRepository();
			
			// cerco i BorderPointFilament nel cerchio
			filamentNames = filamentsRepository.findFilamentsWithBorderPointsInCircle(centreX, centreY, powRadius);					
			
			// cerco i Filament che sono nel cerchio
			for(String filamentName : filamentNames) {
				Filament filament = new Filament();
				filament = filamentsRepository.findFilamentByName(filamentName);
				filaments.add(filament);
			}
			
			for(Filament filament : filaments) {
				FilamentBean filamentBean = new FilamentBean(filament);
				filamentBeans.add(filamentBean);
			}
			
			return filamentBeans;
		}
		
		

		// La funzione restituisce un ArrayList composta di SegmentPoint come segmento 
		private ArrayList<SegmentPoint> findSegmentBySatelliteNameAndId(String filamentName, int idSegment) throws ConfigurationError, DataAccessError {
			FilamentsRepositoryFactory filamentsRepositoryFactory = FilamentsRepositoryFactory.getInstance();
			FilamentsRepository filamentsRepository = filamentsRepositoryFactory.createFilamentsRepository();

			ArrayList<SegmentPoint> segment = new ArrayList<>();
			segment = filamentsRepository.findSegmentBySatelliteNameAndId(filamentName,idSegment);

			return segment;
		}

		// La funzione restituisce la distanza minima dato un segmento dei suoi estremi dal bordo del filamento 
		public double getMinDinstanceSegmentToBorder(SegmentPoint segmentPoint,String satelliteName) throws ConfigurationError, DataAccessError, NotFoundBorderError {
			FilamentsRepositoryFactory filamentsRepositoryFactory = FilamentsRepositoryFactory.getInstance();
			FilamentsRepository filamentsRepository = filamentsRepositoryFactory.createFilamentsRepository();

			ArrayList<BorderPointFilament> border = new ArrayList<>();
			border = filamentsRepository.findFilamentBorder(segmentPoint.getFilament().getName(),satelliteName);
			double minDistance = 0.0;
			boolean isFirst = true;
			// se non ho punti del segmento ho un errrore
			if(border.isEmpty()) {
				throw new NotFoundBorderError();
			}
			// ottengo la distanza tra tutti i punti del contorno e il punto del segmento e restituisco la minima
			for(BorderPointFilament borderPointFilament : border) {
				minDistance = calculateMinDistanceSegmentPointToBorder(borderPointFilament,segmentPoint,minDistance,isFirst);
				isFirst = false;
			}
			
			return minDistance;
		}

		// La funzione calcola la usando la distanza euclidea la distanza minima
		private double calculateMinDistanceSegmentPointToBorder(BorderPointFilament borderPoint, SegmentPoint segmentPoint, double minDistance, boolean isFirst) {
			double distanceX = Math.abs(borderPoint.getPointLongitude()-segmentPoint.getLongitude());
			double squareX = Math.pow(distanceX,2);
			double distanceY = Math.abs(borderPoint.getPointLatitude()-segmentPoint.getLatitude());
			double squareY = Math.pow(distanceY,2);
			double distance = Math.sqrt((squareX+squareY));
			if(isFirst) {
				minDistance = distance;
			}
			if(distance < minDistance) {
				minDistance = distance;
			}
			return minDistance;
		}

		// La funzione restituisce il primo punto del segmento
		private SegmentPoint getFirstSegmentPoint(ArrayList<SegmentPoint> segment) {
			SegmentPoint firstSegmentPoint = new SegmentPoint();
			for(SegmentPoint segmentPoint : segment) {
				if(segmentPoint.getProgNumber()==1) {
					firstSegmentPoint = segmentPoint;
				}
			}
			return firstSegmentPoint;
		}

		// La funzione restituisce l'ultimo punto del segmento
		private SegmentPoint getLastSegmentPoint(ArrayList<SegmentPoint> segment) {
			SegmentPoint lastSegmentPoint = new SegmentPoint();
			for(SegmentPoint segmentPoint : segment) {
				if(segmentPoint.getProgNumber()==segment.size()) {
					lastSegmentPoint.setFilament(new Filament(segmentPoint.getFilament().getName()));
					lastSegmentPoint.setLatitude(segmentPoint.getLatitude());
					lastSegmentPoint.setLongitude(segmentPoint.getLongitude());
					lastSegmentPoint.setProgNumber(segmentPoint.getProgNumber());
					lastSegmentPoint.setType(segmentPoint.getType());
					lastSegmentPoint.setSegmentId(segmentPoint.getSegmentId());
				}
			}
			return lastSegmentPoint;
		}
		
		public ArrayList<StarBeanWithMinDistance> calculateSFDistance(String filamentName) throws ConfigurationError, DataAccessError, NotFoundFilamentError, NotFoundBackBoneError, FilamentWithoutStarsError {
			ArrayList<StarBeanWithMinDistance> starFilamentDistances = new ArrayList<>();
			
			FilamentsRepositoryFactory filamentsRepositoryFactory = FilamentsRepositoryFactory.getInstance();
			FilamentsRepository filamentsRepository = filamentsRepositoryFactory.createFilamentsRepository();
			
			// prendo il filamento con il nome inserito
			Filament filament = filamentsRepository.findFilamentByName(filamentName);
			// se il filamento non esiste lo comunico all'utente e interrompo l'esecuzione
			if(filament==null) {
				throw new NotFoundFilamentError();
			}
			// cerco le stelle nel filamento
			ArrayList<StarBean> starsBeanInTheFilament = searchStarsIntoFilament(filamentName);
			if(!starsBeanInTheFilament.isEmpty()) { // continuo solo se il filamento contiene stelle altrimenti ho finito
				
				// cerco i punti che costituiscono la spina dorsale
				ArrayList<SegmentPoint> backBone = new ArrayList<>();
				backBone = filamentsRepository.getBackBone(filament);
				if(!backBone.isEmpty()) { // continuo se ho punti della spina dorsale ..
					for(StarBean starBean : starsBeanInTheFilament) {
						/** calcolo la distanza minima dalla stella alla spina dorsale per ogni elemento e incapsulo i dati 
						 * in un oggetto StarBeanWithMinDistance, contenente i dati di uno StarBean più la distanza minima 
						 * dallo scheletro  */
						double minDistanceStarFilament = findMinDistanceFromStarToBackBone(starBean,backBone);
						StarBeanWithMinDistance starBeanWithMinDistance = new StarBeanWithMinDistance(starBean,minDistanceStarFilament);
						starFilamentDistances.add(starBeanWithMinDistance);
					}
				} else {//..altrimenti interrompo l'esecuzione e avviso l'utente
					throw new NotFoundBackBoneError();
				}
			} else {
				throw new FilamentWithoutStarsError();
			}
			
			return starFilamentDistances;
		}

		private double findMinDistanceFromStarToBackBone(StarBean starBean, ArrayList<SegmentPoint> backBone) throws NotFoundBackBoneError {

			double minDistanceFromStarToBackBone,actualDistance,x0,x1,y0,y1;
			minDistanceFromStarToBackBone = -1;
			x0 = starBean.getLongitude();
			y0 = starBean.getLatitude();
			int i = 0;
			for(SegmentPoint backBonePoint : backBone) {
				x1 = backBonePoint.getLongitude();
				y1 = backBonePoint.getLatitude();
				if(i==0) { // all'inizio prendo la prima distanza come migliore
					minDistanceFromStarToBackBone = getDistance(y1, y0, x1, x0);
					i++;
				}
				actualDistance = getDistance(y1, y0, x1, x0);
				if(actualDistance < minDistanceFromStarToBackBone) { // prendo la distanza minore tra l'attuale e la precedente
					minDistanceFromStarToBackBone = actualDistance;
				}
				
			}
			if(minDistanceFromStarToBackBone==-1) { // in caso di scheletro assente ho un errore
				throw new NotFoundBackBoneError();
			}
			return minDistanceFromStarToBackBone;
		}

		private double getDistance(double y1, double y0, double x1, double x0) {
			double powDistance = Math.pow((y1-y0), 2) + Math.pow((x1-x0), 2);
			double distance = Math.pow(powDistance, 0.5);
			return distance;
		}
		
		// La funzione restituisce la distanza minima tra gli estremi del segmento e i punti del contorno
				public HashMap<String,Double> calculateMinDistanceFromSegmentToBorder(String filamentName, int idSegment, String satelliteName) throws NotFoundSegmentPointError, ConfigurationError, DataAccessError, NotFoundBorderError {
					HashMap<String,Double> distances = new HashMap<>();
					ArrayList<SegmentPoint> segment = new ArrayList<>();
					segment = findSegmentBySatelliteNameAndId(filamentName,idSegment);
					
					// Se il filamento non ha punti del segmento si ha un errore che vie stampato a schermo
					if(segment.isEmpty()) {
						new NotFoundSegmentPointError();
					}
					
					// prendo gli estremi del segmento
					SegmentPoint begin = new SegmentPoint();
					begin = getFirstSegmentPoint(segment);
					SegmentPoint end = new SegmentPoint();
					end = getLastSegmentPoint(segment);
					
					// misuro le distanze tra gli estremi e il bordo e le metto in un dizionario
					if(begin!=null && end!=null) {
						double firstMinDinstance = getMinDinstanceSegmentToBorder(begin,satelliteName);
						double secondMinDistance = getMinDinstanceSegmentToBorder(end,satelliteName);
						distances.put("firstMinDinstance", firstMinDinstance);
						distances.put("secondMinDistance", secondMinDistance);
						return distances;
						}
					return null;
				}
	    
}
