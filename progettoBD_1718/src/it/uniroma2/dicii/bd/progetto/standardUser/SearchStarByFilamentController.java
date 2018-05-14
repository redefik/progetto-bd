package it.uniroma2.dicii.bd.progetto.standardUser;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Map;
import org.apache.log4j.Logger;
import it.uniroma2.dicii.bd.progetto.star.StarBean;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.errorLogic.ErrorType;
import it.uniroma2.dicii.bd.progetto.errorLogic.GUIError;
import it.uniroma2.dicii.bd.progetto.gui.WindowManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

public class SearchStarByFilamentController {
	
	private static final String NOT_INSERTED_NAME = "Specificare il nome del filamento all'interno del quale cercare le stelle.";
	private static final String NOT_VALID_NAME = "Il filamento specificato non è esistente.";
	private static final String OUTPUT1 = "Sono state trovate ";
	private static final String OUTPUT2 = " stelle all'interno del filamento specificato. ";
	private static final String OUTPUT3	= "\n\nLe percentuali per ogni tipo sono: ";
	//private static final String OUTPUT4 = "\n Nello specifico le stelle trovate sono: ";
	private static final String TASK_IS_RUNNING = "Attendere il completamento dell'operazione";

	@FXML 
	private AnchorPane window;
	@FXML 
	private TextField filamentName;
	@FXML 
	private TextArea outputArea;
	@FXML 
	private Label errorMessage;
	@FXML
	private ProgressIndicator progressIndicator;
	
	private boolean isTaskRunning;
	private String filament;
	private SearchStarByFilamentTask task;
	
	@FXML 
	public void initialize () {
		progressIndicator.setVisible(false);
		isTaskRunning = false;
		WindowManager.getInstance().setWindow(window);
	}
	
	private class SearchStarByFilamentTask extends Task<String> {

		@Override
		protected String call() throws ConfigurationError, DataAccessError  {
			
			isTaskRunning = true;
			updateProgress(-1, 1);
			
			//Si ottiene la lista di tutte le stelle contenute all'interno del filamento
			ArrayList<StarBean> starBeans = StandardUserSession.getInstance().searchStarsIntoFilament(filament);
	
			//Si delega all'istanza di StandardUserSession il calcolo della percentuale per ogni tipo di stella
			Map<String, Double> percentageStarTypes = StandardUserSession.getInstance().getPercentageStarTypes(starBeans);
			
			String output;
			
			output = OUTPUT1 + starBeans.size() + OUTPUT2;
			
			if (starBeans.size() != 0) {
				
				output += OUTPUT3;
				
				NumberFormat nf = new DecimalFormat("0.00");

				for (String type : percentageStarTypes.keySet()) {
					output += "\n     " + nf.format(percentageStarTypes.get(type)) + "% " + type;
				}
				
				//Decommentare le seguenti quattro righe se si vuole l'elenco di stelle trovate nel filamento.
				//Se le stelle sono tante, l'operazione di stampa potrebbe richiedere qualche minuto.
		
//		 		output += OUTPUT4;
//		 		for (StarBean star : starBeans) {
//		 			output += "\n     " + star;
//		 		}
			}
			
			updateProgress(1, 1);
			isTaskRunning = false;
			return output;
		}
		
	}
	
	//Il listener intercetta le eccezioni eventualmente sollevate dal task
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
				}
			}		
		}
	}
	
	//L'handler per gestire il valore di ritorno computato dal task
	private class TaskReturnHandler implements EventHandler<WorkerStateEvent> {

		@Override
		public void handle(WorkerStateEvent event) {
		
			String output = task.getValue();
			outputArea.setText(output);
			outputArea.setVisible(true);
				
		}
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
	
	public void searchStarsByFilament()  {
		
		try {
			
			if (isTaskRunning) {
				WindowManager.getInstance().openInfoWindow(TASK_IS_RUNNING);
				return;
			}
			
			//Si controlla che l'utente abbia specificato un nome per il filamento 
			if (filamentName.getText().equals("")) {
				errorMessage.setText(NOT_INSERTED_NAME);
				return;
			} 
			
			this.filament = filamentName.getText();
			 
			//Si controlla che il filamento specificato esista tra quelli presenti in persistenza
			if (!StandardUserSession.getInstance().isValidFilamentName(filament)) {
				errorMessage.setText(NOT_VALID_NAME);
				return;
			} 
			
			progressIndicator.setVisible(true);
			//l'operazione, che risulta costosa, viene collocata su un thread separato da quello responsabile del disegno della GUI
			task = new SearchStarByFilamentTask();
			progressIndicator.progressProperty().bind(task.progressProperty());
			ExceptionListener exceptionListener = new ExceptionListener();
			task.exceptionProperty().addListener(exceptionListener);
			TaskReturnHandler taskReturnHandler = new TaskReturnHandler();
			task.setOnSucceeded(taskReturnHandler);
			Thread taskThread = new Thread(task);
			taskThread.setDaemon(true);
			taskThread.start();
			
		}catch (DataAccessError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.DATA_ACCESS);
			
		} catch (ConfigurationError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.CONFIGURATION);
		} 
	}

	

	@FXML 
	public void clearMessage() {
		
		if (isTaskRunning) {
			WindowManager.getInstance().openInfoWindow(TASK_IS_RUNNING);
			return;
		}
		errorMessage.setText("");
		outputArea.setVisible(false);
		progressIndicator.setVisible(false);
	}
}
