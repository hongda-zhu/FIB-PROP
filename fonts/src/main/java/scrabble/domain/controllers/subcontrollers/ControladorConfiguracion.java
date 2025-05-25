package scrabble.domain.controllers.subcontrollers;

import java.util.HashMap;
import java.util.Map;

import scrabble.domain.models.Configuracion;
import scrabble.domain.persistences.implementaciones.RepositorioConfiguracionImpl;
import scrabble.domain.persistences.interfaces.RepositorioConfiguracion;
import scrabble.excepciones.ExceptionPersistenciaFallida;

/**
 * Controlador para la gestión de la configuración de la aplicación.
 * Encapsula el acceso a los parámetros configurables como tema y volumen.
 */
public class ControladorConfiguracion {
    /**
     * Repositorio para manejar la persistencia de la configuración.
     */
    private RepositorioConfiguracion repositorio;

    /**
     * Constructor para la clase ControladorConfiguracion.
     * Inicializa una nueva instancia de Configuracion y carga los valores guardados si existen.
     *
     * @pre No hay precondiciones específicas.
     * @post Se crea una instancia de {@code ControladorConfiguracion} y se inicializa con la configuración
     *       cargada desde el repositorio por defecto (o una nueva si no hay datos guardados).
     */
    public ControladorConfiguracion() {
        this(new RepositorioConfiguracionImpl());
    }

    /**
     * Constructor para la clase ControladorConfiguracion con inyección de repositorio.
     * Permite especificar el repositorio a utilizar para pruebas o diferentes implementaciones.
     *
     * @param repositorio El repositorio a utilizar para la persistencia
     * @pre El repositorio no debe ser null.
     * @post Se crea una instancia de {@code ControladorConfiguracion} y se inicializa con la configuración
     *       cargada desde el repositorio especificado (o una nueva si no hay datos guardados).
     * @throws NullPointerException si el repositorio es null
     */
    public ControladorConfiguracion(RepositorioConfiguracion repositorio) {
        if (repositorio == null) {
            throw new NullPointerException("El repositorio no puede ser null");
        }
        this.repositorio = repositorio;
    }

    /**
     * Obtiene el tema visual actual configurado en la aplicación.
     *
     * @pre No hay precondiciones específicas.
     * @return Tema actual
     * @post Se devuelve un String que representa el tema visual actual configurado en la aplicación,
     *       obtenido del objeto Configuracion cargado.
     */
    public String obtenerTema() {
        return this.repositorio.cargar().getTema();
    }

    /**
     * Obtiene el volumen actual configurado en la aplicación.
     *
     * @pre No hay precondiciones específicas.
     * @return Volumen actual
     * @post Se devuelve un entero que representa el nivel de volumen actual configurado en la aplicación.
     */
    // public int obtenerVolumen() {
    //     return this.repositorio.cargar().getVolumen();
    // }

    /**
     * Obtiene el tema visual actual configurado en la aplicación.
     *
     * @pre No hay precondiciones específicas.
     * @return Diccionario actual
     * @post Se devuelve un String que representa el diccionario actual configurado en la aplicación.
     */
    public String obtenerDiccionario() {
        return this.repositorio.cargar().getDiccionario();
    }

    /**
     * Obtiene el tamano actual configurado en la aplicación.
     *
     * @pre No hay precondiciones específicas.
     * @return Tamano actual
     * @post Se devuelve un entero que representa el tamano actual configurado en la aplicación.
     */
    public int obtenerTamano() {
        return this.repositorio.cargar().getTamano();
    }

    /**
     * Obtiene si la música está activado en la aplicación.
     *
     * @pre No hay precondiciones específicas.
     * @return True si la música está activado, en caso contrario devuelve false
     * @post Se devuelve un valor booleano que indica si la música está activada,
     *       obtenido del objeto Configuracion cargado.
     */
    public boolean isMusica() {
        return this.repositorio.cargar().isMusica();
    }

    /**
     * Obtiene si el sonido está activado en la aplicación.
     *
     * @pre No hay precondiciones específicas.
     * @return True si el sonido está activado, en caso contrario devuelve false
     * @post Se devuelve un valor booleano que indica si el sonido está activado,
     *       obtenido del objeto Configuracion cargado.
     */
    public boolean isSonido() {
        return this.repositorio.cargar().isSonido();
    }

    /**
     * Obtiene el volumen de la música de la aplicación.
     *
     * @pre No hay precondiciones específicas.
     * @return Volumen de música
     * @post Se devuelve un entero que representa el volumen de la música,
     *       obtenido del objeto Configuracion cargado.
     */
    public int getVolumenMusica() {
        return this.repositorio.cargar().getVolumenMusica();
    }

