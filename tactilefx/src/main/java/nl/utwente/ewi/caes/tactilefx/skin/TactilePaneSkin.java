/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactilefx.skin;

import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import nl.utwente.ewi.caes.tactilefx.control.TactilePane;

/**
 * The skin for TactilePane.
 * 
 * @author Richard
 */
public class TactilePaneSkin extends SkinBase<TactilePane> {
    TactilePane pane;
    
    public TactilePaneSkin(final TactilePane tactilePane) {
        super(tactilePane);
        
        pane = tactilePane;
        
        consumeMouseEvents(false);
    }
    
    /**
     * Called during the layout pass of the scenegraph. 
     */
    @Override
    protected void layoutChildren(final double x, final double y,
            final double w, final double h) {
        
        // Like a Pane, it will only set the size of managed, resizable content 
        // to their preferred sizes and does not do any node positioning.
        pane.getChildren().stream()
            .filter(Node::isResizable)
            .filter(Node::isManaged)
            .forEach(Node::autosize);
        
        /*  Not working yet
        // Update positions of anchored nodes, autosize for all others.
        for (Node node : pane.getChildren()) {
            Anchor anchor = TactilePane.getAnchor(node);
            if (anchor != null && node.isManaged()) {
                Node anchorNode = anchor.getAnchorNode();
                Bounds anchorBounds = pane.sceneToLocal(anchorNode.localToScene(anchorNode.getBoundsInLocal()));
                Bounds nodeBounds = node.getBoundsInParent();
                
                double areaX = anchorBounds.getMinX() + anchor.getOffsetX();
                double areaY = anchorBounds.getMinY() + anchor.getOffsetY();
                double areaW = anchorBounds.getWidth();
                double areaH = anchorBounds.getHeight();
                HPos hpos = anchor.getAlignment().getHpos();
                VPos vpos = anchor.getAlignment().getVpos();
                
                double deltaW = nodeBounds.getWidth() - anchorBounds.getWidth();
                if (deltaW > 0) {
                    areaW += deltaW;
                    if (hpos == HPos.CENTER) {
                        areaX -= deltaW / 2;
                    } else if (hpos == HPos.RIGHT) {
                        areaX -= deltaW;
                    }
                }
                
                double deltaH = nodeBounds.getHeight() - anchorBounds.getHeight();
                if (deltaH > 0) {
                    areaH += deltaH;
                    if (vpos == VPos.CENTER) {
                        areaY -= deltaW / 2;
                    } else if (vpos == VPos.BOTTOM) {
                        areaY -= deltaW;
                    }
                }
                
                layoutInArea(node, areaX, areaY, areaW, areaH, 0, Insets.EMPTY, false, false, hpos, vpos);
                
                // Only call toFront if necessary
                while(anchorNode != null && !pane.getChildren().contains(anchorNode)) {
                    anchorNode = anchorNode.getParent();
                }
                if (anchorNode != null && anchor.isToFront()){// && pane.getChildren().indexOf(node) < pane.getChildren().indexOf(anchorNode)) {
                    toFront.add(node);
                }
            } else {
                node.autosize();
            }
        }
        for (Node node : toFront) {
            node.toFront();
        }
        toFront.clear();
        */
    }
}
