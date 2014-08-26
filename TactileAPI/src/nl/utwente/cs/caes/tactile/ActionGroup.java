package nl.utwente.cs.caes.tactile;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;

public class ActionGroup extends Group {
	
	public ActionGroup() {
		super();
	}

	public ActionGroup(Node... nodes) {
		super(nodes);
	}

	/**
	 * Finds the first ancestor of this ActionGroup that is a DraggableGroup
	 * 
	 * @return A DraggableGroup that is the first ancestor of this ActionGroup,
	 *         null if there is no such ancestor
	 */
	public final DraggableGroup getDraggableGroupParent() {
		Parent ancestor = this;
		while (!(ancestor instanceof DraggableGroup)) {
			ancestor = ancestor.getParent();
		}
		if (!(ancestor instanceof DraggableGroup)) {
			ancestor = null;
		}
		return (DraggableGroup) ancestor;
	}
}
