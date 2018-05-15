package it.uniroma2.dicii.bd.progetto.standardUser;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import it.uniroma2.dicii.bd.progetto.errorLogic.BatchError;
import it.uniroma2.dicii.bd.progetto.errorLogic.CSVFileParserException;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.errorLogic.ErrorType;
import it.uniroma2.dicii.bd.progetto.errorLogic.GUIError;
import it.uniroma2.dicii.bd.progetto.filament.FilamentBean;
import it.uniroma2.dicii.bd.progetto.gui.WindowManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class SearchFilamentByRegionController {
	@FXML
	private AnchorPane window;
	@FXML
	private TextField latitude;
	@FXML
	private TextField longitude;
	@FXML
	private TextField size;
	@FXML
	private ComboBox<String> shape;
	@FXML
	private Label lenghtLabel;
	@FXML
	private Label errorMessage;
	@FXML
	private ProgressIndicator progressIndicator;
	@FXML
	private Button showFilamentsInATable;

	private boolean isTaskRunning;
	private String chosenShape;
	private ArrayList<FilamentBean> filamentBeans;
	private ArrayList<String> shapes;
	private double galacticLatitude;
	private double galacticLongitude;
	private double lenght;
	private static final String TASK_IS_RUNNING = "Attendere il completamento dell'operazione";
	private static final String GO_TO_TABLE_VIEW = "../gui/tableFilamentsView.fxml";

	@FXML
	public void initialize(){
		showFilamentsInATable.setVisible(false);
		isTaskRunning = false;
		progressIndicator.setVisible(false);
		WindowManager.getInstance().setWindow(window);
		errorMessage.setText("");
		ObservableList<String> observableShapes;
        shapes = new ArrayList<>();
        shapes.add("CERCHIO");
        shapes.add("QUADRATO");
        observableShapes = FXCollections.observableArrayList(shapes);
        shape.setItems(observableShapes);
        shape.setValue(shapes.get(0));
        lenghtLabel.setText("Raggio:");
	}
	
	@FXML
	public void find() {
		if (isTaskRunning) {
			WindowManager.getInstance().openInfoWindow(TASK_IS_RUNNING);
			return;
		}
		clearMessage();
		progressIndicator.setVisible(true);
		// l'operazione, che risulta costosa, viene collocata su un thread separato da quello responsabile del disegno della GUI
		SearchByRegionTask task = new SearchByRegionTask();
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
				} else if (exception instanceof NumberFormatException) {
					Logger.getLogger(getClass()).error(exception.getMessage(), exception);
					errorMessage.setText("Inserire dati validi nei campi");
				}
			}		
		}
	}
	
	private class SearchByRegionTask extends Task<Void> {

		@Override
		protected Void call() throws ConfigurationError, CSVFileParserException, DataAccessError, BatchError {
			isTaskRunning = true;
			updateProgress(-1, 1);
			
			//Si instanzia mediante l'uso di una factory un parser per il file da importare
			galacticLatitude = Double.parseDouble(latitude.getText());
			galacticLongitude = Double.parseDouble(longitude.getText());
			lenght = Double.parseDouble(size.getText());
			chosenShape = shape.getSelectionModel().getSelectedItem();
			
			// trovo tutti i filamenti in una regione circolare o quadrata, in base alla scelta fatta
			filamentBeans = StandardUserSession.getInstance().findFilamentsInARegion(galacticLongitude,galacticLatitude,lenght,chosenShape);
			
			updateProgress(1, 1);
			isTaskRunning = false;
			showFilamentsInATable.setVisible(true);
			return null;
		}
	}

	// La funzione fa aprire una tabella con i risultati appena ottenuti
	public void showFilaments() {
		try {
			TableFilamentsController.setFilaments(filamentBeans);
			WindowManager.getInstance().openWindow(GO_TO_TABLE_VIEW);
			showFilamentsInATable.setVisible(false);
		} catch (GUIError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.GUI);
		}	
	}

	// La funzione cambia scrive raggio o lato in base al fatto se è stato scelto cerchio o quadrato
	@FXML
	public void changeLenghtLabel() {
		if(shape.getSelectionModel().getSelectedItem()=="CERCHIO") {
			lenghtLabel.setText("Raggio:");
		} else {
			lenghtLabel.setText("Lato:");
		}
	}

	@FXML 
	public void clearMessage() {
		errorMessage.setText("");
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
