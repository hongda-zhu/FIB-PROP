package random_tests;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scrabble.domain.controllers.ControladorDomain;
import scrabble.excepciones.ExceptionLanguageExist;
import scrabble.excepciones.ExceptionLanguageNotExist;
import scrabble.excepciones.ExceptionPartidaNotExist;
import scrabble.excepciones.ExceptionRankingOperationFailed;
import scrabble.excepciones.ExceptionUserEsIA;
import scrabble.excepciones.ExceptionUserExist;
import scrabble.excepciones.ExceptionUserInGame;
import scrabble.excepciones.ExceptionUserLoggedIn;
import scrabble.excepciones.ExceptionUserNotExist;
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
        // jugadoresSeleccionados ahora mapea nombre -> nombre
        BooleanWrapper pausado = new BooleanWrapper(false);

        while (!controladorDomain.isJuegoTerminado()) {
            System.out.println("Presiona 'X' para pausar el juego ");

            if (!pausado.value) {
                for (Map.Entry<String, String> entry : jugadoresSeleccionados.entrySet()) {
                    String nombreJugador = entry.getKey();
                    // Ahora nombreJugador es también el identificador
                    
                    Tuple<Map<String, Integer>, Integer> result = controladorDomain.realizarTurno(nombreJugador, dificultad[0], pausado);

                    
                    if (result == null && !pausado.value) {
                        System.out.println("El jugador " + nombreJugador  + " paso la jugada.");
                        controladorDomain.addSkipTrack(nombreJugador);
                    } else if (result != null && !pausado.value) {
                        controladorDomain.inicializarRack(nombreJugador, result.x);
                        controladorDomain.addPuntuacion(nombreJugador, result.y);
                        
                        Map<String, Integer> nuevasFicha = controladorDomain.cogerFichas(7 - controladorDomain.getCantidadFichas(nombreJugador));
                        
                        if (nuevasFicha == null) {
                            controladorDomain.finalizarJuego();
                            
                        } else {
                            for (Map.Entry<String, Integer> fichas : nuevasFicha.entrySet()) {
                                String letra = fichas.getKey();
                                int cantidad = fichas.getValue();
                                for (int i = 0; i < cantidad; i++) {
                                    controladorDomain.agregarFicha(nombreJugador, letra);
                                }   
                            }
                        }
                    }                

                    
                }

                boolean allskiped = true;

                for (Map.Entry<String, String> entry : jugadoresSeleccionados.entrySet()) {
                    String nombreJugador = entry.getValue();
                    if (controladorDomain.getSkipTrack(nombreJugador) < 3) {
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
            String nombreJugador = entry.getKey();
            System.out.println("Jugador: " + nombreJugador);
            System.out.println("Puntuación: " + controladorDomain.getPuntuacion(nombreJugador));
            System.out.println("-----------------------------");
        }        
        System.out.println("Gracias por jugar :)");        

    }

    public static void initializeDefaultSettings() {
        String rutaFichas = "src/provisional_testing_folder/resources/alpha.txt"; // Ruta del archivo de fichas
        String rutaAlphabet = "src/provisional_testing_folder/resources/words.txt"; // Ruta del archivo de fichas    
        if (!controladorDomain.existeLenguaje("Esp")) controladorDomain.anadirLenguaje("Esp", rutaFichas, rutaAlphabet);
        
        // Registrar algunos usuarios por defecto para testing
        if (!controladorDomain.existeJugador("admin")) controladorDomain.registrarUsuario("admin");
        if (!controladorDomain.existeJugador("admin2")) controladorDomain.registrarUsuario("admin2");
    }



    public static void initializeMenus() {
        // 1. Usuarios
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
                                    |                                                                              |
                                    |   [ 1 ] Crear usuario              - Crea un nuevo usuario en el sistema.    |
                                    |   [ 2 ] Eliminar usuario           - Elimina un usuario existente.           |
                                    |   [ 3 ] Mostrar usuarios           - Muestra todos los usuarios registrados. |
                                    |   [ 0 ] Volver                     - Vuelve al Menú Principal.               | 
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);

        menus.put("usuarioCrear", """
                                    +------------------------------------------------------------------------------+
                                    | GESTIÓN DE USUARIOS > CREAR USUARIO                                          |
                                    |                                                                              |
                                    |   ¿Cómo crear un nuevo usuario?                                              |
                                    |   Introduce un nombre de usuario                                             |
                                    |                                                                              |
                                    |   [ 2 ] Crear usuario              - Crea un nuevo usuario en el sistema.    |
                                    |   [ 1 ] Ver usuarios               - Muestra la lista de usuarios existentes |
                                    |   [ 0 ] Volver                     - Vuelve a Gestión de Usuarios.           |
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);

        menus.put("usuarioEliminar", """
                                    +------------------------------------------------------------------------------+
                                    | GESTIÓN DE USUARIOS > ELIMINAR USUARIO                                       |
                                    |                                                                              |
                                    |   ¿Cómo eliminar usuario?                                                    |
                                    |   Introduce un nombre de usuario al que quiere eliminar                      |
                                    |                                                                              |
                                    |   [ 2 ] Eliminar usuario           - Elimina un usuario existente.           |
                                    |   [ 1 ] Ver usuarios               - Muestra la lista de usuarios disponibles|
                                    |   [ 0 ] Volver                     - Vuelve a Gestión de Usuarios.           |
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);
                                                                                                                                                                                                                                                                                    
                                                                                                                                                                                                                          
        // 2. Diccionarios
        menus.put("diccionario", """
                                    +------------------------------------------------------------------------------+
                                    | GESTIÓN DE DICCIONARIOS |                                                    |
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
                                    |                                                                              |
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
                                    |                                                                              |
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
                    System.out.println("Volviendo a Gestión de Usuarios...");
                    break;
                case "1":
                    // Mostrar la lista de usuarios existentes
                    System.out.println("+------------------------------------------------------------------------------+");
                    System.out.println("| USUARIOS EXISTENTES                                                         |");
                    System.out.println("+------------------------------------------------------------------------------+");
                    mostrarUsuariosFormateados(null);
                    break;
                case "2":
                    // Mostrar panel para introducir nombre de usuario
                    System.out.println("""
                        +------------------------------------------------------------------------------+
                        | GESTIÓN DE USUARIOS > CREAR USUARIO > INTRODUCIR NOMBRE DE USUARIO           |
                        |                                                                              |
                        |   Introduce un nombre de usuario:                                            |
                        |                                                                              |
                        +------------------------------------------------------------------------------+
                        """);
                    
                    user = readLine();

                    try {
                        if(controladorDomain.registrarUsuario(user)) {
                            // Mostrar usuarios actualizados con el nuevo usuario
                            System.out.println("+------------------------------------------------------------------------------+");
                            System.out.println("| USUARIOS REGISTRADOS                                                        |");
                            System.out.println("+------------------------------------------------------------------------------+");
                            mostrarUsuariosFormateados(user); // Pasamos el usuario recién creado para destacarlo
                            
                            // Mostrar mensaje de éxito después del listado
                            System.out.println("""
                                +------------------------------------------------------------------------------+
                                | GESTIÓN DE USUARIOS > CREAR USUARIO                                          |
                                |                                                                              |
                                |   ¡Usuario '%s' creado correctamente!                                        |
                                |                                                                              |
                                +------------------------------------------------------------------------------+
                                """.formatted(user));
                        }
                    } catch (ExceptionUserExist e) {
                        System.out.println("""
                            +------------------------------------------------------------------------------+
                            | GESTIÓN DE USUARIOS > CREAR USUARIO                                          |
                            |                                                                              |
                            |   Error: El usuario ya existe.                                               |
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
        System.out.println("| USUARIOS REGISTRADOS                                                        |");
        System.out.println("+------------------------------------------------------------------------------+");
        System.out.println("| NOMBRE            | TIPO      | PARTIDAS          | PUNTUACIÓN              |");
        System.out.println("+-------------------+-----------+-------------------+-------------------------+");
        
        // Primero capturamos la salida original en una cadena de texto
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.PrintStream originalOut = System.out;
        System.setOut(new java.io.PrintStream(baos));
        
        // Llamamos al método que ya existe para mostrar usuarios
        controladorDomain.mostrarTodosUsuariosDebug();
        
        // Restauramos la salida estándar
        System.setOut(originalOut);
        
        // Convertimos la salida capturada a texto
        String output = baos.toString();
        String[] lines = output.split("\\r?\\n");
        
        List<String> usuarios = new ArrayList<>();
        List<String> usuariosIA = new ArrayList<>();
        
        // Procesamos las líneas obtenidas
        boolean procesandoUsuarios = false;
        for (String line : lines) {
            if (line.contains("=== LISTADO DEBUG DE USUARIOS ===")) {
                procesandoUsuarios = true;
                continue;
            }
            
            if (procesandoUsuarios && line.contains("Nombre:")) {
                if (line.contains("Tipo: IA")) {
                    usuariosIA.add(line);
                } else {
                    usuarios.add(line);
                }
            }
        }
        
        // Mostramos los usuarios humanos
        for (String line : usuarios) {
            String nombreUsuario = line.substring(line.indexOf("Nombre: ") + 8, line.indexOf(" |")).trim();
            String partidasInfo = "No hay partidas"; // Valor por defecto
            
            if (line.contains("Partidas:")) {
                String partidasRaw = line.substring(line.indexOf("Partidas: ") + 10).trim();
                // Extraer solo el número de partidas jugadas
                if (partidasRaw.contains("/")) {
                    int numPartidas = Integer.parseInt(partidasRaw.split("/")[1]);
                    if (numPartidas > 0) {
                        partidasInfo = numPartidas + " partida" + (numPartidas > 1 ? "s" : "");
                    }
                }
            }
            
            String puntuacionInfo = "Sin puntuación"; // Valor por defecto
            
            // Destacar el usuario recién creado
            String indicadorNuevo = (nombreUsuario.equals(usuarioNuevo)) ? " ★ NUEVO" : "";
            
            System.out.printf("| %-17s | %-9s | %-17s | %-23s |%s%n", 
                            nombreUsuario, "Humano", partidasInfo, puntuacionInfo, indicadorNuevo);
        }
        
        // Mostramos los usuarios IA
        for (String line : usuariosIA) {
            String nombreUsuario = line.substring(line.indexOf("Nombre: ") + 8, line.indexOf(" |")).trim();
            String dificultad = "DESCONOCIDA";
            
            if (line.contains("Dificultad:")) {
                dificultad = line.substring(line.lastIndexOf("Dificultad: ") + 12).trim();
            }
            
            System.out.printf("| %-17s | IA-%-7s | %-17s | %-23s |%n", 
                            nombreUsuario, dificultad, "N/A", "N/A");
        }
        
        System.out.println("+------------------------------------------------------------------------------+");
        System.out.println("| Total usuarios: " + (usuarios.size() + usuariosIA.size()) + 
                         " (Humanos: " + usuarios.size() + 
                         ", IA: " + usuariosIA.size() + ")                           |");
        System.out.println("+------------------------------------------------------------------------------+");
    }

    public static void manageUserEliminar() throws IOException{
     
        boolean volver = false;
        while (!volver) {
            // Usar el menú predefinido en lugar de texto repetido
            ShowMenu("usuarioEliminar");
            
            String userCommand = readLine().trim();
            String user;

            switch (userCommand) {
                case "0":
                    volver = true;
                    System.out.println("Volviendo a Gestión de Usuarios...");
                    break;
                case "1":
                    // Mostrar la lista de usuarios existentes
                    System.out.println("+------------------------------------------------------------------------------+");
                    System.out.println("| USUARIOS DISPONIBLES PARA ELIMINAR                                          |");
                    System.out.println("+------------------------------------------------------------------------------+");
                    mostrarUsuariosFormateados(null);
                    break;
                case "2":
                    System.out.println("""
                        +------------------------------------------------------------------------------+
                        | GESTIÓN DE USUARIOS > ELIMINAR USUARIO > INTRODUCIR NOMBRE                   |
                        |                                                                              |
                        |   Introduce un nombre de usuario a eliminar:                                 |
                        |                                                                              |
                        +------------------------------------------------------------------------------+
                        """);
                    
                    user = readLine();

                    try {
                        // Verificar si el usuario está en una partida
                        if (controladorDomain.existeJugador(user)) {
                            if (controladorDomain.isEnPartida(user)) {
                                System.out.println("""
                                    +------------------------------------------------------------------------------+
                                    | GESTIÓN DE USUARIOS > ELIMINAR USUARIO                                       |
                                    |                                                                              |
                                    |   Error: El usuario está actualmente en una partida.                         |
                                    |   No se puede eliminar mientras esté en una partida activa.                  |
                                    |                                                                              |
                                    +------------------------------------------------------------------------------+
                                    """);
                                break;
                            }
                        }
                        
                        if(controladorDomain.eliminarUsuario(user)) {
                            // Mostrar usuarios restantes
                            System.out.println("+------------------------------------------------------------------------------+");
                            System.out.println("| USUARIOS REGISTRADOS                                                        |");
                            System.out.println("+------------------------------------------------------------------------------+");
                            mostrarUsuariosFormateados(null);
                            
                            // Mostrar mensaje de éxito después del listado
                            System.out.println("""
                                +------------------------------------------------------------------------------+
                                | GESTIÓN DE USUARIOS > ELIMINAR USUARIO                                       |
                                |                                                                              |
                                |   ¡Usuario '%s' eliminado correctamente!                                     |
                                |                                                                              |
                                +------------------------------------------------------------------------------+
                                """.formatted(user));
                        }
                    } catch (ExceptionUserLoggedIn e) {
                        System.out.println("""
                            +------------------------------------------------------------------------------+
                            | GESTIÓN DE USUARIOS > ELIMINAR USUARIO                                       |
                            |                                                                              |
                            |   Error: El usuario está logueado. Cierra su sesión antes.                   |
                            |                                                                              |
                            +------------------------------------------------------------------------------+
                            """);
                    } catch (ExceptionUserNotExist e) {
                        System.out.println("""
                            +------------------------------------------------------------------------------+
                            | GESTIÓN DE USUARIOS > ELIMINAR USUARIO                                       |
                            |                                                                              |
                            |   Error: El usuario no existe.                                               |
                            |                                                                              |
                            +------------------------------------------------------------------------------+
                            """);
                    } catch (ExceptionRankingOperationFailed e) {
                        System.out.println("""
                            +------------------------------------------------------------------------------+
                            | GESTIÓN DE USUARIOS > ELIMINAR USUARIO                                       |
                            |                                                                              |
                            |   Error: No se ha podido eliminar usuario del ranking.                       |
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
        // Mostrar usuarios en formato personalizado en lugar del debug
        // No imprimir encabezado aquí porque mostrarUsuariosFormateados ya lo hace
        mostrarUsuariosFormateados(null);
        
        // Agregar una notificación de operación realizada
        System.out.println("+------------------------------------------------------------------------------+");
        System.out.println("| GESTIÓN DE USUARIOS                                                         |");
        System.out.println("|                                                                              |");
        System.out.println("| Operación 'Mostrar usuarios' realizada correctamente.                        |");
        System.out.println("|                                                                              |");
        System.out.println("+------------------------------------------------------------------------------+");
        
        // No se muestra submenú, se regresa directamente al menú principal
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
        // nombre -> nombre (mismo valor como identificador único)

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
                        String nombre = readLine();

                        try {
                            if (!controladorDomain.existeJugador(nombre)) {
                                throw new ExceptionUserNotExist();
                            }
                            
                            if (controladorDomain.esIA(nombre)) {
                                throw new ExceptionUserEsIA();
                            }
                            
                            if (controladorDomain.isEnPartida(nombre)) {
                                throw new ExceptionUserInGame();
                            }
                            
                            // El nombre es también el ID en el nuevo sistema
                            jugadoresSeleccionados.put(nombre, nombre);

                            System.out.println("Participarán los siguientes jugadores: ");
                            for (String name : jugadoresSeleccionados.keySet()) {
                                System.out.println("-" + name);
                            }                            
                            contador++;
                        } catch (ExceptionUserNotExist e) {
                            System.out.println("Error: El usuario no existe.");
                            error = true;
                        } catch (ExceptionUserEsIA e) {
                            System.out.println("Error: El usuario es una IA.");
                            error = true;
                        } catch (ExceptionUserInGame e) {
                            System.out.println("Error: El usuario ya está en una partida.");
                            error = true;                        
                        }
                    }
                    
                    if (contador > N && !error) {
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
                String nombre = readLine();

                try {
                    if (!controladorDomain.existeJugador(nombre)) {
                        throw new ExceptionUserNotExist();
                    }
                    
                    if (controladorDomain.esIA(nombre)) {
                        throw new ExceptionUserEsIA();
                    }
                    
                    if (controladorDomain.isEnPartida(nombre)) {
                        throw new ExceptionUserInGame();
                    }
                    
                    // El nombre es también el ID en el nuevo sistema
                    jugadoresSeleccionados.put(nombre, nombre);
                    
                } catch (ExceptionUserNotExist e) {
                    System.out.println("Error: El usuario no existe.");
                    break;
                } catch (ExceptionUserEsIA e) {
                    System.out.println("Error: No puedes seleccionar una IA como jugador humano.");
                    break;
                } catch (ExceptionUserInGame e) {
                    System.out.println("Error: El usuario ya está en una partida.");
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

                // Crear los bots necesarios
                try {
                    for (int i = 0; i < N; i++) {
                        String botNombre = controladorDomain.crearJugadorIA(dificultad[0]);
                        if (botNombre != null) {
                            jugadoresSeleccionados.put(botNombre, botNombre); // El nombre es también el ID
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error al crear los bots: " + e.getMessage());
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

    /**
     * Gestiona el menú de ranking, ofreciendo las opciones para ver, consultar historial y filtrar.
     */
    public static void manageRankingMenu() throws IOException {
        boolean volver = false;

        while (!volver) {
            ShowMenu("ranking");
            String userCommand = readLine().trim();

            switch (userCommand) {
                case "0":
                    volver = true;
                    System.out.println("Volviendo al menú principal...");
                    ShowMenu("principal");
                    break;
                case "1":
                    manageRankingVer();               
                    break;
                case "2":
                    manageRankingHistorial();                   
                    break;
                case "3":
                    manageRankingFiltrar();
                    break;                    
                default:
                    System.out.println("¡Introduce alguno de los comandos disponibles!");
                    break;            
            }
        }
        
        // Guardar los datos del ranking al salir del menú
        controladorDomain.guardarRanking();
    }

    /**
     * Muestra el ranking de jugadores según la estrategia actual.
     */
    public static void manageRankingVer() throws IOException {
        boolean volver = false;
        String criterioActual = "maxima"; // Criterio por defecto
        
        while (!volver) {
            // Obtener la lista ordenada de usuarios según el criterio actual
            List<String> rankingOrdenado = controladorDomain.getRanking(criterioActual);
            
            // Mostrar el encabezado del ranking
            System.out.println("+------------------------------------------------------------------------------+");
            System.out.println("| RANKING DE JUGADORES (" + controladorDomain.getEstrategiaRanking() + ")      |");
            System.out.println("+------------------------------------------------------------------------------+");
            System.out.println("| Posición | Usuario             | Punt. Máxima | Punt. Media | Partidas | Victorias |");
            System.out.println("+----------+---------------------+--------------+-------------+----------+-----------+");
            
            // Mostrar cada usuario en el ranking
            int posicion = 1;
            for (String id : rankingOrdenado) {
                int puntuacionMaxima = controladorDomain.getPuntuacionMaxima(id);
                double puntuacionMedia = controladorDomain.getPuntuacionMedia(id);
                int partidas = controladorDomain.getPartidasJugadas(id);
                int victorias = controladorDomain.getVictorias(id);
                
                System.out.printf("| %-8d | %-19s | %-12d | %-11.2f | %-8d | %-9d |%n", 
                                posicion++, id, puntuacionMaxima, puntuacionMedia, partidas, victorias);
            }
            
            System.out.println("+------------------------------------------------------------------------------+");
            
            // Mostrar las opciones de ordenación
            System.out.println("""
                +------------------------------------------------------------------------------+
                | OPCIONES DE ORDENACIÓN                                                       |
                |                                                                              |
                |   [ 1 ] Ordenar por puntuación máxima                                        |
                |   [ 2 ] Ordenar por puntuación media                                         |
                |   [ 3 ] Ordenar por partidas jugadas                                         |
                |   [ 4 ] Ordenar por ratio de victorias                                       |
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
                    criterioActual = "maxima";
                    controladorDomain.cambiarEstrategiaRanking(criterioActual);
                    break;
                case "2":
                    criterioActual = "media";
                    controladorDomain.cambiarEstrategiaRanking(criterioActual);
                    break;
                case "3":
                    criterioActual = "partidas";
                    controladorDomain.cambiarEstrategiaRanking(criterioActual);
                    break;
                case "4":
                    criterioActual = "victorias";
                    controladorDomain.cambiarEstrategiaRanking(criterioActual);
                    break;
                default:
                    System.out.println("Opción no válida. Inténtalo de nuevo.");
                    break;
            }
        }
    }

    /**
     * Consulta el historial de un jugador específico.
     */
    public static void manageRankingHistorial() throws IOException {
        boolean volver = false;
        
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
                
                // Mostrar la información del jugador
                System.out.println("+------------------------------------------------------------------------------+");
                System.out.println("| HISTORIAL DEL JUGADOR: " + username);
                System.out.println("+------------------------------------------------------------------------------+");
                System.out.println("| Estadísticas generales:");
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
                
                System.out.println("Presiona Enter para continuar...");
                readLine();
            } else {
                System.out.println("Error: El usuario '" + username + "' no está en el ranking.");
                System.out.println("Presiona Enter para continuar...");
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
            System.out.println("""
                +------------------------------------------------------------------------------+
                | FILTRAR RANKING                                                              |
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
                    System.out.println("Opción no válida. Inténtalo de nuevo.");
                    break;
            }
        }
    }

    /**
     * Filtra el ranking por puntuación mínima.
     */
    private static void filtrarPorPuntuacionMinima() throws IOException {
        System.out.println("Introduce la puntuación mínima:");
        
        try {
            int puntuacionMinima = Integer.parseInt(readLine().trim());
            
            // Obtener los usuarios
            List<String> usuarios = controladorDomain.getUsuarios();
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
            System.out.println("Error: Introduce un número válido.");
        }
    }

    /**
     * Filtra el ranking por número mínimo de partidas jugadas.
     */
    private static void filtrarPorPartidasMinimasJugadas() throws IOException {
        System.out.println("Introduce el número mínimo de partidas jugadas:");
        
        try {
            int partidasMinimas = Integer.parseInt(readLine().trim());
            
            // Obtener los usuarios
            List<String> usuarios = controladorDomain.getUsuarios();
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
            System.out.println("Error: Introduce un número válido.");
        }
    }

    /**
     * Filtra el ranking por victorias mínimas.
     */
    private static void filtrarPorVictoriasMinimas() throws IOException {
        System.out.println("Introduce el número mínimo de victorias:");
        
        try {
            int victoriasMinimas = Integer.parseInt(readLine().trim());
            
            // Obtener los usuarios
            List<String> usuarios = controladorDomain.getUsuarios();
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
            System.out.println("Error: Introduce un número válido.");
        }
    }

    /**
     * Muestra un ranking filtrado con los usuarios especificados.
     */
    private static void mostrarRankingFiltrado(List<String> usuariosFiltrados, String criterioDeFiltrado) throws IOException {
        // Ordenar los usuarios filtrados según la estrategia actual
        String criterioActual = controladorDomain.getEstrategiaActual();
        
        // Reordenar la lista de usuarios filtrados según el criterio actual
        List<String> usuariosOrdenados = new ArrayList<>(usuariosFiltrados);
        switch (criterioActual.toLowerCase()) {
            case "maxima":
                usuariosOrdenados.sort((u1, u2) -> {
                    int comp = Integer.compare(controladorDomain.getPuntuacionMaxima(u2), 
                                            controladorDomain.getPuntuacionMaxima(u1));
                    return comp != 0 ? comp : u1.compareTo(u2); // Orden alfabético en caso de empate
                });
                break;
            case "media":
                usuariosOrdenados.sort((u1, u2) -> {
                    int comp = Double.compare(controladorDomain.getPuntuacionMedia(u2), 
                                        controladorDomain.getPuntuacionMedia(u1));
                    return comp != 0 ? comp : u1.compareTo(u2);
                });
                break;
            case "partidas":
                usuariosOrdenados.sort((u1, u2) -> {
                    int comp = Integer.compare(controladorDomain.getPartidasJugadas(u2), 
                                            controladorDomain.getPartidasJugadas(u1));
                    return comp != 0 ? comp : u1.compareTo(u2);
                });
                break;
            case "victorias":
                usuariosOrdenados.sort((u1, u2) -> {
                    double ratio1 = controladorDomain.getPartidasJugadas(u1) > 0 ?
                                    (double) controladorDomain.getVictorias(u1) / controladorDomain.getPartidasJugadas(u1) : 0;
                    double ratio2 = controladorDomain.getPartidasJugadas(u2) > 0 ?
                                    (double) controladorDomain.getVictorias(u2) / controladorDomain.getPartidasJugadas(u2) : 0;
                                    
                    int comp = Double.compare(ratio2, ratio1);
                    return comp != 0 ? comp : u1.compareTo(u2);
                });
                break;
        }
        
        // Mostrar el encabezado del ranking
        System.out.println("+------------------------------------------------------------------------------+");
        System.out.println("| RANKING FILTRADO - " + criterioDeFiltrado);
        System.out.println("| Ordenado por: " + controladorDomain.getEstrategiaRanking());
        System.out.println("+------------------------------------------------------------------------------+");
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
                
                System.out.printf("| %-8d | %-19s | %-12d | %-11.2f | %-8d | %-9d |%n", 
                                posicion++, id, puntuacionMaxima, puntuacionMedia, partidas, victorias);
            }
        }
        
        System.out.println("+------------------------------------------------------------------------------+");
        System.out.println("Presiona Enter para continuar...");
        readLine();
    }
}
