package scrabble.presentation.views;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import scrabble.MainApplication;
import scrabble.presentation.viewControllers.ControladorJugadoresView;



/**
 * Vista para la gestión completa de jugadores del sistema de Scrabble.
 * Proporciona una interfaz integral para visualizar, crear, eliminar y buscar
 * jugadores, así como acceder a sus historiales de partidas y estadísticas.
 * 
 * La vista presenta una tabla interactiva con información detallada de cada jugador
 * incluyendo tipo (humano/IA), estado de partida actual, nombre de partida activa
 * y puntuaciones totales. Permite realizar operaciones CRUD completas sobre
 * los jugadores y navegar a vistas específicas para gestiones más detalladas.
 * 
 * Características principales:
 * - Tabla interactiva ordenable con información completa de jugadores
 * - Funcionalidad de búsqueda en tiempo real con filtrado dinámico
 * - Creación y eliminación segura de jugadores con validación
 * - Acceso directo al historial individual de cada jugador
 * - Indicadores visuales de estado de partida en tiempo real
 * - Interfaz responsiva con redimensionamiento automático de columnas
 * - Aplicación automática de estilos CSS para coherencia visual
 * - Gestión de selección con habilitación/deshabilitación contextual de botones
 * 
 * La vista utiliza el patrón MVC, comunicándose con ControladorJugadoresView
 * para todas las operaciones de lógica de negocio y persistencia de datos.
 * Implementa una tabla JavaFX con datos observables para actualizaciones
 * automáticas y una experiencia de usuario fluida.
 * 
 * @version 1.0
 * @since 1.0
 */
public class GestionJugadoresView {
    private final ControladorJugadoresView controlador;
    private Parent view;
    
    // Componentes FXML
    private Button btnCrearJugador;
    private Button btnVerHistorial;
    private Button btnEliminarJugador;
    private Button btnVolver;
    private TextField txtBuscar;
    private Button btnBuscar;
    private Button btnRestablecer;    
    private TableView<JugadorRow> tablaJugadores;
    private BorderPane root;
    private StackPane menuLateral;
    private StackPane contenedorContenido;



    /**
     * Constructor que inicializa la vista de gestión de jugadores.
     * Carga el archivo FXML, configura la tabla de jugadores y
     * establece todos los manejadores de eventos necesarios.
     * 
     * @pre controlador no debe ser null y debe estar correctamente inicializado.
     * @param controlador Controlador que maneja la lógica de gestión de jugadores
     * @post Se crea una nueva instancia con la tabla configurada y todos
     *       los componentes listos para la gestión de jugadores.
     * @throws NullPointerException si controlador es null
     */    
    public GestionJugadoresView(ControladorJugadoresView controlador) {
        this.controlador = controlador;
        cargarVista();
    }
    


