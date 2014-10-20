package nl.utwente.cs.caes.tactile.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
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
import nl.utwente.cs.caes.tactile.event.TactilePaneEvent;
import nl.utwente.cs.caes.tactile.skin.TactilePaneSkin;

@DefaultProperty("children")
public class TactilePane extends Control {
    // Keys for Attached Properties
    static final String IN_USE = "tactile-pane-in-use";
    static final String ANCHOR = "tactile-pane-anchor";
    static final String ANCHOR_OFFSET = "tactile-pane-anchor-offset";
    static final String VECTOR = "tactile-pane-vector";
    static final String GO_TO_FOREGROUND_ON_CONTACT = "tactile-pane-go-to-foreground-on-contact";
    static final String DRAGGABLE = "tactile-pane-draggable";
    static final String SLIDE_ON_RELEASE = "tactile-pane-slide-on-release";
    static final String NODES_COLLIDING = "tactile-pane-nodes-colliding";
    static final String NODES_PROXIMITY = "tactile-pane-nodes-proximity";
    static final String TRACKER = "tactile-pane-tracker";
    static final String ON_PROXIMITY_ENTERED = "tactile-pane-on-proximity-entered";
    static final String ON_PROXIMITY_LEFT = "tactile-pane-on-proximity-left";
    static final String ON_IN_PROXIMITY = "tactile-pane-on-in-proximity";
    static final String ON_AREA_ENTERED = "tactile-pane-on-area-entered";
    static final String ON_AREA_LEFT = "tactile-pane-on-area-left";
    static final String ON_IN_AREA = "tactile-pane-on-in-area";
    
    // Attached Properties that are only used privately
    static final String MOUSE_EVENT_FILTER = "tactile-pane-mouse-event-filter";
    static final String TOUCH_EVENT_HANDLER = "tactile-pane-touch-event-handler";
    static final String MOUSE_EVENT_HANDLER = "tactile-pane-mouse-event-handler";
    
    // IDs to keep track which finger/cursor started dragging a Node
    static final int NULL_ID = -1;
    static final int MOUSE_ID = -2;
    
    // ATTACHED PROPERTIES
    
    static void setInUse(Node node, boolean inUse) {
        inUsePropertyImpl(node).set(inUse);
    }
    
    public static boolean isInUse(Node node) {
        return inUsePropertyImpl(node).get();
    }
    
    static BooleanProperty inUsePropertyImpl(Node node) {
        BooleanProperty property = (BooleanProperty) getConstraint(node, IN_USE);
        if (property == null) {
            property = new SimpleBooleanProperty(false);
            setConstraint(node, IN_USE, property);
        }
        return property;
    }
    
    /**
     * Whether this {@code Node} is being dragged by the user. If the {@code Node}
     * is not a child of a {@code TactilePane}, it will always return {@code false}.
     */
    public static ReadOnlyBooleanProperty inUseProperty(Node node) {
        return inUsePropertyImpl(node);
    }
    
    public static void setAnchor(Node node, Node anchor) {
        anchorProperty(node).set(anchor);
    }
    
    public static Node getAnchor(Node node) {
        return anchorProperty(node).get();
    }
    
    /**
     * The {@code Node} the given {@code node} is anchored to. When a 
     * {@code Node} is anchored to another {@code Node} (called {@code anchor}), it will move
     * wherever the {@code anchor} moves to, provided that both the {@code node}
     * and the {@code anchor} have the same {@code TactilePane} as ancestor. The actual
     * position the anchored {@code Node} will move to is the sum of the position of the
     * {@code anchor} and the {@code anchorOffset}.
     * 
     * When anchored, the {@code node} will not respond to physics. It will however 
     * still respond to user input. When a user tries to drag an anchored {@code Node}, its {@anchor}
     * will automatically be set to {@code null}.
     */
    public static ObjectProperty<Node> anchorProperty(Node node) {
        ObjectProperty<Node> property = (ObjectProperty<Node>) getConstraint(node, ANCHOR);
        if (property == null) {
            property = new SimpleObjectProperty<>(null);
            setConstraint(node, ANCHOR, property);
        }
        return property;
    }
    
