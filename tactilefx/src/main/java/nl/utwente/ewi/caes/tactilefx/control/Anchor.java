package nl.utwente.ewi.caes.tactilefx.control;

import javafx.geometry.Pos;
import javafx.scene.Node;

/**
 * <p>
 * A class that is used to "anchor" a Node's location to that
 * of another Node. The following code anchors a Node to another Node with a
 * y offset of -50:
 * <p>
 * <pre>
 * Rectangle anchorR = new Rectangle(100, 100);
 * Rectangle anchoredR = new Rectangle(50, 50);
 * TactilePane tp = new TactilePane(anchorR, anchoredR);
 * 
 * // Initialise Anchor
 * Anchor anchor = new Anchor(anchorR, 0, -50);
 * // Set the attached property anchor to the newly created Anchor
 * TactilePane.setAnchor(anchoredR, anchor);
 * </pre>
 * <p>
 * @author Richard
 */
public final class Anchor {
    private final Node anchorNode;
    private double offsetX;
    private double offsetY;
    private Pos alignment;
    private boolean toFront;

    /**
     * Initialises an Anchor with offsets set to 0 and alignment to null.
     * 
     * @param anchorNode the Node that acts as anchor
     */
    public Anchor(Node anchorNode) {
        this(anchorNode, 0, 0, null, true);
    }

    /**
     * Initialises an Anchor with a certain offsetX and offsetY, and
     * alignment set to null.
     * 
     * @param anchorNode the Node that acts as anchor
     * @param offsetX by how much the Node should be offset horizontally
     * @param offsetY by how much the Node should be offset vertically
     */
    public Anchor(Node anchorNode, double offsetX, double offsetY) {
        this(anchorNode, offsetX, offsetY, null, true);
    }

    /**
     * Initialises an Anchor with a given alignment, and offsets set to
     * 0.
     * @param anchorNode the Node that acts as anchor
     * @param alignment the alignment of the Node relative to its anchorNode
     */
    public Anchor(Node anchorNode, Pos alignment) {
        this(anchorNode, 0, 0, alignment, true);
    }

    /**
     * Initialises an Anchor with given offsets and alignment.
     * 
     * @param anchorNode the Node that acts as anchor
     * @param offsetX by how much the Node should be offset horizontally
     * @param offsetY by how much the Node should be offset vertically
     * @param alignment the alignment of the Node relative to its anchorNode
     */
    public Anchor(Node anchorNode, double offsetX, double offsetY, Pos alignment, boolean toFront) {
        if (anchorNode == null) {
            throw new NullPointerException("anchorNode may not be null");
        }

        this.anchorNode = anchorNode;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.alignment = (alignment == null) ? Pos.TOP_LEFT : alignment;
        this.toFront = toFront;
    }

    /**
     * Gets the Node that acts as anchor.
     * 
     * @return the Node that acts as anchor
     */
    public Node getAnchorNode() {
        return anchorNode;
    }
    
    /**
     * Sets by how much the Node should be offset horizontally.
     * @param offsetX by how much the Node should be offset horizontally
     */
    public void setOffsetX(double offsetX) {
        this.offsetX = offsetX;
    }
    
    /**
     * Gets by how much the Node should be offset horizontally.
     * @return by how much the Node should be offset horizontally
     */
    public double getOffsetX() {
        return offsetX;
    }
    
    /**
     * Sets by how much the Node should be offset vertically.
     * 
     * @param offsetY by how much the Node should be offset vertically
     */
    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }

    /**
     * Gets by how much the Node should be offset vertically.
     * 
     * @return by how much the Node should be offset vertically
     */
    public double getOffsetY() {
        return offsetY;
    }
    
    /**
     * Sets the alignment of the Node relative to its anchorNode.
     * 
     * @param alignment the alignment of the Node relative to its anchorNode
     */
    public void setAlignment(Pos alignment) {
        this.alignment = alignment;
    }

    /**
     * Gets the alignment of the Node relative to its anchorNode.
     * 
     * @return the alignment of the Node relative to its anchorNode
     */
    public Pos getAlignment() {
        return alignment;
    }
    
    /**
     * Sets whether the anchored Node should be kept in the front while it's
     * anchored.
     * @param toFront whether the anchored Node should stay in front
     */
    public void setToFront(boolean toFront) {
        this.toFront = toFront;
    }
    
    /**
     * Gets whether the anchored Node should be kept in the front while it's
     * anchored.
     * @return whether the anchored Node should stay in front
     */
    public boolean isToFront() {
        return toFront;
    }
}