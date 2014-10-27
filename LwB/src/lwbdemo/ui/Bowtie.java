/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lwbdemo.ui;

import javafx.animation.AnimationTimer;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;

/**
 *
 * @author Richard
 */
public class Bowtie extends Group {
    private static final double INSET = 10;
    
    final Knot knot;
    
    final HBox hbox;
    final HBox leftBlade;
    final HBox rightBlade;
    final Polygon background;
    
    public Bowtie(String term, String... type) {
        hbox = new HBox();
        leftBlade = new HBox();
        rightBlade = new HBox();
        knot = new Knot(this);
        
        leftBlade.getChildren().add(buildLabel(term));
        leftBlade.setSpacing(2);
        leftBlade.setAlignment(Pos.CENTER);
        
        for (String argument : type) {
            rightBlade.getChildren().add(buildLabel(argument));
            rightBlade.getChildren().add(buildLabel("->"));
        }
        rightBlade.getChildren().remove(rightBlade.getChildren().size() - 1);
        rightBlade.setSpacing(2);
        rightBlade.setAlignment(Pos.CENTER);
        
        hbox.getChildren().addAll(leftBlade, knot, rightBlade);
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER);
        
        // The bowtie without knot
        background = new Polygon();
        background.setFill(Color.BISQUE);
        background.setStroke(Color.BROWN);
        
        // Draws the bowtie
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                Bounds hboxBounds = hbox.getBoundsInParent();
                Bounds knotBounds = Bowtie.this.sceneToLocal(knot.localToScene(knot.getBoundsInLocal()));

                background.getPoints().clear();
                background.getPoints().addAll(new Double[]{
                    // Top left corner
                    hboxBounds.getMinX() - INSET, hboxBounds.getMinY() - INSET,
                    knotBounds.getMinX(), hboxBounds.getMinY() - INSET,
                    knotBounds.getMinX() + knotBounds.getWidth() / 2, hboxBounds.getMinY() + hboxBounds.getHeight() / 2,
                    knotBounds.getMaxX(), hboxBounds.getMinY() - INSET,
                    // Top right corner
                    hboxBounds.getMaxX() + INSET, hboxBounds.getMinY() - INSET,
                    // Bottom right corner
                    hboxBounds.getMaxX() + INSET, hboxBounds.getMaxY() + INSET,
                    knotBounds.getMaxX(), hboxBounds.getMaxY() + INSET,
                    knotBounds.getMinX() + knotBounds.getWidth() / 2, hboxBounds.getMinY() + hboxBounds.getHeight() / 2,
                    knotBounds.getMinX(), hboxBounds.getMaxY() + INSET,
                    // Bottom left corner
                    hboxBounds.getMinX() - INSET, hboxBounds.getMaxY() + INSET
                });
            }
            
        }.start();
        
        getChildren().addAll(background, hbox);
    }
    
    public void exposeHole() {
        if (!rightBlade.getChildren().isEmpty()) {
            Node removeNode = rightBlade.getChildren().get(0);
            rightBlade.getChildren().remove(removeNode);
            if (!rightBlade.getChildren().isEmpty()) {
                // remove arrow
                rightBlade.getChildren().remove(0);
            }
            if (leftBlade.getChildren().size() > 1) {
                leftBlade.getChildren().add(buildLabel("->"));
            }
            leftBlade.getChildren().add(removeNode);
        }
    }
    
    public void coverHole() {
        if (leftBlade.getChildren().size() > 1) {
            Node removeNode = leftBlade.getChildren().get(leftBlade.getChildren().size() - 1);
            leftBlade.getChildren().remove(removeNode);
            if (!rightBlade.getChildren().isEmpty()) {
                // add arrow
                rightBlade.getChildren().add(0, buildLabel("->"));
            }
            if (leftBlade.getChildren().size() > 2) {
                leftBlade.getChildren().remove(leftBlade.getChildren().size() - 1);
            }
            rightBlade.getChildren().add(0, removeNode);
        }
    }
    
    private Label buildLabel(String text) {
        Label label = new Label(text);
        
        Font font = new Font(20);
        label.setFont(font);
        
        return label;
    }
}
