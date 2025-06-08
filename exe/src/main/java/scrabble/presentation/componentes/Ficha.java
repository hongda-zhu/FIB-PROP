package scrabble.presentation.componentes;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;


/**
 * Componente visual que representa una ficha individual de Scrabble.
 * Muestra la letra, puntos asociados y puede ser seleccionada con animaciones.
 * Proporciona efectos visuales como sombras, colores dinámicos y transiciones
 * de elevación para indicar el estado de selección.
 */
public class Ficha extends StackPane {
    private String letra;
    private int puntos;
    private boolean seleccionada;
    private Rectangle fichaFondo;
    private Label letraLabel;
    private Label puntosLabel;
    
    //Tamaño por defecto
    private int tamanoFicha = 40; 
    private static final double ELEVACION_ALTURA = -10.0; 


    /**
     * Constructor con tamaño predeterminado de 40px.
     * @param letra Letra que representa la ficha
     * @param puntos Valor en puntos de la ficha
     * @pre letra != null && !letra.isEmpty() && puntos >= 0
     * @post Ficha creada con tamaño 40px, no seleccionada y cursor tipo mano
     */
    public Ficha(String letra, int puntos) {
        this(letra, puntos, 40); // Tamaño predeterminado de 40px
    }
    

    /**
     * Constructor con tamaño personalizable.
     * @param letra Letra que representa la ficha
     * @param puntos Valor en puntos de la ficha
     * @param tamano Tamaño en píxeles de la ficha (ancho y alto)
     * @pre letra != null && !letra.isEmpty() && puntos >= 0 && tamano > 0
     * @post Ficha creada con tamaño especificado, no seleccionada y apariencia actualizada
     */   
    public Ficha(String letra, int puntos, int tamano) {
        this.letra = letra;
        this.puntos = puntos;
        this.seleccionada = false;
        this.tamanoFicha = tamano;

        // Establecer tamaño fijo
        setMinSize(tamanoFicha, tamanoFicha);
        setPrefSize(tamanoFicha, tamanoFicha);
        setMaxSize(tamanoFicha, tamanoFicha);
        setCursor(javafx.scene.Cursor.HAND);
        actualizarApariencia();
    }
    

    /**
     * Actualiza la apariencia visual de la ficha según su estado actual.
     * Recrea todos los elementos gráficos incluyendo fondo, letra, puntos
     * y efectos visuales como sombras y bordes de selección.
     * @pre La ficha debe estar inicializada
     * @post Elementos visuales recreados según estado de selección actual
     */
    private void actualizarApariencia() {
        getChildren().clear();
        
        // Crear el fondo de la ficha con el tamaño exacto
        fichaFondo = new Rectangle(tamanoFicha, tamanoFicha);
        fichaFondo.setFill(seleccionada ? Color.web("#FFF3CD") : Color.web("#F5DEB3"));
        fichaFondo.setStroke(seleccionada ? Color.web("#856404") : Color.web("#8B4513"));
        fichaFondo.setStrokeWidth(seleccionada ? 2 : 1);
        fichaFondo.setArcWidth(8);
        fichaFondo.setArcHeight(8);
        
        // Añadir efecto de sombra
        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetX(2);
        dropShadow.setOffsetY(2);
        dropShadow.setRadius(3);
        dropShadow.setColor(Color.web("#00000033"));
        fichaFondo.setEffect(dropShadow);
        
        // Crear el label de la letra - ajustar tamaño de fuente según el tamaño de la ficha
        double tamanoFuente = tamanoFicha * 0.5; // 20 para tamaño de 40px
        letraLabel = new Label(String.valueOf(letra));
        letraLabel.setFont(Font.font("Arial", FontWeight.BOLD, tamanoFuente));
        letraLabel.setTextFill(Color.web("#8B4513"));
        StackPane.setAlignment(letraLabel, Pos.CENTER);
        
        // Crear el label de los puntos (en la esquina inferior derecha)
        double tamanoFuentePuntos = tamanoFicha * 0.3; 
        puntosLabel = new Label(String.valueOf(puntos));
        puntosLabel.setFont(Font.font("Arial", tamanoFuentePuntos));
        puntosLabel.setTextFill(Color.web("#8B4513"));
        StackPane.setAlignment(puntosLabel, Pos.BOTTOM_RIGHT);
        puntosLabel.setTranslateX(-tamanoFicha * 0.1); 
        puntosLabel.setTranslateY(-tamanoFicha * 0.1); 
        
        // Si está seleccionada, añadir un borde más pronunciado
        if (seleccionada) {
            Rectangle seleccionBorde = new Rectangle(tamanoFicha, tamanoFicha);
            seleccionBorde.setFill(Color.TRANSPARENT);
            seleccionBorde.setStroke(Color.web("#856404"));
            seleccionBorde.setStrokeWidth(1);
            seleccionBorde.setArcWidth(10);
            seleccionBorde.setArcHeight(10);
            StackPane.setAlignment(seleccionBorde, Pos.CENTER);
            getChildren().add(seleccionBorde);
        }
        
        // Añadir todos los elementos
        getChildren().addAll(fichaFondo, letraLabel, puntosLabel);
    }
    
    /**
     * Selecciona la ficha activando el estado visual de selección.
     * Aplica animación de elevación y reproduce efecto sonoro.
     * Cambia colores y añade borde de selección.
     * @pre La ficha no debe estar ya seleccionada
     * @post seleccionada == true, animación de elevación ejecutada, sonido reproducido
     */    
    public void seleccionar() {
        this.seleccionada = true;
        actualizarApariencia();

        TranslateTransition tt = new TranslateTransition(Duration.millis(100), this);
        tt.setFromY(0);
        tt.setToY(ELEVACION_ALTURA);
        tt.play();     
        SoundEffects.play("pop.mp3");   
    }
    

    /**
     * Deselecciona la ficha desactivando el estado visual de selección.
     * Aplica animación de descenso y reproduce efecto sonoro.
     * Restaura colores originales y quita el borde de selección.
     * @pre La ficha debe estar seleccionada
     * @post seleccionada == false, animación de descenso ejecutada, sonido reproducido
     */    
    public void deseleccionar() {
        this.seleccionada = false;
        actualizarApariencia();

        TranslateTransition tt = new TranslateTransition(Duration.millis(100), this);
        tt.setFromY(ELEVACION_ALTURA);
        tt.setToY(0);
        tt.play(); 
        SoundEffects.play("pop.mp3");       
    }
    

    /**
     * Establece un nuevo tamaño para la ficha.
     * Actualiza dimensiones y recalcula todos los elementos proporcionales.
     * @param tamano Nuevo tamaño en píxeles
     * @pre tamano > 0
     * @post tamanoFicha == tamano && apariencia actualizada con nuevas proporciones
     */
    public void setTamanoFicha(int tamano) {
        this.tamanoFicha = tamano;
        setMinSize(tamanoFicha, tamanoFicha);
        setPrefSize(tamanoFicha, tamanoFicha);
        setMaxSize(tamanoFicha, tamanoFicha);
        actualizarApariencia();
    }
    

    /**
     * Obtiene la letra de la ficha.
     * @return Letra que representa esta ficha
     */    
    public String getLetra() {
        return letra;
    }
    


    /**
     * Obtiene el valor en puntos de la ficha.
     * @return Puntos asociados a esta ficha
     */    
    public int getPuntos() {
        return puntos;
    }
    
    /**
     * Verifica si la ficha está actualmente seleccionada.
     * @return true si está seleccionada, false en caso contrario
     */    
    public boolean isSeleccionada() {
        return seleccionada;
    }
}
