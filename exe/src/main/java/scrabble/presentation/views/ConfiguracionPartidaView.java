package scrabble.presentation.views;

import java.io.IOException;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import scrabble.MainApplication;
import scrabble.presentation.viewControllers.ControladorConfiguracionView;


/**
 * Vista de configuración específica de partida del juego Scrabble.
 * Esta clase proporciona la interfaz de usuario para configurar parámetros
 * específicos que afectan el comportamiento y las reglas de las partidas,
 * incluyendo tamaño del tablero, diccionario de palabras válidas y otras
 * opciones relacionadas con la mecánica de juego.
 * 
 * La vista permite al usuario personalizar aspectos fundamentales del juego
 * que determinan la experiencia de cada partida individual, proporcionando
 * controles especializados con validación en tiempo real para asegurar
 * configuraciones válidas y consistentes.
 * 
 * Características principales:
 * - Campo de texto validado para tamaño de tablero con restricciones mínimas
 * - ComboBox para selección de diccionario con opciones dinámicas
 * - Validación en tiempo real con mensajes de error informativos
 * - Filtrado de entrada para permitir solo valores numéricos válidos
 * - Confirmación automática de valores al perder foco o presionar Enter
 * - Botones de guardar y restablecer con persistencia de configuración
 * - Efectos de hover interactivos en elementos de navegación
 * - Integración completa con sistema de diccionarios del juego
 * - Manejo robusto de errores y validación de entrada
 * - Aplicación inmediata de cambios con retroalimentación visual
 * 
 * La vista utiliza el patrón MVC, comunicándose con ControladorConfiguracionView
 * para todas las operaciones de validación, persistencia y obtención de
 * datos de configuración. Implementa validación exhaustiva de entrada
 * para prevenir configuraciones inválidas que podrían afectar el juego.
 * 
 * Los parámetros configurados en esta vista afectan directamente la creación
 * de nuevas partidas, estableciendo las reglas base y restricciones que
 * determinarán la experiencia de juego de cada sesión individual.
 * 
 * @version 1.0
 * @since 1.0
 */
public class ConfiguracionPartidaView {
    
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
    private TextField txtTamanioTablero;
    
    @FXML
    private Label lblErrorTablero;
    
    @FXML
    private ComboBox<String> comboDiccionario;
    
    @FXML
    private BorderPane root;

    @FXML
    private VBox menuLateral;

    @FXML
    private StackPane contenedorContenido;
    
    
    /**
     * Constructor que inicializa la vista de configuración de partida.
     * Recibe el controlador necesario para manejar la validación y persistencia
     * de configuraciones específicas de partida, y carga automáticamente la
     * vista FXML con todos sus componentes y validaciones correspondientes.
     * 
     * @pre controlador no debe ser null y debe estar correctamente inicializado
     *      con acceso a diccionarios y configuraciones por defecto.
     * @param controlador Controlador que maneja la lógica de configuración de partida
     * @post Se crea una nueva instancia con la vista FXML cargada, todos los
     *       componentes inicializados con valores por defecto, validaciones de
     *       entrada configuradas y efectos visuales aplicados correctamente.
     * @throws NullPointerException si controlador es null
     */    
    public ConfiguracionPartidaView(ControladorConfiguracionView controlador) {
        this.controlador = controlador;
        cargarVista();
    }


    /**
     * Carga la vista FXML de configuración de partida desde el archivo correspondiente.
     * Inicializa el FXMLLoader, establece el controlador de la vista,
     * carga el archivo FXML y configura todos los componentes con sus
     * valores por defecto y sistemas de validación.
     * 
     * @pre El archivo FXML debe existir en la ruta especificada y el controlador
     *      debe tener acceso a configuraciones y diccionarios.
     * @post La vista FXML se carga correctamente, se establece como controlador
     *       de la vista y se inicializan todos los componentes con valores por
     *       defecto, validaciones activas y efectos visuales aplicados.
     * @throws IOException si hay errores al cargar el archivo FXML
     */    
    private void cargarVista() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/views/configuracion-partida.fxml"));
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
     * Inicializa todos los componentes visuales y funcionales de la interfaz.
     * Aplica clases CSS, configura efectos de hover, establece validaciones
     * de entrada, configura listeners de eventos y carga valores por defecto
     * en campos de texto y ComboBox de diccionarios.
     * 
     * @pre Los componentes FXML deben haber sido inyectados correctamente
     *      y el controlador debe estar inicializado con acceso a datos.
     * @post Todos los botones tienen efectos de hover, el campo de tamaño
     *       tiene validación numérica y de rango, el ComboBox se llena con
     *       diccionarios disponibles, los listeners de eventos están configurados
     *       y todos los valores por defecto están establecidos correctamente.
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
        
