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
import scrabble.presentation.viewControllers.ControladorDiccionarioView;

/**
 * Vista para la creación y configuración de nuevos diccionarios de Scrabble.
 * Esta clase proporciona una interfaz completa para que el usuario pueda crear
 * diccionarios personalizados, definiendo el alfabeto con puntuaciones y frecuencias,
 * así como el conjunto de palabras válidas que formarán parte del diccionario.
 * 
 * La vista permite la gestión integral de diccionarios mediante interfaces tabulares
 * interactivas que facilitan la entrada, edición y validación de datos en tiempo real.
 * Proporciona herramientas especializadas para la configuración detallada de cada
 * componente del diccionario, asegurando la consistencia y validez de los datos.
 * 
 * Características principales:
 * - Campo de entrada para nombre del diccionario con validación de formato
 * - Tabla editable de alfabeto con columnas para letra, puntuación y frecuencia
 * - Tabla de palabras con funcionalidad de búsqueda y filtrado en tiempo real
 * - Validación automática de entrada numérica para puntuaciones y frecuencias
 * - Prevención de duplicados tanto en letras del alfabeto como en palabras
 * - Botones de eliminación individual para entradas en ambas tablas
 * - Funcionalidad de búsqueda y filtrado independiente para cada tabla
 * - Botones de añadir elementos que abren popups especializados
 * - Sistema de validación integral antes de la creación del diccionario
 * - Interfaz responsiva que se adapta al tamaño de ventana
 * - Aplicación automática de estilos CSS para coherencia visual
 * - Gestión de altura dinámica de tablas según el número de elementos
 * 
 * La vista utiliza el patrón MVC, comunicándose con ControladorDiccionarioView
 * para todas las operaciones de validación, creación y gestión de diccionarios.
 * Implementa ObservableList y FilteredList para proporcionar funcionalidad
 * reactiva y filtrado en tiempo real de los datos mostrados.
 * 
 * El proceso de creación incluye validación exhaustiva de consistencia entre
 * el alfabeto definido y las palabras ingresadas, asegurando que todas las
 * palabras utilicen únicamente letras presentes en el alfabeto configurado.
 * 
 * @version 1.0
 * @since 1.0
 */

public class CrearDiccionarioView {
    private Parent view;

    private ControladorDiccionarioView controlador;
    private Button btnCancelar;
    private Button btnConfirmar;
    private Button btnAñadirLetra;
    private Button btnAnadirPalabra;
    private HBox headerPalabras;
    private HBox headerAlfabeto;

    private TextField nombreDiccionario;
    private TableView<ObservableList<String>> alfabeto;
    private TableView<String> tablaPalabras;
    private ObservableList<String> palabrasOriginales;
    private ObservableList<ObservableList<String>> alfabetoOriginal;


    /**
     * Constructor que inicializa la vista de creación de diccionario.
     * Configura todos los componentes de la interfaz, incluyendo tablas editables,
     * campos de entrada, botones de acción y sistemas de validación, creando
     * una experiencia completa para la configuración de diccionarios personalizados.
     * 
     * @pre controlador no debe ser null y debe estar correctamente inicializado
     *      con acceso a funcionalidades de gestión de diccionarios.
     * @param controlador Controlador que maneja la lógica de creación y validación de diccionarios
     * @post Se crea una nueva instancia con la interfaz completamente configurada,
     *       tablas inicializadas con funcionalidad de edición y filtrado,
     *       todos los estilos CSS aplicados, listeners de eventos configurados
     *       y la vista lista para la creación de diccionarios.
     * @throws NullPointerException si controlador es null
     */

    public CrearDiccionarioView(ControladorDiccionarioView controlador) {
        this.controlador = controlador;

        // Crear botones
        CrearBotones();

        Text title = new Text("Gestión de Diccionarios - Crear nuevo diccionario");
        title.setFont(new Font("Arial", 20));
        title.setStyle("-fx-fill: white;");
        HBox topBar = new HBox(title);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(15));
        topBar.setStyle("-fx-background-color: #2c3e50;");
        topBar.setMinHeight(90);

