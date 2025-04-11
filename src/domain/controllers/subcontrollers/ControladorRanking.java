package domain.controllers.subcontrollers;

import domain.models.Ranking;
import domain.models.Usuario;
import domain.models.JugadorHumano;
import domain.models.JugadorIA;

import java.io.*;
import java.util.*;

/**
 * Controlador para la gestión del ranking de jugadores.
 * Implementa el patrón Singleton para garantizar una única instancia.
 */
public class ControladorRanking {
    private static ControladorRanking instance;
    private Ranking ranking;
    private Map<String, Usuario> usuarios;
    private static final String RANKING_FILE = "ranking.dat";
    private static final String USUARIOS_FILE = "usuarios.dat";
    
    /**
     * Constructor privado para implementar el patrón Singleton.
     * Inicializa el ranking y carga los datos si existen.
     */
    private ControladorRanking() {
        this.ranking = new Ranking();
        this.usuarios = new HashMap<>();
        cargarDatos();
    }
    
    /**
     * Obtiene la instancia única del controlador (Singleton).
     * 
     * @return Instancia de ControladorRanking
     */
    public static synchronized ControladorRanking getInstance() {
        if (instance == null) {
            instance = new ControladorRanking();
        }
        return instance;
    }
    
    /**
     * Agrega una puntuación para un usuario específico.
     * 
     * @param username Nombre del usuario
     * @param puntuacion Puntuación a agregar
     * @return true si se agregó correctamente, false en caso contrario
     */
    public boolean agregarPuntuacion(String username, int puntuacion) {
        // Verificar si el usuario existe
        if (!usuarios.containsKey(username)) {
            System.err.println("Error: El usuario " + username + " no existe.");
            return false;
        }
        
        boolean resultado = ranking.agregarPuntuacion(username, puntuacion);
        
        if (resultado) {
            // Actualizar estadísticas del usuario
            Usuario usuario = usuarios.get(username);
            usuario.incrementarPartidasJugadas();
            
            // Guardar los cambios
            guardarDatos();
            
            System.out.println("Puntuación " + puntuacion + " agregada para " + username);
        } else {
            System.err.println("Error: No se pudo agregar la puntuación para " + username);
        }
        
        return resultado;
    }
    
    /**
     * Elimina una puntuación específica de un usuario.
     * 
     * @param username Nombre del usuario
     * @param puntuacion Puntuación a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public boolean eliminarPuntuacion(String username, int puntuacion) {
        boolean resultado = ranking.eliminarPuntuacion(username, puntuacion);
        
        if (resultado) {
            // Guardar los cambios
            guardarDatos();
            System.out.println("Puntuación " + puntuacion + " eliminada para " + username);
        } else {
            System.err.println("Error: No se pudo eliminar la puntuación para " + username);
        }
        
        return resultado;
    }
    
    /**
     * Verifica si existe una puntuación específica para un usuario.
     * 
     * @param username Nombre del usuario
     * @param puntuacion Puntuación a verificar
     * @return true si existe la puntuación, false en caso contrario
     */
    public boolean existePuntuacion(String username, int puntuacion) {
        return ranking.existePuntuacion(username, puntuacion);
    }
    
    /**
     * Registra un nuevo usuario en el sistema.
     * 
     * @param username Nombre de usuario
     * @param password Contraseña
     * @return true si se registró correctamente, false en caso contrario
     */
    public boolean registrarUsuario(String username, String password) {
        if (usuarios.containsKey(username)) {
            System.err.println("Error: El usuario " + username + " ya existe.");
            return false;
        }
        
        Usuario nuevoUsuario = new JugadorHumano(username, password);
        usuarios.put(username, nuevoUsuario);
        
        // Guardar los cambios
        guardarDatos();
        
        System.out.println("Usuario " + username + " registrado correctamente.");
        return true;
    }
    
