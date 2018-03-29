package it.uniroma2.dicii.bd.progetto.administration;

import org.apache.log4j.Logger;

import it.uniroma2.dicii.bd.progetto.errorLogic.ErrorType;
import it.uniroma2.dicii.bd.progetto.errorLogic.GUIError;
import it.uniroma2.dicii.bd.progetto.gui.WindowManager;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class AdministrationController {
	
	private static final String REGISTRATION_USER_MENU = "../gui/userRegistrationView.fxml";
	
	
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
	
	@FXML
	public void initialize() {
		WindowManager.getInstance().setWindow(window);
	}
}
