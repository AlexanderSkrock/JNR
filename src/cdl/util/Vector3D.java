package cdl.util;

public class Vector3D extends Vector {

	public Vector3D() {
		super(3);
	}

	private Vector3D(Vector v) {
		super(v);
	}

	public Vector3D(double x, double y, double z) {
		super(x, y, z);
	}

	public final double getX() {
		return super.get(0);
	}

	public final double getY() {
		return super.get(1);
	}

	public final double getZ() {
		return super.get(2);
	}

	public Vector3D crossProduct(Vector3D v) {
		return new Vector3D(this.getY() * v.getZ() - v.getY() * this.getZ(),
				this.getZ() * v.getX() - v.getZ() * this.getX(), this.getX() * v.getY() - v.getX() * this.getY());
	}

	@Override
	public Vector3D scalar(double d){
		return new Vector3D(super.scalar(d));
	}
	
	@Override
	public Vector3D normalize(){
		return new Vector3D(super.normalize());
	}
	
	@Override
	public Vector3D add(Vector v){
		return new Vector3D(super.add(v));
	}
	
	@Override
	public Vector3D subtract(Vector v) {
		return new Vector3D(super.subtract(v));
	}
}
