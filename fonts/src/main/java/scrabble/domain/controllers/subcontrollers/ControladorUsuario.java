package domain.controllers.subcontrollers;

import domain.controllers.subcontrollers.managers.GestorAutenticacion;
import domain.models.JugadorHumano;
import domain.models.JugadorIA;
import domain.models.Jugador;

import java.io.*;
import java.util.*;

/**
 * Controlador para la gestión de usuarios.
 * Implementa el patrón Singleton para garantizar una única instancia.
 */
public class ControladorUsuario {
    private static ControladorUsuario instance;
    private Map<String, Jugador> jugadores;
    private Set<String> jugadoresLogueados;
    private GestorAutenticacion gestorAutenticacion;
    
    private static final String JUGADORES_FILE = "jugadores.dat";
    
    /**
     * Constructor privado para implementar el patrón Singleton.
     * Inicializa la gestión de jugadores y carga los datos si existen.
     */
    private ControladorUsuario() {
        this.jugadores = new HashMap<>();
        this.jugadoresLogueados = new HashSet<>();
        this.gestorAutenticacion = new GestorAutenticacion();
        cargarDatos();
    }
    
    /**
     * Obtiene la instancia única del controlador (Singleton).
     * 
     * @return Instancia de ControladorUsuario
     */
    public static synchronized ControladorUsuario getInstance() {
        if (instance == null) {
            instance = new ControladorUsuario();
        }
        return instance;
    }
    
