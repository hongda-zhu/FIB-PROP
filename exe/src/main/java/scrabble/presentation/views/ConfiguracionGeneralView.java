package scrabble.presentation.views;

import java.io.IOException;
import java.util.Map;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import scrabble.MainApplication;
import scrabble.presentation.componentes.MusicManager;
import scrabble.presentation.componentes.SoundEffects;
import scrabble.presentation.viewControllers.ControladorConfiguracionView;


/**
 * Vista de configuración general del juego Scrabble.
 * Esta clase proporciona la interfaz de usuario para configurar las preferencias
 * generales de la aplicación, incluyendo tema visual, configuración de audio,
 * control de volumen y otras opciones de personalización global.
 * 
 * La vista permite al usuario modificar configuraciones que afectan a toda
 * la aplicación de manera persistente, proporcionando controles intuitivos
 * para ajustar la experiencia de juego según las preferencias personales.
 * 
 * Características principales:
 * - Selección de tema visual (claro/oscuro) mediante ComboBox
 * - Controles de toggle personalizados para música y efectos de sonido
 * - Sliders de volumen con retroalimentación visual en tiempo real
 * - Botones de guardar y restablecer configuración
 * - Efectos de hover interactivos en elementos de navegación
 * - Integración completa con sistemas de audio (MusicManager y SoundEffects)
 * - Carga y persistencia automática de configuraciones
 * - Validación de entrada y manejo de errores robusto
 * - Aplicación inmediata de cambios de configuración
 * 
 * La vista utiliza el patrón MVC, comunicándose con ControladorConfiguracionView
 * para todas las operaciones de persistencia y validación. Implementa
 * controles personalizados con estilos CSS y animaciones JavaFX para
 * proporcionar una experiencia de usuario moderna y responsiva.
 * 
 * Los cambios de configuración se aplican inmediatamente a los sistemas
 * correspondientes (audio, tema, etc.) y se pueden guardar de forma
 * persistente o restablecer a valores por defecto según sea necesario.
 * 
 * @version 1.0
 * @since 1.0
 */
public class ConfiguracionGeneralView {
    
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
    private Button btnGuardar;
    
    @FXML
    private Button btnRestablecer;
    
    @FXML
    private ComboBox<String> comboTema;
    
    @FXML
    private ToggleButton toggleMusica;
    
    @FXML
    private ToggleButton toggleSonido;
    
    @FXML
    private Label lblEstadoMusica;
    
    @FXML
    private Label lblEstadoSonido;
    
    @FXML
    private HBox toggleBgMusica;
    
    @FXML
    private StackPane toggleCircleMusica;
    
    @FXML
    private HBox toggleBgSonido;
    
    @FXML
    private StackPane toggleCircleSonido;

    @FXML
    private Slider sliderMusica;

    @FXML
    private Slider sliderSonido;

    @FXML 
    private Label labelVolumenMusica;

    @FXML
    private Label labelVolumenSonido;

    @FXML 
    private BorderPane root;

    @FXML 
    private VBox menuLateral;

    @FXML
    private StackPane contenedorConfiguración;
    

    /**
     * Constructor que inicializa la vista de configuración general.
     * Recibe el controlador necesario para manejar la persistencia y
     * validación de configuraciones, y carga automáticamente la vista
     * FXML con todos sus componentes y configuraciones iniciales.
     * 
     * @pre controlador no debe ser null y debe estar correctamente inicializado.
     * @param controlador Controlador que maneja la lógica de configuración general
     * @post Se crea una nueva instancia con la vista FXML cargada, todos los
     *       componentes inicializados con valores actuales de configuración,
     *       efectos visuales aplicados y listeners de eventos configurados.
     * @throws NullPointerException si controlador es null
     */    
    public ConfiguracionGeneralView(ControladorConfiguracionView controlador) {
        this.controlador = controlador;
        cargarVista();
    }
    

    /**
     * Carga la vista FXML de configuración general desde el archivo correspondiente.
     * Inicializa el FXMLLoader, establece el controlador de la vista,
     * carga el archivo FXML y configura todos los componentes con sus
     * valores actuales y manejadores de eventos.
     * 
     * @pre El archivo FXML debe existir en la ruta especificada.
     * @post La vista FXML se carga correctamente, se establece como controlador
     *       de la vista y se inicializan todos los componentes con la configuración
     *       actual del sistema y sus efectos visuales correspondientes.
     * @throws IOException si hay errores al cargar el archivo FXML
     */    
    private void cargarVista() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/views/configuracion-general.fxml"));
            loader.setController(this);
            view = loader.load(); 

