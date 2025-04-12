package domain.models;

import java.io.Serializable;

/**
 * Clase base abstracta que implementa la interfaz Usuario.
 * Proporciona una implementación común para los diferentes tipos de usuarios.
 */
public abstract class UsuarioBase implements Usuario, Serializable {
    private static final long serialVersionUID = 1L;
    
    protected String username;
    protected String password;
    protected String email;
    protected String nombreCompleto;
    protected int partidasJugadas;
    protected int partidasGanadas;
    
    /**
     * Constructor de la clase UsuarioBase.
     * 
     * @param username Nombre de usuario
     * @param password Contraseña
     */
    public UsuarioBase(String username, String password) {
        this.username = username;
        this.password = password;
        this.partidasJugadas = 0;
        this.partidasGanadas = 0;
        this.email = "";
        this.nombreCompleto = "";
    }
    
    /**
     * Constructor de la clase UsuarioBase con información extendida.
     * 
     * @param username Nombre de usuario
     * @param password Contraseña
     * @param email Correo electrónico
     * @param nombreCompleto Nombre completo del usuario
     */
    public UsuarioBase(String username, String password, String email, String nombreCompleto) {
        this(username, password);
        this.email = email;
        this.nombreCompleto = nombreCompleto;
    }
    
    @Override
    public String getUsername() {
        return username;
    }
    
    @Override
    public boolean verificarPassword(String passwordToCheck) {
        return this.password.equals(passwordToCheck);
    }
    
    @Override
    public void setPassword(String password) {
        this.password = password;
    }
    
    @Override
    public String getEmail() {
        return email;
    }
    
    @Override
    public void setEmail(String email) {
        this.email = email;
    }
    
    @Override
    public String getNombreCompleto() {
        return nombreCompleto;
    }
    
    @Override
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }
    
    @Override
    public void incrementarPartidasJugadas() {
        this.partidasJugadas++;
    }
    
    @Override
    public void incrementarPartidasGanadas() {
        this.partidasGanadas++;
    }
    
    @Override
    public int getPartidasJugadas() {
        return partidasJugadas;
    }
    
    @Override
    public int getPartidasGanadas() {
        return partidasGanadas;
    }
    
    @Override
    public double getRatioVictorias() {
        return partidasJugadas > 0 ? (double) partidasGanadas / partidasJugadas : 0.0;
    }
    
    @Override
    public String toString() {
        return "Usuario{" +
               "username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", nombreCompleto='" + nombreCompleto + '\'' +
               ", partidasJugadas=" + partidasJugadas +
               ", partidasGanadas=" + partidasGanadas +
               '}';
    }
}
