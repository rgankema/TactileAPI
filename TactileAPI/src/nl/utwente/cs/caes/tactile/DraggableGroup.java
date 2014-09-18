package nl.utwente.cs.caes.tactile;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;

public class DraggableGroup extends Group {

    private long touchId = -1;

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
            if (event.isSynthesized() && event.getTarget() == DraggableGroup.this) {
                event.consume();
            }
        });

        addEventHandler(TouchEvent.TOUCH_PRESSED, event -> {
            if (touchId == -1) {
                touchId = event.getTouchPoint().getId();
                handleTouchDown(dragContext, event.getTouchPoint().getSceneX(), event.getTouchPoint().getSceneY());
            }
            event.consume();
        });

        addEventHandler(TouchEvent.TOUCH_RELEASED, event -> {
            if (touchId == event.getTouchPoint().getId()) {
                handleTouchUp(dragContext, event.getTouchPoint().getSceneX(), event.getTouchPoint().getSceneY());
                touchId = -1;
            }
            event.consume();
        });

        addEventHandler(TouchEvent.TOUCH_MOVED, event -> {
            if (touchId == event.getTouchPoint().getId()) {
                handleTouchMove(dragContext, event.getTouchPoint().getSceneX(), event.getTouchPoint().getSceneY());
            }
            event.consume();
        });

        addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            handleTouchDown(dragContext, event.getSceneX(), event.getSceneY());
            event.consume();
        });

        addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            handleTouchUp(dragContext, event.getSceneX(), event.getSceneY());
            event.consume();
        });

        addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            handleTouchMove(dragContext, event.getSceneX(), event.getSceneY());
            event.consume();
        });
    }

    private void handleTouchDown(DragContext dragContext, double sceneX, double sceneY) {
        if (!isIgnoreUserInput()) {
            setAnchor(null);
            
            setInUse(true);
            
            setVector(Point2D.ZERO);

            dragContext.pastSpeedsX = new double[DragContext.PAST_FRAMES];
            dragContext.pastSpeedsY = new double[DragContext.PAST_FRAMES];
            dragContext.pastIndex = 0;

            if (this.getParent() == null) {
                return;
            }

            // record a delta distance for the drag and drop operation.
            dragContext.deltaX = getLayoutX() - sceneX;
            dragContext.deltaY = getLayoutY() - sceneY;

            if (isGoToForegroundOnInUse()) {
                this.toFront();
            }
        }
    }

    private void handleTouchMove(DragContext dragContext, double sceneX, double sceneY) {
        if (!isIgnoreUserInput()) {
            if (this.getParent() == null) {
                return;
            }

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
    }

    private void handleTouchUp(DragContext dragContext, double sceneX, double sceneY) {
        if (!isIgnoreUserInput()) {
            if (isSlideOnRelease()) {
                double speedX = 0, speedY = 0;
                for (int i = 0; i < DragContext.PAST_FRAMES && i <= dragContext.pastSpeedsX.length; i++) {
                    speedX += dragContext.pastSpeedsX[i];
                    speedY += dragContext.pastSpeedsY[i];
                }
                speedX = speedX / (double) dragContext.pastSpeedsX.length;
                speedY = speedY / (double) dragContext.pastSpeedsY.length;

                setVector(new Point2D(speedX * DragContext.FORCE_MULT, speedY * DragContext.FORCE_MULT));
            }
            setInUse(false);
        }
    }

    /**
     * Whether this {@code DraggableGroup} is currently being controlled by a
     * user.
     */
    private ReadOnlyBooleanWrapper inUse;

    private void setInUse(boolean value) {
        inUsePropertyImpl().set(value);
    }

    public boolean isInUse() {
        return inUse == null ? false : inUseProperty().get();
    }

    public ReadOnlyBooleanProperty inUseProperty() {
        return inUsePropertyImpl().getReadOnlyProperty();
    }

    private ReadOnlyBooleanWrapper inUsePropertyImpl() {
        if (inUse == null) {
            inUse = new ReadOnlyBooleanWrapper() {
                @Override
                public void set(boolean inUse) {
                    if (inUse) {
                        setVector(Point2D.ZERO);
                    } else {
                        setVector(getVector().add(getQueuedVector()));
                        setQueuedVector(Point2D.ZERO);
                    }
                    super.set(inUse);
                }
            };
        }
        return inUse;
    }
    
    /**
     * The {@code Node} this {@code DraggableGroup} is anchored to. When a 
     * {@code DraggableGroup} is anchored to another {@code Node}, it will move
     * wherever the {@code anchor} moves to, provided that both the {@code DraggableGroup}
     * and the {@code anchor} have the same {@code TouchPane} as ancestor. The actual
     * position the anchored {@DraggableGroup} will move to is the sum of the position of the
     * {@code anchor} and the {@code anchorOffset}.
     * 
     * When anchored, the {@code DraggableGroup} will not respond to physics. It will however 
     * still respond to user input. When a user tries to drag an anchored {@code DraggableGroup}, its {@anchor}
     * will automatically be set to {@code null}. To prevent this from happening,
     * set {@code ignoreUserInput} to {@code true}.
     */
    private ObjectProperty<Node> anchor;
    
    public void setAnchor(Node node) {
        anchorProperty().set(node);
    }
    
    public Node getAnchor() {
        return anchorProperty().get();
    }
    
    public ObjectProperty<Node> anchorProperty() {
        if (anchor == null) {
            anchor = new SimpleObjectProperty<Node>() {
                @Override
                public void set(Node value) {
                    super.set(value);
                    setAnchored(value != null);
                }
            };
        }
        return anchor;
    }
    
    /**
     * Whether {@code anchor} has been set. When {@code true}, the {@code DraggableGroup}
     * will not respond to physics.
     * 
     * @defaultvalue false
     */
    private ReadOnlyBooleanWrapper anchored;
    
    public boolean isAnchored() {
        return anchoredPropertyImpl().get();
    }
    
    private void setAnchored(boolean value) {
        anchoredPropertyImpl().set(value);
    }
    
    public ReadOnlyBooleanProperty anchoredProperty() {
        return anchoredPropertyImpl().getReadOnlyProperty();
    }
    
    private ReadOnlyBooleanWrapper anchoredPropertyImpl() {
        if (anchored == null) {
            anchored = new ReadOnlyBooleanWrapper(getAnchor() != null);
        }
        return anchored;
    }
    
    /**
     * Defines the position of this {@code DraggableGroup} relative to its {@anchor}.
     */
    private ObjectProperty<Point2D> anchorOffset;

    public void setAnchorOffset(Point2D offset) {
        anchorOffsetProperty().set(offset);
    }
    
    public Point2D getAnchorOffset() {
        return anchorOffsetProperty().get();
    }
    
    public ObjectProperty<Point2D> anchorOffsetProperty() {
        if (anchorOffset == null) {
            anchorOffset = new SimpleObjectProperty<Point2D>(Point2D.ZERO) {
                @Override
                public void set(Point2D value) {
                    if (value == null) {
                        super.set(Point2D.ZERO);
                    } else {
                        super.set(value);
                    }
                }
            };
        }
        return anchorOffset;
    }
    
    /**
     * Whether this {@code DraggableGroup} ignores physics.
     *
     * @defaultvalue false
     */
    private BooleanProperty ignorePhysics;

    public void setIgnorePhysics(boolean value) {
        ignorePhysicsProperty().set(value);
    }

    public boolean isIgnorePhysics() {
        return ignorePhysicsProperty().get();
    }

    public BooleanProperty ignorePhysicsProperty() {
        if (ignorePhysics == null) {
            ignorePhysics = new SimpleBooleanProperty(false);
        }
        return ignorePhysics;
    }

    /**
     * Whether this {@code DraggableGroup} ignores touch or mouse input.
     *
     * @defaultvalue false
     */
    private BooleanProperty ignoreUserInput;

    public void setIgnoreUserInput(boolean value) {
        ignoreUserInputProperty().set(value);
    }

    public boolean isIgnoreUserInput() {
        return ignoreUserInputProperty().get();
    }

    public BooleanProperty ignoreUserInputProperty() {
        if (ignoreUserInput == null) {
            ignoreUserInput = new SimpleBooleanProperty(false);
        }
        return ignoreUserInput;
    }

    /**
     * Whether this {@code DraggableGroup} will go to the foreground when
     * {@link #inUseProperty() active} is set to true. If set to true,
     * {@link #goToForeground()} is called whenever {@link #inUseProperty() is
     * set to true.
     */
    private BooleanProperty goToForegroundOnInUse;

    public final void setGoToForegroundOnInUse(boolean value) {
        goToForegroundOnInUseProperty().set(value);
    }

    public final boolean isGoToForegroundOnInUse() {
        return goToForegroundOnInUse == null ? true : goToForegroundOnInUse.get();
    }

    public final BooleanProperty goToForegroundOnInUseProperty() {
        if (goToForegroundOnInUse == null) {
            goToForegroundOnInUse = new SimpleBooleanProperty(true);
        }
        return goToForegroundOnInUse;
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

    public final void setSlideOnRelease(boolean value) {
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
     * The 2D velocity vector for this {@code DraggableGroup}
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
            vector = new SimpleObjectProperty<>(new Point2D(0, 0));
        }
        return vector;
    }

    /**
     * The queued 2D velocity vector for this {@code DraggableGroup}
     */
    private ObjectProperty<Point2D> queuedVector;

    public void setQueuedVector(Point2D value) {
        queuedVectorProperty().set(value);
    }

    public Point2D getQueuedVector() {
        return queuedVectorProperty().get();
    }

    public ObjectProperty<Point2D> queuedVectorProperty() {
        if (queuedVector == null) {
            queuedVector = new SimpleObjectProperty<Point2D>(new Point2D(0, 0)) {
                @Override
                public void set(Point2D value) {
                    if (!isInUse()) {
                        setVector(getVector().add(value));
                    } else {
                        super.set(value);
                    }
                }
            };
        }
        return queuedVector;
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