        this.nombreDiccionario = new TextField();
        nombreDiccionario.setPromptText("Ej: MiNuevoDiccionario");

        this.alfabeto = new TableView<>();
        inicializarTablaAlfabeto();

        this.tablaPalabras = new TableView<>();
        inicializarTablaPalabras();

        Label labelNombreDiccionario = new Label("Nombre del Diccionario");
        labelNombreDiccionario.setStyle("-fx-font-size: 18px; -fx-text-fill: #34495e;");

        VBox campos = new VBox(10,
                labelNombreDiccionario, nombreDiccionario,
                headerAlfabeto, alfabeto,
                headerPalabras, tablaPalabras
        );
        campos.setPadding(new Insets(20));
        campos.setStyle("-fx-background-color: #f8f8f8; -fx-border-color: lightgray; -fx-border-radius: 8;");
        aplicarFondoPorTema(campos, controlador.getTema(), "#ffffff", "#1c1747");
        VBox.setVgrow(campos, Priority.ALWAYS);

        HBox botones = new HBox(10, btnCancelar, btnConfirmar);
        botones.setAlignment(Pos.CENTER);
        botones.setPadding(new Insets(10, 40, 10, 10));

        VBox mainContent = new VBox(10, campos, botones);
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setPadding(new Insets(20, 400, 20, 400));
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

        estilizarTablas();

