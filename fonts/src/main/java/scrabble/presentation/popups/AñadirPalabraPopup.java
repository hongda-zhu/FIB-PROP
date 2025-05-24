package scrabble.presentation.popups;

import java.util.Set;

import javafx.collections.ObservableList;
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

public class AñadirPalabraPopup {

    private final ObservableList<String> palabras;
    private final Set<String> alfabeto;

    public AñadirPalabraPopup(ObservableList<String> palabras, Set<String> alfabeto) {
        this.palabras = palabras;
        this.alfabeto = alfabeto;
    }

    public void mostrar(Stage owner) {
        Stage popup = new Stage();
        popup.initOwner(owner);
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Añadir palabra al diccionario");

        TextField campoPalabra = new TextField();
        campoPalabra.setPromptText("Ej: PALABRA");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        campoPalabra.textProperty().addListener((obs, oldVal, newVal) -> {
            String texto = newVal.trim().toUpperCase();

            if (texto.isEmpty()) {
                campoPalabra.setStyle("-fx-border-color: red;");
                errorLabel.setText("Campo obligatorio.");
            } else if (!texto.matches("[\\p{L}·]+")) {
                campoPalabra.setStyle("-fx-border-color: red;");
                errorLabel.setText("Solo letras sin espacios ni símbolos.");
            } else if (palabras.stream().anyMatch(p -> p.equalsIgnoreCase(texto))) {
                campoPalabra.setStyle("-fx-border-color: red;");
                errorLabel.setText("La palabra ya existe.");
            } else if (!puedeConstruirseDesdeAlfabeto(texto)) {
                campoPalabra.setStyle("-fx-border-color: red;");
                errorLabel.setText("Contiene letras no presentes en el alfabeto.");
            } else {
                campoPalabra.setStyle("");
                errorLabel.setText("");
            }
        });

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setOnAction(e -> popup.close());

        Button btnAceptar = new Button("Aceptar");
        btnAceptar.setDefaultButton(true);
        btnAceptar.setOnAction(e -> {
            String texto = campoPalabra.getText().trim().toUpperCase();
            boolean valido = true;
            if (texto.isEmpty()) {
                campoPalabra.setStyle("-fx-border-color: red;");
                errorLabel.setText("Campo obligatorio.");
                valido = false;
            } else if (!texto.matches("[\\p{L}·]+")) {
                campoPalabra.setStyle("-fx-border-color: red;");
                errorLabel.setText("Solo letras sin espacios ni símbolos.");
                valido = false;
            } else if (palabras.stream().anyMatch(p -> p.equalsIgnoreCase(texto))) {
                campoPalabra.setStyle("-fx-border-color: red;");
                errorLabel.setText("La palabra ya existe.");
                valido = false;
            } else if (!puedeConstruirseDesdeAlfabeto(texto)) {
                campoPalabra.setStyle("-fx-border-color: red;");
                errorLabel.setText("Contiene letras no presentes en el alfabeto.");
                valido = false;
            } else {
                campoPalabra.setStyle("");
                errorLabel.setText("");
            }
            if (valido) {
                palabras.add(texto);
                popup.close();
            }
        });

        HBox buttonBox = new HBox(20, btnCancelar, btnAceptar);
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(8,
                new Label("Nueva palabra:"),
                campoPalabra,
                errorLabel,
                buttonBox
        );
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.CENTER_LEFT);

        Scene scene = new Scene(layout);

         // Aplicar CSS
        try {
            String cssResource = "/styles/button.css";
            scene.getStylesheets().add(getClass().getResource(cssResource).toExternalForm());
        } catch (Exception e) {
            System.err.println("Error al cargar CSS: " + e.getMessage());
        }

        btnAceptar.getStyleClass().addAll("btn-effect", "btn-success");
        btnCancelar.getStyleClass().addAll("btn-effect", "btn-danger");

        popup.setScene(scene);
        popup.showAndWait();
    }

    // Algoritmo tipo backtracking para verificar si se puede construir la palabra
    private boolean puedeConstruirseDesdeAlfabeto(String palabra) {
        return puedeFormarseDesde(palabra, 0);
    }

    private boolean puedeFormarseDesde(String palabra, int idx) {
        if (idx == palabra.length()) return true;

        for (String letra : alfabeto) {
            if (palabra.startsWith(letra, idx)) {
                if (puedeFormarseDesde(palabra, idx + letra.length())) return true;
            }
        }
        return false;
    }
}
