package it.uniroma2.dicii.bd.progetto.administration;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.errorLogic.ErrorType;
import it.uniroma2.dicii.bd.progetto.errorLogic.GUIError;
import it.uniroma2.dicii.bd.progetto.gui.WindowManager;
import it.uniroma2.dicii.bd.progetto.satellite.InstrumentBean;
import it.uniroma2.dicii.bd.progetto.satellite.SatelliteBean;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;

public class InstrumentRegistrationController {
	
	private static final String NOT_INSERTED_NAME = "Inserire il nome per lo strumento.";
	private static final String NOT_VALID_LISTBANDS = "Inserire l'elenco di bande nel formato corretto.";
	private static final String INSERT_CONFIRMED = "Inserimento dello strumento effettuato con successo.";
	private static final String ADMINISTRATION_MENU = "../gui/administrationView.fxml";
	private static final String INVALID_NAME_MESSAGE = "Lo strumento specificato è già presente per il satellite selezionato.";

	@FXML
	private AnchorPane window;
	
	@FXML 
	private TextField name;
	
	@FXML 
	private TextField listBands;
	
	@FXML 
	private Label errorMessage;
	
	@FXML 
	private ComboBox<SatelliteBean> satellite;
	
	ArrayList<SatelliteBean> satelliteBeans;

	@FXML 
	public void registerInstrument(){
		
		InstrumentBean instrumentBean = null;
		
		try {
			String instrumentName;
			String instrumentListBands;
			
			// Se l'utente non ha inserito un nome per lo strumento che vuole inserire si mostra un messaggio di errore
			if (name.getText().equals("")) {
				errorMessage.setText(NOT_INSERTED_NAME);
				return;
			} 
			
			instrumentName = name.getText(); 
			
			// Si verifica che l'utente abbia inserito correttamente l'elenco di bande con le quali lavora lo strumento
			if (!parseListBands(listBands.getText())) {
				errorMessage.setText(NOT_VALID_LISTBANDS);
				return;
			}
			
			instrumentListBands = listBands.getText();
			
			instrumentBean = new InstrumentBean (instrumentName, instrumentListBands);
			
			//Si controlla che lo strumento specificato non sia gia' presente per il satellite selezionato
			for (InstrumentBean i : satellite.getSelectionModel().getSelectedItem().getInstrumentBeans() ) {
				if (i.equals(instrumentBean)) {
					errorMessage.setText(INVALID_NAME_MESSAGE);
					return;
				}
			}
			
			AdministrationSession.getInstance().registerInstrument(satellite.getSelectionModel().getSelectedItem(), instrumentBean);
	        
	        WindowManager.getInstance().openInfoWindow(INSERT_CONFIRMED);
			WindowManager.getInstance().changeMenu(ADMINISTRATION_MENU);
		
		} catch (DataAccessError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.DATA_ACCESS);
			
		} catch (ConfigurationError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.CONFIGURATION);
		} catch (GUIError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.GUI);
		}
	}
	
	private boolean parseListBands(String listBands) {
		
		if (listBands.equals("")) {
			return false;
		} 
		
		String[] bands = listBands.split(",");
		
		for (String string : bands) {
			try {
				Double.parseDouble(string);
			} catch (NumberFormatException e) {
				return false;
			}
		}
		
		return true;
	}

	@FXML 
	public void clearMessage() {
		errorMessage.setText("");
	}
	
	@FXML
	public void initialize(){
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

}