        //Cargar css
        try {
            List<String> cssResources = List.of(
                "/styles/table.css",
                "/styles/button.css"
            );

            for (String cssResource : cssResources) {
                URL cssUrl = getClass().getResource(cssResource);
                if (cssUrl != null) {
                    view.getStylesheets().add(cssUrl.toExternalForm());
                    System.out.println("CSS cargado en CrearDiccionarioView: " + cssResource);
                } else {
                    System.err.println("No se pudo encontrar el recurso CSS: " + cssResource);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar CSS en CrearDiccionarioView: " + e.getMessage());
        }

        aplicarcssBotones();
        cambiarColorTextoPorTema(root, controlador.getTema());
    }

    /**
     * Aplica el color de fondo a un contenedor según el tema actual.
     *
     * @param nodo Nodo al que aplicar el estilo.
     * @param tema Tema actual ("Claro" o "Oscuro").
     * @param claroColor Color hexadecimal para el tema claro.
     * @param oscuroColor Color hexadecimal para el tema oscuro.
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
     * Crea e inicializa todos los botones de la interfaz con sus respectivos manejadores.
     * Configura los botones principales de navegación y acción, incluyendo validación
     * completa de datos antes de la creación del diccionario y manejo de popups
     * para añadir elementos al alfabeto y palabras.
     * 
     * @pre El controlador debe estar inicializado y los componentes de interfaz listos.
     * @post Todos los botones están creados con estilos base, manejadores de eventos
     *       configurados, validación integral implementada para la creación de diccionarios,
     *       y funcionalidad de popup lista para añadir elementos a las tablas.
     */
    private void CrearBotones() {
        btnCancelar = crearBoton("Cancelar", "#e74c3c");
        btnCancelar.setOnAction(e -> controlador.mostrarVistaGestion());

        btnConfirmar = crearBoton("Crear Diccionario", "#27ae60");
        btnConfirmar.setOnAction(e -> {
            // Lógica para crear el diccionario
            String nombre = nombreDiccionario.getText();
            if (nombre.isBlank()) {
                nombreDiccionario.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-background-color: #ffe6e6;");
            }
            else {
                nombreDiccionario.setStyle("");
            }
            List<ObservableList<String>> filas = alfabeto.getItems();

            List<String> palabrasList = palabrasOriginales.stream()
                    .filter(palabra -> !palabra.isBlank())
                    .map(String::toUpperCase)
                    .distinct()
                    .toList();

            Set<String> letrasValidas = alfabetoOriginal.stream()
                    .map(fila -> fila.get(0).toUpperCase())
                    .collect(Collectors.toSet());
            List<String> alfabetoList = alfabetoOriginal.stream()
                    .map(item -> item.get(0).toUpperCase() + " " +  item.get(1) + " " +   item.get(2))
                    .toList();        
            if (controlador.checkDiccioanario(nombre, palabrasList, letrasValidas)) {
                controlador.agregarDiccionario(nombre, alfabetoList, palabrasList);
                controlador.mostrarVistaGestion();
            } 
            
        });

        btnAñadirLetra = new Button();
        btnAñadirLetra.setText("+"); // símbolo más
        btnAñadirLetra.setOnAction(e -> {
            controlador.mostrarPopupAñadirLetra(alfabetoOriginal);
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
     * Aplica formato de fuente, colores personalizados y dimensiones apropiadas
     * para mantener consistencia visual en toda la interfaz de creación.
     * 
     * @pre texto y color no deben ser null.
     * @param texto Texto que se mostrará en el botón
     * @param color Color de fondo del botón (formato hexadecimal)
     * @return Button configurado con estilo personalizado y dimensiones apropiadas
     * @post Se devuelve un botón completamente configurado con estilo personalizado,
     *       fuente en negrita, color de fondo especificado, texto blanco,
     *       bordes redondeados y dimensiones apropiadas para la interfaz.
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
     * Aplica las clases CSS específicas a todos los botones de la interfaz.
     * Configura estilos consistentes para botones de acción, navegación
     * y elementos de añadir, asegurando coherencia visual y efectos apropiados.
     * 
     * @pre Todos los botones deben estar inicializados.
     * @post Todos los botones tienen aplicadas sus clases CSS correspondientes:
     *       botones de cancelar con estilo de peligro, confirmar con estilo primario,
     *       y botones de añadir con estilo de botón plus, incluyendo efectos visuales.
     */
    private void aplicarcssBotones() {
        btnCancelar.getStyleClass().addAll("btn-effect", "btn-danger");
        btnConfirmar.getStyleClass().addAll("btn-effect", "btn-primary");
        btnAñadirLetra.getStyleClass().addAll("btn-effect", "plus-button");
        btnAnadirPalabra.getStyleClass().addAll("btn-effect", "plus-button");
    }


    /**
     * Aplica estilos y configuraciones visuales adicionales a ambas TableView.
     * Coordina la estilización de las tablas de alfabeto y palabras para
     * mantener consistencia visual y funcionalidad apropiada en toda la interfaz.
     * 
     * @pre Las tablas de alfabeto y palabras deben estar inicializadas.
     * @post Ambas tablas tienen aplicados estilos CSS consistentes,
     *       configuraciones de dimensiones apropiadas, y funcionalidad
     *       visual optimizada para la experiencia del usuario.
     */
    private void estilizarTablas() {
        estilizarTablaAlfabeto();
        estilizarTablaPalabras();
    }


    /**
     * Aplica estilos y configuraciones específicas a la TableView del alfabeto.
     * Configura dimensiones, comportamiento de columnas, selección y altura
     * de filas para optimizar la experiencia de edición del alfabeto.
     * 
     * @pre La tabla de alfabeto debe estar inicializada.
     * @post La tabla de alfabeto tiene aplicados estilos CSS específicos,
     *       dimensiones configuradas apropiadamente, columnas no reordenables,
     *       selección simple habilitada y altura de filas optimizada.
     */
    private void estilizarTablaAlfabeto() {
        if (alfabeto != null) {
            // Aplicar clase CSS
            alfabeto.getStyleClass().clear();            
            alfabeto.getStyleClass().add("table-view");
            
            // Configurar dimensiones
            alfabeto.setMinWidth(400);
            alfabeto.setPrefWidth(600);
            alfabeto.setMaxWidth(Double.MAX_VALUE);
            
            // Desactivar reordenamiento de columnas
            for (TableColumn<ObservableList<String>, ?> column : alfabeto.getColumns()) {
                column.setReorderable(false);
            }
            
            // Configurar selección
            alfabeto.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.SINGLE);
            
            // Configurar altura de filas 
            alfabeto.setRowFactory(tv -> {
                javafx.scene.control.TableRow<ObservableList<String>> row = new javafx.scene.control.TableRow<>();
                row.setPrefHeight(alfabeto.getFixedCellSize()); // Usar el tamaño fijo ya configurado
                return row;
            });
        }
    }


    /**
     * Aplica estilos y configuraciones específicas a la TableView de palabras.
     * Configura dimensiones, comportamiento de columnas, selección y altura
     * de filas para optimizar la experiencia de gestión de palabras del diccionario.
     * 
     * @pre La tabla de palabras debe estar inicializada.
     * @post La tabla de palabras tiene aplicados estilos CSS específicos,
     *       dimensiones configuradas apropiadamente, columnas no reordenables,
     *       selección simple habilitada y altura de filas optimizada.
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
            
            // Configurar altura de filas 
            tablaPalabras.setRowFactory(tv -> {
                javafx.scene.control.TableRow<String> row = new javafx.scene.control.TableRow<>();
                row.setPrefHeight(tablaPalabras.getFixedCellSize()); // Usar el tamaño fijo ya configurado
                return row;
            });
        }
    }


    /**
     * Inicializa la tabla del alfabeto con columnas editables y funcionalidad completa.
     * Configura columnas para letra, puntuación y frecuencia con validación,
     * botones de eliminación, campo de búsqueda y ajuste dinámico de altura
     * según el número de elementos en la tabla.
     * 
     * @pre alfabeto TableView debe estar inicializada y el controlador disponible.
     * @post La tabla del alfabeto está completamente configurada con columnas
     *       editables para letra, puntuación y frecuencia, validación numérica,
     *       funcionalidad de búsqueda y filtrado, botones de eliminación por fila,
     *       altura dinámica y placeholder informativo cuando esté vacía.
     */
    private void inicializarTablaAlfabeto() {
        alfabeto.setEditable(false);
        this.alfabetoOriginal = FXCollections.observableArrayList();

        FilteredList<ObservableList<String>> filtrada = new FilteredList<>(alfabetoOriginal, s -> true);
        alfabeto.setItems(filtrada);

        // Columna Letra
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
        // Columna de eliminar
        TableColumn<ObservableList<String>, Void> colEliminar = new TableColumn<>("");
        colEliminar.setPrefWidth(40); // tamaño fijo para el botón

        colEliminar.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("x");

            {
                btn.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-cursor: hand; -fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                btn.setOnAction(e -> {
                    int index = getIndex();
                    if (index >= 0 && index < filtrada.size()) {
                        ObservableList<String> item = filtrada.get(index);
                        alfabetoOriginal.remove(item); // elimina de la lista original
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });


        colLetra.setEditable(false);
        colPunt.setEditable(false);
        colFreq.setEditable(false);

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
        headerAlfabeto.getChildren().addAll(alfabetoLabel, campoBusqueda, espacio, btnAñadirLetra);
        headerAlfabeto.setAlignment(Pos.CENTER_LEFT);

        alfabeto.getColumns().addAll(colLetra, colFreq, colPunt, colEliminar);
        alfabeto.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        alfabeto.setPlaceholder(new Label("No hay letras añadidas."));
        // alfabeto.getStyleClass().add("excel-table");

        alfabeto.setFixedCellSize(45);
        alfabeto.setPrefHeight(26); // Inicialmente solo 1 fila visible
        alfabeto.setMaxHeight(250);

        // Ajustar la altura dinámicamente al número de filas
        alfabeto.getItems().addListener((ListChangeListener<ObservableList<String>>) change -> {
            int filas = alfabeto.getItems().size();
            alfabeto.setPrefHeight((filas + 1.0) * alfabeto.getFixedCellSize());
        });


    }


    /**
     * Crea una columna de tabla editable con validación numérica automática.
     * Configura columnas especializadas para campos numéricos como puntuación
     * y frecuencia, con validación visual y confirmación automática de cambios.
     * 
     * @pre titulo no debe ser null, indexColumna debe ser válido para la estructura de datos.
     * @param titulo Nombre que se mostrará en el encabezado de la columna
     * @param indexColumna Índice de la columna en la estructura de datos ObservableList
     * @return TableColumn configurada con validación numérica y edición automática
     * @post Se devuelve una columna completamente configurada con validación numérica,
     *       edición por TextField, confirmación automática al perder foco,
     *       y estilos visuales para indicar valores inválidos o negativos.
     */
    private TableColumn<ObservableList<String>, String> crearColumnaNumericaAutoCommit(String titulo, int indexColumna) {
        TableColumn<ObservableList<String>, String> columna = new TableColumn<>(titulo);

        columna.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(indexColumna)));

        columna.setCellFactory(column -> new TextFieldTableCell<>(new DefaultStringConverter())); 

        columna.setOnEditCommit(event -> event.getRowValue().set(indexColumna, event.getNewValue()));

        return columna;
    }


    /**
     * Inicializa la tabla de palabras con funcionalidad de edición y búsqueda.
     * Configura la columna de palabras con validación de duplicados,
     * botones de eliminación, campo de búsqueda con filtrado en tiempo real
     * y ajuste dinámico de altura según el número de palabras.
     * 
     * @pre tablaPalabras TableView debe estar inicializada.
     * @post La tabla de palabras está completamente configurada con columna
     *       editable para palabras, validación de duplicados, funcionalidad
     *       de búsqueda y filtrado en tiempo real, botones de eliminación,
     *       altura dinámica y placeholder informativo cuando esté vacía.
     */
    private void inicializarTablaPalabras() {
        this.palabrasOriginales = FXCollections.observableArrayList();
        this.tablaPalabras.setEditable(false);
        // this.tablaPalabras.getStyleClass().add("excel-table");

        FilteredList<String> filtrada = new FilteredList<>(palabrasOriginales, s -> true);
        tablaPalabras.setItems(filtrada);

        TableColumn<String, String> palabraCol = new TableColumn<>("Palabra");
        palabraCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        palabraCol.setCellFactory(crearCeldaEditableConValidacion(palabrasOriginales));
        palabraCol.setEditable(false);

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
        tablaPalabras.setPrefHeight(26); // Inicialmente solo 1 fila visible
        tablaPalabras.setMaxHeight(300);

        // Ajustar la altura dinámicamente al número de filas
        tablaPalabras.getItems().addListener((ListChangeListener<String>) change -> {
            int filas = tablaPalabras.getItems().size();
            tablaPalabras.setPrefHeight((filas + 1.0) * tablaPalabras.getFixedCellSize());
        });

        

        
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
     * al perder el foco del campo de edición.
     * 
     * @pre listaBase no debe ser null.
     * @param listaBase Lista observable que contiene las palabras originales para validación
     * @return Callback que produce TableCell personalizadas con validación de palabras
     * @post Se devuelve una factory que genera celdas con funcionalidad completa
     *       de edición, validación de duplicados, prevención de entradas vacías,
     *       confirmación automática de cambios y cancelación en casos de error.
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
     * Obtiene la vista cargada de creación de diccionario.
     * Proporciona acceso a la jerarquía completa de componentes JavaFX
     * configurados para la creación y gestión de diccionarios personalizados.
     * 
     * @pre La vista debe haber sido inicializada completamente en el constructor.
     * @return Parent que contiene la vista completa de creación de diccionario
     * @post Se devuelve la referencia a la vista completamente configurada
     *       sin modificar su estado actual ni los datos en las tablas.
     */
    public Parent getView() {
        return view;
    }
}
