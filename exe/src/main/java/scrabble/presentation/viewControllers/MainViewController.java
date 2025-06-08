package scrabble.presentation.viewControllers;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import scrabble.presentation.PresentationController;


/**
 * Controlador principal del menú de la aplicación Scrabble.
 * Gestiona la navegación entre las principales funcionalidades del sistema
 * y coordina la presentación visual adaptativa según temas configurados,
 * implementando el patrón Singleton para control centralizado.
 * 
 * Características principales:
 * - Navegación centralizada hacia todas las secciones principales
 * - Adaptación automática de estilos según tema activo (claro/oscuro)
 * - Efectos visuales interactivos con animaciones hover personalizadas
 * - Configuración responsiva de imagen de fondo adaptable a ventana
 * - Integración con controladores especializados de cada sección
 * - Manejo de eventos de cierre de aplicación con limpieza apropiada
 * 
 * @version 1.0
 * @since 1.0
 */
public class MainViewController {

    // singleton instance
    private static MainViewController instance;
    
    /**
     * Obtiene la instancia única del controlador del menú principal.
     * Implementa el patrón Singleton para garantizar control centralizado
     * de la navegación y estado del menú principal durante la aplicación.
     * 
     * @pre No hay precondiciones específicas para obtener la instancia.
     * @return La instancia única de MainViewController
     * @post Se devuelve la instancia única, creándola si es la primera invocación.
     */
    public static MainViewController getInstance() {
        if (instance == null) {
            instance = new MainViewController();
        }
        return instance;
    }

    @FXML
    private Button btnGestionJugadores;
    
    @FXML
    private Button btnGestionDiccionarios;
    
    @FXML
    private Button btnGestionPartidas;
    
    @FXML
    private Button btnGestionRankings;
    
    @FXML
    private Button btnConfiguracion;
    
    @FXML
    private Button btnSalir;
    
    @FXML
    private ImageView backgroundImage;
    
    @FXML StackPane overlay;

    private PresentationController presentationController;
    

    /**
     * Inicializa el controlador y configura la interfaz del menú principal.
     * Establece la conexión con el controlador de presentación, configura
     * imagen responsiva, aplica tema actual y añade efectos hover a botones.
     * 
     * @pre Los componentes FXML deben haber sido inyectados correctamente.
     * @post El controlador queda completamente inicializado con imagen de fondo
     *       responsiva, tema aplicado según configuración y efectos hover activos.
     */
    @FXML
    public void initialize() {
        presentationController = PresentationController.getInstance();
        MainViewController.instance = this;
        
        // Configurar imagen de fondo para que ocupe toda la pantalla
        configurarImagenResponsiva();
        configurarTemaOverlay();
           // Agregar efectos hover a los botones
        addHoverEffect(btnGestionJugadores);
        addHoverEffect(btnGestionDiccionarios);
        addHoverEffect(btnGestionPartidas);
        addHoverEffect(btnGestionRankings);
        addHoverEffect(btnConfiguracion);
        addHoverEffect(btnSalir);            
    }
    


    /**
     * Configura el tema visual del overlay según la configuración actual.
     * Aplica colores, transparencias y estilos apropiados para el tema
     * activo (claro/oscuro) al overlay principal y actualiza botones.
     * 
     * @pre overlay y presentationController deben estar inicializados.
     * @post El overlay se configura con colores y estilos apropiados para
     *       el tema actual, y los botones se actualizan con estilos coherentes.
     */
    private void configurarTemaOverlay() {
        if (overlay != null && presentationController != null) {
            String tema = presentationController.getTema();
            
            String colorOverlay;
            String colorTexto;
            String colorBorde;
            
            if ("OSCURO".equalsIgnoreCase(tema)) {
                colorOverlay = "rgba(42, 78, 88, 0.9)"; 
                colorTexto = "#f8f8f8"; 
                colorBorde = "rgba(149, 165, 166, 0.3)";                 
            } else {
               
                colorOverlay = "rgba(255, 255, 255, 0.85)"; 
                colorTexto = "#2c3e50"; 
                colorBorde = "rgba(52, 152, 219, 0.3)"; 
                
            }
            
            // Aplicar el estilo al overlay
            String estiloOverlay = String.format(
                "-fx-background-color: %s; -fx-background-radius: 20;", 
                colorOverlay
            );
            overlay.setStyle(estiloOverlay);
            actualizarEstilosBotonesPorTema(tema, colorTexto, colorBorde);
            
        } 
    }


