package nl.utwente.cs.caes.tactile.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.Event;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.TouchPoint;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import nl.utwente.cs.caes.tactile.ActionGroup;
import nl.utwente.cs.caes.tactile.DraggableGroup;
import nl.utwente.cs.caes.tactile.event.ActionGroupEvent;

public class DebugParent extends StackPane {
	
	Pane overlay = new Pane();
	Map<Integer, TouchCircle> circleByTouchId = new TreeMap<Integer, TouchCircle>();
	Map<Integer, Line> lineByTouchId = new TreeMap<Integer, Line>();
	
	Map<DraggableGroup, Vector> vectorByDraggableGroup = new ConcurrentHashMap<DraggableGroup, Vector>();
	Map<Pair<ActionGroup>, Line> lineByActionGroupPair = new ConcurrentHashMap<Pair<ActionGroup>, Line>();

	List<TouchPoint> touchPoints = new ArrayList<TouchPoint>();
	int touchSetId = 0;
	boolean active = false;
	
	public DebugParent() {
		super();
		initialise();
	}
	
	public DebugParent(Node node) {
		super(node);
		initialise();
	}
	
	private void initialise() {
		overlay.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
		
		// Overlay shouldn't receive events
		overlay.setDisable(true);
		
		// Makes sure the overlay is always drawn on top of the other child
		getChildren().add(overlay);
		getChildren().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable value) {
				getChildren().remove(overlay);
				getChildren().add(overlay);
			}
		});
		
		// Maps mouse events to touch events
		addEventFilter(MouseEvent.ANY, event -> {
			if (getMapMouseToTouch() && !event.isSynthesized()) {
				if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
					TouchPoint tp = createTouchPoint(event);
					TouchEvent tEvent = new TouchEvent(this, event.getTarget(), TouchEvent.TOUCH_PRESSED,
							tp, touchPoints, touchSetId, event.isShiftDown(), event.isControlDown(), 
							event.isAltDown(), event.isMetaDown());
					Event.fireEvent(event.getTarget(), tEvent);
				}
				else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
					TouchPoint tp = createTouchPoint(event);
					TouchEvent tEvent = new TouchEvent(this, event.getTarget(), TouchEvent.TOUCH_MOVED,
							tp, touchPoints, touchSetId, event.isShiftDown(), event.isControlDown(), 
							event.isAltDown(), event.isMetaDown());
					Event.fireEvent(event.getTarget(), tEvent);
				}
				else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
					TouchPoint tp = createTouchPoint(event);
					TouchEvent tEvent = new TouchEvent(this, event.getTarget(), TouchEvent.TOUCH_RELEASED,
							tp, touchPoints, touchSetId, event.isShiftDown(), event.isControlDown(), 
							event.isAltDown(), event.isMetaDown());
					Event.fireEvent(event.getTarget(), tEvent);
					
					touchSetId++;
				}
				// Send synthesized MouseEvent
				MouseEvent mouseEvent = new MouseEvent(event.getSource(), event.getTarget(), event.getEventType(),
						event.getSceneX(), event.getSceneY(), event.getScreenX(), event.getScreenY(), event.getButton(),
						event.getClickCount(), event.isShiftDown(), event.isControlDown(), event.isAltDown(), event.isMetaDown(),
						event.isPrimaryButtonDown(), event.isMiddleButtonDown(), event.isSecondaryButtonDown(), true,
						event.isPopupTrigger(), event.isStillSincePress(), event.getPickResult());
				Event.fireEvent(event.getTarget(), mouseEvent);
				
				event.consume();
			}
		});
		
		addEventFilter(TouchEvent.TOUCH_PRESSED, event -> {
			int touchId = event.getTouchPoint().getId();
			Node target = (Node) event.getTarget();
			Bounds bounds = target.localToScene(target.getBoundsInLocal());
			
			double x = event.getTouchPoint().getSceneX();
			double y = event.getTouchPoint().getSceneY();
			
			TouchCircle circle = new TouchCircle(x, y, getTouchCircleRadius(), touchId);
			circleByTouchId.put(touchId, circle);
			overlay.getChildren().add(circle);
			
			Line line = new Line(x, y, bounds.getMinX(), bounds.getMinY());
			lineByTouchId.put(touchId, line);
			overlay.getChildren().add(line);
			
			circle.relocate(x, y);
			
			ScaleTransition st = new ScaleTransition(new Duration(200), circle);
			st.setFromX(0);
			st.setFromY(0);
			st.setToX(1);
			st.setToY(1);
			
			FadeTransition ft = new FadeTransition(new Duration(200), line);
			ft.setFromValue(0);
			ft.setToValue(1);

			st.play();
			ft.play();
		});
		
		addEventFilter(TouchEvent.TOUCH_MOVED, event -> {
			int touchId = event.getTouchPoint().getId();
			Node target = (Node) event.getTarget();
			Bounds bounds = target.localToScene(target.getBoundsInLocal());
			
			double x = event.getTouchPoint().getSceneX();
			double y = event.getTouchPoint().getSceneY();
			
			TouchCircle circle = circleByTouchId.get(touchId);
			circle.relocate(x, y);
			
			Line line = lineByTouchId.get(touchId);
			line.setStartX(x);
			line.setStartY(y);
			line.setEndX(bounds.getMinX());
			line.setEndY(bounds.getMinY());
		});
		
		addEventFilter(TouchEvent.TOUCH_RELEASED, event -> {
			int touchId = event.getTouchPoint().getId();
			
			TouchCircle circle = circleByTouchId.get(touchId);
			Line line = lineByTouchId.get(touchId);
			
			ScaleTransition st = new ScaleTransition(new Duration(200), circle);
			st.setFromX(1);
			st.setFromY(1);
			st.setToX(0);
			st.setToY(0);
			st.setOnFinished(e -> {
				overlay.getChildren().remove(circle);
			});
			
			FadeTransition ft = new FadeTransition(new Duration(100), line);
			ft.setFromValue(1);
			ft.setToValue(0);
			ft.setOnFinished(e -> {
				overlay.getChildren().remove(line);
			});
			
			st.play();
			ft.play();
		});
		
		addEventFilter(ActionGroupEvent.PROXIMITY_ENTERED, event -> {
			ActionGroup ag1 = event.getTarget();
			ActionGroup ag2 = event.getOtherGroup();
			Bounds b1 = ag1.localToScene(ag1.getBoundsInLocal());
			Bounds b2 = ag2.localToScene(ag2.getBoundsInLocal());
			
			Pair<ActionGroup> pair = new Pair<ActionGroup>(ag1, ag2);
			
			Line line = new Line(b1.getMinX(), b1.getMinY(), b2.getMinX(), b2.getMinY());
			Line old = lineByActionGroupPair.put(pair, line);
			overlay.getChildren().add(line);
			overlay.getChildren().remove(old);
		});
		
		addEventFilter(ActionGroupEvent.PROXIMITY_LEFT, event -> {
			ActionGroup ag1 = event.getTarget();
			ActionGroup ag2 = event.getOtherGroup();
			Pair<ActionGroup> pair = null;
			Line line = null;
			
			for (Pair<ActionGroup> p : lineByActionGroupPair.keySet()) {
				if (p.left == ag1 && p.right == ag2 || p.left == ag2 && p.right == ag1) {
					line = lineByActionGroupPair.get(p);
					pair = p;
					break;
				}
			}
			
			lineByActionGroupPair.remove(pair);
			
			final Line rLine = line;
			
			FadeTransition ft = new FadeTransition(new Duration(100), line);
			ft.setFromValue(1);
			ft.setToValue(0);
			ft.setOnFinished(e -> {
				overlay.getChildren().remove(rLine);
			});
			ft.play();
		});
		
		new AnimationTimer() {

			@Override
			public void handle(long arg0) {
				for (DraggableGroup dg : vectorByDraggableGroup.keySet()) {
					Bounds bounds = dg.localToScene(dg.getBoundsInLocal());
					Vector vector = vectorByDraggableGroup.get(dg);
					
					vector.relocate(bounds.getMinX(), bounds.getMinY());
				}
				for (Pair<ActionGroup> pair : lineByActionGroupPair.keySet()) {
					Bounds b1 = pair.left.localToScene(pair.left.getBoundsInLocal());
					Bounds b2 = pair.right.localToScene(pair.right.getBoundsInLocal());
					
					Line line = lineByActionGroupPair.get(pair);
					line.setStartX(b1.getMinX());
					line.setStartY(b1.getMinY());
					line.setEndX(b2.getMinX());
					line.setEndY(b2.getMinY());
				}
			}
		}.start();
	}
	
	// Returns a TouchPoint for a given MouseEvent
	private TouchPoint createTouchPoint(MouseEvent event) {
		TouchPoint tp = new TouchPoint(1, TouchPoint.State.PRESSED, 
				event.getSceneX(), event.getSceneY(), event.getScreenX(), event.getScreenY(), 
				event.getTarget(), null);
		touchPoints.clear();
		touchPoints.add(tp);
		return tp;
	}
	
	/**
	 * Whether {@code MouseEvents} will be replaced with corresponding {@code TouchEvents}
	 * 
	 * @defaultvalue false
	 */
	private BooleanProperty mapMouseToTouch;
	
	public void setMapMouseToTouch(boolean value) {
		mapMouseToTouchProperty().set(value);
	}
	
	public boolean getMapMouseToTouch() {
		return mapMouseToTouchProperty().get();
	}
	
	public BooleanProperty mapMouseToTouchProperty() {
		if (mapMouseToTouch == null) {
			mapMouseToTouch = new SimpleBooleanProperty(false);
		}
		return mapMouseToTouch;
	}
	
	/**
	 * The radius of the circles that are drawn on touch events.
	 */
	private DoubleProperty touchCircleRadius;
	
	public void setTouchCircleRadius(double value) {
		touchCircleRadiusProperty().set(value);
	}
	
	public double getTouchCircleRadius() {
		return touchCircleRadiusProperty().get();
	}
	
	public DoubleProperty touchCircleRadiusProperty() {
		if (touchCircleRadius == null) {
			touchCircleRadius = new SimpleDoubleProperty(50.0) {
				@Override
				public void set(double value) {
					if (value < 0) {
						value = 0;
					}
					super.set(value);
				}
			};
		}
		return touchCircleRadius;
	}
	
	public void register(DraggableGroup draggable) {
		if (!vectorByDraggableGroup.containsKey(draggable)) {
			Vector vector = new Vector(draggable.vectorProperty());
			vectorByDraggableGroup.put(draggable, vector);
			
			Bounds bounds = draggable.localToScene(draggable.getBoundsInLocal());
			vector.relocate(bounds.getMinX(), bounds.getMinY());
			overlay.getChildren().add(vector);
		}
	}
	
	public void deregister(DraggableGroup draggable) {
		Vector vector = vectorByDraggableGroup.remove(draggable);
		if (vector != null) {
			overlay.getChildren().remove(vector);
		}
		
	}
	
	private class Pair<T> {
		public T left;
		public T right;
		
		public Pair(T left, T right) {
			this.left = left;
			this.right = right;
		}
	}
}
