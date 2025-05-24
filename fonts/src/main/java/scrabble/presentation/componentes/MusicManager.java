package scrabble.presentation.componentes;

import java.io.File;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MusicManager {
    private static MediaPlayer mediaPlayer;
    private static boolean initialized = false;

    public static void initialize() {
        if (initialized) return;
        String path = "src/main/resources/audio/musica.mp3"; // Ruta a tu archivo
        if (!new File(path).exists()) {
            System.err.println("El archivo de música no existe: " + path);
            return;
        }
        Media media = new Media(new File(path).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setVolume(0.5);
        initialized = true;
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
