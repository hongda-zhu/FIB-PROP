package scrabble.excepciones;

/**
 * Excepción lanzada cuando se introduce un tema incorrecto
 */
public class ExceptionIdiomaNotExists extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public ExceptionIdiomaNotExists() {
        super("El idioma seleccionado no está disponible");
    }
    
    public ExceptionIdiomaNotExists(String mensaje) {
        super(mensaje);
    }
} 