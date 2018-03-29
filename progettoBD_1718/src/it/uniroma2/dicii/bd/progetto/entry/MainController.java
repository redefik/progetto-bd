package it.uniroma2.dicii.bd.progetto.entry;

import org.apache.log4j.Logger;
import it.uniroma2.dicii.bd.progetto.errorLogic.ErrorType;
import it.uniroma2.dicii.bd.progetto.errorLogic.GUIError;
import it.uniroma2.dicii.bd.progetto.gui.WindowManager;
import it.uniroma2.dicii.bd.progetto.user.UserBean;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class MainController {
	
	@FXML
	private AnchorPane window;
	
	@FXML 
	private Label welcomeMessage;
	
	private static final String WELCOME = "Benvenuto ";
	private static final String PERMISSION_DENIED = "Accesso negato. Non disponi delle autorizzazioni necessarie\n"
			+ "per accedere all'area riservata all'amministrazione.";
	private static final String ADMINISTRATION_MENU = "../gui/administrationView.fxml";
	
	private static UserBean user;
	
	public static void setUser(UserBean user) {
		MainController.user = user;
	}
	
	@FXML
	public void initialize() {
		welcomeMessage.setText(WELCOME + user.getNome() + " " + user.getCognome());
		WindowManager.getInstance().setWindow(window);
	}
	
	public void goToAdministrationArea() {
		try {
			if (user.getType() == 0) {
				WindowManager.getInstance().changeMenu(ADMINISTRATION_MENU);
			} else {
				WindowManager.getInstance().openInfoWindow(PERMISSION_DENIED);
			}
		} catch (GUIError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.GUI);
		}
	}
	
}
