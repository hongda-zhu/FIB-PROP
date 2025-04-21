// package provisional_testing_folder;
// package domain.testing;

// import scrabble.domain.controllers.ControladorDomain;
// import java.io.File;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Scanner;

// public class Main3 {
//     public static void main(String[] args) {
//         Scanner scanner = new Scanner(System.in);
//         ControladorDomain controladorDomain = new ControladorDomain();
//         int opcion;
//         boolean estaLogeado = false;  // Variable para saber si un usuario está logeado
//         String username = "";  // Almacena el nombre de usuario logeado

//         while (true) {
//             if (!estaLogeado) {
//                 // Menú antes de logear
//                 System.out.println("Seleccione una opción:");
//                 System.out.println("1. Iniciar sesión");
//                 System.out.println("2. Registrar usuario");
//                 System.out.println("0. Salir");  // Siempre tiene la opción de salir
//             } else {
//                 // Menú después de logeado
//                 System.out.println("Seleccione una opción:");
//                 System.out.println("1. Cerrar sesión");
//                 System.out.println("2. Gestión de configuración");
//                 System.out.println("3. Gestión de diccionario");
//                 System.out.println("4. Ver ranking");
//                 System.out.println("5. Agregar puntuación");
//                 System.out.println("6. Gestión de partidas");
//                 System.out.println("0. Salir");
//             }

//             System.out.print("Opción: ");
//             opcion = scanner.nextInt();
//             scanner.nextLine(); // Limpiar el buffer

//             switch (opcion) {
//                 case 1:
//                     if (!estaLogeado) {
//                         // Iniciar sesión (si no está logeado)
//                         System.out.print("Ingrese nombre de usuario: ");
//                         username = scanner.nextLine();
//                         System.out.print("Ingrese contraseña: ");
//                         String password = scanner.nextLine();
//                         try {
//                             controladorDomain.iniciarSesion(username, password);
//                             estaLogeado = true;  // El usuario está logeado
//                             System.out.println("¡Sesión iniciada con éxito!");
//                         } catch (Exception e) {
//                             System.out.println("Error: " + e.getMessage());
//                         }
//                     } else {
//                         // Cerrar sesión (si ya está logeado)
//                         try {
//                             controladorDomain.cerrrarSesion(username);
//                             estaLogeado = false;  // El usuario se desloguea
//                             System.out.println("Sesión cerrada con éxito.");
//                         } catch (Exception e) {
//                             System.out.println("Error: " + e.getMessage());
//                         }
//                     }
//                     break;

//                 case 2:
//                     if (!estaLogeado) {
//                         // Registrar usuario (si no está logeado)
//                         System.out.print("Ingrese nombre de usuario: ");
//                         username = scanner.nextLine();
//                         System.out.print("Ingrese contraseña: ");
//                         String password = scanner.nextLine();
//                         try {
//                             controladorDomain.registrarUsuario(username, password);
//                             System.out.println("Usuario registrado con éxito.");
//                         } catch (Exception e) {
//                             System.out.println("Error: " + e.getMessage());
//                         }
//                     } else {
//                         // Gestión de configuración (si ya está logeado)
//                         System.out.println("Seleccione una opción de configuración:");
//                         System.out.println("1. Modificar configuración");
//                         System.out.println("2. Agregar configuración");
//                         System.out.println("3. Eliminar configuración");
//                         System.out.println("4. Ver configuraciones");
//                         System.out.println("5. Volver");
//                         System.out.print("Opción: ");
//                         int subopcion = scanner.nextInt();
//                         scanner.nextLine(); // Limpiar el buffer

//                         switch (subopcion) {
//                             case 1:
//                                 // Modificar configuración
//                                 System.out.print("Ingrese clave de configuración: ");
//                                 String clave = scanner.nextLine();
//                                 System.out.print("Ingrese nuevo valor: ");
//                                 String nuevoValor = scanner.nextLine();
//                                 try {
//                                     controladorDomain.modificarConfiguracion(clave, nuevoValor);
//                                     System.out.println("Configuración modificada con éxito.");
//                                 } catch (Exception e) {
//                                     System.out.println("Error: " + e.getMessage());
//                                 }
//                                 break;

