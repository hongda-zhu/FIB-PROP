package scrabble.domain.controllers.subcontrollers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import scrabble.domain.models.Ranking;

/**
 * Controlador para la gestión del ranking de jugadores.
 * Implementa el patrón Singleton para garantizar una única instancia.
 */
public class ControladorRanking {
    private static ControladorRanking instance;
    private Ranking ranking;
    private static final String RANKING_FILE = "ranking.dat";
    
    /**
     * Constructor privado para implementar el patrón Singleton.
     * Inicializa el ranking y carga los datos si existen.
     */
    private ControladorRanking() {
        this.ranking = new Ranking();
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
     * @param id ID del usuario
     * @param puntuacion Puntuación a agregar
     * @return true si se agregó correctamente, false en caso contrario
     */
    public boolean agregarPuntuacion(String id, int puntuacion) {
        // Verificar que se proporciona un usuario válido
        if (id == null || id.isEmpty()) {
            System.err.println("Error: El ID del usuario no puede estar vacío.");
            return false;
        }
        
        boolean resultado = ranking.agregarPuntuacion(id, puntuacion);
        
        if (resultado) {
            // Guardar los cambios
            guardarDatos();
            
            System.out.println("Puntuación " + puntuacion + " agregada para " + id);
        } else {
            System.err.println("Error: No se pudo agregar la puntuación para " + id);
        }
        
        return resultado;
    }
    
    /**
     * Actualiza las estadísticas de un usuario (partidas jugadas y victoria).
     * 
     * @param id ID del usuario
     * @param esVictoria true si el usuario ha ganado la partida
     * @return true si se actualizaron correctamente las estadísticas
     */
    public boolean actualizarEstadisticasUsuario(String id, boolean esVictoria) {
        // Verificar que se proporciona un usuario válido
        if (id == null || id.isEmpty()) {
            System.err.println("Error: El ID del usuario no puede estar vacío.");
            return false;
        }
        
        ranking.actualizarEstadisticasUsuario(id, esVictoria);
        guardarDatos();
        
        return true;
    }
    
    /**
     * Elimina una puntuación específica de un usuario.
     * 
     * @param id ID del usuario
     * @param puntuacion Puntuación a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public boolean eliminarPuntuacion(String id, int puntuacion) {
        boolean resultado = ranking.eliminarPuntuacion(id, puntuacion);
        
        if (resultado) {
            // Guardar los cambios
            guardarDatos();
            System.out.println("Puntuación " + puntuacion + " eliminada para " + id);
        } else {
            System.err.println("Error: No se pudo eliminar la puntuación para " + id);
        }
        
        return resultado;
    }
    
    /**
     * Verifica si existe una puntuación específica para un usuario.
     * 
     * @param id ID del usuario
     * @param puntuacion Puntuación a verificar
     * @return true si existe la puntuación, false en caso contrario
     */
    public boolean existePuntuacion(String id, int puntuacion) {
        return ranking.existePuntuacion(id, puntuacion);
    }
    

    /**
     * Verifica si está en el ranking un usuario específico, independientemente de la puntuación.
     * 
     * @param id ID del usuario
     * @return true si existe alguna puntuación, false en caso contrario
     */
    public boolean perteneceRanking(String id) {
        return ranking.perteneceRanking(id);
    }
    /**
     * Elimina un usuario del ranking.
     * 
     * @param id ID del usuario a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public boolean eliminarUsuario(String id) {
        boolean resultado = ranking.eliminarUsuario(id);
        
        if (resultado) {
            // Guardar los cambios
            guardarDatos();
            System.out.println("Usuario " + id + " eliminado del ranking.");
        } else {
            System.err.println("Error: No se pudo eliminar el usuario " + id + " del ranking.");
        }
        
        return resultado;
    }
    
    /**
     * Cambia la estrategia de ordenación del ranking.
     * 
     * @param criterio Criterio de ordenación
     */
    public void cambiarEstrategia(String criterio) {
        ranking.setEstrategia(criterio);
        System.out.println("Estrategia de ranking cambiada a: " + ranking.getEstrategiaActual());
    }
    
    /**
     * Muestra el ranking de usuarios según la estrategia actual.
     */
    public void verRanking() {
        List<String> rankingOrdenado = ranking.getRanking();
        
        System.out.println("===== RANKING DE JUGADORES (" + ranking.getEstrategiaActual() + ") =====");
        System.out.println("Posición | Usuario | Puntuación Máxima | Puntuación Media | Partidas | Victorias");
        System.out.println("--------------------------------------------------------------------------------");
        
        int posicion = 1;
        for (String id : rankingOrdenado) {
            int puntuacionMaxima = ranking.getPuntuacionMaxima(id);
            double puntuacionMedia = ranking.getPuntuacionMedia(id);
            int partidas = ranking.getPartidasJugadas(id);
            int victorias = ranking.getVictorias(id);
            
            System.out.printf("%8d | %-20s | %15d | %15.2f | %7d | %9d%n", 
                             posicion++, id, puntuacionMaxima, puntuacionMedia, partidas, victorias);
        }
        
        System.out.println("================================================================================");
    }
    
    /**
     * Muestra el ranking de usuarios con un criterio específico.
     * 
     * @param criterio Criterio de ordenación
     */
    public void verRanking(String criterio) {
        List<String> rankingOrdenado = ranking.getRanking(criterio);
        
        System.out.println("===== RANKING DE JUGADORES (" + criterio + ") =====");
        System.out.println("Posición | Usuario | Puntuación Máxima | Puntuación Media | Partidas | Victorias");
        System.out.println("--------------------------------------------------------------------------------");
        
        int posicion = 1;
        for (String id : rankingOrdenado) {
            int puntuacionMaxima = ranking.getPuntuacionMaxima(id);
            double puntuacionMedia = ranking.getPuntuacionMedia(id);
            int partidas = ranking.getPartidasJugadas(id);
            int victorias = ranking.getVictorias(id);
            
            System.out.printf("%8d | %-20s | %15d | %15.2f | %7d | %9d%n", 
                             posicion++, id, puntuacionMaxima, puntuacionMedia, partidas, victorias);
        }
        
        System.out.println("================================================================================");
    }
    
    /**
     * Obtiene todas las puntuaciones de un usuario específico.
     * 
     * @param id ID del usuario
     * @return Lista de puntuaciones del usuario
     */
    public List<Integer> getPuntuacionesUsuario(String id) {
        return ranking.getPuntuacionesUsuario(id);
    }
    
    /**
     * Obtiene la puntuación máxima de un usuario específico.
     * 
     * @param id ID del usuario
     * @return Puntuación máxima del usuario
     */
    public int getPuntuacionMaxima(String id) {
        return ranking.getPuntuacionMaxima(id);
    }
    
    /**
     * Obtiene la puntuación media de un usuario específico.
     * 
     * @param id ID del usuario
     * @return Puntuación media del usuario
     */
    public double getPuntuacionMedia(String id) {
        return ranking.getPuntuacionMedia(id);
    }
    
    /**
     * Obtiene el número de partidas jugadas por un usuario específico.
     * 
     * @param id ID del usuario
     * @return Número de partidas jugadas
     */
    public int getPartidasJugadas(String id) {
        return ranking.getPartidasJugadas(id);
    }
    
    /**
     * Obtiene el número de victorias de un usuario específico.
     * 
     * @param id ID del usuario
     * @return Número de victorias
     */
    public int getVictorias(String id) {
        return ranking.getVictorias(id);
    }
    
    /**
     * Lista todas las puntuaciones registradas.
     */
    public void listarPuntuaciones() {
        System.out.println("===== PUNTUACIONES POR USUARIO =====");
        
        for (String id : ranking.getUsuarios()) {
            List<Integer> puntuaciones = ranking.getPuntuacionesUsuario(id);
            if (!puntuaciones.isEmpty()) {
                System.out.println("Usuario: " + id);
                System.out.println("Puntuaciones: " + puntuaciones);
                System.out.println("Máxima: " + ranking.getPuntuacionMaxima(id));
                System.out.println("Media: " + ranking.getPuntuacionMedia(id));
                System.out.println("Partidas jugadas: " + ranking.getPartidasJugadas(id));
                System.out.println("Victorias: " + ranking.getVictorias(id));
                System.out.println("------------------------------");
            }
        }
    }
    
    /**
     * Guarda los datos del ranking en un archivo.
     */
    private void guardarDatos() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RANKING_FILE))) {
            oos.writeObject(ranking);
        } catch (IOException e) {
            System.err.println("Error al guardar el ranking: " + e.getMessage());
        }
    }
    
    /**
     * Carga los datos del ranking desde un archivo.
     */
    private void cargarDatos() {
        File rankingFile = new File(RANKING_FILE);
        if (rankingFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(rankingFile))) {
                ranking = (Ranking) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error al cargar el ranking: " + e.getMessage());
                ranking = new Ranking(); // Si hay error, inicializar con uno nuevo
            }
        }
    }
}