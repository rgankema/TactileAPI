package nl.utwente.cs.caes.tactile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.animation.AnimationTimer;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import nl.utwente.cs.caes.tactile.skin.TactilePaneSkin;

@DefaultProperty("children")
public class TactilePane extends Control {
    static final String IN_USE = "tactile-pane-in-use";
    static final String ANCHOR = "tactile-pane-anchor";
    static final String VECTOR = "tactile-pane-vector";
    static final String GO_TO_FOREGROUND_ON_CONTACT = "tactile-pane-go-to-foreground-on-contact";
    static final String DRAGGABLE = "tactile-pane-draggable";
    
    static final String MOUSE_EVENT_FILTER = "tactile-pane-mouse-event-filter";
    static final String TOUCH_EVENT_HANDLER = "tactile-pane-touch-event-handler";
    static final String MOUSE_EVENT_HANDLER = "tactile-pane-mouse-event-handler";
    
    private Map<Node, DragContext> contextByChild = new HashMap<>();
    
    // STATIC METHODS
    
    static void setInUse(Node node, boolean inUse) {
        setConstraint(node, IN_USE, inUse);
    }
    
    public static boolean isInUse(Node node) {
        Boolean result = (Boolean) getConstraint(node, IN_USE);
        return result == null ? false : result;
    }
    
    public static void setAnchor(Node node, Node anchor) {
        setConstraint(node, ANCHOR, anchor);
    }
    
    public static Node getAnchor(Node node) {
        return (Node) getConstraint(node, ANCHOR);
    }
    
    public static boolean isAnchored(Node node) {
        return getAnchor(node) != null;
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
    
    // CONSTRUCTORS
    
    public TactilePane() {
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);
        // Since this Control is more or less a Pane, focusTraversable should be false by default
        ((StyleableProperty<Boolean>)focusTraversableProperty()).applyStyle(null, false);
        
        getChildren().addListener((ListChangeListener.Change<? extends Node> c) -> {
            c.next();
                for (Node node: c.getRemoved()) {
                    removeEventHandlers(node);
                }
                for (Node node: c.getAddedSubList()) {
                    addEventHandlers(node);
                }
            
        });
    }
    
    public TactilePane(Node... children) {
        this();
        getChildren().addAll(children);
    }
    
    // MAKING CHILDREN DRAGGABLE
    
    // Help class used for moving
    private class DragContext {
        static final int PAST_FRAMES = 20;
        static final int FORCE_MULT = 50;

        double prevX, prevY;
        double deltaX, deltaY;
        int touchId;
        final Node draggable;
        
        public DragContext(Node draggable) {
            this.draggable = draggable;
            touchId = -1;
        }
    }
    
    private void addEventHandlers(Node node) {
        final DragContext dragContext = new DragContext(node);
        
        EventHandler<MouseEvent> mouseFilter = (MouseEvent event) -> {
            if (isDraggable(node) && event.isSynthesized() && event.getTarget() == node) {
                event.consume();
            }
        };
        
        EventHandler<TouchEvent> touchHandler = (TouchEvent event) -> {
            EventType type = event.getEventType();
            
            if (type == TouchEvent.TOUCH_PRESSED) {
                if (dragContext.touchId == -1) {
                    dragContext.touchId = event.getTouchPoint().getId();
                    handleTouchPressed(dragContext, event.getTouchPoint().getSceneX(), event.getTouchPoint().getSceneY());
                }
            } else if (type == TouchEvent.TOUCH_MOVED) {
                if (dragContext.touchId == event.getTouchPoint().getId()) {
                    handleTouchMoved(dragContext, event.getTouchPoint().getSceneX(), event.getTouchPoint().getSceneY());
                }
            } else if (type == TouchEvent.TOUCH_RELEASED) {
                if (dragContext.touchId == event.getTouchPoint().getId()) {
                    handleTouchReleased(dragContext, event.getTouchPoint().getSceneX(), event.getTouchPoint().getSceneY());
                    dragContext.touchId = -1;
                }
            } else return;
            
            event.consume();
        };
        
        EventHandler<MouseEvent> mouseHandler = (MouseEvent event) -> {
            EventType type = event.getEventType();
            
            if (type == MouseEvent.MOUSE_PRESSED) {
                handleTouchPressed(dragContext, event.getSceneX(), event.getSceneY());
            } else if (type == MouseEvent.MOUSE_DRAGGED) {
                handleTouchMoved(dragContext, event.getSceneX(), event.getSceneY());
            } else if (type == MouseEvent.MOUSE_RELEASED) {
                handleTouchReleased(dragContext, event.getSceneX(), event.getSceneY());
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
    
    private void removeEventHandlers(Node node) {
        EventHandler<MouseEvent> mouseFilter = (EventHandler<MouseEvent>) getConstraint(node, MOUSE_EVENT_FILTER);
        EventHandler<TouchEvent> touchHandler = (EventHandler<TouchEvent>) getConstraint(node, TOUCH_EVENT_HANDLER);
        EventHandler<MouseEvent> mouseHandler = (EventHandler<MouseEvent>) getConstraint(node, MOUSE_EVENT_HANDLER);
        
        node.removeEventFilter(MouseEvent.ANY, mouseFilter);
        node.removeEventHandler(TouchEvent.ANY, touchHandler);
        node.removeEventHandler(MouseEvent.ANY, mouseHandler);
    }
    
    private void handleTouchPressed(DragContext dragContext, double sceneX, double sceneY) {
        Node node = dragContext.draggable;
        if (isDraggable(node)) {
            setAnchor(node, null);
            setInUse(node, true);
            setVector(node, Point2D.ZERO);
            
            dragContext.deltaX = node.getLayoutX() - sceneX;
            dragContext.deltaY = node.getLayoutY() - sceneY;

            if (isGoToForegroundOnContact(node)) {
                node.toFront();
            }
        }
    }

    private void handleTouchMoved(DragContext dragContext, double sceneX, double sceneY) {
        Node node = dragContext.draggable;
        if (isDraggable(node) && !isAnchored(node)) {

            double x = sceneX + dragContext.deltaX;
            double y = sceneY + dragContext.deltaY;

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
            // Not sure why, but using relocate when Node is a Circle doesn't work properly,
            // which is why we're setting layoutX and layoutY manually.
            node.setLayoutX(x);
            node.setLayoutY(y);
        }
    }

    private void handleTouchReleased(DragContext dragContext, double sceneX, double sceneY) {
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
