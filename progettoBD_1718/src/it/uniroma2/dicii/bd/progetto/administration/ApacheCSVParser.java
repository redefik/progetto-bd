package it.uniroma2.dicii.bd.progetto.administration;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import it.uniroma2.dicii.bd.progetto.errorLogic.CSVFileParserException;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.filament.BorderPointBean;
import it.uniroma2.dicii.bd.progetto.filament.FilamentBean;
import it.uniroma2.dicii.bd.progetto.repository.FilamentsRepository;
import it.uniroma2.dicii.bd.progetto.repository.FilamentsRepositoryFactory;
import it.uniroma2.dicii.bd.progetto.satellite.InstrumentBean;
import it.uniroma2.dicii.bd.progetto.satellite.SatelliteBean;

public class ApacheCSVParser extends CSVFileParser{
	
	Map<Integer, String> idFilamentToName = new HashMap<Integer, String>();
	Map<String, BorderPointBean> positionToBorderPoint = new HashMap<String, BorderPointBean>();
	
	@Override
	public ArrayList<FilamentBean> getFilamentBeans(File importedFile) throws CSVFileParserException {
		CSVParser csvFileParser = null;
		FileReader fileReader = null;
		try {	
			//Si specifica il formato che le righe del file csv devono rispettare e il file da parsare
			CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(super.FILAMENTS_FILE_HEADERS);
			fileReader = new FileReader(importedFile);
			
			csvFileParser = new CSVParser(fileReader, csvFileFormat);
			
			//Per ogni oggetto di tipo CSVRecord ottenuto dal parsing si crea un oggetto di tipo FilamentoBean
			List<CSVRecord> csvRecords = csvFileParser.getRecords();
			ArrayList<FilamentBean> filamentBeans = new ArrayList<>();
			
			// Nota: nel ciclo viene saltata la riga contenente gli header
			for (int i = 1; i < csvRecords.size(); ++i) {
				
				CSVRecord record = (CSVRecord)csvRecords.get(i);
				FilamentBean filamentBean = new FilamentBean();
				filamentBean.setName(record.get("NAME"));
				filamentBean.setNumber(Integer.parseInt(record.get("IDFIL")));
				filamentBean.setContrast(Double.parseDouble(record.get("CONTRAST")));
				filamentBean.setEllipticity(Double.parseDouble(record.get("ELLIPTICITY")));
				filamentBean.setInstrumentName(record.get("INSTRUMENT"));
				filamentBeans.add(filamentBean);
			}
			return filamentBeans;
			
		} catch (IOException | IllegalArgumentException e) {
			throw new CSVFileParserException(e.getMessage(), e.getCause());
		} finally {
			//Terminato il parsing del file, viene rilasciato il parser
			if (csvFileParser != null) {
				try {
					csvFileParser.close();
				} catch (IOException e) {
					throw new CSVFileParserException(e.getMessage(), e.getCause());
				}
			}
			//Terminato il parsing del file, viene chiuso il canale in lettura con esso
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (IOException e) {
					throw new CSVFileParserException(e.getMessage(), e.getCause());
				}
			}
		}
	}
	
	private boolean addFilamentToBorderPoint (BorderPointBean borderPointBean, int idFilament, ArrayList<InstrumentBean> instrumenBeans) throws ConfigurationError, DataAccessError {
		
		//Si controlla se è stato già ottenuto il nome del filamento a partire dal suo id e dal satellite osservante
		if (idFilamentToName.containsKey(idFilament)) {
			 
			if (borderPointBean.getFilamentNames().contains(idFilamentToName.get(idFilament))){
				//Si controlla se la tupla che si è considerata è doppia. In tal caso viene ignorata.
				return false;
			}
			
			borderPointBean.getFilamentNames().add(idFilamentToName.get(idFilament));
			return true;
			
		} else {
			//Se la corrispondenza non è gia stata risolta occorre interrogare il database
			FilamentsRepositoryFactory filamentsRepositoryFactory = FilamentsRepositoryFactory.getInstance();
			FilamentsRepository filamentsRepository = filamentsRepositoryFactory.createFilamentsRepository();
			String nameFilament = filamentsRepository.searchFilamentByIdAndInstruments(idFilament, instrumenBeans);
			
			if (nameFilament == null) {
				//Se l'id e il satellite specificato non corrispondono a nessun filamento si ritorna false
				return false;
				
			} else {
				idFilamentToName.put(idFilament, nameFilament);
				borderPointBean.getFilamentNames().add(nameFilament);
				return true;
			}
		}
	}
	
	@Override
	public ArrayList<BorderPointBean> getBorderPointBeans(File importedFile, SatelliteBean satelliteBean) 
			throws CSVFileParserException, ConfigurationError, DataAccessError {
		
		CSVParser csvFileParser = null;
		FileReader fileReader = null;
		try {	
			//Si specifica il formato che le righe del file csv devono rispettare e il file da parsare
			CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(super.BORDER_POINTS_FILE_HEADERS);
			fileReader = new FileReader(importedFile);
			
			csvFileParser = new CSVParser(fileReader, csvFileFormat);
			
			List<CSVRecord> csvRecords = csvFileParser.getRecords();
			ArrayList<BorderPointBean> borderPointBeans = new ArrayList<>();
			
			
			// Nota: nel ciclo viene saltata la riga contenente gli header
			for (int i = 1; i < csvRecords.size(); ++i) {
				CSVRecord record = (CSVRecord)csvRecords.get(i);
				
				double latitude = Double.parseDouble(record.get("GLAT_CONT"));
				double longitude = Double.parseDouble(record.get("GLON_CONT"));
				int idFilament = Integer.parseInt(record.get("IDFIL"));
				
				//Si trattano punti del contorno analizzati da un satellite: la posizione galattica li individua univocamente
				String key = "" + latitude + ","+ longitude;
				
				//Se la posizione e' già presente tra quelle da inserire (tale evento si manifesta se un punto 
				//appartiene al contorno di più filamenti) allora si aggiunge soltanto il filamento
				if (positionToBorderPoint.containsKey(key)) {
					addFilamentToBorderPoint(positionToBorderPoint.get(key),idFilament, satelliteBean.getInstrumentBeans());
					
				//Se la posizione non è gia presente tra quelle da inserire allora si crea un punto del contorno che la ricopre
				} else {
					
					BorderPointBean borderPointBean = new BorderPointBean();
					borderPointBean.setLatitude(latitude);
					borderPointBean.setLongitude(longitude);
					borderPointBean.setSatellite(satelliteBean.getName());
					if (addFilamentToBorderPoint(borderPointBean,idFilament, satelliteBean.getInstrumentBeans())) {
						positionToBorderPoint.put(key, borderPointBean);
					}
				}
			}
			
			for (BorderPointBean borderPointBean : positionToBorderPoint.values()) {
				borderPointBeans.add(borderPointBean);
			}
			return borderPointBeans;
			
		} catch (IOException | IllegalArgumentException e) {
			throw new CSVFileParserException(e.getMessage(), e.getCause());
		} finally {
			//Terminato il parsing del file, viene rilasciato il parser e chiuso il canale in lettura con il file
			if (csvFileParser != null) {
				try {
					csvFileParser.close();
				} catch (IOException e) {
					throw new CSVFileParserException(e.getMessage(), e.getCause());
				}
			}
			
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (IOException e) {
					throw new CSVFileParserException(e.getMessage(), e.getCause());
				}
			}
		}
	}
}
