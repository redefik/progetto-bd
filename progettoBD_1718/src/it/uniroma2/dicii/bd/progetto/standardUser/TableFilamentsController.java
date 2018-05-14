package it.uniroma2.dicii.bd.progetto.standardUser;

import java.util.ArrayList;
import it.uniroma2.dicii.bd.progetto.filament.FilamentBean;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class TableFilamentsController {
	@FXML
	private TableView<FilamentBean> tableFilament;
	@FXML
	private TableColumn<FilamentBean,String> name;
	@FXML
	private TableColumn<FilamentBean,Integer> id;
	@FXML
	private TableColumn<FilamentBean,Double> ellipticity;
	@FXML
	private TableColumn<FilamentBean,Double> contrast;
	@FXML
	private TableColumn<FilamentBean,Integer> segmentNumber;
	@FXML
	private TableColumn<FilamentBean,String> instrument;

	private static ArrayList<FilamentBean> filaments;

	public static void setFilaments(ArrayList<FilamentBean> filaments) {
		TableFilamentsController.filaments = filaments;
	}

	@FXML
	public void initialize() {

		name.setCellValueFactory(new PropertyValueFactory<FilamentBean, String>("name"));
		id.setCellValueFactory(new PropertyValueFactory<FilamentBean, Integer>("number"));
		ellipticity.setCellValueFactory(new PropertyValueFactory<FilamentBean, Double>("ellipticity"));
		contrast.setCellValueFactory(new PropertyValueFactory<FilamentBean, Double>("contrast"));
		segmentNumber.setCellValueFactory(new PropertyValueFactory<FilamentBean, Integer>("numberOfSegments"));
		instrument.setCellValueFactory(new PropertyValueFactory<FilamentBean, String>("instrumentName"));

		ObservableList<FilamentBean> filamentBeans = FXCollections.observableArrayList(filaments);
		tableFilament.setItems(filamentBeans);

	}
}
