package domain.excepciones;

/**
 * Excepción lanzada cuando se intenta acceder a una puntuación que no existe.
 */
public class ExceptionPuntuacionNotExist extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public ExceptionPuntuacionNotExist() {
        super("La puntuación no existe");
    }
    
    public ExceptionPuntuacionNotExist(String mensaje) {
        super(mensaje);
    }
} 