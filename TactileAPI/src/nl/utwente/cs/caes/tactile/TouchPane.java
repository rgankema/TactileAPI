package nl.utwente.cs.caes.tactile;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

public class TouchPane extends Pane {
	
	public TouchPane() {
		super();
		initialise();
	}

	public TouchPane(Node... children) {
		super(children);
		initialise();
	}

	// Called by all constructors
	private void initialise() {
		setPhysics(new Physics(this));
		getPhysics().start();
	}
	
	/**
	 * The {@Physics} object for this TouchPane
	 */
	private ReadOnlyObjectWrapper<Physics> physics;
	
	protected void setPhysics(Physics value) {
		physicsProperty().set(value);
	}
	
	public Physics getPhysics() {
		return physicsProperty().get();
	}
	
	protected ReadOnlyObjectWrapper<Physics> physicsProperty() {
		if (physics == null) {
			physics = new ReadOnlyObjectWrapper<Physics>();
		}
		return physics;
	}
	
	public ReadOnlyObjectProperty<Physics> readOnlyPhysicsProperty() {
		return physicsProperty().getReadOnlyProperty();
	}
	
	/**
	 * Registers an ActionGroup to the {@code TouchPane}. The TouchPane will track the
	 * position of the ActionGroup and check for collisions / proximity events.
	 * The ActionGroup should have the controlled {@code TouchPane} as (indirect) ancestor.
	 * 
	 * @param actionGroup
	 *            The ActionGroup that is to be tracked
	 * @throws IllegalArgumentException
	 *             If the ActionGroup does not have this TouchPane as (indirect)
	 *             ancestor
	 */
	public void register(ActionGroup actionGroup) {
		if (getPhysics().actionGroups.add(actionGroup)) {
			Parent ancestor = actionGroup.getParent();
			while (ancestor != this) {
				try {
					ancestor = ancestor.getParent();
				} catch (NullPointerException e) {
					throw new IllegalArgumentException(
							"The provided ActionGroup does not have this TouchPane as ancestor!");
				}
			}
			getPhysics().quadTree.insert(actionGroup);
		}
	}

	/**
	 * Deregisters an ActionGroup from the {@code TouchPane}.
	 * 
	 * @param actionGroup
	 *            The ActionGroup that shoud be deregistered
	 */
	public void deregister(ActionGroup actionGroup) {
		for (ActionGroup ag : actionGroup.getActionGroupsColliding()) {
			ag.getActionGroupsColliding().remove(actionGroup);
		}
		actionGroup.getActionGroupsColliding().clear();
		for (ActionGroup ag : actionGroup.getActionGroupsInProximity()) {
			ag.getActionGroupsInProximity().remove(actionGroup);
		}
		actionGroup.getActionGroupsInProximity().clear();
		
		getPhysics().actionGroups.remove(actionGroup);
		getPhysics().quadTree.remove(actionGroup);
	}
}
