package scrabble.excepciones;

/**
 * Excepción que se lanza cuando se intenta añadir una palabra que ya existe en el diccionario.
 * 
 * Esta excepción se produce durante operaciones de gestión de diccionarios cuando
 * se intenta agregar una palabra que ya está presente en la estructura DAWG.
 * Forma parte del sistema de validación de integridad de diccionarios.
 * 
 * Casos de uso típicos:
 * - Adición de palabras duplicadas durante la creación de diccionarios
 * - Importación de archivos con palabras repetidas
 * - Operaciones de modificación que intentan duplicar entradas
 * - Validación de unicidad en estructuras de datos
 * 
 * @version 2.0
 * @since 1.0
 */
public class ExceptionPalabraExist extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor por defecto que establece un mensaje predeterminado.
     * 
     * @post Se crea una instancia de la excepción con el mensaje "La palabra ya existe en el diccionario".
     */
    public ExceptionPalabraExist() {
        super("La palabra ya existe en el diccionario");
    }

    /**
     * Constructor que permite establecer un mensaje personalizado.
     * 
     * @param message Mensaje de error personalizado que describe el problema específico.
     * @post Se crea una instancia de la excepción con el mensaje proporcionado.
     */
    public ExceptionPalabraExist(String message) {
        super(message);
    }
} 