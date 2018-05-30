package it.uniroma2.dicii.bd.progetto.standardUser;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import it.uniroma2.dicii.bd.progetto.administration.AdministrationSession;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.errorLogic.ErrorType;
import it.uniroma2.dicii.bd.progetto.errorLogic.GUIError;
import it.uniroma2.dicii.bd.progetto.errorLogic.NotFoundBorderError;
import it.uniroma2.dicii.bd.progetto.errorLogic.NotFoundSegmentPointError;
import it.uniroma2.dicii.bd.progetto.gui.WindowManager;
import it.uniroma2.dicii.bd.progetto.satellite.SatelliteBean;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class CalculateSBDistanceController {
	@FXML
	private AnchorPane window;
	@FXML
	private TextField filament;
	@FXML
	private TextField segment;
	@FXML
	private ComboBox<SatelliteBean> satellite;
	@FXML
	private Label errorMessage;
	@FXML
	private ProgressIndicator progressIndicator;
	
	private boolean isTaskRunning;
	private CalculateTask task;
	private ArrayList<SatelliteBean> satelliteBeans = new ArrayList<>();
	private static final String TASK_IS_RUNNING = "Attendere il completamento dell'operazione";
	private static final String SEGMENT_NOT_FOUND = "Segmento non trovato";
	private static final String FILAMENT_WITHOUT_SEGMENT_POINTS = "Il filamento non ha punti del segmento";
	private static final String NOT_VALID_INPUT= "Inserire dati validi nei campi";
	
	@FXML
	public void initialize() {
		isTaskRunning = false;
		progressIndicator.setVisible(false);
		WindowManager.getInstance().setWindow(window);
		errorMessage.setText("");
		ObservableList<SatelliteBean> observableSatelliteBeans;
	    satelliteBeans = new ArrayList<>();
		try {
		    satelliteBeans = AdministrationSession.getInstance().findAllSatellites();
		    observableSatelliteBeans = FXCollections.observableArrayList(satelliteBeans);
		    satellite.setItems(observableSatelliteBeans);
		    if(!satelliteBeans.isEmpty()) {
		    	satellite.setValue(satelliteBeans.get(0));
		    }
		   
		} catch (DataAccessError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.DATA_ACCESS);
		} catch (ConfigurationError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.CONFIGURATION);
		}
	}

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
					errorMessage.setText(NOT_VALID_INPUT);
				} else if (exception instanceof NullPointerException) {
					WindowManager.getInstance().openErrorWindow(ErrorType.CONFIGURATION);
				} else if (exception instanceof NotFoundBorderError) {
					WindowManager.getInstance().openInfoWindow(SEGMENT_NOT_FOUND);
				} else if (exception instanceof NotFoundSegmentPointError)  {
					WindowManager.getInstance().openInfoWindow(FILAMENT_WITHOUT_SEGMENT_POINTS);
				}
			}		
		}
	}
	
	// l'handler intercetta l'oggetto ritornato dal task
	private class TaskReturnHandler implements EventHandler<WorkerStateEvent>{

		@Override
		public void handle(WorkerStateEvent event) {
			progressIndicator.setVisible(false);
			HashMap<String,Double> distances = task.getValue();
			WindowManager.getInstance().openInfoWindow("Distanza minima dal primo estremo: " + distances.get("firstMinDinstance") 
					+ "\nDistanza minima dal secondo estremo: " + distances.get("secondMinDistance"));
			isTaskRunning = false;
		}
		
	}

	private class CalculateTask extends Task<HashMap<String,Double>> {

		@Override
		protected HashMap<String,Double> call() throws ConfigurationError, DataAccessError, NotFoundBorderError, NotFoundSegmentPointError {
			isTaskRunning = true;
			// uso un HashMap che contiene le distanze minime tra i due estremi del segmento e i punti del contorno
			HashMap<String,Double> distances = new HashMap<>(); 
			updateProgress(-1, 1);
			int idSegment = Integer.parseInt(segment.getText());
			String filamentName = filament.getText();
			String satelliteName = satellite.getSelectionModel().getSelectedItem().getName();

			distances = StandardUserSession.getInstance().calculateMinDistanceFromSegmentToBorder(filamentName, idSegment, satelliteName);

			updateProgress(1, 1);
			return distances;			
		}
	}

	public void calculate() {
		if (isTaskRunning) {
			WindowManager.getInstance().openInfoWindow(TASK_IS_RUNNING);
			return;
		}
		errorMessage.setText("");
		progressIndicator.setVisible(true);
		// l'operazione, che risulta costosa, viene collocata su un thread separato da quello responsabile del disegno della GUI
		task = new CalculateTask();
		progressIndicator.progressProperty().bind(task.progressProperty());
		ExceptionListener exceptionListener = new ExceptionListener();
		task.exceptionProperty().addListener(exceptionListener);
		TaskReturnHandler taskReturnHandler = new TaskReturnHandler();
		task.setOnSucceeded(taskReturnHandler);
		// si lancia il task
		Thread taskThread = new Thread(task);
		taskThread.setDaemon(true);
		taskThread.start();	
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
	
}
