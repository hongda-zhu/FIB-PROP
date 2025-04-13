package excepciones;

/**
 * Excepción lanzada cuando una contraseña no cumple con los requisitos de seguridad.
 * Por ejemplo, longitud mínima, caracteres especiales, etc.
 */
public class ExceptionInvalidPassword extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public ExceptionInvalidPassword() {
        super("La contraseña no cumple con los requisitos de seguridad");
    }
    
    public ExceptionInvalidPassword(String mensaje) {
        super(mensaje);
    }
} 