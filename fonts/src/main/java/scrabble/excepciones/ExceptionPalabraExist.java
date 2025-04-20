package scrabble.excepciones;

import java.io.Serializable;

/**
 * Excepción que se lanza cuando se intenta añadir una palabra que ya existe en el diccionario.
 */
public class ExceptionPalabraExist extends Exception implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor que establece un mensaje predeterminado.
     */
    public ExceptionPalabraExist() {
        super("La palabra ya existe en el diccionario");
    }

    /**
     * Constructor que permite establecer un mensaje personalizado.
     * @param message Mensaje de error personalizado
     */
    public ExceptionPalabraExist(String message) {
        super(message);
    }
} 