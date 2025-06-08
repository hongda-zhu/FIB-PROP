package scrabble.presentation.popups;

import javafx.collections.FXCollections;
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

public class AñadirLetraPopup {

    private final ObservableList<ObservableList<String>> alfabeto;

    public AñadirLetraPopup(ObservableList<ObservableList<String>> alfabeto) {
        this.alfabeto = alfabeto;
    }

    public void mostrar(Stage owner) {
        Stage popup = new Stage();
        popup.initOwner(owner);
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Añadir letra al alfabeto");

        TextField campoLetra = new TextField();
        TextField campoPunt = new TextField();
        TextField campoFreq = new TextField();

        Label errorLetra = new Label();
        Label errorPunt = new Label();
        Label errorFreq = new Label();

        errorLetra.setStyle("-fx-text-fill: red;");
        errorPunt.setStyle("-fx-text-fill: red;");
        errorFreq.setStyle("-fx-text-fill: red;");

        campoLetra.setPromptText("Letra");
        campoPunt.setPromptText("Puntuación");
        campoFreq.setPromptText("Frecuencia");

        campoLetra.textProperty().addListener((obs, oldVal, newVal) -> {
            String texto = newVal.trim().toUpperCase();

            if (texto.isEmpty()) {
                campoLetra.setStyle("-fx-border-color: red;");
                errorLetra.setText("Campo obligatorio.");
            } else if (!texto.matches("[a-zA-ZñÑ·]+")) {
                campoLetra.setStyle("-fx-border-color: red;");
                errorLetra.setText("Solo letras, sin espacios ni símbolos.");
            } else if (alfabeto.stream().anyMatch(fila -> fila.get(0).equalsIgnoreCase(texto))) {
                campoLetra.setStyle("-fx-border-color: red;");
                errorLetra.setText("Letra ya existe.");
            } else {
                campoLetra.setStyle("");
                errorLetra.setText("");
            }
        });

        campoPunt.textProperty().addListener((obs, oldVal, newVal) -> {
            if (campoPunt.getText().isEmpty()) {
                campoPunt.setStyle("-fx-border-color: red;");
                errorPunt.setText("Campo obligatorio.");
            } else if (!newVal.matches("\\d+")) {
                campoPunt.setStyle("-fx-border-color: red;");
                errorPunt.setText("Debe ser un número entero positivo.");
            } else {
                campoPunt.setStyle("");
                errorPunt.setText("");
            }
        });

        campoFreq.textProperty().addListener((obs, oldVal, newVal) -> {
            if (campoFreq.getText().isEmpty()) {
                campoFreq.setStyle("-fx-border-color: red;");
                errorFreq.setText("Campo obligatorio.");
            } else if (!newVal.matches("\\d+")) {
                campoFreq.setStyle("-fx-border-color: red;");
                errorFreq.setText("Debe ser un número entero positivo.");
           } else {
                int valor = Integer.parseInt(newVal);
                if (valor <= 0) {
                    campoFreq.setStyle("-fx-border-color: red;");
                    errorFreq.setText("Debe ser mayor que 0.");
                } else {
                    campoFreq.setStyle("");
                    errorFreq.setText("");
                }
            }
        });

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setOnAction(e -> popup.close());

        Button btnAceptar = new Button("Aceptar");
        btnAceptar.setDefaultButton(true);

        btnAceptar.setOnAction(e -> {
            boolean valido = true;

            String letra = campoLetra.getText().trim().toUpperCase();
            if (letra.isEmpty()) {
                campoLetra.setStyle("-fx-border-color: red;");
                errorLetra.setText("Campo obligatorio.");
                valido = false;
            } else if (!letra.matches("[a-zA-ZñÑ·]+")) {
                campoLetra.setStyle("-fx-border-color: red;");
                errorLetra.setText("Solo letras, sin espacios ni símbolos.");
                valido = false;
            } else if (alfabeto.stream().anyMatch(fila -> fila.get(0).equalsIgnoreCase(letra))) {
                campoLetra.setStyle("-fx-border-color: red;");
                errorLetra.setText("Letra ya existe.");
                valido = false;
            } else {
                campoLetra.setStyle("");
                errorLetra.setText("");
            }

            String puntStr = campoPunt.getText().trim();
            if (puntStr.isEmpty()) {
                campoPunt.setStyle("-fx-border-color: red;");
                errorPunt.setText("Campo obligatorio.");
                valido = false;
            } else if (!puntStr.matches("\\d+") || Integer.parseInt(puntStr) < 0) {
                campoPunt.setStyle("-fx-border-color: red;");
                errorPunt.setText("Debe ser un número entero positivo.");
                valido = false;
            } else {
                campoPunt.setStyle("");
                errorPunt.setText("");
            }

            String freqStr = campoFreq.getText().trim();
            if (freqStr.isEmpty()) {
                campoFreq.setStyle("-fx-border-color: red;");
                errorFreq.setText("Campo obligatorio.");
                valido = false;
            } else if (!freqStr.matches("\\d+") || Integer.parseInt(freqStr) <= 0) {
                campoFreq.setStyle("-fx-border-color: red;");
                errorFreq.setText("Debe ser un número entero mayor que 0.");
                valido = false;
            } else {
                campoFreq.setStyle("");
                errorFreq.setText("");
            }

            if (valido) {
                alfabeto.add(FXCollections.observableArrayList(letra, freqStr, puntStr));
                popup.close();
            }
        });

        HBox btns = new HBox(20, btnCancelar, btnAceptar);
        btns.setAlignment(Pos.CENTER);

        VBox form = new VBox(6,
                new Label("Letra:"), campoLetra, errorLetra,
                new Label("Frecuencia:"), campoFreq, errorFreq,               
                new Label("Puntuación:"), campoPunt, errorPunt, btns
        );
        form.setPadding(new Insets(15));
        form.setAlignment(Pos.CENTER_LEFT);

        Scene scene = new Scene(form);

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
}
