package scrabble.domain.persistences.implementaciones;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import scrabble.domain.models.Configuracion;
import scrabble.domain.persistences.interfaces.RepositorioConfiguracion;

/**
 * Implementació concreta del repositori de configuració.
 * Gestiona la persistència de les dades de configuració utilitzant serialització Java.
 * Les dades de configuració es guarden en un fitxer anomenat {@code configuracion.dat}.
 */
public class RepositorioConfiguracionImpl implements RepositorioConfiguracion {
    
    private static final String CONFIG_FILE = "src/main/resources/persistencias/configuracion.dat";
    
    /**
     * Constructor per a la classe {@code RepositorioConfiguracionImpl}.
     * Assegura que el directori on es guardarà el fitxer de configuració existeix.
     * Si el directori no existeix, es crea.
     * 
     * @pre No hi ha precondicions específiques.
     * @post S'ha creat una instància de {@code RepositorioConfiguracionImpl} i
     *       s'ha assegurat l'existència del directori de persistència.
     */
    public RepositorioConfiguracionImpl() {
        // Ensure the directory exists
        File configDir = new File(CONFIG_FILE).getParentFile();
        if (configDir != null && !configDir.exists()) {
            configDir.mkdirs();
        }
    }
    
    /**
     * Guarda l'objecte de configuració donat al sistema de persistència.
     * Utilitza la serialització Java per escriure l'objecte {@link Configuracion}
     * al fitxer especificat per {@code CONFIG_FILE}.
     * 
     * @pre {@code configuracion} no ha de ser nul.
     * @param configuracion L'objecte {@link Configuracion} que es vol guardar.
     * @return {@code true} si la configuració s'ha guardat correctament, 
     *         {@code false} si s'ha produït un error durant el procés de guardat.
     * @post Si l'operació té èxit, l'objecte {@code configuracion} es persisteix al fitxer.
     *       En cas d'error, s'imprimeix un missatge d'error a la sortida d'errors estàndard.
     */
    @Override
    public boolean guardar(Configuracion configuracion) {
        try {
            try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE);
                 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(configuracion);
                return true;
            }
        } catch (IOException e) {
            System.err.println("Error al guardar la configuració: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Carrega l'objecte de configuració des del sistema de persistència.
     * Llegeix l'objecte {@link Configuracion} serialitzat des del fitxer 
     * especificat per {@code CONFIG_FILE}.
     * 
     * @pre No hi ha precondicions específiques.
     * @return L'objecte {@link Configuracion} carregat des del fitxer. Si el fitxer
     *         no existeix o es produeix un error durant la càrrega (per exemple,
     *         {@link IOException} o {@link ClassNotFoundException}), es retorna 
     *         una nova instància de {@code Configuracion} amb valors per defecte.
     * @post Es retorna l'objecte {@code Configuracion} llegit o un de nou per defecte.
     *       En cas d'error durant la càrrega, s'imprimeix un missatge d'error a la
     *       sortida d'errors estàndard.
     */
    @Override
    public Configuracion cargar() {
        File configFile = new File(CONFIG_FILE);
        if (!configFile.exists()) {
            return new Configuracion(); // Retorna configuración por defecto
        }
        
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (Configuracion) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar la configuración: " + e.getMessage());
            return new Configuracion(); // Retorna configuración por defecto en caso de error
        }
    }
} 