package scrabble.excepciones;

import java.io.Serializable;

/**
 * Excepción que se lanza cuando se intenta eliminar una palabra que no existe en el diccionario.
 */
public class ExceptionPalabraNotExist extends Exception implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor que establece un mensaje predeterminado.
     */
    public ExceptionPalabraNotExist() {
        super("La palabra no existe en el diccionario");
    }

    /**
     * Constructor que permite establecer un mensaje personalizado.
     * @param message Mensaje de error personalizado
     */
    public ExceptionPalabraNotExist(String message) {
        super(message);
    }
} 