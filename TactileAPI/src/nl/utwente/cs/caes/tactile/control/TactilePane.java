package nl.utwente.cs.caes.tactile.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import nl.utwente.cs.caes.tactile.skin.TactilePaneSkin;

@DefaultProperty("children")
public class TactilePane extends Control {
    static final String IN_USE = "tactile-pane-in-use";
    static final String ANCHOR = "tactile-pane-anchor";
    static final String ANCHOR_OFFSET = "tactile-pane-anchor-offset";
    static final String VECTOR = "tactile-pane-vector";
    static final String GO_TO_FOREGROUND_ON_CONTACT = "tactile-pane-go-to-foreground-on-contact";
    static final String DRAGGABLE = "tactile-pane-draggable";
    static final String NODES_COLLIDING = "tactile-pane-nodes-colliding";
    static final String NODES_PROXIMITY = "tactile-pane-nodes-proximity";
    static final String TRACKER = "tactile-pane-tracker";
    
    static final String MOUSE_EVENT_FILTER = "tactile-pane-mouse-event-filter";
    static final String TOUCH_EVENT_HANDLER = "tactile-pane-touch-event-handler";
    static final String MOUSE_EVENT_HANDLER = "tactile-pane-mouse-event-handler";
    
    static final int NULL_ID = -1;
    static final int MOUSE_ID = -2;
    
    // STATIC METHODS
    
    static void setInUse(Node node, boolean inUse) {
        setConstraint(node, IN_USE, inUse);
    }
    
    /**
     * Returns whether this {@code Node} is being dragged by the user. If the {@code Node}
     * is not a child of a {@code TactilePane}, it will always return {@code false}.
     */
    public static boolean isInUse(Node node) {
        Boolean result = (Boolean) getConstraint(node, IN_USE);
        return result == null ? false : result;
    }
    
    /**
     * Anchors the {@code node} to the {@code anchor}. If {@code anchor} is not {@code null}, and
     * {@code node} is a child of a {@code TactilePane}, then {@code node} will be relocated
     * to whatever location {@code anchor} moves to, with an offset defined by the {@code anchorOffset}.
     * 
     * An anchored {@code Node} will not respond to physics. When a user tries to drag
     * an anchored {@code Node}, its {@code anchor} will automatically be set to {@code null}.
     */
    public static void setAnchor(Node node, Node anchor) {
        setConstraint(node, ANCHOR, anchor);
    }
    
    /**
     * Returns the {@code Node} {@node} is anchored to.
     */
    public static Node getAnchor(Node node) {
        return (Node) getConstraint(node, ANCHOR);
    }
    
    /**
     * Sets the offset relative to the {@code anchor} by which the given {@code node} will be relocated.
     */
    public static void setAnchorOffset(Node node, Point2D offset) {
        setConstraint(node, ANCHOR_OFFSET, offset);
    }
    
    /**
     * Gets the offset relative to the {@code anchor} by which the given {@code node} will be relocated.
     */
    public static Point2D getAnchorOffset(Node node) {
        Point2D result = (Point2D) getConstraint(node, ANCHOR_OFFSET);
        return result == null ? Point2D.ZERO : result;
    }
    
    public static void setVector(Node node, Point2D vector) {
        setConstraint(node, VECTOR, vector);
    }
    
    public static Point2D getVector(Node node) {
        Point2D result = (Point2D) getConstraint(node, VECTOR);
        return result == null ? Point2D.ZERO : result;
    }
    
    public static void setGoToForegroundOnContact(Node node, boolean goToForegroundOnContact) {
        setConstraint(node, GO_TO_FOREGROUND_ON_CONTACT, goToForegroundOnContact);
    }
    
    public static boolean isGoToForegroundOnContact(Node node) {
        Boolean result = (Boolean) getConstraint(node, GO_TO_FOREGROUND_ON_CONTACT);
        return result == null ? true : result;
    }
    