//                             case 2:
//                                 // Agregar configuración
//                                 System.out.print("Ingrese clave de configuración: ");
//                                 clave = scanner.nextLine();
//                                 System.out.print("Ingrese valor: ");
//                                 String valor = scanner.nextLine();
//                                 try {
//                                     controladorDomain.agregarConfiguracion(clave, valor);
//                                     System.out.println("Configuración agregada con éxito.");
//                                 } catch (Exception e) {
//                                     System.out.println("Error: " + e.getMessage());
//                                 }
//                                 break;

//                             case 3:
//                                 // Eliminar configuración
//                                 System.out.print("Ingrese clave de configuración: ");
//                                 clave = scanner.nextLine();
//                                 try {
//                                     controladorDomain.eliminarConfiguracion(clave);
//                                     System.out.println("Configuración eliminada con éxito.");
//                                 } catch (Exception e) {
//                                     System.out.println("Error: " + e.getMessage());
//                                 }
//                                 break;

//                             case 4:
//                                 // Ver configuraciones
//                                 controladorDomain.listarConfiguraciones();
//                                 break;

//                             case 5:
//                                 // Volver al menú principal
//                                 break;

//                             default:
//                                 System.out.println("Opción no válida.");
//                         }
//                     }
//                     break;

//                 case 3:
//                     // Gestión de diccionario (solo si está logeado)
//                     if (estaLogeado) {
//                         System.out.println("Seleccione una opción de gestión de diccionario:");
//                         System.out.println("1. Crear diccionario");
//                         System.out.println("2. Agregar palabra al diccionario");
//                         System.out.println("3. Eliminar palabra del diccionario");
//                         System.out.println("4. Importar diccionario");
//                         System.out.println("5. Volver");
//                         System.out.print("Opción: ");
//                         int subopcion = scanner.nextInt();
//                         scanner.nextLine(); // Limpiar el buffer

//                         switch (subopcion) {
//                             case 1:
//                                 // Crear diccionario
//                                 System.out.print("Ingrese nombre del diccionario: ");
//                                 String diccionario = scanner.nextLine();
//                                 List<String> palabras = new ArrayList<>();
//                                 System.out.println("Ingrese palabras (escriba 'fin' para terminar): ");
//                                 while (true) {
//                                     String palabra = scanner.nextLine();
//                                     if (palabra.equals("fin")) break;
//                                     palabras.add(palabra);
//                                 }
//                                 try {
//                                     controladorDomain.crearDiccionario(diccionario, palabras);
//                                     System.out.println("Diccionario creado con éxito.");
//                                 } catch (Exception e) {
//                                     System.out.println("Error: " + e.getMessage());
//                                 }
//                                 break;

//                             case 2:
//                                 // Agregar palabra al diccionario
//                                 System.out.print("Ingrese nombre del diccionario: ");
//                                 diccionario = scanner.nextLine();
//                                 System.out.print("Ingrese palabra: ");
//                                 String palabra = scanner.nextLine();
//                                 try {
//                                     controladorDomain.agregarPalabraADiccionario(diccionario, palabra);
//                                     System.out.println("Palabra agregada al diccionario con éxito.");
//                                 } catch (Exception e) {
//                                     System.out.println("Error: " + e.getMessage());
//                                 }
//                                 break;

//                             case 3:
//                                 // Eliminar palabra del diccionario
//                                 System.out.print("Ingrese nombre del diccionario: ");
//                                 diccionario = scanner.nextLine();
//                                 System.out.print("Ingrese palabra: ");
//                                 palabra = scanner.nextLine();
//                                 try {
//                                     controladorDomain.eliminarPalabraDeDiccionario(diccionario, palabra);
//                                     System.out.println("Palabra eliminada del diccionario con éxito.");
//                                 } catch (Exception e) {
//                                     System.out.println("Error: " + e.getMessage());
//                                 }
//                                 break;

