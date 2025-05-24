package scrabble.presentation.views;

import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;
import scrabble.presentation.componentes.DiccionarioVisual;
import scrabble.presentation.viewControllers.ControladorDiccionario;

/**
 * Clase que representa la vista para modificar un diccionario.
 */

public class ModificarDiccionarioView {
    private Parent view;
    private ControladorDiccionario controlador;

    private TextField campoNombre;
    private TableView<ObservableList<String>> tablaAlfabeto;
    private Button btnCancelar;
    private Button btnGuardar;
    private TableView<String> tablaPalabras;
    private Button btnAnadirPalabra;
    private HBox headerPalabras;
    private ObservableList<String> palabrasOriginales;
    private ObservableList<ObservableList<String>> alfabetoOriginal;
    private HBox headerAlfabeto;

    /**
     * Constructor de la vista de modificación de diccionario.
     *
     * @param controlador      Controlador de la vista.
     * @param diccionarioVisual Diccionario visual a modificar.
     */

    public ModificarDiccionarioView(ControladorDiccionario controlador, DiccionarioVisual diccionarioVisual) {
        this.controlador = controlador;

        crearBotones();

        Label title = new Label("Gestión de Diccionarios - Modificar diccionario");
        title.setFont(new Font("Arial", 20));
        title.setStyle("-fx-text-fill: white;");
        HBox topBar = new HBox(title);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(15));
        topBar.setStyle("-fx-background-color: #2c3e50;");

        Label subtitle = new Label("Modificar Diccionario");
        subtitle.setFont(new Font("Arial", 16));
        subtitle.setStyle("-fx-font-weight: bold;");

        this.campoNombre = new TextField(diccionarioVisual.getNombre());
        campoNombre.setDisable(true);

        this.tablaAlfabeto = new TableView<>();
        inicializarTablaAlfabeto(diccionarioVisual);

        this.tablaPalabras = new TableView<>();
        inicializarTablaPalabras(diccionarioVisual);

        VBox campos = new VBox(10,
                new Label("Nombre del Diccionario"), campoNombre,
                headerAlfabeto, tablaAlfabeto,
                headerPalabras, tablaPalabras
        );
        campos.setPadding(new Insets(20));
        campos.setStyle("-fx-background-color: #f8f8f8; -fx-border-color: lightgray; -fx-border-radius: 8;");
        VBox.setVgrow(campos, Priority.ALWAYS);

        HBox botones = new HBox(10, btnCancelar, btnGuardar);
        botones.setAlignment(Pos.CENTER);
        botones.setPadding(new Insets(10));

        VBox mainContent = new VBox(10, subtitle, campos, botones);
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setPadding(new Insets(20));
        mainContent.setStyle("-fx-background-color: #ffffff;");

        VBox root = new VBox(10, topBar, mainContent);
        VBox.setVgrow(mainContent, Priority.ALWAYS);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f0f0f0;");
        root.setPadding(new Insets(20));
        root.setMaxWidth(1200);

        root.widthProperty().addListener((obs, oldVal, newVal) -> {
            mainContent.setPrefWidth(newVal.doubleValue() * 0.8);
        });

        root.heightProperty().addListener((obs, oldVal, newVal) -> {
            campos.setPrefHeight(newVal.doubleValue() * 0.5);
        });

        this.view = root;

        try {
            List<String> cssResources = List.of("/styles/button.css", "/styles/excel-table.css");
            for (String cssResource : cssResources) {
                URL cssUrl = getClass().getResource(cssResource);
                if (cssUrl != null) {
                    view.getStylesheets().add(cssUrl.toExternalForm());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar CSS en ModificarDiccionarioView: " + e.getMessage());
        }

        aplicarCssBotones();
    }

    /**
     * Inicializa la tabla del alfabeto.
     *
     * @param diccionarioVisual Diccionario visual a modificar.
     */

    private void inicializarTablaAlfabeto(DiccionarioVisual diccionarioVisual) {
        tablaAlfabeto.setEditable(false);
        this.alfabetoOriginal = FXCollections.observableArrayList();
        tablaAlfabeto.getStyleClass().add("excel-table");

        FilteredList<ObservableList<String>> filtrada = new FilteredList<>(alfabetoOriginal, s -> true);
        tablaAlfabeto.setItems(filtrada);

        TableColumn<ObservableList<String>, String> colLetra = new TableColumn<>("Letra");
        colLetra.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));

