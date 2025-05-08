package scrabble.domain.persistences.implementaciones;

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
import java.util.stream.Collectors;

import scrabble.domain.models.Jugador;
import scrabble.domain.persistences.interfaces.RepositorioJugador;

/**
 * Implementación del repositorio de jugadores utilizando serialización Java.
 */
public class RepositorioJugadorImpl implements RepositorioJugador {
    
    private static final String JUGADORES_FILE = "src/main/resources/persistencias/jugadores.dat";
    
    /**
     * Constructor que asegura que el directorio de persistencia existe.
     */
    public RepositorioJugadorImpl() {
        // Asegurar que el directorio existe
        File directory = new File("src/main/resources/persistencias");
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
    
    @Override
    public boolean guardarTodos(Map<String, Jugador> jugadores) {
        try {
            // Asegurar que el directorio existe
            File jugadoresDir = new File(JUGADORES_FILE).getParentFile();
            if (jugadoresDir != null && !jugadoresDir.exists()) {
                jugadoresDir.mkdirs();
            }
            
            try (FileOutputStream fos = new FileOutputStream(JUGADORES_FILE);
                 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(jugadores);
                return true;
            }
        } catch (IOException e) {
            System.err.println("Error al guardar los jugadores: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Jugador> cargarTodos() {
        File jugadoresFile = new File(JUGADORES_FILE);
        if (!jugadoresFile.exists()) {
            return new HashMap<>(); // Devolver mapa vacío si no hay archivo
        }
        
        try (FileInputStream fis = new FileInputStream(JUGADORES_FILE);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (Map<String, Jugador>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar los jugadores: " + e.getMessage());
            return new HashMap<>(); // Devolver mapa vacío en caso de error
        }
    }
    
    @Override
    public Jugador buscarPorNombre(String nombre) {
        Map<String, Jugador> jugadores = cargarTodos();
        return jugadores.get(nombre);
    }
    
    @Override
    public List<String> obtenerNombresJugadoresHumanos() {
        Map<String, Jugador> jugadores = cargarTodos();
        return jugadores.entrySet().stream()
                .filter(entry -> !entry.getValue().esIA())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> obtenerNombresJugadoresIA() {
        Map<String, Jugador> jugadores = cargarTodos();
        return jugadores.entrySet().stream()
                .filter(entry -> entry.getValue().esIA())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> obtenerNombresTodosJugadores() {
        return new ArrayList<>(cargarTodos().keySet());
    }
}