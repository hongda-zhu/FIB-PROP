package scrabble.presentation.viewControllers;

import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import scrabble.MainApplication;
import scrabble.presentation.PresentationController;
import scrabble.presentation.views.ConfiguracionAyudaView;
import scrabble.presentation.views.ConfiguracionGeneralView;
import scrabble.presentation.views.ConfiguracionPartidaView;


/**
 * Controlador para la gestión de configuración de la aplicación Scrabble.
 * Maneja la navegación entre las tres vistas de configuración principales
 * (general, partida y ayuda) y coordina las operaciones de guardado y
 * carga de preferencias del usuario mediante el patrón Singleton.
 * 
 * Características principales:
 * - Navegación fluida entre vistas de configuración con mantenimiento de estado
 * - Gestión centralizada de configuración general y específica de partida
 * - Integración con sistema de temas visuales y persistencia de preferencias
 * - Manejo seguro de cambios de interfaz mediante Platform.runLater
 * - Preservación de dimensiones y estado de ventana durante navegación
 * - Sistema de alertas integrado para retroalimentación al usuario
 * - Gestión de valores por defecto para diccionarios y tamaños de tablero
 * 
 * @version 1.0
 * @since 1.0
 */
public class ControladorConfiguracionView {
    
    // Singleton instance
    private static ControladorConfiguracionView instance;
    
    private Stage stage;
    private PresentationController presentationController;
    private Parent vistaAnterior;
    private Scene scene;

    // Vistas
    private ConfiguracionGeneralView vistaGeneral;
    private ConfiguracionPartidaView vistaPartida;
    private ConfiguracionAyudaView vistaAyuda;
    
    // Vista actualmente mostrada
    private String vistaActual;

    // Fichero de configuración
    private static final String CONFIG_FILE = "src/main/resources/config/config.properties";

    private StringProperty temaActual = new SimpleStringProperty("Claro");
    
    /**
     * Constructor privado para implementación del patrón Singleton.
     * Inicializa la conexión con el controlador de presentación para
     * acceso a funcionalidades del sistema de configuración.
     * 
     * @pre No hay precondiciones específicas para la construcción.
     * @post Se crea una instancia con el controlador de presentación inicializado
     *       y lista para configuración de vistas de configuración.
     */
    private ControladorConfiguracionView() {
        this.presentationController = PresentationController.getInstance();
    }
    

    /**
     * Obtiene la instancia única del controlador de configuración.
     * Implementa el patrón Singleton para garantizar una sola instancia
     * durante toda la ejecución de la aplicación.
     * 
     * @pre No hay precondiciones específicas para obtener la instancia.
     * @return La instancia única de ControladorConfiguracionView
     * @post Se devuelve la instancia única, creándola si es la primera invocación.
     */
    public static ControladorConfiguracionView getInstance() {
        if (instance == null) {
            instance = new ControladorConfiguracionView();
        }
        return instance;
    }
    

    /**
     * Obtiene el diccionario configurado por defecto para nuevas partidas.
     * Consulta la configuración actual para obtener qué diccionario se
     * utiliza automáticamente cuando se crean partidas sin especificar otro.
     * 
     * @pre El sistema debe tener un diccionario por defecto configurado.
     * @return String con el nombre del diccionario por defecto
     * @post Se devuelve el diccionario por defecto sin modificar la configuración.
     */    
    public String getDiccionarioDefault() {
        return presentationController.getDiccionarioDefault();
    }


    /**
     * Obtiene el tamaño de tablero configurado por defecto para nuevas partidas.
     * Consulta la configuración actual para obtener las dimensiones que se
     * utilizan automáticamente cuando se crean partidas sin especificar tamaño.
     * 
     * @pre El sistema debe tener un tamaño por defecto configurado.
     * @return int con el tamaño de tablero por defecto
     * @post Se devuelve el tamaño por defecto sin modificar la configuración.
     */
    public int getTamanoDefault() {
        return presentationController.getTamanoDefault();
    }


