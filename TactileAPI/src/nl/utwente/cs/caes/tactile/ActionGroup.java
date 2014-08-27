package nl.utwente.cs.caes.tactile;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;

public class ActionGroup extends Group {
	private Set<ActionGroup> actionGroupsColliding = new HashSet<ActionGroup>();
	private Set<ActionGroup> actionGroupsInProximity = new HashSet<ActionGroup>();
	
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
	
	public Set<ActionGroup> getActionGroupsCollidingUnmodifiable() {
		return Collections.unmodifiableSet(actionGroupsColliding);
	}
	
	public Set<ActionGroup> getActionGroupsInProximityUnmodifiable() {
		return Collections.unmodifiableSet(actionGroupsColliding);
	}
	
	protected Set<ActionGroup> getActionGroupsColliding(){
		return actionGroupsColliding;
	}
	
	protected Set<ActionGroup> getActionGroupsInProximity(){
		return actionGroupsInProximity;
	}
}
