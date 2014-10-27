package lwbdemo;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lwbdemo.ui.Bowtie;
import nl.utwente.cs.caes.tactile.control.TactilePane;
import nl.utwente.cs.caes.tactile.debug.DebugParent;

public class Main extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        TactilePane tactilePane = new TactilePane();
        tactilePane.setBordersCollide(true);
        
        tactilePane.getChildren().add(new Bowtie("map", "(a->b)", "[a]", "[b]"));
        
        for (Node child : tactilePane.getChildren()) {
            TactilePane.setSlideOnRelease(child, true);
        }
        
        BorderPane root = new BorderPane();
        root.setCenter(tactilePane);
        
        DebugParent debug = new DebugParent(root);
        debug.registerTactilePane(tactilePane);
        
        Scene scene = new Scene(debug, 800, 600);
        
        stage.setScene(scene);
        stage.show();
    }
    
    public static void main(String[] args) {
        Main.launch(args);
    }
}
