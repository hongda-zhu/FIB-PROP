package scrabble.excepciones;

/**
 * Excepción lanzada cuando se intenta crear una partida que ya existe.
 */
public class ExceptionPartidaExist extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public ExceptionPartidaExist() {
        super("La partida ya existe");
    }
    
    public ExceptionPartidaExist(String mensaje) {
        super(mensaje);
    }
} 