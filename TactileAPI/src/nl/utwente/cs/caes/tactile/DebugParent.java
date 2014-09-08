package nl.utwente.cs.caes.tactile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.TouchPoint;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class DebugParent extends StackPane {
	Pane overlay = new Pane();
	
	Map<Integer, Color> colorByEventId = new HashMap<Integer, Color>();
	Map<Integer, Circle> circleByEventId = new HashMap<Integer, Circle>();

	List<TouchPoint> touchPoints = new ArrayList<TouchPoint>();
	int touchSetId = 0;
	
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
		addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (getMapMouseToTouch()) {
					if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
						touchPoints.clear();
						TouchPoint tp = createTouchPoint(event);
						touchPoints.add(tp);
						TouchEvent tEvent = new TouchEvent(this, event.getTarget(), TouchEvent.TOUCH_PRESSED,
								tp, touchPoints, touchSetId, event.isShiftDown(), event.isControlDown(), 
								event.isAltDown(), event.isMetaDown());
						Event.fireEvent(event.getTarget(), tEvent);
					}
					else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
						TouchPoint tp = createTouchPoint(event);
						touchPoints.add(tp);
						TouchEvent tEvent = new TouchEvent(this, event.getTarget(), TouchEvent.TOUCH_MOVED,
								tp, touchPoints, touchSetId, event.isShiftDown(), event.isControlDown(), 
								event.isAltDown(), event.isMetaDown());
						Event.fireEvent(event.getTarget(), tEvent);
					}
					else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
						TouchPoint tp = createTouchPoint(event);
						touchPoints.add(tp);
						TouchEvent tEvent = new TouchEvent(this, event.getTarget(), TouchEvent.TOUCH_RELEASED,
								tp, touchPoints, touchSetId, event.isShiftDown(), event.isControlDown(), 
								event.isAltDown(), event.isMetaDown());
						Event.fireEvent(event.getTarget(), tEvent);
	
						touchSetId++;
					}
					event.consume();
				}
			}
		});
		
		addEventFilter(TouchEvent.TOUCH_PRESSED, new EventHandler<TouchEvent>() {
			@Override
			public void handle(TouchEvent event) {
				if(!colorByEventId.containsKey(event.getEventSetId())){
					double r = Math.random();
					double g = Math.random();
					double b = Math.random();
					colorByEventId.put(event.getEventSetId(), 
							new Color(r, g, b, 0.5));
				}
				
				double x = event.getTouchPoint().getSceneX();
				double y = event.getTouchPoint().getSceneY();
				
				Circle circle = new Circle(x, y, getTouchCircleRadius());
				circle.setFill(new Color(0,0,0,0));
				circle.setStroke(colorByEventId.get(event.getEventSetId()));
				circle.setStrokeWidth(2);
				
				circleByEventId.put(event.getEventSetId(), circle);
				overlay.getChildren().add(circle);
			}
		});
		
		addEventFilter(TouchEvent.TOUCH_MOVED, new EventHandler<TouchEvent>() {
			@Override
			public void handle(TouchEvent event) {
				Circle circle = circleByEventId.get(event.getEventSetId());
				
				double x = event.getTouchPoint().getSceneX() - circle.getRadius();
				double y = event.getTouchPoint().getSceneY() - circle.getRadius();
				
				circle.relocate(x, y);
			}
		});
		
		addEventFilter(TouchEvent.TOUCH_RELEASED, new EventHandler<TouchEvent>() {
			@Override
			public void handle(TouchEvent event) {
				colorByEventId.remove(event.getEventSetId());
				Circle circle = circleByEventId.get(event.getEventSetId());
				overlay.getChildren().remove(circle);
			}
		});
	}
	
	// Returns a TouchPoint for a given MouseEvent
	private TouchPoint createTouchPoint(MouseEvent event) {
		TouchPoint tp = new TouchPoint(touchPoints.size(), TouchPoint.State.PRESSED, 
				event.getSceneX(), event.getSceneY(), event.getScreenX(), event.getScreenY(), 
				event.getTarget(), null);
		return tp;
	}
	
	/**
	 * Whether {@code MouseEvents} will be replaced with corresponding {@code TouchEvents}
	 * 
	 * @defaultvalue true
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
			mapMouseToTouch = new SimpleBooleanProperty(true);
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
	
	public void register(TouchPane touchPane) {
		
	}
	
	public void register(DraggableGroup draggable) {
		
	}
}
