package it.uniroma2.dicii.bd.progetto.entry;
import java.io.IOException;

import it.uniroma2.dicii.bd.progetto.gui.WindowManager;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

public class LoginController {

	@FXML 
	AnchorPane window;
	@FXML 
	TextField username;
	@FXML 
	PasswordField password;
	
	public void login() throws IOException {
		
		String user = username.getText();
		String pass = password.getText();
		
		LoginSession ls = LoginSession.getInstance();
		//UserBean userBean = ls.
		
		WindowManager.getInstance().changeMenu(window, "../gui/mainView.fxml");
		
	}
}
