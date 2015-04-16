/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactilefx.event;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Node;

/**
 * Event that occurs when two Nodes collide with each other, or are in
 * each others proximity. In order for these events to be fired, the appropriate
 * Nodes have to be added to a TactilePane's active nodes list.
 *
 * @see nl.utwente.ewi.caes.tactilefx.control.TactilePane#getActiveNodes() TactilePane.getActiveNodes
 * @author Richard
 */
public class TactilePaneEvent extends Event {
    /**
     * Common supertype for all TactilePane event types.
     */
    public static final EventType<TactilePaneEvent> ANY = new EventType<>(
            Event.ANY, "ANY");
    /**
     * This event occurs when a Node enters the bounds of another Node.
     */
    public static final EventType<TactilePaneEvent> AREA_ENTERED = new EventType<>(
            ANY, "AREA_ENTERED");
    /**
     * This event occurs continuously while a Node's bounds intersects
     * with those of another Node.
     */
    public static final EventType<TactilePaneEvent> IN_AREA = new EventType<>(
            ANY, "IN_AREA");
    /**
     * This event occurs when a Node leaves the bounds of another Node.
     */
    public static final EventType<TactilePaneEvent> AREA_LEFT = new EventType<>(
            ANY, "AREA_LEFT");
    /**
     * This event occurs when a Node enters the proximity of another Node.
     */
    public static final EventType<TactilePaneEvent> PROXIMITY_ENTERED = new EventType<>(
            ANY, "PROXIMITY_ENTERED");
    /**
     * This event occurs continuously when a Node is in the proximity of another
     * Node.
     */
    public static final EventType<TactilePaneEvent> IN_PROXIMITY = new EventType<>(
            ANY, "IN_PROXIMITY");
    /**
     * This event occurs when a Node leaves the proximity of another Node.
     */
    public static final EventType<TactilePaneEvent> PROXIMITY_LEFT = new EventType<>(
            ANY, "PROXIMITY_LEFT");

    private Node other;

    public TactilePaneEvent(EventType<TactilePaneEvent> eventType, Node target, Node otherNode) {
        super(eventType);
        this.target = target;
        this.other = otherNode;
    }

    /**
     * Returns the target {@code Node} of this event
     */
    public Node getTarget() {
        return (Node) target;
    }

    /**
     * Returns the other {@code Node} that entered/left the target's proximity,
     * or collided with it.
     */
    public Node getOther() {
        return other;
    }
}
