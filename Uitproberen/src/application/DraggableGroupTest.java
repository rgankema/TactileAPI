package application;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import nl.utwente.cs.caes.tactile.ActionGroup;
import nl.utwente.cs.caes.tactile.DraggableGroup;
import nl.utwente.cs.caes.tactile.TouchPane;
import nl.utwente.cs.caes.tactile.event.CollisionEvent;

public class DraggableGroupTest extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage primaryStage) {
		primaryStage.setTitle("The test");
		Group root = new Group();
		Scene scene = new Scene(root, 800, 800);

		TouchPane tp = new TouchPane();

		for (int i = 0; i < 15; i++) {
			FlowPane panel1 = new FlowPane();
			Rectangle rect = new Rectangle(0, 0, 25, 25);
			if(i%3==0)
				rect.setFill(Color.RED);
			else if (i%3==1)
				rect.setFill(Color.BLUE);
			else
				rect.setFill(Color.GREEN);
			
			ActionGroup ag1 = new ActionGroup(rect);
			panel1.getChildren().add(ag1);
			panel1.getChildren().add(new Label("LABEL! :D"));
			DraggableGroup draggableGroup1 = new DraggableGroup(panel1);
			tp.getChildren().add(draggableGroup1);
			draggableGroup1.setTranslateX(Math.random()*750);
			draggableGroup1.setTranslateY(Math.random()*750);
			tp.register(ag1);
			
			ag1.addEventHandler(CollisionEvent.ANY, new EventHandler<CollisionEvent>(){

				@Override
				public void handle(CollisionEvent event) {
					System.out.println(event.getEventType()+" "+event.getSource()+"->"+event.getTarget());
					event.consume();
				}				
			});
		}

		root.getChildren().add(tp);

		primaryStage.setScene(scene);
		primaryStage.show();
	}

}