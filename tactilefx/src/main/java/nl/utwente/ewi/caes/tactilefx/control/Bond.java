package nl.utwente.ewi.caes.tactilefx.control;

import javafx.scene.Node;

/**
 * Defines a Bond between two Nodes in a TactilePane. When Node A has a
 * bond with a Node B, Node A will be attracted towards Node B by means of a
 * vector that is set in the attached property {@link nl.utwente.ewi.caes.tactilefx.control.TactilePane#vectorProperty(javafx.scene.Node) vector}.
 * 
 * @author Richard
 */
public class Bond {
    private final Node bondNode;
    private double distance;
    private double forceMultiplier;
    
    /**
     * Initializes a new {@code Bond}.
     * @param bondNode  The Node that should be followed
     * @param distance  The distance from the bondNode that should be aimed for
     * @param forceMultiplier Multiplied with the vector that is the result of
     * this Bond. Used to control the speed with which a Node will follow the {@code bondNode}
     */
    public Bond(Node bondNode, double distance, double forceMultiplier) {
        this.bondNode = bondNode;
        this.distance = distance;
        this.forceMultiplier = forceMultiplier;
    }
    
    /**
     * Initializes a new {@code Bond} with a default {@code forceMultiplier} of 0.5.
     * @param bondNode  The Node that should be followed
     * @param distance  The distance from the bondNode that should be aimed for
     */
    public Bond(Node bondNode, double distance) {
        this(bondNode, distance, 0.5);
    }
    
    /**
     * Gets the Node that should be followed.
     * 
     * @return the Node that should be followed
     */
    public Node getBondNode() {
        return bondNode;
    }

    /**
     * Sets the distance the Node should aim to keep from its bondNode.
     * 
     * @param distance the distance the Node should aim to keep from its bondNode
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }
    
    /**
     * Gets the distance the Node should aim to keep from its bondNode.
     * 
     * @return the distance the Node should aim to keep from its bondNode
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Sets the force multiplier. This is multiplied with the vector that is the result of
     * this Bond. Used to control the speed with which a Node will follow the {@code bondNode}
     * 
     * @param forceMultiplier the force multiplier
     */
    public void setForceMultiplier(double forceMultiplier) {
        this.forceMultiplier = forceMultiplier;
    }
    
    /**
     * Gets the force multiplier. This is multiplied with the vector that is the result of
     * this Bond. Used to control the speed with which a Node will follow the {@code bondNode}
     * 
     * @return forceMultiplier the force multiplier
     */
    public double getForceMultiplier() {
        return forceMultiplier;
    }
}