            aplicarTema();
            inicializarComponentes();
            cambiarColorTextoPorTema(root, controlador.getTema());
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
        aplicarFondoPorTema(contenedorConfiguración, controlador.getTema(), "#ffffff", "#1c1747");
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
     * Inicializa todos los componentes visuales y funcionales de la interfaz.
     * Aplica clases CSS, configura efectos de hover, carga configuración actual,
     * establece valores iniciales en controles y configura listeners de eventos
     * para sliders y otros elementos interactivos.
     * 
     * @pre Los componentes FXML deben haber sido inyectados correctamente
     *      y el controlador debe estar inicializado.
     * @post Todos los botones tienen efectos de hover, el ComboBox se llena con
     *       opciones de tema, los toggles reflejan el estado actual de audio,
     *       los sliders muestran volúmenes actuales con labels actualizados,
     *       y todos los listeners están configurados para cambios en tiempo real.
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
        
        // Configurar botones de acción
        if (btnRestablecer != null) {
            btnRestablecer.setOnAction(this::onRestablecerClick);
            btnRestablecer.getStyleClass().add("btn-effect");
        }
        
        if (btnGuardar != null) {
            btnGuardar.setOnAction(this::onGuardarClick);
            btnGuardar.getStyleClass().add("btn-effect");
        }

        Map<String, String> config = controlador.cargarConfiguracion();
        
        if (comboTema != null) {
            comboTema.getItems().clear();
            comboTema.getItems().addAll("Claro", "Oscuro");
            comboTema.setValue(config.getOrDefault("tema", "Claro"));
            controlador.actualizarTema(comboTema.getValue());
            aplicarTema();
            comboTema.valueProperty().addListener((obs, oldVal, newVal) -> {
                controlador.actualizarTema(newVal);
                aplicarTema();
                cambiarColorTextoPorTema(root, controlador.getTema());
            });
        }
        
        // Inicializar toggle música
        if (toggleMusica != null) {
            toggleMusica.setSelected(Boolean.parseBoolean(config.getOrDefault("musica", "true")));
            onToggleMusicaClick(null);
        }
        
        // Inicializar toggle sonido
        if (toggleSonido != null) {
            toggleSonido.setSelected(Boolean.parseBoolean(config.getOrDefault("sonido", "true")));
            onToggleSonidoClick(null);
        }

        if (sliderMusica != null) {
            String volumenMusicaStr = config.getOrDefault("volumenMusica", "50");
            try {
                sliderMusica.setValue(Double.parseDouble(volumenMusicaStr));
            } catch (NumberFormatException e) {
                sliderMusica.setValue(50);
            }
        }

        if (sliderSonido != null) {
            String volumenSonidoStr = config.getOrDefault("volumenSonido", "50");
            try {
                sliderSonido.setValue(Double.parseDouble(volumenSonidoStr));
            } catch (NumberFormatException e) {
                sliderSonido.setValue(50);
            }
        }

        if (labelVolumenMusica != null) {
            labelVolumenMusica.setText(String.valueOf((int) sliderMusica.getValue()));
            sliderMusica.valueProperty().addListener((obs, oldVal, newVal) -> {
                labelVolumenMusica.setText(String.valueOf(newVal.intValue()));
                MusicManager.setVolume((double) newVal.intValue()/100.0);
            });
        }

