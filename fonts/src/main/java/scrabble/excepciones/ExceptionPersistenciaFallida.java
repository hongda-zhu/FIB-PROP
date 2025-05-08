package scrabble.excepciones;

/**
 * Excepción que se lanza cuando ocurre un error durante operaciones de persistencia.
 */
public class ExceptionPersistenciaFallida extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructor predeterminado sin mensaje de error.
     */
    public ExceptionPersistenciaFallida() {
        super("Error durante la operación de persistencia");
    }
    
    /**
     * Constructor con mensaje de error personalizado.
     * 
     * @param mensaje Mensaje descriptivo del error
     */
    public ExceptionPersistenciaFallida(String mensaje) {
        super(mensaje);
    }
    
    /**
     * Constructor con mensaje y causa del error.
     * 
     * @param mensaje Mensaje descriptivo del error
     * @param causa Excepción que causó el error
     */
    public ExceptionPersistenciaFallida(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
} 