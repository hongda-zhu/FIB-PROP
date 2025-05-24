package scrabble;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import scrabble.presentation.PresentationController;
import scrabble.presentation.viewControllers.MainViewController;

/**
 * Entry point para la aplicación
 */
public class MainApplication extends Application {
    public static boolean initialized = false;

    @Override
    public void start(Stage stage) throws IOException {
        if (!initialized) {
            PresentationController.getInstance().initializeDefaultSettings();
            initialized = true;
        }
        // MusicManager.initialize();
        // MusicManager.play();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
       
        stage.setTitle("SCRABBLE");
        stage.setMinWidth(1200);
        stage.setMinHeight(800);
        
        stage.setScene(scene);
        stage.setMaximized(true);
        
        // Set up close event handler usando el patrón singleton
        stage.setOnCloseRequest(event -> {
            MainViewController mainController = MainViewController.getInstance();

            if (mainController != null) {
                mainController.handleCloseEvent(event);
            } else {
                
                System.exit(0);
            }
        });
        
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}