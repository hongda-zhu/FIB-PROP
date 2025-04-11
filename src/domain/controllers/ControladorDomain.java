package domain.controllers;

import domain.controllers.subcontrollers.*;

import java.util.*;

/*
 * ControladorDomain.java
 * 
 * Este controlador actúa como un intermediario entre los controladores de configuración,
 * juego y ranking. Facilita la comunicación entre ellos y proporciona una interfaz
 * unificada para interactuar con el dominio del juego.
 */

public class ControladorDomain {
    private ControladorConfiguracion controladorConfiguracion;
    private ControladorJuego controladorJuego;
    private ControladorRanking controladorRanking;

    /*
        * Constructor de ControladorDomain
        * Inicializa los controladores de configuración, juego y ranking.
    */
    public ControladorDomain() {
        this.controladorConfiguracion = new ControladorConfiguracion();
        this.controladorJuego = new ControladorJuego();
        this.controladorRanking = new ControladorRanking();
    }
    /*
        * Método para iniciar la sesión de un jugador
        * @param username Nombre de usuario del jugador
        * @param password Contraseña del jugador
    */
    public void iniciarSesion(String username, String password) {
        if (!controladorConfiguracion.existeUsuario(username)) {
            throw new ExceptionUserNotExist();
        }
        controladorConfiguracion.autenticar(username, password);
    }
    /*
        * Método para registrar un jugador
        * @param username Nombre de usuario del jugador
        * @param password Contraseña del jugador
    */
    public void registrarUsuario(String username, String password) {
        if (controladorConfiguracion.existeUsuario(username)) {
            throw new ExceptionUserExist();
        }
        controladorConfiguracion.registrarUsuario(username, password);
    }  

    public void cerrrarSesion(String username) {
        if (!controladorConfiguracion.existeUsuario(username)) {
            throw new ExceptionUserNotExist();
        }
        if (!controladorConfiguracion.isLoggedIn(username)) {
            throw new ExceptionUserNotLoggedIn();
        }
        // Cerrar sesión del usuario
        controladorConfiguracion.cerrarSesion(username);
    }

    public void modificarConfiguracion(String clave, String nuevoValor) {
        if (!controladorConfiguracion.existeConfiguracion(clave)) {
            throw new ExceptionConfigNotExist();
        }
        controladorConfiguracion.actualizarConfiguracion(clave, nuevoValor);
    }

    public void agregarConfiguracion(String clave, String valor) {
        if (controladorConfiguracion.existeConfiguracion(clave)) {
            throw new ExceptionConfigExist();
        }
        controladorConfiguracion.agregarConfiguracion(clave, valor);
    }

    public void eliminarConfiguracion(String clave) {
        if (!controladorConfiguracion.existeConfiguracion(clave)) {
            throw new ExceptionConfigNotExist();
        }
        controladorConfiguracion.eliminarConfiguracion(clave);
    }

    public void verConfiguracion(String clave) {
        if (!controladorConfiguracion.existeConfiguracion(clave)) {
            throw new ExceptionConfigNotExist();
        }
        controladorConfiguracion.verConfiguracion(clave);
    }

    public void listarConfiguraciones() {
        controladorConfiguracion.listarConfiguraciones();
    }

    public void crearDiccionario(String nombre, List<String> palabras) {
        if (controladorJuego.existeDiccionario(nombre)) {
            throw new ExceptionDiccionarioExist();
        }
        controladorJuego.crearDiccionario(nombre, palabras);
    }

    public void agregarPalabraADiccionario(String nombre, String palabra) {
        if (!controladorJuego.existeDiccionario(nombre)) {
            throw new ExceptionDiccionarioNotExist();
        }
        controladorJuego.agregarPalabraADiccionario(nombre, palabra);
    }

    public void eliminarPalabraDeDiccionario(String nombre, String palabra) {
        if (!controladorJuego.existeDiccionario(nombre)) {
            throw new ExceptionDiccionarioNotExist();
        }
        if (!controladorJuego.existePalabraEnDiccionario(nombre, palabra)) {
            throw new ExceptionPalabraNotExist();
        }
        if (controladorJuego.existeDiccionarioEnPartida(nombre)) {
            throw new ExceptionDiccionarioInUse();
        }
        controladorJuego.eliminarPalabraDeDiccionario(nombre, palabra);
    }

    public void importarDiccionario(String nombre, File archivo) {
        if (!archivo.exists()) {
            throw new ExceptionFileNotExist();
        }
        if (controladorJuego.existeDiccionario(nombre)) {
            throw new ExceptionDiccionarioExist();
        }
        controladorJuego.importarDiccionario(nombre, archivo);
    }

    public void definirPartida(String nombre, String diccionario, Boolean ordenador, String dificultad) {
        if (!controladorJuego.existeDiccionario(diccionario)) {
            throw new ExceptionDiccionarioNotExist();
        }
        if (controladorJuego.existePartida(nombre)) {
            throw new ExceptionPartidaExist();
        }
        if (ordenador) {
            controladorJuego.definirPartida(nombre, diccionario, dificultad);
        }
        controladorJuego.definirPartida(nombre, diccionario);
    }

    public void iniciarJuego(String username) {
        if (!controladorJuego.existePartida(username)) {
            throw new ExceptionPartidaNotExist();
        }
        controladorJuego.iniciarJuego(username);
    }
    /*
        * Método para jugar
        * Está incompleto
    */
    public void jugar(String username, String palabra, int fila, int columna, String direccion) {
        controladorJuego.jugar(username, palabra, fila, columna, direccion);
    }

    public void guardarPartida(String username) {
        controladorJuego.guardarPartida(username);
    }

    public void cargarPartida(String username, int idPartida) {
        if (!controladorJuego.existePartidaGuardada(username, idPartida)) {
            throw new ExceptionPartidaNotExist();
        }
        controladorJuego.cargarPartida(username, idPartida);
    }

    public void verRanking() {
        controladorRanking.verRanking();
    }

    public void agregarPuntuacion(String username, int puntuacion) {
        if (!controladorConfiguracion.existeUsuario(username)) {
            throw new ExceptionUserNotExist();
        }
        controladorRanking.agregarPuntuacion(username, puntuacion);
    }

    public void eliminarPuntuacion(String username, int puntuacion) {
        if (!controladorRanking.existePuntuacion(username, puntuacion)) {
            throw new ExceptionPuntuacionNotExist();
        }
        controladorRanking.eliminarPuntuacion(username, puntuacion);
    }

    public void listarPuntuaciones() {
        controladorRanking.listarPuntuaciones();
    }

    public void verPartidasGuardadas(String username) {
        controladorJuego.verPartidasGuardadas(username);
    }

    public void eliminarPartidaGuardada(String username, int idPartida) {
        if (!controladorJuego.existePartidaGuardada(username, idPartida)) {
            throw new ExceptionPartidaNotExist();
        }
        if (controladorJuego.existePartidaEnUso(username, idPartida)) {
            throw new ExceptionPartidaInUse();
        }
        controladorJuego.eliminarPartidaGuardada(username, idPartida);
    }
}
