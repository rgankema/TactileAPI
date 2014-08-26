package nl.utwente.cs.caes.tactile.event;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import nl.utwente.cs.caes.tactile.ActionGroup;

import javafx.geometry.Point2D;

public class ProximityEvent extends Event {

	private static final long serialVersionUID = 6560316895353707967L;

	public static final EventType<CollisionEvent> PROXIMITY_ENTERED = new EventType<CollisionEvent>(
			ANY, "PROXIMITY_ENTERED");
	public static final EventType<CollisionEvent> PROXIMITY_LEFT = new EventType<CollisionEvent>(
			ANY, "PROXIMITY_LEFT");

	private ActionGroup group1, group2;
	private Point2D group1Center, group2Center;
	private double angle, smallestPositiveGap;

	public ProximityEvent(EventType<ProximityEvent> eventType, ActionGroup group1, ActionGroup group2) {
		super(eventType);
		this.group1 = group1;
		this.group2 = group2;
		
		// Calculate smallest positive gap, center points and angle
		Bounds bb1 = group1.localToScene(group1.getBoundsInLocal());
		Bounds bb2 = group2.localToScene(group2.getBoundsInLocal());
		
		
		group1Center = new Point2D(bb1.getMinX() + bb1.getWidth() / 2, bb1.getMinY() + bb1.getHeight() / 2);
		group2Center = new Point2D(bb2.getMinX() + bb2.getWidth() / 2, bb2.getMinY() + bb2.getHeight() / 2);
		angle = group1Center.angle(group2Center);
		
		double xGap, yGap;
		// If group1 is to the left of group2
		if (group1Center.getX() < group2Center.getX()) {
			xGap = bb2.getMinX() - bb1.getMinX() + bb1.getWidth();
		} else {
			xGap = bb2.getMinX() + bb2.getWidth() - bb1.getMinX();
		}
		// If group1 is above group2
		if (group1Center.getY() < group2Center.getY()) {
			yGap = bb2.getMinY() - bb1.getMinY() + bb1.getHeight();
		} else {
			yGap = bb2.getMinY() + bb2.getHeight() - bb1.getMinY();
		}
		
		if(xGap <= 0) { 
			smallestPositiveGap = yGap;
		} else if (yGap <= 0) {
			smallestPositiveGap = xGap;
		} else {
			smallestPositiveGap = Math.min(xGap,  yGap);
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
	 * Returns the angle between the centerpoints of the two ActionGroups.
	 * @return	The angle between the two groups, in degrees
	 */
	public double getAngle() {
		return angle;
	}
	
	/**
	 * Returns the vertical or horizontal distance between the two ActionGroups' borders,
	 * depending on which is smallest, yet still higher than 0. This can be used to approximate
	 * the distance between the two ActionGroups.
	 * @return	A double representing the smallest positive gap
	 */
	public double getSmallestPostiveGap() {
		// Ik heb hiervoor gekozen omdat precieze afstand niet heel belangrijk is, en dit snel
		// te berekenen is. Weet nog niet zeker of dat de beste keuze is.
		return smallestPositiveGap;
	}
}
