package nl.utwente.cs.caes.tactile;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
	
	private void addResizeListeners(){
		TouchPane thisPane = this;
		widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observableValue,
					Number oldWidth, Number newWidth) {
				quadTree = new QuadTree(thisPane.localToScene(getBoundsInLocal()));
			}
			
			
			
		});
	}

	public void register(InteractableGroup object) {
		
	}
}
