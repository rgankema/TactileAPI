/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lwbdemo.ui;

import lwbdemo.model.Term;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import nl.utwente.cs.caes.tactile.control.TactilePane;
import nl.utwente.cs.caes.tactile.control.TactilePane.Anchor;

/**
 *
 * @author Richard
 */
public class Bowtie extends Group {
    static final double OFFSET = 10;
    
    
    final Knot knot;
    final HBox hbox;
    final TermBlade termBlade;
    final TypeBlade typeBlade;
    final Polygon background;
    
    private final String name;
    
    private boolean compact = false;
    private TermDisplay anchor = null;
    
    public Bowtie(TactilePane tracker, String name, Term... terms) {
        this.name = name;
        
        termBlade = new TermBlade(this, name);
        knot = new Knot();
        typeBlade = new TypeBlade(this, terms);
        
        hbox = new HBox();
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(termBlade, knot, typeBlade);
                
        // Draws the bowtie
        background = new Polygon();
        background.setFill(Color.BISQUE);
        background.setStroke(Color.BROWN);
        
        hbox.boundsInParentProperty().addListener((obs, oldVal, newVal) -> {
            drawBackground();
        });
        knot.boundsInParentProperty().addListener((obs, oldVal, newVal) -> {
            drawBackground();
        });
        
        getChildren().addAll(background, hbox);
        
        TactilePane.setTracker(typeBlade, tracker);
    }
    
    public String getName() {
        return this.name;
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
            //TactilePane.setTracker(typeBlade, null);
            
            shrink();
            
            TactilePane.setAnchor(this, new Anchor(termDisplay, Anchor.Pos.CENTER));
        }
        anchor = termDisplay;
    }
    
    private void shrink() {
        if (!compact) {
            hbox.getChildren().removeAll(knot, typeBlade);
            compact = true;
        }
    }
    
    private void grow() {
        if (compact) {
            hbox.getChildren().addAll(knot, typeBlade);
            compact = false;
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
                hboxBounds.getMinX() - OFFSET, hboxBounds.getMinY() - OFFSET,
                knotBounds.getMinX(), hboxBounds.getMinY() - OFFSET,
                knotBounds.getMinX() + knotBounds.getWidth() / 2, hboxBounds.getMinY() + hboxBounds.getHeight() / 2,
                knotBounds.getMaxX(), hboxBounds.getMinY() - OFFSET,
                // Top right corner
                hboxBounds.getMaxX() + OFFSET, hboxBounds.getMinY() - OFFSET,
                // Bottom right corner
                hboxBounds.getMaxX() + OFFSET, hboxBounds.getMaxY() + OFFSET,
                knotBounds.getMaxX(), hboxBounds.getMaxY() + OFFSET,
                knotBounds.getMinX() + knotBounds.getWidth() / 2, hboxBounds.getMinY() + hboxBounds.getHeight() / 2,
                knotBounds.getMinX(), hboxBounds.getMaxY() + OFFSET,
                // Bottom left corner
                hboxBounds.getMinX() - OFFSET, hboxBounds.getMaxY() + OFFSET
            });
        }
    }
    
    // NESTED CLASSES
    class Knot extends Circle {
        private static final double RADIUS = 15;
        private double anchorX;
        
        public Knot() {
            super(RADIUS);
            setFill(Color.RED);

            setOnMousePressed(event -> {
                // Record location of click event in Knot
                anchorX = event.getX();
                event.consume();
            });

            setOnMouseDragged(event -> {
                Bounds thisBounds = this.localToScene(getBoundsInLocal());
                double thisCenterX = thisBounds.getMinX() + thisBounds.getWidth() / 2;

                double x = event.getSceneX() - anchorX;

                if (x < thisCenterX && termBlade.getChildren().size() > 1) {
                    Node left = termBlade.getChildren().get(termBlade.getChildren().size() - 1);
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
                        coverHole();
                    }
                }

                if (x > thisCenterX && typeBlade.getChildren().size() > 1) {
                    Node right = typeBlade.getChildren().get(0);
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
                        exposeHole();
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
        }
    }
    
}
