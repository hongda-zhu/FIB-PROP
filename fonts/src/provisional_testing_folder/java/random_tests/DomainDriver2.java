package random_tests;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scrabble.domain.controllers.ControladorDomain;
import scrabble.excepciones.ExceptionLanguageExist;
import scrabble.excepciones.ExceptionLanguageNotExist;
import scrabble.excepciones.ExceptionPartidaNotExist;
import scrabble.excepciones.ExceptionPasswordMismatch;
import scrabble.excepciones.ExceptionRankingOperationFailed;
import scrabble.excepciones.ExceptionUserEsIA;
import scrabble.excepciones.ExceptionUserExist;
import scrabble.excepciones.ExceptionUserInGame;
import scrabble.excepciones.ExceptionUserLoggedIn;
import scrabble.excepciones.ExceptionUserNotExist;
import scrabble.excepciones.ExceptionUserNotLoggedIn;
import scrabble.helpers.BooleanWrapper;
import scrabble.helpers.Tuple;

/**
 * Clase principal para probar el dominio de Scrabble.
 * Proporciona una interfaz de línea de comandos para interactuar con las funcionalidades del juego.
 * @author Equipo Scrabble
 */



public class DomainDriver2 {

    static ControladorDomain controladorDomain = new ControladorDomain();
    static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    static HashMap<String, String> menus = new HashMap<>();
    static String command;
    static boolean readingFile;   
    
