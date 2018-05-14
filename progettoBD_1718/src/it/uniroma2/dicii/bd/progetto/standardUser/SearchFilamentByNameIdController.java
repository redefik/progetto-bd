package it.uniroma2.dicii.bd.progetto.standardUser;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.errorLogic.ErrorType;
import it.uniroma2.dicii.bd.progetto.errorLogic.GUIError;
import it.uniroma2.dicii.bd.progetto.filament.FilamentBean;
import it.uniroma2.dicii.bd.progetto.filament.FilamentInfo;
import it.uniroma2.dicii.bd.progetto.gui.WindowManager;
import it.uniroma2.dicii.bd.progetto.satellite.InstrumentBean;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ProgressIndicator;

public class SearchFilamentByNameIdController {
	
	private static final String INVALID_ID_FORMAT = "L'id del filamento non è nel formato valido";
	private static final String TASK_IS_RUNNING = "Attendere!";
	private static final String FILAMENT_INFO_NOT_AVAILABLE = "Le informazioni sul filamento indicato non sono disponibili";
	private static final String FILAMENT_NOT_SPECIFIED = "Specifica prima il filamento da cercare";
	private static final String FILAMENT_INFO_MENU = "../gui/filamentInfoView.fxml";

	@FXML 
	private AnchorPane window;
	@FXML 
	private ToggleGroup searchType;
	@FXML 
	private TextField tvFilamentName;
	@FXML 
	private Label labelFilamentName;
	@FXML 
	private Label labelFilamentId;
	@FXML 
	private TextField tvFilamentId;
	@FXML 
	private ComboBox<InstrumentBean> cBoxFilamentInstrument;
	@FXML
	private Label labelFilamentInstrument;
	@FXML
	private RadioButton rBtnNameSearch;
	@FXML
	private RadioButton rBtnIdSearch;
	@FXML 
	private Label errorMessage;
	@FXML 
	private ProgressIndicator progressIndicator;
	
	private ArrayList<InstrumentBean> instrumentBeans;
	private boolean isTaskRunning;
	private SearchFilamentTask task;
	
	// la ricerca delle informazioni derivate del filamento viene assegnata ad un thread separato dall'FX-thread responsabile della GUI
	private class SearchFilamentTask extends Task<FilamentInfo> {

		@Override
		protected FilamentInfo call() throws ConfigurationError, DataAccessError {
			isTaskRunning = true;
			updateProgress(-1, 1); // si aziona l'indicatore di caricamento
			StandardUserSession standardUserSession = StandardUserSession.getInstance();
			FilamentBean filament;
			// innanzitutto, si verifica l'esistenza del filamento ricercandolo secondo la modalità indicata dall'utente
			RadioButton searchTypeBtn = (RadioButton) searchType.getSelectedToggle();
			if (searchTypeBtn.equals(rBtnNameSearch)) {
				String filamentName = tvFilamentName.getText();
				filament = standardUserSession.findFilamentByName(filamentName);
			} else {
				int filamentId = Integer.parseInt(tvFilamentId.getText());
				String instrumentName = cBoxFilamentInstrument.getSelectionModel().getSelectedItem().getName();
				filament = standardUserSession.findFilamentByIdAndInstrument(filamentId, instrumentName);
			}
			isTaskRunning = false;
			if (filament == null) {
				return null;
			}
			FilamentInfo filamentInfo = standardUserSession.getFilamentInfo(filament);
			return filamentInfo;
		}	
	}
	
	// l'handler intercetta l'oggetto ritornato dal task
	private class TaskReturnHandler implements EventHandler<WorkerStateEvent> {

