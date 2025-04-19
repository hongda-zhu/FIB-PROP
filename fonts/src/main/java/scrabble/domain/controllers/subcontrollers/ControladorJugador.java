package scrabble.domain.controllers.subcontrollers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import scrabble.domain.controllers.subcontrollers.managers.GestorAutenticacion;
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
    private Map<String, Jugador> jugadores; //key: user id -> value: Jugador 
    private Set<String> jugadoresLogueados;
    private GestorAutenticacion gestorAutenticacion;
    
    private static final String JUGADORES_FILE = "jugadores.dat";
    
    /**
     * Constructor privado para implementar el patrón Singleton.
     * Inicializa la gestión de jugadores y carga los datos si existen.
     */
    private ControladorJugador() {
        this.jugadores = new HashMap<>();
        this.jugadoresLogueados = new HashSet<>();
        this.gestorAutenticacion = new GestorAutenticacion();
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
     * @param id ID del jugador
     * @return La puntuación del jugador
     */    
    public int getPuntuacion(String id) {
        Jugador j = getJugador(id);
        return j.getPuntuacion();
    }

    /**
     * Obtiene el contador de turnos que un jugador ha omitido.
     * 
     * @param id ID del jugador
     * @return El número de turnos omitidos por el jugador
     */
    public int getSkipTrack(String id) {
        Jugador j = getJugador(id);
        return j.getSkipTrack();
    }

    /**
     * Agrega una ficha al jugador.
     * 
     * @param id ID del jugador
     * @param letra Ficha que se va a agregar
     */
    public void agregarFicha(String id, String letra) {
        Jugador j = getJugador(id);
        j.agregarFicha(letra);
    }

    /**
     * Obtiene la cantidad de fichas que tiene un jugador.
     * 
     * @param id ID del jugador
     * @return La cantidad de fichas del jugador
     */
    public int getCantidadFichas(String id) {
        Jugador j = getJugador(id);
        return j.getCantidadFichas();
    }

    /**
     * Añade puntuación a un jugador.
     * 
     * @param id ID del jugador
     * @param puntuacion La puntuación a añadir
     */
    public void addPuntuacion(String id, int puntuacion) {
        Jugador j = getJugador(id);
        j.addPuntuacion(puntuacion);
    }

    /**
     * Obtiene el rack de un jugador.
     * 
     * @param id ID del jugador
     * @return El rack del jugador 
     */
    public Map<String,Integer> getRack(String id) {
        Jugador j = getJugador(id);
        return j.getRack();
    } 
    
    /**
     * Obtiene el nivel de dificultad de un jugador, si es un jugador IA.
     * 
     * @param id ID del jugador
     * @return El nivel de dificultad del jugador si es una IA, o null si no lo es
     */    
    public Dificultad getNivelDificultad(String id) {
        if (!existeJugador(id)) {
            return null;
        }

        Jugador jugador = jugadores.get(id);

        if (jugador instanceof JugadorIA jugadorIA) {
            return jugadorIA.getNivelDificultad();
        }

        return null;
    }

    /**
     * Inicializa el rack de un jugador con un conjunto de fichas.
     * 
     * @param id ID del jugador
     * @param rack El conjunto de fichas a asignar al jugador
     */
    public void inicializarRack(String id, Map<String, Integer> rack) {
        Jugador j = getJugador(id);
        j.inicializarRack(rack);
    }

    /**
     * Obtiene el nombre de un jugador dado su ID.
     * 
     * @param id ID del jugador
     * @return El nombre del jugador
     */
    public String getNombre(String id) {
        Jugador j = getJugador(id);
        return j.getNombre();
    }

    /**
     * Aumenta el contador de turnos omitidos de un jugador.
     * 
     * @param id ID del jugador
     */
    public void addSkipTrack(String id) {
        Jugador j = getJugador(id);
        j.addSkipTrack();
    }
    
    /**
    * Obtiene listado completo de usuarios (SOLO DEBUG o podemos dejarlo)
    * @return Lista con todos los datos
    */
    public List<String> getTodosUsuariosDebug() {
        List<String> usuarios = new ArrayList<>();
        Map<String, String> credenciales = gestorAutenticacion.getCredencialesDebug();
        
        // Jugadores humanos
        for (String id : getJugadoresHumanos()) {
            JugadorHumano jugador = (JugadorHumano) getJugador(id);

            usuarios.add(String.format(
                "ID: %-10s | Nombre: %-15s | Contraseña: %-12s | Partidas: %d/%d | Logueado: %s",
                id,
                jugador.getNombre(),
                credenciales.getOrDefault(id, "N/A"),
                jugador.getPartidasGanadas(),
                jugador.getPartidasJugadas(),
                jugador.isLogueado() ? "Sí" : "No"
            ));
        }
        
        // Jugadores IA
        for (String id : getJugadoresIA()) {
            JugadorIA ia = (JugadorIA) getJugador(id);
            usuarios.add(String.format(
                "ID: %-10s | Nombre: %-10s | Tipo: IA %-10s | Dificultad: %s",
                id,
                ia.getNombre(),
                ia.getClass().getSimpleName(),
                ia.getNivelDificultad()
            ));
        }
        
        return usuarios;
    }


   /**
     * Obtiene el ID de un jugador dado su nombre 
     * @param nombre Nombre a buscar
     * @return ID del jugador o null si no se encuentra
     */
    public String getIdPorNombre(String nombre) {
        if (nombre == null || jugadores == null) return null;

            for (Map.Entry<String, Jugador> entry : jugadores.entrySet()) {
                if (entry.getValue().getNombre().equals(nombre)) {
                    return entry.getKey();
                }
            }        
        

        return null;
    }

    

    /**
    * Verifica si un determinado jugador es IA.
    *
    * @pre El jugador con el ID dado debe existir en el sistema  
    * @param id ID del jugador
    * @return true si el jugador es IA, false en caso contrario
    */    
    public boolean esIA(String id) {
        // como en el método verificarPassword no paso la instancia Jugador directamente, porque se crearía un acoplamiento con domainController-Jugador
        Jugador jugador = getJugador(id);
        return jugador.esIA();
    }

    /**
    * Verifica si la contraseña introducida coincide con la del jugador.
    *
    * @pre El jugador con el ID dado debe existir en el sistema
    * @pre El jugador debe ser una instancia de JugadorHumano    
    * @param id ID del jugador
    * @param password Contraseña a verificar
    * @return true si la contraseña es correcta, false en caso contrario
    */
    public boolean verificarPassword(String id, String password) {
        // Parece redundante pasar id en vez de jugadorHumano pero si no se crearía un acoplamiento en domainController con la clase jugadorHumano
        Jugador jugador = jugadores.get(id);
        JugadorHumano jugadorHumano = (JugadorHumano) jugador;
        return jugadorHumano.verificarPassword(password);
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
        
        // Solo los jugadores humanos requieren autenticación
        if (esIA(id)) {
            return false;
        }

        boolean pwdCorrecto = verificarPassword(id, password);
        
        Jugador jugador = jugadores.get(id);
        JugadorHumano jugadorHumano = (JugadorHumano) jugador;               

        if (gestorAutenticacion.autenticar(id, password) && pwdCorrecto) {
            jugadoresLogueados.add(id);
            jugadorHumano.setLogueado(true);   
            guardarDatos();         
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
        boolean res = registrarUsuario(id, id, password); // Por defecto, el nombre es igual al ID
        return res;
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
    public boolean registrarJugadorIA(String id, Dificultad dificultad) {
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
    //       System.out.println("\n=== ESTADO ANTES DEL CAMBIO ===");
    //    getJugadoresHumanos().forEach(jugadorId -> {
    //     Jugador j = getJugador(jugadorId);
    //     System.out.println("- ID: " + jugadorId + " | Nombre: " + j.getNombre());
    // });

        ((JugadorHumano) jugador).setNombre(nuevoNombre);
    //     System.out.println("\n=== ESTADO DESPUÉS DEL CAMBIO ===");
    // getJugadoresHumanos().forEach(jugadorId -> {
    //     Jugador j = getJugador(jugadorId);
    //     System.out.println("- ID: " + jugadorId + " | Nombre: " + j.getNombre());
    // });        
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