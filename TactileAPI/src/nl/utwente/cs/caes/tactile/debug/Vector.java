package nl.utwente.cs.caes.tactile.debug;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

class Vector extends Pane{
	static Color COLOR_OPAQUE = new Color(0, 0, 1, 1);
	static Color COLOR_SEMI_TRANSPARENT = new Color(0, 0, 1, 0.5);
	private static double OFFSET =  10;
	
	private Line line = new Line(0, 0, 0, 0);
	private Label label = new Label("");
	
	public Vector(ObjectProperty<Point2D> vectorProperty) {
		line.setStroke(COLOR_OPAQUE);
		line.setStrokeWidth(2);
		label.setTextFill(COLOR_OPAQUE);
		
		getChildren().add(line);
		getChildren().add(label);
		
		vectorProperty.addListener(observable -> {
			update(vectorProperty.get());
		});
		
		update(vectorProperty.get());
	}
	
	private void update(Point2D vector) {
		double x = vector.getX();
		double y = vector.getY();
		
		if (vector.equals(Point2D.ZERO)) {
			line.setEndX(0);
			line.setEndY(0);
			line.setVisible(false);
			label.setText("");
			return;
		}
		
		line.setVisible(true);
		double labelOffsetX, labelOffsetY;
		
		line.setEndX(x);
		line.setEndY(y);
		label.setText(String.format("%1.2f, %1.2f", x, y));
		
		labelOffsetX = x > 0 ? OFFSET : -OFFSET - label.getWidth();
		labelOffsetY = y > 0 ? OFFSET : -OFFSET - label.getHeight();
		
		label.relocate(x / 2 + labelOffsetX, y / 2 + labelOffsetY);
	}
}
