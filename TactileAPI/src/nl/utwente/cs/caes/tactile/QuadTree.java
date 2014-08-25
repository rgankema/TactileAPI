package nl.utwente.cs.caes.tactile;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.shape.Rectangle;

public class QuadTree {
	private final int MAX_OBJECTS = 10;
	private final int MAX_LEVELS = 5;

	private List<Bounds> objectBoundingBoxes = new ArrayList<Bounds>();
	private Bounds bounds;
	private QuadTree parent;
	private QuadTree[] children = new QuadTree[4];
	private int level;

	/**
	 * Creates a new QuadTree with the given maximum amount of objects per node
	 * and the bounds of this node.
	 * 
	 * @param bounds
	 *            The rectangle this node covers
	 */
	public QuadTree(Bounds bounds) {
		this.parent = null;
		this.bounds = bounds;
		this.level = 0;
	}

	private QuadTree(Bounds bounds, QuadTree parent, int level) {
		this.parent = parent;
		this.bounds = bounds;
		this.level = level;
	}

	/**
	 * Gets the parent of this node.
	 * 
	 * @return The parent of this node
	 */
	public QuadTree getParent() {
		return parent;
	}

	/**
	 * Gets the root of this tree.
	 * 
	 * @return The root of this tree
	 */
	public QuadTree getRoot() {
		QuadTree root = this;
		while (root.getParent() != null) {
			root = root.getParent();
		}
		return root;
	}

	/**
	 * Gets the bounds of this node.
	 * 
	 * @return The bounds of this node
	 */
	public Bounds getBounds() {
		return bounds;
	}

	/**
	 * Clears the QuadTree.
	 */
	public void clear() {
		objectBoundingBoxes.clear();

		for (int i = 0; i < children.length; i++) {
			children[i].clear();
			children[i] = null;
		}
	}

	/**
	 * Retrieves all objects that could collide with the given object
	 * 
	 * @param boundingBox
	 *            The BoundingBox to find objects for
	 * @return A list of objects rectangle could collide with
	 */
	public List retrieve(BoundingBox boundingBox) {
		int index = getIndex(boundingBox);
		List returnObjects = new ArrayList();
		if (index != -1 && children[0] != null) {
			children[index].retrieve(returnObjects, boundingBox);
		}

		returnObjects.addAll(objectBoundingBoxes);

		return returnObjects;
	}

	// Help method for recursion
	private List retrieve(List returnObjects, BoundingBox boundingBox) {
		int index = getIndex(boundingBox);
		if (index != -1 && children[0] != null) {
			children[index].retrieve(returnObjects, boundingBox);
		}

		returnObjects.addAll(objectBoundingBoxes);

		return returnObjects;
	}

	/**
	 * Inserts a rectangle into the QuadTree.
	 * 
	 * @param bounds
	 *            The Bounds to insert
	 */
	public void insert(Bounds bounds) {
		if (children[0] != null) {
			int index = getIndex(bounds);

			if (index != -1) {
				children[index].insert(bounds);

				return;
			}
		}

		objectBoundingBoxes.add(bounds);

		if (objectBoundingBoxes.size() > MAX_OBJECTS && level < MAX_LEVELS
				&& children[0] == null) {
			split();

			int i = 0;
			while (i < objectBoundingBoxes.size()) {
				int index = getIndex(objectBoundingBoxes.get(i));
				if (index != -1) {
					children[index].insert(objectBoundingBoxes.remove(i));
				} else {
					i++;
				}
			}
		}
	}

	/**
	 * Creates four children for this node.
	 */
	private void split() {
		double halfWidth = bounds.getWidth() / 2.0;
		double halfHeight = bounds.getHeight() / 2.0;
		double x = bounds.getMinX();
		double y = bounds.getMinY();

		children[0] = new QuadTree(new BoundingBox(x, y, halfWidth, halfHeight),
				this, level + 1);
		children[1] = new QuadTree(new BoundingBox(x + halfWidth, y, halfWidth,
				halfHeight), this, level + 1);
		children[2] = new QuadTree(new BoundingBox(x + halfWidth, y + halfHeight,
				halfWidth, halfHeight), this, level + 1);
		children[3] = new QuadTree(new BoundingBox(x, y + halfHeight, halfWidth,
				halfHeight), this, level + 1);
	}

	/**
	 * Determines which node the rectangle belongs to. Returns -1 if it doesn't
	 * fit in a child node.
	 * 
	 * @param bounds
	 *            The Bounds to find the index for
	 * @return The index for rectangle
	 */
	private int getIndex(Bounds bounds) {
		int index = -1;
		double verticalMidpoint = bounds.getMinX() + (bounds.getWidth() / 2);
		double horizontalMidpoint = bounds.getMinY() + (bounds.getHeight() / 2);

		// Object can completely fit within the top quadrants
		boolean topQuadrant = (bounds.getMinY() < horizontalMidpoint && bounds
				.getMinY() + bounds.getHeight() < horizontalMidpoint);
		// Object can completely fit within the bottom quadrants
		boolean bottomQuadrant = (bounds.getMinY() > horizontalMidpoint);

		// Object can completely fit within the left quadrants
		if (bounds.getMinX() < verticalMidpoint
				&& bounds.getMinX() + bounds.getWidth() < verticalMidpoint) {
			if (topQuadrant) {
				index = 0;
			} else if (bottomQuadrant) {
				index = 3;
			}
		}
		// Object can completely fit within the right quadrants
		else if (bounds.getMinX() > verticalMidpoint) {
			if (topQuadrant) {
				index = 1;
			} else if (bottomQuadrant) {
				index = 2;
			}
		}

		return index;
	}

	/**
	 * Updates an object. If the object doesn't fit in the node it's currently
	 * in, it will be moved to one it does belong to.
	 * 
	 * @param oldObject
	 * @param newObject
	 */
	public void update(BoundingBox oldObject, BoundingBox newObject) {
		if (oldObject.equals(newObject)) {
			return;
		}

		// Can be optimized
		delete(oldObject);
		insert(newObject);
	}

	/**
	 * Deletes an object, and returns the QuadTree it was deleted from.
	 * 
	 * @param object
	 *            The object that is to be deleted.
	 * @return The QuadTree it was deleted from. Null if the object does not
	 *         exist in the QuadTree.
	 */
	public QuadTree delete(Bounds object) {
		if (objectBoundingBoxes.remove(object)) {
			return this;
		}

		if (children[0] == null) {
			return null;
		}

		int index = getIndex(object);
		return children[index].delete(object);
	}

}
