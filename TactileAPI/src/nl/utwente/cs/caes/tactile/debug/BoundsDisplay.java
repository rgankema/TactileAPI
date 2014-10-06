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
        
        NumberBinding proximityWidth = Bindings.add(boundsWidth, proximityThreshold);
        NumberBinding proximityHeight = Bindings.add(boundsHeight, proximityThreshold);
        proximityOverlay.widthProperty().bind(proximityWidth);
        proximityOverlay.heightProperty().bind(proximityHeight);
        
        boundsOverlay.setFill(new Color(0.2, 0.5, 0.6, 0.2));
        proximityOverlay.setFill(new Color(0.6, 0.2, 0.5, 0.2));
        
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
