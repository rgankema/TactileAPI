package lwbdemo;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lwbdemo.ui.Bowtie;
import lwbdemo.model.Function;
import lwbdemo.model.ListType;
import lwbdemo.model.AbstractType;
import lwbdemo.model.FinalType;
import nl.utwente.cs.caes.tactile.control.TactilePane;
import nl.utwente.cs.caes.tactile.debug.DebugParent;

public class Main extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        TactilePane tactilePane = new TactilePane();
        tactilePane.setBordersCollide(true);
        
        AbstractType a = new AbstractType("a");
        AbstractType b = new AbstractType("b");
        Function f1 = new Function(a, b);
        
        AbstractType c = new AbstractType("c");
        AbstractType d = new AbstractType("d");
        
        AbstractType e = new AbstractType("e");
        
        tactilePane.getChildren().add(new Bowtie(tactilePane, "add", new FinalType("Num"), new FinalType("Num"), new FinalType("Num")));
        tactilePane.getChildren().add(new Bowtie(tactilePane, "map", f1, new ListType(a), new ListType(b)));
        tactilePane.getChildren().add(new Bowtie(tactilePane, "\"foo\"", new ListType(new FinalType("Char"))));
        tactilePane.getChildren().add(new Bowtie(tactilePane, "const", c, d, c));
        tactilePane.getChildren().add(new Bowtie(tactilePane, "length", new ListType(e), new FinalType("Num")));
        tactilePane.getChildren().add(new Bowtie(tactilePane, "1", new FinalType("Num")));
        
        for (Node child : tactilePane.getChildren()) {
            TactilePane.setSlideOnRelease(child, true);
        }
        
        BorderPane root = new BorderPane();
        root.setCenter(tactilePane);
        
        DebugParent debug = new DebugParent(root);
        debug.setOverlayVisible(false);
        debug.registerTactilePane(tactilePane);
        
        Scene scene = new Scene(debug, 800, 600);
        
        stage.setScene(scene);
        stage.show();
    }
    
    public static void main(String[] args) {
        Main.launch(args);
    }
}
