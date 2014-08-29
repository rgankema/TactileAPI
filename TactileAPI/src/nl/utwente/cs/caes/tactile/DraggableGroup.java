package nl.utwente.cs.caes.tactile;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.Pane;

public class DraggableGroup extends Group {
	
	public DraggableGroup(Node... nodes) {
		super(nodes);
		initialise();
	}
	
	public DraggableGroup() {
		super();
		initialise();
	}
	
	// Called by the constructors
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
				setActive(true);
				// record a delta distance for the drag and drop operation.
				dragDelta.x = getTranslateX()
						- event.getTouchPoint().getSceneX();
				dragDelta.y = getTranslateY()
						- event.getTouchPoint().getSceneY();
				if (isGoToForegroundOnActive()) {
					goToForeground();
				}
			}
		});

		setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				setActive(true);
				// record a delta distance for the drag and drop operation.
				dragDelta.x = getTranslateX() - event.getSceneX();
				dragDelta.y = getTranslateY() - event.getSceneY();
				if (isGoToForegroundOnActive()) {
					goToForeground();
				}
			}
		});

		setOnTouchReleased(new EventHandler<TouchEvent>() {
			@Override
			public void handle(TouchEvent event) {
				setActive(false);
			}
		});

		setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				setActive(false);
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
	
	/**
	 * 
	 */
	private ReadOnlyBooleanWrapper active = new ReadOnlyBooleanWrapper();
	
	private void setActive(boolean value) {
		activePropertyImpl().set(true);
	}
	
	public boolean isActive() {
		return active == null ? false : active.get();
	}
	
	public ReadOnlyBooleanProperty activeProperty(){
		return activePropertyImpl().getReadOnlyProperty();
	}
	
	private ReadOnlyBooleanWrapper activePropertyImpl() {
		if (active == null) {
			active = new ReadOnlyBooleanWrapper();
		}
		return active;
	}
	
	/**
	 * Whether this {@code Node} will go to the foreground when {@link #activeProperty() active} is 
	 * set to true. If set to true, {@link #goToForeground()} is called whenever {@link #activeProperty() is
	 * set to true.
	 */
	private BooleanProperty goToForegroundOnActive;
	
	public BooleanProperty goToForegroundOnActiveProperty(){
		if (goToForegroundOnActive == null) {
			goToForegroundOnActive = new SimpleBooleanProperty();
			goToForegroundOnActive.set(true);
		}
		return goToForegroundOnActive;
	}
	
	public void setGoToForegroundOnActive(boolean value){
		goToForegroundOnActiveProperty().set(value);
	}
	
	public boolean isGoToForegroundOnActive(){
		return goToForegroundOnActive == null ? true : goToForegroundOnActive.get();
	}
	
	/**
	 * Requests this {@code DraggableGroup} to move away from another {@code DraggableGroup}.
	 * The speed with which it will move away depends on the distance from the other group.
	 * @param group	The {@code DraggableGroup} to move away from
	 */
	public void moveAwayFrom(DraggableGroup group){
		Bounds thisBounds = this.localToScene(this.getBoundsInLocal());
		Bounds otherBounds = group.localToScene(group.getBoundsInLocal());
		double x1 = thisBounds.getMinX() + thisBounds.getWidth() / 2;
		double y1 = thisBounds.getMinY() + thisBounds.getHeight() / 2;
		double x2 = otherBounds.getMinX() + thisBounds.getWidth() / 2;
		double y2 = otherBounds.getMinY() + thisBounds.getHeight() / 2;
		Point2D thisCenterPoint = new Point2D(x1, y1);
		Point2D otherCenterPoint = new Point2D(x2, y2);
		// The direction to move to
		double angle = otherCenterPoint.angle(thisCenterPoint);
		
		// Move away
	}
	
	public final void goToForeground() {
		Node ancestor = this;
		Node ancestorParent = this.getParent();
		while (!(ancestorParent instanceof Pane)) {
			ancestorParent = ancestor;
			ancestor = ancestor.getParent();
		}
		if ((ancestorParent instanceof Pane)) {
			((Pane)ancestorParent).getChildren().remove(ancestor);
			((Pane)ancestorParent).getChildren().add(ancestor);
		}
	}

	// Help class used for moving
	private class Delta {
		double x, y;
	}
}
