package scrabble.presentation.views;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import scrabble.MainApplication;
import scrabble.presentation.viewControllers.ControladorJugadoresView;
import scrabble.presentation.viewControllers.ControladorJugadoresView.EstadisticasJugador;



/**
 * Vista especializada para mostrar el historial detallado de un jugador específico.
 * Presenta información estadística completa y el registro histórico de todas las
 * partidas jugadas por un jugador individual del sistema de Scrabble.
 * 
 * La vista proporciona una interfaz informativa y visualmente atractiva que muestra
 * estadísticas comprehensivas incluyendo puntuaciones totales, máximas, medias,
 * número de partidas jugadas, victorias obtenidas y ratio de éxito. Además,
 * presenta un listado detallado de todas las puntuaciones individuales obtenidas
 * en cada partida registrada en el sistema.
 * 
 * Características principales:
 * - Visualización completa de estadísticas del jugador con métricas clave
 * - Lista estilizada e interactiva del historial de puntuaciones por partida
 * - Interfaz de solo lectura optimizada para consulta de información
 * - Diseño visual moderno con efectos de sombra y estilos personalizados
 * - Manejo inteligente de estados vacíos con mensajes informativos
 * - Aplicación automática de estilos CSS para coherencia visual
 * - Integración seamless con el sistema de navegación de la aplicación
 * - Carga dinámica y segura de componentes FXML con validación de tipos
 * - Estilización personalizada de ListView sin interactividad de selección
 * 
 * La vista utiliza el patrón MVC, obteniendo todos los datos del jugador
 * a través del ControladorJugadoresView y presentando la información de
 * manera clara y estructurada. Implementa un diseño centrado en la experiencia
 * de usuario para consulta eficiente de información histórica.
 * 
 * @version 1.0
 * @since 1.0
 */
public class HistorialJugadorView {
    private final ControladorJugadoresView controlador;
    private final String nombreJugador;
    private Parent view;
    
    // Componentes FXML
    private Label lblNombreJugador;
    private Label lblPuntuacionTotal;
    private Label lblPuntuacionMaxima;
    private Label lblPuntuacionMedia;
    private Label lblPartidasJugadas;
    private Label lblVictorias;
    private Label lblRatioVictorias;
    private ListView<String> listaHistorial;
    private Button btnVolver;
    private BorderPane root;
    private StackPane menuLateral;
    private StackPane contenedorContenido;
    

    /**
     * Constructor que inicializa la vista de historial de jugador específico.
     * Carga el archivo FXML, inicializa componentes y configura la presentación
     * de datos históricos y estadísticas del jugador especificado.
     * 
     * @pre controlador no debe ser null y debe estar correctamente inicializado,
     *      nombreJugador no debe ser null ni vacío y debe existir en el sistema.
     * @param controlador Controlador que maneja la lógica de gestión de jugadores
     * @param nombreJugador Nombre del jugador cuyo historial se va a mostrar
     * @post Se crea una nueva instancia con todos los componentes configurados
     *       y los datos del jugador cargados y presentados correctamente.
     * @throws NullPointerException si controlador o nombreJugador son null
     * @throws IllegalArgumentException si nombreJugador está vacío
     */    
    public HistorialJugadorView(ControladorJugadoresView controlador, String nombreJugador) {
        this.controlador = controlador;
        this.nombreJugador = nombreJugador;
        cargarVista();
    }
    

