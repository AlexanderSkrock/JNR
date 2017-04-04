package cdl.entities;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cdl.data.PlayerInfo;
import cdl.movementManager.HitBox;
import cdl.sounds.SoundPlayer;
import cdl.space.World;
import cdl.util.Vector2D;

public class Player extends Entity implements KeyListener {
	private Map<Integer, Control> keys = new HashMap<>();
	protected boolean jumpable = false;
	public static final Control LEFT = new Control();
	public static final Control RIGHT = new Control();
	public static final Control JUMP = new Control();
	private BufferedImage heart;
	private long lastDamageTick = 0;
	private Integer lives = 3;

	public Player(World root, Position p) {
		super(root, p);
		super.graphics = 10;
		this.reset(new HitBox(p, 0.8, 1.6));
		super.textures = PlayerInfo.PI.getSprites();
		keys.put(PlayerInfo.PI.getLeftKey(), LEFT);
		keys.put(PlayerInfo.PI.getRightKey(), RIGHT);
		keys.put(PlayerInfo.PI.getJumpKey(), JUMP);
		heart = PlayerInfo.PI.getHeart();
	}

	@Override
	public void draw(Graphics g) {
		rl.lock();
		// System.out.println("draw");
		try {
			BufferedImage texture;
			if (Math.abs(this.vel.getVelX()) > 0.01) {
				int index = (graphic / 5 % graphics);
				texture = textures[index];
			} else {
				texture = textures[2];
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

			if (!root.isEditable()) {
				for (int i = 0; i < lives; i++) {
					g.drawImage(heart, ((heart.getWidth() + 10) * i) + 10, 10, null);
				}
			}
		} finally {
			rl.unlock();
		}

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (!keys.containsKey(e.getKeyCode()))
			return;
		int iKeyCode = e.getKeyCode();
		Control c = keys.get(iKeyCode);
		if (c == JUMP)
			c.set(true);
		if (c == LEFT) {
			LEFT.set(true);
			setOrientation(Orientation.LEFT);
		}
		if (c == RIGHT) {
			RIGHT.set(true);
			setOrientation(Orientation.RIGHT);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (!keys.containsKey(e.getKeyCode()))
			return;
		int iKeyCode = e.getKeyCode();
		Control c = keys.get(iKeyCode);
		c.set(false);
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void tick() {
		wl.lock();
		try {
			if (LEFT.get()) {
				vel.accelerate(7. / World.TPS, Orientation.LEFT);
			} else if (RIGHT.get()) {
				vel.accelerate(7. / World.TPS, Orientation.RIGHT);
			}

			super.tick();

			if (JUMP.get()) {
				if (jumpable) {
					SoundPlayer.SP.playSound(SoundPlayer.JUMP);
					this.vel.accelerate(7., Orientation.UP);
				}
			}
			jumpable = false;
			root.centerCamera(new Point2D.Double(p.getX(), p.getY()));
		} finally {
			wl.unlock();
		}
	}

	@Override
	public void hit(HitBox other, Vector2D mov) {
		rl.lock();
		try {
			HitBox temp = this.intersectingArea(other);
			if (temp.getHeight() - temp.getWidth() < 0 && mov.getY() > 0) {
				jumpable = true;
			}
		} finally {
			rl.unlock();
		}
	}

	@Override
	public void save(BufferedWriter bw) throws IOException {
		bw.write("Player ");
		bw.write(Double.toString(getLeftBound() + getWidth() / 2));
		bw.write(";");
		bw.write(Double.toString(getTopBound() + getHeight() / 2));
		bw.newLine();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Player;
	}

	public static class Control {
		private boolean status = false;

		public synchronized boolean get() {
			return status;
		}

		public synchronized void set(boolean b) {
			status = b;
		}
	}

	@Override
	public void destroy() {
		super.destroy();
		if (root.isEditable())
			return;
		root.end();
	}

	@Override
	public void damage(int d) {
		wl.lock();
		try {
			if (root.getTick() - lastDamageTick < 50)
				return;
			lastDamageTick = root.getTick();
			lives -= d;
			if (lives <= 0)
				this.destroy();
		} finally {
			wl.unlock();
		}
	}

	public void resetKeys() {
		LEFT.set(false);
		RIGHT.set(false);
		JUMP.set(false);
	}
	
	@Override
	public String toString(){
		return "Player";
	}
}
