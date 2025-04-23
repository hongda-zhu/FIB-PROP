package scrabble.excepciones;

/**
 * Excepción lanzada cuando una operación en el sistema de ranking no se puede completar.
 * Puede incluir detalles específicos sobre el tipo de error.
 */
public class ExceptionRankingOperationFailed extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public ExceptionRankingOperationFailed() {
        super("La operación en el ranking no se pudo completar");
    }
    
    public ExceptionRankingOperationFailed(String mensaje) {
        super(mensaje);
    }
} 