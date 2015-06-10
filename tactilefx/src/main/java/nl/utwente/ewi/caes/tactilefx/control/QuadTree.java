package nl.utwente.ewi.caes.tactilefx.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;

class QuadTree {

    private final int MAX_DEPTH = 5;
    private final int MAX_OBJECTS = 10;

    private QuadTree parent;
    private QuadTree[] children;
    private final int level;
    private final Map<Node, Bounds> proximityBoundsByObject = new HashMap<>();

    /**
     * Constructor of the QuadTree
     *
     * @param bounds The bounds of the 2D space that this QuadTree divides
     */
    public QuadTree(Bounds bounds) {
        setBounds(bounds);
        this.level = 0;
    }

    private QuadTree(Bounds bounds, QuadTree parent) {
        setBounds(bounds);
        this.parent = parent;
        this.level = parent.level + 1;
    }

    /**
     * The maximum distance between two {@code ActionGroups} at which they are
     * considered neighbours, and thus a proximity event is fired. An
     * IllegalArgumentException is thrown when this value is set lower than 0.
     *
     * @defaultvalue 25.0
     */
    private DoubleProperty proximityThreshold;

    public final void setProximityThreshold(double threshold) {
        proximityThresholdProperty().set(threshold);
    }

    public final double getProximityThreshold() {
        return proximityThresholdProperty().get();
    }

    public final DoubleProperty proximityThresholdProperty() {
        if (proximityThreshold == null) {

            double value = 25.0;
            if (parent != null) {
                value = parent.getProximityThreshold();
            }

            proximityThreshold = new SimpleDoubleProperty(value) {
                @Override
                public void set(double value) {
                    if (value >= 0) {
                        super.set(value);
                        if (children != null) {
                            for (QuadTree child : children) {
                                child.setProximityThreshold(value);
                            }
                        }
                    } else {
                        throw new IllegalArgumentException("Proximity threshold should be a positive value");
                    }
                }
            };
        }
        return proximityThreshold;
    }

    /**
     * The bounds of the QuadTree
     */
    private ObjectProperty<Bounds> bounds;

    public final void setBounds(Bounds bounds) {
        boundsProperty().set(bounds);
    }

    public final Bounds getBounds() {
        return boundsProperty().get();
    }

    public final ObjectProperty<Bounds> boundsProperty() {
        if (bounds == null) {
            bounds = new SimpleObjectProperty<Bounds>() {
                @Override
                public void set(Bounds value) {
                    if (!value.equals(get())) {
                        super.set(value);
                        // Update bounds of children
                        if (children != null) {
                            double halfWidth = getBounds().getWidth() / 2.0;
                            double halfHeight = getBounds().getHeight() / 2.0;
                            double x = getBounds().getMinX();
                            double y = getBounds().getMinY();

                            children[0].setBounds(new BoundingBox(x, y, halfWidth, halfHeight));
                            children[1].setBounds(new BoundingBox(x + halfWidth, y, halfWidth, halfHeight));
                            children[2].setBounds(new BoundingBox(x + halfWidth, y + halfHeight, halfWidth, halfHeight));
                            children[3].setBounds(new BoundingBox(x, y + halfHeight, halfWidth, halfHeight));
                        }
                        // If root, update
                        if (parent == null) {
                            update();
                        }
                    }
                }
            };
        }
        return bounds;
    }

    /**
     * Clears the QuadTree and its children
     */
    public void clear() {
        if (children != null) {
            for (int i = 0; i < 4; i++) {
                children[i].clear();
                children[i] = null;
            }
        }
        proximityBoundsByObject.clear();
        parent = null;
    }

    /**
     * Inserts a node into the QuadTree
     *
     * @param node The node that is to be inserted
     */
    public void insert(Node node) {
        Bounds bounds = node.localToScene(node.getBoundsInLocal());
        if (getProximityThreshold() > 0) {
            Bounds boundsAround = getProximityBounds(bounds);
            insert(node, boundsAround);
        } else {
            insert(node, bounds);
        }
    }

    private void insert(Node node, Bounds bounds) {
        QuadTree insertNode = getTreeNode(bounds);
        if (insertNode == this || insertNode == null) {
            proximityBoundsByObject.put(node, bounds);

            Set<Node> objects = proximityBoundsByObject.keySet();
            if (objects.size() >= MAX_OBJECTS && this.level < MAX_DEPTH && children == null) {
                split();
            }
        } else {
            insertNode.insert(node, bounds);
        }
    }

