package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ApplicationLauncher extends Application {

	public static final String PRELOADER_SCREEN = "preloader"; 
    public static final String PRELOADER_SCREEN_FXML = "pages/ApplicationPreloader.fxml"; 
    public static final String MAIN_SCREEN = "application"; 
    public static final String MAIN_SCREEN_FXML = "pages/MainApplication.fxml"; 
    
	@Override
	public void start(Stage primaryStage) {
		
		// StackPanel
		ScreensController mainContainer = new ScreensController(); 
	    
		mainContainer.loadScreen(PRELOADER_SCREEN, PRELOADER_SCREEN_FXML); 
	    mainContainer.loadScreen(MAIN_SCREEN, MAIN_SCREEN_FXML); 
	    mainContainer.setScreen(PRELOADER_SCREEN);

	    Scene scene = new Scene(mainContainer); 
	    primaryStage.setScene(scene);
	    primaryStage.show(); 
	}

	public static void main(String[] args) {
		launch(args);
	}

}
