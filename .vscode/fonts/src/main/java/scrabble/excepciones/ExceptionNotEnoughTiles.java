package scrabble.excepciones;

/**
 * Excepción lanzada cuando se intenta crear una partida que ya existe.
 */
public class ExceptionNotEnoughTiles extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public ExceptionNotEnoughTiles() {
        super("No hay suficientes fichas");
    }
    
    public ExceptionNotEnoughTiles(String mensaje) {
        super(mensaje);
    }
} 