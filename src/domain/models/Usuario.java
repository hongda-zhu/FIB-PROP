package domain.models;

import java.io.Serializable;

/**
 * Clase que representa a un usuario en el sistema.
 * Contiene información básica de identificación y autenticación.
 */
public class Usuario implements Serializable {
    protected String username;
    protected String password;
    protected String email;
    // Información adicional que puede ser útil para el perfil
    protected String nombreCompleto;
    protected int partidasJugadas;
    protected int partidasGanadas;
    
    /**
     * Constructor de la clase Usuario.
     * 
     * @param username Nombre de usuario
     * @param password Contraseña
     */
    public Usuario(String username, String password) {
        this.username = username;
        this.password = password;
        this.partidasJugadas = 0;
        this.partidasGanadas = 0;
    }
    
    /**
     * Constructor de la clase Usuario con información extendida.
     * 
     * @param username Nombre de usuario
     * @param password Contraseña
     * @param email Correo electrónico
     * @param nombreCompleto Nombre completo del usuario
     */
    public Usuario(String username, String password, String email, String nombreCompleto) {
        this(username, password);
        this.email = email;
        this.nombreCompleto = nombreCompleto;
    }
    
    /**
     * Verifica si la contraseña proporcionada coincide con la del usuario.
     * 
     * @param passwordToCheck Contraseña a verificar
     * @return true si la contraseña coincide, false en caso contrario
     */
    public boolean verificarPassword(String passwordToCheck) {
        return this.password.equals(passwordToCheck);
    }
    
    /**
     * Incrementa el contador de partidas jugadas.
     */
    public void incrementarPartidasJugadas() {
        this.partidasJugadas++;
    }
    
    /**
     * Incrementa el contador de partidas ganadas.
     */
    public void incrementarPartidasGanadas() {
        this.partidasGanadas++;
    }
    
    /**
     * Obtiene el ratio de victorias del usuario.
     * 
     * @return Ratio de victorias (partidas ganadas / partidas jugadas)
     */
    public double getRatioVictorias() {
        return partidasJugadas > 0 ? (double) partidasGanadas / partidasJugadas : 0.0;
    }
    
    // Getters y Setters
    
    public String getUsername() {
        return username;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getNombreCompleto() {
        return nombreCompleto;
    }
    
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }
    
    public int getPartidasJugadas() {
        return partidasJugadas;
    }
    
    public int getPartidasGanadas() {
        return partidasGanadas;
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