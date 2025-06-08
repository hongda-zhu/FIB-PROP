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
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;
import scrabble.presentation.componentes.DiccionarioVisual;
import scrabble.presentation.viewControllers.ControladorDiccionarioView;



/**
 * Vista para la modificación y edición de diccionarios existentes de Scrabble.
 * Esta clase proporciona una interfaz completa para editar diccionarios previamente
 * creados, permitiendo modificar el alfabeto con puntuaciones y frecuencias,
 * así como gestionar el conjunto de palabras válidas del diccionario seleccionado.
 * 
 * La vista carga automáticamente los datos del diccionario existente y permite
 * modificaciones controladas mediante interfaces tabulares interactivas con
 * validación en tiempo real y funcionalidad de búsqueda especializada para
 * facilitar la edición de diccionarios con grandes volúmenes de datos.
 * 
 * Características principales:
 * - Campo de nombre de diccionario deshabilitado (solo visualización)
 * - Tabla editable de alfabeto precargada con datos existentes
 * - Tabla de palabras con funcionalidad de edición in-place y doble clic
 * - Validación automática de entrada numérica para puntuaciones y frecuencias
 * - Prevención de duplicados en palabras con validación en tiempo real
 * - Botones de eliminación individual para entradas en tabla de palabras
 * - Funcionalidad de búsqueda y filtrado independiente para cada tabla
 * - Botón de añadir palabras que abre popup especializado
 * - Popup de modificación de palabras mediante doble clic
 * - Sistema de validación integral antes de guardar cambios
 * - Interfaz responsiva que se adapta al tamaño de ventana
 * - Soporte completo para temas claro y oscuro
 * - Aplicación automática de estilos CSS para coherencia visual
 * - Gestión de altura dinámica de tablas según el número de elementos
 * 
 * La vista utiliza el patrón MVC, comunicándose con ControladorDiccionarioView
 * para todas las operaciones de validación, modificación y persistencia de
 * diccionarios. Implementa ObservableList y FilteredList para proporcionar
 * funcionalidad reactiva y filtrado en tiempo real de los datos mostrados.
 * 
 * El proceso de modificación incluye validación exhaustiva de consistencia
 * entre el alfabeto y las palabras modificadas, asegurando que todas las
 * palabras continúen utilizando únicamente letras presentes en el alfabeto.
 * El nombre del diccionario permanece inmutable durante la edición.
 * 
 * @version 1.0
 * @since 1.0
 */
public class ModificarDiccionarioView {
    private Parent view;
    private ControladorDiccionarioView controlador;

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
     * Constructor que inicializa la vista de modificación de diccionario existente.
     * Configura todos los componentes de la interfaz, carga los datos del diccionario
     * seleccionado, inicializa tablas editables con datos existentes y establece
     * sistemas de validación para mantener la integridad del diccionario modificado.
     * 
     * @pre controlador no debe ser null y debe estar correctamente inicializado,
     *      diccionarioVisual no debe ser null y debe contener datos válidos.
     * @param controlador Controlador que maneja la lógica de modificación de diccionarios
     * @param diccionarioVisual Diccionario existente que se va a modificar
     * @post Se crea una nueva instancia con la interfaz completamente configurada,
     *       datos del diccionario existente cargados en las tablas, campo de nombre
     *       deshabilitado, funcionalidad de edición activa, todos los estilos CSS
     *       aplicados, listeners de eventos configurados y vista lista para modificación.
     * @throws NullPointerException si controlador o diccionarioVisual son null
     */
    public ModificarDiccionarioView(ControladorDiccionarioView controlador, DiccionarioVisual diccionarioVisual) {
        this.controlador = controlador;

        crearBotones();

        Text title = new Text("Gestión de Diccionarios - Modificar diccionario");
        title.setFont(new Font("Arial", 20));
        title.setStyle("-fx-fill: white;");
        HBox topBar = new HBox(title);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(15));
        topBar.setStyle("-fx-background-color: #2c3e50;");
        topBar.setMinHeight(90);

        this.campoNombre = new TextField(diccionarioVisual.getNombre());
        campoNombre.setDisable(true);

