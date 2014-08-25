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
		initialise();
	}
	
    public TouchPane(Node... children){
		super(children);
		initialise();
	}
	
    // Called by all constructors
	private void initialise() {
		TouchPane thisPane = this;
		
		// Add resize listeners (needs optimisation)
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
		
		// Initialise QuadTree
		quadTree = new QuadTree(this.localToScene(getBoundsInLocal()));
	}


	public void register(InteractableGroup object) {
		Bounds objectBounds = object.localToScene(object.getBoundsInLocal());
		objectByBounds.put(objectBounds, object);
		quadTree.insert(objectBounds);
	}
	
	public void deregister(InteractableGroup object) {
		Bounds toRemove = null;
		for (Bounds bounds : objectByBounds.keySet()){
			if (objectByBounds.get(bounds) == object) {
				toRemove = bounds;
				break;
			}
		}
		objectByBounds.remove(toRemove);
		quadTree.delete(toRemove);
	}

}
