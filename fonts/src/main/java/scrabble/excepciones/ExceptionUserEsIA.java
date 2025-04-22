package scrabble.excepciones;

/**
 * Excepción lanzada cuando se intenta realizar una operación de eliminación con un usuario
 * que es una IA.
 */
public class ExceptionUserEsIA extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public ExceptionUserEsIA() {
        super("No se puede realizar esta operación porque el usuario es una IA");
    }
    
    public ExceptionUserEsIA(String mensaje) {
        super(mensaje);
    }
} 