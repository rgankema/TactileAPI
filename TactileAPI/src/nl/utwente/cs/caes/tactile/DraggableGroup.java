package nl.utwente.cs.caes.tactile;

import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

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
		final DragContext dragContext = new DragContext();
		DraggableGroup thisGroup = this;
		
		setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				setActive(true);
				// record a delta distance for the drag and drop operation.
				dragContext.deltaX = getTranslateX() - event.getSceneX();
				dragContext.deltaY = getTranslateY() - event.getSceneY();
				if (isGoToForegroundOnActive()) {
					goToForeground();
				}
			}
		});

		setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				setActive(false);
			}
		});

		setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				setTranslateX(event.getSceneX() + dragContext.deltaX);
				setTranslateY(event.getSceneY() + dragContext.deltaY);
			}
		});

		/*	Misschien hebben we niet eens TouchEvents nodig, ziet er naar uit dat alles met MouseEvents ook al werkt
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
				dragContext.deltaX = getTranslateX()
						- event.getTouchPoint().getSceneX();
				dragContext.deltaY = getTranslateY()
						- event.getTouchPoint().getSceneY();
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
		
		setOnTouchMoved(new EventHandler<TouchEvent>() {
			@Override
			public void handle(TouchEvent event) {
				setTranslateX(event.getTouchPoint().getSceneX() + dragContext.deltaX);
				setTranslateY(event.getTouchPoint().getSceneY() + dragContext.deltaY);
			}
		});
		*/

	}

	/**
	 * Whether this {@code DraggableGroup} is currently being controlled by a
	 * user. A {@code DraggableGroup} becomes active on a touch press, or on
	 * mouse down, and becomes inactive when the touch is released, or on mouse
	 * up.
	 */
	private ReadOnlyBooleanWrapper active = new ReadOnlyBooleanWrapper();
	
	private void setActive(boolean value) {
		activePropertyImpl().set(value);
	}
	
	public boolean isActive() {
		return active == null ? false : activeProperty().get();
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
	 * Whether this {@code DraggableGroup} will go to the foreground when {@link #activeProperty() active} is 
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
	 * Requests this {@code DraggableGroup} to move away from another
	 * {@code DraggableGroup}. The speed with which it will move away depends on
	 * the distance from the other group.
	 * 
	 * @param group
	 *            The {@code DraggableGroup} to move away from
	 * @param distance
	 *            The maximum value of the resulting horizontal or vertical gap
	 *            between the two DraggableGroups
	 * @throws IllegalArgumentException
	 *             When a negative value is provided for distance
	 */
	public void moveAwayFrom(DraggableGroup group, double distance){
		if (distance < 0) {
			throw new IllegalArgumentException("distance cannot be a negative value!");
		}
		
		Bounds thisBounds = this.localToScene(this.getBoundsInLocal());
		Bounds otherBounds = group.localToScene(group.getBoundsInLocal());
		double thisX = thisBounds.getMinX() + thisBounds.getWidth() / 2;
		double thisY = thisBounds.getMinY() + thisBounds.getHeight() / 2;
		double otherX = otherBounds.getMinX() + thisBounds.getWidth() / 2;
		double otherY = otherBounds.getMinY() + thisBounds.getHeight() / 2;
		
		double distanceX = thisX - otherX;
		double distanceY = thisY - otherY;
		double ratio = distanceX / distanceY;
		
		double gapX, gapY;
		
		if (distanceX < 0) {
			gapX = thisBounds.getMaxX() - otherBounds.getMinX();
		} else {
			gapX = otherBounds.getMaxX() - thisBounds.getMinX();
		}
		if (distanceY < 0) {
			gapY = thisBounds.getMaxY() - otherBounds.getMinY();
		} else {
			gapY = otherBounds.getMaxY() - thisBounds.getMinY();
		}
		
		// Only if either the horizontal or vertical distance is smaller than
		// the desired distance between the ActionGroups we need to actually move.
		if (gapX < distance || gapY < distance) {
			double deltaX = distance - gapX;
			double deltaY = distance - gapY;
			double destX, destY;
			
			if (gapX < gapY) {
				if (distanceX < 0) {
					destX = thisX - deltaX;
				} else {
					destX = thisX + deltaX;
				}
				if (distanceY < 0) {
					destY = thisY - deltaX / ratio;
				} else {
					destY = thisY + deltaX / ratio;
				}
			} else {
				if (distanceY < 0) {
					destY = thisY - deltaY;
				} else {
					destY = thisY + deltaY;
				}
				if (distanceX < 0) {
					destX = thisX - deltaY * ratio;
				} else {
					destX = thisX + deltaY * ratio;
				}
			}

			Path path = new Path(new MoveTo(thisX, thisY), new LineTo(destX, destY));
			PathTransition transition = new PathTransition(new Duration(500), path, this);
			transition.setInterpolator(new Interpolator() {
				@Override
				protected double curve(double t) {
					return Math.sqrt(t);
				}
			});
			transition.play();
		}
		
		
	}
	
	/**
	 * Removes this {@code DraggableGroup} from its parent's children and adds
	 * itself again so that it will be rendered on top of the other children.
	 * Only works if this {@code DraggableGroup} has a {@code Pane} as parent.
	 */
	public final void goToForeground() {
		Parent parent = this.getParent();
		if (parent != null && parent instanceof Pane) {
			((Pane)parent).getChildren().remove(this);
			((Pane)parent).getChildren().add(this);
		}
	}

	// Help class used for moving
	private class DragContext {
		double deltaX, deltaY;
	}
}
