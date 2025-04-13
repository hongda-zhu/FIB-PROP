/* package domain.controllers;

import domain.controllers.subcontrollers.*;
import excepciones.*;
import java.io.File;
import java.nio.file.SecureDirectoryStream;
import java.util.List;
import java.util.Scanner;

public class ControladorDomain {
    private final ControladorConfiguracion controladorConfiguracion;
    private final ControladorJuego controladorJuego;
    private final ControladorRanking controladorRanking;
    private final ControladorUsuario controladorUsuario;

    public ControladorDomain() {
        this.controladorConfiguracion = new ControladorConfiguracion();
        this.controladorJuego = new ControladorJuego();
        this.controladorRanking = ControladorRanking.getInstance();
        this.controladorUsuario = ControladorUsuario.getInstance();
    }

    public void iniciarSesion(String id, String password) {
        if (!controladorUsuario.existeJugador(id)) {
            throw new ExceptionUserNotExist();
        }
        if (!controladorUsuario.autenticar(id, password)) {
            throw new ExceptionPasswordMismatch();
        }
    }

    public void registrarUsuario(String id, String password) {
        if (controladorUsuario.existeJugador(id)) {
            throw new ExceptionUserExist();
        }
        if (!controladorUsuario.registrarUsuario(id, password)) {
            throw new RuntimeException("Error al registrar usuario.");
        }
    }

    public void cerrrarSesion(String id) {
        if (!controladorUsuario.existeJugador(id)) {
            throw new ExceptionUserNotExist();
        }
        if (!controladorUsuario.isLoggedIn(id)) {
            throw new ExceptionUserNotLoggedIn();
        }
        controladorUsuario.cerrarSesion(id);
    }

    public void eliminarUsuario(String id) {
        if (!controladorUsuario.existeJugador(id)) {
            throw new ExceptionUserNotExist();
        }
        if (controladorUsuario.isLoggedIn(id)) {
            throw new ExceptionUserLoggedIn();
        }
        controladorUsuario.eliminarUsuario(id);
    }

    public boolean cambiarContrasena(String id, String oldPass, String nuevaContrasena) {
        if (!controladorUsuario.existeJugador(id)) {
            throw new ExceptionUserNotExist();
        }
        return controladorUsuario.cambiarContrasena(id, oldPass, nuevaContrasena);
    }

    public boolean cambiarNombre(String id, String nuevoNombre) {
        if (!controladorUsuario.existeJugador(id)) {
            throw new ExceptionUserNotExist();
        }
        return controladorUsuario.cambiarNombre(id, nuevoNombre);
    }

    // public void setIdioma(String idioma) {
    //     controladorConfiguracion.setIdioma(idioma);
    // }

    // public void setTema(String tema) {
    //     controladorConfiguracion.setTema(tema);
    // }

    public void setVolumen(int volumen) {
        controladorConfiguracion.setVolumen(volumen);
    }

    public void iniciarJuego(String language) {
        controladorJuego.iniciarJuego(language);
        
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
        if (!controladorUsuario.existeUsuario(username)) {
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
*/