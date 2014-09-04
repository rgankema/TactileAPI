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
		DraggableGroup thisGroup = this;
		
		setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				currentTouches++;
				setActive(true);
				
				dragContext.pastSpeedsX = new double[DragContext.PAST_FRAMES];
				dragContext.pastSpeedsY = new double[DragContext.PAST_FRAMES];
				dragContext.pastIndex = 0;
				
				if (thisGroup.getParent() == null) {
					return;
				}
				
				// record a delta distance for the drag and drop operation.
				dragContext.deltaX = getLayoutX() - event.getSceneX();
				dragContext.deltaY = getLayoutY() - event.getSceneY();				
				
				if (isGoToForegroundOnActive()) {
					goToForeground();
				}
			}
		});

		setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				double speedX = 0, speedY = 0;
				for(int i = 0; i < DragContext.PAST_FRAMES && i <= dragContext.pastSpeedsX.length; i++){
					speedX += dragContext.pastSpeedsX[i];
					speedY += dragContext.pastSpeedsY[i];
				}
				speedX = speedX / (double) dragContext.pastSpeedsX.length;
				speedY = speedY / (double) dragContext.pastSpeedsY.length;

				setVector(new Point2D(speedX*DragContext.FORCE_MULT,speedY*DragContext.FORCE_MULT));
				
				currentTouches--;
				setActive(false);
			}
		});

		setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (thisGroup.getParent() == null || currentTouches > 1)
					return;
				
				double x = event.getSceneX() + dragContext.deltaX;
				double y = event.getSceneY() + dragContext.deltaY;
				
				Parent parent = getParent();
				if (parent instanceof TouchPane) {
					TouchPane pane = (TouchPane) parent;
					if (pane.isBordersCollide()) {
						Bounds paneBounds = pane.getBoundsInLocal();
						Bounds thisBounds = thisGroup.getBoundsInLocal();
						
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
				dragContext.pastIndex = (dragContext.pastIndex + 1) % dragContext.PAST_FRAMES;
				
				relocate(x, y);
			}
		});

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
				currentTouches++;
				setActive(true);
				
				dragContext.pastSpeedsX = new double[DragContext.PAST_FRAMES];
				dragContext.pastSpeedsY = new double[DragContext.PAST_FRAMES];
				dragContext.pastIndex = 0;
				
				if (thisGroup.getParent() == null) {
					return;
				}
				
				// record a delta distance for the drag and drop operation.
				dragContext.deltaX = getLayoutX() - event.getTouchPoint().getSceneX();
				dragContext.deltaY = getLayoutY() - event.getTouchPoint().getSceneY();				
				
				if (isGoToForegroundOnActive()) {
					goToForeground();
				}
			}
		});


		setOnTouchReleased(new EventHandler<TouchEvent>() {
			@Override
			public void handle(TouchEvent event) {
				double speedX = 0, speedY = 0;
				for(int i = 0; i < DragContext.PAST_FRAMES && i <= dragContext.pastSpeedsX.length; i++){
					speedX += dragContext.pastSpeedsX[i];
					speedY += dragContext.pastSpeedsY[i];
				}
				speedX = speedX / (double) dragContext.pastSpeedsX.length;
				speedY = speedY / (double) dragContext.pastSpeedsY.length;

				setVector(new Point2D(speedX*DragContext.FORCE_MULT,speedY*DragContext.FORCE_MULT));
				
				currentTouches--;
				setActive(false);
			}
		});
		
		setOnTouchMoved(new EventHandler<TouchEvent>() {
			@Override
			public void handle(TouchEvent event) {
				if (thisGroup.getParent() == null || currentTouches > 1)
					return;
				
				double x = event.getTouchPoint().getSceneX() + dragContext.deltaX;
				double y = event.getTouchPoint().getSceneY() + dragContext.deltaY;
				
				Parent parent = getParent();
				if (parent instanceof TouchPane) {
					TouchPane pane = (TouchPane) parent;
					if (pane.isBordersCollide()) {
						Bounds paneBounds = pane.getBoundsInLocal();
						Bounds thisBounds = thisGroup.getBoundsInLocal();
						
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
		});

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
		static final int PAST_FRAMES = 20;
		static final int FORCE_MULT = 50;
		
		double deltaX, deltaY;
		double[] pastSpeedsX, pastSpeedsY; //Keep record of past translation amounts
		int pastIndex;
	}
}
