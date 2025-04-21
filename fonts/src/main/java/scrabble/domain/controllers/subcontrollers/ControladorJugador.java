package scrabble.domain.controllers.subcontrollers;

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

import scrabble.domain.models.Jugador;
import scrabble.domain.models.JugadorHumano;
import scrabble.domain.models.JugadorIA;
import scrabble.helpers.Dificultad;

/**
 * Controlador para la gestión de usuarios.
 * Implementa el patrón Singleton para garantizar una única instancia.
 */
public class ControladorJugador {
    private static ControladorJugador instance;
    private Map<String, Jugador> jugadores; // key: nombre -> value: Jugador 
    
    private static final String JUGADORES_FILE = "jugadores.dat";
    
    /**
     * Constructor privado para implementar el patrón Singleton.
     * Inicializa la gestión de jugadores y carga los datos si existen.
     */
    private ControladorJugador() {
        this.jugadores = new HashMap<>();
        cargarDatos();
    }
    
    /**
     * Obtiene la instancia única del controlador (Singleton).
     * 
     * @return Instancia de ControladorJugador
     */
    public static synchronized ControladorJugador getInstance() {
        if (instance == null) {
            instance = new ControladorJugador();
        }
        return instance;
    }
    
    /**
     * Obtiene la puntuación de un jugador.
     * 
     * @param nombre Nombre del jugador
     * @return La puntuación del jugador
     */    
    public int getPuntuacion(String nombre) {
        Jugador j = getJugador(nombre);
        return j.getPuntuacion();
    }

    /**
     * Obtiene el contador de turnos que un jugador ha omitido.
     * 
     * @param nombre Nombre del jugador
     * @return El número de turnos omitidos por el jugador
     */
    public int getSkipTrack(String nombre) {
        Jugador j = getJugador(nombre);
        return j.getSkipTrack();
    }

    /**
     * Agrega una ficha al jugador.
     * 
     * @param nombre Nombre del jugador
     * @param letra Ficha que se va a agregar
     */
    public void agregarFicha(String nombre, String letra) {
        Jugador j = getJugador(nombre);
        j.agregarFicha(letra);
    }

    /**
     * Obtiene la cantidad de fichas que tiene un jugador.
     * 
     * @param nombre Nombre del jugador
     * @return La cantidad de fichas del jugador
     */
    public int getCantidadFichas(String nombre) {
        Jugador j = getJugador(nombre);
        return j.getCantidadFichas();
    }

    /**
     * Añade puntuación a un jugador.
     * 
     * @param nombre Nombre del jugador
     * @param puntuacion La puntuación a añadir
     */
    public void addPuntuacion(String nombre, int puntuacion) {
        Jugador j = getJugador(nombre);
        j.addPuntuacion(puntuacion);
    }

    /**
     * Obtiene el rack de un jugador.
     * 
     * @param nombre Nombre del jugador
     * @return El rack del jugador 
     */
    public Map<String,Integer> getRack(String nombre) {
        Jugador j = getJugador(nombre);
        return j.getRack();
    } 
    
    /**
     * Obtiene el nivel de dificultad de un jugador, si es un jugador IA.
     * 
     * @param nombre Nombre del jugador
     * @return El nivel de dificultad del jugador si es una IA, o null si no lo es
     */    
    public Dificultad getNivelDificultad(String nombre) {
        if (!existeJugador(nombre)) {
            return null;
        }

        Jugador jugador = jugadores.get(nombre);

        if (jugador instanceof JugadorIA jugadorIA) {
            return jugadorIA.getNivelDificultad();
        }

        return null;
    }

    /**
     * Inicializa el rack de un jugador con un conjunto de fichas.
     * 
     * @param nombre Nombre del jugador
     * @param rack El conjunto de fichas a asignar al jugador
     */
    public void inicializarRack(String nombre, Map<String, Integer> rack) {
        Jugador j = getJugador(nombre);
        j.inicializarRack(rack);
    }

    /**
     * Obtiene el nombre de un jugador.
     * 
     * @param nombre Nombre del jugador
     * @return El nombre del jugador
     */
    public String getNombre(String nombre) {
        Jugador j = getJugador(nombre);
        return j.getNombre();
    }

    /**
     * Aumenta el contador de turnos omitidos de un jugador.
     * 
     * @param nombre Nombre del jugador
     */
    public void addSkipTrack(String nombre) {
        Jugador j = getJugador(nombre);
        j.addSkipTrack();
    }
    