    /**
     * Obtiene el volumen del sonido de la aplicación.
     *
     * @pre No hay precondiciones específicas.
     * @return Volumen del sonido
     * @post Se devuelve un entero que representa el volumen del sonido,
     *       obtenido del objeto Configuracion cargado.
     */
    public int getVolumenSonido() {
        return this.repositorio.cargar().getVolumenSonido();
    }


    /**
     * Establece un nuevo tema visual para la configuración.
     *
     * @pre El tema a establecer no es null.
     * @param t Tema a establecer
     * @post El tema de la configuración se actualiza al valor especificado y
     *       la configuración se guarda persistentemente.
     * @throws ExceptionPersistenciaFallida Si ocurre un error al guardar la configuración.
     * @throws NullPointerException si el parámetro t es null
     */
    public void setTema(String t) throws ExceptionPersistenciaFallida {
        Configuracion configuracion = repositorio.cargar();
        configuracion.setTema(t);
        repositorio.guardar(configuracion);
    }

    /**
     * Establece un nuevo nivel de volumen para la configuración.
     *
     * @pre El volumen debe estar en el rango permitido.
     * @param v Volumen a establecer
     * @post El nivel de volumen de la configuración se actualiza al valor especificado y se guarda la configuración.
     * @throws IllegalArgumentException si el volumen está fuera del rango permitido
     */
    // public void setVolumen(int v) throws ExceptionPersistenciaFallida {
    //     Configuracion configuracion = repositorio.cargar();
    //     configuracion.setVolumen(v);
    //     repositorio.guardar(configuracion);
    // }

    /**
     * Establece un nuevo diccionario  para la configuración.
     *
     * @pre El diccionario a establecer no es null.
     * @param d Diccionario a establecer
     * @post El diccionario de la configuración se actualiza al valor especificado y
     *       la configuración se guarda persistentemente.
     * @throws ExceptionPersistenciaFallida Si ocurre un error al guardar la configuración.
     * @throws NullPointerException si el parámetro d es null
     */
    public void setDiccionario(String d) throws ExceptionPersistenciaFallida {
        Configuracion configuracion = repositorio.cargar();
        configuracion.setDiccionario(d);
        repositorio.guardar(configuracion);
    }

    /**
     * Establece un nuevo tamano para la configuración.
     *
     * @pre El tamaño a establecer es un entero.
     * @param t Tamano a establecer
     * @post El tamaño del tablero en la configuración se actualiza al valor especificado y
     *       la configuración se guarda persistentemente.
     * @throws ExceptionPersistenciaFallida Si ocurre un error al guardar la configuración.
     */
    public void setTamano(int t) throws ExceptionPersistenciaFallida {
        Configuracion configuracion = repositorio.cargar();
        configuracion.setTamano(t);
        repositorio.guardar(configuracion);
    }

    /**
     * Establece el estado de la música.
     *
     * @pre No hay precondiciones específicas.
     * @param b True or false
     * @post El estado de la música en la configuración se actualiza al valor especificado y
     *       la configuración se guarda persistentemente.
     * @throws ExceptionPersistenciaFallida Si ocurre un error al guardar la configuración.
     */
    public void setMusica(boolean b) throws ExceptionPersistenciaFallida {
        Configuracion configuracion = repositorio.cargar();
        configuracion.setMusica(b);
        repositorio.guardar(configuracion);
    }

    /**
     * Establece un nuevo volumen de música para la configuración.
     *
     * @pre El volumen a establecer es un entero en el rango permitido por el modelo Configuracion.
     * @param t Volumen a establecer
     * @post El volumen de la música en la configuración se actualiza al valor especificado y
     *       la configuración se guarda persistentemente.
     * @throws ExceptionPersistenciaFallida Si ocurre un error al guardar la configuración.
     * @throws IllegalArgumentException si el volumen está fuera del rango permitido por el modelo Configuracion.
     */
    public void setVolumenMusica(int t) throws ExceptionPersistenciaFallida {
        Configuracion configuracion = repositorio.cargar();
        configuracion.setVolumenMusica(t);
        repositorio.guardar(configuracion);
    }

