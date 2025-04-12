package domain.excepciones;

/**
 * Excepción lanzada cuando se intenta realizar una operación que requiere
 * que el usuario esté logueado, pero no lo está.
 */
public class ExceptionUserNotLoggedIn extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public ExceptionUserNotLoggedIn() {
        super("El usuario no ha iniciado sesión");
    }
    
    public ExceptionUserNotLoggedIn(String mensaje) {
        super(mensaje);
    }
} 