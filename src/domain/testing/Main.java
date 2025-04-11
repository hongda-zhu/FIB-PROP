package domain.testing;

import java.util.Scanner;

import domain.controllers.*;
import domain.controllers.subcontrollers.ControladorConfiguracion;

public class Main {
    public static void main(String[] args) {
        ControladorConfiguracion controlador = new ControladorConfiguracion();
        Scanner scanner = new Scanner(System.in);

      
       
        Boolean logged = false;
        Boolean exit = false;
        
        while (!logged && !exit) {
            
        	System.out.println("=== Sistema de Configuración ===");
            System.out.println("1. Iniciar sesión");
            System.out.println("2. Registrarse");
            System.out.println("0. Salir");  
            
            int opcion_login;
            opcion_login = scanner.nextInt();
            scanner.nextLine();
            switch (opcion_login) {
                case 1:
                	String username, password;
                	
                    System.out.println("Introduce un nombre de usuario:"); 
                    username = scanner.nextLine();
                    if(!controlador.existeUsuario(username)) System.out.println("ERROR: usuario no registrado!");
                    else {
                        System.out.println("Introduce una contraseña:"); 
                        password = scanner.nextLine();
                        if (controlador.autenticar(username, password)) logged = true;
                        	
                    }
                    

                    break;
                    
                case 2:
                    System.out.println("Introduce un nombre de usuario:"); 
                    
                    username = scanner.nextLine();
                    if(controlador.existeUsuario(username)) System.out.println("ERROR: usuario ya registrado!");
                    
                    
                    System.out.println("Introduce una contraseña:"); 
                    
                    password = scanner.nextLine();                	
                	
                    if(controlador.registrarUsuario(username, password)) System.out.println("Se ha registrado correctamente!");
      
                    break;
                case 0:
                        System.out.println("¡Hasta luego!");
                        exit = true;
                        break;
                default:
                        System.out.println("Opción inválida.");
                        break;
            }    	
        }

        if (logged) {
        	System.out.println("Se ha iniciado correctamente!");
        }
        /* int opcion;
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
        } while (opcion != 0); */
        
    }
}
