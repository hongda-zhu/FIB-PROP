package excepciones;

/**
 * Excepción lanzada cuando se intenta realizar una operación que requiere
 * que el usuario esté logueado, pero no lo está.
 */
public class ExceptionUserLoggedIn extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public ExceptionUserLoggedIn() {
        super("El usuario ha iniciado sesión");
    }
    
    public ExceptionUserLoggedIn(String mensaje) {
        super(mensaje);
    }
} 