    /**
     * Autentica a un jugador en el sistema.
     * 
     * @param id ID del jugador
     * @param password Contraseña
     * @return true si la autenticación es exitosa, false en caso contrario
     */
    public boolean autenticar(String id, String password) {
        if (!existeJugador(id)) {
            return false;
        }
        
        Jugador jugador = jugadores.get(id);
        
        // Solo los jugadores humanos requieren autenticación
        if (jugador.esIA()) {
            return false;
        }
        
        JugadorHumano jugadorHumano = (JugadorHumano) jugador;
        
        if (gestorAutenticacion.autenticar(id, password) && jugadorHumano.verificarPassword(password)) {
            jugadoresLogueados.add(id);
            jugadorHumano.setLogueado(true);
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Verifica si un jugador existe en el sistema.
     * 
     * @param id ID del jugador
     * @return true si el jugador existe, false en caso contrario
     */
    public boolean existeJugador(String id) {
        return jugadores.containsKey(id);
    }
    
    /**
     * Verifica si un jugador está actualmente logueado.
     * 
     * @param id ID del jugador
     * @return true si el jugador está logueado, false en caso contrario
     */
    public boolean isLoggedIn(String id) {
        return jugadoresLogueados.contains(id);
    }
    
    /**
     * Cierra la sesión de un jugador.
     * 
     * @param id ID del jugador
     * @return true si se cerró la sesión correctamente, false en caso contrario
     */
    public boolean cerrarSesion(String id) {
        if (!isLoggedIn(id)) {
            return false;
        }
        
        jugadoresLogueados.remove(id);
        
        Jugador jugador = jugadores.get(id);
        if (!jugador.esIA()) {
            ((JugadorHumano) jugador).setLogueado(false);
        }
        
        return true;
    }
    
    /**
     * Registra un nuevo jugador humano en el sistema.
     * 
     * @param id ID del jugador
     * @param password Contraseña
     * @return true si se registró correctamente, false en caso contrario
     */
    public boolean registrarUsuario(String id, String password) {
        return registrarUsuario(id, id, password); // Por defecto, el nombre es igual al ID
    }
    
    /**
     * Registra un nuevo jugador humano en el sistema con un nombre personalizado.
     * 
     * @param id ID del jugador
     * @param nombre Nombre del jugador
     * @param password Contraseña
     * @return true si se registró correctamente, false en caso contrario
     */
    public boolean registrarUsuario(String id, String nombre, String password) {
        if (existeJugador(id)) {
            return false;
        }
        
        JugadorHumano nuevoJugador = new JugadorHumano(id, nombre, password);
        jugadores.put(id, nuevoJugador);
        gestorAutenticacion.registrar(id, password);
        
        guardarDatos();
        return true;
    }
    
    /**
     * Registra un nuevo jugador IA en el sistema.
     * 
     * @param id ID para la IA
     * @param dificultad Nivel de dificultad
     * @return true si se registró correctamente, false en caso contrario
     */
    public boolean registrarJugadorIA(String id, JugadorIA.Dificultad dificultad) {
        if (existeJugador(id)) {
            return false;
        }
        
        JugadorIA nuevoJugadorIA = new JugadorIA(id, dificultad);
        jugadores.put(id, nuevoJugadorIA);
        
        guardarDatos();
        return true;
    }
    
    /**
     * Elimina un jugador del sistema.
     * 
     * @param id ID del jugador a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public boolean eliminarUsuario(String id) {
        if (!existeJugador(id)) {
            return false;
        }
        
        // Si está logueado, cerrar sesión
        if (isLoggedIn(id)) {
            cerrarSesion(id);
        }
        
        jugadores.remove(id);
        gestorAutenticacion.eliminarUsuario(id);
        
        guardarDatos();
        return true;
    }
    
    /**
     * Cambia la contraseña de un jugador.
     * 
     * @param id ID del jugador
     * @param oldPassword Contraseña actual
     * @param newPassword Nueva contraseña
     * @return true si se cambió correctamente, false en caso contrario
     */
    public boolean cambiarContrasena(String id, String oldPassword, String newPassword) {
        if (!existeJugador(id)) {
            return false;
        }
        
        Jugador jugador = jugadores.get(id);
        if (jugador.esIA()) {
            return false; // Las IAs no tienen contraseña
        }
        
        JugadorHumano jugadorHumano = (JugadorHumano) jugador;
        if (!jugadorHumano.verificarPassword(oldPassword)) {
            return false;
        }
        
        jugadorHumano.setPassword(newPassword);
        gestorAutenticacion.cambiarContrasena(id, newPassword);
        
        guardarDatos();
        return true;
    }
    
    /**
     * Cambia el nombre de un jugador humano.
     * 
     * @param id ID del jugador
     * @param nuevoNombre Nuevo nombre
     * @return true si se cambió correctamente, false en caso contrario
     */
    public boolean cambiarNombre(String id, String nuevoNombre) {
        if (!existeJugador(id)) {
            return false;
        }
        
        Jugador jugador = jugadores.get(id);
        if (jugador.esIA()) {
            return false; // No permitimos cambiar el nombre de las IAs
        }
        
        ((JugadorHumano) jugador).setNombre(nuevoNombre);
        
        guardarDatos();
        return true;
    }
    
    /**
     * Obtiene un jugador por su ID.
     * 
     * @param id ID del jugador
     * @return Jugador encontrado o null si no existe
     */
    public Jugador getJugador(String id) {
        return jugadores.get(id);
    }
    
    /**
     * Incrementa el contador de partidas jugadas para un jugador.
     * 
     * @param id ID del jugador
     * @return true si se incrementó correctamente, false en caso contrario
     */
    public boolean incrementarPartidasJugadas(String id) {
        if (!existeJugador(id)) {
            return false;
        }
        
        Jugador jugador = jugadores.get(id);
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
     * @param id ID del jugador
     * @return true si se incrementó correctamente, false en caso contrario
     */
    public boolean incrementarPartidasGanadas(String id) {
        if (!existeJugador(id)) {
            return false;
        }
        
        Jugador jugador = jugadores.get(id);
        if (jugador.esIA()) {
            return false; // Las IAs no tienen contador de partidas
        }
        
        ((JugadorHumano) jugador).incrementarPartidasGanadas();
        
        guardarDatos();
        return true;
    }
    
    /**
     * Establece si un jugador humano está en una partida.
     * 
     * @param id ID del jugador
     * @param enPartida true si está en partida, false en caso contrario
     * @return true si se estableció correctamente, false en caso contrario
     */
    public boolean setEnPartida(String id, boolean enPartida) {
        if (!existeJugador(id)) {
            return false;
        }
        
        Jugador jugador = jugadores.get(id);
        if (jugador.esIA()) {
            return false; // Las IAs no pueden estar en múltiples partidas
        }
        
        ((JugadorHumano) jugador).setEnPartida(enPartida);
        guardarDatos();
        return true;
    }
    
    /**
     * Verifica si un jugador humano está en una partida.
     * 
     * @param id ID del jugador
     * @return true si está en partida, false en caso contrario
     */
    public boolean isEnPartida(String id) {
        if (!existeJugador(id)) {
            return false;
        }
        
        Jugador jugador = jugadores.get(id);
        if (jugador.esIA()) {
            return false; // Las IAs no están "en partida" del mismo modo que los humanos
        }
        
        return ((JugadorHumano) jugador).isEnPartida();
    }
    
    /**
     * Establece la puntuación para un jugador.
     * 
     * @param id ID del jugador
     * @param puntuacion Puntuación
     * @return true si se estableció correctamente, false en caso contrario
     */
    public boolean setPuntuacion(String id, int puntuacion) {
        if (!existeJugador(id)) {
            return false;
        }
        
        Jugador jugador = jugadores.get(id);
        jugador.setPuntuacion(puntuacion);
        guardarDatos();
        return true;
    }
    
    /**
     * Obtiene una lista de IDs de todos los jugadores registrados.
     * 
     * @return Lista de IDs de jugadores
     */
    public List<String> getJugadoresRegistrados() {
        return new ArrayList<>(jugadores.keySet());
    }
    
    /**
     * Obtiene una lista de IDs de todos los jugadores humanos.
     * 
     * @return Lista de IDs de jugadores humanos
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
     * Obtiene una lista de IDs de todos los jugadores IA.
     * 
     * @return Lista de IDs de jugadores IA
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