    /**
     * Establece el diccionario por defecto para nuevas partidas.
     * Actualiza la configuración para usar el diccionario especificado
     * como opción por defecto en futuras creaciones de partidas.
     * 
     * @pre nombre no debe ser null y debe corresponder a un diccionario existente.
     * @param nombre Nombre del diccionario a establecer como por defecto
     * @post El diccionario especificado queda configurado como por defecto.
     */
    public void setDiccionario(String nombre) {
        presentationController.setDiccionarioDefault(nombre);
    }


    /**
     * Establece el tamaño de tablero por defecto para nuevas partidas.
     * Actualiza la configuración para usar el tamaño especificado como
     * opción por defecto en futuras creaciones de partidas.
     * 
     * @pre tamano debe ser un valor positivo válido para tableros de Scrabble.
     * @param tamano Tamaño del tablero a establecer como por defecto
     * @post El tamaño especificado queda configurado como por defecto.
     */
    public void setTamano(int tamano) {
        presentationController.setTamanoDefault(tamano);
    }


    /**
     * Obtiene la lista completa de diccionarios disponibles en el sistema.
     * Proporciona todos los diccionarios que pueden ser seleccionados
     * como por defecto o utilizados en partidas específicas.
     * 
     * @pre No hay precondiciones específicas para consultar diccionarios.
     * @return Lista de nombres de todos los diccionarios disponibles
     * @post Se devuelve la lista completa sin modificar la disponibilidad.
     */
    public List<String> getAllDiccionarios() {
        return presentationController.getAllDiccionariosDisponibles();
    }
    

    /**
     * Inicializa el controlador con el stage principal y configura las vistas.
     * Crea todas las vistas de configuración, establece la escena inicial
     * y configura estilos CSS manteniendo el estado de ventana anterior.
     * 
     * @pre stage no debe ser null y debe ser el stage principal de la aplicación.
     * @param stage Stage principal donde mostrar las vistas de configuración
     * @post Todas las vistas están inicializadas, la vista general se muestra
     *       por defecto, CSS aplicado y dimensiones de ventana preservadas.
     */    
    public void initialize(Stage stage) {
        this.stage = stage;
        
        try {
            // Inicializar las vistas
            vistaGeneral = new ConfiguracionGeneralView(this);
            vistaPartida = new ConfiguracionPartidaView(this);
            vistaAyuda = new ConfiguracionAyudaView(this);
            
            // Obtener las dimensiones actuales de la ventana
            double width = stage.getScene() != null ? stage.getScene().getWidth() : 1000;
            double height = stage.getScene() != null ? stage.getScene().getHeight() : 700;
            boolean isMaximized = stage.isMaximized();
            
            Parent initialView = vistaGeneral != null ? vistaGeneral.getView() : null;
            if (initialView != null) {
                scene = new Scene(initialView, width, height);
                    
                // Aplicar CSS
                try {
                    String cssResource = "/styles/button.css";
                    if (getClass().getResource(cssResource) != null) {
                        scene.getStylesheets().add(getClass().getResource(cssResource).toExternalForm());
                    }
                } catch (Exception e) {
                    System.err.println("Error al aplicar CSS: " + e.getMessage());
                }
                
                // USAR Platform.runLater para cambios de UI
                Platform.runLater(() -> {
                    if (stage != null && scene != null) {
                        stage.setScene(scene);
                        stage.setTitle("Configuración - General");
                        vistaActual = "general";

                        if (isMaximized) {
                            stage.setMaximized(false); 
                            stage.setMaximized(true);
                        }
                    }
                });
            }
        } catch (Exception e) {
            System.err.println("Error en initialize: " + e.getMessage());
            e.printStackTrace();
        }
    }
    

