package scrabble.presentation.popups;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import scrabble.presentation.componentes.Ficha;
import scrabble.presentation.views.VistaTablero;


/**
 * Popup modal para seleccionar fichas del rack para intercambio durante el juego.
 * Proporciona interfaz interactiva que permite seleccionar múltiples fichas
 * del rack actual del jugador para intercambiar por fichas aleatorias de la bolsa.
 * 
 * Características principales:
 * - Interfaz visual de fichas seleccionables con feedback inmediato
 * - Seguimiento en tiempo real del número de fichas seleccionadas
 * - Validación automática para habilitar/deshabilitar confirmación
 * - Representación visual exacta de fichas con valores de puntuación
 * - Integración con sistema de intercambio de fichas del juego
 * 
 * @version 1.0
 * @since 1.0
 */
public class CambiarFichasPopup {
    
    private Stage stage;
    private List<Ficha> fichasSeleccionadas = new ArrayList<>();
    private List<String> letrasSeleccionadas = new ArrayList<>();
    private Label lblSeleccionadas;
    private int casillaTamaño;
    private VistaTablero vistaTablero;



    /**
     * Constructor que inicializa el popup con el rack actual del jugador.
     * Configura la ventana modal y prepara la interfaz para mostrar todas
     * las fichas disponibles del jugador para selección de intercambio.
     * 
     * @pre rack no debe ser null, casillaTamaño debe ser positivo, vistaTablero inicializada.
     * @param rack Mapa con letras y cantidades de fichas disponibles del jugador
     * @param casillaTamaño Tamaño en píxeles para renderizar cada ficha
     * @param vistaTablero Referencia a la vista del tablero para obtener puntuaciones
     * @post El popup queda configurado y listo para mostrar con todas las fichas
     *       del rack representadas visualmente y eventos de selección configurados.
     */
    public CambiarFichasPopup(Map<String, Integer> rack, int casillaTamaño, VistaTablero vistaTablero) {
        this.vistaTablero = vistaTablero;
        this.casillaTamaño = casillaTamaño;
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("");
        stage.setResizable(false);
        
        crearInterfaz(rack);
    }
    

    /**
     * Crea la interfaz visual completa del popup de intercambio de fichas.
     * Genera representación visual de todas las fichas del rack, configura
     * eventos de selección y establece botones de confirmación y cancelación.
     * 
     * @pre rack no debe ser null y debe contener las fichas actuales del jugador.
     * @param rack Mapa con las fichas disponibles para intercambio
     * @post La interfaz está completamente configurada con fichas interactivas,
     *       contador de selección y botones funcionales con estilos CSS aplicados.
     */
    private void crearInterfaz(Map<String, Integer> rack) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        
        Label titulo = new Label("Cambiar fichas");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        lblSeleccionadas = new Label("0 seleccionadas");
        lblSeleccionadas.setStyle("-fx-font-size: 14px;");
        
        HBox fichasContainer = new HBox(5);
        fichasContainer.setAlignment(Pos.CENTER);
        fichasContainer.setPadding(new Insets(10));
        
        for (Map.Entry<String, Integer> entry : rack.entrySet()) {
            String letra = entry.getKey();
            int cantidad = entry.getValue();
            int puntos = obtenerPuntosPorLetra(letra);
            
            for (int i = 0; i < cantidad; i++) {
                Ficha ficha = new Ficha(letra, puntos);
                ficha.setMinSize(casillaTamaño, casillaTamaño);
                ficha.setPrefSize(casillaTamaño, casillaTamaño);
                ficha.setMaxSize(casillaTamaño, casillaTamaño);
                
                // Configurar evento de clic para seleccionar/deseleccionar
                ficha.setOnMouseClicked(e -> {
                    if (ficha.isSeleccionada()) {
                        ficha.deseleccionar();
                        fichasSeleccionadas.remove(ficha);
                        letrasSeleccionadas.remove(String.valueOf(ficha.getLetra()));
                        
                    } else {
                        ficha.seleccionar();
                        fichasSeleccionadas.add(ficha);
                        letrasSeleccionadas.add(String.valueOf(ficha.getLetra()));
                    }
                    actualizarSeleccionadas();
                });
                
                fichasContainer.getChildren().add(ficha);
            }
        }
        
        HBox botonesContainer = new HBox(10);
        botonesContainer.setAlignment(Pos.CENTER);
        
        String cssPath = PausaPopup.class.getResource("/styles/button.css").toExternalForm();
        
        Button btnConfirmar = new Button("Confirmar");
        btnConfirmar.getStyleClass().add("btn-effect");
        btnConfirmar.getStyleClass().add("btn-success");
        btnConfirmar.setMinWidth(120);
        btnConfirmar.setMinHeight(40);
        btnConfirmar.setPrefHeight(40);
        btnConfirmar.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        btnConfirmar.setDisable(true); 
        
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.getStyleClass().add("btn-effect");
        btnCancelar.getStyleClass().add("btn-danger");
        btnCancelar.setMinWidth(120);
        btnCancelar.setMinHeight(40);
        btnCancelar.setPrefHeight(40);
        btnCancelar.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
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
        
        scene.getStylesheets().add(cssPath);
        
        stage.setScene(scene);
    }
    

    /**
     * Actualiza la etiqueta que muestra el número de fichas seleccionadas.
     * Proporciona retroalimentación visual inmediata al usuario sobre
     * cuántas fichas tiene actualmente seleccionadas para intercambio.
     * 
     * @pre lblSeleccionadas debe estar inicializada correctamente.
     * @post La etiqueta muestra el número actual de fichas seleccionadas
     *       en formato legible para el usuario.
     */
    private void actualizarSeleccionadas() {
        lblSeleccionadas.setText(fichasSeleccionadas.size() + " seleccionadas");
    }
    

    /**
     * Muestra el popup modal y espera interacción del usuario.
     * Presenta la interfaz de selección de fichas y bloquea la ventana padre
     * hasta que el usuario confirme el intercambio o cancele la operación.
     * 
     * @pre La interfaz debe haber sido creada correctamente.
     * @return Lista de strings con las letras seleccionadas para intercambio
     * @post Se devuelve lista de letras seleccionadas si se confirmó,
     *       o lista vacía si se canceló la operación.
     */
    public List<String> mostrarYEsperar() {
        stage.showAndWait();
        return letrasSeleccionadas;
    }
    

    /**
     * Obtiene los puntos de puntuación correspondientes a una letra específica.
     * Consulta el valor de puntuación de la letra en el diccionario actual
     * para mostrar información correcta en las fichas visuales.
     * 
     * @pre letra no debe ser null y debe existir en el alfabeto del diccionario.
     * @param letra Letra cuyo valor de puntuación obtener
     * @return int con los puntos correspondientes a la letra especificada
     * @post Se devuelve el valor de puntuación sin modificar el diccionario.
     */
    private int obtenerPuntosPorLetra(String letra) {
        return vistaTablero.obtenerPuntosPorLetra(letra);
    }
}