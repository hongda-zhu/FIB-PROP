package scrabble;

import java.io.IOException;
import java.util.Map;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import scrabble.presentation.PresentationController;
import scrabble.presentation.componentes.MusicManager;
import scrabble.presentation.componentes.SoundEffects;
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

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
       
        stage.setTitle("SCRABBLE");
        stage.setMinWidth(400);
        stage.setMinHeight(300);
        
        stage.setScene(scene);
        stage.setResizable(true);
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

        Map<String, String> defaultSettings = PresentationController.getInstance().cargarConfiguracion();
        MusicManager.initialize();
        if (Boolean.parseBoolean(defaultSettings.getOrDefault("musica", "true"))) MusicManager.play();
        else MusicManager.pause();
        String volumenMusicaStr = defaultSettings.getOrDefault("volumenMusica", "50");
        MusicManager.setVolume((Double.parseDouble(volumenMusicaStr))/ 100.0);
        if (Boolean.parseBoolean(defaultSettings.getOrDefault("sonido", "true"))) SoundEffects.habilitar(true);
        else SoundEffects.habilitar(false);
        String volumenSonidoStr = defaultSettings.getOrDefault("volumenSonido", "50");
        SoundEffects.setVolumen((Double.parseDouble(volumenSonidoStr))/ 100.0);
        
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}