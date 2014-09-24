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

class Physics {

    protected static final double TIME_STEP = 1d / 60d;
    private static final double BOUNCE = 0.50;
    private static final Point2D LEFT_NORMAL = new Point2D(1, 0);
    private static final Point2D RIGHT_NORMAL = new Point2D(-1, 0);
    private static final Point2D TOP_NORMAL = new Point2D(0, 1);
    private static final Point2D BOTTOM_NORMAL = new Point2D(0, -1);

    private TouchPane pane;
    private AnimationTimer timer;
    Set<ActionGroup> actionGroups;
    QuadTree quadTree;

    Physics(TouchPane pane) {
        this.pane = pane;
        initialise();
    }

    private void initialise() {
        ConcurrentHashMap<ActionGroup, Boolean> map = new ConcurrentHashMap<>();
        actionGroups = Collections.newSetFromMap(map);

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
    
    public boolean track(ActionGroup actionGroup) {
        if (actionGroups.add(actionGroup)) {
            quadTree.insert(actionGroup);
            return true;
        }
        return false;
    }
    
    public boolean stopTracking(ActionGroup actionGroup) {
        if (actionGroups.remove(actionGroup)) {
            quadTree.remove(actionGroup);
            return true;
        }
        return false;
    }

    private void updatePositions() {
        List<DraggableGroup> draggableGroups = new ArrayList<>();
        for (Node child : pane.getChildren()) {
            if (child instanceof DraggableGroup) {
                draggableGroups.add((DraggableGroup) child);
            }
        }
        for (DraggableGroup dg : draggableGroups) {
            dg.setVector(dg.getVector().multiply(0.95));
            if (Math.abs(dg.getVector().getX()) < 1 && Math.abs(dg.getVector().getY()) < 1) {
                dg.setVector(Point2D.ZERO);
            }
            if (!dg.isInUse() && !dg.isAnchored() && !dg.getVector().equals(Point2D.ZERO)) {
                translate(dg, dg.getVector().getX() * TIME_STEP, dg.getVector().getY() * TIME_STEP);
            } else {
                Node anchor = dg.getAnchor();
                if (anchor != null) {
                    Bounds bounds = anchor.localToScene(anchor.getBoundsInLocal());
                    Bounds boundsInPane = pane.sceneToLocal(bounds);
                    Point2D offset = dg.getAnchorOffset();
                    dg.relocate(boundsInPane.getMinX() + offset.getX(), boundsInPane.getMinY() + offset.getY());
                    dg.toFront();
                }
            }
        }
    }

    private void translate(DraggableGroup draggableGroup, double deltaX, double deltaY) {
        if (!pane.isBordersCollide()) {
            draggableGroup.setLayoutX(draggableGroup.getLayoutX() + deltaX);
            draggableGroup.setLayoutY(draggableGroup.getLayoutY() + deltaY);
            return;
        }

        Bounds tpBounds = pane.getBoundsInLocal();
        Bounds dgBounds = draggableGroup.getBoundsInParent();

        double destX = draggableGroup.getLayoutX() + deltaX;
        double destY = draggableGroup.getLayoutY() + deltaY;
        double ratio = deltaX / deltaY;

        Bounds dgDestinationBounds = new BoundingBox(destX, destY, dgBounds.getWidth(), dgBounds.getHeight());

        if (tpBounds.contains(dgDestinationBounds)) {
            draggableGroup.setLayoutX(draggableGroup.getLayoutX() + deltaX);
            draggableGroup.setLayoutY(draggableGroup.getLayoutY() + deltaY);
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
                draggableGroup.setLayoutX(draggableGroup.getLayoutX() + deltaX);
                draggableGroup.setLayoutY(draggableGroup.getLayoutY() + deltaY);
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
            draggableGroup.setLayoutX(draggableGroup.getLayoutX() + deltaX);
            draggableGroup.setLayoutY(draggableGroup.getLayoutY() + deltaY);
            draggableGroup.setVector(vecReflection.multiply(1 / TIME_STEP).multiply(BOUNCE));
            translate(draggableGroup, vecReflection.getX(), vecReflection.getY());

        }
    }

    private void checkCollisions() {
        // Update QuadTree
        quadTree.update();

        for (ActionGroup thisObject : actionGroups) {
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
                ActionGroup otherObject = (ActionGroup) otherNode;

                if (thisObject == otherObject) {
                    continue;
                }

                Bounds otherBounds = otherObject.localToScene(otherObject.getBoundsInLocal());

                if (thisBounds.intersects(otherBounds)) {
                    if (thisObject.getActionGroupsColliding().add(otherObject)) {
                        otherObject.getActionGroupsColliding().add(thisObject);

                        thisObject.fireEvent(new ActionGroupEvent(ActionGroupEvent.AREA_ENTERED, thisObject, otherObject));
                        otherObject.fireEvent(new ActionGroupEvent(ActionGroupEvent.AREA_ENTERED, otherObject, thisObject));
                    }
                } else {
                    if (thisObject.getActionGroupsColliding().remove(otherObject)) {
                        otherObject.getActionGroupsColliding().remove(thisObject);

                        thisObject.fireEvent(new ActionGroupEvent(ActionGroupEvent.AREA_LEFT, thisObject, otherObject));
                        otherObject.fireEvent(new ActionGroupEvent(ActionGroupEvent.AREA_LEFT, otherObject, thisObject));
                    }
                    if (proximityBounds != null && proximityBounds.intersects(otherBounds)) {
                        if (thisObject.getActionGroupsInProximity().add(otherObject)) {
                            otherObject.getActionGroupsInProximity().add(thisObject);

                            thisObject.fireEvent(new ActionGroupEvent(ActionGroupEvent.PROXIMITY_ENTERED, thisObject, otherObject));
                            otherObject.fireEvent(new ActionGroupEvent(ActionGroupEvent.PROXIMITY_ENTERED, otherObject, thisObject));
                        }
                    } else {
                        if (thisObject.getActionGroupsInProximity().remove(otherObject)) {
                            otherObject.getActionGroupsInProximity().remove(thisObject);

                            thisObject.fireEvent(new ActionGroupEvent(ActionGroupEvent.PROXIMITY_LEFT, thisObject, otherObject));
                            otherObject.fireEvent(new ActionGroupEvent(ActionGroupEvent.PROXIMITY_LEFT, otherObject, thisObject));
                        }
                    }
                }
            }
        }
    }
}