    /**
     * Carga la vista FXML de gestión de jugadores.
     * Inicializa el archivo FXML, aplica estilos CSS, obtiene referencias
     * a componentes y configura todos los elementos de la interfaz.
     * 
     * @pre El archivo FXML debe existir en la ruta especificada.
     * @post La vista FXML se carga correctamente con todos los estilos aplicados,
     *       componentes referenciados, tabla configurada y eventos establecidos.
     * @throws IOException si hay errores al cargar el archivo FXML
     */    
    private void cargarVista() {
        try {
            // Cargar FXML SIN controlador
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/views/gestion-jugadores-view.fxml"));
            view = loader.load();
            
            // Cargar CSS
            try {
                String cssResource = "/styles/button.css";
                URL cssUrl = getClass().getResource(cssResource);
                if (cssUrl != null) {
                    view.getStylesheets().add(cssUrl.toExternalForm());
                }
                else {
                    controlador.mostrarAlerta("error", "Error inesperado", "No se ha podido cargar la vista");
                }
            } catch (Exception e) {
                    controlador.mostrarAlerta("error", "Error inesperado", "No se ha podido cargar la vista");

            }
            
            // Obtener referencias a los componentes
            btnCrearJugador = (Button) view.lookup("#btnCrearJugador");
            btnVerHistorial = (Button) view.lookup("#btnVerHistorial");
            btnEliminarJugador = (Button) view.lookup("#btnEliminarJugador");
            btnVolver = (Button) view.lookup("#btnVolver");
            txtBuscar = (TextField) view.lookup("#txtBuscar");
            btnBuscar = (Button) view.lookup("#btnBuscar");
            btnRestablecer = (Button) view.lookup("#btnRestablecer");            
            tablaJugadores = (TableView<JugadorRow>) view.lookup("#tablaJugadores");
            root = (BorderPane) view.lookup("#root");
            menuLateral = (StackPane) view.lookup("#menuLateral");
            contenedorContenido = (StackPane) view.lookup("#contenedorContenido");
            
            aplicarEstiloBotones();
            configurarTabla();
            configurarEventos();
            cargarJugadores();
            aplicarTema();
            cambiarColorTextoPorTema(root, controlador.getTema());
            
        } catch (IOException e) {
            e.printStackTrace();
            controlador.mostrarAlerta("error", "Error", "No se pudo cargar la vista de gestión de jugadores");
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
     * Aplica las clases CSS apropiadas a todos los botones de la interfaz.
     * Establece estilos visuales consistentes para botones según su función:
     * primarios, secundarios, de peligro, etc.
     * 
     * @pre Los botones deben haber sido inicializados correctamente.
     * @post Todos los botones tienen aplicadas las clases CSS correspondientes
     *       a su función específica (primary, danger, effect, etc.).
     */
    private void aplicarEstiloBotones() {
        if (btnCrearJugador != null) {
            btnCrearJugador.getStyleClass().addAll("btn-effect", "btn-primary");
        }
        
        if (btnVerHistorial != null) {
            btnVerHistorial.getStyleClass().addAll("btn-effect", "btn-primary");
        }
        
        if (btnEliminarJugador != null) {
            btnEliminarJugador.getStyleClass().addAll("btn-effect", "btn-danger");
        }
        
        if (btnVolver != null) {
            btnVolver.getStyleClass().add("btn-effect");
        }
        
        if (btnBuscar != null) {
            btnBuscar.getStyleClass().addAll("btn-effect", "btn-primary");
        }

        if (btnRestablecer != null) {
            btnRestablecer.getStyleClass().add("btn-effect");
        }        
    }
    


    /**
     * Configura la estructura y comportamiento de la tabla de jugadores.
     * Define columnas, políticas de redimensionamiento, estilos visuales
     * y manejadores de eventos para selección de filas.
     * 
     * @pre tablaJugadores debe estar inicializada.
     * @post La tabla queda completamente configurada con todas sus columnas,
     *       políticas de redimensionamiento, estilos CSS y listeners de selección.
     *       Los botones contextuales se habilitan/deshabilitan según la selección.
     */
    private void configurarTabla() {
        if (tablaJugadores != null) {
            tablaJugadores.getColumns().clear();
            
            tablaJugadores.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
            
            tablaJugadores.setMinWidth(100); 
            tablaJugadores.setPrefWidth(1000);
            tablaJugadores.setMaxWidth(Double.MAX_VALUE);

            TableColumn<JugadorRow, String> colNombre = new TableColumn<>("Nombre");
            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            
            TableColumn<JugadorRow, String> colTipo = new TableColumn<>("Tipo");
            colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
            
            TableColumn<JugadorRow, String> colEnPartida = new TableColumn<>("En partida");
            colEnPartida.setCellValueFactory(new PropertyValueFactory<>("enPartida"));
            
            TableColumn<JugadorRow, String> colNombrePartida = new TableColumn<>("Nombre partida");
            colNombrePartida.setCellValueFactory(new PropertyValueFactory<>("nombrePartida"));
            
            TableColumn<JugadorRow, Integer> colPuntuacion = new TableColumn<>("Puntuación");
            colPuntuacion.setCellValueFactory(new PropertyValueFactory<>("puntuacion"));
            
            tablaJugadores.getColumns().addAll(colNombre, colTipo, colEnPartida, colNombrePartida, colPuntuacion);
            tablaJugadores.getStyleClass().add("modern-table");
            
            colNombre.setMaxWidth(1f * Integer.MAX_VALUE * 20); 
            colTipo.setMaxWidth(1f * Integer.MAX_VALUE * 15);   
            colEnPartida.setMaxWidth(1f * Integer.MAX_VALUE * 15); 
            colNombrePartida.setMaxWidth(1f * Integer.MAX_VALUE * 30);
            colPuntuacion.setMaxWidth(1f * Integer.MAX_VALUE * 20); 
            
            tablaJugadores.setPlaceholder(new javafx.scene.control.Label("No hay jugadores disponibles"));
            
            cargarCSSTablas();
            
            tablaJugadores.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                boolean haySeleccion = newSelection != null;
                if (btnVerHistorial != null) btnVerHistorial.setDisable(!haySeleccion);
                if (btnEliminarJugador != null) btnEliminarJugador.setDisable(!haySeleccion);
            });
        }
    }



