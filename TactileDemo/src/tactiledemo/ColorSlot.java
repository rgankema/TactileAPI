package tactiledemo;

import java.util.HashMap;
import java.util.Map;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import nl.utwente.cs.caes.tactile.ActivePane;
import nl.utwente.cs.caes.tactile.DragPane;
import nl.utwente.cs.caes.tactile.event.ActivePaneEvent;

/**
 * An ActivePane that reacts on ColorItems. ColorItems can be dropped on
 * a ColorSlot, and will then be anchored to it.
 */
public class ColorSlot extends ActivePane {
    Rectangle background;
    ColorSlotPane parent;
    Map<ColorItem, ChangeListener<Boolean>> dropListenerByColorItem = new HashMap<>();
    
    public ColorSlot(ColorSlotPane parent) {
        this.parent = parent;
        
        background = new Rectangle(50, 50);
        background.setFill(Color.DARKGREY);
        background.setStroke(Color.DARKGREY);
        background.setStrokeWidth(4);
        setContent(background);
        
        setOnProximityEntered(event -> onProximityEntered(event));
        setOnProximityLeft(event -> onProximityLeft(event));
        setOnAreaEntered(event -> onAreaEntered(event));
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
    
    private void onProximityEntered(ActivePaneEvent event) {
        ActivePane slot = event.getTarget();
        ActivePane otherAP = event.getOther();
        
        if (otherAP instanceof ColorItem) {
            ColorItem colorItem = (ColorItem) otherAP;
            
            // If the ColorSlotPane this slot belongs to is grey, then
            // its border will be set to the approaching ColorItem's color
            // If the ColorSlotPane has a different color from the approaching
            // ColorItem, then it should flee away
            if (parent.getBackgroundColor() == Color.GREY) {
                parent.setBorderColor(colorItem.getColor());
            } else if (!parent.getBorderColor().equals(colorItem.getColor())) {
                slot.moveAwayFrom(otherAP, 500);
            }
        }
    }
    
    private void onProximityLeft(ActivePaneEvent event) {
        ActivePane otherAP = event.getOther();
        
        if (otherAP instanceof ColorItem) {
            ColorItem colorItem = (ColorItem) otherAP;
            
            // Set the ColorSlotPane's border back to grey if it's not hosting
            // another ColorItem
            if (parent.getBackgroundColor() == Color.GREY) {
                parent.setBorderColor(Color.GREY);
            }
        }
    }
    
    private void onAreaEntered(ActivePaneEvent event) {
        ActivePane otherAP = event.getOther();
        
        if (otherAP instanceof ColorItem) {
            ColorItem colorItem = (ColorItem) otherAP;
            DragPane dragParent = colorItem.getDragPaneParent();
            
            // When a ColorItem enters the area, call onDropped when its DragPane
            // is not in use anymore. That way only ColorItems that are actively
            // dragged and dropped in a slot will be accepted, rather than any
            // ColorItem that enters the area
            ChangeListener<Boolean> listener = (observable, oldVal, newVal) -> {
                if (!newVal) {
                    onDropped(colorItem);
                }
            };
            dragParent.inUseProperty().addListener(listener);
            dropListenerByColorItem.put(colorItem, listener);
        }
    }
    
    private void onAreaLeft(ActivePaneEvent event) {
        ActivePane otherAP = event.getOther();
        
        if (otherAP instanceof ColorItem) {
            ColorItem colorItem = (ColorItem) otherAP;
            
            //Stop listening for drag and drop operation
            ChangeListener<Boolean> listener = dropListenerByColorItem.remove(colorItem);
            colorItem.getDragPaneParent().inUseProperty().removeListener(listener);
            
            // If the ActionPane that has left the area of the ColorSlot is the
            // ColorItem that is anchored to it, then ColorItem will be set to null
            if (colorItem == getColorItem()) {
                setColorItem(null);
            }
        }
    }
    
    private void onDropped(ColorItem colorItem) {
        // If the ColorSlot has room for a ColorItem, then that ColorItem
        // will be anchored to that ColorSlot
        if (getColorItem() == null) {
            setColorItem((ColorItem) colorItem);
            colorItem.getDragPaneParent().setAnchor(this);
            colorItem.getDragPaneParent().setAnchorOffset(new Point2D(2, 2));
        }
    }
}
