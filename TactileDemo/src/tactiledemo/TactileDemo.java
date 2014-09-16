package tactiledemo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.utwente.cs.caes.tactile.ActionGroup;
import nl.utwente.cs.caes.tactile.DraggableGroup;
import nl.utwente.cs.caes.tactile.TouchPane;
import nl.utwente.cs.caes.tactile.debug.DebugParent;

public class TactileDemo extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        TouchPane root = (TouchPane) FXMLLoader.load(getClass().getResource("Main.fxml"));
        
        registerActionGroups(root, root);
        
        DebugParent debug = new DebugParent(root);
        for (Node child : root.getChildren()) {
            if (child instanceof DraggableGroup) {
                debug.register((DraggableGroup)child);
            }
        }
        debug.setMapMouseToTouch(true);
        
        Scene scene = new Scene(debug);
        stage.setScene(scene);
        stage.show();
    }
    
    private void registerActionGroups(TouchPane touchPane, Parent parent) {
        for (Node node: parent.getChildrenUnmodifiable()) {
            if (node instanceof Parent) {
                registerActionGroups(touchPane, (Parent) node);
            }
            if (node instanceof ActionGroup) {
                touchPane.register((ActionGroup) node);
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