    /**
     * Carga los estilos CSS específicos para la tabla de jugadores.
     * Aplica hojas de estilo especializadas para mejorar la apariencia
     * visual de la tabla, verificando que no se dupliquen las cargas.
     * 
     * @pre La vista debe estar inicializada.
     * @post Se cargan los estilos CSS específicos para tablas si no estaban
     *       previamente cargados. Se muestra alerta si hay errores en la carga.
     */
    private void cargarCSSTablas() {
        try {
            String tableCssResource = "/styles/table.css";
            URL tableCssUrl = getClass().getResource(tableCssResource);
            if (tableCssUrl != null) {
                // Verificar si ya está cargado
                boolean yaEstaCargado = false;
                for (String stylesheet : view.getStylesheets()) {
                    if (stylesheet.contains("table.css")) {
                        yaEstaCargado = true;
                        break;
                    }
                }
                
                if (!yaEstaCargado) {
                    view.getStylesheets().add(tableCssUrl.toExternalForm());
                }
            } else {
                controlador.mostrarAlerta("error", "Error inesperado", "No se ha podido cargar la vista");
            }
        } catch (Exception e) {
                controlador.mostrarAlerta("error", "Error inesperado", "No se ha podido cargar la vista");
        }
    }



    /**
     * Configura todos los manejadores de eventos de los componentes interactivos.
     * Establece las acciones para botones de gestión, búsqueda y navegación,
     * incluyendo validación de selecciones antes de ejecutar acciones.
     * 
     * @pre Todos los componentes interactivos deben estar inicializados.
     * @post Todos los botones y campos tienen configurados sus manejadores
     *       de eventos correspondientes con validación apropiada de estados.
     */
    private void configurarEventos() {
        if (btnCrearJugador != null) {
            btnCrearJugador.setOnAction(e -> controlador.mostrarVistaCrearJugador());
        }
        
        if (btnVerHistorial != null) {
            btnVerHistorial.setOnAction(e -> {
                JugadorRow seleccionado = tablaJugadores.getSelectionModel().getSelectedItem();
                if (seleccionado != null) {
                    controlador.mostrarVistaHistorialJugador(seleccionado.getNombre());
                } else {
                    controlador.mostrarAlerta("warning", "Advertencia", "Por favor, seleccione un jugador antes de acceder a su historial");
                }
            });
        }
        
        if (btnEliminarJugador != null) {
            btnEliminarJugador.setOnAction(e -> {
                JugadorRow seleccionado = tablaJugadores.getSelectionModel().getSelectedItem();
                if (seleccionado != null) {
                    controlador.eliminarJugador(seleccionado.getNombre());
                } else {
                    controlador.mostrarAlerta("warning", "Advertencia", "Por favor, seleccione un jugador para eliminar");
                }
            });
        }
        
        if (btnVolver != null) {
            btnVolver.setOnAction(e -> controlador.volverAMenuPrincipal());
        }
        
        if (btnBuscar != null) {
            btnBuscar.setOnAction(e -> {
                String patron = txtBuscar.getText();
                buscarJugadores(patron);
            });
        }

        if (btnRestablecer != null) {
            btnRestablecer.setOnAction(e -> {
                txtBuscar.clear(); 
                cargarJugadores(); 
            });
        }        

        if (txtBuscar != null) {
            txtBuscar.setOnAction(e -> {
                String patron = txtBuscar.getText();
                buscarJugadores(patron);
            });
        }        
    }
    


