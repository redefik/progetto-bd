package it.uniroma2.dicii.bd.progetto.entry;

import org.apache.log4j.Logger;

import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.errorLogic.ErrorType;
import it.uniroma2.dicii.bd.progetto.errorLogic.GUIError;
import it.uniroma2.dicii.bd.progetto.gui.WindowManager;
import it.uniroma2.dicii.bd.progetto.user.UserBean;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class LoginController {

	@FXML 
	private AnchorPane window;
	@FXML 
	private TextField username;
	@FXML 
	private PasswordField password;
	@FXML
	private Label errorMessage;
	
	private static final String MAIN_MENU = "../gui/mainView.fxml";
	
	@FXML
	public void initialize() {
		WindowManager.getInstance().setWindow(window);
	}
	
	public void login() throws ConfigurationError, DataAccessError, GUIError {
		
		try {
		
			String user = username.getText();
			String pass = password.getText();
			
			// Ricerca l'utente con l'username e la password dati
			LoginSession loginSession = LoginSession.getInstance();
			UserBean userBean = loginSession.findUser(user, pass);
			
			if (userBean == null) {
				// Se l'utente non viene trovato, compare un messaggio di errore
				errorMessage.setVisible(true);
			} else {
				// Se l'utente viene trovato, apro la finestra successiva passandole il bean corrispondente
				MainController.setUser(userBean);
			
				WindowManager.getInstance().changeMenu(MAIN_MENU);
			}
		} catch(ConfigurationError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.CONFIGURATION);
		} catch(DataAccessError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.DATA_ACCESS);
		} catch(GUIError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.GUI);
			gotoPreviousMenu();
		}
		
	}
	
	// Elimina il messaggio di errore non appena l'utente riprova a inserire username o password
	public void clearLabel() {
		errorMessage.setVisible(false);
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
