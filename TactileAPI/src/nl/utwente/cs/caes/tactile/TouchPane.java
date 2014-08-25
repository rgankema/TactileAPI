package nl.utwente.cs.caes.tactile;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class TouchPane extends Pane {
	private Map<Bounds, InteractableGroup> objectByBounds = new HashMap<Bounds, InteractableGroup>();
	private QuadTree quadTree;
	
	public TouchPane() {
		super();
	}
	
        public TouchPane(Node... children){
		super(children);
	}
	
	private void addResizeListeners(){
		TouchPane thisPane = this;
		
		// Can be optimised
		widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observableValue,
					Number oldWidth, Number newWidth) {
				quadTree = new QuadTree(thisPane.localToScene(getBoundsInLocal()));
				for (Bounds bounds : objectByBounds.keySet()) {
					quadTree.insert(bounds);
				}
			}
		});
		
		heightProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observableValue,
					Number oldHeight, Number newHeight) {
				quadTree = new QuadTree(thisPane.localToScene(getBoundsInLocal()));
				for (Bounds bounds : objectByBounds.keySet()) {
					quadTree.insert(bounds);
				}
			}
		});
	}

    public void register(InteractableGroup object) {
    }
}