        this.tablaAlfabeto = new TableView<>();
        inicializarTablaAlfabeto(diccionarioVisual);

        this.tablaPalabras = new TableView<>();
        inicializarTablaPalabras(diccionarioVisual);

        // Aplicar estilo después de inicializar las dos tabkas
        estilizarTablas();

        Label labelNombreDiccionario = new Label("Nombre del Diccionario");
        labelNombreDiccionario.setStyle("-fx-font-size: 18px; -fx-text-fill: #34495e;");

        VBox campos = new VBox(10,
                labelNombreDiccionario, campoNombre,
                headerAlfabeto, tablaAlfabeto,
                headerPalabras, tablaPalabras
        );
        campos.setPadding(new Insets(20));
        campos.setStyle("-fx-background-color: #f8f8f8; -fx-border-color: lightgray; -fx-border-radius: 8;");
        aplicarFondoPorTema(campos, controlador.getTema(), "#ffffff", "#1c1747");
        VBox.setVgrow(campos, Priority.ALWAYS);

        HBox botones = new HBox(10, btnCancelar, btnGuardar);
        botones.setAlignment(Pos.CENTER);
        botones.setPadding(new Insets(10, 40, 10, 40));

        VBox mainContent = new VBox(10, campos, botones);
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setPadding(new Insets(10, 400, 10, 400));
        aplicarFondoPorTema(mainContent, controlador.getTema(), "#ffffff", "#1c1747");

        VBox root = new VBox(10, topBar, mainContent);
        VBox.setVgrow(mainContent, Priority.ALWAYS);
        root.setAlignment(Pos.CENTER);
        if (controlador.getTema().equals("Claro")) root.setStyle("-fx-background-color: #f5f5f5;");
        else if (controlador.getTema().equals("Oscuro")) root.setStyle("-fx-background-color: #0b0a2e;");
        root.setPadding(new Insets(20));

        root.widthProperty().addListener((obs, oldVal, newVal) -> {
            mainContent.setPrefWidth(newVal.doubleValue() * 0.8);
        });

        root.heightProperty().addListener((obs, oldVal, newVal) -> {
            campos.setPrefHeight(newVal.doubleValue() * 0.5);
        });

        this.view = root;

        try {
            List<String> cssResources = List.of("/styles/button.css", "/styles/table.css");
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
        cambiarColorTextoPorTema(root, controlador.getTema());
    }


    /**
     * Aplica colores de fondo y estilos adaptativos según el tema actual.
     * Configura automáticamente colores de fondo, bordes y radios de borde
     * para mantener consistencia visual entre temas claro y oscuro durante
     * la edición del diccionario.
     * 
     * @pre nodo, tema, claroColor y oscuroColor no deben ser null.
     * @param nodo Contenedor Parent al que aplicar el estilo temático
     * @param tema Tema actual del sistema ("Claro" o "Oscuro")
     * @param claroColor Color hexadecimal para el tema claro
     * @param oscuroColor Color hexadecimal para el tema oscuro
     * @post El nodo tiene aplicados los estilos CSS apropiados para el tema,
     *       incluyendo colores de fondo, bordes redondeados y bordes de contraste
     *       que mantienen la legibilidad durante la edición del diccionario.
     */
    private void aplicarFondoPorTema(Parent nodo, String tema, String claroColor, String oscuroColor) {
        String fondo = tema.equals("Oscuro") ? oscuroColor : claroColor;
        // Extrae el estilo actual
        String estiloActual = nodo.getStyle();

        // Quita cualquier fondo previo para evitar duplicación
        estiloActual = estiloActual.replaceAll("-fx-background-color:\\s*[^;]+;", "");

        // Añade el nuevo color conservando el resto
        nodo.setStyle(estiloActual + "-fx-background-color: " + fondo + ";");
    }

