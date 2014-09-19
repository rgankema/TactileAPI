package nl.utwente.cs.caes.tactile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javafx.animation.AnimationTimer;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import nl.utwente.cs.caes.tactile.event.ActionGroupEvent;
import nl.utwente.cs.caes.tactile.event.ActivePaneEvent;

class Physics {

    private static final double TIME_STEP = 1d / 60d;
    private static final double BOUNCE = 0.50;
    private static final Point2D LEFT_NORMAL = new Point2D(1, 0);
    private static final Point2D RIGHT_NORMAL = new Point2D(-1, 0);
    private static final Point2D TOP_NORMAL = new Point2D(0, 1);
    private static final Point2D BOTTOM_NORMAL = new Point2D(0, -1);

    private TouchPane pane;
    private AnimationTimer timer;
    Set<ActivePane> activePanes;
    QuadTree quadTree;

    Physics(TouchPane pane) {
        this.pane = pane;
        initialise();
    }

    private void initialise() {
        ConcurrentHashMap<ActivePane, Boolean> map = new ConcurrentHashMap<>();
        activePanes = Collections.newSetFromMap(map);

         // Initialise quadTree
        quadTree = new QuadTree(pane.localToScene(pane.getBoundsInLocal()));
        
        // Add resize listeners (needs optimisation)
        pane.widthProperty().addListener((observableValue, oldWidth, newWidth) -> {
            quadTree.setBounds(pane.localToScene(pane.getBoundsInLocal()));
        });

        pane.heightProperty().addListener((observableValue, oldHeight, newHeight) -> {
            quadTree.setBounds(pane.localToScene(pane.getBoundsInLocal()));
        });

        timer = new AnimationTimer() {
            private double accumulatedTime;
            private long previousTime = 0;

            @Override
            public void handle(long currentTime) {
                if (previousTime == 0) {
                    previousTime = currentTime;
                    return;
                }

                double secondsEllapsed = (currentTime - previousTime) / 1e9d;
                accumulatedTime += secondsEllapsed;
                previousTime = currentTime;

                while (accumulatedTime >= TIME_STEP) {
                    updatePositions();
                    checkCollisions();
                    accumulatedTime -= TIME_STEP;
                }
            }
        };
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }
    
    public QuadTree getQuadTree() {
        return quadTree;
    }
    
    public boolean track(ActivePane activePane) {
        if (activePanes.add(activePane)) {
            quadTree.insert(activePane);
            return true;
        }
        return false;
    }
    
    public boolean stopTracking(ActivePane activePane) {
        if (activePanes.remove(activePane)) {
            quadTree.remove(activePane);
            return true;
        }
        return false;
    }

    private void updatePositions() {
        List<DragPane> dragPanes = new ArrayList<>();
        for (Node child : pane.getChildren()) {
            if (child instanceof DragPane) {
                dragPanes.add((DragPane) child);
            }
        }
        for (DragPane dp : dragPanes) {
            dp.setVector(dp.getVector().multiply(0.95));
            if (Math.abs(dp.getVector().getX()) < 1 && Math.abs(dp.getVector().getY()) < 1) {
                dp.setVector(Point2D.ZERO);
            }
            if (!dp.isInUse() && !dp.isAnchored() && !dp.getVector().equals(Point2D.ZERO)) {
                translate(dp, dp.getVector().getX() * TIME_STEP, dp.getVector().getY() * TIME_STEP);
            } else {
                Node anchor = dp.getAnchor();
                if (anchor != null) {
                    Bounds bounds = anchor.localToScene(anchor.getBoundsInLocal());
                    Bounds boundsInPane = pane.sceneToLocal(bounds);
                    Point2D offset = dp.getAnchorOffset();
                    dp.relocate(boundsInPane.getMinX() + offset.getX(), boundsInPane.getMinY() + offset.getY());
                    dp.toFront();
                }
            }
        }
    }

