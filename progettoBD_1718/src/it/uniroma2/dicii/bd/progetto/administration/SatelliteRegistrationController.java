package it.uniroma2.dicii.bd.progetto.administration;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.errorLogic.ErrorType;
import it.uniroma2.dicii.bd.progetto.gui.WindowManager;
import it.uniroma2.dicii.bd.progetto.satellite.AgencyBean;
import it.uniroma2.dicii.bd.progetto.satellite.SelectableAgencyBean;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

public class SatelliteRegistrationController {

	@FXML
	private AnchorPane window;
	
	@FXML
	private TextField name;
	
	@FXML
	private TextField begin;
	
	@FXML
	private TextField end;
	
	@FXML
	private TableView<SelectableAgencyBean> tableAgency;
	
	private ArrayList<SelectableAgencyBean> agencyBeans;
	
//	@FXML
//	private TableColumn<SelectableAgencyBean, String> agencyName;
//	
//	@FXML
//	private TableColumn<SelectableAgencyBean, CheckBox> agencySelection;
	
	@FXML
	public void initialize() {
		try {
			WindowManager.getInstance().setWindow(window);
			
			tableAgency.setEditable(true);
			TableColumn<SelectableAgencyBean, String> agencyName = new TableColumn<SelectableAgencyBean, String>("Nome");
	        agencyName.setCellValueFactory(new PropertyValueFactory<SelectableAgencyBean, String>("name"));
	        TableColumn<SelectableAgencyBean, CheckBox> agencySelection = new TableColumn<SelectableAgencyBean, CheckBox>("");
	        agencySelection.setCellValueFactory(new PropertyValueFactory<SelectableAgencyBean, CheckBox>("isSelected"));
	        tableAgency.getColumns().add(agencyName);
	        tableAgency.getColumns().add(agencySelection);
	        
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
		
	}
	
}
