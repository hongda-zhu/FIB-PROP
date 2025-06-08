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
 * Implementación del repositorio de jugadores con gestión completa de usuarios humanos e IA.
 * 
 * Gestiona la persistencia de las datos de jugadores utilizando serialización Java.
 * Un mapa completo de jugadores (nombre -> {@link Jugador}) se guarda en un único archivo
 * llamado {@code jugadores.dat}. Proporciona operaciones especializadas para diferentes
 * tipos de jugadores y consultas optimizadas.
 * 
 * Funcionalidades principales:
 * - Persistencia unificada de jugadores humanos e IA
 * - Operaciones de búsqueda y filtrado por tipo de jugador
 * - Gestión automática de directorios de persistencia
 * - Consultas especializadas para diferentes categorías de usuarios
 * - Manejo robusto de errores con mapas vacíos por defecto
 * - Operaciones de carga/guardado optimizadas para grandes volúmenes
 * 
 * Esta implementación centraliza toda la información de jugadores en un solo
 * archivo para garantizar consistencia y facilitar operaciones de backup.
 * 
 * @version 2.0
 * @since 1.0
 */
public class RepositorioJugadorImpl implements RepositorioJugador {
    
    private static final String JUGADORES_FILE = "src/main/resources/persistencias/jugadores.dat";
    
    /**
     * Constructor per a la classe {@code RepositorioJugadorImpl}.
     * Assegura que el directori de persistència per als jugadors existeix.
     * Si el directori no existeix, es crea.
     * 
     * @pre No hi ha precondicions específiques.
     * @post S'ha creat una instància de {@code RepositorioJugadorImpl} i
     *       s'ha assegurat l'existència del directori de persistència.
     */
    public RepositorioJugadorImpl() {
        // Asegurar que el directorio existe
        File directory = new File("src/main/resources/persistencias");
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
    
    /**
     * Guarda un mapa de jugadors al sistema de persistència.
     * Serialitza l'objecte {@code Map<String, Jugador>} complet al fitxer especificat
     * per {@code JUGADORES_FILE}.
     * 
     * @pre {@code jugadores} no ha de ser nul.
     * @param jugadores Un mapa on les claus són els noms dels jugadors i els valors són
     *                  els objectes {@link Jugador} corresponents.
     * @return {@code true} si tots els jugadors s'han guardat correctament,
     *         {@code false} si s'ha produït un error durant el procés de guardat.
     * @post Si l'operació té èxit, el mapa de jugadors es persisteix al fitxer.
     *       En cas d'error, s'imprimeix un missatge d'error a la sortida d'errors estàndard.
     */
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
    
    /**
     * Carrega tots els jugadors des del sistema de persistència.
     * Deserialitza el mapa de jugadors des del fitxer {@code JUGADORES_FILE}.
     * 
     * @pre No hi ha precondicions específiques.
     * @return Un {@code Map<String, Jugador>} amb tots els jugadors carregats. Si el fitxer
     *         no existeix o es produeix un error durant la càrrega (per exemple,
     *         {@link IOException} o {@link ClassNotFoundException}), es retorna un mapa buit.
     * @post Es retorna el mapa de jugadors llegit o un mapa buit en cas d'error o inexistència.
     *       En cas d'error durant la càrrega, s'imprimeix un missatge d'error a la
     *       sortida d'errors estàndard.
     */
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
    
    /**
     * Busca un jugador pel seu nom dins dels jugadors carregats.
     * 
     * @pre {@code nombre} no ha de ser nul.
     * @param nombre El nom del jugador a buscar.
     * @return L'objecte {@link Jugador} corresponent al nom proporcionat, o {@code null}
     *         si no es troba cap jugador amb aquest nom.
     * @post Es retorna el jugador trobat o {@code null}.
     */
    @Override
    public Jugador buscarPorNombre(String nombre) {
        Map<String, Jugador> jugadores = cargarTodos();
        return jugadores.get(nombre);
    }
    
    /**
     * Obté una llista amb els noms de tots els jugadors humans registrats.
     * Filtra els jugadors carregats per identificar aquells que no són IA.
     * 
     * @pre No hi ha precondicions específiques.
     * @return Una {@code List<String>} que conté els noms dels jugadors humans.
     *         Si no hi ha jugadors humans, la llista serà buida.
     * @post Es retorna una llista de noms de jugadors humans.
     */
    @Override
    public List<String> obtenerNombresJugadoresHumanos() {
        Map<String, Jugador> jugadores = cargarTodos();
        return jugadores.entrySet().stream()
                .filter(entry -> !entry.getValue().esIA())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    /**
     * Obté una llista amb els noms de tots els jugadors IA registrats.
     * Filtra els jugadors carregats per identificar aquells que són IA.
     * 
     * @pre No hi ha precondicions específiques.
     * @return Una {@code List<String>} que conté els noms dels jugadors IA.
     *         Si no hi ha jugadors IA, la llista serà buida.
     * @post Es retorna una llista de noms de jugadors IA.
     */
    @Override
    public List<String> obtenerNombresJugadoresIA() {
        Map<String, Jugador> jugadores = cargarTodos();
        return jugadores.entrySet().stream()
                .filter(entry -> entry.getValue().esIA())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    /**
     * Obté una llista amb els noms de tots els jugadors registrats (humans i IA).
     * 
     * @pre No hi ha precondicions específiques.
     * @return Una {@code List<String>} que conté els noms de tots els jugadors.
     *         Si no hi ha jugadors, la llista serà buida.
     * @post Es retorna una llista de noms de tots els jugadors.
     */
    @Override
    public List<String> obtenerNombresTodosJugadores() {
        return new ArrayList<>(cargarTodos().keySet());
    }
}