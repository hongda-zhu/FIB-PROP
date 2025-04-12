package domain.excepciones;

/**
 * Excepción lanzada cuando se intenta acceder a un diccionario que no existe.
 */
public class ExceptionDiccionarioNotExist extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public ExceptionDiccionarioNotExist() {
        super("El diccionario no existe");
    }
    
    public ExceptionDiccionarioNotExist(String mensaje) {
        super(mensaje);
    }
} 