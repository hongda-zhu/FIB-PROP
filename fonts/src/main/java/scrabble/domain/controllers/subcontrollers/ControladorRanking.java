package scrabble.domain.controllers.subcontrollers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import scrabble.domain.models.rankingStrategy.RankingOrderStrategy;
import scrabble.domain.models.rankingStrategy.RankingOrderStrategyFactory;

/**
 * Controlador para la gestión del ranking de jugadores.
 * Implementa el patrón Singleton para garantizar una única instancia.
 */
public class ControladorRanking {
    private static ControladorRanking instance;
    
    // Estructura de datos para almacenar las puntuaciones de los usuarios
    private Map<String, List<Integer>> puntuacionesPorUsuario;
    private Map<String, Integer> puntuacionMaximaPorUsuario;
    private Map<String, Double> puntuacionMediaPorUsuario;
    private Map<String, Integer> partidasJugadasPorUsuario;
    private Map<String, Integer> victoriasUsuario;
    
    // Estrategia actual de ordenación
    private String estrategiaActual;
    
    private static final String RANKING_FILE = "ranking.json";
    private static final Gson gson = new Gson();
    
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
        puntuacionesPorUsuario.computeIfAbsent(nombre, k -> new ArrayList<>()).add(puntuacion);
        
        // Actualizar puntuación máxima
        puntuacionMaximaPorUsuario.put(nombre, 
            Math.max(puntuacionMaximaPorUsuario.getOrDefault(nombre, 0), puntuacion));
        
        // Recalcular puntuación media
        List<Integer> puntuaciones = puntuacionesPorUsuario.get(nombre);
        double media = puntuaciones.stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0.0);
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
        partidasJugadasPorUsuario.put(nombre, 
            partidasJugadasPorUsuario.getOrDefault(nombre, 0) + 1);
        
        // Si es victoria, incrementar contador de victorias
        if (esVictoria) {
            victoriasUsuario.put(nombre, 
                victoriasUsuario.getOrDefault(nombre, 0) + 1);
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
            puntuacionMaximaPorUsuario.put(nombre, 
                puntuaciones.isEmpty() ? 0 : puntuaciones.stream().max(Integer::compare).orElse(0));
            
            // Recalcular puntuación media
            double media = puntuaciones.isEmpty() ? 0.0 : puntuaciones.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
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
     * Obtiene todos los nombres de usuario registrados en el ranking.
     * 
     * @return Conjunto de nombres de usuario
     */
    public List<String> getUsuarios() {
        return new ArrayList<>(puntuacionesPorUsuario.keySet());
    }
    
    /**
     * Guarda los datos del ranking en un archivo JSON.
     */
    public void guardarDatos() {
        try (FileWriter file = new FileWriter(RANKING_FILE)) {
            JsonObject rankingData = new JsonObject();
            
            // Guardar estrategia actual
            rankingData.addProperty("estrategiaActual", estrategiaActual);
            
            // Guardar puntuaciones por usuario
            JsonObject puntuacionesJson = new JsonObject();
            for (Map.Entry<String, List<Integer>> entry : puntuacionesPorUsuario.entrySet()) {
                JsonArray puntuaciones = new JsonArray();
                for (Integer valor : entry.getValue()) {
                    puntuaciones.add(valor);
                }
                puntuacionesJson.add(entry.getKey(), puntuaciones);
            }
            rankingData.add("puntuacionesPorUsuario", puntuacionesJson);
            
            // Guardar partidas jugadas
            JsonObject partidasJson = new JsonObject();
            for (Map.Entry<String, Integer> entry : partidasJugadasPorUsuario.entrySet()) {
                partidasJson.addProperty(entry.getKey(), entry.getValue());
            }
            rankingData.add("partidasJugadasPorUsuario", partidasJson);
            
            // Guardar victorias
            JsonObject victoriasJson = new JsonObject();
            for (Map.Entry<String, Integer> entry : victoriasUsuario.entrySet()) {
                victoriasJson.addProperty(entry.getKey(), entry.getValue());
            }
            rankingData.add("victoriasUsuario", victoriasJson);
            
            file.write(gson.toJson(rankingData));
            file.flush();
            
        } catch (IOException e) {
            System.err.println("Error al guardar el ranking: " + e.getMessage());
        }
    }
    
    /**
     * Carga los datos del ranking desde un archivo JSON.
     */
    private void cargarDatos() {
        File rankingFile = new File(RANKING_FILE);
        if (rankingFile.exists()) {
            try (FileReader reader = new FileReader(rankingFile)) {
                JsonObject rankingData = JsonParser.parseReader(reader).getAsJsonObject();
                
                // Cargar estrategia actual
                if (rankingData.has("estrategiaActual")) {
                    estrategiaActual = rankingData.get("estrategiaActual").getAsString();
                }
                
                // Cargar puntuaciones por usuario
                if (rankingData.has("puntuacionesPorUsuario")) {
                    JsonObject puntuacionesJson = rankingData.getAsJsonObject("puntuacionesPorUsuario");
                    for (Map.Entry<String, JsonElement> entry : puntuacionesJson.entrySet()) {
                        String nombre = entry.getKey();
                        JsonArray puntuacionesArray = entry.getValue().getAsJsonArray();
                        
                        List<Integer> puntuaciones = new ArrayList<>();
                        for (JsonElement puntuacion : puntuacionesArray) {
                            puntuaciones.add(puntuacion.getAsInt());
                        }
                        
                        puntuacionesPorUsuario.put(nombre, puntuaciones);
                        
                        // Calcular puntuación máxima
                        int max = puntuaciones.isEmpty() ? 0 : 
                            puntuaciones.stream().max(Integer::compare).orElse(0);
                        puntuacionMaximaPorUsuario.put(nombre, max);
                        
                        // Calcular puntuación media
                        double media = puntuaciones.isEmpty() ? 0.0 : 
                            puntuaciones.stream().mapToInt(Integer::intValue).average().getAsDouble();
                        puntuacionMediaPorUsuario.put(nombre, media);
                    }
                }
                
                // Cargar partidas jugadas
                if (rankingData.has("partidasJugadasPorUsuario")) {
                    JsonObject partidasJson = rankingData.getAsJsonObject("partidasJugadasPorUsuario");
                    for (Map.Entry<String, JsonElement> entry : partidasJson.entrySet()) {
                        String nombre = entry.getKey();
                        partidasJugadasPorUsuario.put(nombre, entry.getValue().getAsInt());
                    }
                }
                
                // Cargar victorias
                if (rankingData.has("victoriasUsuario")) {
                    JsonObject victoriasJson = rankingData.getAsJsonObject("victoriasUsuario");
                    for (Map.Entry<String, JsonElement> entry : victoriasJson.entrySet()) {
                        String nombre = entry.getKey();
                        victoriasUsuario.put(nombre, entry.getValue().getAsInt());
                    }
                }
                
            } catch (IOException e) {
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