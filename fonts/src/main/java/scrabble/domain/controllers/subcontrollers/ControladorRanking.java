package scrabble.domain.controllers.subcontrollers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scrabble.domain.models.rankingStrategy.RankingOrderStrategy;
import scrabble.domain.models.rankingStrategy.RankingOrderStrategyFactory;

/**
 * Controlador para la gestión del ranking de jugadores.
 * Implementa el patrón Singleton para garantizar una única instancia.
 */
public class ControladorRanking implements Serializable {
    private static final long serialVersionUID = 1L;
    private static transient ControladorRanking instance;
    
    // Estructura de datos para almacenar las puntuaciones de los usuarios
    private Map<String, List<Integer>> puntuacionesPorUsuario;
    private Map<String, Integer> puntuacionMaximaPorUsuario;
    private Map<String, Double> puntuacionMediaPorUsuario;
    private Map<String, Integer> partidasJugadasPorUsuario;
    private Map<String, Integer> victoriasUsuario;
    
    // Estrategia actual de ordenación
    private String estrategiaActual;
    
    private static final String RANKING_FILE = "ranking.dat";
    
    /**
     * Constructor privado para implementar el patrón Singleton.
     * Inicializa las estructuras de datos y carga los datos existentes.
     */
    private ControladorRanking() {
        this.puntuacionesPorUsuario = new HashMap<>();
        this.puntuacionMaximaPorUsuario = new HashMap<>();
        this.puntuacionMediaPorUsuario = new HashMap<>();
        this.partidasJugadasPorUsuario = new HashMap<>();
        this.victoriasUsuario = new HashMap<>();
        this.estrategiaActual = "maxima"; // Estrategia por defecto
        
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
     * @param nombre Nombre del usuario
     * @param puntuacion Puntuación a agregar
     * @return true si se agregó correctamente, false en caso contrario
     */
    public boolean agregarPuntuacion(String nombre, int puntuacion) {
        // Verificar que se proporciona un usuario válido
        if (nombre == null || nombre.isEmpty()) {
            return false;
        }
        
        if (puntuacion < 0) {
            return false; // No se permiten puntuaciones negativas
        }
        
        // Agregar puntuación a la lista
        if (!puntuacionesPorUsuario.containsKey(nombre)) {
            puntuacionesPorUsuario.put(nombre, new ArrayList<>());
        }
        puntuacionesPorUsuario.get(nombre).add(puntuacion);
        
        // Actualizar puntuación máxima
        int maxActual = puntuacionMaximaPorUsuario.getOrDefault(nombre, 0);
        if (puntuacion > maxActual) {
            puntuacionMaximaPorUsuario.put(nombre, puntuacion);
        }
        
        // Recalcular puntuación media
        List<Integer> puntuaciones = puntuacionesPorUsuario.get(nombre);
        double suma = 0;
        for (Integer p : puntuaciones) {
            suma += p;
        }
        double media = puntuaciones.isEmpty() ? 0 : suma / puntuaciones.size();
        puntuacionMediaPorUsuario.put(nombre, media);
        
        return true;
    }
    
    /**
     * Actualiza las estadísticas de un usuario (partidas jugadas y victoria).
     * 
     * @param nombre Nombre del usuario
     * @param esVictoria true si el usuario ha ganado la partida
     * @return true si se actualizaron correctamente las estadísticas
     */
    public boolean actualizarEstadisticasUsuario(String nombre, boolean esVictoria) {
        // Verificar que se proporciona un usuario válido
        if (nombre == null || nombre.isEmpty()) {
            return false;
        }
        
        // Incrementar contador de partidas jugadas
        int partidasActuales = partidasJugadasPorUsuario.getOrDefault(nombre, 0);
        partidasJugadasPorUsuario.put(nombre, partidasActuales + 1);
        
        // Si es victoria, incrementar contador de victorias
        if (esVictoria) {
            int victoriasActuales = victoriasUsuario.getOrDefault(nombre, 0);
            victoriasUsuario.put(nombre, victoriasActuales + 1);
        }
        
        return true;
    }
    
    /**
     * Verifica si un usuario está en el ranking.
     * 
     * @param nombre Nombre del usuario
     * @return true si el usuario existe en el ranking, false en caso contrario
     */
    public boolean perteneceRanking(String nombre) {
        return puntuacionesPorUsuario.containsKey(nombre);
    }
    
    /**
     * Elimina una puntuación específica de un usuario.
     * 
     * @param nombre Nombre del usuario
     * @param puntuacion Puntuación a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public boolean eliminarPuntuacion(String nombre, int puntuacion) {
        if (!puntuacionesPorUsuario.containsKey(nombre)) {
            return false;
        }
        
        List<Integer> puntuaciones = puntuacionesPorUsuario.get(nombre);
        boolean removed = puntuaciones.remove(Integer.valueOf(puntuacion));
        
        if (removed) {
            // Recalcular puntuación máxima
            int max = 0;
            for (Integer p : puntuaciones) {
                if (p > max) {
                    max = p;
                }
            }
            puntuacionMaximaPorUsuario.put(nombre, max);
            
            // Recalcular puntuación media
            double suma = 0;
            for (Integer p : puntuaciones) {
                suma += p;
            }
            double media = puntuaciones.isEmpty() ? 0 : suma / puntuaciones.size();
            puntuacionMediaPorUsuario.put(nombre, media);
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Verifica si existe una puntuación específica para un usuario.
     * 
     * @param nombre Nombre del usuario
     * @param puntuacion Puntuación a verificar
     * @return true si existe la puntuación, false en caso contrario
     */
    public boolean existePuntuacion(String nombre, int puntuacion) {
        return puntuacionesPorUsuario.containsKey(nombre) && 
               puntuacionesPorUsuario.get(nombre).contains(puntuacion);
    }
    
    /**
     * Elimina un usuario del ranking.
     * 
     * @param nombre Nombre del usuario a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public boolean eliminarUsuario(String nombre) {
        if (!perteneceRanking(nombre)) {
            return false;
        }
        
        puntuacionesPorUsuario.remove(nombre);
        puntuacionMaximaPorUsuario.remove(nombre);
        puntuacionMediaPorUsuario.remove(nombre);
        partidasJugadasPorUsuario.remove(nombre);
        victoriasUsuario.remove(nombre);
        
        return true;
    }
    
    /**
     * Cambia la estrategia de ordenación del ranking.
     * 
     * @param criterio Criterio de ordenación
     */
    public void setEstrategia(String criterio) {
        this.estrategiaActual = criterio;
    }
    
    /**
     * Obtiene la estrategia actual de ordenación.
     * 
     * @return Nombre de la estrategia actual
     */
    public String getEstrategiaActual() {
        RankingOrderStrategy estrategia = RankingOrderStrategyFactory.createStrategy(estrategiaActual);
        return estrategia.getNombre();
    }
    
    /**
     * Obtiene el ranking de usuarios según la estrategia actual.
     * 
     * @return Lista ordenada de nombres de usuario
     */
    public List<String> getRanking() {
        RankingOrderStrategy estrategia = RankingOrderStrategyFactory.createStrategy(estrategiaActual);
        return estrategia.ordenarRanking(
            puntuacionesPorUsuario,
            puntuacionMaximaPorUsuario,
            puntuacionMediaPorUsuario,
            partidasJugadasPorUsuario,
            victoriasUsuario
        );
    }
    
    /**
     * Obtiene el ranking de usuarios con un criterio específico.
     * 
     * @param criterio Criterio de ordenación
     * @return Lista ordenada de nombres de usuario
     */
    public List<String> getRanking(String criterio) {
        RankingOrderStrategy estrategia = RankingOrderStrategyFactory.createStrategy(criterio);
        return estrategia.ordenarRanking(
            puntuacionesPorUsuario,
            puntuacionMaximaPorUsuario,
            puntuacionMediaPorUsuario,
            partidasJugadasPorUsuario,
            victoriasUsuario
        );
    }
    
    /**
     * Obtiene todas las puntuaciones de un usuario específico.
     * 
     * @param nombre Nombre del usuario
     * @return Lista de puntuaciones del usuario
     */
    public List<Integer> getPuntuacionesUsuario(String nombre) {
        return puntuacionesPorUsuario.getOrDefault(nombre, new ArrayList<>());
    }
    
    /**
     * Obtiene la puntuación máxima de un usuario específico.
     * 
     * @param nombre Nombre del usuario
     * @return Puntuación máxima del usuario
     */
    public int getPuntuacionMaxima(String nombre) {
        return puntuacionMaximaPorUsuario.getOrDefault(nombre, 0);
    }
    
    /**
     * Obtiene la puntuación media de un usuario específico.
     * 
     * @param nombre Nombre del usuario
     * @return Puntuación media del usuario
     */
    public double getPuntuacionMedia(String nombre) {
        return puntuacionMediaPorUsuario.getOrDefault(nombre, 0.0);
    }
    
    /**
     * Obtiene el número de partidas jugadas por un usuario específico.
     * 
     * @param nombre Nombre del usuario
     * @return Número de partidas jugadas
     */
    public int getPartidasJugadas(String nombre) {
        return partidasJugadasPorUsuario.getOrDefault(nombre, 0);
    }
    
    /**
     * Obtiene el número de victorias de un usuario específico.
     * 
     * @param nombre Nombre del usuario
     * @return Número de victorias
     */
    public int getVictorias(String nombre) {
        return victoriasUsuario.getOrDefault(nombre, 0);
    }
    
    /**
     * Obtiene la puntuación total acumulada de un usuario específico.
     * Suma todas las puntuaciones individuales registradas para el usuario.
     * 
     * @param nombre Nombre del usuario
     * @return Puntuación total acumulada
     */
    public int getPuntuacionTotal(String nombre) {
        List<Integer> puntuaciones = getPuntuacionesUsuario(nombre);
        int total = 0;
        for (Integer p : puntuaciones) {
            total += p;
        }
        return total;
    }
    
    /**
     * Obtiene todos los nombres de usuario registrados en el ranking.
     * 
     * @return Conjunto de nombres de usuario
     */
    public List<String> getUsuarios() {
        return new ArrayList<>(puntuacionesPorUsuario.keySet());
    }
    
    /**
     * Guarda los datos del ranking en un archivo serializado.
     */
    public void guardarDatos() {
        try {
            FileOutputStream fos = new FileOutputStream(RANKING_FILE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
            fos.close();
        } catch (IOException e) {
            System.err.println("Error al guardar el ranking: " + e.getMessage());
        }
    }
    
    /**
     * Carga los datos del ranking desde un archivo serializado.
     */
    private void cargarDatos() {
        File rankingFile = new File(RANKING_FILE);
        if (rankingFile.exists()) {
            try {
                FileInputStream fis = new FileInputStream(RANKING_FILE);
                ObjectInputStream ois = new ObjectInputStream(fis);
                ControladorRanking loaded = (ControladorRanking) ois.readObject();
                ois.close();
                fis.close();
                
                // Copiar datos del ranking cargado
                this.puntuacionesPorUsuario = loaded.puntuacionesPorUsuario;
                this.puntuacionMaximaPorUsuario = loaded.puntuacionMaximaPorUsuario;
                this.puntuacionMediaPorUsuario = loaded.puntuacionMediaPorUsuario;
                this.partidasJugadasPorUsuario = loaded.partidasJugadasPorUsuario;
                this.victoriasUsuario = loaded.victoriasUsuario;
                this.estrategiaActual = loaded.estrategiaActual;
                
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error al cargar el ranking: " + e.getMessage());
                // Inicializar con datos vacíos
                this.puntuacionesPorUsuario = new HashMap<>();
                this.puntuacionMaximaPorUsuario = new HashMap<>();
                this.puntuacionMediaPorUsuario = new HashMap<>();
                this.partidasJugadasPorUsuario = new HashMap<>();
                this.victoriasUsuario = new HashMap<>();
            }
        }
    }

    /**
     * Devuelve un mapa con las puntuaciones máximas por usuario.
     * 
     * @return Mapa de usuarios y sus puntuaciones máximas
     */
    public Map<String, Integer> getMapaPuntuacionesMaximas() {
        return new HashMap<>(puntuacionMaximaPorUsuario);
    }
    
    /**
     * Devuelve un mapa con las puntuaciones medias por usuario.
     * 
     * @return Mapa de usuarios y sus puntuaciones medias
     */
    public Map<String, Double> getMapaPuntuacionesMedias() {
        return new HashMap<>(puntuacionMediaPorUsuario);
    }
    
    /**
     * Devuelve un mapa con las partidas jugadas por usuario.
     * 
     * @return Mapa de usuarios y su número de partidas jugadas
     */
    public Map<String, Integer> getMapaPartidasJugadas() {
        return new HashMap<>(partidasJugadasPorUsuario);
    }
    
    /**
     * Devuelve un mapa con las victorias por usuario.
     * 
     * @return Mapa de usuarios y su número de victorias
     */
    public Map<String, Integer> getMapaVictorias() {
        return new HashMap<>(victoriasUsuario);
    }
}