    /**
     * Establece el estado del sonido.
     *
     * @pre No hay precondiciones específicas.
     * @param b True or false
     * @post El estado del sonido en la configuración se actualiza al valor especificado y
     *       la configuración se guarda persistentemente.
     * @throws ExceptionPersistenciaFallida Si ocurre un error al guardar la configuración.
     */
    public void setSonido(boolean b) throws ExceptionPersistenciaFallida {
        Configuracion configuracion = repositorio.cargar();
        configuracion.setSonido(b);
        repositorio.guardar(configuracion);
    }

    /**
     * Establece un nuevo volumen de sonido para la configuración.
     *
     * @pre El volumen a establecer es un entero en el rango permitido por el modelo Configuracion.
     * @param t Volumen a establecer
     * @post El volumen del sonido en la configuración se actualiza al valor especificado y
     *       la configuración se guarda persistentemente.
     * @throws ExceptionPersistenciaFallida Si ocurre un error al guardar la configuración.
     * @throws IllegalArgumentException si el volumen está fuera del rango permitido por el modelo Configuracion.
     */
    public void setVolumenSonido(int t) throws ExceptionPersistenciaFallida {
        Configuracion configuracion = repositorio.cargar();
        configuracion.setVolumenSonido(t);
        repositorio.guardar(configuracion);
    }

    /**
     * Carga la configuración desde el repositorio.
     *
     * @pre El repositorio debe estar inicializado.
     * @return {@code true} si la configuración se cargó exitosamente (si hay datos guardados), {@code false} en caso contrario.
     * @throws ExceptionPersistenciaFallida Si ocurre un error durante la carga.
     * @post La configuración del objeto actual se actualiza con los datos cargados desde el repositorio si existen;
     *       si no hay datos, se inicializa con valores por defecto.
     */
    public boolean cargarConfiguracion() throws ExceptionPersistenciaFallida {
         Configuracion loadedConfig = repositorio.cargar();
         // La lógica interna del repositorio.cargar ya maneja si el archivo no existe
         // Aquí simplemente confirmamos si se cargó algo (aunque cargar siempre devuelve un objeto, nuevo o no)
         // Podríamos verificar si loadedConfig tiene valores por defecto vs. cargados, pero es más complejo
         // Una pre-condición más precisa es que el repositorio esté inicializado.
         return loadedConfig != null; // Siempre debería ser true si repositorio.cargar() devuelve un objeto Configuracion
    }

    /**
     * Guarda la configuración general de la aplicación en un archivo de propiedades.
     *
     * @pre El tema, musicaActivada, sonidoActivado, volumenMusica y volumenSonido son valores que pueden ser procesados por los métodos set correspondientes.
     * @param tema             El tema visual seleccionado por el usuario.
     * @param musicaActivada   Indica si la música está activada (true) o desactivada (false).
     * @param sonidoActivado   Indica si los efectos de sonido están activados (true) o desactivados (false).
     * @param volumenMusica    El volumen establecido de la música.
     * @param volumenSonido    El volumen establecido del sonido.
     *
     * Este método almacena los valores proporcionados en un archivo de configuración,
     * sobrescribiendo cualquier configuración previa. Si ocurre un error de entrada/salida
     * durante el proceso de guardado, se imprime la traza de la excepción.
     *
     * @post La configuración (tema, estado de música/sonido, volumen de música/sonido)
     *       se intenta actualizar y guardar persistentemente mediante llamadas individuales a los métodos set.
     *       En caso de error de persistencia en cualquier llamada set, se imprime la traza de la excepción.
     */
    public void guardarConfiguracionGeneral(String tema, boolean musicaActivada, boolean sonidoActivado, int volumenMusica, int volumenSonido) {
        try {
            setTema(tema);
            setMusica(musicaActivada);
            setSonido(sonidoActivado);
            setVolumenMusica(volumenMusica);
            setVolumenSonido(volumenSonido);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Devuelve un mapa que contiene las claves y valores de configuración cargados.
     *
     * @pre No hay precondiciones específicas.
     * @return Un mapa que contiene las claves ('tema', 'musica', 'sonido', 'volumenMusica', 'volumenSonido')
     *         y sus valores de configuración cargados.
     * @post Se devuelve un mapa no nulo con la información de configuración actual.
     */
    public Map<String, String> getConfMap() {
        Configuracion c = repositorio.cargar();
        Map<String, String> m = new HashMap<>();
        m.put("tema", c.getTema());
        m.put("musica", String.valueOf(c.isMusica()));
        m.put("sonido", String.valueOf(c.isSonido()));
        m.put("volumenMusica", String.valueOf(c.getVolumenMusica()));
        m.put("volumenSonido", String.valueOf(c.getVolumenSonido()));
        return m;
    }
}