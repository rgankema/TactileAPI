package application;

import java.net.SocketException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import nl.utwente.cs.caes.tactile.ActionGroup;
import nl.utwente.cs.caes.tactile.DraggableGroup;
import nl.utwente.cs.caes.tactile.TouchPane;
import nl.utwente.cs.caes.tactile.event.ActionGroupEvent;

public class Uitproberen extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage primaryStage) throws SocketException {
		primaryStage.setTitle("The test");
		Group root = new Group();
		Scene scene = new Scene(root, 800, 800);

		TouchPane tp = new TouchPane();
		tp.setProximityThreshold(30);
		
		for (int i = 0; i < 15; i++) {
			FlowPane fp = new FlowPane();
			fp.setPrefWidth(60);
			fp.setBackground(new Background(new BackgroundFill(Color.YELLOW, null, null)));
			
			Rectangle rect = new Rectangle(0, 0, 25, 25);
			if (i % 3 == 0)
				rect.setFill(Color.RED);
			else if (i % 3 == 1)
				rect.setFill(Color.BLUE);
			else
				rect.setFill(Color.GREEN);
			
			ActionGroup ag = new ActionGroup(rect);
			ag.setId(Integer.toString(i % 3));
			
			fp.getChildren().add(ag);
			fp.getChildren().add(new Label(Integer.toString(i)));
			
			DraggableGroup dg = new DraggableGroup(fp);
			tp.getChildren().add(dg);
			dg.setTranslateX(Math.random()*750);
			dg.setTranslateY(Math.random()*750);
			tp.register(ag);
			
			ag.addEventHandler(ActionGroupEvent.ANY, new EventHandler<ActionGroupEvent>() {
				@Override
				public void handle(ActionGroupEvent event) {
					System.out.println(event.getEventType()+" "+event.getSource()+"->"+event.getTarget());
				}
			});
			
			ag.addEventHandler(ActionGroupEvent.PROXIMITY_ENTERED, new EventHandler<ActionGroupEvent>() {
				@Override
				public void handle(ActionGroupEvent event) {
					if (!event.getOtherGroup().getDraggableGroupParent().isActive() && !event.getOtherGroup().getId().equals(event.getTarget().getId())){
						event.getOtherGroup().moveAwayFrom(event.getTarget(), tp.getProximityThreshold() * 2.5);
					}
					event.consume();
				}				
			});
		}

		root.getChildren().add(tp);

		primaryStage.setScene(scene);
		primaryStage.show();
	}

}