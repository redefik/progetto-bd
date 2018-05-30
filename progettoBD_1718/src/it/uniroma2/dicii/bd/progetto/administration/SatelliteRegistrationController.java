package it.uniroma2.dicii.bd.progetto.administration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.errorLogic.ErrorType;
import it.uniroma2.dicii.bd.progetto.errorLogic.GUIError;
import it.uniroma2.dicii.bd.progetto.gui.WindowManager;
import it.uniroma2.dicii.bd.progetto.satellite.AgencyBean;
import it.uniroma2.dicii.bd.progetto.satellite.SatelliteBean;
import it.uniroma2.dicii.bd.progetto.satellite.SelectableAgencyBean;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

public class SatelliteRegistrationController {
	
	private static final String INVALID_RANGE_DATE_MESSAGE = 
			"La data di inizio attività deve essere precedente a quella di fine.";
	private static final String NOT_INSERTED_DATE = "Inserire una data di inizio attività.";
	private static final String INVALID_DATE_MESSAGE = "Inserire le date nel formato DD/MM/AAAA.";
	private static final String NOT_INSERTED_NAME = "Inserire il nome per il Satellite.";
	private static final String NOT_INSERTED_AGENCY = "Specificare almeno un'agenzia.";
	private static final String INVALID_NAME_MESSAGE = "Il satellite specificato è già presente.";
	private static final String INSERT_CONFIRMED = "Inserimento del satellite effettuato con successo.";
	
	@FXML
	private AnchorPane window;
	
	@FXML
	private TextField name;
	
	@FXML
	private TextField begin;
	
	@FXML
	private TextField end;
	
	@FXML
	private Label errorMessage;
	
	@FXML
	private TableView<SelectableAgencyBean> tableAgency;
	
	private ArrayList<SelectableAgencyBean> agencyBeans;
	
	@FXML
	private TableColumn<SelectableAgencyBean, String> agencyName;
	
	@FXML
	private TableColumn<SelectableAgencyBean, CheckBox> agencySelection;
	
	@FXML 
	public void clearMessage() {
		errorMessage.setText("");
	}
	
	@FXML
	public void initialize() {
		try {
			WindowManager.getInstance().setWindow(window);
			
			// Si inizializza la tabella con tutte le agenzie presenti nel DB. In questo modo l'utente amministratore
			// puo' selezionare tra quelle presenti le agenzie che hanno partecipato alla missione relativa al satellite
			tableAgency.setEditable(true);
			agencyName.setCellValueFactory(new PropertyValueFactory<SelectableAgencyBean, String>("name"));
	        agencySelection.setCellValueFactory(new PropertyValueFactory<SelectableAgencyBean, CheckBox>("checkBox"));
	        
	        ArrayList<AgencyBean> agencies = AdministrationSession.getInstance().findAllAgencies();
	        
	        agencyBeans = new ArrayList<>();
	        for (AgencyBean agency : agencies) {
	        	agencyBeans.add(new SelectableAgencyBean(agency.getName()));
	        }
	        
	        ObservableList<SelectableAgencyBean> selectableAgencyBeans = FXCollections.observableArrayList(agencyBeans);
	        tableAgency.setItems(selectableAgencyBeans);
	        
		} catch (DataAccessError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.DATA_ACCESS);
		} catch (ConfigurationError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.CONFIGURATION);
		}
	}

	public void registerSatellite() {
		try {
			Date beginDate = null;
			Date endDate = null;
			String satelliteName;
			
			// Si verifica che sia stato inserito un nome
			if (name.getText().equals("")) {
				errorMessage.setText(NOT_INSERTED_NAME);
				return;
			}
			
			satelliteName = name.getText();
			
			SimpleDateFormat dateParser = new SimpleDateFormat("dd/MM/yyyy");
			
			// Si verifica che sia stata inserita una data di inizio attivita' e in caso affermativo 
			// si verifica anche che sia nel formato corretto
			if (begin.getText().equals("")) {
				errorMessage.setText(NOT_INSERTED_DATE);
				return;
			}
			beginDate = dateParser.parse(begin.getText());
			
			// Se la data di termine attivita' e' stata inserita si controlla se e' nel formato corretto
			if (!end.getText().equals("")) {
				endDate = dateParser.parse(end.getText());
			}
		
			// Si verifica che la data di termine attivita' sia successiva a quella di inizio
			if (endDate!= null && beginDate.after(endDate)) {
				errorMessage.setText(INVALID_RANGE_DATE_MESSAGE);
				return;
			}
			
			// Si crea una lista con tutte le agezie che hanno partecipato alla missione relativa al satellite
			ArrayList<AgencyBean> selectedAgencies = new ArrayList<AgencyBean>();
			for (SelectableAgencyBean elem : agencyBeans) {
				if (elem.getCheckBox().isSelected()) {
					selectedAgencies.add(new AgencyBean(elem.getName()));
				}
			}
			
			if (selectedAgencies.isEmpty()) {
				errorMessage.setText(NOT_INSERTED_AGENCY);
				return;
			}
			
			// Se i controlli vengono superati si registra il satellite
	        SatelliteBean satelliteBean = new SatelliteBean(satelliteName, beginDate, endDate);
	        AdministrationSession.getInstance().registerSatellite(satelliteBean, selectedAgencies);
	        
	        WindowManager.getInstance().openInfoWindow(INSERT_CONFIRMED);
	        gotoPreviousMenu();
	        
		} catch (ParseException e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			errorMessage.setText(INVALID_DATE_MESSAGE);

		} catch(ConfigurationError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.CONFIGURATION);
		} catch(DataAccessError e) {
			// Se l'inserimento fallisce si controlla che non esista gia' un satellite con il nome specificato
			try {
				if (AdministrationSession.getInstance().isAvailableName(name.getText())) {
					errorMessage.setText(INVALID_NAME_MESSAGE);
				}else {
					Logger.getLogger(getClass()).error(e.getMessage(), e);
					WindowManager.getInstance().openErrorWindow(ErrorType.DATA_ACCESS);
				}
			} catch (DataAccessError e1) {
				Logger.getLogger(getClass()).error(e1.getMessage(), e1);
				WindowManager.getInstance().openErrorWindow(ErrorType.DATA_ACCESS);
			} catch (ConfigurationError e1) {
				Logger.getLogger(getClass()).error(e1.getMessage(), e1);
				WindowManager.getInstance().openErrorWindow(ErrorType.CONFIGURATION);
			}
		} 
	}
	
	public void gotoPreviousMenu() {
		try {
			WindowManager.getInstance().goToPreviousMenu();
		} catch (GUIError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.GUI);
		}
	}
	
}
