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
import nl.utwente.cs.caes.tactile.ActivePane;
import nl.utwente.cs.caes.tactile.DragPane;
import nl.utwente.cs.caes.tactile.TouchPane;
import nl.utwente.cs.caes.tactile.debug.DebugParent;
import nl.utwente.cs.caes.tactile.event.ActivePaneEvent;

public class Uitproberen extends Application {
	List<ActivePane> removedActivePanes = new ArrayList<>();
	List<DragPane> removedDragPanes = new ArrayList<>();
	
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
			
			ActivePane ap = new ActivePane(rect);
			ap.setId(Integer.toString(i));
			
			fp.getChildren().add(ap);
			fp.getChildren().add(new Label(Integer.toString(i)));
			
			DragPane dp = new DragPane(fp);
			tp.getChildren().add(dp);
			dp.relocate(Math.random()*950, Math.random()*500);
			tp.register(ap);
			
			ap.addEventFilter(ActivePaneEvent.ANY, event -> {
				System.out.println(event.getEventType() + ": " + event.getTarget() + "->" + event.getOther());
			});
			
			ap.addEventHandler(ActivePaneEvent.PROXIMITY_ENTERED, (ActivePaneEvent event) -> {
                            if (Integer.parseInt(event.getOther().getId()) % 3 != Integer.parseInt(event.getTarget().getId()) % 3){
                                event.getOther().moveAwayFrom(event.getTarget(), tp.getProximityThreshold() * 10);
                            }
                        });
			
			ap.addEventHandler(ActivePaneEvent.AREA_ENTERED, (ActivePaneEvent event) -> {
                            if (!event.getTarget().getDragPaneParent().isInUse() && Integer.parseInt(event.getOther().getId()) % 3 != Integer.parseInt(event.getTarget().getId()) % 3){
                                tp.deregister(event.getTarget());
                                tp.getChildren().remove(event.getTarget().getDragPaneParent());
                                debugParent.deregister(event.getTarget().getDragPaneParent());
                                removedActivePanes.add(event.getTarget());
                                removedDragPanes.add(event.getTarget().getDragPaneParent());
                            }
                            event.consume();
                        });
			debugParent.register(dp);
		}
		
		CheckBox checkSlide = new CheckBox("Slide on release");
		for (Node child : tp.getChildren()) {
			if (child instanceof DragPane) {
				checkSlide.selectedProperty().bindBidirectional(((DragPane) child).slideOnReleaseProperty());
			}
		}
		checkSlide.setSelected(true);
		
		CheckBox checkCollision = new CheckBox("Walls collide");
		checkCollision.selectedProperty().bindBidirectional(tp.bordersCollideProperty());
		
		Slider proximitySlider = new Slider(0, 200, 30);
		proximitySlider.valueProperty().bindBidirectional(tp.proximityThresholdProperty());
		
                
		Button resetButton = new Button("Reset");
		resetButton.setOnAction((ActionEvent event) -> {
                    for (DragPane d : removedDragPanes) {
                        tp.getChildren().add(d);
                        debugParent.register(d);
                    }
                    removedDragPanes.clear();
                    for (ActivePane a : removedActivePanes) {
                        tp.register(a);
                    }
                    removedActivePanes.clear();
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
