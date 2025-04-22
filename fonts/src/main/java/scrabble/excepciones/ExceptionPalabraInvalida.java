package scrabble.excepciones;

/**
 * Excepción que se lanza cuando una palabra contiene caracteres no válidos para el diccionario.
 */
public class ExceptionPalabraInvalida extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor que establece un mensaje predeterminado.
     */
    public ExceptionPalabraInvalida() {
        super("La palabra contiene caracteres no válidos para este diccionario");
    }

    /**
     * Constructor que permite establecer un mensaje personalizado.
     * @param message Mensaje de error personalizado
     */
    public ExceptionPalabraInvalida(String message) {
        super(message);
    }
} 