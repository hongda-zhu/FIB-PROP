package scrabble.excepciones;

/**
 * Excepción que se lanza cuando ocurre un error al guardar o cargar datos.
 */
public class ExceptionPersistenciaFallida extends Exception {
    private static final long serialVersionUID = 1L;
    private final String tipo;

    /**
     * Constructor que establece un mensaje predeterminado.
     */
    public ExceptionPersistenciaFallida() {
        super("Error de persistencia: no se pudo completar la operación de datos");
        this.tipo = "general";
    }

    /**
     * Constructor que permite establecer un mensaje personalizado.
     * @param message Mensaje de error personalizado
     */
    public ExceptionPersistenciaFallida(String message) {
        super(message);
        this.tipo = "general";
    }
    
    /**
     * Constructor que permite establecer un mensaje personalizado y el tipo de dato.
     * @param message Mensaje de error personalizado
     * @param tipo Tipo de dato (jugadores, ranking, diccionario, etc.)
     */
    public ExceptionPersistenciaFallida(String message, String tipo) {
        super(message);
        this.tipo = tipo;
    }
    
    /**
     * Obtiene el tipo de dato afectado por el error de persistencia.
     * @return El tipo de dato
     */
    public String getTipo() {
        return tipo;
    }
} 