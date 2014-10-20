package nl.utwente.cs.caes.tactile.control;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javafx.animation.AnimationTimer;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import nl.utwente.cs.caes.tactile.event.TactilePaneEvent;

class PhysicsTimer extends AnimationTimer {
    // TODO: misschien een idee om hier properties van te maken
    
    // Length of a time step
    protected static final double TIME_STEP = 1d / 60d;
    // Multiplied with each vector every time step
    private static final double FRICTION = 0.95;
    // Multiplier for a reflection vector
    private static final double BOUNCE = 0.70;
    // Multiplier for the slide vector
    private static final double SLIDE = 1.8;
    // The threshold at which a vector is set to the zero vector
    private static final double THRESHOLD = 2.5;
    // Default value for force
    protected static final double DEFAULT_FORCE = 100;
    
    // Normal vectors
    private static final Point2D LEFT_NORMAL = new Point2D(1, 0);
    private static final Point2D RIGHT_NORMAL = new Point2D(-1, 0);
    private static final Point2D TOP_NORMAL = new Point2D(0, 1);
    private static final Point2D BOTTOM_NORMAL = new Point2D(0, -1);

    private double accumulatedTime;
    private long previousTime = 0;
    
    TactilePane pane;
    ConcurrentHashMap<Node, Point2D> locationByNode;

    PhysicsTimer(TactilePane tactilePane) {
        this.pane = tactilePane;
        
        locationByNode = new ConcurrentHashMap<>();
    }
    
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

    private void updatePositions() {
        for (Node node: pane.getChildren()) {
            if (!TactilePane.isDraggable(node)) {
                continue;
            }
            
            Point2D vector = TactilePane.getVector(node);
            
            // Multiply with FRICTION to model friction
            vector = vector.multiply(FRICTION);
            TactilePane.setVector(node, vector);
            
            // If the resulting vector is small enough, set the vector to zero vector
            if (Math.abs(vector.getX()) < THRESHOLD && Math.abs(vector.getY()) < THRESHOLD) {
                vector = Point2D.ZERO;
                TactilePane.setVector(node, vector);
            }
            
            Node anchor = TactilePane.getAnchor(node);
            // If the node is in use, update its vector for slide behaviour
            if (TactilePane.isInUse(node) && TactilePane.isSlideOnRelease(node)) {
                Point2D prevLocation = locationByNode.get(node);
                
                // Calculate change in position
                double deltaX = node.getLayoutX() - prevLocation.getX();
                double deltaY = node.getLayoutY() - prevLocation.getY();
                
                // Update vector
                Point2D newVector = TactilePane.getVector(node).add(new Point2D(deltaX , deltaY).multiply(SLIDE));
                TactilePane.setVector(node, newVector);
            }
            // If the node is not actively being used and not anchored update the node's position according to vector
            else if (anchor == null && !vector.equals(Point2D.ZERO)) {
                layoutNode(node, vector.multiply(TIME_STEP));
            } 
            // If anchored, update the node's position according to its anchor
            else if (anchor != null) {
                Bounds bounds = anchor.localToScene(anchor.getBoundsInLocal());
                Bounds boundsInPane = pane.sceneToLocal(bounds);
                Point2D offset = TactilePane.getAnchorOffset(node);
                node.relocate(boundsInPane.getMinX() + offset.getX(), boundsInPane.getMinY() + offset.getY());
                node.toFront();
            }
            
            // Record the new location
            locationByNode.put(node, new Point2D(node.getLayoutX(), node.getLayoutY()));
        }
    }

