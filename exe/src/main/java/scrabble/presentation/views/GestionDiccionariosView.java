package scrabble.presentation.views;

import java.net.URL;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import scrabble.presentation.componentes.DiccionarioVisual;
import scrabble.presentation.viewControllers.ControladorDiccionarioView;

/**
 * Vista principal para la gestión integral de diccionarios de Scrabble.
 * Esta clase proporciona una interfaz completa para administrar diccionarios
 * existentes, permitiendo operaciones de creación, modificación, eliminación,
 * importación y visualización de diccionarios personalizados del sistema.
 * 
 * La vista presenta una tabla centralizada con todos los diccionarios disponibles
 * y un panel lateral con botones de acción para realizar operaciones específicas.
 * Incluye funcionalidad de doble clic para vista detallada y manejo inteligente
 * del estado de botones según la selección actual del usuario.
 * 
 * Características principales:
 * - Tabla central con lista completa de diccionarios disponibles
 * - Panel lateral con botones de acción organizados por funcionalidad
 * - Funcionalidad de doble clic para vista detallada de diccionarios
 * - Habilitación/deshabilitación automática de botones según selección
 * - Soporte completo para temas claro y oscuro con estilos adaptativos
 * - Interfaz responsiva que se adapta al tamaño de ventana
 * - Integración con popups especializados para operaciones complejas
 * - Validación de selección antes de ejecutar operaciones destructivas
 * - Mensajes informativos y de confirmación para todas las acciones
 * - Aplicación automática de estilos CSS para coherencia visual
 * - Gestión de altura dinámica de tabla según contenido
 * - Placeholder informativo cuando no hay diccionarios disponibles
 * 
 * La vista utiliza el patrón MVC, comunicándose con ControladorDiccionarioView
 * para todas las operaciones de gestión de diccionarios. Implementa listeners
 * reactivos que actualizan automáticamente el estado de la interfaz según
 * las acciones del usuario y cambios en los datos subyacentes.
 * 
 * El diseño de la interfaz enfatiza la usabilidad mediante la organización
 * lógica de funciones y retroalimentación visual inmediata para todas las
 * interacciones del usuario, incluyendo confirmaciones para operaciones
 * críticas como eliminación de diccionarios.
 * 
 * @version 1.0
 * @since 1.0
 */
public class GestionDiccionariosView {
    private final Parent view;

    private ControladorDiccionarioView controlador;

