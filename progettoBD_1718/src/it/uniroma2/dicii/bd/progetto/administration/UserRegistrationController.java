package it.uniroma2.dicii.bd.progetto.administration;

import org.apache.log4j.Logger;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.errorLogic.ErrorType;
import it.uniroma2.dicii.bd.progetto.errorLogic.GUIError;
import it.uniroma2.dicii.bd.progetto.gui.WindowManager;
import it.uniroma2.dicii.bd.progetto.user.UserBean;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class UserRegistrationController {
	
	private static final String NOT_VALID_USERNAME = "L'username deve avere al minimo 6 caratteri.";
	private static final String NOT_VALID_PASSWORD = "La password deve avere al minimo 6 caratteri.";
	private static final String NOT_AVAILABLE_USERNAME = "Username gia' in uso.";
	private static final String NOT_CONFIRMED_PASSWORD = "Le password inserite non coincidono.";
	private static final String REGISTRATION_CONFIRMED = "La registrazione e' stata effettuata con successo.";
	private static final String ADMINISTRATION_MENU = "../gui/administrationView.fxml";
	
	@FXML
	private AnchorPane window;
	@FXML
	private TextField username;
	@FXML
	private TextField firstName;
	@FXML
	private TextField lastName;
	@FXML
	private TextField mail;
	@FXML
	private PasswordField password;
	@FXML
	private PasswordField confirmedPassword;
	@FXML
	private CheckBox admin;
	@FXML
	private Label errorMessage;
	
	
	public void registerUser() {
		
		try {
			
			// Si verifica che username e password abbiano il numero di caratteri opportuno 
			
			if (!isValidUsername()) {
				errorMessage.setText(NOT_VALID_USERNAME);
				return;
			}
			if (!isValidPassword()) {
				errorMessage.setText(NOT_VALID_PASSWORD);
				return;
			}
			
			// Si verifica che l'utente abbia inserito due volte la password desiderata
			if (!isConfirmedPassword()) {
				errorMessage.setText(NOT_CONFIRMED_PASSWORD);
				return;
			}
			
			// Si verifica che lo username non sia attualmente in uso
			if (!isAvailableUsername(username.getText())) {
				errorMessage.setText(NOT_AVAILABLE_USERNAME);
				return;
			}
			
			// Si registra l'utente
			UserBean userBean = new UserBean();
			userBean.setFirstName(firstName.getText());
			userBean.setLastName(lastName.getText());
			userBean.setMail(mail.getText());
			userBean.setUsername(username.getText());
			userBean.setPassword(password.getText());
			
			// L'utente di tipo 0 ha il massimo dei privilegi (amministratore), l'utente di tipo 1 e' utente base
			if (admin.isSelected()) {
				userBean.setType(0);
			} else {
				userBean.setType(1);
			}
			
			AdministrationSession.getInstance().registerUser(userBean);
			
			WindowManager.getInstance().openInfoWindow(REGISTRATION_CONFIRMED);
			WindowManager.getInstance().changeMenu(ADMINISTRATION_MENU);
			
		} catch(GUIError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.GUI);
		} catch(ConfigurationError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.CONFIGURATION);
		} catch(DataAccessError e) {
			
			//Si gestisce l'eventualita' in cui nel tempo che intercorre tra il controllo dell'username e
			//l'inserimento dell'utente, un altro amministratore inserisce un altro utente con lo stesso username
			try {
				if (!isAvailableUsername(username.getText())) {
					errorMessage.setText(NOT_AVAILABLE_USERNAME);
				}
			} catch(ConfigurationError e1) {
				Logger.getLogger(getClass()).error(e.getMessage(), e);
				WindowManager.getInstance().openErrorWindow(ErrorType.CONFIGURATION);
			} catch(DataAccessError e1) {
				Logger.getLogger(getClass()).error(e.getMessage(), e);
				WindowManager.getInstance().openErrorWindow(ErrorType.DATA_ACCESS);
			}
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.DATA_ACCESS);
		} 
	}
	
	private boolean isValidPassword() {
		return (password.getText().length() >= 6);
	}
	
	private boolean isConfirmedPassword() {
		return (password.getText().equals(confirmedPassword.getText()));
	}
	
	private boolean isValidUsername() {
		return (username.getText().length() >= 6);
	}
	
	private boolean isAvailableUsername(String username) throws DataAccessError, ConfigurationError {
		
		AdministrationSession administrationSession = AdministrationSession.getInstance();
		return administrationSession.isAvailableUsername(username);
		
	}

	@FXML 
	public void clearMessage() {
		errorMessage.setText("");
	}
	
	@FXML
	public void initialize() {
		WindowManager.getInstance().setWindow(window);
	}
}
