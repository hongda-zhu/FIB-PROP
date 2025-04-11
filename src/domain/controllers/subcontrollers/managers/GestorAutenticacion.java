package domain.controllers.subcontrollers.managers;
import java.util.HashMap;
import java.util.Map;

public class GestorAutenticacion {
    private Map<String, String> usuarios; // mapeo usuario -> contraseña

    public GestorAutenticacion() {
        this.usuarios = new HashMap<>();
    }

    public boolean autenticar(String usuario, String contrasena) {
        return usuarios.containsKey(usuario) && usuarios.get(usuario).equals(contrasena);
    }

    public boolean registrar(String usuario, String contrasena) {
        if (usuarios.containsKey(usuario)) return false;
        usuarios.put(usuario, contrasena);
        return true;
    }

    public boolean eliminarUsuario(String usuario) {
        return usuarios.remove(usuario) != null;
    }

    public boolean cambiarContrasena(String usuario, String nuevaContrasena) {
        if (!usuarios.containsKey(usuario)) return false;
        usuarios.put(usuario, nuevaContrasena);
        return true;
    }
    
    public boolean existe (String usuario) {
    	if(usuarios.containsKey(usuario)) return true;
    	else return false;
    }
    
}