    /**
     * Muestra la vista de configuración general de la aplicación.
     * Cambia la interfaz para mostrar controles de tema, audio y otras
     * preferencias generales del usuario de forma segura.
     * 
     * @pre La vista general debe estar inicializada correctamente.
     * @post Se muestra la vista de configuración general y se actualiza el título.
     */
    public void mostrarVistaGeneral() {
        Platform.runLater(() -> {
            try {
                if (vistaGeneral != null && scene != null) {
                    Parent view = vistaGeneral.getView();
                    if (view != null) {
                        cambiarVista(view, "Configuración - General");
                        vistaActual = "general";
                    }
                }
            } catch (Exception e) {
                System.err.println("Error al mostrar vista general: " + e.getMessage());
                presentationController.mostrarAlerta("error", "Error", "Error al cargar vista de configuración general");
            }
        });
    }
    

    /**
     * Muestra la vista de configuración específica de partida.
     * Cambia la interfaz para mostrar controles de tamaño de tablero,
     * diccionario por defecto y otras opciones de partida.
     * 
     * @pre La vista de partida debe estar inicializada correctamente.
     * @post Se muestra la vista de configuración de partida y se actualiza el título.
     */
    public void mostrarVistaPartida() {
        Platform.runLater(() -> {
            try {
                if (vistaPartida != null && scene != null) {
                    Parent view = vistaPartida.getView();
                    if (view != null) {
                        cambiarVista(view, "Configuración - Partida");
                        vistaActual = "partida";
                    }
                }
            } catch (Exception e) {
                System.err.println("Error al mostrar vista partida: " + e.getMessage());
                presentationController.mostrarAlerta("error", "Error", "Error al cargar vista de configuración de partida");
            }
        });
    }
    

    /**
     * Muestra la vista de ayuda y contacto de la aplicación.
     * Cambia la interfaz para mostrar información de ayuda, documentación
     * y opciones de contacto para soporte técnico.
     * 
     * @pre La vista de ayuda debe estar inicializada correctamente.
     * @post Se muestra la vista de ayuda y se actualiza el título correspondiente.
     */
    public void mostrarVistaAyuda() {
        Platform.runLater(() -> {
            try {
                if (vistaAyuda != null && scene != null) {
                    Parent view = vistaAyuda.getView();
                    if (view != null) {
                        cambiarVista(view, "Configuración - Ayuda");
                        vistaActual = "ayuda";
                    }
                }
            } catch (Exception e) {
                System.err.println("Error al mostrar vista ayuda: " + e.getMessage());
                presentationController.mostrarAlerta("error", "Error", "Error al cargar vista de configuración de ayuda");
            }
        });
    }
    

    /**
     * Cambia el contenido de la escena actual de forma segura.
     * Actualiza la raíz de la escena existente y el título de la ventana
     * manteniendo dimensiones y propiedades de la ventana.
     * 
     * @pre view y title no deben ser null, scene y stage deben estar inicializados.
     * @param view Nueva vista a mostrar como contenido principal
     * @param title Nuevo título para la ventana de la aplicación
     * @post La vista se cambia exitosamente y el título se actualiza.
     */
    private void cambiarVista(Parent view, String title) {
        try {
            if (scene != null && view != null && stage != null) {
                scene.setRoot(view);
                stage.setTitle(title);
            }
        } catch (Exception e) {
            System.err.println("Error al cambiar vista: " + e.getMessage());
        }
    }


