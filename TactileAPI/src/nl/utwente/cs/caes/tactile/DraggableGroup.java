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
				System.out.println("TouchEvent");
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
				System.out.println("MouseEvent");
				isInUse = true;
				// record a delta distance for the drag and drop operation.
				dragDelta.x = getTranslateX() - event.getSceneX();
				dragDelta.y = getTranslateY() - event.getSceneY();
			}
		});

		setOnTouchReleased(new EventHandler<TouchEvent>() {
			@Override
			public void handle(TouchEvent event) {
				System.out.println("TouchEvent");
				isInUse = false;
			}
		});

		setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println("MouseEvent");
				isInUse = false;
			}
		});

		setOnTouchMoved(new EventHandler<TouchEvent>() {
			@Override
			public void handle(TouchEvent event) {
				System.out.println("TouchEvent");
				setTranslateX(event.getTouchPoint().getSceneX() + dragDelta.x);
				setTranslateY(event.getTouchPoint().getSceneY() + dragDelta.y);
			}
		});

		setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println("MouseEvent");
				setTranslateX(event.getSceneX() + dragDelta.x);
				setTranslateY(event.getSceneY() + dragDelta.y);
			}
		});

	}

	private class Delta {
		double x, y;
	}
}
