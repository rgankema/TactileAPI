package application;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import nl.utwente.cs.caes.tactile.ActionGroup;
import nl.utwente.cs.caes.tactile.DraggableGroup;

public class Uitproberen2 extends Application {
	List<ActionGroup> removedAction = new ArrayList<ActionGroup>();
	List<DraggableGroup> removedDraggable = new ArrayList<DraggableGroup>();
	
	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage primaryStage) throws SocketException {
		primaryStage.setTitle("Uitproberen");

		BorderPane root = new BorderPane();
		Button button = new Button("Click Me");
		button.setOnAction(event -> {
			System.out.println("1");
		});
		button.setOnAction(event -> {
			System.out.println("2");
		});
		button.addEventHandler(ActionEvent.ANY, event -> {
			System.out.println("3");
		});
		button.addEventHandler(ActionEvent.ANY, event -> {
			System.out.println("4");
		});
		root.setCenter(button);
		
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

}
