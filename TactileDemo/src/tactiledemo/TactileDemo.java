package tactiledemo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.utwente.cs.caes.tactile.control.TactilePane;
import nl.utwente.cs.caes.tactile.debug.DebugParent;

public class TactileDemo extends Application {
    DebugParent debug;
    
    @Override
    public void start(Stage stage) throws Exception {
        TactilePane root = (TactilePane) FXMLLoader.load(getClass().getResource("Main.fxml"));
        
        debug = new DebugParent(root);
        debug.registerTactilePane(root);
        registerActiveNodes(root, root);
        debug.setMapMouseToTouch(false);
        
        Scene scene = new Scene(debug);
        stage.setFullScreen(true);
        stage.setScene(scene);
        stage.show();
    }
    
    // Looks for any active node and registers it to the debugger
    private void registerActiveNodes(TactilePane tactilePane, Parent parent) {
        for (Node node: parent.getChildrenUnmodifiable()) {
            if (node instanceof Parent) {
                registerActiveNodes(tactilePane, (Parent) node);
            }
            if (TactilePane.getTracker(node) != null) {
                debug.registerActiveNode(node, TactilePane.getTracker(node));
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
