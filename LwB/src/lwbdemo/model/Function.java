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
public class Function extends Term {
    final Term[] terms;
    
    public Function(Term... terms) {
        if (terms.length < 2) {
            throw new IllegalArgumentException();
        }
        
        this.terms = terms;
    }
    
    public Term[] getArguments() {
        return terms;
    }
    
    private StringProperty string;
    
    public StringProperty stringProperty() {
        if (string == null) {
            string = new SimpleStringProperty();
            
            StringProperty[] stringProperties = new StringProperty[terms.length];
            for (int i = 0; i < terms.length; i++) {
                stringProperties[i] = terms[i].stringProperty();
            }
            
            Object[] concatArgs = new Object[stringProperties.length * 2 + 1];
            int i = 0;
            concatArgs[i] = "(";
            for (i = 1; i < concatArgs.length - 1; i++) {
                
                if ((i-1) % 2 == 0) {
                    concatArgs[i] = stringProperties[(i-1) / 2];
                } else {
                    concatArgs[i] = "->";
                }
            }
            concatArgs[i] = ")";
            
            string.bind(Bindings.concat(concatArgs));
        }
        return string;
    }
    
    @Override
    public boolean canBeSet(Term term) {
        if (term == null) {
            return true;
        }
        // Klopt bij lange na niet, geeft alleen een idee
        if (term instanceof Function) {
            Function other = (Function) term;
            if (this.terms.length == other.terms.length) {
                for (int i = 0; i < terms.length; i++) {
                    if (!this.terms[i].canBeSet(other.terms[i])) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean setTerm(Term term) {
        if (term == null) {
            for (int i = 0; i < terms.length; i++) {
                this.terms[i].setTerm(null);
            }
            return true;
        }
        if (canBeSet(term)) {
            Function other = (Function) term;
            for (int i = 0; i < terms.length; i++) {
                this.terms[i].setTerm(other.terms[i]);
            }
            return true;
        }
        return false;
    }
    
    public Term getTerm() {
        return this;
    }

    
}
