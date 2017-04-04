package cdl.movementManager;

import java.util.EventObject;

import cdl.entities.Entity;
import cdl.util.Vector2D;

@SuppressWarnings("serial")
public class MovementEvent extends EventObject {

	private Vector2D mov;

	public MovementEvent(Entity source, Vector2D movement) {
		super(source);
		mov = movement;
	}

	@Override
	public Entity getSource() {
		return (Entity) super.source;
	}

	public Vector2D getMovement() {
		return mov;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof MovementEvent)
			return (this.source.equals(((MovementEvent) o).source));
		return false;
	}

	@Override
	public int hashCode() {
		return source.hashCode()+ mov.hashCode();
	}
}
