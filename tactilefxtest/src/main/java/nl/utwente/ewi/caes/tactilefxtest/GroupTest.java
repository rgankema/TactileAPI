/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactilefxtest;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import nl.utwente.ewi.caes.tactilefx.control.TactilePane;

/**
 *
 * @author Richard
 */
public class GroupTest extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        AnchorPane root = new AnchorPane();
        
        TactilePane tp = new TactilePane();
        tp.setBordersCollide(true);
        tp.setPrefSize(500, 500);
        
        Group group = new Group();
        group.getChildren().add(new Rectangle(50, 50, 50, 50));
        group.getChildren().add(new Rectangle(150, 150, 50, 50));
        
        //TactilePane.setDraggable(group, false);
        tp.getChildren().add(group);
        tp.getChildren().add(new Circle(25));
        
        root.getChildren().add(tp);
        AnchorPane.setLeftAnchor(tp, 50d);
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
