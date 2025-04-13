package domain.models;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Diccionario {

    private Map<String, Dawg> allDawgs;
    private Map<String, Map<String, Integer>> allAlphabets;
    private Map<String, Map<String, Integer>> allBag;

    public Diccionario() {
        this.allDawgs = new HashMap<>();
        this.allAlphabets = new HashMap<>();
        this.allBag = new HashMap<>();
    }


    public void addDawg(String name, String filePath) {

        Dawg dawg = new Dawg();
        if (filePath == null || filePath.isEmpty()) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Introduce palabras separadas por enter. Escribe 'FIN' para terminar:");
        
            List<String> palabras = new ArrayList<>();
            while (true) {
                String palabra = scanner.nextLine();
                if (palabra.equalsIgnoreCase("FIN")) {
                    break;
                }
                palabras.add(palabra);
            }
            inicializarDawg(dawg, palabras);
        } else {
            // Si se proporciona una ruta de archivo, inicializa el Dawg desde el archivo
            List<String> palabras = leerArchivoLineaPorLinea(filePath);
            inicializarDawg(dawg, palabras);
        }
        this.allDawgs.put(name, dawg);
    }

    public List<String> leerArchivoLineaPorLinea(String rutaArchivo) {
        List<String> lineas = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(rutaArchivo))) {
            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine().trim();
                if (!linea.isEmpty()) { 
                    lineas.add(linea);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
        return lineas;
    }

    public void inicializarDawg (Dawg dawg, List<String> palabras) {
        for (String palabra : palabras) {
            dawg.insert(palabra);
        }
    }

    public void addAlphabet(String name, String rutaArchivo) {

        if (this.allAlphabets == null) {
            this.allAlphabets = new HashMap<>();
        }
        Map<String, Integer> alphabet = new HashMap<>();
        Map<String, Integer> bag = new HashMap<>();

        List<String> lineas = leerArchivoLineaPorLinea(rutaArchivo);
        for (String linea : lineas) {
                String[] partes = linea.split(" ");
                if (partes.length == 3) {
                    String caracter = partes[0];
                    int frecuencia = Integer.parseInt(partes[1]); 
                    int puntos = Integer.parseInt(partes[2]);
                    alphabet.put(caracter, puntos);
                    bag.put(caracter, frecuencia);                     
                }
                else {
                    System.out.println("Línea con formato incorrecto: " + linea);                
                }
        }
        this.allAlphabets.put(name, alphabet);
        this.allBag.put(name, bag);
    }

    public void deleteDawg(String name) {
        if (this.allDawgs != null) {
            this.allDawgs.remove(name);
        }
    }

    public void deleteAlphabet(String name) {
        if (this.allAlphabets != null) {
            this.allAlphabets.remove(name);
        }
    }


    public Dawg getDawg(String nombre) {
        return this.allDawgs.get(nombre);
    }


    public Map<String, Integer> getAlphabet(String nombre) {
        return this.allAlphabets.get(nombre);
    }

    public Map<String, Integer> getBag(String nombre) {
        return this.allBag.get(nombre);
    }




}
