package scrabble.excepciones;

/**
 * Excepción que encapsula mensajes informativos
 * 
 * 
 * Esta excepción está diseñada para transmitir información desde las capas internas
 * hacia las capas de presentación, permitiendo que sea el DomainDriver quien maneje
 * la presentación de estos mensajes al usuario.
 */
public class ExceptionLoggingOperacion extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final String tipoOperacion;
    private final boolean esError;

    /**
     * Constructor principal con mensaje y tipo de operación.
     * 
     * @param message Mensaje informativo o de error
     * @param tipoOperacion Categoría o tipo de operación (ej: "eliminación", "modificación", etc.)
     * @param esError true si es un mensaje de error, false si es informativo
     */
    public ExceptionLoggingOperacion(String message, String tipoOperacion, boolean esError) {
        super(message);
        this.tipoOperacion = tipoOperacion;
        this.esError = esError;
    }

    /**
     * Constructor simplificado que asume que es un mensaje informativo (no error).
     * 
     * @param message Mensaje informativo
     * @param tipoOperacion Categoría o tipo de operación
     */
    public ExceptionLoggingOperacion(String message, String tipoOperacion) {
        this(message, tipoOperacion, false);
    }

    /**
     * Obtiene el tipo de operación asociada al mensaje.
     * 
     * @return El tipo de operación
     */
    public String getTipoOperacion() {
        return tipoOperacion;
    }

    /**
     * Indica si el mensaje es de error o informativo.
     * 
     * @return true si es un mensaje de error, false si es informativo
     */
    public boolean esError() {
        return esError;
    }
} 