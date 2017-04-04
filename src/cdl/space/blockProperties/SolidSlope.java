package cdl.space.blockProperties;

import java.awt.geom.Point2D;

import cdl.entities.Entity;
import cdl.entities.Entity.Orientation;
import cdl.movementManager.MovementEvent;
import cdl.space.Block;
import cdl.util.Vector2D;

public class SolidSlope extends BlockProperty {

	private static final int PROP_ID = 2;
	private final int orient; // 0 := unten rechts; 1:= unten links;
								// 2:= oben links; 3:= oben rechts;

	public SolidSlope(Block b, int orientation) {
		super(b);
		orient = orientation;
	}

	@Override
	public void movementOccured(MovementEvent evt) {
		Entity ent = evt.getSource();
		if (ent.isIntersecting(ref)) {
			ent.wl.lock();
			try {
				Point2D[] eCorners = ent.getCorners(), bCorners = ref.getCorners();
				for (int i = 0; i < eCorners.length; i++) {
					Point2D ec = eCorners[(i + 2) % bCorners.length], bc = bCorners[i];
					Vector2D ip = new Vector2D(bc, ec);
					if (i == orient) {
						if (orient == 0) {
							if (ip.getY() + ip.getX() > 1) {
								ent.move((ip.getY() - ip.getX()) / 2, (ip.getX() - ip.getY()) / 2);
								ent.vel.friction(1, new Orientation(Math.PI * 0.25));
								ent.hit(ref, evt.getMovement());
							}
						} else if (orient == 1) {
							if (ip.getY() > ip.getX()) {
								ent.move((ip.getY() + ip.getX() - 1) / 2, (-ip.getX() + 1 - ip.getY()) / 2);
								ent.vel.friction(1, new Orientation(Math.PI * 1.25));
								ent.hit(ref, evt.getMovement());
							}
						} else if (orient == 2) {
							if (ip.getX() + ip.getY() < 1) {
								ent.move((ip.getY() - ip.getX()) / 2, (ip.getX() - ip.getY()) / 2);
								ent.vel.friction(1, new Orientation(Math.PI * 0.25));
								ent.hit(ref, evt.getMovement());
							}
						} else if (orient == 3) {
							if (ip.getX() > ip.getY()) {
								ent.move((ip.getY() + ip.getX() - 1) / 2, (-ip.getX() + 1 - ip.getY()) / 2);
								ent.vel.friction(1, new Orientation(Math.PI * 1.25));
								ent.hit(ref, evt.getMovement());
							}
						}
					} else {
						Vector2D mov = evt.getMovement();
						if (orient == 0) {
							if (ip.getY() + ip.getX() < 1)
								continue;
						} else if (orient == 1) {
							if (ip.getY() > ip.getX())
								continue;
						} else if (orient == 2) {
							if (ip.getX() + ip.getY() > 1)
								continue;
						} else if (orient == 3) {
							if (ip.getX() > ip.getY())
								continue;
						}
						// determining the direction
						if (ip.getX() / mov.getX() > ip.getY() / mov.getY()) {
							ent.move(ip.getX(), 0);
							ent.vel.friction(1, Orientation.RIGHT);
							ent.hit(ref, evt.getMovement());
						} else {
							ent.move(0, ip.getY());
							ent.vel.friction(1, Orientation.DOWN);
							ent.hit(ref, evt.getMovement());
						}
					}
				}
			} finally {
				ent.wl.unlock();
			}

		}

	}
	@Override
	public boolean equals(Object obj) {
		return obj instanceof SolidSlope;
	}

	@Override
	public int getPropID() {
		return PROP_ID;
	}
}
