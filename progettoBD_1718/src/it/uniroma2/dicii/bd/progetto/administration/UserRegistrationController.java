package it.uniroma2.dicii.bd.progetto.administration;

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
	
	@FXML 
	public void registerUser() {
		
		if (!isValidUsername()) {
			errorMessage.setText(NOT_VALID_USERNAME);
			return;
		}
		if (!isValidPassword()) {
			errorMessage.setText(NOT_VALID_PASSWORD);
			return;
		}
		if (!isConfirmedPassword()) {
			errorMessage.setText(NOT_CONFIRMED_PASSWORD);
			return;
		}
		if (!isAvailableUsername()) {
			errorMessage.setText(NOT_AVAILABLE_USERNAME);
			return;
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
	
	private boolean isAvailableUsername() {
		//TODO
		return true;
	}

	@FXML 
	public void clearMessage() {
		errorMessage.setText("");
	}

}
