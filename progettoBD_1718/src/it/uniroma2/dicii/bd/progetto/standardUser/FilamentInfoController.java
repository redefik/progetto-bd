package it.uniroma2.dicii.bd.progetto.standardUser;

import it.uniroma2.dicii.bd.progetto.filament.FilamentInfo;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class FilamentInfoController {

	@FXML 
	private Label labelFilamentName;
	@FXML 
	private Label labelCentroidLongitude;
	@FXML 
	private Label labelCentroidLatitude;
	@FXML 
	private Label labelBorderLength;
	@FXML 
	private Label labelNumOfSegments;
	
	private static FilamentInfo filamentInfo;
	
	public static void setFilamentInfo(FilamentInfo filamentInfo) {
		FilamentInfoController.filamentInfo = filamentInfo;
	}

	// all'apertura il controller popola i campi della finestra con le informazioni del filamento cercato
	@FXML
	public void initialize() {
		labelFilamentName.setText(filamentInfo.getName());
		labelCentroidLongitude.setText(String.valueOf(filamentInfo.getCentroidLongitude()));
		labelCentroidLatitude.setText(String.valueOf(filamentInfo.getCentroidLatitude()));
		labelBorderLength.setText(String.valueOf(filamentInfo.getBorderLength()));
		labelNumOfSegments.setText(String.valueOf(filamentInfo.getNumberOfSegments()));
	}

}
