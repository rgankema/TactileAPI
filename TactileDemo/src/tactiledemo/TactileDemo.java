package tactiledemo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
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
        
        Scene scene = new Scene(debug);
        //stage.setFullScreen(true);
        stage.setOnCloseRequest(event -> { Platform.exit(); });
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
