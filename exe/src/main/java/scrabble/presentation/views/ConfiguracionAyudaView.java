package scrabble.presentation.views;

import java.io.IOException;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import scrabble.MainApplication;
import scrabble.presentation.viewControllers.ControladorConfiguracionView;


/**
 * Vista principal del menú de configuración y ayuda del juego Scrabble.
 * Esta clase proporciona la interfaz de usuario para acceder a las diferentes
 * secciones de configuración del juego, incluyendo configuración general,
 * configuración de partida, ayuda y contacto.
 * 
 * La vista implementa un menú navegable con efectos visuales que mejoran
 * la experiencia del usuario. Cada botón del menú proporciona acceso directo
 * a su sección correspondiente mediante el controlador asociado.
 * 
 * Características principales:
 * - Menú principal de navegación con botones estilizados
 * - Efectos de hover interactivos para mejorar la experiencia visual
 * - Integración completa con el controlador de configuración
 * - Carga automática de vista FXML con manejo de errores
 * - Aplicación consistente de estilos CSS
 * - Navegación fluida entre diferentes secciones de configuración
 * 
 * La vista utiliza el patrón MVC, delegando toda la lógica de navegación
 * y configuración al ControladorConfiguracionView. Implementa animaciones
 * CSS y JavaFX para proporcionar retroalimentación visual inmediata
 * a las interacciones del usuario.
 * 
 * @version 1.0
 * @since 1.0
 */
public class ConfiguracionAyudaView {
    
    private final ControladorConfiguracionView controlador;
    private Parent view;
    
    // Componentes de la interfaz
    @FXML
    private Button btnGeneral;
    
    @FXML
    private Button btnPartida;
    
    @FXML
    private Button btnAyuda;
    
    @FXML
    private Button btnVolver;
    
    @FXML
    private Button btnContacto;

    @FXML
    private BorderPane root;

    @FXML
    private VBox menuLateral;

    @FXML
    private StackPane contenedorContenido;    
    

    /**
     * Constructor que inicializa la vista de configuración y ayuda.
     * Recibe el controlador necesario para manejar la navegación entre
     * las diferentes secciones de configuración y carga automáticamente
     * la vista FXML correspondiente.
     * 
     * @pre controlador no debe ser null y debe estar correctamente inicializado.
     * @param controlador Controlador que maneja la lógica de navegación y configuración
     * @post Se crea una nueva instancia con la vista FXML cargada y todos los
     *       componentes inicializados con sus efectos visuales correspondientes.
     * @throws NullPointerException si controlador es null
     */    
    public ConfiguracionAyudaView(ControladorConfiguracionView controlador) {
        this.controlador = controlador;
        cargarVista();
    }
    

    /**
     * Carga la vista FXML de configuración y ayuda desde el archivo correspondiente.
     * Inicializa el FXMLLoader, establece el controlador de la vista,
     * carga el archivo FXML y configura todos los componentes visuales.
     * 
     * @pre El archivo FXML debe existir en la ruta especificada.
     * @post La vista FXML se carga correctamente, se establece como controlador
     *       de la vista y se inicializan todos los componentes con sus estilos
     *       y efectos correspondientes.
     * @throws IOException si hay errores al cargar el archivo FXML
     */    
    private void cargarVista() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/views/configuracion-ayuda.fxml"));
            loader.setController(this);
            view = loader.load();
            aplicarTema();
            inicializarComponentes();
            cambiarColorTextoPorTema(root, controlador.getTema());
            controlador.temaProperty().addListener((obs, oldVal, newVal) -> {
                aplicarTema(); 
                cambiarColorTextoPorTema(root, controlador.getTema());
            }); 
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar vista: " + e.getMessage());
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
     * Inicializa todos los componentes visuales de la interfaz.
     * Aplica las clases CSS correspondientes a cada botón, configura
     * los efectos de hover y establece los manejadores de eventos necesarios.
     * 
     * @pre Los componentes FXML deben haber sido inyectados correctamente.
     * @post Todos los botones tienen aplicadas sus clases CSS, efectos de hover
     *       configurados y manejadores de eventos establecidos. El botón de
     *       contacto tiene configurado su evento específico.
     */    
    private void inicializarComponentes() {
        // Aplicar efectos a botones de menú
        if (btnGeneral != null) {
            btnGeneral.getStyleClass().add("config-menu-button");
            addHoverEffect(btnGeneral);
        }
        if (btnPartida != null) {
            btnPartida.getStyleClass().add("config-menu-button");
            addHoverEffect(btnPartida);
        }
        if (btnAyuda != null) {
            btnAyuda.getStyleClass().add("config-menu-button");
            addHoverEffect(btnAyuda);
        }
        if (btnVolver != null) {
            btnVolver.getStyleClass().add("config-menu-button");
            addHoverEffect(btnVolver);
        }
        
        // Configurar botón de contacto
        if (btnContacto != null) {
            btnContacto.setOnAction(this::onContactoClick);
            btnContacto.getStyleClass().add("btn-effect");
        }
    }
    



