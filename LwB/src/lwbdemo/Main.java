package lwbdemo;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lwbdemo.ui.Bowtie;
import lwbdemo.model.Function;
import lwbdemo.model.List;
import lwbdemo.model.VariableType;
import lwbdemo.model.ConstantType;
import nl.utwente.cs.caes.tactile.control.TactilePane;
import nl.utwente.cs.caes.tactile.debug.DebugParent;

public class Main extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        TactilePane tactilePane = new TactilePane();
        
        // map bowtie
        VariableType a = new VariableType("a");
        VariableType b = new VariableType("b");
        Function f1 = new Function(new VariableType("a"), new VariableType("b"));
        Bowtie btFuncMap = new Bowtie(tactilePane, "map", f1, new List(a), new List(b));
        // cost bowtie
        VariableType c = new VariableType("c");
        VariableType d = new VariableType("d");
        Bowtie btFuncConst = new Bowtie(tactilePane, "const", c, d, c);
        // length bowtie
        VariableType e = new VariableType("e");
        Bowtie btFuncLength = new Bowtie(tactilePane, "length", new List(e), ConstantType.INT);
        // add bowtie
        Bowtie btFuncAdd = new Bowtie(tactilePane, "add", ConstantType.INT, ConstantType.INT, ConstantType.INT);
        // "foo" bowtie
        Bowtie btStringFoo = new Bowtie(tactilePane, "\"foo\"", new List(ConstantType.CHAR));
        // 1 bowtie
        Bowtie btInt1 = new Bowtie(tactilePane, "1", ConstantType.INT);
        
        tactilePane.setBordersCollide(true);
        tactilePane.getChildren().addAll(btFuncMap, btFuncConst, btFuncLength, btFuncAdd, btStringFoo, btInt1);
        for (Node child : tactilePane.getChildren()) {
            TactilePane.setSlideOnRelease(child, true);
        }
        
        DebugParent debug = new DebugParent(tactilePane);
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
