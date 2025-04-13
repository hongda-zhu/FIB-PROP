package excepciones;

/**
 * Excepción lanzada cuando las contraseñas proporcionadas no coinciden.
 * Útil para operaciones como cambio de contraseña o registro.
 */
public class ExceptionPasswordMismatch extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public ExceptionPasswordMismatch() {
        super("Las contraseñas no coinciden");
    }
    
    public ExceptionPasswordMismatch(String mensaje) {
        super(mensaje);
    }
} 