    public static void setAnchorOffset(Node node, Point2D offset) {
        anchorOffsetProperty(node).set(offset);
    }
    
    public static Point2D getAnchorOffset(Node node) {
        return anchorOffsetProperty(node).get();
    }
    
    /**
     * Defines the position of this {@code node} relative to its {@anchor}, if it
     * has one.
     */
    public static ObjectProperty<Point2D> anchorOffsetProperty(Node node) {
        ObjectProperty<Point2D> property = (ObjectProperty<Point2D>) getConstraint(node, ANCHOR_OFFSET);
        if (property == null) {
            property = new SimpleObjectProperty<>(Point2D.ZERO);
            setConstraint(node, ANCHOR_OFFSET, property);
        }
        return property;
    }
    
    public static void setVector(Node node, Point2D vector) {
        vectorProperty(node).set(vector);
    }
    
    public static Point2D getVector(Node node) {
        return vectorProperty(node).get();
    }
    
    /**
     * The 2D velocity vector for this {@code node}. Primarily intended for physics.
     */
    public static ObjectProperty<Point2D> vectorProperty(Node node) {
        ObjectProperty<Point2D> property = (ObjectProperty<Point2D>) getConstraint(node, VECTOR);
        if (property == null) {
            property = new SimpleObjectProperty<>(Point2D.ZERO);
            setConstraint(node, VECTOR, property);
        }
        return property;
    }
    
    public static void setGoToForegroundOnContact(Node node, boolean goToForegroundOnContact) {
        goToForegroundOnContactProperty(node).set(goToForegroundOnContact);
    }
    
    public static boolean isGoToForegroundOnContact(Node node) {
        return goToForegroundOnContactProperty(node).get();
    }
    
    /**
     * Whether this {@code node} will go to the foreground when the user starts
     * a drag gesture with it.
     * 
     * @defaultvalue true
     */
    public static BooleanProperty goToForegroundOnContactProperty(Node node) {
        BooleanProperty property = (BooleanProperty) getConstraint(node, GO_TO_FOREGROUND_ON_CONTACT);
        if (property == null) {
            property = new SimpleBooleanProperty(true);
            setConstraint(node, GO_TO_FOREGROUND_ON_CONTACT, property);
        }
        return property;
    }
    
    public static void setDraggable(Node node, boolean draggable) {
        draggableProperty(node).set(draggable);
    }
    
    public static boolean isDraggable(Node node) {
        return draggableProperty(node).get();
    }
    
    /**
     * Whether the given node can be dragged by the user. Only nodes that are a direct child of
     * a {@code TactilePane} can be dragged.
     * 
     * @defaultvalue true
     */
    public static BooleanProperty draggableProperty(Node node) {
        BooleanProperty property = (BooleanProperty) getConstraint(node, DRAGGABLE);
        if (property == null) {
            property = new SimpleBooleanProperty(true) {
                @Override
                public void set(boolean draggable) {
                    if (!draggable) {
                        // A node that is not draggable cannot be in use
                        setInUse(node, false);
                    }
                    super.set(draggable);
                }
            };
            setConstraint(node, DRAGGABLE, property);
        }
        return property;
    }
    
    public static void setSlideOnRelease(Node node, boolean slideOnRelease) {
        slideOnReleaseProperty(node).set(slideOnRelease);
    }
    
    public static boolean isSlideOnRelease(Node node) {
        return slideOnReleaseProperty(node).get();
    }
    
    /**
     * Whether the given {@code Node} will get a vector in the direction it was
     * moving when the user stops dragging that {@code Node}
     *
     * @defaultvalue false
     */
    public static BooleanProperty slideOnReleaseProperty(Node node) {
        BooleanProperty property = (BooleanProperty) getConstraint(node, SLIDE_ON_RELEASE);
        if (property == null) {
            property = new SimpleBooleanProperty(false);
            setConstraint(node, SLIDE_ON_RELEASE, property);
        }
        return property;
    }
    