    /**
     * Carga todos los jugadores del sistema en la tabla de visualización.
     * Obtiene la lista completa de jugadores del controlador, genera filas
     * de datos con información detallada y actualiza la tabla.
     * 
     * @pre tablaJugadores debe estar configurada correctamente.
     * @post La tabla se actualiza con información completa de todos los jugadores
     *       ordenados alfabéticamente. Se selecciona automáticamente el primer
     *       elemento si hay datos disponibles.
     */
    private void cargarJugadores() {
        if (tablaJugadores != null) {
            List<String> jugadores = controlador.getJugadores();            
            ObservableList<JugadorRow> datos = FXCollections.observableArrayList();
            jugadores.sort(String::compareToIgnoreCase);
            // Generar filas para cada jugador
            for (String jugador : jugadores) {
                String tipo = controlador.esIA(jugador) ? "Bot" : "Humano";
                boolean isPlaying = controlador.isEnPartida(jugador);
                String playing = isPlaying ? "Sí" : "No";
                String nombrePartida = isPlaying ? (controlador.getNombrePartidaActual(jugador)) : "-";
                int total = controlador.getPuntuacionTotal(jugador); 
                datos.add(new JugadorRow(jugador, tipo, playing, nombrePartida, total));
            }
            
            // Asignar datos a la tabla
            tablaJugadores.setItems(datos);
            
            // Seleccionar primer elemento si hay datos
            if (!datos.isEmpty()) {
                tablaJugadores.getSelectionModel().select(0);
            }
        }
    }
    


    /**
     * Busca jugadores según un patrón de texto especificado.
     * Filtra la lista de jugadores mostrada en la tabla basándose
     * en coincidencias del patrón con los nombres de jugadores.
     * 
     * @pre patron no debe ser null, tablaJugadores debe estar inicializada.
     * @param patron Texto a buscar en los nombres de jugadores
     * @post La tabla se actualiza mostrando solo los jugadores cuyos nombres
     *       coinciden con el patrón. Se personaliza el placeholder según
     *       los resultados y se selecciona el primer elemento si hay coincidencias.
     * @throws NullPointerException si patron es null
     */
    private void buscarJugadores(String patron) {
        if (tablaJugadores != null) {
            List<String> jugadoresFiltrados = controlador.buscarJugadores(patron);
            
            ObservableList<JugadorRow> datos = FXCollections.observableArrayList();
            
            // Generar filas para cada jugador
            for (String jugador : jugadoresFiltrados) {
                datos.add(new JugadorRow(jugador, "Humano", "No", "-", 0));
            }
            tablaJugadores.setItems(datos);
            if (datos.isEmpty()) {
                tablaJugadores.setPlaceholder(new javafx.scene.control.Label("No se ha encontrado ningún jugador con nombre: " + patron));
            } else {
                tablaJugadores.setPlaceholder(new javafx.scene.control.Label("No hay jugadores disponibles"));
                tablaJugadores.getSelectionModel().select(0);
            }
        }
    }
    


    /**
     * Obtiene la vista cargada de gestión de jugadores.
     * Proporciona acceso a la jerarquía de componentes JavaFX
     * para su integración en la aplicación principal.
     * 
     * @pre La vista debe haber sido cargada previamente.
     * @return Parent que contiene la vista completa de gestión de jugadores
     * @post Se devuelve la referencia a la vista cargada sin modificar su estado.
     */    
    public Parent getView() {
        return view;
    }
    


    /**
     * Clase estática interna que representa una fila en la tabla de jugadores.
     * Encapsula toda la información mostrada de un jugador en la tabla
     * incluyendo identificación, estado, partida actual y estadísticas.
     * 
     * Utilizada por la TableView para mostrar datos estructurados de cada
     * jugador con las propiedades necesarias para el binding automático
     * de datos con las columnas de la tabla mediante PropertyValueFactory.
     * 
     * La clase proporciona encapsulación completa de los datos del jugador
     * para la visualización tabular, manteniendo consistencia en los tipos
     * de datos y facilitando el ordenamiento y filtrado de información.
     * 
     * @version 1.0
     * @since 1.0
     */
    public static class JugadorRow {
        private String nombre;
        private String tipo;
        private String enPartida;
        private String nombrePartida;
        private int puntuacion;
        


