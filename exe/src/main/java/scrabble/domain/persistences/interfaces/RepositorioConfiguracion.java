package scrabble.domain.persistences.interfaces;

import scrabble.domain.models.Configuracion;

/**
 * Interfaz que define las operaciones de persistencia para la configuración.
 * 
 * Esta interfaz establece el contrato para la persistencia de objetos de configuración,
 * proporcionando métodos para guardar y cargar configuraciones del sistema.
 * Implementa el patrón Repository para abstraer los detalles de persistencia
 * y permitir diferentes implementaciones de almacenamiento.
 * 
 * 
 * @version 2.0
 * @since 1.0
 */
public interface RepositorioConfiguracion {
    /**
     * Guarda la configuración actual en el sistema de persistencia.
     * 
     * @pre configuracion no debe ser null
     * @param configuracion La configuración a guardar
     * @return true si la operación fue exitosa, false en caso contrario
     * @post Si la operación es exitosa, la configuración se persiste en el sistema de almacenamiento
     */
    boolean guardar(Configuracion configuracion);
    
    /**
     * Carga la configuración desde el sistema de persistencia.
     * 
     * @pre No hay precondiciones específicas
     * @return La configuración cargada o una nueva configuración por defecto si no existe
     * @post Se devuelve un objeto Configuracion válido, nunca null
     */
    Configuracion cargar();
} 