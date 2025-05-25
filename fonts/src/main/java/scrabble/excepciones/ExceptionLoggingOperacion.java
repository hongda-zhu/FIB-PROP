package scrabble.excepciones;

/**
 * Excepción que encapsula mensajes informativos y de logging del sistema.
 * 
 * Esta excepción está diseñada para transmitir información desde las capas internas
 * hacia las capas de presentación, permitiendo que sea el DomainDriver quien maneje
 * la presentación de estos mensajes al usuario. Actúa como un mecanismo de comunicación
 * entre capas para eventos que requieren notificación pero no necesariamente representan errores.
 * 
 * Funcionalidades principales:
 * - Transmisión de mensajes informativos entre capas
 * - Categorización de mensajes por tipo de operación
 * - Distinción entre mensajes de error e informativos
 * - Facilitación del logging y auditoría del sistema
 * 
 * @version 2.0
 * @since 1.0
 */
public class ExceptionLoggingOperacion extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final String tipoOperacion;
    private final boolean esError;

    /**
     * Constructor principal con mensaje, tipo de operación y clasificación de error.
     * 
     * @param message Mensaje informativo o de error que describe la operación realizada.
     * @param tipoOperacion Categoría o tipo de operación (ej: "eliminación", "modificación", "creación", etc.).
     * @param esError true si es un mensaje de error, false si es informativo.
     * @post Se crea una instancia con el mensaje, tipo de operación y clasificación especificados.
     */
    public ExceptionLoggingOperacion(String message, String tipoOperacion, boolean esError) {
        super(message);
        this.tipoOperacion = tipoOperacion;
        this.esError = esError;
    }

    /**
     * Constructor simplificado que asume que es un mensaje informativo (no error).
     * 
     * @param message Mensaje informativo que describe la operación realizada.
     * @param tipoOperacion Categoría o tipo de operación realizada.
     * @post Se crea una instancia con el mensaje y tipo de operación especificados, marcada como informativa.
     */
    public ExceptionLoggingOperacion(String message, String tipoOperacion) {
        this(message, tipoOperacion, false);
    }

    /**
     * Obtiene el tipo de operación asociada al mensaje.
     * 
     * @return El tipo de operación que generó este mensaje de logging.
     * @post Se devuelve una cadena que identifica el tipo de operación.
     */
    public String getTipoOperacion() {
        return tipoOperacion;
    }

    /**
     * Indica si el mensaje es de error o informativo.
     * 
     * @return true si es un mensaje de error, false si es informativo.
     * @post Se devuelve un booleano que clasifica el tipo de mensaje.
     */
    public boolean esError() {
        return esError;
    }
} 