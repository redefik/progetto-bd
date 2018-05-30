package it.uniroma2.dicii.bd.progetto.standardUser;

import org.apache.log4j.Logger;

import it.uniroma2.dicii.bd.progetto.errorLogic.ErrorType;
import it.uniroma2.dicii.bd.progetto.errorLogic.GUIError;
import it.uniroma2.dicii.bd.progetto.gui.WindowManager;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class SearchFilamentController {
	
	private static final String SEARCH_FILAMENT_BY_ID_MENU = "../gui/searchFilamentByNameIdView.fxml";
	private static final String SEARCH_FILAMENT_BY_CONTRAST_MENU = "../gui/searchFilamentByContrastEllipticity.fxml";
	private static final String SEARCH_FILAMENT_BY_NUMSEGMENTS_MENU = "../gui/searchFilamentByNumOfSegments.fxml";
	private static final String SEARCH_FILAMENT_BY_REGION = "../gui/searchFilamentByRegionView.fxml";


	@FXML 
	private AnchorPane window;
	
	@FXML
	public void initialize(){
		WindowManager.getInstance().setWindow(window);
	}

	public void goToFindById() {
		try {
			WindowManager.getInstance().changeMenu(SEARCH_FILAMENT_BY_ID_MENU);
		} catch(GUIError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.GUI);
			gotoPreviousMenu();
		}
	}

	public void goToFindByContrast() {
		try {
			WindowManager.getInstance().changeMenu(SEARCH_FILAMENT_BY_CONTRAST_MENU);
		} catch(GUIError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.GUI);
			gotoPreviousMenu();
		}
	}

	public void goToFindBySegmentNumber() {
		try {
			WindowManager.getInstance().changeMenu(SEARCH_FILAMENT_BY_NUMSEGMENTS_MENU);
		} catch(GUIError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.GUI);
			gotoPreviousMenu();
		}
	}

	public void goToFindByRegion() {
		try {
			WindowManager.getInstance().changeMenu(SEARCH_FILAMENT_BY_REGION);
		} catch (GUIError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.GUI);
			gotoPreviousMenu();
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