    /**
     * Returns the QuadTree that should store an object with the given Bounds
     *
     * @param objectBounds The bounds of a certain object
     * @return The QuadTree it belongs to
     */
    private QuadTree getTreeNode(Bounds objectBounds) {
        if (this.getBounds().contains(objectBounds)) {
            if (children != null) {
                for (int i = 0; i < 4; i++) {
                    if (children[i].getBounds().contains(objectBounds)) {
                        return children[i];
                    }
                }
            }
            return this;
        }
        return parent;
    }

    /**
     * Creates four children for this node, and adds objects from this node to
     * the corresponding child nodes.
     */
    private void split() {
        double halfWidth = getBounds().getWidth() / 2.0;
        double halfHeight = getBounds().getHeight() / 2.0;
        double x = getBounds().getMinX();
        double y = getBounds().getMinY();

        children = new QuadTree[4];
        children[0] = new QuadTree(
                new BoundingBox(x, y, halfWidth, halfHeight), this);
        children[1] = new QuadTree(new BoundingBox(x + halfWidth, y, halfWidth,
                halfHeight), this);
        children[2] = new QuadTree(new BoundingBox(x + halfWidth, y
                + halfHeight, halfWidth, halfHeight), this);
        children[3] = new QuadTree(new BoundingBox(x, y + halfHeight,
                halfWidth, halfHeight), this);

        Iterator<Node> iterator = proximityBoundsByObject.keySet().iterator();
        while (iterator.hasNext()) {
            Node object = iterator.next();
            Bounds objectBounds = proximityBoundsByObject.get(object);
            QuadTree insertNode = getTreeNode(objectBounds);

            if (insertNode != this && insertNode != null) {
                iterator.remove();
                insertNode.proximityBoundsByObject.put(object, objectBounds);
            }
        }
    }

    /**
     * Removes a node from the QuadTree
     *
     * @param node The node that is to be removed
     */
    public void remove(Node node) {
        Bounds bounds = node.localToScene(node.getBoundsInLocal());
        QuadTree removeNode = getTreeNode(getProximityBounds(bounds));

        if (removeNode == this || removeNode == null) {
            proximityBoundsByObject.remove(node);
        } else {
            removeNode.remove(node);
        }
    }

    /**
     * Updates the QuadTree. Each object that has changed bounds is inserted
     * again.
     */
    public void update() {
        Iterator<Node> iterator = proximityBoundsByObject.keySet().iterator();
        List<Node> objectsToAdd = new ArrayList<>();
        List<Bounds> boundsToAdd = new ArrayList<>();

        while (iterator.hasNext()) {
            Node object = iterator.next();
            if (!TactilePane.isDirty(object)) continue;
            
            Bounds bounds = object.localToScene(object.getBoundsInLocal());
            Bounds boundsAround = getProximityBounds(bounds);

            iterator.remove();
            objectsToAdd.add(object);
            boundsToAdd.add(boundsAround);
        }

        for (int i = 0; i < objectsToAdd.size(); i++) {
            insert(objectsToAdd.get(i), boundsToAdd.get(i));
        }

        if (children != null) {
            for (int i = 0; i < 4; i++) {
                children[i].update();
            }
        }
    }

    // Help method to get the Bounds needed for proximity detection
    private Bounds getProximityBounds(Bounds bounds) {
        double x = bounds.getMinX() - getProximityThreshold() / 2;
        double y = bounds.getMinY() - getProximityThreshold() / 2;
        double w = bounds.getWidth() + getProximityThreshold();
        double h = bounds.getHeight() + getProximityThreshold();
        return new BoundingBox(x, y, w, h);
    }

    /**
     * Retrieves all the nodes that could be in the proximity (or collide
     * with) the given node.
     *
     * @param node The node to find neighbours for
     */
    public List<Node> retrieve(Node node) {
        List<Node> returnObjects = retrieve(node, new ArrayList<>());
        returnObjects.remove(node);
        return returnObjects;
    }

    private List<Node> retrieve(Node node, List<Node> returnObjects) {
        QuadTree retrieveNode = getTreeNode(proximityBoundsByObject.get(node));

        if ((retrieveNode == this || retrieveNode == null || retrieveNode.level < this.level) && children != null) {
            for (QuadTree child : children) {
                child.retrieve(node, returnObjects);
            }
        } else if (retrieveNode != null && retrieveNode.level > this.level) {
            retrieveNode.retrieve(node, returnObjects);
        }

        returnObjects.addAll(proximityBoundsByObject.keySet());
        return returnObjects;
    }
}
