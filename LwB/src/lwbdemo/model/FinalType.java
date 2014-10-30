/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lwbdemo.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Richard
 */
public class FinalType extends Term {
    
    public FinalType(String name) {
        string = new SimpleStringProperty(name);
    }
    
    private StringProperty string;
    
    @Override
    public StringProperty stringProperty() {
        return string;
    }

    @Override
    public boolean canBeSet(Term term) {
        return (term instanceof FinalType && term.getString().equals(this.getString()));
    }
    
    @Override
    public boolean setTerm(Term term) {
        return canBeSet(term);
    }
    
    @Override
    public Term getTerm() {
        return null;
    }
    
}