    /**
     * Registra un nuevo usuario en el sistema con información extendida.
     * 
     * @param username Nombre de usuario
     * @param password Contraseña
     * @param email Correo electrónico
     * @param nombreCompleto Nombre completo
     * @return true si se registró correctamente, false en caso contrario
     */
    public boolean registrarUsuario(String username, String password, String email, String nombreCompleto) {
        if (usuarios.containsKey(username)) {
            System.err.println("Error: El usuario " + username + " ya existe.");
            return false;
        }
        
        Usuario nuevoUsuario = new JugadorHumano(username, password, email, nombreCompleto);
        usuarios.put(username, nuevoUsuario);
        
        // Guardar los cambios
        guardarDatos();
        
        System.out.println("Usuario " + username + " registrado correctamente.");
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
        if (usuarios.containsKey(username)) {
            System.err.println("Error: El usuario " + username + " ya existe.");
            return false;
        }
        
        JugadorIA nuevoJugadorIA = new JugadorIA(username, dificultad);
        usuarios.put(username, nuevoJugadorIA);
        
        // Guardar los cambios
        guardarDatos();
        
        System.out.println("Jugador IA " + username + " registrado correctamente.");
        return true;
    }
    
    /**
     * Elimina un usuario del sistema.
     * 
     * @param username Nombre del usuario a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public boolean eliminarUsuario(String username) {
        if (!usuarios.containsKey(username)) {
            System.err.println("Error: El usuario " + username + " no existe.");
            return false;
        }
        
        usuarios.remove(username);
        ranking.eliminarUsuario(username);
        
        // Guardar los cambios
        guardarDatos();
        
        System.out.println("Usuario " + username + " eliminado correctamente.");
        return true;
    }
    
    /**
     * Obtiene un usuario por su nombre de usuario.
     * 
     * @param username Nombre del usuario
     * @return Usuario encontrado o null si no existe
     */
    public Usuario getUsuario(String username) {
        return usuarios.getOrDefault(username, null);
    }
    
    /**
     * Verifica si un usuario existe en el sistema.
     * 
     * @param username Nombre del usuario
     * @return true si el usuario existe, false en caso contrario
     */
    public boolean existeUsuario(String username) {
        return usuarios.containsKey(username);
    }
    
    /**
     * Muestra el ranking de usuarios ordenado por puntuación máxima.
     */
    public void verRanking() {
        List<String> rankingOrdenado = ranking.getRankingPorPuntuacionMaxima();
        
        System.out.println("===== RANKING DE JUGADORES =====");
        System.out.println("Posición | Usuario | Puntuación Máxima | Puntuación Media | Partidas");
        System.out.println("------------------------------------------------------------------");
        
        int posicion = 1;
        for (String username : rankingOrdenado) {
            Usuario usuario = usuarios.get(username);
            int puntuacionMaxima = ranking.getPuntuacionMaxima(username);
            double puntuacionMedia = ranking.getPuntuacionMedia(username);
            int partidas = usuario != null ? usuario.getPartidasJugadas() : 0;
            
            System.out.printf("%8d | %-20s | %15d | %15.2f | %7d%n", 
                             posicion++, username, puntuacionMaxima, puntuacionMedia, partidas);
        }
        
        System.out.println("==================================================================");
    }
    
    /**
     * Muestra el ranking de usuarios filtrado y ordenado según un criterio.
     * 
     * @param criterio Criterio de ordenación: "max" (máxima), "media" o "partidas"
     */
    public void verRanking(String criterio) {
        List<String> rankingOrdenado;
        
        switch (criterio.toLowerCase()) {
            case "max":
                rankingOrdenado = ranking.getRankingPorPuntuacionMaxima();
                break;
            case "media":
                rankingOrdenado = ranking.getRankingPorPuntuacionMedia();
                break;
            case "partidas":
                rankingOrdenado = ranking.getRankingPorPartidasJugadas();
                break;
            default:
                System.err.println("Criterio no válido. Usando puntuación máxima por defecto.");
                rankingOrdenado = ranking.getRankingPorPuntuacionMaxima();
        }
        
        System.out.println("===== RANKING DE JUGADORES (" + criterio + ") =====");
        System.out.println("Posición | Usuario | Puntuación Máxima | Puntuación Media | Partidas");
        System.out.println("------------------------------------------------------------------");
        
        int posicion = 1;
        for (String username : rankingOrdenado) {
            Usuario usuario = usuarios.get(username);
            int puntuacionMaxima = ranking.getPuntuacionMaxima(username);
            double puntuacionMedia = ranking.getPuntuacionMedia(username);
            int partidas = usuario != null ? usuario.getPartidasJugadas() : 0;
            
            System.out.printf("%8d | %-20s | %15d | %15.2f | %7d%n", 
                             posicion++, username, puntuacionMaxima, puntuacionMedia, partidas);
        }
        
        System.out.println("==================================================================");
    }
    
