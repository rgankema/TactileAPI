package nl.utwente.cs.caes.tactile;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.shape.Rectangle;

public class QuadTree {
	private final int MAX_OBJECTS = 10;
	private final int MAX_LEVELS = 5;

	private List<Rectangle> objects = new ArrayList<Rectangle>();
	private Rectangle bounds;
	private QuadTree[] children = new QuadTree[4];
	private int level;

	/**
	 * Creates a new QuadTree with the given maximum amount of objects per node
	 * and the bounds of this node.
	 * 
	 * @param bounds
	 *            The rectangle this node covers
	 */
	public QuadTree(Rectangle bounds) {
		this.bounds = bounds;
		this.level = 0;
	}

	private QuadTree(Rectangle bounds, int level) {
		this.bounds = bounds;
		this.level = level;
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
	 * @param rectangle	The rectangle to find objects for
	 * @return			A list of objects rectangle could collide with
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
				level + 1);
		children[1] = new QuadTree(new Rectangle(x + halfWidth, y, halfWidth,
				halfHeight), level + 1);
		children[2] = new QuadTree(new Rectangle(x + halfWidth, y + halfHeight,
				halfWidth, halfHeight), level + 1);
		children[3] = new QuadTree(new Rectangle(x, y + halfHeight, halfWidth,
				halfHeight), level + 1);
	}

	/**
	 * Determines which node the rectangle belongs to. Returns -1 if it doesn't
	 * fit in a single node.
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

}
