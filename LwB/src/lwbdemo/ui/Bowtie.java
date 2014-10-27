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
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;

/**
 *
 * @author Richard
 */
public class Bowtie extends Group {
    private static final double INSET = 10;
    
    final Knot knot;
    final Circle dummy;
    
    final HBox hbox;
    final Polygon background;
    
    public Bowtie(String term, String... type) {
        hbox = new HBox();
        
        knot = new Knot(this);
        dummy = new Circle(knot.getRadius());
        dummy.setVisible(false);
        
        knot.layoutXProperty().bind(dummy.layoutXProperty());
        knot.layoutYProperty().bind(dummy.layoutYProperty());
        
        hbox.getChildren().add(buildLabel(term));
        hbox.getChildren().add(dummy);
        for (String argument : type) {
            hbox.getChildren().add(buildLabel(argument));
        }
        hbox.getChildren().add(knot);
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER);
        
        background = new Polygon();
        background.setFill(Color.BISQUE);
        background.setStroke(Color.BROWN);
        
        new AnimationTimer() {

            @Override
            public void handle(long now) {
                Bounds hboxBounds = hbox.getBoundsInParent();
                Bounds knotBounds = Bowtie.this.sceneToLocal(knot.localToScene(knot.getBoundsInLocal()));

                background.getPoints().clear();
                background.getPoints().addAll(new Double[]{
                    hboxBounds.getMinX() - INSET, hboxBounds.getMinY() - INSET,
                    knotBounds.getMinX(), hboxBounds.getMinY() - INSET,
                    knotBounds.getMinX() + knotBounds.getWidth() / 2, hboxBounds.getMinY() + hboxBounds.getHeight() / 2,
                    knotBounds.getMaxX(), hboxBounds.getMinY() - INSET,
                    hboxBounds.getMaxX() + INSET, hboxBounds.getMinY() - INSET,

                    hboxBounds.getMaxX() + INSET, hboxBounds.getMaxY() + INSET,
                    knotBounds.getMaxX(), hboxBounds.getMaxY() + INSET,
                    knotBounds.getMinX() + knotBounds.getWidth() / 2, hboxBounds.getMinY() + hboxBounds.getHeight() / 2,
                    knotBounds.getMinX(), hboxBounds.getMaxY() + INSET,
                    hboxBounds.getMinX() - INSET, hboxBounds.getMaxY() + INSET
                });
            }
            
        }.start();
        
        getChildren().add(background);
        getChildren().add(hbox);
    }
    
    private Label buildLabel(String text) {
        Label label = new Label(text);
        
        Font font = new Font(20);
        label.setFont(font);
        
        return label;
    }
}