    /**
     * Returns the set of {@code Nodes} that are registered to the same
     * {@code TactilePane} as the given {@code node}, and are currently
     * colliding with that {@code node}
     */
    public static ObservableSet<Node> getNodesColliding(Node node) {
        ObservableSet<Node> result = (ObservableSet<Node>) getConstraint(node, NODES_COLLIDING);
        if (result == null) {
            result = FXCollections.observableSet(new HashSet<Node>());
            setConstraint(node, NODES_COLLIDING, result);
        }
        return result;
    }
    
    /**
     * Returns the set of {@code Nodes} that are registered to the same
     * {@code TactilePane} as the given {@code node}, and are currently in the
     * proximity of that {@code node}
     */
    public static ObservableSet<Node> getNodesInProximity(Node node) {
        ObservableSet<Node> result = (ObservableSet<Node>) getConstraint(node, NODES_PROXIMITY);
        if (result == null) {
            result = FXCollections.observableSet(new HashSet<Node>());
            setConstraint(node, NODES_PROXIMITY, result);
        }
        return result;
    }
    
    public static void setOnInProximity(Node node, EventHandler<? super TactilePaneEvent> handler) {
        onInProximityProperty(node).set(handler);
    }
    
    public static EventHandler<? super TactilePaneEvent> getOnInProximity(Node node) {
        return onInProximityProperty(node).get();
    }
    
    /**
     * Defines a function to be called continuously when another {@code Node} is
     * in the proximity of this {@code node}.
     */
    public static ObjectProperty<EventHandler<? super TactilePaneEvent>> onInProximityProperty(Node node) {
        ObjectProperty<EventHandler<? super TactilePaneEvent>> property = (ObjectProperty<EventHandler<? super TactilePaneEvent>>) getConstraint(node, ON_IN_PROXIMITY);
        if (property == null) {
            property = new SimpleObjectProperty<EventHandler<? super TactilePaneEvent>>(null) {
                @Override
                public void set(EventHandler<? super TactilePaneEvent> handler) {
                    EventHandler<? super TactilePaneEvent> oldHandler = get();
                    if (oldHandler != null) {
                        node.removeEventHandler(TactilePaneEvent.IN_PROXIMITY, oldHandler);
                    }
                    if (handler != null) {
                        node.addEventHandler(TactilePaneEvent.IN_PROXIMITY, handler);
                    }
                    super.set(handler);
                }
            };
            setConstraint(node, ON_IN_PROXIMITY, property);
        }
        return property;
    }
    
    public static void setOnProximityEntered(Node node, EventHandler<? super TactilePaneEvent> handler) {
        onProximityEnteredProperty(node).set(handler);
    }
    
    public static EventHandler<? super TactilePaneEvent> getOnProximityEntered(Node node) {
        return onProximityEnteredProperty(node).get();
    }
    
    /**
     * Defines a function to be called when another {@code Node} enters the
     * proximity of this {@code node}.
     */
    public static ObjectProperty<EventHandler<? super TactilePaneEvent>> onProximityEnteredProperty(Node node) {
        ObjectProperty<EventHandler<? super TactilePaneEvent>> property = (ObjectProperty<EventHandler<? super TactilePaneEvent>>) getConstraint(node, ON_PROXIMITY_ENTERED);
        if (property == null) {
            property = new SimpleObjectProperty<EventHandler<? super TactilePaneEvent>>(null) {
                @Override
                public void set(EventHandler<? super TactilePaneEvent> handler) {
                    EventHandler<? super TactilePaneEvent> oldHandler = get();
                    if (oldHandler != null) {
                        node.removeEventHandler(TactilePaneEvent.PROXIMITY_ENTERED, oldHandler);
                    }
                    if (handler != null) {
                        node.addEventHandler(TactilePaneEvent.PROXIMITY_ENTERED, handler);
                    }
                    super.set(handler);
                }
            };
            setConstraint(node, ON_PROXIMITY_ENTERED, property);
        }
        return property;
    }
    
