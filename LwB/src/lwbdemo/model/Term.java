/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lwbdemo.model;

import javafx.beans.property.StringProperty;

/**
 *
 * @author Richard
 */
public abstract class Term {
    public String getString() {
        return stringProperty().get();
    }
    
    public abstract StringProperty stringProperty();
    
    public abstract boolean canBeSet(Term term);
    
    public abstract Term getTerm();
    
    public abstract boolean setTerm(Term term);
}
