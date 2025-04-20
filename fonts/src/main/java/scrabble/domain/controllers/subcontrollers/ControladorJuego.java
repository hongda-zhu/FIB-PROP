package scrabble.domain.controllers.subcontrollers;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.Serializable;

import scrabble.domain.controllers.subcontrollers.managers.GestorJugada;
import scrabble.domain.controllers.subcontrollers.managers.GestorJugada.Direction;
import scrabble.domain.models.Bolsa;
import scrabble.domain.models.Tablero;
import scrabble.helpers.Triple;
import scrabble.helpers.Tuple;
import scrabble.helpers.Dificultad;
import scrabble.helpers.BooleanWrapper;
import scrabble.domain.models.Diccionario;
import scrabble.domain.models.Dawg;


/**
 * ControladorJuego es la clase encargada de gestionar el flujo del juego de Scrabble.
 * Esta clase se encarga de iniciar el juego, gestionar los turnos de los jugadores,
 * validar las jugadas y mantener el estado del juego.
 */

public class ControladorJuego {

    private GestorJugada gestorJugada;
    private Bolsa bolsa;
    private boolean juegoTerminado = false;
    private boolean juegoIniciado = false;
    private ControladorDiccionario controladorDiccionario;
    private String idiomaActual;

  
    public ControladorJuego() {
        this.gestorJugada = new GestorJugada(new Tablero()) ;
        this.bolsa = null;
        this.controladorDiccionario = ControladorDiccionario.getInstance();
        this.idiomaActual = null;
    }

    /*
     * Método para iniciar el juego.
     * Este método inicializa la bolsa de fichas y carga el diccionario correspondiente al idioma seleccionado.
     * @param idioma El idioma del juego 
     */

    public void iniciarJuego(String idioma) {
        this.idiomaActual = idioma;
        
        try {
            // Obtener el diccionario del controlador de diccionarios
            Diccionario diccionario = this.controladorDiccionario.getDiccionario(idioma);
            
            // Inicializar la bolsa con las fichas del diccionario
            bolsa = new Bolsa();
            bolsa.llenarBolsa(diccionario.getBag(idioma));
            
            // Configurar el GestorJugada con el diccionario adecuado
            gestorJugada.setDiccionario(diccionario);
            gestorJugada.setLenguaje(idioma);
            
        } catch (Exception e) {
            System.err.println("Error al iniciar el juego: " + e.getMessage());
        }
    }

    public void creaTableroNxN(int N) {
        gestorJugada.creaTableroNxN(N);
    }

    /*
     * Método para limpiar la consola.
     * Este método imprime una serie de líneas vacías para simular la limpieza de la consola.
     */

    private void limpiarConsola() {
        for (int i = 0; i < 25; i++) {
            System.out.println();  // Imprimir 50 líneas vacías
        }
    }

    /*
     * Método para añadir un nuevo idioma al juego.
     * Este método permite añadir un nuevo idioma al juego, especificando el nombre del idioma,
     * la ruta del archivo con juegos de letras y la ruta del diccionario.
     * @param nombre El nombre del idioma a añadir
     * @param rutaArchivoAlpha La ruta del archivo con juegos de letras correspondiente al idioma
     * @param rutaArchivoWords La ruta del diccionario correspondiente al idioma
     * @throws IOException Si hay problemas al leer los archivos.
     */

    public void anadirLenguaje(String nombre, String rutaArchivoAlpha, String rutaArchivoWords) throws IOException {
        // Delegar la creación del diccionario al controlador de diccionarios
        this.controladorDiccionario.crearDiccionario(nombre, rutaArchivoAlpha, rutaArchivoWords);
    }

    /*
     * Método para establecer el idioma del juego.
     * Este método permite establecer el idioma del juego, especificando el nombre del idioma.
     * @param nombre El nombre del idioma a establecer
     */

    public void setLenguaje(String nombre) {
        this.idiomaActual = nombre;
        
        try {
            Diccionario diccionario = this.controladorDiccionario.getDiccionario(nombre);
            gestorJugada.setDiccionario(diccionario);
            gestorJugada.setLenguaje(nombre);
        } catch (Exception e) {
            System.err.println("Error al establecer el idioma: " + e.getMessage());
        }
    }

    /*
     * Método para verificar si un idioma existe en el juego.
     * Este método permite verificar si un idioma ya ha sido añadido al juego.
     * @param nombre El nombre del idioma a verificar
     * @return true si el idioma existe, false en caso contrario
     */

