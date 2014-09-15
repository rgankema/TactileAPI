package nl.utwente.cs.caes.tactile;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class TouchPane extends Pane {
    Physics physics;
    
    public TouchPane() {
        super();
        initialise();
    }

    public TouchPane(Node... children) {
        super(children);
        initialise();
    }

    // Called by all constructors
    private void initialise() {
        physics = new Physics(this);
        physics.start();
    }

    /**
     * Whether {@code DraggableGroups} will collide with the borders of this
     * TouchPane. If set to true the {@code TouchPane} will prevent
     * {@code ActionGroups} that are moving because of user input or physics to
     * move outside of the {@code TouchPane's} boundaries.
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
     * Specifies how close two ActionGroups have to be to each other to fire
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

    /**
     * Registers an ActionGroup to the {@code TouchPane}. The TouchPane will
     * track the position of the ActionGroup and check for collisions /
     * proximity events. The ActionGroup should have the controlled
     * {@code TouchPane} as indirect ancestor, but this is not enforced.
     *
     * @param actionGroup The ActionGroup that is to be tracked
     */
    public void register(ActionGroup actionGroup) {
        physics.track(actionGroup);
    }

    /**
     * Deregisters an ActionGroup from the {@code TouchPane}.
     *
     * @param actionGroup The ActionGroup that shoud be deregistered
     */
    public void deregister(ActionGroup actionGroup) {
        for (ActionGroup ag : actionGroup.getActionGroupsColliding()) {
            ag.getActionGroupsColliding().remove(actionGroup);
        }
        actionGroup.getActionGroupsColliding().clear();
        for (ActionGroup ag : actionGroup.getActionGroupsInProximity()) {
            ag.getActionGroupsInProximity().remove(actionGroup);
        }
        actionGroup.getActionGroupsInProximity().clear();

        physics.stopTracking(actionGroup);
    }
}
