package tactiledemo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.utwente.cs.caes.tactile.ActivePane;
import nl.utwente.cs.caes.tactile.DragPane;
import nl.utwente.cs.caes.tactile.TouchPane;
import nl.utwente.cs.caes.tactile.debug.DebugParent;

public class TactileDemo extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        TouchPane root = (TouchPane) FXMLLoader.load(getClass().getResource("Main.fxml"));
        
        registerActivePanes(root, root);
        
        DebugParent debug = new DebugParent(root);
        for (Node child : root.getChildren()) {
            if (child instanceof DragPane) {
                debug.register((DragPane)child);
            }
        }
        debug.setMapMouseToTouch(false);
        
        Scene scene = new Scene(debug);
        stage.setFullScreen(true);
        stage.setScene(scene);
        stage.show();
    }
    
    private void registerActivePanes(TouchPane touchPane, Parent parent) {
        for (Node node: parent.getChildrenUnmodifiable()) {
            if (node instanceof Parent) {
                registerActivePanes(touchPane, (Parent) node);
            }
            if (node instanceof ActivePane) {
                touchPane.register((ActivePane) node);
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