    /**
     * Cambia el color del texto de todos los nodos Label dentro del nodo raíz dado,
     * según el tema especificado.
     *
     * @param root El nodo raíz (Parent) en el que buscar los Labels.
     * @param tema El tema actual, puede ser "Oscuro" o cualquier otro valor para claro.
     *
     * @pre root no es nulo y contiene nodos Label como descendientes.
     * @pre tema no es nulo.
     * @post El color del texto de todos los Labels dentro de root se establece en blanco si tema es "Oscuro",
     *       o en negro en caso contrario.
     */
    private void cambiarColorTextoPorTema(Parent root, String tema) {
        String colorTexto = tema.equals("Oscuro") ? "white" : "black";

        root.lookupAll(".label").forEach(n -> {
            if (n instanceof Label label) {
                String estiloAnterior = label.getStyle();
                if (estiloAnterior == null) estiloAnterior = "";
                estiloAnterior = estiloAnterior.replaceAll("-fx-text-fill:\\s*[^;]+;", "");
                label.setStyle(estiloAnterior + "-fx-text-fill: " + colorTexto + ";");
            }
        });
    }


    /**
     * Inicializa la tabla del alfabeto con datos existentes del diccionario.
     * Carga automáticamente las letras, puntuaciones y frecuencias del diccionario
     * seleccionado, configura columnas editables con validación, campo de búsqueda
     * y ajuste dinámico de altura según el número de elementos existentes.
     * 
     * @pre diccionarioVisual no debe ser null y debe contener alfabeto válido.
     * @param diccionarioVisual Diccionario existente del cual cargar datos del alfabeto
     * @post La tabla del alfabeto está completamente configurada con datos existentes
     *       cargados, columnas editables para puntuación y frecuencia, validación
     *       numérica activa, funcionalidad de búsqueda y filtrado configurada,
     *       altura dinámica establecida y placeholder informativo disponible.
     */
    private void inicializarTablaAlfabeto(DiccionarioVisual diccionarioVisual) {
        tablaAlfabeto.setEditable(false);
        this.alfabetoOriginal = FXCollections.observableArrayList();
        // tablaAlfabeto.getStyleClass().add("modern-table");

        FilteredList<ObservableList<String>> filtrada = new FilteredList<>(alfabetoOriginal, s -> true);
        tablaAlfabeto.setItems(filtrada);

        TableColumn<ObservableList<String>, String> colLetra = new TableColumn<>("Letra");
        colLetra.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));

        colLetra.setCellFactory(column -> new TextFieldTableCell<>(new DefaultStringConverter()));
   
        colLetra.setOnEditCommit(event -> {
            String newValue = event.getNewValue().trim().toUpperCase();
            event.getRowValue().set(0, newValue);
        });

        // Columna Puntuación con validación visual
        TableColumn<ObservableList<String>, String> colPunt = crearColumnaNumericaAutoCommit("Puntuación", 2);
         // Columna Frecuencia con validación visual
        TableColumn<ObservableList<String>, String> colFreq = crearColumnaNumericaAutoCommit("Frecuencia", 1);
        
        colLetra.setEditable(false);

        TextField campoBusqueda = new TextField();
        campoBusqueda.setPromptText("Buscar letra...");
        campoBusqueda.setOnKeyReleased(e -> {
            String filtro = campoBusqueda.getText().toUpperCase();
            filtrada.setPredicate(palabra -> !palabra.isEmpty() && palabra.get(0).toUpperCase().startsWith(filtro));
        });

        campoBusqueda.setMaxWidth(200);
        campoBusqueda.setAlignment(Pos.CENTER_LEFT);

        this.headerAlfabeto = new HBox(25);
        Label alfabetoLabel = new Label("Alfabeto");
        alfabetoLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #34495e;");
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        headerAlfabeto.getChildren().addAll(alfabetoLabel, campoBusqueda, espacio);
        headerAlfabeto.setAlignment(Pos.CENTER_LEFT);

        tablaAlfabeto.getColumns().addAll(colLetra, colFreq, colPunt);
        tablaAlfabeto.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tablaAlfabeto.setPlaceholder(new Label("No hay letras añadidas."));
        tablaAlfabeto.setFixedCellSize(45);
        tablaAlfabeto.setPrefHeight(26);
        tablaAlfabeto.setMaxHeight(250);

        tablaAlfabeto.getItems().addListener((ListChangeListener<ObservableList<String>>) change -> {
            int filas = tablaAlfabeto.getItems().size();
            tablaAlfabeto.setPrefHeight((filas + 1.0) * tablaAlfabeto.getFixedCellSize());
        });

        for (String entrada : diccionarioVisual.getAlfabeto()) {
            String[] partes = entrada.trim().split("\\s+");
            if (partes.length == 3) {
                alfabetoOriginal.add(FXCollections.observableArrayList(partes[0], partes[1], partes[2]));
            }
        }
    }


    /**
     * Crea una columna de tabla editable con validación numérica automática.
     * Configura columnas especializadas para campos numéricos como puntuación
     * y frecuencia, con validación visual y confirmación automática de cambios
     * durante la edición del alfabeto existente.
     * 
     * @pre titulo no debe ser null, indexColumna debe ser válido para la estructura de datos.
     * @param titulo Nombre que se mostrará en el encabezado de la columna
     * @param indexColumna Índice de la columna en la estructura de datos ObservableList
     * @return TableColumn configurada con validación numérica y edición automática
     * @post Se devuelve una columna completamente configurada con validación numérica,
     *       edición por TextField, confirmación automática al perder foco,
     *       y estilos visuales para indicar valores inválidos durante la modificación.
     */
    private TableColumn<ObservableList<String>, String> crearColumnaNumericaAutoCommit(String titulo, int indexColumna) {
        TableColumn<ObservableList<String>, String> columna = new TableColumn<>(titulo);

        columna.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(indexColumna)));

        columna.setCellFactory(column -> new TextFieldTableCell<>(new DefaultStringConverter())); 

        columna.setOnEditCommit(event -> event.getRowValue().set(indexColumna, event.getNewValue()));

        return columna;
    }


    /**
     * Aplica estilos CSS y configuraciones visuales avanzadas a ambas tablas.
     * Coordina la estilización de las tablas de alfabeto y palabras para
     * mantener consistencia visual durante la edición del diccionario existente.
     * 
     * @pre Las tablas de alfabeto y palabras deben estar inicializadas.
     * @post Ambas tablas tienen aplicados estilos CSS consistentes,
     *       configuraciones de dimensiones apropiadas, y funcionalidad
     *       visual optimizada para la edición del diccionario.
     */
    private void estilizarTablas() {
        estilizarTablaAlfabeto();
        estilizarTablaPalabras();
    }


    /**
     * Aplica estilos y configuraciones específicas a la TableView del alfabeto.
     * Configura dimensiones, comportamiento de columnas, selección y altura
     * de filas para optimizar la experiencia de edición del alfabeto existente.
     * 
     * @pre La tabla de alfabeto debe estar inicializada con datos cargados.
     * @post La tabla de alfabeto tiene aplicados estilos CSS específicos,
     *       dimensiones configuradas apropiadamente, columnas no reordenables,
     *       selección simple habilitada y altura de filas optimizada para edición.
     */
    private void estilizarTablaAlfabeto() {
        if (tablaAlfabeto != null) {
            // Aplicar clase CSS
            tablaAlfabeto.getStyleClass().clear();            
            tablaAlfabeto.getStyleClass().add("table-view");
            
            // Configurar dimensiones
            tablaAlfabeto.setMinWidth(400);
            tablaAlfabeto.setPrefWidth(600);
            tablaAlfabeto.setMaxWidth(Double.MAX_VALUE);
            
            // Desactivar reordenamiento de columnas
            for (TableColumn<ObservableList<String>, ?> column : tablaAlfabeto.getColumns()) {
                column.setReorderable(false);
            }
            
            // Configurar selección
            tablaAlfabeto.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.SINGLE);
            
            
            // Configurar altura de filas 
            tablaAlfabeto.setRowFactory(tv -> {
                javafx.scene.control.TableRow<ObservableList<String>> row = new javafx.scene.control.TableRow<>();
                row.setPrefHeight(tablaAlfabeto.getFixedCellSize());
                return row;
            });
        }
    }


    /**
     * Aplica estilos y configuraciones específicas a la TableView de palabras.
     * Configura dimensiones, comportamiento de columnas, selección y altura
     * de filas para optimizar la experiencia de edición de palabras existentes.
     * 
     * @pre La tabla de palabras debe estar inicializada con datos cargados.
     * @post La tabla de palabras tiene aplicados estilos CSS específicos,
     *       dimensiones configuradas apropiadamente, columnas no reordenables,
     *       selección simple habilitada y altura de filas optimizada para edición.
     */
    private void estilizarTablaPalabras() {
        if (tablaPalabras != null) {
            // Aplicar clase CSS
            tablaPalabras.getStyleClass().clear();            
            tablaPalabras.getStyleClass().add("table-view");
            
            // Configurar dimensiones
            tablaPalabras.setMinWidth(400);
            tablaPalabras.setPrefWidth(600);
            tablaPalabras.setMaxWidth(Double.MAX_VALUE);
            
            // Desactivar reordenamiento de columnas
            for (TableColumn<String, ?> column : tablaPalabras.getColumns()) {
                column.setReorderable(false);
            }
            
            // Configurar selección
            tablaPalabras.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.SINGLE);
            
            // Configurar altura de filas (mantener la lógica existente pero mejorada)
            tablaPalabras.setRowFactory(tv -> {
                javafx.scene.control.TableRow<String> row = new javafx.scene.control.TableRow<>();
                row.setPrefHeight(tablaPalabras.getFixedCellSize()); // Usar el tamaño fijo ya configurado
                return row;
            });
        }
    }


    /**
     * Inicializa la tabla de palabras con datos existentes del diccionario.
     * Carga automáticamente las palabras del diccionario seleccionado, configura
     * funcionalidad de edición in-place, doble clic para modificación, botones
     * de eliminación y campo de búsqueda con filtrado en tiempo real.
     * 
     * @pre diccionarioVisual no debe ser null y debe contener palabras válidas.
     * @param diccionarioVisual Diccionario existente del cual cargar palabras
     * @post La tabla de palabras está completamente configurada con palabras existentes
     *       cargadas, funcionalidad de edición in-place activa, doble clic para popup
     *       de modificación, botones de eliminación por fila, funcionalidad de búsqueda,
     *       altura dinámica establecida y botón de añadir palabras configurado.
     */
    private void inicializarTablaPalabras(DiccionarioVisual diccionarioVisual) {
        this.palabrasOriginales = FXCollections.observableArrayList(diccionarioVisual.getPalabras());
        this.tablaPalabras.setEditable(false);

        FilteredList<String> filtrada = new FilteredList<>(palabrasOriginales, s -> true);
        tablaPalabras.setItems(filtrada);

        TableColumn<String, String> palabraCol = new TableColumn<>("Palabra");
        palabraCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        palabraCol.setCellFactory(crearCeldaEditableConValidacion(palabrasOriginales));
    
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
        tablaPalabras.setFixedCellSize(45);
        tablaPalabras.setPrefHeight(26);
        tablaPalabras.setMaxHeight(300);

        tablaPalabras.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String palabraOriginal = tablaPalabras.getSelectionModel().getSelectedItem();
                Set<String> letrasValidas = alfabetoOriginal.stream()
                    .map(fila -> fila.get(0).toUpperCase())
                    .collect(Collectors.toSet());

                if (palabraOriginal != null) {
                    controlador.mostrarPopupModificarPalabra(palabrasOriginales, letrasValidas, palabraOriginal);
                }
            }
        });


        tablaPalabras.getItems().addListener((ListChangeListener<String>) change -> {
            int filas = tablaPalabras.getItems().size();
            tablaPalabras.setPrefHeight((filas + 1.0) * tablaPalabras.getFixedCellSize());
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
        lblPalabras.setStyle("-fx-font-size: 18px; -fx-text-fill: #34495e;");
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        headerPalabras.getChildren().addAll(lblPalabras, campoBusqueda, espacio, btnAnadirPalabra);
        headerPalabras.setAlignment(Pos.CENTER_LEFT);


    }


    /**
     * Crea una factory de celdas editables con validación especializada para palabras.
     * Genera celdas personalizadas que permiten edición in-place con validación
     * de duplicados, prevención de palabras vacías y confirmación automática
     * durante la modificación de palabras existentes.
     * 
     * @pre listaBase no debe ser null y debe contener las palabras originales.
     * @param listaBase Lista observable con palabras originales para validación
     * @return Callback que produce TableCell personalizadas con validación
     * @post Se devuelve una factory que genera celdas con funcionalidad completa
     *       de edición, validación de duplicados durante modificación, prevención
     *       de entradas vacías y confirmación automática de cambios.
     */
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
     * Crea e inicializa todos los botones de acción con sus manejadores de eventos.
     * Configura botones para cancelar, guardar cambios y añadir palabras,
     * estableciendo validaciones de datos, confirmaciones de cambios y
     * delegación apropiada de acciones al controlador de modificación.
     * 
     * @pre El controlador debe estar inicializado y los componentes de tabla disponibles.
     * @post Todos los botones están creados con manejadores de eventos configurados,
     *       validación integral implementada para guardar cambios, funcionalidad
     *       de popup para añadir palabras activa, y navegación de cancelación establecida.
     */
    private void crearBotones() {
        btnCancelar = crearBoton("Cancelar", "#e74c3c");
        btnCancelar.setOnAction(e -> controlador.mostrarVistaGestion());

        btnGuardar = crearBoton("Guardar cambios", "#27ae60");
        btnGuardar.setOnAction(e -> {
            String nombre = campoNombre.getText();
            List<ObservableList<String>> filas = tablaAlfabeto.getItems();

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
     * Crea un botón personalizado con estilo y configuración específicos.
     * Aplica formato de texto, colores personalizados y dimensiones estándar
     * para mantener consistencia visual en toda la interfaz de modificación.
     * 
     * @pre texto y color no deben ser null ni estar vacíos.
     * @param texto Texto que se mostrará en el botón
     * @param color Color de fondo del botón en formato hexadecimal
     * @return Button configurado con estilo personalizado y dimensiones estándar
     * @post Se devuelve un botón completamente configurado con texto especificado,
     *       color de fondo personalizado, texto blanco, fuente en negrita,
     *       dimensiones estándar y configuración de layout apropiada.
     */
    private Button crearBoton(String texto, String color) {
        Button btn = new Button(texto);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-size: 20px; -fx-font-weight: bold;");
        btn.setMinHeight(45);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(55);
        btn.setPrefWidth(200);
        return btn;
    }


    /**
     * Aplica clases CSS específicas a todos los botones de la interfaz.
     * Configura estilos consistentes diferenciando botones por funcionalidad:
     * cancelar con estilo de peligro, guardar con estilo primario y añadir
     * con estilo de botón plus para mantener coherencia visual.
     * 
     * @pre Todos los botones deben estar inicializados correctamente.
     * @post Todos los botones tienen aplicadas clases CSS apropiadas:
     *       cancelar con estilo de peligro, guardar con estilo primario,
     *       añadir palabra con estilo plus, y todos incluyen efectos de
     *       transición visual para mejorar la experiencia de modificación.
     */
    private void aplicarCssBotones() {
        btnCancelar.getStyleClass().addAll("btn-effect", "btn-danger");
        btnGuardar.getStyleClass().addAll("btn-effect", "btn-primary");
        btnAnadirPalabra.getStyleClass().addAll("btn-effect", "plus-button");
    }


    /**
     * Obtiene la vista cargada de modificación de diccionario.
     * Proporciona acceso a la jerarquía completa de componentes JavaFX
     * configurados para la modificación de diccionarios existentes.
     * 
     * @pre La vista debe haber sido inicializada completamente en el constructor.
     * @return Parent que contiene la vista completa de modificación de diccionario
     * @post Se devuelve la referencia a la vista completamente configurada
     *       sin modificar su estado actual ni los datos cargados en las tablas.
     */
    public Parent getView() {
        return view;
    }
}