    /**
     * Carga la vista FXML de historial de jugador.
     * Inicializa el archivo FXML, aplica estilos CSS, obtiene referencias seguras
     * a todos los componentes y configura eventos y presentación de datos.
     * 
     * @pre El archivo FXML debe existir en la ruta especificada.
     * @post La vista FXML se carga correctamente con todos los estilos aplicados,
     *       componentes referenciados de forma segura, ListView estilizada y
     *       datos del jugador cargados automáticamente.
     * @throws IOException si hay errores al cargar el archivo FXML
     */    
    private void cargarVista() {
        try {
            // Cargar FXML
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/views/historial-jugador-view.fxml"));
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
            
            Object tempLblNombreJugador = view.lookup("#lblNombreJugador");
            if (tempLblNombreJugador instanceof Label) {
                lblNombreJugador = (Label) tempLblNombreJugador;
            }
            
            Object tempLblPuntuacionTotal = view.lookup("#lblPuntuacionTotal");
            if (tempLblPuntuacionTotal instanceof Label) {
                lblPuntuacionTotal = (Label) tempLblPuntuacionTotal;
            }
            
            Object tempLblPuntuacionMaxima = view.lookup("#lblPuntuacionMaxima");
            if (tempLblPuntuacionMaxima instanceof Label) {
                lblPuntuacionMaxima = (Label) tempLblPuntuacionMaxima;
            }
            
            Object tempLblPuntuacionMedia = view.lookup("#lblPuntuacionMedia");
            if (tempLblPuntuacionMedia instanceof Label) {
                lblPuntuacionMedia = (Label) tempLblPuntuacionMedia;
            }
            
            Object tempLblPartidasJugadas = view.lookup("#lblPartidasJugadas");
            if (tempLblPartidasJugadas instanceof Label) {
                lblPartidasJugadas = (Label) tempLblPartidasJugadas;
            }
            
            Object tempLblVictorias = view.lookup("#lblVictorias");
            if (tempLblVictorias instanceof Label) {
                lblVictorias = (Label) tempLblVictorias;
            }
            
            Object tempLblRatioVictorias = view.lookup("#lblRatioVictorias");
            if (tempLblRatioVictorias instanceof Label) {
                lblRatioVictorias = (Label) tempLblRatioVictorias;
            }
            
            Object tempListaHistorial = view.lookup("#listaHistorial");
            if (tempListaHistorial instanceof ListView<?>) {
                @SuppressWarnings("unchecked")
                ListView<String> castedListView = (ListView<String>) tempListaHistorial;
                listaHistorial = castedListView;
            }
            
            Object tempBtnVolver = view.lookup("#btnVolver");
            if (tempBtnVolver instanceof Button) {
                btnVolver = (Button) tempBtnVolver;
            }

            Object temproot = view.lookup("#root");
            if (temproot instanceof BorderPane) {
                root = (BorderPane) temproot;
            }
            
            Object tempmenuLateral = view.lookup("#menuLateral");
            if (tempmenuLateral instanceof StackPane) {
                menuLateral = (StackPane) tempmenuLateral;
            }

            Object tempcontenedorContenido = view.lookup("#contenedorContenido");
            if (tempcontenedorContenido instanceof StackPane) {
                contenedorContenido = (StackPane) tempcontenedorContenido;
            }
            
            if (btnVolver != null) {
                btnVolver.getStyleClass().add("btn-effect");
            }
            
            estilizarListView();
            
            if (btnVolver != null) {
                btnVolver.setOnAction(e -> controlador.mostrarVistaGestionJugadores());
            }
            
            cargarDatosJugador();

            aplicarTema();

            cambiarColorTextoPorTema(root, controlador.getTema());
            
        } catch (IOException e) {
            e.printStackTrace();
            controlador.mostrarAlerta("error", "Error", "No se pudo cargar la vista de historial del jugador");
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
     * Aplica estilos visuales modernos y personalizados a la ListView.
     * Configura la apariencia, comportamiento y factory de celdas para crear
     * una lista estilizada sin interactividad de selección.
     * 
     * @pre listaHistorial debe estar inicializada.
     * @post La ListView queda completamente estilizada con apariencia moderna,
     *       efectos de sombra aplicados, celdas personalizadas configuradas
     *       y modo de selección deshabilitado para uso informativo.
     */
    private void estilizarListView() {
        if (listaHistorial != null) {
            // Deshabilitar la selección de manera segura
            listaHistorial.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            listaHistorial.getSelectionModel().clearSelection();
            listaHistorial.setFocusTraversable(false);
            
            // Aplicar estilos a la ListView
            listaHistorial.setStyle("{-fx-background-color: #f8f9fa; " +
                                   "-fx-border-color: #e0e0e0; " + 
                                   "-fx-border-radius: 8; " +
                                   "-fx-background-radius: 8; " +
                                   "-fx-padding: 5; }");
            
            // Aplicar efecto de sombra
            DropShadow dropShadow = new DropShadow();
            dropShadow.setColor(Color.rgb(0, 0, 0, 0.05));
            dropShadow.setRadius(4);
            dropShadow.setOffsetY(1);
            listaHistorial.setEffect(dropShadow);
            
            // Establecer altura de celdas
            listaHistorial.setFixedCellSize(40);
            
            // Factory personalizado para las celdas (sin hover ni selección)
            listaHistorial.setCellFactory(param -> new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                        setStyle("-fx-background-color: transparent; " +
                                "-fx-border-color: transparent;");
                    } else {
                        setText(item);
                        setStyle("-fx-background-color: transparent; " +
                                "-fx-padding: 8 12 8 12; " +
                                "-fx-border-color: transparent transparent #f0f0f0 transparent; " +
                                "-fx-font-size: 14px; " +
                                "-fx-text-fill: #303f9f;");
                    }
                }
            });
            
            URL scrollStyleUrl = getClass().getResource("/styles/button.css");
            if (scrollStyleUrl != null) {
                listaHistorial.getStylesheets().add(scrollStyleUrl.toExternalForm());
            }
            
            try {
                if (listaHistorial.lookup(".scroll-bar:vertical") != null) {
                    listaHistorial.lookup(".scroll-bar:vertical").setStyle("-fx-background-color: transparent; -fx-pref-width: 12;");
                }
                
                if (listaHistorial.lookup(".scroll-bar:vertical .thumb") != null) {
                    listaHistorial.lookup(".scroll-bar:vertical .thumb").setStyle("-fx-background-color: #bbbbbb; -fx-background-radius: 6;");
                }
            } catch (Exception e) {
                controlador.mostrarAlerta("error", "Error inesperado", "No se ha podido cargar la vista");
            }
        }
    }
    


    /**
     * Carga y presenta todos los datos estadísticos del jugador seleccionado.
     * Obtiene información completa del controlador y actualiza todos los
     * componentes de la interfaz con los datos correspondientes.
     * 
     * @pre nombreJugador debe existir en el sistema y todos los labels
     *      deben estar inicializados correctamente.
     * @post Todos los labels de estadísticas muestran información actualizada
     *       del jugador, la lista de historial se llena con todas las puntuaciones
     *       registradas o muestra mensaje informativo si no hay datos.
     */
    private void cargarDatosJugador() {
        // Establecer nombre del jugador
        if (lblNombreJugador != null) {
            lblNombreJugador.setText("Información de jugador: " + nombreJugador);
        }
        
        // Obtener estadísticas del jugador desde el controlador
        EstadisticasJugador stats = controlador.getEstadisticasJugador(nombreJugador);
        
        if (lblPuntuacionTotal != null) {
            lblPuntuacionTotal.setText(String.valueOf(stats.getPuntuacionTotal()));
        }
        
        if (lblPuntuacionMaxima != null) {
            lblPuntuacionMaxima.setText(String.valueOf(stats.getPuntuacionMaxima()));
        }
        
        if (lblPuntuacionMedia != null) {
            lblPuntuacionMedia.setText(String.valueOf(stats.getPuntuacionMedia()));
        }
        
        if (lblPartidasJugadas != null) {
            lblPartidasJugadas.setText(String.valueOf(stats.getPartidasJugadas()));
        }
        
        if (lblVictorias != null) {
            lblVictorias.setText(String.valueOf(stats.getVictorias()));
        }
        
        if (lblRatioVictorias != null) {
            lblRatioVictorias.setText(stats.getRatioVictorias());
        }
        
        // Cargar historial de puntuaciones
        if (listaHistorial != null) {
            listaHistorial.getItems().clear();
            List<Integer> historial = controlador.getPuntuacionesUsuario(nombreJugador);
            if (historial.isEmpty()) {
                listaHistorial.setPlaceholder(new Label("El jugador no tiene partidas registradas"));
            } else {
                int partidaNum = 1;
                for (Integer puntuacion : historial) {
                    listaHistorial.getItems().add("Partida " + partidaNum + ": " + puntuacion + " pts");
                    partidaNum++;
                }
            }
        }
    }


    /**
     * Obtiene la vista cargada de historial de jugador.
     * Proporciona acceso a la jerarquía de componentes JavaFX
     * para su integración en la aplicación principal.
     * 
     * @pre La vista debe haber sido cargada previamente.
     * @return Parent que contiene la vista completa de historial del jugador
     * @post Se devuelve la referencia a la vista cargada sin modificar su estado.
     */
    public Parent getView() {
        return view;
    }
}