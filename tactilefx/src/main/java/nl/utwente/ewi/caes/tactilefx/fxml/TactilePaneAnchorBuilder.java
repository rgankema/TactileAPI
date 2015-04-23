package nl.utwente.ewi.caes.tactilefx.fxml;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.util.Builder;
import nl.utwente.ewi.caes.tactilefx.control.Anchor;

/**
 * Builder for the {@code TactilePane.Anchor} class.
 * 
 * @author Richard
 */
public class TactilePaneAnchorBuilder implements Builder<Anchor> {
    private Node anchorNode = null;
    private double offsetX = 0;
    private double offsetY = 0;
    private Pos alignment = Pos.TOP_LEFT;
    private boolean toFront = true;

    public Node getAnchorNode() {
        return anchorNode;
    }

    public void setAnchorNode(Node anchorNode) {
        this.anchorNode = anchorNode;
    }

    public double getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(double offsetX) {
        this.offsetX = offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }

    public Pos getAlignment() {
        return alignment;
    }

    public void setAlignment(Pos alignment) {
        this.alignment = alignment;
    }
    
    public boolean isToFront() {
        return toFront;
    }
    
    public void setToFront(boolean toFront) {
        this.toFront = toFront;
    }

    @Override
    public Anchor build() {
        return new Anchor(anchorNode, offsetX, offsetY, alignment, toFront);
    }

}