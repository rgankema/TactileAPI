/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lwbdemo.ui;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Knot extends Circle {
    private static final double RADIUS = 15;
    
    private double anchorX;
    
    protected Knot(Bowtie bowtie) {
        super(RADIUS);
        
        setOnMousePressed(event -> {
            // Record location of click event in Knot
            anchorX = event.getX();
            event.consume();
        });
        
        setOnMouseDragged(event -> {
            Bounds thisBounds = this.localToScene(getBoundsInLocal());
            double thisCenterX = thisBounds.getMinX() + thisBounds.getWidth() / 2;
            
            double x = event.getSceneX() - anchorX;
            
            if (x < thisCenterX && bowtie.leftBlade.getChildren().size() > 1) {
                Node left = bowtie.leftBlade.getChildren().get(bowtie.leftBlade.getChildren().size() - 1);
                Bounds leftBounds = left.localToScene(left.getBoundsInLocal());
                double leftCenterX = leftBounds.getMinX() + leftBounds.getWidth() / 2;
                
                double delta = thisCenterX - leftCenterX;
                double progress = 1 - (x - leftCenterX) / delta;
                if (progress < 0) progress = 0;
                if (progress > 1) progress = 1;
                
                setScaleX(1 + progress * 0.3);
                setScaleY(1 - progress * 0.3);
                setTranslateX(-RADIUS * progress);
                
                if (x < leftCenterX) {
                    bowtie.coverHole();
                }
            }
            
            if (x > thisCenterX && bowtie.rightBlade.getChildren().size() > 0) {
                Node right = bowtie.rightBlade.getChildren().get(0);
                Bounds rightBounds = right.localToScene(right.getBoundsInLocal());
                double rightCenterX = rightBounds.getMinX() + rightBounds.getWidth() / 2;
                
                double delta = rightCenterX - thisCenterX;
                double progress = 1 - (rightCenterX - x) / delta;
                if (progress < 0) progress = 0;
                if (progress > 1) progress = 1;
                
                setScaleX(1 + progress * 0.3);
                setScaleY(1 - progress * 0.3);
                setTranslateX(RADIUS * progress);
                
                if (x > rightCenterX) {
                    bowtie.exposeHole();
                }
            }
            
            event.consume();
        });
        
        setOnMouseReleased(event -> {
            setScaleX(1);
            setScaleY(1);
            setTranslateX(0);
            event.consume();
        });
        
        setFill(Color.RED);
    }
}
