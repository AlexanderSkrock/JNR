package cdl.util;

import java.awt.geom.Point2D;

public class Vector2D extends Vector {

	private double direction = Double.NaN;
	private Vector2D orth = null;

	private Vector2D(Vector v) {
		super(v);
	}

	public Vector2D() {
		super(2);
	}

	public Vector2D(Point2D p) {
		super(p);
	}

	public Vector2D(Point2D a, Point2D b) {
		this(b.getX() - a.getX(), b.getY() - a.getY());
	}

	public Vector2D(double x, double y) {
		super(x, y);
	}

	public final double getX() {
		return super.get(0);
	}

	public final double getY() {
		return super.get(1);
	}

	public final Point2D getPosition() {
		return new Point2D.Double(getX(), getY());
	}

	public final double getDirection() {
		if (Double.isNaN(direction)) {
			direction = Math.atan2(getY(), getX());
			if (direction < 0)
				direction += 2 * Math.PI;
		}
		return direction;
	}

	public Vector2D rotate(double radians) {
		return new Vector2D(abs() * Math.cos(getDirection() + radians), abs() * Math.sin(getDirection() + radians));
	}

	public Vector2D orthogonal() {
		if (orth == null) {
			orth = new Vector2D(getY(), -getX());
		}
		return orth;
	}

	@Override
	public Vector2D scalar(double d) {
		return new Vector2D(super.scalar(d));
	}

	@Override
	public Vector2D normalize() {
		return new Vector2D(super.normalize());
	}

	@Override
	public Vector2D add(Vector v) {
		return new Vector2D(super.add(v));
	}

	@Override
	public Vector2D subtract(Vector v) {
		return new Vector2D(super.subtract(v));
	}
}
