/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.cs.caes.tactile.skin;


import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import nl.utwente.cs.caes.tactile.DragPane;


public class DragPaneSkin extends SkinBase<DragPane> {
    
    public DragPaneSkin(final DragPane dragPane) {
        super(dragPane);
        
        dragPane.contentProperty().addListener((observable, oldContent, newContent) -> {
            getChildren().remove(oldContent);
            getChildren().add(newContent);
        });
        
        Node content = dragPane.getContent();
        if (content != null) {
            getChildren().add(content);
        }
    }
}
