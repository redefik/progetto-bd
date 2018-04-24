package it.uniroma2.dicii.bd.progetto.standardUser;

import org.apache.log4j.Logger;

import it.uniroma2.dicii.bd.progetto.errorLogic.ErrorType;
import it.uniroma2.dicii.bd.progetto.errorLogic.GUIError;
import it.uniroma2.dicii.bd.progetto.gui.WindowManager;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class SearchFilamentController {

	@FXML 
	private AnchorPane window;
	
	@FXML
	public void initialize(){
		WindowManager.getInstance().setWindow(window);
	}

	public void goToFindById() {}

	public void goToFindByContrast() {}

	public void goToFindBySegmentNumber() {}

	public void goToFindByRegion() {}
	
	public void gotoPreviousMenu() {
		try {
			WindowManager.getInstance().goToPreviousMenu();
		} catch (GUIError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.GUI);
		}
	}
	

}