    /**
    * Obtiene listado completo de usuarios (SOLO DEBUG o podemos dejarlo)
    * @return Lista con todos los datos
    */
    public List<String> getTodosUsuariosDebug() {
        List<String> usuarios = new ArrayList<>();
        
        // Jugadores humanos
        for (String nombre : getJugadoresHumanos()) {
            JugadorHumano jugador = (JugadorHumano) getJugador(nombre);

            usuarios.add(String.format(
                "Nombre: %-15s | Partidas: %d/%d",
                jugador.getNombre(),
                jugador.getPartidasGanadas(),
                jugador.getPartidasJugadas()
            ));
        }
        
        // Jugadores IA
        for (String nombre : getJugadoresIA()) {
            JugadorIA ia = (JugadorIA) getJugador(nombre);
            usuarios.add(String.format(
                "Nombre: %-15s | Tipo: IA | Dificultad: %s",
                ia.getNombre(),
                ia.getNivelDificultad()
            ));
        }
        
        return usuarios;
    }

    /**
    * Verifica si un determinado jugador es IA.
    *
    * @pre El jugador con el nombre dado debe existir en el sistema  
    * @param nombre Nombre del jugador
    * @return true si el jugador es IA, false en caso contrario
    */    
    public boolean esIA(String nombre) {
        Jugador jugador = getJugador(nombre);
        return jugador.esIA();
    }
    
    /**
     * Verifica si un jugador existe en el sistema.
     * 
     * @param nombre Nombre del jugador
     * @return true si el jugador existe, false en caso contrario
     */
    public boolean existeJugador(String nombre) {
        return jugadores.containsKey(nombre);
    }
    
    /**
     * Verifica si un jugador humano está en una partida.
     * 
     * @param nombre Nombre del jugador
     * @return true si está en partida, false en caso contrario
     */
    public boolean isEnPartida(String nombre) {
        if (!existeJugador(nombre)) {
            return false;
        }
        
        Jugador jugador = jugadores.get(nombre);
        if (jugador.esIA()) {
            return false; // Las IAs no están "en partida" del mismo modo que los humanos
        }
        
        return ((JugadorHumano) jugador).isEnPartida();
    }
    
    /**
     * Registra un nuevo jugador humano en el sistema.
     * 
     * @param nombre Nombre del jugador
     * @return true si se registró correctamente, false en caso contrario
     */
    public boolean registrarUsuario(String nombre) {
        if (existeJugador(nombre)) {
            return false;
        }
        
        JugadorHumano nuevoJugador = new JugadorHumano(nombre);
        jugadores.put(nombre, nuevoJugador);
        
        guardarDatos();
        return true;
    }
    
    /**
     * Registra un nuevo jugador IA en el sistema.
     * 
     * @param dificultad Nivel de dificultad
     * @return true si se registró correctamente, false en caso contrario
     */
    public boolean registrarJugadorIA(Dificultad dificultad) {
        // Para JugadorIA, ahora usamos su constructor que genera un nombre automático
        JugadorIA nuevoJugadorIA = new JugadorIA(dificultad);
        
        // El nombre real viene del jugador generado
        String nombreReal = nuevoJugadorIA.getNombre();
        
        // Verificamos que no exista
        if (existeJugador(nombreReal)) {
            return false;
        }
        
        jugadores.put(nombreReal, nuevoJugadorIA);
        guardarDatos();
        return true;
    }
    
    /**
     * Alternativa para registrar un jugador IA con un nombre específico.
     * Este método se mantiene por compatibilidad con código existente.
     * 
     * @param nombre Nombre para referenciar (no se usa)
     * @param dificultad Nivel de dificultad
     * @return true si se registró correctamente, false en caso contrario
     */
    public boolean registrarJugadorIA(String nombre, Dificultad dificultad) {
        return registrarJugadorIA(dificultad);
    }
    
    /**
     * Elimina un jugador del sistema.
     * 
     * @param nombre Nombre del jugador a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public boolean eliminarUsuario(String nombre) {
        if (!existeJugador(nombre)) {
            return false;
        }
        
        jugadores.remove(nombre);
        guardarDatos();
        return true;
    }
    
    /**
     * Establece si un jugador humano está en una partida.
     * 
     * @param nombre Nombre del jugador
     * @param enPartida true si está en partida, false en caso contrario
     * @return true si se estableció correctamente, false en caso contrario
     */
    public boolean setEnPartida(String nombre, boolean enPartida) {
        if (!existeJugador(nombre)) {
            return false;
        }
        
        Jugador jugador = jugadores.get(nombre);
        if (jugador.esIA()) {
            return false; // Las IAs no pueden estar en múltiples partidas
        }
        
        ((JugadorHumano) jugador).setEnPartida(enPartida);
        guardarDatos();
        return true;
    }
    
