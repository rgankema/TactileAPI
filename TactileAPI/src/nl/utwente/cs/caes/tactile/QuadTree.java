package nl.utwente.cs.caes.tactile;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.shape.Rectangle;

class QuadTree {
	private final int MAX_OBJECTS = 10;
	private final int MAX_LEVELS = 5;

	private List<Rectangle> objects;
	private Rectangle bounds;
	private QuadTree parent;
	private QuadTree[] children;
	private int level;

	/**
	 * Creates a new QuadTree with the given maximum amount of objects per node
	 * and the bounds of this node.
	 * 
	 * @param bounds
	 *            The rectangle this node covers
	 */
	public QuadTree(Rectangle bounds) {
		this.parent = null;
		this.bounds = bounds;
		this.level = 0;
	}

	private QuadTree(Rectangle bounds, QuadTree parent, int level) {
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
	public Rectangle getBounds() {
		return bounds;
	}

	/**
	 * Clears the QuadTree.
	 */
	public void clear() {
		objects.clear();

		for (int i = 0; i < children.length; i++) {
			children[i].clear();
			children[i] = null;
		}
	}

	/**
	 * Retrieves all objects that could collide with the given object
	 * 
	 * @param rectangle
	 *            The rectangle to find objects for
	 * @return A list of objects rectangle could collide with
	 */
	public List retrieve(Rectangle rectangle) {
		int index = getIndex(rectangle);
		List returnObjects = new ArrayList();
		if (index != -1 && children[0] != null) {
			children[index].retrieve(returnObjects, rectangle);
		}

		returnObjects.addAll(objects);

		return returnObjects;
	}

	// Help method for recursion
	private List retrieve(List returnObjects, Rectangle rectangle) {
		int index = getIndex(rectangle);
		if (index != -1 && children[0] != null) {
			children[index].retrieve(returnObjects, rectangle);
		}

		returnObjects.addAll(objects);

		return returnObjects;
	}

	/**
	 * Inserts a rectangle into the QuadTree.
	 * 
	 * @param rectangle
	 *            The Rectangle to insert
	 */
	public void insert(Rectangle rectangle) {
		if (children[0] != null) {
			int index = getIndex(rectangle);

			if (index != -1) {
				children[index].insert(rectangle);

				return;
			}
		}

		objects.add(rectangle);

		if (objects.size() > MAX_OBJECTS && level < MAX_LEVELS
				&& children[0] == null) {
			split();

			int i = 0;
			while (i < objects.size()) {
				int index = getIndex(objects.get(i));
				if (index != -1) {
					children[index].insert(objects.remove(i));
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
		double x = bounds.getX();
		double y = bounds.getY();

		children[0] = new QuadTree(new Rectangle(x, y, halfWidth, halfHeight),
				this, level + 1);
		children[1] = new QuadTree(new Rectangle(x + halfWidth, y, halfWidth,
				halfHeight), this, level + 1);
		children[2] = new QuadTree(new Rectangle(x + halfWidth, y + halfHeight,
				halfWidth, halfHeight), this, level + 1);
		children[3] = new QuadTree(new Rectangle(x, y + halfHeight, halfWidth,
				halfHeight), this, level + 1);
	}

	/**
	 * Determines which node the rectangle belongs to. Returns -1 if it doesn't
	 * fit in a child node.
	 * 
	 * @param rectangle
	 *            The rectangle to find the index for
	 * @return The index for rectangle
	 */
	private int getIndex(Rectangle rectangle) {
		int index = -1;
		double verticalMidpoint = bounds.getX() + (bounds.getWidth() / 2);
		double horizontalMidpoint = bounds.getY() + (bounds.getHeight() / 2);

		// Object can completely fit within the top quadrants
		boolean topQuadrant = (rectangle.getY() < horizontalMidpoint && rectangle
				.getY() + rectangle.getHeight() < horizontalMidpoint);
		// Object can completely fit within the bottom quadrants
		boolean bottomQuadrant = (rectangle.getY() > horizontalMidpoint);

		// Object can completely fit within the left quadrants
		if (rectangle.getX() < verticalMidpoint
				&& rectangle.getX() + rectangle.getWidth() < verticalMidpoint) {
			if (topQuadrant) {
				index = 0;
			} else if (bottomQuadrant) {
				index = 3;
			}
		}
		// Object can completely fit within the right quadrants
		else if (rectangle.getX() > verticalMidpoint) {
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
	public void update(Rectangle oldObject, Rectangle newObject) {
		if (oldObject.equals(newObject)) {
			return;
		}

		// Can be optimised
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
	public QuadTree delete(Rectangle object) {
		if (objects.remove(object)) {
			return this;
		}

		if (children[0] == null) {
			return null;
		}

		int index = getIndex(object);
		return children[index].delete(object);
	}

}
