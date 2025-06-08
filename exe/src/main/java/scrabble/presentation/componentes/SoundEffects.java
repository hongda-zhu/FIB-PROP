package scrabble.presentation.componentes;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.media.AudioClip;


/**
 * Gestor de efectos de sonido para la aplicación.
 * Proporciona funcionalidades para reproducir clips de audio cortos con
 * control de volumen global y habilitación/deshabilitación de sonidos.
 * Utiliza un sistema de caché para optimizar el rendimiento cargando
 * los archivos de audio solo una vez y reutilizándolos en reproducciones posteriores.
 */
public class SoundEffects {
    private static final Map<String, AudioClip> clips = new HashMap<>();
    private static double volumen = 0.5; // volumen global por defecto (50%)
    private static boolean habilitado = true;


    /**
     * Establece el volumen global para todos los efectos de sonido.
     * Actualiza el volumen de todos los clips ya cargados en memoria.
     * El valor se ajusta automáticamente al rango válido [0.0, 1.0].
     * @param v Nivel de volumen deseado
     * @pre Ninguna (el método normaliza automáticamente el valor)
     * @post volumen está en rango [0.0, 1.0] && todos los clips existentes tienen el nuevo volumen
     */
    public static void setVolumen(double v) {
        volumen = Math.max(0.0, Math.min(1.0, v)); // asegurarse que esté en [0.0, 1.0]
        // Actualizar todos los clips ya cargados
        for (AudioClip clip : clips.values()) {
            clip.setVolume(volumen);
        }
    }

    /**
     * Habilita o deshabilita la reproducción de efectos de sonido.
     * Cuando está deshabilitado, las llamadas a play() son ignoradas.
     * @param estado true para habilitar sonidos, false para deshabilitarlos
     * @pre Ninguna
     * @post habilitado == estado
     */
    public static void habilitar(boolean estado) {
        habilitado = estado;
    }


    /**
     * Reproduce un efecto de sonido por nombre de archivo.
     * Si el archivo no está en caché, lo carga desde /audio/nombre.
     * Si los sonidos están deshabilitados, no reproduce nada.
     * Si el archivo no existe, registra un error y continúa sin excepción.
     * @param nombre Nombre del archivo de audio (ej: "pop.mp3", "click.wav")
     * @pre nombre != null && !nombre.isEmpty()
     * @post Si habilitado && archivo existe: sonido reproducido
     *       Si archivo no existe: error registrado en consola
     *       Si no habilitado: no se reproduce nada
     */
    public static void play(String nombre) {
        if (!habilitado) return;

        if (!clips.containsKey(nombre)) {
            URL url = SoundEffects.class.getResource("/audio/" + nombre);
            if (url == null) {
                System.err.println("No se encontró el sonido: " + nombre);
                return;
            }
            AudioClip clip = new AudioClip(url.toString());
            clip.setVolume(volumen); // volumen actual
            clips.put(nombre, clip);
        }
        clips.get(nombre).play();
    }
}