    /**
     * Establece la puntuación para un jugador.
     * 
     * @param nombre Nombre del jugador
     * @param puntuacion Puntuación
     * @return true si se estableció correctamente, false en caso contrario
     */
    public boolean setPuntuacion(String nombre, int puntuacion) {
        if (!existeJugador(nombre)) {
            return false;
        }
        
        Jugador jugador = jugadores.get(nombre);
        jugador.setPuntuacion(puntuacion);
        guardarDatos();
        return true;
    }
    
    /**
     * Obtiene una lista de nombres de todos los jugadores registrados.
     * 
     * @return Lista de nombres de jugadores
     */
    public List<String> getJugadoresRegistrados() {
        return new ArrayList<>(jugadores.keySet());
    }
    
    /**
     * Obtiene una lista de nombres de todos los jugadores humanos.
     * 
     * @return Lista de nombres de jugadores humanos
     */
    public List<String> getJugadoresHumanos() {
        List<String> jugadoresHumanos = new ArrayList<>();
        
        for (Map.Entry<String, Jugador> entry : jugadores.entrySet()) {
            if (!entry.getValue().esIA()) {
                jugadoresHumanos.add(entry.getKey());
            }
        }
        
        return jugadoresHumanos;
    }
    
    /**
     * Obtiene una lista de nombres de todos los jugadores IA.
     * 
     * @return Lista de nombres de jugadores IA
     */
    public List<String> getJugadoresIA() {
        List<String> jugadoresIA = new ArrayList<>();
        
        for (Map.Entry<String, Jugador> entry : jugadores.entrySet()) {
            if (entry.getValue().esIA()) {
                jugadoresIA.add(entry.getKey());
            }
        }
        
        return jugadoresIA;
    }
    
    /**
     * Obtiene un jugador por su nombre.
     * 
     * @param nombre Nombre del jugador
     * @return Jugador encontrado o null si no existe
     */
    public Jugador getJugador(String nombre) {
        return jugadores.get(nombre);
    }
    
    /**
     * Incrementa el contador de partidas jugadas para un jugador.
     * 
     * @param nombre Nombre del jugador
     * @return true si se incrementó correctamente, false en caso contrario
     */
    public boolean incrementarPartidasJugadas(String nombre) {
        if (!existeJugador(nombre)) {
            return false;
        }
        
        Jugador jugador = jugadores.get(nombre);
        if (jugador.esIA()) {
            return false; // Las IAs no tienen contador de partidas
        }
        
        ((JugadorHumano) jugador).incrementarPartidasJugadas();
        
        guardarDatos();
        return true;
    }
    
    /**
     * Incrementa el contador de partidas ganadas para un jugador.
     * 
     * @param nombre Nombre del jugador
     * @return true si se incrementó correctamente, false en caso contrario
     */
    public boolean incrementarPartidasGanadas(String nombre) {
        if (!existeJugador(nombre)) {
            return false;
        }
        
        Jugador jugador = jugadores.get(nombre);
        if (jugador.esIA()) {
            return false; // Las IAs no tienen contador de partidas
        }
        
        ((JugadorHumano) jugador).incrementarPartidasGanadas();
        
        guardarDatos();
        return true;
    }
    
    /**
     * Guarda los datos de los jugadores en un archivo.
     */
    private void guardarDatos() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(JUGADORES_FILE))) {
            oos.writeObject(jugadores);
        } catch (IOException e) {
            System.err.println("Error al guardar los jugadores: " + e.getMessage());
        }
    }
    
    /**
     * Carga los datos de los jugadores desde un archivo.
     */
    @SuppressWarnings("unchecked")
    private void cargarDatos() {
        File jugadoresFile = new File(JUGADORES_FILE);
        if (jugadoresFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(jugadoresFile))) {
                jugadores = (Map<String, Jugador>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error al cargar los jugadores: " + e.getMessage());
                jugadores = new HashMap<>(); // Si hay error, inicializar con uno nuevo
            }
        }
    }
}