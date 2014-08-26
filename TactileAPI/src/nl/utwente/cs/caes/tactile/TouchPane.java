package nl.utwente.cs.caes.tactile;

import java.util.IdentityHashMap;
import java.util.Map;

import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

public class TouchPane extends Pane {
	private Map<Bounds, ActionGroup> objectByBounds = new IdentityHashMap<Bounds, ActionGroup>();
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
				for (Bounds bounds : objectByBounds.keySet()) {
					quadTree.insert(bounds);
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
				for (Bounds bounds : objectByBounds.keySet()) {
					quadTree.insert(bounds);
				}
			}
		});

		// Initialise QuadTree
		quadTree = new QuadTree(this.localToScene(getBoundsInLocal()));

		// Create AnimationTimer for collision checking
		new AnimationTimer() {

			@Override
			public void handle(long now) {
				// Update QuadTree, can likely be optimised
				Map<Bounds, ActionGroup> newMap = new IdentityHashMap<Bounds, ActionGroup>();
				for (Bounds oldBounds : objectByBounds.keySet()) {
					ActionGroup actionGroup = objectByBounds.get(oldBounds);
					Bounds newBounds = actionGroup.localToScene(actionGroup
							.getBoundsInLocal());
					quadTree.update(oldBounds, newBounds);
					newMap.put(newBounds, actionGroup);
				}
				objectByBounds = newMap;

				// Check for collisions
				for (Bounds bounds : objectByBounds.keySet()) {
					for (Bounds otherBounds : quadTree.retrieve(bounds)) {
						if (bounds != otherBounds) {
							if (bounds.intersects(otherBounds)) {
								System.out.println("Collision detected");
								// Todo: Fire real event, and also detect proximity
							}
						}
					}
				}
			}
		}.start();
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
		if (!objectByBounds.containsValue(actionGroup)) {
			Parent ancestor = actionGroup.getParent();
			while (ancestor != this) {
				try {
					ancestor = ancestor.getParent();
				} catch (NullPointerException e) {
					throw new IllegalArgumentException(
							"The provided ActionGroup does not have this TouchPane as ancestor!");
				}
			}

			Bounds objectBounds = actionGroup.localToScene(actionGroup
					.getBoundsInLocal());
			objectByBounds.put(objectBounds, actionGroup);
			quadTree.insert(objectBounds);
		}
	}

	/**
	 * Deregisters an ActionGroup from the TouchPane.
	 * 
	 * @param actionGroup
	 *            The ActionGroup that shoud be deregistered
	 */
	public void deregister(ActionGroup actionGroup) {
		Bounds toRemove = null;
		for (Bounds bounds : objectByBounds.keySet()) {
			if (objectByBounds.get(bounds) == actionGroup) {
				toRemove = bounds;
				break;
			}
		}
		objectByBounds.remove(toRemove);
		quadTree.remove(toRemove);
	}
}