    public static void setDraggable(Node node, boolean draggable) {
        // If the Node is set to not draggable, then it cannot be in use.
        if (!draggable) {
            setInUse(node, false);
        }
        setConstraint(node, DRAGGABLE, draggable);
    }
    
    public static boolean isDraggable(Node node) {
        Boolean result = (Boolean) getConstraint(node, DRAGGABLE);
        return result == null ? true : result;
    }
    
    public static ObservableSet<Node> getNodesColliding(Node node) {
        ObservableSet<Node> result = (ObservableSet<Node>) getConstraint(node, NODES_COLLIDING);
        if (result == null) {
            result = FXCollections.observableSet(new HashSet<Node>());
            setConstraint(node, NODES_COLLIDING, result);
        }
        return result;
    }
    
    public static ObservableSet<Node> getNodesInProximity(Node node) {
        ObservableSet<Node> result = (ObservableSet<Node>) getConstraint(node, NODES_PROXIMITY);
        if (result == null) {
            result = FXCollections.observableSet(new HashSet<Node>());
            setConstraint(node, NODES_PROXIMITY, result);
        }
        return result;
    }
    
    /**
     * Calls {@code register} on the given TactilePane with {@code node} as argument.
     * If {@code tactilePane} is {@code null}, {@code node} will be deregistered
     * at its previous {@code TactilePane}, if one exists.
     */
    public static void setTracker(Node node, TactilePane tactilePane) {
        if (tactilePane == null) {
            TactilePane oldPane = getTracker(node);
            if (oldPane != null) {
                oldPane.deregister(node);
            }
        } else {
            tactilePane.register(node);
        }
    }
    
    /**
     * The {@code TactilePane} where {@code node} is currently registered.
     */
    public static TactilePane getTracker(Node node) {
        return (TactilePane) getConstraint(node, TRACKER);
    }
    
    // Used to attach a Property to a Node
    static void setConstraint(Node node, Object key, Object value) {
        if (value == null) {
            node.getProperties().remove(key);
        } else {
            node.getProperties().put(key, value);
        }
        if (node.getParent() != null) {
            node.getParent().requestLayout();
        }
    }

