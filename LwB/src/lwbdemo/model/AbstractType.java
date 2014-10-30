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
public class AbstractType extends Term{
    final String name;
    Term term;
    
    public AbstractType(String name) {
        this.name = name;
    }
    
    @Override
    public boolean canBeSet(Term term) {
        return true;
    }
    
    @Override
    public boolean setTerm(Term term) {
        if (this.term != null) {
            if (term != null) {
                return this.term.setTerm(term);
            } else {
                this.term = term;
                stringProperty().unbind();
                stringProperty().set(this.name);
            }
        } else {
            if (term != null) {
                this.term = term;
                stringProperty().bind(term.stringProperty());
            } else {
                this.term = term;
                stringProperty().unbind();
                stringProperty().set(this.name);
            }
        }
        return true;
    }
    
    public Term getTerm() {
        return term;
    }
    
    private StringProperty string;
    
    @Override
    public StringProperty stringProperty() {
        if (string == null) {
            string = new SimpleStringProperty(name);
        }
        return string;
    }
}
