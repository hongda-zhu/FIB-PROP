package scrabble.presentation.views;

import java.io.IOException;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import scrabble.MainApplication;
import scrabble.presentation.viewControllers.ControladorConfiguracionView;

public class ConfiguracionPartidaView {
    
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
    private TextField txtTamanioTablero;
    
    @FXML
    private Label lblErrorTablero;
    
    @FXML
    private ComboBox<String> comboDificultad;
    
    @FXML
    private TextField txtBots;
    
    @FXML
    private Label lblErrorBots;
    
    public ConfiguracionPartidaView(ControladorConfiguracionView controlador) {
        this.controlador = controlador;
        cargarVista();
    }
    
    private void cargarVista() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/views/configuracion-partida.fxml"));
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
        
        // Configurar campos y validación
        if (txtTamanioTablero != null) {
            txtTamanioTablero.setText("15");
            
            // Permitir solo números
            txtTamanioTablero.addEventFilter(javafx.scene.input.KeyEvent.KEY_TYPED, event -> {
                if (!event.getCharacter().matches("[0-9]")) {
                    event.consume();
                }
            });
            
            // Validar cuando cambia el texto
            txtTamanioTablero.textProperty().addListener((observable, oldValue, newValue) -> {
                validarTamanioTablero(newValue);
            });
            
            // Validar al perder el foco
            txtTamanioTablero.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    confirmarTamanioTablero();
                }
            });
            
            // Validar al presionar Enter
            txtTamanioTablero.setOnAction(e -> confirmarTamanioTablero());
        }
        
        // Inicializar ComboBox
        if (comboDificultad != null) {
            comboDificultad.getItems().clear();
            comboDificultad.getItems().addAll("Fácil", "Difícil");
            comboDificultad.setValue("Fácil");
        }
        
        // Configurar campo de bots
        if (txtBots != null) {
            txtBots.setText("2");
            
            // Permitir solo números
            txtBots.addEventFilter(javafx.scene.input.KeyEvent.KEY_TYPED, event -> {
                if (!event.getCharacter().matches("[0-9]")) {
                    event.consume();
                }
            });
            
            // Validar cuando cambia el texto
            txtBots.textProperty().addListener((observable, oldValue, newValue) -> {
                validarNumeroBots(newValue);
            });
            
            // Validar al perder el foco
            txtBots.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    confirmarNumeroBots();
                }
            });
            
            // Validar al presionar Enter
            txtBots.setOnAction(e -> confirmarNumeroBots());
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
    
    /**
     * Valida el tamaño del tablero ingresado
     */
    private boolean validarTamanioTablero(String valor) {
        if (lblErrorTablero == null) return true;
        
        try {
            int tamano = Integer.parseInt(valor);
            if (tamano < 15) {
                lblErrorTablero.setText("El tamaño debe ser al menos 15");
                lblErrorTablero.setVisible(true);
                return false;
            } else {
                lblErrorTablero.setVisible(false);
                return true;
            }
        } catch (NumberFormatException e) {
            if (valor.isEmpty()) {
                lblErrorTablero.setText("Debes introducir un tamaño");
            } else {
                lblErrorTablero.setText("Por favor, introduce un número válido");
            }
            lblErrorTablero.setVisible(true);
            return false;
        }
    }
    
    /**
     * Confirma el tamaño del tablero cuando se pierde el foco o se presiona Enter
     */
    private void confirmarTamanioTablero() {
        if (txtTamanioTablero != null && !txtTamanioTablero.getText().isEmpty()) {
            if (validarTamanioTablero(txtTamanioTablero.getText())) {
                System.out.println("Tamaño de tablero confirmado: " + txtTamanioTablero.getText());
            }
        }
    }
    
    /**
     * Valida el número de bots ingresado
     */
    private boolean validarNumeroBots(String valor) {
        if (lblErrorBots == null) return true;
        
        try {
            int numBots = Integer.parseInt(valor);
            if (numBots < 0 || numBots > 4) {
                lblErrorBots.setText("El número de bots debe estar entre 0 y 4");
                lblErrorBots.setVisible(true);
                return false;
            } else {
                lblErrorBots.setVisible(false);
                return true;
            }
        } catch (NumberFormatException e) {
            if (valor.isEmpty()) {
                lblErrorBots.setText("Debes introducir un número de bots");
            } else {
                lblErrorBots.setText("Por favor, introduce un número válido");
            }
            lblErrorBots.setVisible(true);
            return false;
        }
    }
    
    /**
     * Confirma el número de bots cuando se pierde el foco o se presiona Enter
     */
    private void confirmarNumeroBots() {
        if (txtBots != null && !txtBots.getText().isEmpty()) {
            if (validarNumeroBots(txtBots.getText())) {
                System.out.println("Número de bots confirmado: " + txtBots.getText());
            }
        }
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
    
    public void onGuardarClick(ActionEvent event) {
        try {
            // Validar campos
            boolean tamanioValido = validarTamanioTablero(txtTamanioTablero.getText());
            boolean botsValidos = validarNumeroBots(txtBots.getText());
            
            if (!tamanioValido || !botsValidos) {
                return;
            }
            
            int tamanioTablero = Integer.parseInt(txtTamanioTablero.getText());
            int numBots = Integer.parseInt(txtBots.getText());
            
            // Guardar configuración
            controlador.guardarConfiguracionPartida(
                    tamanioTablero,
                    comboDificultad.getValue(),
                    numBots
            );
            
        } catch (NumberFormatException e) {
            controlador.showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Error", 
                    "Por favor, ingrese valores numéricos válidos.");
        }
    }
    
    public void onRestablecerClick(ActionEvent event) {
        // Restablecer valores
        if (txtTamanioTablero != null) {
            txtTamanioTablero.setText("15");
            if (lblErrorTablero != null) {
                lblErrorTablero.setVisible(false);
            }
        }
        
        if (comboDificultad != null) {
            comboDificultad.setValue("Fácil");
        }
        
        if (txtBots != null) {
            txtBots.setText("2");
            if (lblErrorBots != null) {
                lblErrorBots.setVisible(false);
            }
        }
        
        controlador.showAlert(javafx.scene.control.Alert.AlertType.INFORMATION, "Valores restablecidos", 
                "Se han restablecido los valores por defecto de la configuración de partida.");
    }
    
    public Parent getView() {
        return view;
    }
}