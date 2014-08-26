package nl.utwente.cs.caes.tactile;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;

public class DraggableGroup extends Group {
	private boolean isInUse = false;	// When true this group can only move by user input

	public DraggableGroup(Node... nodes) {
		super(nodes);
		initialise();
	}
	
	public DraggableGroup() {
		super();
		initialise();
	}
	
	private void initialise() {
		final Delta dragDelta = new Delta();

		// Consume any synthesized MouseEvent so that TouchEvents aren't handled twice
		addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.isSynthesized()) {
					event.consume();
				}
			}
		});
		
		
		setOnTouchPressed(new EventHandler<TouchEvent>() {
			@Override
			public void handle(TouchEvent event) {
				isInUse = true;
				// record a delta distance for the drag and drop operation.
				dragDelta.x = getTranslateX()
						- event.getTouchPoint().getSceneX();
				dragDelta.y = getTranslateY()
						- event.getTouchPoint().getSceneY();
			}
		});

		setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				isInUse = true;
				// record a delta distance for the drag and drop operation.
				dragDelta.x = getTranslateX() - event.getSceneX();
				dragDelta.y = getTranslateY() - event.getSceneY();
			}
		});

		setOnTouchReleased(new EventHandler<TouchEvent>() {
			@Override
			public void handle(TouchEvent event) {
				isInUse = false;
			}
		});

		setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				isInUse = false;
			}
		});

		setOnTouchMoved(new EventHandler<TouchEvent>() {
			@Override
			public void handle(TouchEvent event) {
				setTranslateX(event.getTouchPoint().getSceneX() + dragDelta.x);
				setTranslateY(event.getTouchPoint().getSceneY() + dragDelta.y);
			}
		});

		setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				setTranslateX(event.getSceneX() + dragDelta.x);
				setTranslateY(event.getSceneY() + dragDelta.y);
			}
		});
	}
	
	// Kan aangeroepen worden door een ActionGroup om weg te gaan als er een
	// andere incompatible ActionGroup in de buurt is. De distance is de
	// distance (ongeveer) tussen de twee ActionGroups, de angle is de richting
	// waarin de DraggableGroup moet bewegen, in graden. Bij een kleinere distance
	// zou er dan bijvoorbeeld harder weggegaan kunnen worden?
	protected void requestMove(double smallestPositiveGap, double angle){
		if (!isInUse){
			// Ga weg
		}
	}

	private class Delta {
		double x, y;
	}
}