    public boolean existeLenguaje(String nombre) {
        return this.controladorDiccionario.existeDiccionario(nombre);
    }
    
    /**
    * Devuelve la lista de nombres de los diccionarios disponibles actualmente en el sistema.
    * 
    * @return Lista de nombres de diccionarios disponibles.
    */
    public List<String> getDiccionariosDisponibles() {
        return this.controladorDiccionario.getDiccionariosDisponibles();
    }
    /* 
     * Método para hacer el primer turno del jugador.
     * Este método permite al jugador realizar su primer movimiento en el juego.
     * @param nombreJugador El nombre del jugador que está realizando el movimiento
     * @param rack El rack del jugador, que contiene las letras disponibles para jugar
     * @return Un objeto Tuple que contiene el nuevo rack del jugador y los puntos obtenidos por la jugada
     */

    // private Tuple<Map<String, Integer>, Integer> primerTurno(String nombreJugador, Map<String, Integer> rack) {

    //     Triple<String,Tuple<Integer, Integer>, Direction> move = this.gestorJugada.jugarTurno();

    //     if (move == null) {
    //         // Si el jugador decide pasar su turno, se puede implementar aquí
    //         System.out.println(nombreJugador + "no puedes pasar de turno, eres el primero en jugar.");
    //         return null;
    //     }
    //     if (this.gestorJugada.isValidFirstMove(move, rack)) {
    //         this.juegoIniciado = true;
    //         return new Tuple<Map<String, Integer>, Integer>(this.gestorJugada.makeMove(move, rack), this.gestorJugada.calculateMovePoints(move));
    //     } else {
    //         System.out.println("La jugada no es válida. Intenta nuevamente.");
    //         return primerTurno(nombreJugador, rack); // Verifica si la jugada es válida
    //     }
    // }

    /*
     * Método para realizar un turno en el juego.
     * Este método permite al jugador realizar su turno, ya sea humano o IA.
     * @param nombreJugador El nombre del jugador que está realizando el movimiento
     * @param rack El rack del jugador, que contiene las letras disponibles para jugar
     * @param isIA Indica si el jugador es una IA o un jugador humano
     * @param dificultad La dificultad de la IA (si aplica)
     * @param pausado Flag para indicar si la partida se pausa
     * @return Un objeto Tuple que contiene el nuevo rack del jugador y los puntos obtenidos por la jugada
     */

    public Tuple<Map<String, Integer>, Integer> realizarTurno(String nombreJugador, Map<String, Integer> rack,  boolean isIA, Dificultad dificultad, BooleanWrapper pausado) {
        limpiarConsola();
        this.gestorJugada.mostrarTablero();

        System.out.println("Tu rack actual es: " + rack);
        return realizarAccion(nombreJugador, rack, isIA, dificultad, juegoIniciado, pausado);
    }

    /*
     * Método para realizar una acción en el juego.
     * Este método permite al jugador realizar una acción, ya sea colocar palabras, pasar o intercambiar fichas.
     * @param nombreJugador El nombre del jugador que está realizando la acción
     * @param rack El rack del jugador, que contiene las letras disponibles para jugar
     * @param isIA Indica si el jugador es una IA o un jugador humano
     * @param dificultad La dificultad de la IA (si aplica)
     * @param isFirst Indica si es el primer turno de la partida
     * @param pausado Flag para indicar si la partida se pausa
     * @return Un objeto Tuple que contiene el nuevo rack del jugador y los puntos obtenidos por la jugada
     */

