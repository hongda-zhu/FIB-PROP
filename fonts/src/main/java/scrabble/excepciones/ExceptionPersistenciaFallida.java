package scrabble.excepciones;

/**
 * Excepción que se lanza cuando ocurre un error durante operaciones de persistencia de datos.
 * 
 * Esta excepción encapsula todos los errores relacionados con el almacenamiento y
 * recuperación de datos del sistema, incluyendo problemas de E/O, serialización,
 * acceso a archivos y corrupción de datos. Extiende Exception para forzar el
 * manejo explícito de errores de persistencia.
 * 
 * Casos de uso típicos:
 * - Errores de lectura/escritura de archivos
 * - Problemas de serialización/deserialización
 * - Falta de permisos de acceso a directorios
 * - Corrupción de datos persistidos
 * - Falta de espacio en disco
 * - Problemas de red en sistemas distribuidos
 * 
 * @version 2.0
 * @since 1.0
 */
public class ExceptionPersistenciaFallida extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructor por defecto que crea la excepción con un mensaje estándar.
     * 
     * @post Se crea una instancia de la excepción con un mensaje genérico sobre errores de persistencia.
     */
    public ExceptionPersistenciaFallida() {
        super("Error durante la operación de persistencia");
    }
    
    /**
     * Constructor que permite especificar un mensaje personalizado para la excepción.
     * 
     * @param mensaje Mensaje descriptivo específico sobre el error de persistencia ocurrido.
     * @post Se crea una instancia de la excepción con el mensaje proporcionado.
     */
    public ExceptionPersistenciaFallida(String mensaje) {
        super(mensaje);
    }
    
    /**
     * Constructor que permite especificar un mensaje y la causa raíz del error.
     * 
     * @param mensaje Mensaje descriptivo específico sobre el error de persistencia.
     * @param causa Excepción original que causó este error de persistencia.
     * @post Se crea una instancia de la excepción con el mensaje y causa proporcionados,
     *       permitiendo el rastreo completo de la cadena de errores.
     */
    public ExceptionPersistenciaFallida(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
} 