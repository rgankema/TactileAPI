package nl.utwente.cs.caes.tactile.skin;

import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import nl.utwente.cs.caes.tactile.ActivePane;


public class ActivePaneSkin extends SkinBase<ActivePane> {
    
    public ActivePaneSkin(final ActivePane activePane) {
        super(activePane);
        
        activePane.contentProperty().addListener((observable, oldContent, newContent) -> {
            getChildren().remove(oldContent);
            getChildren().add(newContent);
        });
        
        Node content = activePane.getContent();
        if (content != null) {
            getChildren().add(content);
        }
        
        consumeMouseEvents(false);
    }
}
