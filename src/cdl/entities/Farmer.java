package cdl.entities;

import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import cdl.movementManager.HitBox;
import cdl.movementManager.MovementEvent;
import cdl.space.World;
import cdl.space.blockProperties.BlockProperty;

public class Farmer extends Entity {
	protected boolean angry = Boolean.FALSE;
	protected BufferedImage[] walking = new BufferedImage[4];
	protected BufferedImage[] running = new BufferedImage[4];

	public Farmer(World root, Position p) {
		super(root, p);
	}

	@Override
	public void movementOccured(MovementEvent evt) {
		if (!(evt.getSource() instanceof Player))
			return;
		Entity src = evt.getSource();
		if (src.isIntersecting(this)) {
			src.wl.lock();
			try {
				HitBox intersec = src.intersectingArea(this);
				if (intersec.getWidth() > intersec.getHeight()) {
					if (src.contains(this.getCornerA()) || src.contains(this.getCornerB())) {
						src.vel.friction(1, Orientation.DOWN);
						src.vel.accelerate(5, Orientation.UP);
						this.damage();
					} else {
						src.damage();
					}
				} else {
					src.damage();
				}
			} finally {
				src.wl.unlock();
			}
		}
	}

	/*
	 * Farmer ohne Forke 1. Läuft so lange nach rechts bid Hindernis/Lücke ->
	 * Umdrehen -> Weiterlaufen 2. Wenn am Kopf getroffen -> Sterben 3. Wenn in
	 * Reichweite X vom Spieler -> Schnell, vel * 2
	 * 
	 * Farmer mit Forke 1. Wenn am Kopf getroffen -> Sterben 2. Wenn Player
	 * Forke oben berührt -> Spieler stirbt oder verliert Leben 3. Wenn in
	 * Reichweite X vom Spieler -> Schnell, vel * 2
	 */
	@Override
	public void tick() {
		Player p = root.getPlayer();
		if (p == null || p.getCornerA().distance(this.getCornerA()) > 48)
			return;

		wl.lock();
		double accel = angry ? 1 : 0.5;
		try {
			if (this.vel.getVelX() > 0) {
				Point2D corn = this.getCornerC();
				corn.setLocation(corn.getX() + 0.5, corn.getY() + 0.5);
				if (root.getBlock((int) (corn.getX() % 16), (int) (corn.getY() % 16))
						.containsProperty(BlockProperty.SOLID))
					vel.accelerate(accel, Orientation.RIGHT);
				else
					vel.accelerate(accel, Orientation.LEFT);
			} else {
				Point2D corn = this.getCornerD();
				corn.setLocation(corn.getX() - 0.5, corn.getY() + 0.5);
				if (root.getBlock((int) (corn.getX() % 16), (int) (corn.getY() % 16))
						.containsProperty(BlockProperty.SOLID))
					vel.accelerate(accel, Orientation.LEFT);
				else
					vel.accelerate(accel, Orientation.RIGHT);
			}
			super.tick();
		} finally {
			wl.unlock();
		}
	}

	@Override
	public void draw(Graphics g) {
		rl.lock();
		try {
			BufferedImage texture;
			if (Math.abs(this.vel.getVelX()) > 0.01) {
				int index = (graphic / 6 % graphics);
				if (angry)
					texture = running[index];
				else
					texture = walking[index];
			} else {
				if (angry)
					texture = running[0];
				else
					texture = walking[0];
			}
			if (this.getOrientation().getAxialOrientation().equals(Orientation.LEFT)) {

				if (root != null) {
					double s = root.getScale();
					Point2D cp = root.getCamPos();
					g.drawImage(texture, (int) ((this.getLeftBound() - cp.getX() + this.getWidth()) * s),
							(int) ((this.getTopBound() - cp.getY()) * s), (int) (s * -this.getWidth()),
							(int) (s * this.getHeight()), null);
				} else {
					g.drawImage(texture, texture.getWidth(), 0, -texture.getWidth(), texture.getHeight(), null);
				}
			} else {
				if (root != null) {
					double s = root.getScale();
					Point2D cp = root.getCamPos();
					g.drawImage(texture, (int) ((this.getLeftBound() - cp.getX()) * s),
							(int) ((this.getTopBound() - cp.getY()) * s), (int) (s * this.getWidth()),
							(int) (s * this.getHeight()), null);
				} else {
					g.drawImage(texture, 0, 0, null);
				}

			}

			graphic++;

		} finally {
			rl.unlock();
		}
	}

	@Override
	public void damage(int i) {
		destroy();
	}

	@Override
	public void destroy() {
		wl.lock();
		try {
			System.out.println("Kill me");
			super.destroy();
			if (root.isEditable()) {
				return;
			}
			long lastTick = root.getTick() + 120;
			this.vel.friction(1);
			this.vel.accelerate(5, Orientation.DOWN);
			root.addTickable(() -> {
				if (lastTick < root.getTick()) {
					Farmer.this.draw(root.getCanGraphics());
				} else {
					Farmer.this.root.removeTickable(this);
				}
			});
		} finally {
			wl.unlock();
		}
	}

	@Override
	public String toString() {
		return "Farmer";
	}
}