package scrabble.presentation.viewControllers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import scrabble.MainApplication;
import scrabble.presentation.PresentationController;
import scrabble.presentation.componentes.DiccionarioVisual;
import scrabble.presentation.popups.AñadirLetraPopup;
import scrabble.presentation.popups.AñadirPalabraPopup;
import scrabble.presentation.popups.ModificarPalabraPopup;
import scrabble.presentation.views.CrearDiccionarioView;
import scrabble.presentation.views.GestionDiccionariosView;
import scrabble.presentation.views.ModificarDiccionarioView;

/**
 * Controlador de vista para la gestión de diccionarios en la aplicación Scrabble.
 * 
 * Esta clase gestiona la interfaz gráfica relacionada con la administración de diccionarios,
 * incluyendo su creación, edición, importación, y eliminación. Se encarga de 
 * coordinar las interacciones del usuario con el modelo de diccionario, proporcionando 
 * una experiencia visual y funcional coherente.
 * 
 * Funcionalidades principales:
 * - Visualización de la lista completa de diccionarios disponibles
 * - Creación de nuevos diccionarios con validación de datos
 * - Importación de diccionarios desde archivos externos
 * - Interacción con el servicio de persistencia del repositorio
 * 
 * Este controlador trabaja estrechamente con las vistas JavaFX y
 * utiliza patrones de diseño como MVC para separar claramente la lógica de presentación
 * y la lógica de negocio.
 * 
 * @version 1.2
 * @since 1.0
 */


public class ControladorDiccionarioView {

    private static ControladorDiccionarioView instance;

    private PresentationController presentationController;
    private Stage stage;
    private Parent vistaAnterior;
    private BorderPane layout;
    private ObservableList<DiccionarioVisual> diccionarios;

   /**
     * Constructor privado para el patrón singleton.
     * 
     * @pre No debe llamarse directamente fuera de la clase.
     * @post Se crea una instancia de ControladorDiccionarioView con el controlador de presentación.
     */

    private ControladorDiccionarioView() {
        this.presentationController = PresentationController.getInstance();
    }

    /**
     * Retorna la instancia única del controlador (singleton).
     * 
     * @return instancia del ControladorDiccionarioView.
     * @pre Debe haberse llamado al menos una vez para obtener la instancia.
     * @post Se garantiza que solo existe una instancia.
     */

    public static ControladorDiccionarioView getInstance() {
        if (instance == null) {
            instance = new ControladorDiccionarioView();
        }
        return instance;
    }

    /**
     * Inicializa la interfaz de gestión de diccionarios.
     * 
     * @param stage ventana principal de la aplicación.
     * @pre El stage debe estar creado.
     * @post Se muestra la vista de gestión de diccionarios.
     */

    public void inicializar(Stage stage) {
        // Mostrar la vista inicial
        this.stage = stage;
        layout = new BorderPane();
        GestionDiccionariosView vistaGestion = new GestionDiccionariosView(this);
        layout.setCenter(vistaGestion.getView());
        Scene escena = new Scene(layout, stage.getWidth(), stage.getHeight());
        stage.setScene(escena);
        stage.setTitle("Gestión de Diccionarios");
        stage.setMaximized(true);
        stage.show();
    }

     /**
     * Muestra la vista de gestión de diccionarios.
     * 
     * @pre El layout debe estar inicializado.
     * @post Se cambia el contenido central del layout a la vista de gestión.
     */

    public void mostrarVistaGestion() {
        layout.setCenter(new GestionDiccionariosView(this).getView());
    }

    /**
     * Muestra la vista para crear un nuevo diccionario.
     * 
     * @pre El layout debe estar inicializado.
     * @post Se reemplaza el contenido central por la vista de creación.
     */
    public void mostrarVistaCrear() {
        layout.setCenter(new CrearDiccionarioView(this).getView());
    }

    /**
     * Muestra la vista para modificar un diccionario existente.
     * 
     * @param diccionario diccionario a modificar.
     * @post Se reemplaza el contenido central con la vista de edición del diccionario dado.
     */

