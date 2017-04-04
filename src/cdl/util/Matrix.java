package cdl.util;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Matrix<T> implements Iterable<T> {
	private int size_x;
	private int size_y;
	private Object[][] matrix;
	private int mc = 0; // ModCount

	public Matrix() {
		size_x = 0;
		size_y = 0;
		matrix = new Object[0][0];
	}

	public Matrix(Matrix<T> m) {
		this.size_x = m.size_x;
		this.size_y = m.size_y;
		this.matrix = new Object[m.Xsize()][m.Ysize()];
		for (int i = 0; i < this.Xsize(); i++) {
			for (int j = 0; j < this.Ysize(); j++) {
				this.matrix[i][j] = m.get(i, j);
			}
		}
	}

	public Matrix(int Xsize, int Ysize) {
		if (Xsize < 0 || Ysize < 0) {
			throw new IllegalArgumentException("Illegal Size: X: " + Xsize + "; Y: " + Ysize);
		}
		matrix = new Object[Xsize][Ysize];
		size_x = Xsize;
		size_y = Ysize;
	}

	public Matrix<T> put(int posx, int posy, T obj) {
		if (posx >= size_x || posx < 0) {
			throw new IllegalArgumentException("Illegal x-Value:" + posx + "\nx-Size:" + size_x);
		}
		if (posy >= size_y || posy < 0) {
			throw new IllegalArgumentException("Illegal y-Value:" + posy + "\ny-Size:" + size_y);
		}
		mc++;
		matrix[posx][posy] = obj;
		return this;
	}

	public Matrix<T> put(int posx, int posy, T[][] matrix) {
		// accesses put(x,y)
		try {
			for (int i = 1; i < matrix.length; i++) {
				if (matrix[i - 1].length != matrix[i].length) {
					throw new IllegalArgumentException("Matrix has no consistent dimensions");
				}
			}
		} catch (NullPointerException e) {
			throw new IllegalArgumentException("Matrix is not complete");
		}
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				System.out.println("i: " + i + "\tj: " + j);
				this.put(posx + j, posy + i, matrix[i][j]);
			}
		}

		return this;
	}

	public Matrix<T> put(int posx, int posy, Matrix<T> matrix) {
		// accesses put(x,y)
		Matrix<T> temp = new Matrix<>(matrix);
		try {
			for (int i = 0; i < matrix.size_x; i++) {
				for (int j = 0; j < matrix.size_y; j++) {
					this.put(posx + i, posy + j, ((T) temp.get(i, j)));
				}
			}
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("The Matrix doesn't fit!");
		}
		return this;
	}

	public int Xsize() {
		return size_x;
	}

	public int Ysize() {
		return size_y;
	}

	public Matrix<T> resize(int Xsize, int Ysize) {
		if (Xsize < 0 || Ysize < 0) {
			throw new IllegalArgumentException("Illegal Size: X: " + Xsize + "; Y: " + Ysize);
		}
		size_x = Xsize;
		size_y = Ysize;
		Object[][] temp = matrix;
		matrix = new Object[Xsize][Ysize];
		for (int i = 0; i < temp.length && i < matrix.length; i++) {
			for (int j = 0; j < temp[i].length && j < matrix[i].length; j++) {
				matrix[i][j] = temp[i][j];
			}
		}
		return this;
	}

	public Matrix<T> translate(int posx, int posy, int width, int heigth, int movex, int movey) {
		Matrix<T> temp = this.get(posx, posy, width, heigth);
		this.put(posx, posy, new Matrix<T>(width, heigth));
		this.put(posx + movex, posy + movey, temp);
		return this;
	}

	@SuppressWarnings("unchecked")
	public T get(int posx, int posy) {
		if (posx >= size_x || posx < 0) {
			throw new IllegalArgumentException("Illegal x-Value:" + posx + "\nx-Size:" + size_x);
		}
		if (posy >= size_y || posy < 0) {
			throw new IllegalArgumentException("Illegal y-Value:" + posy + "\ny-Size:" + size_y);
		}
		return (T) matrix[posx][posy];
	}

	public Matrix<T> get(int posx, int posy, int width, int heigth) {
		// accesses get(x,y)
		Matrix<T> m = new Matrix<>(width, heigth);
		for (int i = posy; i < posy + heigth; i++) {
			for (int j = posx; j < posx + width; j++) {
				m.put(j - posx, i - posy, this.get(j, i));
			}
		}
		return m;
	}

	public Object[] getColumn(int posx) {
		if (posx >= size_x || posx < 0) {
			throw new IllegalArgumentException("Illegal x-Value:" + posx + "\nx-Size:" + size_x);
		}
		Object[] o = new Object[size_y];
		for (int i = 0; i < size_y; i++) {
			o[i] = matrix[posx][i];
		}
		return o;
	}

	@SuppressWarnings("unchecked")
	public T[] getColumn(int posx, T[] array) {
		if (posx >= size_x || posx < 0) {
			throw new IllegalArgumentException("Illegal x-Value:" + posx + "\nx-Size:" + size_x);
		}
		if (array.length != size_y) {
			throw new IllegalArgumentException(
					"not-matching ArraySize: " + array.length + "\n Matrix y-Size:: " + size_y);
		}
		for (int i = 0; i < size_y; i++) {
			array[i] = (T) matrix[posx][i];
		}
		return array;
	}

	public Object[] getRow(int posy) {
		if (posy >= size_y || posy < 0) {
			throw new IllegalArgumentException("Illegal y-Value:" + posy + "\ny-Size:" + size_y);
		}
		Object[] o = new Object[size_x];
		for (int i = 0; i < size_x; i++) {
			o[i] = matrix[i][posy];
		}
		return o;
	}

	@SuppressWarnings("unchecked")
	public T[] getRow(int posy, T[] array) {
		if (posy >= size_y || posy < 0) {
			throw new IllegalArgumentException("Illegal y-Value:" + posy + "\ny-Size:" + size_y);
		}
		if (array.length != size_x) {
			throw new IllegalArgumentException(
					"not-matching ArraySize: " + array.length + "\n Matrix x-Size:: " + size_x);
		}
		for (int i = 0; i < size_x; i++) {
			array[i] = (T) matrix[i][posy];
		}
		return array;
	}

	public void list() {
		System.out.println();
		for (int i = 0; i < size_y; i++) {
			for (int j = 0; j < size_x; j++) {
				System.out.print((matrix[j][i] == null ? "null" : matrix[j][i].toString()) + ";");
			}
			System.out.println();
		}
	}

	public Matrix<T> getSubMatrix(int x, int y, int width, int heigth) {
		return new SubMatrix(x, y, width, heigth);
	}

	@Override
	public Iterator<T> iterator() { // zeilenweise
		return new MatrixIterator();
	}

	protected class MatrixIterator implements Iterator<T> {

		private int myMC;
		private int x, y, width, heigth;
		private int cursor = 0;
		private int lastRet = -1;
		private int size;

		MatrixIterator() {
			myMC = mc;
			x = 0;
			y = 0;
			width = size_x;
			heigth = size_y;
			size = width * heigth;
		}

		MatrixIterator(int x, int y, int width, int heigth) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.heigth = heigth;
			size = width * heigth;
		}

		@Override
		public boolean hasNext() {
			return cursor < size;
		}

		@SuppressWarnings("unchecked")
		@Override
		public T next() {
			if (myMC != mc)
				throw new ConcurrentModificationException();
			if (!hasNext())
				throw new NoSuchElementException();
			lastRet = cursor;
			mc++;
			myMC++;
			return (T) matrix[x + (cursor % width)][y + (cursor++ / width)];
		}

		@Override
		public void remove() {
			if (lastRet == -1)
				throw new IllegalStateException("no last Element to remove");
			matrix[x + (lastRet % width)][y + (lastRet / width)] = null;
			lastRet = -1;
			mc++;
			myMC++;
		}
	}

	protected class SubMatrix extends Matrix<T> {
		private final int x, y, width, heigth;

		SubMatrix(int x, int y, int width, int heigth) {
			this.x = x;
			this.y = y;
			this.heigth = heigth;
			this.width = width;
		}

		@Override
		public Matrix<T> put(int posx, int posy, T obj) {
			mc++;
			if (posx + x >= size_x || posx < 0) {
				throw new IllegalArgumentException("Illegal x-Value:" + posx + "\nx-Size:" + size_x);
			}
			if (posy + x >= size_y || posy < 0) {
				throw new IllegalArgumentException("Illegal y-Value:" + posy + "\ny-Size:" + size_y);
			}
			mc++;
			matrix[posx + x][posy + y] = obj;
			return this;
		}

		@Override
		public int Xsize() {
			return width;
		}

		@Override
		public int Ysize() {
			return heigth;
		}

		@Override
		public Matrix<T> resize(int Xsize, int Ysize) {
			throw new UnsupportedOperationException("This is a SubMatrix");
		}

		@Override
		public Matrix<T> translate(int posx, int posy, int width, int heigth, int movex, int movey) {
			throw new UnsupportedOperationException("This is a SubMatrix");
		}

		@SuppressWarnings("unchecked")
		@Override
		public T get(int posx, int posy) {
			if (posx + x >= size_x || posx < 0) {
				throw new IllegalArgumentException("Illegal x-Value:" + posx + "\nx-Size:" + (size_x - x));
			}
			if (posy + y >= size_y || posy < 0) {
				throw new IllegalArgumentException("Illegal y-Value:" + posy + "\ny-Size:" + (size_y - y));
			}
			return (T) matrix[posx + x][posy + y];
		}

		@Override
		public Object[] getColumn(int posx) {
			if (posx >= width || posx < 0) {
				throw new IllegalArgumentException("Illegal x-Value:" + posx + "\nx-Size:" + width);
			}
			Object[] o = new Object[heigth];
			for (int i = 0; i < heigth; i++) {
				o[i] = matrix[posx + x][i + y];
			}
			return o;
		}

		@Override
		@SuppressWarnings("unchecked")
		public T[] getColumn(int posx, T[] array) {
			if (posx >= width || posx < 0) {
				throw new IllegalArgumentException("Illegal x-Value:" + posx + "\nx-Size:" + width);
			}
			if (array.length != heigth) {
				throw new IllegalArgumentException(
						"not-matching ArraySize: " + array.length + "\n Matrix y-Size:: " + heigth);
			}
			for (int i = 0; i < heigth; i++) {
				array[i] = (T) matrix[posx + x][i + y];
			}
			return array;
		}

		@Override
		public Object[] getRow(int posy) {
			if (posy >= size_y || posy < 0) {
				throw new IllegalArgumentException("Illegal y-Value:" + posy + "\ny-Size:" + heigth);
			}
			Object[] o = new Object[width];
			for (int i = 0; i < width; i++) {
				o[i] = matrix[i + x][posy + y];
			}
			return o;
		}

		@Override
		@SuppressWarnings("unchecked")
		public T[] getRow(int posy, T[] array) {
			if (posy >= size_y || posy < 0) {
				throw new IllegalArgumentException("Illegal y-Value:" + posy + "\ny-Size:" + width);
			}
			if (array.length != width) {
				throw new IllegalArgumentException(
						"not-matching ArraySize: " + array.length + "\n Matrix x-Size:: " + width);
			}
			for (int i = 0; i < width; i++) {
				array[i] = (T) matrix[i + x][posy + y];
			}
			return array;
		}

		public void list() {
			System.out.println();
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < heigth; j++) {
					System.out.print((matrix[j + x][i + y] == null ? "null" : matrix[j + x][i + y].toString()) + ";");
				}
				System.out.println();
			}
		}

		public Matrix<T> getSubMatrix(int x, int y, int width, int heigth) {
			return Matrix.this.getSubMatrix(this.x + x, this.y + y, this.width + width, this.heigth + heigth);
		}

		@Override
		public Iterator<T> iterator() {
			// zeilenweise
			return new MatrixIterator(x, y, width, heigth);
		}

	}
}