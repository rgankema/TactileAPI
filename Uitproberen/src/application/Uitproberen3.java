/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import nl.utwente.cs.caes.tactile.TactilePane;

/**
 *
 * @author Richard
 */
public class Uitproberen3 extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        TactilePane root = new TactilePane();
        //root.setPrefSize(600, 400);
        root.setBackground(new Background(new BackgroundFill(Color.ALICEBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        
        Circle c1 = new Circle(50);
        Circle c2 = new Circle(50);
        Circle c3 = new Circle(50);
        Circle c4 = new Circle(50);
        c1.relocate(0, 0);
        c2.relocate(100, 70);
        c3.relocate(60, 60);
        c4.relocate(120, 150);
        root.getChildren().addAll(c1, c2, c3, c4);
                
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
