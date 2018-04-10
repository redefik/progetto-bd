package it.uniroma2.dicii.bd.progetto.administration;

import java.util.ArrayList;

import it.uniroma2.dicii.bd.progetto.gui.WindowManager;
import it.uniroma2.dicii.bd.progetto.satellite.AgencyBean;
import it.uniroma2.dicii.bd.progetto.satellite.SelectableAgencyBean;
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
	
//	@FXML
//	private TableColumn<SelectableAgencyBean, String> agencyName;
//	
//	@FXML
//	private TableColumn<SelectableAgencyBean, CheckBox> agencySelection;
	
	@FXML
	public void initialize() {
		WindowManager.getInstance().setWindow(window);
		
		tableAgency.setEditable(true);
		TableColumn<SelectableAgencyBean, String> agencyName = new TableColumn<SelectableAgencyBean, String>("Nome");
        agencyName.setCellValueFactory(new PropertyValueFactory<SelectableAgencyBean, String>("name"));
        TableColumn<SelectableAgencyBean, CheckBox> agencySelection = new TableColumn<SelectableAgencyBean, CheckBox>("");
        agencySelection.setCellValueFactory(new PropertyValueFactory<SelectableAgencyBean, CheckBox>("isSelected"));
        
        //ArrayList<AgencyBean> agencies = AdministrationSession.getInstance().findAllAgencies();
	}
	
}
