package scrabble.excepciones;

/**
 * Excepción que se lanza cuando falla una operación sobre un diccionario.
 */
public class ExceptionDiccionarioOperacionFallida extends Exception {
    private static final long serialVersionUID = 1L;
    private final String tipoOperacion;

    /**
     * Constructor que establece un mensaje predeterminado.
     */
    public ExceptionDiccionarioOperacionFallida() {
        super("Falló una operación en el diccionario");
        this.tipoOperacion = "desconocida";
    }

    /**
     * Constructor que permite establecer un mensaje personalizado.
     * @param message Mensaje de error personalizado
     */
    public ExceptionDiccionarioOperacionFallida(String message) {
        super(message);
        this.tipoOperacion = "desconocida";
    }

    /**
     * Constructor que permite establecer un mensaje personalizado y el tipo de operación.
     * @param message Mensaje de error personalizado
     * @param tipoOperacion Tipo de operación (crear, eliminar, modificar, etc.)
     */
    public ExceptionDiccionarioOperacionFallida(String message, String tipoOperacion) {
        super(message);
        this.tipoOperacion = tipoOperacion;
    }
    
    /**
     * Obtiene el tipo de operación que falló.
     * @return El tipo de operación
     */
    public String getTipoOperacion() {
        return tipoOperacion;
    }
} 