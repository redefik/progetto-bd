package it.uniroma2.dicii.bd.progetto.standardUser;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.errorLogic.ErrorType;
import it.uniroma2.dicii.bd.progetto.errorLogic.GUIError;
import it.uniroma2.dicii.bd.progetto.errorLogic.InvalidBrightnessError;
import it.uniroma2.dicii.bd.progetto.errorLogic.InvalidEllipticityError;
import it.uniroma2.dicii.bd.progetto.filament.ContrastEllipticityResearchResult;
import it.uniroma2.dicii.bd.progetto.filament.FilamentBean;
import it.uniroma2.dicii.bd.progetto.gui.WindowManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.ProgressIndicator;

public class SearchFilamentByContrastEllipticityController {
	
	private static final String NOT_COMPLETED_FIELDS = "Compila tutti i campi del form.";
	private static final String INVALID_INPUT = "Il formato dei dati di input non è valido.";
	private static final String INVALID_BRIGHTNESS = "La luminosità deve essere un numero positivo";
	private static final String INVALID_ELLIPTICITY = "I valori di ellitticità devono essere compresi tra 1 e 10 esclusi.";
	private static final String NOT_SUITABLE_FILAMENTS = "Nessun filamento risponde ai requisiti indicati.";
	private static final String RESEARCH_RESULT_MENU = "../gui/contrastEllipticityResearchResultView.fxml";
	private static final String TASK_IS_RUNNING = "Attendere il completamento della ricerca.";
	
	@FXML 
	private TextField tfMinBrightness;
	@FXML 
	private TextField tfMinEllipticity;
	@FXML 
	private TextField tfMaxEllipticity;
	@FXML 
	private Label labelErrorMessage;
	@FXML 
	private AnchorPane window;
	@FXML 
	private ProgressIndicator progressIndicator;
	
	private boolean isTaskRunning;
	private SearchFilamentsTask task;
	
	
	
	// il task esegue la ricerca dei filamenti su un thread separato dall'FX-thread che disegna la GUI
	private class SearchFilamentsTask extends Task<ContrastEllipticityResearchResult> {

		@Override
		protected ContrastEllipticityResearchResult call() throws InvalidBrightnessError, InvalidEllipticityError, ConfigurationError, DataAccessError {
			updateProgress(-1, 1); // si aziona l'indicatore di caricamento
			isTaskRunning = true;
			StandardUserSession standardUserSession = StandardUserSession.getInstance(); 
			// il controller si limita ad effettuare un controllo sintattico mediante parseDouble
			// il controllo semantico viene demandato all'oggetto Session
			double minBrightness = Double.parseDouble(tfMinBrightness.getText());
			double minEllipticity = Double.parseDouble(tfMinEllipticity.getText());
			double maxEllipticity = Double.parseDouble(tfMaxEllipticity.getText());
			ContrastEllipticityResearchResult researchResult = 
					standardUserSession.findFilamentsByContrastAndEllipticity(minBrightness, minEllipticity, maxEllipticity);
			isTaskRunning = false;
			return researchResult;
		}
		
	}
	
	// l'handler intercetta l'oggetto ritornato dal task
	private class TaskReturnHandler implements EventHandler<WorkerStateEvent> {

		@Override
		public void handle(WorkerStateEvent event) {
			progressIndicator.setVisible(false);
			ContrastEllipticityResearchResult researchResult = task.getValue();
			ArrayList<FilamentBean> suitableFilaments = researchResult.getSuitableFilaments();
			// se nessun filamento nel catalogo risponde ai requisiti viene mostrata all'utente una finestra d'informazione
			if (suitableFilaments.isEmpty()) {
				WindowManager.getInstance().openInfoWindow(NOT_SUITABLE_FILAMENTS);
				return;
			}
			// se sono stati trovati filamenti compatibili con le richieste vengono visualizzati in forma tabellare
			// la visualizzazione per una migiliore fruibilità avviene all'interno di una nuova finestra
			try {
				ContrastEllipticityResearchResultController.setResearchResult(researchResult);
				WindowManager.getInstance().openWindow(RESEARCH_RESULT_MENU);
			} catch (GUIError e) {
				Logger.getLogger(getClass()).error(e.getMessage(), e);
				WindowManager.getInstance().openErrorWindow(ErrorType.GUI);
				gotoPreviousMenu();
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
					labelErrorMessage.setText(INVALID_INPUT);
				} else if (exception instanceof InvalidBrightnessError) {
					labelErrorMessage.setText(INVALID_BRIGHTNESS);
				} else if (exception instanceof InvalidEllipticityError) {
					labelErrorMessage.setText(INVALID_ELLIPTICITY);
				}
			}		
		}
	}
		

	@FXML
	public void initialize() {
		WindowManager.getInstance().setWindow(window);
		isTaskRunning = false;
		progressIndicator.setVisible(false);
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
 
	public void searchFilaments() {
		if (isTaskRunning) {
			WindowManager.getInstance().openInfoWindow(TASK_IS_RUNNING);
			return;
		}
		// se alcuni campi non sono stati compilati viene mostrato all'utente un messaggio d'errore
		if (tfMinBrightness.getText().equals("") || tfMinEllipticity.getText().equals("") || tfMaxEllipticity.getText().equals("")) {
			labelErrorMessage.setText(NOT_COMPLETED_FIELDS);
			return;
		}
		progressIndicator.setVisible(true);
		task = new SearchFilamentsTask();
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
		labelErrorMessage.setText("");
	}

}
