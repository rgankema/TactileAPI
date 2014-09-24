package nl.utwente.cs.caes.tactile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javafx.beans.DefaultProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import nl.utwente.cs.caes.tactile.skin.ActivePaneSkin;
import nl.utwente.cs.caes.tactile.event.ActivePaneEvent;

@DefaultProperty("content")
public class ActivePane extends Control {
    private final ConcurrentHashMap<ActivePane, InvalidationListener> listenerByActionPane
            = new ConcurrentHashMap<>();

    private final Set<ActivePane> activePanesColliding = new HashSet<>();
    private final Set<ActivePane> activePanesCollidingUnmodifiable = Collections.unmodifiableSet(activePanesColliding);
    private final Set<ActivePane> activePanesInProximity = new HashSet<>();
    private final Set<ActivePane> activePanesInProximityUnmodifiable = Collections.unmodifiableSet(activePanesInProximity);

    // CONSTRUCTORS
    
    public ActivePane() {
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);
        // TODO Not sure if focusTraversable really should be set to false for ActivePane
        ((StyleableProperty<Boolean>)focusTraversableProperty()).applyStyle(null, false); 
    }

    public ActivePane(Node content) {
        this();
        setContent(content);
    }

    // PROPERTIES
    
    /**
     * The node used as the content of this ContentControl.
     */
    private ObjectProperty<Node> content;

    public final void setContent(Node value) {
        contentProperty().set(value);
    }

    public final Node getContent() {
        return content == null ? null : content.get();
    }

    public final ObjectProperty<Node> contentProperty() {
        if (content == null) {
            content = new SimpleObjectProperty<Node>(this, "content") {
                @Override
                public void set(Node content) {
                    Node oldContent = get();
                    if (oldContent != null) {
                        ActivePane.this.getChildren().remove(oldContent);
                    }
                    if (content != null) {
                        ActivePane.this.getChildren().add(content);
                    }
                }
            };
        }
        return content;
    }
    
    /**
     * Defines a function to be called when another {@code ActionGroup} enters
     * the proximity of this {@code ActionGroup}
     */
    ObjectProperty<EventHandler<? super ActivePaneEvent>> onProximityEntered;

    public void setOnProximityEntered(EventHandler<? super ActivePaneEvent> eventHandler) {
        onProximityEnteredProperty().set(eventHandler);
    }

    public EventHandler<? super ActivePaneEvent> getOnProximityEntered() {
        return onProximityEnteredProperty().get();
    }

    public ObjectProperty<EventHandler<? super ActivePaneEvent>> onProximityEnteredProperty() {
        if (onProximityEntered == null) {
            onProximityEntered = new SimpleObjectProperty<EventHandler<? super ActivePaneEvent>>() {
                @Override
                public void set(EventHandler<? super ActivePaneEvent> value) {
                    if (getOnProximityEntered() != null) {
                        removeEventHandler(ActivePaneEvent.PROXIMITY_ENTERED, getOnProximityLeft());
                    }
                    addEventHandler(ActivePaneEvent.PROXIMITY_ENTERED, value);
                    super.set(value);
                }
            };
        }
        return onProximityEntered;
    }

    /**
     * Defines a function to be called when another {@code ActionGroup} leaves
     * the proximity of this {@code ActionGroup}
     */
    ObjectProperty<EventHandler<? super ActivePaneEvent>> onProximityLeft;

    public void setOnProximityLeft(EventHandler<? super ActivePaneEvent> eventHandler) {
        onProximityLeftProperty().set(eventHandler);
    }

    public EventHandler<? super ActivePaneEvent> getOnProximityLeft() {
        return onProximityLeftProperty().get();
    }

    public ObjectProperty<EventHandler<? super ActivePaneEvent>> onProximityLeftProperty() {
        if (onProximityLeft == null) {
            onProximityLeft = new SimpleObjectProperty<EventHandler<? super ActivePaneEvent>>() {
                @Override
                public void set(EventHandler<? super ActivePaneEvent> value) {
                    if (getOnProximityLeft() != null) {
                        removeEventHandler(ActivePaneEvent.PROXIMITY_LEFT, getOnProximityLeft());
                    }
                    addEventHandler(ActivePaneEvent.PROXIMITY_LEFT, value);
                    super.set(value);
                }
            };
        }
        return onProximityLeft;
    }

    /**
     * Defines a function to be called when another {@code ActionGroup} enters
     * the area of this {@code ActionGroup}
     */
    ObjectProperty<EventHandler<? super ActivePaneEvent>> onAreaEntered;

    public void setOnAreaEntered(EventHandler<? super ActivePaneEvent> eventHandler) {
        onAreaEnteredProperty().set(eventHandler);
    }

    public EventHandler<? super ActivePaneEvent> getOnAreaEntered() {
        return onAreaEnteredProperty().get();
    }

    public ObjectProperty<EventHandler<? super ActivePaneEvent>> onAreaEnteredProperty() {
        if (onAreaEntered == null) {
            onAreaEntered = new SimpleObjectProperty<EventHandler<? super ActivePaneEvent>>() {
                @Override
                public void set(EventHandler<? super ActivePaneEvent> value) {
                    if (getOnAreaEntered() != null) {
                        removeEventHandler(ActivePaneEvent.AREA_ENTERED, getOnAreaEntered());
                    }
                    addEventHandler(ActivePaneEvent.AREA_ENTERED, value);
                    super.set(value);
                }
            };
        }
        return onAreaEntered;
    }

    /**
     * Defines a function to be called when another {@code ActionGroup} leaves
     * the area of this {@code ActionGroup}
     */
    ObjectProperty<EventHandler<? super ActivePaneEvent>> onAreaLeft;

    public void setOnAreaLeft(EventHandler<? super ActivePaneEvent> eventHandler) {
        onAreaLeftProperty().set(eventHandler);
    }

    public EventHandler<? super ActivePaneEvent> getOnAreaLeft() {
        return onProximityLeftProperty().get();
    }

    public ObjectProperty<EventHandler<? super ActivePaneEvent>> onAreaLeftProperty() {
        if (onAreaLeft == null) {
            onAreaLeft = new SimpleObjectProperty<EventHandler<? super ActivePaneEvent>>() {
                @Override
                public void set(EventHandler<? super ActivePaneEvent> value) {
                    if (getOnAreaLeft() != null) {
                        removeEventHandler(ActivePaneEvent.AREA_LEFT, getOnAreaLeft());
                    }
                    addEventHandler(ActivePaneEvent.AREA_LEFT, value);
                    super.set(value);
                }
            };
        }
        return onAreaLeft;
    }
    
    // METHODS
    
     /**
     * Finds the first ancestor of this ActivePane that is a DragPane
     */
    // Niet zeker of dit er in moet blijven
    public final DragPane getDragPaneParent() {
        Parent ancestor = getParent();
        while (!(ancestor instanceof DragPane)) {
            ancestor = ancestor.getParent();
        }
        if (!(ancestor instanceof DragPane)) {
            ancestor = null;
        }
        return (DragPane) ancestor;
    }

    public Set<ActivePane> getActivePanesCollidingUnmodifiable() {
        return activePanesCollidingUnmodifiable;
    }

    public Set<ActivePane> getActivePanesInProximityUnmodifiable() {
        return activePanesInProximityUnmodifiable;
    }

    protected Set<ActivePane> getActivePanesColliding() {
        return activePanesColliding;
    }

    protected Set<ActivePane> getActivePanesInProximity() {
        return activePanesInProximity;
    }

    /**
     * Requests this {@code ActionGroup} to move away from another
     * {@code ActionGroup}. This {@code ActionGroup} will be given a vector that
     * will be added to the vector of the first {@code DraggableGroup} that is
     * an ancestor of this {
     *
     * @ActionGroup}. The magnitude of this vector depends on how far away this {
     * @ActionGroup} is from the other {
     * @ActionGroup}, and the value of {@code force}.
     *
     * @param group The {@code ActionGroup} to move away from
     * @param force The higher this number, the greater the magnitude of the
     * vector that will be given to this {@code ActionGroup}
     * @throws IllegalArgumentException When a negative value is provided for
     * force
     */
    public void moveAwayFrom(ActivePane group, double force) {
        // TODO dit moet naar DragPane
        if (force < 0) {
            throw new IllegalArgumentException("Force cannot be a negative value");
        }
        if (getDragPaneParent() == null) {
            return;
        }

        Bounds thisBounds = this.localToScene(this.getBoundsInLocal());
        Bounds otherBounds = group.localToScene(group.getBoundsInLocal());

        double thisX = thisBounds.getMinX() + thisBounds.getWidth() / 2;
        double thisY = thisBounds.getMinY() + thisBounds.getHeight() / 2;
        double otherX = otherBounds.getMinX() + thisBounds.getWidth() / 2;
        double otherY = otherBounds.getMinY() + thisBounds.getHeight() / 2;

        double distanceX = thisX - otherX;
        double distanceY = thisY - otherY;
        double ratio = distanceX / distanceY;

        double gapX, gapY;

        if (distanceX < 0) {
            // If this ActionGroup is to the left of the other
            gapX = otherBounds.getMinX() - thisBounds.getMaxX();
        } else {
            gapX = thisBounds.getMinX() - otherBounds.getMaxX();
        }
        if (distanceY < 0) {
            // If this ActionGroup is above the other
            gapY = otherBounds.getMinY() - thisBounds.getMaxY();
        } else {
            gapY = thisBounds.getMinY() - otherBounds.getMaxY();
        }

        // Only if either the horizontal or vertical distance is smaller than
        // the desired distance between the ActionGroups we need to actually move.
        if (gapX < force || gapY < force) {
            double deltaX, deltaY;
            double maxDeltaX = force - gapX;
            double maxDeltaY = force - gapY;

            // Calculate the amount of translation needed in X and Y
            if (gapX < gapY) {
                deltaX = force - gapX;
                if (distanceX < 0) {
                    deltaX = -deltaX;
                }
                deltaY = deltaX / ratio;
            } else {
                deltaY = force - gapY;
                if (distanceY < 0) {
                    deltaY = -deltaY;
                }
                deltaX = deltaY * ratio;
            }

            // Make sure we don't overshoot
            if (Math.abs(deltaX) > maxDeltaX) {
                deltaX = maxDeltaX;
                if (distanceX < 0) {
                    deltaX = -deltaX;
                }
                deltaY = deltaX / ratio;
            }
            if (Math.abs(deltaY) > maxDeltaY) {
                deltaY = maxDeltaY;
                if (distanceY < 0) {
                    deltaY = -deltaY;
                }
                deltaX = deltaY * ratio;
            }

            getDragPaneParent().setQueuedVector(new Point2D(deltaX, deltaY));
        }
    }
    
    // STYLESHEET HANDLING
    
    // The selector class
    private static String DEFAULT_STYLE_CLASS = "active-pane";
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
    protected Skin<ActivePane> createDefaultSkin() {
        return new ActivePaneSkin(this);
    }
}
