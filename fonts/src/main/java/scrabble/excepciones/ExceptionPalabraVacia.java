package scrabble.excepciones;

import java.io.Serializable;

/**
 * Excepción que se lanza cuando se intenta procesar una palabra vacía.
 */
public class ExceptionPalabraVacia extends Exception implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor que establece un mensaje predeterminado.
     */
    public ExceptionPalabraVacia() {
        super("No se puede procesar una palabra vacía");
    }

    /**
     * Constructor que permite establecer un mensaje personalizado.
     * @param message Mensaje de error personalizado
     */
    public ExceptionPalabraVacia(String message) {
        super(message);
    }
} 