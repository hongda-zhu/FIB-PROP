package scrabble.presentation.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.skin.TextInputControlSkin;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import scrabble.MainApplication;
import scrabble.helpers.Direction;
import scrabble.helpers.Triple;
import scrabble.helpers.Tuple;
import scrabble.presentation.componentes.CasillaDisplay;
import scrabble.presentation.componentes.Ficha;
import scrabble.helpers.TipoCasilla;
import scrabble.presentation.popups.CambiarFichasPopup;
import scrabble.presentation.popups.PausaPopup;
import scrabble.presentation.viewControllers.ControladorPartidaView;

public class VistaTablero {
    
    @FXML private BorderPane panelPrincipal;
    @FXML private GridPane tablero;
    @FXML private HBox fichasJugador;
    @FXML private Label jugadorActual;
    @FXML private Button btnConfirmar;
    @FXML private Button btnCancelar;
    @FXML private Button btnPasarTurno;  
    @FXML private VBox puntuacionesContainer; 
    @FXML private VBox historialContainer;
    @FXML private Label lblFichasRestantes;
    @FXML private ScrollPane scrollPane;
    @FXML private VBox filasNumeracion;
    @FXML private HBox columnasNumeracion;
    @FXML private Button btnCambiarFichas;    
    @FXML private Label lblDiccionarioSeleccionado;
    private ControladorPartidaView controlador;
    private Ficha fichaSeleccionada;
    private Parent view;
    private Integer N;
    private boolean saved;
    private List<String> historialJugadas = new ArrayList<>();
    private final int MAX_HISTORIAL_ENTRIES = 20; // Límite de entradas en el historial
    private boolean firstMove;
    // == PARÁMETROS NECESARIOS PARA JUGAR LA PARTIDA == //
    private List<String> jugadores;
    private int jugadorActualIndex = 0;
    private String jugadorActualNombre;
    private Map<String, Integer> jugadoresPuntuaciones;
    private Map<String, Integer> rackActual;
    private  Direction direccionActual;

    // Lista para rastrear las fichas colocadas en el turno actual 
    private List<FichaColocada> fichasColocadasEnTurnoActual = new ArrayList<>();
    
    private Map<Tuple<Integer, Integer>, String> posiciones; 
    // Tamaño fijo para cada casilla
    private final int CASILLA_SIZE = 40; // Tamaño en pixeles   

    // Información temporal para mantener una ficha colocada pero no confirmada
    private class FichaColocada {
        private String letra;
        private int puntos;
        private int fila;
        private int columna;
        
        public FichaColocada(String letra, int puntos, int fila, int columna) {
            this.letra = letra;
            this.puntos = puntos;
            this.fila = fila;
            this.columna = columna;
        }

        public String getLetra() {
            return this.letra;
        }

    }
 
    /**
     * Constructor que carga el FXML y configura los elementos
     */
    public VistaTablero(ControladorPartidaView controlador) {
        this.controlador = controlador;
        this.N = controlador.getSize();
        this.saved = false;
        this.posiciones = new HashMap<>();
        this.firstMove = true;
        cargarVista();
    }
    
    public void setTableroSize(Integer n) {
        this.N = n;
    }
    /**
     * Método para cargar la vista FXML
     */
    private void cargarVista() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/views/tablero-view.fxml"));
            loader.setController(this); // Establecer esta clase como controlador
            view = loader.load();
            
            // Configurar botón volver manualmente si es necesario
            Button btnVolver = (Button) view.lookup("#btnVolver");
            if (btnVolver != null) {
                btnVolver.setOnAction(e -> controlador.volver());
            }
                        
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void setControlador(ControladorPartidaView controlador) {
        this.controlador = controlador;
    }


    /**
     * Devuelve la vista cargada
     */
    public Parent getView() {
        return view;
    }    
    