    public static void main(String[] args) throws IOException {
        controladorDomain = new ControladorDomain();
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

        // Existen opciones innecesarias en algunos menus, quitarlas cuando todas las funcionalidades estén acabadas
        initializeMenus();
        initializeDefaultSettings();
        ShowMenu("bienvenida");
        readingFile = false;
        boolean successful;
        
        while ((command = readLine()) != null) {
            boolean menu = false;
            try {
                String input;
                String[] parts;
                successful = true;
                command = command.toLowerCase();
                command = command.stripTrailing();
                
                switch (command) {
                    /* MENUS */
                    case "0":
                        System.exit(0);
                        break;
                    case "1":

                        menu = true;
                        manageUserMenu();
                        command = "";
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
                        ShowMenu("juego");
                        menu = true;
                        break;
                        
                    case "5":
                        ShowMenu("ranking");
                        menu = true;
                        break;
                        
                    case "6":
                        ShowMenu("configuracion");
                        menu = true;
                        break;
                    case "7":
                        System.out.println("Introduce la ruta del archivo que se cargará para procesar comandos (directorio actual: " + System.getProperty("user.dir") + "): ");
                        String filePath = readLine();
                        reader = new BufferedReader(new FileReader(filePath));
                        readingFile = true;
                        break;                    
                    case "help":
                    case "ayuda":
                        ShowMenu("ayuda");
                        menu = true;
                        break;

                    case "exit":
                    case "salir":
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
    }


    public static void managePartidaIniciar(String nombrePartida, String idiomaSeleccionado, HashMap<String, String> jugadoresSeleccionados, Integer N, String[] dificultad) throws IOException{
        controladorDomain.iniciarPartida(nombrePartida, jugadoresSeleccionados, idiomaSeleccionado, N);
        // jugadoresSeleccionados mapea nombre -> id
        BooleanWrapper pausado = new BooleanWrapper(false);

        while (!controladorDomain.isJuegoTerminado()) {
            System.out.println("Presiona 'X' para pausar el juego ");

            if (!pausado.value) {
                for (Map.Entry<String, String> entry : jugadoresSeleccionados.entrySet()) {
                    String username = entry.getKey();
                    String id = entry.getValue();
                    
                    Tuple<Map<String, Integer>, Integer> result = controladorDomain.realizarTurno(username, id, dificultad[0], pausado);

                    
                    if (result == null && !pausado.value) {
                        System.out.println("El jugador " + username  + " paso la jugada.");
                        controladorDomain.addSkipTrack(id);
                    } else if (result != null && !pausado.value) {
                        controladorDomain.inicializarRack(id, result.x);
                        controladorDomain.addPuntuacion(id, result.y);
                        
                        Map<String, Integer> nuevasFicha = controladorDomain.cogerFichas(7 - controladorDomain.getCantidadFichas(id));
                        
                        if (nuevasFicha == null) {
                            controladorDomain.finalizarJuego();
                            
                        } else {
                            for (Map.Entry<String, Integer> fichas : nuevasFicha.entrySet()) {
                                String letra = fichas.getKey();
                                int cantidad = fichas.getValue();
                                for (int i = 0; i < cantidad; i++) {
                                    controladorDomain.agregarFicha(id, letra);
                                }   
                            }
                        }
                    }                

                    
                }

                boolean allskiped = true;

                for (Map.Entry<String, String> entry : jugadoresSeleccionados.entrySet()) {
                    if (controladorDomain.getSkipTrack(entry.getValue()) < 3) {
                        allskiped = false;
                        break;
                    }
                }
                if (allskiped) {
                    System.out.println("Los jugadores han pasado mas de 2 veces consecutivas. El juego ha terminado.");
                    controladorDomain.finalizarJuego();
                }            
            }
            else {
                // Aún está bugueado, falta retocar algunas cosas
                boolean reanudar = false;

                while (!reanudar) {
                    ShowMenu("pausado");
                    String command = readLine();

                    switch (command) {
                        case "1":
                            //guarda
                            break;
                        case "2":
                            //reinicia
                            break;
                        case "X":
                            pausado.value = true;
                            reanudar = true;
                            //reanuda
                            break;   
                        case "0":
                            break;
                            //abandona                                                     
                        default:
                            System.out.println("¡Introduce alguno de los comandos disponibles!");
                            break;
                            
                    }
                }
 

            }

            
            
        }

        // Mostrar estadísticas finales
        System.out.println("Estadísticas finales:");
        for (Map.Entry<String, String> entry : jugadoresSeleccionados.entrySet()) {
            System.out.println("Jugador: " + entry.getKey());
            System.out.println("Puntuación: " + controladorDomain.getPuntuacion(entry.getKey()));
            System.out.println("-----------------------------");
        }        
        System.out.println("Gracias por jugar :)");        

    }

    public static void initializeDefaultSettings() {
        String rutaFichas = "src/provisional_testing_folder/resources/alpha.txt"; // Ruta del archivo de fichas
        String rutaAlphabet = "src/provisional_testing_folder/resources/words.txt"; // Ruta del archivo de fichas    
        if (!controladorDomain.existeLenguaje("Esp")) controladorDomain.anadirLenguaje("Esp", rutaFichas, rutaAlphabet);
        // controladorDomain.registrarUsuario("admin", "");
        controladorDomain.iniciarSesion("admin", "");
        controladorDomain.iniciarSesion("admin2", "");

        // controladorDomain.crearJugadorIA("test", "FACIL");

    }



    public static void initializeMenus() {
        menus.put("bienvenida", """
                                    +------------------------------------------------------------------------------+
                                    | MENÚ DE BIENVENIDA |                                                         |
                                    +------------------------------------------------------------------------------+
                                    |                       ¡BIENVENIDO A SCRABBLE!                                |
                                    |                                                                              |
                                    |        Para ver los comandos disponibles, selecciona uno de los menús        |
                                    |                 introduciendo un número y pulsando enter.                    |
                                    |          Dentro de cada menú, puedes ver los comandos disponibles.           |
                                    |               Los comandos se pueden ejecutar desde cualquier ventana.       |
                                    |                                                                              |
                                    |                           [ 1 ] Gestión de Usuarios                          |
                                    |                           [ 2 ] Gestión de Diccionarios                      |
                                    |                           [ 3 ] Gestión de Partidas                          |
                                    |                           [ 4 ] Acciones de Juego                            |
                                    |                           [ 5 ] Gestión de Rankings                          |
                                    |                           [ 6 ] Configuración                                |
                                    |                           [ 7 ] Ejecutar Archivo                             |
                                    |                           [ 0 ] Salir                                        |
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);
        
        menus.put("principal", """
                                    +------------------------------------------------------------------------------+
                                    | MENÚ PRINCIPAL |                                                             |
                                    +------------------------------------------------------------------------------+
                                    |                 Selecciona un menú o ejecuta un comando.                     |
                                    |                                                                              |
                                    |                           [ 1 ] Gestión de Usuarios                          |
                                    |                           [ 2 ] Gestión de Diccionarios                      |
                                    |                           [ 3 ] Gestión de Partidas                          |
                                    |                           [ 4 ] Acciones de Juego                            |
                                    |                           [ 5 ] Gestión de Rankings                          |
                                    |                           [ 6 ] Configuración                                |
                                    |                           [ 7 ] Ejecutar Archivo                             |
                                    |                           [ 0 ] Salir                                        |
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);
        
        menus.put("usuario", """
                                    +------------------------------------------------------------------------------+
                                    | GESTIÓN DE USUARIOS |                                                        |
                                    +------------------------------------------------------------------------------+
                                    |                  Ejecuta cualquiera de los comandos mostrados.               |
                                    |                                                                              |
                                    |   [ 1 ] Registrar usuario          - Registra un nuevo usuario en el sistema.|
                                    |   [ 2 ] Iniciar sesion             - Inicia sesión con un usuario existente. |
                                    |   [ 3 ] Cerrar sesion              - Cierra sesión de un usuario existente.  |
                                    |   [ 4 ] Eliminar usuario           - Elimina un usuario existente.           |
                                    |   [ 5 ] Cambiar nombre             - Cambia nombre a un usuario existente del|
                                    |                                      del sistema.                            |
                                    |   [ 6 ] Cambiar contraseña         - Cambia contraseña a un usuario existente|                                                         
                                    |                                      del sistema.                            |
                                    |   [ 7 ] Mostrar usuarios           - Muestra todos los usuarios registrados  |                       
                                    |   [ 0 ] Volver                     - Vuelve al Menú Principal.               | 
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);

        menus.put("usuarioRegistrar", """
                                    +------------------------------------------------------------------------------+
                                    | GESTIÓN DE USUARIOS > REGISTRAR USUARIO                                      |
                                    +------------------------------------------------------------------------------+
                                    |   ¿Cómo registrar un nuevo usuario?                                          |
                                    |   1. Introduce un nombre de usuario                                          |
                                    |   2. Introduce una contraseña                                                |                                                                              
                                    |                                                                              |
                                    |   [ 1 ] Comenzar                   - Comienza a introducir datos             |
                                    |   [ 0 ] Volver                     - Vuelve a Gestión de Usuarios.           | 
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);   

        menus.put("usuarioIniciar", """
                                    +------------------------------------------------------------------------------+
                                    | GESTIÓN DE USUARIOS > INICIAR SESIÓN                                         |
                                    +------------------------------------------------------------------------------+
                                    |   ¿Cómo iniciar sesión?                                                      |
                                    |   1. Introduce un nombre de usuario                                          |
                                    |   2. Introduce una contraseña                                                |                                                                              
                                    |                                                                              |
                                    |   [ 1 ] Comenzar                   - Comienza a introducir datos             |
                                    |   [ 0 ] Volver                     - Vuelve a Gestión de Usuarios.           | 
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);

        menus.put("usuarioCerrar", """
                                    +------------------------------------------------------------------------------+
                                    | GESTIÓN DE USUARIOS > CERRAR SESIÓN                                          |
                                    +------------------------------------------------------------------------------+
                                    |   ¿Cómo cerrar sesión?                                                       |
                                    |   1. Introduce un nombre de usuario al que quiere cerrar sesión              |
                                    |                                                                              |
                                    |   [ 1 ] Comenzar                   - Comienza a introducir datos             |
                                    |   [ 0 ] Volver                     - Vuelve a Gestión de Usuarios.           | 
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);  

        menus.put("usuarioEliminar", """
                                    +------------------------------------------------------------------------------+
                                    | GESTIÓN DE USUARIOS > ELIMINAR USUARIO                                       |
                                    +------------------------------------------------------------------------------+
                                    |   ¿Cómo eliminar usuario?                                                    |
                                    |   1. Introduce un nombre de usuario al que quiere eliminar                   |
                                    |                                                                              |
                                    |   [ 1 ] Comenzar                   - Comienza a introducir datos             |
                                    |   [ 0 ] Volver                     - Vuelve a Gestión de Usuarios.           | 
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);

        menus.put("usuarioNombre", """
                                    +------------------------------------------------------------------------------+
                                    | GESTIÓN DE USUARIOS > CAMBIAR NOMBRE DE USUARIO                              |
                                    +------------------------------------------------------------------------------+
                                    |   ¿Cómo cambiar de nombre?                                                   |
                                    |   1. Introduce un nombre de usuario que quiere modificar                     |
                                    |   2. Introduce el nuevo nombre que desee                                     |
                                    |                                                                              |
                                    |   [ 1 ] Comenzar                   - Comienza a introducir datos             |
                                    |   [ 0 ] Volver                     - Vuelve a Gestión de Usuarios.           | 
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);

        menus.put("usuarioContrasena", """
                                    +------------------------------------------------------------------------------+
                                    | GESTIÓN DE USUARIOS > CAMBIAR CONTRASEÑA DE USUARIO                          |
                                    +------------------------------------------------------------------------------+
                                    |   ¿Cómo cambiar de nombre de usuario?                                        |
                                    |   1. Introduce el nombre de usuario que quiere modificar su contraseña       |
                                    |   2. Introduce su antigua contraseña                                         |
                                    |   3. Introduce su nueva contraseña                                           |
                                    |                                                                              |
                                    |   [ 1 ] Comenzar                   - Comienza a introducir datos             |
                                    |   [ 0 ] Volver                     - Vuelve a Gestión de Usuarios.           | 
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);    

        menus.put("usuarioMostrar", """
                                    +------------------------------------------------------------------------------+
                                    | GESTIÓN DE USUARIOS > MOSTRAR USUARIOS                                       |
                                    +------------------------------------------------------------------------------+
                                    |    pinga                                                                     |
                                    |                                                                              |
                                    |   [ 0 ] Volver                     - Vuelve a Gestión de Usuarios.           | 
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);                                                                                                                                                                                                                                                                                            
                                                                                                                                                                                                                          
        
        menus.put("diccionario", """
                                    +------------------------------------------------------------------------------+
                                    | GESTIÓN DE DICCIONARIOS |                                                    |
                                    +------------------------------------------------------------------------------+
                                    |                  Ejecuta cualquiera de los comandos mostrados.               |
                                    |                                                                              |
                                    |   [ 1 ] Crear diccionario          - Crea un nuevo diccionario.              |
                                    |   [ 2 ] Importar diccionario       - Importa un diccionario desde un archivo.|
                                    |   [ 3 ] Consultar diccionarios     - Muestra todos los diccionarios disponi- |
                                    |                                    - bles.                                   |
                                    |                                                                              |
                                    |   [ 0 ] Volver                     - Vuelve al Menú Principal.               | 
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);  

        menus.put("diccionarioCrear", """
                            +------------------------------------------------------------------------------+
                            | GESTIÓN DE DICCIONARIOS > CREAR DICCIONARIO                                  |
                            +------------------------------------------------------------------------------+
                            |   ¿Cómo crear nuevo diccionario?                                               |
                            |   1. Seleccione la opción "1"                                                  |
                            |   2. Introduzca el nombre del nuevo diccionario                                |
                            |   3. Introduzca la ruta en la que se encuentra el alfabeto                     | 
                            |   4. Introduzca la ruta en la que se encuentra el diccionario                  | 
                            |                                                                              |  
                            |   Ejemplo:                                                                     |      
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
                            |   1. Seleccione la opción "1"                                                |
                            |   2. Introduzca el nombre del nuevo diccionario                              |
                            |   3. Introduzca la ruta en la que se encuentra el alfabeto                   | 
                            |   4. Introduzca la ruta en la que se encuentra el diccionario                | 
                            |                                                                              |  
                            |   Ejemplo:                                                                     |      
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
                            | 1. Seleccione la opción "1"                                                  |
                            | 2. Introduzca el nombre del nuevo diccionario                                |
                            | 3. Introduzca la ruta en la que se encuentra el alfabeto                     | 
                            | 4. Introduzca la ruta en la que se encuentra el diccionario                  | 
                            |                                                                              |  
                            | Ejemplo:                                                                     |      
                            |                                                                              |                                                                                                    |
                            |   [ 1 ] Comenzar                   - Comenzar a introducir datos             |
                            |   [ 0 ] Volver                     - Vuelve a Gestión de Diccionarios.       | 
                            |                                                                              |
                            +------------------------------------------------------------------------------+
                            """);  
        menus.put("partida", """
                                    +------------------------------------------------------------------------------+
                                    | GESTIÓN DE PARTIDAS |                                                        |
                                    +------------------------------------------------------------------------------+
                                    |                 Ejecuta cualquiera de los comandos mostrados.                |
                                    |                                                                              |
                                    |   [ 1 ] Definir partida nueva      - Configura una nueva partida.            |
                                    |   [ 2 ] Cargar partida             - Carga una partida guardada.             |
                                    |   [ 0 ] Volver                     - Vuelve al Menú Principal.               | 
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);

        menus.put("partidacargar", """
                                    +------------------------------------------------------------------------------+
                                    | GESTIÓN DE PARTIDAS > CARGAR PARTIDA                                         |
                                    +------------------------------------------------------------------------------+
                                    |   A continuación se listan el nombre de todos los diccionarios disponibles.  |                                                                           
                                    |   Seleccione la opción que desee.                                            |
                                    |                                                                              |
                                    |   [ 1 ] Seleccionar               - Introduce el nombre del diccionario      |
                                    |   [ 0 ] Volver                    - Vuelve a Definir Partida Nueva.          | 
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);

        menus.put("partidaDefinir", """
                                    +------------------------------------------------------------------------------+
                                    | GESTIÓN DE PARTIDAS > DEFINIR PARTIDA NUEVA                                  |
                                    +------------------------------------------------------------------------------+
                                    |   Antes de iniciar la partida se ha de seleccionar un diccionario, definir el|
                                    |   modo de juego e indicar un nombre para la partida obligatoriamente.        |                                                                      
                                    |                                                                              |
                                    |   [ 1 ] Iniciar Partida           - Inicia la partida con los ajustes        |
                                    |                                     definidos.                               |                                                                                                                 
                                    |   [ 2 ] Seleccionar Diccionario   - Selecciona uno de los diccionarios       | 
                                    |                                     disponibles                              |
                                    |   [ 3 ] Definir modo              - Indica qué modo de juego será la partida | 
                                    |   [ 4 ] Definir tamaño tablero    - Introduce el tamaño del tablero. Por de- |                                                                         
                                    |                                     -fecto es 15x15.                         | 
                                    |   [ 5 ] Nombre de la partida      - Introduce el nombre que desees a la par- |
                                    |                                     -tida.                                   |      
                                    |                                                                              |
                                    |   [ 0 ] Volver                    - Vuelve a Gestión de Partidas.            | 
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);

        menus.put("partidaDefinirDiccionario", """
                                    +------------------------------------------------------------------------------+
                                    | GESTIÓN DE PARTIDAS > DEFINIR PARTIDA NUEVA > SELECCIONAR DICCIONARIO        |
                                    +------------------------------------------------------------------------------+
                                    |   A continuación se listan el nombre de todos los diccionarios disponibles.  |                                                                            |
                                    |   Seleccione la opción que desee.                                            |
                                    |                                                                              |
                                    |   [ 1 ] Seleccionar               - Introduce el nombre del diccionario      |
                                    |   [ 0 ] Volver                    - Vuelve a Definir Partida Nueva.          | 
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);   

        menus.put("partidaDefinirModo", """
                                    +------------------------------------------------------------------------------+
                                    | GESTIÓN DE PARTIDAS > DEFINIR PARTIDA NUEVA > DEFINIR MODO                   |
                                    +------------------------------------------------------------------------------+
                                    |   Selecciona el modo de juego                                                |                                                                           
                                    |                                                                              |
                                    |   [ 1 ] Solitario                 - Juega contra bots                        | 
                                    |   [ 2 ] Multijugador              - Juega contra otros jugadores             |
                                    |                                                                              |
                                    |   [ 0 ] Volver                    - Vuelve a Definir Partida Nueva.          | 
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);                            

        menus.put("partidaDefinirModoSolitario", """
                                    +------------------------------------------------------------------------------+
                                    | GESTIÓN DE PARTIDAS > DEFINIR PARTIDA NUEVA > DEFINIR MODO > MULTIJUGADOR    |
                                    +------------------------------------------------------------------------------+
                                    |   ¡Puedes jugar contra múltiples bots si así lo deseas!                      |
                                    |   1. Introduce el número de bots totales                                     |
                                    |                                                                              |
                                    |   [ 1 ] Comenzar                  - Comienza a introducir datos              |                                                                              |
                                    |   [ 0 ] Volver                    - Vuelve a Definir Modo                    | 
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """); 

        menus.put("partidaDefinirModoMultijugador", """
                                    +------------------------------------------------------------------------------+
                                    | GESTIÓN DE PARTIDAS > DEFINIR PARTIDA NUEVA > DEFINIR MODO > MULTIJUGADOR    |
                                    +------------------------------------------------------------------------------+
                                    |   ¿Cómo definir qué jugadores participarán en la partida multijugador?       |
                                    |   1. Introduce el número de jugadores totales                                |
                                    |   2. Para cada jugador, introduce su nombre de usuario                        |                                                                                                                   
                                    |                                                                              |
                                    |   [ 1 ] Comenzar                  - Comienza a introducir datos              |                                                                              |
                                    |   [ 0 ] Volver                    - Vuelve a Definir Modo                    | 
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);                                                                                                                                                
        
        menus.put("pausado", """
                                    +------------------------------------------------------------------------------+
                                    | PARTIDA EN PAUSA                                                             |
                                    +------------------------------------------------------------------------------+
                                    |                  Ejecuta cualquiera de los comandos mostrados.               |
                                    |                                                                              |
                                    |   [ 1 ] Guardar partida            - Guarda el estado actual de la partida.  |
                                    |   [ 2 ] Reiniciar partida          - Reinicia la partida actual.             |
                                    |   [ X ] Reanudar partida           - Vuelve a la partida actual.             |
                                    |   [ 0 ] Abandonar partida          - Abandona la partida actual.             |
                                    |                                                                              | 
                                    +------------------------------------------------------------------------------+
                                    """);
        
        menus.put("ranking", """
                                    +------------------------------------------------------------------------------+
                                    | GESTIÓN DE RANKINGS |                                                        |
                                    +------------------------------------------------------------------------------+
                                    |                  Ejecuta cualquiera de los comandos mostrados.               |
                                    |                                                                              |
                                    |   [ 1 ] Ver ranking                - Muestra el ranking de jugadores.        |
                                    |   [ 2 ] Consultar historial        - Consulta el historial de un jugador.    |
                                    |   [ 3 ] Filtrar ranking            - Filtra el ranking según criterios.      |
                                    |   [ 0 ] Volver                     - Vuelve al Menú Principal.               | 
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);
        
        menus.put("configuracion", """
                                    +------------------------------------------------------------------------------+
                                    | CONFIGURACIÓN |                                                              |
                                    +------------------------------------------------------------------------------+
                                    |                  Ejecuta cualquiera de los comandos mostrados.               |
                                    |                                                                              |
                                    |   [ 1 ] Configurar idioma          - Cambia el idioma de la aplicación.      |
                                    |   [ 2 ] Configurar tema            - Cambia el tema de la aplicación.        |
                                    |   [ 3 ] Configurar volumen         - Ajusta el volumen de los sonidos.       |
                                    |   [ 0 ] Volver                     - Vuelve al Menú Principal.               | 
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);
        
        menus.put("ayuda", """
                                    +------------------------------------------------------------------------------+
                                    | AYUDA | Escribe '0' para volver al menú principal.                           |
                                    +------------------------------------------------------------------------------+
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
                                    |  manual de usuario o visita nuestra web.                                     |
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);
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
                    System.out.println("Volviendo al menú principal...");
                    ShowMenu("principal");
                    break;
                case "1":
                    manageUserRegistrar();               
                    break;
                case "2":
                    manageUserIniciar();                   
                    break;
                case "3":
                    manageUserCerrar();
                    break;
                case "4":
                    manageUserEliminar();                   
                    break;
                case "5":
                    manageUserCambiarNombre();
                    break;
                case "6":
                    manageUserCambiarContrasena();
                    break;
                case "7":
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
            ShowMenu("usuarioRegistrar");
            String userCommand = readLine().trim();
            String user;
            String pwd;
            switch (userCommand) {
                case "0":
                    volver = true;
                    System.out.println("Volviendo a Gestión de Usuarios...");
                    break;
                case "1":
                    System.out.println("Introduce un nombre de usuario: ");
                    user = readLine();
                    System.out.println("Introduce una contraseña: ");
                    pwd = readLine();
                    System.out.println(pwd);

                    try {
                        if(controladorDomain.registrarUsuario(user, pwd)) System.out.println("Se ha registrado correctamente!");  
                    } catch (ExceptionUserExist e) {
                        System.out.println("Error: El usuario ya existe.");                    
                    }
                  
                    break;    
                default:
                    System.out.println("¡Introduce alguno de los comandos disponibles!");
                    break;
            }
        }
    }

    public static void manageUserIniciar() throws IOException{
     
        boolean volver = false;
        while (!volver) {
            ShowMenu("usuarioIniciar");
            String userCommand = readLine().trim();
            String user;
            String pwd;
            switch (userCommand) {
                case "0":
                    volver = true;
                    System.out.println("Volviendo a Gestión de Usuarios...");
                    break;
                case "1":
                    System.out.println("Introduce un nombre de usuario: ");
                    user = readLine();
                    System.out.println("Introduce una contraseña: ");
                    pwd = readLine();
                    System.out.println(pwd);
                    try {
                        if (controladorDomain.iniciarSesion(user, pwd)) {
                            System.out.println("¡Sesión iniciada correctamente!");
                        }
                        else System.out.println("¡Contraseña incorrecta!");

                    } catch (ExceptionUserNotExist e) {
                        System.out.println("Error: El usuario no existe.");
                    } catch (ExceptionUserEsIA e) {
                        System.out.println("Error: " + e.getMessage());
                    }catch (ExceptionUserLoggedIn e) {
                        System.out.println("Error: " + e.getMessage());
                    } 
                    catch (Exception e) {
                        System.out.println("Error inesperado: " + e.getMessage());
                    }
                        break;  
                default:
                    System.out.println("¡Introduce alguno de los comandos disponibles!");
                    break;
            }
        }
    }

    public static void manageUserCerrar() throws IOException{
     
        boolean volver = false;
        while (!volver) {
            ShowMenu("usuarioCerrar");
            String userCommand = readLine().trim();
            String user;
            String pwd;
            switch (userCommand) {
                case "0":
                    volver = true;
                    System.out.println("Volviendo a Gestión de Usuarios...");
                    break;
                case "1":
                    System.out.println("Introduce un nombre de usuario: ");
                    user = readLine();
                    try {
                        if(controladorDomain.cerrarSesion(user)) System.out.println("Se ha cerrado correctamente!");                                        
                    } catch (ExceptionUserNotLoggedIn e) {
                        System.out.println("Error: El usuario no está logueado.");
                    } catch (ExceptionUserNotExist e) {
                        System.out.println("Error: El usuario no existe.");
                    } catch (ExceptionUserEsIA e) {
                        System.out.println("Error: El usuario introducido es una IA. ¡Las IAs no necesitan iniciar sesión!");                    
                    }
                    break;  
                default:
                    System.out.println("¡Introduce alguno de los comandos disponibles!");
                    break;
            }
        }
    }

    public static void manageUserEliminar() throws IOException{
     
        boolean volver = false;
        while (!volver) {
            ShowMenu("usuarioEliminar");
            String userCommand = readLine().trim();
            String user;

            switch (userCommand) {
                case "0":
                    volver = true;
                    System.out.println("Volviendo a Gestión de Usuarios...");
                    break;
                case "1":
                    System.out.println("Introduce un nombre de usuario: ");
                    user = readLine();

                    try {
                        if(controladorDomain.eliminarUsuario(user)) System.out.println("Se ha eliminado correctamente!");                                        
                    } catch (ExceptionUserLoggedIn e) {
                        System.out.println("Error: El usuario está logueado. Cierra su sesión antes.");
                    } catch (ExceptionUserNotExist e) {
                        System.out.println("Error: El usuario no existe.");
                    } catch (ExceptionRankingOperationFailed e) {
                        System.out.println("Error: No se ha podido eliminar usuario del ranking.");                    
                    }
                    break;  
                default:
                    System.out.println("¡Introduce alguno de los comandos disponibles!");
                    break;
            }
        }
    }

    public static void manageUserCambiarNombre() throws IOException{
     
        boolean volver = false;

        while (!volver) {
            ShowMenu("usuarioNombre");
            String userCommand = readLine().trim();
            String user;
            String new_name;

            switch (userCommand) {
                case "0":
                    volver = true;
                    System.out.println("Volviendo a Gestión de Usuarios...");
                    break;
                case "1":
                    System.out.println("Introduce el nombre de usuario: ");
                    user = readLine();
                    System.out.println("Introduce un nuevo nombre de usuario: ");
                    new_name = readLine();                    
                    
                    try {
                        if(controladorDomain.cambiarNombre(user, new_name)) System.out.println("Se ha reestablecido el nombre de usuario correctamente!");                                        
                    } catch (ExceptionUserNotExist e) {
                        System.out.println("Error: El usuario no existe.");
                    } catch (ExceptionUserEsIA e) {
                        System.out.println("Error: " + e.getMessage());                    
                    }
                    break;  
                default:
                    System.out.println("¡Introduce alguno de los comandos disponibles!");
                    break;
            }
        }
    }
    
    public static void manageUserCambiarContrasena() throws IOException{
     
        boolean volver = false;

        while (!volver) {
            ShowMenu("usuarioContrasena");
            String userCommand = readLine().trim();
            String user;
            String old_pwd;
            String new_pwd;

            switch (userCommand) {
                case "0":
                    volver = true;
                    System.out.println("Volviendo a Gestión de Usuarios...");
                    break;
                case "1":
                    System.out.println("Introduce el nombre de usuario: ");
                    user = readLine();

                    System.out.println("Introduce su antigua contraseña: ");
                    new_pwd = readLine();                    

                    System.out.println("Introduce su nueva contraseña: ");
                    old_pwd = readLine();                                        
                    try {
                        if(controladorDomain.cambiarContrasena(user, new_pwd, old_pwd)) System.out.println("Se ha reestablecido la contraseña correctamente!");    
                        else System.out.println("Operación fallida...");                                      
                    } catch (ExceptionUserNotExist e) {
                        System.out.println("Error: El usuario no existe.");
                    } catch (ExceptionUserEsIA e) {
                        System.out.println("Error: El usuario al que intenta cambiar de contraseña es una IA.");
                    }catch (ExceptionPasswordMismatch e) {
                        System.out.println("Error: La antigua contraseña introducida no es correcta.");
                    }
                    break;  
                default:
                    System.out.println("¡Introduce alguno de los comandos disponibles!");
                    break;
            }
        }
    }            

    public static void manageUserMostrar() throws IOException{
      
        boolean volver = false;
        controladorDomain.mostrarTodosUsuariosDebug();
        while (!volver) {
            ShowMenu("usuarioMostrar");
            String userCommand = readLine().trim();
            String user;

            switch (userCommand) {
                case "0":
                    volver = true;
                    System.out.println("Volviendo a Gestión de Usuarios...");
                    break; 
                default:
                    System.out.println("¡Introduce alguno de los comandos disponibles!");
                    break;
            }
        }
    }

    public static void manageDiccionaryMenu() throws IOException{
        boolean volver = false;

        while (!volver) {
            ShowMenu("diccionario");
            String userCommand = readLine().trim();

            switch (userCommand) {
                case "0":
                    volver = true;
                    System.out.println("Volviendo al menú principal...");
                    ShowMenu("principal");
                    break;
                case "1":
                    manageDiccionarioCrear();
                    break;
                case "2":
                    manageDiccionarioImportar();                
                    break;
                case "3":
                    printDiccionariosDisponibles();
                    break;                    

            }
        }    
    }

    public static void printDiccionariosDisponibles() {
        System.out.println("Diccionarios disponibles:" );
        List<String> diccDisponibles = controladorDomain.getDiccionariosDisponibles(); 
        if (diccDisponibles.isEmpty()) {
            System.out.println("No hay ningún diccionario disponible :(" );                
        }
        else {
            for (String nombre: diccDisponibles) {
                System.out.println("-"+ nombre);               
            }
        }    
    }
    public static void manageDiccionarioCrear() throws IOException{
        // Esta opcion creo que no es necesaria
        boolean volver = false;
        while (!volver) {
            ShowMenu("diccionarioCrear");
            String userCommand = readLine().trim();
            String user;

            switch (userCommand) {
                case "0":
                    volver = true;
                    System.out.println("Volviendo a Gestión de Diccionarios...");
                    break;
                case "1":
                    System.out.println("Introduce un nombre de usuario: ");
                    user = readLine();

                    try {
                        if(controladorDomain.eliminarUsuario(user)) System.out.println("Se ha eliminado correctamente!");                                        
                    } catch (ExceptionUserLoggedIn e) {
                        System.out.println("Error: El usuario está logueado. Cierra su sesión antes.");
                    } 
                    break;  
                default:
                    System.out.println("¡Introduce alguno de los comandos disponibles!");
                    break;
            }
        }
    }    

    public static void manageDiccionarioImportar() throws IOException{
     
        boolean volver = false;
        while (!volver) {
            ShowMenu("diccionarioImportar");
            String userCommand = readLine().trim();

            switch (userCommand) {
                case "0":
                    volver = true;
                    System.out.println("Volviendo a Gestión de Diccionarios...");
                    break;
                case "1":
                    String nombre;
                    String alphaRoute;
                    String diccRoute;

                    System.out.println("Introduce un nombre para el diccionario: ");
                    nombre= readLine();

                    System.out.println("Introduce la ruta del alfabeto: ");
                    alphaRoute = readLine();
                                
                    System.out.println("Introduce la ruta del diccionario: ");
                    diccRoute = readLine();

                    try {

                        controladorDomain.anadirLenguaje(nombre, alphaRoute, diccRoute);
                        if (controladorDomain.existeLenguaje(nombre))  {
                            System.out.println("Se ha importado correctamente!");                            
                        }
                        else  System.out.println("Operación fallida!");       

                    } catch (ExceptionLanguageExist e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;  
                default:
                    System.out.println("¡Introduce alguno de los comandos disponibles!");
                    break;
            }
        }
    } 

    public static void managePartidaMenu() throws IOException{
        boolean volver = false;

        while (!volver) {
            ShowMenu("partida");
            String userCommand = readLine().trim();

            switch (userCommand) {
                case "0":
                    volver = true;
                    System.out.println("Volviendo al menú principal...");
                    ShowMenu("principal");
                    break;
                case "1":
                    managePartidaDefinir();
                    break;
                case "2":
                    managePartidaCargar();                
                    break;
                    

            }
        }    
    }

    public static void managePartidaDefinir() throws IOException {
        boolean volver = false;
        String idiomaSeleccionado = null;
        HashMap<String, String> jugadoresSeleccionados = null;
        Integer N = 15;
        String nombrePartida = null;
        String[] dificultad = {""}; // Array para poder mutar la variable al pasar como parametro...

        while (!volver) {
            ShowMenu("partidaDefinir");
            String userCommand = readLine().trim();

            switch (userCommand) {
                case "0":
                    volver = true;
                    System.out.println("Volviendo a Gestión de Partidas...");
                    break;
                case "1":
                    if (nombrePartida == null) {
                        System.out.println("Debe de indicar un nombre de partida antes de iniciar la partida.");
                        break;                    
                    }
                    
                    if (idiomaSeleccionado == null) {
                        System.out.println("Debe de seleccionar un diccionario antes de iniciar la partida.");
                        break;
                    } 
                    
                    if (jugadoresSeleccionados == null) {
                        System.out.println("Debe de seleccionar los jugadores antes de iniciar la partida.");
                        break;                    
                    }
                    String header = "===INICIANDO PARTIDA " + nombrePartida + "===";
                    String footer = "=".repeat(header.length());                                       
                    System.out.println(header);
                    System.out.println("-Diccionario seleccionado: " + idiomaSeleccionado);
                    System.out.println("-Jugadores seleccionados: " + jugadoresSeleccionados);
                    System.out.println("-Tamaño del tablero: " + N.toString() + "x" + N.toString());
                    if (!dificultad[0].equals("")) System.out.println(dificultad[0]);
                    System.out.println(footer);

                    managePartidaIniciar(nombrePartida, idiomaSeleccionado, jugadoresSeleccionados, N, dificultad);
                    
                    break;
                case "2":
                    idiomaSeleccionado = managePartidaSeleccionarDiccionario();

                    if (idiomaSeleccionado != null) {
                        System.out.println("Idioma seleccionado: " + idiomaSeleccionado);
                    }

                    break;
                case "3":
                    jugadoresSeleccionados = managePartidaDefinirModo(dificultad);

                    if (jugadoresSeleccionados != null) {
                        System.out.println("Jugadores definidos: " + jugadoresSeleccionados);
                    }

                    break;
                case "4":
                    System.out.println("Introduce un tamaño de tablero (mínimo 15): ");
                    N = Integer.parseInt(readLine());

                    if (N < 15) {
                        System.out.println("¡Tamaño incorrecto!");
                        N = 15;
                        break;
                    }

                    break;
                case "5":
                    System.out.println("Introduce un nombre de partida: ");
                    nombrePartida = readLine();
                    break;  

                default:
                    System.out.println("¡Introduce alguno de los comandos disponibles!");
                    break;
            }
        }
    }

    public static String managePartidaSeleccionarDiccionario() throws IOException {
        boolean volver = false;
        String idiomaSeleccionado = null;

        while (!volver) {
            ShowMenu("partidaDefinirDiccionario");
            printDiccionariosDisponibles();
            String userCommand = readLine().trim();

            switch (userCommand) {
                case "0":
                    volver = true;
                    System.out.println("Volviendo a Definir Partida Nueva...");
                    break;
                case "1":
                    System.out.println("Introduce el nombre del diccionario a seleccionar: ");
                    idiomaSeleccionado = readLine();

                    try {
                        controladorDomain.setLenguaje(idiomaSeleccionado);
                    } catch (ExceptionLanguageNotExist e) {
                        System.out.println("Error: " + e.getMessage());
                        idiomaSeleccionado = null;
                    }

                    volver = true;
                    break;
                default:
                    System.out.println("¡Introduce alguno de los comandos disponibles!");
                    break;
            }
        }
        return idiomaSeleccionado;
    }

    public static HashMap<String, String> managePartidaDefinirModo(String[] dificultad) throws IOException {
        boolean volver = false;
        HashMap<String, String> jugadoresSeleccionados = new HashMap<>();

        while (!volver) {
            ShowMenu("partidaDefinirModo");
            String userCommand = readLine().trim();
            
            switch (userCommand) {
                case "0":
                    volver = true;
                    System.out.println("Volviendo a Definir Partida Nueva...");
                    break;
                case "1":
                    managePartidaDefinirModoSolitario(jugadoresSeleccionados, dificultad);
                    System.out.println("Participarán los siguientes jugadores: ");
                    for (String username : jugadoresSeleccionados.keySet()) {
                        System.out.println("-" + username);
                    }                    
                    volver = true;
                    break;
                case "2":
                    managePartidaDefinirModoMultijugador(jugadoresSeleccionados);
                    System.out.println("Participarán los siguientes jugadores: ");
                    for (String username : jugadoresSeleccionados.keySet()) {
                        System.out.println("-" + username);
                    }
                    volver = true;
                    break;
                default:
                    System.out.println("¡Introduce alguno de los comandos disponibles!");
                    break;
            }
        }
        return jugadoresSeleccionados;
    }

    public static void managePartidaDefinirModoMultijugador(HashMap<String, String> jugadoresSeleccionados) throws IOException {
        boolean volver = false;
         // nombre id

        while (!volver) {
            ShowMenu("partidaDefinirModoMultijugador");
            String userCommand = readLine().trim();

            switch (userCommand) {
                case "0":
                    volver = true;
                    System.out.println("Volviendo a Definir Modo...");
                    break;
                case "1":
                    jugadoresSeleccionados.clear(); // Limpiamos por si es un reintento

                    System.out.println("Introduce el número de jugadores: ");
                    int N;

                    try {
                        N = Integer.parseInt(readLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Número inválido. Inténtalo de nuevo.");
                        break;
                    }

                    int contador = 1;
                    boolean error = false; // indicates if an exception ocurred while getting usernames, so that user has to repeat the process again.
                    while (contador <= N && !error) {
                        System.out.println("Introduce el nombre de usuario del jugador " + contador + ": ");
                        String username = readLine();

                        try {
                            String id = controladorDomain.getIdPorNombre(username);
                            if (controladorDomain.playerReadyToPlay(id)) {
                                jugadoresSeleccionados.put(username, id);
                            }

                            System.out.println("Participarán los siguientes jugadores: ");
                                for (String name : jugadoresSeleccionados.keySet()) {
                                    System.out.println("-" + name);
                            }                            
                            contador++;
                        } catch (ExceptionUserNotExist e) {
                            System.out.println("Error: " + e.getMessage());
                            error = true;
                        } catch (ExceptionUserEsIA e) {
                            System.out.println("Error: " + e.getMessage());
                            error = true;
                        } catch (ExceptionUserNotLoggedIn e) {
                            System.out.println("Error: " + e.getMessage());
                            error = true;                            
                        } catch (ExceptionUserInGame e ) {
                            System.out.println("Error: " + e.getMessage());
                            error = true;                        
                        }
                    
                    }
                    if (contador == N && !error) {
                        volver = true;                    
                    }
                    break;

                default:
                    System.out.println("¡Introduce alguno de los comandos disponibles!");
                    break;                    
            }


        }
    }
    
public static void managePartidaDefinirModoSolitario(HashMap<String, String> jugadoresSeleccionados, String[] dificultad) throws IOException {
    boolean volver = false;

    while (!volver) {
        ShowMenu("partidaDefinirModoSolitario");
        String userCommand = readLine().trim();

        switch (userCommand) {
            case "0":
                volver = true;
                System.out.println("Volviendo a Definir Modo...");
                break;

            case "1":
                jugadoresSeleccionados.clear();

                // Enter human player
                System.out.println("Introduce el nombre de usuario del jugador: ");
                String username = readLine();

                try {
                    String id = controladorDomain.getIdPorNombre(username);
                    if (controladorDomain.playerReadyToPlay(id)) {
                        jugadoresSeleccionados.put(username, id);
                    } else {
                        System.out.println("El jugador no está listo para jugar.");
                        break;
                    }
                } catch (ExceptionUserNotExist e) {
                    System.out.println("Error: " + e.getMessage());
                    break;
                } catch (ExceptionUserNotLoggedIn e) {
                    System.out.println("Error: " + e.getMessage());
                    break;                
                } catch (ExceptionUserInGame e) {
                    System.out.println("Error: " + e.getMessage());
                    break;                
                }

                // Enter number of bots and difficulty
                int N;

                try {
                    System.out.println("Introduce el número de bots (mínimo 1) en la partida: ");
                    N = Integer.parseInt(readLine());

                    if (N < 1) {
                        System.out.println("Número inválido. Inténtalo de nuevo.");
                        break;
                    }

                    System.out.println("Selecciona la dificultad de los bots (FACIL/DIFICIL): ");
                    dificultad[0] = readLine().trim().toUpperCase();

                    if (!dificultad[0].equals("FACIL") && !dificultad[0].equals("DIFICIL")) {
                        System.out.println("¡Selecciona una dificultad correcta!");
                        break;
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Número inválido. Inténtalo de nuevo.");
                    break;
                }

                // Insert bots or generate enough quantity of them (if needed) to satisfy the N number of bots
                try {
                    List<String> jugadoresIA = controladorDomain.getJugadoresIA();
                    int botsNeeded = N - jugadoresIA.size();

                    if (botsNeeded > 0) {
                        int newIdInt = jugadoresIA.isEmpty() ? 0 : Integer.parseInt(jugadoresIA.get(jugadoresIA.size() - 1)) + 1;

                        for (int i = 0; i < botsNeeded; i++) {
                            String newId = String.valueOf(newIdInt++);
                            controladorDomain.crearJugadorIA(newId, dificultad[0]);
                            jugadoresIA.add(newId);
                        }
                    }

                    for (int i = 0; i < N; i++) {
                        String botId = jugadoresIA.get(i);
                        String botName = controladorDomain.getNombrePorId(botId);
                        jugadoresSeleccionados.put(botName, botId);
                    }                    

                } catch (ExceptionUserExist e) {
                    System.out.println("Error: " + e.getMessage());
                    break;
                }

                volver = true;
                break;

            default:
                System.out.println("¡Introduce alguno de los comandos disponibles!");
                break;
        }
    }
}

     
    

    public static void managePartidaCargar () throws IOException{
     
        boolean volver = false;
        while (!volver) {
            ShowMenu("partidaCargar");
            String userCommand = readLine().trim();

            switch (userCommand) {
                case "0":
                    volver = true;
                    System.out.println("Volviendo a Gestión de Partidas...");
                    break;
                case "1":
                    String nombre;

                    System.out.println("Introduce un nombre para seleccionar la partida deseada: ");
                    nombre= readLine();

                    try {
                        // controladorDomain.cargarPartida(nombre);                                       
                    } catch (ExceptionPartidaNotExist e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                default:
                    System.out.println("¡Introduce alguno de los comandos disponibles!");
                    break;
            }
        }
    }

    

}