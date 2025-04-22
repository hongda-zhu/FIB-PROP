package scrabble.excepciones;

/**
 * Excepción lanzada cuando se intenta realizar una operación con un usuario
 * que ya está participando en una partida.
 */
public class ExceptionUserInGame extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public ExceptionUserInGame() {
        super("El usuario ya está participando en una partida");
    }
    
    public ExceptionUserInGame(String mensaje) {
        super(mensaje);
    }
} 