    public static void setOnProximityLeft(Node node, EventHandler<? super TactilePaneEvent> handler) {
        onProximityLeftProperty(node).set(handler);
    }
    
    public static EventHandler<? super TactilePaneEvent> getOnProximityLeft(Node node) {
        return onProximityLeftProperty(node).get();
    }
    
    /**
     * Defines a function to be called when another {@code Node} leaves the
     * proximity of this {@code node}.
     */
    public static ObjectProperty<EventHandler<? super TactilePaneEvent>> onProximityLeftProperty(Node node) {
        ObjectProperty<EventHandler<? super TactilePaneEvent>> property = (ObjectProperty<EventHandler<? super TactilePaneEvent>>) getConstraint(node, ON_PROXIMITY_LEFT);
        if (property == null) {
            property = new SimpleObjectProperty<EventHandler<? super TactilePaneEvent>>(null) {
                @Override
                public void set(EventHandler<? super TactilePaneEvent> handler) {
                    EventHandler<? super TactilePaneEvent> oldHandler = get();
                    if (oldHandler != null) {
                        node.removeEventHandler(TactilePaneEvent.PROXIMITY_LEFT, oldHandler);
                    }
                    if (handler != null) {
                        node.addEventHandler(TactilePaneEvent.PROXIMITY_LEFT, handler);
                    }
                    super.set(handler);
                }
            };
            setConstraint(node, ON_PROXIMITY_LEFT, property);
        }
        return property;
    }
    
    public static void setOnInArea(Node node, EventHandler<? super TactilePaneEvent> handler) {
        onInAreaProperty(node).set(handler);
    }
    
    public static EventHandler<? super TactilePaneEvent> getOnInArea(Node node) {
        return onInAreaProperty(node).get();
    }
    
    /**
     * Defines a function to be called continuously when another {@code Node} is
     * in the bounds of this {@code node}.
     */
    public static ObjectProperty<EventHandler<? super TactilePaneEvent>> onInAreaProperty(Node node) {
        ObjectProperty<EventHandler<? super TactilePaneEvent>> property = (ObjectProperty<EventHandler<? super TactilePaneEvent>>) getConstraint(node, ON_IN_AREA);
        if (property == null) {
            property = new SimpleObjectProperty<EventHandler<? super TactilePaneEvent>>(null) {
                @Override
                public void set(EventHandler<? super TactilePaneEvent> handler) {
                    EventHandler<? super TactilePaneEvent> oldHandler = get();
                    if (oldHandler != null) {
                        node.removeEventHandler(TactilePaneEvent.IN_AREA, oldHandler);
                    }
                    if (handler != null) {
                        node.addEventHandler(TactilePaneEvent.IN_AREA, handler);
                    }
                    super.set(handler);
                }
            };
            setConstraint(node, ON_IN_AREA, property);
        }
        return property;
    }
    
    public static void setOnAreaEntered(Node node, EventHandler<? super TactilePaneEvent> handler) {
        onAreaEnteredProperty(node).set(handler);
    }
    
    public static EventHandler<? super TactilePaneEvent> getOnAreaEntered(Node node) {
        return onAreaEnteredProperty(node).get();
    }
    
    /**
     * Defines a function to be called when another {@code Node} enters the
     * bounds of this {@code node}.
     */
    public static ObjectProperty<EventHandler<? super TactilePaneEvent>> onAreaEnteredProperty(Node node) {
        ObjectProperty<EventHandler<? super TactilePaneEvent>> property = (ObjectProperty<EventHandler<? super TactilePaneEvent>>) getConstraint(node, ON_AREA_ENTERED);
        if (property == null) {
            property = new SimpleObjectProperty<EventHandler<? super TactilePaneEvent>>(null) {
                @Override
                public void set(EventHandler<? super TactilePaneEvent> handler) {
                    EventHandler<? super TactilePaneEvent> oldHandler = get();
                    if (oldHandler != null) {
                        node.removeEventHandler(TactilePaneEvent.AREA_ENTERED, oldHandler);
                    }
                    if (handler != null) {
                        node.addEventHandler(TactilePaneEvent.AREA_ENTERED, handler);
                    }
                    super.set(handler);
                }
            };
            setConstraint(node, ON_AREA_ENTERED, property);
        }
        return property;
    }
    
