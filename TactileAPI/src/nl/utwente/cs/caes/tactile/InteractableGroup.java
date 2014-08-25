package nl.utwente.cs.caes.tactile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.scene.Group;
import javafx.scene.Node;

public class InteractableGroup extends Group {
	// Not sure if necessary yet
	private Set<InteractableGroup> objectsInProximity = new HashSet<InteractableGroup>();
	private Set<InteractableGroup> objectsColliding = new HashSet<InteractableGroup>();
	
	public InteractableGroup() {
		super();
	}
	
	public InteractableGroup(Node... nodes){
		super(nodes);
	}
}