    public void mostrarVistaModificar(DiccionarioVisual diccionario) {
        layout.setCenter(new ModificarDiccionarioView(this, diccionario).getView());
    }

    /**
     * Muestra un popup para importar un diccionario.
     */

    // public void mostrarPopupImportar() {
    //     ImportarDiccionarioPopup popup = new ImportarDiccionarioPopup();
    //     popup.mostrar(stage);
    // }

     /**
     * Muestra un popup para añadir una nueva letra al alfabeto.
     * 
     * @param alfabeto alfabeto actual al que se añadirá una nueva letra.
     * @pre La lista debe ser válida y no nula.
     * @post Se abre una ventana para añadir letras.
     */

    public void mostrarPopupAñadirLetra(ObservableList<ObservableList<String>> alfabeto) {
        AñadirLetraPopup popup = new AñadirLetraPopup(alfabeto);
        popup.mostrar(stage);
    }

    /**
     * Muestra un popup para añadir una palabra al diccionario.
     * 
     * @param palabras palabras actuales del diccionario.
     * @param alfabeto conjunto de letras válidas para construir palabras.
     * @pre La lista de palabras y el alfabeto no deben ser nulos.
     * @post Se muestra un popup que permite añadir una palabra al diccionario.
     */
    public void mostrarPopupAñadirPalabra(ObservableList<String> palabras, Set<String> alfabeto) {
        AñadirPalabraPopup popup = new AñadirPalabraPopup(palabras, alfabeto);
        popup.mostrar(stage);
    }

    /**
     * Muestra un popup para modificar una palabra existente.
     * 
     * @param palabras lista actual de palabras.
     * @param alfabeto conjunto de letras válidas.
     * @param palabraOriginal palabra que se desea modificar.
     * @pre La lista de palabras y el alfabeto no deben ser nulos. La palabra original debe existir en la lista.
     * @post Se muestra un popup que permite modificar la palabra seleccionada en el diccionario.
     */
    public void mostrarPopupModificarPalabra(ObservableList<String> palabras, Set<String> alfabeto, String palabraOriginal) {
        ModificarPalabraPopup popup = new ModificarPalabraPopup(palabras, alfabeto, palabraOriginal);
        popup.mostrar(stage);
    }

    /**
     * Muestra una ventana emergente (popup) con la información detallada de un diccionario visual.
     * @pre El objeto diccionario no es null y contiene información válida sobre el diccionario a mostrar.
     * @post Se muestra una ventana modal con el nombre, alfabeto y lista de palabras del diccionario proporcionado.
     *
     * @param diccionario El objeto DiccionarioVisual cuyo contenido se mostrará en el popup.
     */
    public void mostrarPopupDiccionario(DiccionarioVisual diccionario) {
        Stage popupStage = new Stage();
        popupStage.setTitle("Diccionario: " + diccionario.getNombre());

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f9f9f9;");

        // Etiqueta de cabecera
        Label titulo = new Label("Nombre del Diccionario: " + diccionario.getNombre());
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Alfabeto
        Label lblAlfabeto = new Label("Alfabeto:");
        lblAlfabeto.setStyle("-fx-font-weight: bold;");
        TextArea areaCabecera = new TextArea(String.join("\n", diccionario.getAlfabeto()));
        areaCabecera.setEditable(false);
        areaCabecera.setWrapText(true);
        areaCabecera.setPrefRowCount(8);
        areaCabecera.setStyle("-fx-control-inner-background: #ffffff; -fx-border-color: #cccccc;");

        // Palabras
        Label lblPalabras = new Label("Palabras:");
        lblPalabras.setStyle("-fx-font-weight: bold;");
        ListView<String> listaPalabras = new ListView<>(FXCollections.observableArrayList(diccionario.getPalabras()));
        listaPalabras.setPrefHeight(300);
        listaPalabras.setStyle("-fx-border-color: #cccccc;");

        root.getChildren().addAll(titulo, lblAlfabeto, areaCabecera, lblPalabras, listaPalabras);

        Scene scene = new Scene(root, 600, 550);
        popupStage.setScene(scene);
        popupStage.initOwner(stage);
        popupStage.showAndWait();
    }


