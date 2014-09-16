package tactiledemo;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import nl.utwente.cs.caes.tactile.ActionGroup;
import nl.utwente.cs.caes.tactile.event.ActionGroupEvent;

/**
 * An ActionGroup that reacts on ColorItems. ColorItems can be dropped on
 * a ColorSlot, and will then be anchored to it.
 */
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
    
    /**
     * The ColorItem that is anchored to this slot
     */
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
            
            // If the ColorSlotPane this slot belongs to is grey, then
            // its border will be set to the approaching ColorItem's color
            // If the ColorSlotPane has a different color from the approaching
            // ColorItem, then it should flee away
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
            
            // Set the ColorSlotPane's border back to grey if it's not hosting
            // another ColorItem
            if (parent.getBackgroundColor() == Color.GREY) {
                parent.setBorderColor(Color.GREY);
            }
        }
    }
    
    private void onDropped(ActionGroupEvent event) {
        ActionGroup otherAG = event.getOtherGroup();
        
        if (otherAG instanceof ColorItem) {
            // If the ColorSlot has room for a ColorItem, then that ColorItem
            // will be anchored to that ColorSlot
            if (getColorItem() == null) {
                setColorItem((ColorItem) otherAG);
                otherAG.getDraggableGroupParent().setAnchor(this);
                otherAG.getDraggableGroupParent().setAnchorOffset(new Point2D(2, 2));
            }
        }
    }
    
    private void onAreaLeft(ActionGroupEvent event) {
        ActionGroup otherAG = event.getOtherGroup();
        
        // If the ActionGroup that has left the area of the ColorSlot is the
        // ColorItem that is anchored to it, then ColorItem will be set to null
        if (otherAG == getColorItem()) {
            setColorItem(null);
        }
    }
}