        colLetra.setCellFactory(column -> new TextFieldTableCell<>(new DefaultStringConverter()));
        // {
        //     @Override
        //     protected void updateItem(String item, boolean empty) {
        //         super.updateItem(item, empty);
        //         if (empty || item == null) {
        //             setText(null);
        //         } else {
        //             setText(item);
        //             setStyle("-fx-background-color: #eeeeee; -fx-text-fill: gray;");
        //         }
        //     }
        // });

        colLetra.setOnEditCommit(event -> {
            String newValue = event.getNewValue().trim().toUpperCase();
            event.getRowValue().set(0, newValue);
        });

        // Columna Puntuación con validación visual
        TableColumn<ObservableList<String>, String> colPunt = crearColumnaNumericaAutoCommit("Puntuación", 1);
         // Columna Frecuencia con validación visual
        TableColumn<ObservableList<String>, String> colFreq = crearColumnaNumericaAutoCommit("Frecuencia", 2);
        
        colLetra.setEditable(false);

        TextField campoBusqueda = new TextField();
        campoBusqueda.setPromptText("Buscar letra...");
        campoBusqueda.setOnKeyReleased(e -> {
            String filtro = campoBusqueda.getText().toUpperCase();
            filtrada.setPredicate(palabra -> !palabra.isEmpty() && palabra.get(0).toUpperCase().startsWith(filtro));
        });

        campoBusqueda.setMaxWidth(200);
        campoBusqueda.setAlignment(Pos.CENTER_LEFT);

        this.headerAlfabeto = new HBox(10);
        Label alfabetoLabel = new Label("Alfabeto");
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        headerAlfabeto.getChildren().addAll(alfabetoLabel, campoBusqueda, espacio);
        headerAlfabeto.setAlignment(Pos.CENTER_LEFT);

        tablaAlfabeto.getColumns().addAll(colLetra, colPunt, colFreq);
        tablaAlfabeto.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaAlfabeto.setPlaceholder(new Label("No hay letras añadidas."));
        tablaAlfabeto.setFixedCellSize(25);
        tablaAlfabeto.setPrefHeight(26);
        tablaAlfabeto.setMaxHeight(250);

        tablaAlfabeto.getItems().addListener((ListChangeListener<ObservableList<String>>) change -> {
            int filas = tablaAlfabeto.getItems().size();
            tablaAlfabeto.setPrefHeight((filas + 1.05) * tablaAlfabeto.getFixedCellSize());
        });