    public void initialize() {
        try {
            // Inicializar el tablero y componentes
            inicializarTablero();
            
            // Inicializar fichas
            inicializarFichasJugador();
            
            // Diccionario
            if (lblDiccionarioSeleccionado != null) {
                lblDiccionarioSeleccionado.setText(controlador.getDiccionario());
            }

            if (scrollPane != null) {
                scrollPane.setPannable(true);
                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                scrollPane.setFitToHeight(false);
                scrollPane.setFitToWidth(false);
            }
            
            // Configurar botones
            if (btnConfirmar != null) btnConfirmar.setOnAction(e -> confirmarJugada());
            if (btnCancelar != null) btnCancelar.setOnAction(e -> cancelarJugada());
            if (btnPasarTurno != null) btnPasarTurno.setOnAction(e -> pasarTurno());
            if (btnCambiarFichas != null) btnCambiarFichas.setOnAction(e -> cambiarFichas());
            
            if (historialContainer == null) {
                historialContainer = (VBox) view.lookup("#historialContainer");
            }            
            // No necesitamos configurar btnPausarPartida porque ya tiene un controlador en el FXML (onAction="#pausarPartida")
            
            if (jugadorActual != null) {
                jugadorActual.setText("Iniciando partida...");
            }

            // Iniciar la partida después de que todo esté configurado
            Platform.runLater(this::iniciarPartida);
        } catch (Exception e) {
            System.err.println("Error en initialize: " + e.getMessage());
            e.printStackTrace();
        }
    }

/**
 * Inicia la partida obteniendo los jugadores y configurando el primer turno
 */
private void iniciarPartida() {
    try {
        // Obtener los jugadores de la partida
        jugadoresPuntuaciones = controlador.getJugadoresActuales();
        jugadores = new ArrayList<>(jugadoresPuntuaciones.keySet());
        
        if (controlador.getCargado()) {
            System.out.println("Restaurando estado del tablero para partida cargada...");
            restaurarEstadoTablero();
        }  

        // Iniciar con el primer jugador
        jugadorActualIndex = -1; 
        siguienteTurno();
    } catch (Exception e) {
        e.printStackTrace();
        controlador.mostrarAlerta("error", "Error", "Error al iniciar la partida: " + e.getMessage());
    }
} 


/**
 *  método para restaurar el estado del tablero de una partida cargada
 */
private void restaurarEstadoTablero() {
    try {
        // Obtener el estado del tablero guardado
        Map<Tuple<Integer, Integer>, String> estadoTablero = controlador.getEstadoTablero();
        
        if (estadoTablero == null || estadoTablero.isEmpty()) {
            System.out.println("No hay fichas guardadas en el tablero");
            return;
        }
        
        System.out.println("Restaurando " + estadoTablero.size() + " fichas en el tablero");
        
        // Iterar sobre todas las posiciones con fichas guardadas
        for (Map.Entry<Tuple<Integer, Integer>, String> entry : estadoTablero.entrySet()) {
            Tuple<Integer, Integer> posicion = entry.getKey();
            String letra = entry.getValue();
            
            if (letra != null && !letra.trim().isEmpty()) {
                int fila = posicion.x;
                int columna = posicion.y;
                
                // Buscar la casilla correspondiente en el tablero
                CasillaDisplay casilla = encontrarCasilla(fila, columna);
                
                if (casilla != null) {
                    int puntos = obtenerPuntosPorLetra(letra);
                    casilla.colocarFicha(letra, puntos, true);                    
                    posiciones.put(new Tuple<>(fila, columna), letra);
                    
                    System.out.println("Ficha restaurada: " + letra + " en posición (" + fila + "," + columna + ")");
                } else {
                    System.err.println("No se encontró casilla para posición (" + fila + "," + columna + ")");
                }
            }
        }
        
        Platform.runLater(() -> {
            tablero.requestLayout();
            System.out.println("Tablero restaurado completamente");
        });
        
        if (!estadoTablero.isEmpty()) {
            firstMove = false;
        }
        
    } catch (Exception e) {
        System.err.println("Error al restaurar estado del tablero: " + e.getMessage());
        e.printStackTrace();
        controlador.mostrarAlerta("error", "Error", "Error al restaurar el estado del tablero: " + e.getMessage());
    }
}

/**
 * Método auxiliar para encontrar una casilla específica en el tablero
 */
private CasillaDisplay encontrarCasilla(int fila, int columna) {
    for (Node node : tablero.getChildren()) {
        if (node instanceof CasillaDisplay) {
            CasillaDisplay casilla = (CasillaDisplay) node;
            if (casilla.getFila() == fila && casilla.getColumna() == columna) {
                return casilla;
            }
        }
    }
    return null;
}

/**
 * Limpia el estado de la jugada actual
 */
private void limpiarJugadaActual() {
    fichaSeleccionada = null;    
    direccionActual = null;    
    fichasColocadasEnTurnoActual.clear();

}

/**
 * Pasa al siguiente jugador
 */
private void siguienteTurno() {
    // Verificar si la partida ha terminado
    if (controlador.isJuegoTerminado()) {
        finalizarPartida();
        return;
    }
    
    // Limpiar estado actual
    limpiarJugadaActual(); 

    jugadorActualIndex = (jugadorActualIndex + 1) % jugadores.size();
    jugadorActualNombre = jugadores.get(jugadorActualIndex);
    if (controlador.esIA(jugadorActualNombre)) {
        manejarTurnoIA();
    }
    else {
        // Actualizar interfaz para el nuevo jugador
        actualizarInterfazJugador();

    }

}

/**
 * Maneja el turno de un jugador IA
 * @return true si se ha completado el turno, false si hubo un error
 */
private boolean manejarTurnoIA() {
    try {
        // if (jugadorActual != null) {
        //     jugadorActual.setText("Turno de: " + jugadorActualNombre + " (IA)");
        // }
        
        Triple<String, Tuple<Integer, Integer>, Direction> jugadaIA = new Triple<>("null", null, null);
        int puntos = controlador.realizarTurnoPartida(jugadorActualNombre, jugadaIA);

        if (jugadaIA.getx().equals("") || jugadaIA.gety() == null) {
            // La IA no encontró movimientos válidos
            agregarEntradaHistorial(jugadorActualNombre, "P", 0);
            pasarTurno();
            return true;
        }
        
        String palabra = jugadaIA.getx();
        Tuple<Integer, Integer> posFinal = jugadaIA.gety();
        Direction direccion = jugadaIA.getz();
        
        colocarFichasIA(palabra, posFinal, direccion);
        controlador.actualizarJugador(jugadorActualNombre, puntos);
        agregarEntradaHistorial(jugadorActualNombre, palabra, puntos);        
        // actualizarInfoPartida();
        controlador.comprobarFinPartida(jugadoresPuntuaciones);
        
        if (controlador.isJuegoTerminado()) {
            finalizarPartida();
         } 
         else {
            siguienteTurno();
         }
        //   else {
        //     new Thread(() -> {
        //         try {
        //             Thread.sleep(1500); 
        //             Platform.runLater(() -> siguienteTurno());
        //         } catch (InterruptedException e) {
        //             e.printStackTrace();
        //             Platform.runLater(() -> siguienteTurno());
        //         }
        //     }).start();
        // }
        if (firstMove) firstMove = false;
        return true;
    } catch (Exception e) {
        e.printStackTrace();
        controlador.mostrarAlerta("error", "Error en turno IA", "Error al procesar el turno de la IA: " + e.getMessage());
        return false;
    }
}

/**
 * Coloca las fichas en el tablero para la jugada de la IA
 * @param palabra La palabra a colocar
 * @param posFinal La posición final de la palabra
 * @param direccion La dirección de la palabra (HORIZONTAL o VERTICAL)
 */
private void colocarFichasIA(String palabra, Tuple<Integer, Integer> posFinal, Direction direccion) {
    if (palabra == null || palabra.isEmpty() || posFinal == null) {
        return;
    }
    
    try {
        int filaFinal = posFinal.x;
        int columnaFinal = posFinal.y;
        int longitud = palabra.length();
        
        // Calcular la posición inicial basándonos en la posición final y dirección
        int filaInicial, columnaInicial;
        
        if (direccion == Direction.HORIZONTAL) {
            filaInicial = filaFinal;
            columnaInicial = columnaFinal - (longitud - 1);
        } else { 
            filaInicial = filaFinal - (longitud - 1);
            columnaInicial = columnaFinal;
        }
        
        // Recorrer palabra y colocar cada letra
        for (int i = 0; i < longitud; i++) {
            int fila = (direccion == Direction.HORIZONTAL) ? filaInicial : filaInicial + i;
            int columna = (direccion == Direction.HORIZONTAL) ? columnaInicial + i : columnaInicial;
            String letra = String.valueOf(palabra.charAt(i));
            
            // Verificar si ya hay una ficha en esa posición
            Tuple<Integer, Integer> pos = new Tuple<>(fila, columna);
            if (posiciones.get(pos) != null) {
                continue;
            }
            
            // Buscar la casilla correspondiente
            for (Node node : tablero.getChildren()) {
                if (node instanceof CasillaDisplay) {
                    CasillaDisplay casilla = (CasillaDisplay) node;
                    if (casilla.getFila() == fila && casilla.getColumna() == columna) {
                    
                        int puntos = obtenerPuntosPorLetra(String.valueOf(letra));                        
                        casilla.colocarFicha(letra, puntos, true);                        
                        posiciones.put(new Tuple<>(fila, columna), letra);
                        
                        break;
                    }
                }
            }
        }
        
        Platform.runLater(() -> tablero.requestLayout());
        
    } catch (Exception e) {
        System.err.println("Error al colocar fichas de IA: " + e.getMessage());
        e.printStackTrace();
    }
}

/**
 * Actualiza la interfaz para mostrar la información del jugador actual
 */
private void actualizarInterfazJugador() {
    if (jugadorActual != null) {
        jugadorActual.setText("Turno de: " + jugadorActualNombre);
    }
    
    actualizarInfoPartida();
    actualizarRackJugador();
}

/**
 * Actualiza la información de la partida (puntuaciones, fichas restantes)
 */
private void actualizarInfoPartida() {
    actualizarPuntuaciones();    
    actualizarFichasRestantes();
}
/**
 * Actualiza la visualización de puntuaciones de los jugadores
 */
private void actualizarPuntuaciones() {
    if (puntuacionesContainer != null) {
        puntuacionesContainer.getChildren().clear();
        
        Label lblTitulo = new Label("Puntuaciones");
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #333;");
        puntuacionesContainer.getChildren().add(lblTitulo);
        
        jugadoresPuntuaciones = controlador.getJugadoresActuales();
        
        for (Map.Entry<String, Integer> entry : jugadoresPuntuaciones.entrySet()) {
            String nombre = entry.getKey();
            int puntos = entry.getValue();
            
            HBox jugadorRow = new HBox(10);
            jugadorRow.setAlignment(Pos.CENTER_LEFT);
            
            Label lblNombre = new Label(nombre + ":");
            lblNombre.setPrefWidth(150);
            lblNombre.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
            
            Label lblPuntos = new Label(String.valueOf(puntos) + " pts");
            lblPuntos.setStyle("-fx-font-size: 14px; -fx-text-fill: #333; -fx-font-weight: bold;");
            
            // Resaltar al jugador actual
            if (nombre.equals(jugadorActualNombre)) {
                lblNombre.setStyle("-fx-font-size: 14px; -fx-text-fill: #ff9800; -fx-font-weight: bold;");
                lblPuntos.setStyle("-fx-font-size: 14px; -fx-text-fill: #ff9800; -fx-font-weight: bold;");
            }
            
            jugadorRow.getChildren().addAll(lblNombre, lblPuntos);
            puntuacionesContainer.getChildren().add(jugadorRow);
        }
    }
}

/**
 * Actualiza la visualización de las fichas restantes en la bolsa
 */
private void actualizarFichasRestantes() {
    int fichasRestantes = controlador.getCantidadFichasRestantes();    
    if (lblFichasRestantes != null) {
        lblFichasRestantes.setText("Fichas en la bolsa: " + fichasRestantes);
    }
}

/**
 * Actualiza el rack de fichas del jugador actual
 */
private void actualizarRackJugador() {
    if (fichasJugador != null) {
        fichasJugador.getChildren().clear();
        rackActual = controlador.getRackJugador(jugadorActualNombre);
        
        if (rackActual == null) {
            controlador.mostrarAlerta("error", "Error", "No se pudo obtener el rack del jugador");
            return;
        }
        
        // Crear fichas para cada letra en el rack
        for (Map.Entry<String, Integer> entry : rackActual.entrySet()) {
            String letra = entry.getKey();
            int cantidad = entry.getValue();
            
            // Puntos para la letra (esto podría obtenerse del controlador)
            int puntos = obtenerPuntosPorLetra(letra);
            
            for (int i = 0; i < cantidad; i++) {
                Ficha ficha = new Ficha(letra, puntos);
                ficha.setMinSize(CASILLA_SIZE, CASILLA_SIZE);
                ficha.setPrefSize(CASILLA_SIZE, CASILLA_SIZE);
                ficha.setMaxSize(CASILLA_SIZE, CASILLA_SIZE);
                
                configurarEventosFicha(ficha);
                fichasJugador.getChildren().add(ficha);
            }
        }

        
    }
}

/**
 * Añade una entrada al historial de jugadas
 * @param jugadorNombre Nombre del jugador
 * @param palabra Palabra jugada (o descripción de acción)
 * @param puntos Puntos obtenidos
 */
private void agregarEntradaHistorial(String jugadorNombre, String palabra, int n) {
    String entrada;
    
    // Crear la entrada según el tipo de acción
    if (palabra.equals("P")) {
        entrada = jugadorNombre + " pasó turno";
    } else if (palabra.equals("CF")) {
        entrada = jugadorNombre + " cambió (" + n + ") fichas";
    } else {
        entrada = jugadorNombre + " colocó palabra " + palabra + " (" + n + " pts)";
    }
    
    // Añadir al principio (más reciente arriba)
    historialJugadas.add(0, entrada);
    
    // Limitar el tamaño del historial
    if (historialJugadas.size() > MAX_HISTORIAL_ENTRIES) {
        historialJugadas.remove(historialJugadas.size() - 1);
    }
    
    actualizarVistaHistorial();
}

/**
 * Actualiza la visualización del historial en la interfaz
 */
private void actualizarVistaHistorial() {
    if (historialContainer != null) {
        // Limpiar el contenedor
        historialContainer.getChildren().clear();
        
        for (String entrada : historialJugadas) {
            Label lblEntrada = new Label(entrada);
            lblEntrada.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
            
            // Destacar jugadas con alta puntuación 
            if (entrada.contains(" pts)")) {
                int ptsIndex = entrada.lastIndexOf("(") + 1;
                int ptsEndIndex = entrada.lastIndexOf(" pts)");
                try {
                    int pts = Integer.parseInt(entrada.substring(ptsIndex, ptsEndIndex));
                    if (pts > 15) {
                        lblEntrada.setStyle("-fx-font-size: 14px; -fx-text-fill: #ff9800; -fx-font-weight: bold;");
                    }
                } catch (NumberFormatException e) {
                    break;
                }
            }
            
            historialContainer.getChildren().add(lblEntrada);
        }
    }
}
/**
 * Obtiene los puntos correspondientes a una letra
 */
public int obtenerPuntosPorLetra(String letra) {
    Integer puntos = controlador.obtenerPuntosPorLetra(letra);
    return puntos != null ? puntos : 0; // Devuelve 0 si es null
    
}

/**
 * Construye una palabra dada la direccion
 * @param d la direccion
 * @param fichas las fichas colocadas  por el jugador en el turno actual ordenadas por posición creciente
 * @return Posible palabra 
 */
private Triple<StringBuilder, Integer, Integer> buildPalabraHorizontal(Direction d, List<FichaColocada> fichas) {
    StringBuilder palabra = new StringBuilder();
    int start;
    int end;
    int fila = fichas.get(0).fila;

    if (d == Direction.HORIZONTAL) {
        end = fichas.get(fichas.size() -1 ).columna;
        start = fichas.get(0).columna-1;
        System.err.println("Letra ordenada 1: " + fichas.get(0).letra + " | Su fila, col es: " + fichas.get(0).fila + "," + fichas.get(0).columna + "| Valor de Start: " + start);
        Tuple <Integer, Integer> posicion = new Tuple<> (fila, start);

        if (posiciones.get(posicion) == null) start = fichas.get(0).columna;
        // Posibles letras contiguas a la izquierda
        while (posiciones.get(posicion) != null && start > 0) {
            palabra.insert(0, posiciones.get(posicion));
            System.err.println("Contigua izquierda: " + palabra);
            start--;
            posicion =  new Tuple<> (fila, start);
        }

        
        // Rellenar letras entre medias
        for (int i = 0; i < fichas.size(); i++) {
            palabra.append(fichas.get(i).letra);
            
            System.err.println("Contigua entre medias: " + palabra);
            if (i + 1 < fichas.size()) {
                int nextLetterDist = fichas.get(i + 1).columna - fichas.get(i).columna;
                System.err.println("La distancia entre " + fichas.get(i).letra + " y " + fichas.get(i+1).letra + "es de: " + nextLetterDist + "| " +  fichas.get(i + 1).columna + " "+  fichas.get(i).columna);
                if (nextLetterDist > 1) {
                    System.err.println("He entrado al if");

                    for (int col = fichas.get(i).columna + 1; col < fichas.get(i+1).columna; col++) {
                        Tuple <Integer, Integer> pos = new Tuple<> (fila, col);
                        System.err.println("He entrado al for");

                        if (posiciones.get(pos) != null) {
                            palabra.append(posiciones.get(pos));
                            System.err.println("He colocado letra");

                        }
                        else {
                            // Hay un hueco entre las palabras colocadas, jugada inválida.
                            System.err.println("Hay un hueco en la palabra, porque en la pos:" + pos.x + ", " + pos.y + " no hay letra :(");
                            System.err.println("Iterando sobre letra " + fichas.get(i).letra + " tiene su siguiente posicion en (x,y): " + fichas.get(i).fila + fichas.get(i).columna);

                            return new Triple<StringBuilder,Integer,Integer>(new StringBuilder(), -1, -1);
                        }
                    }
                }            
            
            }
        }

        boolean finished = false;
        // Posibles letras contiguas a la derecha
        for (int col = fichas.get(fichas.size() - 1).columna + 1; col < N && !finished; col++) {
            System.err.println("Contigua en derecha: " + palabra);
            Tuple <Integer, Integer> pos = new Tuple<> (fila, col);
            if (posiciones.get(pos) != null) {
                palabra.append(posiciones.get(pos));
                end = col;
            }
            else {
                finished = true;
            }
        }        

    }
    else {
        return new Triple<StringBuilder,Integer,Integer>(new StringBuilder(), -1, -1);
    }

    return new Triple<StringBuilder,Integer,Integer>(palabra, fila, end);
}


/**
 * Construye una palabra dada la direccion vertical
 * @param d la direccion
 * @param fichas las fichas colocadas por el jugador en el turno actual ordenadas por posición creciente
 * @return Posible palabra 
 */
private Triple<StringBuilder, Integer, Integer> buildPalabraVertical(Direction d, List<FichaColocada> fichas) {
    System.err.println("HE ENTRADO EN BUILD VERTICAL");
    StringBuilder palabra = new StringBuilder();
    int start;
    int end;
    int columna = fichas.get(0).columna;

    if (d == Direction.VERTICAL) {
        end = fichas.get(fichas.size() - 1).fila;
        start = fichas.get(0).fila - 1;
        System.err.println("Letra ordenada 1: " + fichas.get(0).letra + " | Su fila, col es: " + fichas.get(0).fila + "," + fichas.get(0).columna + "| Valor de Start: " + start);
        Tuple<Integer, Integer> posicion = new Tuple<>(start, columna);

        if (posiciones.get(posicion) == null) start = fichas.get(0).fila;
        // Posibles letras contiguas arriba
        while (posiciones.get(posicion) != null && start > 0) {
            palabra.insert(0, posiciones.get(posicion));
            System.err.println("Contigua arriba: " + palabra);
            start--;
            posicion = new Tuple<>(start, columna);
        }

        // Rellenar letras entre medias
        for (int i = 0; i < fichas.size(); i++) {
            palabra.append(fichas.get(i).letra);
            
            System.err.println("Contigua entre medias: " + palabra);
            if (i + 1 < fichas.size()) {
                int nextLetterDist = fichas.get(i + 1).fila - fichas.get(i).fila;
                System.err.println("La distancia entre " + fichas.get(i).letra + " y " + fichas.get(i+1).letra + "es de: " + nextLetterDist + "| " +  fichas.get(i + 1).fila + " "+  fichas.get(i).fila);

                if (nextLetterDist > 1) {
                    System.err.println("He entrado al if");
                    // for (int fila = fichas.get(i).fila+1; fila < nextLetterDist; fila++) {
                    for (int fila = fichas.get(i).fila + 1; fila < fichas.get(i + 1).fila; fila++) {                    
                        Tuple<Integer, Integer> pos = new Tuple<>(fila, columna);
                        System.err.println("He entrado al for");

                        if (posiciones.get(pos) != null) {
                            palabra.append(posiciones.get(pos));
                            System.err.println("He colocado letra");

                        }
                        else {
                            // Hay un hueco entre las palabras colocadas, jugada inválida.
                            System.err.println("Hay un hueco en la palabra, porque en la pos:" + pos.x + ", " + pos.y + " no hay letra :(");
                            System.err.println("Iterando sobre letra " + fichas.get(i).letra + " tiene su siguiente posicion en (x,y): " + fichas.get(i).fila + fichas.get(i).columna);

                            return new Triple<StringBuilder, Integer, Integer>(new StringBuilder(), -2, -2);
                        }
                    }
                }            
            }
        }

        boolean finished = false;
        // Posibles letras contiguas abajo
        for (int fila = fichas.get(fichas.size() - 1).fila + 1; fila < N && !finished; fila++) {
            System.err.println("Contigua abajo: " + palabra);
            Tuple<Integer, Integer> pos = new Tuple<>(fila, columna);
            if (posiciones.get(pos) != null) {
                palabra.append(posiciones.get(pos));
                end = fila;
            }
            else {
                finished = true;
            }
        }        
    }
    else {
        return new Triple<StringBuilder, Integer, Integer>(new StringBuilder(), -1, -1);
    }

    return new Triple<StringBuilder, Integer, Integer>(palabra, end, columna);
}
/**
 * Verifica si las fichas colocadas en el turno actual forman una jugada válida
 */
private Tuple<Boolean, Triple<String, Tuple <Integer, Integer>, Direction>> verificarJugadaValida() {

    Tuple<Boolean,Triple<String,Tuple<Integer,Integer>,Direction>> result = new Tuple<Boolean,Triple<String,Tuple<Integer,Integer>,Direction>>(false, new Triple<>("", new Tuple<>(-1, -1), Direction.HORIZONTAL));
    if (fichasColocadasEnTurnoActual.isEmpty()) {
        return result;
    }
    
    // Construir la palabra y obtener la última ficha colocada
    StringBuilder palabra = new StringBuilder();
    Tuple<Integer, Integer> posicionUltimaLetra = null;
    
    // Si hay más de una ficha, determinar la dirección
    if (fichasColocadasEnTurnoActual.size() > 1) {
        // Ordenar las fichas por posición
        List<FichaColocada> fichasOrdenadas = new ArrayList<>(fichasColocadasEnTurnoActual);
        int fila1 = fichasOrdenadas.get(0).fila;
        int col1 = fichasOrdenadas.get(0).columna;
        boolean mismaFila = true;
        boolean mismaColumna = true;
        
        for (int i = 1; i < fichasOrdenadas.size(); i++) {
            if (fichasOrdenadas.get(i).fila != fila1) {
                mismaFila = false;
            }
            if (fichasOrdenadas.get(i).columna != col1) {
                mismaColumna = false;
            }
        }
        
        if (mismaFila && !mismaColumna) {
            direccionActual = Direction.HORIZONTAL;
            // Ordenar por columna
            fichasOrdenadas.sort(Comparator.comparingInt(f -> f.columna));
        } else if (!mismaFila && mismaColumna) {
            direccionActual = Direction.VERTICAL;
            // Ordenar por fila
            fichasOrdenadas.sort(Comparator.comparingInt(f -> f.fila));
        } else {
            // No alineadas
            return result;
        }

        System.out.println("LA DIRECCION ES: " + direccionActual);
        // Triple <StringBuilder, Integer, Integer> possibleWord = buildPalabraHorizontal(direccionActual, fichasOrdenadas);
        Triple <StringBuilder, Integer, Integer> possibleWord = direccionActual == Direction.HORIZONTAL ? buildPalabraHorizontal(direccionActual, fichasOrdenadas) : buildPalabraVertical(direccionActual, fichasOrdenadas);
        palabra = possibleWord.getx();

        if (palabra.length() == 0|| possibleWord.y == -1 || possibleWord.z == -1) {
            System.err.println("Error al intentar colocar la palabra: " + palabra);
            System.err.println("Error palabra o posiciones érroneas (x, y): " + possibleWord.x + "," + possibleWord.y);
            return result;
        } 
     
            
        posicionUltimaLetra = new Tuple<>(possibleWord.gety(), possibleWord.getz());
        System.err.println("posiciones (x, y): " + possibleWord.gety() + possibleWord.getz());

    }
    else {
        // Cuando solo hay una ficha colocada
        if (fichasColocadasEnTurnoActual.size() == 1) {
            FichaColocada ficha = fichasColocadasEnTurnoActual.get(0);
            List<FichaColocada> fichasList = new ArrayList<>();
            fichasList.add(ficha);
            
            // Intentar construir palabra horizontal
            Triple<StringBuilder, Integer, Integer> palabraHorizontal = 
                buildPalabraHorizontal(Direction.HORIZONTAL, fichasList);
            
            // Intentar construir palabra vertical
            Triple<StringBuilder, Integer, Integer> palabraVertical = 
                buildPalabraVertical(Direction.VERTICAL, fichasList);
            
            // Verificar si alguna dirección produjo una palabra válida (de longitud >= 2)
            if (palabraHorizontal.getx().length() >= 2) {
                // Usar la palabra horizontal
                palabra = palabraHorizontal.getx();
                direccionActual = Direction.HORIZONTAL;
                posicionUltimaLetra = new Tuple<>(ficha.fila, palabraHorizontal.getz());
            } else if (palabraVertical.getx().length() >= 2) {
                // Usar la palabra vertical
                palabra = palabraVertical.getx();
                direccionActual = Direction.VERTICAL;
                posicionUltimaLetra = new Tuple<>(palabraVertical.gety(), ficha.columna);
            } else {
                // No se formó ninguna palabra válida en ninguna dirección
                return new Tuple<>(false, new Triple<>("", new Tuple<>(-1, -1), Direction.HORIZONTAL));
            }
            
            System.err.println("Se ha intentado colocar la palabra: " + palabra);
            
            // Crear la jugada con la posición de la última letra
            Triple<String, Tuple<Integer, Integer>, Direction> jugada = 
                new Triple<>(
                    palabra.toString(), 
                    posicionUltimaLetra, 
                    direccionActual
                );
            
            // Verificar con el controlador
            Boolean valid = jugada.getx().contains("#") ? true : controlador.esMovimientoValido(jugada, rackActual);
            return new Tuple<>(valid, jugada);
        }
    
    }
    
    System.err.println("Se ha intentado colocar la palabra: " + palabra);
    
    // Crear la jugada con la posición de la última letra
    Triple<String, Tuple<Integer, Integer>, Direction> jugada = 
        new Triple<>(
            palabra.toString(), 
            posicionUltimaLetra, 
            direccionActual
        );
    
    
    Boolean valid = jugada.getx().contains("#") ? true : controlador.esMovimientoValido(jugada, rackActual);
    return new Tuple<Boolean,Triple<String,Tuple<Integer,Integer>,Direction>>(valid, jugada);
}
    /**
    * Confirma la jugada actual
    */
    private void confirmarJugada() {
        if (fichasColocadasEnTurnoActual.isEmpty()) {
            controlador.mostrarAlerta("warning", "Jugada vacía", "No has colocado ninguna ficha en el tablero, coloca al menos una!");
            return;
        }

        if (firstMove) {
            boolean centrado = false;
            boolean contiguo = false;
            Tuple<Integer, Integer> center = new Tuple<Integer,Integer>(N/2, N/2); 
            for (FichaColocada f : fichasColocadasEnTurnoActual) {
                if (f.fila == center.x && f.columna == center.y) {
                    centrado = true;
                }
                ArrayList<Tuple<Integer, Integer>> dirs = new ArrayList<>(Arrays.asList(
                    new Tuple<>(1, 0), 
                    new Tuple<>(-1, 0), 
                    new Tuple<>(0, 1), 
                    new Tuple<>(0, -1)
                ));

                for (Tuple<Integer, Integer> d : dirs) {
                    if (!firstMove && posiciones.get(new Tuple<>(f.fila + d.x, f.columna + d.y)) != null) {
                        contiguo = true;
                    }
                }

            } 

            if(!centrado) {
                controlador.mostrarAlerta("warning", "Jugada inválida", "¡Es el primer turno, debes de colocar una ficha en el centro del tablero!");
                cancelarJugada();
                return;
            } 

            if(!contiguo && !firstMove) {
                controlador.mostrarAlerta("warning", "Jugada inválida", "¡Movimiento ilegal por las reglas del juego, la palabra debe estar contigua con al menos una ficha del tablero!");
                cancelarJugada();
                return;
            }                           
        }

        


        Tuple<Boolean, Triple<String, Tuple <Integer, Integer>, Direction>> result = verificarJugadaValida();        
        // Verificar si la jugada es válida
        if (!result.x) {
            controlador.mostrarAlerta("warning", "Jugada inválida", "¡No existe la palabra colocada!");
            cancelarJugada();
            return;
        }
        
        Triple<String, Tuple <Integer, Integer>, Direction> jugada = result.y;
        
        try {
            // Realizar el turno
            int puntos = controlador.realizarTurnoPartida(jugadorActualNombre, jugada);
            agregarEntradaHistorial(jugadorActualNombre, jugada.getx(), puntos);
            // Actualizar puntuación
            controlador.actualizarJugador(jugadorActualNombre, puntos);
            
            // Mostrar resultado
            for (FichaColocada ficha : fichasColocadasEnTurnoActual) {
                Integer fila = ficha.fila;
                Integer columna = ficha.columna;

                posiciones.put(new Tuple<>(ficha.fila, ficha.columna), ficha.getLetra());
                // Estilo ficha confirmada
                for (Node node : tablero.getChildren()) {
                    if (node instanceof CasillaDisplay) {
                        CasillaDisplay casilla = (CasillaDisplay) node;
                        if (casilla.getFila() == ficha.fila && casilla.getColumna() == ficha.columna) {
                            casilla.marcarFichaComoConfirmada();
                        }
                    }
                }
            }
            
            if (firstMove) firstMove = false;

            // Limpiar lista de fichas colocadas
            fichasColocadasEnTurnoActual.clear();
            
            // Comprobar fin de partida
            controlador.comprobarFinPartida(jugadoresPuntuaciones);
            
            if (controlador.isJuegoTerminado()) {
                actualizarInfoPartida();            
                finalizarPartida();
            } else {
                // Pasar al siguiente jugador
                siguienteTurno();
            }
        } catch (Exception e) {
            e.printStackTrace();
            controlador.mostrarAlerta("error", "Error", "Error al confirmar la jugada: " + e.getMessage());
        }
    }


/**
* Inicializa el tablero con las casillas especiales y numeración
*/
private void inicializarTablero() {
    if (tablero != null) {
        // Reset por si se cargó un tablero anteriormente
        tablero.getChildren().clear();
        if (filasNumeracion != null) filasNumeracion.getChildren().clear();
        if (columnasNumeracion != null) columnasNumeracion.getChildren().clear();
        
        // Estilizar enumaraciones
        if (filasNumeracion != null) {
            filasNumeracion.setStyle("-fx-background-color: white; -fx-padding: 5; -fx-min-width: 30;");
            filasNumeracion.setMinWidth(30);
            filasNumeracion.setPrefWidth(30);
            filasNumeracion.setVisible(true);
        }
        
        if (columnasNumeracion != null) {
            columnasNumeracion.setStyle("-fx-background-color: white; -fx-padding: 5; -fx-min-height: 30;");
            columnasNumeracion.setMinHeight(30);
            columnasNumeracion.setPrefHeight(30);
            columnasNumeracion.setVisible(true);
        }
        
        // Crear espacio en la esquina superior
        if (columnasNumeracion != null) {
            Label cornerLabel = new Label("");
            cornerLabel.setMinSize(30, 30);
            cornerLabel.setPrefSize(30, 30);
            cornerLabel.setStyle("-fx-background-color: white;");
            columnasNumeracion.getChildren().add(cornerLabel);
        }
        
        // Crear numeración de columnas
        for (int col = 0; col < N; col++) {
            Label colLabel = new Label(String.valueOf(col));
            colLabel.setMinSize(CASILLA_SIZE, 30);
            colLabel.setMaxSize(CASILLA_SIZE, 30);
            colLabel.setPrefSize(CASILLA_SIZE, 30);
            colLabel.setAlignment(Pos.CENTER);
            colLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333333;");
            
            if (columnasNumeracion != null) {
                columnasNumeracion.getChildren().add(colLabel);
            }
        }
        
        // Crea tablero NxN
        for (int fila = 0; fila < N; fila++) {
            // Añadir numeración de fila
            if (filasNumeracion != null) {
                Label rowLabel = new Label(String.valueOf(fila));
                rowLabel.setMinSize(30, CASILLA_SIZE);
                rowLabel.setMaxSize(30, CASILLA_SIZE);
                rowLabel.setPrefSize(30, CASILLA_SIZE);
                rowLabel.setAlignment(Pos.CENTER);
                rowLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333333;");
                filasNumeracion.getChildren().add(rowLabel);
            }
            
            for (int col = 0; col < N; col++) {
                CasillaDisplay casilla = new CasillaDisplay(fila, col, CASILLA_SIZE);
                
                definirTipoCasilla(casilla, fila, col);
                
                // Configurar evento para detectar cuando se pasa el ratón sobre la casilla
                final int finalFila = fila;
                final int finalCol = col;
                
                casilla.setOnMouseEntered(e -> {
                    if (fichaSeleccionada != null && !casilla.tieneFicha()) {
                        casilla.resaltar(true);
                    }
                });
                
                casilla.setOnMouseExited(e -> {
                    if (fichaSeleccionada != null) {
                        casilla.resaltar(false);
                    }
                });
                
                // Detectar clic en la casilla para colocar ficha seleccionada
                casilla.setOnMouseClicked(e -> {
                    if (fichaSeleccionada != null && !casilla.tieneFicha()) {
                        colocarFichaEnCasilla(casilla, finalFila, finalCol);
                    } else if (casilla.tieneFicha() && !casilla.esFichaConfirmada() && fichaSeleccionada == null) {
                        // Retirar ficha no confirmada al hacer clic
                        retirarFichaDeCasilla(casilla);
                    }
                });
                
                tablero.add(casilla, col, fila);
            }

        }
        
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                posiciones.put(new Tuple<>(i, j), null);
            }   
        }
        // Centrar el tablero en el área de visualización
        centrarTableroEnScrollPane();
    }
}

