/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactilefx.debug;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.TouchPoint;

/**
 * <p>
 * An EventHandler that can be used to test TouchEvents using only a regular
 * mouse. It consumes incoming MouseEvents, and fires equivalent TouchEvents. In
 * contrast to regular TouchEvents, MouseToTouchMappe does not synthesize
 * MouseEvents to go along with the TouchEvents. It is recommended to add this
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
    
    private EventTarget target;
    private PickResult pickResult;
    private boolean shiftDown;
    private boolean controlDown;
    private boolean altDown;
    private boolean metaDown;
    private double x, y, screenX, screenY;
    
    private int eventSetId = 1;
    
    /**
     * Creates a new MouseToTouchMapper
     */
    public MouseToTouchMapper() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (pressed && !moved) {
                    fireStationary();
                }
                moved = false;
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
        
        x = event.getX();
        y = event.getY();
        screenX = event.getScreenX();
        screenY = event.getScreenY();

        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            pressed = true;
            firePressed();
        }
        if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            moved = true;
            fireMoved();
        }
        if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
            pressed = false;
            fireReleased();
        }
        event.consume();
    }

    private void firePressed() {
        TouchPoint tp = createTouchPoint(TouchPoint.State.PRESSED);
        List<TouchPoint> tpList = Collections.singletonList(tp);
        TouchEvent te = new TouchEvent(TouchEvent.TOUCH_PRESSED, tp, tpList, eventSetId, shiftDown, controlDown, altDown, metaDown);
        Event.fireEvent(target, te);
        eventSetId++;
    }

    private void fireStationary() {
        TouchPoint tp = createTouchPoint(TouchPoint.State.STATIONARY);
        List<TouchPoint> tpList = Collections.singletonList(tp);
        TouchEvent te = new TouchEvent(TouchEvent.TOUCH_STATIONARY, tp, tpList, eventSetId, shiftDown, controlDown, altDown, metaDown);
        Event.fireEvent(target, te);
        eventSetId++;
    }

    private void fireMoved() {
        TouchPoint tp = createTouchPoint(TouchPoint.State.MOVED);
        List<TouchPoint> tpList = Collections.singletonList(tp);
        TouchEvent te = new TouchEvent(TouchEvent.TOUCH_MOVED, tp, tpList, eventSetId, shiftDown, controlDown, altDown, metaDown);
        Event.fireEvent(target, te);
        eventSetId++;
    }

    private void fireReleased() {
        TouchPoint tp = createTouchPoint(TouchPoint.State.RELEASED);
        List<TouchPoint> tpList = Collections.singletonList(tp);
        TouchEvent te = new TouchEvent(TouchEvent.TOUCH_RELEASED, tp, tpList, eventSetId, shiftDown, controlDown, altDown, metaDown);
        Event.fireEvent(target, te);
        eventSetId = 1;
    }

    private TouchPoint createTouchPoint(TouchPoint.State state) {
        TouchPoint tp = new TouchPoint(1, state, x, y, screenX, screenY, target, null);
        return tp;
    }
}