    private void layoutNode(Node node, Point2D delta) {
        double deltaX = delta.getX();
        double deltaY = delta.getY();
        
        if (!pane.isBordersCollide()) {
            node.setLayoutX(node.getLayoutX() + deltaX);
            node.setLayoutY(node.getLayoutY() + deltaY);
            return;
        }

        Bounds paneBounds = pane.getBoundsInLocal();
        Bounds nodeBounds = node.getBoundsInParent();

        // The bounds this node would get when it would be translated by deltaX and deltaY
        Bounds destination = new BoundingBox(nodeBounds.getMinX() + deltaX, nodeBounds.getMinY() + deltaY, nodeBounds.getWidth(), nodeBounds.getHeight());
        
        if (paneBounds.contains(destination)) {
            node.setLayoutX(node.getLayoutX() + deltaX);
            node.setLayoutY(node.getLayoutY() + deltaY);
        } else {
            Point2D vecChopped, vc1 = null, vc2 = null, vecRest, vecNormal, vecReflection;
            double ratio = deltaX / deltaY;
            
            if (deltaX < 0 && destination.getMinX() < paneBounds.getMinX()) {
                deltaX = paneBounds.getMinX() - nodeBounds.getMinX();
                deltaY = deltaX / ratio;
                vc1 = new Point2D(deltaX, deltaY);
            } else if (deltaX > 0 && destination.getMaxX() > paneBounds.getMaxX()) {
                deltaX = paneBounds.getMaxX() - nodeBounds.getMaxX();
                deltaY = deltaX / ratio;
                vc1 = new Point2D(deltaX, deltaY);
            }
            if (deltaY < 0 && destination.getMinY() < paneBounds.getMinY()) {
                deltaY = paneBounds.getMinY() - nodeBounds.getMinY();
                deltaX = deltaY * ratio;
                vc2 = new Point2D(deltaX, deltaY);
            } else if (deltaY > 0 && destination.getMaxY() > paneBounds.getMaxY()) {
                deltaY = paneBounds.getMaxY() - nodeBounds.getMaxY();
                deltaX = deltaY * ratio;
                vc2 = new Point2D(deltaX, deltaY);
            }

            if (vc1 == null && vc2 == null) {
                node.setLayoutX(node.getLayoutX() + deltaX);
                node.setLayoutY(node.getLayoutY() + deltaY);
                return;
            } else if (vc1 == null || (vc2 != null && vc1.magnitude() > vc2.magnitude())) {
                // Would hit top/bottom boundary before left/right
                vecChopped = vc2;
                vecNormal = (deltaY < 0) ? TOP_NORMAL : BOTTOM_NORMAL;
            } else {
                // Would hit left/right boundary before top/bottom
                vecChopped = vc1;
                vecNormal = (deltaX < 0) ? LEFT_NORMAL : RIGHT_NORMAL;
            }

            vecRest = delta.subtract(vecChopped);
            vecReflection = vecRest.subtract(vecNormal.multiply(2 * vecRest.dotProduct(vecNormal)));
            node.setLayoutX(node.getLayoutX() + deltaX);
            node.setLayoutY(node.getLayoutY() + deltaY);
            TactilePane.setVector(node, vecReflection.multiply(1 / TIME_STEP).multiply(BOUNCE));
            layoutNode(node, vecReflection);
        }
    }
    
    private void checkCollisions() {
        // Update QuadTree
        pane.quadTree.update();

        for (Node thisNode : pane.getActiveNodes()) {
            Bounds thisBounds = thisNode.localToScene(thisNode.getBoundsInLocal());
            Bounds proximityBounds = null;
            double proximityThreshold = pane.getProximityThreshold();
            if (proximityThreshold > 0) {
                double x = thisBounds.getMinX() - proximityThreshold;
                double y = thisBounds.getMinY() - proximityThreshold;
                double w = thisBounds.getWidth() + proximityThreshold * 2;
                double h = thisBounds.getHeight() + proximityThreshold * 2;
                proximityBounds = new BoundingBox(x, y, w, h);
            }

            List<Node> otherNodes = pane.quadTree.retrieve(thisNode);
            for (Node otherNode : otherNodes) {
                Bounds otherBounds = otherNode.localToScene(otherNode.getBoundsInLocal());

                if (thisBounds.intersects(otherBounds)) {
                    if (TactilePane.getNodesColliding(thisNode).add(otherNode)) {
                        TactilePane.getNodesColliding(otherNode).add(thisNode);

                        thisNode.fireEvent(new TactilePaneEvent(TactilePaneEvent.AREA_ENTERED, thisNode, otherNode));
                        otherNode.fireEvent(new TactilePaneEvent(TactilePaneEvent.AREA_ENTERED, otherNode, thisNode));
                    }
                    thisNode.fireEvent(new TactilePaneEvent(TactilePaneEvent.IN_AREA, thisNode, otherNode));
                } else {
                    if (TactilePane.getNodesColliding(thisNode).remove(otherNode)) {
                        TactilePane.getNodesColliding(otherNode).remove(thisNode);

                        thisNode.fireEvent(new TactilePaneEvent(TactilePaneEvent.AREA_LEFT, thisNode, otherNode));
                        otherNode.fireEvent(new TactilePaneEvent(TactilePaneEvent.AREA_LEFT, otherNode, thisNode));
                    }
                    if (proximityBounds != null && proximityBounds.intersects(otherBounds)) {
                        if (TactilePane.getNodesInProximity(thisNode).add(otherNode)) {
                            TactilePane.getNodesInProximity(otherNode).add(thisNode);

                            thisNode.fireEvent(new TactilePaneEvent(TactilePaneEvent.PROXIMITY_ENTERED, thisNode, otherNode));
                            otherNode.fireEvent(new TactilePaneEvent(TactilePaneEvent.PROXIMITY_ENTERED, otherNode, thisNode));
                        }
                        thisNode.fireEvent(new TactilePaneEvent(TactilePaneEvent.IN_PROXIMITY, thisNode, otherNode));
                    } else {
                        if (TactilePane.getNodesInProximity(thisNode).remove(otherNode)) {
                            TactilePane.getNodesInProximity(otherNode).remove(thisNode);

                            thisNode.fireEvent(new TactilePaneEvent(TactilePaneEvent.PROXIMITY_LEFT, thisNode, otherNode));
                            otherNode.fireEvent(new TactilePaneEvent(TactilePaneEvent.PROXIMITY_LEFT, otherNode, thisNode));
                        }
                    }
                }
            }
        }
    }
}