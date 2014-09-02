package nl.utwente.cs.caes.tactile;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class TouchPane extends Pane {
	private PhysicsController physics;
	
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
		physics = new PhysicsController(this);
		physics.start();
	}
	
	/**
	 * Whether {@code DraggableGroups} will collide with the left border of this TouchPane.
	 * If set to true the {@code TouchPane} will prevent {@code ActionGroups} that are moving
	 * because of user input or physics to move farther to the left than x = 0.
	 */
	private BooleanProperty leftBorderCollides;
	
	public final void setLeftBorderCollides(boolean value) {
		leftBorderCollidesProperty().set(value);
	}
	
	public final boolean isLeftBorderCollides() {
		return leftBorderCollidesProperty().get();
	}
	
	public final BooleanProperty leftBorderCollidesProperty() {
		if (leftBorderCollides == null) {
			leftBorderCollides = new SimpleBooleanProperty(false);
		}
		return leftBorderCollides;
	}
	
	/**
	 * Whether {@code DraggableGroups} will collide with the right border of this TouchPane.
	 * If set to true the {@code TouchPane} will prevent {@code ActionGroups} that are moving
	 * because of user input or physics to move farther to the right than x = TouchPane.width -
	 * ActionGroup.width.
	 */
	private BooleanProperty rightBorderCollides;
	
	public final void setRightBorderCollides(boolean value) {
		rightBorderCollidesProperty().set(value);
	}
	
	public final boolean isRightBorderCollides() {
		return rightBorderCollidesProperty().get();
	}
	
	public final BooleanProperty rightBorderCollidesProperty() {
		if (rightBorderCollides == null) {
			rightBorderCollides = new SimpleBooleanProperty(false);
		}
		return rightBorderCollides;
	}
	
	/**
	 * Whether {@code DraggableGroups} will collide with the top border of this TouchPane.
	 * If set to true the {@code TouchPane} will prevent {@code ActionGroups} that are moving
	 * because of user input or physics to move farther to the top than y = 0.
	 */
	private BooleanProperty topBorderCollides;
	
	public final void setTopBorderCollides(boolean value) {
		topBorderCollidesProperty().set(value);
	}
	
	public final boolean isTopBorderCollides() {
		return topBorderCollidesProperty().get();
	}
	
	public final BooleanProperty topBorderCollidesProperty() {
		if (topBorderCollides == null) {
			topBorderCollides = new SimpleBooleanProperty(false);
		}
		return topBorderCollides;
	}
	
	/**
	 * Whether {@code DraggableGroups} will collide with the bottom border of this TouchPane.
	 * If set to true the {@code TouchPane} will prevent {@code ActionGroups} that are moving
	 * because of user input or physics to move farther to the bottom than y = TouchPane.height -
	 * ActionGroup.height.
	 */
	private BooleanProperty bottomBorderCollides;
	
	public final void setBottomBorderCollides(boolean value) {
		bottomBorderCollidesProperty().set(value);
	}
	
	public final boolean isBottomBorderCollides() {
		return bottomBorderCollidesProperty().get();
	}
	
	public final BooleanProperty bottomBorderCollidesProperty() {
		if (bottomBorderCollides == null) {
			bottomBorderCollides = new SimpleBooleanProperty(false);
		}
		return bottomBorderCollides;
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
		return physics.proximityThresholdProperty();
	}

	/**
	 * Registers an ActionGroup to the {@code TouchPane}. The TouchPane will track the
	 * position of the ActionGroup and check for collisions / proximity events.
	 * The ActionGroup should have the {@code TouchPane} as (indirect) ancestor.
	 * 
	 * @param actionGroup
	 *            The ActionGroup that is to be tracked
	 * @throws IllegalArgumentException
	 *             If the ActionGroup does not have this TouchPane as (indirect)
	 *             ancestor
	 */
	public void register(ActionGroup actionGroup) {
		physics.register(actionGroup);
	}
	
	/**
	 * Deregisters an ActionGroup from the {@code TouchPane}.
	 * 
	 * @param actionGroup
	 *            The ActionGroup that should be deregistered
	 */
	public void deregister(ActionGroup actionGroup) {
		physics.deregister(actionGroup);
	}
}
