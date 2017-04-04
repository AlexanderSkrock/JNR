package cdl.movementManager;

import java.awt.Point;
import java.awt.geom.Point2D;

public class HitBox {
	protected Point2D lt = new Point2D.Double(0,0), rb = new Point2D.Double(0,0);

	public HitBox(HitBox hb) {
		lt.setLocation(hb.getLeftBound(), hb.getTopBound());
		rb.setLocation(hb.getRightBound(), hb.getBottomBound());

	}

	public HitBox() {
	}

	public HitBox(Position p, double width, double heigth) {
		varTest(width);
		varTest(heigth);
		if (width < 0 || heigth < 0) {
			throw new IllegalArgumentException("only positive widths and heigths are allowed!");
		}
		lt.setLocation(p.getX() - width / 2, p.getY() - heigth / 2);
		rb.setLocation(p.getX() + width / 2, p.getY() + heigth / 2);
	}

	public HitBox(Point2D p, double width, double heigth) {
		lt.setLocation(p);
		rb.setLocation(width + lt.getX(), heigth + lt.getY());
	}

	public HitBox(double x, double y, double width, double heigth) {
		lt.setLocation(x, y);
		rb.setLocation(width + x, heigth + y);
	}

	public boolean isIntersecting(HitBox hb) {
		if (hb.getBottomBound() - this.getTopBound() >= 0)
			if (this.getBottomBound() - hb.getTopBound() >= 0)
				if (hb.getRightBound() - this.getLeftBound() >= 0)
					if (this.getRightBound() - hb.getLeftBound() >= 0)
						return true;
		return false;
	}

	public HitBox intersectingArea(HitBox hb) {
		HitBox temp = new HitBox();
		temp.lt.setLocation(Math.max(this.getLeftBound(), hb.getLeftBound()),
				Math.max(this.getTopBound(), hb.getTopBound()));
		temp.rb.setLocation(Math.min(this.getRightBound(), hb.getRightBound()),
				Math.min(this.getBottomBound(), hb.getBottomBound()));
		return temp;
	}

	public double getHeight() {
		return rb.getY() - lt.getY();
	}

	public double getWidth() {
		return rb.getX() - lt.getX();
	}

	public double getTopBound() {
		return lt.getY();
	}

	public double getBottomBound() {
		return rb.getY();
	}

	public double getLeftBound() {
		return lt.getX();
	}

	public double getRightBound() {
		return rb.getX();
	}

	public Point2D getCornerA() {
		return new Point2D.Double(lt.getX(), lt.getY());
	}

	public Point2D getCornerB() {
		return new Point2D.Double(rb.getX(), lt.getY());
	}

	public Point2D getCornerC() {
		return new Point2D.Double(rb.getX(), rb.getY());
	}

	public Point2D getCornerD() {
		return new Point2D.Double(lt.getX(), rb.getY());
	}

	public Point2D[] getCorners() {
		Point2D[] ret = { getCornerA(), getCornerB(), getCornerC(), getCornerD() };
		return ret;
	}

	public Point2D getPosition() {
		return new Point2D.Double(this.getLeftBound() + this.getWidth() / 2, this.getTopBound() + this.getHeight() / 2);
	}

	public boolean contains(Point2D p) {
		if (lt.getX() < p.getX() && p.getX() < rb.getX())
			if (lt.getY() < p.getY() && p.getY() < rb.getY())
				return true;
		return false;
	}

	private void varTest(double d) {
		if (Double.isInfinite(d) || Double.isNaN(d))
			throw new IllegalArgumentException("HitBox does not accept NaN or Infinity");
	}

	protected void reset(HitBox hb) {// resets the HitBox to the Values of
										// another
		lt.setLocation(hb.getLeftBound(), hb.getTopBound());
		rb.setLocation(hb.getRightBound(), hb.getBottomBound());
	}

	public static class Position {
		private double x, y;

		public Position() {
			x = -1;
			y = -1;
		}

		public Position(double x, double y) {
			varTest(x);
			varTest(y);
			this.x = x;
			this.y = y;
		}

		public Position(Point p) {
			this.x = p.x;
			this.y = p.y;
		}

		public Position(Position p) {
			this.x = p.x;
			this.y = p.y;
		}

		public void move(double x, double y) {
			this.x += x;
			this.y += y;
		}

		private void varTest(double d) {
			if (d == Double.NaN || d == Double.POSITIVE_INFINITY || d == Double.NEGATIVE_INFINITY)
				throw new IllegalArgumentException("Position does not accept NaN or Infinity");
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

		@Override
		public String toString() {
			return "X: " + x + "\t Y: " + y;
		}

		@Override
		public int hashCode() {
			return (int) (getX() + (1 << 16) * getY());
		}
	}
}
