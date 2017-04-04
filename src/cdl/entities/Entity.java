package cdl.entities;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import cdl.movementManager.HitBox;
import cdl.movementManager.MovementEvent;
import cdl.movementManager.MovementListener;
import cdl.movementManager.Tickable;
import cdl.space.Chunk;
import cdl.space.Drawable;
import cdl.space.World;
import cdl.util.Vector2D;

public class Entity extends HitBox implements MovementListener, Tickable, Drawable {
	protected BufferedImage[] textures;
	protected String name = "null";
	protected World root;
	protected Orientation orient = Orientation.RIGHT;
	public Velocity vel = new Velocity();
	protected Position p = new Position();

	protected int graphic = 0;
	protected int graphics = 4;

	// !!!HALTE DIESE KLASSE THREADSICHER!!!
	public final ReentrantReadWriteLock rrwl = new ReentrantReadWriteLock(true);
	public final ReentrantReadWriteLock.ReadLock rl = rrwl.readLock();
	public final ReentrantReadWriteLock.WriteLock wl = rrwl.writeLock();

	public Entity(World root, Position p) {
		super();
		if (root == null)
			throw new IllegalArgumentException("Entity needs World");
		this.root = root;
		this.root.addEntity(this);
		this.p = p;

		// ich weiß nicht wie ich ein Entity bauen soll!!!
		this.getChunk().addMovementListener(this);
	}

	public void move(Vector2D v) {
		move(v.getX(), v.getY());
	}

	public void move(double x, double y) {
		wl.lock();
		try {
			Position temp = new Position(p);
			temp.move(x, y);
			if ((((int) p.getX()) / 16 != ((int) temp.getX()) / 16)
					|| (((int) p.getY()) / 16 != ((int) temp.getY()) / 16)) {
				try {
					getChunk().removeMovementListener(this);
					p = temp;
					getChunk().addMovementListener(this);
				} catch (IllegalArgumentException e) {
					this.destroy();
				}
			} else
				p = temp;
			this.reset(new HitBox(p, this.getWidth(), this.getHeight()));
		} finally {
			wl.unlock();
		}
	}

	public void move() {
		rl.lock();
		try {
			if (vel.getVelX() == 0 && vel.getVelY() == 0) {
				return;
			}
		} finally {
			rl.unlock();
		}

		wl.lock();
		try {
			move(vel.getVelX() / World.TPS, vel.getVelY() / World.TPS);
		} finally {
			wl.unlock();
		}

		try {
			root.notifyMovement(new MovementEvent(this, vel.getVel()));
		} catch (IllegalArgumentException e) {
			this.destroy();
		}
	}

	@Override
	public void draw(Graphics g) {
		rl.lock();
		// System.out.println("draw");
		try {
			BufferedImage texture;
			if (Math.abs(this.vel.getVelX()) > 0.01) {
				System.out.println("VEL = " + this.vel.getVelX());
				int index = (graphic / 5 % graphics);
				System.out.println(index);
				texture = textures[index];
			} else {
				texture = textures[2];
			}
			if (this.getOrientation().getAxialOrientation().equals(Orientation.LEFT)) {
				// System.out.println("LEFT");

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
				// System.out.println("RIGHT");
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

		} finally

		{
			rl.unlock();
		}
	}

	public void setName(String n) {
		name = n;
	}

	public String getName() {
		return name;
	}

	protected void setOrientation(Orientation o) {
		orient = o;
	}

	protected Orientation getOrientation() {
		return orient;
	}

	public static class Orientation {
		private double orient;

		public static Orientation RIGHT = new Orientation(0 * PI);
		public static Orientation DOWN = new Orientation(0.5 * PI);
		public static Orientation LEFT = new Orientation(1 * PI);
		public static Orientation UP = new Orientation(1.5 * PI);

		public Orientation(double o) {
			while (o < 0)
				o += 2 * PI;
			while (o >= 2 * PI)
				o -= 2 * PI;
			orient = o;
		}

		public Orientation(Velocity v) {
			this(v.getDirection());
		}

		public Orientation(Vector2D v) {
			this(v.getDirection());
		}

		public double getOrient() {
			return orient;
		}

		public Orientation getAxialOrientation() {
			if (orient < 0.25 * PI)
				return RIGHT;
			if (orient < 0.75 * PI)
				return DOWN;
			if (orient < 1.25 * PI)
				return LEFT;
			if (orient < 1.75 * PI)
				return UP;
			return RIGHT;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Orientation)
				if (abs(Double.compare(((Orientation) o).orient, this.orient)) < PI / (1 << 9)) {
					return true;
				}
			return false;
		}

		@Override
		public int hashCode() {
			return (int) (orient / (2 * PI)) * (1 << 32);
		}
	}

	public static class Velocity {
		private Vector2D vel = new Vector2D();
		double velmax = 7.5;
		private static final double SMALLEST_RELEVANT_VELOCITY = 1. / (1 << 5);

		public Velocity() {
		}

		public Velocity(double velx, double vely) {
			varTest(velx);
			varTest(vely);
			vel = new Vector2D(velx, vely);
			relCheck();
			maxVelCheck();
		}

		public Velocity(int velx, int vely) {
			vel = new Vector2D(velx, vely);
			relCheck();
			maxVelCheck();
		}

		public Velocity(double vel, Orientation o) {
			varTest(vel);
			this.vel = new Vector2D(cos(o.getOrient()) * vel, sin(o.getOrient()) * vel);
			relCheck();
			maxVelCheck();
		}

