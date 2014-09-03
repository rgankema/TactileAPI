package nl.utwente.cs.caes.tactile;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class DraggableGroup extends Group {
	
	// Number of frames over which speed is calculated
	public static final int pastFrames = 20;
	//Multiplication for length of vector to make them big enough
	public static final double forceMult = 50;
	
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
				dragContext.deltaX = getLayoutX() - event.getSceneX();
				dragContext.deltaY = getLayoutY() - event.getSceneY();
				
				dragContext.spdPastX = new double[pastFrames];
				dragContext.spdPastY = new double[pastFrames];
				dragContext.pastIndex = 0;
				if (isGoToForegroundOnActive()) {
					goToForeground();
				}
			}
		});

		setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				//TODO implement speed on release
				double speedX = 0, speedY = 0;
				for(int i = 0; i < pastFrames && i <= dragContext.spdPastX.length; i++){
					speedX += dragContext.spdPastX[i];
					speedY += dragContext.spdPastY[i];
				}
				speedX = speedX / (double) dragContext.spdPastX.length;
				speedY = speedY / (double) dragContext.spdPastY.length;
				speedX = -1.0 * speedX;
				speedY = -1.0 * speedY;
				System.out.println("X: " + speedX);
				System.out.println("y: " + speedY);

				setVector(new Point2D(speedX*forceMult,speedY*forceMult));
				
				setActive(false);
			}
		});

		setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				
				
				double x = event.getSceneX() + dragContext.deltaX;
				double y = event.getSceneY() + dragContext.deltaY;
				
				Parent parent = getParent();
				if (parent instanceof TouchPane) {
					TouchPane pane = (TouchPane) parent;
					if (pane.isBordersCollide()) {
						DraggableGroup dg = DraggableGroup.this;
						Bounds paneBounds = pane.getBoundsInLocal();
						Bounds thisBounds = dg.getBoundsInLocal();
						
						if (x < paneBounds.getMinX()) {
							x = paneBounds.getMinX();
						} else if (x > paneBounds.getMaxX() - thisBounds.getWidth()) {
							x = paneBounds.getMaxX() - thisBounds.getWidth();
						}
						if (y < paneBounds.getMinY()) {
							y = paneBounds.getMinY();
						} else if (y > paneBounds.getMaxY() - thisBounds.getHeight()) {
							y = paneBounds.getMaxY() - thisBounds.getHeight();
						}
					}
				}
				
				dragContext.spdPastX[dragContext.pastIndex] = getLayoutX() - x;
				dragContext.spdPastY[dragContext.pastIndex] = getLayoutY() - y;
				dragContext.pastIndex = (dragContext.pastIndex + 1) % pastFrames;
				
				relocate(x, y);
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
				dragContext.deltaX = getLayoutX()
						- event.getTouchPoint().getSceneX();
				dragContext.deltaY = getTranslateY()
						- event.getLayoutX().getSceneY();
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
	 * The 2D velocity vector for this {@DraggableGroup}
	 */
	private ReadOnlyObjectWrapper<Point2D> vector;
	
	void setVector(Point2D value) {
		vectorPropertyImpl().set(value);
	}
	
	public Point2D getVector() {
		return vectorPropertyImpl().get();
	}
	
	public ReadOnlyObjectProperty<Point2D> vectorProperty() {
		return vectorPropertyImpl().getReadOnlyProperty();
	}
	
	ReadOnlyObjectWrapper<Point2D> vectorPropertyImpl() {
		if (vector == null) {
			vector = new ReadOnlyObjectWrapper<Point2D>(new Point2D(0, 0));
		}
		return vector;
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
		//double prevX, prevY;
		double[] spdPastX, spdPastY; //Keep record of past translation amounts
		int pastIndex;
	}
}
