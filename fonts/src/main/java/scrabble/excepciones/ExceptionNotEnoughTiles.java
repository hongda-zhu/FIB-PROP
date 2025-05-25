package scrabble.excepciones;

/**
 * Excepción lanzada cuando no hay suficientes fichas disponibles para completar una operación.
 * 
 * Esta excepción se produce cuando se intenta realizar una acción que requiere más fichas
 * de las que están disponibles en la bolsa o en el atril del jugador. Es fundamental
 * para el control del flujo del juego y la validación de movimientos.
 * 
 * Casos de uso típicos:
 * - Intento de robar fichas cuando la bolsa está vacía
 * - Operaciones de intercambio sin fichas suficientes
 * - Validación de movimientos con fichas insuficientes
 * - Control de fin de juego por agotamiento de fichas
 * 
 * @version 2.0
 * @since 1.0
 */
public class ExceptionNotEnoughTiles extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructor por defecto que crea la excepción con un mensaje estándar.
     * 
     * @post Se crea una instancia de la excepción con el mensaje "No hay suficientes fichas".
     */
    public ExceptionNotEnoughTiles() {
        super("No hay suficientes fichas");
    }
    
    /**
     * Constructor que permite especificar un mensaje personalizado para la excepción.
     * 
     * @param mensaje Mensaje descriptivo específico sobre el error ocurrido.
     * @post Se crea una instancia de la excepción con el mensaje proporcionado.
     */
    public ExceptionNotEnoughTiles(String mensaje) {
        super(mensaje);
    }
} 