package scrabble.domain.persistences.implementaciones;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scrabble.domain.models.Diccionario;
import scrabble.domain.persistences.interfaces.RepositorioDiccionario;

/**
 * Implementación del repositorio de diccionarios utilizando serialización Java y archivos.
 */
public class RepositorioDiccionarioImpl implements RepositorioDiccionario {
    
    private static final String DICCIONARIOS_INDEX_FILE = "src/main/resources/persistencias/diccionarios_index.dat";
    
    /**
     * Constructor que asegura que el directorio de persistencia existe.
     */
    public RepositorioDiccionarioImpl() {
        // Asegurar que el directorio existe
        File directory = new File("src/main/resources/persistencias");
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
    
    @Override
    public boolean guardar(String nombre, Diccionario diccionario, String path) {
        // Esta implementación asume que el diccionario ya ha sido guardado como archivos
        // en la ruta especificada y solo necesitamos guardar la referencia en el índice
        Map<String, String> diccionariosPaths = cargarIndice();
        diccionariosPaths.put(nombre, path);
        return guardarIndice(diccionariosPaths);
    }
    
    @Override
    public boolean guardarIndice(Map<String, String> diccionariosPaths) {
        try {
            // Asegurar que el directorio existe
            File indexDir = new File(DICCIONARIOS_INDEX_FILE).getParentFile();
            if (indexDir != null && !indexDir.exists()) {
                indexDir.mkdirs();
            }
            
            try (FileOutputStream fos = new FileOutputStream(DICCIONARIOS_INDEX_FILE);
                 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(diccionariosPaths);
                return true;
            }
        } catch (IOException e) {
            System.err.println("Error al guardar el índice de diccionarios: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, String> cargarIndice() {
        File indexFile = new File(DICCIONARIOS_INDEX_FILE);
        if (!indexFile.exists()) {
            return new HashMap<>(); // Retornar un mapa vacío si no existe el índice
        }
        
        try (FileInputStream fis = new FileInputStream(DICCIONARIOS_INDEX_FILE);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (Map<String, String>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar el índice de diccionarios: " + e.getMessage());
            return new HashMap<>(); // Retornar un mapa vacío en caso de error
        }
    }
    
    @Override
    public Diccionario cargar(String nombre) throws IOException {
        Map<String, String> diccionariosPaths = cargarIndice();
        String path = diccionariosPaths.get(nombre);
        
        if (path == null) {
            return null; // No existe ese diccionario en el índice
        }
        
        // Cargar el diccionario desde los archivos
        Diccionario diccionario = new Diccionario();
        
        // Leer alpha.txt
        Path alphaPath = Paths.get(path, "alpha.txt");
        if (!Files.exists(alphaPath)) {
            throw new IOException("No se encuentra el archivo alpha.txt para el diccionario '" + nombre + "'.");
        }
        List<String> lineasAlpha = Files.readAllLines(alphaPath);
        diccionario.setAlphabet(lineasAlpha);
        
        // Leer words.txt
        Path wordsPath = Paths.get(path, "words.txt");
        if (!Files.exists(wordsPath)) {
            throw new IOException("No se encuentra el archivo words.txt para el diccionario '" + nombre + "'.");
        }
        List<String> palabras = Files.readAllLines(wordsPath);
        diccionario.setDawg(palabras);
        
        return diccionario;
    }
    
    @Override
    public boolean eliminar(String nombre) {
        Map<String, String> diccionariosPaths = cargarIndice();
        String path = diccionariosPaths.remove(nombre);
        
        if (path == null) {
            return false; // No existe ese diccionario en el índice
        }
        
        // Guardar el índice actualizado
        boolean indexGuardado = guardarIndice(diccionariosPaths);
        
        // Eliminar los archivos físicos
        try {
            Path dirPath = Paths.get(path);
            if (Files.exists(dirPath)) {
                Files.walk(dirPath)
                    .sorted(java.util.Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
                return indexGuardado;
            }
            return indexGuardado;
        } catch (IOException e) {
            System.err.println("Error al eliminar los archivos del diccionario: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean existe(String nombre) {
        Map<String, String> diccionariosPaths = cargarIndice();
        return diccionariosPaths.containsKey(nombre);
    }
    
    @Override
    public List<String> listarDiccionarios() {
        Map<String, String> diccionariosPaths = cargarIndice();
        return new ArrayList<>(diccionariosPaths.keySet());
    }
    
    @Override
    public boolean verificarDiccionarioValido(String nombre) {
        Map<String, String> diccionariosPaths = cargarIndice();
        String path = diccionariosPaths.get(nombre);
        
        if (path == null) {
            return false; // No existe ese diccionario en el índice
        }
        
        Path dirPath = Paths.get(path);
        Path alphaPath = dirPath.resolve("alpha.txt");
        Path wordsPath = dirPath.resolve("words.txt");
        
        return Files.exists(dirPath) && Files.isDirectory(dirPath) && 
               Files.exists(alphaPath) && Files.exists(wordsPath);
    }
}