    /**
     * Actualiza los estilos de botones según el tema visual activo.
     * Aplica estilos específicos de tema a botones secundarios cuando
     * el tema oscuro está activo para mantener consistencia visual.
     * 
     * @pre tema, colorTexto y colorBorde no deben ser null.
     * @param tema Tema visual activo ("OSCURO" o "CLARO")
     * @param colorTexto Color del texto para el tema actual
     * @param colorBorde Color del borde para el tema actual
     * @post Los botones secundarios tienen estilos actualizados si el tema es oscuro.
     */
    private void actualizarEstilosBotonesPorTema(String tema, String colorTexto, String colorBorde) {
        if ("OSCURO".equalsIgnoreCase(tema)) {
            actualizarEstiloBotonSecundario(btnGestionJugadores, colorTexto, colorBorde);
            actualizarEstiloBotonSecundario(btnGestionDiccionarios, colorTexto, colorBorde);
            actualizarEstiloBotonSecundario(btnGestionRankings, colorTexto, colorBorde);
            actualizarEstiloBotonSecundario(btnConfiguracion, colorTexto, colorBorde);
        }
    }
    

    /**
     * Actualiza el estilo visual de un botón secundario específico.
     * Aplica configuración completa de estilo incluyendo colores, bordes,
     * efectos de sombra y propiedades de cursor para tema oscuro.
     * 
     * @pre boton, colorTexto y colorBorde no deben ser null.
     * @param boton Botón al que aplicar el nuevo estilo
     * @param colorTexto Color del texto a aplicar
     * @param colorBorde Color del borde a aplicar
     * @post El botón tiene aplicado el estilo completo para tema oscuro
     *       con todos los efectos visuales configurados.
     */
    private void actualizarEstiloBotonSecundario(Button boton, String colorTexto, String colorBorde) {
        if (boton != null) {
            String nuevoEstilo = String.format(
                "-fx-background-color: rgba(52, 73, 94, 0.90); " + // Fondo oscuro
                "-fx-text-fill: %s; " + 
                "-fx-font-size: 14px; " +
                "-fx-font-weight: 500; " +
                "-fx-background-radius: 25; " +
                "-fx-border-radius: 25; " +
                "-fx-border-color: %s; " + 
                "-fx-border-width: 2; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0, 0, 2); " +
                "-fx-cursor: hand;",
                colorTexto, colorBorde
            );
            
            boton.setStyle(nuevoEstilo);
        }
    }

