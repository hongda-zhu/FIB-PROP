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

import scrabble.domain.controllers.subcontrollers.ControladorJuego;
import scrabble.domain.persistences.interfaces.RepositorioPartida;
import scrabble.excepciones.ExceptionPersistenciaFallida;

/**
 * Implementación de la interfaz {@link RepositorioPartida} con gestión completa de partidas guardadas.
 * 
 * Gestiona la persistencia de partidas (objetos {@link ControladorJuego}) utilizando serialización Java,
 * guardándolas y recuperándolas desde un archivo binario llamado {@code partidas.dat}.
 * Las partidas se almacenan en un mapa donde la clave es el ID de la partida (un entero)
 * y el valor es el objeto {@code ControladorJuego} correspondiente con todo su estado.
 * 
 * Funcionalidades principales:
 * - Persistencia completa del estado de partidas de Scrabble
 * - Generación automática de IDs únicos para nuevas partidas
 * - Operaciones CRUD completas (crear, leer, actualizar, eliminar)
 * - Listado y gestión de múltiples partidas guardadas
 * - Manejo robusto de errores con excepciones específicas
 * - Gestión automática de directorios de persistencia
 * - Serialización eficiente de estados complejos de juego
 * 
 * Esta implementación permite a los jugadores guardar y reanudar partidas en cualquier
 * momento, manteniendo la integridad completa del estado del juego incluyendo tablero,
 * jugadores, puntuaciones y configuración.
 * 
 * @version 2.0
 * @since 1.0
 */
public class RepositorioPartidaImpl implements RepositorioPartida {
    
    private static final String DIRECTORIO_PERSISTENCIA = "src/main/resources/persistencias";
    private static final String ARCHIVO_PARTIDAS = DIRECTORIO_PERSISTENCIA + "/partidas.dat";
    
