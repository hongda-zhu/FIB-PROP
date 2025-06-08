package scrabble.presentation.popups;

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
 * Popup modal para selección de letra al usar fichas comodín durante el juego.
 * Presenta interfaz visual con todas las letras disponibles del alfabeto
 * para que el jugador elija qué letra representará su ficha comodín.
 * 
 * Características principales:
 * - Visualización organizada de todas las letras del alfabeto actual
 * - Fichas interactivas con efectos hover para mejor experiencia
 * - Organización automática en filas para optimizar espacio visual
 * - Selección directa por clic con cierre automático del popup
 * - Integración completa con sistema de puntuación del diccionario
 * 
 * @version 1.0
 * @since 1.0
 */
public class ComodinPopup {
    
    private Stage stage;
    private String letraSeleccionada = null;
    private int casillaTamaño;
    private VistaTablero vistaTablero;


    /**
     * Constructor que inicializa el popup para selección de letra comodín.
     * Configura la ventana modal y establece referencias necesarias para
     * obtener alfabeto y puntuaciones del diccionario actual del juego.
     * 
     * @pre casillaTamaño debe ser positivo, vistaTablero debe estar inicializada.
     * @param casillaTamaño Tamaño en píxeles para renderizar cada ficha de letra
     * @param vistaTablero Referencia a la vista del tablero para acceso al alfabeto
     * @post El popup queda configurado y listo para mostrar con configuración modal
     *       establecida y referencias a datos del juego configuradas.
     */
    public ComodinPopup(int casillaTamaño, VistaTablero vistaTablero) {
        this.vistaTablero = vistaTablero;
        this.casillaTamaño = casillaTamaño;
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("Seleccionar letra para comodín");
        stage.setResizable(false);
        
        crearInterfaz();
    }
    

    /**
     * Crea la interfaz visual completa para selección de letra comodín.
     * Genera grid organizado de fichas representando todas las letras del alfabeto,
     * configura eventos de interacción y establece efectos visuales hover.
     * 
     * @pre vistaTablero debe proporcionar acceso válido al alfabeto del diccionario.
     * @post La interfaz está completamente configurada con fichas de todas las letras
     *       organizadas en filas, efectos hover aplicados y eventos de selección activos.
     */
    private void crearInterfaz() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        
        Label titulo = new Label("Selecciona la letra del comodín");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label instruccion = new Label("Haz clic en la letra que quieres usar:");
        instruccion.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        
        // Contenedor para las fichas organizadas en filas
        VBox fichasContainer = new VBox(10);
        fichasContainer.setAlignment(Pos.CENTER);
        fichasContainer.setPadding(new Insets(10));
        
        Map<String, Integer> alfabeto = vistaTablero.getAllAlphabet();
        
        HBox filaActual = new HBox(5);
        filaActual.setAlignment(Pos.CENTER);
        int fichasPorFila = 0;
        final int MAX_FICHAS_POR_FILA = 7;
        
        for (Map.Entry<String, Integer> entry : alfabeto.entrySet()) {
            String letra = entry.getKey();
            
            if (letra.equals("#")) {
                continue;
            }
            
            int puntos = entry.getValue();
            
            Ficha ficha = new Ficha(letra, puntos);
            ficha.setMinSize(casillaTamaño, casillaTamaño);
            ficha.setPrefSize(casillaTamaño, casillaTamaño);
            ficha.setMaxSize(casillaTamaño, casillaTamaño);
            
            ficha.setOnMouseClicked(e -> {
                letraSeleccionada = letra;
                stage.close();
            });
            
            ficha.setOnMouseEntered(e -> {
                ficha.setStyle(ficha.getStyle() + "; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 0);");
            });
            
            ficha.setOnMouseExited(e -> {
                ficha.setStyle(ficha.getStyle().replace("; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 0)", ""));
            });
            
            filaActual.getChildren().add(ficha);
            fichasPorFila++;
            
            // Si completamos una fila o es la última letra, añadir la fila al contenedor
            if (fichasPorFila == MAX_FICHAS_POR_FILA) {
                fichasContainer.getChildren().add(filaActual);
                filaActual = new HBox(5);
                filaActual.setAlignment(Pos.CENTER);
                fichasPorFila = 0;
            }
        }
        
        // Añadir la última fila si tiene elementos
        if (fichasPorFila > 0) {
            fichasContainer.getChildren().add(filaActual);
        }
        
        // Botón cancelar
        HBox botonesContainer = new HBox(10);
        botonesContainer.setAlignment(Pos.CENTER);
        
        String cssPath = PausaPopup.class.getResource("/styles/button.css").toExternalForm();
        
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.getStyleClass().add("btn-effect");
        btnCancelar.getStyleClass().add("btn-danger");
        btnCancelar.setMinWidth(120);
        btnCancelar.setMinHeight(40);
        btnCancelar.setPrefHeight(40);
        btnCancelar.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        btnCancelar.setOnAction(e -> {
            letraSeleccionada = null;
            stage.close();
        });
        
        botonesContainer.getChildren().add(btnCancelar);
        
        root.getChildren().addAll(titulo, instruccion, fichasContainer, botonesContainer);
        Scene scene = new Scene(root);
        
        scene.getStylesheets().add(cssPath);
        
        stage.setScene(scene);
    }
    

    /**
     * Muestra el popup modal y espera selección del usuario.
     * Presenta la interfaz de selección de letras y bloquea la ventana padre
     * hasta que el usuario seleccione una letra o cancele la operación.
     * 
     * @pre La interfaz debe haber sido creada correctamente con todas las fichas.
     * @return String con la letra seleccionada o null si se canceló
     * @post Se devuelve la letra seleccionada por el usuario para el comodín,
     *       o null si se canceló la operación sin seleccionar ninguna letra.
     */
    public String mostrarYEsperar() {
        stage.showAndWait();
        return letraSeleccionada;
    }
}