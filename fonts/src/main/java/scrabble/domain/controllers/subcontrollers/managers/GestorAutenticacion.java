package domain.controllers.subcontrollers.managers;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

/**
 * Gestor de autenticación de usuarios.
 * Maneja las operaciones relacionadas con la autenticación de usuarios en el sistema.
 */
public class GestorAutenticacion implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Map<String, String> credenciales; // mapeo id -> contraseña

    /**
     * Constructor del GestorAutenticacion.
     * Inicializa el mapa de credenciales.
     */
    public GestorAutenticacion() {
        this.credenciales = new HashMap<>();
    }

    /**
     * Autentica a un usuario verificando sus credenciales.
     * 
     * @param id ID del usuario
     * @param contrasena Contraseña del usuario
     * @return true si las credenciales son correctas, false en caso contrario
     */
    public boolean autenticar(String id, String contrasena) {
        return credenciales.containsKey(id) && credenciales.get(id).equals(contrasena);
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * 
     * @param id ID del usuario
     * @param contrasena Contraseña del usuario
     * @return true si el registro fue exitoso, false si el usuario ya existe
     */
    public boolean registrar(String id, String contrasena) {
        if (credenciales.containsKey(id)) return false;
        credenciales.put(id, contrasena);
        return true;
    }

    /**
     * Elimina un usuario del sistema.
     * 
     * @param id ID del usuario a eliminar
     * @return true si el usuario fue eliminado, false si no existía
     */
    public boolean eliminarUsuario(String id) {
        return credenciales.remove(id) != null;
    }

    /**
     * Cambia la contraseña de un usuario.
     * 
     * @param id ID del usuario
     * @param nuevaContrasena Nueva contraseña
     * @return true si el cambio fue exitoso, false si el usuario no existe
     */
    public boolean cambiarContrasena(String id, String nuevaContrasena) {
        if (!credenciales.containsKey(id)) return false;
        credenciales.put(id, nuevaContrasena);
        return true;
    }
    
    /**
     * Verifica si un usuario existe en el sistema.
     * 
     * @param id ID del usuario
     * @return true si el usuario existe, false en caso contrario
     */
    public boolean existe(String id) {
        return credenciales.containsKey(id);
    }
}