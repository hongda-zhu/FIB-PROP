package scrabble.domain.persistences.implementaciones;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Set;

import scrabble.domain.models.Ranking;
import scrabble.domain.models.rankingStrategy.PlayerRankingStats;
import scrabble.domain.persistences.interfaces.RepositorioRanking;

/**
 * Implementación del repositorio de ranking utilizando serialización Java.
 */
public class RepositorioRankingImpl implements RepositorioRanking {
    
    private static final String RANKING_FILE = "src/main/resources/persistencias/ranking.dat";
    
    @Override
    public boolean guardar(Ranking ranking) {
        try {
            // Asegurar que el directorio existe
            File rankingDir = new File(RANKING_FILE).getParentFile();
            if (rankingDir != null && !rankingDir.exists()) {
                rankingDir.mkdirs();
            }
            
            try (FileOutputStream fos = new FileOutputStream(RANKING_FILE);
                 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(ranking);
                return true;
            }
        } catch (IOException e) {
            System.err.println("Error al guardar el ranking: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public Ranking cargar() {
        File rankingFile = new File(RANKING_FILE);
        if (!rankingFile.exists()) {
            return new Ranking(); // Retornar un nuevo ranking si no existe el archivo
        }
        
        try (FileInputStream fis = new FileInputStream(RANKING_FILE);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            Ranking ranking = (Ranking) ois.readObject();
            return ranking != null ? ranking : new Ranking();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar el ranking: " + e.getMessage());
            return new Ranking(); // Retornar un nuevo ranking en caso de error
        }
    }
    
    @Override
    public boolean actualizarEstadisticasJugador(String nombre, PlayerRankingStats stats) {
        Ranking ranking = cargar();
        
        // La implementación depende de cómo están estructuradas las estadísticas en el Ranking
        // Asumiendo que hay un método para actualizar o añadir estadísticas
        // Este bloque debe adaptarse según la implementación de Ranking
        
        // Ejemplo simplificado (pseudocódigo)
        // ranking.actualizarEstadisticasJugador(nombre, stats);
        
        return guardar(ranking);
    }
    
    @Override
    public boolean eliminarJugador(String nombre) {
        Ranking ranking = cargar();
        boolean eliminado = ranking.eliminarUsuario(nombre);
        
        if (eliminado) {
            return guardar(ranking);
        }
        
        return false;
    }
    
    @Override
    public List<String> obtenerRankingOrdenado(String criterio) {
        Ranking ranking = cargar();
        return ranking.getRanking(criterio);
    }
    
    @Override
    public Set<String> obtenerTodosJugadores() {
        Ranking ranking = cargar();
        return ranking.getUsuarios();
    }
    
    @Override
    public int obtenerPuntuacionMaxima(String nombre) {
        Ranking ranking = cargar();
        return ranking.getPuntuacionMaxima(nombre);
    }
    
    @Override
    public double obtenerPuntuacionMedia(String nombre) {
        Ranking ranking = cargar();
        return ranking.getPuntuacionMedia(nombre);
    }
    
    @Override
    public int obtenerPartidasJugadas(String nombre) {
        Ranking ranking = cargar();
        return ranking.getPartidasJugadas(nombre);
    }
    
    @Override
    public int obtenerVictorias(String nombre) {
        Ranking ranking = cargar();
        return ranking.getVictorias(nombre);
    }
    
    @Override
    public int obtenerPuntuacionTotal(String nombre) {
        Ranking ranking = cargar();
        return ranking.getPuntuacionTotal(nombre);
    }
    
    @Override
    public boolean existeJugador(String nombre) {
        Ranking ranking = cargar();
        return ranking.perteneceRanking(nombre);
    }
}