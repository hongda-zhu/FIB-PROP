package scrabble.domain.controllers.subcontrollers.managers;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
/**
 * Gestor de autenticación de usuarios.
 * Maneja las operaciones relacionadas con la autenticación de usuarios en el sistema.
 */
public class GestorAutenticacion implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String JUGADORES_PASSWORD = "credenciales.dat";
    private Map<String, String> credenciales; // mapeo id -> contraseña

    /**
     * Constructor del GestorAutenticacion.
     * Inicializa el mapa de credenciales.
     */
    public GestorAutenticacion() {
        this.credenciales = new HashMap<>();
        cargarDatos();
    }

    /**
    * Obtiene una copia del mapa de credenciales (SOLO PARA DEBUG o podemos dejarlo)
    * @return Map con id->contraseña 
    */
    public Map<String, String> getCredencialesDebug() {
        return new HashMap<>(credenciales); // Devolvemos una copia por seguridad
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
        guardarDatos();
        return true;
    }

    /**
     * Elimina un usuario del sistema.
     * 
     * @param id ID del usuario a eliminar
     * @return true si el usuario fue eliminado, false si no existía
     */
    public boolean eliminarUsuario(String id) {
        boolean removed = credenciales.remove(id) != null;
        if (removed) {
            guardarDatos(); // Guardar solo si hubo cambios
        }
        return removed;
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
        guardarDatos();
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

    /**
    * Guarda los datos de autenticación en un archivo.
    */
    private void guardarDatos() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(JUGADORES_PASSWORD))) {
            oos.writeObject(credenciales);
        } catch (IOException e) {
            System.err.println("Error al guardar las credenciales: " + e.getMessage());
        }
    }

    /**
    * Carga los datos de autenticación desde un archivo.
    */
    @SuppressWarnings("unchecked")
    private void cargarDatos() {
        File credencialesFile = new File(JUGADORES_PASSWORD);
        if (credencialesFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(credencialesFile))) {
                credenciales = (Map<String, String>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error al cargar las credenciales: " + e.getMessage());
                credenciales = new HashMap<>(); // Si hay error, inicializar con uno nuevo
            }
        }
    }   
}