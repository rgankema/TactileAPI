package nl.utwente.cs.caes.tactile;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import nl.utwente.cs.caes.tactile.event.ActionGroupEvent;

public class ActionGroup extends Group {

    private final ConcurrentHashMap<ActionGroup, InvalidationListener> listenerByActionGroup
            = new ConcurrentHashMap<>();

    private final Set<ActionGroup> actionGroupsColliding = new HashSet<>();
    private final Set<ActionGroup> actionGroupsCollidingUnmodifiable = Collections.unmodifiableSet(actionGroupsColliding);
    private final Set<ActionGroup> actionGroupsInProximity = new HashSet<>();
    private final Set<ActionGroup> actionGroupsInProximityUnmodifiable = Collections.unmodifiableSet(actionGroupsColliding);

    public ActionGroup() {
        super();
        initialise();
    }

    public ActionGroup(Node... nodes) {
        super(nodes);
        initialise();
    }

    private void initialise() {
        // Fire Drop Events
        addEventFilter(ActionGroupEvent.AREA_ENTERED, event -> {
            if (event.getTarget() == this && event.getOtherGroup().getDraggableGroupParent().isActive()) {
                InvalidationListener listener = observable -> {
                    if (!event.getOtherGroup().getDraggableGroupParent().isActive()) {
                        if (event.getTarget().getActionGroupsColliding().contains(event.getOtherGroup())) {
                            fireEvent(new ActionGroupEvent(ActionGroupEvent.DROPPED, event.getTarget(), event.getOtherGroup()));
                        }
                    }
                };
                event.getOtherGroup().getDraggableGroupParent().activeProperty().addListener(listener);
                listenerByActionGroup.put(event.getOtherGroup(), listener);
            }
        });

        addEventFilter(ActionGroupEvent.AREA_LEFT, event -> {
            InvalidationListener listener = listenerByActionGroup.remove(event.getOtherGroup());
            if (listener != null) {
                event.getOtherGroup().getDraggableGroupParent().activeProperty().removeListener(listener);
            }
        });
    }

    /**
     * Defines a function to be called when another {@code ActionGroup} enters
     * the proximity of this {@code ActionGroup}
     */
    ObjectProperty<EventHandler<? super ActionGroupEvent>> onProximityEntered;

    public void setOnProximityEntered(EventHandler<? super ActionGroupEvent> eventHandler) {
        onProximityEnteredProperty().set(eventHandler);
    }

    public EventHandler<? super ActionGroupEvent> getOnProximityEntered() {
        return onProximityEnteredProperty().get();
    }

