package scrabble.excepciones;

/**
 * Excepción que se lanza cuando una palabra contiene caracteres no válidos para el diccionario.
 * 
 * Esta excepción se produce cuando se intenta procesar una palabra que contiene
 * caracteres que no están incluidos en el alfabeto del diccionario actual,
 * o cuando la palabra no cumple con las reglas de validación establecidas.
 * Es fundamental para mantener la integridad de los diccionarios.
 * 
 * Casos de uso típicos:
 * - Palabras con caracteres fuera del alfabeto del diccionario
 * - Validación de entrada durante la creación de diccionarios
 * - Verificación de palabras en movimientos de juego
 * - Control de calidad en importación de archivos de diccionario
 * 
 * @version 2.0
 * @since 1.0
 */
public class ExceptionPalabraInvalida extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor por defecto que establece un mensaje predeterminado.
     * 
     * @post Se crea una instancia de la excepción con el mensaje "La palabra contiene caracteres no válidos para este diccionario".
     */
    public ExceptionPalabraInvalida() {
        super("La palabra contiene caracteres no válidos para este diccionario");
    }

    /**
     * Constructor que permite establecer un mensaje personalizado.
     * 
     * @param message Mensaje de error personalizado que describe el problema específico.
     * @post Se crea una instancia de la excepción con el mensaje proporcionado.
     */
    public ExceptionPalabraInvalida(String message) {
        super(message);
    }
} 