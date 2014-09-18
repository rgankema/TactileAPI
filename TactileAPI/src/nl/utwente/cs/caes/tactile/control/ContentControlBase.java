package nl.utwente.cs.caes.tactile.control;


import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.StyleableProperty;
import javafx.scene.Node;
import javafx.scene.control.Control;

@DefaultProperty("content")
public abstract class ContentControlBase extends Control {
    
    // Constructors
    
    protected ContentControlBase() {
        ((StyleableProperty<Boolean>)focusTraversableProperty()).applyStyle(null, false); 
    }
    
    // Properties
    
    /**
     * The node used as the content of this ContentControl.
     */
    private ObjectProperty<Node> content;

    public final void setContent(Node value) {
        contentProperty().set(value);
    }

    public final Node getContent() {
        return content == null ? null : content.get();
    }

    public final ObjectProperty<Node> contentProperty() {
        if (content == null) {
            content = new SimpleObjectProperty<>(this, "content");
        }
        return content;
    }
}