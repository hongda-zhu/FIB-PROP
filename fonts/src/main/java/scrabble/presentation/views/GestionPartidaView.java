package scrabble.presentation.views;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import scrabble.MainApplication;
import scrabble.excepciones.ExceptionDiccionarioExist;
import scrabble.excepciones.ExceptionPalabraInvalida;
import scrabble.presentation.viewControllers.ControladorPartidaView;
import javafx.scene.layout.Region;
/**
 * Vista para gestión de partidas
 */
public class GestionPartidaView {
    private final ControladorPartidaView controlador;
    private Parent view;
    
    // Componentes FXML
    private Button btnJugarNuevaPartida;
    private Button btnEliminarPartida;
    private Button btnCargarPartida;
    private Button btnVolver;
    private ListView<String> listaPartidas;
    
    public GestionPartidaView(ControladorPartidaView controlador) {
        this.controlador = controlador;
        cargarVista();
    }
    
    private void cargarVista() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/views/gestion-partida-view.fxml"));
            view = loader.load();

            try {
                String cssResource = "/styles/button.css";
                URL cssUrl = getClass().getResource(cssResource);
                if (cssUrl != null) {
                    view.getStylesheets().add(cssUrl.toExternalForm());
                    System.out.println("CSS cargado en GestionPartidaView");
                }
                else {
                    System.err.println("No se pudo encontrar el recurso CSS: " + cssResource);
                }

                String tableCssResource = "/styles/tables.css";
                URL tableCssUrl = getClass().getResource(tableCssResource);
                if (tableCssUrl != null) {
                    view.getStylesheets().add(tableCssUrl.toExternalForm());
                    System.out.println("CSS de tablas cargado en GestionPartidaView");
                }
                else {
                    System.err.println("No se pudo encontrar el recurso CSS: " + tableCssResource);
                }                
            } catch (Exception e) {
                System.err.println("Error al cargar CSS en GestionPartidaView: " + e.getMessage());
            }                        
            // Obtener referencias a los componentes usando lookup
            btnJugarNuevaPartida = (Button) view.lookup("#btnJugarNuevaPartida");
            btnEliminarPartida = (Button) view.lookup("#btnEliminarPartida");
            btnCargarPartida = (Button) view.lookup("#btnCargarPartida");
            btnVolver = (Button) view.lookup("#btnVolver");
            listaPartidas = (ListView<String>) view.lookup("#listaPartidas");
            
