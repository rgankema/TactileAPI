/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lwbdemo.ui;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Knot extends Circle {
    private static final double RADIUS = 15;
    
    final HBox parent;
    final Bowtie bowtie;
    
    double anchorX;
    
    protected Knot(Bowtie bowtie) {
        super(RADIUS);
        
        setManaged(false);
        this.bowtie = bowtie;
        this.parent = bowtie.hbox;
        
        setOnMousePressed(event -> {
            anchorX = event.getX();
            event.consume();
        });
        
        setOnMouseDragged(event -> {
            int position = parent.getChildren().indexOf(bowtie.dummy);
            
            Bounds thisBounds = getBoundsInParent();
            double thisCenterX = thisBounds.getMinX() + thisBounds.getWidth() / 2;
            
            Point2D pointInParent = parent.sceneToLocal(new Point2D(event.getSceneX(), event.getSceneY()));
            double x = pointInParent.getX() - anchorX;
            
            if (x < thisCenterX && position > 1) {
                Node left = parent.getChildren().get(position - 1);
                Bounds leftBounds = left.getBoundsInParent();
                double leftCenterX = leftBounds.getMinX() + leftBounds.getWidth() / 2;
                
                double delta = thisCenterX - leftCenterX;
                double progress = 1 - (x - leftCenterX) / delta;
                if (progress < 0) progress = 0;
                if (progress > 1) progress = 1;
                
                setScaleX(1 + progress * 0.3);
                setScaleY(1 - progress * 0.3);
                setTranslateX(-RADIUS * progress);
                
                if (x < leftCenterX) {
                    parent.getChildren().remove(bowtie.dummy);
                    position--;
                    parent.getChildren().add(position, bowtie.dummy);
                }
            }
            
            if (x > thisCenterX && position < parent.getChildren().size() - 2) {
                Node right = parent.getChildren().get(position + 1);
                Bounds rightBounds = right.getBoundsInParent();
                double rightCenterX = rightBounds.getMinX() + rightBounds.getWidth() / 2;
                
                double delta = rightCenterX - thisCenterX;
                double progress = 1 - (rightCenterX - x) / delta;
                if (progress < 0) progress = 0;
                if (progress > 1) progress = 1;
                
                setScaleX(1 + progress * 0.3);
                setScaleY(1 - progress * 0.3);
                setTranslateX(RADIUS * progress);
                
                if (pointInParent.getX() - anchorX > rightCenterX) {
                    parent.getChildren().remove(bowtie.dummy);
                    position++;
                    parent.getChildren().add(position, bowtie.dummy);
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
