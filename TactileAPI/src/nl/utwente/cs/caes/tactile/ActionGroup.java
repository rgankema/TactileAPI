package nl.utwente.cs.caes.tactile;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.util.Duration;

public class ActionGroup extends Group {
	private Set<ActionGroup> actionGroupsColliding = new HashSet<ActionGroup>();
	private Set<ActionGroup> actionGroupsCollidingUnmodifiable = Collections.unmodifiableSet(actionGroupsColliding);
	private Set<ActionGroup> actionGroupsInProximity = new HashSet<ActionGroup>();
	private Set<ActionGroup> actionGroupsInProximityUnmodifiable = Collections.unmodifiableSet(actionGroupsColliding);
	
	public ActionGroup() {
		super();
	}

	public ActionGroup(Node... nodes) {
		super(nodes);
	}

	/**
	 * Finds the first ancestor of this ActionGroup that is a DraggableGroup
	 * 
	 * @return A DraggableGroup that is the first ancestor of this ActionGroup,
	 *         null if there is no such ancestor
	 */
	public final DraggableGroup getDraggableGroupParent() {
		Parent ancestor = this;
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
	 * {@code ActionGroup}. The speed with which it will move away depends on
	 * the distance from the other group.
	 * 
	 * @param group
	 *            The {@code ActionGroup} to move away from
	 * @param distance
	 *            The maximum value of the resulting horizontal or vertical gap
	 *            between the two {@code ActionGroups}
	 * @throws IllegalArgumentException
	 *             When a negative value is provided for distance
	 */
	public void moveAwayFrom(ActionGroup group, double distance){
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
			
			// Animate the transition
			TranslateTransition transition = new TranslateTransition(new Duration(500), this.getDraggableGroupParent());
			transition.setByX(deltaX);
			transition.setByY(deltaY);
			transition.setInterpolator(new Interpolator() {
				@Override
				protected double curve(double t) {
					return Math.sqrt(t);
				}
			});
			transition.play();
		}
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
