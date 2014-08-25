package application;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;


public class CollisionTest extends Application {


    private ArrayList<Rectangle> rectangleArrayList;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("The test");
        Group root = new Group();
        Scene scene = new Scene(root, 400, 400);

        rectangleArrayList = new ArrayList<Rectangle>();
        
        for(int i = 0; i < 1000; i++){
        	Rectangle block = new Rectangle(30.0, 30.0, Color.GREEN);
        	
        	rectangleArrayList.add(block);
            setDragListeners(block);
        }
        root.getChildren().addAll(rectangleArrayList);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void setDragListeners(final Rectangle block) {
        final Delta dragDelta = new Delta();

        block.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // record a delta distance for the drag and drop operation.
                dragDelta.x = block.getTranslateX() - mouseEvent.getSceneX();
                dragDelta.y = block.getTranslateY() - mouseEvent.getSceneY();
                block.setCursor(Cursor.NONE);
            }
        });
        block.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                block.setCursor(Cursor.HAND);
            }
        });
        block.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                block.setTranslateX(mouseEvent.getSceneX() + dragDelta.x);
                block.setTranslateY(mouseEvent.getSceneY() + dragDelta.y);
                checkBounds(block);

            }
        });
    }

    private void checkBounds(Shape block) {
    	  boolean collisionDetected = false;
    	  boolean approachDetected = false;
    	  for (Shape static_bloc : rectangleArrayList) {
    	    if (static_bloc != block) {
    	      static_bloc.setFill(Color.GREEN);
    	      
    	      Bounds boundsInParent = static_bloc.getBoundsInParent();
    	      Bounds boundsAroundParent = new BoundingBox(boundsInParent.getMinX() - 10,
    	    		  boundsInParent.getMinY() - 10, boundsInParent.getWidth() + 20,
    	    		  boundsInParent.getHeight() + 20);
    	      
    	      if (block.getBoundsInParent().intersects(static_bloc.getBoundsInParent())) {
    	    	  static_bloc.setFill(Color.BLUE);
    	    	  collisionDetected = true;
    	      } else if (block.getBoundsInParent().intersects(boundsAroundParent)) {
    	    	  static_bloc.setFill(Color.RED);
    	    	  approachDetected = true;
    	      } else {
    	    	  static_bloc.setFill(Color.GREEN);
    	      }
    	    }
    	  }

    	  if (collisionDetected) {
    	    block.setFill(Color.BLUE);
    	  } else if (approachDetected) {
    		block.setFill(Color.RED);  
    	  } else {
    	    block.setFill(Color.GREEN);
    	  }
    	}

    class Delta {
        double x, y;
    }
}