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
    
    // MouseEvent parameters
    private boolean dragging = false;
    private boolean primaryDown = true;
    private boolean stillSincePress = true;
    
    // TouchEvent parameters
    private EventTarget target;
    private PickResult pickResult;
    private boolean shiftDown;
    private boolean controlDown;
    private boolean altDown;
    private boolean metaDown;
    private double x, y, screenX, screenY;
    private int eventSetId = 1;
    
    // For synthesizing MouseEvents
    private long startTime = -1;
    
    
    
    /**
     * Creates a new MouseToTouchMapper
     */
    public MouseToTouchMapper() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (pressed && !moved) {
                    fireTouchEvent(State.STATIONARY, TouchEvent.TOUCH_STATIONARY);
                }
                moved = false;
                
                if (!dragging && !stillSincePress) {
                    dragging = true;
                    primaryDown = System.currentTimeMillis() - startTime < 500;
                    fireMouseEvent(MouseEvent.MOUSE_PRESSED);
                    fireMouseEvent(MouseEvent.DRAG_DETECTED);
                } else if (dragging) {
                    fireMouseEvent(MouseEvent.MOUSE_DRAGGED);
                }
            }
        };
        timer.start();
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.isSynthesized()) return;
        
        target = event.getTarget();
        pickResult = event.getPickResult();
        shiftDown = event.isShiftDown();
        controlDown = event.isControlDown();
        altDown = event.isAltDown();
        metaDown = event.isMetaDown();
        stillSincePress = event.isStillSincePress();
        
        x = event.getX();
        y = event.getY();
        screenX = event.getScreenX();
        screenY = event.getScreenY();

        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            pressed = true;
            startTime = System.currentTimeMillis();
            fireTouchEvent(State.PRESSED, TouchEvent.TOUCH_PRESSED);
        }
        if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            moved = true;
            fireTouchEvent(State.MOVED, TouchEvent.TOUCH_MOVED);
        }
        if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
            pressed = false;
            startTime = -1;
            fireTouchEvent(State.RELEASED, TouchEvent.TOUCH_PRESSED);
            
            if (event.isStillSincePress()) {
                fireMouseEvent(MouseEvent.MOUSE_CLICKED);
            }
            fireMouseEvent(MouseEvent.MOUSE_RELEASED);
            fireMouseEvent(MouseEvent.MOUSE_CLICKED);
            eventSetId = 1;
        }
        event.consume();
    }

    private void fireTouchEvent(TouchPoint.State state, EventType<TouchEvent> type) {
        TouchPoint tp = new TouchPoint(1, state, x, y, screenX, screenY, target, null);
        List<TouchPoint> tpList = Collections.singletonList(tp);
        
        TouchEvent te = new TouchEvent(type, tp, tpList, eventSetId, shiftDown, controlDown, altDown, metaDown);
        Event.fireEvent(target, te);
    }

    private void fireMouseEvent(EventType<MouseEvent> type) {
        MouseButton button = (primaryDown ? MouseButton.PRIMARY : MouseButton.SECONDARY);
        int clickCount = (type == MouseEvent.MOUSE_MOVED) ? 0 : 1;
        boolean popupTrigger = (type == MouseEvent.MOUSE_CLICKED || type == MouseEvent.MOUSE_RELEASED) && !primaryDown;
        
        MouseEvent me = new MouseEvent(type, x, y, screenX, screenY, button, clickCount, shiftDown, controlDown, 
                altDown, metaDown, primaryDown, false, !primaryDown, true, popupTrigger, stillSincePress, pickResult);
        Event.fireEvent(target, me);
    }
}
