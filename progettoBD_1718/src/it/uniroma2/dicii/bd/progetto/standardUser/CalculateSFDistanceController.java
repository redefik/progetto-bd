package it.uniroma2.dicii.bd.progetto.standardUser;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.errorLogic.ErrorType;
import it.uniroma2.dicii.bd.progetto.errorLogic.FilamentWithoutStarsError;
import it.uniroma2.dicii.bd.progetto.errorLogic.GUIError;
import it.uniroma2.dicii.bd.progetto.errorLogic.NotFoundBackBoneError;
import it.uniroma2.dicii.bd.progetto.errorLogic.NotFoundFilamentError;
import it.uniroma2.dicii.bd.progetto.gui.WindowManager;
import it.uniroma2.dicii.bd.progetto.star.StarBeanWithMinDistance;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class CalculateSFDistanceController {
	@FXML
	private ProgressIndicator progressIndicator;
	@FXML
	private AnchorPane window;
	@FXML
	private Button showStarsAndDistancesInATable;
	@FXML
	private TextField filament;
	
	private boolean isTaskRunning;
	private ArrayList<StarBeanWithMinDistance> starFilamentDistances = new ArrayList<>();
	private static final String TASK_IS_RUNNING = "Attendere il completamento dell'operazione";
	private static final String FILAMENT_NOT_FOUND = "Segmento non trovato";
	private static final String BACK_BONE_NOT_FOUND = "Il filamento non ha una spina dorsale";
	private static final String FILAMENT_WITHOUT_STARS = "Il filamento non contiene stelle";
	private static final String GO_TO_TABLE_VIEW = "../gui/tableSFDistanceView.fxml";
	
	@FXML
	public void initialize () {
		progressIndicator.setVisible(false);
		isTaskRunning = false;
		WindowManager.getInstance().setWindow(window);
		showStarsAndDistancesInATable.setVisible(false);
	}
	
	public void gotoPreviousMenu() {
		try {
			if (isTaskRunning) {
				WindowManager.getInstance().openInfoWindow(TASK_IS_RUNNING);
				return;
			}
			WindowManager.getInstance().goToPreviousMenu();
		} catch (GUIError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.GUI);
		}
	}
	
	@FXML
	public void calculate() {
		if (isTaskRunning) {
			WindowManager.getInstance().openInfoWindow(TASK_IS_RUNNING);
			return;
		}
		progressIndicator.setVisible(true);
		// l'operazione, che risulta costosa, viene collocata su un thread separato da quello responsabile del disegno della GUI
		CalculateSFDistanceTask task = new CalculateSFDistanceTask();
		progressIndicator.progressProperty().bind(task.progressProperty());
		ExceptionListener exceptionListener = new ExceptionListener();
		task.exceptionProperty().addListener(exceptionListener);
		// si lancia il task
		Thread taskThread = new Thread(task);
		taskThread.setDaemon(true);
		taskThread.start();
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
				} else if (exception instanceof NotFoundFilamentError) {
					WindowManager.getInstance().openInfoWindow(FILAMENT_NOT_FOUND);
				} else if (exception instanceof NotFoundBackBoneError) {
					WindowManager.getInstance().openInfoWindow(BACK_BONE_NOT_FOUND);
				} else if (exception instanceof FilamentWithoutStarsError) {
					WindowManager.getInstance().openInfoWindow(FILAMENT_WITHOUT_STARS);
				}
			}
		}
	}
	
	private class CalculateSFDistanceTask extends Task<Void> {

		@Override
		protected Void call() throws Exception {
			isTaskRunning = true;
			updateProgress(-1, 1);			
			String filamentName = filament.getText();
			
			// calcolo la distanza tra le stelle contenute all'interno del filamento e la sua spina dorsale
			starFilamentDistances = StandardUserSession.getInstance().calculateSFDistance(filamentName);

			updateProgress(1, 1);
			isTaskRunning = false;
			showStarsAndDistancesInATable.setVisible(true);
			return null;
		}
	}
	
	// La funzione fa aprire una tabella con i risultati appena ottenuti
		public void showStarFilamentDistances() {
			try {
				TableSFDistanceController.setStarFilamentDistances(starFilamentDistances);
				WindowManager.getInstance().openWindow(GO_TO_TABLE_VIEW);
				showStarsAndDistancesInATable.setVisible(false);
			} catch (GUIError e) {
				Logger.getLogger(getClass()).error(e.getMessage(), e);
				WindowManager.getInstance().openErrorWindow(ErrorType.GUI);
				gotoPreviousMenu();
			}	
		}
}
