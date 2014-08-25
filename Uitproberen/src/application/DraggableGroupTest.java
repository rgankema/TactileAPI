package application;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import nl.utwente.cs.caes.tactile.DraggableGroup;


public class DraggableGroupTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("The test");
        Group root = new Group();
        Scene scene = new Scene(root, 400, 400);

        FlowPane panel1 = new FlowPane();
        panel1.getChildren().add(new Button("Doet niets!"));
        panel1.getChildren().add(new Label("LABEL! :D"));
        
        FlowPane panel2 = new FlowPane();
        panel2.getChildren().add(new Button("Doet ook niets!"));
        panel2.getChildren().add(new Label("NOG EEN LABEL! :D"));

        DraggableGroup draggableGroup1 = new DraggableGroup(panel1);
        DraggableGroup draggableGroup2 = new DraggableGroup(panel2);
        
        root.getChildren().add(draggableGroup1);
        root.getChildren().add(draggableGroup2);
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    
}