package it.uniroma2.dicii.bd.progetto.standardUser;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.errorLogic.ErrorType;
import it.uniroma2.dicii.bd.progetto.errorLogic.GUIError;
import it.uniroma2.dicii.bd.progetto.errorLogic.NotFoundError;
import it.uniroma2.dicii.bd.progetto.filament.SegmentPoint;
import it.uniroma2.dicii.bd.progetto.gui.WindowManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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
	private TextField satellite;
	@FXML
	private Label errorMessage;
	@FXML
	private ProgressIndicator progressIndicator;
	
	private boolean isTaskRunning;
	private CalculateTask task;
	private static final String TASK_IS_RUNNING = "Attendere il completamento dell'operazione";
	
	@FXML
	public void initialize() {
		isTaskRunning = false;
		progressIndicator.setVisible(false);
		WindowManager.getInstance().setWindow(window);
		errorMessage.setText("");
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
					errorMessage.setText("Inserire correttamente i tipi di dato");
				} else if (exception instanceof NullPointerException) {
					WindowManager.getInstance().openErrorWindow(ErrorType.CONFIGURATION);
				} else if (exception instanceof NotFoundError) {
					WindowManager.getInstance().openInfoWindow("Segmento non trovato");
				}
			}		
		}
	}

	private class Distances {
		private double firstMinDistance;
		private double secondMinDistance;
		
		Distances () {}
		
		public double getFirstMinDistance() { return firstMinDistance; }

		public void setFirstMinDistance(double firstMinDistance) { this.firstMinDistance = firstMinDistance; }

		public double getSecondMinDistance() { return secondMinDistance; }

		public void setSecondMinDistance(double secondMinDistance) { this.secondMinDistance = secondMinDistance; }

	}
	
	private class TaskReturnHandler implements EventHandler<WorkerStateEvent>{

		@Override
		public void handle(WorkerStateEvent event) {
			progressIndicator.setVisible(false);
			Distances distances = task.getValue();
			System.out.println(distances.getFirstMinDistance());
			WindowManager.getInstance().openInfoWindow("Distanza minima dal primo estremo: " + distances.getFirstMinDistance() 
					+ "\nDistanza minima dal secondo estremo: " + distances.getSecondMinDistance());
			isTaskRunning = false;
		}
		
	}

	private class CalculateTask extends Task<Distances> {

		@Override
		protected Distances call() throws ConfigurationError, DataAccessError, NotFoundError {
			isTaskRunning = true;
			Distances distances = new Distances();
			updateProgress(-1, 1);
			int idSegment = Integer.parseInt(segment.getText());
			String filamentName = filament.getText();
			String satelliteName = satellite.getText();
			ArrayList<SegmentPoint> segment = new ArrayList<>();
			segment = StandardUserSession.getInstance().findSegment(filamentName,idSegment);
			
			SegmentPoint begin = new SegmentPoint();
			begin = StandardUserSession.getInstance().getFirst(segment);
			SegmentPoint end = new SegmentPoint();
			end = StandardUserSession.getInstance().getLast(segment);
			
			if(begin!=null && end!=null) {			
				double firstMinDinstance = StandardUserSession.getInstance().misureMinDinstance(begin,satelliteName);
				double secondMinDistance = StandardUserSession.getInstance().misureMinDinstance(end,satelliteName);
				distances.setFirstMinDistance(firstMinDinstance);
				distances.setSecondMinDistance(secondMinDistance);
				return distances;
				}

			updateProgress(1, 1);
			return null;			
		}
	}

	public void calculate() {
		if (isTaskRunning) {
			WindowManager.getInstance().openInfoWindow(TASK_IS_RUNNING);
			return;
		}
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
	
	/*
	public void calculate() throws ConfigurationError, DataAccessError {
		int idSegment = Integer.parseInt(segment.getText());
		String filamentName = filament.getText();
		String satelliteName = satellite.getText();
		ArrayList<SegmentPoint> segment = new ArrayList<>();
		segment = StandardUserSession.getInstance().findSegment(filamentName,idSegment);
		SegmentPoint begin = StandardUserSession.getInstance().getFirst(segment);
		SegmentPoint end = StandardUserSession.getInstance().getLast(segment);
		if(begin!=null && end!=null) {
			double firstMinDinstance = StandardUserSession.getInstance().misureMinDinstance(begin,satelliteName);
			double secondMinDistance = StandardUserSession.getInstance().misureMinDinstance(end,satelliteName);
			WindowManager.getInstance().openInfoWindow("Prima distanza minima: " + firstMinDinstance 
					+ "\nSeconda distanza minima: " + secondMinDistance + "\n");
		} else {
			errorMessage.setText("Segmento non trovato");
		}
	}
	*/

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
