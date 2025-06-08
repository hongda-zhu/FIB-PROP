package scrabble.presentation.componentes;

import java.net.URL;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


/**
 * Gestor de música de fondo para la aplicación.
 * Proporciona funcionalidades para reproducir, pausar y controlar el volumen
 * de la música de fondo de forma centralizada. Utiliza el patrón Singleton
 * para garantizar una única instancia del reproductor de música.
 * La música se reproduce en bucle infinito una vez iniciada.
 */
public class MusicManager {
    private static MediaPlayer mediaPlayer;
    private static boolean initialized = false;


    /**
     * Inicializa el reproductor de música cargando el archivo de audio.
     * Solo se ejecuta una vez, las llamadas posteriores son ignoradas.
     * Configura el reproductor en modo bucle infinito con volumen al 50%.
     * @pre El archivo /audio/musica.wav debe existir en los recursos
     * @post Si la inicialización es exitosa: initialized == true && mediaPlayer != null
     *       Si falla: initialized == false && se registra el error en consola
     */
    public static void initialize() {
        if (initialized) return;

        try {
            URL url = MusicManager.class.getResource("/audio/musica.wav");
            if (url == null) {
                System.err.println("No se pudo encontrar el archivo de música en los recursos.");
                return;
            }

            Media media = new Media(url.toExternalForm());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.setVolume(0.5);
            initialized = true;
        } catch (Exception e) {
            System.err.println("Error al inicializar el reproductor de música:");
            e.printStackTrace();
        }
    }


    /**
     * Inicia la reproducción de la música de fondo.
     * Si la música ya está reproduciéndose, no tiene efecto.
     * @pre El reproductor debe estar inicializado
     * @post Si mediaPlayer != null: música iniciada o continúa reproduciéndose
     */
    public static void play() {
        if (mediaPlayer != null) mediaPlayer.play();
    }


    /**
     * Pausa la reproducción de la música de fondo.
     * La música puede reanudarse posteriormente con play().
     * @pre El reproductor debe estar inicializado
     * @post Si mediaPlayer != null: música pausada
     */
    public static void pause() {
        if (mediaPlayer != null) mediaPlayer.pause();
    }

    /**
     * Establece el volumen de la música de fondo.
     * @param vol Nivel de volumen entre 0.0 (silencio) y 1.0 (volumen máximo)
     * @pre 0.0 <= vol <= 1.0 && el reproductor debe estar inicializado
     * @post Si mediaPlayer != null: volumen actualizado al valor especificado
     */
    public static void setVolume(double vol) {
        if (mediaPlayer != null) mediaPlayer.setVolume(vol);
    }

    /**
     * Verifica si la música está actualmente reproduciéndose.
     * @return true si la música está sonando, false en caso contrario
     * @pre Ninguna
     * @post Resultado refleja el estado actual de reproducción
     */
    public static boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }
}