    /**
     * Configura la imagen de fondo para comportamiento responsivo.
     * Establece bindings para que la imagen se adapte automáticamente
     * al tamaño de la ventana manteniendo calidad y rendimiento óptimos.
     * 
     * @pre backgroundImage debe estar inicializada correctamente.
     * @post La imagen de fondo se adapta automáticamente al tamaño de ventana
     *       con propiedades de suavizado y cache activadas para rendimiento.
     */
    private void configurarImagenResponsiva() {
        if (backgroundImage != null) {
            backgroundImage.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {

                    backgroundImage.fitWidthProperty().bind(newScene.widthProperty());
                    backgroundImage.fitHeightProperty().bind(newScene.heightProperty());
                    backgroundImage.setPreserveRatio(false); 
                    backgroundImage.setSmooth(true);          
                    backgroundImage.setCache(true);           
                    
                }
            });
        }
    }

    /**
     * Añade efectos hover interactivos modernos a un botón específico.
     * Configura animaciones de escala y traslación que se ejecutan
     * cuando el mouse entra y sale del área del botón.
     * 
     * @pre button no debe ser null.
     * @param button Botón al que aplicar los efectos hover
     * @post El botón tiene configuradas animaciones de escala (103%) y traslación
     *       vertical (-2px) que se ejecutan suavemente al hacer hover, excepto
     *       el botón salir que solo tiene efecto de escala.
     */
    public void addHoverEffect(Button button) {
        // Efectos de escala
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), button);
        scaleIn.setToX(1.03);
        scaleIn.setToY(1.03);
        
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), button);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);
        
        // Efectos de traslación (excepto para el botón salir)
        TranslateTransition translateIn = new TranslateTransition(Duration.millis(200), button);
        translateIn.setToY(-2);
        
        TranslateTransition translateOut = new TranslateTransition(Duration.millis(200), button);
        translateOut.setToY(0);
        
        // Aplicar efectos
        button.setOnMouseEntered(e -> {
            scaleIn.play();
            if (button != btnSalir) {
                translateIn.play();
            }
        });
        
        button.setOnMouseExited(e -> {
            scaleOut.play();
            if (button != btnSalir) {
                translateOut.play();
            }
        });
    }    
    

    /**
     * Maneja el evento de clic en el botón de Gestión de Jugadores.
     * Navega hacia la vista de gestión de jugadores creando el controlador
     * apropiado y preservando la vista actual para navegación de retorno.
     * 
     * @pre btnGestionJugadores debe estar correctamente inicializado en FXML.
     * @param event Evento de acción generado por el clic del botón
     * @post Se navega a la vista de gestión de jugadores con vista anterior
     *       preservada para posible retorno al menú principal.
     */
    @FXML
    private void onGestionJugadoresClick(ActionEvent event) {
        Stage stage = (Stage) btnGestionJugadores.getScene().getWindow();
        Parent vistaActual = stage.getScene().getRoot();
        
        ControladorJugadoresView controlador = new ControladorJugadoresView(stage);
        controlador.setVistaAnterior(vistaActual);        
    }
    

    /**
     * Maneja el evento de clic en el botón de Gestión de Diccionarios.
     * Navega hacia la vista de gestión de diccionarios obteniendo la instancia
     * singleton del controlador y configurando navegación de retorno.
     * 
     * @pre btnGestionDiccionarios debe estar correctamente inicializado en FXML.
     * @param event Evento de acción generado por el clic del botón
     * @post Se navega a la vista de gestión de diccionarios con controlador
     *       singleton inicializado y vista anterior preservada.
     */
    @FXML
    private void onGestionDiccionariosClick(ActionEvent event) {
        Stage stage = (Stage) btnGestionDiccionarios.getScene().getWindow();
        Parent vistaActual = stage.getScene().getRoot();
    
        ControladorDiccionarioView controlador = ControladorDiccionarioView.getInstance();
        controlador.setVistaAnterior(vistaActual);
        controlador.inicializar(stage);
    }
    

    /**
     * Maneja el evento de clic en el botón de Gestión de Partidas.
     * Navega hacia la vista de gestión de partidas creando el controlador
     * de partidas y estableciendo la vista actual como anterior.
     * 
     * @pre btnGestionPartidas debe estar correctamente inicializado en FXML.
     * @param event Evento de acción generado por el clic del botón
     * @post Se navega a la vista de gestión de partidas con controlador
     *       configurado y navegación de retorno establecida.
     */
    @FXML
    private void onGestionPartidasClick(ActionEvent event) {
        Stage stage = (Stage) btnGestionPartidas.getScene().getWindow();
        Parent vistaActual = stage.getScene().getRoot();
        
        ControladorPartidaView controladorPartidas = new ControladorPartidaView(stage);
        controladorPartidas.setVistaAnterior(vistaActual);
    }
    

    /**
     * Maneja el evento de clic en el botón de Gestión de Rankings.
     * Navega hacia la vista de gestión de rankings obteniendo la instancia
     * singleton del controlador y configurando estado para retorno.
     * 
     * @pre btnGestionRankings debe estar correctamente inicializado en FXML.
     * @param event Evento de acción generado por el clic del botón
     * @post Se navega a la vista de gestión de rankings con controlador
     *       singleton inicializado y vista anterior preservada.
     */
    @FXML
    private void onGestionRankingsClick(ActionEvent event) {
        Stage stage = (Stage) btnGestionRankings.getScene().getWindow();
        Parent vistaActual = stage.getScene().getRoot();
        ControladorRankingView controladorRanking = ControladorRankingView.getInstance();
        controladorRanking.setVistaAnterior(vistaActual);
        controladorRanking.inicializar(stage);
    }
    

    /**
     * Maneja el evento de clic en el botón de Configuración.
     * Navega hacia la vista de configuración obteniendo la instancia singleton
     * del controlador de configuración y estableciendo navegación de retorno.
     * 
     * @pre btnConfiguracion debe estar correctamente inicializado en FXML.
     * @param event Evento de acción generado por el clic del botón
     * @post Se navega a la vista de configuración con controlador singleton
     *       inicializado y vista anterior preservada para retorno.
     */
    @FXML
    private void onConfiguracionClick(ActionEvent event) {
        Stage stage = (Stage) btnConfiguracion.getScene().getWindow();
        Parent vistaActual = stage.getScene().getRoot();
        ControladorConfiguracionView ctrlConfig = ControladorConfiguracionView.getInstance();
        ctrlConfig.setVistaAnterior(vistaActual);            
        ctrlConfig.initialize(stage);
    }
    

    /**
     * Maneja el evento de clic en el botón de Salir de la aplicación.
     * Cierra la ventana principal de la aplicación finalizando la ejecución
     * del programa de manera controlada.
     * 
     * @pre btnSalir debe estar correctamente inicializado en FXML.
     * @param event Evento de acción generado por el clic del botón
     * @post La ventana principal se cierra y la aplicación termina su ejecución.
     */
    @FXML
    private void onSalirClick(ActionEvent event) {
        Stage stage = (Stage) btnSalir.getScene().getWindow();
        stage.close();
    }
    

    /**
     * Maneja el evento de cierre de la ventana principal de la aplicación.
     * Ejecuta limpieza apropiada y finaliza la aplicación de manera segura
     * cuando se cierra la ventana por cualquier medio del sistema.
     * 
     * @pre event debe ser un evento válido de cierre de ventana.
     * @param event Evento de cierre de ventana generado por el sistema
     * @post La aplicación se cierra completamente con código de salida 0.
     */
    public void handleCloseEvent(WindowEvent event) {
        System.exit(0);
    }
}