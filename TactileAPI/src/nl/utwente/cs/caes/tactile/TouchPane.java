package nl.utwente.cs.caes.tactile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.animation.AnimationTimer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import nl.utwente.cs.caes.tactile.event.CollisionEvent;

public class TouchPane extends Pane {
	private Set<ActionGroup> actionGroups = new HashSet<ActionGroup>();
	private QuadTree quadTree;

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
		TouchPane thisPane = this;

		// Add resize listeners (needs optimisation)
		widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(
					ObservableValue<? extends Number> observableValue,
					Number oldWidth, Number newWidth) {
				quadTree = new QuadTree(thisPane
						.localToScene(getBoundsInLocal()));
				for (Node ag : actionGroups) {
					quadTree.insert(ag);
				}
			}
		});

		heightProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(
					ObservableValue<? extends Number> observableValue,
					Number oldHeight, Number newHeight) {
				quadTree = new QuadTree(thisPane
						.localToScene(getBoundsInLocal()));
				for (Node ag : actionGroups) {
					quadTree.insert(ag);
				}
			}
		});

		// Initialise QuadTree
		quadTree = new QuadTree(this.localToScene(getBoundsInLocal()));

		// Create AnimationTimer for collision checking
		new AnimationTimer() {

			@Override
			public void handle(long now) {
				// Update QuadTree
				quadTree.update();

				for (ActionGroup thisObject : actionGroups) {
					Bounds thisBounds = thisObject.localToScene(thisObject.getBoundsInLocal());
					Bounds proximityBounds = null;
					double proximityThreshold = getProximityThreshold();
					if (proximityThreshold > 0) {
						double x = thisBounds.getMinX() - proximityThreshold;
						double y = thisBounds.getMinY() - proximityThreshold;
						double w = thisBounds.getWidth() + proximityThreshold * 2;
						double h = thisBounds.getHeight() + proximityThreshold * 2;
						proximityBounds = new BoundingBox(x, y, w, h);
					}

					List<Node> otherObjects = quadTree.retrieve(thisObject);
					for (Node otherNode : otherObjects) {
						ActionGroup otherObject = (ActionGroup) otherNode;

						if (thisObject == otherObject) {
							continue;
						}

						Bounds otherBounds = otherObject.localToScene(otherObject.getBoundsInLocal());
						
						if (thisBounds.intersects(otherBounds)) {
							if (thisObject.getActionGroupsColliding().add(otherObject)) {
								otherObject.getActionGroupsColliding().add(thisObject);
								
								thisObject.fireEvent(new CollisionEvent(
										CollisionEvent.COLLISION_STARTED,
										thisObject, otherObject));
								otherObject.fireEvent(new CollisionEvent(
										CollisionEvent.COLLISION_STARTED,
										otherObject, thisObject));
							}
						} 
						else {
							if (thisObject.getActionGroupsColliding().remove(otherObject)) {
								otherObject.getActionGroupsColliding().remove(thisObject);
								
								thisObject.fireEvent(new CollisionEvent(
										CollisionEvent.COLLISION_ENDED,
										thisObject, otherObject));
								otherObject.fireEvent(new CollisionEvent(
										CollisionEvent.COLLISION_ENDED,
										otherObject, thisObject));
							}
							if (proximityBounds != null && proximityBounds.intersects(otherBounds)) {
								if (thisObject.getActionGroupsInProximity().add(otherObject)) {
									otherObject.getActionGroupsInProximity().add(thisObject);
									
									thisObject.fireEvent(new CollisionEvent(
											CollisionEvent.PROXIMITY_ENTERED,
											thisObject, otherObject));
									otherObject.fireEvent(new CollisionEvent(
											CollisionEvent.PROXIMITY_ENTERED,
											otherObject, thisObject));
								}	
							}
							else {
								if (thisObject.getActionGroupsInProximity().remove(otherObject)) {
									otherObject.getActionGroupsInProximity().remove(thisObject);
									
									thisObject.fireEvent(new CollisionEvent(
											CollisionEvent.PROXIMITY_LEFT,
											thisObject, otherObject));
									otherObject.fireEvent(new CollisionEvent(
											CollisionEvent.PROXIMITY_LEFT,
											otherObject, thisObject));
								}
							}
						}
					}
				}
			}
		}.start();
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
		return quadTree.proximityThresholdProperty();
	}

	/**
	 * Registers an ActionGroup to the TouchPane. The TouchPane will track the
	 * position of the ActionGroup and check for collisions / proximity events.
	 * The ActionGroup should have this TouchPane as (indirect) ancestor.
	 * 
	 * @param actionGroup
	 *            The ActionGroup that is to be tracked
	 * @throws IllegalArgumentException
	 *             If the ActionGroup does not have this TouchPane as (indirect)
	 *             ancestor
	 */
	public void register(ActionGroup actionGroup) {
		if (actionGroups.add(actionGroup)) {
			Parent ancestor = actionGroup.getParent();
			while (ancestor != this) {
				try {
					ancestor = ancestor.getParent();
				} catch (NullPointerException e) {
					throw new IllegalArgumentException(
							"The provided ActionGroup does not have this TouchPane as ancestor!");
				}
			}
			quadTree.insert(actionGroup);
		}
	}

	/**
	 * Deregisters an ActionGroup from the TouchPane.
	 * 
	 * @param actionGroup
	 *            The ActionGroup that shoud be deregistered
	 */
	public void deregister(ActionGroup actionGroup) {
		actionGroups.remove(actionGroup);
		quadTree.remove(actionGroup);
	}
}
