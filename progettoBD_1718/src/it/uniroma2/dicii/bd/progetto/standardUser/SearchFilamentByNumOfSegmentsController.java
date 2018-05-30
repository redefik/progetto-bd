package it.uniroma2.dicii.bd.progetto.standardUser;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.errorLogic.ErrorType;
import it.uniroma2.dicii.bd.progetto.errorLogic.GUIError;
import it.uniroma2.dicii.bd.progetto.errorLogic.InvalidNumOfSegmentsError;
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

public class SearchFilamentByNumOfSegmentsController {
	
	private static final String TASK_IS_RUNNING = "Attendere il completamento della ricerca.";
	private static final String INVALID_NUM_OF_SEGMENTS_FORMAT = "Il formato dei dati inseriti non e'valido.";
	private static final String NOT_COMPLETED_FIELDS = "Specifica entrambi gli estremi dell'intervallo";
	private static final String INVALID_NUM_OF_SEGMENTS = 
			"I dati inseriti non sono corretti. Nota: l'ampiezza dell'intervallo deve essere > 2.";
	private static final String NOT_SUITABLE_FILAMENTS = "Nessun filamento soddisfa i requisiti indicati.";
	private static final String RESEARCH_RESULT_MENU = "../gui/numOfSegmentsResearchResultView.fxml";

	@FXML 
	private TextField tfMinNum;
	@FXML
	private TextField tfMaxNum;
	@FXML
	private Label labelErrorMessage;
	@FXML
	private AnchorPane window;
	@FXML
	private ProgressIndicator progressIndicator;
	
	private boolean isTaskRunning;
	private SearchFilamentTask task; 

	// il task esegue la ricerca dei filamenti su un thread separato dall'FX-thread responsabile di disegnare la GUI
	private class SearchFilamentTask extends Task<ArrayList<FilamentBean>> {

		@Override
		protected ArrayList<FilamentBean> call() throws InvalidNumOfSegmentsError, ConfigurationError, DataAccessError {
			isTaskRunning = true;
			updateProgress(-1, 1);
			StandardUserSession standardUserSession = StandardUserSession.getInstance();
			// attraverso parseInt il controller effettua un controllo sul tipo di natura sintattica
			// il controllo semantico viene demandato all'oggetto Session
			int minNumOfSegments = Integer.parseInt(tfMinNum.getText());
			int maxNumOfSegments = Integer.parseInt(tfMaxNum.getText());
			ArrayList<FilamentBean> suitableFilaments = 
					standardUserSession.findFilamentsByNumOfSegments(minNumOfSegments, maxNumOfSegments);
			isTaskRunning = false;
			return suitableFilaments;
		}
		
	}
	
	
	// l'handler intercetta l'oggetto ritornato dal task
	private class TaskReturnHandler implements EventHandler<WorkerStateEvent> {

		@Override
		public void handle(WorkerStateEvent event) {
			progressIndicator.setVisible(false);
			ArrayList<FilamentBean> suitableFilaments;
			suitableFilaments = task.getValue();
			// se nessun filamento risponde ai requisiti indicati allora si mostra una finestra d'informazione all'utente
			if (suitableFilaments.isEmpty()) {
				WindowManager.getInstance().openInfoWindow(NOT_SUITABLE_FILAMENTS);
				return;
			}
			// se sono stati trovati filamenti compatibili con le richieste vengono visualizzati in forma tabellare
			// la visualizzazione per una migiliore fruibilità avviene all'interno di una nuova finestra
			try {
				NumOfSegmentsResearchResultController.setSuitableFilaments(suitableFilaments);
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
					 labelErrorMessage.setText(INVALID_NUM_OF_SEGMENTS_FORMAT);
				 } else if (exception instanceof InvalidNumOfSegmentsError) {
					 labelErrorMessage.setText(INVALID_NUM_OF_SEGMENTS);
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
		if (tfMinNum.getText().equals("") || tfMaxNum.getText().equals("")) {
			labelErrorMessage.setText(NOT_COMPLETED_FIELDS);
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
		labelErrorMessage.setText("");
	}

}
