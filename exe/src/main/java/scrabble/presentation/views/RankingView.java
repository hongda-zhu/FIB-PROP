package scrabble.presentation.views;

import java.net.URL;
import java.util.Comparator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import scrabble.presentation.componentes.JugadorRanking;
import scrabble.presentation.viewControllers.ControladorRankingView;


/**
 * Vista principal para la gestión y visualización del ranking de jugadores de Scrabble.
 * Esta clase proporciona una interfaz completa para consultar, filtrar, ordenar y
 * administrar las estadísticas y posiciones de todos los jugadores registrados
 * en el sistema, ofreciendo múltiples criterios de análisis y gestión.
 * 
 * La vista presenta una tabla centralizada con estadísticas detalladas de jugadores
 * y herramientas de filtrado y ordenación avanzadas para análisis personalizado
 * del rendimiento. Incluye funcionalidad de eliminación de jugadores del ranking
 * y validación en tiempo real de criterios de filtrado numéricos.
 * 
 * Características principales:
 * - Tabla completa con estadísticas detalladas de jugadores (posición, puntos, partidas)
 * - Sistema de ordenación múltiple por diferentes criterios estadísticos
 * - Filtrado avanzado con validación numérica en tiempo real
 * - Eliminación controlada de jugadores del ranking con confirmación
 * - Validación automática de entrada numérica con mensajes de error informativos
 * - Funcionalidad de restablecimiento completo de filtros y ordenación
 * - Soporte completo para temas claro y oscuro con estilos adaptativos
 * - Interfaz responsiva que se adapta al tamaño de ventana
 * - Habilitación/deshabilitación automática de botones según selección
 * - Separación clara entre datos originales y datos filtrados
 * - Aplicación automática de estilos CSS para coherencia visual
 * - Placeholder informativo cuando no hay datos disponibles
 * - Gestión de altura optimizada para filas de tabla
 * 
 * La vista utiliza el patrón MVC, comunicándose con ControladorRankingView
 * para todas las operaciones de consulta de estadísticas, eliminación de
 * jugadores y gestión de datos del ranking. Implementa ObservableList
 * separadas para datos originales y filtrados, permitiendo operaciones
 * de filtrado reversibles sin pérdida de información.
 * 
 * El sistema de filtrado incluye validación exhaustiva de entrada numérica
 * con retroalimentación visual inmediata, y criterios múltiples basados en
 * puntuaciones totales, máximas, medias, número de partidas y victorias,
 * proporcionando análisis detallado del rendimiento de jugadores.
 * 
 * @version 1.0
 * @since 1.0
 */
public class RankingView {

    private final Parent view;

    private ControladorRankingView controlador;

    private Button volverBtn;
    private Button eliminarBtn;
    private Button searchBtn;
    private Button restablecerBtn;
    private TableView<JugadorRanking> table;
    private ComboBox<String> orderCombo;
    private ComboBox<String> filterCombo;
    private TextField quantityField;
    private ObservableList<JugadorRanking> datosOriginales;
    private ObservableList<JugadorRanking> datosVisibles;
    private Label errorLabel;
    


