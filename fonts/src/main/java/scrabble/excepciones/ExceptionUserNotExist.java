package excepciones;

/**
 * Excepción lanzada cuando se intenta acceder a un usuario que no existe.
 */
public class ExceptionUserNotExist extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public ExceptionUserNotExist() {
        super("El usuario no existe");
    }
    
    public ExceptionUserNotExist(String mensaje) {
        super(mensaje);
    }
} 