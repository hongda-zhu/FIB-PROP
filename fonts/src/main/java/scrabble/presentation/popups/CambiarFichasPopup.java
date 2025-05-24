package scrabble.presentation.popups;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import scrabble.presentation.componentes.Ficha;

public class CambiarFichasPopup {
    
    private Stage stage;
    private List<Ficha> fichasSeleccionadas = new ArrayList<>();
    private List<String> letrasSeleccionadas = new ArrayList<>();
    private Label lblSeleccionadas;
    private int casillaTamaño;
    
    /**
     * Constructor para el popup de cambio de fichas
     * @param rack El rack actual del jugador (mapa de letras y cantidades)
     * @param casillaTamaño El tamaño de cada casilla de ficha
     */
    public CambiarFichasPopup(Map<String, Integer> rack, int casillaTamaño) {
        this.casillaTamaño = casillaTamaño;
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("");
        stage.setResizable(false);
        
        // Crear la interfaz del popup
        crearInterfaz(rack);
    }
    
    /**
     * Crea la interfaz del popup
     * @param rack El rack del jugador
     */
    private void crearInterfaz(Map<String, Integer> rack) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        
        // Título
        Label titulo = new Label("Cambiar fichas");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Etiqueta para mostrar el número de fichas seleccionadas
        lblSeleccionadas = new Label("0 seleccionadas");
        lblSeleccionadas.setStyle("-fx-font-size: 14px;");
        
        // Contenedor para las fichas
        HBox fichasContainer = new HBox(5);
        fichasContainer.setAlignment(Pos.CENTER);
        fichasContainer.setPadding(new Insets(10));
        
        // Añadir fichas al contenedor
        for (Map.Entry<String, Integer> entry : rack.entrySet()) {
            String letra = entry.getKey();
            int cantidad = entry.getValue();
            int puntos = obtenerPuntosPorLetra(letra.charAt(0));
            
            for (int i = 0; i < cantidad; i++) {
                Ficha ficha = new Ficha(letra.charAt(0), puntos);
                ficha.setMinSize(casillaTamaño, casillaTamaño);
                ficha.setPrefSize(casillaTamaño, casillaTamaño);
                ficha.setMaxSize(casillaTamaño, casillaTamaño);
                
                // Configurar evento de clic para seleccionar/deseleccionar
                ficha.setOnMouseClicked(e -> {
                    if (ficha.isSeleccionada()) {
                        ficha.deseleccionar();
                        fichasSeleccionadas.remove(ficha);
                        letrasSeleccionadas.remove(String.valueOf(ficha.getLetra()));
                        
                        // Animar ficha hacia abajo cuando se deselecciona
                        TranslateTransition tt = new TranslateTransition(Duration.millis(100), ficha);
                        tt.setFromY(-10);
                        tt.setToY(0);
                        tt.play();
                    } else {
                        ficha.seleccionar();
                        fichasSeleccionadas.add(ficha);
                        letrasSeleccionadas.add(String.valueOf(ficha.getLetra()));
                        
                        // Animar ficha hacia arriba cuando se selecciona
                        TranslateTransition tt = new TranslateTransition(Duration.millis(100), ficha);
                        tt.setFromY(0);
                        tt.setToY(-10);
                        tt.play();
                    }
                    actualizarSeleccionadas();
                });
                
                fichasContainer.getChildren().add(ficha);
            }
        }
        
        // Botones
        HBox botonesContainer = new HBox(10);
        botonesContainer.setAlignment(Pos.CENTER);
        
        // Cargar CSS desde archivo
        String cssPath = PausaPopup.class.getResource("/styles/button.css").toExternalForm();
        
        // Crear botones con los estilos CSS de GenericPopup
        Button btnConfirmar = new Button("Confirmar");
        btnConfirmar.getStyleClass().add("btn-effect");
        btnConfirmar.getStyleClass().add("btn-success");
        btnConfirmar.setMinWidth(120);
        btnConfirmar.setMinHeight(40);
        btnConfirmar.setPrefHeight(40);
        btnConfirmar.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        btnConfirmar.setDisable(true); // Deshabilitar inicialmente hasta que se seleccione alguna ficha
        
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.getStyleClass().add("btn-effect");
        btnCancelar.getStyleClass().add("btn-danger");
        btnCancelar.setMinWidth(120);
        btnCancelar.setMinHeight(40);
        btnCancelar.setPrefHeight(40);
        btnCancelar.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Eventos de botones
        btnConfirmar.setOnAction(e -> {
            stage.close();
        });
        
        btnCancelar.setOnAction(e -> {
            fichasSeleccionadas.clear();
            letrasSeleccionadas.clear();
            stage.close();
        });
        
        // Actualizar estado del botón confirmar cuando cambia la selección
        lblSeleccionadas.textProperty().addListener((obs, oldVal, newVal) -> {
            btnConfirmar.setDisable(fichasSeleccionadas.isEmpty());
        });
        
        botonesContainer.getChildren().addAll(btnCancelar, btnConfirmar);
        
        root.getChildren().addAll(titulo, lblSeleccionadas, fichasContainer, botonesContainer);
        Scene scene = new Scene(root);
        
        // Aplicar estilos CSS
        scene.getStylesheets().add(cssPath);
        
        stage.setScene(scene);
    }
    
    /**
     * Actualiza la etiqueta con el número de fichas seleccionadas
     */
    private void actualizarSeleccionadas() {
        lblSeleccionadas.setText(fichasSeleccionadas.size() + " seleccionadas");
    }
    
    /**
     * Muestra el popup y espera a que el usuario lo cierre
     * @return Lista de letras seleccionadas para cambiar
     */
    public List<String> mostrarYEsperar() {
        stage.showAndWait();
        return letrasSeleccionadas;
    }
    
    /**
     * Obtiene los puntos correspondientes a una letra
     */
    private int obtenerPuntosPorLetra(char letra) {
        switch (Character.toUpperCase(letra)) {
            case 'A': case 'E': case 'O': case 'S': case 'I': case 'N': case 'L': case 'R': case 'T': case 'U':
                return 1;
            case 'D': case 'G':
                return 2;
            case 'B': case 'C': case 'M': case 'P':
                return 3;
            case 'F': case 'H': case 'V': case 'Y':
                return 4;
            case 'J': case 'K': case 'Ñ': case 'Q': case 'W': case 'X':
                return 8;
            case 'Z':
                return 10;
            default:
                return 0; // Para comodines o caracteres no reconocidos
        }
    }
}