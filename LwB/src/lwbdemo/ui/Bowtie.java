/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lwbdemo.ui;

import lwbdemo.model.Term;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import nl.utwente.cs.caes.tactile.control.TactilePane;
import nl.utwente.cs.caes.tactile.control.TactilePane.Anchor;

/**
 *
 * @author Richard
 */
public class Bowtie extends Group {
    private static final double BG_OFFSET = 10;
    
    final Knot knot;
    final HBox hbox;
    final TermBlade termBlade;
    final TypeBlade typeBlade;
    final Polygon background;
    
    private boolean compact = false;
    private TermDisplay anchor = null;
    
    public Bowtie(TactilePane tracker, String name, Term... terms) {
        termBlade = new TermBlade(this, name);
        typeBlade = new TypeBlade(this, terms);
        knot = new Knot(this);
        
        hbox = new HBox();
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(termBlade, knot, typeBlade);
                
        // Draws the bowtie
        background = new Polygon();
        background.setFill(Color.BISQUE);
        background.setStroke(Color.BROWN);
        background.setStrokeWidth(2);
        
        hbox.boundsInParentProperty().addListener((obs, oldVal, newVal) -> {
            drawBackground();
        });
        knot.boundsInParentProperty().addListener((obs, oldVal, newVal) -> {
            drawBackground();
        });
        
        getChildren().addAll(background, hbox);
        
        TactilePane.setTracker(typeBlade, tracker);
    }
    
    public TermDisplay getAnchor() {
        return anchor;
    }
    
    public Term getType() {
        return typeBlade.getType();
    }
    
    public void exposeHole() {
        TermDisplay removeNode = typeBlade.popTerm();
        if (removeNode != null) {
            termBlade.pushTerm(removeNode);
        }
    }
    
    public void coverHole() {
        TermDisplay removeNode = termBlade.popTerm();
        if (removeNode != null) {
            typeBlade.pushTerm(removeNode);
        }
    }
    
    public void anchorAt(TermDisplay termDisplay) {
        if (termDisplay == null) {
            grow();
            
            TactilePane.setAnchor(this, null);
            TactilePane.setTracker(typeBlade, (TactilePane) getParent());
        } else {
            shrink();
            
            TactilePane.setAnchor(this, new Anchor(termDisplay, 1, 1, Anchor.Pos.CENTER));
        }
        anchor = termDisplay;
    }
    
    private void shrink() {
        if (!compact) {
            hbox.getChildren().removeAll(knot, typeBlade);
            compact = true;
            
            background.setFill(Color.LIGHTGREEN);
        }
    }
    
    private void grow() {
        if (compact) {
            hbox.getChildren().addAll(knot, typeBlade);
            compact = false;
            
            background.setFill(Color.BISQUE);
            drawBackground();
        }
    }
    
    private void drawBackground() {
        Bounds hboxBounds = hbox.getBoundsInParent();
        background.getPoints().clear();
        if (compact) {
            background.getPoints().addAll(new Double[]{
                hboxBounds.getMinX(), hboxBounds.getMinY(),
                hboxBounds.getMaxX(), hboxBounds.getMinY(),
                hboxBounds.getMaxX(), hboxBounds.getMaxY(),
                hboxBounds.getMinX(), hboxBounds.getMaxY()
            });
        } else {
            Bounds knotBounds = knot.getBoundsInParent(); //Bowtie.this.sceneToLocal(knot.localToScene(knot.getBoundsInLocal()));

            background.getPoints().addAll(new Double[]{
                // Top left corner
                hboxBounds.getMinX() - BG_OFFSET, hboxBounds.getMinY() - BG_OFFSET,
                knotBounds.getMinX(), hboxBounds.getMinY() - BG_OFFSET,
                knotBounds.getMinX() + knotBounds.getWidth() / 2, hboxBounds.getMinY() + hboxBounds.getHeight() / 2,
                knotBounds.getMaxX(), hboxBounds.getMinY() - BG_OFFSET,
                // Top right corner
                hboxBounds.getMaxX() + BG_OFFSET, hboxBounds.getMinY() - BG_OFFSET,
                // Bottom right corner
                hboxBounds.getMaxX() + BG_OFFSET, hboxBounds.getMaxY() + BG_OFFSET,
                knotBounds.getMaxX(), hboxBounds.getMaxY() + BG_OFFSET,
                knotBounds.getMinX() + knotBounds.getWidth() / 2, hboxBounds.getMinY() + hboxBounds.getHeight() / 2,
                knotBounds.getMinX(), hboxBounds.getMaxY() + BG_OFFSET,
                // Bottom left corner
                hboxBounds.getMinX() - BG_OFFSET, hboxBounds.getMaxY() + BG_OFFSET
            });
        }
    }
}