    private void translate(DragPane dragPane, double deltaX, double deltaY) {
        if (!pane.isBordersCollide()) {
            dragPane.setLayoutX(dragPane.getLayoutX() + deltaX);
            dragPane.setLayoutY(dragPane.getLayoutY() + deltaY);
            return;
        }

        Bounds tpBounds = pane.getBoundsInLocal();
        Bounds dgBounds = dragPane.getBoundsInParent();

        double destX = dragPane.getLayoutX() + deltaX;
        double destY = dragPane.getLayoutY() + deltaY;
        double ratio = deltaX / deltaY;

        Bounds dgDestinationBounds = new BoundingBox(destX, destY, dgBounds.getWidth(), dgBounds.getHeight());

        if (tpBounds.contains(dgDestinationBounds)) {
            dragPane.setLayoutX(dragPane.getLayoutX() + deltaX);
            dragPane.setLayoutY(dragPane.getLayoutY() + deltaY);
        } else {
            Point2D vecOriginal = new Point2D(deltaX, deltaY);
            Point2D vecNew = null, vecNew1 = null, vecNew2 = null, vecRest, vecNormal = null, vecReflection;

            Bounds left = new BoundingBox(tpBounds.getMinX() - 1e6d, tpBounds.getMinY() - 1e6d, 1e6d, tpBounds.getHeight() + 2 * 1e6d);
            Bounds right = new BoundingBox(tpBounds.getMaxX(), tpBounds.getMinY() - 1e6d, 1e6d, tpBounds.getHeight() + 2 * 1e6d);
            Bounds top = new BoundingBox(tpBounds.getMinX() - 1e6d, tpBounds.getMinY() - 1e6d, tpBounds.getWidth() + 2 * 1e6d, 1e6d);
            Bounds bottom = new BoundingBox(tpBounds.getMinX() - 1e6d, tpBounds.getMaxY(), tpBounds.getWidth() + 2 * 1e6d, 1e6d);

            if (deltaX < 0 && left.intersects(dgDestinationBounds)) {
                deltaX = tpBounds.getMinX() - dgBounds.getMinX();
                deltaY = deltaX / ratio;
                vecNew1 = new Point2D(deltaX, deltaY);
            } else if (deltaX > 0 && right.intersects(dgDestinationBounds)) {
                deltaX = tpBounds.getMaxX() - dgBounds.getMaxX();
                deltaY = deltaX / ratio;
                vecNew1 = new Point2D(deltaX, deltaY);
            }
            if (deltaY < 0 && top.intersects(dgDestinationBounds)) {
                deltaY = tpBounds.getMinY() - dgBounds.getMinY();
                deltaX = deltaY * ratio;
                vecNew2 = new Point2D(deltaX, deltaY);
            } else if (deltaY > 0 && bottom.intersects(dgDestinationBounds)) {
                deltaY = tpBounds.getMaxY() - dgBounds.getMaxY();
                deltaX = deltaY * ratio;
                vecNew2 = new Point2D(deltaX, deltaY);
            }

            if (vecNew1 == null && vecNew2 == null) {
                dragPane.setLayoutX(dragPane.getLayoutX() + deltaX);
                dragPane.setLayoutY(dragPane.getLayoutY() + deltaY);
                return;
            } else if (vecNew1 == null || (vecNew2 != null && vecNew1.magnitude() > vecNew2.magnitude())) {
                // Would hit top/bottom boundary before left/right
                vecNew = vecNew2;
                vecNormal = (deltaY < 0) ? TOP_NORMAL : BOTTOM_NORMAL;
            } else if (vecNew1 != null) {
                // Would hit left/right boundary before top/bottom
                vecNew = vecNew1;
                vecNormal = (deltaX < 0) ? LEFT_NORMAL : RIGHT_NORMAL;
            }

            vecRest = vecOriginal.subtract(vecNew);
            vecReflection = vecRest.subtract(vecNormal.multiply(2 * vecRest.dotProduct(vecNormal)));
            dragPane.setLayoutX(dragPane.getLayoutX() + deltaX);
            dragPane.setLayoutY(dragPane.getLayoutY() + deltaY);
            dragPane.setVector(vecReflection.multiply(1 / TIME_STEP).multiply(BOUNCE));
            translate(dragPane, vecReflection.getX(), vecReflection.getY());

        }
    }

    private void checkCollisions() {
        // Update QuadTree
        quadTree.update();

        for (ActivePane thisObject : activePanes) {
            Bounds thisBounds = thisObject.localToScene(thisObject.getBoundsInLocal());
            Bounds proximityBounds = null;
            double proximityThreshold = pane.getProximityThreshold();
            if (proximityThreshold > 0) {
                double x = thisBounds.getMinX() - proximityThreshold;
                double y = thisBounds.getMinY() - proximityThreshold;
                double w = thisBounds.getWidth() + proximityThreshold * 2;
                double h = thisBounds.getHeight() + proximityThreshold * 2;
                proximityBounds = new BoundingBox(x, y, w, h);
            }

            List<Node> otherObjects = quadTree.retrieve(thisObject);
            for (Node otherNode : otherObjects) {
                ActivePane otherObject = (ActivePane) otherNode;

                if (thisObject == otherObject) {
                    continue;
                }

                Bounds otherBounds = otherObject.localToScene(otherObject.getBoundsInLocal());

                if (thisBounds.intersects(otherBounds)) {
                    if (thisObject.getActivePanesColliding().add(otherObject)) {
                        otherObject.getActivePanesColliding().add(thisObject);

                        thisObject.fireEvent(new ActivePaneEvent(ActivePaneEvent.AREA_ENTERED, thisObject, otherObject));
                        otherObject.fireEvent(new ActivePaneEvent(ActivePaneEvent.AREA_ENTERED, otherObject, thisObject));
                    }
                } else {
                    if (thisObject.getActivePanesColliding().remove(otherObject)) {
                        otherObject.getActivePanesColliding().remove(thisObject);

                        thisObject.fireEvent(new ActivePaneEvent(ActivePaneEvent.AREA_LEFT, thisObject, otherObject));
                        otherObject.fireEvent(new ActivePaneEvent(ActivePaneEvent.AREA_LEFT, otherObject, thisObject));
                    }
                    if (proximityBounds != null && proximityBounds.intersects(otherBounds)) {
                        if (thisObject.getActivePanesInProximity().add(otherObject)) {
                            otherObject.getActivePanesInProximity().add(thisObject);

                            thisObject.fireEvent(new ActivePaneEvent(ActivePaneEvent.PROXIMITY_ENTERED, thisObject, otherObject));
                            otherObject.fireEvent(new ActivePaneEvent(ActivePaneEvent.PROXIMITY_ENTERED, otherObject, thisObject));
                        }
                    } else {
                        if (thisObject.getActivePanesInProximity().remove(otherObject)) {
                            otherObject.getActivePanesInProximity().remove(thisObject);

                            thisObject.fireEvent(new ActivePaneEvent(ActivePaneEvent.PROXIMITY_LEFT, thisObject, otherObject));
                            otherObject.fireEvent(new ActivePaneEvent(ActivePaneEvent.PROXIMITY_LEFT, otherObject, thisObject));
                        }
                    }
                }
            }
        }
    }
}
