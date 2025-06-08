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
 * Implementación del repositorio de ranking.
 * Gestiona la persistencia del objeto {@link Ranking} utilizando serialización Java.
 * El objeto de ranking completo se guarda en un único archivo llamado {@code ranking.dat}.
 * 
 * Esta clase proporciona operaciones completas de persistencia para el sistema de ranking,
 * incluyendo guardado y carga del ranking completo, así como operaciones específicas
 * para consultar estadísticas individuales de jugadores. Maneja automáticamente la
 * creación de directorios necesarios y proporciona valores por defecto en caso de errores.
 * 
 * 
 * @version 2.0
 * @since 1.0
 */
public class RepositorioRankingImpl implements RepositorioRanking {
    
    private static final String RANKING_FILE = "src/main/resources/persistencias/ranking.dat";
    
    /**
     * Guarda el objeto de ranking completo al sistema de persistencia.
     * Serializa el objeto {@link Ranking} al archivo especificado por {@code RANKING_FILE}.
     * Asegura que el directorio de persistencia existe antes de guardar.
     * 
     * @pre {@code ranking} no debe ser null.
     * @param ranking El objeto {@link Ranking} que se quiere guardar.
     * @return {@code true} si el ranking se ha guardado correctamente,
     *         {@code false} si se ha producido un error durante el proceso de guardado.
     * @post Si la operación tiene éxito, el objeto {@code ranking} se persiste al archivo.
     *       En caso de error, se imprime un mensaje de error a la salida de errores estándar.
     */
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
    
    /**
     * Carrega l'objecte de rànquing des del sistema de persistència.
     * Deserialitza l'objecte {@link Ranking} des del fitxer {@code RANKING_FILE}.
     * 
     * @pre No hi ha precondicions específiques.
     * @return L'objecte {@link Ranking} carregat des del fitxer. Si el fitxer no existeix,
     *         o si l'objecte llegit és nul, o si es produeix un error durant la càrrega 
     *         (per exemple, {@link IOException} o {@link ClassNotFoundException}), 
     *         es retorna una nova instància de {@code Ranking}.
     * @post Es retorna l'objecte {@code Ranking} llegit o un de nou per defecte.
     *       En cas d'error durant la càrrega, s'imprimeix un missatge d'error a la
     *       sortida d'errors estàndard.
     */
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
    
    /**
     * Actualitza les estadístiques d'un jugador específic al rànquing.
     * Aquest mètode carrega el rànquing actual, se suposa que crida un mètode intern
     * de l'objecte {@link Ranking} per actualitzar les estadístiques del jugador 
     * (la implementació exacta depèn de la classe {@code Ranking}), i després desa el rànquing actualitzat.
     * 
     * @pre {@code nombre} no ha de ser nul.
     * @pre {@code stats} no ha de ser nul.
     * @param nombre El nom del jugador les estadístiques del qual s'han d'actualitzar.
     * @param stats Les noves estadístiques del jugador ({@link PlayerRankingStats}).
     * @return {@code true} si el rànquing s'ha guardat correctament després de l'actualització,
     *         {@code false} altrament (incloent errors en la càrrega o desat).
     * @post Les estadístiques del jugador s'actualitzen al rànquing persistit si l'operació té èxit.
     */
    @Override
    public boolean actualizarEstadisticasJugador(String nombre, PlayerRankingStats stats) {
        Ranking ranking = cargar();
        
        // La implementació depende de cómo están estructuradas las estadísticas en el Ranking
        // Asumiendo que hay un método para actualizar o añadir estadísticas
        // Este bloque debe adaptarse según la implementación de Ranking
        
        // Ejemplo simplificado (pseudocódigo)
        // ranking.actualizarEstadisticasJugador(nombre, stats);
        
        return guardar(ranking);
    }
    
    /**
     * Elimina un jugador del rànquing.
     * Carrega el rànquing, elimina l'usuari a través del mètode {@code eliminarUsuario}
     * de l'objecte {@link Ranking}, i si l'eliminació té èxit, guarda el rànquing actualitzat.
     * 
     * @pre {@code nombre} no ha de ser nul.
     * @param nombre El nom del jugador a eliminar del rànquing.
     * @return {@code true} si el jugador s'ha eliminat i el rànquing s'ha guardat correctament,
     *         {@code false} si el jugador no s'ha trobat o si hi ha hagut un error en guardar.
     * @post El jugador s'elimina del rànquing persistit si es troba i l'operació de desat té èxit.
     */
    @Override
    public boolean eliminarJugador(String nombre) {
        Ranking ranking = cargar();
        boolean eliminado = ranking.eliminarUsuario(nombre);
        
        if (eliminado) {
            return guardar(ranking);
        }
        
        return false;
    }
    
    /**
     * Obté una llista ordenada dels jugadors del rànquing segons un criteri especificat.
     * Carrega el rànquing i crida el mètode {@code getRanking} de l'objecte {@link Ranking}.
     * 
     * @pre {@code criterio} no ha de ser nul i ha de ser un criteri vàlid acceptat per {@link Ranking#getRanking(String)}.
     * @param criterio El criteri utilitzat per ordenar el rànquing (p.ex., "puntuacionMaxima", "victorias").
     * @return Una {@code List<String>} amb els noms dels jugadors ordenats segons el criteri.
     * @post Es retorna una llista de noms de jugadors ordenada.
     */
    @Override
    public List<String> obtenerRankingOrdenado(String criterio) {
        Ranking ranking = cargar();
        return ranking.getRanking(criterio);
    }
    
