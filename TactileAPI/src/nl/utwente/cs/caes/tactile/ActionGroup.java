package nl.utwente.cs.caes.tactile;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;

public class ActionGroup extends Group {
	private Set<ActionGroup> actionGroupsColliding = new HashSet<ActionGroup>();
	private Set<ActionGroup> actionGroupsCollidingUnmodifiable = Collections.unmodifiableSet(actionGroupsColliding);
	private Set<ActionGroup> actionGroupsInProximity = new HashSet<ActionGroup>();
	private Set<ActionGroup> actionGroupsInProximityUnmodifiable = Collections.unmodifiableSet(actionGroupsColliding);
	
	public ActionGroup() {
		super();
	}
	
	public ActionGroup(Node... nodes){
		super(nodes);
	}


	/**
	 * Finds the first ancestor of this ActionGroup that is a DraggableGroup
	 * 
	 * @return A DraggableGroup that is the first ancestor of this ActionGroup,
	 *         null if there is no such ancestor
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
	
	/**
	 * Requests this {@code ActionGroup} to move away from another
	 * {@code ActionGroup}.
	 * 
	 * @param group
	 *            The {@code ActionGroup} to move away from
	 * @param distance
	 *            The maximum value of the resulting horizontal or vertical gap
	 *            between the two {@code ActionGroups}
	 * @param duration
	 *            How long the animation should play
	 * @throws IllegalArgumentException
	 *             When a negative value is provided for distance
	 */
	public void moveAwayFrom(ActionGroup group, double distance, double duration){
		if (distance < 0) {
			throw new IllegalArgumentException("distance cannot be a negative value!");
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
		if (gapX < distance || gapY < distance) {
			double deltaX, deltaY;
			double maxDeltaX = distance - gapX;
			double maxDeltaY = distance - gapY;
			
			// Calculate the amount of translation needed in X and Y
			if (gapX < gapY) {
				deltaX = distance - gapX;
				if (distanceX < 0) {
					deltaX = -deltaX;
				}
				deltaY = deltaX / ratio;
			} else {
				deltaY = distance - gapY;
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
			
			getDraggableGroupParent().setVector(getDraggableGroupParent().getVector().add(deltaX, deltaY));
		}
	}

	/**
	 * Requests this {@code ActionGroup} to move away from another
	 * {@code ActionGroup}. The animation will take 500ms.
	 * 
	 * @param group
	 *            The {@code ActionGroup} to move away from
	 * @param distance
	 *            The maximum value of the resulting horizontal or vertical gap
	 *            between the two {@code ActionGroups}
	 * @throws IllegalArgumentException
	 *             When a negative value is provided for distance
	 */
	public void moveAwayFrom(ActionGroup group, double distance) {
		moveAwayFrom(group, distance, 500);
	}
	
	/**
	 * Requests this {@code ActionGroup} to move away from another
	 * {@code ActionGroup}. The animation will take 500ms, and the distance will
	 * be equal to the ProximityThreshold of the first ancestor that is a
	 * {@code TouchPane}.
	 * 
	 * @param group
	 *            The {@code ActionGroup} to move away from
	 * @throws IllegalArgumentException
	 *             When a negative value is provided for distance
	 * @throws IllegalStateException
	 *             When this {@ActionGroup} does not have a
	 *             {@TouchPane} as ancestor
	 */
	public void moveAwayFrom(ActionGroup group) {
		Parent ancestor = getDraggableGroupParent();
		while (!(ancestor instanceof TouchPane)) {
			ancestor = ancestor.getParent();
		}
		if (!(ancestor instanceof TouchPane)) {
			throw new IllegalStateException("This ActionGroup does not have a TouchPane as ancestor!");
		}
		
		double distance = ((TouchPane)ancestor).getProximityThreshold();
		moveAwayFrom(group, distance, 500);
	}
	public Set<ActionGroup> getActionGroupsCollidingUnmodifiable() {
		return actionGroupsCollidingUnmodifiable;
	}
	
	public Set<ActionGroup> getActionGroupsInProximityUnmodifiable() {
		return actionGroupsInProximityUnmodifiable;
	}
	
	protected Set<ActionGroup> getActionGroupsColliding(){
		return actionGroupsColliding;
	}
	
	protected Set<ActionGroup> getActionGroupsInProximity(){
		return actionGroupsInProximity;
	}
}
