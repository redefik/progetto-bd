package it.uniroma2.dicii.bd.progetto.administration;

import org.apache.log4j.Logger;

import it.uniroma2.dicii.bd.progetto.errorLogic.ErrorType;
import it.uniroma2.dicii.bd.progetto.errorLogic.GUIError;
import it.uniroma2.dicii.bd.progetto.gui.WindowManager;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class AdministrationController {
	
	private static final String REGISTRATION_USER_MENU = "../gui/userRegistrationView.fxml";
	private static final String REGISTRATION_SATELLITE_MENU = "../gui/satelliteRegistrationView.fxml";
	private static final String REGISTRATION_INSTRUMENT_MENU = "../gui/instrumentRegistrationView.fxml";
	private static final String MAIN_VIEW_MENU = "../gui/mainView.fxml";
	private static final String IMPORT_MENU = "../gui/fileImportView.fxml";

	
	@FXML
	private AnchorPane window; 
	
	public void registerUser() {
		try {
			WindowManager.getInstance().changeMenu(REGISTRATION_USER_MENU);
		} catch (GUIError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.GUI);
		}
	}
	
	public void registerSatellite() {
		try {
			WindowManager.getInstance().changeMenu(REGISTRATION_SATELLITE_MENU);
		} catch (GUIError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.GUI);
		}
	}
	
	public void registerInstrument() {
		try {
			WindowManager.getInstance().changeMenu(REGISTRATION_INSTRUMENT_MENU);
		} catch (GUIError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.GUI);
		}
	}
	
	public void backToMainView() {
		try {
			WindowManager.getInstance().changeMenu(MAIN_VIEW_MENU);
		} catch (GUIError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.GUI);
		}
	}

	
	@FXML
	public void initialize() {
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

	public void goToImportMenu() {
		try {
			WindowManager.getInstance().changeMenu(IMPORT_MENU);
		} catch (GUIError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.GUI);
		}
	}

}