    /**
     * Retorna al menú principal manteniendo el estado de la ventana.
     * Carga la vista principal de la aplicación preservando dimensiones,
     * estado de maximización y estilos CSS aplicados previamente.
     * 
     * @pre stage debe estar inicializado con una escena válida.
     * @post Se navega al menú principal con estado de ventana preservado.
     */
    public void volverAMenuPrincipal() {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/views/main-view.fxml"));
                Parent mainView = loader.load();
                
                if (mainView != null && stage != null) {
                    Scene currentScene = stage.getScene();
                    double width = currentScene != null ? currentScene.getWidth() : 1000;
                    double height = currentScene != null ? currentScene.getHeight() : 700;
                    
                    Scene newScene = new Scene(mainView, width, height);
                    
                    // Mantener CSS
                    if (currentScene != null && !currentScene.getStylesheets().isEmpty()) {
                        newScene.getStylesheets().addAll(currentScene.getStylesheets());
                    }
                    
                    boolean wasMaximized = stage.isMaximized();
                    
                    stage.setScene(newScene);
                    stage.setTitle("SCRABBLE");
                    
                    // Mantener maximizado si estaba maximizado
                    if (wasMaximized) {
                        Platform.runLater(() -> {
                            stage.setMaximized(false);
                            stage.setMaximized(true);
                        });
                    }
                }
                
            } catch (Exception e) {
                System.err.println("Error al volver al menú principal: " + e.getMessage());
                e.printStackTrace();
                presentationController.mostrarAlerta("error", "Error", "No se pudo volver al menú principal");
            }
        });
    }
    


    /**
     * Establece una vista anterior para navegación de retorno.
     * Permite implementar funcionalidad de "volver" a vistas previas
     * cuando sea necesario mantener el flujo de navegación.
     * 
     * @pre vista puede ser null si no hay vista anterior válida.
     * @param vista Vista anterior a la que se puede retornar
     * @post La vista anterior queda registrada para posible navegación de retorno.
     */
    public void setVistaAnterior(Parent vista) {
        this.vistaAnterior = vista;
    }
    

    /**
     * Guarda la configuración general de la aplicación de forma persistente.
     * Persiste las preferencias de usuario incluyendo tema visual,
     * configuración de audio y niveles de volumen en archivo de configuración.
     * 
     * @pre Los parámetros deben tener valores válidos dentro de rangos esperados.
     * @param tema Tema visual seleccionado ("Claro" o "Oscuro")
     * @param musicaActivada Estado de activación de música de fondo
     * @param sonidoActivado Estado de activación de efectos de sonido
     * @param volumenMusica Nivel de volumen de música (0-100)
     * @param volumenSonido Nivel de volumen de efectos (0-100)
     * @post La configuración se guarda persistentemente para futuras sesiones.
     */
    public void guardarConfiguracionGeneral(String tema, boolean musicaActivada, boolean sonidoActivado, int volumenMusica, int volumenSonido) {
        try {
            presentationController.guardarConfiguracionGeneral(tema, musicaActivada, sonidoActivado, volumenMusica, volumenSonido);
        } catch (Exception e) {
            System.err.println("Error al guardar configuración: " + e.getMessage());
            mostrarAlerta("error", "Error", "No se pudo guardar la configuración");
        }
    }



    /**
     * Carga la configuración actual desde archivo de propiedades.
     * Lee las preferencias guardadas previamente, utilizando valores
     * por defecto si el archivo no existe o hay errores de lectura.
     * 
     * @pre No hay precondiciones específicas para cargar configuración.
     * @return Mapa con claves y valores de configuración, vacío si hay errores
     * @post Se devuelve la configuración disponible sin modificar archivos.
     */
    public Map<String, String> cargarConfiguracion() {
        try {
            return presentationController.cargarConfiguracion();
        } catch (Exception e) {
            System.err.println("Error al cargar configuración: " + e.getMessage());
            return Map.of(); // Retornar mapa vacío en caso de error
        }
    }
    

    /**
     * Guarda la configuración específica de partida con valores validados.
     * Persiste las preferencias de tamaño de tablero y diccionario por defecto,
     * mostrando confirmación exitosa al usuario tras el guardado.
     * 
     * @pre tamanioTablero debe ser positivo, diccionario debe existir en el sistema.
     * @param tamanioTablero Tamaño de tablero por defecto a configurar
     * @param diccionario Nombre del diccionario por defecto a establecer
     * @post La configuración se guarda y se muestra mensaje de confirmación.
     */
    public void guardarConfiguracionPartida(int tamanioTablero, String diccionario) {
        try {
            presentationController.setDiccionarioDefault(diccionario);
            presentationController.setTamanoDefault(tamanioTablero);
            presentationController.mostrarAlerta("info", "Configuración guardada", 
                    "La configuración de partida ha sido guardada correctamente.");
        } catch (Exception e) {
            System.err.println("Error al guardar configuración de partida: " + e.getMessage());
            mostrarAlerta("error", "Error", "No se pudo guardar la configuración de partida");
        }
    }

    /**
     * Establece el tema visual de la aplicación.
     * 
     * @pre El tema debe ser una opción válida soportada por la aplicación.
     * @param tema El tema a establecer
     * @post El tema de la configuración se actualiza y se persiste.
     */
    public void setTema(String tema) {
        presentationController.setTema(tema);
    }

    /**
     * Actualiza el valor del tema actual en tiempo de ejecución, permitiendo la reactividad
     * mediante listeners vinculados a la propiedad.
     * 
     * @param tema Nuevo tema a establecer ("Claro" u "Oscuro").
     * @pre El valor debe ser una cadena válida y compatible con los estilos definidos.
     * @post El tema actual se actualiza, y los componentes vinculados a la propiedad se notificarán del cambio.
     */

    public void actualizarTema(String tema) {
        temaActual.set(tema);
    }

    /**
     * Devuelve la propiedad observable del tema actual.
     * 
     * @return Una StringProperty que contiene el tema actual ("Claro" u "Oscuro").
     * @post Se puede usar para enlazar cambios de tema dinámicamente en distintas vistas.
     */

    public StringProperty temaProperty() {
        return temaActual;
    }

    /**
     * Obtiene el valor actual del tema seleccionado.
     * 
     * @return El tema actual como cadena ("Claro" u "Oscuro").
     * @post Permite acceder al valor actual del tema para personalizar estilos en componentes.
     */

    public String getTema() {
        return temaActual.get();
    }
    

    /**
     * Muestra información de contacto para soporte técnico.
     * Presenta al usuario los datos de contacto disponibles para
     * obtener ayuda técnica o reportar problemas con la aplicación.
     * 
     * @pre No hay precondiciones específicas para mostrar información.
     * @post Se muestra una alerta con información de contacto de soporte.
     */
    public void mostrarInformacionContacto() {
        presentationController.mostrarAlerta("info", "Contacto de Soporte", 
                "Email: jose.miguel.urquiza@upc.edu\r\n" + 
                "\nTeléfono: 123-456-789");
    }

    /**
     * Obtiene el tema actual desde el controlador de presentación.
     *
     * @return una cadena que representa el tema actual.
     * @pre presentationController debe estar inicializado.
     * @post Se retorna el tema actualmente seleccionado en la aplicación.
     */
    // public String getTema() {
    //     return presentationController.getTema();
    // }
    

    /**
     * Muestra alertas y notificaciones al usuario con formato consistente.
     * Proporciona un método centralizado para mostrar mensajes de diferentes
     * tipos con formato uniforme en toda la interfaz de configuración.
     * 
     * @pre tipo, titulo y mensaje no deben ser null.
     * @param tipo Tipo de alerta ("info", "warning", "error", "success")
     * @param titulo Título de la ventana de alerta
     * @param mensaje Contenido del mensaje a mostrar
     * @post Se muestra la alerta con formato apropiado al tipo especificado.
     */
    public void mostrarAlerta(String tipo, String titulo, String mensaje) {
        presentationController.mostrarAlerta(tipo, titulo, mensaje);
    }
    

    /**
     * Obtiene el stage principal para acceso desde las vistas.
     * Proporciona acceso al stage de la aplicación para operaciones
     * que requieren manipulación directa de la ventana principal.
     * 
     * @pre stage debe haber sido inicializado en initialize().
     * @return Stage principal de la aplicación
     * @post Se devuelve la referencia al stage sin modificar su estado.
     */
    public Stage getStage() {
        return stage;
    }
    

    /**
     * Obtiene el identificador de la vista actualmente mostrada.
     * Proporciona información sobre cuál de las tres vistas de configuración
     * está siendo mostrada actualmente al usuario.
     * 
     * @pre vistaActual debe haber sido establecida durante la navegación.
     * @return String con el identificador de la vista actual
     * @post Se devuelve el identificador sin modificar el estado de navegación.
     */    
    public String getVistaActual() {
        return vistaActual;
    }
}