		@Override
		public void handle(WorkerStateEvent event) {
			progressIndicator.setVisible(false);
			FilamentInfo filamentInfo;
			filamentInfo = task.getValue();
			// se il filamento specificato non è disponibile, viena mostrata all'utente una finestra d'informazione
			if (filamentInfo == null) {
				WindowManager.getInstance().openInfoWindow(FILAMENT_INFO_NOT_AVAILABLE);
			// per una migliore fruibilità si visualizza il risultato della ricerca in una nuova finestra
			} else {
				try {
					FilamentInfoController.setFilamentInfo(filamentInfo);
					WindowManager.getInstance().openWindow(FILAMENT_INFO_MENU);
				} catch (GUIError e) {
					Logger.getLogger(getClass()).error(e.getMessage(), e);
					WindowManager.getInstance().openErrorWindow(ErrorType.GUI);	
				}
			}
			
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
				 } else if (exception instanceof DataAccessError) {
					 WindowManager.getInstance().openErrorWindow(ErrorType.DATA_ACCESS);
				 } else if (exception instanceof NumberFormatException) {
					 errorMessage.setText(INVALID_ID_FORMAT);
				 }
			}		
		}
	}
	
	
	@FXML
	public void initialize() {
		WindowManager.getInstance().setWindow(window);
		isTaskRunning = false;
		progressIndicator.setVisible(false);
		// La combobox viene inizializzata con gli strumenti presenti nello strato di persistenza
		try {
			ObservableList<InstrumentBean> observableInstrumentBeans;
	        instrumentBeans = new ArrayList<>();
	        instrumentBeans = StandardUserSession.getInstance().findAllInstruments();
	        observableInstrumentBeans = FXCollections.observableArrayList(instrumentBeans);
	        cBoxFilamentInstrument.setItems(observableInstrumentBeans);
	        cBoxFilamentInstrument.setValue(instrumentBeans.get(0));
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

	public void disableIdSearchFields() {
		if (isTaskRunning) {
			WindowManager.getInstance().openInfoWindow(TASK_IS_RUNNING);
			return;
		}
		labelFilamentId.setDisable(true);
		tvFilamentId.setDisable(true);
		tvFilamentId.setText("");
		labelFilamentInstrument.setDisable(true);
		cBoxFilamentInstrument.setDisable(true);
		errorMessage.setText("");
		labelFilamentName.setDisable(false);
		tvFilamentName.setDisable(false);
	}

	public void disableNameSearchFields() {
		if (isTaskRunning) {
			WindowManager.getInstance().openInfoWindow(TASK_IS_RUNNING);
			return;
		}
		labelFilamentName.setDisable(true);
		tvFilamentName.setDisable(true);
		errorMessage.setText("");
		tvFilamentName.setText("");
		labelFilamentId.setDisable(false);
		tvFilamentId.setDisable(false);
		labelFilamentInstrument.setDisable(false);
		cBoxFilamentInstrument.setDisable(false);
	}

	public void searchFilament() {
		if (isTaskRunning) {
			WindowManager.getInstance().openInfoWindow(TASK_IS_RUNNING);
			return;
		}
		// se l'utente non ha specificato nome o id la ricerca non parte e viene mostrato un messaggio d'errore
		RadioButton searchTypeBtn = (RadioButton) searchType.getSelectedToggle();
		if (searchTypeBtn.equals(rBtnNameSearch) && tvFilamentName.getText().equals("") || 
				searchTypeBtn.equals(rBtnIdSearch) && tvFilamentId.getText().equals("") ) {
			errorMessage.setText(FILAMENT_NOT_SPECIFIED);
			return;
		}
		progressIndicator.setVisible(true);
		task = new SearchFilamentTask();
		progressIndicator.progressProperty().bind(task.progressProperty());
		ExceptionListener exceptionListener = new ExceptionListener();
		task.exceptionProperty().addListener(exceptionListener);
		TaskReturnHandler taskReturnHandler = new TaskReturnHandler();
		task.setOnSucceeded(taskReturnHandler);
		Thread taskThread = new Thread(task);
		taskThread.setDaemon(true);
		taskThread.start();
	}

	public void clearErrorMessage() {
		errorMessage.setText("");
	}

}
