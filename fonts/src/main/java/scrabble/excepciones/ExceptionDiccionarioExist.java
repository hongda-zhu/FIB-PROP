package scrabble.excepciones;

/**
 * Excepción lanzada cuando se intenta crear un diccionario que ya existe.
 */
public class ExceptionDiccionarioExist extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public ExceptionDiccionarioExist() {
        super("El diccionario ya existe");
    }
    
    public ExceptionDiccionarioExist(String mensaje) {
        super(mensaje);
    }
} 