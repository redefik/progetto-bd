package it.uniroma2.dicii.bd.progetto.administration;

import java.io.File;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import it.uniroma2.dicii.bd.progetto.errorLogic.BatchError;
import it.uniroma2.dicii.bd.progetto.errorLogic.CSVFileParserException;
import it.uniroma2.dicii.bd.progetto.errorLogic.ConfigurationError;
import it.uniroma2.dicii.bd.progetto.errorLogic.DataAccessError;
import it.uniroma2.dicii.bd.progetto.errorLogic.ErrorType;
import it.uniroma2.dicii.bd.progetto.errorLogic.GUIError;
import it.uniroma2.dicii.bd.progetto.filament.FilamentBean;
import it.uniroma2.dicii.bd.progetto.gui.WindowManager;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

public class FilamentsImportController {
	
	private static final String IMPORT_FILE_FORMAT_NAME = "csv";
	private static final String IMPORT_FILE_FORMAT_EXTENSION = "*.csv";
	private static final String NOT_SELECTED_FILE_MESSAGE = "Seleziona prima un file da importare.";
	private static final String IMPORT_SUCCESS = "Import del file completato.";
	private static final String ADMINISTRATION_MENU = "../gui/administrationView.fxml";
	private static final String IMPORT_FAILED = "Import del file non riuscito.";

	private File importedFile;

	@FXML 
	private AnchorPane window;
	@FXML
	private TextField importedFilePath;
	@FXML 
	private Label errorMessage;
	
	@FXML
	public void initialize() {
		WindowManager.getInstance().setWindow(window);
	}


	public void selectFile() {
		errorMessage.setText("");
		WindowManager windowManager = WindowManager.getInstance();
		//Si permette all'utente di scegliere dal proprio disposito il file da importare, si permette la ricerca solo 
		//tra file di uno specifico formato (nel caso specifico .csv)
		importedFile = windowManager.getFileFromSystemExplorerWithFormat(IMPORT_FILE_FORMAT_NAME, IMPORT_FILE_FORMAT_EXTENSION);
		if (importedFile != null) {
			importedFilePath.setText(importedFile.getAbsolutePath());
		}
	}

	public void importFile() {
		try {
			// Se l'utente non ha specificato un file da importare si stampa un messaggio di errore
			if (importedFile == null) {
				errorMessage.setText(NOT_SELECTED_FILE_MESSAGE);
				return;
			}
			
			// Si instanzia mediante l'uso di una factory un parser per il file da importare
			CSVFileParserFactory parserFactory = CSVFileParserFactory.getInstance();
			CSVFileParser parser = parserFactory.createCSVFileParser();
			
			//Si delega a un oggetto di tipo CSVFileParser il parser del file per ottenere una lista di FilamentBean
			ArrayList<FilamentBean> filamentBeans = parser.getFilamentBeans(importedFile);
			
			//Si delega alla classe AdministrationSession l'inserimento dei filamenti in persistenza
			AdministrationSession administrationSession = AdministrationSession.getInstance();
			administrationSession.insertFilaments(filamentBeans);
			
			WindowManager.getInstance().openInfoWindow(IMPORT_SUCCESS);
			WindowManager.getInstance().changeMenu(ADMINISTRATION_MENU);
			
		} catch(ConfigurationError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.CONFIGURATION);
		} catch(CSVFileParserException e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.CSVFILE_PARSING);
		} catch (GUIError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.GUI);
		} catch (BatchError e) {
			WindowManager.getInstance().openDetailedErrorWindow(IMPORT_FAILED, e.getMessage());
		} catch (DataAccessError e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			WindowManager.getInstance().openErrorWindow(ErrorType.DATA_ACCESS);
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
