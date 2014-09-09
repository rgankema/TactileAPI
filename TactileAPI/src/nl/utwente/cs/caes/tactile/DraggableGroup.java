package nl.utwente.cs.caes.tactile;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.Pane;

public class DraggableGroup extends Group {
	private int currentTouches = 0;
	
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
		DragContext dragContext = new DragContext();
		
		// Consume any synthesized MouseEvent so that TouchEvents aren't handled twice
		addEventFilter(MouseEvent.ANY, event -> {
			if (event.isSynthesized()) {
				event.consume();
			}
		});
		
		setOnTouchPressed(event -> {
			handleTouchDown(dragContext, event.getTouchPoint().getSceneX(), event.getTouchPoint().getSceneY());
			event.consume();
		});


		setOnTouchReleased(event -> {
			handleTouchUp(dragContext, event.getTouchPoint().getSceneX(), event.getTouchPoint().getSceneY());
			event.consume();
		});
		
		setOnTouchMoved(event -> {
			handleTouchMove(dragContext, event.getTouchPoint().getSceneX(), event.getTouchPoint().getSceneY());
			event.consume();
		});
		
		setOnMousePressed(event -> {
			handleTouchDown(dragContext, event.getSceneX(), event.getSceneY());
			event.consume();
		});

		setOnMouseReleased(event -> {
			handleTouchUp(dragContext, event.getSceneX(), event.getSceneY());
			event.consume();
		});

		setOnMouseDragged(event -> {
			handleTouchMove(dragContext, event.getSceneX(), event.getSceneY());
			event.consume();
		});
	}
	
	private void handleTouchDown(DragContext dragContext, double sceneX, double sceneY) {
		currentTouches++;
		setActive(true);
		if (isZeroVectorOnActive()) {
			vectorProperty().set(Point2D.ZERO);
		}
		
		dragContext.pastSpeedsX = new double[DragContext.PAST_FRAMES];
		dragContext.pastSpeedsY = new double[DragContext.PAST_FRAMES];
		dragContext.pastIndex = 0;
		
		if (this.getParent() == null) {
			return;
		}
		
		// record a delta distance for the drag and drop operation.
		dragContext.deltaX = getLayoutX() - sceneX;
		dragContext.deltaY = getLayoutY() - sceneY;				
		
		if (isGoToForegroundOnActive()) {
			goToForeground();
		}
	}
	
	private void handleTouchMove(DragContext dragContext, double sceneX, double sceneY) {
		if (this.getParent() == null || currentTouches > 1)
			return;
		
		double x = sceneX + dragContext.deltaX;
		double y = sceneY + dragContext.deltaY;
		
		Parent parent = getParent();
		if (parent instanceof TouchPane) {
			TouchPane pane = (TouchPane) parent;
			if (pane.isBordersCollide()) {
				Bounds paneBounds = pane.getBoundsInLocal();
				Bounds thisBounds = this.getBoundsInLocal();
				
				if (x < paneBounds.getMinX()) {
					x = paneBounds.getMinX();
				} else if (x + thisBounds.getWidth() > paneBounds.getMaxX()) {
					x = paneBounds.getMaxX() - thisBounds.getWidth();
				}
				if (y < paneBounds.getMinY()) {
					y = paneBounds.getMinY();
				} else if (y + thisBounds.getHeight() > paneBounds.getMaxY()) {
					y = paneBounds.getMaxY() - thisBounds.getHeight();
				}
			}
		}
		
		dragContext.pastSpeedsX[dragContext.pastIndex] = x - getLayoutX();
		dragContext.pastSpeedsY[dragContext.pastIndex] = y - getLayoutY();
		dragContext.pastIndex = (dragContext.pastIndex + 1) % DragContext.PAST_FRAMES;
		
		relocate(x, y);
	}
	
	private void handleTouchUp(DragContext dragContext, double sceneX, double sceneY) {
		if (isSlideOnRelease()) {
			double speedX = 0, speedY = 0;
			for(int i = 0; i < DragContext.PAST_FRAMES && i <= dragContext.pastSpeedsX.length; i++){
				speedX += dragContext.pastSpeedsX[i];
				speedY += dragContext.pastSpeedsY[i];
			}
			speedX = speedX / (double) dragContext.pastSpeedsX.length;
			speedY = speedY / (double) dragContext.pastSpeedsY.length;
	
			setVector(new Point2D(speedX*DragContext.FORCE_MULT,speedY*DragContext.FORCE_MULT));
		}
		currentTouches--;
		setActive(false);
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
	
	public final void setGoToForegroundOnActive(boolean value) {
		goToForegroundOnActiveProperty().set(value);
	}
	
	public final boolean isGoToForegroundOnActive() {
		return goToForegroundOnActive == null ? true : goToForegroundOnActive.get();
	}
	
	public final BooleanProperty goToForegroundOnActiveProperty() {
		if (goToForegroundOnActive == null) {
			goToForegroundOnActive = new SimpleBooleanProperty(true);
		}
		return goToForegroundOnActive;
	}
	
	/**
	 * Whether this {@code DraggableGroup} will slide further in the direction
	 * it was being dragged to after a TouchReleased or MouseReleased event. If
	 * true, the {@code DraggableGroup} will be given a vector that will cause
	 * it to slide in that direction.
	 * 
	 * @defaultvalue false
	 */
	private BooleanProperty slideOnRelease;
	
	public final void setSlideOnRelase(boolean value) {
		slideOnReleaseProperty().set(value);
	}
	
	public final boolean isSlideOnRelease() {
		return slideOnReleaseProperty().get();
	}
	
	public final BooleanProperty slideOnReleaseProperty() {
		if (slideOnRelease == null) {
			slideOnRelease = new SimpleBooleanProperty(false);
		}
		return slideOnRelease;
	}
	
	/**
	 * Whether this {@code DraggableGroup} will set its {@link #vector} to a zero vector
	 * when {@link #active} is set to true.
	 * 
	 * @defaultvalue true
	 */
	private BooleanProperty zeroVectorOnActive;
	
	public void setZeroVectorOnActive(boolean value) {
		zeroVectorOnActiveProperty().set(value);
	}
	
	public boolean isZeroVectorOnActive() {
		return zeroVectorOnActiveProperty().get();
	}
	
	public final BooleanProperty zeroVectorOnActiveProperty() {
		if (zeroVectorOnActive == null) {
			zeroVectorOnActive = new SimpleBooleanProperty(true);
		}
		return zeroVectorOnActive;
	}
	
	/**
	 * The 2D velocity vector for this {@DraggableGroup}
	 */
	private ObjectProperty<Point2D> vector;
	
	public void setVector(Point2D value) {
		vectorProperty().set(value);
	}
	
	public Point2D getVector() {
		return vectorProperty().get();
	}
	
	public ObjectProperty<Point2D> vectorProperty() {
		if (vector == null) {
			vector = new SimpleObjectProperty<Point2D>(new Point2D(0, 0));
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
		static final int PAST_FRAMES = 20;
		static final int FORCE_MULT = 50;
		
		double deltaX, deltaY;
		double[] pastSpeedsX, pastSpeedsY; //Keep record of past translation amounts
		int pastIndex;
	}
}
