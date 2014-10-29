/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lwbdemo.model;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Richard
 */
public class ListType extends Term{
    final Term initialTerm;
    
    Term term;
    
    public ListType(Term term) {
        this.initialTerm = term;
        this.term = term;
    }
    
    @Override
    public boolean canBeSet(Term term) {
        return (term == null || term instanceof ListType);
    }
    
    public boolean setTerm(Term term) {
        if (term == null) {
            return this.term.setTerm(null);
        }
        if (term instanceof ListType) {
            ListType list = (ListType) term;
            return this.term.setTerm(list.getTerm());
        }
        return false;
    }
    
    public Term getTerm() {
        return term;
    }
    
    public StringProperty string;
    
    public StringProperty stringProperty() {
        if (string == null) {
            string = new SimpleStringProperty();
            string.bind(Bindings.concat("[", this.term.stringProperty(), "]"));
        }
        return string;
    }
    
    
}
