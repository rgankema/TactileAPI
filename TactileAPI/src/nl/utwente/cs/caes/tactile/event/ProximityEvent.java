package nl.utwente.cs.caes.tactile.event;

import nl.utwente.cs.caes.tactile.ActionGroup;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Bounds;

public class ProximityEvent extends Event {

	private static final long serialVersionUID = 6560316895353707967L;

	public static final EventType<CollisionEvent> PROXIMITY_ENTERED = new EventType<CollisionEvent>(
			ANY, "PROXIMITY_ENTERED");
	public static final EventType<CollisionEvent> PROXIMITY_LEFT = new EventType<CollisionEvent>(
			ANY, "PROXIMITY_LEFT");

	private ActionGroup group1;
	private ActionGroup group2;
	private double manhattanDistance;

	public ProximityEvent(EventType<ProximityEvent> eventType, ActionGroup group1, ActionGroup group2) {
		super(eventType);
		this.group1 = group1;
		this.group2 = group2;
		
		// Calculate Manhattan distance
		Bounds bb1 = group1.localToScene(group1.getBoundsInLocal());
		Bounds bb2 = group2.localToScene(group2.getBoundsInLocal());
		
		double xGap, yGap;
		// If group1 is to the left of group2
		if (bb1.getMinX() + bb1.getWidth() / 2 < bb2.getMinX() + bb2.getWidth() / 2) {
			xGap = bb2.getMinX() - bb1.getMinX() + bb1.getWidth();
		} else {
			xGap = bb2.getMinX() + bb2.getWidth() - bb1.getMinX();
		}
		// If group1 is above group2
		if (bb1.getMinY() + bb1.getHeight() /2 < bb2.getMinY() + bb2.getHeight() / 2) {
			yGap = bb2.getMinY() - bb1.getMinY() + bb1.getHeight();
		} else {
			yGap = bb2.getMinY() + bb2.getHeight() - bb1.getMinY();
		}
		
		if(xGap <= 0) { 
			manhattanDistance = yGap;
		} else if (yGap <= 0) {
			manhattanDistance = xGap;
		} else {
			manhattanDistance = Math.min(xGap,  yGap);
		}
	}

	/**
	 * Returns an array of length 2, containing the ActionGroups that triggered
	 * the event.
	 * 
	 * @return
	 */
	public ActionGroup[] getCollidingGroupPair() {
		ActionGroup[] result = { group1, group2 };
		return result;
	}
	
	/**
	 * Returns the Manhattan distance between the two ActionGroups
	 * @return	A double representing the Manhattan distance
	 */
	public double getManhattanDistance() {
		return manhattanDistance;
	}
}
