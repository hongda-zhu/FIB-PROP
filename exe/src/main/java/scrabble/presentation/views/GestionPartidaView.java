package scrabble.presentation.views;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import scrabble.MainApplication;
import scrabble.presentation.viewControllers.ControladorPartidaView;



/**
 * Vista para la gestión integral de partidas guardadas del sistema de Scrabble.
 * Permite visualizar, cargar, eliminar y crear partidas, proporcionando una
 * interfaz centralizada para la administración completa del ciclo de vida
 * de las partidas en el sistema.
 * 
 * La vista presenta una tabla interactiva con información resumida de cada partida
 * guardada incluyendo identificador único, diccionario utilizado y número de
 * jugadores participantes. Proporciona operaciones completas para la gestión
 * del ciclo de vida de las partidas guardadas con validación de selecciones
 * y manejo de estados contextuales.
 * 
 * Características principales:
 * - Tabla interactiva con resumen completo de partidas guardadas
 * - Carga segura de partidas desde punto de guardado específico
 * - Eliminación controlada de partidas obsoletas o no deseadas
 * - Creación directa de nuevas partidas desde la interfaz
 * - Integración completa con el sistema de persistencia de datos
 * - Interfaz responsiva con redimensionamiento automático de columnas
 * - Gestión contextual de botones basada en selecciones de usuario
 * - Aplicación automática de estilos CSS para coherencia visual
 * - Placeholder personalizado para estados sin datos
 * 
 * La vista utiliza el patrón MVC, comunicándose con ControladorPartidaView
 * para todas las operaciones de lógica de negocio, persistencia de datos
 * y navegación entre vistas. Implementa una tabla JavaFX con datos observables
 * para actualizaciones automáticas y una experiencia de usuario fluida.
 * 
 * @version 1.0
 * @since 1.0
 */
public class GestionPartidaView {
    private final ControladorPartidaView controlador;
    private Parent view;
    
    // Componentes FXML
    private Button btnJugarNuevaPartida;
    private Button btnEliminarPartida;
    private Button btnCargarPartida;
    private Button btnVolver;
    private TableView<PartidaRow> tablaPartidas;
    private BorderPane root;
    private StackPane menuLateral;
    private StackPane contenedorContenido;
    


    /**
     * Constructor que inicializa la vista de gestión de partidas.
     * Carga el archivo FXML, configura la tabla de partidas y
     * establece todos los manejadores de eventos necesarios.
     * 
     * @pre controlador no debe ser null y debe estar correctamente inicializado.
     * @param controlador Controlador que maneja la lógica de gestión de partidas
     * @post Se crea una nueva instancia con la tabla configurada y todos
     *       los componentes listos para la gestión de partidas.
     * @throws NullPointerException si controlador es null
     */    
    public GestionPartidaView(ControladorPartidaView controlador) {
        this.controlador = controlador;
        cargarVista();
    }
    


    /**
     * Clase estática interna que representa una fila en la tabla de partidas.
     * Encapsula la información resumida de una partida guardada incluyendo
     * identificador único, configuración utilizada y número de participantes.
     * 
     * Utilizada por la TableView para mostrar datos estructurados de cada
     * partida con las propiedades necesarias para el binding automático
     * de datos con las columnas de la tabla mediante PropertyValueFactory.
     * 
     * La clase proporciona encapsulación completa de los metadatos esenciales
     * de una partida para la visualización tabular, facilitando la identificación,
     * selección y gestión de partidas guardadas por parte del usuario.
     * 
     * @version 1.0
     * @since 1.0
     */
    public static class PartidaRow {
        private Integer id;
        private String diccionario;
        private Integer numJugadores;
        

        /**
         * Constructor que inicializa una fila de partida con su información resumida.
         * Crea una representación completa de los metadatos esenciales de una
         * partida guardada para su visualización en la tabla de gestión.
         * 
         * @pre Ningún parámetro debe ser null.
         * @param id Identificador único de la partida
         * @param diccionario Diccionario utilizado en la partida
         * @param numJugadores Número de jugadores en la partida
         * @post Se crea una nueva instancia con la información completa de la partida
         *       lista para ser utilizada en la tabla de visualización.
         */        
        public PartidaRow(Integer id, String diccionario, Integer numJugadores) {
            this.id = id;
            this.diccionario = diccionario;
            this.numJugadores = numJugadores;
        }
        

        /**
         * Obtiene el identificador único de la partida.
         * 
         * @pre El objeto debe estar correctamente inicializado.
         * @return Identificador único de la partida
         * @post Se devuelve el ID sin modificar el estado del objeto.
         */        
        public Integer getId() { return id; }


