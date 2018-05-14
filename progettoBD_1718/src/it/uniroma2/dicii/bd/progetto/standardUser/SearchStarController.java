package it.uniroma2.dicii.bd.progetto.standardUser;

import org.apache.log4j.Logger;

import it.uniroma2.dicii.bd.progetto.errorLogic.ErrorType;
import it.uniroma2.dicii.bd.progetto.errorLogic.GUIError;
import it.uniroma2.dicii.bd.progetto.gui.WindowManager;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class SearchStarController {

	@FXML 
	private AnchorPane window;
	private static final String SEARCH_BY_FILAMENT_MENU = "../gui/searchStarByFilamentView.fxml";
	private static final String SEARCH_BY_REGION_MENU = "../gui/searchStarByRegionView.fxml";
	
	@FXML 
	public void initialize () {
		WindowManager.getInstance().setWindow(window);
	}
	
	public void gotoPreviousMenu() {
		try {
			WindowManager.getInstance().goToPreviousMenu();
		} catch (GUIError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.GUI);
		}
	}

	public void goToSearchByFilament() {
		try {
			WindowManager.getInstance().changeMenu(SEARCH_BY_FILAMENT_MENU);
		} catch (GUIError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.GUI);
		}
	}

	public void goToSearchByRegion() {
		try {
			WindowManager.getInstance().changeMenu(SEARCH_BY_REGION_MENU);
		} catch (GUIError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.GUI);
		}
	}

}
