package domain.controllers.subcontrollers;

import domain.controllers.subcontrollers.managers.GestorAutenticacion;
import domain.models.*;

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
     * @param username Nombre de usuario
     * @param password Contraseña
     * @return true si la autenticación es exitosa, false en caso contrario
     */
    public boolean autenticar(String username, String password) {
        if (!existeUsuario(username)) {
            return false;
        }
        
        if (gestorAutenticacion.autenticar(username, password)) {
            usuariosLogueados.add(username);
            
            // Si es un JugadorHumano, actualizar su estado logueado
            Usuario usuario = usuarios.get(username);
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
     * @param username Nombre de usuario
     * @return true si el usuario existe, false en caso contrario
     */
    public boolean existeUsuario(String username) {
        return usuarios.containsKey(username);
    }
    
    /**
     * Verifica si un usuario está actualmente logueado.
     * 
     * @param username Nombre de usuario
     * @return true si el usuario está logueado, false en caso contrario
     */
    public boolean isLoggedIn(String username) {
        return usuariosLogueados.contains(username);
    }
    
    /**
     * Cierra la sesión de un usuario.
     * 
     * @param username Nombre de usuario
     * @return true si se cerró la sesión correctamente, false en caso contrario
     */
    public boolean cerrarSesion(String username) {
        if (!isLoggedIn(username)) {
            return false;
        }
        
        usuariosLogueados.remove(username);
        
        // Si es un JugadorHumano, actualizar su estado logueado
        Usuario usuario = usuarios.get(username);
        if (usuario instanceof JugadorHumano) {
            ((JugadorHumano) usuario).setLogueado(false);
        }
        
        return true;
    }
    
    /**
     * Registra un nuevo jugador humano en el sistema.
     * 
     * @param username Nombre de usuario
     * @param password Contraseña
     * @return true si se registró correctamente, false en caso contrario
     */
    public boolean registrarUsuario(String username, String password) {
        if (existeUsuario(username)) {
            return false;
        }
        
        JugadorHumano nuevoJugador = new JugadorHumano(username, password);
        usuarios.put(username, nuevoJugador);
        gestorAutenticacion.registrar(username, password);
        
        guardarDatos();
        return true;
    }
    
    /**
     * Registra un nuevo jugador humano en el sistema con información extendida.
     * 
     * @param username Nombre de usuario
     * @param password Contraseña
     * @param email Correo electrónico
     * @param nombreCompleto Nombre completo
     * @return true si se registró correctamente, false en caso contrario
     */
    public boolean registrarUsuario(String username, String password, String email, String nombreCompleto) {
        if (existeUsuario(username)) {
            return false;
        }
        
        JugadorHumano nuevoJugador = new JugadorHumano(username, password, email, nombreCompleto);
        usuarios.put(username, nuevoJugador);
        gestorAutenticacion.registrar(username, password);
        
        guardarDatos();
        return true;
    }
    
    /**
     * Registra un nuevo jugador IA en el sistema.
     * 
     * @param username Nombre para la IA
     * @param dificultad Nivel de dificultad
     * @return true si se registró correctamente, false en caso contrario
     */
    public boolean registrarJugadorIA(String username, JugadorIA.Dificultad dificultad) {
        if (existeUsuario(username)) {
            return false;
        }
        
        JugadorIA nuevoJugadorIA = new JugadorIA(username, dificultad);
        usuarios.put(username, nuevoJugadorIA);
        
        guardarDatos();
        return true;
    }
    
    /**
     * Elimina un usuario del sistema.
     * 
     * @param username Nombre del usuario a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public boolean eliminarUsuario(String username) {
        if (!existeUsuario(username)) {
            return false;
        }
        
        // Si está logueado, cerrar sesión
        if (isLoggedIn(username)) {
            cerrarSesion(username);
        }
        
        usuarios.remove(username);
        gestorAutenticacion.eliminarUsuario(username);
        
        guardarDatos();
        return true;
    }
    
    /**
     * Cambia la contraseña de un usuario.
     * 
     * @param username Nombre de usuario
     * @param oldPassword Contraseña actual
     * @param newPassword Nueva contraseña
     * @return true si se cambió correctamente, false en caso contrario
     */
    public boolean cambiarContrasena(String username, String oldPassword, String newPassword) {
        if (!existeUsuario(username)) {
            return false;
        }
        
        Usuario usuario = usuarios.get(username);
        if (!usuario.verificarPassword(oldPassword)) {
            return false;
        }
        
        usuario.setPassword(newPassword);
        gestorAutenticacion.cambiarContrasena(username, newPassword);
        
        guardarDatos();
        return true;
    }
    
    /**
     * Actualiza la información del perfil de un usuario.
     * 
     * @param username Nombre de usuario
     * @param email Nuevo correo electrónico
     * @param nombreCompleto Nuevo nombre completo
     * @return true si se actualizó correctamente, false en caso contrario
     */
    public boolean actualizarPerfil(String username, String email, String nombreCompleto) {
        if (!existeUsuario(username)) {
            return false;
        }
        
        Usuario usuario = usuarios.get(username);
        usuario.setEmail(email);
        usuario.setNombreCompleto(nombreCompleto);
        
        guardarDatos();
        return true;
    }
    
    /**
     * Obtiene un usuario por su nombre de usuario.
     * 
     * @param username Nombre del usuario
     * @return Usuario encontrado o null si no existe
     */
    public Usuario getUsuario(String username) {
        return usuarios.get(username);
    }
    
    /**
     * Incrementa el contador de partidas jugadas para un usuario.
     * 
     * @param username Nombre del usuario
     * @return true si se incrementó correctamente, false en caso contrario
     */
    public boolean incrementarPartidasJugadas(String username) {
        if (!existeUsuario(username)) {
            return false;
        }
        
        Usuario usuario = usuarios.get(username);
        usuario.incrementarPartidasJugadas();
        
        guardarDatos();
        return true;
    }
    
    /**
     * Incrementa el contador de partidas ganadas para un usuario.
     * 
     * @param username Nombre del usuario
     * @return true si se incrementó correctamente, false en caso contrario
     */
    public boolean incrementarPartidasGanadas(String username) {
        if (!existeUsuario(username)) {
            return false;
        }
        
        Usuario usuario = usuarios.get(username);
        usuario.incrementarPartidasGanadas();
        
        guardarDatos();
        return true;
    }
    
    /**
     * Establece si un jugador humano está en una partida.
     * 
     * @param username Nombre del usuario
     * @param enPartida true si está en partida, false en caso contrario
     * @return true si se estableció correctamente, false en caso contrario
     */
    public boolean setEnPartida(String username, boolean enPartida) {
        if (!existeUsuario(username)) {
            return false;
        }
        
        Usuario usuario = usuarios.get(username);
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
     * @param username Nombre del usuario
     * @return true si está en partida, false en caso contrario
     */
    public boolean isEnPartida(String username) {
        if (!existeUsuario(username)) {
            return false;
        }
        
        Usuario usuario = usuarios.get(username);
        if (usuario instanceof JugadorHumano) {
            return ((JugadorHumano) usuario).isEnPartida();
        }
        
        return false;
    }
    
    /**
     * Obtiene una lista de nombres de todos los usuarios registrados.
     * 
     * @return Lista de nombres de usuario
     */
    public List<String> getUsuariosRegistrados() {
        return new ArrayList<>(usuarios.keySet());
    }
    
    /**
     * Obtiene una lista de nombres de todos los usuarios humanos.
     * 
     * @return Lista de nombres de usuarios humanos
     */
    public List<String> getUsuariosHumanos() {
        List<String> usuariosHumanos = new ArrayList<>();
        
        for (Map.Entry<String, Usuario> entry : usuarios.entrySet()) {
            if (entry.getValue() instanceof JugadorHumano) {
                usuariosHumanos.add(entry.getKey());
            }
        }
        
        return usuariosHumanos;
    }
    
    /**
     * Obtiene una lista de nombres de todos los usuarios IA.
     * 
     * @return Lista de nombres de usuarios IA
     */
    public List<String> getUsuariosIA() {
        List<String> usuariosIA = new ArrayList<>();
        
        for (Map.Entry<String, Usuario> entry : usuarios.entrySet()) {
            if (entry.getValue() instanceof JugadorIA) {
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