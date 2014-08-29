package nl.utwente.cs.caes.tactile.event;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import nl.utwente.cs.caes.tactile.ActionGroup;

public class CollisionEvent extends Event {
	
	private static final long serialVersionUID = -3088628728530387241L;
	
	public static final EventType<CollisionEvent> ANY = new EventType<CollisionEvent>(
			Event.ANY, "ANY");
	public static final EventType<CollisionEvent> COLLISION_STARTED = new EventType<CollisionEvent>(
			ANY, "COLLISION_STARTED");
	public static final EventType<CollisionEvent> COLLISION_ENDED = new EventType<CollisionEvent>(
			ANY, "COLLISION_ENDED");
	public static final EventType<CollisionEvent> PROXIMITY_ENTERED = new EventType<CollisionEvent>(
			ANY, "PROXIMITY_ENTERED");
	public static final EventType<CollisionEvent> PROXIMITY_LEFT = new EventType<CollisionEvent>(
			ANY, "PROXIMITY_LEFT");
	
	private ActionGroup otherGroup;
		
	public CollisionEvent(EventType<CollisionEvent> eventType, EventTarget target, ActionGroup otherGroup) {
		super(eventType);
		this.target = target;
		this.otherGroup = otherGroup;
	}
	
	/**
	 * Returns the other ActionGroup that entered/left the target's proximity, or collided with it.
	 * @return
	 */
	public ActionGroup getOtherGroup() {
		return otherGroup;
	}
}
