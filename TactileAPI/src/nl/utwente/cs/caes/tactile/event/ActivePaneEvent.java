package nl.utwente.cs.caes.tactile.event;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import nl.utwente.cs.caes.tactile.ActivePane;

public class ActivePaneEvent extends Event {
	
	private static final long serialVersionUID = -3088628728530387241L;
	
	public static final EventType<ActivePaneEvent> ANY = new EventType<>(
			Event.ANY, "ANY");
	public static final EventType<ActivePaneEvent> AREA_ENTERED = new EventType<>(
			ANY, "AREA_ENTERED");
	public static final EventType<ActivePaneEvent> AREA_LEFT = new EventType<>(
			ANY, "AREA_LEFT");
	public static final EventType<ActivePaneEvent> PROXIMITY_ENTERED = new EventType<>(
			ANY, "PROXIMITY_ENTERED");
	public static final EventType<ActivePaneEvent> PROXIMITY_LEFT = new EventType<>(
			ANY, "PROXIMITY_LEFT");
	public static final EventType<ActivePaneEvent> DROPPED = new EventType<>(
			ANY, "DROPPED");
	
	private ActivePane otherGroup;
		
	public ActivePaneEvent(EventType<ActivePaneEvent> eventType, EventTarget target, ActivePane otherGroup) {
		super(eventType);
		this.target = target;
		this.otherGroup = otherGroup;
	}
	
	/**
	 * Returns the target {@code ActivePane} of this event
	 */
	public ActivePane getTarget() {
		return (ActivePane) target;
	}
	
	/**
	 * Returns the other ActivePane that entered/left the target's proximity, or collided with it.
	 */
	public ActivePane getOtherGroup() {
		return otherGroup;
	}
}