    public static void setOnAreaLeft(Node node, EventHandler<? super TactilePaneEvent> handler) {
        onAreaLeftProperty(node).set(handler);
    }
    
    public static EventHandler<? super TactilePaneEvent> getOnAreaLeft(Node node) {
        return onAreaLeftProperty(node).get();
    }
    
    /**
     * Defines a function to be called when another {@code Node} leaves the
     * bounds of this {@code node}.
     */
    public static ObjectProperty<EventHandler<? super TactilePaneEvent>> onAreaLeftProperty(Node node) {
        ObjectProperty<EventHandler<? super TactilePaneEvent>> property = (ObjectProperty<EventHandler<? super TactilePaneEvent>>) getConstraint(node, ON_AREA_LEFT);
        if (property == null) {
            property = new SimpleObjectProperty<EventHandler<? super TactilePaneEvent>>(null) {
                @Override
                public void set(EventHandler<? super TactilePaneEvent> handler) {
                    EventHandler<? super TactilePaneEvent> oldHandler = get();
                    if (oldHandler != null) {
                        node.removeEventHandler(TactilePaneEvent.AREA_LEFT, oldHandler);
                    }
                    if (handler != null) {
                        node.addEventHandler(TactilePaneEvent.AREA_LEFT, handler);
                    }
                    super.set(handler);
                }
            };
            setConstraint(node, ON_AREA_LEFT, property);
        }
        return property;
    }
    
    /**
     * Calls {@code register} on the given TactilePane with {@code node} as
     * argument. If {@code tactilePane} is {@code null}, {@code node} will be
     * deregistered at its previous {@code TactilePane}, if one exists.
     */
    public static void setTracker(Node node, TactilePane tactilePane) {
        if (tactilePane == null) {
            TactilePane oldPane = getTracker(node);
            if (oldPane != null) {
                oldPane.getActiveNodes().remove(node);
            }
        } else {
            tactilePane.getActiveNodes().add(node);
        }
    }
    
    /**
     * The {@code TactilePane} which is currently tracking {@code node}.
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
    
    // STATIC METHODS
    
    public static void moveAwayFrom(Node move, Node from, double force) {
        if (move.getParent() == null) {
            return;
        }
        
        Node moveDraggable = move;
        while(!(moveDraggable.getParent() instanceof TactilePane)) {
            moveDraggable = moveDraggable.getParent();
            
            if (move.getParent() == null) {
                return;
            }
        }

        Bounds moveBounds = move.localToScene(move.getBoundsInLocal());
        Bounds fromBounds = from.localToScene(from.getBoundsInLocal());

        double moveX = moveBounds.getMinX() + moveBounds.getWidth() / 2;
        double moveY = moveBounds.getMinY() + moveBounds.getHeight() / 2;
        double fromX = fromBounds.getMinX() + moveBounds.getWidth() / 2;
        double fromY = fromBounds.getMinY() + moveBounds.getHeight() / 2;

        double distanceX = moveX - fromX;
        double distanceY = moveY - fromY;

        Point2D vector = new Point2D(distanceX, distanceY).normalize().multiply(force);
        TactilePane.setVector(moveDraggable, TactilePane.getVector(move).add(vector));
    }
    
    /**
     * Moves two nodes away from each other with the default level of force.
     * @param move - node moving away
     * @param from - Node move is moving away from
     */
    public static void moveAwayFrom(Node move, Node from) {
    	moveAwayFrom(move, from, PhysicsTimer.DEFAULT_FORCE);
    }
    
    // INSTANCE VARIABLES
    private final PhysicsTimer physics;
    final QuadTree quadTree;
    private final ObservableSet<Node> activeNodes;
    
