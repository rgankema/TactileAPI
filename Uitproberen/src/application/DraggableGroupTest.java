package application;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import nl.utwente.cs.caes.tactile.ActionGroup;
import nl.utwente.cs.caes.tactile.DraggableGroup;
import nl.utwente.cs.caes.tactile.TouchPane;

public class DraggableGroupTest extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage primaryStage) {
		primaryStage.setTitle("The test");
		Group root = new Group();
		Scene scene = new Scene(root, 600, 600);

		TouchPane tp = new TouchPane();

		for (int i = 0; i < 25; i++) {
			FlowPane panel1 = new FlowPane();
			ActionGroup ag1 = new ActionGroup(new Rectangle(0, 0, 25, 25));
			panel1.getChildren().add(ag1);
			panel1.getChildren().add(new Label("LABEL! :D"));
			DraggableGroup draggableGroup1 = new DraggableGroup(panel1);
			tp.getChildren().add(draggableGroup1);
			draggableGroup1.setTranslateX(Math.random()*550);
			draggableGroup1.setTranslateY(Math.random()*550);
			tp.register(ag1);
		}

		root.getChildren().add(tp);

		primaryStage.setScene(scene);
		primaryStage.show();
	}

}