//                             case 4:
//                                 // Importar diccionario
//                                 System.out.print("Ingrese nombre del diccionario: ");
//                                 diccionario = scanner.nextLine();
//                                 System.out.print("Ingrese ruta del archivo: ");
//                                 String rutaArchivo = scanner.nextLine();
//                                 File archivo = new File(rutaArchivo);
//                                 try {
//                                     controladorDomain.importarDiccionario(diccionario, archivo);
//                                     System.out.println("Diccionario importado con éxito.");
//                                 } catch (Exception e) {
//                                     System.out.println("Error: " + e.getMessage());
//                                 }
//                                 break;

//                             case 5:
//                                 // Volver al menú principal
//                                 break;

//                             default:
//                                 System.out.println("Opción no válida.");
//                         }
//                     } else {
//                         System.out.println("Opción no válida.");
//                     }
//                     break;

//                 case 4:
//                     // Ver ranking (solo si está logeado)
//                     if (estaLogeado) {
//                         controladorDomain.verRanking();
//                     } else {
//                         System.out.println("Opción no válida.");
//                     }
//                     break;

//                 case 5:
//                     // Agregar puntuación (solo si está logeado)
//                     if (estaLogeado) {
//                         System.out.print("Ingrese nombre de usuario: ");
//                         username = scanner.nextLine();
//                         System.out.print("Ingrese puntuación: ");
//                         int puntuacion = scanner.nextInt();
//                         try {
//                             controladorDomain.agregarPuntuacion(username, puntuacion);
//                             System.out.println("Puntuación agregada con éxito.");
//                         } catch (Exception e) {
//                             System.out.println("Error: " + e.getMessage());
//                         }
//                     } else {
//                         System.out.println("Opción no válida.");
//                     }
//                     break;

//                 case 6:
//                     // Gestión de partidas (solo si está logeado)
//                     if (estaLogeado) {
//                         System.out.println("Seleccione una opción de gestión de partidas:");
//                         System.out.println("1. Crear partida");
//                         System.out.println("2. Iniciar partida");
//                         System.out.println("3. Jugar");
//                         System.out.println("4. Guardar partida");
//                         System.out.println("5. Cargar partida");
//                         System.out.println("6. Volver");
//                         System.out.print("Opción: ");
//                         int subopcion = scanner.nextInt();
//                         scanner.nextLine(); // Limpiar el buffer

//                         switch (subopcion) {
//                             case 1:
//                                 // Crear partida
//                                 System.out.print("Ingrese nombre de la partida: ");
//                                 String nombrePartida = scanner.nextLine();
//                                 System.out.print("Ingrese nombre del diccionario: ");
//                                 String diccionarioPartida = scanner.nextLine();
//                                 try {
//                                     controladorDomain.crearPartida(nombrePartida, diccionarioPartida);
//                                     System.out.println("Partida creada con éxito.");
//                                 } catch (Exception e) {
//                                     System.out.println("Error: " + e.getMessage());
//                                 }
//                                 break;

//                             case 2:
//                                 // Iniciar partida
//                                 System.out.print("Ingrese nombre de la partida: ");
//                                 nombrePartida = scanner.nextLine();
//                                 try {
//                                     controladorDomain.iniciarPartida(nombrePartida);
//                                     System.out.println("Partida iniciada con éxito.");
//                                 } catch (Exception e) {
//                                     System.out.println("Error: " + e.getMessage());
//                                 }
//                                 break;