        if (labelVolumenSonido != null) {
            labelVolumenSonido.setText(String.valueOf((int) sliderSonido.getValue()));
            sliderSonido.valueProperty().addListener((obs, oldVal, newVal) -> {
                labelVolumenSonido.setText(String.valueOf(newVal.intValue()));
                SoundEffects.setVolumen((double) newVal.intValue()/100.0);
            });
        }
    }
    


    /**
     * Añade efectos visuales de hover a un botón específico del menú.
     * Configura animaciones de escala y translación que proporcionan
     * retroalimentación visual inmediata cuando el usuario interactúa
     * con los elementos de navegación del menú.
     * 
     * @pre button no debe ser null.
     * @param button Botón de menú al que se aplicarán los efectos de hover
     * @post El botón tiene configuradas animaciones suaves de entrada y salida
     *       que se ejecutan automáticamente al hacer hover. Las animaciones
     *       incluyen escalado (105%) y desplazamiento horizontal (5px) con
     *       duración de 200ms para una transición fluida.
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
     * Aunque esta vista ya representa la configuración general, este método
     * permite refrescar la vista actual o mantener consistencia en la navegación.
     * 
     * @pre El controlador debe estar inicializado.
     * @param event Evento de acción generado por el clic del botón
     * @post Se refresca o mantiene la vista actual de configuración general
     *       a través del controlador, asegurando consistencia en la navegación.
     */    
    @FXML
    public void onGeneralClick(ActionEvent event) {
        controlador.mostrarVistaGeneral();
    }
    

    /**
     * Maneja el evento de clic en el botón de configuración de partida.
     * Delega al controlador la navegación hacia la vista de configuración
     * específica de partida, guardando implícitamente el estado actual.
     * 
     * @pre El controlador debe estar inicializado.
     * @param event Evento de acción generado por el clic del botón
     * @post Se navega hacia la vista de configuración de partida a través
     *       del controlador, manteniendo los cambios actuales en memoria.
     */    
    @FXML
    public void onPartidaClick(ActionEvent event) {
        controlador.mostrarVistaPartida();
    }
    

    /**
     * Maneja el evento de clic en el botón de ayuda.
     * Delega al controlador la navegación hacia la vista de ayuda,
     * proporcionando acceso a documentación y guías de configuración.
     * 
     * @pre El controlador debe estar inicializado.
     * @param event Evento de acción generado por el clic del botón
     * @post Se navega hacia la vista de ayuda a través del controlador,
     *       manteniendo el contexto actual de configuración general.
     */    
    @FXML
    public void onAyudaClick(ActionEvent event) {
        controlador.mostrarVistaAyuda();
    }
    

    /**
     * Maneja el evento de clic en el botón de volver.
     * Delega al controlador la navegación de regreso al menú principal,
     * cerrando la vista de configuración sin guardar cambios automáticamente.
     * 
     * @pre El controlador debe estar inicializado.
     * @param event Evento de acción generado por el clic del botón
     * @post Se navega de vuelta al menú principal a través del controlador,
     *       manteniendo los cambios no guardados en memoria hasta la próxima sesión.
     */    
    @FXML
    public void onVolverClick(ActionEvent event) {
        Map<String, String> config = controlador.cargarConfiguracion();
        if (Boolean.parseBoolean(config.get("musica"))) MusicManager.play();
        else MusicManager.pause();
        MusicManager.setVolume(Double.parseDouble(config.get("volumenMusica"))/100.0);
        SoundEffects.setVolumen(Double.parseDouble(config.get("volumenSonido"))/100.0);
        controlador.volverAMenuPrincipal();
    }
    

    /**
     * Maneja el evento de cambio en el toggle de música.
     * Actualiza el estado visual del toggle, modifica la configuración de audio
     * a través del MusicManager y actualiza las etiquetas de estado correspondientes.
     * 
     * @pre toggleMusica, toggleBgMusica y lblEstadoMusica deben estar inicializados,
     *      MusicManager debe estar disponible.
     * @param event Evento de acción generado por el cambio del toggle (puede ser null)
     * @post El estado visual del toggle se actualiza con colores y posición apropiados,
     *       la música se reproduce o pausa según el estado seleccionado,
     *       y la etiqueta de estado muestra "Activado" o "Desactivado" correctamente.
     */    
    @FXML
    public void onToggleMusicaClick(ActionEvent event) {
        boolean activado = toggleMusica.isSelected();
        
        if (activado) {
            // Música activada
            if (toggleBgMusica != null) {
                toggleBgMusica.setStyle("-fx-background-color: #2ecc71; -fx-background-radius: 15;");
                toggleBgMusica.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
                MusicManager.play();
            }
            if (lblEstadoMusica != null) {
                lblEstadoMusica.setText("Activado");
            }
        } else {
            // Música desactivada
            if (toggleBgMusica != null) {
                toggleBgMusica.setStyle("-fx-background-color: #e74c3c; -fx-background-radius: 15;");
                toggleBgMusica.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                MusicManager.pause();
            }
            if (lblEstadoMusica != null) {
                lblEstadoMusica.setText("Desactivado");
            }
        }
    }
    

    /**
     * Maneja el evento de cambio en el toggle de efectos de sonido.
     * Actualiza el estado visual del toggle, modifica la configuración de audio
     * a través del SoundEffects y actualiza las etiquetas de estado correspondientes.
     * 
     * @pre toggleSonido, toggleBgSonido y lblEstadoSonido deben estar inicializados,
     *      SoundEffects debe estar disponible.
     * @param event Evento de acción generado por el cambio del toggle (puede ser null)
     * @post El estado visual del toggle se actualiza con colores y posición apropiados,
     *       los efectos de sonido se habilitan o deshabilitan según el estado seleccionado,
     *       y la etiqueta de estado muestra "Activado" o "Desactivado" correctamente.
     */    
    @FXML
    public void onToggleSonidoClick(ActionEvent event) {
        boolean activado = toggleSonido.isSelected();
        
        if (activado) {
            // Sonido activado
            if (toggleBgSonido != null) {
                toggleBgSonido.setStyle("-fx-background-color: #2ecc71; -fx-background-radius: 15;");
                toggleBgSonido.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
                SoundEffects.habilitar(true);
            }
            if (lblEstadoSonido != null) {
                lblEstadoSonido.setText("Activado");
            }
        } else {
            // Sonido desactivado
            if (toggleBgSonido != null) {
                toggleBgSonido.setStyle("-fx-background-color: #e74c3c; -fx-background-radius: 15;");
                toggleBgSonido.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                SoundEffects.habilitar(false);
            }
            if (lblEstadoSonido != null) {
                lblEstadoSonido.setText("Desactivado");
            }
        }
    }
    

    /**
     * Maneja el evento de clic en el botón de guardar configuración.
     * Recopila todos los valores actuales de la configuración general,
     * los valida y los guarda de forma persistente a través del controlador.
     * 
     * @pre Todos los componentes de configuración deben estar inicializados
     *      y el controlador debe estar disponible.
     * @param event Evento de acción generado por el clic del botón
     * @post Si todos los componentes están disponibles, se guardan el tema,
     *       estados de audio y volúmenes en el sistema de persistencia,
     *       y se muestra una alerta de éxito. Si hay error, se muestra alerta de error.
     */    
    public void onGuardarClick(ActionEvent event) {
        if (comboTema != null && toggleMusica != null && toggleSonido != null) {
            // Guardar configuración general a través del controlador
            controlador.guardarConfiguracionGeneral(
                    comboTema.getValue(),
                    toggleMusica.isSelected(),
                    toggleSonido.isSelected(),
                    (int) sliderMusica.getValue(),
                    (int) sliderSonido.getValue()
            );
            controlador.mostrarAlerta("success", "Configuración guardada", 
                    "La configuración general se ha guardado correctamente.");
        }
        else controlador.mostrarAlerta("error", "Error", 
                "No se pudo guardar la configuración general.");
    }
    

    /**
     * Maneja el evento de clic en el botón de restablecer configuración.
     * Restaura todos los valores de configuración general a sus valores por defecto,
     * actualizando inmediatamente la interfaz y los sistemas de audio correspondientes.
     * 
     * @pre Todos los componentes de configuración deben estar inicializados,
     *      MusicManager y SoundEffects deben estar disponibles.
     * @param event Evento de acción generado por el clic del botón
     * @post El tema se establece en "Claro", música y sonido se activan,
     *       ambos volúmenes se establecen en 50%, la interfaz se actualiza
     *       para reflejar los cambios, los sistemas de audio se ajustan
     *       inmediatamente, y se muestra una alerta informativa de confirmación.
     */    
    public void onRestablecerClick(ActionEvent event) {
        // Restablecer valores por defecto
        if (comboTema != null) comboTema.setValue("Claro");
        
        if (toggleMusica != null) {
            toggleMusica.setSelected(true);
            onToggleMusicaClick(null); // Actualiza la UI
        }
        
        if (toggleSonido != null) {
            toggleSonido.setSelected(true);
            onToggleSonidoClick(null); // Actualiza la UI
        }

        // Volumen música a 50
        if (sliderMusica != null) {
            sliderMusica.setValue(50);
            MusicManager.setVolume(0.5);
        }
        if (labelVolumenMusica != null) {
            labelVolumenMusica.setText("50");
        }

        // Volumen sonido a 50
        if (sliderSonido != null) {
            sliderSonido.setValue(50);
            SoundEffects.setVolumen(0.5);
        }
        if (labelVolumenSonido != null) {
            labelVolumenSonido.setText("50");
        }
        
        controlador.mostrarAlerta("info", "Valores restablecidos", 
                "Se han restablecido los valores por defecto de la configuración general.");
    }
    

    /**
     * Obtiene la vista cargada de configuración general.
     * Proporciona acceso a la jerarquía completa de componentes JavaFX
     * para su integración en la aplicación principal.
     * 
     * @pre La vista debe haber sido cargada previamente mediante cargarVista().
     * @return Parent que contiene la vista completa de configuración general
     * @post Se devuelve la referencia a la vista cargada sin modificar
     *       su estado actual ni la configuración de sus componentes.
     */    
    public Parent getView() {
        return view;
    }
}