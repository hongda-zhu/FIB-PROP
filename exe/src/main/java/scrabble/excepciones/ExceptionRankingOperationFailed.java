package scrabble.excepciones;

/**
 * Excepción lanzada cuando una operación en el sistema de ranking no se puede completar.
 * 
 * Esta excepción se produce cuando fallan operaciones relacionadas con la gestión
 * del ranking de jugadores, incluyendo actualización de estadísticas, cálculo de
 * posiciones, persistencia de datos o consultas de clasificación. Puede incluir
 * detalles específicos sobre el tipo de error ocurrido.
 * 
 * Casos de uso típicos:
 * - Errores en la actualización de estadísticas de jugadores
 * - Fallos en el cálculo de rankings ordenados
 * - Problemas de persistencia del ranking
 * - Errores en consultas de estadísticas específicas
 * - Fallos en operaciones de eliminación de jugadores del ranking
 * 
 * @version 2.0
 * @since 1.0
 */
public class ExceptionRankingOperationFailed extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructor por defecto que crea la excepción con un mensaje estándar.
     * 
     * @post Se crea una instancia de la excepción con el mensaje "La operación en el ranking no se pudo completar".
     */
    public ExceptionRankingOperationFailed() {
        super("La operación en el ranking no se pudo completar");
    }
    
    /**
     * Constructor que permite especificar un mensaje personalizado para la excepción.
     * 
     * @param mensaje Mensaje descriptivo específico sobre el error ocurrido.
     * @post Se crea una instancia de la excepción con el mensaje proporcionado.
     */
    public ExceptionRankingOperationFailed(String mensaje) {
        super(mensaje);
    }
} 