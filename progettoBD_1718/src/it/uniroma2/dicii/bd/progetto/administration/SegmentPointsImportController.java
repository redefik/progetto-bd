package it.uniroma2.dicii.bd.progetto.administration;

import java.io.File;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import it.uniroma2.dicii.bd.progetto.errorLogic.BatchError;
import it.uniroma2.dicii.bd.progetto.errorLogic.CSVFileParserException;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.errorLogic.ErrorType;
import it.uniroma2.dicii.bd.progetto.errorLogic.GUIError;
import it.uniroma2.dicii.bd.progetto.filament.SegmentPointImported;
import it.uniroma2.dicii.bd.progetto.gui.WindowManager;
import it.uniroma2.dicii.bd.progetto.satellite.SatelliteBean;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.ProgressIndicator;

public class SegmentPointsImportController {
	
	private static final String IMPORT_FILE_FORMAT_NAME = "csv";
	private static final String IMPORT_FILE_FORMAT_EXTENSION = "*.csv";
	private static final String NOT_SELECTED_FILE_MESSAGE = "Seleziona prima un file da importare.";
	private static final String IMPORT_FAILED = "Import del file non riuscito.";
	private static final String TASK_IS_RUNNING = "Attendere il completamento dell'operazione";
	
	@FXML
	private ComboBox<SatelliteBean> cBoxSatellites;
	@FXML
	private TextField importedFilePath;
	@FXML
	private Label errorMessage;
	@FXML
	private AnchorPane window;
	@FXML 
	private ProgressIndicator progressIndicator;
	
	private File importedFile;
	private ArrayList<SatelliteBean> satelliteBeans;
	private SatelliteBean selectedSatellite;
	// l'attributo serve ad evitare che durante l'import l'utente interagisca in modo unsafe con l'interfaccia
	private boolean isTaskRunning;
	
	// il task svolge l'import del file su un thread separato per evitare un comportamento unresponsive da parte della GUI  
	private class SegmentPointsImportTask extends Task<Void> {
		
		@Override
		protected Void call() throws ConfigurationError, CSVFileParserException, DataAccessError, BatchError {
		
			isTaskRunning = true;
			updateProgress(-1, 1); // si aziona l'indicatore di caricamento
			CSVFileParserFactory parserFactory = CSVFileParserFactory.getInstance();
			CSVFileParser fileParser = parserFactory.createCSVFileParser();
			ArrayList<SegmentPointImported> segmentPoints = fileParser.getSegmentPoints(importedFile);
			AdministrationSession administrationSession = AdministrationSession.getInstance();
			administrationSession.insertSegmentPoints(segmentPoints, selectedSatellite);
			updateProgress(1, 1); // l'indicatore di caricamento segnalera' il completamento dell'operazione
			isTaskRunning = false;
			return null;
			
		}
	}
	
	// il listener intercetta le eccezioni eventualmente sollevate dal task
	private class ExceptionListener implements ChangeListener<Throwable> {