/**
 * Pasa el turno al siguiente jugador
 */
private void pasarTurno() {
    try {
        cancelarJugada();
        // Crear una jugada de "pasar turno"
        Triple<String, Tuple<Integer, Integer>, Direction> pasarJugada = 
            new Triple<>("P", null, null);
        
        // Realizar el turno
        controlador.realizarTurnoPartida(jugadorActualNombre, pasarJugada);
        agregarEntradaHistorial(jugadorActualNombre, "P", 0);        
        // Comprobar fin de partida
        
        if (controlador.isJuegoTerminado()) {
            System.err.println("Desde jugador " + jugadorActualNombre + " se ha acabado la partida");
            finalizarPartida();
        } else {
            // showAlert(Alert.AlertType.INFORMATION, "Turno pasado", 
            //     "Has pasado tu turno. Ahora es el turno de: " + jugadores.get((jugadorActualIndex + 1) % jugadores.size()));
            
            // Pasar al siguiente jugador
            siguienteTurno();
            
            actualizarInfoPartida();            
        }
    } catch (Exception e) {
        e.printStackTrace();
        // showAlert(Alert.AlertType.ERROR, "Error", "Error al pasar turno: " + e.getMessage());
    }
}

/**
 * Cambia las fichas seleccionadas por otras nuevas de la bolsa
 */
