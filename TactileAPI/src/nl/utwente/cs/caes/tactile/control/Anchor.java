/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.cs.caes.tactile.control;

import javafx.scene.Node;

public class Anchor {
    final Node anchorNode;
    final double offsetX;
    final double offsetY;
    final Pos alignment;

    public enum Pos {
        TOP_LEFT, TOP_CENTER, TOP_RIGHT,
        CENTER_LEFT, CENTER, CENTER_RIGHT,
        BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT
    }
    public Anchor(Node anchorNode) {
        this(anchorNode, 0, 0, null);
    }

    public Anchor(Node anchorNode, double offsetX, double offsetY) {
        this(anchorNode, offsetX, offsetY, null);
    }

    public Anchor(Node anchorNode, Pos alignment) {
        this(anchorNode, 0, 0, alignment);
    }

    public Anchor(Node anchorNode, double offsetX, double offsetY, Pos alignment) {
        if (anchorNode == null) {
            throw new NullPointerException("anchorNode may not be null");
        }

        this.anchorNode = anchorNode;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.alignment = (alignment == null) ? Pos.TOP_LEFT : alignment;
    }

    public Node getAnchorNode() {
        return anchorNode;
    }
    public double getOffsetX() {
        return offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public Pos getAlignment() {
        return alignment;
    }
}