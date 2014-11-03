/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.cs.caes.tactile.control;

import javafx.scene.Node;

/**
 *
 * @author Richard
 */
public class Bond {
    private final Node bondNode;
    private double distance;
    private double forceMultiplier;
    
    public Bond(Node bondNode, double distance, double forceMultiplier) {
        this.bondNode = bondNode;
        this.distance = distance;
        this.forceMultiplier = forceMultiplier;
    }
    
    public Bond(Node bondNode, double distance) {
        this(bondNode, distance, 0.5);
    }
    
    public Node getBondNode() {
        return bondNode;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
    
    public double getDistance() {
        return distance;
    }

    public void setForceMultiplier(double forceMultiplier) {
        this.forceMultiplier = forceMultiplier;
    }
    
    public double getForceMultiplier() {
        return forceMultiplier;
    }
}
