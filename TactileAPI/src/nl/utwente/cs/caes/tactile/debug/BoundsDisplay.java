package nl.utwente.cs.caes.tactile.debug;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


class BoundsDisplay extends Pane {
    Rectangle boundsOverlay = new Rectangle();
    Rectangle proximityOverlay = new Rectangle();
    
    public BoundsDisplay(double width, double height, DoubleProperty proximityThreshold) {
        setBoundsWidth(width);
        setBoundsHeight(height);
        
        boundsOverlay.widthProperty().bind(boundsWidth);
        boundsOverlay.heightProperty().bind(boundsHeight);
        
        NumberBinding offset = Bindings.divide(Bindings.negate(proximityThreshold), 2.0);
        proximityOverlay.xProperty().bind(offset);
        proximityOverlay.yProperty().bind(offset);
        
        proximityOverlay.widthProperty().bind(Bindings.add(boundsWidth, proximityThreshold));
        proximityOverlay.heightProperty().bind(Bindings.add(boundsHeight, proximityThreshold));
        
        boundsOverlay.setFill(new Color(1, 0, 0, 0.1));
        boundsOverlay.setStroke(new Color(1, 0, 0, 0.5));
        proximityOverlay.setFill(new Color(1, 0, 0, 0.1));
        
        getChildren().addAll(boundsOverlay, proximityOverlay);
    }
    
    DoubleProperty boundsWidth = new SimpleDoubleProperty();
    
    public final void setBoundsWidth(double width) {
        boundsWidth.set(width);
    }
    
    DoubleProperty boundsHeight = new SimpleDoubleProperty();
    
    public final void setBoundsHeight(double height) {
        boundsHeight.set(height);
    }
}
