package scrabble.excepciones;

/**
 * Excepción lanzada cuando se intenta crear un usuario que ya existe.
 */
public class ExceptionUserExist extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public ExceptionUserExist() {
        super("El usuario ya existe");
    }
    
    public ExceptionUserExist(String mensaje) {
        super(mensaje);
    }
} 