        for (String entrada : diccionarioVisual.getAlfabeto()) {
            String[] partes = entrada.trim().split("\\s+");
            if (partes.length == 3) {
                alfabetoOriginal.add(FXCollections.observableArrayList(partes[0], partes[1], partes[2]));
            }
        }
    }

    private TableColumn<ObservableList<String>, String> crearColumnaNumericaAutoCommit(String titulo, int indexColumna) {
        TableColumn<ObservableList<String>, String> columna = new TableColumn<>(titulo);

        columna.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(indexColumna)));

        columna.setCellFactory(column -> new TextFieldTableCell<>(new DefaultStringConverter())); 
        // {
        //     @Override
        //     public void startEdit() {
        //         super.startEdit();
        //         TextField textField = (TextField) getGraphic();
        //         if (textField != null) {
        //             textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
        //                 if (!newVal && isEditing()) {
        //                     commitEdit(textField.getText()); // simula Enter al perder el foco
        //                 }
        //             });
        //         }
        //     }

        //     @Override
        //     public void updateItem(String item, boolean empty) {
        //         super.updateItem(item, empty);
        //         if (empty || item == null || item.isBlank()) {
        //             setText(null);
        //             setStyle("");
        //         } else {
        //             setText(item);
        //             try {
        //                 int valor = Integer.parseInt(item);
        //                 if (valor < 0) {
        //                     setStyle("-fx-background-color: #ffe6e6; -fx-text-fill: #e74c3c;");
        //                 } else {
        //                     setStyle("");
        //                 }
        //             } catch (NumberFormatException e) {
        //                 setStyle("-fx-background-color: #ffe6e6; -fx-text-fill: #e74c3c;");
        //             }
        //         }
        //     }
        // });

        columna.setOnEditCommit(event -> event.getRowValue().set(indexColumna, event.getNewValue()));

        return columna;
    }

    /**
     * Inicializa la tabla de palabras.
     *
     * @param diccionarioVisual Diccionario visual a modificar.
     */

    private void inicializarTablaPalabras(DiccionarioVisual diccionarioVisual) {
        this.palabrasOriginales = FXCollections.observableArrayList(diccionarioVisual.getPalabras());
        this.tablaPalabras.setEditable(false);
        this.tablaPalabras.getStyleClass().add("excel-table");

        FilteredList<String> filtrada = new FilteredList<>(palabrasOriginales, s -> true);
        tablaPalabras.setItems(filtrada);

        TableColumn<String, String> palabraCol = new TableColumn<>("Palabra");
        palabraCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        palabraCol.setCellFactory(crearCeldaEditableConValidacion(palabrasOriginales));
        // palabraCol.setOnEditCommit(event -> {
        //     int index = event.getTablePosition().getRow();
        //     String palabraAnterior = filtrada.get(index);
        //     int originalIndex = palabrasOriginales.indexOf(palabraAnterior);

        //     if (originalIndex != -1) {
        //         palabrasOriginales.set(originalIndex, event.getNewValue());
        //     }
        // });

        TableColumn<String, Void> colEliminar = new TableColumn<>("");
        colEliminar.setPrefWidth(14);
        colEliminar.setMaxWidth(140);
        colEliminar.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("x");

            {
                btn.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-cursor: hand; -fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                btn.setOnAction(e -> {
                    int index = getIndex();
                    if (index >= 0 && index < filtrada.size()) {
                        String item = filtrada.get(index);
                        palabrasOriginales.remove(item); // elimina de la lista original
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        tablaPalabras.getColumns().addAll(palabraCol, colEliminar);
        tablaPalabras.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaPalabras.setPlaceholder(new Label("No hay palabras añadidas."));
        tablaPalabras.setFixedCellSize(25);
        tablaPalabras.setPrefHeight(26);
        tablaPalabras.setMaxHeight(300);


        tablaPalabras.getItems().addListener((ListChangeListener<String>) change -> {
            int filas = tablaPalabras.getItems().size();
            tablaPalabras.setPrefHeight((filas + 1.05) * tablaPalabras.getFixedCellSize());
        });

        palabrasOriginales.add("");

        int lastIndex = palabrasOriginales.size() - 1;
        if (lastIndex >= 0 && palabrasOriginales.get(lastIndex).isEmpty()) {
            palabrasOriginales.remove(lastIndex);
        }

        
        TextField campoBusqueda = new TextField();
        campoBusqueda.setPromptText("Buscar palabra...");
        campoBusqueda.setOnKeyReleased(e -> {
            String filtro = campoBusqueda.getText().toUpperCase();
            filtrada.setPredicate(palabra -> palabra.toUpperCase().startsWith(filtro));
        });
        campoBusqueda.setMaxWidth(200);
        campoBusqueda.setAlignment(Pos.CENTER_LEFT);
        this.headerPalabras = new HBox(10);
        Label lblPalabras = new Label("Diccionario");
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        headerPalabras.getChildren().addAll(lblPalabras, campoBusqueda, espacio, btnAnadirPalabra);
        headerPalabras.setAlignment(Pos.CENTER_LEFT);

    }

    private Callback<TableColumn<String, String>, TableCell<String, String>> crearCeldaEditableConValidacion(ObservableList<String> listaBase) {
        return column -> new TableCell<>() {
            private final TextField textField = new TextField();

            {
                // Al perder el foco, aplicar edición
                textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal && isEditing()) {
                        commitEdit(textField.getText());
                    }
                });
            }

            @Override
            public void startEdit() {
                super.startEdit();
                textField.setText(getItem());
                setGraphic(textField);
                setText(null);
                textField.selectAll();
                textField.requestFocus();
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                setText(getItem());
                setGraphic(null);
            }

            @Override
            public void commitEdit(String newValue) {
                if (newValue == null || newValue.isBlank()) {
                    cancelEdit(); // No aceptamos palabras vacías
                    return;
                }

                // Evitar duplicados
                int currentIndex = getIndex();
                String oldValue = getItem();
                if (!newValue.equals(oldValue) && listaBase.contains(newValue)) {
                    cancelEdit(); // No duplicados
                    return;
                }

                super.commitEdit(newValue);

                // Reemplazar en la lista base
                int originalIndex = listaBase.indexOf(oldValue);
                if (originalIndex != -1) {
                    listaBase.set(originalIndex, newValue);
                }

                setText(newValue);
                setGraphic(null);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (isEditing()) {
                        textField.setText(item);
                        setGraphic(textField);
                        setText(null);
                    } else {
                        setText(item);
                        setGraphic(null);
                    }
                }
            }
        };
    }

    /**
     * Crea los botones de la vista.
     */

    private void crearBotones() {
        btnCancelar = crearBoton("Cancelar", "#e74c3c");
        btnCancelar.setOnAction(e -> controlador.mostrarVistaGestion());

        btnGuardar = crearBoton("Guardar cambios", "#27ae60");
        btnGuardar.setOnAction(e -> {
            String nombre = campoNombre.getText();
            List<ObservableList<String>> filas = tablaAlfabeto.getItems();

            // for (int i = 0; i < filas.size(); i++) {
            //     ObservableList<String> fila = filas.get(i);
            //     if (fila.size() < 3 || fila.get(0).isBlank() || fila.get(1).isBlank() || fila.get(2).isBlank()) {
            //         controlador.mostrarAlerta("error", "Error", "Fila incompleta en la fila " + (i + 1));
            //         return;
            //     }
            //     try {
            //         Integer.parseInt(fila.get(1));
            //         Integer.parseInt(fila.get(2));
            //     } catch (NumberFormatException ex) {
            //         controlador.mostrarAlerta("error", "Error", "Puntuación o frecuencia inválida en la fila " + (i + 1));
            //         return;
            //     }
            // }

            List<String> alfabetoList = alfabetoOriginal.stream()
                    .map(item -> item.get(0).toUpperCase() + " " + item.get(1) + " " + item.get(2))
                    .toList();   

            Set<String> letrasValidas = alfabetoOriginal.stream()
                    .map(fila -> fila.get(0).toUpperCase())
                    .collect(Collectors.toSet());

            List<String> palabrasList = palabrasOriginales.stream()
                    .filter(palabra -> !palabra.isBlank())
                    .map(String::toUpperCase)
                    .distinct()
                    .toList();

            if (controlador.checkDiccioanario(palabrasList, letrasValidas)) {
                controlador.modificarDiccionario(nombre, alfabetoList, palabrasList);
                controlador.mostrarVistaGestion();
            }
        });
        btnAnadirPalabra = new Button("+");
        btnAnadirPalabra.setOnAction(e -> {
            Set<String> letrasValidas = alfabetoOriginal.stream()
                    .map(fila -> fila.get(0).toUpperCase())
                    .collect(Collectors.toSet());
            controlador.mostrarPopupAñadirPalabra(palabrasOriginales, letrasValidas);
        });
    }

    /**
     * Crea un botón con el texto y color especificados.
     *
     * @param texto Texto del botón.
     * @param color Color de fondo del botón.
     * @return Botón creado.
     */

    private Button crearBoton(String texto, String color) {
        Button btn = new Button(texto);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 6;");
        btn.setMinHeight(45);
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    /**
     * Aplica estilos CSS a los botones.
     */

    private void aplicarCssBotones() {
        btnCancelar.getStyleClass().addAll("btn-effect", "btn-danger");
        btnGuardar.getStyleClass().addAll("btn-effect", "btn-primary");
        btnAnadirPalabra.getStyleClass().addAll("btn-effect", "plus-button");
    }

    public Parent getView() {
        return view;
    }
}
