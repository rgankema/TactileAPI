package nl.utwente.cs.caes.tactile.event;

import nl.utwente.cs.caes.tactile.ActionGroup;
import javafx.event.Event;
import javafx.event.EventType;

public class CollisionEvent extends Event {
	
	private static final long serialVersionUID = -3088628728530387241L;
	
	public static final EventType<CollisionEvent> COLLISION_STARTED = new EventType<CollisionEvent>(
			ANY, "COLLISION_STARTED");
	public static final EventType<CollisionEvent> COLLISION_ENDED = new EventType<CollisionEvent>(
			ANY, "COLLISION_ENDED");
	
	private ActionGroup group1;
	private ActionGroup group2;
	
	public CollisionEvent(EventType<CollisionEvent> eventType, ActionGroup group1, ActionGroup group2) {
		super(eventType);
		this.group1 = group1;
		this.group2 = group2;
	}
	
	/**
	 * Returns an array of length 2, containing the ActionGroups that triggered the event.
	 * @return	
	 */
	public ActionGroup[] getCollidingGroupPair() {
		ActionGroup[] result = {group1, group2};
		return result;
	}
}