private void cambiarFichas() {
    try {
        // Primero, cancelar cualquier jugada en curso para devolver las fichas al rack
        cancelarJugada();
        
        // Obtener el rack actual del jugador
        rackActual = controlador.getRackJugador(jugadorActualNombre);
        if (rackActual == null || rackActual.isEmpty()) {
            controlador.mostrarAlerta("error", "Error", "No se pudo obtener el rack del jugador o está vacío");
            return;
        }
        
        // Mostrar el popup para que el usuario seleccione las fichas
        CambiarFichasPopup popup = new CambiarFichasPopup(rackActual, CASILLA_SIZE, this);
        List<String> fichasACambiar = popup.mostrarYEsperar();
        if (fichasACambiar.isEmpty()) {
            return; 
        }
        if (controlador.getCantidadFichasRestantes() < fichasACambiar.size()) {
            controlador.mostrarAlerta("error", "Error", 
    "No se pudieron cambiar las fichas, hay menos fichas disponibles de las que quieres cambiar"); 
            return;       
        }
                
        // Realizar el intercambio de fichas
        boolean intercambioExitoso = controlador.intercambiarFichas(jugadorActualNombre, fichasACambiar);
        
        if (intercambioExitoso) {
            actualizarInfoPartida();
            
            Triple<String, Tuple<Integer, Integer>, Direction> cambiarJugada = 
                new Triple<>("CF", null, null);
            
            controlador.realizarTurnoPartida(jugadorActualNombre, cambiarJugada);
            
            agregarEntradaHistorial(jugadorActualNombre, "CF", fichasACambiar.size());
            
            // Actualizar el rack
            actualizarRackJugador();
            
            // Comprobar fin de partida
            controlador.comprobarFinPartida(jugadoresPuntuaciones);
            
            if (controlador.isJuegoTerminado()) {
                finalizarPartida();
            } else {
                // Pasar al siguiente jugador
                siguienteTurno();
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
        controlador.mostrarAlerta("error", "Error", "Error al cambiar fichas: " + e.getMessage());
    }
}     

/**
 * Finaliza la partida y muestra el resultado detallado
 */
private void finalizarPartida() {
    try {
        jugadoresPuntuaciones = controlador.getJugadoresActuales();
        
        // Determinar el ganador y calcular estadísticas
        String ganador = determinarGanador();
        String estadisticasFinales = generarEstadisticasFinales();
        String mensajeCompleto = construirMensajeFinal(ganador, estadisticasFinales);
        
        // Liberar jugadores de la partida actual si no está cargada
        if (!controlador.getCargado()) {
            controlador.liberarJugadores();
        } else {
            controlador.setCargado(false);
        }
        
        mostrarResultadoFinal(mensajeCompleto);
        
    } catch (Exception e) {
        e.printStackTrace();
        controlador.mostrarAlerta("error", "Error", "Error al finalizar la partida: " + e.getMessage());
        controlador.volver();
    }
}

/**
 * Determina el ganador de la partida
 */
private String determinarGanador() {
    if (jugadoresPuntuaciones == null || jugadoresPuntuaciones.isEmpty()) {
        return "No hay jugadores";
    }
    
    // Encontrar la puntuación máxima
    int puntuacionMaxima = jugadoresPuntuaciones.values().stream()
            .mapToInt(Integer::intValue)
            .max()
            .orElse(0);
    
    // Encontrar todos los jugadores con la puntuación máxima (por si hay empate)
    List<String> ganadores = jugadoresPuntuaciones.entrySet().stream()
            .filter(entry -> entry.getValue() == puntuacionMaxima)
            .map(Map.Entry::getKey)
            .collect(java.util.stream.Collectors.toList());
    
    if (ganadores.size() == 1) {
        return ganadores.get(0);
    } else {
        return "Empate entre: " + String.join(", ", ganadores);
    }
}

/**
 * Genera estadísticas finales de la partida
 */
private String generarEstadisticasFinales() {
    StringBuilder stats = new StringBuilder();
    
    // Puntuaciones finales ordenadas de mayor a menor
    List<Map.Entry<String, Integer>> puntuacionesOrdenadas = jugadoresPuntuaciones.entrySet()
            .stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .collect(java.util.stream.Collectors.toList());
    
    stats.append("PUNTUACIONES FINALES:\n");
    for (int i = 0; i < puntuacionesOrdenadas.size(); i++) {
        Map.Entry<String, Integer> entry = puntuacionesOrdenadas.get(i);
        String posicion = (i + 1) + "º";
        String tipoJugador = controlador.esIA(entry.getKey()) ? " (IA)" : " (Humano)";
        stats.append(String.format("%s %s%s: %d puntos\n", 
                posicion, entry.getKey(), tipoJugador, entry.getValue()));
    }
    
    return stats.toString();
}

/**
 * Construye el mensaje final completo
 */
private String construirMensajeFinal(String ganador, String estadisticas) {
    StringBuilder mensaje = new StringBuilder();
    
    // Título principal
    mensaje.append(" ¡PARTIDA FINALIZADA! \n\n");
    
    // Ganador
    if (ganador.startsWith("Empate")) {
        mensaje.append(ganador).append("!\n\n");
    } else {
        String tipoGanador = controlador.esIA(ganador) ? " (IA)" : " (Humano)";
        mensaje.append("🏆 ¡Ganador: ").append(ganador).append(tipoGanador).append("!\n\n");
    }
    
    // Estadísticas
    mensaje.append(estadisticas);
    
    return mensaje.toString();
}

/**
 * Muestra el resultado final usando el popup de pausa
 */
private void mostrarResultadoFinal(String mensaje) {

    List<PausaPopup.PopupButton> buttons = List.of(
        new PausaPopup.PopupButton("Salir", PausaPopup.ButtonStyle.INFO, 
            stage -> {
                stage.close();
                Platform.runLater(() -> {
                    controlador.volver();
                });
            })
    );
    
    PausaPopup.show("Resultado Final", mensaje, buttons);
}

/**
 * Centra el tablero en el ScrollPane para una mejor visualización
 */
private void centrarTableroEnScrollPane() {
    if (scrollPane != null) {
        // Limpiar cualquier contenido previo
        scrollPane.setContent(null);
        
        // Crear un nuevo contenedor para el tablero y las numeraciones
        BorderPane tableroConNumeracion = new BorderPane();
        int leftPadding;
        if (N == 15) leftPadding = 150;
        else leftPadding = 0;
        
        tableroConNumeracion.setPadding(new Insets(20,0, 0,leftPadding));
        tableroConNumeracion.setCenter(tablero);
        tableroConNumeracion.setLeft(filasNumeracion);
        tableroConNumeracion.setTop(columnasNumeracion);
        tableroConNumeracion.setStyle("-fx-background-color: white;");
        
        // Calcular el tamaño necesario
        double tableroWidth = N * CASILLA_SIZE;
        double tableroHeight = N * CASILLA_SIZE;
        
        // Configurar un StackPane para centrar todo
        StackPane contenedorCentrado = new StackPane();
        contenedorCentrado.setPadding(new Insets(20));
        contenedorCentrado.getChildren().add(tableroConNumeracion);
        StackPane.setAlignment(tableroConNumeracion, Pos.CENTER);
        
        // Establecer el contenido del scrollPane
        scrollPane.setContent(contenedorCentrado);
        
        // Configuración del ScrollPane
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setFitToWidth(false);
        scrollPane.setFitToHeight(false);
        
        aplicarEstiloScrollbars();
        
        // Centrar el viewport después de renderizar
        Platform.runLater(() -> {
            scrollPane.layout();
            scrollPane.setHvalue(0.5);
            scrollPane.setVvalue(0.5);
            
        });
    }
}

/**
 * Aplica un estilo a las barras de desplazamiento
 */
private void aplicarEstiloScrollbars() {
    if (scrollPane != null) {
        scrollPane.getStyleClass().add("modern-scroll-pane");        
        String cssPath = getClass().getResource("/styles/partida.css").toExternalForm();
        scrollPane.getStylesheets().add(cssPath);
        
        Platform.runLater(() -> {
            if (scrollPane.getScene() != null && !scrollPane.getScene().getStylesheets().contains(cssPath)) {
                scrollPane.getScene().getStylesheets().add(cssPath);
            }
        });
        
        // scrollpane listeners
        scrollPane.viewportBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            if (newBounds != null && scrollPane.getContent() != null) {
                double contentWidth = scrollPane.getContent().getBoundsInLocal().getWidth();
                double contentHeight = scrollPane.getContent().getBoundsInLocal().getHeight();
                
                // Comprobar si necesitamos scrollbars
                boolean needsHorizontalScroll = contentWidth > newBounds.getWidth();
                boolean needsVerticalScroll = contentHeight > newBounds.getHeight();
                
                Platform.runLater(() -> {
                    scrollPane.setHbarPolicy(needsHorizontalScroll ? 
                        ScrollPane.ScrollBarPolicy.AS_NEEDED : 
                        ScrollPane.ScrollBarPolicy.NEVER);
                    
                    scrollPane.setVbarPolicy(needsVerticalScroll ? 
                        ScrollPane.ScrollBarPolicy.AS_NEEDED : 
                        ScrollPane.ScrollBarPolicy.NEVER);
                });
            }
        });
        
        scrollPane.setFitToWidth(false);
        scrollPane.setFitToHeight(false);
    }
}
    /**
     * Define el tipo de casilla especial según su posición
     */
    private void definirTipoCasilla(CasillaDisplay casilla, int fila, int col) {
        if (N == 15) {
            // Triple Word (TP)
            List<Tuple<Integer, Integer>> twPositions = Arrays.asList(
                new Tuple<>(0, 0), new Tuple<>(0, 7), new Tuple<>(0, 14),
                new Tuple<>(7, 0), new Tuple<>(7, 14),
                new Tuple<>(14, 0), new Tuple<>(14, 7), new Tuple<>(14, 14)
            );
            
            // Triple Letter (TL)
            List<Tuple<Integer, Integer>> tlPositions = Arrays.asList(
                new Tuple<>(1, 5), new Tuple<>(1, 9),
                new Tuple<>(5, 1), new Tuple<>(5, 5), new Tuple<>(5, 9), new Tuple<>(5, 13),
                new Tuple<>(9, 1), new Tuple<>(9, 5), new Tuple<>(9, 9), new Tuple<>(9, 13),
                new Tuple<>(13, 5), new Tuple<>(13, 9)
            );
            
            // Double Word (DP)
            List<Tuple<Integer, Integer>> dwPositions = Arrays.asList(
                new Tuple<>(1, 1), new Tuple<>(1, 13),
                new Tuple<>(2, 2), new Tuple<>(2, 12),
                new Tuple<>(3, 3), new Tuple<>(3, 11),
                new Tuple<>(4, 4), new Tuple<>(4, 10),
                new Tuple<>(10, 4), new Tuple<>(10, 10),
                new Tuple<>(11, 3), new Tuple<>(11, 11),
                new Tuple<>(12, 2), new Tuple<>(12, 12),
                new Tuple<>(13, 1), new Tuple<>(13, 13)
            );
            
            // Double Letter (DL)
            List<Tuple<Integer, Integer>> dlPositions = Arrays.asList(
                new Tuple<>(0, 3), new Tuple<>(0, 11),
                new Tuple<>(2, 6), new Tuple<>(2, 8),
                new Tuple<>(3, 0), new Tuple<>(3, 7), new Tuple<>(3, 14),
                new Tuple<>(6, 2), new Tuple<>(6, 6), new Tuple<>(6, 8), new Tuple<>(6, 12),
                new Tuple<>(7, 3), new Tuple<>(7, 11),
                new Tuple<>(8, 2), new Tuple<>(8, 6), new Tuple<>(8, 8), new Tuple<>(8, 12),
                new Tuple<>(11, 0), new Tuple<>(11, 7), new Tuple<>(11, 14),
                new Tuple<>(12, 6), new Tuple<>(12, 8),
                new Tuple<>(14, 3), new Tuple<>(14, 11)
            );
            
            Tuple<Integer, Integer> posicionActual = new Tuple<>(fila, col);
            
            if (twPositions.contains(posicionActual)) {
                casilla.setTipo(TipoCasilla.PALABRA_TRIPLE);
            } else if (tlPositions.contains(posicionActual)) {
                casilla.setTipo(TipoCasilla.LETRA_TRIPLE);
            } else if (dwPositions.contains(posicionActual)) {
                casilla.setTipo(TipoCasilla.PALABRA_DOBLE);
            } else if (dlPositions.contains(posicionActual)) {
                casilla.setTipo(TipoCasilla.LETRA_DOBLE);
            } else if (fila == 7 && col == 7) {
                casilla.setTipo(TipoCasilla.CENTRO);
            } else {
                casilla.setTipo(TipoCasilla.NORMAL);
            }        
        }
        else {
            // Para tableros más grandes que 15x15, generar un patrón escalable
            List<Tuple<Integer, Integer>> twPositions = new ArrayList<>();
            List<Tuple<Integer, Integer>> tlPositions = new ArrayList<>();
            List<Tuple<Integer, Integer>> dwPositions = new ArrayList<>();
            List<Tuple<Integer, Integer>> dlPositions = new ArrayList<>();
            
            // Calcular el centro del tablero
            int centro = N / 2;
            
            
            // Esquinas - siempre en las cuatro esquinas
            twPositions.add(new Tuple<>(0, 0));
            twPositions.add(new Tuple<>(0, N-1));
            twPositions.add(new Tuple<>(N-1, 0));
            twPositions.add(new Tuple<>(N-1, N-1));
            
            // Determinar la cantidad y distribución en los bordes
            int numTPPorBorde;
            if (N % 2 != 0) { // N impar
                // Para N impar: incluir el centro en los TP y distribuir simétricamente
                numTPPorBorde = 3; 
                
                // Añadir TPs en el centro de cada borde
                twPositions.add(new Tuple<>(0, centro));
                twPositions.add(new Tuple<>(N-1, centro));
                twPositions.add(new Tuple<>(centro, 0));
                twPositions.add(new Tuple<>(centro, N-1));
            } else { // N par
             
                numTPPorBorde = 4; // Solo esquinas y 2 posiciones equidistantes por borde
                
                // Posiciones centrales en los bordes (dos por borde para N par)
                twPositions.add(new Tuple<>(0, centro-1));
                twPositions.add(new Tuple<>(0, centro+1));
                twPositions.add(new Tuple<>(N-1, centro-1));
                twPositions.add(new Tuple<>(N-1, centro+1));
                twPositions.add(new Tuple<>(centro-1, 0));
                twPositions.add(new Tuple<>(centro+1, 0));
                twPositions.add(new Tuple<>(centro-1, N-1));
                twPositions.add(new Tuple<>(centro+1, N-1));
            }
            
            // 2. Letra Triple (TL) 
            int offset = Math.max(N / 7, 2); // Distancia desde los bordes, mínimo 2
            
            // Patrones diagonales
            for (int i = offset; i < N-offset; i += offset * 2) {
                if (i != centro && Math.abs(i - centro) > 1) { // Evitar centro y adyacentes
                    tlPositions.add(new Tuple<>(i, i));
                    tlPositions.add(new Tuple<>(i, N-1-i));
                    tlPositions.add(new Tuple<>(N-1-i, i));
                    tlPositions.add(new Tuple<>(N-1-i, N-1-i));
                }
            }
            
            // Añadir posiciones adicionales en los bordes internos
            for (int i = offset * 2; i < N-offset*2; i += offset * 2) {
                tlPositions.add(new Tuple<>(offset, i));
                tlPositions.add(new Tuple<>(N-1-offset, i));
                tlPositions.add(new Tuple<>(i, offset));
                tlPositions.add(new Tuple<>(i, N-1-offset));
            }
            
            // 3. Palabra Doble (DP)
            // Patrones diagonales
            for (int i = 1; i < N/2; i += 2) {
                if (i > 0 && i < N-1 && i != centro) {
                    dwPositions.add(new Tuple<>(i, i));
                    dwPositions.add(new Tuple<>(i, N-1-i));
                    dwPositions.add(new Tuple<>(N-1-i, i));
                    dwPositions.add(new Tuple<>(N-1-i, N-1-i));
                }
            }
            
            // 4. Letra Doble (DL)
            // Patrón de cruz centrado
            for (int i = 2; i < N-2; i += 2) {
                if (Math.abs(i - centro) > 2) { // Evitar cerca del centro
                    dlPositions.add(new Tuple<>(centro, i));
                    dlPositions.add(new Tuple<>(i, centro));
                }
            }
            
            // Añadir posiciones adicionales alrededor del borde
            for (int i = 2; i < N-2; i += 3) {
                if (i != centro && i % 2 != 0) { 
                    dlPositions.add(new Tuple<>(2, i));
                    dlPositions.add(new Tuple<>(N-3, i));
                    dlPositions.add(new Tuple<>(i, 2));
                    dlPositions.add(new Tuple<>(i, N-3));
                }
            }
            
            // 5. Casilla Central 
            
            // Aplicar el tipo de casilla según la posición
            Tuple<Integer, Integer> posicionActual = new Tuple<>(fila, col);
            
            if (fila == centro && col == centro) {
                casilla.setTipo(TipoCasilla.CENTRO);
            } else if (twPositions.contains(posicionActual)) {
                casilla.setTipo(TipoCasilla.PALABRA_TRIPLE);
            } else if (tlPositions.contains(posicionActual)) {
                casilla.setTipo(TipoCasilla.LETRA_TRIPLE);
            } else if (dwPositions.contains(posicionActual)) {
                casilla.setTipo(TipoCasilla.PALABRA_DOBLE);
            } else if (dlPositions.contains(posicionActual)) {
                casilla.setTipo(TipoCasilla.LETRA_DOBLE);
            } else {
                casilla.setTipo(TipoCasilla.NORMAL);
            }
            
            if (twPositions.contains(posicionActual)) {
                tlPositions.remove(posicionActual);
                dwPositions.remove(posicionActual);
                dlPositions.remove(posicionActual);
            } else if (tlPositions.contains(posicionActual)) {
                dwPositions.remove(posicionActual);
                dlPositions.remove(posicionActual);
            } else if (dwPositions.contains(posicionActual)) {
                dlPositions.remove(posicionActual);
            }
        }
                
    }
    
    /**
     * Inicializa las fichas del jugador actual con tamaño proporcional
     */
    private void inicializarFichasJugador() {
        if (fichasJugador != null) {
            fichasJugador.getChildren().clear();
            String[] letras = {"S", "C", "R", "A", "BB", "L", "E"};
            int[] puntos = {1, 3, 1, 1, 3, 1, 1};
            
            for (int i = 0; i < 7; i++) {
                Ficha ficha = new Ficha(letras[i], puntos[i]);
                // Establecer tamaño fijo para mantener proporciones
                ficha.setMinSize(CASILLA_SIZE, CASILLA_SIZE);
                ficha.setPrefSize(CASILLA_SIZE, CASILLA_SIZE);
                ficha.setMaxSize(CASILLA_SIZE, CASILLA_SIZE);
                
                configurarEventosFicha(ficha);
                fichasJugador.getChildren().add(ficha);
            }
        }
    }
    
    /**
     * Configura los eventos de mouse para una ficha
     */
    private void configurarEventosFicha(Ficha ficha) {
        // Configurar evento de clic para seleccionar/deseleccionar la ficha
        ficha.setOnMouseClicked(e -> {
            if (fichaSeleccionada == ficha) {
                fichaSeleccionada.deseleccionar();
                fichaSeleccionada = null;
                for (Node node : tablero.getChildren()) {
                    if (node instanceof CasillaDisplay) {
                        ((CasillaDisplay) node).resaltar(false);
                    }
                }
            } else {
                // Deseleccionar la ficha anterior si existe
                if (fichaSeleccionada != null) {
                    fichaSeleccionada.deseleccionar();
                }
                ficha.seleccionar();
                fichaSeleccionada = ficha;                
                System.out.println("Ficha seleccionada: " + ficha.getLetra());
                
            }
            
            e.consume();
        });
    }
 
    /**
     * Coloca una ficha en una casilla del tablero
     */
    private void colocarFichaEnCasilla(CasillaDisplay casilla, int fila, int col) {
        if (fichaSeleccionada != null && !casilla.tieneFicha()) {
            System.out.println("Colocando ficha " + fichaSeleccionada.getLetra() + " en casilla " + fila + "," + col);
            
            // Colocar la ficha en la casilla como no confirmada
            casilla.colocarFicha(fichaSeleccionada.getLetra(), fichaSeleccionada.getPuntos(), false);
            
            // Registrar esta ficha como colocada en el turno actual
            fichasColocadasEnTurnoActual.add(new FichaColocada(
                fichaSeleccionada.getLetra(),
                fichaSeleccionada.getPuntos(),
                fila,
                col
            ));
            
            // Quitar la ficha seleccionada del rack
            if (fichasJugador != null) {
                fichasJugador.getChildren().remove(fichaSeleccionada);
            }
            
            // Quitar selección
            fichaSeleccionada = null;
            
            // Quitar resaltado de todas las casillas
            for (Node node : tablero.getChildren()) {
                if (node instanceof CasillaDisplay) {
                    ((CasillaDisplay) node).resaltar(false);
                }
            }
        }
    }
    
    /**
     * Retira una ficha de una casilla y la devuelve al rack
     */
    private void retirarFichaDeCasilla(CasillaDisplay casilla) {
        if (casilla.tieneFicha() && !casilla.esFichaConfirmada()) {
            System.out.println("Retirando ficha de casilla " + casilla.getFila() + "," + casilla.getColumna());
            
            // Obtener información de la ficha
            String letra = casilla.getLetraFicha();
            int puntos = casilla.getPuntosFicha();
            int fila = casilla.getFila();
            int columna = casilla.getColumna();
            
            // Quitar la ficha de la casilla
            casilla.quitarFicha();
            devolverFichaAJugador(letra, puntos, fila, columna);
        }
    }
    
    /**
     * Verifica si una ficha en la posición dada ha sido confirmada
     */
    public boolean esFichaConfirmada(int fila, int columna) {
        return posiciones.get(new Tuple<>(fila, columna)) != null;
    }
    
    /**
     * Método llamado cuando se retira una ficha de una casilla
     * También debe eliminarla de la lista de fichas colocadas en el turno actual
     */
    public void fichaRetiradaDelTablero(int fila, int columna) {
        fichasColocadasEnTurnoActual.removeIf(f -> f.fila == fila && f.columna == columna);
    }
    
    /**
     * Devuelve una ficha retirada del tablero al rack del jugador
     * Solo permite retirar fichas no confirmadas
     */
    public void devolverFichaAJugador(String letra, int puntos, int fila, int columna) {
        if (esFichaConfirmada(fila, columna)) {
            System.out.println("No se puede retirar una ficha confirmada");
            return;
        }
        fichaRetiradaDelTablero(fila, columna);
        
        // Devolver ficha al jugador
        Ficha nuevaFicha = new Ficha(letra, puntos);
        nuevaFicha.setMinSize(CASILLA_SIZE, CASILLA_SIZE);
        nuevaFicha.setPrefSize(CASILLA_SIZE, CASILLA_SIZE);
        nuevaFicha.setMaxSize(CASILLA_SIZE, CASILLA_SIZE);
        configurarEventosFicha(nuevaFicha);
        
        if (fichasJugador != null) {
            fichasJugador.getChildren().add(nuevaFicha);
            System.out.println("Ficha " + letra + " devuelta al rack del jugador");
        }
    }


    
    /**
     * Cancela la jugada actual, retirando todas las fichas no confirmadas
     */
    private void cancelarJugada() {
        System.out.println("Cancelando jugada...");
        
        // Retirar todas las fichas colocadas en este turno y devolverlas al rack
        List<FichaColocada> copiaDeFichasColocadas = new ArrayList<>(fichasColocadasEnTurnoActual);
        for (FichaColocada ficha : copiaDeFichasColocadas) {
            // Buscar la casilla correspondiente
            CasillaDisplay casilla = null;
            for (Node node : tablero.getChildren()) {
                if (node instanceof CasillaDisplay) {
                    CasillaDisplay casillaActual = (CasillaDisplay) node;
                    if (casillaActual.getFila() == ficha.fila && casillaActual.getColumna() == ficha.columna) {
                        casilla = casillaActual;
                        break;
                    }
                }
            }
            
            if (casilla != null && casilla.tieneFicha() && !casilla.esFichaConfirmada()) {
                // Quitar la ficha de la casilla
                casilla.quitarFicha();
                
                // Devolver la ficha al rack
                devolverFichaAJugador(ficha.letra, ficha.puntos, ficha.fila, ficha.columna);
            }
        }
        
        // Limpiar la lista de fichas colocadas en este turno
        fichasColocadasEnTurnoActual.clear();
    }
    
    @FXML
    private void pausarPartida() {
        System.out.println("Pausando partida...");
        
        // Definir botones del menú de pausa
        List<PausaPopup.PopupButton> buttons = List.of(
            new PausaPopup.PopupButton("Reanudar", PausaPopup.ButtonStyle.SUCCESS, 
                stage -> {
                    stage.close();
                }),
                
            new PausaPopup.PopupButton("Guardar", PausaPopup.ButtonStyle.INFO,
                stage -> {
                    // Llamar a guardar partida
                    stage.close();
                    Platform.runLater(() -> {
                        guardarPartida();
                    });                    
                }),
                
            new PausaPopup.PopupButton("Salir", PausaPopup.ButtonStyle.DANGER,
                stage -> {
                    // Mostrar confirmación de salida
                    stage.close();                    
                    Platform.runLater(() -> {
                        salirPartida();
                    }); 
                })
        );
        
        // Mostrar el popup
        PausaPopup.show("Juego pausado", null, buttons);
    }
    
    /**
    * Actualiza el método guardar partida para usar el controlador
    */
    private void guardarPartida() {
        try {
            boolean guardadoExitoso = controlador.guardarPartida();
            if (guardadoExitoso) {
                System.err.println("Se ha guardado correctamente!");
                this.saved = true;
                PausaPopup.showInfo(
                    "Información",
                    "Partida guardada exitosamente.",
                    stage -> {
                        stage.close();
                    }
                );
            } else {
                controlador.mostrarAlerta("error", "Error al guardar", "No se pudo guardar la partida");
            }
        } catch (Exception e) {
            e.printStackTrace();
            controlador.mostrarAlerta("error", "Error", "Error al guardar la partida: " + e.getMessage());
        }
    }

    /**
    * Actualiza el método de salir para liberar los jugadores
    */
    private void salirPartida() {
        if (!saved) {
            PausaPopup.showConfirmation(
                "Confirmar salida",
                "¿Estás seguro de que quieres salir?\nSe perderá el progreso no guardado.",
                stage -> {
                    // Confirmado
                    stage.close();
                    if (!controlador.getCargado()) controlador.liberarJugadores();
                    else controlador.setCargado(false);
                    controlador.volver();
                },
                stage -> {
                    stage.close();
                }
            );        
        }
        else {
            controlador.volver();
        }
    }
    
    private void salir() {
        if (controlador != null) {
            controlador.volver();
        }
    }
}
