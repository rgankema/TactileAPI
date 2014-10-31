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
import nl.utwente.cs.caes.tactile.control.TactilePane;

public class TactileBuilderFactory implements BuilderFactory {
    private JavaFXBuilderFactory defaultBuilderFactory = new JavaFXBuilderFactory();
    
    @Override
    public Builder<?> getBuilder(Class<?> type) {
        if (type == TactilePane.Anchor.class) {
            return new TactilePaneAnchorBuilder();
        } else if (type == TactilePane.Bond.class) {
            throw new UnsupportedOperationException("Not implemented yet");
        } else {
            return defaultBuilderFactory.getBuilder(type);
        }
    }
    
    public static class TactilePaneAnchorBuilder implements Builder<TactilePane.Anchor> {
        private Node anchorNode = null;
        private double xOffset = 0;
        private double yOffset = 0;
        private TactilePane.Anchor.Pos alignment = TactilePane.Anchor.Pos.TOP_LEFT;

        public void setAnchorNode(Node anchorNode) {
            this.anchorNode = anchorNode;
        }

        public void setxOffset(double xOffset) {
            this.xOffset = xOffset;
        }

        public void setyOffset(double yOffset) {
            this.yOffset = yOffset;
        }

        public void setAlignment(TactilePane.Anchor.Pos alignment) {
            this.alignment = alignment;
        }

        @Override
        public TactilePane.Anchor build() {
            return new TactilePane.Anchor(anchorNode, xOffset, yOffset, alignment);
        }

    }
}
