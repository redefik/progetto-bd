package it.uniroma2.dicii.bd.progetto.administration;

import org.apache.log4j.Logger;
import it.uniroma2.dicii.bd.progetto.errorLogic.ErrorType;
import it.uniroma2.dicii.bd.progetto.errorLogic.GUIError;
import it.uniroma2.dicii.bd.progetto.gui.WindowManager;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class FileImportController {
	
	private static final String FILAMENTS_IMPORT_MENU = "../gui/filamentsImportView.fxml";
	private static final String BORDER_POINTS_IMPORT_MENU = "../gui/borderPointsImportView.fxml";
	
	@FXML 
	private AnchorPane window;

	@FXML
	public void initialize() {
		WindowManager.getInstance().setWindow(window);
	}
	
	public void goToFilamentsImportView() {
		try {
			WindowManager.getInstance().changeMenu(FILAMENTS_IMPORT_MENU);
		} catch (GUIError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.GUI);
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

	public void goToBorderPointsImportView() {
		try {
			WindowManager.getInstance().changeMenu(BORDER_POINTS_IMPORT_MENU);
		} catch (GUIError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.GUI);
		}
	}
}