        /**
         * Constructor que inicializa una fila de jugador con toda su información.
         * Crea una representación completa del estado y estadísticas de un jugador
         * para su visualización en la tabla de gestión.
         * 
         * @pre Ningún parámetro String debe ser null.
         * @param nombre Nombre único del jugador
         * @param tipo Tipo de jugador ("Humano" o "Bot")
         * @param enPartida Estado de participación ("Sí" o "No")
         * @param nombrePartida Nombre de partida actual o "-"
         * @param puntuacion Puntuación total acumulada
         * @post Se crea una nueva instancia con toda la información del jugador
         *       lista para ser utilizada en la tabla de visualización.
         */        
        public JugadorRow(String nombre, String tipo, String enPartida, String nombrePartida, int puntuacion) {
            this.nombre = nombre;
            this.tipo = tipo;
            this.enPartida = enPartida;
            this.nombrePartida = nombrePartida;
            this.puntuacion = puntuacion;
        }
   


        /**
         * Obtiene el nombre del jugador.
         * 
         * @pre El objeto debe estar correctamente inicializado.
         * @return Nombre del jugador
         * @post Se devuelve el nombre sin modificar el estado del objeto.
         */   
        public String getNombre() { return nombre; }



        /**
         * Establece el nombre del jugador.
         * 
         * @pre nombre no debe ser null.
         * @param nombre Nuevo nombre para el jugador
         * @post Se actualiza el nombre del jugador con el valor especificado.
         */        
        public void setNombre(String nombre) { this.nombre = nombre; }
        


        /**
         * Obtiene el tipo del jugador.
         * 
         * @pre El objeto debe estar correctamente inicializado.
         * @return Tipo del jugador ("Humano" o "Bot")
         * @post Se devuelve el tipo sin modificar el estado del objeto.
         */        
        public String getTipo() { return tipo; }



        /**
         * Establece el tipo del jugador.
         * 
         * @pre tipo no debe ser null y debe ser "Humano" o "Bot".
         * @param tipo Nuevo tipo para el jugador
         * @post Se actualiza el tipo del jugador con el valor especificado.
         */        
        public void setTipo(String tipo) { this.tipo = tipo; }
        


        /**
         * Obtiene el estado de participación en partida.
         * 
         * @pre El objeto debe estar correctamente inicializado.
         * @return Estado de participación ("Sí" o "No")
         * @post Se devuelve el estado sin modificar el objeto.
         */        
        public String getEnPartida() { return enPartida; }



        /**
         * Establece el estado de participación en partida.
         * 
         * @pre enPartida no debe ser null y debe ser "Sí" o "No".
         * @param enPartida Nuevo estado de participación
         * @post Se actualiza el estado de participación con el valor especificado.
         */        
        public void setEnPartida(String enPartida) { this.enPartida = enPartida; }
        


        /**
         * Obtiene el nombre de la partida actual.
         * 
         * @pre El objeto debe estar correctamente inicializado.
         * @return Nombre de la partida actual o "-" si no está en partida
         * @post Se devuelve el nombre de partida sin modificar el estado.
         */        
        public String getNombrePartida() { return nombrePartida; }


        /**
         * Establece el nombre de la partida actual.
         * 
         * @pre nombrePartida no debe ser null.
         * @param nombrePartida Nuevo nombre de partida o "-"
         * @post Se actualiza el nombre de partida con el valor especificado.
         */        
        public void setNombrePartida(String nombrePartida) { this.nombrePartida = nombrePartida; }
        

        /**
         * Obtiene la puntuación total del jugador.
         * 
         * @pre El objeto debe estar correctamente inicializado.
         * @return Puntuación total acumulada del jugador
         * @post Se devuelve la puntuación sin modificar el estado del objeto.
         */        
        public int getPuntuacion() { return puntuacion; }



        /**
         * Establece la puntuación total del jugador.
         * 
         * @pre puntuacion debe ser un valor no negativo.
         * @param puntuacion Nueva puntuación total para el jugador
         * @post Se actualiza la puntuación total con el valor especificado.
         */        
        public void setPuntuacion(int puntuacion) { this.puntuacion = puntuacion; }
    }
}