package scrabble.presentation.views;

import java.io.IOException;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.util.Duration;
import scrabble.MainApplication;
import scrabble.presentation.viewControllers.ControladorConfiguracionView;

public class ConfiguracionAyudaView {
    
    private final ControladorConfiguracionView controlador;
    private Parent view;
    
    // Componentes de la interfaz
    @FXML
    private Button btnGeneral;
    
    @FXML
    private Button btnPartida;
    
    @FXML
    private Button btnAyuda;
    
    @FXML
    private Button btnVolver;
    
    @FXML
    private Button btnContacto;
    
    public ConfiguracionAyudaView(ControladorConfiguracionView controlador) {
        this.controlador = controlador;
        cargarVista();
    }
    
    private void cargarVista() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/views/configuracion-ayuda.fxml"));
            loader.setController(this);
            view = loader.load();
            inicializarComponentes();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar vista: " + e.getMessage());
        }
    }
    
    private void inicializarComponentes() {
        // Aplicar efectos a botones de menú
        if (btnGeneral != null) {
            btnGeneral.getStyleClass().add("config-menu-button");
            addHoverEffect(btnGeneral);
        }
        if (btnPartida != null) {
            btnPartida.getStyleClass().add("config-menu-button");
            addHoverEffect(btnPartida);
        }
        if (btnAyuda != null) {
            btnAyuda.getStyleClass().add("config-menu-button");
            addHoverEffect(btnAyuda);
        }
        if (btnVolver != null) {
            btnVolver.getStyleClass().add("config-menu-button");
            addHoverEffect(btnVolver);
        }
        
        // Configurar botón de contacto
        if (btnContacto != null) {
            btnContacto.setOnAction(this::onContactoClick);
            btnContacto.getStyleClass().add("btn-effect");
        }
    }
    
    /**
     * Añade efecto hover a los botones
     */
    private void addHoverEffect(Button button) {
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
            translateIn.play();
        });
        
        button.setOnMouseExited(e -> {
            scaleOut.play();
            translateOut.play();
        });
    }
    
    @FXML
    public void onGeneralClick(ActionEvent event) {
        controlador.mostrarVistaGeneral();
    }
    
    @FXML
    public void onPartidaClick(ActionEvent event) {
        controlador.mostrarVistaPartida();
    }
    
    @FXML
    public void onAyudaClick(ActionEvent event) {
        controlador.mostrarVistaAyuda();
    }
    
    @FXML
    public void onVolverClick(ActionEvent event) {
        controlador.volverAMenuPrincipal();
    }
    
    @FXML
    public void onContactoClick(ActionEvent event) {
        controlador.mostrarInformacionContacto();
    }
    
    public Parent getView() {
        return view;
    }
}