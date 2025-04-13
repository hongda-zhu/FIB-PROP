package scrabble.excepciones;

/**
 * Excepción lanzada cuando se introduce un tema incorrecto
 */
public class ExceptionTemaNotExists extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public ExceptionTemaNotExists() {
        super("El tema seleccionado no existe");
    }
    
    public ExceptionTemaNotExists(String mensaje) {
        super(mensaje);
    }
} 