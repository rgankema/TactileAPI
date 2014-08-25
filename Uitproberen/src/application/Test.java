/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package application;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.shape.Rectangle;
import nl.utwente.cs.caes.tactile.QuadTree;


/**
 *
 * @author 
 */
public class Test {
    
    Map<Object, Rectangle> objectToRectangle;
    
    public static void main(String[] args) {
        Rectangle rectangle1;
        rectangle1 = new Rectangle(0.0, 0.0, 50.0, 51.0);
        Rectangle rectangle2;
        rectangle2 = new Rectangle(5.0, 5.0, 30.0, 31.0);
        QuadTree qt = new QuadTree(rectangle1);
        qt.insert(rectangle2);
        System.out.println(qt.retrieve(rectangle2));
    }

    public Test() {
        this.objectToRectangle = new HashMap<>();
    }
    
    
    public Rectangle getRectangle(Object object){
        return objectToRectangle.get(object);
    }
}
