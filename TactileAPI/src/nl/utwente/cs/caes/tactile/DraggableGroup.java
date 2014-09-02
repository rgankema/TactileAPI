package nl.utwente.cs.caes.tactile;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
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
		final DragContext dragContext = new DragContext();
		
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
	
	public BooleanProperty goToForegroundOnActiveProperty() {
		if (goToForegroundOnActive == null) {
			goToForegroundOnActive = new SimpleBooleanProperty();
			goToForegroundOnActive.set(true);
		}
		return goToForegroundOnActive;
	}
	
	public void setGoToForegroundOnActive(boolean value) {
		goToForegroundOnActiveProperty().set(value);
	}
	
	public boolean isGoToForegroundOnActive() {
		return goToForegroundOnActive == null ? true : goToForegroundOnActive.get();
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
