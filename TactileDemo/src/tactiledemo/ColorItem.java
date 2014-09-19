
package tactiledemo;


import javafx.beans.property.ObjectProperty;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import nl.utwente.cs.caes.tactile.ActionGroup;
import nl.utwente.cs.caes.tactile.ActivePane;

/**
 * An ActionGroup that contains a colored square. Interacts with ColorSlots.
 */
public class ColorItem extends ActivePane {
    Rectangle rectangle;
    
    public ColorItem() {
        rectangle = new Rectangle(50, 50);
        setContent(rectangle);
    }
    
    public void setColor(Paint color) {
        colorProperty().set(color);
    }
    
    public Paint getColor() {
        return colorProperty().get();
    }
    
    public ObjectProperty<Paint> colorProperty() {
        return rectangle.fillProperty();
    }
}