    /**
     * Añade efectos visuales de hover a un botón específico.
     * Configura animaciones de escala y translación que se ejecutan
     * cuando el mouse entra y sale del área del botón, proporcionando
     * retroalimentación visual inmediata al usuario.
     * 
     * @pre button no debe ser null.
     * @param button Botón al que se aplicarán los efectos de hover
     * @post El botón tiene configuradas animaciones de entrada y salida
     *       que se ejecutan automáticamente al hacer hover. Las animaciones
     *       incluyen escalado (105%) y desplazamiento horizontal (5px).
     */
    private void addHoverEffect(Button button) {
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), button);
        scaleIn.setToX(1.05);
        scaleIn.setToY(1.05);
        
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), button);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);
        
        TranslateTransition translateIn = new TranslateTransition(Duration.millis(200), button);
        translateIn.setToX(5);
        
        TranslateTransition translateOut = new TranslateTransition(Duration.millis(200), button);
        translateOut.setToX(0);
        
        button.setOnMouseEntered(e -> {
            scaleIn.play();
            translateIn.play();
        });
        
        button.setOnMouseExited(e -> {
            scaleOut.play();
            translateOut.play();
        });
    }
    

    /**
     * Maneja el evento de clic en el botón de configuración general.
     * Delega al controlador la navegación hacia la vista de configuración
     * general del juego, donde se pueden modificar preferencias globales.
     * 
     * @pre El controlador debe estar inicializado.
     * @param event Evento de acción generado por el clic del botón
     * @post Se navega hacia la vista de configuración general a través
     *       del controlador, manteniendo el estado actual de la aplicación.
     */    
    @FXML
    public void onGeneralClick(ActionEvent event) {
        controlador.mostrarVistaGeneral();
    }
    

    /**
     * Maneja el evento de clic en el botón de configuración de partida.
     * Delega al controlador la navegación hacia la vista de configuración
     * específica de partida, donde se pueden establecer parámetros de juego.
     * 
     * @pre El controlador debe estar inicializado.
     * @param event Evento de acción generado por el clic del botón
     * @post Se navega hacia la vista de configuración de partida a través
     *       del controlador, permitiendo al usuario modificar parámetros específicos.
     */    
    @FXML
    public void onPartidaClick(ActionEvent event) {
        controlador.mostrarVistaPartida();
    }
    

    /**
     * Maneja el evento de clic en el botón de ayuda.
     * Delega al controlador la navegación hacia la vista de ayuda,
     * donde se proporciona información sobre cómo usar el juego.
     * 
     * @pre El controlador debe estar inicializado.
     * @param event Evento de acción generado por el clic del botón
     * @post Se navega hacia la vista de ayuda a través del controlador,
     *       proporcionando acceso a la documentación y guías del juego.
     */    
    @FXML
    public void onAyudaClick(ActionEvent event) {
        controlador.mostrarVistaAyuda();
    }
    

    /**
     * Maneja el evento de clic en el botón de volver.
     * Delega al controlador la navegación de regreso al menú principal
     * de la aplicación, cerrando la vista actual de configuración.
     * 
     * @pre El controlador debe estar inicializado.
     * @param event Evento de acción generado por el clic del botón
     * @post Se navega de vuelta al menú principal a través del controlador,
     *       cerrando la vista actual y regresando al estado anterior.
     */    
    @FXML
    public void onVolverClick(ActionEvent event) {
        controlador.volverAMenuPrincipal();
    }
    

    /**
     * Maneja el evento de clic en el botón de contacto.
     * Delega al controlador la acción de mostrar información de contacto,
     * proporcionando al usuario formas de comunicarse con el soporte.
     * 
     * @pre El controlador debe estar inicializado.
     * @param event Evento de acción generado por el clic del botón
     * @post Se muestra la información de contacto a través del controlador,
     *       permitiendo al usuario acceder a canales de comunicación disponibles.
     */    
    @FXML
    public void onContactoClick(ActionEvent event) {
        controlador.mostrarInformacionContacto();
    }


    /**
     * Obtiene la vista cargada de configuración y ayuda.
     * Proporciona acceso a la jerarquía completa de componentes JavaFX
     * para su integración en la aplicación principal.
     * 
     * @pre La vista debe haber sido cargada previamente mediante cargarVista().
     * @return Parent que contiene la vista completa de configuración y ayuda
     * @post Se devuelve la referencia a la vista cargada sin modificar
     *       su estado actual ni sus componentes internos.
     */    
    public Parent getView() {
        return view;
    }
}