package excepciones;

/**
 * Excepción lanzada cuando se intenta acceder a una partida que no existe.
 */
public class ExceptionPartidaNotExist extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public ExceptionPartidaNotExist() {
        super("La partida no existe");
    }
    
    public ExceptionPartidaNotExist(String mensaje) {
        super(mensaje);
    }
} 