        // Configurar campos y validación
        if (txtTamanioTablero != null) {
            txtTamanioTablero.setText(String.valueOf(controlador.getTamanoDefault()));
            
            // Permitir solo números
            txtTamanioTablero.addEventFilter(javafx.scene.input.KeyEvent.KEY_TYPED, event -> {
                if (!event.getCharacter().matches("[0-9]")) {
                    event.consume();
                }
            });
            
            // Validar cuando cambia el texto
            txtTamanioTablero.textProperty().addListener((observable, oldValue, newValue) -> {
                validarTamanioTablero(newValue);
            });
            
            // Validar al perder el foco
            txtTamanioTablero.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    confirmarTamanioTablero();
                }
            });
            
            // Validar al presionar Enter
            txtTamanioTablero.setOnAction(e -> confirmarTamanioTablero());
        }
        
        // Inicializar ComboBox
        if (comboDiccionario != null) {
            comboDiccionario.getItems().clear();
            comboDiccionario.getItems().addAll(controlador.getAllDiccionarios());
            comboDiccionario.setValue(controlador.getDiccionarioDefault());
        }

    }
    
   /**
     * Añade efectos visuales de hover a un botón específico del menú.
     * Configura animaciones de escala y translación que proporcionan
     * retroalimentación visual inmediata cuando el usuario interactúa
     * con los elementos de navegación del menú de configuración.
     * 
     * @pre button no debe ser null.
     * @param button Botón de menú al que se aplicarán los efectos de hover
     * @post El botón tiene configuradas animaciones suaves de entrada y salida
     *       que se ejecutan automáticamente al hacer hover. Las animaciones
     *       incluyen escalado (105%) y desplazamiento horizontal (5px) con
     *       duración de 200ms para proporcionar transiciones fluidas.
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
     * Valida el tamaño del tablero ingresado por el usuario.
     * Verifica que el valor sea un número entero válido y que cumple
     * con los requisitos mínimos del juego (al menos 15 casillas).
     * Actualiza el label de error con mensajes informativos según el tipo de error.
     * 
     * @pre valor no debe ser null, lblErrorTablero debe estar inicializado.
     * @param valor Cadena de texto ingresada por el usuario para el tamaño
     * @return true si el valor es válido y cumple todos los requisitos,
     *         false si hay algún error de formato o valor fuera de rango
     * @post Si el valor es válido, se oculta el label de error. Si es inválido,
     *       se muestra un mensaje específico de error (formato, rango o vacío)
     *       y se hace visible el label de error correspondiente.
     */
    private boolean validarTamanioTablero(String valor) {
        if (lblErrorTablero == null) return true;
        
        try {
            int tamano = Integer.parseInt(valor);
            if (tamano < 15) {
                lblErrorTablero.setText("El tamaño debe ser al menos 15");
                lblErrorTablero.setVisible(true);
                return false;
            } else {
                lblErrorTablero.setVisible(false);
                return true;
            }
        } catch (NumberFormatException e) {
            if (valor.isEmpty()) {
                lblErrorTablero.setText("Debes introducir un tamaño");
            } else {
                lblErrorTablero.setText("Por favor, introduce un número válido");
            }
            lblErrorTablero.setVisible(true);
            return false;
        }
    }
    


    /**
     * Confirma el tamaño del tablero cuando se pierde el foco o se presiona Enter.
     * Ejecuta la validación final del valor ingresado y proporciona
     * retroalimentación al usuario sobre la validez de la configuración.
     * 
     * @pre txtTamanioTablero debe estar inicializado.
     * @post Si el campo no está vacío y el valor es válido, se confirma
     *       la configuración y se registra en la consola. Si es inválido,
     *       se mantiene el mensaje de error visible sin confirmar el valor.
     */
    private void confirmarTamanioTablero() {
        if (txtTamanioTablero != null && !txtTamanioTablero.getText().isEmpty()) {
            if (validarTamanioTablero(txtTamanioTablero.getText())) {
                System.out.println("Tamaño de tablero confirmado: " + txtTamanioTablero.getText());
            }
        }
    }
    

    /**
     * Maneja el evento de clic en el botón de configuración general.
     * Delega al controlador la navegación hacia la vista de configuración
     * general, manteniendo el estado actual de configuración de partida.
     * 
     * @pre El controlador debe estar inicializado.
     * @param event Evento de acción generado por el clic del botón
     * @post Se navega hacia la vista de configuración general a través
     *       del controlador, preservando los cambios actuales no guardados.
     */    
    @FXML
    public void onGeneralClick(ActionEvent event) {
        controlador.mostrarVistaGeneral();
    }
    

    /**
     * Maneja el evento de clic en el botón de configuración de partida.
     * Aunque esta vista ya representa la configuración de partida, este método
     * permite refrescar la vista actual o mantener consistencia en la navegación.
     * 
     * @pre El controlador debe estar inicializado.
     * @param event Evento de acción generado por el clic del botón
     * @post Se refresca o mantiene la vista actual de configuración de partida
     *       a través del controlador, asegurando consistencia en la navegación.
     */    
    @FXML
    public void onPartidaClick(ActionEvent event) {
        controlador.mostrarVistaPartida();
    }
    

    /**
     * Maneja el evento de clic en el botón de ayuda.
     * Delega al controlador la navegación hacia la vista de ayuda,
     * proporcionando acceso a documentación específica de configuración de partida.
     * 
     * @pre El controlador debe estar inicializado.
     * @param event Evento de acción generado por el clic del botón
     * @post Se navega hacia la vista de ayuda a través del controlador,
     *       manteniendo el contexto actual de configuración de partida.
     */    
    @FXML
    public void onAyudaClick(ActionEvent event) {
        controlador.mostrarVistaAyuda();
    }
    

    /**
     * Maneja el evento de clic en el botón de volver.
     * Delega al controlador la navegación de regreso al menú principal,
     * cerrando la vista de configuración de partida sin guardar cambios automáticamente.
     * 
     * @pre El controlador debe estar inicializado.
     * @param event Evento de acción generado por el clic del botón
     * @post Se navega de vuelta al menú principal a través del controlador,
     *       manteniendo los cambios no guardados en memoria hasta la próxima sesión.
     */    
    @FXML
    public void onVolverClick(ActionEvent event) {
        controlador.volverAMenuPrincipal();
    }
    

    /**
     * Maneja el evento de clic en el botón de guardar configuración.
     * Valida todos los campos de configuración de partida y, si son válidos,
     * guarda la configuración de forma persistente a través del controlador.
     * 
     * @pre txtTamanioTablero y comboDiccionario deben estar inicializados,
     *      el controlador debe estar disponible para guardar configuración.
     * @param event Evento de acción generado por el clic del botón
     * @post Si la validación es exitosa, se guarda el tamaño del tablero
     *       y diccionario seleccionado de forma persistente. Si hay errores
     *       de validación, se detiene el proceso y se muestran mensajes de error.
     *       En caso de errores de formato, se muestra una alerta de error.
     */    
    public void onGuardarClick(ActionEvent event) {
        try {
            // Validar campos
            boolean tamanioValido = validarTamanioTablero(txtTamanioTablero.getText());
            
            if (!tamanioValido) {
                return;
            }
            
            int tamanioTablero = Integer.parseInt(txtTamanioTablero.getText());
            
            // Guardar configuración
            controlador.guardarConfiguracionPartida(
                    tamanioTablero,
                    comboDiccionario.getValue()
            );
            
        } catch (NumberFormatException e) {
            controlador.mostrarAlerta("error", "Error", 
                    "Por favor, ingrese valores numéricos válidos.");
        }
    }
    

    /**
     * Maneja el evento de clic en el botón de restablecer configuración.
     * Restaura todos los valores de configuración de partida a sus valores
     * por defecto, limpiando mensajes de error y actualizando la interfaz.
     * 
     * @pre txtTamanioTablero, comboDiccionario y lblErrorTablero deben estar
     *      inicializados, el controlador debe proporcionar valores por defecto.
     * @param event Evento de acción generado por el clic del botón
     * @post El tamaño del tablero se establece al valor por defecto,
     *       el diccionario se selecciona por defecto, se ocultan todos
     *       los mensajes de error, la interfaz se actualiza para reflejar
     *       los cambios, y se muestra una alerta informativa de confirmación.
     */    
    public void onRestablecerClick(ActionEvent event) {
        // Restablecer valores
        if (txtTamanioTablero != null) {
            txtTamanioTablero.setText(String.valueOf(controlador.getTamanoDefault()));
            if (lblErrorTablero != null) {
                lblErrorTablero.setVisible(false);
            }
        }
        
        if (comboDiccionario != null) {

            comboDiccionario.setValue(controlador.getDiccionarioDefault());
        }
        
        controlador.mostrarAlerta("info", "Valores restablecidos", 
                "Se han restablecido los valores por defecto de la configuración de partida.");
    }
    

    /**
     * Obtiene la vista cargada de configuración de partida.
     * Proporciona acceso a la jerarquía completa de componentes JavaFX
     * para su integración en la aplicación principal.
     * 
     * @pre La vista debe haber sido cargada previamente mediante cargarVista().
     * @return Parent que contiene la vista completa de configuración de partida
     * @post Se devuelve la referencia a la vista cargada sin modificar
     *       su estado actual ni la configuración de sus componentes.
     */    
    public Parent getView() {
        return view;
    }
}