    private Button btnCrear;
    private Button btnModificar;
    private Button btnEliminar;
    private Button btnImportar;
    private Button btnVolver;
    private TableView<DiccionarioVisual> listaDiccionarios;

  
    /**
     * Constructor que inicializa la vista de gestión de diccionarios.
     * Configura la interfaz completa incluyendo tabla de diccionarios,
     * panel de botones laterales, aplicación de temas, estilos CSS
     * y funcionalidad responsiva para diferentes tamaños de ventana.
     * 
     * @pre controlador no debe ser null y debe estar correctamente inicializado
     *      con acceso a funcionalidades de gestión de diccionarios y temas.
     * @param controlador Controlador que maneja la lógica de gestión de diccionarios
     * @post Se crea una nueva instancia con la interfaz completamente configurada,
     *       tabla de diccionarios cargada con datos actuales, botones de acción
     *       con estado apropiado, estilos de tema aplicados, funcionalidad responsiva
     *       activa y todos los listeners de eventos configurados correctamente.
     * @throws NullPointerException si controlador es null
     */
    public GestionDiccionariosView(ControladorDiccionarioView controlador) {
        this.controlador = controlador;
        Text titulo = new Text("Gestión de Diccionarios");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        titulo.setStyle("-fx-fill: white;");
        HBox header = new HBox(titulo);
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: #2f3e4e; -fx-padding: 15px;");
        header.setMinHeight(90);

        VBox izquierda = new VBox(15);
        izquierda.setPadding(new Insets(20, 40, 20, 40));
        izquierda.setSpacing(10);
        aplicarFondoPorTema(izquierda, controlador.getTema(), "#ffffff", "#1c1747");
        // Crear botones
        CrearBotones();


        izquierda.getChildren().addAll(btnCrear, btnImportar, btnModificar, btnEliminar, btnVolver);


        VBox diccionariosBox = new VBox(10);
        diccionariosBox.setPadding(new Insets(20, 100, 20, 100));

        cargarDiccionarios();

        diccionariosBox.getChildren().addAll(listaDiccionarios);
        aplicarFondoPorTema(diccionariosBox, controlador.getTema(), "#ffffff", "#1c1747");

        HBox contenido = new HBox(20, izquierda, diccionariosBox);
        contenido.setPadding(new Insets(40));
        contenido.setAlignment(Pos.CENTER);
        contenido.setSpacing(40);
        HBox.setHgrow(izquierda, Priority.ALWAYS);
        HBox.setHgrow(diccionariosBox, Priority.ALWAYS);

        VBox root = new VBox(header, contenido);
        VBox.setVgrow(contenido, Priority.ALWAYS);

        // Bind the sizes of the components to adapt to the window size
        root.setPrefSize(800, 600);
        if (controlador.getTema().equals("Claro")) root.setStyle("-fx-background-color: #f5f5f5;");
        else if (controlador.getTema().equals("Oscuro")) root.setStyle("-fx-background-color: #0b0a2e;");
        root.widthProperty().addListener((obs, oldVal, newVal) -> {
            izquierda.setPrefWidth(newVal.doubleValue() * 0.2);
            diccionariosBox.setPrefWidth(newVal.doubleValue() * 0.7);
        });

        root.heightProperty().addListener((obs, oldVal, newVal) -> {
            listaDiccionarios.setPrefHeight(newVal.doubleValue() * 0.7);
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
            System.err.println("Error al cargar CSS en GestionDiccionariosView: " + e.getMessage());
        }

        aplicarCssBotones();
        cambiarColorTextoPorTema(root, controlador.getTema());
    }

    /**
     * Aplica colores de fondo y estilos adaptativos según el tema actual.
     * Configura automáticamente colores de fondo, bordes y radios de borde
     * para mantener consistencia visual entre temas claro y oscuro.
     * 
     * @pre nodo, tema, claroColor y oscuroColor no deben ser null.
     * @param nodo Contenedor Parent al que aplicar el estilo temático
     * @param tema Tema actual del sistema ("Claro" o "Oscuro")
     * @param claroColor Color hexadecimal para el tema claro
     * @param oscuroColor Color hexadecimal para el tema oscuro
     * @post El nodo tiene aplicados los estilos CSS apropiados para el tema,
     *       incluyendo colores de fondo, bordes redondeados y bordes de contraste
     *       que mantienen la legibilidad y coherencia visual del tema seleccionado.
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
     * Inicializa y configura la tabla principal de diccionarios disponibles.
     * Carga los datos actuales desde el controlador, configura columnas,
     * estilos visuales, funcionalidad de doble clic y listeners de selección
     * para habilitar/deshabilitar botones según el estado actual.
     * 
     * @pre El controlador debe estar inicializado y disponible para consultas.
     * @post La tabla de diccionarios está completamente configurada con datos
     *       actuales, columna de nombres centrada, funcionalidad de doble clic
     *       para vista detallada, listeners de selección activos, estilos CSS
     *       aplicados y placeholder informativo cuando esté vacía.
     */
    private void cargarDiccionarios() {
        // Aquí se cargarían los diccionarios desde el modelo
        listaDiccionarios = new TableView<>();
        listaDiccionarios.setPlaceholder(new Label("No hay diccionarios disponibles"));
        listaDiccionarios.setItems(FXCollections.observableArrayList(controlador.getDiccionarios()));

        
        estilizarTabla();        
        
        TableColumn<DiccionarioVisual, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombre()));
        colNombre.setMinWidth(300);
        colNombre.setCellFactory(column -> {
            TableCell<DiccionarioVisual, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item);
                        setAlignment(Pos.CENTER); // Centrado desde código
                    }
                }
            };
            return cell;
        });
        listaDiccionarios.getColumns().setAll(colNombre);
        listaDiccionarios.setPrefHeight(400);
        listaDiccionarios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        listaDiccionarios.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                DiccionarioVisual diccionarioSeleccionado = listaDiccionarios.getSelectionModel().getSelectedItem();
                if (diccionarioSeleccionado != null) {
                    controlador.mostrarPopupDiccionario(diccionarioSeleccionado);
                }
            }
        });

        // Enfocar la columna seleccionada cuando se selecciona
        DiccionarioVisual seleccionado = listaDiccionarios.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            int row = listaDiccionarios.getItems().indexOf(seleccionado);
            listaDiccionarios.getFocusModel().focus(row, listaDiccionarios.getColumns().get(0));
            listaDiccionarios.getSelectionModel().select(row);
            listaDiccionarios.scrollTo(row); // opcional, para asegurar visibilidad
        }
    }


    /**
     * Aplica estilos CSS y configuraciones visuales avanzadas a la tabla principal.
     * Configura dimensiones, comportamiento de columnas, modos de selección,
     * placeholders estilizados y listeners para manejo automático del estado
     * de botones según la selección actual del usuario.
     * 
     * @pre listaDiccionarios debe estar inicializada y los botones disponibles.
     * @post La tabla tiene aplicados estilos CSS modernos, dimensiones apropiadas,
     *       columnas no reordenables, selección simple habilitada, placeholder
     *       estilizado, listeners de selección configurados para botones
     *       y altura de filas optimizada para la experiencia del usuario.
     */
    private void estilizarTabla() {
        if (listaDiccionarios != null) {
            // Aplicar clase CSS
            listaDiccionarios.getStyleClass().add("modern-table");
            
            // Configurar dimensiones
            listaDiccionarios.setMinWidth(300);
            listaDiccionarios.setPrefWidth(500);
            listaDiccionarios.setMaxWidth(Double.MAX_VALUE);
            
            // Desactivar reordenamiento de columnas
            for (TableColumn<DiccionarioVisual, ?> column : listaDiccionarios.getColumns()) {
                column.setReorderable(false);
            }
            
            // Configurar selección
            listaDiccionarios.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.SINGLE);
            
            // Placeholder 
            Label placeholderLabel = new Label("No hay diccionarios disponibles");
            placeholderLabel.setStyle("-fx-text-fill: #757575; -fx-font-style: italic; -fx-font-size: 14px;");
            listaDiccionarios.setPlaceholder(placeholderLabel);
            
            // Event listener para habilitar/deshabilitar botones según selección
            listaDiccionarios.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                boolean haySeleccion = newSelection != null;
                if (btnModificar != null) btnModificar.setDisable(!haySeleccion);
                if (btnEliminar != null) btnEliminar.setDisable(!haySeleccion);
            });
            
            // Configurar altura de filas
            listaDiccionarios.setRowFactory(tv -> {
                javafx.scene.control.TableRow<DiccionarioVisual> row = new javafx.scene.control.TableRow<>();
                row.setPrefHeight(45); 
                return row;
            });
        }
    }


    /**
     * Aplica clases CSS específicas a todos los botones de la interfaz.
     * Configura estilos consistentes diferenciando botones por funcionalidad:
     * primarios para acciones constructivas, peligro para destructivas
     * y efectos visuales uniformes para toda la interfaz.
     * 
     * @pre Todos los botones deben estar inicializados correctamente.
     * @post Todos los botones tienen aplicadas clases CSS apropiadas:
     *       crear, modificar e importar con estilo primario, eliminar con
     *       estilo de peligro, volver con efectos básicos, y todos incluyen
     *       efectos de transición visual para mejorar la experiencia de usuario.
     */
    private void aplicarCssBotones() {
        btnCrear.getStyleClass().addAll("btn-effect", "btn-primary");
        btnModificar.getStyleClass().addAll("btn-effect", "btn-primary");
        btnEliminar.getStyleClass().addAll("btn-effect", "btn-danger");
        btnImportar.getStyleClass().addAll("btn-effect", "btn-primary");
        btnVolver.getStyleClass().add("btn-effect");
    }


    /**
     * Crea e inicializa todos los botones de acción con sus manejadores de eventos.
     * Configura botones para crear, modificar, eliminar, importar y volver,
     * estableciendo validaciones de selección, confirmaciones de seguridad
     * y delegación apropiada de acciones al controlador correspondiente.
     * 
     * @pre El controlador debe estar inicializado y listaDiccionarios disponible.
     * @post Todos los botones están creados con estilos base, manejadores de eventos
     *       configurados, validaciones de selección implementadas para operaciones
     *       que requieren elementos seleccionados, y mensajes de error apropiados
     *       para guiar al usuario en el uso correcto de la interfaz.
     */
    private void CrearBotones() {
        btnCrear = crearBoton("Crear Diccionario", "#3498db");
        btnModificar = crearBoton("Modificar Diccionario", "#3498db");
        btnEliminar = crearBoton("Eliminar Diccionario", "#e74c3c");
        btnImportar = crearBoton("Importar Diccionario", "#3498db");
        btnVolver = crearBoton("Volver", "#95a5a6");

        btnVolver.setOnAction(e -> controlador.volverAMenuPrincipal());
        btnImportar.setOnAction(e -> controlador.abrirSelectorDeDirectorio());
        btnCrear.setOnAction(e -> controlador.mostrarVistaCrear());
        btnModificar.setOnAction(e -> {
            DiccionarioVisual diccionarioSeleccionado = listaDiccionarios.getSelectionModel().getSelectedItem();
            if (diccionarioSeleccionado != null) {
                controlador.mostrarVistaModificar(diccionarioSeleccionado);
            } else {
                controlador.mostrarAlerta("error", "Selecciona un diccionario", "Por favor, selecciona un diccionario para modificar.");
            }
        });
        btnEliminar.setOnAction(e -> eliminarDiccionario());
    }


    /**
     * Crea un botón personalizado con estilo y configuración específicos.
     * Aplica formato de texto, colores personalizados, dimensiones estándar
     * y propiedades de layout apropiadas para mantener consistencia visual
     * en todos los botones de la interfaz de gestión.
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
     * Ejecuta el proceso de eliminación de un diccionario seleccionado.
     * Valida que haya una selección activa, solicita confirmación al controlador,
     * actualiza la interfaz según el resultado y proporciona retroalimentación
     * apropiada al usuario sobre el éxito o fallo de la operación.
     * 
     * @pre La tabla de diccionarios debe estar inicializada y el controlador disponible.
     * @post Si hay un diccionario seleccionado y la eliminación es exitosa,
     *       se remueve de la tabla y se muestra mensaje de confirmación.
     *       Si no hay selección o falla la eliminación, se muestra mensaje
     *       de error apropiado guiando al usuario sobre la acción correcta.
     */
    private void eliminarDiccionario() {
        DiccionarioVisual diccionarioSeleccionado = listaDiccionarios.getSelectionModel().getSelectedItem();
        if (diccionarioSeleccionado != null) {
            // Aquí se llamaría al método del controlador para eliminar el diccionario
            if (controlador.eliminarDiccionario(diccionarioSeleccionado)) {
                listaDiccionarios.getItems().remove(diccionarioSeleccionado);
                controlador.mostrarAlerta("success", "Diccionario eliminado", "El diccionario " + diccionarioSeleccionado.getNombre() + " ha sido eliminado correctamente.");
            }
            
        } else {   
            controlador.mostrarAlerta("error", "Selecciona un diccionario", "Por favor, selecciona un diccionario para eliminar.");
        }
    }

    /**
     * Obtiene la vista cargada de gestión de diccionarios.
     * Proporciona acceso a la jerarquía completa de componentes JavaFX
     * configurados para la gestión integral de diccionarios del sistema.
     * 
     * @pre La vista debe haber sido inicializada completamente en el constructor.
     * @return Parent que contiene la vista completa de gestión de diccionarios
     * @post Se devuelve la referencia a la vista completamente configurada
     *       sin modificar su estado actual ni la configuración de componentes.
     */ 
    public Parent getView() {
        return view;
    }
}
