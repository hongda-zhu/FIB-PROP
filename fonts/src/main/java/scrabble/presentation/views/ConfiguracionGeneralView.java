package scrabble.presentation.views;

import java.io.IOException;
import java.util.Map;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import scrabble.MainApplication;
import scrabble.presentation.viewControllers.ControladorConfiguracionView;

public class ConfiguracionGeneralView {
    
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
    private Button btnGuardar;
    
    @FXML
    private Button btnRestablecer;
    
    @FXML
    private ComboBox<String> comboTema;
    
    @FXML
    private ToggleButton toggleMusica;
    
    @FXML
    private ToggleButton toggleSonido;
    
    @FXML
    private Label lblEstadoMusica;
    
    @FXML
    private Label lblEstadoSonido;
    
    @FXML
    private HBox toggleBgMusica;
    
    @FXML
    private StackPane toggleCircleMusica;
    
    @FXML
    private HBox toggleBgSonido;
    
    @FXML
    private StackPane toggleCircleSonido;

    @FXML
    private Slider sliderMusica;

    @FXML
    private Slider sliderSonido;

    @FXML 
    private Label labelVolumenMusica;

    @FXML
    private Label labelVolumenSonido;
    
    
    public ConfiguracionGeneralView(ControladorConfiguracionView controlador) {
        this.controlador = controlador;
        cargarVista();
    }
    
    private void cargarVista() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/views/configuracion-general.fxml"));
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
        
        // Configurar botones de acción
        if (btnRestablecer != null) {
            btnRestablecer.setOnAction(this::onRestablecerClick);
            btnRestablecer.getStyleClass().add("btn-effect");
        }
        
        if (btnGuardar != null) {
            btnGuardar.setOnAction(this::onGuardarClick);
            btnGuardar.getStyleClass().add("btn-effect");
        }

        Map<String, String> config = controlador.cargarConfiguracion();
        
        if (comboTema != null) {
            comboTema.getItems().clear();
            comboTema.getItems().addAll("Claro", "Oscuro");
            comboTema.setValue(config.getOrDefault("tema", "Claro"));
        }
        
        // Inicializar toggle música
        if (toggleMusica != null) {
            toggleMusica.setSelected(Boolean.parseBoolean(config.getOrDefault("musica", "true")));
            onToggleMusicaClick(null);
        }
        
        // Inicializar toggle sonido
        if (toggleSonido != null) {
            toggleSonido.setSelected(Boolean.parseBoolean(config.getOrDefault("sonido", "true")));
            onToggleSonidoClick(null);
        }

        if (sliderMusica != null) {
            String volumenMusicaStr = config.getOrDefault("volumenMusica", "50");
            try {
                sliderMusica.setValue(Double.parseDouble(volumenMusicaStr));
            } catch (NumberFormatException e) {
                sliderMusica.setValue(50);
            }
        }

        if (sliderSonido != null) {
            String volumenSonidoStr = config.getOrDefault("volumenSonido", "50");
            try {
                sliderSonido.setValue(Double.parseDouble(volumenSonidoStr));
            } catch (NumberFormatException e) {
                sliderSonido.setValue(50);
            }
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
    public void onToggleMusicaClick(ActionEvent event) {
        boolean activado = toggleMusica.isSelected();
        
        if (activado) {
            // Música activada
            if (toggleBgMusica != null) {
                toggleBgMusica.setStyle("-fx-background-color: #2ecc71; -fx-background-radius: 15;");
                toggleBgMusica.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
                // MusicManager.play();
            }
            if (lblEstadoMusica != null) {
                lblEstadoMusica.setText("Activado");
            }
        } else {
            // Música desactivada
            if (toggleBgMusica != null) {
                toggleBgMusica.setStyle("-fx-background-color: #e74c3c; -fx-background-radius: 15;");
                toggleBgMusica.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                // MusicManager.pause();
            }
            if (lblEstadoMusica != null) {
                lblEstadoMusica.setText("Desactivado");
            }
        }
    }
    
    @FXML
    public void onToggleSonidoClick(ActionEvent event) {
        boolean activado = toggleSonido.isSelected();
        
        if (activado) {
            // Sonido activado
            if (toggleBgSonido != null) {
                toggleBgSonido.setStyle("-fx-background-color: #2ecc71; -fx-background-radius: 15;");
                toggleBgSonido.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
            }
            if (lblEstadoSonido != null) {
                lblEstadoSonido.setText("Activado");
            }
        } else {
            // Sonido desactivado
            if (toggleBgSonido != null) {
                toggleBgSonido.setStyle("-fx-background-color: #e74c3c; -fx-background-radius: 15;");
                toggleBgSonido.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            }
            if (lblEstadoSonido != null) {
                lblEstadoSonido.setText("Desactivado");
            }
        }
    }
    
    public void onGuardarClick(ActionEvent event) {
        if (comboTema != null && toggleMusica != null && toggleSonido != null) {
            // Guardar configuración general a través del controlador
            controlador.guardarConfiguracionGeneral(
                    comboTema.getValue(),
                    toggleMusica.isSelected(),
                    toggleSonido.isSelected(),
                    (int) sliderMusica.getValue(),
                    (int) sliderSonido.getValue()
            );
            controlador.mostrarAlerta("success", "Configuración guardada", 
                    "La configuración general se ha guardado correctamente.");
        }
        else controlador.mostrarAlerta("error", "Error", 
                "No se pudo guardar la configuración general.");
    }
    
    public void onRestablecerClick(ActionEvent event) {
        // Restablecer valores por defecto
        if (comboTema != null) comboTema.setValue("Claro");
        
        if (toggleMusica != null) {
            toggleMusica.setSelected(false);
            onToggleMusicaClick(null); // Actualiza la UI
        }
        
        if (toggleSonido != null) {
            toggleSonido.setSelected(true);
            onToggleSonidoClick(null); // Actualiza la UI
        }
        
        controlador.showAlert(javafx.scene.control.Alert.AlertType.INFORMATION, "Valores restablecidos", 
                "Se han restablecido los valores por defecto de la configuración general.");
    }
    
    public Parent getView() {
        return view;
    }
}