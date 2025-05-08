package scrabble.domain.persistences.interfaces;

import scrabble.domain.models.Configuracion;

/**
 * Interfaz que define las operaciones de persistencia para la configuración.
 */
public interface RepositorioConfiguracion {
    /**
     * Guarda la configuración actual en el sistema de persistencia.
     * 
     * @param configuracion La configuración a guardar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    boolean guardar(Configuracion configuracion);
    
    /**
     * Carga la configuración desde el sistema de persistencia.
     * 
     * @return La configuración cargada o una nueva configuración por defecto si no existe
     */
    Configuracion cargar();
} 