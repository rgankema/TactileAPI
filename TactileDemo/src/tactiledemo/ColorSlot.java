package tactiledemo;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import nl.utwente.cs.caes.tactile.ActionGroup;
import nl.utwente.cs.caes.tactile.event.ActionGroupEvent;

public class ColorSlot extends ActionGroup {
    Rectangle background;
    ColorSlotPane parent;
    
    public ColorSlot(ColorSlotPane parent) {
        this.parent = parent;
        
        background = new Rectangle(50, 50);
        background.setFill(Color.DARKGREY);
        background.setStroke(Color.DARKGREY);
        background.setStrokeWidth(4);
        getChildren().add(background);
        
        setOnProximityEntered(event -> onProximityEntered(event));
        setOnProximityLeft(event -> onProximityLeft(event));
        setOnDropped(event -> onDropped(event));
        setOnAreaLeft(event -> onAreaLeft(event));
    }
    
    private ObjectProperty<ColorItem> colorItem;
    
    public ColorItem getColorItem() {
        return colorItemProperty().get();
    }
    
    public void setColorItem(ColorItem value) {
        colorItemProperty().set(value);
    }
    
    public ObjectProperty<ColorItem> colorItemProperty() {
        if (colorItem == null) {
            colorItem = new SimpleObjectProperty<>();
        }
        return colorItem;
    }
     
    private void onProximityEntered(ActionGroupEvent event) {
        ActionGroup slot = event.getTarget();
        ActionGroup otherAG = event.getOtherGroup();
        
        if (otherAG instanceof ColorItem) {
            ColorItem colorItem = (ColorItem) otherAG;
            
            if (parent.getBackgroundColor() == Color.GREY) {
                parent.setBorderColor(colorItem.getColor());
            } else if (!parent.getBorderColor().equals(colorItem.getColor())) {
                slot.moveAwayFrom(otherAG, 500);
            }
        }
    }
    
    private void onProximityLeft(ActionGroupEvent event) {
        ActionGroup otherAG = event.getOtherGroup();
        
        if (otherAG instanceof ColorItem) {
            ColorItem colorItem = (ColorItem) otherAG;
            if (parent.getBackgroundColor() == Color.GREY) {
                parent.setBorderColor(Color.GREY);
            }
        }
    }
    
    private void onDropped(ActionGroupEvent event) {
        ActionGroup otherAG = event.getOtherGroup();
        
        if (otherAG instanceof ColorItem) {
                if (getColorItem() == null) {
                    setColorItem((ColorItem) otherAG);
                    otherAG.getDraggableGroupParent().setAnchor(this);
                    otherAG.getDraggableGroupParent().setAnchorOffset(new Point2D(2, 2));
                }
        }
    }
    
    private void onAreaLeft(ActionGroupEvent event) {
        ActionGroup otherAG = event.getOtherGroup();
        
        if (otherAG == getColorItem()) {
            setColorItem(null);
        }
    }
}
