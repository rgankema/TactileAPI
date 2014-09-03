package nl.utwente.cs.caes.tactile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javafx.animation.AnimationTimer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.shape.Line;
import nl.utwente.cs.caes.tactile.event.ActionGroupEvent;

public class PhysicsController extends AnimationTimer {
	private static final double TIME_STEP = 1d/60d;
	
	private double accumulatedTime;
	private long previousTime = 0;
	
	private Set<ActionGroup> actionGroups;
	private QuadTree quadTree;
	private TouchPane pane;
	
	public PhysicsController(TouchPane pane) {
		this.pane = pane;
		initialise();
	}
	
	private void initialise() {
		ConcurrentHashMap<ActionGroup, Boolean> map = new ConcurrentHashMap<>();
		actionGroups = Collections.newSetFromMap(map);
		
		// Add resize listeners (needs optimisation)
		pane.widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(
					ObservableValue<? extends Number> observableValue,
					Number oldWidth, Number newWidth) {
				quadTree.setBounds(pane.localToScene(pane.getBoundsInLocal()));
			}
		});

		pane.heightProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(
					ObservableValue<? extends Number> observableValue,
					Number oldHeight, Number newHeight) {
				quadTree.setBounds(pane.localToScene(pane.getBoundsInLocal()));
			}
		});

		// Initialise QuadTree
		quadTree = new QuadTree(pane.localToScene(pane.getBoundsInLocal()));
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
	
	@Override
	public void handle(long currentTime) {
		if (previousTime == 0) {
			previousTime = currentTime;
			return;
		}
		
		double secondsEllapsed = (currentTime - previousTime) / 1e9d;
		accumulatedTime += secondsEllapsed;
		previousTime = currentTime;
		
		while(accumulatedTime >= TIME_STEP) {
			updatePositions();
			checkCollisions();
			accumulatedTime -= TIME_STEP;
		}
	}
	
	private void updatePositions() {
		List<DraggableGroup> draggableGroups = new ArrayList<DraggableGroup>();
		for (Node child : pane.getChildren()) {
			if (child instanceof DraggableGroup) {
				if (!((DraggableGroup) child).getVector().equals(Point2D.ZERO)) {
					draggableGroups.add((DraggableGroup)child);
				}
			}
		}
		for (DraggableGroup dg : draggableGroups) {
			dg.setVector(dg.getVector().multiply(0.9));
			if (Math.abs(dg.getVector().getX()) < 0.1 && Math.abs(dg.getVector().getY()) < 0.1) {
				dg.setVector(Point2D.ZERO);
				continue;
			}
			
			translate(dg, dg.getVector().getX() * TIME_STEP, dg.getVector().getY() * TIME_STEP);
		}
	}
	
	private void translate(DraggableGroup draggableGroup, double deltaX, double deltaY) {
		// Het hele gezeik met Bounds blijkt niet goed te werken, dus dit ook niet
		if (!pane.isBordersCollide()) {
			draggableGroup.setLayoutX(draggableGroup.getLayoutX() + deltaX);
			draggableGroup.setLayoutY(draggableGroup.getLayoutY() + deltaY);
			return;
		}
		
		Bounds tpBounds = pane.localToScene(pane.getBoundsInLocal());
		Bounds dgBounds = draggableGroup.localToScene(draggableGroup.getBoundsInLocal());
		
		double destX = dgBounds.getMinX() + deltaX;
		double destY = dgBounds.getMinY() + deltaY;
		double ratio = deltaX / deltaY;
		
		Bounds dgDestinationBounds = new BoundingBox(destX, destY, dgBounds.getWidth(), dgBounds.getHeight());
		
		if (tpBounds.contains(dgDestinationBounds)) {
			draggableGroup.setLayoutX(draggableGroup.getLayoutX() + deltaX);
			draggableGroup.setLayoutY(draggableGroup.getLayoutY() + deltaY);
		} else {
			Point2D vec1 = null, vec2 = null;
			if (deltaX < 0 && destX < tpBounds.getMinX()) {
				deltaX = tpBounds.getMinX() - dgBounds.getMinX();
				deltaY = deltaX / ratio;
				vec1 = new Point2D(deltaX, deltaY);
			}
			else if (deltaX > 0 && destX > tpBounds.getMaxX() - dgBounds.getWidth()) {
				deltaX = tpBounds.getMaxX() - dgBounds.getMaxX();
				deltaY = deltaX / ratio;
				vec1 = new Point2D(deltaX, deltaY);
			}
			if (deltaY < 0 && destY < tpBounds.getMinY()) {
				deltaY = tpBounds.getMinY() - dgBounds.getMinY();
				deltaX = deltaY * ratio;
				vec2 = new Point2D(deltaX, deltaY);
			}
			else if (deltaY > 0 && destY > tpBounds.getMaxY() - dgBounds.getHeight()) {
				deltaY = tpBounds.getMaxY() - dgBounds.getMaxY();
				deltaX = deltaY * ratio;
				vec2 = new Point2D(deltaX, deltaY);
			}
			if (vec1 == null || (vec2 != null && vec1.magnitude() > vec2.magnitude())) {
				deltaX = vec2.getX();
				deltaY = vec2.getY();
			} else {
				deltaX = vec1.getX();
				deltaY = vec1.getY();
			}
			// TODO Reflection vector berekenen
			draggableGroup.setLayoutX(draggableGroup.getLayoutX() + deltaX);
			draggableGroup.setLayoutY(draggableGroup.getLayoutY() + deltaY);
			
		}
		
	}
	
	private void checkCollisions() {
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

						thisObject.fireEvent(new ActionGroupEvent(ActionGroupEvent.AREA_ENTERED, thisObject, otherObject));
						otherObject.fireEvent(new ActionGroupEvent(ActionGroupEvent.AREA_ENTERED, otherObject, thisObject));
					}
				} else {
					if (thisObject.getActionGroupsColliding().remove(otherObject)) {
						otherObject.getActionGroupsColliding().remove(thisObject);

						thisObject.fireEvent(new ActionGroupEvent(ActionGroupEvent.AREA_LEFT, thisObject, otherObject));
						otherObject.fireEvent(new ActionGroupEvent(ActionGroupEvent.AREA_LEFT, otherObject, thisObject));
					}
					if (proximityBounds != null && proximityBounds.intersects(otherBounds)) {
						if (thisObject.getActionGroupsInProximity().add(otherObject)) {
							otherObject.getActionGroupsInProximity().add(thisObject);

							thisObject.fireEvent(new ActionGroupEvent(ActionGroupEvent.PROXIMITY_ENTERED, thisObject, otherObject));
							otherObject.fireEvent(new ActionGroupEvent(ActionGroupEvent.PROXIMITY_ENTERED, otherObject, thisObject));
						}
					} else {
						if (thisObject.getActionGroupsInProximity().remove(otherObject)) {
							otherObject.getActionGroupsInProximity().remove(thisObject);

							thisObject.fireEvent(new ActionGroupEvent(ActionGroupEvent.PROXIMITY_LEFT, thisObject, otherObject));
							otherObject.fireEvent(new ActionGroupEvent(ActionGroupEvent.PROXIMITY_LEFT, otherObject, thisObject));
						}
					}
				}
			}
		}
	}
	
	/**
	 * Registers an ActionGroup to the {@code PhysicsController}. The TouchPane will track the
	 * position of the ActionGroup and check for collisions / proximity events.
	 * The ActionGroup should have the controlled {@code TouchPane} as (indirect) ancestor.
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
			while (ancestor != pane) {
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
	 * Deregisters an ActionGroup from the {@code PhysicsController}.
	 * 
	 * @param actionGroup
	 *            The ActionGroup that shoud be deregistered
	 */
	public void deregister(ActionGroup actionGroup) {
		actionGroups.remove(actionGroup);
		quadTree.remove(actionGroup);
	}
}
