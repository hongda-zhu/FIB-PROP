package domain.excepciones;

/**
 * Excepción lanzada cuando se intenta registrar una puntuación inválida en el ranking.
 * Por ejemplo, puntuaciones negativas o fuera de rango.
 */
public class ExceptionInvalidScore extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public ExceptionInvalidScore() {
        super("La puntuación proporcionada no es válida");
    }
    
    public ExceptionInvalidScore(String mensaje) {
        super(mensaje);
    }
} 