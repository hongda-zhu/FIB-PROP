package scrabble.excepciones;

/**
 * Excepción que se lanza cuando se intenta eliminar una palabra que no existe en el diccionario.
 * 
 * Esta excepción se produce cuando se realizan operaciones sobre una palabra
 * que no está presente en la estructura DAWG del diccionario. Es fundamental
 * para la validación de operaciones de modificación y consulta de diccionarios.
 * 
 * Casos de uso típicos:
 * - Eliminación de palabras inexistentes del diccionario
 * - Búsqueda de palabras no registradas
 * - Operaciones de modificación sobre palabras no válidas
 * - Validación de existencia durante consultas
 * 
 * @version 2.0
 * @since 1.0
 */
public class ExceptionPalabraNotExist extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor por defecto que establece un mensaje predeterminado.
     * 
     * @post Se crea una instancia de la excepción con el mensaje "La palabra no existe en el diccionario".
     */
    public ExceptionPalabraNotExist() {
        super("La palabra no existe en el diccionario");
    }

    /**
     * Constructor que permite establecer un mensaje personalizado.
     * 
     * @param message Mensaje de error personalizado que describe el problema específico.
     * @post Se crea una instancia de la excepción con el mensaje proporcionado.
     */
    public ExceptionPalabraNotExist(String message) {
        super(message);
    }
} 