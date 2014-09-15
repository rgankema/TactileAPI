package tactiledemo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.property.ObjectProperty;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import nl.utwente.cs.caes.tactile.ActionGroup;
import nl.utwente.cs.caes.tactile.event.ActionGroupEvent;

public class ColorSlotPane extends Pane {
    Rectangle background;
    ActionGroup leftSlot;
    ActionGroup rightSlot;
    Rectangle leftSlotBackground;
    Rectangle rightSlotBackground;
    ColorItem leftColorItem = null;
    ColorItem rightColorItem = null;
    
    public ColorSlotPane() {
        background = new Rectangle(190, 90);
        background.setStrokeWidth(5);
        background.setFill(Color.GREY);
        background.setStroke(Color.GREY);
        
        leftSlotBackground = new Rectangle(50, 50);
        leftSlotBackground.setFill(Color.DARKGREY);
        leftSlotBackground.setStroke(Color.DARKGREY);
        leftSlotBackground.setStrokeWidth(3);
        leftSlot = new ActionGroup(leftSlotBackground);
        leftSlot.relocate(20, 20);
        leftSlot.setOnProximityEntered(event -> onProximityEntered(event));
        leftSlot.setOnProximityLeft(event -> onProximityLeft(event));
        leftSlot.setOnDropped(event -> onDropped(event));
        leftSlot.setOnAreaLeft(event -> onAreaLeft(event));
        
        rightSlotBackground = new Rectangle(50, 50);
        rightSlotBackground.setFill(Color.DARKGREY);
        rightSlotBackground.setStroke(Color.DARKGREY);
        rightSlotBackground.setStrokeWidth(3);
        rightSlot = new ActionGroup(rightSlotBackground);
        rightSlot.relocate(120, 20);
        rightSlot.setOnProximityEntered(event -> onProximityEntered(event));
        rightSlot.setOnProximityLeft(event -> onProximityLeft(event));
        rightSlot.setOnDropped(event -> onDropped(event));
        rightSlot.setOnAreaLeft(event -> onAreaLeft(event));
        
        getChildren().addAll(background, leftSlot, rightSlot);
    }
    
    public void setBorderColor(Paint color) {
        borderColorProperty().set(color);
    }
    
    public Paint getBorderColor() {
        return borderColorProperty().get();
    }
    
    public ObjectProperty<Paint> borderColorProperty() {
        return background.strokeProperty();
    }
    
    public void setBackgroundColor(Paint color) {
        backgroundColorProperty().set(color);
    }
    
    public Paint getBackgroundColor() {
        return backgroundColorProperty().get();
    }
    
    public ObjectProperty<Paint> backgroundColorProperty() {
        return background.fillProperty();
    }
    
    private List<ColorItem> colorItems = new ArrayList<>();
    
    private void onProximityEntered(ActionGroupEvent event) {
        ActionGroup slot = event.getTarget();
        ActionGroup otherAG = event.getOtherGroup();
        
        if (otherAG instanceof ColorItem) {
            ColorItem colorItem = (ColorItem) otherAG;
            
            if (getBorderColor() == Color.GREY) {
                setBorderColor(colorItem.getColor());
                colorItems.add(colorItem);
            } else if (!getBorderColor().equals(colorItem.getColor())) {
                slot.moveAwayFrom(otherAG, 500);
            } else {
                colorItems.add(colorItem);
            }
        }
    }
    
    private void onProximityLeft(ActionGroupEvent event) {
        ActionGroup otherAG = event.getOtherGroup();
        
        if (otherAG instanceof ColorItem) {
            ColorItem colorItem = (ColorItem) otherAG;
            colorItems.remove(colorItem);
            if (getBackgroundColor() == Color.GREY) {
                setBorderColor(Color.GREY);
            }
            if (colorItems.size() > 0) {
                setBorderColor(colorItems.get(colorItems.size() - 1).getColor());
            }
        }
    }
    
    private void onDropped(ActionGroupEvent event) {
        ActionGroup otherAG = event.getOtherGroup();
        ActionGroup slot = event.getTarget();
        
        if (otherAG instanceof ColorItem) {
            if (slot == leftSlot) {
                if (leftColorItem == null) {
                    if (rightColorItem != null && !rightColorItem.getColor().equals(getBackgroundColor())) {
                        rightColorItem.getDraggableGroupParent().setAnchor(null);
                        rightColorItem.moveAwayFrom(otherAG, 500);
                    }
                    leftColorItem = (ColorItem) otherAG;
                    otherAG.getDraggableGroupParent().setAnchor(leftSlot);
                    setBackgroundColor(leftColorItem.getColor());
                }
            } else { //rightslot 
                if (rightColorItem == null) {
                    if (leftColorItem != null && !leftColorItem.getColor().equals(getBackgroundColor())) {
                        leftColorItem.getDraggableGroupParent().setAnchor(null);
                        leftColorItem.moveAwayFrom(otherAG, 500);
                    }
                    rightColorItem = (ColorItem) otherAG;
                    otherAG.getDraggableGroupParent().setAnchor(rightSlot);
                    setBackgroundColor(rightColorItem.getColor());
                }
            }
        }
    }
    
    private void onAreaLeft(ActionGroupEvent event) {
        ActionGroup otherAG = event.getOtherGroup();
        ActionGroup slot = event.getTarget();
        
        if (otherAG instanceof ColorItem) {
            if (slot == leftSlot) {
                leftColorItem = null;
                if (rightColorItem == null) {
                    setBackgroundColor(Color.GREY);
                }
            } else { //rightslot 
                rightColorItem = null;
                if (leftColorItem == null) {
                    setBackgroundColor(Color.GREY);
                }
            }
        }
    }
}
