package scrabble.domain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import scrabble.domain.controllers.ControladorDomain;
import scrabble.excepciones.ExceptionDiccionarioExist;
import scrabble.excepciones.ExceptionLanguageNotExist;
import scrabble.excepciones.ExceptionPalabraInvalida;
import scrabble.excepciones.ExceptionPersistenciaFallida;
import scrabble.excepciones.ExceptionRankingOperationFailed;
import scrabble.excepciones.ExceptionUserEsIA;
import scrabble.excepciones.ExceptionUserExist;
import scrabble.excepciones.ExceptionUserInGame;
import scrabble.excepciones.ExceptionUserLoggedIn;
import scrabble.excepciones.ExceptionUserNotExist;
import scrabble.helpers.Dificultad;
import scrabble.helpers.Direction;
import scrabble.helpers.Triple;
import scrabble.helpers.Tuple;

/**
 * Clase principal para probar el dominio de Scrabble.
 * Proporciona una interfaz de línea de comandos para interactuar con las funcionalidades del juego.
 * 
 */
public class DomainDriver {

    static ControladorDomain controladorDomain = new ControladorDomain();
    static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    static HashMap<String, String> menus = new HashMap<>();
    static String command;
    static boolean readingFile;   
    
    public static void main(String[] args) throws IOException {
        // Registrar shutdown hook para eliminar persistencias cuando se cierra la aplicación inesperadamente
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nLimpiando archivos de persistencia antes de cerrar...");
            controladorDomain.limpiarPersistencias();
        }));

        initializeMenus();
        initializeDefaultSettings();
        
        controladorDomain = new ControladorDomain();
        
        // Verificar diccionarios al iniciar
        verificarDiccionariosExistentes();
        
        if (!readingFile) {
            System.out.println("\nBienvenido al Driver del Dominio.");
            System.out.println("Escriba 'help' para ver los comandos disponibles");
            System.out.println("Escriba 'quit' para salir");
        }
        
        // Inicializamos el sistema con los ajustes predeterminados solo una vez
        menus();
    }
    
    public static String readLine() throws IOException {
        String line = reader.readLine();
        if (readingFile) {
            if (line == null) {
                readingFile = false;
                System.out.println("Terminada la lectura del archivo");
                reader = new BufferedReader(new InputStreamReader(System.in));
                line = reader.readLine();
            }
            else {
                System.out.println(">" + line);
            }
        }
        return line;
    }

    // Ejecución principal
    private static void menus() throws IOException {
        initializeMenus();
        initializeDefaultSettings();
        ShowMenu("principal");
        readingFile = false;
        boolean successful;
        
        while ((command = readLine()) != null) {
            boolean menu = false;
            try {
                successful = true;
                command = command.toLowerCase();
                command = command.stripTrailing();
                
                switch (command) {
                    case "0":
                    case "salir":
                        // Limpiar persistencias antes de salir
                        controladorDomain.limpiarPersistencias();
                        System.exit(0);
                        break;
                    
                    case "1":
                        menu = true;
                        manageUserMenu();
                        break;
                    
                    case "2":
                        menu = true;
                        manageDiccionaryMenu();
                        break;

                    case "3":
                        menu = true;
                        managePartidaMenu();
                        break;
                        
                    case "4":
                        menu = true;
                        manageRankingMenu();
                        break;
                        
                    case "5":
                        menu = true;
                        manageConfiguracionMenu();
                        break;
                    case "6":
                        System.out.println("Introduce la ruta del archivo que se cargará para procesar comandos (directorio actual: " + System.getProperty("user.dir") + "): ");
                        String rutaArchivo = readLine();
                        reader = new BufferedReader(new FileReader(rutaArchivo));
                        readingFile = true;
                        break;                    
                    case "help":
                    case "ayuda":
                        ShowMenu("ayuda");
                        menu = true;
                        break;

                    case "exit":
                        System.exit(0);
                        break;
                    
                    
                    // Aquí iría la implementación de los comandos específicos para cada funcionalidad
                    // Por ejemplo: registrar usuario, crear diccionario, iniciar partida, etc.
                    default:
                        System.out.println("Comando no encontrado. Escribe 'ayuda' para ver los comandos disponibles.");
                        successful = false;
                        break;
                }
                
                if (!menu && successful) {
                    System.out.println("Finalizada la ejecución del comando '" + command + "'. Volviendo al menú principal...");
                }
    
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
            
            System.err.flush();
            if (!menu) ShowMenu("principal");
        }
        Scanner scanner = new Scanner(System.in);
        scanner.close(); // Add this line at the end of the method
    }

    public static Triple<String, Tuple<Integer, Integer>, Direction> jugarTurno() {
        Scanner scanner = new Scanner(System.in);
        String palabra = "";
        // Leer la palabra

        System.out.print("Introduce la palabra a colocar ('P' para pasar, 'X' para el menu, 'CF' para cambiar fichas): ");
        

        System.out.println("Coloca una palabra en el tablero.");
        palabra = scanner.nextLine().toUpperCase();
        
        if (palabra.equals("X")) {
            return new Triple<String,Tuple<Integer,Integer>,Direction>("X", null, null);
        }
        
        // Si el usuario escribe 'p', retornar null
        if (palabra.equals("P")) {
            return new Triple<String,Tuple<Integer,Integer>,Direction>("P", null, null);
        }

        if (palabra.equals("CF")){
            return new Triple<String,Tuple<Integer,Integer>,Direction>("CF", null, null);
        }
        
        
        // Leer la posición de la última letra (coordenada X e Y)
        int x = -1;
        int y = -1;
        boolean coordenadasValidas = false;
        
        while (!coordenadasValidas) {
            System.out.print("Introduce las coordenadas (a b (eje vertical/ eje horizontal)) de la última letra de tu palabra: ");
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
            System.out.print("Introduce la orientación de tu palabra 'H' (horizontal) o 'V' (vertical): ");
            dir = scanner.nextLine().toUpperCase();
            
            if (dir.equals("H") || dir.equals("V")) {
                direccionValida = true;
                Direction direction = dir.equals("H") ? Direction.HORIZONTAL : Direction.VERTICAL;
                
                // Devolver la respuesta en formato Triple
                return new Triple<>(palabra, new Tuple<>(x, y), direction);
            } else {
                System.out.println("Dirección no válida. Debe ser HORIZONTAL o VERTICAL.");
            }
        }
        
        // Este return nunca debería alcanzarse, pero es necesario para la compilación
        return null;
    }


    public static void managePartidaIniciar(String idiomaSeleccionado, Map<String, Integer> jugadoresSeleccionados, Integer N, boolean cargado) throws IOException, ExceptionPersistenciaFallida{

        if (!cargado) controladorDomain.managePartidaIniciar(idiomaSeleccionado, jugadoresSeleccionados, N);

        while (!controladorDomain.isJuegoTerminado()) {

            for (String nombreJugador : jugadoresSeleccionados.keySet()) {

                System.out.println(controladorDomain.mostrarStatusPartida(nombreJugador));
                System.out.println(controladorDomain.mostrarRack(nombreJugador));

                Triple<String, Tuple<Integer, Integer>, Direction> result = new Triple<String,Tuple<Integer,Integer>,Direction>("IA", null, null);

                if (!controladorDomain.esIA(nombreJugador)) {
                    boolean jugadaValida = false;
                    
                    while (!jugadaValida) {
                        result = jugarTurno();
                        if (result.x == "X") {
                            juegoPausado();
                            System.out.println(controladorDomain.mostrarStatusPartida(nombreJugador));
                            System.out.println(controladorDomain.mostrarRack(nombreJugador));
                        } else if (result.x == "P") {
                            System.out.println("El jugador ha decidido pasar su turno.");
                            jugadaValida = true;
                        } else if (result.x == "CF") {
                            System.out.println(controladorDomain.mostrarRack(nombreJugador));
                            List<String> fichasCambiar = pedirFichasCambiar(controladorDomain.getRack(nombreJugador));
                            controladorDomain.intercambiarFichas(nombreJugador, fichasCambiar);
                            System.out.println("El jugador ha decidido cambiar fichas.");
                            System.out.println("Tu nuevo rack: ");

                            System.out.println(controladorDomain.mostrarRack(nombreJugador));
                            jugadaValida = true;
                        } else {
                            if (!controladorDomain.isValidMove(result, controladorDomain.getRack(nombreJugador))) {
                                System.out.println("Movimiento inválido. Intenta de nuevo.");
                            } else {
                                jugadaValida = true;
                            }
                        }
                    }
                }
                int puntoGanados = controladorDomain.realizarTurnoPartida(nombreJugador, result);
                controladorDomain.actualizarJugadores(nombreJugador, puntoGanados);

            }
            controladorDomain.comprobarFinPartida(jugadoresSeleccionados);
        }
        System.out.println(controladorDomain.finalizarJuego(controladorDomain.getJugadoresActuales()));
    }  

    private static List<String> pedirFichasCambiar(Map<String,Integer> rack) {
        List<String> fichasCambiar = new ArrayList<>();
        System.out.println("Introduce las letras que quieres cambiar (separadas por espacios): ");
        String input = null;
        try {
            input = readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        String[] letras = input.split(" ");
        for (String letra : letras) {
            if (rack.containsKey(letra)) {
                fichasCambiar.add(letra);
            } else {
                System.out.println("La letra " + letra + " no está en tu atril. Omitida.");
            }
        }
        return fichasCambiar;
    }

    public static void juegoPausado() throws IOException {
        System.out.println("Juego pausado. Selecciona una opción del menú.");
        ShowMenu("partidapausada");
        boolean continuar = false;
        String command = readLine();
        while(!continuar) {
            switch (command) {
                case "1":
                    System.out.println("+--------------------------------------+");
                    System.out.println("| CONTINUAR JUEGO                      |");
                    System.out.println("+--------------------------------------+");
                    System.out.println("| Volviendo al juego...                |");
                    System.out.println("+--------------------------------------+");
                    continuar = true;
                    break;

                case "2":
                    System.out.println("+--------------------------------------+");
                    System.out.println("| SALIR DEL JUEGO                      |");
                    System.out.println("+--------------------------------------+");
                    System.out.println("| Partida no guardada.                 |");
                    System.out.println("| ¿Quieres guardar la partida? (s/n)   |");
                    System.out.println("+--------------------------------------+");

                    String respuesta = readLine();
                    if (respuesta.equalsIgnoreCase("s")) {
                        System.out.println("+--------------------------------------+");
                        System.out.println("| GUARDANDO PARTIDA                    |");
                        System.out.println("+--------------------------------------+");
                        controladorDomain.guardarPartida();
                        System.out.println("| Partida guardada correctamente.      |");
                        System.out.println("+--------------------------------------+");
                    } else {
                        System.out.println("+--------------------------------------+");
                        System.out.println("| PARTIDA NO GUARDADA                  |");
                        System.out.println("+--------------------------------------+");
                        controladorDomain.aliberarJugadoresActuales();
                    }
                    menus();
                    break;
                default:
                    System.out.println("Opción no válida. Intenta de nuevo.");
            }
        }
    }
        
      

    public static void initializeDefaultSettings() {
        String resourcesPath = "src/main/resources/diccionarios/";
        java.io.File resourcesDir = new java.io.File(resourcesPath);

        if (resourcesDir.exists() && resourcesDir.isDirectory()) {
            java.io.File[] directories = resourcesDir.listFiles(java.io.File::isDirectory);

            if (directories != null) {
                for (java.io.File dir : directories) {
                    String languageName = dir.getName();
                    java.io.File alphaFile = new java.io.File(dir, "alpha.txt");
                    java.io.File wordsFile = new java.io.File(dir, "words.txt");

                    if (alphaFile.exists() && wordsFile.exists()) {
                        try {
                            if (!controladorDomain.existeLenguaje(languageName)) {
                                controladorDomain.anadirLenguaje(languageName, alphaFile.getPath(), wordsFile.getPath());
                            }
                        } catch (Exception e) {
                            System.err.println("Error al cargar el lenguaje '" + languageName + "': " + e.getMessage());
                        }
                    } else {
                        System.err.println("Faltan archivos en el directorio '" + dir.getName() + "'. Se requieren 'alpha.txt' y 'words.txt'.");
                    }
                }
            }
        } else {
            System.err.println("El directorio de recursos no existe o no es válido: " + resourcesPath);
        }
        
        // Registrar algunos usuarios por defecto para testing solo si no existen
        if (!controladorDomain.existeJugador("xuanyi")) controladorDomain.registrarUsuario("xuanyi");
        if (!controladorDomain.existeJugador("jiahao")) controladorDomain.registrarUsuario("jiahao");
        if (!controladorDomain.existeJugador("admin")) controladorDomain.registrarUsuario("admin");
        if (!controladorDomain.existeJugador("songhe")) controladorDomain.registrarUsuario("songhe");
        if (!controladorDomain.existeJugador("hongda")) controladorDomain.registrarUsuario("hongda");
        controladorDomain.crearJugadorIA(Dificultad.FACIL, "DummyEZ");
        controladorDomain.crearJugadorIA(Dificultad.DIFICIL, "DummyHardCore");
        // Verificamos si hay datos en el ranking antes de inicializar
        if (!hayDatosEnRanking()) {
            initializeRankingData();
        }
    }


    private static void initializeRankingData() {
        System.out.println("Inicializando datos de ranking para demostración...");
        
        // Limpiamos cualquier dato previo que pudiera existir
        List<String> usuarios = controladorDomain.getUsuariosRanking();
        for (String usuario : usuarios) {
            controladorDomain.eliminarUsuarioRanking(usuario);
        }
        
        // Creamos algunos usuarios para el ranking si no existen
        String[] jugadoresMuestra = {"hongda", "xuanyi", "jiahao", "songhe"};
        for (String jugador : jugadoresMuestra) {
            if (!controladorDomain.existeJugador(jugador)) {
                controladorDomain.registrarUsuario(jugador);
            }
        }
        
        // Simulamos varias partidas con diferentes resultados
        
        // Partida 1: hongda gana
        Map<String, Integer> puntuacionesPartida1 = new HashMap<>();
        puntuacionesPartida1.put("hongda", 120);
        puntuacionesPartida1.put("xuanyi", 85);
        puntuacionesPartida1.put("jiahao", 90);
        List<String> ganadoresPartida1 = Collections.singletonList("hongda");
        controladorDomain.finalizarPartidaJugadoresMultiple(puntuacionesPartida1, ganadoresPartida1);
        
        // Partida 2: jiahao gana
        Map<String, Integer> puntuacionesPartida2 = new HashMap<>();
        puntuacionesPartida2.put("hongda", 75);
        puntuacionesPartida2.put("jiahao", 130);
        puntuacionesPartida2.put("songhe", 60);
        List<String> ganadoresPartida2 = Collections.singletonList("jiahao");
        controladorDomain.finalizarPartidaJugadoresMultiple(puntuacionesPartida2, ganadoresPartida2);
        
        // Partida 3: xuanyi gana
        Map<String, Integer> puntuacionesPartida3 = new HashMap<>();
        puntuacionesPartida3.put("xuanyi", 110);
        puntuacionesPartida3.put("jiahao", 95);
        puntuacionesPartida3.put("songhe", 85);
        List<String> ganadoresPartida3 = Collections.singletonList("xuanyi");
        controladorDomain.finalizarPartidaJugadoresMultiple(puntuacionesPartida3, ganadoresPartida3);
        
        // Partida 4: hongda y songhe empatan (ambos ganan)
        Map<String, Integer> puntuacionesPartida4 = new HashMap<>();
        puntuacionesPartida4.put("hongda", 100);
        puntuacionesPartida4.put("xuanyi", 80);
        puntuacionesPartida4.put("songhe", 100);
        List<String> ganadoresPartida4 = Arrays.asList("hongda", "songhe");
        controladorDomain.finalizarPartidaJugadoresMultiple(puntuacionesPartida4, ganadoresPartida4);

        // Partida 5: hongda y songhe empatan (ambos ganan)
        Map<String, Integer> puntuacionesPartida5 = new HashMap<>();
        puntuacionesPartida5.put("jiahao", 1000);
        puntuacionesPartida5.put("xuanyi", 80);
        puntuacionesPartida5.put("songhe", 2000);
        List<String> ganadoresPartida5 = Arrays.asList("jiahao", "songhe");
        controladorDomain.finalizarPartidaJugadoresMultiple(puntuacionesPartida5, ganadoresPartida5);

        // Partida 6: hongda y songhe empatan (ambos ganan)
        Map<String, Integer> puntuacionesPartida6 = new HashMap<>();
        puntuacionesPartida6.put("jiahao", 1000);
        puntuacionesPartida6.put("xuanyi", 40);
        puntuacionesPartida6.put("songhe", 100);
        List<String> ganadoresPartida6 = Arrays.asList("jiahao");
        controladorDomain.finalizarPartidaJugadoresMultiple(puntuacionesPartida6, ganadoresPartida6);
        
        System.out.println("Datos de ranking inicializados correctamente.");
        
        // Mostrar un resumen de las estadísticas generadas
        System.out.println("\nResumen de estadísticas generadas:");
        System.out.println("----------------------------------");
        System.out.println("Jugador | Partidas | Victorias | Puntuación Total | Máxima | Media");
        System.out.println("----------------------------------------------------------");
        
        for (String jugador : jugadoresMuestra) {
            int partidas = controladorDomain.getPartidasJugadas(jugador);
            int victorias = controladorDomain.getVictorias(jugador);
            int puntuacionTotal = controladorDomain.getPuntuacionTotal(jugador);
            int puntuacionMaxima = controladorDomain.getPuntuacionMaxima(jugador);
            double puntuacionMedia = controladorDomain.getPuntuacionMedia(jugador);
            
            System.out.printf("%-7s | %-8d | %-9d | %-15d | %-6d | %.2f%n", 
                              jugador, partidas, victorias, puntuacionTotal, puntuacionMaxima, puntuacionMedia);
        }
        System.out.println("----------------------------------------------------------");
    }
    
    /**
     * Comprueba si ya existen datos en el ranking
     * 
     * @return true si hay datos, false si no hay
     */
    private static boolean hayDatosEnRanking() {
        List<String> usuarios = controladorDomain.getUsuariosRanking();
        if (usuarios.isEmpty()) {
            return false;
        }
        
        // Comprobamos si al menos un usuario tiene partidas jugadas
        for (String usuario : usuarios) {
            if (controladorDomain.getPartidasJugadas(usuario) > 0) {
                return true;
            }
        }
        
        return false;
    }

    public static void initializeMenus() {
        menus.put("ayuda", """
                                    +------------------------------------------------------------------------------+
                                    | AYUDA | Escribe '0' para volver al menú principal.                           |
                                    |                                                                              |
                                    |  Ejecuta cualquiera de los comandos disponibles escribiéndolos y pulsando    |
                                    |  enter. Después, sigue las instrucciones del comando.                        |
                                    |                                                                              |
                                    |  Los menús son únicamente informativos. Consúltalos para ver los comandos    |
                                    |  disponibles. Puedes ejecutar cualquier comando desde cualquier parte del    |
                                    |  programa.                                                                   |
                                    |                                                                              |
                                    |  El juego Scrabble consiste en formar palabras en un tablero, utilizando     |
                                    |  fichas con letras. Cada jugador dispone de un soporte con 7 fichas que      |
                                    |  puede usar para formar palabras en cada turno.                              |
                                    |                                                                              |
                                    |  Para más información sobre las reglas del juego y cómo jugar, consulta el   |
                                    |  manual de usuario.                                  |
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);
        
        // Menús principales
        menus.put("principal", """
                                    \n+------------------------------------------------------------------------------+
                                    | MENÚ PRINCIPAL |                                                             |
                                    +------------------------------------------------------------------------------+
                                    |                 Selecciona un menú o ejecuta un comando.                     |
                                    |                                                                              |
                                    |                           [ 1 ] Gestión de Jugadores                         |
                                    |                           [ 2 ] Gestión de Diccionarios                      |
                                    |                           [ 3 ] Gestión de Partidas                          |
                                    |                           [ 4 ] Gestión de Rankings                          |
                                    |                           [ 5 ] Gestión de Configuración                     |
                                    |                           [ 6 ] Ejecutar Archivo                             |
                                    |                           [ 0 ] Salir                                        |
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);
        
        menus.put("usuario", """
                                    +------------------------------------------------------------------------------+
                                    | GESTIÓN DE JUGADORES |                                                       |
                                    |                                                                              |
                                    |   [ 1 ] Crear jugador              - Crea un nuevo jugador en el sistema.    |
                                    |   [ 2 ] Eliminar jugador           - Elimina un jugador existente.           |
                                    |   [ 3 ] Mostrar jugadores          - Muestra todos los jugadores registrados.|
                                    |   [ 0 ] Volver                     - Vuelve al Menú Principal.               | 
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);

        menus.put("usuarioCrear", """
                                    +------------------------------------------------------------------------------+
                                    | GESTIÓN DE JUGADORES > CREAR JUGADOR                                         |
                                    |                                                                              |
                                    |   ¿Cómo crear un nuevo jugador?                                              |
                                    |   Introduce un nombre de jugador                                             |
                                    |                                                                              |
                                    |   [ 1 ] Crear jugador              - Crea un nuevo jugador en el sistema.    |
                                    |   [ 2 ] Ver jugadores              - Muestra la lista de jugadores existentes|
                                    |   [ 0 ] Volver                     - Vuelve a Gestión de Jugadores.          |
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);

        menus.put("usuarioEliminar", """
                                    +------------------------------------------------------------------------------+
                                    | GESTIÓN DE JUGADORES > ELIMINAR JUGADOR                                      |
                                    |                                                                              |
                                    |   ¿Cómo eliminar jugador?                                                    |
                                    |   Introduce un nombre de jugador al que quiere eliminar                      |
                                    |                                                                              |
                                    |   [ 1 ] Eliminar jugador           - Elimina un jugador existente.           |
                                    |   [ 2 ] Ver jugadores              - Lista los jugadores disponibles.        |
                                    |   [ 0 ] Volver                     - Vuelve a Gestión de Jugadores.          |
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);
                                                                                                                                                                                                                                                                                    
                                                                                                                                                                                                                          
        // 2. Diccionarios
        menus.put("diccionario", """
                                    +------------------------------------------------------------------------------+
                                    | GESTIÓN DE DICCIONARIOS |                                                    |
                                    |                                                                              |
                                    |   [ 1 ] Crear diccionario          - Crea un nuevo diccionario.              |
                                    |   [ 2 ] Eliminar diccionario       - Elimina un diccionario existente.       |
                                    |   [ 3 ] Importar diccionario       - Importa un diccionario desde un archivo.|
                                    |   [ 4 ] Modificar diccionario      - Añade o elimina palabras de un dicc.    |
                                    |   [ 5 ] Mostrar diccionarios       - Muestra los diccionarios disponibles.   |
                                    |                                                                              |
                                    |   [ 0 ] Volver                     - Vuelve al Menú Principal.               | 
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);  

        menus.put("diccionarioCrear", """
                            +------------------------------------------------------------------------------+
                            | GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO                                  |
                            +------------------------------------------------------------------------------+
                            |   ¿Cómo crear nuevo diccionario?                                             |
                            |   Seleccione la opción "1"                                                   |
                            |   Introduzca el nombre del nuevo diccionario                                 |
                            |   Introduzca la ruta en la que se encuentra el alfabeto                      | 
                            |   Introduzca la ruta en la que se encuentra el diccionario                   | 
                            |                                                                              |  
                            |   Ejemplo:                                                                   |      
                            |                                                                              |                                                                                                    |
                            |   [ 1 ] Comenzar                   - Comenzar a introducir datos             |
                            |   [ 0 ] Volver                     - Vuelve a Gestión de Diccionarios.       | 
                            |                                                                              |
                            +------------------------------------------------------------------------------+
                            """);

        menus.put("diccionarioSeleccionar", """
                            +------------------------------------------------------------------------------+
                            | GESTIÓN DE DICCIONARIOS > SELECCIONAR DICCIONARIO                            |
                            +------------------------------------------------------------------------------+
                            |   ¿Cómo seleccionar diccionario?                                             |
                            |   Seleccione la opción "1"                                                   |
                            |   Introduzca el nombre del nuevo diccionario                                 |
                            |   Introduzca la ruta en la que se encuentra el alfabeto                      | 
                            |   Introduzca la ruta en la que se encuentra el diccionario                   | 
                            |                                                                              |  
                            |   Ejemplo:                                                                   |      
                            |                                                                              |                                                                                                    |
                            |   [ 1 ] Comenzar                   - Comenzar a introducir datos             |
                            |   [ 0 ] Volver                     - Vuelve a Gestión de Diccionarios.       | 
                            |                                                                              |
                            +------------------------------------------------------------------------------+
                            """);                            

        menus.put("diccionarioImportar", """
                            +------------------------------------------------------------------------------+
                            | GESTIÓN DE DICCIONARIOS > IMPORTAR DICCIONARIO                               |
                            +------------------------------------------------------------------------------+
                            | ¿Cómo importar nuevo diccionario?                                            |
                            |   Seleccione la opción "1"                                                   |
                            |   Introduzca el nombre del nuevo diccionario                                 |
                            |   Introduzca la ruta en la que se encuentra el alfabeto                      | 
                            |   Introduzca la ruta en la que se encuentra el diccionario                   | 
                            |                                                                              |  
                            |   Ejemplo:                                                                   |      
                            |                                                                              |                                                                                                    |
                            |   [ 1 ] Comenzar                   - Comenzar a introducir datos             |
                            |   [ 0 ] Volver                     - Vuelve a Gestión de Diccionarios.       | 
                            |                                                                              |
                            +------------------------------------------------------------------------------+
                            """); 
        // 3. Partidas
        menus.put("partida", """
                                    +------------------------------------------------------------------------------+
                                    | GESTIÓN DE PARTIDAS |                                                        |
                                    +------------------------------------------------------------------------------+
                                    |                                                                              |
                                    |   [ 1 ] Iniciar una nueva partida  - Configura una nueva partida.            |
                                    |   [ 2 ] Cargar partida             - Carga una partida guardada.             |
                                    |   [ 3 ] Eliminar partida           - Eliminar una partida guardada.          |
                                    |   [ 0 ] Volver                     - Vuelve al Menú Principal.               | 
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);
        menus.put("partidapausada", """
                                        +------------------------------------------------------------------------------+
                                        | PAUSA |                                                                      |
                                        +------------------------------------------------------------------------------+
                                        |                                                                              |
                                        |   [ 1 ] Continuar jugando          - Continua jugando                        |
                                        |   [ 2 ] Salir                      - Salir de la partida.                    |                                                          |
                                        |                                                                              |
                                        +------------------------------------------------------------------------------+
                                        """);
        menus.put("partidacargar", """
                                    +------------------------------------------------------------------------------+
                                    | GESTIÓN DE PARTIDAS > CARGAR PARTIDA                                         |
                                    +------------------------------------------------------------------------------+
                                    |   Que quieres hacer?                                                         |  
                                    |                                                                              |
                                    |   [ 1 ] Seleccionar               - Seleciona entre tus partidas guardadas   |
                                    |   [ 0 ] Volver                    - Vuelve a Definir Partida Nueva.          | 
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);

        menus.put("partidainiciar", """
                                    +------------------------------------------------------------------------------+
                                    | GESTIÓN DE PARTIDAS > INICIAR PARTIDA                                        |
                                    +------------------------------------------------------------------------------+
                                    |   Empieza a jugar Scrabble!                                                  |
                                    |                                                                              | 
                                    |   [ 1 ] Iniciar Partida           - Inicia la partida                        |
                                    |   [ 2 ] Configuraciones           - Modifica la configuracion de tu partida  |                    
                                    |                                                                              |                                   
                                    |                                                                              |
                                    |   [ 0 ] Volver                    - Vuelve a Gestión de Partidas.            | 
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);
        menus.put("partidaconfigurar", """
                                    +------------------------------------------------------------------------------+
                                    | GESTIÓN DE PARTIDAS > INICIAR PARTIDA > CONFIGURACIONES                      |
                                    +------------------------------------------------------------------------------+
                                    |                                                                              | 
                                    |   [ 1 ] Define Jugadores          - Inicia la partida                        |
                                    |   [ 2 ] Define Tablero            - Modifica la configuracion de tu partida  | 
                                    |   [ 3 ] Define Diccionario        - Modifica la configuracion de tu partida  |                                                     
                                    |                                                                              |                                   
                                    |                                                                              |
                                    |   [ 0 ] Volver                    - Vuelve para iniciar la partida.          | 
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);
       
        menus.put("partidainiciarjugadores", """
                                    +------------------------------------------------------------------------------+
                                    | GESTIÓN DE PARTIDAS > INICIAR PARTIDA > DEFINIR JUGADORES                    |
                                    +------------------------------------------------------------------------------+
                                    |   Anade jugadores a tu juego!                                                |
                                    |                                                                              | 
                                    |   [ 1 ] Continuar                 - Continuar la inicializacion              |
                                    |   [ 2 ] Ver jugadores             - Ver jugadores actuales de la partida     | 
                                    |   [ 3 ] Anadir jugador            - Anadir jugadores actuales del juego      | 
                                    |   [ 4 ] Eliminar jugador          - Eliminar jugadores actuales del juego    |      
                                    |   [ 5 ] Ver jugadores disponibles - Ver jugadores actuales del juego         |                        
                                    |                                                                              | 
                                    |                                                                              |                                                                                                     
                                    |   [ 0 ] Volver                    - Vuelve a Gestión de Partidas.            | 
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);                                         
        menus.put("partidainiciartablero", """
                                    +------------------------------------------------------------------------------+
                                    | GESTIÓN DE PARTIDAS > INICIAR PARTIDA > DEFINIR TABLERO                      |
                                    +------------------------------------------------------------------------------+
                                    |   Define el tamano del tablero! (Por defecto, solo el tablero de 15x15       |
                                    |   tendra casillas especiales).                                               |       
                                    |                                                                              | 
                                    |   [ 1 ] Continuar                 - Continuar la inicializacion              |
                                    |   [ 2 ] Ver tamano actual         - Ver el tamano actual                     |
                                    |   [ 3 ] Modificar tamano actual   - Modifica el tamano actual                |
                                    |                                                                              | 
                                    |                                                                              |                                                     
                                    |   [ 0 ] Volver                    - Vuelve a Gestión de Partidas.            | 
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);

        menus.put("partidainiciardiccionario", """
                                        +------------------------------------------------------------------------------+
                                        | GESTIÓN DE PARTIDAS > INICIAR PARTIDA > DEFINIR DICCIONARIO                  |
                                        +------------------------------------------------------------------------------+
                                        |   Seleciona el diccionario que quieras usar.                                 |       
                                        |                                                                              | 
                                        |   [ 1 ] Continuar                 - Continuar la inicializacion              |
                                        |   [ 2 ] Ver seleccion actual      - Ver diccionario actual                   |
                                        |   [ 3 ] Selecionar un diccionario - Seleciona un diccionario                 |
                                        |   [ 4 ] Ver diccionarios          - Ver diccionarios disponibles             | 
                                        |                                                                              |                                                     
                                        |   [ 0 ] Volver                    - Vuelve a Gestión de Partidas.            | 
                                        |                                                                              |
                                        +------------------------------------------------------------------------------+
                                        """);                                                                                                                                                 
        
        menus.put("ranking", """
                                    +------------------------------------------------------------------------------+
                                    | GESTIÓN DE RANKINGS |                                                        |
                                    |                                                                              |
                                    |   [ 1 ] Ver ranking                - Muestra el ranking de jugadores.        |
                                    |   [ 2 ] Consultar historial        - Consulta el historial de un jugador.    |
                                    |   [ 3 ] Filtrar ranking            - Filtra el ranking según criterios.      |
                                    |   [ 4 ] Eliminar jugador           - Elimina un jugador del ranking.           |
                                    |   [ 0 ] Volver                     - Vuelve al menú principal.                 | 
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);

        ///Configuración
        
        menus.put("configuracion", """
                                    +------------------------------------------------------------------------------+
                                    | CONFIGURACIÓN - PRÓXIMAMENTE                                                 |
                                    +------------------------------------------------------------------------------+
                                    |                                                                              |
                                    |   La funcionalidad de configuración estará disponible en la entrega 3.       |
                                    |                                                                              |
                                    |   Pulse ENTER para volver al menú principal.                                 |
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);
        
        // Menú de ranking
        StringBuilder rankingMenu = new StringBuilder();
        rankingMenu.append("+------------------------------------------------------------------------------+\n");
        rankingMenu.append("| GESTIÓN DE RANKING                                                           |\n");
        rankingMenu.append("+------------------------------------------------------------------------------+\n");
        rankingMenu.append("|                                                                              |\n");
        rankingMenu.append("| [ 1 ] Ver ranking                - Muestra el ranking de jugadores.          |\n");
        rankingMenu.append("| [ 2 ] Ver historial              - Historial de puntuaciones por jugador.    |\n");
        rankingMenu.append("| [ 3 ] Filtrar ranking            - Filtra el ranking según criterios.        |\n");
        rankingMenu.append("| [ 4 ] Eliminar jugador           - Elimina un jugador del ranking.           |\n");
        rankingMenu.append("| [ 0 ] Volver                     - Vuelve al menú principal.                 |\n");
        rankingMenu.append("|                                                                              |\n");
        rankingMenu.append("+------------------------------------------------------------------------------+");
        menus.put("RANKING", rankingMenu.toString());
    }

    public static void ShowMenu(String menu) {
        System.out.print(menus.get(menu));
    }
    
    // Aquí irían los métodos de implementación para cada comando
    // ...

    public static void manageUserMenu () throws IOException {
        boolean volver = false;

        while (!volver) {
            ShowMenu("usuario");
            String userCommand = readLine().trim();

            switch (userCommand) {
                case "0":
                    volver = true;
                    ShowMenu("principal");
                    break;
                case "1":
                    manageUserRegistrar();               
                    break;
                case "2":
                    manageUserEliminar();                   
                    break;
                case "3":
                    manageUserMostrar();
                    break;                    
                default:
                    System.out.println("¡Introduce alguno de los comandos disponibles!");
                    break;            
            }
        }
    }

    public static void manageUserRegistrar() throws IOException{
        boolean volver = false;
        while (!volver) {
            // Usar el menú predefinido en lugar de texto repetido
            ShowMenu("usuarioCrear");
            
            String userCommand = readLine().trim();
            String user;

            switch (userCommand) {
                case "0":
                    volver = true;
                    System.out.println("Volviendo a Gestión de Jugadores...");
                    break;
                case "2":
                    // Mostrar la lista de jugadores existentes sin añadir un encabezado redundante
                    mostrarUsuariosFormateados(null);
                    break;
                case "1":
                    // Mostrar panel para introducir nombre de jugador
                    System.out.println("""
                        +------------------------------------------------------------------------------+
                        | GESTIÓN DE JUGADORES > CREAR JUGADOR > INTRODUCIR NOMBRE                     |
                        |                                                                              |
                        |   Introduce un nombre de jugador:                                            |
                        |                                                                              |
                        +------------------------------------------------------------------------------+
                        """);
                    
                    user = readLine();

                    try {
                        if(controladorDomain.registrarUsuario(user)) {
                            // Mostrar jugadores actualizados con el nuevo jugador
                            mostrarUsuariosFormateados(user); // Pasamos el jugador recién creado para destacarlo
                            
                            // Mostrar mensaje de éxito después del listado
                            System.out.println("""
                                +------------------------------------------------------------------------------+
                                | GESTIÓN DE JUGADORES > CREAR JUGADOR                                         |
                                |                                                                              |
                                |   ¡Jugador '%s' creado correctamente!                                        |
                                |                                                                              |
                                +------------------------------------------------------------------------------+
                                """.formatted(user));
                        }
                    } catch (ExceptionUserExist e) {
                        System.out.println("""
                            +------------------------------------------------------------------------------+
                            | GESTIÓN DE JUGADORES > CREAR JUGADOR                                         |
                            |                                                                              |
                            |   Error: El jugador ya existe.                                               |
                            |                                                                              |
                            +------------------------------------------------------------------------------+
                            """);                    
                    }
                  
                    break;    
                default:
                    System.out.println("¡Introduce alguno de los comandos disponibles!");
                    break;
            }
        }
    }

    /**
     * Muestra la información formateada de todos los usuarios, destacando el usuario recién creado.
     * @param usuarioNuevo Nombre del usuario recién creado (null si no hay ninguno)
     */
    private static void mostrarUsuariosFormateados(String usuarioNuevo) {
        System.out.println("+------------------------------------------------------------------------------+");
        System.out.println("| JUGADORES REGISTRADOS                                                        |");
        System.out.println("+------------------------------------------------------------------------------+");
        System.out.println("| NOMBRE            | TIPO      | JUGANDO | 0    | PUNTOS TOTAL   |");
        System.out.println("+-------------------+-----------+---------+-------------------+----------------+");
        
        // Obtener la lista de usuarios humanos y ordenarla alfabéticamente
        List<String> usuarios = controladorDomain.getUsuariosHumanos();
        Collections.sort(usuarios);
        
        // Recorrer la lista ordenada y mostrar la información de cada usuario
        for (String nombreUsuario : usuarios) {
            // Obtenemos información adicional del usuario
            String tipoUsuario = "Humano";
            
            // Verificamos si el jugador está en partida
            boolean enPartida = controladorDomain.isEnPartida(nombreUsuario);
            String estadoPartida = enPartida ? "Sí" : "No";
            
            // Nombre de la partida actual
            String nombrePartida = controladorDomain.getNombrePartidaActual(nombreUsuario);
            if (nombrePartida == null || nombrePartida.isEmpty()) {
                nombrePartida = "-";
            }
            
            // Información del ranking (solo puntuación total)
            String puntuacionTotal;
            if (controladorDomain.perteneceRanking(nombreUsuario)) {
                int puntos = controladorDomain.getPuntuacionTotalDirecta(nombreUsuario);
                puntuacionTotal = puntos + " pts";
            } else {
                puntuacionTotal = "No en ranking";
            }
            
            // Destacar el usuario recién creado
            String indicadorNuevo = (nombreUsuario.equals(usuarioNuevo)) ? " ★" : "";
            
            System.out.printf("| %-17s | %-9s | %-7s | %-17s | %-14s |%s%n", 
                            nombreUsuario, tipoUsuario, estadoPartida, nombrePartida, puntuacionTotal, indicadorNuevo);
        }
        
        System.out.println("+------------------------------------------------------------------------------+");
        System.out.println("| Total jugadores: " + usuarios.size() + 
                         " (Humanos: " + usuarios.size() + ")                                              |");
        System.out.println("+------------------------------------------------------------------------------+");
    }

    public static void manageUserEliminar() throws IOException{
        // Verificar si hay usuarios humanos para eliminar
        // Método alternativo para verificar si hay usuarios humanos
        // Capturamos la salida del método mostrarUsuariosFormateados para analizarla
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.PrintStream originalOut = System.out;
        System.setOut(new java.io.PrintStream(baos));
        
        // Llamamos al método que ya existe para mostrar usuarios
        mostrarUsuariosFormateados(null);
        
        // Restauramos la salida estándar
        System.setOut(originalOut);
        
        // Verificamos si hay usuarios (buscando la línea que contiene "Total usuarios: 0")
        String output = baos.toString();
        if (output.contains("Total jugadores: 0")) {
            System.out.println("""
                +------------------------------------------------------------------------------+
                | GESTIÓN DE JUGADORES > ELIMINAR JUGADOR                                      |
                |                                                                              |
                |   No hay jugadores registrados para eliminar.                                |
                |   Debes crear al menos un jugador primero.                                   |
                |                                                                              |
                +------------------------------------------------------------------------------+
                """);
            return; // Volvemos al menú de gestión de usuarios
        }
        
        boolean volver = false;
        while (!volver) {
            // Usar el menú predefinido en lugar de texto repetido
            ShowMenu("usuarioEliminar");
            
            String userCommand = readLine().trim();
            String user;

            switch (userCommand) {
                case "0":
                    volver = true;
                    System.out.println("Volviendo a Gestión de Jugadores...");
                    break;
                case "2":
                    // Mostrar la lista de jugadores existentes sin añadir un encabezado redundante
                    mostrarUsuariosFormateados(null);
                    break;
                case "1":
                    System.out.println("""
                        +------------------------------------------------------------------------------+
                        | GESTIÓN DE JUGADORES > ELIMINAR JUGADOR > INTRODUCIR NOMBRE                  |
                        |                                                                              |
                        |   Introduce un nombre de jugador a eliminar:                                 |
                        |                                                                              |
                        +------------------------------------------------------------------------------+
                        """);
                    
                    user = readLine();

                    try {
                        // Ahora la verificación de si está en partida se hace en el controlador
                        if(controladorDomain.eliminarUsuario(user)) {
                            // Mostrar usuarios restantes
                            mostrarUsuariosFormateados(null);
                            
                            // Mostrar mensaje de éxito después del listado
                            System.out.println("""
                                +------------------------------------------------------------------------------+
                                | GESTIÓN DE JUGADORES > ELIMINAR JUGADOR                                      |
                                |                                                                              |
                                |   ¡Jugador '%s' eliminado correctamente!                                     |
                                |                                                                              |
                                +------------------------------------------------------------------------------+
                                """.formatted(user));
                        }
                    } catch (ExceptionUserEsIA e) {
                        System.out.println("""
                            +------------------------------------------------------------------------------+
                            | GESTIÓN DE JUGADORES > ELIMINAR JUGADOR                                      |
                            |                                                                              |
                            |   Error: No se puede eliminar un jugador IA.                                 |
                            |                                                                              |
                            +------------------------------------------------------------------------------+
                            """);
                    } catch (ExceptionUserInGame e) {
                        System.out.println("""
                            +------------------------------------------------------------------------------+
                            | GESTIÓN DE JUGADORES > ELIMINAR JUGADOR                                      |
                            |                                                                              |
                            |   Error: El jugador está actualmente en una partida.                         |
                            |   No se puede eliminar mientras esté en una partida activa.                  |
                            |                                                                              |
                            +------------------------------------------------------------------------------+
                            """);
                    } catch (ExceptionUserLoggedIn e) {
                        System.out.println("""
                            +------------------------------------------------------------------------------+
                            | GESTIÓN DE JUGADORES > ELIMINAR JUGADOR                                      |
                            |                                                                              |
                            |   Error: El jugador está logueado. Cierra su sesión antes.                   |
                            |                                                                              |
                            +------------------------------------------------------------------------------+
                            """);
                    } catch (ExceptionUserNotExist e) {
                        System.out.println("""
                            +------------------------------------------------------------------------------+
                            | GESTIÓN DE JUGADORES > ELIMINAR JUGADOR                                      |
                            |                                                                              |
                            |   Error: El jugador no existe.                                               |
                            |                                                                              |
                            +------------------------------------------------------------------------------+
                            """);
                    } catch (ExceptionRankingOperationFailed e) {
                        System.out.println("""
                            +------------------------------------------------------------------------------+
                            | GESTIÓN DE JUGADORES > ELIMINAR JUGADOR                                      |
                            |                                                                              |
                            |   Error: No se ha podido eliminar jugador del ranking.                       |
                            |                                                                              |
                            +------------------------------------------------------------------------------+
                            """);                    
                    }
                    break;  
                default:
                    System.out.println("¡Introduce alguno de los comandos disponibles!");
                    break;
            }
        }
    }

    public static void manageUserMostrar() throws IOException{
        // Mostrar jugadores en formato personalizado en lugar del debug
        // No imprimir encabezado aquí porque mostrarUsuariosFormateados ya lo hace
        mostrarUsuariosFormateados(null);
        
        // Agregar una notificación de operación realizada
        System.out.println("+------------------------------------------------------------------------------+");
        System.out.println("| GESTIÓN DE JUGADORES                                                         |");
        System.out.println("|                                                                              |");
        System.out.println("| Operación 'Mostrar jugadores' realizada correctamente.                       |");
        System.out.println("|                                                                              |");
        System.out.println("+------------------------------------------------------------------------------+");
        
        // No se muestra submenú, se regresa directamente al menú principal
    }

    public static void manageDiccionaryMenu() throws IOException {
        boolean volver = false;

        while (!volver) {
            ShowMenu("diccionario");
            String userCommand = readLine().trim();

            switch (userCommand) {
                case "0":
                    volver = true;
                    ShowMenu("principal");
                    break;
                case "1":
                    crearDiccionarioInteractivo();
                    break;
                case "2":
                    eliminarDiccionarioInteractivo();
                    break;
                case "3":
                    importarDiccionarioInteractivo();
                    break;
                case "4":
                    modificarDiccionarioInteractivo();
                    break;
                case "5":
                    mostrarDiccionariosCargados();
                    break;
                default:
                    System.out.println("¡Introduce alguno de los comandos disponibles!");
                    break;
            }
        }
    }

    /**
     * Crea un nuevo diccionario de manera interactiva.
     * Primero crea un directorio, luego crea y rellena los archivos alpha.txt y words.txt.
     */
    private static void crearDiccionarioInteractivo() throws IOException {
        showNotification("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO", 
                       "Ingrese el nombre del nuevo diccionario (será el nombre de la carpeta):",
                       "Presione Enter para continuar después de cada entrada.",
                       "Para cancelar en cualquier momento, ingrese '-1'.");
        
        String nombre = leerLinea("> ");
        
        if (nombre.isEmpty() || nombre.equals("-1")) {
            showNotification("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO", 
                             "Operación cancelada por el usuario.");
            return;
        }
        
        // Validar que el nombre solo contenga caracteres válidos para nombre de carpeta
        if (!nombre.matches("^[a-zA-Z0-9_-]+$")) {
            showNotification("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO", 
                             "Error: Nombre inválido. Use solo letras, números, guiones o guiones bajos.",
                             "Operación cancelada.");
            return;
        }
        
        // Construir la ruta del directorio
        String RESOURCE_BASE_PATH = "src/main/resources/diccionarios/";
        java.nio.file.Path dictPath = java.nio.file.Paths.get(RESOURCE_BASE_PATH, nombre);
        
        // Verificar si ya existe una carpeta o un diccionario cargado con ese nombre
        if (java.nio.file.Files.exists(dictPath)) {
            showNotification("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO", 
                             "Error: Ya existe una carpeta con el nombre '" + nombre + "'.",
                             "Operación cancelada.");
            return;
        }
        
        if (controladorDomain.existeDiccionario(nombre)) {
            showNotification("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO", 
                             "Error: Ya hay un diccionario cargado con el nombre '" + nombre + "'.",
                             "Operación cancelada.");
            return;
        }
        
        try {
            // Crear el directorio
            java.nio.file.Files.createDirectories(dictPath);
            
            // Crear alpha.txt
            java.nio.file.Path alphaFile = dictPath.resolve("alpha.txt");
            List<String> alphabetLines = new ArrayList<>();
            
            showNotification("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO", 
                           "Ingrese los caracteres del alfabeto (formato: LETRA FRECUENCIA PUNTOS), uno por línea.",
                           "Ejemplo: A 9 1,  CH 1 5 o  en formato comodín # 2 0 (comodín)",
                           "Para terminar, introduzca una línea vacía o ingrese '-1' o ",
                           "Presione Enter después de cada entrada.");
            
            String alphaLine;
            // Usamos un Map para detectar letras duplicadas, guardando la línea completa
            Map<String, String> letrasAlpha = new HashMap<>();
            
            while (!(alphaLine = leerLinea("> ")).isEmpty()) {
                // Comprobar si el usuario quiere cancelar
                if (alphaLine.equals("-1")) {
                    showNotification("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO", 
                                    "Creación del diccionario '" + nombre + "' cancelada por el usuario.");
                    // Limpiar recursos creados
                    if (java.nio.file.Files.exists(dictPath)) {
                        java.nio.file.Files.walk(dictPath)
                             .sorted(java.util.Comparator.reverseOrder())
                             .map(java.nio.file.Path::toFile)
                             .forEach(java.io.File::delete);
                    }
                    return;
                }
                
                // Validar el formato de la entrada: LETRA FRECUENCIA PUNTOS
                // Actualizado para permitir letras compuestas (más de un carácter)
                if (!alphaLine.matches("^[A-Za-z#]+\\s+\\d+\\s+\\d+$")) {
                    showNotification("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO", 
                                   "Error: Formato incorrecto. Debe ser 'LETRA FRECUENCIA PUNTOS'.",
                                   "Ejemplo: A 9 1,  CH 1 5 o  en formato comodín # 2 0 (comodín)",
                                   "Ingrese un valor válido o presione enter vacío para volver al menú o -1 para cancelar.");
                    continue;
                }
                
                // Validar que los valores numéricos sean válidos
                String[] parts = alphaLine.split("\\s+");
                int frecuencia = Integer.parseInt(parts[1]);
                int puntos = Integer.parseInt(parts[2]);
                
                if (frecuencia < 1) {
                    showNotification("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO", 
                                   "Error: La frecuencia debe ser mayor que 0.",
                                   "Ejemplo: A 9 1,  CH 1 5 o  en formato comodín # 2 0 (comodín)",
                                   "Ingrese un valor válido o presione enter vacío para volver al menú o -1 para cancelar.");
                    continue;
                }
                
                if (puntos < 0) {
                    showNotification("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO", 
                                   "Error: Los puntos no pueden ser negativos.",
                                   "Ejemplo: A 9 1,  CH 1 5 o  en formato comodín # 2 0 (comodín)",
                                   "Ingrese un valor válido o presione enter vacío para volver al menú o -1 para cancelar.");
                    continue;
                }
                
                // Comprobar si la letra ya existe en el alfabeto
                String letra = parts[0].toUpperCase();
                
                // Validación específica para comodines
                if (letra.equals("#")) {
                    if (frecuencia > 2) {
                        showNotification("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO", 
                                       "Error: La frecuencia para comodines (#) no puede ser mayor que 2.",
                                       "El comodín debe tener formato: # [1-2] 0",
                                       "Ingrese un valor válido o presione enter vacío para volver al menú o -1 para cancelar.");
                        continue;
                    }
                    
                    if (puntos != 0) {
                        showNotification("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO", 
                                       "Error: El valor en puntos para comodines (#) debe ser siempre 0.",
                                       "El comodín debe tener formato: # [1-2] 0",
                                       "Ingrese un valor válido o presione enter vacío para volver al menú o -1 para cancelar.");
                        continue;
                    }
                }
                
                if (letrasAlpha.containsKey(letra)) {
                    // La letra ya existe, preguntar si desea sobrescribirla
                    String lineaAnterior = letrasAlpha.get(letra);
                    showNotification("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO", 
                                   "La letra '" + letra + "' ya existe en el alfabeto con configuración: " + lineaAnterior,
                                   "Nuevo valor propuesto: " + alphaLine,
                                   "¿Desea sobrescribir los valores existentes con los nuevos? (s/n)");
                    
                    String respuesta = leerLinea("> ").trim().toLowerCase();
                    if (respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí")) {
                        // Sobrescribir la letra
                        letrasAlpha.put(letra, alphaLine);
                        showNotification("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO", 
                                       "✓ Valores actualizados para la letra '" + letra + "'.",
                                       "Ingrese otra letra o deje vacío para terminar o -1 para cancelar.");
                    } else {
                        showNotification("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO", 
                                       "Se mantienen los valores originales para la letra '" + letra + "'.",
                                       "Ingrese otra letra o deje vacío para terminar o -1 para cancelar.");
                    }
                } else {
                    // Nueva letra, añadirla al mapa
                    letrasAlpha.put(letra, alphaLine);
                    
                    // Mostrar confirmación de letra añadida
                    showNotification("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO", 
                                   "✓ Formato correcto. Letra '" + letra + "' añadida al alfabeto.",
                                   "Ingrese otra letra o deje vacío para terminar o -1 para cancelar.");
                }
            }
            
            if (letrasAlpha.isEmpty()) {
                showNotification("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO", 
                                "Error: El alfabeto está vacío. Se necesita al menos una letra.",
                                "Operación cancelada.");
                // Limpiar recursos creados
                if (java.nio.file.Files.exists(dictPath)) {
                    java.nio.file.Files.walk(dictPath)
                         .sorted(java.util.Comparator.reverseOrder())
                         .map(java.nio.file.Path::toFile)
                         .forEach(java.io.File::delete);
                }
                return;
            }
            
            // Convertir el mapa de letras a lista de líneas para escribir al archivo
            alphabetLines = new ArrayList<>(letrasAlpha.values());
            
            java.nio.file.Files.write(alphaFile, alphabetLines, java.nio.charset.StandardCharsets.UTF_8);
            showNotification("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO", 
                            "Archivo alpha.txt creado correctamente.");
            
            // Extraer los tokens válidos del alfabeto (preservando letras multicarácter como CH, RR)
            Set<String> validTokens = new HashSet<>();
            for (String line : alphabetLines) {
                String letra = line.split("\\s+")[0].toUpperCase();
                validTokens.add(letra);
            }
            
            // También extraer caracteres individuales para validación
            Set<Character> validChars = new HashSet<>();
            for (String token : validTokens) {
                for (char c : token.toCharArray()) {
                    validChars.add(c);
                }
            }
            
            // Crear words.txt
            java.nio.file.Path wordsFile = dictPath.resolve("words.txt");
            List<String> wordLines = new ArrayList<>();
            
            showNotification("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO", 
                           "Ingrese las palabras válidas, una por línea.",
                           "Para terminar, introduzca una línea vacía o ingrese '-1'.",
                           
                           "El alfabeto actual contiene las letras: " + validTokens,
                           "Presione Enter después de cada palabra.");
            
            String wordLine;
            while (!(wordLine = leerLinea("> ")).isEmpty()) {
                // Comprobar si el usuario quiere cancelar
                if (wordLine.equals("-1")) {
                    showNotification("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO", 
                                    "Creación del diccionario '" + nombre + "' cancelada por el usuario.");
                    // Limpiar recursos creados
                    if (java.nio.file.Files.exists(dictPath)) {
                        java.nio.file.Files.walk(dictPath)
                             .sorted(java.util.Comparator.reverseOrder())
                             .map(java.nio.file.Path::toFile)
                             .forEach(java.io.File::delete);
                    }
                    return;
                }
                
                if (wordLine.trim().isEmpty()) {
                    showNotification("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO", 
                                  "Error: La palabra no puede estar vacía.",
                                  "Presione Enter e intente de nuevo.");
                    continue;
                }
                
                String palabra = wordLine.toUpperCase();
                
                // Nueva validación que verifica si la palabra puede componerse usando exclusivamente
                // los tokens del alfabeto (como "CC", "RR", etc.)
                boolean esValida = true;
                String palabraPendiente = palabra;
                Set<String> tokensNoEncontrados = new HashSet<>();
                
                // Intentar consumir la palabra usando tokens en orden descendente por longitud (primero los más largos)
                List<String> tokensPorLongitud = new ArrayList<>(validTokens);
                tokensPorLongitud.sort((t1, t2) -> Integer.compare(t2.length(), t1.length()));
                
                while (!palabraPendiente.isEmpty() && esValida) {
                    boolean consumido = false;
                    
                    for (String token : tokensPorLongitud) {
                        if (palabraPendiente.startsWith(token)) {
                            palabraPendiente = palabraPendiente.substring(token.length());
                            consumido = true;
                            break;
                        }
                    }
                    
                    if (!consumido) {
                        esValida = false;
                        // Registrar qué partes no pueden consumirse
                        tokensNoEncontrados.add(palabraPendiente.substring(0, Math.min(3, palabraPendiente.length())));
                    }
                }
                
                if (!esValida) {
                    showNotification("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO", 
                                   "Error: La palabra '" + palabra + "' no puede formarse con los tokens disponibles en el alfabeto.",
                                   "Tokens disponibles: " + validTokens,
                                   "Recuerde que solo puede usar combinaciones exactas de los tokens listados.",
                                   "Presione Enter e intente de nuevo.");
                    continue;
                }
                
                // Comprobar si la palabra ya existe en la lista
                if (wordLines.contains(palabra)) {
                    showNotification("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO", 
                                  "Error: La palabra '" + palabra + "' ya existe en el diccionario.",
                                  "Presione Enter e ingrese una palabra diferente.");
                    continue;
                }
                
                wordLines.add(palabra);
                
                // Mostrar confirmación de palabra añadida
                showNotification("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO", 
                               "✓ Palabra '" + palabra + "' añadida al diccionario.",
                               "Ingrese otra palabra o deje vacío para terminar.");
            }
            
            if (wordLines.isEmpty()) {
                showNotification("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO", 
                                "Error: La lista de palabras está vacía. Se necesita al menos una palabra.",
                                "Operación cancelada.");
                // Limpiar recursos creados
                if (java.nio.file.Files.exists(dictPath)) {
                    java.nio.file.Files.walk(dictPath)
                         .sorted(java.util.Comparator.reverseOrder())
                         .map(java.nio.file.Path::toFile)
                         .forEach(java.io.File::delete);
                }
                return;
            }
            
            // Ordenar las palabras por orden lexicográfico
            wordLines = wordLines.stream().sorted().collect(java.util.stream.Collectors.toList());
            java.nio.file.Files.write(wordsFile, wordLines, java.nio.charset.StandardCharsets.UTF_8);
            
            // Intentar cargar el diccionario
            try {
                controladorDomain.anadirLenguaje(nombre, alphaFile.toString(), wordsFile.toString());
                
                // Obtener los tokens del alfabeto para mostrarlos correctamente
                Set<String> tokensAlphabet = controladorDomain.getTokensAlfabeto(nombre);
                
                // Formatear los tokens para mostrarlos de forma más legible
                StringBuilder tokensStr = new StringBuilder();
                int count = 0;
                for (String token : tokensAlphabet) {
                    tokensStr.append(token);
                    count++;
                    if (count < tokensAlphabet.size()) {
                        tokensStr.append(", ");
                    }
                    // Añadir salto de línea cada 8 tokens para mejor legibilidad
                    if (count % 8 == 0 && count < tokensAlphabet.size()) {
                        tokensStr.append("\n");
                    }
                }
                
                showNotification("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO", 
                               "Diccionario '" + nombre + "' creado y cargado correctamente.",
                               "Contiene " + wordLines.size() + " palabras y " + tokensAlphabet.size() + " letras/tokens en el alfabeto.",
                               "",
                               "Tokens del alfabeto:",
                               tokensStr.toString());
            } catch (ExceptionDiccionarioExist e) {
                showNotification("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO", 
                              "Error: Ya existe un diccionario con ese nombre. Este comportamiento es inesperado.",
                              "Los archivos han sido creados correctamente, pero el diccionario no ha sido cargado.");
            } catch (ExceptionPalabraInvalida e) {
                showNotification("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO", 
                              "Error al cargar el diccionario: " + e.getMessage(),
                              "Los archivos han sido creados correctamente, pero el diccionario no ha sido cargado completamente.");
            } catch (Exception e) {
                showNotification("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO", 
                              "Error al cargar el diccionario: " + e.getMessage(),
                              "Los archivos han sido creados correctamente, pero el diccionario no ha sido cargado completamente.");
            }
            
        } catch (IOException e) {
            showNotification("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO", 
                            "Error al crear el diccionario: " + e.getMessage());
            // Limpiar recursos en caso de fallo
            try {
                if (java.nio.file.Files.exists(dictPath)) {
                    java.nio.file.Files.walk(dictPath)
                         .sorted(java.util.Comparator.reverseOrder())
                         .map(java.nio.file.Path::toFile)
                         .forEach(java.io.File::delete);
                }
            } catch (IOException cleanupEx) {
                showNotification("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO", 
                                "Error adicional al limpiar: " + cleanupEx.getMessage());
            }
        }
    }

    /**
     * Elimina un diccionario existente.
     * Muestra los diccionarios cargados y permite eliminar uno junto con sus archivos.
     */
    private static void eliminarDiccionarioInteractivo() throws IOException {
        List<String> diccionarios = controladorDomain.getDiccionariosDisponibles();
        
        if (diccionarios.isEmpty()) {
            showNotification("GESTIÓN DE DICCIONARIOS > ELIMINAR DICCIONARIO", 
                            "No hay diccionarios cargados para eliminar.");
            return;
        }
        
        // Ordenar los diccionarios alfabéticamente
        Collections.sort(diccionarios);
        
        // Preparar los mensajes con las opciones
        List<String> mensajes = new ArrayList<>();
        mensajes.add("Seleccione el diccionario a eliminar (¡esto borrará sus archivos!):");
        mensajes.add("");
        
        // Mostrar diccionarios numerados
        for (int i = 0; i < diccionarios.size(); i++) {
            mensajes.add("  [" + (i+1) + "] " + diccionarios.get(i));
        }
        mensajes.add("  [0] Volver");
        
        // Mostrar la notificación con las opciones
        showNotification("GESTIÓN DE DICCIONARIOS > ELIMINAR DICCIONARIO", 
                        mensajes.toArray(new String[0]));
        
        String seleccion = leerLinea("> ");
        
        if (seleccion.isEmpty() || seleccion.equals("0")) {
            showNotification("GESTIÓN DE DICCIONARIOS > ELIMINAR DICCIONARIO", 
                            "Operación cancelada.");
            return;
        }
        
        String nombre;
        // Comprobar si la selección es un número
        if (seleccion.matches("\\d+")) {
            int indice = Integer.parseInt(seleccion) - 1;
            if (indice < 0 || indice >= diccionarios.size()) {
                showNotification("GESTIÓN DE DICCIONARIOS > ELIMINAR DICCIONARIO", 
                               "Error: Número de diccionario no válido.");
                return;
            }
            nombre = diccionarios.get(indice);
        } else {
            nombre = seleccion;
            if (!diccionarios.contains(nombre)) {
                showNotification("GESTIÓN DE DICCIONARIOS > ELIMINAR DICCIONARIO", 
                               "Error: No existe un diccionario con ese nombre.");
                return;
            }
        }
        
        // Confirmar eliminación
        showNotification("GESTIÓN DE DICCIONARIOS > ELIMINAR DICCIONARIO", 
                         "¿Está seguro de eliminar '" + nombre + "' y todos sus archivos?",
                         "",
                         "Ingrese 's' para confirmar o 'n' para cancelar.");
        
        String confirmacion = leerLinea("> ");
        if (!confirmacion.equalsIgnoreCase("s")) {
            showNotification("GESTIÓN DE DICCIONARIOS > ELIMINAR DICCIONARIO", 
                            "Operación cancelada.");
            return;
        }
        
        try {
            controladorDomain.eliminarDiccionario(nombre);
            showNotification("GESTIÓN DE DICCIONARIOS > ELIMINAR DICCIONARIO", 
                            "¡Diccionario '" + nombre + "' eliminado correctamente!");
        } catch (scrabble.excepciones.ExceptionDiccionarioNotExist e) {
            showNotification("GESTIÓN DE DICCIONARIOS > ELIMINAR DICCIONARIO", 
                            "Error: " + e.getMessage());
        } catch (IOException e) {
            showNotification("GESTIÓN DE DICCIONARIOS > ELIMINAR DICCIONARIO", 
                            "Error al eliminar los archivos: " + e.getMessage());
        }
    }

    /**
     * Importa un diccionario existente desde una ruta especificada por el usuario.
     * Permite cargar un diccionario desde cualquier carpeta que contenga los archivos
     * alpha.txt y words.txt correctamente formateados.
     */
    private static void importarDiccionarioInteractivo() throws IOException {
        // Solicitar la ruta del directorio al usuario
        showNotification("GESTIÓN DE DICCIONARIOS > IMPORTAR DICCIONARIO", 
                        "Indique la ruta del directorio que contiene el diccionario a importar:",
                        "(El directorio debe contener los archivos alpha.txt y words.txt)",
                        "Ejemplos: 'nombre_diccionario' => 'src/nombre_diccionario'");
        
        String rutaOriginal = leerLinea("> ");
        
        if (rutaOriginal.isEmpty()) {
            showNotification("GESTIÓN DE DICCIONARIOS > IMPORTAR DICCIONARIO", 
                           "Operación cancelada.");
            return;
        }
        
        // Normalizar la ruta (eliminar barras finales si existen)
        String rutaDirectorio = rutaOriginal.replaceAll("[/\\\\]+$", "");
        
        // Lista de posibles rutas a probar
        List<String> rutasAProbar = new ArrayList<>();
        rutasAProbar.add(rutaDirectorio); // Ruta original proporcionada
        
        // Si es una ruta relativa que comienza con '/' o 'src/', probar sin el prefijo también
        if (rutaDirectorio.startsWith("/")) {
            rutasAProbar.add(rutaDirectorio.substring(1)); // Sin la barra inicial
        }
        
        // Probar con una ruta relativa al directorio del proyecto
        String directorioBase = System.getProperty("user.dir");
        rutasAProbar.add(Paths.get(directorioBase, rutaDirectorio).toString());
        
        // Si es una ruta que empieza con src/, probar también directamente desde la raíz del proyecto
        if (rutaDirectorio.startsWith("src/")) {
            rutasAProbar.add(Paths.get(directorioBase, rutaDirectorio).toString());
        } else {
            // Si no empieza con src/, probar añadiendo src/ al principio
            rutasAProbar.add(Paths.get(directorioBase, "src", rutaDirectorio).toString());
        }
        
        // Probar cada posible ruta
        boolean directorioEncontrado = false;
        String rutaValida = null;
        
        for (String ruta : rutasAProbar) {
            if (controladorDomain.esDiccionarioValido(ruta)) {
                directorioEncontrado = true;
                rutaValida = ruta;
                break;
            }
        }
        
        if (!directorioEncontrado) {
            showNotification("GESTIÓN DE DICCIONARIOS > IMPORTAR DICCIONARIO", 
                           "Error: El directorio especificado no es válido o no contiene los archivos necesarios.",
                           "Verifique que la ruta '" + rutaOriginal + "' exista y contenga los archivos alpha.txt y words.txt.",
                           "Asegúrese de que alpha.txt contiene el formato: 'LETRA FRECUENCIA PUNTOS' por línea.",
                           "y que words.txt contiene una palabra válida por línea.");
            return;
        }
        
        // Obtener el nombre del diccionario (último segmento de la ruta)
        Path dirPath = Paths.get(rutaValida);
        String nombreDiccionario = dirPath.getFileName().toString();
        
        // Preguntar si desea usar un nombre personalizado
        showNotification("GESTIÓN DE DICCIONARIOS > IMPORTAR DICCIONARIO", 
                       "Nombre detectado para el diccionario: '" + nombreDiccionario + "'",
                       "¿Desea utilizar este nombre o especificar uno diferente?",
                       "  [1] Usar nombre detectado ('" + nombreDiccionario + "')",
                       "  [2] Especificar un nombre diferente",
                       "  [0] Volver / Cancelar operación");
        
        String opcionNombre = leerLinea("> ");

        // Verificar si el usuario quiere cancelar
        if (opcionNombre.equals("0")) {
            showNotification("GESTIÓN DE DICCIONARIOS > IMPORTAR DICCIONARIO", 
                           "Operación cancelada por el usuario.");
            return;
        }
        
        if (opcionNombre.equals("2")) {
            showNotification("GESTIÓN DE DICCIONARIOS > IMPORTAR DICCIONARIO", 
                           "Ingrese el nombre para el diccionario:",
                           "(Use solo letras, números, guiones o guiones bajos)");
            String nombrePersonalizado = leerLinea("> ");
            
            if (nombrePersonalizado.isEmpty()) {
                showNotification("GESTIÓN DE DICCIONARIOS > IMPORTAR DICCIONARIO", 
                               "No se ingresó ningún nombre. Se usará el nombre detectado: '" + nombreDiccionario + "'");
            } else if (!nombrePersonalizado.matches("^[a-zA-Z0-9_-]+$")) {
                showNotification("GESTIÓN DE DICCIONARIOS > IMPORTAR DICCIONARIO", 
                               "Error: Nombre inválido. Se usará el nombre detectado: '" + nombreDiccionario + "'");
            } else {
                nombreDiccionario = nombrePersonalizado;
            }
        }
        
        // Verificar que no exista ya un diccionario con ese nombre
        if (controladorDomain.existeDiccionario(nombreDiccionario)) {
            showNotification("GESTIÓN DE DICCIONARIOS > IMPORTAR DICCIONARIO", 
                           "Error: Ya existe un diccionario con el nombre '" + nombreDiccionario + "'.",
                           "¿Desea reemplazarlo?",
                           "  [1] Sí, reemplazar el diccionario existente",
                           "  [2] No, cancelar la operación");
            
            String opcionReemplazar = leerLinea("> ");
            
            if (!opcionReemplazar.equals("1")) {
                showNotification("GESTIÓN DE DICCIONARIOS > IMPORTAR DICCIONARIO", 
                               "Operación cancelada por el usuario.");
                return;
            }
            
            // Eliminar el diccionario existente
            try {
                controladorDomain.eliminarDiccionario(nombreDiccionario);
                
                // Verificar que el diccionario se haya eliminado correctamente
                if (controladorDomain.existeDiccionario(nombreDiccionario)) {
                    showNotification("GESTIÓN DE DICCIONARIOS > IMPORTAR DICCIONARIO", 
                                  "Error: No se pudo eliminar el diccionario existente.",
                                  "Operación cancelada.");
                    return;
                }
            } catch (Exception e) {
                showNotification("GESTIÓN DE DICCIONARIOS > IMPORTAR DICCIONARIO", 
                               "Error al eliminar el diccionario existente: " + e.getMessage(),
                               "Operación cancelada.");
                return;
            }
        }
        
        // Intentar importar el diccionario
        try {
            controladorDomain.crearDiccionario(nombreDiccionario, rutaValida);
            showNotification("GESTIÓN DE DICCIONARIOS > IMPORTAR DICCIONARIO", 
                            "¡Diccionario '" + nombreDiccionario + "' importado y cargado exitosamente desde:",
                            rutaValida);
        } catch (ExceptionDiccionarioExist e) {
            // Esta excepción no debería ocurrir si la eliminación fue correcta
            showNotification("GESTIÓN DE DICCIONARIOS > IMPORTAR DICCIONARIO", 
                            "Error inesperado: El diccionario '" + nombreDiccionario + "' sigue existiendo después de eliminarlo.",
                            "Por favor, inténtelo de nuevo o use un nombre diferente.",
                            "Operación cancelada.");
        } catch (ExceptionPalabraInvalida e) {
            showNotification("GESTIÓN DE DICCIONARIOS > IMPORTAR DICCIONARIO", 
                            "Error: El archivo de palabras contiene caracteres no válidos según el alfabeto.",
                            e.getMessage(),
                            "Operación cancelada.");
        } catch (Exception e) {
            showNotification("GESTIÓN DE DICCIONARIOS > IMPORTAR DICCIONARIO", 
                            "Error al cargar el diccionario: " + e.getMessage(),
                            "Verifique que los archivos alpha.txt y words.txt tienen el formato correcto.");
        }
    }

    /**
     * Modifica un diccionario añadiendo o eliminando palabras.
     * Verifica que la palabra a añadir cumpla con los caracteres del alfabeto.
     */
    private static void modificarDiccionarioInteractivo() throws IOException {
        List<String> diccionarios = controladorDomain.getDiccionariosDisponibles();
        
        if (diccionarios.isEmpty()) {
            showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                            "No hay diccionarios cargados para modificar.");
            return;
        }
        
        // Ordenar los diccionarios alfabéticamente
        Collections.sort(diccionarios);
        
        // Preparar los mensajes con las opciones
        List<String> mensajes = new ArrayList<>();
        mensajes.add("Seleccione el diccionario a modificar:");
        mensajes.add("");
        
        for (int i = 0; i < diccionarios.size(); i++) {
            mensajes.add("  [" + (i+1) + "] " + diccionarios.get(i));
        }
        mensajes.add("  [0] Volver");
        
        // Mostrar la notificación con las opciones
        showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                        mensajes.toArray(new String[0]));
        
        String seleccion = leerLinea("> ");
        
        if (seleccion.isEmpty() || seleccion.equals("0")) {
            showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                            "Operación cancelada.");
            return;
        }
        
        String nombre;
        // Comprobar si la selección es un número
        if (seleccion.matches("\\d+")) {
            int indice = Integer.parseInt(seleccion) - 1;
            if (indice < 0 || indice >= diccionarios.size()) {
                showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                               "Error: Número de diccionario no válido.");
                return;
            }
            nombre = diccionarios.get(indice);
        } else {
            nombre = seleccion;
            if (!diccionarios.contains(nombre)) {
                showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                               "Error: No existe un diccionario con ese nombre.");
                return;
            }
        }
        
        // Preparar opciones de operación
        mensajes.clear();
        mensajes.add("Seleccione la operación para el diccionario '" + nombre + "':");
        mensajes.add("");
        mensajes.add("  [1] Añadir palabra");
        mensajes.add("  [2] Modificar palabra");
        mensajes.add("  [3] Eliminar palabra");
        mensajes.add("  [0] Volver");
        
        // Mostrar notificación con opciones
        showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                        mensajes.toArray(new String[0]));
        
        String op = readLine();
        if (op.equals("0")) {
            showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                            "Operación cancelada.");
            return;
        }
        
        if (!op.equals("1") && !op.equals("2") && !op.equals("3")) {
            showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                            "Opción inválida. Operación cancelada.");
            return;
        }
        
        // Opción 1: Añadir palabra
        if (op.equals("1")) {
            boolean seguirAñadiendo = true;
            while (seguirAñadiendo) {
                // Obtener alfabeto para validación
                Set<Character> alfabeto = null;
                try {
                    alfabeto = controladorDomain.getCaracteresAlfabeto(nombre);
                } catch (Exception e) {
                    showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                                    "Error al obtener caracteres válidos del alfabeto: " + e.getMessage(),
                                    "Operación cancelada.");
                    break;
                }
                
                // Información sobre el alfabeto
                List<String> mensajeAlfabeto = new ArrayList<>();
                mensajeAlfabeto.add("Ingrese la palabra a añadir al diccionario '" + nombre + "':");
                
                if (alfabeto != null && !alfabeto.isEmpty()) {
                    String alfabetoStr = alfabeto.stream()
                        .sorted()
                        .map(String::valueOf)
                        .collect(java.util.stream.Collectors.joining(", "));
                    mensajeAlfabeto.add("");
                    mensajeAlfabeto.add("Alfabeto disponible: [" + alfabetoStr + "]");
                    mensajeAlfabeto.add("Solo se permiten palabras con estos caracteres.");
                }
                
                mensajeAlfabeto.add("");
                mensajeAlfabeto.add("[0] Volver");
                
                // Pedir la palabra a añadir
                showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                                mensajeAlfabeto.toArray(new String[0]));
                
                String palabra = leerLinea("> ");
                if (palabra.isEmpty()) {
                    showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                                    "No se ingresó ninguna palabra. Intente de nuevo o escriba '0' para volver.");
                    continue;
                }
                
                // Verificar si el usuario quiere volver
                if (palabra.equals("0")) {
                    seguirAñadiendo = false;
                    continue;
                }
                
                // Intentar añadir la palabra
                try {
                    controladorDomain.modificarPalabraDiccionario(nombre, palabra, true);
                    showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                                    "Palabra '" + palabra.toUpperCase() + "' añadida correctamente al diccionario '" + nombre + "'.",
                                    "",
                                    "Puede seguir añadiendo palabras o escribir '0' para volver.");
                } catch (Exception e) {
                    showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                                    "Error al añadir la palabra: " + e.getMessage(),
                                    "",
                                    "Intente con otra palabra o escriba '0' para volver.");
                }
            }
            
            // Al salir del bucle, volver al menú de modificar diccionario
            modificarDiccionarioInteractivo(nombre);
            return;
        }
        // Opción 2: Modificar palabra
        else if (op.equals("2")) {
            modificarPalabraEnDiccionario(nombre);
        }
        // Opción 3: Eliminar palabra
        else {
            // Pedir la palabra a eliminar
            showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                            "Ingrese la palabra a eliminar del diccionario '" + nombre + "':");
            
            String palabra = leerLinea("> ");
            if (palabra.isEmpty()) {
                showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                                "No se ingresó ninguna palabra. Operación cancelada.");
                return;
            }
            
            // Intentar eliminar la palabra
            try {
                controladorDomain.modificarPalabraDiccionario(nombre, palabra, false);
                showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                                "Palabra '" + palabra + "' eliminada correctamente del diccionario '" + nombre + "'.");
                modificarDiccionarioInteractivo(nombre);
            } catch (Exception e) {
                showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                                "Error al eliminar la palabra: " + e.getMessage());
                modificarDiccionarioInteractivo(nombre);
            }
        }
    }

    /**
     * Método auxiliar para reutilizar la lógica de modificación de palabras
     */
    private static void modificarPalabraEnDiccionario(String nombreDiccionario) throws IOException {
        // Pedir la palabra a modificar
        showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO  > MODIFICAR PALABRA", 
                        "Ingrese la palabra que desea modificar del diccionario '" + nombreDiccionario + "':");
        
        String palabraOriginal = leerLinea("> ");
        if (palabraOriginal.isEmpty()) {
            showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                            "No se ingresó ninguna palabra. Operación cancelada.");
            modificarDiccionarioInteractivo(nombreDiccionario);
            return;
        }
        
        // Verificar si la palabra existe en el diccionario
        boolean existePalabra = false;
        try {
            existePalabra = controladorDomain.existePalabra(nombreDiccionario, palabraOriginal);
        } catch (Exception e) {
            showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                            "Error al verificar la palabra: " + e.getMessage());
            modificarDiccionarioInteractivo(nombreDiccionario);
            return;
        }
        
        if (!existePalabra) {
            showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                            "La palabra '" + palabraOriginal + "' no existe en el diccionario '" + nombreDiccionario + "'.",
                            "Operación cancelada.");
            modificarDiccionarioInteractivo(nombreDiccionario);
            return;
        }
        
        // Intentamos obtener los caracteres válidos del alfabeto
        Set<Character> alfabeto = null;
        try {
            alfabeto = controladorDomain.getCaracteresAlfabeto(nombreDiccionario);
        } catch (Exception e) {
            System.err.println("Error al obtener caracteres del alfabeto: " + e.getMessage());
        }
        
        List<String> mensajeNuevaPalabra = new ArrayList<>();
        mensajeNuevaPalabra.add("La palabra '" + palabraOriginal + "' existe en el diccionario.");
        
        // Añadir información del alfabeto si está disponible
        if (alfabeto != null && !alfabeto.isEmpty()) {
            String alfabetoStr = alfabeto.stream()
                .sorted()
                .map(String::valueOf)
                .collect(java.util.stream.Collectors.joining(", "));
            mensajeNuevaPalabra.add("");
            mensajeNuevaPalabra.add("Alfabeto disponible: [" + alfabetoStr + "]");
            mensajeNuevaPalabra.add("Solo se permiten palabras con estos caracteres.");
        }
        
        mensajeNuevaPalabra.add("");
        mensajeNuevaPalabra.add("Ingrese la nueva palabra que reemplazará a '" + palabraOriginal + "':");
        
        showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO  > MODIFICAR PALABRA", 
                        mensajeNuevaPalabra.toArray(new String[0]));
        
        String palabraNueva = leerLinea("> ");
        if (palabraNueva.isEmpty()) {
            showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                            "No se ingresó ninguna palabra nueva. Operación cancelada.");
            modificarDiccionarioInteractivo(nombreDiccionario);
            return;
        }
        
        // Intentar modificar la palabra
        try {
            controladorDomain.modificarPalabraEnDiccionario(nombreDiccionario, palabraOriginal, palabraNueva);
            showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                            "Palabra '" + palabraOriginal + "' modificada correctamente a '" + palabraNueva + "' en el diccionario '" + nombreDiccionario + "'.");
            modificarDiccionarioInteractivo(nombreDiccionario);
        } catch (ExceptionPalabraInvalida e) {
            // Si la palabra no es válida, preguntar si quiere intentar con otra palabra
            List<String> opcionesMensaje = new ArrayList<>();
            opcionesMensaje.add("Error: " + e.getMessage());
            opcionesMensaje.add("");
            opcionesMensaje.add("¿Desea intentar con otra palabra?");
            opcionesMensaje.add("");
            opcionesMensaje.add("  [1] Sí, ingresar otra palabra");
            opcionesMensaje.add("  [0] No, volver al menú anterior");
            
            showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                            opcionesMensaje.toArray(new String[0]));
            
            String opcionReintento = leerLinea("> ");
            if (opcionReintento.equals("1")) {
                // Volver a pedir palabra a modificar
                modificarPalabraEnDiccionario(nombreDiccionario);
            } else {
                // Volver al menú de operaciones
                modificarDiccionarioInteractivo(nombreDiccionario);
            }
        } catch (Exception e) {
            showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                            "Error al modificar la palabra: " + e.getMessage());
            modificarDiccionarioInteractivo(nombreDiccionario);
        }
    }
    
    /**
     * Sobrecarga de modificarDiccionarioInteractivo para poder volver al mismo menú con un diccionario ya seleccionado
     */
    private static void modificarDiccionarioInteractivo(String nombreDiccionario) throws IOException {
        // Verificar que el diccionario existe
        if (!controladorDomain.existeDiccionario(nombreDiccionario)) {
            showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                            "El diccionario '" + nombreDiccionario + "' ya no existe.");
            return;
        }
        
        // Preparar opciones de operación
        List<String> mensajes = new ArrayList<>();
        mensajes.add("Seleccione la operación para el diccionario '" + nombreDiccionario + "':");
        mensajes.add("");
        mensajes.add("  [1] Añadir palabra");
        mensajes.add("  [2] Modificar palabra");
        mensajes.add("  [3] Eliminar palabra");
        mensajes.add("  [0] Volver");
        
        // Mostrar notificación con opciones
        showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                        mensajes.toArray(new String[0]));
        
        String op = readLine();
        if (op.equals("0")) {
            // Volver al menú de selección de diccionario
            modificarDiccionarioInteractivo();
            return;
        }
        
        if (!op.equals("1") && !op.equals("2") && !op.equals("3")) {
            showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                            "Opción inválida. Operación cancelada.");
            modificarDiccionarioInteractivo(nombreDiccionario);
            return;
        }
        
        // Opción 1: Añadir palabra
        if (op.equals("1")) {
            boolean seguirAñadiendo = true;
            while (seguirAñadiendo) {
                // Obtener alfabeto para validación
                Set<Character> alfabeto = null;
                try {
                    alfabeto = controladorDomain.getCaracteresAlfabeto(nombreDiccionario);
                } catch (Exception e) {
                    showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                                    "Error al obtener caracteres válidos del alfabeto: " + e.getMessage(),
                                    "Operación cancelada.");
                    break;
                }
                
                // Información sobre el alfabeto
                List<String> mensajeAlfabeto = new ArrayList<>();
                mensajeAlfabeto.add("Ingrese la palabra a añadir al diccionario '" + nombreDiccionario + "':");
                
                if (alfabeto != null && !alfabeto.isEmpty()) {
                    String alfabetoStr = alfabeto.stream()
                        .sorted()
                        .map(String::valueOf)
                        .collect(java.util.stream.Collectors.joining(", "));
                    mensajeAlfabeto.add("");
                    mensajeAlfabeto.add("Alfabeto disponible: [" + alfabetoStr + "]");
                    mensajeAlfabeto.add("Solo se permiten palabras con estos caracteres.");
                }
                
                mensajeAlfabeto.add("");
                mensajeAlfabeto.add("[0] Volver");
                
                // Pedir la palabra a añadir
                showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                                mensajeAlfabeto.toArray(new String[0]));
                
                String palabra = leerLinea("> ");
                if (palabra.isEmpty()) {
                    showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                                    "No se ingresó ninguna palabra. Intente de nuevo o escriba '0' para volver.");
                    continue;
                }
                
                // Verificar si el usuario quiere volver
                if (palabra.equals("0")) {
                    seguirAñadiendo = false;
                    continue;
                }
                
                // Intentar añadir la palabra
                try {
                    controladorDomain.modificarPalabraDiccionario(nombreDiccionario, palabra, true);
                    showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                                    "Palabra '" + palabra.toUpperCase() + "' añadida correctamente al diccionario '" + nombreDiccionario + "'.",
                                    "",
                                    "Puede seguir añadiendo palabras o escribir '0' para volver.");
                } catch (Exception e) {
                    showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                                    "Error al añadir la palabra: " + e.getMessage(),
                                    "",
                                    "Intente con otra palabra o escriba '0' para volver.");
                }
            }
            
            // Al salir del bucle, volver al menú de modificar diccionario
            modificarDiccionarioInteractivo(nombreDiccionario);
            return;
        }
        // Opción 2: Modificar palabra
        else if (op.equals("2")) {
            modificarPalabraEnDiccionario(nombreDiccionario);
        }
        // Opción 3: Eliminar palabra
        else {
            // Pedir la palabra a eliminar
            showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                            "Ingrese la palabra a eliminar del diccionario '" + nombreDiccionario + "':");
            
            String palabra = leerLinea("> ");
            if (palabra.isEmpty()) {
                showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                                "No se ingresó ninguna palabra. Operación cancelada.");
                modificarDiccionarioInteractivo(nombreDiccionario);
                return;
            }
            
            // Intentar eliminar la palabra
            try {
                controladorDomain.modificarPalabraDiccionario(nombreDiccionario, palabra, false);
                showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                                "Palabra '" + palabra + "' eliminada correctamente del diccionario '" + nombreDiccionario + "'.");
                modificarDiccionarioInteractivo(nombreDiccionario);
            } catch (Exception e) {
                showNotification("GESTIÓN DE DICCIONARIOS > MODIFICAR DICCIONARIO ", 
                                "Error al eliminar la palabra: " + e.getMessage());
                modificarDiccionarioInteractivo(nombreDiccionario);
            }
        }
    }

    /**
     * Muestra los diccionarios actualmente cargados en el sistema.
     */
    private static void mostrarDiccionariosCargados() {
        List<String> diccionarios = controladorDomain.getDiccionariosDisponibles();
        
        if (diccionarios.isEmpty()) {
            showNotification("GESTIÓN DE DICCIONARIOS > MOSTRAR DICCIONARIOS", 
                            "No hay diccionarios cargados en el sistema.");
            return;
        }
        
        // Ordenar los diccionarios alfabéticamente
        Collections.sort(diccionarios);
        
        boolean seguirMostrandoDiccionarios = true;
        
        while (seguirMostrandoDiccionarios) {
            // Construir la lista de diccionarios para mostrar en la notificación
            List<String> mensajes = new ArrayList<>();
            mensajes.add("Diccionarios disponibles:");
            
            for (int i = 0; i < diccionarios.size(); i++) {
                mensajes.add((i + 1) + ". " + diccionarios.get(i));
            }
            
            mensajes.add(""); // Línea en blanco
            mensajes.add("Se han mostrado " + diccionarios.size() + " diccionario(s) disponible(s).");
            mensajes.add("");
            mensajes.add("Escriba el número del diccionario para ver sus valores de alpha.txt");
            mensajes.add("o pulse Enter para volver al menú anterior.");
            
            // Mostrar la notificación con todos los mensajes
            showNotification("GESTIÓN DE DICCIONARIOS > MOSTRAR DICCIONARIOS", 
                            mensajes.toArray(new String[0]));
            
            // Esperar input del usuario para seleccionar un diccionario
            String seleccion = leerLinea("> ");
            
            // Si el usuario no ingresa nada, volver al menú
            if (seleccion.trim().isEmpty() || seleccion.equals("0")) {
                seguirMostrandoDiccionarios = false;
                continue;
            }
            
            try {
                int indice = Integer.parseInt(seleccion.trim());
                // Verificar que el índice es válido
                if (indice >= 1 && indice <= diccionarios.size()) {
                    String nombreDiccionario = diccionarios.get(indice - 1);
                    mostrarAlphaDiccionario(nombreDiccionario);
                    // No salimos del bucle, permitiendo al usuario seleccionar otro diccionario
                } else {
                    showNotification("GESTIÓN DE DICCIONARIOS > MOSTRAR DICCIONARIOS", 
                                    "Número de diccionario inválido.");
                }
            } catch (NumberFormatException e) {
                showNotification("GESTIÓN DE DICCIONARIOS > MOSTRAR DICCIONARIOS", 
                                "Por favor, ingrese un número válido.");
            }
        }
    }
    
    /**
     * Muestra los valores del archivo alpha.txt de un diccionario específico.
     * @param nombreDiccionario Nombre del diccionario a mostrar
     */
    private static void mostrarAlphaDiccionario(String nombreDiccionario) {
        // Definir la ruta donde se encuentran los recursos de los diccionarios
        String RESOURCE_BASE_PATH = "src/main/resources/diccionarios/";
        Path dictPath = Paths.get(RESOURCE_BASE_PATH, nombreDiccionario);
        Path alphaPath = dictPath.resolve("alpha.txt");
        
        // Verificar si el archivo alpha.txt existe
        if (!Files.exists(alphaPath)) {
            showNotification("GESTIÓN DE DICCIONARIOS > MOSTRAR VALORES ALPHA", 
                            "Error: No se encontró el archivo alpha.txt para el diccionario '" + nombreDiccionario + "'.");
            return;
        }
        
        try {
            // Leer el contenido del archivo alpha.txt
            List<String> lineas = Files.readAllLines(alphaPath, StandardCharsets.UTF_8);
            
            if (lineas.isEmpty()) {
                showNotification("GESTIÓN DE DICCIONARIOS > MOSTRAR VALORES ALPHA", 
                                "El archivo alpha.txt del diccionario '" + nombreDiccionario + "' está vacío.");
                return;
            }
            
            // Preparar los mensajes para mostrar
            List<String> mensajes = new ArrayList<>();
            mensajes.add("Valores del archivo alpha.txt del diccionario '" + nombreDiccionario + "':");
            mensajes.add("");
            mensajes.add(String.format("%-10s %-10s %-10s", "LETRA", "FRECUENCIA", "PUNTOS"));
            mensajes.add("----------------------------------------");
            
            // Procesar cada línea del archivo alpha.txt
            for (String linea : lineas) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) {
                    continue; // Ignorar líneas vacías o comentarios
                }
                
                String[] partes = linea.split("\\s+");
                if (partes.length >= 3) {
                    String letra = partes[0];
                    String frecuencia = partes[1];
                    String puntos = partes[2];
                    
                    // Formatear y añadir la línea a los mensajes
                    mensajes.add(String.format("%-10s %-10s %-10s", letra, frecuencia, puntos));
                }
            }
            
            mensajes.add("");
            mensajes.add("Total de letras: " + (mensajes.size() - 5)); // Restar encabezados
            
            // Mostrar los valores del archivo alpha.txt
            showNotification("GESTIÓN DE DICCIONARIOS > MOSTRAR VALORES ALPHA", 
                            mensajes.toArray(new String[0]));
            
        } catch (IOException e) {
            showNotification("GESTIÓN DE DICCIONARIOS > MOSTRAR VALORES ALPHA", 
                            "Error al leer el archivo alpha.txt: " + e.getMessage());
        }
    }

    /**
     * Método auxiliar para leer una línea de texto con un prompt.
     */
    private static String leerLinea(String prompt) {
        System.out.print(prompt);
        try {
            return readLine();
        } catch (IOException e) {
            System.err.println("Error al leer entrada: " + e.getMessage());
            return "";
        }
    }

    public static void managePartidaMenu() throws IOException, ExceptionPersistenciaFallida{
        boolean volver = false;

        while (!volver) {
            ShowMenu("partida");
            String userCommand = readLine().trim();

            switch (userCommand) {
                case "0":
                    volver = true;
                    ShowMenu("principal");
                    break;
                case "1":
                    System.out.println("Mostrando menu de incializacion...");
                    managePartidaDefinir();
                    break;
                case "2":
                    System.out.println("Mostrando menu de carga de partidas...");
                    managePartidaCargar();                
                    break;
                case "3":
                    System.out.println("Mostrando menu de eliminacion de partidas...");
                    managePartidaEliminar();                
                    break;
                default:
                    System.out.println("¡Introduce alguno de los comandos disponibles!");
                    break;
            }
        }    
    }

    private static void managePartidaEliminar() throws IOException {
        boolean volver = false;

        while (!volver) {
            List<Integer> partidasGuardadas = controladorDomain.getPartidasGuardadas();
            if (partidasGuardadas.isEmpty()) {
                System.out.println("+--------------------------------------+");
                System.out.println("| No hay partidas guardadas disponibles |");
                System.out.println("+--------------------------------------+");

                return;
            } else {
                System.out.println("+--------------------------------------+");
                System.out.println("| PARTIDAS GUARDADAS                   |");
                System.out.println("+--------------------------------------+");
                for (int i = 0; i < partidasGuardadas.size(); i++) {
                    System.out.printf("| %2d. %-30s |\n", i + 1, partidasGuardadas.get(i));
                }
                System.out.println("+--------------------------------------+");
            }

            System.out.println("Selecciona una partida para eliminar [El numero de la partida, no el indice] (0 para volver): ");
            String userCommand = readLine().trim();
            switch (userCommand) {
                case "0":
                    volver = true;
                    System.out.println("Volviendo a Gestión de Partidas...");
                    break;
                default:
                    int partidaIndex;
                    try {
                        partidaIndex = Integer.parseInt(userCommand);
                        if (controladorDomain.eliminarPartidaGuardada(partidaIndex)){
                            System.out.println("Partida eliminada correctamente.");
                        } else {
                            System.out.println("No se pudo eliminar la partida. Intenta de nuevo.");
                        }
                        volver = true;
                    } catch (NumberFormatException e) {
                        System.out.println("Entrada no válida. Intenta de nuevo.");
                        break;
                    }
                    break;
            }
        }
    }

    private static void managePartidaCargar() throws IOException, ExceptionPersistenciaFallida {
        boolean volver = false;

        while (!volver) {
            List<Integer> partidasGuardadas = controladorDomain.getPartidasGuardadas();
            if (partidasGuardadas.isEmpty()) {
                System.out.println("+--------------------------------------+");
                System.out.println("| No hay partidas guardadas disponibles |");
                System.out.println("+--------------------------------------+");

                return;
            } else {
                System.out.println("+--------------------------------------+");
                System.out.println("| PARTIDAS GUARDADAS                   |");
                System.out.println("+--------------------------------------+");
                for (int i = 0; i < partidasGuardadas.size(); i++) {
                    System.out.printf("| %2d. %-30s |\n", i + 1, partidasGuardadas.get(i));
                }
                System.out.println("+--------------------------------------+");
            }

            System.out.println("Selecciona una partida para cargar (0 para volver): ");
            String userCommand = readLine().trim();

            switch (userCommand) {
                case "0":
                    volver = true;
                    System.out.println("Volviendo a Gestión de Partidas...");
                    break;

                default:
                    int partidaIndex;
                    try {
                        partidaIndex = Integer.parseInt(userCommand) - 1;
                        if (partidaIndex < 0 || partidaIndex >= partidasGuardadas.size()) {
                            System.out.println("Índice de partida no válido. Intenta de nuevo.");
                            break;
                        }
                       
                        controladorDomain.cargarPartida(partidaIndex);
                        System.out.println("Partida cargada correctamente.");
                        managePartidaIniciar(null, controladorDomain.getJugadoresActuales(), null, true);
                        volver = true;
                    } catch (NumberFormatException e) {
                        System.out.println("Entrada no válida. Intenta de nuevo.");
                        break;
                    }
                    System.out.println("¡Introduce alguno de los comandos disponibles!");
                    break;
            }
        }
    }

    public static void managePartidaDefinir() throws IOException, ExceptionPersistenciaFallida {
        boolean volver = false;
        String idiomaSeleccionado = "";
        Map<String, Integer> jugadoresSeleccionados = new HashMap<>();
        Integer N = -1;
        String step = "1";

        if (!jugadoresSeleccionados.isEmpty() && idiomaSeleccionado != "" && N != -1) {
            step = "4";
        }

        while (!volver) {
            
            switch (step) {
                case "0":
                    volver = true;
                    System.out.println("Volviendo a Gestión de Partidas...");
                    break;
                case "1":
                    //comprovar parametros configurar o iniciar directamente
                    Tuple<Map<String, Integer>, String> resJugador = managePartidaJugadores();
                    jugadoresSeleccionados = resJugador.x;
                    step = resJugador.y;
                    break;
                case "2":
                    Tuple<String, String> resDiccionario = managePartidaDiccionario();
                    idiomaSeleccionado = resDiccionario.x;
                    step = resDiccionario.y;
                    break;
                case "3":
                    Tuple<Integer, String> resTablero = managePartidaTablero();
                    N = resTablero.x;
                    step = resTablero.y;
                    break;        
                case "4":
                    if (!jugadoresSeleccionados.isEmpty() && idiomaSeleccionado != "" && N != -1) {
                        // Iniciar partida con los parámetros seleccionados
                        managePartidaIniciar(idiomaSeleccionado, jugadoresSeleccionados, N, false);
                        volver = true;
                        System.out.println("+--------------------------------------+");
                        System.out.println("| PARTIDA FINALIZADA                   |");
                        System.out.println("|                                      |");
                        System.out.println("| ¡Gracias por jugar Scrabble!         |");
                        System.out.println("|                                      |");
                        System.out.println("+--------------------------------------+");
                        volver = true;
                    } else {
                        System.out.println("+--------------------------------------+");
                        System.out.println("| INFORMACIÓN                          |");
                        System.out.println("|                                      |");
                        System.out.println("| No se han configurado todos los      |");
                        System.out.println("| parámetros necesarios para iniciar   |");
                        System.out.println("| la partida.                          |");
                        System.out.println("|                                      |");
                        System.out.println("+--------------------------------------+");
                    }
                    break;
                default:
                    System.out.println("¡Introduce alguno de los comandos disponibles!");
                    break;
            }
        }
    }
    public static Tuple<Map<String, Integer>, String> managePartidaJugadores() throws IOException {
        boolean volver = false;
        Map<String, Integer> jugadoresSeleccionados = new HashMap<String, Integer>();
        


        System.out.println("+------------------------------------------------------------------------------+");
        System.out.println("| JUGADORES DISPONIBLES PARA SELECCIONAR                                       |");
        System.out.println("+------------------------------------------------------------------------------+");

        List<String> jugadoresDisponibles = controladorDomain.getUsuariosHumanos();
        List<String> jugadoresIA = controladorDomain.getJugadoresIA();
        
        // Ordenamos alfabéticamente las listas de jugadores
        Collections.sort(jugadoresDisponibles);
        Collections.sort(jugadoresIA);
        
        if (jugadoresDisponibles.isEmpty() && jugadoresIA.isEmpty()) {
            System.out.println("| No hay jugadores disponibles para seleccionar.                              |");
        } else {
            System.out.println("| Jugadores Humanos:                                                           |");
            for (String jugador : jugadoresDisponibles) {
                if (!controladorDomain.isEnPartida(jugador)) {
                    System.out.printf("| - %-74s |\n", jugador);
                }
            }
            
            System.out.println("|                                                                              |");
            System.out.println("| Jugadores IA:                                                                |");
            for (String jugadorIA : jugadoresIA) {
                if (!controladorDomain.isEnPartida(jugadorIA)) {
                    System.out.printf("| - %-74s |\n", jugadorIA);
                }
            }
        }

        System.out.println("+------------------------------------------------------------------------------+");

        while (!volver) {
            ShowMenu("partidainiciarjugadores");
            String userCommand = readLine().trim();

            switch (userCommand) {
                case "0":
                    volver = true;
                    System.out.println("Volviendo a Gestión de Partidas...");
                    break;

                case "1":
                    if (jugadoresSeleccionados.isEmpty()) {
                        System.out.println("+--------------------------------------+");
                        System.out.println("| INFORMACIÓN                          |");
                        System.out.println("|                                      |");
                        System.out.println("| No hay jugadores seleccionados.      |");
                        System.out.println("|                                      |");
                        System.out.println("+--------------------------------------+");
                        break;
                    } else {
                        System.out.println("+--------------------------------------+");
                        System.out.println("| JUGADORES SELECCIONADOS              |");
                        System.out.println("+--------------------------------------+");
                        for (String jugador : jugadoresSeleccionados.keySet()) {
                            System.out.printf("| - %-32s |\n", jugador);
                        }
                        System.out.println("+--------------------------------------+");
                        volver = true;
                        return new Tuple<Map<String, Integer>, String>(jugadoresSeleccionados, "2"); // Salir del bucle y devolver los jugadores seleccionados
                    }
                case "2":
                    // Mostrar jugadores existentes
                    if (jugadoresSeleccionados == null || jugadoresSeleccionados.isEmpty()) {
                        System.out.println("+--------------------------------------+");
                        System.out.println("| INFORMACIÓN                          |");
                        System.out.println("|                                      |");
                        System.out.println("| No hay jugadores seleccionados.      |");
                        System.out.println("|                                      |");
                        System.out.println("+--------------------------------------+");
                    } else {
                        System.out.println("+--------------------------------------+");
                        System.out.println("| JUGADORES SELECCIONADOS              |");
                        System.out.println("+--------------------------------------+");
                        for (String jugador : jugadoresSeleccionados.keySet()) {
                            System.out.printf("| - %-35s |\n", jugador);
                        }
                        System.out.println("+--------------------------------------+");
                    }
                    break;
                case "3":
                    // Añadir jugador
                    System.out.println("+------------------------------------------------------------------------------+");
                    System.out.println("| AÑADIR JUGADOR                                                               |");
                    System.out.println("+------------------------------------------------------------------------------+");
                    System.out.println("| Introduce el nombre de usuario del jugador:                                  |");
                    System.out.println("+------------------------------------------------------------------------------+");
                    
                    String nombre = readLine();

                    try {
                        if (!controladorDomain.existeJugador(nombre)) {
                            throw new ExceptionUserNotExist(nombre);
                        }

                        if (controladorDomain.isEnPartida(nombre)) {
                            throw new ExceptionUserInGame(nombre);
                        }
                        
                        // El nombre es también el ID en el nuevo sistema
                        jugadoresSeleccionados.put(nombre, 0); // Añadir jugador a la lista de seleccionados

                        // Mostrar mensaje formateado en caja
                        showNotification("GESTIÓN DE JUGADORES > AÑADIR JUGADOR ",
                                "✓ El usuario '" + nombre + "' ha sido añadido correctamente a la partida.");
                        
                        } catch (ExceptionUserNotExist e) {
                            showNotification("GESTIÓN DE JUGADORES > AÑADIR JUGADOR",
                                    "✗ Error: El usuario '" + nombre + "' no existe.");
                        } catch (ExceptionUserInGame e) {
                            showNotification("GESTIÓN DE JUGADORES > AÑADIR JUGADOR",
                                    "✗ Error: El usuario '" + nombre + "' ya está en una partida.");
                        }
                        break;
                case "4":
                    // Eliminar jugador
                    System.out.println("+------------------------------------------------------------------------------+");
                    System.out.println("| GESTIÓN DE JUGADORES > ELIMINAR JUGADOR                                      |");
                    System.out.println("+------------------------------------------------------------------------------+");
                    System.out.println("| Introduce el nombre de usuario del jugador a eliminar:                       |");
                    System.out.println("+------------------------------------------------------------------------------+");

                    String nombreEliminar = readLine();

                    if (jugadoresSeleccionados.containsKey(nombreEliminar)) {
                        jugadoresSeleccionados.remove(nombreEliminar);
                        showNotification("GESTIÓN DE JUGADORES > ELIMINAR JUGADOR",
                                "✓ El usuario '" + nombreEliminar + "' ha sido eliminado correctamente de la partida.");
                    } else {
                        showNotification("GESTIÓN DE JUGADORES > ELIMINAR JUGADOR",
                                "✗ Error: El usuario '" + nombreEliminar + "' no está en la lista de jugadores seleccionados.");
                    }
                    break;
                case "5":
                    // Mostrar jugadores disponibles para seleccionar
                    System.out.println("+------------------------------------------------------------------------------+");
                    System.out.println("| JUGADORES DISPONIBLES PARA SELECCIONAR                                       |");
                    System.out.println("+------------------------------------------------------------------------------+");
                    
                    // Obtener listas actualizadas y ordenarlas
                    List<String> jugadoresDisponiblesActualizados = controladorDomain.getUsuariosHumanos();
                    List<String> jugadoresIAActualizados = controladorDomain.getJugadoresIA();
                    Collections.sort(jugadoresDisponiblesActualizados);
                    Collections.sort(jugadoresIAActualizados);
                    
                    if (jugadoresDisponiblesActualizados.isEmpty() && jugadoresIAActualizados.isEmpty()) {
                        System.out.println("| No hay jugadores disponibles para seleccionar.                              |");
                    } else {
                        System.out.println("| Jugadores Humanos:                                                           |");
                        for (String jugador : jugadoresDisponiblesActualizados) {
                            if (!controladorDomain.isEnPartida(jugador)) {
                                System.out.printf("| - %-74s |\n", jugador);
                            }
                        }
                        
                        System.out.println("|                                                                              |");
                        System.out.println("| Jugadores IA:                                                                |");
                        for (String jugadorIA : jugadoresIAActualizados) {
                            if (!controladorDomain.isEnPartida(jugadorIA)) {
                                System.out.printf("| - %-74s |\n", jugadorIA);
                            }
                        }
                    }

                    System.out.println("+------------------------------------------------------------------------------+");
                    break;
                
                default:
                    System.out.println("¡Introduce alguno de los comandos disponibles!");
                    break;
            }
        }

        return new Tuple<Map<String, Integer>,String>(new HashMap<>(), "0"); // Devolver un conjunto vacío si no se seleccionan jugadores
    }

    public static Tuple<Integer, String> managePartidaTablero() throws IOException {
        boolean volver = false;
        int N = 15; // Valor por defecto, se puede cambiar en el menú


        System.out.println("+--------------------------------------+");
        System.out.println("| INFORMACIÓN                          |");
        System.out.println("|                                      |");
        System.out.println("| El tamaño actual del tablero es:     |");
        System.out.println("| " + N + "x" + N + " ".repeat(36 - String.valueOf(N).length() * 2) + "|");
        System.out.println("|                                      |");
        System.out.println("+--------------------------------------+");
    
        while (!volver) {
            ShowMenu("partidainiciartablero");
            String userCommand = readLine().trim();

            switch (userCommand) {
                case "0":
                    volver = true;
                    System.out.println("Volviendo a Gestión de Partidas...");
                    break;
                case "1":
                    volver = true;
                    return new Tuple<Integer, String>(N, "4"); // Salir del bucle y devolver el tamaño del tablero
                case "2":
                    System.out.println("+--------------------------------------+");
                    System.out.println("| INFORMACIÓN                          |");
                    System.out.println("|                                      |");
                    System.out.println("| El tamaño actual del tablero es:     |");
                    System.out.println("| " + N + "x" + N + " ".repeat(36 - String.valueOf(N).length() * 2) + "|");
                    System.out.println("|                                      |");
                    System.out.println("+--------------------------------------+");
                    break;
                case "3":
                    System.out.println("Introduce el tamaño del tablero (N x N) [Solo un numero > 15]: ");
                    try {
                        if (N < 15) {
                            System.out.println("El tamaño del tablero debe ser mayor o igual a 15. Inténtalo de nuevo.");
                            break;
                        }
                        N = Integer.parseInt(readLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Número inválido. Inténtalo de nuevo.");
                    }

                    System.out.println("+--------------------------------------+");
                    System.out.println("| INFORMACIÓN                          |");
                    System.out.println("|                                      |");
                    System.out.println("| El tamaño actual del tablero es:    |");
                    System.out.println("| " + N + "x" + N + " ".repeat(34 - String.valueOf(N).length() * 2) + "|");
                    System.out.println("|                                      |");
                    System.out.println("+--------------------------------------+");

                    break;
                
                default:
                System.out.println("¡Introduce alguno de los comandos disponibles!");
                break;
            }
        }
        return new Tuple<Integer, String>(15, "2"); // Devolver el tamaño por defecto si no se selecciona nada
    }

    public static Tuple<String, String> managePartidaDiccionario() throws IOException {
        boolean volver = false;
        String idiomaSeleccionado = "";
        printDiccionariosDisponibles();
        while (!volver) {
            ShowMenu("partidainiciardiccionario");
            String userCommand = readLine().trim();

            switch (userCommand) {
                case "0":
                    volver = true;
                    System.out.println("Volviendo a Gestión de Partidas...");
                    break;
                case "1":
                    if (idiomaSeleccionado != "") {
                        System.out.println("+------------------------------------------------------------------------------+");
                        System.out.println("| INFORMACIÓN                                                                  |");
                        System.out.println("|                                                                              |");
                        System.out.println("  | Diccionario seleccionado correctamente: " + idiomaSeleccionado + " ".repeat(83 - 46 - idiomaSeleccionado.length()) + "|");
                        System.out.println("|                                                                              |");
                        System.out.println("+------------------------------------------------------------------------------+");
                        volver = true;
                        return new Tuple<String,String>(idiomaSeleccionado, "3"); // Salir del bucle y devolver el idioma seleccionado
                    } else {
                        System.out.println("+------------------------------------------------------------------------------+");
                        System.out.println("| INFORMACIÓN                                                                  |");
                        System.out.println("|                                                                              |");
                        System.out.println("| No se ha seleccionado ningún diccionario.                                    |");
                        System.out.println("|                                                                              |");
                        System.out.println("+------------------------------------------------------------------------------+");
                        break;
                    }
                case "2":
                    if (idiomaSeleccionado != "") {
                        System.out.println("+------------------------------------------------------------------------------+");
                        System.out.println("| INFORMACIÓN                                                                  |");
                        System.out.println("|                                                                              |");
                        System.out.println("  | Diccionario seleccionado correctamente: " + idiomaSeleccionado + " ".repeat(78 - 46 - idiomaSeleccionado.length()) + "|");
                        System.out.println("|                                                                              |");
                        System.out.println("+------------------------------------------------------------------------------+");
                    } else {
                        System.out.println("+------------------------------------------------------------------------------+");
                        System.out.println("| INFORMACIÓN                                                                  |");
                        System.out.println("|                                                                              |");
                        System.out.println("| No se ha seleccionado ningún diccionario.                                    |");
                        System.out.println("|                                                                              |");
                        System.out.println("+------------------------------------------------------------------------------+");
                    }
                    break;

                case "3":
                    System.out.println("Introduce el nombre del diccionario a seleccionar: ");
                    idiomaSeleccionado = readLine();
                    
                    try {
                        if (!controladorDomain.existeDiccionario(idiomaSeleccionado)) {
                            System.out.println("+------------------------------------------------------------------------------+");
                            System.out.println("| ERROR                                                                        |");
                            System.out.println("|                                                                              |");
                            System.out.println("| Diccionario no encontrado. Asegúrate de que el nombre sea correcto.          |");
                            System.out.println("|                                                                              |");
                            System.out.println("+------------------------------------------------------------------------------+");
                            idiomaSeleccionado = "";
                            break;
                        }
                        // controladorDomain.setLenguaje(idiomaSeleccionado);
                        System.out.println("+------------------------------------------------------------------------------+");
                        System.out.println("| INFORMACIÓN                                                                  |");
                        System.out.println("|                                                                              |");
                        System.out.println("| Diccionario seleccionado correctamente: " + idiomaSeleccionado + " ".repeat(78 - 46 - idiomaSeleccionado.length()) + "|");
                        System.out.println("|                                                                              |");
                        System.out.println("+------------------------------------------------------------------------------+");
                    } catch (ExceptionLanguageNotExist e) {
                        System.out.println("+------------------------------------------------------------------------------+");
                        System.out.println("| ERROR                                                                        |");
                        System.out.println("|                                                                              |");
                        System.out.println("| Diccionario no encontrado. Asegúrate de que el nombre sea correcto.          |");
                        System.out.println("|                                                                              |");
                        System.out.println("+------------------------------------------------------------------------------+");
                        idiomaSeleccionado = null;
                    }
                    break;
                case "4":
                    printDiccionariosDisponibles();
                    break;
                default:
                    System.out.println("¡Introduce alguno de los comandos disponibles!");
                    break;
            }
        }
        return new Tuple<String,String>(idiomaSeleccionado, "1");
    }




    
    /**
     * Gestiona el menú de ranking, permitiendo ver, filtrar o eliminar jugadores del ranking.
     */
    public static void manageRankingMenu() throws IOException {
        String opcion;
        
        do {
            // Usar el menú formateado predefinido
            ShowMenu("RANKING");
            
            opcion = readLine();
            
            switch (opcion) {
                case "1": // Ver ranking
                    manageRankingVer();
                    break;
                case "2": // Ver historial de puntuaciones
                    manageRankingHistorial();
                    break;
                case "3": // Filtrar ranking
                    manageRankingFiltrar();
                    break;
                case "4": // Eliminar jugador del ranking
                    manageRankingEliminarJugador();
                    break;                
                case "0": // Volver al menú principal
                    break;
                default:
                    System.out.println("Opción no válida. Inténtelo de nuevo.");
            }
        } while (!opcion.equals("0"));
        
        
        // Mostrar el menú principal al volver
        ShowMenu("principal");
    }

    /**
     * Permite eliminar un jugador del sistema de ranking sin eliminarlo del sistema.
     */
    public static void manageRankingEliminarJugador() throws IOException {
        System.out.println("\n+-------------------------------------------------------------------------------+");
        System.out.println("| GESTIÓN DE RANKING > ELIMINAR JUGADOR DEL RANKING                             |");
        System.out.println("+-------------------------------------------------------------------------------+");
        System.out.println("| Esta acción eliminará al jugador del ranking, pero seguirá registrado         |");
        System.out.println("| en el sistema. Si desea eliminar completamente un jugador, use la opción      |");
        System.out.println("| de 'Eliminar jugador' en el menú de Gestión de Jugadores.                     |");
        System.out.println("+-------------------------------------------------------------------------------+");
        
        // Mostrar jugadores en el ranking
        List<String> jugadoresRanking = controladorDomain.getUsuariosRanking();
        
        if (jugadoresRanking.isEmpty()) {
            showNotification("Ranking vacío", "No hay jugadores en el ranking actualmente.");
            return;
        }
        
        System.out.println("+-------------------------------------------------------------------------------+");
        System.out.println("| JUGADORES EN EL RANKING                                                       |");
        System.out.println("+-------------------------------------------------------------------------------+");
        for (int i = 0; i < jugadoresRanking.size(); i++) {
            System.out.printf("| %-2d. %-73s |\n", (i + 1), jugadoresRanking.get(i));
        }
        System.out.println("+-------------------------------------------------------------------------------+");
        
        // Solicitar número del jugador a eliminar
        System.out.println("+-------------------------------------------------------------------------------+");
        System.out.println("| Seleccione el número del jugador a eliminar (0 para cancelar):                |");
        System.out.println("+-------------------------------------------------------------------------------+");
        String seleccion = readLine().trim();
        
        // Verificar cancelación
        if (seleccion.equals("0")) {
            System.out.println("Operación cancelada.");
            return;
        }
        
        // Validar la entrada y convertir a índice
        int indice;
        try {
            indice = Integer.parseInt(seleccion) - 1; // Restamos 1 porque mostramos desde 1 pero el índice empieza en 0
            if (indice < 0 || indice >= jugadoresRanking.size()) {
                showNotification("Error", "Número de jugador inválido. Debe estar entre 1 y " + jugadoresRanking.size());
                return;
            }
        } catch (NumberFormatException e) {
            showNotification("Error", "Debe introducir un número válido.");
            return;
        }
        
        // Obtener el nombre del jugador seleccionado
        String nombreJugador = jugadoresRanking.get(indice);
        
        // Confirmar eliminación
        System.out.println("\n+-----------------------------------------------------------------------------+");
        System.out.printf("| ¿Está seguro de eliminar a '%s' del ranking? (s/n): %-28s|\n", nombreJugador, "");
        System.out.println("+-------------------------------------------------------------------------------+");
        String confirmacion = readLine().trim().toLowerCase();
        
        if (confirmacion.equals("s")) {
            try {
                // Verificar estado antes de la eliminación
                boolean existeAntes = controladorDomain.perteneceRanking(nombreJugador);
                
                // Intentar eliminar jugador del ranking
                boolean eliminado = controladorDomain.eliminarUsuarioRanking(nombreJugador);
                
                
                // Verificar estado después de la eliminación
                boolean existeDespues = controladorDomain.perteneceRanking(nombreJugador);
                
                if (eliminado && !existeDespues) {
                    showNotification("OPERACIÓN EXITOSA", 
                                   "El jugador '" + nombreJugador + "' ha sido eliminado del ranking.",
                                   "Su puntuación total ha sido reseteada a 0.");
                } else {
                    String mensaje;
                    if (!existeAntes) {
                        mensaje = "El jugador no estaba en el ranking antes de la operación.";
                    } else if (existeDespues) {
                        mensaje = "Error: El jugador sigue en el ranking después de intentar eliminarlo.";
                    } else {
                        mensaje = "Error: Falló la operación, pero el jugador ya no está en el ranking.";
                    }
                    
                    showNotification("ERROR", 
                                   "No se pudo eliminar al jugador '" + nombreJugador + "' del ranking.",
                                   mensaje);
                }
            } catch (Exception e) {
                showNotification("ERROR", 
                               "Error al eliminar jugador del ranking: " + e.getMessage(),
                               "Por favor, inténtelo de nuevo o contacte con soporte.");
                e.printStackTrace();
            }
        } else {
            System.out.println("\nOperación cancelada.");
        }
        
    }

    /**
     * Muestra el ranking de jugadores según la estrategia actual.
     */
    public static void manageRankingVer() throws IOException {
        boolean volver = false;
        String criterioActual = "total"; // Criterio por defecto cambiado a puntuación total
        
        while (!volver) {
            // Obtener la lista ordenada de usuarios según el criterio actual
            List<String> rankingOrdenado = controladorDomain.getRanking(criterioActual);
            
            // Obtener el nombre de la estrategia para mostrar en el título
            String nombreEstrategia = controladorDomain.getEstrategiaRanking();
            
            // Convertir a formato abreviado más claro
            String estrategiaFormateada;
            switch (criterioActual) {
                case "maxima":
                    estrategiaFormateada = "P.MÁXIMA";
                    break;
                case "media":
                    estrategiaFormateada = "P.MEDIA";
                    break;
                case "partidas":
                    estrategiaFormateada = "PARTIDAS";
                    break;
                case "victorias":
                    estrategiaFormateada = "VICTORIAS";
                    break;
                case "total":
                    estrategiaFormateada = "P.TOTAL";
                    break;
                default:
                    estrategiaFormateada = nombreEstrategia.toUpperCase();
            }
            
            // Calcular espaciado correcto para el título
            int espaciosTitulo = (97 - estrategiaFormateada.length() - 27) / 2; // 27 es la longitud de "RANKING DE JUGADORES ()"
            String espaciosIzquierda = " ".repeat(espaciosTitulo);
            // Asegurar que el espaciado a la derecha completa exactamente 78 caracteres
            int espaciosDerecha = 97 - espaciosTitulo - 27 - estrategiaFormateada.length() - 1; // -1 por el pipe al final
            
            // Limpiamos la pantalla con varias líneas en blanco para minimizar interferencias
            System.out.println("\n");
            
            // Construir toda la salida como un solo bloque de texto
            StringBuilder output = new StringBuilder();
            
            // Encabezado y título
            output.append("+--------------------------------------------------------------------------------------------+\n");
            output.append("|").append(espaciosIzquierda).append("RANKING DE JUGADORES (").append(estrategiaFormateada).append(")");
            output.append(" ".repeat(espaciosDerecha)).append("|\n");
            output.append("+--------------------------------------------------------------------------------------------+\n");
            

            output.append("| Posición | Jugador        | P. Total    | P. Máxima   | P. Media    | Partidas | Victorias |\n");
            output.append("+----------+----------------+-------------+-------------+-------------+----------+-----------+\n");
            
            // Contenido de la tabla
            int posicion = 1;
            for (String id : rankingOrdenado) {
                // Obtener estadísticas del jugador para la tabla
                int puntuacionTotal = controladorDomain.getPuntuacionTotal(id);
                int puntuacionMaxima = controladorDomain.getPuntuacionMaxima(id);
                double puntuacionMedia = controladorDomain.getPuntuacionMedia(id);
                int partidas = controladorDomain.getPartidasJugadas(id);
                int victorias = controladorDomain.getVictorias(id);
                
                // Construir la fila con formato adecuado
                String fila = String.format("| %-8d | %-14s | %-11d | %-11d | %-11.2f | %-8d | %-9d |",
                                           posicion, id, puntuacionTotal, puntuacionMaxima, puntuacionMedia, partidas, victorias);
                output.append(fila).append("\n");
                posicion++;
            }
            
            output.append("+--------------------------------------------------------------------------------------------+\n");
            output.append("+------------------------------------------------------------------------------------+\n");
            output.append("| GESTIÓN DE RANKING > VER RANKING                                                   |\n");
            output.append("| OPCIONES DE ORDENACIÓN                                                             |\n");
            output.append("|                                                                                    |\n");
            output.append("|   [ 1 ] Ordenar por puntuación total                                               |\n");
            output.append("|   [ 2 ] Ordenar por puntuación máxima                                              |\n");
            output.append("|   [ 3 ] Ordenar por puntuación media                                               |\n");
            output.append("|   [ 4 ] Ordenar por partidas   jugadas                                             |\n");
            output.append("|   [ 5 ] Ordenar por victorias                                                      |\n");
            output.append("|   [ 0 ] Volver                                                                     |\n");
            output.append("|                                                                                    |\n");
            output.append("+------------------------------------------------------------------------------------+\n");
            
            // Mostrar toda la salida de una vez
            System.out.println(output.toString());
            
            // Lectura de opción
            String opcion = readLine().trim();
            
            switch (opcion) {

                case "1":
                    criterioActual = "total";
                    controladorDomain.cambiarEstrategiaRanking(criterioActual);
                break;
                case "2":
                    criterioActual = "maxima";
                    controladorDomain.cambiarEstrategiaRanking(criterioActual);
                break;
                case "3":
                    criterioActual = "media";
                    controladorDomain.cambiarEstrategiaRanking(criterioActual);
                    break;
                case "4":
                    criterioActual = "partidas";
                    controladorDomain.cambiarEstrategiaRanking(criterioActual);
                    break;
                case "5":
                    criterioActual = "victorias";
                    controladorDomain.cambiarEstrategiaRanking(criterioActual);
                    break;
                case "0":
                    volver = true;
                    break;
                default:
                    System.out.println("Opción no válida. Inténtelo de nuevo.");
            }
        }
        
        
        // Mostrar el menú principal al volver
        ShowMenu("principal");
    }

    /**
     * Consulta el historial de un jugador específico.
     */
    public static void manageRankingHistorial() throws IOException {
        boolean volver = false;
        System.out.println("\n");
        while (!volver) {
            System.out.println("""
                +------------------------------------------------------------------------------+
                | CONSULTAR HISTORIAL DE JUGADOR                                               |
                |                                                                              |
                |   Introduce el nombre del jugador (o '0' para volver):                       |
                |                                                                              |
                +------------------------------------------------------------------------------+
                """);
                
            String username = readLine().trim();
            
            if (username.equals("0")) {
                volver = true;
                continue;
            }
            
            if (controladorDomain.perteneceRanking(username)) {
                // Obtener los datos del jugador
                List<Integer> puntuaciones = controladorDomain.getPuntuacionesUsuario(username);
                int puntuacionMaxima = controladorDomain.getPuntuacionMaxima(username);
                double puntuacionMedia = controladorDomain.getPuntuacionMedia(username);
                int partidasJugadas = controladorDomain.getPartidasJugadas(username);
                int victorias = controladorDomain.getVictorias(username);
                int puntuacionTotal = controladorDomain.getPuntuacionTotalDirecta(username);
                
                // Mostrar la información del jugador
                System.out.println("+------------------------------------------------------------------------------+");
                System.out.println("| HISTORIAL DEL JUGADOR: " + username + generateSpaces(username.length()) + "|");
                System.out.println("+------------------------------------------------------------------------------+");
                System.out.println("| Estadísticas generales:");
                System.out.println("| - Puntuación total acumulada: " + puntuacionTotal);
                System.out.println("| - Puntuación máxima: " + puntuacionMaxima);
                System.out.println("| - Puntuación media: " + String.format("%.2f", puntuacionMedia));
                System.out.println("| - Partidas jugadas: " + partidasJugadas);
                System.out.println("| - Victorias: " + victorias);
                if (partidasJugadas > 0) {
                    double ratioVictorias = (double) victorias / partidasJugadas * 100;
                    System.out.println("| - Ratio de victorias: " + String.format("%.2f%%", ratioVictorias));
                }
                
                // Mostrar historial de puntuaciones
                System.out.println("| ");
                System.out.println("| Historial de puntuaciones:");
                if (puntuaciones.isEmpty()) {
                    System.out.println("| No hay puntuaciones registradas.");
                } else {
                    System.out.println("| ");
                    for (int i = 0; i < puntuaciones.size(); i++) {
                        System.out.println("| Partida " + (i+1) + ": " + puntuaciones.get(i) + " puntos");
                    }
                }
               
                System.out.println("+------------------------------------------------------------------------------+");
                System.out.println("| INFORMACIÓN                                                                  |");
                System.out.println("|                                                                              |");
                System.out.println("| Presiona Enter para continuar...                                             |");
                System.out.println("|                                                                              |");
                System.out.println("+------------------------------------------------------------------------------+");
                readLine();
            } else {
                System.out.println("+------------------------------------------------------------------------------+");
                System.out.println("| ERROR                                                                        |");
                System.out.println("|                                                                              |");
                System.out.println("| El usuario '" + username + "' no está en el ranking.                           |");
                System.out.println("|                                                                              |");
                System.out.println("+------------------------------------------------------------------------------+");
                System.out.println("+------------------------------------------------------------------------------+");
                System.out.println("| INFORMACIÓN                                                                  |");
                System.out.println("|                                                                              |");
                System.out.println("| Presiona Enter para continuar...                                             |");
                System.out.println("|                                                                              |");
                System.out.println("+------------------------------------------------------------------------------+");
                readLine();
            }
        }
    }

    /**
     * Filtra el ranking según criterios específicos.
     */
    public static void manageRankingFiltrar() throws IOException {
        boolean volver = false;
        
        while (!volver) {
            System.out.println("\n");
            System.out.println("""
                +------------------------------------------------------------------------------+
                | GESTIÓN DE RANKING > FILTRAR RANKING                                         |
                |                                                                              |
                |   [ 1 ] Filtrar por puntuación mínima                                        |
                |   [ 2 ] Filtrar por partidas jugadas mínimas                                 |
                |   [ 3 ] Filtrar por victorias mínimas                                        |
                |   [ 0 ] Volver                                                               |
                |                                                                              |
                +------------------------------------------------------------------------------+
                """);
                
            String opcion = readLine().trim();
            
            switch (opcion) {
                case "0":
                    volver = true;
                    break;
                case "1":
                    filtrarPorPuntuacionMinima();
                    break;
                case "2":
                    filtrarPorPartidasMinimasJugadas();
                    break;
                case "3":
                    filtrarPorVictoriasMinimas();
                    break;
                default:
                    System.out.println("+------------------------------------------------------------------------------+");
                    System.out.println("| ERROR                                                                        |");
                    System.out.println("|                                                                              |");
                    System.out.println("| Opción no válida. Inténtalo de nuevo.                                        |");
                    System.out.println("|                                                                              |");
                    System.out.println("+------------------------------------------------------------------------------+");
                    break;
            }
        }
    }

    /**
     * Filtra el ranking por puntuación mínima.
     */
    private static void filtrarPorPuntuacionMinima() throws IOException {
        System.out.println("+------------------------------------------------------------------------------+");
        System.out.println("| FILTRAR POR PUNTUACIÓN MÍNIMA                                                |");
        System.out.println("|                                                                              |");
        System.out.println("| Introduce la puntuación mínima:                                              |");
        System.out.println("|                                                                              |");
        System.out.println("+------------------------------------------------------------------------------+");
        
        try {
            int puntuacionMinima = Integer.parseInt(readLine().trim());
            
            // Obtener los usuarios
            List<String> usuarios = controladorDomain.getUsuariosRanking();
            List<String> usuariosFiltrados = new ArrayList<>();
            
            // Filtrar por puntuación mínima
            for (String id : usuarios) {
                if (controladorDomain.getPuntuacionMaxima(id) >= puntuacionMinima) {
                    usuariosFiltrados.add(id);
                }
            }
            
            // Mostrar el ranking filtrado
            mostrarRankingFiltrado(usuariosFiltrados, "Puntuación mínima: " + puntuacionMinima);
        } catch (NumberFormatException e) {
            System.out.println("+------------------------------------------------------------------------------+");
            System.out.println("| ERROR                                                                        |");
            System.out.println("|                                                                              |");
            System.out.println("| Introduce un número válido.                                                  |");
            System.out.println("|                                                                              |");
            System.out.println("+------------------------------------------------------------------------------+");
            System.out.println("+------------------------------------------------------------------------------+");
            System.out.println("| INFORMACIÓN                                                                  |");
            System.out.println("|                                                                              |");
            System.out.println("| Presiona Enter para continuar...                                             |");
            System.out.println("|                                                                              |");
            System.out.println("+------------------------------------------------------------------------------+");
            readLine();
        }
    }

    /**
     * Filtra el ranking por número mínimo de partidas jugadas.
     */
    private static void filtrarPorPartidasMinimasJugadas() throws IOException {
        System.out.println("+------------------------------------------------------------------------------+");
        System.out.println("| FILTRAR POR PARTIDAS MÍNIMAS                                                 |");
        System.out.println("|                                                                              |");
        System.out.println("| Introduce el número mínimo de partidas jugadas:                              |");
        System.out.println("|                                                                              |");
        System.out.println("+------------------------------------------------------------------------------+");
        
        try {
            int partidasMinimas = Integer.parseInt(readLine().trim());
            
            // Obtener los usuarios
            List<String> usuarios = controladorDomain.getUsuariosRanking();
            List<String> usuariosFiltrados = new ArrayList<>();
            
            // Filtrar por partidas mínimas
            for (String id : usuarios) {
                if (controladorDomain.getPartidasJugadas(id) >= partidasMinimas) {
                    usuariosFiltrados.add(id);
                }
            }
            
            // Mostrar el ranking filtrado
            mostrarRankingFiltrado(usuariosFiltrados, "Partidas mínimas jugadas: " + partidasMinimas);
        } catch (NumberFormatException e) {
            System.out.println("+------------------------------------------------------------------------------+");
            System.out.println("| ERROR                                                                        |");
            System.out.println("|                                                                              |");
            System.out.println("| Introduce un número válido.                                                  |");
            System.out.println("|                                                                              |");
            System.out.println("+------------------------------------------------------------------------------+");
            System.out.println("+------------------------------------------------------------------------------+");
            System.out.println("| INFORMACIÓN                                                                  |");
            System.out.println("|                                                                              |");
            System.out.println("| Presiona Enter para continuar...                                             |");
            System.out.println("|                                                                              |");
            System.out.println("+------------------------------------------------------------------------------+");
            readLine();
        }
    }

    /**
     * Filtra el ranking por victorias mínimas.
     */
    private static void filtrarPorVictoriasMinimas() throws IOException {
        System.out.println("+------------------------------------------------------------------------------+");
        System.out.println("| FILTRAR POR VICTORIAS MÍNIMAS                                                |");
        System.out.println("|                                                                              |");
        System.out.println("| Introduce el número mínimo de victorias:                                     |");
        System.out.println("|                                                                              |");
        System.out.println("+------------------------------------------------------------------------------+");
        
        try {
            int victoriasMinimas = Integer.parseInt(readLine().trim());
            
            // Obtener los usuarios
            List<String> usuarios = controladorDomain.getUsuariosRanking();
            List<String> usuariosFiltrados = new ArrayList<>();
            
            // Filtrar por victorias mínimas
            for (String id : usuarios) {
                if (controladorDomain.getVictorias(id) >= victoriasMinimas) {
                    usuariosFiltrados.add(id);
                }
            }
            
            // Mostrar el ranking filtrado
            mostrarRankingFiltrado(usuariosFiltrados, "Victorias mínimas: " + victoriasMinimas);
        } catch (NumberFormatException e) {
            System.out.println("+------------------------------------------------------------------------------+");
            System.out.println("| ERROR                                                                        |");
            System.out.println("|                                                                              |");
            System.out.println("| Introduce un número válido.                                                  |");
            System.out.println("|                                                                              |");
            System.out.println("+------------------------------------------------------------------------------+");
            System.out.println("+------------------------------------------------------------------------------+");
            System.out.println("| INFORMACIÓN                                                                  |");
            System.out.println("|                                                                              |");
            System.out.println("| Presiona Enter para continuar...                                             |");
            System.out.println("|                                                                              |");
            System.out.println("+------------------------------------------------------------------------------+");
            readLine();
        }
    }

    /**
     * Muestra un ranking filtrado con los usuarios especificados.
     */
    private static void mostrarRankingFiltrado(List<String> usuariosFiltrados, String criterioDeFiltrado) throws IOException {
        // Ordenar los usuarios filtrados según la estrategia actual
        String criterioActual = controladorDomain.getEstrategiaActual();
        
        // Reordenar la lista de usuarios filtrados según el criterio actual usando el controlador
        List<String> usuariosOrdenados = controladorDomain.ordenarUsuariosPorCriterio(usuariosFiltrados, criterioActual);
        
        // Mostrar el encabezado del ranking
        System.out.println("+------------------------------------------------------------------------------------+");
        System.out.println("| RANKING FILTRADO - " + criterioDeFiltrado + "                                        |");
        System.out.println("+------------------------------------------------------------------------------------+");
        System.out.println("| Posición | Usuario             | Punt. Máxima | Punt. Media | Partidas | Victorias |");
        System.out.println("+----------+---------------------+--------------+-------------+----------+-----------+");
        
        if (usuariosOrdenados.isEmpty()) {
            System.out.println("| No hay usuarios que cumplan con el criterio de filtrado.                      |");
        } else {
            // Mostrar cada usuario en el ranking
            int posicion = 1;
            for (String id : usuariosOrdenados) {
                int puntuacionMaxima = controladorDomain.getPuntuacionMaxima(id);
                double puntuacionMedia = controladorDomain.getPuntuacionMedia(id);
                int partidas = controladorDomain.getPartidasJugadas(id);
                int victorias = controladorDomain.getVictorias(id);
                
                System.out.printf("| %-8d | %-19s | %-12d | %-11.2f | %-8d | %-9d |\n", 
                                posicion++, id, puntuacionMaxima, puntuacionMedia, partidas, victorias);
            }
        }
        
        System.out.println("+------------------------------------------------------------------------------------+");
        System.out.println("+------------------------------------------------------------------------------------+");
        System.out.println("| INFORMACIÓN                                                                        |");
        System.out.println("|                                                                                    |");
        System.out.println("| Presiona Enter para continuar...                                                   |");
        System.out.println("|                                                                                    |");
        System.out.println("+------------------------------------------------------------------------------------+");
        readLine();
    }

    /**
     * Genera los espacios necesarios para alinear correctamente el borde derecho.
     * @param usernameLength Longitud del nombre de usuario
     * @return Cadena con los espacios necesarios
     */
    private static String generateSpaces(int usernameLength) {
        // La longitud total disponible es de 62 caracteres (78 - 16)
        // 16 es la longitud de "| HISTORIAL DEL JUGADOR: "
        int espaciosNecesarios = 62 - usernameLength;
        return " ".repeat(Math.max(0, espaciosNecesarios));
    }

    /**
     * Muestra los diccionarios disponibles.
     */
    public static void printDiccionariosDisponibles() {
        System.out.println("+------------------------------------------------------------------------------+");
        System.out.println("| DICCIONARIOS DISPONIBLES                                                    |");
        System.out.println("+------------------------------------------------------------------------------+");
        
        List<String> diccDisponibles = controladorDomain.getDiccionariosDisponibles();
        
        // Ordenar los diccionarios alfabéticamente
        Collections.sort(diccDisponibles);
        
        if (diccDisponibles.isEmpty()) {
            System.out.println("| No hay ningún diccionario disponible :(                                     |");
        } else {
            for (String nombre : diccDisponibles) {
                System.out.printf("| - %-74s |\n", nombre);
            }
        }
        
        System.out.println("+------------------------------------------------------------------------------+");
    }

    // Add this helper method after ShowMenu method
    /**
     * Muestra una notificación formateada con un borde y un título.
     * @param title Título de la notificación (se mostrará en la esquina superior izquierda)
     * @param messages Mensajes a mostrar en la notificación
     */
    public static void showNotification(String title, String... messages) {
        final int WIDTH = 78; // Ancho total de la caja
        
        System.out.println("+" + "-".repeat(WIDTH) + "+");
        
        // Limitar la longitud del título para evitar desbordamientos
        String displayTitle = title.length() > WIDTH - 4 ? title.substring(0, WIDTH - 7) + "..." : title;
        
        // Centrado del título si es "GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO"
        if (title.equals("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO")) {
            int spaces = (WIDTH - displayTitle.length()) / 2;
            System.out.println("|" + " ".repeat(spaces) + displayTitle + " ".repeat(WIDTH - spaces - displayTitle.length()) + "|");
        } else {
            System.out.println("| " + displayTitle + " ".repeat(WIDTH - 2 - displayTitle.length()) + "|");
        }
        
        System.out.println("|" + " ".repeat(WIDTH) + "|");
        
        for (String message : messages) {
            // Si estamos en el menú "GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO", ajustamos la indentación
            if (title.equals("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO")) {
                if (message.isEmpty()) {
                    System.out.println("|" + " ".repeat(WIDTH) + "|");
                } else {
                    // Alineación más centrada para este menú específico
                    int indent = 4; // Espacios de indentación
                    
                    // Dividir el mensaje en múltiples líneas si es demasiado largo
                    if (message.length() <= WIDTH - (2 * indent)) {
                        System.out.println("|" + " ".repeat(indent) + message + " ".repeat(WIDTH - indent - message.length()) + "|");
                    } else {
                        // Dividir el mensaje en múltiples líneas
                        int currentPos = 0;
                        while (currentPos < message.length()) {
                            int endPos = Math.min(currentPos + (WIDTH - (2 * indent)), message.length());
                            // Ajustar endPos para evitar cortar palabras en medio
                            if (endPos < message.length() && !Character.isWhitespace(message.charAt(endPos))) {
                                int lastSpace = message.lastIndexOf(' ', endPos);
                                if (lastSpace > currentPos) {
                                    endPos = lastSpace;
                                }
                            }
                            String line = message.substring(currentPos, endPos);
                            System.out.println("|" + " ".repeat(indent) + line + " ".repeat(WIDTH - indent - line.length()) + "|");
                            currentPos = endPos;
                            if (currentPos < message.length() && Character.isWhitespace(message.charAt(currentPos))) {
                                currentPos++; // Saltar el espacio inicial de la siguiente línea
                            }
                        }
                    }
                }
            } else {
                // Comportamiento estándar para otros menús
                // Dividir el mensaje en múltiples líneas si es demasiado largo
                if (message.length() <= WIDTH - 6) {
                    System.out.println("|   " + message + " ".repeat(WIDTH - 3 - message.length()) + "|");
                } else {
                    // Dividir el mensaje en múltiples líneas
                    int currentPos = 0;
                    while (currentPos < message.length()) {
                        int endPos = Math.min(currentPos + (WIDTH - 6), message.length());
                        // Ajustar endPos para evitar cortar palabras en medio
                        if (endPos < message.length() && !Character.isWhitespace(message.charAt(endPos))) {
                            int lastSpace = message.lastIndexOf(' ', endPos);
                            if (lastSpace > currentPos) {
                                endPos = lastSpace;
                            }
                        }
                        String line = message.substring(currentPos, endPos);
                        System.out.println("|   " + line + " ".repeat(WIDTH - 3 - line.length()) + "|");
                        currentPos = endPos;
                        if (currentPos < message.length() && Character.isWhitespace(message.charAt(currentPos))) {
                            currentPos++; // Saltar el espacio inicial de la siguiente línea
                        }
                    }
                }
            }
        }
        
        // Añadir línea vacía adicional para "GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO"
        if (title.equals("GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO")) {
            System.out.println("|" + " ".repeat(WIDTH) + "|");
        }
        
        System.out.println("|" + " ".repeat(WIDTH) + "|");
        System.out.println("+" + "-".repeat(WIDTH) + "+");
    }

    /**
     * Verifica que los diccionarios cargados en el sistema siguen siendo válidos.
     * Elimina automáticamente aquellos cuyos archivos de origen ya no existen o son inaccesibles.
     */
    private static void verificarDiccionariosExistentes() {
        try {
            List<String> diccionarios = controladorDomain.getDiccionariosDisponibles();
            if (diccionarios.isEmpty()) {
                return; // No hay diccionarios para verificar
            }
            
            List<String> diccionariosInvalidos = new ArrayList<>();
            
            // Identificar diccionarios inválidos (cuyos archivos ya no existen)
            for (String nombre : diccionarios) {
                if (!controladorDomain.verificarDiccionarioValido(nombre)) {
                    diccionariosInvalidos.add(nombre);
                }
            }
            
            // Ordenar la lista de diccionarios inválidos alfabéticamente
            Collections.sort(diccionariosInvalidos);
            
            // Informar y eliminar los diccionarios inválidos
            if (!diccionariosInvalidos.isEmpty()) {
                // Preparar los mensajes para la notificación
                List<String> mensajes = new ArrayList<>();
                mensajes.add("Se han detectado " + diccionariosInvalidos.size() + " diccionario(s) cuyos archivos de origen");
                mensajes.add("ya no están disponibles o son inaccesibles.");
                mensajes.add("");
                mensajes.add("Estos diccionarios serán eliminados automáticamente del sistema:");
                
                for (String nombre : diccionariosInvalidos) {
                    mensajes.add(" - " + nombre);
                    try {
                        controladorDomain.eliminarDiccionario(nombre);
                    } catch (Exception e) {
                        mensajes.add("   (Error al eliminar: " + e.getMessage() + ")");
                    }
                }
                
                mensajes.add("");
                mensajes.add("Para volver a usar estos diccionarios, deberá importarlos nuevamente.");
                
                // Mostrar la notificación con los resultados
                showNotification("VERIFICACIÓN DE DICCIONARIOS", mensajes.toArray(new String[0]));
            }
        } catch (Exception e) {
            showNotification("ERROR DE VERIFICACIÓN", 
                          "Se produjo un error al verificar los diccionarios cargados en el sistema:",
                          e.getMessage());
        }
    }

    /**
     * Método de prueba para demostrar la funcionalidad de eliminar usuario del ranking.
     * Este método se puede llamar desde el main para testing durante desarrollo.
     */
    public static void testEliminarUsuarioRanking() {
        try {
            String testUser = "usuarioPrueba";
            
            // 1. Registrar usuario de prueba si no existe
            if (!controladorDomain.existeJugador(testUser)) {
                System.out.println("Creando usuario de prueba: " + testUser);
                controladorDomain.registrarUsuario(testUser);
            }
            
            // 2. Agregar puntuación para que aparezca en el ranking
            if (!controladorDomain.perteneceRanking(testUser)) {
                System.out.println("Agregando puntuación de prueba al usuario: " + testUser);
                controladorDomain.agregarPuntuacion(testUser, 100);
                controladorDomain.actualizarEstadisticasUsuario(testUser, true); // Una victoria
            }
            
            // Agregar puntuación total de prueba
            controladorDomain.addPuntuacionTotal(testUser, 500);
            
            // 3. Mostrar ranking antes de la eliminación
            System.out.println("\n=== RANKING ANTES DE ELIMINAR USUARIO ===");
            List<String> ranking = controladorDomain.getRanking();
            for (String usuario : ranking) {
                System.out.println("- " + usuario + ": " + controladorDomain.getPuntuacionMaxima(usuario) + " puntos");
            }
            
            // 4. Verificar que el usuario está en el ranking
            if (!controladorDomain.perteneceRanking(testUser)) {
                System.out.println("ERROR: El usuario no aparece en el ranking después de agregar puntuación.");
                return;
            }
            
            // 5. Mostrar la puntuación total antes de la eliminación
            int puntuacionTotalAntes = controladorDomain.getPuntuacionTotalDirecta(testUser);
            System.out.println("\nPuntuación total de '" + testUser + "' ANTES de eliminar del ranking: " + puntuacionTotalAntes);
            
            // 6. Eliminar usuario del ranking
            System.out.println("\nEliminando usuario '" + testUser + "' del ranking...");
            boolean eliminado = controladorDomain.eliminarUsuarioRanking(testUser);
            if (eliminado) {
                System.out.println("Usuario eliminado correctamente del ranking.");
            } else {
                System.out.println("Error: No se pudo eliminar el usuario del ranking.");
                return;
            }
            
            // 7. Mostrar ranking después de la eliminación
            System.out.println("\n=== RANKING DESPUÉS DE ELIMINAR USUARIO ===");
            ranking = controladorDomain.getRanking();
            for (String usuario : ranking) {
                System.out.println("- " + usuario + ": " + controladorDomain.getPuntuacionMaxima(usuario) + " puntos");
            }
            
            // 8. Verificar que el usuario sigue existiendo como jugador y su puntuación total se ha reseteado
            boolean existeJugador = controladorDomain.existeJugador(testUser);
            int puntuacionTotalDespues = controladorDomain.getPuntuacionTotalDirecta(testUser);
            
            System.out.println("\n¿El usuario sigue registrado en el sistema? " + 
                             (existeJugador ? "SÍ" : "NO"));
            System.out.println("Puntuación total de '" + testUser + "' DESPUÉS de eliminar del ranking: " + puntuacionTotalDespues);
            
            if (puntuacionTotalDespues == 0) {
                System.out.println("✓ CORRECTO: La puntuación total ha sido reseteada a 0");
            } else {
                System.out.println("✗ ERROR: La puntuación total NO ha sido reseteada a 0");
            }
            ;
            System.out.println("\nPrueba de eliminación de usuario del ranking completada.");
            
        } catch (Exception e) {
            System.err.println("Error durante la prueba: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Muestra un mensaje indicando que la configuración estará disponible en la entrega 3
     * y vuelve al menú principal.
     */
    public static void manageConfiguracionMenu() throws IOException {
        System.out.println("""
            +------------------------------------------------------------------------------+
            | CONFIGURACIÓN - PRÓXIMAMENTE                                                |
            +------------------------------------------------------------------------------+
            |                                                                              |
            |   La funcionalidad de configuración estará disponible en la entrega 3.       |
            |                                                                              |
            |   Pulse ENTER para volver al menú principal.                                 |
            |                                                                              |
            +------------------------------------------------------------------------------+
            """);
        
        readLine(); // Esperar a que el usuario pulse ENTER
        ShowMenu("principal");
    }
}