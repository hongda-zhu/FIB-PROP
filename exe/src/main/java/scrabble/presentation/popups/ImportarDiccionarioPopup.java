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

public class ImportarDiccionarioPopup {

    public void mostrar(Stage owner) {
        Stage popup = new Stage();
        popup.initOwner(owner);
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Importar diccionario");

        // Título
        Label titulo = new Label("Importar diccionario");
        titulo.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        HBox header = new HBox(titulo);
        header.setStyle("-fx-background-color: #2f3e4e; -fx-padding: 15px;");
        header.setAlignment(Pos.CENTER_LEFT);

        // Campo ruta
        Label lblRuta = new Label("Ruta del directorio");
        TextField inputRuta = new TextField();
        inputRuta.setPromptText("Ej: fonts/src/main/resources");

        Label recordatorio = new Label("Reminder: debe contener los archivos alpha.txt y words.txt.");
        recordatorio.setStyle("-fx-text-fill: #888; -fx-font-size: 11px;");

        VBox campos = new VBox(5, lblRuta, inputRuta, recordatorio);
        campos.setPadding(new Insets(20));

        // Botones
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        btnCancelar.setOnAction(e -> popup.close());

        Button btnImportar = new Button("Importar");
        btnImportar.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        btnImportar.setOnAction(e -> {
            String ruta = inputRuta.getText();
            System.out.println("Importando desde: " + ruta);
            // Aquí puedes añadir la lógica real de importación
            popup.close();
        });

        HBox botones = new HBox(15, btnCancelar, btnImportar);
        botones.setAlignment(Pos.CENTER);
        botones.setPadding(new Insets(0, 20, 20, 20));

        VBox layout = new VBox(header, campos, botones);
        layout.setStyle("-fx-background-color: #fdfcfa; -fx-border-radius: 10; -fx-background-radius: 10;");
        layout.setPrefWidth(400);

        popup.setScene(new Scene(layout));
        popup.showAndWait();
    }
}
