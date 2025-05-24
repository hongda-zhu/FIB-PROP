package scrabble.presentation.viewControllers;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import scrabble.presentation.PresentationController;

/**
 * Controlador para el menú principal de la aplicación
 * Maneja la navegación entre las principales pantallas de la aplicación.
 */
public class MainViewController {

    // singleton instance
    private static MainViewController instance;
    
    /**
     * Obtiene la instancia singleton del MainViewController
     * 
     * @return Instancia única del MainViewController
     */
    public static MainViewController getInstance() {
        if (instance == null) {
            instance = new MainViewController();
        }
        return instance;
    }

    @FXML
    private Button btnGestionJugadores;
    
    @FXML
    private Button btnGestionDiccionarios;
    
    @FXML
    private Button btnGestionPartidas;
    
    @FXML
    private Button btnGestionRankings;
    
    @FXML
    private Button btnConfiguracion;
    
    @FXML
    private Button btnSalir;
    
    private PresentationController presentationController;
    
    /**
     *  Inicializa el controlador
     */
    @FXML
    public void initialize() {
        presentationController = PresentationController.getInstance();
        MainViewController.instance = this;
    
        addHoverEffect(btnGestionJugadores);
        addHoverEffect(btnGestionDiccionarios);
        addHoverEffect(btnGestionPartidas);
        addHoverEffect(btnGestionRankings);
        addHoverEffect(btnConfiguracion);
        addHoverEffect(btnSalir);    
    }
    
    public void addHoverEffect(Button button) {
     
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), button);
        scaleIn.setToX(1.05);
        scaleIn.setToY(1.05);
        
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), button);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);
        
      
        TranslateTransition translateIn = new TranslateTransition(Duration.millis(200), button);
        translateIn.setToX(5);
        
        TranslateTransition translateOut = new TranslateTransition(Duration.millis(200), button);
        translateOut.setToX(0);
        
    
        button.setOnMouseEntered(e -> {
            scaleIn.play();
            if (button != btnSalir) {
                translateIn.play();
            }
        });
        
        button.setOnMouseExited(e -> {
            scaleOut.play();
            if (button != btnSalir) {
                translateOut.play();
            }
        });
    }    
    
    /**
     * Gestiona el comportamiento al dar click en Gestión de Jugadores
     */
    @FXML
    private void onGestionJugadoresClick(ActionEvent event) {
        // TODO: Implement player management screen navigation
        Stage stage = (Stage) btnGestionJugadores.getScene().getWindow();
        Parent vistaActual = stage.getScene().getRoot();
        
        ControladorJugadoresView controlador = new ControladorJugadoresView(stage);
        controlador.setVistaAnterior(vistaActual);        
    }
    
    /**
     * Gestiona el comportamiento al dar click en Gestión de Diccionarios
     */
    @FXML
    private void onGestionDiccionariosClick(ActionEvent event) {
        Stage stage = (Stage) btnGestionDiccionarios.getScene().getWindow();
        Parent vistaActual = stage.getScene().getRoot();
    
        ControladorDiccionario controlador = ControladorDiccionario.getInstance();
        controlador.setVistaAnterior(vistaActual);
        controlador.inicializar(stage);

    }
    
    /**
        * Gestiona el comportamiento al dar click en Gestión de Partidas
        */
        @FXML
        private void onGestionPartidasClick(ActionEvent event) {

            Stage stage = (Stage) btnGestionPartidas.getScene().getWindow();
            Parent vistaActual = stage.getScene().getRoot();
            
          
            ControladorPartidaView controladorPartidas = new ControladorPartidaView(stage);
            controladorPartidas.setVistaAnterior(vistaActual);
        }
    
    /**
     * Gestiona el comportamiento al dar click en Gestión ranking
     */
    @FXML
    private void onGestionRankingsClick(ActionEvent event) {
        Stage stage = (Stage) btnGestionRankings.getScene().getWindow();
        Parent vistaActual = stage.getScene().getRoot();
        ControladorRanking controladorRanking = ControladorRanking.getInstance();
        controladorRanking.setVistaAnterior(vistaActual);
        controladorRanking.inicializar(stage);
    }
    
    /**
     * Gestiona el comportamiento al dar click en Configuración
     */
    @FXML
    private void onConfiguracionClick(ActionEvent event) {
            Stage stage = (Stage) btnConfiguracion.getScene().getWindow();
            Parent vistaActual = stage.getScene().getRoot();
            ControladorConfiguracionView ctrlConfig = ControladorConfiguracionView.getInstance();
            ctrlConfig.setVistaAnterior(vistaActual);            
            ctrlConfig.initialize(stage);
    }
    
    /**
     * Gestiona el comportamiento al dar click en Salir
     */
    @FXML
    private void onSalirClick(ActionEvent event) {
        Stage stage = (Stage) btnSalir.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Muestra alerta con los params especificados
     */
    private void showErrorAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    /**
     * Cerrar aplicación por lo que sea
     */
    public void handleCloseEvent(WindowEvent event) {
        System.exit(0);
    }
}