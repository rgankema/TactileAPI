package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import nl.utwente.cs.caes.tactile.control.TactilePane;
import nl.utwente.cs.caes.tactile.debug.DebugParent;

public class QuadTreeTest extends Application {
    static final int RECTANGLES = 2;
    static final int CIRCLES = 5;
    static final int WIDTH = 1200;
    static final int HEIGHT = 900;
    
    DebugParent debug;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
    	BorderPane root = new BorderPane();
        
    	//Init TactilePane
        TactilePane tactilePane = new TactilePane();
        tactilePane.setBackground(new Background(new BackgroundFill(Color.ALICEBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        tactilePane.setBordersCollide(true);
        tactilePane.setProximityThreshold(0);
        
        for (Node node: tactilePane.getChildren()) {
            tactilePane.getActiveNodes().add(node);
        }
                
        // Init Control Pane
        FlowPane controlLayout = new FlowPane();
        CheckBox enableDebug = new CheckBox("Enable Debug Mode");
        enableDebug.setSelected(true);
        controlLayout.getChildren().add(enableDebug);
        
        //root.setLeft(new Button("Loze ruimte"));
        root.setCenter(tactilePane);
        root.setBottom(controlLayout);
        
        // Debug layer
        debug = new DebugParent(root);
        debug.overlayVisibleProperty().bindBidirectional(enableDebug.selectedProperty());
        debug.registerTactilePane(tactilePane);
        
        // Key bindings
        debug.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (event.getCode() == KeyCode.F11) {
                primaryStage.setFullScreen(!primaryStage.isFullScreen());
                event.consume();
            }
        });
        
        // Add new object on shift click
        tactilePane.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            if (event.isShiftDown()) {
                Rectangle rect = new Rectangle(event.getX(), event.getY(), 5, 5);
                tactilePane.getChildren().add(rect);
                TactilePane.setTracker(rect, tactilePane);
            }
        });
        
        Scene scene = new Scene(debug);
        primaryStage.setFullScreen(false);
        primaryStage.setOnCloseRequest(event -> { Platform.exit(); });
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
