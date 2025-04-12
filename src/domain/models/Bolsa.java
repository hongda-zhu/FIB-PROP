package domain.models;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Bolsa {
    private List<Ficha> fichas;

    public void llenarBolsa(String rutaArchivo) {
        fichas = new ArrayList<>();
    
        List<String> lineas = leerArchivoLineaPorLinea(rutaArchivo);
        for (String linea : lineas) {
                String[] partes = linea.split(" ");
                if (partes.length == 3) {
                    String caracter = partes[0];
                    int frecuencia = Integer.parseInt(partes[1]);
                    int puntos = Integer.parseInt(partes[2]);
                    agregarFichas(caracter, frecuencia, puntos);                     
                }
                else {
                    System.out.println("Línea con formato incorrecto: " + linea);                
                }
        }
      
        Collections.shuffle(fichas);
    }


    private List<String> leerArchivoLineaPorLinea(String rutaArchivo) {
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
        
    private void agregarFichas(String letra, int cantidad, int valor) {
        for (int i = 0; i < cantidad; i++) {
            fichas.add(new Ficha(letra, valor));
        }
    }
    

    public Ficha sacarFicha() {
        if (fichas.isEmpty()) return null;
        return fichas.remove(0);
    }

    public int getCantidadFichas() {
        return fichas.size();
    }
}

