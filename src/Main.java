import config.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ControladorConfiguracion controlador = new ControladorConfiguracion();
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Sistema de Configuración ===");
        System.out.println("1. Iniciar sesión");
        System.out.println("2. Registrarse");
        System.out.println("0. Salir");        
        int opcion_login;
        opcion_login = scanner.nextInt();

        switch (opcion_login) {
            case 1:

                break;
            case 2:    
                break;
            case 0:
                    System.out.println("¡Hasta luego!");
                    break;
                default:
                    System.out.println("Opción inválida.");
                    break;
        }
        System.out.print("Introduce: ");
        String usuario = scanner.nextLine();

        System.out.print("Contraseña: ");
        String contrasena = scanner.nextLine();

        if (!controlador.autenticar(usuario, contrasena)) {
            System.out.println("¡Autenticación fallida!");
            return;
        }

        int opcion;
        do {
            System.out.println("\n--- Menú ---");
            System.out.println("1. Agregar configuración");
            System.out.println("2. Ver configuración");
            System.out.println("3. Actualizar configuración");
            System.out.println("4. Eliminar configuración");
            System.out.println("5. Listar configuraciones");
            System.out.println("0. Salir");
            System.out.print("Selecciona una opción: ");
            opcion = scanner.nextInt();
            scanner.nextLine(); // limpiar buffer

            String clave, valor;
            switch (opcion) {
                case 1:
                    System.out.print("Clave: ");
                    clave = scanner.nextLine();
                    System.out.print("Valor: ");
                    valor = scanner.nextLine();
                    controlador.agregarConfiguracion(clave, valor);
                    break;
                case 2:
                    System.out.print("Clave: ");
                    clave = scanner.nextLine();
                    controlador.verConfiguracion(clave);
                    break;
                case 3:
                    System.out.print("Clave: ");
                    clave = scanner.nextLine();
                    System.out.print("Nuevo valor: ");
                    valor = scanner.nextLine();
                    controlador.actualizarConfiguracion(clave, valor);
                    break;
                case 4:
                    System.out.print("Clave: ");
                    clave = scanner.nextLine();
                    controlador.eliminarConfiguracion(clave);
                    break;
                case 5:
                    controlador.listarConfiguraciones();
                    break;
                case 0:
                    System.out.println("¡Hasta luego!");
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        } while (opcion != 0);
    }
}