        /**
         * Establece el identificador único de la partida.
         * 
         * @pre id no debe ser null y debe ser único en el sistema.
         * @param id Nuevo identificador para la partida
         * @post Se actualiza el identificador de la partida con el valor especificado.
         */        
        public void setId(Integer id) { this.id = id; }
        

        /**
         * Obtiene el nombre del diccionario utilizado en la partida.
         * 
         * @pre El objeto debe estar correctamente inicializado.
         * @return Nombre del diccionario de la partida
         * @post Se devuelve el diccionario sin modificar el estado del objeto.
         */        
        public String getDiccionario() { return diccionario; }


        /**
         * Establece el diccionario utilizado en la partida.
         * 
         * @pre diccionario no debe ser null y debe ser un diccionario válido.
         * @param diccionario Nuevo diccionario para la partida
         * @post Se actualiza el diccionario de la partida con el valor especificado.
         */        
        public void setDiccionario(String diccionario) { this.diccionario = diccionario; }


        /**
         * Obtiene el número de jugadores participantes en la partida.
         * 
         * @pre El objeto debe estar correctamente inicializado.
         * @return Número de jugadores en la partida
         * @post Se devuelve el número de jugadores sin modificar el estado.
         */        
        public Integer getNumJugadores() { return numJugadores; }


        /**
         * Establece el número de jugadores participantes en la partida.
         * 
         * @pre numJugadores no debe ser null y debe ser un valor positivo.
         * @param numJugadores Nuevo número de jugadores para la partida
         * @post Se actualiza el número de jugadores con el valor especificado.
         */        
        public void setNumJugadores(Integer numJugadores) { this.numJugadores = numJugadores; }
    } 


    /**
     * Carga la vista FXML de gestión de partidas.
     * Inicializa el archivo FXML, aplica estilos CSS, obtiene referencias
     * a componentes y configura todos los elementos de la interfaz.
     * 
     * @pre El archivo FXML debe existir en la ruta especificada.
     * @post La vista FXML se carga correctamente con todos los estilos aplicados,
     *       componentes referenciados, tabla configurada y eventos establecidos.
     *       Se cargan las partidas existentes automáticamente.
     * @throws IOException si hay errores al cargar el archivo FXML
     */
    private void cargarVista() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/views/gestion-partida-view.fxml"));
            view = loader.load();

            try {
                String cssResource = "/styles/button.css";
                URL cssUrl = getClass().getResource(cssResource);
                if (cssUrl != null) {
                    view.getStylesheets().add(cssUrl.toExternalForm());
                }

                String tableCssResource = "/styles/table.css";
                URL tableCssUrl = getClass().getResource(tableCssResource);
                if (tableCssUrl != null) {
                    view.getStylesheets().add(tableCssUrl.toExternalForm());
                }
            } catch (Exception e) {
                controlador.mostrarAlerta("error", "Error inesperado", "No se ha podido cargar la vista");                    
            }                        
            // Obtener referencias a los componentes usando lookup
            btnJugarNuevaPartida = (Button) view.lookup("#btnJugarNuevaPartida");
            btnEliminarPartida = (Button) view.lookup("#btnEliminarPartida");
            btnCargarPartida = (Button) view.lookup("#btnCargarPartida");
            btnVolver = (Button) view.lookup("#btnVolver");
            tablaPartidas = (TableView<PartidaRow>) view.lookup("#tablaPartidas");
            root = (BorderPane) view.lookup("#root");
            menuLateral = (StackPane) view.lookup("#menuLateral");
            contenedorContenido = (StackPane) view.lookup("#contenedorContenido");
            
