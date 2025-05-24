package scrabble.presentation.componentes;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import scrabble.presentation.views.VistaTablero;

public class CasillaDisplay extends StackPane {
    private TipoCasilla tipo;
    
    private int fila;
    private int columna;
    private Label casillaLabel;
    private StackPane fichaContainer;
    private Label fichaLabel;
    private Label puntosLabel;
    private boolean tieneFicha;
    private boolean fichaConfirmada;
    
    // Elementos para el resaltado durante la selección
    private Rectangle bordeResaltado;
    
    // Tamaño configurable de la casilla
    private int tamano = 40; // Valor predeterminado
    
    public CasillaDisplay(int fila, int columna) {
        this(fila, columna, 40); // Usar tamaño predeterminado
    }
    
    public CasillaDisplay(int fila, int columna, int tamano) {
        this.fila = fila;
        this.columna = columna;
        this.tieneFicha = false;
        this.fichaConfirmada = false;
        this.tamano = tamano;
        
        // Crear el label para la casilla base
        casillaLabel = new Label();
        casillaLabel.setFont(Font.font(tamano * 0.3)); // Hacer el texto proporcional al tamaño
        
        // Crear el borde de resaltado para cuando se selecciona una ficha
        bordeResaltado = new Rectangle(tamano, tamano);
        bordeResaltado.setFill(Color.TRANSPARENT);
        bordeResaltado.setStroke(Color.ORANGE);
        bordeResaltado.setStrokeWidth(2.5);
        bordeResaltado.setArcWidth(8);
        bordeResaltado.setArcHeight(8);
        bordeResaltado.setVisible(false);

        this.getChildren().addAll(casillaLabel, bordeResaltado);
        this.setAlignment(Pos.CENTER);
        
        // Establecer tamaño fijo para cada casilla
        this.setMinSize(tamano, tamano);
        this.setPrefSize(tamano, tamano);
        this.setMaxSize(tamano, tamano);
    }
    
    /**
     * Configura el evento de clic para poder retirar fichas del tablero
     * NOTA: Este método sigue existiendo por compatibilidad pero no hace nada,
     * ya que ahora los clics se manejan directamente en VistaTablero
     */
    public void configurarEventoRetiroFicha(VistaTablero vistaTablero) {
        // Este método queda vacío porque ahora los clics se manejan en VistaTablero
    }
    
    /**
     * Establece un nuevo tamaño para la casilla
     */
    public void setTamano(int nuevoTamano) {
        this.tamano = nuevoTamano;
        this.setMinSize(tamano, tamano);
        this.setPrefSize(tamano, tamano);
        this.setMaxSize(tamano, tamano);
        
        // Actualizar el tamaño del borde de resaltado
        if (bordeResaltado != null) {
            bordeResaltado.setWidth(tamano);
            bordeResaltado.setHeight(tamano);
        }
        
        // Si hay una ficha, actualizarla también
        if (tieneFicha && fichaContainer != null) {
            fichaContainer.setMinSize(tamano, tamano);
            fichaContainer.setPrefSize(tamano, tamano);
            fichaContainer.setMaxSize(tamano, tamano);
            
            // Recrear la ficha con el nuevo tamaño
            String letra = getLetraFicha();
            int puntos = getPuntosFicha();
            boolean confirmada = fichaConfirmada;
            
            quitarFicha();
            colocarFicha(letra, puntos, confirmada);
        }
    }
    
    /**
     * Activa o desactiva el resaltado visual cuando una ficha está seleccionada
     * y el cursor está sobre esta casilla
     */
    public void resaltar(boolean resaltar) {
        // Solo resaltar si la casilla no tiene ficha
        if (!tieneFicha) {
            bordeResaltado.setVisible(resaltar);
            if (resaltar) {
                bordeResaltado.toFront();
                this.setStyle(this.getStyle() + " -fx-cursor: hand;");
            }
            else {
                String currentStyle = this.getStyle();
                if (currentStyle != null && currentStyle.contains("-fx-cursor: hand;")) {
                    this.setStyle(currentStyle.replace(" -fx-cursor: hand;", ""));
                }            
            }
        }
    }
    
    public void setTipo(TipoCasilla tipo) {
        this.tipo = tipo;
        switch (tipo) {
            case PALABRA_TRIPLE:
                this.setStyle("-fx-background-color: #d4916e; -fx-border-color: #dddddd;");
                casillaLabel.setText("TP");
                casillaLabel.setTextFill(Color.WHITE);
                casillaLabel.setFont(Font.font("Arial", FontWeight.BOLD, tamano * 0.275)); // ~11px para tamano=40
                break;
            case PALABRA_DOBLE:
                this.setStyle("-fx-background-color: #f2c79b; -fx-border-color: #dddddd;");
                casillaLabel.setText("DP");
                casillaLabel.setTextFill(Color.web("#4a3428"));
                casillaLabel.setFont(Font.font("Arial", FontWeight.BOLD, tamano * 0.275)); // ~11px para tamano=40
                break;
            case LETRA_TRIPLE:
                this.setStyle("-fx-background-color: #a6837a; -fx-border-color: #dddddd;");
                casillaLabel.setText("TL");
                casillaLabel.setTextFill(Color.WHITE);
                casillaLabel.setFont(Font.font("Arial", FontWeight.BOLD, tamano * 0.275)); // ~11px para tamano=40
                break;
            case LETRA_DOBLE:
                this.setStyle("-fx-background-color: #e6d5b8; -fx-border-color: #dddddd;");
                casillaLabel.setText("DL");
                casillaLabel.setTextFill(Color.web("#5c4a3d"));
                casillaLabel.setFont(Font.font("Arial", FontWeight.BOLD, tamano * 0.275)); // ~11px para tamano=40
                break;
            case CENTRO:
                this.setStyle("-fx-background-color: rgb(250, 190, 37); -fx-border-color: #dddddd;");
                casillaLabel.setText("★");
                casillaLabel.setTextFill(Color.web("#8b6914"));
                casillaLabel.setFont(Font.font("Arial", FontWeight.BOLD, tamano * 0.35)); // ~14px para tamano=40
                break;
            case NORMAL:
            default:
                this.setStyle("-fx-background-color:rgb(253, 242, 214); -fx-border-color: #dddddd;");
                casillaLabel.setText("");
                break;
        }        
    }
    
