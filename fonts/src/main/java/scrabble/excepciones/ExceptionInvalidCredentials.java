package scrabble.excepciones;

/**
 * Excepción lanzada cuando las credenciales proporcionadas son inválidas.
 * Más específica que ExceptionAuthFail para casos donde se requiere mayor detalle.
 */
public class ExceptionInvalidCredentials extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public ExceptionInvalidCredentials() {
        super("Las credenciales proporcionadas son inválidas");
    }
    
    public ExceptionInvalidCredentials(String mensaje) {
        super(mensaje);
    }
} 