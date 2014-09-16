package tactiledemo;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class ColorSlotPane extends Pane {
    Rectangle background;
    ColorSlot leftSlot;
    ColorSlot rightSlot;
    
    public ColorSlotPane() {
        background = new Rectangle(190, 90);
        background.setStrokeWidth(5);
        background.setFill(Color.GREY);
        background.setStroke(Color.GREY);
        
        leftSlot = new ColorSlot(this);
        leftSlot.relocate(20, 20);
        leftSlot.colorItemProperty().addListener((ObservableValue<? extends ColorItem> observable, ColorItem oldValue, ColorItem newValue) -> {
            if (newValue == null) {
                if (rightSlot.getColorItem() == null) {
                    setBackgroundColor(Color.GREY);
                }
            } else {
                if (rightSlot.getColorItem() == null) {
                    setBackgroundColor(newValue.getColor());
                    setBorderColor(newValue.getColor());
                } else if (!newValue.getColor().equals(rightSlot.getColorItem().getColor())) {
                    rightSlot.getColorItem().getDraggableGroupParent().setAnchor(null);
                    rightSlot.getColorItem().moveAwayFrom(newValue, 500);
                    setBackgroundColor(newValue.getColor());
                    setBorderColor(newValue.getColor());
                }
            }
        });
        
        rightSlot = new ColorSlot(this);
        rightSlot.relocate(120, 20);
        rightSlot.colorItemProperty().addListener((ObservableValue<? extends ColorItem> observable, ColorItem oldValue, ColorItem newValue) -> {
            if (newValue == null) {
                if (leftSlot.getColorItem() == null) {
                    setBackgroundColor(Color.GREY);
                }
            } else {
                if (leftSlot.getColorItem() == null) {
                    setBackgroundColor(newValue.getColor());
                    setBorderColor(newValue.getColor());
                } else if (!newValue.getColor().equals(leftSlot.getColorItem().getColor())) {
                    leftSlot.getColorItem().getDraggableGroupParent().setAnchor(null);
                    leftSlot.getColorItem().moveAwayFrom(newValue, 1000);
                    setBackgroundColor(newValue.getColor());
                    setBorderColor(newValue.getColor());
                }
            }
        });
        
        getChildren().addAll(background, leftSlot, rightSlot);
    }
    
    public final void setBorderColor(Paint color) {
        borderColorProperty().set(color);
    }
    
    public final Paint getBorderColor() {
        return borderColorProperty().get();
    }
    
    public final ObjectProperty<Paint> borderColorProperty() {
        return background.strokeProperty();
    }
    
    public final void setBackgroundColor(Paint color) {
        backgroundColorProperty().set(color);
    }
    
    public final Paint getBackgroundColor() {
        return backgroundColorProperty().get();
    }
    
    public final ObjectProperty<Paint> backgroundColorProperty() {
        return background.fillProperty();
    }
}