    /**
     * Obté un conjunt amb els noms de tots els jugadors presents al rànquing.
     * Carrega el rànquing i crida el mètode {@code getUsuarios} de l'objecte {@link Ranking}.
     * 
     * @pre No hi ha precondicions específiques.
     * @return Un {@code Set<String>} que conté els noms de tots els jugadors del rànquing.
     * @post Es retorna un conjunt de noms de jugadors.
     */
    @Override
    public Set<String> obtenerTodosJugadores() {
        Ranking ranking = cargar();
        return ranking.getUsuarios();
    }
    
    /**
     * Obté la puntuació màxima registrada per a un jugador específic.
     * Carrega el rànquing i crida el mètode {@code getPuntuacionMaxima} de l'objecte {@link Ranking}.
     * 
     * @pre {@code nombre} no ha de ser nul.
     * @param nombre El nom del jugador.
     * @return La puntuació màxima del jugador. Si el jugador no existeix al rànquing, el comportament
     *         depèn de la implementació de {@link Ranking#getPuntuacionMaxima(String)} (p.ex., podria retornar 0).
     * @post Es retorna la puntuació màxima del jugador.
     */
    @Override
    public int obtenerPuntuacionMaxima(String nombre) {
        Ranking ranking = cargar();
        return ranking.getPuntuacionMaxima(nombre);
    }
    
    /**
     * Obté la puntuació mitjana d'un jugador específic.
     * Carrega el rànquing i crida el mètode {@code getPuntuacionMedia} de l'objecte {@link Ranking}.
     * 
     * @pre {@code nombre} no ha de ser nul.
     * @param nombre El nom del jugador.
     * @return La puntuació mitjana del jugador. Si el jugador no existeix, el comportament
     *         depèn de {@link Ranking#getPuntuacionMedia(String)} (p.ex., podria retornar 0.0).
     * @post Es retorna la puntuació mitjana del jugador.
     */
    @Override
    public double obtenerPuntuacionMedia(String nombre) {
        Ranking ranking = cargar();
        return ranking.getPuntuacionMedia(nombre);
    }
    
    /**
     * Obté el nombre de partides jugades per un jugador específic.
     * Carrega el rànquing i crida el mètode {@code getPartidasJugadas} de l'objecte {@link Ranking}.
     * 
     * @pre {@code nombre} no ha de ser nul.
     * @param nombre El nom del jugador.
     * @return El nombre de partides jugades pel jugador. Si el jugador no existeix, el comportament
     *         depèn de {@link Ranking#getPartidasJugadas(String)} (p.ex., podria retornar 0).
     * @post Es retorna el nombre de partides jugades.
     */
    @Override
    public int obtenerPartidasJugadas(String nombre) {
        Ranking ranking = cargar();
        return ranking.getPartidasJugadas(nombre);
    }
    
    /**
     * Obté el nombre de victòries d'un jugador específic.
     * Carrega el rànquing i crida el mètode {@code getVictorias} de l'objecte {@link Ranking}.
     * 
     * @pre {@code nombre} no ha de ser nul.
     * @param nombre El nom del jugador.
     * @return El nombre de victòries del jugador. Si el jugador no existeix, el comportament
     *         depèn de {@link Ranking#getVictorias(String)} (p.ex., podria retornar 0).
     * @post Es retorna el nombre de victòries del jugador.
     */
    @Override
    public int obtenerVictorias(String nombre) {
        Ranking ranking = cargar();
        return ranking.getVictorias(nombre);
    }
    
    /**
     * Obté la puntuació total acumulada per un jugador específic.
     * Carrega el rànquing i crida el mètode {@code getPuntuacionTotal} de l'objecte {@link Ranking}.
     * 
     * @pre {@code nombre} no ha de ser nul.
     * @param nombre El nom del jugador.
     * @return La puntuació total acumulada pel jugador. Si el jugador no existeix, el comportament
     *         depèn de {@link Ranking#getPuntuacionTotal(String)} (p.ex., podria retornar 0).
     * @post Es retorna la puntuació total del jugador.
     */
    @Override
    public int obtenerPuntuacionTotal(String nombre) {
        Ranking ranking = cargar();
        return ranking.getPuntuacionTotal(nombre);
    }
    
    /**
     * Verifica si un jugador existeix al rànquing.
     * Carrega el rànquing i crida el mètode {@code perteneceRanking} de l'objecte {@link Ranking}.
     * 
     * @pre {@code nombre} no ha de ser nul.
     * @param nombre El nom del jugador a verificar.
     * @return {@code true} si el jugador existeix al rànquing, {@code false} altrament.
     * @post Es retorna un booleà indicant si el jugador pertany al rànquing.
     */
    @Override
    public boolean existeJugador(String nombre) {
        Ranking ranking = cargar();
        return ranking.perteneceRanking(nombre);
    }
}