package application;

import java.net.SocketException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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

		TouchPane tp = new TouchPane();
		tp.setBackground(new Background(new BackgroundFill(Color.GREY, null, null)));
		tp.setBordersCollide(true);
		tp.setProximityThreshold(30);
		tp.setPrefSize(800, 800);
		
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
			dg.relocate(Math.random()*750, Math.random()*700);
			tp.register(ag);
			
			ag.addEventHandler(ActionGroupEvent.ANY, new EventHandler<ActionGroupEvent>() {
				@Override
				public void handle(ActionGroupEvent event) {
				//	System.out.println(event.getEventType()+" "+event.getSource()+"->"+event.getTarget());
				}
			});
			
			ag.addEventHandler(ActionGroupEvent.PROXIMITY_ENTERED, new EventHandler<ActionGroupEvent>() {
				@Override
				public void handle(ActionGroupEvent event) {
					if (!event.getOtherGroup().getDraggableGroupParent().isActive() && !event.getOtherGroup().getId().equals(event.getTarget().getId())){
						event.getOtherGroup().moveAwayFrom(event.getTarget(), tp.getProximityThreshold() * 25, 300);
					}
				}				
			});
			
			ag.addEventHandler(ActionGroupEvent.AREA_ENTERED, new EventHandler<ActionGroupEvent>() {
				@Override
				public void handle(ActionGroupEvent event) {
					if (!event.getOtherGroup().getDraggableGroupParent().isActive() && !event.getOtherGroup().getId().equals(event.getTarget().getId())){
						if (event.getTarget().getDraggableGroupParent().isActive()) {
							tp.deregister(event.getOtherGroup());
							tp.getChildren().remove(event.getOtherGroup().getDraggableGroupParent());
						}
					}
					event.consume();
				}				
			});
		}

		Parent root = new FlowPane(tp);
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

}