    static Object getConstraint(Node node, Object key) {
        if (node.hasProperties()) {
            Object value = node.getProperties().get(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }
    
    // INSTANCE VARIABLES
    private Physics physics;
    
    // CONSTRUCTORS
    
    public TactilePane() {
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);
        // Since this Control is more or less a Pane, focusTraversable should be false by default
        ((StyleableProperty<Boolean>)focusTraversableProperty()).applyStyle(null, false);
        
        getChildren().addListener((ListChangeListener.Change<? extends Node> c) -> {
            c.next();
            for (Node node: c.getRemoved()) {
                removeDragEventHandlers(node);
            }
            for (Node node: c.getAddedSubList()) {
                addDragEventHandlers(node);
            }
        });
        
        physics = new Physics(this);
        physics.start();
    }
    
    public TactilePane(Node... children) {
        this();
        getChildren().addAll(children);
    }
    
    // MAKING CHILDREN DRAGGABLE
    
    // Help class used for moving
    private class DragContext {
        final Node draggable;   // The node that is dragged around
        double localX, localY;  // The x,y position of the Event in the Node
        int touchId;            // The id of the finger/cursor that is currently dragging the Node
        
        public DragContext(Node draggable) {
            this.draggable = draggable;
            touchId = -1;
        }
        
        @Override
        public String toString() {
            return "DragContext [draggable = " + draggable + ", touchId = " + touchId + ", localX = " + localX + ", localY = " + localY + "]";
        }
    }
    
    private void addDragEventHandlers(Node node) {
        final DragContext dragContext = new DragContext(node);
        
        EventHandler<MouseEvent> mouseFilter = event -> {
            if (isDraggable(node) && event.isSynthesized() && event.getTarget() == node) {
                event.consume();
            }
        };
        
        EventHandler<TouchEvent> touchHandler = event -> {
            EventType type = event.getEventType();
            
            if (type == TouchEvent.TOUCH_PRESSED) {
                if (dragContext.touchId == NULL_ID) {
                    dragContext.touchId = event.getTouchPoint().getId();
                    handleTouchPressed(dragContext, event.getTouchPoint().getSceneX(), event.getTouchPoint().getSceneY());
                }
            } else if (type == TouchEvent.TOUCH_MOVED) {
                if (dragContext.touchId == event.getTouchPoint().getId()) {
                    handleTouchMoved(dragContext, event.getTouchPoint().getSceneX(), event.getTouchPoint().getSceneY());
                }
            } else if (type == TouchEvent.TOUCH_RELEASED) {
                if (dragContext.touchId == event.getTouchPoint().getId()) {
                    handleTouchReleased(dragContext);
                    dragContext.touchId = NULL_ID;
                }
            } else return;
            
            event.consume();
        };
        
        EventHandler<MouseEvent> mouseHandler = event -> {
            EventType type = event.getEventType();
            
            if (type == MouseEvent.MOUSE_PRESSED) {
                if (dragContext.touchId == NULL_ID) {
                    dragContext.touchId = MOUSE_ID;
                    handleTouchPressed(dragContext, event.getSceneX(), event.getSceneY());
                }
            } else if (type == MouseEvent.MOUSE_DRAGGED) {
                
                if (dragContext.touchId == MOUSE_ID) {
                    handleTouchMoved(dragContext, event.getSceneX(), event.getSceneY());
                }
            } else if (type == MouseEvent.MOUSE_RELEASED) {
                if (dragContext.touchId == MOUSE_ID) {
                    handleTouchReleased(dragContext);
                    dragContext.touchId = NULL_ID;
                }
            } else return;
            event.consume();
        };
        
        setConstraint(node, MOUSE_EVENT_FILTER, mouseFilter);
        setConstraint(node, TOUCH_EVENT_HANDLER, touchHandler);
        setConstraint(node, MOUSE_EVENT_HANDLER, mouseHandler);
        
        node.addEventFilter(MouseEvent.ANY, mouseFilter);
        node.addEventHandler(TouchEvent.ANY, touchHandler);
        node.addEventHandler(MouseEvent.ANY, mouseHandler);
    }
    
    private void removeDragEventHandlers(Node node) {
        EventHandler<MouseEvent> mouseFilter = (EventHandler<MouseEvent>) getConstraint(node, MOUSE_EVENT_FILTER);
        EventHandler<TouchEvent> touchHandler = (EventHandler<TouchEvent>) getConstraint(node, TOUCH_EVENT_HANDLER);
        EventHandler<MouseEvent> mouseHandler = (EventHandler<MouseEvent>) getConstraint(node, MOUSE_EVENT_HANDLER);
        
        node.removeEventFilter(MouseEvent.ANY, mouseFilter);
        node.removeEventHandler(TouchEvent.ANY, touchHandler);
        node.removeEventHandler(MouseEvent.ANY, mouseHandler);
        
        setConstraint(node, MOUSE_EVENT_FILTER, null);
        setConstraint(node, TOUCH_EVENT_HANDLER, null);
        setConstraint(node, MOUSE_EVENT_HANDLER, null);
    }
    
    private void handleTouchPressed(final DragContext dragContext, double sceneX, double sceneY) {
        Node node = dragContext.draggable;
        if (isDraggable(node)) {
            setAnchor(node, null);
            setInUse(node, true);
            setVector(node, Point2D.ZERO);
            
            Bounds nodeBounds = node.getBoundsInParent();
            
            dragContext.localX = sceneX - nodeBounds.getMinX();
            dragContext.localY = sceneY - nodeBounds.getMinY();

            if (isGoToForegroundOnContact(node)) {
                node.toFront();
            }
        }
    }

    private void handleTouchMoved(final DragContext dragContext, double sceneX, double sceneY) {
        Node node = dragContext.draggable;
        if (isDraggable(node) && getAnchor(node) == null) {

            double x = sceneX - dragContext.localX;
            double y = sceneY - dragContext.localY;

            if (isBordersCollide()) {
                Bounds paneBounds = this.getBoundsInLocal();
                Bounds nodeBounds = node.getBoundsInParent();

                if (x < paneBounds.getMinX()) {
                    x = paneBounds.getMinX();
                } else if (x + nodeBounds.getWidth() > paneBounds.getMaxX()) {
                    x = paneBounds.getMaxX() - nodeBounds.getWidth();
                }
                if (y < paneBounds.getMinY()) {
                    y = paneBounds.getMinY();
                } else if (y + nodeBounds.getHeight() > paneBounds.getMaxY()) {
                    y = paneBounds.getMaxY() - nodeBounds.getHeight();
                }
            }
            node.relocate(x, y);
        }
    }

    private void handleTouchReleased(final DragContext dragContext) {
        Node node = dragContext.draggable;
        setInUse(node, false);
    }
    
    
    // PROPERTIES
    
   /**
     *
     * @return modifiable list of children.
     */
    @Override public ObservableList<Node> getChildren() {
        return super.getChildren();
    }
    
    /**
     * Whether children will collide with the borders of this
     * {@code TactilePane}. If set to true the {@code TactilePane} will prevent
     * children that are moving because of user input or physics to
     * move outside of the {@code TactilePane's} boundaries.
     *
     * @defaultvalue false
     */
    private BooleanProperty bordersCollide;

    public final void setBordersCollide(boolean value) {
        bordersCollideProperty().set(value);
    }

    public final boolean isBordersCollide() {
        return bordersCollideProperty().get();
    }

    public final BooleanProperty bordersCollideProperty() {
        if (bordersCollide == null) {
            bordersCollide = new SimpleBooleanProperty(false);
        }
        return bordersCollide;
    }
    
    public final void setProximityThreshold(double threshold) {
        proximityThresholdProperty().set(threshold);
    }

    public final double getProximityThreshold() {
        return proximityThresholdProperty().get();
    }

    /**
     * Specifies how close two ActivePanes have to be to each other to fire
     * {@code CollisionEvent#PROXIMITY_ENTERED} events. When set to 0, the
     * TouchPane won't fire {@code CollisionEvent#PROXIMITY_ENTERED} events at
     * all. {@code CollisionEvent#PROXIMITY_LEFT} events will still be fired for
     * any ActionGroup pair that entered each other's proximity before the
     * threshold was set to 0. When set to a negative value, an
     * IllegalArgumentException is thrown.
     *
     * @defaultvalue 25.0
     */
    public final DoubleProperty proximityThresholdProperty() {
        return physics.getQuadTree().proximityThresholdProperty();
    }
    
    // INSTANCE METHODS
    
    public void register(Node... nodes) {
        for (Node node: nodes) {
            TactilePane oldPane = getTracker(node);
            if (oldPane != null) {
                oldPane.deregister(node);
            }
            physics.startTracking(node);
            setConstraint(node, TRACKER, this);
        }
    }
    
    public void deregister(Node... nodes) {
        for (Node node: nodes) {
            physics.stopTracking(node);
            setConstraint(node, TRACKER, null);
        }
    }
    
    // STYLESHEET HANDLING
    
    // The selector class
    private static String DEFAULT_STYLE_CLASS = "tactile-pane";
    // TODO PseudoClasses maken
    
    private static final class StyleableProperties {
        // TODO CSSMetaData maken voor properties

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
        static {
                final List<CssMetaData<? extends Styleable, ?>> styleables = 
                    new ArrayList<>(Control.getClassCssMetaData());

                STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }
    
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Skin<TactilePane> createDefaultSkin() {
        return new TactilePaneSkin(this);
    }
}
