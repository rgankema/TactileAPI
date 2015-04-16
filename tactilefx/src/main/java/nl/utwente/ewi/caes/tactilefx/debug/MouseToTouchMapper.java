/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactilefx.debug;

import java.util.Collections;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.TouchPoint;
import javafx.scene.input.TouchPoint.State;

/**
 * <p>
 * An EventHandler that can be used to test TouchEvents using only a regular
 * mouse. It consumes incoming MouseEvents, and fires equivalent TouchEvents.
 * Like regular TouchEvents, MouseToTouchMappe synthesizes MouseEvents to go
 * along with the TouchEvents. The behavior of the synthesized MouseEvents is
 * based on empirical data from Windows 8.1, and as such it cannot be guaranteed
 * that it mimics other platforms as well. It is recommended to add this object
 * to the Scene as an EventFilter.
 * <p>
 * For example:
 * <pre>
 * {@code
 * Scene scene = new Scene(root);
 * scene.addEventFilter(new MouseToTouchMapper());
 * }</pre>
 * <p>
 * 
 * @author Richard
 */
public final class MouseToTouchMapper implements EventHandler<MouseEvent> {
    
    private boolean pressed = false;
    private boolean moved = false;
    private boolean dragging = false;
    
    // MouseEvent parameters
    private PickResult pickResult;
    private boolean primaryDown = true;
    private boolean stillSincePress = true;
    
    // TouchEvent parameters
    private EventTarget target;
    private boolean shiftDown;
    private boolean controlDown;
    private boolean altDown;
    private boolean metaDown;
    private double x, y, screenX, screenY;
    private int eventSetId = 1;
    
    // To determine MouseEvent.MOUSE_DRAGGED events
    private long startTime = -1;
    private double startScreenX, startScreenY;
    
    /**
     * Creates a new MouseToTouchMapper
     */
    public MouseToTouchMapper() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (pressed) {
                    if (!moved) {
                        // Mouse button prssed but not moving is equivalent to touch stationary
                        fireTouchEvent(State.STATIONARY, TouchEvent.TOUCH_STATIONARY);
                        moved = false;
                    }
                }
            }
        };
        timer.start();
    }

    @Override
    public void handle(MouseEvent event) {
        // Only map real mouse events
        if (event.isSynthesized()) return;
        
        // Store mouse event parameters
        target = event.getTarget();
        x = event.getX();
        y = event.getY();
        screenX = event.getScreenX();
        screenY = event.getScreenY();
        shiftDown = event.isShiftDown();
        controlDown = event.isControlDown();
        altDown = event.isAltDown();
        metaDown = event.isMetaDown();
        pickResult = event.getPickResult();

        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            startScreenX = screenX;
            startScreenY = screenY;
            
            pressed = true;
            stillSincePress = true;
            startTime = System.currentTimeMillis();
            eventSetId = 1;
            
            // Mouse press is equivalent to touch press
            fireTouchEvent(State.PRESSED, TouchEvent.TOUCH_PRESSED);
            // No mouse press is synthesized just yet, need to wait to determine
            // whether the touch event stands for a left or right click
        }
        if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            moved = true;
            // Mouse drag is equivalent to touch move
            fireTouchEvent(State.MOVED, TouchEvent.TOUCH_MOVED);
            // Fire synthesized mouse drag event
            if (dragging) {
                fireMouseEvent(MouseEvent.MOUSE_DRAGGED);
            } else {
                // If the touchpoint has moved further than a certain threshold, its time to synthesize mouse pressed/dragged
                if ((Math.abs(screenX - startScreenX) > 5 || Math.abs(screenY - startScreenY) > 5)) {
                    dragging = true;
                    stillSincePress = false;
                    primaryDown = System.currentTimeMillis() - startTime < 500;
                    // The DRAG_DETECTED event always comes after the first MOUSE_DRAGGED event, which of course comes after a MOUSE_PRESSED event
                    fireMouseEvent(MouseEvent.MOUSE_PRESSED);
                    fireMouseEvent(MouseEvent.MOUSE_DRAGGED);
                    fireMouseEvent(MouseEvent.DRAG_DETECTED);
                }
            }
        }
        if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
            // Mouse release is equivalent to touch release
            fireTouchEvent(State.RELEASED, TouchEvent.TOUCH_RELEASED);
            
            // If there was no dragging, mouse press still has to be synthesized
            if (!dragging) {
                primaryDown = System.currentTimeMillis() - startTime < 500;
                fireMouseEvent(MouseEvent.MOUSE_PRESSED);
            }
            // Synthesize mouse release and mouse click
            fireMouseEvent(MouseEvent.MOUSE_RELEASED);
            fireMouseEvent(MouseEvent.MOUSE_CLICKED);
            
            pressed = false;
            dragging = false;
        }
        event.consume();
    }

    // Fires a touch event based on the current state and the given TouchPoint.State and EventType
    private void fireTouchEvent(State state, EventType<TouchEvent> type) {
        TouchPoint tp = new TouchPoint(1, state, x, y, screenX, screenY, target, null);
        List<TouchPoint> tpList = Collections.singletonList(tp);
        
        TouchEvent te = new TouchEvent(type, tp, tpList, eventSetId, shiftDown, controlDown, altDown, metaDown);
        Event.fireEvent(target, te);
    }

    // Fires a mouse event based on the current state and the given EventType
    private void fireMouseEvent(EventType<MouseEvent> type) {
        MouseButton button = (primaryDown ? MouseButton.PRIMARY : MouseButton.SECONDARY);
        int clickCount = (type == MouseEvent.MOUSE_MOVED) ? 0 : 1;
        boolean popupTrigger = (type == MouseEvent.MOUSE_CLICKED || type == MouseEvent.MOUSE_RELEASED) && !primaryDown;
        
        MouseEvent me = new MouseEvent(type, x, y, screenX, screenY, button, clickCount, shiftDown, controlDown, 
                altDown, metaDown, primaryDown, false, !primaryDown, true, popupTrigger, stillSincePress, pickResult);
        Event.fireEvent(target, me);
    }
}