    public ObjectProperty<EventHandler<? super ActionGroupEvent>> onProximityEnteredProperty() {
        if (onProximityEntered == null) {
            onProximityEntered = new SimpleObjectProperty<EventHandler<? super ActionGroupEvent>>() {
                @Override
                public void set(EventHandler<? super ActionGroupEvent> value) {
                    if (getOnProximityEntered() != null) {
                        removeEventHandler(ActionGroupEvent.PROXIMITY_ENTERED, getOnProximityLeft());
                    }
                    addEventHandler(ActionGroupEvent.PROXIMITY_ENTERED, value);
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
    ObjectProperty<EventHandler<? super ActionGroupEvent>> onProximityLeft;

    public void setOnProximityLeft(EventHandler<? super ActionGroupEvent> eventHandler) {
        onProximityLeftProperty().set(eventHandler);
    }

    public EventHandler<? super ActionGroupEvent> getOnProximityLeft() {
        return onProximityLeftProperty().get();
    }

    public ObjectProperty<EventHandler<? super ActionGroupEvent>> onProximityLeftProperty() {
        if (onProximityLeft == null) {
            onProximityLeft = new SimpleObjectProperty<EventHandler<? super ActionGroupEvent>>() {
                @Override
                public void set(EventHandler<? super ActionGroupEvent> value) {
                    if (getOnProximityLeft() != null) {
                        removeEventHandler(ActionGroupEvent.PROXIMITY_LEFT, getOnProximityLeft());
                    }
                    addEventHandler(ActionGroupEvent.PROXIMITY_LEFT, value);
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
    ObjectProperty<EventHandler<? super ActionGroupEvent>> onAreaEntered;

    public void setOnAreaEntered(EventHandler<? super ActionGroupEvent> eventHandler) {
        onAreaEnteredProperty().set(eventHandler);
    }

    public EventHandler<? super ActionGroupEvent> getOnAreaEntered() {
        return onAreaEnteredProperty().get();
    }

    public ObjectProperty<EventHandler<? super ActionGroupEvent>> onAreaEnteredProperty() {
        if (onAreaEntered == null) {
            onAreaEntered = new SimpleObjectProperty<EventHandler<? super ActionGroupEvent>>() {
                @Override
                public void set(EventHandler<? super ActionGroupEvent> value) {
                    if (getOnAreaEntered() != null) {
                        removeEventHandler(ActionGroupEvent.AREA_ENTERED, getOnAreaEntered());
                    }
                    addEventHandler(ActionGroupEvent.AREA_ENTERED, value);
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
    ObjectProperty<EventHandler<? super ActionGroupEvent>> onAreaLeft;

    public void setOnAreaLeft(EventHandler<? super ActionGroupEvent> eventHandler) {
        onAreaLeftProperty().set(eventHandler);
    }

    public EventHandler<? super ActionGroupEvent> getOnAreaLeft() {
        return onProximityLeftProperty().get();
    }

    public ObjectProperty<EventHandler<? super ActionGroupEvent>> onAreaLeftProperty() {
        if (onAreaLeft == null) {
            onAreaLeft = new SimpleObjectProperty<EventHandler<? super ActionGroupEvent>>() {
                @Override
                public void set(EventHandler<? super ActionGroupEvent> value) {
                    if (getOnAreaLeft() != null) {
                        removeEventHandler(ActionGroupEvent.AREA_LEFT, getOnAreaLeft());
                    }
                    addEventHandler(ActionGroupEvent.AREA_LEFT, value);
                    super.set(value);
                }
            };
        }
        return onAreaLeft;
    }
    
    /**
     * Defines a function to be called when another {@code ActionGroup} is dropped
     * in the area of this {@code ActionGroup}
     */
    ObjectProperty<EventHandler<? super ActionGroupEvent>> onDropped;

    public void setOnDropped(EventHandler<? super ActionGroupEvent> eventHandler) {
        onDroppedPropperty().set(eventHandler);
    }

    public EventHandler<? super ActionGroupEvent> getOnDropped() {
        return onDroppedPropperty().get();
    }

    public ObjectProperty<EventHandler<? super ActionGroupEvent>> onDroppedPropperty() {
        if (onDropped == null) {
            onDropped = new SimpleObjectProperty<EventHandler<? super ActionGroupEvent>>() {
                @Override
                public void set(EventHandler<? super ActionGroupEvent> value) {
                    if (getOnDropped() != null) {
                        removeEventHandler(ActionGroupEvent.DROPPED, getOnDropped());
                    }
                    addEventHandler(ActionGroupEvent.DROPPED, value);
                    super.set(value);
                }
            };
        }
        return onDropped;
    }


    /**
     * Finds the first ancestor of this ActionGroup that is a DraggableGroup
     *
     * @return A DraggableGroup that is the first ancestor of this ActionGroup,
     * null if there is no such ancestor
     */
    public final DraggableGroup getDraggableGroupParent() {
        Parent ancestor = getParent();
        while (!(ancestor instanceof DraggableGroup)) {
            ancestor = ancestor.getParent();
        }
        if (!(ancestor instanceof DraggableGroup)) {
            ancestor = null;
        }
        return (DraggableGroup) ancestor;
    }

    public Set<ActionGroup> getActionGroupsCollidingUnmodifiable() {
        return actionGroupsCollidingUnmodifiable;
    }

    public Set<ActionGroup> getActionGroupsInProximityUnmodifiable() {
        return actionGroupsInProximityUnmodifiable;
    }

    protected Set<ActionGroup> getActionGroupsColliding() {
        return actionGroupsColliding;
    }

    protected Set<ActionGroup> getActionGroupsInProximity() {
        return actionGroupsInProximity;
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
    public void moveAwayFrom(ActionGroup group, double force) {
        if (force < 0) {
            throw new IllegalArgumentException("Force cannot be a negative value");
        }
        if (getDraggableGroupParent() == null) {
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

            getDraggableGroupParent().setQueuedVector(new Point2D(deltaX, deltaY));
        }
    }
}