    private Tuple<Map<String, Integer>, Integer> realizarAccion(String nombreJugador, Map<String, Integer> rack, boolean isIA, Dificultad dificultad, boolean isFirst, BooleanWrapper pausado) {
        // El jugador puede colocar palabras, pasar o intercambiar fichas.
        if (!isIA) {
            // El jugador humano puede ingresar una palabra o hacer una acción
            // Aquí implementas la lógica para que el jugador humano haga su jugada.
            System.out.println(nombreJugador + "Es tu turno, coloca una palabra o skip");

            Triple<String,Tuple<Integer, Integer>, Direction> move = this.gestorJugada.jugarTurno(pausado);

            if (move == null) {
                // Si el jugador decide pasar su turno, se puede implementar aquí
                System.out.println(nombreJugador + " ha decidido pasar su turno.");
                return null;
            } else {   
                if (juegoIniciado? this.gestorJugada.isValidMove(move, rack):this.gestorJugada.isValidFirstMove(move, rack)){
                    this.juegoIniciado = true;
                    return new Tuple<Map<String,Integer>,Integer>(this.gestorJugada.makeMove(move, rack), this.gestorJugada.calculateMovePoints(move));
                } else {
                    System.out.println("La jugada no es válida. Intenta nuevamente.");
                    return realizarAccion(nombreJugador, rack, isIA, dificultad, isFirst, pausado);// Verifica si la jugada es válida
                }
            }
        } else {
            Set<Triple<String,Tuple<Integer, Integer>, Direction>> move = this.gestorJugada.searchAllMoves(rack, this.juegoIniciado);
            if (move == null || move.isEmpty()) {
                System.out.println("El jugador IA no puede hacer una jugada.");
                return null;
            } else {
                Triple<String,Tuple<Integer, Integer>, Direction> bestMove = null;
                int bestMovePoints = 0;
                for (Triple<String,Tuple<Integer, Integer>, Direction> m : move) {
                    int currentMovePoints = this.gestorJugada.calculateMovePoints(m);
                    if (bestMove == null || currentMovePoints > bestMovePoints) {
                        bestMove = m;
                        bestMovePoints = currentMovePoints;
                    }
                    if (dificultad == Dificultad.FACIL) {
                        if (currentMovePoints > 0) {
                            break; // Si la dificultad es fácil, no es necesario buscar más
                        }
                    }
                }
                System.out.println("El jugador IA está haciendo su jugada.");
                this.juegoIniciado = true;
                return new Tuple<Map<String,Integer>,Integer>(this.gestorJugada.makeMove(bestMove, rack), bestMovePoints);
            }
        
        }
    }

    /*
     * Método para obtener la cantidad de fichas restantes en la bolsa.
     * Este método devuelve la cantidad de fichas restantes en la bolsa.
     * @return La cantidad de fichas restantes en la bolsa
     */

    public int getCantidadFichas() {
        // Verifica si el juego ha terminado (por ejemplo, si la bolsa está vacía o si un jugador ha usado todas sus fichas)
        return this.bolsa.getCantidadFichas();
    }

    /*
     * Método para finalizar el juego.
     * Este método se llama cuando el juego ha terminado, ya sea porque un jugador ha ganado o porque no hay más fichas en la bolsa.
     * En este caso, se muestra un mensaje indicando que el juego ha terminado.
     */

    public void finalizarJuego() {
        System.out.println("El juego ha terminado.");
        juegoTerminado = true;
    }

    /*
     * Método para reiniciar el juego.
     * Este método reinicia el juego, restableciendo el estado del juego y la bolsa de fichas.
     */

    public void reiniciarJuego() {
        this.gestorJugada = new GestorJugada(new Tablero());
        juegoTerminado = false;
        juegoIniciado = false;
    }

    /*
     * Método para obtener el estado del juego.
     * Este método devuelve un booleano que indica si el juego ha terminado o no.
     * @return true si el juego ha terminado, false en caso contrario
     */

    public boolean isJuegoTerminado() {
        return juegoTerminado;
    }

    /*
     * Método para coger fichas de la bolsa.
     * Este método permite al jugador coger una cantidad específica de fichas de la bolsa.
     * @param cantidad La cantidad de fichas a coger
     * @return Un mapa que contiene las fichas cogidas y su cantidad
     */
    
    public Map<String, Integer> cogerFichas(int cantidad) {
        Map<String, Integer> fichas = new HashMap<>();

        for (int i = 0; i < cantidad; i++) {
            String ficha = this.bolsa.sacarFicha();
            if (ficha != null) {
                fichas.put(ficha, fichas.getOrDefault(ficha, 0) + 1);
            } else return null; // No hay suficientes fichas en la bolsa
        }
        return fichas;
    }

    public void meterFichas(Map<String, Integer> fichas) {
        for (Map.Entry<String, Integer> entry : fichas.entrySet()) {
            String ficha = entry.getKey();
            int cantidad = entry.getValue();
            for (int i = 0; i < cantidad; i++) {
                this.bolsa.agregarFichas(ficha, cantidad);
            }
        }
    }
    // MÉTODOS DE JIAHAO, no se si funcionan, no quiero testearlos por la complejidad,
    // /*
    //  * Método para verificar si un ID de partida existe.
    //  * Este método verifica si un ID de partida ya ha sido guardado en el archivo de estado del juego.
    //  * @param partidaId El ID de la partida a verificar
    //  * @return true si el ID de la partida existe, false en caso contrario
    //  */

