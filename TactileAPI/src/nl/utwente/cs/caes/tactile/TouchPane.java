package nl.utwente.cs.caes.tactile;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

@Deprecated
public class TouchPane extends Pane {
    Physics physics;
    
    public TouchPane() {
        super();
        physics = new Physics(this);
        physics.start();
    }

    public TouchPane(Node... children) {
        this();
        getChildren().addAll(children);
    }

    /**
     * Whether {@code DraggableGroups} will collide with the borders of this
     * TouchPane. If set to true the {@code TouchPane} will prevent
     * {@code DraggableGroups} that are moving because of user input or physics to
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
     * Specifies how close two ActivePanes have to be to each other to fire
     * {@code CollisionEvent#PROXIMITY_ENTERED} events. When set to 0, the
     * TouchPane won't fire {@code CollisionEvent#PROXIMITY_ENTERED} events at
     * all. {@code CollisionEvent#PROXIMITY_LEFT} events will still be fired for
 any ActionGroup pair that entered each other's proximity before the
 threshold was set to 0. When set to a negative value, an
 IllegalArgumentException is thrown.
     *
     * @defaultvalue 25.0
     */
    public final DoubleProperty proximityThresholdProperty() {
        return physics.getQuadTree().proximityThresholdProperty();
    }

    /**
     * Registers an ActivePane to the {@code TouchPane}. The TouchPane will
 track the position of the ActivePane and check for collisions /
 proximity events. The ActivePane should have the controlled
 {@code TouchPane} as indirect ancestor, but this is not enforced.
     *
     * @param activePane The ActivePane that is to be tracked
     */
    public void register(ActivePane activePane) {
        physics.track(activePane);
    }

    /**
     * Deregisters an ActivePane from the {@code TouchPane}.
     *
     * @param activePane The ActivePane that shoud be deregistered
     */
    public void deregister(ActivePane activePane) {
        for (ActivePane ag : activePane.getActivePanesColliding()) {
            ag.getActivePanesColliding().remove(activePane);
        }
        activePane.getActivePanesColliding().clear();
        for (ActivePane ag : activePane.getActivePanesInProximity()) {
            ag.getActivePanesInProximity().remove(activePane);
        }
        activePane.getActivePanesInProximity().clear();

        physics.stopTracking(activePane);
    }
    
    /* Kan gebruikt worden om een willekeurige node te tracken in FXML
    public static void setTracker(Node node, TouchPane touchPane) {
        touchPane.register(activePane);
    }
    
    public static TouchPane getTracker(Node node) {
        return null;
    }
    */
}
