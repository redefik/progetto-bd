package it.uniroma2.dicii.bd.progetto.standardUser;

import java.util.ArrayList;
import java.util.Iterator;
import it.uniroma2.dicii.bd.progetto.star.StarBeanWithMinDistance;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class TableSFDistanceController {
	@FXML
	private TableView<StarBeanWithMinDistance> tableStarBeanWithMinDistance;
	@FXML
	private TableColumn<StarBeanWithMinDistance,String> columnStarName;
	@FXML
	private TableColumn<StarBeanWithMinDistance,Double> columnStarFlow, columnMinDistance;
	@FXML
	private Button nextStarBeanWithMinDistance,previousStarBeanWithMinDistance;
	
	private ArrayList<StarBeanWithMinDistance> suitableStarBeanWithMinDistance;
	private long totalSuitableStarBeanWithMinDistance;
	private ObservableList<StarBeanWithMinDistance> observableStarBeanWithMinDistance;
	// l'attributo memorizza la posizione del primo elemento di suitableStarBeanWithMinDistance appartenente al successivo gruppo di MAX_TABLE_ROWS stelle
	private int firstNextIndex;
	// l'attributo memorizza la posizione del primo elemento di suitableStarBeanWithMinDistance appartenente al gruppo precedente di MAX_TABLE_ROWS stelle
	private int firstPreviousIndex;
	private static ArrayList<StarBeanWithMinDistance> resultStarBeanFilamentDistances;
	private static int MAX_TABLE_ROWS = 20;
	
	public static void setStarFilamentDistances(ArrayList<StarBeanWithMinDistance> starFilamentDistances) {
		TableSFDistanceController.resultStarBeanFilamentDistances = starFilamentDistances;
	}
	
	@FXML
	public void initialize() {
	
		// si inizializza la tabella con le prime MAX_TABLE_ROWS (al più) stelle trovate
		columnStarName.setCellValueFactory(new PropertyValueFactory<StarBeanWithMinDistance,String>("name"));
		columnStarFlow.setCellValueFactory(new PropertyValueFactory<StarBeanWithMinDistance,Double>("flow"));
		columnMinDistance.setCellValueFactory(new PropertyValueFactory<StarBeanWithMinDistance,Double>("minDistanceFromBackBone"));
		
		suitableStarBeanWithMinDistance = resultStarBeanFilamentDistances;
		totalSuitableStarBeanWithMinDistance = suitableStarBeanWithMinDistance.size();
		
		ArrayList<StarBeanWithMinDistance> visibleStarBeanWithMinDistance = new ArrayList<StarBeanWithMinDistance>();
		int i;
		for (i = 0; i != totalSuitableStarBeanWithMinDistance && i < MAX_TABLE_ROWS; ++i) {
			visibleStarBeanWithMinDistance.add(suitableStarBeanWithMinDistance.get(i));
		}
		firstNextIndex = i;
		
		
		// se le stelle trovate sono MAX_TABLE_ROWS o meno allora si disabilita il tasto per mostrare i successivi MAX_TABLE_ROWS 
		if (firstNextIndex == totalSuitableStarBeanWithMinDistance) {
			nextStarBeanWithMinDistance.setDisable(true);
		}
		// il bottone per la navigazione indietro viene disabilitato perchè le stelle visualizzate sono i primi in lista
		previousStarBeanWithMinDistance.setDisable(true);
		// le modifiche sull'observableList si ripercuoteranno automaticamente sulla tabella
		observableStarBeanWithMinDistance = FXCollections.observableArrayList(visibleStarBeanWithMinDistance);
		tableStarBeanWithMinDistance.setItems(observableStarBeanWithMinDistance);
	}
	
	public void visualizeNextStarBeanWithMinDistance() {
		//il bottone per la navigazione indietro viene abilitato se non già abilitato
		previousStarBeanWithMinDistance.setDisable(false);
		// si rimuovono dall'observableList le stelle attualmente visualizzate
		clearObservableList();
		// si memorizza la posizione di partenza del gruppo di stelle correntemente visualizzato
		firstPreviousIndex = firstNextIndex - MAX_TABLE_ROWS;
		// si aggiungono le prossime MAX_TABLE_ROWS (al più) stelle all'observableList
		int i;
		for (i = firstNextIndex; i != totalSuitableStarBeanWithMinDistance && i < firstNextIndex + MAX_TABLE_ROWS; ++i) {
			observableStarBeanWithMinDistance.add(suitableStarBeanWithMinDistance.get(i));
		}
		firstNextIndex = i;
		// se le stelle sono finite allora si disabilita il bottone
		if (firstNextIndex == totalSuitableStarBeanWithMinDistance) {
			nextStarBeanWithMinDistance.setDisable(true);
		}
	}

	public void visualizePreviousStarBeanWithMinDistance() {
		// il bottone per la navigazione in avanti viene abilitato se non già abilitato
		nextStarBeanWithMinDistance.setDisable(false);
		// si rimuovono dall'observableList le stelle correntemente visualizzate
		clearObservableList();
		int i;
		for (i = firstPreviousIndex; i < firstPreviousIndex + MAX_TABLE_ROWS; ++i) {
			observableStarBeanWithMinDistance.add(suitableStarBeanWithMinDistance.get(i));
		}
		firstNextIndex = i;
		firstPreviousIndex -= MAX_TABLE_ROWS;
		// se le stelle visualizzate sono le prime della lista il bottone viene disabilitato
		if (firstPreviousIndex < 0) {
			previousStarBeanWithMinDistance.setDisable(true);
		}
	}
	
	private void clearObservableList() {
		Iterator<StarBeanWithMinDistance> iterator = observableStarBeanWithMinDistance.iterator();
		while (iterator.hasNext()) {
			iterator.next();
			iterator.remove();
		}
	}
}
