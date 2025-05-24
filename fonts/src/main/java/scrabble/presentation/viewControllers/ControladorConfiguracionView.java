package scrabble.presentation.viewControllers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import scrabble.MainApplication;
import scrabble.presentation.PresentationController;
import scrabble.presentation.views.ConfiguracionAyudaView;
import scrabble.presentation.views.ConfiguracionGeneralView;
import scrabble.presentation.views.ConfiguracionPartidaView;

/**
 * Controlador para la pantalla de configuración de la aplicación
 * Maneja la navegación entre las tres vistas de configuración: general, partida y ayuda
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
    
    /**
     * Constructor privado para implementar el patrón Singleton
     */
    private ControladorConfiguracionView() {
        this.presentationController = PresentationController.getInstance();
    }
    
    /**
     * Obtiene la instancia singleton del ControladorConfiguracionView
     * 
     * @return Instancia única del ControladorConfiguracionView
     */
    public static ControladorConfiguracionView getInstance() {
        if (instance == null) {
            instance = new ControladorConfiguracionView();
        }
        return instance;
    }
    
    public void initialize(Stage stage) {
        this.stage = stage;
        
        // Inicializar las vistas
        vistaGeneral = new ConfiguracionGeneralView(this);
        vistaPartida = new ConfiguracionPartidaView(this);
        vistaAyuda = new ConfiguracionAyudaView(this);
        
        // Obtener las dimensiones actuales de la ventana
        double width = stage.getScene().getWidth();
        double height = stage.getScene().getHeight();
        boolean isMaximized = stage.isMaximized();
        
        Parent initialView = vistaGeneral.getView();
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
        
        stage.setScene(scene);
        stage.setTitle("Configuración - General");
        vistaActual = "general";

        if (isMaximized) {
            // Se ve que hay que hacerlo asi para que reenderice
            stage.setMaximized(false); 
            stage.setMaximized(true);
        }        
    }
    
    /**
     * Muestra la vista de configuración general
     */
    public void mostrarVistaGeneral() {
        try {
            Parent view = vistaGeneral.getView();
            cambiarVista(view, "Configuración - General");
            vistaActual = "general";
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error al cargar vista de configuración general: " + e.getMessage());
        }
    }
    
    /**
     * Muestra la vista de configuración de partida
     */
    public void mostrarVistaPartida() {
        try {
            Parent view = vistaPartida.getView();
            cambiarVista(view, "Configuración - Partida");
            vistaActual = "partida";
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error al cargar vista de configuración de partida: " + e.getMessage());
        }
    }
    
    /**
     * Muestra la vista de ayuda
     */
    public void mostrarVistaAyuda() {
        try {
            Parent view = vistaAyuda.getView();
            cambiarVista(view, "Configuración - Ayuda");
            vistaActual = "ayuda";
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error al cargar vista de configuración de ayuda: " + e.getMessage());
        }
    }
    
    /**
     * Método para cambiar el contenido de la escena existente
     */
    private void cambiarVista(Parent view, String title) {
        scene.setRoot(view);
        stage.setTitle(title);
    }

    /**
     * Método auxiliar para mostrar una vista y aplicar CSS
     */
    private void mostrarVista(Parent view, String title) {
        Scene currentScene = stage.getScene();
        Scene newScene = new Scene(view, currentScene.getWidth(), currentScene.getHeight());
        
        // Aplicar CSS
        if (currentScene != null && !currentScene.getStylesheets().isEmpty()) {
            newScene.getStylesheets().addAll(currentScene.getStylesheets());
        } else {
            try {
                String cssResource = "/styles/button.css";
                if (getClass().getResource(cssResource) != null) {
                    newScene.getStylesheets().add(getClass().getResource(cssResource).toExternalForm());
                }
            } catch (Exception e) {
                System.err.println("Error al aplicar CSS: " + e.getMessage());
            }
        }
        
        stage.setScene(newScene);
        stage.setTitle(title);
    }

    /**
    * Vuelve al menú principal
    */
    public void volverAMenuPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/views/main-view.fxml"));
            Parent mainView = loader.load();
            
            Scene currentScene = stage.getScene();
            Scene newScene = new Scene(mainView, currentScene.getWidth(), currentScene.getHeight());
            
            // Mantener CSS
            if (!currentScene.getStylesheets().isEmpty()) {
                newScene.getStylesheets().addAll(currentScene.getStylesheets());
            }
            
            stage.setScene(newScene);
            stage.setTitle("SCRABBLE");
            
            // Mantener maximizado si estaba maximizado
            if (stage.isMaximized()) {
                stage.setMaximized(false);
                stage.setMaximized(true);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo volver al menú principal");
        }
    }
    /**
     * Establece la vista anterior para poder volver
     */
    public void setVistaAnterior(Parent vista) {
        this.vistaAnterior = vista;
    }
    
    /**
     * Guarda la configuración general
     */
    public void guardarConfiguracionGeneral(String idioma, String tema, boolean musicaActivada, boolean sonidoActivado) {
        Properties props = new Properties();
        props.setProperty("idioma", idioma);
        props.setProperty("tema", tema);
        props.setProperty("musica", Boolean.toString(musicaActivada));
        props.setProperty("sonido", Boolean.toString(sonidoActivado));

        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
            props.store(out, "Configuración de la aplicación");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga la configuración de la aplicación
     */

    public static Properties cargarConfiguracion() {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(CONFIG_FILE)) {
            props.load(in);
        } catch (IOException e) {
            System.out.println("No se pudo cargar configuración, se usarán valores por defecto.");
        }
        return props;
    }
    
    
    /**
     * Guarda la configuración de partida
     */
    public void guardarConfiguracionPartida(int tamanioTablero, String dificultad, int numBots) {
        showAlert(Alert.AlertType.INFORMATION, "Configuración guardada", 
                "La configuración de partida ha sido guardada correctamente.");
    }
    
    /**
     * Muestra información de contacto
     */
    public void mostrarInformacionContacto() {
        showAlert(Alert.AlertType.INFORMATION, "Contacto de Soporte", 
                "Email: jose.miguel.urquiza@upc.edu\r\n" + 
                "\nTeléfono: 123-456-789\nHorario: Lunes a Viernes de 9:00 a 18:00");
    }
    
    /**
     * Muestra una alerta
     */
    public void showAlert(Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(type.toString());
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Muestra una alerta
     */

    public void mostrarAlerta(String tipo, String titulo, String mensaje) {
        presentationController.mostrarAlerta(tipo, titulo, mensaje);
    }
    
    // Getters para acceder al stage y otros recursos desde las vistas
    public Stage getStage() {
        return stage;
    }
    
    public String getVistaActual() {
        return vistaActual;
    }
}