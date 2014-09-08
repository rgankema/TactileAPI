package application;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import nl.utwente.cs.caes.tactile.ActionGroup;
import nl.utwente.cs.caes.tactile.DebugParent;
import nl.utwente.cs.caes.tactile.DraggableGroup;
import nl.utwente.cs.caes.tactile.TouchPane;
import nl.utwente.cs.caes.tactile.event.ActionGroupEvent;

public class Uitproberen2 extends Application {
	List<ActionGroup> removedAction = new ArrayList<ActionGroup>();
	List<DraggableGroup> removedDraggable = new ArrayList<DraggableGroup>();
	
	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage primaryStage) throws SocketException {
		primaryStage.setTitle("Uitproberen");
		

		TouchPane tp = new TouchPane();
		tp.setBackground(new Background(new BackgroundFill(Color.GREY, null, null)));
		tp.setBordersCollide(true);
		tp.setProximityThreshold(30);
		tp.setPrefSize(1000, 600);

		DebugParent root = new DebugParent(tp);
		
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

}
