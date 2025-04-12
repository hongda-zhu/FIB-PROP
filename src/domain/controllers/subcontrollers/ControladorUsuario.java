package domain.controllers.subcontrollers;

import domain.controllers.subcontrollers.managers.GestorAutenticacion;
import domain.models.JugadorHumano;
import domain.models.JugadorIA;
import domain.models.Usuario;

import java.io.*;
import java.util.*;

/**
 * Controlador para la gestión de usuarios.
 * Implementa el patrón Singleton para garantizar una única instancia.
 */
public class ControladorUsuario {
    private static ControladorUsuario instance;
    private Map<String, Usuario> usuarios;
    private Set<String> usuariosLogueados;
    private GestorAutenticacion gestorAutenticacion;
    
    private static final String USUARIOS_FILE = "usuarios.dat";
    
    /**
     * Constructor privado para implementar el patrón Singleton.
     * Inicializa la gestión de usuarios y carga los datos si existen.
     */
    private ControladorUsuario() {
        this.usuarios = new HashMap<>();
        this.usuariosLogueados = new HashSet<>();
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
     * Autentica a un usuario en el sistema.
     * 
     * @param id ID del usuario
     * @param password Contraseña
     * @return true si la autenticación es exitosa, false en caso contrario
     */
    public boolean autenticar(String id, String password) {
        if (!existeUsuario(id)) {
            return false;
        }
        
        if (gestorAutenticacion.autenticar(id, password)) {
            usuariosLogueados.add(id);
            
            // Si es un JugadorHumano, actualizar su estado logueado
            Usuario usuario = usuarios.get(id);
            if (usuario instanceof JugadorHumano) {
                ((JugadorHumano) usuario).setLogueado(true);
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Verifica si un usuario existe en el sistema.
     * 
     * @param id ID del usuario
     * @return true si el usuario existe, false en caso contrario
     */
    public boolean existeUsuario(String id) {
        return usuarios.containsKey(id);
    }
    
    /**
     * Verifica si un usuario está actualmente logueado.
     * 
     * @param id ID del usuario
     * @return true si el usuario está logueado, false en caso contrario
     */
    public boolean isLoggedIn(String id) {
        return usuariosLogueados.contains(id);
    }
    
    /**
     * Cierra la sesión de un usuario.
     * 
     * @param id ID del usuario
     * @return true si se cerró la sesión correctamente, false en caso contrario
     */
    public boolean cerrarSesion(String id) {
        if (!isLoggedIn(id)) {
            return false;
        }
        
        usuariosLogueados.remove(id);
        
        // Si es un JugadorHumano, actualizar su estado logueado
        Usuario usuario = usuarios.get(id);
        if (usuario instanceof JugadorHumano) {
            ((JugadorHumano) usuario).setLogueado(false);
        }
        
        return true;
    }
    
    /**
     * Registra un nuevo jugador humano en el sistema.
     * 
     * @param id ID del usuario
     * @param password Contraseña
     * @return true si se registró correctamente, false en caso contrario
     */
    public boolean registrarUsuario(String id, String password) {
        if (existeUsuario(id)) {
            return false;
        }
        
        JugadorHumano nuevoJugador = new JugadorHumano(id, password);
        usuarios.put(id, nuevoJugador);
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
        if (existeUsuario(id)) {
            return false;
        }
        
        JugadorIA nuevoJugadorIA = new JugadorIA(id, dificultad);
        usuarios.put(id, nuevoJugadorIA);
        
        guardarDatos();
        return true;
    }
    
    /**
     * Elimina un usuario del sistema.
     * 
     * @param id ID del usuario a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public boolean eliminarUsuario(String id) {
        if (!existeUsuario(id)) {
            return false;
        }
        
        // Si está logueado, cerrar sesión
        if (isLoggedIn(id)) {
            cerrarSesion(id);
        }
        
        usuarios.remove(id);
        gestorAutenticacion.eliminarUsuario(id);
        
        guardarDatos();
        return true;
    }
    
    /**
     * Cambia la contraseña de un usuario.
     * 
     * @param id ID del usuario
     * @param oldPassword Contraseña actual
     * @param newPassword Nueva contraseña
     * @return true si se cambió correctamente, false en caso contrario
     */
    public boolean cambiarContrasena(String id, String oldPassword, String newPassword) {
        if (!existeUsuario(id)) {
            return false;
        }
        
        Usuario usuario = usuarios.get(id);
        if (!usuario.verificarPassword(oldPassword)) {
            return false;
        }
        
        usuario.setPassword(newPassword);
        gestorAutenticacion.cambiarContrasena(id, newPassword);
        
        guardarDatos();
        return true;
    }
    
    /**
     * Obtiene un usuario por su ID.
     * 
     * @param id ID del usuario
     * @return Usuario encontrado o null si no existe
     */
    public Usuario getUsuario(String id) {
        return usuarios.get(id);
    }
    
    /**
     * Incrementa el contador de partidas jugadas para un usuario.
     * 
     * @param id ID del usuario
     * @return true si se incrementó correctamente, false en caso contrario
     */
    public boolean incrementarPartidasJugadas(String id) {
        if (!existeUsuario(id)) {
            return false;
        }
        
        Usuario usuario = usuarios.get(id);
        if (usuario.esIA()) {
            return false; // Las IAs no tienen contador de partidas
        }
        
        usuario.incrementarPartidasJugadas();
        
        guardarDatos();
        return true;
    }
    
    /**
     * Incrementa el contador de partidas ganadas para un usuario.
     * 
     * @param id ID del usuario
     * @return true si se incrementó correctamente, false en caso contrario
     */
    public boolean incrementarPartidasGanadas(String id) {
        if (!existeUsuario(id)) {
            return false;
        }
        
        Usuario usuario = usuarios.get(id);
        if (usuario.esIA()) {
            return false; // Las IAs no tienen contador de partidas
        }
        
        usuario.incrementarPartidasGanadas();
        
        guardarDatos();
        return true;
    }
    
    /**
     * Establece si un jugador humano está en una partida.
     * 
     * @param id ID del usuario
     * @param enPartida true si está en partida, false en caso contrario
     * @return true si se estableció correctamente, false en caso contrario
     */
    public boolean setEnPartida(String id, boolean enPartida) {
        if (!existeUsuario(id)) {
            return false;
        }
        
        Usuario usuario = usuarios.get(id);
        if (usuario instanceof JugadorHumano) {
            ((JugadorHumano) usuario).setEnPartida(enPartida);
            guardarDatos();
            return true;
        }
        
        return false;
    }
    
    /**
     * Verifica si un jugador humano está en una partida.
     * 
     * @param id ID del usuario
     * @return true si está en partida, false en caso contrario
     */
    public boolean isEnPartida(String id) {
        if (!existeUsuario(id)) {
            return false;
        }
        
        Usuario usuario = usuarios.get(id);
        if (usuario instanceof JugadorHumano) {
            return ((JugadorHumano) usuario).isEnPartida();
        }
        
        return false;
    }
    
    /**
     * Establece la puntuación para un jugador IA.
     * 
     * @param id ID de la IA
     * @param puntuacion Puntuación
     * @return true si se estableció correctamente, false en caso contrario
     */
    public boolean setPuntuacionIA(String id, int puntuacion) {
        if (!existeUsuario(id)) {
            return false;
        }
        
        Usuario usuario = usuarios.get(id);
        if (usuario instanceof JugadorIA) {
            ((JugadorIA) usuario).setPuntuacion(puntuacion);
            guardarDatos();
            return true;
        }
        
        return false;
    }
    
    /**
     * Establece la puntuación de última partida para un jugador humano.
     * 
     * @param id ID del jugador
     * @param puntuacion Puntuación
     * @return true si se estableció correctamente, false en caso contrario
     */
    public boolean setPuntuacionUltimaPartida(String id, int puntuacion) {
        if (!existeUsuario(id)) {
            return false;
        }
        
        Usuario usuario = usuarios.get(id);
        if (usuario instanceof JugadorHumano) {
            ((JugadorHumano) usuario).setPuntuacionUltimaPartida(puntuacion);
            guardarDatos();
            return true;
        }
        
        return false;
    }
    
    /**
     * Obtiene una lista de IDs de todos los usuarios registrados.
     * 
     * @return Lista de IDs de usuario
     */
    public List<String> getUsuariosRegistrados() {
        return new ArrayList<>(usuarios.keySet());
    }
    
    /**
     * Obtiene una lista de IDs de todos los usuarios humanos.
     * 
     * @return Lista de IDs de usuarios humanos
     */
    public List<String> getUsuariosHumanos() {
        List<String> usuariosHumanos = new ArrayList<>();
        
        for (Map.Entry<String, Usuario> entry : usuarios.entrySet()) {
            if (!entry.getValue().esIA()) {
                usuariosHumanos.add(entry.getKey());
            }
        }
        
        return usuariosHumanos;
    }
    
    /**
     * Obtiene una lista de IDs de todos los usuarios IA.
     * 
     * @return Lista de IDs de usuarios IA
     */
    public List<String> getUsuariosIA() {
        List<String> usuariosIA = new ArrayList<>();
        
        for (Map.Entry<String, Usuario> entry : usuarios.entrySet()) {
            if (entry.getValue().esIA()) {
                usuariosIA.add(entry.getKey());
            }
        }
        
        return usuariosIA;
    }
    
    /**
     * Guarda los datos de los usuarios en un archivo.
     */
    private void guardarDatos() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USUARIOS_FILE))) {
            oos.writeObject(usuarios);
        } catch (IOException e) {
            System.err.println("Error al guardar los usuarios: " + e.getMessage());
        }
    }
    
    /**
     * Carga los datos de los usuarios desde un archivo.
     */
    @SuppressWarnings("unchecked")
    private void cargarDatos() {
        File usuariosFile = new File(USUARIOS_FILE);
        if (usuariosFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(usuariosFile))) {
                usuarios = (Map<String, Usuario>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error al cargar los usuarios: " + e.getMessage());
                usuarios = new HashMap<>(); // Si hay error, inicializar con uno nuevo
            }
        }
    }
}