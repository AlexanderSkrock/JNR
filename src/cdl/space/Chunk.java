package cdl.space;

import java.awt.Graphics;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.swing.event.EventListenerList;

import cdl.movementManager.MovementEvent;
import cdl.movementManager.MovementListener;
import cdl.util.Matrix;

public class Chunk implements MovementListener, Drawable {

	private World root;
	private Matrix<Block> blocks = new Matrix<>(16, 16);
	private Point pos; // in the scale of chunks
	EventListenerList ell = new EventListenerList();// movementListeners

	public Chunk(World root, int x, int y) {
		this.root = root;
		this.root.addMovementListener(this);
		pos = new Point(-1, -1);
		for (int i = 0; i < blocks.Xsize(); i++) {
			for (int j = 0; j < blocks.Ysize(); j++) {
				blocks.put(i, j, new Block(this, 0, new Point(i + 16 * x, j + 16 * y)));
			}
		}
	}

	public Chunk(World root, BufferedReader br, Point pos) throws IOException {
		build(root, br, pos);
	}

	ByteBuffer build(World root, Point pos) {
		if (this.root != null)
			this.root.removeMovementListener(this);
		root.addMovementListener(this);
		for (int i = 0; i < blocks.Xsize(); i++)
			for (int j = 0; j < blocks.Ysize(); j++)
				blocks.put(i, j, new Block(this, 0, new Point(j + (this.pos.x * 16), i + (this.pos.y * 16))));
		return null;
	}

	BufferedReader build(World root, BufferedReader br, Point pos) throws IOException {
		if (this.root != null)
			this.root.removeMovementListener(this);
		root.addMovementListener(this);
		this.root = root;
		this.pos = pos;
		String line;
		do {
			line = br.readLine();
		} while (line.trim().equals(""));
		if (line.contains("Chunk")) {
			if (line.contains("empty")) {
				for (int i = 0; i < blocks.Ysize(); i++) {
					for (int j = 0; j < blocks.Xsize(); j++) {
						blocks.put(j, i, new Block(this, 0, new Point(j + (this.pos.x * 16), i + (this.pos.y * 16))));
					}
				}
			} else {
				for (int i = 0; i < blocks.Ysize(); i++) {
					for (int j = 0; j < blocks.Xsize(); j++) {
						blocks.put(j, i, new Block(this, br, new Point(j + (this.pos.x * 16), i + (this.pos.y * 16))));
					}
				}
			}
		} else {
			System.out.println(line);
			System.out.println(br.toString());
			throw new IOException(line);
		}
		return br;
	}

	BufferedWriter save(BufferedWriter bw) throws IOException {
		if (isEmpty())
			return bw;
		for (int i = 0; i < blocks.Ysize(); i++) {
			for (int j = 0; j < blocks.Xsize(); j++) {
				blocks.get(j, i).save(bw);
			}
			bw.newLine();
		}
		return bw;
	}

	public boolean isEmpty() {
		for (Block b : blocks)
			if (b.getID() != 0)
				return false;
		return true;
	}

	public Point getPos() {
		synchronized (pos) {
			return pos;
		}
	}

	public void setPos(Point p) {
		synchronized (pos) {
			this.pos = p;
			for (int i = 0; i < blocks.Xsize(); i++) {
				for (int j = 0; j < blocks.Ysize(); j++) {
					blocks.get(i, j).setPos(new Point(this.pos.x * 16 + i, this.pos.y * 16 + j));
				}
			}
		}
	}

	public void addMovementListener(MovementListener ml) {
		ell.add(MovementListener.class, ml);
	}

	public void removeMovementListener(MovementListener ml) {
		ell.remove(MovementListener.class, ml);
	}

	public void notifyMovement(MovementEvent evt) {
		for (MovementListener l : ell.getListeners(MovementListener.class)) {
			root.worldManager.execute(/* Lambda expression */() -> l.movementOccured(evt));
		}
	}

	@Override
	public void movementOccured(MovementEvent evt) {
		notifyMovement(evt);
	}

	@Override
	public Chunk getChunk() {
		return this;
	}

	public World getRoot() {
		return root;
	}

	public Block getBlock(int x, int y) {
		return blocks.get(x, y);
	}

	@Override
	public void draw(Graphics g) {
		for (Block b : blocks)
			b.draw(g);
	}

	public void putBlock(int id, int x, int y) {
		blocks.get(x % 16, y % 16).destroy();
		blocks.put(x % 16, y % 16, new Block(this, id, new Point(x, y)));
	}

	public void removeBlock(Point p) {
		removeBlock(p.x, p.y);
	}

	public void removeBlock(int x, int y) {
		blocks.get(x % 16, y % 16).destroy();
		blocks.put(x % 16, y % 16, new Block(this, 0, new Point(x, y)));
	}

}
