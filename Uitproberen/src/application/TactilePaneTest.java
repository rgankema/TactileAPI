/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import nl.utwente.cs.caes.tactile.control.TactilePane;
import nl.utwente.cs.caes.tactile.debug.DebugParent;
import nl.utwente.cs.caes.tactile.event.TactilePaneEvent;

/**
 *
 * @author Richard
 */
public class TactilePaneTest extends Application {
    static final int RECTANGLES = 2;
    static final int CIRCLES = 5;
    static final int WIDTH = 800;
    static final int HEIGHT = 600;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        TactilePane root = new TactilePane();
        root.setPrefSize(WIDTH, HEIGHT);
        root.setBackground(new Background(new BackgroundFill(Color.ALICEBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        root.setBordersCollide(true);
        
        for (int i = 0; i < RECTANGLES; i++) {
            Rectangle rectangle = new Rectangle(80, 80);
            rectangle.relocate(Math.random() * (WIDTH - 80), Math.random() * (HEIGHT - 80));
            TactilePane.setDraggable(rectangle, false);
            root.getChildren().add(rectangle);
        }
        for (int i = 0; i < CIRCLES; i++) {
            Circle circle = new Circle(50);
            circle.relocate(Math.random() * (WIDTH - 100), Math.random() * (HEIGHT - 100));
            TactilePane.setSlideOnRelease(circle, true);
            TactilePane.setOnInProximity(circle, event -> {
                if (!TactilePane.isInUse(circle)) {
                    TactilePane.moveAwayFrom(circle, event.getOther(), 100);
                }
            });
            root.getChildren().add(circle);
        }
        for (Node node: root.getChildren()) {
            root.startTracking(node);
        }
        
        root.addEventFilter(TactilePaneEvent.ANY, event -> {
            System.out.println(event.getEventType());
        });
        
        // Debug meuk
        DebugParent debug = new DebugParent(root);
        debug.registerTactilePane(root);
        for (Node node : root.getChildren()) {
            debug.registerActiveNode(node, root);
        }
        
        Scene scene = new Scene(debug);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