            aplicarEstiloBotones();  
            estilizarListView();          
            configurarEventos();
            cargarPartidas();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo cargar la vista de gestión de partidas");
        }
    }
    
    /**
    * Aplica estilos modernos a la ListView similar a los de la tabla
    */
    private void estilizarListView() {
        if (listaPartidas != null) {
            listaPartidas.getStyleClass().add("modern-list");
            
            listaPartidas.setMinWidth(100);
            listaPartidas.setPrefWidth(Region.USE_COMPUTED_SIZE);
            listaPartidas.setMaxWidth(Double.MAX_VALUE);
            
            listaPartidas.setCellFactory(lv -> {
                javafx.scene.control.ListCell<String> cell = new javafx.scene.control.ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                            setStyle("-fx-background-color: transparent;");
                        } else {
                            setText(item);
                            
                            if (item.startsWith("--")) {
                                setStyle("-fx-padding: 8px 15px; " +
                                        "-fx-text-fill: #757575; " +
                                        "-fx-font-style: italic; " +
                                        "-fx-alignment: center;");
                            } else {
                                setStyle("-fx-padding: 8px 15px; " +
                                        "-fx-text-fill: #2c3e50; " +
                                        "-fx-border-color: transparent transparent #f0f0f0 transparent;");
                            }
                        }
                    }
                };
                
                // Estilo al pasar el mouse
                cell.setOnMouseEntered(e -> {
                    if (!cell.isEmpty()) {
                        cell.setStyle(cell.getStyle() + "-fx-background-color: #f5f9ff;");
                    }
                });
                
                cell.setOnMouseExited(e -> {
                    if (!cell.isEmpty()) {
                        if (cell.getItem() != null && cell.getItem().startsWith("--")) {
                            cell.setStyle("-fx-padding: 8px 15px; " +
                                        "-fx-text-fill: #757575; " +
                                        "-fx-font-style: italic; " +
                                        "-fx-alignment: center;");
                        } else {
                            cell.setStyle("-fx-padding: 8px 15px; " +
                                        "-fx-text-fill: #2c3e50; " +
                                        "-fx-border-color: transparent transparent #f0f0f0 transparent;");
                        }
                    }
                });
                
                return cell;
            });
            
            // Configurar el placeholder cuando la lista está vacía
            javafx.scene.control.Label placeholderLabel = new javafx.scene.control.Label("No hay partidas guardadas");
            placeholderLabel.setStyle("-fx-text-fill: #757575; -fx-font-style: italic; -fx-font-size: 14px;");
            listaPartidas.setPlaceholder(placeholderLabel);
        }
    }    
    /**
     * Aplicar clases CSS a los botones
     */
    private void aplicarEstiloBotones() {
        if (btnJugarNuevaPartida != null) {
            btnJugarNuevaPartida.getStyleClass().addAll("btn-effect", "btn-primary");
            System.out.println("Clases CSS aplicadas a btnJugarNuevaPartida: " + btnJugarNuevaPartida.getStyleClass());
        }
        
        if (btnEliminarPartida != null) {
            btnEliminarPartida.getStyleClass().addAll("btn-effect", "btn-danger");
            System.out.println("Clases CSS aplicadas a btnEliminarPartida: " + btnEliminarPartida.getStyleClass());
        }
        
        if (btnCargarPartida != null) {
            btnCargarPartida.getStyleClass().addAll("btn-effect", "btn-success");
            System.out.println("Clases CSS aplicadas a btnCargarPartida: " + btnCargarPartida.getStyleClass());
        }
        
        if (btnVolver != null) {
            btnVolver.getStyleClass().add("btn-effect");
            System.out.println("Clases CSS aplicadas a btnVolver: " + btnVolver.getStyleClass());
        }
    }

    private void configurarEventos() throws ExceptionDiccionarioExist, IOException, ExceptionPalabraInvalida {
        if (btnJugarNuevaPartida != null) {
            
            btnJugarNuevaPartida.setOnAction(e -> {
                try {
                    controlador.crearNuevaPartida();
                } catch (ExceptionDiccionarioExist | IOException | ExceptionPalabraInvalida ex) {
                    // Maneja la excepción - muestra un mensaje de error
                    showAlert(Alert.AlertType.ERROR, "Error", 
                        "Error al crear una nueva partida: " + ex.getMessage());
                    ex.printStackTrace(); // Para depuración
                }
            });
        }
        
        if (btnCargarPartida != null) {
            btnCargarPartida.setOnAction(e -> {
                String partidaSeleccionada = listaPartidas.getSelectionModel().getSelectedItem();
                if (partidaSeleccionada != null) {
                    System.out.println("CARGANDO PARTIDA " + partidaSeleccionada);
                    Integer id = Integer.parseInt(partidaSeleccionada);
                    controlador.cargarPartida(id);
                } else {
                    controlador.mostrarAlerta("warning", "Advertencia", "Por favor, seleccione una partida para cargar");
                }
            });
        }
        
        if (btnEliminarPartida != null) {
            btnEliminarPartida.setOnAction(e -> {
                String partidaSeleccionada = listaPartidas.getSelectionModel().getSelectedItem();
                if (partidaSeleccionada != null) {
                    listaPartidas.getItems().remove(partidaSeleccionada);
                    Integer id = Integer.parseInt(partidaSeleccionada);
                    controlador.eliminarPartidaGuardada(id);
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
    
    private void cargarPartidas() {
        if (listaPartidas != null) {
            listaPartidas.getItems().clear();
            List <Integer> partidasGuardadas = controlador.getPartidasGuardadasID();
            if (partidasGuardadas.size() < 1) {
                listaPartidas.getItems().add("-- No hay partidas guardadas --");
                return;
            }

            System.err.println("PARTIDAS GUARDADAS");
            for (int i = 0; i < partidasGuardadas.size(); i++) {
                // System.out.printf("| %2d. %-30s |\n", i + 1, partidasGuardadas.get(i));

                listaPartidas.getItems().add(partidasGuardadas.get(i).toString());
            }

        }
    }
    
    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(type.toString());
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public Parent getView() {
        return view;
    }
}