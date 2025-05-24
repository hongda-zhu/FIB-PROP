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
import javafx.scene.paint.Color;
import scrabble.MainApplication;
import scrabble.presentation.viewControllers.ControladorJugadoresView;
import scrabble.presentation.viewControllers.ControladorJugadoresView.EstadisticasJugador;

/**
 * Vista para mostrar el historial de un jugador
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
    
    public HistorialJugadorView(ControladorJugadoresView controlador, String nombreJugador) {
        this.controlador = controlador;
        this.nombreJugador = nombreJugador;
        cargarVista();
    }
    
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
                    System.out.println("CSS cargado en HistorialJugadorView");
                }
                else {
                    System.err.println("No se pudo encontrar el recurso CSS: " + cssResource);
                }
            } catch (Exception e) {
                System.err.println("Error al cargar CSS en HistorialJugadorView: " + e.getMessage());
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
            
            if (btnVolver != null) {
                btnVolver.getStyleClass().add("btn-effect");
            }
            
            estilizarListView();
            
            if (btnVolver != null) {
                btnVolver.setOnAction(e -> controlador.mostrarVistaGestionJugadores());
            }
            
            cargarDatosJugador();
            
        } catch (IOException e) {
            e.printStackTrace();
            controlador.mostrarAlerta("error", "Error", "No se pudo cargar la vista de historial del jugador");
        }
    }
    
    /**
     * Aplica estilos modernos a la ListView sin interactividad
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
                System.err.println("Error al estilizar componentes del ScrollBar: " + e.getMessage());
            }
        }
    }
    
    /**
     * Carga los datos del jugador seleccionado
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
    
    public Parent getView() {
        return view;
    }
}