    // CONSTRUCTORS
    
    public TactilePane() {
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);
        // Since this Control is more or less a Pane, focusTraversable should be false by default
        ((StyleableProperty<Boolean>)focusTraversableProperty()).applyStyle(null, false);
        
        getChildren().addListener((ListChangeListener.Change<? extends Node> c) -> {
            c.next();
            for (Node node: c.getRemoved()) {
                // Delay removal of drag event handlers, just in case all that
                // happened is a node.toFront() call.
                TimerTask removeDragEventHandlers = new TimerTask() {
                    @Override
                    public void run() {
                        if (node.getParent() != TactilePane.this) {
                            removeDragEventHandlers(node);
                        }
                    }
                };
                Timer timer = new Timer();
                timer.schedule(removeDragEventHandlers, 500);
            }
            for (Node node: c.getAddedSubList()) {
                addDragEventHandlers(node);
            }
        });
        
        // Initialise quadTree
        quadTree = new QuadTree(this.localToScene(this.getBoundsInLocal()));
        
        this.widthProperty().addListener((observableValue, oldWidth, newWidth) -> {
            quadTree.setBounds(this.localToScene(this.getBoundsInLocal()));
        });

        this.heightProperty().addListener((observableValue, oldHeight, newHeight) -> {
            quadTree.setBounds(this.localToScene(this.getBoundsInLocal()));
        });
        
        activeNodes = FXCollections.observableSet(Collections.newSetFromMap(new ConcurrentHashMap<>()));
        activeNodes.addListener((SetChangeListener.Change<? extends Node> change) -> {
            if (change.wasAdded()) {
                Node node = change.getElementAdded();
                TactilePane oldPane = getTracker(node);
                if (oldPane != null) {
                    oldPane.getActiveNodes().remove(node);
                }
                quadTree.insert(node);
                setConstraint(node, TRACKER, TactilePane.this);
            }
            else {
                Node node = change.getElementRemoved();
                quadTree.insert(node);
                TactilePane.getNodesColliding(node).clear();
                TactilePane.getNodesInProximity(node).clear();
                setConstraint(node, TRACKER, null);
            }
        });
        
        physics = new PhysicsTimer(this);
        physics.start();
    }
    
    public TactilePane(Node... children) {
        this();
        getChildren().addAll(children);
    }
    
    // MAKING CHILDREN DRAGGABLE
    
    // Help class used for dragging Nodes
    private class DragContext {
        final Node draggable;   // The Node that is dragged around
        double localX, localY;  // The x,y position of the Event in the Node
        int touchId;            // The id of the finger/cursor that is currently dragging the Node
        
        public DragContext(Node draggable) {
            this.draggable = draggable;
            touchId = -1;
        }
        
        @Override
        public String toString() {
            return String.format("DragContext [draggable = %s, ,touchId = %d, localX = %f, localY = %f]", draggable.toString(), touchId, localX, localY);
        }
    }
    
    private void addDragEventHandlers(Node node) {
        if (getConstraint(node, MOUSE_EVENT_FILTER) != null) {
            // The node already has drag event handlers
            return;
        }
        
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
    
    
    // INSTANCE PROPERTIES
    
   /**
     *
     * @return modifiable list of children.
     */
    @Override public ObservableList<Node> getChildren() {
        return super.getChildren();
    }
    
    public ObservableSet<Node> getActiveNodes() {
        return activeNodes;
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
     * Specifies how close two {@code Nodes} have to be to each other to be
     * considered in each others proximity. When set to 0, TactilePane won't fire
     * {@code PROXIMITY_ENTERED} or {@code IN_PROXIMITY} events at all.
     * {@code PROXIMITY_LEFT} events will still be fired for any pair of
     * {@code Nodes} that entered each other's proximity before the threshold
     * was set to 0. When set to a negative value, an IllegalArgumentException
     * is thrown.
     *
     * @defaultvalue 25.0
     */
    public final DoubleProperty proximityThresholdProperty() {
        return quadTree.proximityThresholdProperty();
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