    /**
     * Constructor per defecte per a {@code RepositorioPartidaImpl}.
     * Assegura que el directori de persistència ({@code DIRECTORIO_PERSISTENCIA}) existeix.
     * Si el directori no existeix, es crea.
     * 
     * @pre No hi ha precondicions específiques.
     * @post S'ha creat una instància de {@code RepositorioPartidaImpl} i
     *       s'ha assegurat l'existència del directori de persistència.
     */
    public RepositorioPartidaImpl() {
        // Asegurar que el directorio de persistencia existe
        File directorio = new File(DIRECTORIO_PERSISTENCIA);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }
    }
    
    /**
     * Guarda o actualitza una partida al repositori.
     * Carrega totes les partides existents, afegeix o reemplaça la partida donada
     * utilitzant el seu ID, i després desa el mapa actualitzat de partides.
     * 
     * @pre {@code id} és un identificador vàlid per a la partida.
     * @pre {@code partida} no ha de ser nul i representa l'estat del joc a guardar.
     * @param id L'identificador únic de la partida.
     * @param partida L'objecte {@link ControladorJuego} que representa l'estat de la partida.
     * @return {@code true} si la partida s'ha guardat (o actualitzat) correctament.
     * @throws ExceptionPersistenciaFallida Si es produeix un error durant la càrrega o el desat de les partides.
     * @post La partida especificada es desa o s'actualitza al fitxer de persistència.
     */
    @Override
    public boolean guardar(int id, ControladorJuego partida) throws ExceptionPersistenciaFallida {
        Map<Integer, ControladorJuego> mapaPartidas = cargarTodasLasPartidas();
        
        // Añadir o actualizar la partida en el mapa
        mapaPartidas.put(id, partida);
        
        // Guardar todo el mapa actualizado
        return guardarTodasLasPartidas(mapaPartidas);
    }
    
    /**
     * Carrega una partida específica des del repositori utilitzant el seu ID.
     * 
     * @pre {@code id} és l'identificador de la partida a carregar.
     * @param id L'identificador únic de la partida a carregar.
     * @return L'objecte {@link ControladorJuego} corresponent a la partida carregada.
     *         Retorna {@code null} si no es troba cap partida amb l'ID especificat.
     * @throws ExceptionPersistenciaFallida Si es produeix un error durant la càrrega de les partides.
     * @post Es retorna l'estat de la partida si es troba, o {@code null} altrament.
     */
    @Override
    public ControladorJuego cargar(int id) throws ExceptionPersistenciaFallida {
        Map<Integer, ControladorJuego> mapaPartidas = cargarTodasLasPartidas();
        return mapaPartidas.get(id);
    }
    
    /**
     * Elimina una partida del repositori utilitzant el seu ID.
     * 
     * @pre {@code id} és l'identificador de la partida a eliminar.
     * @param id L'identificador únic de la partida a eliminar.
     * @return {@code true} si la partida s'ha eliminat correctament. 
     *         Retorna {@code false} si no existia cap partida amb l'ID especificat.
     * @throws ExceptionPersistenciaFallida Si es produeix un error durant la càrrega o el desat de les partides.
     * @post La partida amb l'ID especificat s'elimina del fitxer de persistència, si existia.
     */
    @Override
    public boolean eliminar(int id) throws ExceptionPersistenciaFallida {
        Map<Integer, ControladorJuego> mapaPartidas = cargarTodasLasPartidas();
        
        if (!mapaPartidas.containsKey(id)) {
            return false;  // La partida no existe
        }
        
        mapaPartidas.remove(id);
        return guardarTodasLasPartidas(mapaPartidas);
    }
    
    /**
     * Retorna una llista amb els IDs de totes les partides guardades al repositori.
     * 
     * @pre No hi ha precondicions específiques.
     * @return Una {@code List<Integer>} que conté els identificadors de totes les partides emmagatzemades.
     *         Si no hi ha partides, la llista serà buida.
     * @throws ExceptionPersistenciaFallida Si es produeix un error durant la càrrega de les partides.
     * @post Es retorna una llista dels IDs de les partides.
     */
    @Override
    public List<Integer> listarTodas() throws ExceptionPersistenciaFallida {
        Map<Integer, ControladorJuego> mapaPartidas = cargarTodasLasPartidas();
        return new ArrayList<>(mapaPartidas.keySet());
    }
    
    /**
     * Genera un nou ID únic per a una nova partida.
     * L'ID generat és un més que l'ID màxim actualment existent al repositori.
     * Si no hi ha partides, comença des d'1 (0+1).
     * 
     * @pre No hi ha precondicions específiques.
     * @return Un nou identificador enter únic per a una partida.
     * @throws ExceptionPersistenciaFallida Si es produeix un error durant la càrrega de les partides.
     * @post Es retorna un ID que no està actualment en ús.
     */
    @Override
    public int generarNuevoId() throws ExceptionPersistenciaFallida {
        Map<Integer, ControladorJuego> mapaPartidas = cargarTodasLasPartidas();
        
        // Encontrar el ID máximo actual
        int maxId = 0;
        for (Integer id : mapaPartidas.keySet()) {
            if (id > maxId) {
                maxId = id;
            }
        }
        
        // Devolver el siguiente ID disponible
        return maxId + 1;
    }
    
    /**
     * Mètode privat auxiliar per carregar totes les partides des del fitxer de persistència.
     * Deserialitza el mapa de partides (ID -> {@link ControladorJuego}) des de {@code ARCHIVO_PARTIDAS}.
     * 
     * @pre El fitxer {@code ARCHIVO_PARTIDAS} ha d'existir o ser creat si no existeix.
     * @return Un {@code Map<Integer, ControladorJuego>} amb totes les partides carregades. 
     *         Retorna un mapa buit si el fitxer no existeix.
     * @throws ExceptionPersistenciaFallida Si el fitxer no conté un format de mapa vàlid 
     *                                      o si ocorre un error d'E/S o de deserialització.
     * @post Es retorna el mapa de partides llegit del fitxer o un mapa buit.
     */
    @SuppressWarnings("unchecked")
    private Map<Integer, ControladorJuego> cargarTodasLasPartidas() throws ExceptionPersistenciaFallida {
        File archivo = new File(ARCHIVO_PARTIDAS);
        
        if (!archivo.exists()) {
            return new HashMap<>();  // Devolver mapa vacío si no existe el archivo
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                return (Map<Integer, ControladorJuego>) obj;
            } else {
                throw new ExceptionPersistenciaFallida("El archivo no contiene un formato válido de partidas");
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new ExceptionPersistenciaFallida("Error al cargar las partidas: " + e.getMessage());
        }
    }
    
    /**
     * Mètode privat auxiliar per guardar el mapa complet de partides al fitxer de persistència.
     * Serialitza el mapa de partides (ID -> {@link ControladorJuego}) a {@code ARCHIVO_PARTIDAS}.
     * 
     * @pre {@code mapaPartidas} no ha de ser nul.
     * @param mapaPartidas El mapa (ID -> {@link ControladorJuego}) que es vol guardar.
     * @return {@code true} si l'operació de desat ha estat exitosa.
     * @throws ExceptionPersistenciaFallida Si ocorre un error d'E/S durant el desat.
     * @post El contingut de {@code mapaPartidas} se serialitza i s'escriu al fitxer {@code ARCHIVO_PARTIDAS}.
     */
    private boolean guardarTodasLasPartidas(Map<Integer, ControladorJuego> mapaPartidas) 
            throws ExceptionPersistenciaFallida {
        
        File archivo = new File(ARCHIVO_PARTIDAS);
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
            oos.writeObject(mapaPartidas);
            return true;
        } catch (IOException e) {
            throw new ExceptionPersistenciaFallida("Error al guardar las partidas: " + e.getMessage());
        }
    }
}