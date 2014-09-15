package application;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Slider;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import nl.utwente.cs.caes.tactile.ActionGroup;
import nl.utwente.cs.caes.tactile.DraggableGroup;
import nl.utwente.cs.caes.tactile.TouchPane;
import nl.utwente.cs.caes.tactile.debug.DebugParent;
import nl.utwente.cs.caes.tactile.event.ActionGroupEvent;

public class Uitproberen extends Application {
	List<ActionGroup> removedAction = new ArrayList<>();
	List<DraggableGroup> removedDraggable = new ArrayList<>();
	
	public static void main(String[] args) {
		launch(args);
	}

        @Override
	public void start(Stage primaryStage) throws SocketException {
		primaryStage.setTitle("Uitproberen");

		TouchPane tp = new TouchPane();
		tp.setBackground(new Background(new BackgroundFill(Color.GREY, null, null)));
		tp.setBordersCollide(true);
		tp.setProximityThreshold(30);
		tp.setPrefSize(1000, 600);
		
		FlowPane buttonPane = new FlowPane();
		
		ScrollPane sp1 = new ScrollPane(tp);
		sp1.setVbarPolicy(ScrollBarPolicy.NEVER);
		sp1.setHbarPolicy(ScrollBarPolicy.NEVER);
		sp1.setPannable(true);
		sp1.setCursor(Cursor.DEFAULT);
		
		BorderPane bp = new BorderPane();
		bp.setCenter(sp1);
		bp.setBottom(buttonPane);
		
		DebugParent debugParent = new DebugParent(bp);
		Scene scene = new Scene(debugParent);
		//Scene scene = new Scene(bp);
		
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
			ag.setId(Integer.toString(i));
			
			fp.getChildren().add(ag);
			fp.getChildren().add(new Label(Integer.toString(i)));
			
			DraggableGroup dg = new DraggableGroup(fp);
			tp.getChildren().add(dg);
			dg.relocate(Math.random()*950, Math.random()*500);
			tp.register(ag);
			
			ag.addEventFilter(ActionGroupEvent.ANY, event -> {
				System.out.println(event.getEventType() + ": " + event.getTarget() + "->" + event.getOtherGroup());
			});
			
			ag.addEventHandler(ActionGroupEvent.PROXIMITY_ENTERED, (ActionGroupEvent event) -> {
                            if (Integer.parseInt(event.getOtherGroup().getId()) % 3 != Integer.parseInt(event.getTarget().getId()) % 3){
                                event.getOtherGroup().moveAwayFrom(event.getTarget(), tp.getProximityThreshold() * 10);
                            }
                        });
			
			ag.addEventHandler(ActionGroupEvent.DROPPED, (ActionGroupEvent event) -> {
                            if (Integer.parseInt(event.getOtherGroup().getId()) % 3 != Integer.parseInt(event.getTarget().getId()) % 3){
                                tp.deregister(event.getOtherGroup());
                                tp.getChildren().remove(event.getOtherGroup().getDraggableGroupParent());
                                debugParent.deregister(event.getOtherGroup().getDraggableGroupParent());
                                removedAction.add(event.getOtherGroup());
                                removedDraggable.add(event.getOtherGroup().getDraggableGroupParent());
                            }
                            event.consume();
                        });
			debugParent.register(dg);
		}
		
		CheckBox checkSlide = new CheckBox("Slide on release");
		for (Node child : tp.getChildren()) {
			if (child instanceof DraggableGroup) {
				checkSlide.selectedProperty().bindBidirectional(((DraggableGroup) child).slideOnReleaseProperty());
			}
		}
		checkSlide.setSelected(true);
		
		CheckBox checkCollision = new CheckBox("Walls collide");
		checkCollision.selectedProperty().bindBidirectional(tp.bordersCollideProperty());
		
		Slider proximitySlider = new Slider(0, 200, 30);
		proximitySlider.valueProperty().bindBidirectional(tp.proximityThresholdProperty());
		
		Button resetButton = new Button("Reset");
		resetButton.setOnAction((ActionEvent event) -> {
                    for (DraggableGroup d : removedDraggable) {
                        tp.getChildren().add(d);
                        debugParent.register(d);
                    }
                    removedDraggable.clear();
                    for (ActionGroup a : removedAction) {
                        tp.register(a);
                    }
                    removedAction.clear();
                });
		
		buttonPane.getChildren().add(checkSlide);
		buttonPane.getChildren().add(checkCollision);
		buttonPane.getChildren().add(proximitySlider);
		buttonPane.getChildren().add(new Label("Proximity threshold"));
		buttonPane.getChildren().add(resetButton);
		
		debugParent.setMapMouseToTouch(true);
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}

}
