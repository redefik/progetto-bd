package it.uniroma2.dicii.bd.progetto.standardUser;

import java.util.ArrayList;
import java.util.Iterator;

import it.uniroma2.dicii.bd.progetto.filament.ContrastEllipticityResearchResult;
import it.uniroma2.dicii.bd.progetto.filament.FilamentBean;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;

public class ContrastEllipticityResearchResultController {

	@FXML 
	private Label labelMinEllipticity;
	@FXML 
	private Label labelMaxEllipticity;
	@FXML
	private Label labelMinBrightness;
	@FXML
	private Label labelResult;
	@FXML 
	private TableColumn<FilamentBean, String> tColName;
	@FXML
	private TableColumn<FilamentBean, Integer> tColId;
	@FXML
	private TableColumn<FilamentBean, Double> tColEllipticity;
	@FXML
	private TableColumn<FilamentBean, Double> tColContrast;
	@FXML
	private TableColumn<FilamentBean, Integer> tColSegments;
	@FXML
	private TableColumn<FilamentBean, String> tColInstrument;
	@FXML
	private TableView<FilamentBean> tableFilaments;
	@FXML
	private Button btnNextFilaments;
	@FXML
	private Button btnPreviousFilaments;
	
	private static int MAX_TABLE_ROWS = 20;
	private static ContrastEllipticityResearchResult researchResult;
	private ArrayList<FilamentBean> suitableFilaments;
	private long totalSuitableFilaments;
	private ObservableList<FilamentBean> observableFilaments;
	// l'attributo memorizza la posizione del primo elemento di suitableFilaments appartenente al successivo gruppo di MAX_TABLE_ROWS filamenti
	private int firstNextIndex;
	// l'attributo memorizza la posizione del primo elemento di suitableFilaments appartenente al gruppo precedente di MAX_TABLE_ROWS filamenti
	private int firstPreviousIndex;
	
	
	
	public static void setResearchResult(ContrastEllipticityResearchResult researchResult) {
		ContrastEllipticityResearchResultController.researchResult = researchResult;
	}

	@FXML
	public void initialize() {
		labelMinEllipticity.setText(String.valueOf(researchResult.getMinEllipticity()));
		labelMaxEllipticity.setText(String.valueOf(researchResult.getMaxEllipticity()));
		labelMinBrightness.setText(String.valueOf(researchResult.getMinBrightness()));
		labelResult.setText(String.valueOf(researchResult.getSuitableFilamentsFraction() * 100));
		// viene creato il bind tra le colonne della tabella e gli attributi di FilamentBean
		tColName.setCellValueFactory(new PropertyValueFactory<FilamentBean, String>("name"));
		tColId.setCellValueFactory(new PropertyValueFactory<FilamentBean, Integer>("number"));
		tColEllipticity.setCellValueFactory(new PropertyValueFactory<FilamentBean, Double>("ellipticity"));
		tColContrast.setCellValueFactory(new PropertyValueFactory<FilamentBean, Double>("contrast"));
		tColSegments.setCellValueFactory(new PropertyValueFactory<FilamentBean, Integer>("numberOfSegments"));
		tColInstrument.setCellValueFactory(new PropertyValueFactory<FilamentBean, String>("instrumentName"));
		// si inizializza la tabella con i primi MAX_TABLE_ROWS (al più) filamenti trovati
		suitableFilaments = researchResult.getSuitableFilaments();
		totalSuitableFilaments = suitableFilaments.size();
		ArrayList<FilamentBean> visibleFilaments = new ArrayList<FilamentBean>();
		int i;
		for (i = 0; i != totalSuitableFilaments && i < MAX_TABLE_ROWS; ++i) {
			visibleFilaments.add(suitableFilaments.get(i));
		}
		firstNextIndex = i;
		// se i filamenti trovati sono MAX_TABLE_ROWS o meno allora si disabilita il tasto per mostrare i successivi MAX_TABLE_ROWS 
		if (firstNextIndex == totalSuitableFilaments) {
			btnNextFilaments.setDisable(true);
		}
		// il bottone per la navigazione indietro viene disabilitato perchè i filamenti visualizzati sono i primi in lista
		btnPreviousFilaments.setDisable(true);
		// le modifiche sull'observableList si ripercuoteranno automaticamente sulla tabella
		observableFilaments = FXCollections.observableArrayList(visibleFilaments);
	    tableFilaments.setItems(observableFilaments);
	}

	public void visualizeNextFilaments() {
		//il bottone per la navigazione indietro viene abilitato se non già abilitato
		btnPreviousFilaments.setDisable(false);
		// si rimuovono dall'observableList i filamenti attualmente visualizzati
		clearObservableList();
		// si memorizza la posizione di partenza del gruppo di filamenti correntemente visualizzato
		firstPreviousIndex = firstNextIndex - MAX_TABLE_ROWS;
		// si aggiungono i prossimi MAX_TABLE_ROWS (al più) filamenti all'observableList
		int i;
		for (i = firstNextIndex; i != totalSuitableFilaments && i < firstNextIndex + MAX_TABLE_ROWS; ++i) {
			observableFilaments.add(suitableFilaments.get(i));
		}
		firstNextIndex = i;
		// se i filamenti sono finiti allora si disabilita il bottone
		if (firstNextIndex == totalSuitableFilaments) {
			btnNextFilaments.setDisable(true);
		}
	}

	public void visualizePreviousFilaments() {
		// il bottone per la navigazione in avanti viene abilitato se non già abilitato
		btnNextFilaments.setDisable(false);
		// si rimuovono dall'observableList i filamenti correntemente visualizzati
		clearObservableList();
		int i;
		for (i = firstPreviousIndex; i < firstPreviousIndex + MAX_TABLE_ROWS; ++i) {
			observableFilaments.add(suitableFilaments.get(i));
		}
		firstNextIndex = i;
		firstPreviousIndex -= MAX_TABLE_ROWS;
		// se i filamenti visualizzati sono i primi della lista il bottone viene disabilitato
		if (firstPreviousIndex < 0) {
			btnPreviousFilaments.setDisable(true);
		}
	}
	
	private void clearObservableList() {
		Iterator<FilamentBean> iterator = observableFilaments.iterator();
		while (iterator.hasNext()) {
			iterator.next();
			iterator.remove();
		}
	}
}
