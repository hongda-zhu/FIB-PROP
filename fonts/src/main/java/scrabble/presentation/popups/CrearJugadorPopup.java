package scrabble.presentation.popups;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import scrabble.presentation.viewControllers.ControladorJugadoresView;

/**
 * Popup para crear un nuevo jugador
 */
public class CrearJugadorPopup {
    private final ControladorJugadoresView controlador;
    
    public CrearJugadorPopup(ControladorJugadoresView controlador) {
        this.controlador = controlador;
    }
    
    public void mostrar(Stage owner) {
        Stage popup = new Stage();
        popup.initOwner(owner);
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Crear jugador");
        popup.initStyle(StageStyle.UNDECORATED);
        // Título
        Label titulo = new Label("Crear jugador");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        HBox header = new HBox(titulo);
        header.setStyle("-fx-background-color: #2f3e4e; -fx-padding: 20px;");
        header.setAlignment(Pos.CENTER);
        header.setPrefHeight(80);

        // Campo nombre
        Label lblNombre = new Label("Nombre del jugador");
        lblNombre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        TextField inputNombre = new TextField();
        inputNombre.setPromptText("Ingrese un nombre para el jugador");
        inputNombre.setPrefHeight(40);
        inputNombre.setStyle("-fx-font-size: 14px; -fx-background-radius: 5px;");
        
        // Descripción opcional
        Label descripcion = new Label("El nombre debe ser único y se utilizará para identificar al jugador en las partidas.");
        descripcion.setStyle("-fx-text-fill: #555; -fx-font-size: 12px;");

        VBox campos = new VBox(10, lblNombre, inputNombre, descripcion);
        campos.setPadding(new Insets(30));
        campos.setAlignment(Pos.CENTER_LEFT);

        // Botones
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.getStyleClass().addAll("btn-effect", "btn-danger");
        btnCancelar.setPrefWidth(120);
        btnCancelar.setPrefHeight(40);
        // Color sólido rojo como en la imagen de referencia
        btnCancelar.setStyle("-fx-font-size: 14px; -fx-background-color: #f44336; -fx-text-fill: white;");
        btnCancelar.setOnAction(e -> popup.close());

        Button btnAceptar = new Button("Aceptar");
        btnAceptar.getStyleClass().addAll("btn-effect", "btn-success");
        btnAceptar.setPrefWidth(120);
        btnAceptar.setPrefHeight(40);
        // Color sólido verde como en la imagen de referencia
        btnAceptar.setStyle("-fx-font-size: 14px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnAceptar.setOnAction(e -> {
            String nombre = inputNombre.getText();
            if (nombre == null || nombre.trim().isEmpty()) {
                controlador.mostrarAlerta("warning", "Advertencia", "Por favor, ingrese un nombre para el jugador");
                return;
            }
            
            try {
                boolean creado = controlador.crearJugador(nombre);
                if (creado) {
                    controlador.mostrarAlerta("success", "Información", "Jugador creado exitosamente");
                    popup.close();
                    
                    // Actualizar la vista del controlador después de cerrar el popup
                    controlador.mostrarVistaGestionJugadores();
                }
                else {
                    controlador.mostrarAlerta("error", "Error", "El nombre del jugador ya existe.");
                }
            } catch (Exception ex) {
                // Capturar la excepción que se lanza cuando el usuario ya existe
                String mensaje = "No se pudo crear el jugador. ";
                if (ex.getMessage() != null && !ex.getMessage().isEmpty()) {
                    mensaje += ex.getMessage();
                } else {
                    mensaje += "El nombre ya existe.";
                }
                controlador.mostrarAlerta("error", "Error", mensaje);
                System.out.println("Error capturado: " + ex.getClass().getName() + " - " + ex.getMessage());
                popup.close();
            }
        });

        HBox botones = new HBox(20, btnCancelar, btnAceptar);
        botones.setAlignment(Pos.CENTER);
        botones.setPadding(new Insets(20, 20, 30, 20));

        VBox layout = new VBox(header, campos, botones);
        layout.setStyle("-fx-background-color: white; -fx-border-radius: 10; -fx-background-radius: 10;");
        layout.setPrefWidth(550);

        // Cargar el CSS para los botones
        Scene scene = new Scene(layout);
        try {
            String cssResource = "/styles/button.css";
            scene.getStylesheets().add(getClass().getResource(cssResource).toExternalForm());
        } catch (Exception e) {
            System.err.println("Error al cargar CSS: " + e.getMessage());
        }

        popup.setScene(scene);
        popup.setWidth(550);
        popup.setHeight(400);
        popup.showAndWait();
    }

}