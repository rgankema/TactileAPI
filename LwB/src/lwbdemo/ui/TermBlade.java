/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lwbdemo.ui;

import javafx.scene.control.Label;

class TermBlade extends BowtieBlade {
    Label nameLabel;
    
    public TermBlade(Bowtie bowtie, String name) {
        super(bowtie);
        
        nameLabel = new Label(name);
        nameLabel.setFont(NAME_FONT);
        
        getChildren().add(nameLabel);
    }

    @Override
    protected void pushTerm(TermDisplay term) {
        term.setActive(true);
        getChildren().add(term);
    }

    @Override
    protected TermDisplay popTerm() {
        TermDisplay result = null;
        if (getChildren().size() > 1) {
            result = (TermDisplay) getChildren().get(getChildren().size() - 1);
            if (result.isActive()) {
                getChildren().remove(result);
            } else {
                result = null;
            }
        }
        return result;
    }
}
