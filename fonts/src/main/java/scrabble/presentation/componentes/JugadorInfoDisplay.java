package scrabble.presentation.componentes;
public class JugadorInfoDisplay extends javafx.scene.layout.HBox {
    private String nombre;
    private int puntuacion;
    
    public JugadorInfoDisplay(String nombre, int puntuacion) {
        this.nombre = nombre;
        this.puntuacion = puntuacion;
        
        // Configurar apariencia
        setSpacing(10);
        setPadding(new javafx.geometry.Insets(5));
        
        javafx.scene.control.Label nombreLabel = new javafx.scene.control.Label(nombre);
        nombreLabel.setStyle("-fx-font-weight: bold;");
        
        javafx.scene.control.Label puntuacionLabel = new javafx.scene.control.Label(puntuacion + " pts");
        
        getChildren().addAll(nombreLabel, puntuacionLabel);
    }
    
    public void actualizarPuntuacion(int nuevaPuntuacion) {
        this.puntuacion = nuevaPuntuacion;
        ((javafx.scene.control.Label)getChildren().get(1)).setText(puntuacion + " pts");
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public int getPuntuacion() {
        return puntuacion;
    }
}