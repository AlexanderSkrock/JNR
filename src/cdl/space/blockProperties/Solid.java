package cdl.space.blockProperties;

import java.awt.geom.Point2D;

import cdl.entities.Entity;
import cdl.movementManager.HitBox;
import cdl.movementManager.MovementEvent;
import cdl.space.Block;
import cdl.util.Vector2D;

public class Solid extends BlockProperty {

	private static final int PROP_ID = 1;

	public Solid(Block b) {
		super(b);
	}

	@Override
	public void movementOccured(MovementEvent evt) {
		Entity ent = evt.getSource();
		if (ent.isIntersecting(ref)) {
			ent.wl.lock();
			try {
				boolean moved = false;
				Point2D[] bCorns = ref.getCorners();
				for (int i = 0; i < 4; i++) {
					Point2D[] eCorns = ent.getCorners();
					if (ref.contains(eCorns[i])) {
						moved = true;
						Vector2D intVec = new Vector2D(eCorns[i], bCorns[(i + 2) % 4]);
						if (Math.abs(intVec.getX()) < 0.05 && Math.abs(intVec.getY()) < 0.05)
							continue;
						Vector2D mov = evt.getMovement();
						if (Math.abs(intVec.getX()) < Math.abs(intVec.getY())) {
							if (mov.getX() * intVec.getX() < 0) {
								ent.move(intVec.getX(), 0);
								ent.vel.friction(1, Entity.Orientation.RIGHT);
							}
						} else {
							if (mov.getY() * intVec.getY() < 0) {
								ent.move(0, intVec.getY());
								ent.vel.friction(1, Entity.Orientation.DOWN);
							}
						}
					}
				}
				if (!moved) {
					HitBox temp = ent.intersectingArea(ref);
					if (temp.getHeight() > temp.getWidth()) {
						if (evt.getMovement().getX() < 0)
							ent.move(temp.getWidth(), 0);
						else
							ent.move(-temp.getWidth(), 0);
						ent.vel.friction(1, Entity.Orientation.RIGHT);
					} else {
						if (evt.getMovement().getY() < 0)
							ent.move(0, temp.getHeight());
						else
							ent.move(0, -temp.getHeight());
						ent.vel.friction(1, Entity.Orientation.DOWN);
					}
				}
			} finally {
				ent.wl.unlock();
			}
			ent.hit(ref, evt.getMovement());
		}
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Solid;
	}

	@Override
	public int getPropID() {
		return PROP_ID;
	}
}
