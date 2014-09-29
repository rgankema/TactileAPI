package nl.utwente.cs.caes.tactile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.DefaultProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import nl.utwente.cs.caes.tactile.skin.TactilePaneSkin;

@DefaultProperty("children")
public class TactilePane extends Control {
    static final String IN_USE = "tactile-pane-in-use";
    static final String ANCHOR = "tactile-pane-anchor";
    static final String VECTOR = "tactile-pane-vector";
    static final String GO_TO_FOREGROUND_ON_CONTACT = "tactile-pane-go-to-foreground-on-contact";
    static final String DRAGGABLE = "tactile-pane-draggable";
    
    static final String TOUCH_DOWN_EVENT_HANDLER = "tactile-pane-touch-down-event-handler";
    static final String TOUCH_MOVED_EVENT_HANDLER = "tactile-pane-touch-moved-event-handler";
    static final String TOUCH_UP_EVENT_HANDLER = "tactile-pane-touch-up-event-handler";
    
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
            for (Node node: c.getRemoved()) {
                // Remove event handlers
            }
            for (Node node: c.getAddedSubList()) {
                // Add event handlers
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
        final Node draggable;
        
        public DragContext(Node draggable) {
            this.draggable = draggable;
        }
    }
    
    private void updateSlide(DragContext dragContext) {
        Node draggable = dragContext.draggable;
        
    	//Calculate change in position
    	double diffX = getLayoutX() - dragContext.prevX;
    	double diffY = getLayoutY() - dragContext.prevY;
    	
    	Point2D deltaVec = new Point2D(diffX , diffY);
    	Point2D newVec = deltaVec.add(getVector(draggable));
    	
    	setVector(draggable, newVec);
    	
    	// Record a delta distance for the drag and drop operation.
        dragContext.prevX = getLayoutX();
        dragContext.prevY = getLayoutY();
		
    }
    
    private void handleTouchDown(DragContext dragContext, double sceneX, double sceneY) {
        Node draggable = dragContext.draggable;
        
        if (isDraggable(draggable)) {
            setAnchor(draggable, null);
            setInUse(draggable, true);
            setVector(draggable, Point2D.ZERO);
                        
            // record the difference between the touch event and the center of the object.
            dragContext.deltaX = getLayoutX() - sceneX;
            dragContext.deltaY = getLayoutY() - sceneY;

            if (this.getParent() == null) {
                return;
            }

            if (isGoToForegroundOnContact(draggable)) {
                this.toFront();
            }
        }
    }

    private void handleTouchMove(DragContext dragContext, double sceneX, double sceneY) {
        Node draggable = dragContext.draggable;
        if (isDraggable(draggable) && !isAnchored(draggable)) {
            if (this.getParent() == null) {
                return;
            }

            double x = sceneX + dragContext.deltaX;
            double y = sceneY + dragContext.deltaY;

            Parent parent = getParent();
            if (isBordersCollide()) {
                Bounds paneBounds = this.getBoundsInLocal();
                Bounds draggableBounds = draggable.getBoundsInLocal();

                if (x < paneBounds.getMinX()) {
                    x = paneBounds.getMinX();
                } else if (x + draggableBounds.getWidth() > paneBounds.getMaxX()) {
                    x = paneBounds.getMaxX() - draggableBounds.getWidth();
                }
                if (y < paneBounds.getMinY()) {
                    y = paneBounds.getMinY();
                } else if (y + draggableBounds.getHeight() > paneBounds.getMaxY()) {
                    y = paneBounds.getMaxY() - draggableBounds.getHeight();
                }
            }

            draggable.relocate(x, y);
        }
    }

    private void handleTouchUp(DragContext dragContext, double sceneX, double sceneY) {
        Node draggable = dragContext.draggable;
        setInUse(draggable, false);
    }
    
    
    // PROPERTIES
    
   /**
     *
     * @return modifiable list of children.
     */
    @Override public ObservableList<Node> getChildren() {
        return super.getChildren();
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