    // public boolean existeIdPartida(String partidaId) {
    //     try {
    //         File archivo = new File("estado_juego.dat");
    //         if (archivo.exists()) {
    //             try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
    //                 List<Map<String, Object>> historial = (List<Map<String, Object>>) ois.readObject();
    //                 for (Map<String, Object> estadoJuego : historial) {
    //                     if (estadoJuego.get("id").equals(partidaId)) {
    //                         return true;
    //                     }
    //                 }
    //             } catch (EOFException ignored) {}
    //         }
    //     } catch (IOException | ClassNotFoundException e) {
    //         System.err.println("Error al verificar el ID de la partida: " + e.getMessage());
    //     }
    //     return false;
    // }

    // /*
    //  * Método para guardar el estado de la partida.
    //  * Este método guarda el estado actual del juego en un archivo, incluyendo el ID de la partida,
    //  * el gestor de jugadas, la bolsa de fichas y los racks de los jugadores.
    //  * @param partidaId El ID de la partida a guardar
    //  * @param racks Un mapa que contiene los racks de los jugadores
    //  */

    // public void guardarPartida(String partidaId, Map<String, Map<String, Integer>> racks) {
    //     //ACORDARSE PONER PUNTUACION Y DE QUIEN ES EL TURNO
    //     try {
    //         List<Map<String, Object>> historial = new ArrayList<>();
    
    //         File archivo = new File("estado_juego.dat");
    //         if (archivo.exists()) {
    //             try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
    //                 historial = (List<Map<String, Object>>) ois.readObject();
    //             } catch (EOFException ignored) {}
    //         }

    //         // Crear nuevo estado
    //         Map<String, Object> estadoJuego = new HashMap<>();
    //         estadoJuego.put("id", partidaId); // ID único para la partida
    //         estadoJuego.put("gestorJugada", this.gestorJugada);
    //         estadoJuego.put("bolsa", this.bolsa);
    //         estadoJuego.put("juegoTerminado", this.juegoTerminado);
    //         estadoJuego.put("juegoIniciado", this.juegoIniciado);
    //         estadoJuego.put("racks", racks);

    //         // Añadirlo a la lista
    //         historial.add(estadoJuego);

    //         // Guardar la lista completa
    //         try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("estado_juego.dat"))) {
    //             oos.writeObject(historial);
    //             System.out.println("Estado de la partida con ID " + partidaId + " guardado correctamente.");
    //         } catch (IOException e) {
    //             System.err.println("Error al guardar el estado del juego: " + e.getMessage());
    //         }
    //     } catch (IOException | ClassNotFoundException e) {
    //         System.err.println("Error al guardar el estado del juego: " + e.getMessage());
    //     }
    // }
    
    // /*
    //  * Método para cargar el estado de la partida.
    //  * Este método carga el estado del juego desde un archivo, restaurando el gestor de jugadas,
    //  * la bolsa de fichas y los racks de los jugadores.
    //  * @param partidaId El ID de la partida a cargar
    //  * @return true si la carga fue exitosa, false en caso contrario
    //  */
    
    // public Map<String, Map<String, Integer>> cargarPartida(String partidaId) {
    //     // Implementación para cargar el estado del juego desde un archivo usando el ID de la partida
    //     try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("estado_juego.dat"))) {
    //         List<Map<String, Object>> historial = (List<Map<String, Object>>) ois.readObject();

    //         for (Map<String, Object> estadoJuego : historial) {
    //             if (estadoJuego.get("id").equals(partidaId)) {
    //                 // Restaurar el estado del juego
    //                 this.gestorJugada = (GestorJugada) estadoJuego.get("gestorJugada");
    //                 this.bolsa = (Bolsa) estadoJuego.get("bolsa");
    //                 this.juegoTerminado = (boolean) estadoJuego.get("juegoTerminado");
    //                 this.juegoIniciado = (boolean) estadoJuego.get("juegoIniciado");

    //                 // Restaurar los racks de los jugadores
    //                 Map<String, Map<String, Integer>> racks = (Map<String, Map<String, Integer>>) estadoJuego.get("racks");
    //                 if (racks != null) {
    //                     System.out.println("Racks de los jugadores restaurados: " + racks);
    //                     return racks;
    //                 }

    //                 System.out.println("El estado del juego con ID " + partidaId + " se ha cargado correctamente.");
    //             }
    //         }

    //         System.out.println("No se encontró una partida con el ID " + partidaId + ".");
    //     } catch (IOException | ClassNotFoundException e) {
    //         System.err.println("Error al cargar el estado del juego: " + e.getMessage());
    //         return null;
    //     }
    //     return null; // Si no se encontró la partida o hubo un error
    // }
}