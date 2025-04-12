package domain.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import domain.controllers.subcontrollers.managers.GestorJugada.Direction;

/**
 * Clase que representa a un jugador humano en el sistema.
 */
public class JugadorHumano extends Jugador {
    private static final long serialVersionUID = 1L;
    private int puntuacionUltimaPartida;

    private String password;
    private int partidasJugadas;
    private int partidasGanadas;
    private boolean enPartida;
    private boolean logueado;
    
    private Map<Character, Integer> rack; 
    private int skipTrack;
    /**
     * Constructor de la clase JugadorHumano.
     * 
     * @param id Identificador único del jugador
     * @param password Contraseña
     */
    public JugadorHumano(String id, String password) {
        this(id, id, password); // Por defecto, el nombre es igual al ID
        this.puntuacionUltimaPartida = 0;
        this.enPartida = false;
        this.rack = new HashMap<>(); // Inicialmente no tiene fichas en el rack
    }
    
    /**
     * Constructor de la clase JugadorHumano con nombre personalizado.
     * 
     * @param id Identificador único del jugador
     * @param nombre Nombre para mostrar del jugador
     * @param password Contraseña
     */
    public JugadorHumano(String id, String nombre, String password) {
        super(id, nombre);
        this.password = password;
        this.partidasJugadas = 0;
        this.partidasGanadas = 0;
        this.enPartida = false;
        this.logueado = false;
    }
    
    /**
     * Establece si el jugador está actualmente en una partida.
     * 
     * @param enPartida true si está en partida, false en caso contrario
     */
    public void setEnPartida(boolean enPartida) {
        this.enPartida = enPartida;
    }
    
    /**
     * Verifica si el jugador está actualmente en una partida.
     * 
     * @return true si está en partida, false en caso contrario
     */
    public boolean isEnPartida() {
        return enPartida;
    }
    
    /**
     * Establece si el jugador está logueado.
     * 
     * @param logueado true si está logueado, false en caso contrario
     */
    public void setLogueado(boolean logueado) {
        this.logueado = logueado;
    }
    
    /**
     * Verifica si el jugador está logueado.
     * 
     * @return true si está logueado, false en caso contrario
     */
    public boolean isLogueado() {
        return logueado;
    }
    
    /**
     * Establece el nombre para mostrar del jugador.
     * 
     * @param nombre Nuevo nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    /**
     * Establece la puntuación de la última partida jugada.
     * 
     * @param puntuacion Puntuación obtenida
     */
    @Override
    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }
    
    /**
     * Obtiene la puntuación de la última partida jugada.
     * 
     * @return Puntuación de la última partida
     */
    @Override
    public int getPuntuacion() {
        return puntuacion;
    }
    
    /**
     * Verifica si la contraseña proporcionada coincide con la del jugador.
     * 
     * @param password Contraseña a verificar
     * @return true si la contraseña coincide, false en caso contrario
     */
    public boolean verificarPassword(String passwordToCheck) {
        return this.password.equals(passwordToCheck);
    }
    
    /**
     * Establece una nueva contraseña para el jugador.
     * 
     * @param password Nueva contraseña
     */
    public void setPassword(String password) {
        this.password = password;
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
     * Obtiene el número de partidas jugadas.
     * 
     * @return Número de partidas jugadas
     */
    public int getPartidasJugadas() {
        return partidasJugadas;
    }
    
    /**
     * Obtiene el número de partidas ganadas.
     * 
     * @return Número de partidas ganadas
     */
    public int getPartidasGanadas() {
        return partidasGanadas;
    }
    
    /**
     * Obtiene el ratio de victorias del jugador.
     * 
     * @return Ratio de victorias (partidas ganadas / partidas jugadas)
     */
    public double getRatioVictorias() {
        return partidasJugadas > 0 ? (double) partidasGanadas / partidasJugadas : 0.0;
    }
    
    @Override
    public boolean esIA() {
        return false;
    }


    public Map<Character, Integer> getFichas() {
        return rack;
    }

    public int getPuntaje() {
        return puntuacionUltimaPartida;
    }

    public void addPuntaje(int puntos) {
        this.puntuacionUltimaPartida += puntos;
    }

    public void addSkipTrack() {
        this.skipTrack += 1;
    }

    public int getSkipTrack() {
        return skipTrack;
    }

    public void setSkipTrack(int skipTrack) {
        this.skipTrack = skipTrack;
    }

    @Override
    public String toString() {
        return "JugadorHumano{" +
               "id='" + id + '\'' +
               ", nombre='" + nombre + '\'' +
               ", partidasJugadas=" + partidasJugadas +
               ", partidasGanadas=" + partidasGanadas +
               ", puntuacion=" + puntuacion +
               ", enPartida=" + enPartida +
               ", logueado=" + logueado +
               '}';
    }

    public Triple<String, Tuple<Integer, Integer>, Direction> jugarTurno() {
        Scanner scanner = new Scanner(System.in);
        
        // Leer la palabra
        System.out.print("Introduce la palabra a colocar (o 'p' para pasar): ");
        String palabra = scanner.nextLine();
        
        // Si el usuario escribe 'p', retornar null
        if (palabra.equals("p")) {
            return null;
        }
        
        // Leer la posición de la última letra (coordenada X e Y)
        int x = -1;
        int y = -1;
        boolean coordenadasValidas = false;
        
        while (!coordenadasValidas) {
            System.out.print("Introduce la posición de la última letra (X Y): ");
            try {
                x = scanner.nextInt();
                y = scanner.nextInt();
                scanner.nextLine(); // Consume the remaining newline
                coordenadasValidas = true;
            } catch (Exception e) {
                scanner.nextLine(); // Clear the invalid input
                System.out.println("Formato incorrecto. Debes introducir dos números separados por un espacio.");
            }
        }
        
        // Leer la dirección
        String dir;
        boolean direccionValida = false;
        
        while (!direccionValida) {
            System.out.print("Introduce la dirección (HORIZONTAL o VERTICAL): ");
            dir = scanner.nextLine().toUpperCase();
            
            if (dir.equals("HORIZONTAL") || dir.equals("VERTICAL")) {
                direccionValida = true;
                Direction direction = dir.equals("HORIZONTAL") ? Direction.HORIZONTAL : Direction.VERTICAL;
                
                // Devolver la respuesta en formato Triple
                return new Triple<>(palabra, new Tuple<>(x, y), direction);
            } else {
                System.out.println("Dirección no válida. Debe ser HORIZONTAL o VERTICAL.");
            }
        }
        
        // Este return nunca debería alcanzarse, pero es necesario para la compilación
        return null;
    }

    public void robarFichas(Bolsa bolsa, int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            Ficha ficha = bolsa.sacarFicha();
            if (ficha != null) {
                rack.put(ficha.getLetra(), ficha.getValor());
            }
        }
    }

}