    public boolean tieneFicha() {
        return tieneFicha;
    }
    
    /**
     * Coloca una ficha en una casilla del tablero
     */
    public void colocarFicha(String letra, int puntos, boolean confirmada) {
        if (tieneFicha) {
            quitarFicha();
        }
        
        tieneFicha = true;
        fichaConfirmada = confirmada;
        
        fichaContainer = new StackPane();
        fichaContainer.setMinSize(tamano, tamano);
        fichaContainer.setPrefSize(tamano, tamano);
        fichaContainer.setMaxSize(tamano, tamano);
        
        Rectangle fichaFondo = new Rectangle(tamano * 0.95, tamano * 0.95); // Ligeramente más pequeño que la casilla
        Color colorFondo = confirmada ? Color.web("#E6CCAB") : Color.web("#F5DEB3");
        Color colorBorde = confirmada ? Color.web("#6E4529") : Color.web("#8B4513");
        
        fichaFondo.setFill(colorFondo);
        fichaFondo.setStroke(colorBorde); 
        fichaFondo.setStrokeWidth(confirmada ? 3 : 2);
        fichaFondo.setArcWidth(8);
        fichaFondo.setArcHeight(8);
        
        fichaLabel = new Label(letra);
        fichaLabel.setFont(Font.font("Arial", FontWeight.BOLD, tamano * 0.5)); // ~20px para tamano=40
        fichaLabel.setTextFill(Color.web("#8B4513")); 
        
        puntosLabel = new Label(String.valueOf(puntos));
        puntosLabel.setFont(Font.font("Arial", tamano * 0.3)); // ~12px para tamano=40
        puntosLabel.setTextFill(Color.web("#8B4513"));
        
        StackPane.setAlignment(puntosLabel, Pos.BOTTOM_RIGHT);
        puntosLabel.setTranslateX(-tamano * 0.1); // ~-5px para tamano=40
        puntosLabel.setTranslateY(-tamano * 0.1); // ~-5px para tamano=40
        
        fichaContainer.getChildren().addAll(fichaFondo, fichaLabel, puntosLabel);
        
        // Añadir el contenedor de la ficha a la casilla
        this.getChildren().add(fichaContainer);
        fichaContainer.toFront();
        
        // Ocultar el label de la casilla (TP, DL, etc.) y el borde de resaltado
        casillaLabel.setVisible(false);
        bordeResaltado.setVisible(false);
        
        System.out.println("Ficha " + letra + " colocada en casilla " + fila + "," + columna + (confirmada ? " (confirmada)" : ""));
    }
    
    /**
     * Sobrecarga del método colocarFicha para mantener compatibilidad
     */
    public void colocarFicha(String letra, int puntos) {
        colocarFicha(letra, puntos, false);
    }
    
    /**
     * Marca una ficha existente como confirmada
     */
    public void marcarFichaComoConfirmada() {
        if (tieneFicha && !fichaConfirmada) {
            fichaConfirmada = true;
            
            // marcar ficha como confirmada
            String letra = getLetraFicha();
            int puntos = getPuntosFicha();
            quitarFicha();
            colocarFicha(letra, puntos, true);
            System.out.println("Ficha en casilla " + fila + "," + columna + " marcada como confirmada");
        }
    }
    
    public void quitarFicha() {
        if (tieneFicha && fichaContainer != null) {
            this.getChildren().remove(fichaContainer);
            casillaLabel.setVisible(true);
            tieneFicha = false;
            fichaConfirmada = false;
            fichaContainer = null;
            fichaLabel = null;
            puntosLabel = null;
            System.out.println("Ficha retirada de casilla " + fila + "," + columna);
        }
    }
    
    public String getLetraFicha() {
        if (tieneFicha && fichaLabel != null) {
            return fichaLabel.getText();
        }
        return "";
    }
    
    public int getPuntosFicha() {
        if (tieneFicha && puntosLabel != null) {
            return Integer.parseInt(puntosLabel.getText());
        }
        return 0;
    }
    
    public int getFila() {
        return fila;
    }
    
    public int getColumna() {
        return columna;
    }
    
    public TipoCasilla getTipo() {
        return tipo;
    }
    
    /**
     * Verifica si la ficha en esta casilla está confirmada
     */
    public boolean esFichaConfirmada() {
        return fichaConfirmada;
    }
}