    /**
     * Obtiene todas las puntuaciones de un usuario específico.
     * 
     * @param username Nombre del usuario
     * @return Lista de puntuaciones del usuario
     */
    public List<Integer> getPuntuacionesUsuario(String username) {
        return ranking.getPuntuacionesUsuario(username);
    }
    
    /**
     * Obtiene la puntuación máxima de un usuario específico.
     * 
     * @param username Nombre del usuario
     * @return Puntuación máxima del usuario
     */
    public int getPuntuacionMaxima(String username) {
        return ranking.getPuntuacionMaxima(username);
    }
    
    /**
     * Obtiene la puntuación media de un usuario específico.
     * 
     * @param username Nombre del usuario
     * @return Puntuación media del usuario
     */
    public double getPuntuacionMedia(String username) {
        return ranking.getPuntuacionMedia(username);
    }
    
    /**
     * Registra una victoria para un usuario.
     * 
     * @param username Nombre del usuario
     * @return true si se registró correctamente, false en caso contrario
     */
    public boolean registrarVictoria(String username) {
        if (!usuarios.containsKey(username)) {
            System.err.println("Error: El usuario " + username + " no existe.");
            return false;
        }
        
        Usuario usuario = usuarios.get(username);
        usuario.incrementarPartidasGanadas();
        
        // Guardar los cambios
        guardarDatos();
        
        System.out.println("Victoria registrada para " + username);
        return true;
    }
    
    /**
     * Guarda los datos del ranking y usuarios en archivos.
     */
    private void guardarDatos() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RANKING_FILE))) {
            oos.writeObject(ranking);
            System.out.println("Ranking guardado correctamente.");
        } catch (IOException e) {
            System.err.println("Error al guardar el ranking: " + e.getMessage());
        }
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USUARIOS_FILE))) {
            oos.writeObject(usuarios);
            System.out.println("Usuarios guardados correctamente.");
        } catch (IOException e) {
            System.err.println("Error al guardar los usuarios: " + e.getMessage());
        }
    }
    
    /**
     * Carga los datos del ranking y usuarios desde archivos.
     */
    @SuppressWarnings("unchecked")
    private void cargarDatos() {
        File rankingFile = new File(RANKING_FILE);
        if (rankingFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(rankingFile))) {
                ranking = (Ranking) ois.readObject();
                System.out.println("Ranking cargado correctamente.");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error al cargar el ranking: " + e.getMessage());
                ranking = new Ranking(); // Si hay error, inicializar con uno nuevo
            }
        }
        
        File usuariosFile = new File(USUARIOS_FILE);
        if (usuariosFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(usuariosFile))) {
                usuarios = (Map<String, Usuario>) ois.readObject();
                System.out.println("Usuarios cargados correctamente.");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error al cargar los usuarios: " + e.getMessage());
                usuarios = new HashMap<>(); // Si hay error, inicializar con uno nuevo
            }
        }
    }
    
    /**
     * Lista todas las puntuaciones registradas.
     */
    public void listarPuntuaciones() {
        System.out.println("===== PUNTUACIONES POR USUARIO =====");
        
        for (String username : usuarios.keySet()) {
            List<Integer> puntuaciones = ranking.getPuntuacionesUsuario(username);
            if (!puntuaciones.isEmpty()) {
                System.out.println("Usuario: " + username);
                System.out.println("Puntuaciones: " + puntuaciones);
                System.out.println("Máxima: " + ranking.getPuntuacionMaxima(username));
                System.out.println("Media: " + ranking.getPuntuacionMedia(username));
                System.out.println("------------------------------");
            }
        }
    }
}