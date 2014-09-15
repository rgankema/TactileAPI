package nl.utwente.cs.caes.tactile.event;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import nl.utwente.cs.caes.tactile.ActionGroup;

public class ActionGroupEvent extends Event {
	
	private static final long serialVersionUID = -3088628728530387241L;
	
	public static final EventType<ActionGroupEvent> ANY = new EventType<>(
			Event.ANY, "ANY");
	public static final EventType<ActionGroupEvent> AREA_ENTERED = new EventType<>(
			ANY, "AREA_ENTERED");
	public static final EventType<ActionGroupEvent> AREA_LEFT = new EventType<>(
			ANY, "AREA_LEFT");
	public static final EventType<ActionGroupEvent> PROXIMITY_ENTERED = new EventType<>(
			ANY, "PROXIMITY_ENTERED");
	public static final EventType<ActionGroupEvent> PROXIMITY_LEFT = new EventType<>(
			ANY, "PROXIMITY_LEFT");
	public static final EventType<ActionGroupEvent> DROPPED = new EventType<>(
			ANY, "DROPPED");
	
	private ActionGroup otherGroup;
		
	public ActionGroupEvent(EventType<ActionGroupEvent> eventType, EventTarget target, ActionGroup otherGroup) {
		super(eventType);
		this.target = target;
		this.otherGroup = otherGroup;
	}
	
	/**
	 * Returns the target {@code ActionGroup} of this event
	 */
	public ActionGroup getTarget() {
		return (ActionGroup) target;
	}
	
	/**
	 * Returns the other ActionGroup that entered/left the target's proximity, or collided with it.
	 */
	public ActionGroup getOtherGroup() {
		return otherGroup;
	}
}
