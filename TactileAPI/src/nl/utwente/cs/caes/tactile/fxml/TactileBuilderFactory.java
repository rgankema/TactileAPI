/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.cs.caes.tactile.fxml;

import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.util.Builder;
import javafx.util.BuilderFactory;
import nl.utwente.cs.caes.tactile.control.Anchor;
import nl.utwente.cs.caes.tactile.control.TactilePane;

public class TactileBuilderFactory implements BuilderFactory {
    private final JavaFXBuilderFactory defaultBuilderFactory = new JavaFXBuilderFactory();
    
    @Override
    public Builder<?> getBuilder(Class<?> type) {
        if (type == Anchor.class) {
            return new TactilePaneAnchorBuilder();
        } else if (type == TactilePane.Bond.class) {
            throw new UnsupportedOperationException("Not supported yet");
        } else {
            return defaultBuilderFactory.getBuilder(type);
        }
    }
    
    public static class TactilePaneAnchorBuilder implements Builder<Anchor> {
        private Node anchorNode = null;
        private double offsetX = 0;
        private double offsetY = 0;
        private Anchor.Pos alignment = Anchor.Pos.TOP_LEFT;

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

        public Anchor.Pos getAlignment() {
            return alignment;
        }
        
        public void setAlignment(Anchor.Pos alignment) {
            this.alignment = alignment;
        }

        @Override
        public Anchor build() {
            return new Anchor(anchorNode, offsetX, offsetY, alignment);
        }

    }
}
