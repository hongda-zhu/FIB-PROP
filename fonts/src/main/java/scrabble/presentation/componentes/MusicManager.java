package scrabble.presentation.componentes;

import java.net.URL;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MusicManager {
    private static MediaPlayer mediaPlayer;
    private static boolean initialized = false;

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

    public static void play() {
        if (mediaPlayer != null) mediaPlayer.play();
    }

    public static void pause() {
        if (mediaPlayer != null) mediaPlayer.pause();
    }

    public static void setVolume(double vol) {
        if (mediaPlayer != null) mediaPlayer.setVolume(vol);
    }

    public static boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }
}




