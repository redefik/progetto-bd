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
import it.uniroma2.dicii.bd.progetto.filament.BorderPointBean;
import it.uniroma2.dicii.bd.progetto.gui.WindowManager;
import it.uniroma2.dicii.bd.progetto.satellite.SatelliteBean;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;

public class BorderPointsImportController {
	
	private static final String IMPORT_FILE_FORMAT_NAME = "csv";
	private static final String IMPORT_FILE_FORMAT_EXTENSION = "*.csv";
	private static final String NOT_SELECTED_FILE_MESSAGE = "Seleziona prima un file da importare.";
	private static final String IMPORT_SUCCESS = "Import del file completato.";
	private static final String ADMINISTRATION_MENU = "../gui/administrationView.fxml";
	private static final String IMPORT_FAILED = "Import del file non riuscito.";
	private static final String TASK_IS_RUNNING = "Attendere il completamento dell'operazione";
	
	@FXML 
	private AnchorPane window;
	
	@FXML 
	private TextField importedFilePath;
	
	@FXML 
	private Label errorMessage;
	
	@FXML 
	private ComboBox<SatelliteBean> satellite;
	@FXML 
	private ProgressIndicator progressIndicator;

	
	private ArrayList<SatelliteBean> satelliteBeans;
	private File importedFile;
	private boolean isTaskRunning;
	
	private class BorderPointsImportTask extends Task<Void> {

		@Override
		protected Void call() throws ConfigurationError, CSVFileParserException, DataAccessError, BatchError {
			isTaskRunning = true;
			updateProgress(-1, 1);
			// Si instanzia mediante l'uso di una factory un parser per il file da importare
			CSVFileParserFactory parserFactory = CSVFileParserFactory.getInstance();
			CSVFileParser parser = parserFactory.createCSVFileParser();
						
			//Si delega a un oggetto di tipo CSVFileParser il parser del file per ottenere una lista di BorderPointBean
			SatelliteBean satelliteBean = satellite.getSelectionModel().getSelectedItem();
			ArrayList<BorderPointBean> borderPointBeans = parser.getBorderPointBeans(importedFile, satelliteBean);
						
			//Si delega alla classe AdministrationSession l'inserimento dei punti del contorno in persistenza
			AdministrationSession administrationSession = AdministrationSession.getInstance();
			administrationSession.insertBorderPoints(borderPointBeans);
						
			updateProgress(1, 1);
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
		progressIndicator.setVisible(false);
		isTaskRunning = false;
		WindowManager.getInstance().setWindow(window);
		
		// Si inizializza la comboBox con tutti i satelliti presenti in persistenza
		try {
			ObservableList<SatelliteBean> observableSatelliteBeans;
	        satelliteBeans = new ArrayList<>();
	        satelliteBeans = AdministrationSession.getInstance().findAllSatellites();
	        observableSatelliteBeans = FXCollections.observableArrayList(satelliteBeans);
	        satellite.setItems(observableSatelliteBeans);
	        satellite.setValue(satelliteBeans.get(0));
	        
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
		//Si permette all'utente di scegliere dal proprio disposito il file da importare, si permette la ricerca solo 
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
		// Se l'utente non ha specificato un file da importare si stampa un messaggio di errore
		if (importedFile == null) {
			errorMessage.setText(NOT_SELECTED_FILE_MESSAGE);
			return;
		}
		progressIndicator.setVisible(true);
		// l'operazione, che risulta costosa, viene collocata su un thread separato da quello responsabile del disegno della GUI
		BorderPointsImportTask task = new BorderPointsImportTask();
		// si pone il ProgressIndicator in ascolto degli aggiornamenti provenienti dal task relativi all'avanzamento dell'operazione
		progressIndicator.progressProperty().bind(task.progressProperty());
		ExceptionListener exceptionListener = new ExceptionListener();
		task.exceptionProperty().addListener(exceptionListener);
		// si lancia il task
		Thread taskThread = new Thread(task);
		taskThread.setDaemon(true);
		taskThread.start();
	}
	

	public void importFile() {
		try {
			// Se l'utente non ha specificato un file da importare si stampa un messaggio di errore
			if (importedFile == null) {
				errorMessage.setText(NOT_SELECTED_FILE_MESSAGE);
				return;
			}
			
			// Si instanzia mediante l'uso di una factory un parser per il file da importare
			CSVFileParserFactory parserFactory = CSVFileParserFactory.getInstance();
			CSVFileParser parser = parserFactory.createCSVFileParser();
			
			//Si delega a un oggetto di tipo CSVFileParser il parser del file per ottenere una lista di BorderPointBean
			SatelliteBean satelliteBean = satellite.getSelectionModel().getSelectedItem();
			ArrayList<BorderPointBean> borderPointBeans = parser.getBorderPointBeans(importedFile, satelliteBean);
			
			//Si delega alla classe AdministrationSession l'inserimento dei punti del contorno in persistenza
			AdministrationSession administrationSession = AdministrationSession.getInstance();
			administrationSession.insertBorderPoints(borderPointBeans);
			
			
			WindowManager.getInstance().openInfoWindow(IMPORT_SUCCESS);
			WindowManager.getInstance().changeMenu(ADMINISTRATION_MENU);
			
		
		} catch(ConfigurationError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.CONFIGURATION);
		} catch(CSVFileParserException e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.CSVFILE_PARSING);
		} catch (GUIError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.GUI);
		} catch (BatchError e) {
			WindowManager.getInstance().openDetailedErrorWindow(IMPORT_FAILED, e.getMessage());
		} catch (DataAccessError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.DATA_ACCESS);
		}
		
	}
}
