package provisional_testing_folder;

import java.util.HashMap;
import java.util.Scanner;
// import scrabble.domain.controllers.ControladorDomain;

/**
 * Clase principal para ejecutar el programa.
 * Esta clase contiene el método main que inicia la aplicación y gestiona la interacción con el usuario.
 */

public class DomainDriver {
    static boolean showmenu = true; // Variable para controlar el menú
    static boolean logged = false; // Variable para controlar el estado de inicio de sesión
    static HashMap<String, String> menus = new HashMap<>(); // Mapa para almacenar los menús
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        iniciarMenus(); // Inicializar los menús
        // ControladorDomain controladorDomain = new ControladorDomain();
        int opcion;
        while (showmenu) {
            printMenu("welcome"); // Imprimir el menú de bienvenida
            System.out.println("Por favor, escribe el número de la opción deseada: ");
            try {
                opcion = Integer.parseInt(scanner.nextLine()); // Leer la opción del usuario
            } catch (NumberFormatException e) {
                System.out.println("Opción no válida. Por favor, introduce un número.");
                continue; // Volver a mostrar el menú si la opción no es válida
            }
            switch (opcion) {
                case 1 -> {
                    cleanConsole();
                    System.out.println("Ingrese ID de usuario:");
                    String id = scanner.nextLine();
                    System.out.println("Ingrese contraseña:");
                    String password = scanner.nextLine();
                    if (true) {
                        cleanConsole();
                        System.out.println("Inicio de sesión exitoso.");
                        show_menu_after_login();
                        
                    } //else {
                    //     System.out.println("Error al iniciar sesión. Verifique su ID y contraseña.");
                    // }
                }
                case 2 -> {
                    cleanConsole();
                    System.out.println("Ingrese ID de usuario:");
                    String id = scanner.nextLine();
                    System.out.println("Ingrese contraseña:");
                    String password = scanner.nextLine();
                    // controladorDomain.registrarUsuario(id, password);
                }
                case 3 -> {
                    System.out.println("Saliendo...");
                    showmenu = false; // Salir del bucle y cerrar el programa
                }
                default -> System.out.println("Opción no válida. Por favor, introduce un número entre 1 y 3.");
            }
        }      
    }

    /**
     * Método para limpiar la consola.
     * Este método imprime varias líneas en blanco para simular la limpieza de la consola.
     */

    public static void cleanConsole() {
        for (int i = 0; i < 20 ; i++) {
            System.out.println();
        }
    }

    /**
     * Método para imprimir un menú específico.
     * @param menu El nombre del menú a imprimir.
     */

    public static void printMenu(String menu) {
        System.out.println(menus.get(menu));
    }

    public static void show_menu_after_login() {
       logged = true; // Cambiar el estado de inicio de sesión a verdadero
       while (showmenu && logged) {
            printMenu("menu_after_login"); // Imprimir el menú después de iniciar sesión
            System.out.println("Por favor, escribe el número de la opción deseada: ");
            int opcion;
            Scanner scanner = new Scanner(System.in);
            try {
                opcion = Integer.parseInt(scanner.nextLine()); // Leer la opción del usuario
            } catch (NumberFormatException e) {
                System.out.println("Opción no válida. Por favor, introduce un número.");
                continue; // Volver a mostrar el menú si la opción no es válida
            }
            switch (opcion) {
                case 1 -> System.out.println("Opción 1 seleccionada.");
                case 2 -> System.out.println("Opción 2 seleccionada.");
                case 3 -> System.out.println("Opción 3 seleccionada.");
                case 4 -> {
                    cleanConsole();
                    System.out.println("Cerrando sesión...");
                    logged = false; // Cambiar el estado de inicio de sesión a falso
                }
                case 5 -> {
                    System.out.println("Saliendo...");
                    showmenu = false; // Salir del bucle y cerrar el programa
                    logged = false; // Cambiar el estado de inicio de sesión a falso
                }
                default -> System.out.println("Opción no válida. Por favor, introduce un número entre 1 y 5.");
            }
        }
    }

    /**
     * Método para inicializar los menús.
     * Este método llena el mapa de menús con las opciones disponibles.
     */

    public static void iniciarMenus() {
            menus.put("welcome", """
                +--------------------------------------------------------------------------------+
                |                           BIENVENIDO A SCRABBLE                                |
                +--------------------------------------------------------------------------------+
                |                                                                                |
                |                  ¡Prepárate para formar palabras épicas!                       |
                |                                                                                |
                |                       [1] Iniciar sesión                                       |
                |                       [2] Registrar Usuario                                    |
                |                       [3] Salir                                                |
                |                                                                                |
                |                                                                                |
                +--------------------------------------------------------------------------------+
                """
            );
             menus.put("menu_after_login", """
                +--------------------------------------------------------------------------------+
                |                           BIENVENIDO A SCRABBLE                                |
                +--------------------------------------------------------------------------------+
                |                                                                                |
                |                  ¡Prepárate para formar palabras épicas!                       |
                |                                                                                |
                |                       [1] Gestión de partidas                                  |
                |                       [2] Gestión de configuración                             |
                |                       [3] Ver ranking                                          |
                |                       [4] Cerrar sesión                                        |
                |                       [5] Salir                                                |
                |                                                                                |
                +--------------------------------------------------------------------------------+
                """
            );
    }
}


