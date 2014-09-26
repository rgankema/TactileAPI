package nl.utwente.cs.caes.tactile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.DefaultProperty;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import nl.utwente.cs.caes.tactile.skin.TactilePaneSkin;

@DefaultProperty("children")
public class TactilePane extends Control {
    // CONSTRUCTORS
    
    public TactilePane() {
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);
        // Since this Control is more or less a Pane, focusTraversable should be false by default
        ((StyleableProperty<Boolean>)focusTraversableProperty()).applyStyle(null, false); 
    }
    
    public TactilePane(Node... children) {
        this();
        getChildren().addAll(children);
    }
    
    // METHODS
    
    /**
     *
     * @return modifiable list of children.
     */
    @Override public ObservableList<Node> getChildren() {
        return super.getChildren();
    }
    
    // STYLESHEET HANDLING
    
    // The selector class
    private static String DEFAULT_STYLE_CLASS = "tactile-pane";
    // TODO PseudoClasses maken
    
    private static final class StyleableProperties {
        // TODO CSSMetaData maken voor properties

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
        static {
                final List<CssMetaData<? extends Styleable, ?>> styleables = 
                    new ArrayList<>(Control.getClassCssMetaData());

                STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }
    
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Skin<TactilePane> createDefaultSkin() {
        return new TactilePaneSkin(this);
    }
}
