package nl.utwente.cs.caes.tactile.control;

import javafx.scene.Node;

public class DragPane extends ContentControlBase {
    
    private static String DEFAULT_STYLE_CLASS = "drag-pane";
    
    // Constructors
    
    public DragPane() {
        super();
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);
    }
    
    public DragPane(Node content) {
        this();
        setContent(content);
    }
    
    // Properties
}