		@Override
		public void changed(ObservableValue<? extends Throwable> observable, Throwable oldValue, Throwable exception) {
			if(exception != null) {
				 isTaskRunning = false;
				 progressIndicator.setVisible(false);
				 Logger.getLogger(getClass()).error(exception.getMessage(), exception);
				 if (exception instanceof ConfigurationError) {
					 WindowManager.getInstance().openErrorWindow(ErrorType.CONFIGURATION);
				 } else if (exception instanceof CSVFileParserException) {
					 WindowManager.getInstance().openErrorWindow(ErrorType.CSVFILE_PARSING);
				 } else if (exception instanceof DataAccessError) {
					 WindowManager.getInstance().openErrorWindow(ErrorType.DATA_ACCESS);
				 } else if (exception instanceof BatchError) {
					 WindowManager.getInstance().openDetailedErrorWindow(IMPORT_FAILED, exception.getMessage());
				 }
			}		
		}
	}
	
	
	@FXML
	public void initialize() {
		try {
			isTaskRunning = false;
			progressIndicator.setVisible(false);
			WindowManager.getInstance().setWindow(window);
			ObservableList<SatelliteBean> observableSatelliteBeans;
	        satelliteBeans = new ArrayList<>();
	        satelliteBeans = AdministrationSession.getInstance().findAllSatellites();
	        observableSatelliteBeans = FXCollections.observableArrayList(satelliteBeans);
	        cBoxSatellites.setItems(observableSatelliteBeans);
	        cBoxSatellites.setValue(satelliteBeans.get(0));
	        
		} catch (DataAccessError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.DATA_ACCESS);
		} catch (ConfigurationError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.CONFIGURATION);
		}
	}
	
	public void gotoPreviousMenu() {
		if (isTaskRunning) {
			WindowManager.getInstance().openInfoWindow(TASK_IS_RUNNING);
			return;
		}
		try {
			WindowManager.getInstance().goToPreviousMenu();
		} catch (GUIError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.GUI);
		}
	}
	
	public void selectFile() {
		if (isTaskRunning) {
			WindowManager.getInstance().openInfoWindow(TASK_IS_RUNNING);
			return;
		}
		errorMessage.setText("");
		WindowManager windowManager = WindowManager.getInstance();
		//Si permette all'utente di scegliere dal proprio dispositivo il file da importare, si permette la ricerca solo 
		//tra file di uno specifico formato (nel caso specifico .csv)
		importedFile = windowManager.getFileFromSystemExplorerWithFormat(IMPORT_FILE_FORMAT_NAME, IMPORT_FILE_FORMAT_EXTENSION);
		if (importedFile != null) {
			importedFilePath.setText(importedFile.getAbsolutePath());
		}
	}
	
	public void betterImportFile() {
		if (isTaskRunning) {
			WindowManager.getInstance().openInfoWindow(TASK_IS_RUNNING);
			return;
		}
		
		if (importedFile == null) {
			errorMessage.setText(NOT_SELECTED_FILE_MESSAGE);
			return;
		}
		progressIndicator.setVisible(true);
		selectedSatellite = cBoxSatellites.getSelectionModel().getSelectedItem();
		// l'operazione, che risulta costosa, viene collocata su un thread separato da quello responsabile del disegno della GUI
		SegmentPointsImportTask task = new SegmentPointsImportTask();
		// si pone il ProgressIndicator in ascolto degli aggiornamenti provenienti dal task relativi all'avanzamento dell'operazione
		progressIndicator.progressProperty().bind(task.progressProperty());
		ExceptionListener exceptionListener = new ExceptionListener();
		task.exceptionProperty().addListener(exceptionListener);
		// si lancia il task
		Thread taskThread = new Thread(task);
		taskThread.setDaemon(true);
		taskThread.start();
	}
/*
	// questo metodo innesca le stesse operazioni innescate da betterImportFile ma senza svincolarle dal thread che gestisce la GUI
	public void importFile() {
		try {
			SatelliteBean selectedSatellite = cBoxSatellites.getSelectionModel().getSelectedItem();
			if (importedFile == null) {
				errorMessage.setText(NOT_SELECTED_FILE_MESSAGE);
				return;
			}
						
			CSVFileParserFactory parserFactory = CSVFileParserFactory.getInstance();
			CSVFileParser fileParser = parserFactory.createCSVFileParser();
			
			
			ArrayList<SegmentPointImported> segmentPoints = fileParser.getSegmentPoints(importedFile);
			
			AdministrationSession administrationSession = AdministrationSession.getInstance();
			administrationSession.insertSegmentPoints(segmentPoints, selectedSatellite);
			WindowManager.getInstance().openInfoWindow(IMPORT_SUCCESS);
			WindowManager.getInstance().changeMenu(ADMINISTRATION_MENU);
			
		} catch (ConfigurationError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.CONFIGURATION);
		} catch(CSVFileParserException e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.CSVFILE_PARSING);
		} catch (GUIError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.GUI);
		} catch (DataAccessError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.DATA_ACCESS);
		} catch (BatchError e) {
			WindowManager.getInstance().openDetailedErrorWindow(IMPORT_FAILED, e.getMessage());
		}
	}
*/	
}
