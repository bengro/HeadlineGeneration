package ui.pages;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import main.Config;
import main.Generator;
import ui.ApplicationLauncher;
import ui.ScreensController;

public class ApplicationLoaderController implements Initializable, IPage {

	ScreensController myController;
	
	@FXML
	private TextField ducPath;
	
	@FXML
	private ProgressIndicator progressBar;
	
	@FXML
	private Button launchButton;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		ducPath.setText(Config.ducDirectory);
		
		launchButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				// check if duc directory valid
				File ducDirectory = new File(ducPath.getText());
				if(ducDirectory.isDirectory()) {
					executeFileExplorer();
					launchButton.setDisable(true);
					ducPath.setDisable(true);
				}
			}
		});
		
	}

	protected void executeFileExplorer() {
		Task<Void> task = new Task<Void>() {
		    @Override public Void call() {

		        String[] args = {"ui-mode"};
		        Generator.main(args);

		        myController.setScreen(ApplicationLauncher.MAIN_SCREEN);
		        return null;
		    }
		};
		
		progressBar.progressProperty().bind(task.progressProperty());
		new Thread(task).start();
	}
	
	@Override
	public void setScreenParent(ScreensController screenController) {
		myController = screenController;
	}

	@Override
	public void reactivateScreen() {
		//TODO: needed if navigating back from running application.
	}
	
}
