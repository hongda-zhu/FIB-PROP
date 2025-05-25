package scrabble.excepciones;

/**
 * Excepción lanzada cuando se intenta realizar una operación con un usuario que ya está participando en una partida.
 * 
 * Esta excepción se produce cuando se intenta iniciar una nueva partida o realizar
 * operaciones que requieren que el usuario esté disponible, pero el usuario ya
 * está involucrado en una partida activa. Forma parte del sistema de control
 * de estado de usuarios y gestión de partidas concurrentes.
 * 
 * Casos de uso típicos:
 * - Intento de unirse a múltiples partidas simultáneamente
 * - Operaciones que requieren usuario disponible
 * - Control de concurrencia en partidas multijugador
 * - Validación de estado antes de iniciar nuevas partidas
 * 
 * @version 2.0
 * @since 1.0
 */
public class ExceptionUserInGame extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructor por defecto que crea la excepción con un mensaje estándar.
     * 
     * @post Se crea una instancia de la excepción con el mensaje "El usuario ya está participando en una partida".
     */
    public ExceptionUserInGame() {
        super("El usuario ya está participando en una partida");
    }
    
    /**
     * Constructor que permite especificar un mensaje personalizado para la excepción.
     * 
     * @param mensaje Mensaje descriptivo específico sobre el error ocurrido.
     * @post Se crea una instancia de la excepción con el mensaje proporcionado.
     */
    public ExceptionUserInGame(String mensaje) {
        super(mensaje);
    }
} 