		public Velocity(int vel, Orientation o) {
			this.vel = new Vector2D(cos(o.getOrient()) * vel, sin(o.getOrient()) * vel);
			relCheck();
			maxVelCheck();
		}

		public void accelerate(double vel, Orientation o) {
			varTest(vel);
			this.vel = this.vel.add(new Vector2D(vel * cos(o.getOrient()), vel * sin(o.getOrient())));
			relCheck();
			maxVelCheck();
		}

		public void friction(double coefficient, Orientation o) {
			varTest(coefficient);
			Vector2D temp = vel.rotate(o.getOrient());
			vel = new Vector2D(temp.getX() * (1 - coefficient), temp.getY()).rotate(-o.getOrient());
			relCheck();
			maxVelCheck();
		}

		public void friction(double coefficient) {
			varTest(coefficient);
			vel = new Vector2D(vel.getX() * (1 - coefficient), vel.getY() * (1 - coefficient));
			maxVelCheck();
			relCheck();
		}

		public double getAbsVel() {
			return vel.abs();
		}

		public Vector2D getVel() {
			return vel;
		}

		public double getVelX() {
			return vel.getX();
		}

		public double getVelY() {
			return vel.getY();
		}

		public void setMaxVel(Double vel) {
			varTest(vel);
			velmax = abs(vel);
		}

		private void maxVelCheck() {
			if (vel.getX() > velmax)
				vel = new Vector2D(velmax, vel.getY());
			else if (vel.getX() < -velmax)
				vel = new Vector2D(-velmax, vel.getY());
			if (vel.getY() > velmax)
				vel = new Vector2D(vel.getX(), velmax);
			else if (vel.getY() < -velmax)
				vel = new Vector2D(vel.getX(), -velmax);
		}

		public double getDirection() {
			if (vel.getX() == 0 && vel.getY() == 0)
				return 0;
			double orient;
			orient = vel.getDirection();
			if (orient < 0)
				return orient + 2 * PI;
			return orient;
		}

		@Override
		public int hashCode() {
			return (int) (vel.getX() + (1 << 16) * vel.getY());
		}

		private void varTest(double d) {
			if (Double.isInfinite(d) || Double.isNaN(d))
				throw new IllegalArgumentException("Velocity does not accept NaN or Infinity");
		}

		private void relCheck() {
			if (abs(vel.getX()) < SMALLEST_RELEVANT_VELOCITY)
				vel = new Vector2D(0, vel.getY());
			if (abs(vel.getY()) < SMALLEST_RELEVANT_VELOCITY)
				vel = new Vector2D(vel.getX(), 0);
		}

	}

	@Override
	public void movementOccured(MovementEvent evt) {

	}

	@Override
	public Chunk getChunk() {
		return root.getChunk(((int) p.getX()) / 16, ((int) p.getY()) / 16);
	}

	@Override
	public void tick() {
		this.vel.accelerate(root.getGravitation() / World.TPS, Orientation.DOWN);
		this.vel.friction(1.5 / World.TPS, Orientation.LEFT);
		this.move();
	}

	public void hit(HitBox other, Vector2D mov) {

	}

	@Override
	public int hashCode() {
		return (p.hashCode() + vel.hashCode() + orient.hashCode());
	}

	// Now I have to Override all the methods of HitBox to make them threadsave
	@Override
	public boolean isIntersecting(HitBox hb) {
		boolean ret;
		rl.lock();
		try {
			ret = super.isIntersecting(hb);
		} finally {
			rl.unlock();
		}
		return ret;
	}

	@Override
	public HitBox intersectingArea(HitBox hb) {
		HitBox ret;
		rl.lock();
		try {
			if (hb instanceof Entity)
				((Entity) hb).rl.lock();
			try {
				ret = super.intersectingArea(hb);
			} finally {
				if (hb instanceof Entity)
					((Entity) hb).rl.lock();
			}
		} finally {
			rl.unlock();
		}
		return ret;
	}

	@Override
	public double getHeight() {
		double ret;
		rl.lock();
		try {
			ret = super.getHeight();
		} finally {
			rl.unlock();
		}
		return ret;
	}

	@Override
	public double getWidth() {
		double ret;
		rl.lock();
		try {
			ret = super.getWidth();
		} finally {
			rl.unlock();
		}
		return ret;
	}

	@Override
	public double getTopBound() {
		double ret;
		rl.lock();
		try {
			ret = super.getTopBound();
		} finally {
			rl.unlock();
		}
		return ret;
	}

	@Override
	public double getBottomBound() {
		double ret;
		rl.lock();
		try {
			ret = super.getBottomBound();
		} finally {
			rl.unlock();
		}
		return ret;
	}

	@Override
	public double getLeftBound() {
		double ret;
		rl.lock();
		try {
			ret = super.getLeftBound();
		} finally {
			rl.unlock();
		}
		return ret;
	}

	@Override
	public double getRightBound() {
		double ret;
		rl.lock();
		try {
			ret = super.getRightBound();
		} finally {
			rl.unlock();
		}
		return ret;
	}

	public void damage() {
		damage(1);
	}

	public void damage(int d) {

	}

	public void destroy() {
		wl.lock();
		try {
			try {
				this.getChunk().removeMovementListener(this);
			} catch (IllegalArgumentException e) {
			}
			this.root.removeEntity(this);
		} finally {
			wl.unlock();
		}
	}

	public void save(BufferedWriter bw) throws IOException {
	
	}
	
	@Override
	public String toString(){
		return "Entity";
	}
}
