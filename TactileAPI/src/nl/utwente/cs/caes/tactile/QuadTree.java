package nl.utwente.cs.caes.tactile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;

class QuadTree {
	private final int MAX_DEPTH = 5;
	private final int MAX_OBJECTS = 10;
	private double proximityThreshold;

	// Codes for getIndex

	private QuadTree parent;
	private QuadTree[] children;
	private Bounds bounds;
	private final int level;
	private Map<Node, Bounds> proximityBoundsByObject = new HashMap<Node, Bounds>();

	/**
	 * Constructor of the QuadTree
	 * 
	 * @param bounds
	 *            The bounds of the 2D space that this QuadTree divides
	 * @param proximityThreshold
	 *            The maximum gap between two Nodes which makes them neighbors
	 */
	public QuadTree(Bounds bounds) {
		this.bounds = bounds;
		this.level = 0;
		this.proximityThreshold = 25;
	}

	private QuadTree(Bounds bounds, QuadTree parent) {
		this.bounds = bounds;
		this.parent = parent;
		this.level = parent.level + 1;
		this.proximityThreshold = parent.proximityThreshold;
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
	 * Inserts an object into the QuadTree
	 * 
	 * @param object
	 *            The object that is to be inserted
	 */
	public void insert(Node object) {
		Bounds bounds = object.localToScene(object.getBoundsInLocal());
		Bounds boundsAround = getProximityBounds(bounds);
		insert(object, boundsAround);
	}

	private void insert(Node object, Bounds bounds) {
		QuadTree insertNode = getTreeNode(bounds);
		if (insertNode == this || insertNode == null) {
			proximityBoundsByObject.put(object, bounds);

			Set<Node> objects = proximityBoundsByObject.keySet();
			if (objects.size() >= MAX_OBJECTS && this.level < MAX_DEPTH) {
				split();
			}
		} else {
			insertNode.insert(object, bounds);
		}
	}

	/**
	 * Returns the QuadTree that should store an object with the given Bounds
	 * 
	 * @param objectBounds
	 *            The bounds of a certain object
	 * @return The QuadTree it belongs to
	 */
	private QuadTree getTreeNode(Bounds objectBounds) {
		if (this.bounds.contains(objectBounds)) {
			if (children != null) {
				for (int i = 0; i < 4; i++) {
					if (children[i].bounds.contains(objectBounds)) {
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
		double halfWidth = bounds.getWidth() / 2.0;
		double halfHeight = bounds.getHeight() / 2.0;
		double x = bounds.getMinX();
		double y = bounds.getMinY();

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
	 * Removes an object from the QuadTree
	 * 
	 * @param object
	 *            The object that is to be removed
	 */
	public void remove(Node object) {
		Bounds bounds = object.localToScene(object.getBoundsInLocal());
		QuadTree removeNode = getTreeNode(getProximityBounds(bounds));

		if (removeNode == this) {
			proximityBoundsByObject.remove(object);
		} else {
			removeNode.remove(object);
		}
	}

	/**
	 * Updates the QuadTree. Each object that has changed bounds is inserted
	 * again.
	 */
	public void update() {
		Iterator<Node> iterator = proximityBoundsByObject.keySet().iterator();
		List<Node> objectsToAdd = new ArrayList<Node>();
		List<Bounds> boundsToAdd = new ArrayList<Bounds>();

		while (iterator.hasNext()) {
			Node object = iterator.next();
			Bounds bounds = object.localToScene(object.getBoundsInLocal());
			Bounds boundsAround = getProximityBounds(bounds);

			if (!boundsAround.equals(proximityBoundsByObject.get(object))) {
				iterator.remove();
				objectsToAdd.add(object);
				boundsToAdd.add(boundsAround);
			}
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

	/**
	 * Sets the proximity threshold. This value is used to determine whether two
	 * objects are within range to trigger a ProximityEvent.
	 * 
	 * @param threshold
	 *            The new value for the proximity threshold
	 */
	public void setProximityThreshold(double threshold) {
		if (threshold > 0) {
			this.proximityThreshold = threshold;
			for (QuadTree child : children) {
				child.setProximityThreshold(threshold);
			}
		} else {
			throw new IllegalArgumentException(
					"Proximity threshold should be a positive value");
		}
	}

	/**
	 * Gets the current proximity threshold. This value is used to determine
	 * whether two objects are within range to trigger a ProximityEvent. The
	 * default value is 25.
	 * 
	 * @return The current value for the proximity threshold
	 */
	public double getProximityThreshold() {
		return this.proximityThreshold;
	}

	// Help method to get the Bounds needed for proximity detection
	private Bounds getProximityBounds(Bounds bounds) {
		double x = bounds.getMinX() - proximityThreshold / 2;
		double y = bounds.getMinY() - proximityThreshold / 2;
		double w = bounds.getWidth() + proximityThreshold;
		double h = bounds.getHeight() + proximityThreshold;
		return new BoundingBox(x, y, w, h);
	}

	/**
	 * Retrieves all the objects that could be in the proximity (or collide
	 * with) the given object.
	 * 
	 * @param object
	 *            The object to find neighbors for
	 */
	public List<Node> retrieve(Node object) {
		List<Node> returnObjects = new ArrayList<Node>();
		return retrieve(object, returnObjects);
	}

	private List<Node> retrieve(Node object, List<Node> returnObjects) {
		QuadTree retrieveNode = getTreeNode(proximityBoundsByObject.get(object));

		if (retrieveNode != this && retrieveNode != null) {
			retrieveNode.retrieve(object, returnObjects);
		}

		returnObjects.addAll(proximityBoundsByObject.keySet());
		return returnObjects;
	}
}
