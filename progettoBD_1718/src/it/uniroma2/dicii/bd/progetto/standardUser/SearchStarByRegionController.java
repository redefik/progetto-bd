package it.uniroma2.dicii.bd.progetto.standardUser;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;
import org.apache.log4j.Logger;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.errorLogic.ErrorType;
import it.uniroma2.dicii.bd.progetto.errorLogic.GUIError;
import it.uniroma2.dicii.bd.progetto.gui.WindowManager;
import it.uniroma2.dicii.bd.progetto.star.StarsIntoRegion;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;

public class SearchStarByRegionController {
	
	private static final String NOT_VALID_INPUT_MESSAGE = "I dati relativi al rettangolo in cui cercare le stelle devono essere"
			  + " numeri reali.";
	private static final String NOT_INSERTED_INPUT_MESSAGE = "Inserire i dati relativi al rettangolo in cui cercare le stelle "
					 + "prima di proseguire.";
	private static final String OUTPUT1 = "Sono state trovate ";
	private static final String OUTPUT2 = " stelle all'interno della regione specificata. \nDi queste il ";
	private static final String OUTPUT3 = "% sono interne a filamenti e il ";
	private static final String OUTPUT4 = "% sono esterne a filamenti." ;
	private static final String OUTPUT5 = "\nLe percentuali di ogni tipo per le stelle interne sono: ";
	private static final String OUTPUT6 = "\nLe percentuali di ogni tipo per le stelle esterne sono: ";
	private static final String TASK_IS_RUNNING = "Attendere il completamento dell'operazione";

	@FXML 
	private AnchorPane window;
	@FXML 
	private TextField textFieldLatitude;
	@FXML 
	private TextField textFieldLongitude;
	@FXML 
	private TextField textFieldWidth;
	@FXML 
	private TextField textFieldHeigth;
	@FXML 
	private Label errorMessage;
	@FXML 
	private TextArea outputArea;
	@FXML
	private ProgressIndicator progressIndicator;
	
	private boolean isTaskRunning;
	double latitude, longitude, width, heigth;
	private SearchStarByRegionTask task;
	
	@FXML 
	public void initialize () {
		progressIndicator.setVisible(false);
		isTaskRunning = false;
		WindowManager.getInstance().setWindow(window);
		
	}
	
	private class SearchStarByRegionTask extends Task<String> {

		@Override
		protected String call() throws ConfigurationError, DataAccessError  {
			
			isTaskRunning = true;
			updateProgress(-1, 1);
			
			//Si delega all'istanza di StandardUserSession la ricerca delle stelle nella regione.
			//L'oggetto ritornato di tipo StarsIntoRegion ha due campi corrispondenti a una lista per 
			//le stelle interne ai filamenti e una per quelle esterne
			StarsIntoRegion starIntoRegion;
			starIntoRegion = StandardUserSession.getInstance().searchStarsIntoRegion(latitude, longitude, width, heigth);
			
			//Si delega all'istanza di StandardUserSession il calcolo delle percentuali per entrambi i gruppi di stelle
			Map<String, Double> percentageInternalStarTypes;
			Map<String, Double> percentageExternalStarTypes;
			percentageInternalStarTypes = StandardUserSession.getInstance().getPercentageStarTypes(starIntoRegion.getInternalStars());
			percentageExternalStarTypes = StandardUserSession.getInstance().getPercentageStarTypes(starIntoRegion.getExternalStars());
			
			
			//Si prepara l'output da stampare una volta completata la ricerca
			String output;
			NumberFormat nf = new DecimalFormat("0.00");
			
			output = OUTPUT1 + starIntoRegion.getAllStarNumber() + OUTPUT2 + nf.format(starIntoRegion.getPercentageInternalStar()) +
					OUTPUT3 + nf.format(starIntoRegion.getPercentageExternalStar()) + OUTPUT4;
			
			if (starIntoRegion.getInternalStars().size() != 0) {
				
				output += OUTPUT5;
				
				for (String type : percentageInternalStarTypes.keySet()) {
					output += "\n     " + nf.format(percentageInternalStarTypes.get(type)) + "% " + type;
				}
			
			}
			
			if (starIntoRegion.getExternalStars().size() != 0) {
			
				output += OUTPUT6;
				
				for (String type : percentageExternalStarTypes.keySet()) {
					output += "\n     " + nf.format(percentageExternalStarTypes.get(type)) + "% " + type;
				}
				
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


	public void clearMessage() {
		
		if (isTaskRunning) {
			WindowManager.getInstance().openInfoWindow(TASK_IS_RUNNING);
			return;
		}
		progressIndicator.setVisible(false);
		errorMessage.setText("");
		outputArea.setVisible(false);
	}

	public void searchStarByRegion() {
		
		try {
		
		
			if (isTaskRunning) {
				WindowManager.getInstance().openInfoWindow(TASK_IS_RUNNING);
				return;
			}

			//Si controlla che l'utente abbia inserito tutti i dati relativi al rettangolo in cui effettuare la ricerca
			if (textFieldLatitude.getText().equals("")| textFieldLongitude.getText().equals("") | 
					textFieldWidth.getText().equals("") | textFieldHeigth.getText().equals("")) {
				errorMessage.setText(NOT_INSERTED_INPUT_MESSAGE);
				return;
			}
			
			//Se in questa fase il parsing fallisce si solleva un eccezione che comporta l'apparire di un messaggio di errore
			latitude = Double.parseDouble(textFieldLatitude.getText());
			longitude = Double.parseDouble(textFieldLongitude.getText());
			width = Double.parseDouble(textFieldWidth.getText());
			heigth = Double.parseDouble(textFieldHeigth.getText());
			
			progressIndicator.setVisible(true);
			//l'operazione, che risulta costosa, viene collocata su un thread separato da quello responsabile del disegno della GUI
			task = new SearchStarByRegionTask();
			progressIndicator.progressProperty().bind(task.progressProperty());
			ExceptionListener exceptionListener = new ExceptionListener();
			task.exceptionProperty().addListener(exceptionListener);
			TaskReturnHandler taskReturnHandler = new TaskReturnHandler();
			task.setOnSucceeded(taskReturnHandler);
			Thread taskThread = new Thread(task);
			taskThread.setDaemon(true);
			taskThread.start();
	
		} catch (NumberFormatException e) {
			errorMessage.setText(NOT_VALID_INPUT_MESSAGE);
		} 
	}

}
