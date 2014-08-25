package nl.utwente.cs.caes.tactile;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class TouchPane extends Pane {
	private Map<Rectangle, InteractableGroup> objectByBounds = new HashMap<Rectangle, InteractableGroup>();
    private QuadTree quadTree;

    public TouchPane() {
            super();
    }
	
    public TouchPane(Node... children){
		super(children);
	}

    public void register(InteractableGroup object) {
        
    }
}
