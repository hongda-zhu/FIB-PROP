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
 * Implementación concreta del repositorio de configuración.
 * Gestiona la persistencia de los datos de configuración utilizando serialización.
 */
public class RepositorioConfiguracionImpl implements RepositorioConfiguracion {
    
    private static final String CONFIG_FILE = "src/main/resources/persistencias/configuracion.dat";
    
    /**
     * Constructor que asegura que el directorio de persistencia existe.
     */
    public RepositorioConfiguracionImpl() {
        // Ensure the directory exists
        File configDir = new File(CONFIG_FILE).getParentFile();
        if (configDir != null && !configDir.exists()) {
            configDir.mkdirs();
        }
    }
    
    @Override
    public boolean guardar(Configuracion configuracion) {
        try {
            try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE);
                 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(configuracion);
                return true;
            }
        } catch (IOException e) {
            System.err.println("Error al guardar la configuración: " + e.getMessage());
            return false;
        }
    }
    
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