    /**
     * Constructor que inicializa la vista de gestión y visualización de ranking.
     * Configura la interfaz completa incluyendo tabla de estadísticas, controles
     * de filtrado y ordenación, aplicación de temas, estilos CSS y funcionalidad
     * responsiva para diferentes tamaños de ventana con carga automática de datos.
     * 
     * @pre controlador no debe ser null y debe estar correctamente inicializado
     *      con acceso a funcionalidades de consulta de ranking y estadísticas.
     * @param controlador Controlador que maneja la lógica de gestión de ranking
     * @post Se crea una nueva instancia con la interfaz completamente configurada,
     *       tabla de ranking cargada con estadísticas actuales, controles de filtrado
     *       y ordenación funcionales, estilos de tema aplicados, funcionalidad responsiva
     *       activa y todos los listeners de eventos configurados correctamente.
     * @throws NullPointerException si controlador es null
     */
    public RankingView(ControladorRankingView controlador) {
        this.controlador = controlador;
        crearBotones();
        // Barra superior con título
        Text title = new Text("Gestión de Ranking");
        title.setFont(new Font("Arial", 24));
        title.setFill(Color.WHITE);
        StackPane topBar = new StackPane(title);
        topBar.setPadding(new Insets(15));
        topBar.setStyle("-fx-background-color: #2f3e4e;");
        topBar.setMinHeight(90);

        // Filtros (no funcionales, solo estéticos como en la imagen)
        this.orderCombo = new ComboBox<>();
        orderCombo.setPromptText("Ordenar por:");
        orderCombo.setDisable(false);
        orderCombo.setItems(FXCollections.observableArrayList(
        "P. Total", "P. Máxima", "P. Media", "Partidas", "Victorias"
        ));
        
        orderCombo.setOnAction(e -> aplicarOrden(orderCombo.getValue()));

        this.filterCombo = new ComboBox<>();
        filterCombo.setPromptText("Filtrar por:");
        filterCombo.setDisable(false);
        filterCombo.setItems(FXCollections.observableArrayList(
        "P. Total", "P. Máxima", "P. Media", "Partidas", "Victorias"
        ));

        this.quantityField = new TextField();
        quantityField.setPromptText("Cantidad a filtrar:");
        quantityField.setDisable(true);

        filterCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            quantityField.setDisable(newVal == null || newVal.isBlank());
            errorLabel.setText("");
        });
        
        quantityField.setPrefWidth(150);
        quantityField.setMaxWidth(150);

        this.errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setStyle("-fx-font-size: 12;");
        errorLabel.setVisible(false);

        errorLabel.setMaxWidth(150);

        quantityField.textProperty().addListener((obs, oldVal, newVal) -> {
            validarCantidadEnTiempoReal(newVal);
        });

        VBox quantityBox = new VBox(2, quantityField, errorLabel);
        quantityBox.setPadding(new Insets(16, 0, 0, 0));


        HBox filterBox = new HBox(10, orderCombo, new Region(), filterCombo, quantityBox, searchBtn, restablecerBtn);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        filterBox.setPadding(new Insets(10, 0, 10, 0));
        HBox.setHgrow(filterBox.getChildren().get(1), Priority.ALWAYS);

        // Tabla
        this.table = new TableView<>();
        
        inicializarTabla();

        aplicarOrden("P.Total");

        Button page1 = new Button("1");
        page1.setStyle("-fx-background-color: #2E3A59; -fx-text-fill: white;");
        Button page2 = new Button("2");
        Button prev = new Button("<<");
        Button next = new Button(">>");

        HBox pagination = new HBox(5, prev, page1, page2, next);
        pagination.setAlignment(Pos.CENTER);

        HBox controlBox = new HBox(20, volverBtn, eliminarBtn);
        controlBox.setAlignment(Pos.CENTER);
        controlBox.setPadding(new Insets(20));

        table.setPrefWidth(1000);

        HBox centeredBox = new HBox(table);
        centeredBox.setAlignment(Pos.CENTER);

        // Layout principal
        VBox layout = new VBox();
        layout.setSpacing(10);
        aplicarFondoPorTema(layout, controlador.getTema(), "#ffffff", "#1c1747");
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(filterBox, centeredBox, controlBox);
        layout.setMaxWidth(1200);

        HBox contenedorCentral = new HBox(layout);
        contenedorCentral.setAlignment(Pos.CENTER);
        contenedorCentral.setPadding(new Insets(20));

        VBox root = new VBox(topBar, contenedorCentral);
        VBox.setVgrow(layout, Priority.ALWAYS);

        if (controlador.getTema().equals("Claro")) root.setStyle("-fx-background-color: #f5f5f5;");
        else if (controlador.getTema().equals("Oscuro")) root.setStyle("-fx-background-color: #0b0a2e;");

        this.view = root;

        try {
            List<String> cssResources = List.of(
                "/styles/table.css",
                "/styles/button.css"
            );

            for (String cssResource : cssResources) {
                URL cssUrl = getClass().getResource(cssResource);
                if (cssUrl != null) {
                    view.getStylesheets().add(cssUrl.toExternalForm());
                    System.out.println("CSS cargado en CrearDiccionarioView: " + cssResource);
                } else {
                    System.err.println("No se pudo encontrar el recurso CSS: " + cssResource);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar CSS en CrearDiccionarioView: " + e.getMessage());
        }
        aplicarCssBotones();
        cambiarColorTextoPorTema(root, controlador.getTema());
        eliminarBtn.setDisable(true);
    }

 
     /**
     * Aplica colores de fondo y estilos adaptativos según el tema actual.
     * Configura automáticamente colores de fondo, bordes y radios de borde
     * para mantener consistencia visual entre temas claro y oscuro durante
     * la visualización del ranking.
     * 
     * @pre nodo, tema, claroColor y oscuroColor no deben ser null.
     * @param nodo Contenedor Parent al que aplicar el estilo temático
     * @param tema Tema actual del sistema ("Claro" o "Oscuro")
     * @param claroColor Color hexadecimal para el tema claro
     * @param oscuroColor Color hexadecimal para el tema oscuro
     * @post El nodo tiene aplicados los estilos CSS apropiados para el tema,
     *       incluyendo colores de fondo, bordes redondeados y bordes de contraste
     *       que mantienen la legibilidad durante la visualización del ranking.
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
     * Aplica estilos CSS y configuraciones visuales avanzadas a la tabla de ranking.
     * Configura dimensiones, comportamiento de columnas, modos de selección,
     * placeholders estilizados y listeners para manejo automático del estado
     * de botones según la selección actual del usuario.
     * 
     * @pre table debe estar inicializada y el botón eliminar disponible.
     * @post La tabla tiene aplicados estilos CSS modernos, dimensiones apropiadas,
     *       columnas no reordenables, selección simple habilitada, placeholder
     *       estilizado, listeners de selección configurados para botón eliminar
     *       y altura de filas optimizada para visualización de estadísticas.
     */
    private void estilizarTabla() {
        if (table != null) {
            // Aplicar clase CSS
            table.getStyleClass().add("modern-table");
            
            // Configurar dimensiones
            table.setMinWidth(600);
            table.setPrefWidth(800);
            table.setMaxWidth(Double.MAX_VALUE);
            
            // Desactivar reordenamiento de columnas
            for (TableColumn<JugadorRanking, ?> column : table.getColumns()) {
                column.setReorderable(false);
            }
            
            // Configurar selección
            table.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.SINGLE);
            
            // Placeholder personalizado con mejor estilo
            Label placeholderLabel = new Label("No hay jugadores en el ranking");
            placeholderLabel.setStyle("-fx-text-fill: #757575; -fx-font-style: italic; -fx-font-size: 14px;");
            table.setPlaceholder(placeholderLabel);
            
            // Event listener para habilitar/deshabilitar botón según selección
            table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                boolean haySeleccion = newSelection != null;
                if (eliminarBtn != null) eliminarBtn.setDisable(!haySeleccion);
            });
            
            // Configurar altura de filas
            table.setRowFactory(tv -> {
                javafx.scene.control.TableRow<JugadorRanking> row = new javafx.scene.control.TableRow<>();
                row.setPrefHeight(45); 
                return row;
            });
            
            table.setPadding(new Insets(0));
        }
    }


    /**
     * Aplica clases CSS específicas a todos los botones de la interfaz.
     * Configura estilos consistentes diferenciando botones por funcionalidad:
     * eliminar con estilo de peligro, otros con estilo primario y efectos
     * visuales uniformes para toda la interfaz de ranking.
     * 
     * @pre Todos los botones deben estar inicializados correctamente.
     * @post Todos los botones tienen aplicadas clases CSS apropiadas:
     *       eliminar con estilo de peligro, volver, buscar y restablecer con
     *       estilo primario, y todos incluyen efectos de transición visual
     *       para mejorar la experiencia de gestión del ranking.
     */    
    private void aplicarCssBotones() {
        eliminarBtn.getStyleClass().addAll("btn-effect", "btn-danger");
        volverBtn.getStyleClass().addAll("btn-effect", "btn-primary");
        searchBtn.getStyleClass().addAll("btn-effect", "btn-primary");
        restablecerBtn.getStyleClass().addAll("btn-effect", "btn-primary");
    }

 
     /**
     * Aplica ordenación a la tabla según el criterio estadístico especificado.
     * Configura comparadores específicos para diferentes métricas de rendimiento
     * y aplica ordenación descendente para mostrar mejores resultados primero,
     * actualizando inmediatamente la visualización de la tabla.
     * 
     * @pre criterio no debe ser null, table debe estar inicializada con datos.
     * @param criterio Criterio de ordenación estadística a aplicar
     * @post La tabla se reordena según el criterio especificado en orden descendente,
     *       mostrando los mejores valores primero para el criterio seleccionado.
     *       Se soportan criterios: P. Total, P. Máxima, P. Media, Partidas, Victorias.
     */
    private void aplicarOrden(String criterio) {
        Comparator<JugadorRanking> comparador;
        if (criterio == null) return;
        switch (criterio) {
            case "P. Total":
                comparador = Comparator.comparingInt(JugadorRanking::getPuntosTotales).reversed();
                break;
            case "P. Máxima":
                comparador = Comparator.comparingInt(JugadorRanking::getPuntosMaximos).reversed();
                break;
            case "P. Media":
                comparador = Comparator.comparingDouble(JugadorRanking::getPuntosMedias).reversed();
                break;
            case "Partidas":
                comparador = Comparator.comparingInt(JugadorRanking::getPartidas).reversed();
                break;    
            case "Victorias":
                comparador = Comparator.comparingInt(JugadorRanking::getVictorias).reversed();
                break;
            default:
                comparador = Comparator.comparingInt(JugadorRanking::getPuntosTotales).reversed();
                break;
        }
        FXCollections.sort(table.getItems(), comparador);
    }


    /**
     * Ejecuta el proceso de filtrado de la tabla según criterios especificados.
     * Valida la entrada del usuario, extrae criterios de filtrado y límite numérico,
     * ejecuta validaciones de formato y rango, y aplica el filtro a los datos
     * mostrando mensajes de error apropiados si la validación falla.
     * 
     * @pre Los controles de filtrado deben estar inicializados correctamente.
     * @post Si la validación es exitosa, se aplica el filtro especificado a la tabla.
     *       Si hay errores de validación (criterio vacío, formato numérico inválido,
     *       valores negativos), se muestran alertas informativas guiando al usuario.
     */
    private void filtrarTabla() {
        // Implementar lógica de filtrado aquí
        // Por ejemplo, filtrar los jugadores que cumplen con el criterio y el límite
        String criterio = filterCombo.getValue();
        String limitText = quantityField.getText().trim();
        if (criterio == null) {
            controlador.mostrarAlerta("error", "Filtrado de tabla", "Debe seleccionar un criterio de filtrado.");
            return;
        }
        try {
            Double limit;
            if (limitText.isBlank()) limit = 0.0;
            else limit = Double.parseDouble(limitText);
            if (limit < 0) {
                controlador.mostrarAlerta("error", "Filtrado de tabla", "La cantidad de filtro debe ser un número positivo");
                return;
            }
            aplicarFiltro(criterio, limit);
        } catch (NumberFormatException e) {
            controlador.mostrarAlerta("error", "Filtrado de tabla", "La cantidad de filtro no és un número válido");
        }
    }


    /**
     * Valida la entrada numérica del campo de cantidad en tiempo real.
     * Proporciona retroalimentación visual inmediata sobre la validez del valor
     * ingresado, mostrando mensajes de error específicos para diferentes tipos
     * de problemas de formato o rango de valores.
     * 
     * @pre errorLabel debe estar inicializado para mostrar mensajes.
     * @param valor Cadena de texto ingresada por el usuario para validar
     * @post Si el valor es válido (número positivo), se oculta el mensaje de error.
     *       Si es inválido, se muestra mensaje específico: campo vacío, formato no
     *       numérico, o valor negativo, y se hace visible el label de error.
     */
    private void validarCantidadEnTiempoReal(String valor) {
        if (valor == null || valor.isBlank()) {
            errorLabel.setText("Ingrese una cantidad.");
            errorLabel.setVisible(true);
        } else {
            try {
                double cantidad = Double.parseDouble(valor);
                if (cantidad < 0) {
                    errorLabel.setText("Debe ser positivo.");
                    errorLabel.setVisible(true);
                } else {
                    errorLabel.setVisible(false);
                }
            } catch (NumberFormatException e) {
                errorLabel.setText("Debe ser un número.");
                errorLabel.setVisible(true);
            }
        }
    }


    /**
     * Aplica filtro específico a los datos del ranking según criterio y límite.
     * Filtra la lista de jugadores manteniendo solo aquellos que cumplen con
     * el criterio estadístico especificado y el valor mínimo establecido,
     * actualizando los datos visibles sin modificar los originales.
     * 
     * @pre criterio no debe ser null, limit debe ser un valor numérico válido,
     *      datosOriginales y datosVisibles deben estar inicializados.
     * @param criterio Criterio estadístico para el filtrado
     * @param limit Valor mínimo que deben cumplir los jugadores para el criterio
     * @post datosVisibles contiene solo jugadores que cumplen el criterio con
     *       valores mayores o iguales al límite especificado. datosOriginales
     *       permanece inalterado para permitir restablecimiento de filtros.
     */
    private void aplicarFiltro(String criterio, Double limit) {
        datosVisibles.setAll(
            datosOriginales.filtered(jugador -> {
                switch (criterio) {
                    case "P. Total":
                        return jugador.getPuntosTotales() >= limit;
                    case "P. Máxima":
                        return jugador.getPuntosMaximos() >= limit;
                    case "P. Media":
                        return jugador.getPuntosMedias() >= limit;
                    case "Partidas":
                        return jugador.getPartidas() >= limit;
                    case "Victorias":
                        return jugador.getVictorias() >= limit;
                    default:
                        return true;
                }
            })
        );
    }


    /**
     * Inicializa la tabla de ranking con columnas y datos completos de jugadores.
     * Configura todas las columnas estadísticas, carga datos actuales desde el
     * controlador, calcula estadísticas derivadas y establece la tabla con
     * datos ordenados por puntuación total por defecto.
     * 
     * @pre El controlador debe estar inicializado con acceso a datos de usuarios.
     * @post La tabla está completamente configurada con columnas para posición,
     *       nombre, puntuaciones (total, máxima, media), partidas y victorias,
     *       datos cargados desde el sistema, estadísticas calculadas correctamente,
     *       listas separadas para datos originales y visibles, y estilos aplicados.
     */
    private void inicializarTabla() {
        table.getStyleClass().add("excel-table");
        TableColumn<JugadorRanking, Number> colPos = new TableColumn<>("Posición");
        colPos.setCellValueFactory(cellData -> {
            int index = table.getItems().indexOf(cellData.getValue()) + 1;
            return new javafx.beans.property.SimpleIntegerProperty(index);
        });

        TableColumn<JugadorRanking, String> colNombre = new TableColumn<>("Jugador");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<JugadorRanking, Number> colTotal = new TableColumn<>("P. Total");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("puntosTotales"));

        TableColumn<JugadorRanking, Number> colMax = new TableColumn<>("P. Máxima");
        colMax.setCellValueFactory(new PropertyValueFactory<>("puntosMaximos"));

        TableColumn<JugadorRanking, Number> colMedia = new TableColumn<>("P. Media");
        colMedia.setCellValueFactory(new PropertyValueFactory<>("puntosMedias"));

        TableColumn<JugadorRanking, Number> colPartidas = new TableColumn<>("Partidas");
        colPartidas.setCellValueFactory(new PropertyValueFactory<>("partidas"));

        TableColumn<JugadorRanking, Number> colVictorias = new TableColumn<>("Victorias");
        colVictorias.setCellValueFactory(new PropertyValueFactory<>("victorias"));

        table.getColumns().addAll(colPos, colNombre, colTotal, colMax, colMedia, colPartidas, colVictorias);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        datosOriginales = FXCollections.observableArrayList();
        List<String> usuarios = controlador.getUsuariosRanking();
        for (String usuario: usuarios) {
            int pt = controlador.getPuntuacionTotal(usuario);
            int pm = controlador.getPuntuacionMaxima(usuario);
            double pmed = Math.round(controlador.getPuntuacionMedia(usuario) * 100.0) / 100.0;
            int partidas = controlador.getPartidasJugadas(usuario);
            int victorias = controlador.getvictorias(usuario);

            JugadorRanking jugador = new JugadorRanking(usuario, pt, pm, pmed, partidas, victorias);

            datosOriginales.add(jugador);
        }



        // table.setPadding(new Insets(200));
        datosVisibles = FXCollections.observableArrayList(datosOriginales);
        table.setItems(datosVisibles);

        estilizarTabla();
    }


    /**
     * Crea e inicializa todos los botones de acción con sus manejadores de eventos.
     * Configura botones para volver, eliminar jugador, buscar, y restablecer,
     * estableciendo validaciones de selección, confirmaciones de eliminación
     * y funcionalidad de restablecimiento completo de filtros y ordenación.
     * 
     * @pre El controlador debe estar inicializado y la tabla disponible para consultas.
     * @post Todos los botones están creados con manejadores de eventos configurados,
     *       validaciones de selección implementadas para eliminación, funcionalidad
     *       de filtrado y restablecimiento activa, y navegación de retorno establecida.
     */
    private void crearBotones() {
        volverBtn = crearBoton("Volver", "#95a5a6");
        volverBtn.setOnAction(e -> controlador.volverAMenuPrincipal());

        eliminarBtn = crearBoton("Eliminar Jugador del Ranking", "#e74c3c");
        eliminarBtn.setOnAction(e -> {
            JugadorRanking seleccionado = table.getSelectionModel().getSelectedItem();
            if (seleccionado == null) {
                controlador.mostrarAlerta("info", "Eliminar jugador", "Seleccione un jugador del ranking para eliminar.");
                return;
            }
            if (controlador.eliminarUsuarioRanking(seleccionado.getNombre())) {
                datosOriginales.remove(seleccionado);
                datosVisibles.remove(seleccionado);
                controlador.mostrarAlerta("success", "Jugador eliminado", "El jugador " + seleccionado.getNombre() + " ha sido eliminado del ranking.");
            } else {
                controlador.mostrarAlerta("error", "Eliminar jugador", "No se pudo eliminar el jugador " + seleccionado.getNombre() + "del ranking.");
            }
        });

        searchBtn = crearBoton("Buscar", "#3498db");
        searchBtn.setOnAction(e -> filtrarTabla());

        restablecerBtn = crearBoton("Restablecer", "#3498db");
        restablecerBtn.setOnAction(e -> {
            orderCombo.getSelectionModel().clearSelection();
            orderCombo.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? orderCombo.getPromptText() : item);
                }
            });
            filterCombo.getSelectionModel().clearSelection();
            filterCombo.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? filterCombo.getPromptText() : item);
                }
            });
            quantityField.clear();
            quantityField.setPromptText("Cantidad a filtrar:");
            datosVisibles.setAll(datosOriginales);
            errorLabel.setText("");
            aplicarOrden("P. Total");
        });
    }


    /**
     * Crea un botón personalizado con estilo y configuración específicos.
     * Aplica formato de texto, colores personalizados y dimensiones estándar
     * para mantener consistencia visual en toda la interfaz de ranking.
     * 
     * @pre texto y color no deben ser null ni estar vacíos.
     * @param texto Texto que se mostrará en el botón
     * @param color Color de fondo del botón en formato hexadecimal
     * @return Button configurado con estilo personalizado y dimensiones estándar
     * @post Se devuelve un botón completamente configurado con texto especificado,
     *       color de fondo personalizado, texto blanco, fuente en negrita,
     *       dimensiones estándar y configuración de layout apropiada.
     */
    private Button crearBoton(String texto, String color) {
        Button btn = new Button(texto);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 6;");
        btn.setMinHeight(45);
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    /**
     * Obtiene la vista cargada de gestión de ranking.
     * Proporciona acceso a la jerarquía completa de componentes JavaFX
     * configurados para la visualización y gestión del ranking de jugadores.
     * 
     * @pre La vista debe haber sido inicializada completamente en el constructor.
     * @return Parent que contiene la vista completa de gestión de ranking
     * @post Se devuelve la referencia a la vista completamente configurada
     *       sin modificar su estado actual ni los datos cargados en la tabla.
     */
    public Parent getView() {
        return view;
    }
    
}