            aplicarEstiloBotones();  
            configurarTabla();
            estilizarTabla();          
            configurarEventos();
            cargarPartidas();
            aplicarTema();
            cambiarColorTextoPorTema(root, controlador.getTema());
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo cargar la vista de gestión de partidas");
        }
    }

    /**
     * Aplica el tema visual actual al contenedor principal de la vista.
     * Este método obtiene el tema seleccionado desde el controlador y ajusta
     * el color de fondo del contenedor principal en función de si el tema es
     * "Claro" u "Oscuro".
     * @pre: El controlador debe tener un tema válido ("Claro" u "Oscuro").
     * @post: El color de fondo del contenedor principal se actualiza según el tema seleccionado.
     */

    private void aplicarTema() {
        String tema = controlador.getTema();
        if (tema.equals("Claro")) {
            root.setStyle("-fx-background-color: #f5f5f5;");
        } else {
            root.setStyle("-fx-background-color: #0b0a2e;");
        }
        aplicarFondoPorTema(menuLateral, controlador.getTema(), "#ffffff", "#1c1747");
        aplicarFondoPorTema(contenedorContenido, controlador.getTema(), "#ffffff", "#1c1747");
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
     * Configura la estructura básica y columnas de la tabla de partidas.
     * Define las columnas principales, establece políticas de redimensionamiento
     * y configura el placeholder para estados sin datos.
     * 
     * @pre tablaPartidas debe estar inicializada.
     * @post La tabla queda configurada con todas sus columnas definidas,
     *       políticas de redimensionamiento establecidas y estilos CSS aplicados.
     *       Se establece el placeholder para cuando no hay partidas guardadas.
     */
    private void configurarTabla() {
        if (tablaPartidas != null) {
            tablaPartidas.getColumns().clear();
            
            tablaPartidas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
            tablaPartidas.getStyleClass().add("modern-table");
            
            // Configurar columnas
            TableColumn<PartidaRow, Integer> colID = new TableColumn<>("ID");
            colID.setCellValueFactory(new PropertyValueFactory<>("id"));
            
            TableColumn<PartidaRow, String> colDiccionario = new TableColumn<>("Diccionario");
            colDiccionario.setCellValueFactory(new PropertyValueFactory<>("diccionario"));
            
            TableColumn<PartidaRow, Integer> colNumJugadores = new TableColumn<>("Jugadores");
            colNumJugadores.setCellValueFactory(new PropertyValueFactory<>("numJugadores"));
            
            tablaPartidas.getColumns().addAll(colID, colDiccionario, colNumJugadores);
            
            // Configurar anchos proporcionales
            colID.setMaxWidth(1f * Integer.MAX_VALUE * 20);
            colDiccionario.setMaxWidth(1f * Integer.MAX_VALUE * 50);
            colNumJugadores.setMaxWidth(1f * Integer.MAX_VALUE * 30);
            
            // Placeholder cuando no hay partidas
            tablaPartidas.setPlaceholder(new javafx.scene.control.Label("No hay partidas guardadas"));
        }
    }    



    /**
     * Aplica estilos avanzados y configuraciones adicionales a la tabla.
     * Establece dimensiones, comportamientos de selección, desactiva reordenamiento
     * de columnas y configura listeners para manejo contextual de botones.
     * 
     * @pre tablaPartidas debe estar configurada básicamente.
     * @post La tabla tiene aplicados todos los estilos avanzados, configuración
     *       de selección única, placeholder personalizado y listeners que
     *       habilitan/deshabilitan botones según la selección actual.
     */
    private void estilizarTabla() {
        if (tablaPartidas != null) {
            // Aplicar clase CSS
            tablaPartidas.getStyleClass().add("modern-table");
            
            // Configurar dimensiones
            tablaPartidas.setMinWidth(400);
            tablaPartidas.setPrefWidth(600);
            tablaPartidas.setMaxWidth(Double.MAX_VALUE);
            
            // Desactivar reordenamiento de columnas
            for (TableColumn<PartidaRow, ?> column : tablaPartidas.getColumns()) {
                column.setReorderable(false);
            }
            
            // Configurar selección
            tablaPartidas.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.SINGLE);
            
            // Placeholder personalizado
            javafx.scene.control.Label placeholderLabel = new javafx.scene.control.Label("No hay partidas guardadas");
            placeholderLabel.setStyle("-fx-text-fill: #757575; -fx-font-style: italic; -fx-font-size: 14px;");
            tablaPartidas.setPlaceholder(placeholderLabel);
            
            // Event listener para habilitar/deshabilitar botones según selección
            tablaPartidas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                boolean haySeleccion = newSelection != null;
                if (btnCargarPartida != null) btnCargarPartida.setDisable(!haySeleccion);
                if (btnEliminarPartida != null) btnEliminarPartida.setDisable(!haySeleccion);
            });
        }
    }



    /**
     * Aplica las clases CSS apropiadas a todos los botones de la interfaz.
     * Establece estilos visuales consistentes para botones según su función
     * específica: primarios, de éxito, de peligro, etc.
     * 
     * @pre Los botones deben haber sido inicializados correctamente.
     * @post Todos los botones tienen aplicadas las clases CSS correspondientes
     *       a su función específica (primary, success, danger, effect).
     */
    private void aplicarEstiloBotones() {
        if (btnJugarNuevaPartida != null) {
            btnJugarNuevaPartida.getStyleClass().addAll("btn-effect", "btn-primary");
        }
        
        if (btnEliminarPartida != null) {
            btnEliminarPartida.getStyleClass().addAll("btn-effect", "btn-danger");
        }
        
        if (btnCargarPartida != null) {
            btnCargarPartida.getStyleClass().addAll("btn-effect", "btn-success");
        }
        
        if (btnVolver != null) {
            btnVolver.getStyleClass().add("btn-effect");
        }
    }


    /**
     * Configura todos los manejadores de eventos de los componentes interactivos.
     * Establece las acciones para botones de gestión y navegación, incluyendo
     * validación de selecciones antes de ejecutar operaciones críticas.
     * 
     * @pre Todos los componentes interactivos deben estar inicializados.
     * @post Todos los botones tienen configurados sus manejadores de eventos
     *       correspondientes con validación apropiada de estados y manejo
     *       de errores para operaciones críticas.
     */
    private void configurarEventos()  {
        if (btnJugarNuevaPartida != null) {
            
            btnJugarNuevaPartida.setOnAction(e -> {
                try {
                    controlador.crearNuevaPartida();
                } catch (Exception ex) {
                    // Maneja la excepción - muestra un mensaje de error
                    controlador.mostrarAlerta("Error", "Error inesperado", "No se ha podido cargar la vista");
                    controlador.volver();
                }
            });
        }
        
        if (btnCargarPartida != null) {
            btnCargarPartida.setOnAction(e -> {
                PartidaRow partidaSeleccionada = tablaPartidas.getSelectionModel().getSelectedItem();
                if (partidaSeleccionada != null) {
                    controlador.cargarPartida(partidaSeleccionada.getId());
                } else {
                    controlador.mostrarAlerta("warning", "Advertencia", "Por favor, seleccione una partida para cargar");
                }
            });
        }
        
        if (btnEliminarPartida != null) {
            btnEliminarPartida.setOnAction(e -> {
                PartidaRow partidaSeleccionada = tablaPartidas.getSelectionModel().getSelectedItem();
                if (partidaSeleccionada != null) {
                    controlador.eliminarPartidaGuardada(partidaSeleccionada.getId());
                    controlador.mostrarAlerta("success", "Partida eliminada", "¡La partida " + partidaSeleccionada.getId() + " ha sido eliminada existosamente!");
                    cargarPartidas();

                } else {
                    controlador.mostrarAlerta("warning", "Advertencia", "Por favor, seleccione una partida para eliminar");
                }
            });
        }
        
        if (btnVolver != null) {
            btnVolver.setOnAction(e -> controlador.volverAMenuPrincipal());
        }
    }


     /**
     * Carga todas las partidas guardadas del sistema en la tabla de visualización.
     * Obtiene la lista de partidas del controlador, genera filas de datos
     * con información resumida y actualiza la tabla automáticamente.
     * 
     * @pre tablaPartidas debe estar configurada correctamente.
     * @post La tabla se actualiza con información completa de todas las partidas
     *       guardadas disponibles. Se selecciona automáticamente la primera
     *       partida si hay datos disponibles.
     */   
    private void cargarPartidas() {
        if (tablaPartidas != null) {
            List<Integer> partidasGuardadas = controlador.getPartidasGuardadasID();
            ObservableList<PartidaRow> datos = FXCollections.observableArrayList();
            
            for (Integer id : partidasGuardadas) {
                 String diccionario = controlador.getDiccionarioPartida(id);
                int numJugadores = controlador.getNumJugadoresPartida(id); 
                
                datos.add(new PartidaRow(id, diccionario, numJugadores));
            }
            
            tablaPartidas.setItems(datos);
            
            // Seleccionar primer elemento si hay datos
            if (!datos.isEmpty()) {
                tablaPartidas.getSelectionModel().select(0);
            }
        }
    }


    /**
     * Muestra una alerta de sistema con el tipo y mensaje especificados.
     * Utiliza el sistema de alertas nativo de JavaFX para mostrar mensajes
     * informativos, de advertencia o de error al usuario.
     * 
     * @pre type, header y content no deben ser null.
     * @param type Tipo de alerta (ERROR, WARNING, INFORMATION, etc.)
     * @param header Texto del encabezado de la alerta
     * @param content Contenido detallado del mensaje
     * @post Se muestra una ventana modal con la alerta especificada
     *       y se espera a que el usuario la cierre antes de continuar.
     */
    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(type.toString());
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


    /**
     * Obtiene la vista cargada de gestión de partidas.
     * Proporciona acceso a la jerarquía de componentes JavaFX
     * para su integración en la aplicación principal.
     * 
     * @pre La vista debe haber sido cargada previamente.
     * @return Parent que contiene la vista completa de gestión de partidas
     * @post Se devuelve la referencia a la vista cargada sin modificar su estado.
     */
    public Parent getView() {
        return view;
    }
}