    /**
     * Establece una vista anterior para volver a ella más tarde.
     * 
     * @param vista vista previa que se desea almacenar.
     * @pre La vista pasada no debe ser nula.
     * @post La variable vistaAnterior almacena la vista proporcionada.
     */
    public void setVistaAnterior(Parent vista) {
        this.vistaAnterior = vista;
    }

    /**
     * Vuelve a la vista anterior establecida previamente.
     * 
     * @post Se restaura la vista previa si existía.
     */

    public void volverAVistaAnterior() {
        if (vistaAnterior != null) {
            layout.setCenter(vistaAnterior);
        }
    }

    /**
     * Vuelve a la vista anterior establecida previamente.
     * 
     * @post Se restaura la vista previa si existía.
     */
    public void volverAMenuPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/views/main-view.fxml"));
            Parent mainView = loader.load();
            
            Scene currentScene = stage.getScene();
            Scene newScene = new Scene(mainView, currentScene.getWidth(), currentScene.getHeight());
            
            // Mantener CSS
            if (!currentScene.getStylesheets().isEmpty()) {
                newScene.getStylesheets().addAll(currentScene.getStylesheets());
            }
            
            stage.setScene(newScene);
            stage.setTitle("SCRABBLE");
            
            // Mantener maximizado si estaba maximizado
            if (stage.isMaximized()) {
                stage.setMaximized(false);
                stage.setMaximized(true);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error", "Error al volver al menú principal.");
        }
    }

    /**
     * Agrega un nuevo diccionario al sistema.
     * 
     * @param nombre nombre del nuevo diccionario.
     * @param alfabeto lista de letras válidas.
     * @param palabras palabras del diccionario.
     * @pre El nombre no debe existir previamente y los datos deben ser válidos.
     * @post Se crea y guarda el diccionario, y se actualiza la interfaz.
     */

    public void agregarDiccionario(String nombre, List<String> alfabeto, List<String> palabras) {
        // try {
            
        //     // Crear directorio en recursos
        //     String RESOURCE_BASE_PATH = "src/main/resources/diccionarios/";
        //     Path dictPath = Paths.get(RESOURCE_BASE_PATH, nombre);
        //     Files.createDirectories(dictPath);

        //     // Crear y escribir alpha.txt
        //     Path alphaFile = dictPath.resolve("alpha.txt");
        //     Files.write(alphaFile, alfabeto, StandardCharsets.UTF_8);

        //     // Crear y escribir words.txt ordenadas
        //     List<String> palabrasOrdenadas = palabras.stream()
        //             .map(String::toUpperCase)
        //             .distinct()
        //             .sorted()
        //             .toList();
        //     Path wordsFile = dictPath.resolve("words.txt");
        //     Files.write(wordsFile, palabrasOrdenadas, StandardCharsets.UTF_8);

        //     // Crear el diccionario en capa dominio
        //     presentationController.crearDiccionario(nombre, dictPath.toString());

        //     // Agregar a la vista
        //     DiccionarioVisual nuevoDiccionario = new DiccionarioVisual(nombre, alfabeto, palabrasOrdenadas);
        //     diccionarios.add(nuevoDiccionario);
        //     mostrarAlerta("Success", "Crear diccionario", "Diccionario '" + nombre + "' creado y cargado correctamente.");

        // } catch (Exception e) {
        //     mostrarAlerta("Error", "Crear diccionario", "No se pudo crear el diccionario:\n" + e.getMessage());
        //     // Intentar limpiar recursos
        //     try {
        //         Path dictPath = Paths.get("src/main/resources/diccionarios/", nombre);
        //         if (Files.exists(dictPath)) {
        //             Files.walk(dictPath)
        //                 .sorted(Comparator.reverseOrder())
        //                 .map(Path::toFile)
        //                 .forEach(File::delete);
        //         }
        //     } catch (IOException ignored) {
        //     }
        // }

        List<String> palabrasOrdenadas = palabras.stream()
                .map(String::toUpperCase)
                .distinct()
                .toList();

        // Nota xuanyi: el trim es necesario porque si no petaria si intentas crear un directorio tipo: /nombre /; con un espacio al final del nombre.
        
        try {
            presentationController.crearDiccionario(nombre.trim(), alfabeto, palabrasOrdenadas);
            DiccionarioVisual nuevoDiccionario = new DiccionarioVisual(nombre.trim(), alfabeto, palabrasOrdenadas);
            diccionarios.add(nuevoDiccionario);
            mostrarAlerta("Success", "Crear diccionario", "Diccionario '" + nombre + "' creado y cargado correctamente."); 
        } catch (Exception e) {
            mostrarAlerta("Error", "Crear diccionario", "Error al crear el diccionario " + nombre);
        }
                
    }

    /**
     * Verifica que un diccionario sea válido.
     * 
     * @param nombre nombre del diccionario.
     * @param palabrasList lista de palabras.
     * @param letras conjunto de letras válidas.
     * @return true si el diccionario es válido, false en caso contrario.
     * @post Si retorna true, el nombre es único, no está vacío, el alfabeto y la lista de palabras no están vacíos,
     *       y todas las palabras pueden formarse con el alfabeto dado.
     */
    public boolean checkDiccioanario(String nombre, List<String> palabrasList, Set<String> letras) {
        StringBuilder errores = new StringBuilder();

        if (nombre.isBlank()) {
            errores.append("El nombre no puede estar vacío.\n");
        }
        if (letras.isEmpty()) {
            errores.append("El alfabeto no puede estar vacío.\n");
        }
        if (palabrasList.isEmpty()) {
            errores.append("La lista de palabras no puede estar vacía.\n");
        }

        if (errores.length() > 0) {
            mostrarAlerta("Error", "Crear diccionario", errores.toString());
            return false;
        }   
        if (diccionarios.stream().anyMatch(diccionario -> diccionario.getNombre().equals(nombre))) {
            // Manejar error: diccionario ya existe
            mostrarAlerta("Error", "Crear diccionario", "El nombre de diccionario ya existe, por favor elige otro nombre.");
            return false;
        }
        for (String palabra : palabrasList) {
            if (!puedeConstruirseDesdeAlfabeto(palabra, letras)) {
                mostrarAlerta("Error", "Crear diccionario", "La palabra \"" + palabra + "\" contiene letras que no están en el alfabeto.");
                return false;
            }
        }
        return true;
    }

    /**
     * Verifica si una palabra se puede construir con las letras del alfabeto.
     * 
     * @param palabra palabra a verificar.
     * @param alfabeto conjunto de letras válidas.
     * @return true si puede formarse, false en caso contrario.
     * @pre La palabra y el conjunto de letras deben ser válidos y no nulos.
     * @post Se determina si la palabra puede formarse completamente con las letras del alfabeto.
     */

    private boolean puedeConstruirseDesdeAlfabeto(String palabra, Set<String> alfabeto) {
        return puedeFormarseDesde(palabra, alfabeto, 0);
    }

    /**
     * Método recursivo que verifica si una palabra puede formarse desde un índice usando letras del alfabeto.
     * 
     * @param palabra palabra a formar.
     * @param alfabeto conjunto de letras válidas.
     * @param idx índice actual desde el cual verificar.
     * @return true si puede formarse, false en caso contrario.
     * @pre palabra no nula, alfabeto válido, índice entre 0 y palabra.length().
     * @post Se comprueba recursivamente si es posible formar la palabra con letras del alfabeto desde el índice dado.
     */

    private boolean puedeFormarseDesde(String palabra, Set<String> alfabeto, int idx) {
        if (idx == palabra.length()) return true;

        for (String letra : alfabeto) {
            if (palabra.startsWith(letra, idx)) {
                if (puedeFormarseDesde(palabra, alfabeto, idx + letra.length())) return true;
            }
        }
        return false;
    }

    /**
     * Verifica si el diccionario es válido.
     *
     * @param palabrasList Lista de palabras.
     * @param letras       Conjunto de letras válidas.
     * @return true si el diccionario es válido, false en caso contrario.Ç
     * @post Si retorna true, el alfabeto y la lista de palabras no están vacíos,
     *       y todas las palabras pueden formarse con el alfabeto dado.
     */

    public boolean  checkDiccioanario(List<String> palabrasList, Set<String> letras) {
        StringBuilder errores = new StringBuilder();
        if (letras.isEmpty()) {
            errores.append("El alfabeto no puede estar vacío.\n");
        }
        if (palabrasList.isEmpty()) {
            errores.append("La lista de palabras no puede estar vacía.\n");
        }

        if (errores.length() > 0) {
            mostrarAlerta("Error", "Crear diccionario", errores.toString());
            return false;
        }   
        for (String palabra : palabrasList) {
            if (!puedeConstruirseDesdeAlfabeto(palabra, letras)) {
                mostrarAlerta("Error", "Crear diccionario", "La palabra \"" + palabra + "\" contiene letras que no están en el alfabeto.");
                return false;
            }
        }
        return true;
    }

    /**
     * Verifica si el diccionario es válido.
     *
     * @param ruta Ruta del diccionario.
     * @return true si el diccionario es válido, false en caso contrario.
     */

    // public boolean esDiccionarioValido(String ruta) {
    //     File alphaFile = new File(ruta, "alpha.txt");
    //     File wordsFile = new File(ruta, "words.txt");

    //     if (!alphaFile.exists() || !wordsFile.exists()) {
    //         return false;
    //     }

    //     Set<Character> letrasValidas = new HashSet<>();

    //     // Validar alpha.txt
    //     try (BufferedReader br = new BufferedReader(new FileReader(alphaFile))) {
    //         String linea;
    //         while ((linea = br.readLine()) != null) {
    //             String[] partes = linea.trim().split("\\s+");
    //             if (partes.length != 3) return false;

    //             String letraStr = partes[0];
    //             int puntuacion = Integer.parseInt(partes[1]);
    //             int frecuencia = Integer.parseInt(partes[2]);

    //             if (letraStr.length() != 1 || !Character.isLetter(letraStr.charAt(0))) return false;
    //             if (puntuacion <= 0 || frecuencia <= 0) return false;

    //             letrasValidas.add(letraStr.toUpperCase().charAt(0)); // Guardar letras válidas en mayúsculas
    //         }

    //         if (letrasValidas.isEmpty()) return false;
    //     } catch (Exception e) {
    //         return false;
    //     }

    //     // Validar words.txt
    //     try (BufferedReader br = new BufferedReader(new FileReader(wordsFile))) {
    //         String linea;
    //         while ((linea = br.readLine()) != null) {
    //             String palabra = linea.trim().toUpperCase();

    //             // La palabra debe contener solo letras válidas
    //             for (char c : palabra.toCharArray()) {
    //                 if (!letrasValidas.contains(c)) {
    //                     return false;
    //                 }
    //             }
    //         }
    //     } catch (Exception e) {
    //         return false;
    //     }

    //     return true;
    // }


    /**
     * Modifica un diccionario existente.
     *
     * @param nombre   Nombre del diccionario a modificar.
     * @param alfabeto Nuevo alfabeto.
     * @param nuevasPalabras Nuevas palabras.
     * @pre El nombre debe corresponder a un diccionario existente y los datos deben ser válidos.
     * @post Se actualiza el diccionario correspondiente en la vista y el modelo con los nuevos datos proporcionados.
     */

    public void modificarDiccionario(String nombre, List<String> alfabeto, List<String> nuevasPalabras) {
        DiccionarioVisual diccionario = diccionarios.stream()
                .filter(d -> d.getNombre().equals(nombre))
                .findFirst()
                .orElse(null);

        if (diccionario == null) {
            mostrarAlerta("Error", "Modificar diccionario", "El diccionario no existe.");
            return;
        }

        try {
            // 1. Sobrescribir el alfabeto directamente en el archivo
            // Path alphaPath = Paths.get("src/main/resources/diccionarios", nombre, "alpha.txt");
            // Files.write(alphaPath, alfabeto, StandardCharsets.UTF_8);

            // 2. Obtener conjunto original y nuevo
            Set<String> palabrasOriginales = new HashSet<>(diccionario.getPalabras());
            Set<String> palabrasNuevas = new HashSet<>(nuevasPalabras);

            // 3. Añadir nuevas palabras
            for (String nueva : palabrasNuevas) {
                if (!palabrasOriginales.contains(nueva)) {
                    presentationController.modificarPalabraDiccionario(nombre, nueva, true);
                }
            }

            // 4. Eliminar palabras que ya no están
            for (String original : palabrasOriginales) {
                if (!palabrasNuevas.contains(original)) {
                    presentationController.modificarPalabraDiccionario(nombre, original, false);
                }
            }

            // 5. Actualizar vista
            diccionario.getAlfabeto().clear();
            diccionario.getAlfabeto().addAll(alfabeto);
            diccionario.getPalabras().clear();
            diccionario.getPalabras().addAll(nuevasPalabras);

            mostrarAlerta("Success", "Modificar diccionario", "Diccionario modificado con éxito.");
        } catch (Exception e) {
            mostrarAlerta("Error", "Modificar diccionario", "No se pudo modificar el diccionario:\n" + e.getMessage());
        }
    }
    /**
     * Elimina un diccionario existente.
     *
     * @param diccionario Diccionario a eliminar.
     * @return true si el diccionario se puede eliminar, false en caso contrario.
     * @pre El diccionario debe estar en uso por esta vista y no debe estar enlazado a ninguna partida activa.
     * @post Si es posible, el diccionario es eliminado de la vista y del sistema.
     */
    public boolean eliminarDiccionario(DiccionarioVisual diccionario) {
        String nombre = diccionario.getNombre();
        for (int index: presentationController.getPartidasGuardadas()) {
            String dic = presentationController.getDiccionarioPartida(index);
            if (dic.equals(nombre)) {
                mostrarAlerta("Error", "Eliminar diccionario", "No se pudo eliminar el diccionario " + nombre + ". Está usando en una partida.");
                return false;
            }

        }
        try {
            presentationController.eliminarDiccionario(nombre);
            diccionarios.remove(diccionario);
        }
        catch (Exception e) {
            mostrarAlerta("Error", "Eliminar diccionario", "No se pudo eliminar el diccionario:\n" + e.getMessage());
            
        }
        return true;
    }

    /**
     * Abre un selector de carpetas para importar un diccionario externo.
     * 
     * @post Se lee el contenido y se pregunta al usuario por el nombre nuevo del diccionario.
     */

    public void abrirSelectorDeDirectorio() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Seleccionar carpeta del diccionario (debe contener alpha.txt y words.txt)");

        File directorioSeleccionado = directoryChooser.showDialog(stage);
        if (directorioSeleccionado == null) {
            mostrarAlerta("Error", "Importar diccionario", "No se seleccionó ninguna carpeta.");
            return;
        }

        String ruta = directorioSeleccionado.getAbsolutePath();
        Path rutaSeleccionada = directorioSeleccionado.toPath().toAbsolutePath().normalize();
        Path rutaProhibida = Paths.get("src/main/resources/diccionarios").toAbsolutePath().normalize();

        if (rutaSeleccionada.startsWith(rutaProhibida)) {
            mostrarAlerta("Error", "Importar diccionario", "No se puede importar diccionario desde este carpeta.");
            return;
        }

        // Leer contenidos para agregarlos con agregarDiccionario(...)
        Path alphaPath = Paths.get(ruta, "alpha.txt");
        Path wordsPath = Paths.get(ruta, "words.txt");

        List<String> alfabeto;
        List<String> palabras;
        try {
            alfabeto = Files.readAllLines(alphaPath, StandardCharsets.UTF_8);
            palabras = Files.readAllLines(wordsPath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            mostrarAlerta("Error", "Importar diccionario", "No se pudo leer el diccionario:\n" + e.getMessage());
            return;
        }

        // Pedir nombre del diccionario
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setHeaderText("Nombre del diccionario:");
        inputDialog.setContentText("Introduce un nombre único:");

        inputDialog.showAndWait().ifPresent(nombre -> {
            if (nombre.isBlank()) {
                mostrarAlerta("Error", "Importar diccionario", "Debe ingresar un nombre para el diccionario.");
                return;
            }

            // Validar si ya existe
            if (diccionarios.stream().anyMatch(dic -> dic.getNombre().equals(nombre))) {
                mostrarAlerta("Error", "Importar diccionario", "Ya existe un diccionario con ese nombre.");
                return;
            }

            try {
                agregarDiccionario(nombre, alfabeto, palabras);
                // mostrarAlerta("Sucess", "Importar diccionario", "Diccionario importado con éxito.");
                mostrarVistaGestion();
            } catch (Exception e) {
                mostrarAlerta("Error", "Importar diccionario", "No se pudo importar el diccionario:\n" + e.getMessage());
            }
        });
    }

    /**
     * Obtiene la lista de diccionarios disponibles.
     *
     * @return Lista observable de diccionarios.
     * @pre presentationController debe estar inicializado.
     * @post Si la lista de diccionarios no estaba inicializada, se carga desde el controlador de presentación;
     *       en cualquier caso, se retorna la lista observable de diccionarios.
     */
    public ObservableList<DiccionarioVisual> getDiccionarios() {
        if (diccionarios == null) {
            diccionarios = FXCollections.observableArrayList();
            List<String> nombresDiccionarios = presentationController.getDiccionariosDisponibles();
            for (String nombre : nombresDiccionarios) {
                List<String> alfabeto = new ArrayList<>();
                List<String> palabras = new ArrayList<>(); 
                alfabeto = presentationController.getListaAlfabeto(nombre);
                palabras = presentationController.getListaPalabras(nombre);
                DiccionarioVisual diccionario = new DiccionarioVisual(nombre, alfabeto, palabras);
                diccionarios.add(diccionario);
            }
        }
        return diccionarios;
    }

    /**
     * Obtiene el tema actual desde el controlador de presentación.
     *
     * @return una cadena que representa el tema actual.
     * @pre presentationController debe estar inicializado.
     * @post Se retorna el tema actualmente seleccionado en la aplicación.
     */
    public String getTema() {
        return presentationController.getTema();
    }

    /*
     * Muestra un aviso al usuario.
     * @param tipo Tipo de alerta
     * @param titulo Titulo de la alerta
     * @param mensaje Mensaje a mostrar
     * @pre Los parámetros no deben ser nulos.
     * @post Se muestra un mensaje de alerta al usuario a través del controlador de presentación.
     */

    public void mostrarAlerta(String tipo, String titulo, String mensaje) {
        presentationController.mostrarAlerta(tipo, titulo, mensaje);
    }

    /**
     * Muestra un mensaje de error al usuario.
     *
     * @param mensaje Mensaje a mostrar.
     */

    // public void mostrarError(String mensaje) {
    //     // Implementar lógica para mostrar un mensaje de error al usuario
    //     Alert alert = new Alert(Alert.AlertType.WARNING, mensaje, ButtonType.OK);
    //     alert.setTitle("Advertencia");
    //     alert.setHeaderText(null);
    //     alert.showAndWait();
    // }

    /**
     * Muestra un mensaje de éxito al usuario.
     *
     * @param mensaje Mensaje a mostrar.
     */

    // public void mostrarMensaje(String mensaje) {
    //     // Implementar lógica para mostrar un mensaje al usuario
    //     Alert alert = new Alert(Alert.AlertType.INFORMATION, mensaje, ButtonType.OK);
    //     alert.setTitle("Éxito");
    //     alert.setHeaderText(null);
    //     alert.showAndWait();
    // }

    
}
