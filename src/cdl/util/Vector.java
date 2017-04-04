package cdl.util;

import java.awt.geom.Point2D;
import java.util.Arrays;

public class Vector {

	protected final double[] vec;
	protected double abs = Double.NaN;
	protected Vector norm = null;
	protected int hash = 0;
	protected boolean hashed = false;

	public Vector(int dim) {// creates an o Vector with dim dimensions
		if (dim <= 0) {
			throw new IllegalArgumentException("A Vector needs at least 1 Dimension");
		}
		vec = new double[dim];
	}

	protected Vector(Vector v) {
		vec = v.vec;
	}

	public Vector(Point2D p) {
		this(2);
		vec[0] = p.getX();
		vec[1] = p.getY();
	}

	public Vector(double... d) {
		if (d.length <= 0) {
			throw new IllegalArgumentException("A Vector needs at least 1 Dimension");
		}
		vec = Arrays.copyOf(d, d.length);
	}

	public int dimensions() {
		return vec.length;
	}

	public double get(int dim) {
		return vec[dim];
	}

	public Vector scalar(double d) {
		Vector ret = new Vector(vec.length);
		for (int i = 0; i < vec.length; i++) {
			ret.vec[i] *= d;
		}
		return ret;
	}

	public double scalar(Vector v) {
		if (vec.length != v.vec.length)
			throw new IllegalArgumentException("The Dimensions of two Vectors have to be the same");
		double result = 0;
		for (int i = 0; i < vec.length; i++) {
			result += vec[i] * v.vec[i];
		}
		return result;
	}

	public Vector add(Vector v) {
		if (vec.length != v.vec.length)
			throw new IllegalArgumentException("The Dimensions of two Vectors have to be the same");
		Vector result = new Vector(vec.length);
		for (int i = 0; i < vec.length; i++) {
			result.vec[i] = vec[i] + v.vec[i];
		}
		return result;
	}

	public Vector subtract(Vector v) {
		if (vec.length != v.vec.length)
			throw new IllegalArgumentException("The Dimensions of two Vectors have to be the same");
		Vector result = new Vector(vec.length);
		for (int i = 0; i < vec.length; i++) {
			result.vec[i] = vec[i] - v.vec[i];
		}
		return result;
	}

	public double abs() {
		if (Double.isNaN(abs)) {
			abs = 0;
			for (int i = 0; i < vec.length; i++) {
				abs += (vec[i] * vec[i]);
			}
			abs = Math.sqrt(abs);
		}
		return abs;
	}

	public Vector normalize() {
		if (norm == null) {
			if (abs() == 0)
				throw new UnsupportedOperationException("Cannot normalize a vector with a length of 0");
			norm = new Vector(vec.length);
			for (int i = 0; i < vec.length; i++) {
				norm.vec[i] = vec[i] / abs;
			}
		}
		return norm;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o instanceof Vector) {
			if (vec == ((Vector) o).vec)
				return true;
			if (vec.length != ((Vector) o).vec.length)
				return false;
			for (int i = 0; i < vec.length; i++)
				if (vec[i] != ((Vector) o).vec[i])
					return false;
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (!hashed) {
			hash += vec.length;
			for (int i = 0; i < vec.length; i++) {
				hash += vec[i] * (i + 2);
			}
		}
		return hash;
	}
}