//                             // case 3:
//                             //     // Jugar
//                             //     System.out.print("Ingrese nombre de la partida: ");
//                             //     nombrePartida = scanner.nextLine();
//                             //     System.out.print("Ingrese palabra: ");
//                             //     palabra = scanner.nextLine();
//                             //     System.out.print("Ingrese fila: ");
//                             //     int fila = scanner.nextInt();
//                             //     System.out.print("Ingrese columna: ");
//                             //     int columna = scanner.nextInt();
//                             //     scanner.nextLine(); // Limpiar el buffer
//                             //     System.out.print("Ingrese dirección (horizontal/vertical): ");
//                             //     String direccion = scanner.nextLine();
//                             //     try {
//                             //         controladorDomain.jugar(nombrePartida, palabra, fila, columna, direccion);
//                             //         System.out.println("Jugada realizada con éxito.");
//                             //     } catch (Exception e) {
//                             //         System.out.println("Error: " + e.getMessage());
//                             //     }
//                             //     break;  
//                             case 4:
//                                 // Guardar partida
//                                 System.out.print("Ingrese nombre de la partida: ");
//                                 nombrePartida = scanner.nextLine();
//                                 try {
//                                     controladorDomain.guardarPartida(nombrePartida);
//                                     System.out.println("Partida guardada con éxito.");
//                                 } catch (Exception e) {
//                                     System.out.println("Error: " + e.getMessage());
//                                 }
//                                 break;
//                             case 5:
//                                 // Cargar partida
//                                 System.out.print("Ingrese nombre de la partida: ");
//                                 nombrePartida = scanner.nextLine();
//                                 System.out.print("Ingrese ID de la partida: ");
//                                 int idPartida = scanner.nextInt();
//                                 try {
//                                     controladorDomain.cargarPartida(nombrePartida, idPartida);
//                                     System.out.println("Partida cargada con éxito.");
//                                 } catch (Exception e) {
//                                     System.out.println("Error: " + e.getMessage());
//                                 }
//                                 break;
//                             case 6:
//                                 // Volver al menú principal
//                                 break;
//                             default:
//                                 System.out.println("Opción no válida.");
//                         }
//                     } else {
//                         System.out.println("Opción no válida.");
//                     }
//                     break;
                        
//                 case 0:
//                     // Salir
//                     System.out.println("¡Hasta luego!");
//                     scanner.close();
//                     return; // Sale del programa

//                 default:
//                     System.out.println("Opción no válida.");
//             }
//         }
//     }
// }


// int opcion;
//         Scanner scanner = new Scanner(System.in);
//         System.out.println("Selecciona una opción:");
//         System.out.println("1. Dos jugadores");
//         System.out.println("2. Jugador vs IA");
//         System.out.println("Opción: ");
//         opcion = scanner.nextInt();
//         scanner.nextLine(); // Consumir el salto de línea
//         switch (opcion) {
//             case 1:
//                 System.out.println("Introduce el id del jugador 1:");
//                 String idJugador1 = scanner.nextLine();
//                 if (!ControladorJugador.existeJugador(idJugador1)) {
//                     throw new ExceptionUserNotExist();
//                 }
//                 while (!ControladorJugador.isLoggedIn(idJugador1)) {
//                     System.out.println("El jugador 1 no ha iniciado sesión.");
//                     System.out.println("Por favor, inicie sesión.");
//                     System.out.println("Introduce la contraseña:");
//                     String password = scanner.nextLine();
//                     if (ControladorJugador.autenticar(idJugador1, password)) {
//                         System.out.println("Jugador 1 autenticado.");
//                     } else {
//                        System.out.println("Contraseña incorrecta.");
//                     }
//                 }
//                 System.out.println("Introduce el id del jugador 2:");
//                 String idJugador2 = scanner.nextLine();
//                  if (!ControladorJugador.existeJugador(idJugador2)) {
//                     throw new ExceptionUserNotExist();
//                 }
//                 while (!ControladorJugador.isLoggedIn(idJugador2)) {
//                     System.out.println("El jugador 2 no ha iniciado sesión.");
//                     System.out.println("Por favor, inicie sesión.");
//                     System.out.println("Introduce la contraseña:");
//                     String password = scanner.nextLine();
//                     if (ControladorJugador.autenticar(idJugador2, password)) {
//                         System.out.println("Jugador 1 autenticado.");
//                     } else {
//                         System.out.println("Contraseña incorrecta.");
//                     }
//                 }
                
//                 break;
//             case 2:

//         }