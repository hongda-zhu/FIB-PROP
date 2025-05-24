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

public class EliminarJugadorPopup {
    private final Stage popupStage;

    public EliminarJugadorPopup() {
        popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        // Esto quita la barra de arriba del popup
        popupStage.initStyle(StageStyle.UNDECORATED);


        Label title = new Label("Eliminar Jugador");
        title.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        HBox header = new HBox(title);
        header.setStyle("-fx-background-color: #2c3e50; -fx-padding: 15px;");
        header.setAlignment(Pos.CENTER_LEFT);
        Label nombreJugadorLabel = new Label("Nombre del jugador:");
        TextField nombreJugadorField = new TextField();
        nombreJugadorField.setPromptText("Nombre del jugador a eliminar");

        VBox campos = new VBox(10, nombreJugadorLabel, nombreJugadorField);
        campos.setPadding(new Insets(20));

        Button btnEliminar = new Button("Aceptar");
        btnEliminar.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        // btnEliminar.setOnAction(e -> eliminarJugador());

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        btnCancelar.setOnAction(e -> popupStage.close());

        HBox buttonBox = new HBox(15, btnCancelar, btnEliminar);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(0, 20, 20, 20));

        VBox layout = new VBox(header, campos, buttonBox);
        layout.setStyle("-fx-background-color: #fdfcfa; -fx-border-radius: 10; -fx-background-radius: 10;");
        layout.setPrefWidth(400);

        Scene scene = new Scene(layout, 400, 200);
        popupStage.setScene(scene);
    }

    public void mostrar(Stage owner) {
        popupStage.initOwner(owner);
        popupStage.showAndWait();
    }
}