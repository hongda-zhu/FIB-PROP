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
 * Implementación concreta del repositorio de configuración del sistema.
 * 
 * Gestiona la persistencia completa de los datos de configuración de la aplicación
 * utilizando serialización Java. Los datos incluyen configuraciones de tema visual,
 * audio, diccionario por defecto y tamaño del tablero. Los datos se almacenan en
 * un archivo binario llamado {@code configuracion.dat}.
 * 
 * Funcionalidades principales:
 * - Persistencia automática de configuraciones del sistema
 * - Creación automática de directorios de persistencia
 * - Manejo robusto de errores con valores por defecto
 * - Serialización/deserialización eficiente de objetos Configuracion
 * - Gestión transparente del ciclo de vida de archivos
 * 
 * Esta implementación sigue el patrón Repository y proporciona una interfaz
 * limpia para la persistencia de configuraciones, ocultando los detalles de
 * implementación del almacenamiento físico.
 * 
 * @version 2.0
 * @since 1.0
 */
public class RepositorioConfiguracionImpl implements RepositorioConfiguracion {
    
    private static final String CONFIG_FILE = "src/main/resources/persistencias/configuracion.dat";
    
    /**
     * Constructor para la clase {@code RepositorioConfiguracionImpl}.
     * Asegura que el directorio donde se guardará el archivo de configuración existe.
     * Si el directorio no existe, se crea automáticamente.
     * 
     * @pre No hay precondiciones específicas.
     * @post Se ha creado una instancia de {@code RepositorioConfiguracionImpl} y
     *       se ha asegurado la existencia del directorio de persistencia.
     */
    public RepositorioConfiguracionImpl() {
        // Ensure the directory exists
        File configDir = new File(CONFIG_FILE).getParentFile();
        if (configDir != null && !configDir.exists()) {
            configDir.mkdirs();
        }
    }
    
    /**
     * Guarda el objeto de configuración dado al sistema de persistencia.
     * Utiliza la serialización Java para escribir el objeto {@link Configuracion}
     * al archivo especificado por {@code CONFIG_FILE}.
     * 
     * @pre {@code configuracion} no debe ser null.
     * @param configuracion El objeto {@link Configuracion} que se quiere guardar.
     * @return {@code true} si la configuración se ha guardado correctamente, 
     *         {@code false} si se ha producido un error durante el proceso de guardado.
     * @post Si la operación tiene éxito, el objeto {@code configuracion} se persiste al archivo.
     *       En caso de error, se imprime un mensaje de error a la salida de errores estándar.
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
     * Carga el objeto de configuración desde el sistema de persistencia.
     * Lee el objeto {@link Configuracion} serializado desde el archivo 
     * especificado por {@code CONFIG_FILE}.
     * 
     * @pre No hay precondiciones específicas.
     * @return El objeto {@link Configuracion} cargado desde el archivo. Si el archivo
     *         no existe o se produce un error durante la carga (por ejemplo,
     *         {@link IOException} o {@link ClassNotFoundException}), se retorna 
     *         una nueva instancia de {@code Configuracion} con valores por defecto.
     * @post Se retorna el objeto {@code Configuracion} leído o uno nuevo por defecto.
     *       En caso de error durante la carga, se imprime un mensaje de error a la
     *       salida de errores estándar.
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