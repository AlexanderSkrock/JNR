package cdl.space;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import cdl.data.BlockInfo;
import cdl.movementManager.HitBox;
import cdl.space.blockProperties.BlockProperty;

public class Block extends HitBox implements Drawable {
	private Chunk root;
	private int id;
	private Point pos; // in the scale of Blocks
	private SortedSet<BlockProperty> props = new TreeSet<>();

	private BufferedImage texture;
	private String name;

	public Block(int id) {
		this(null);
		this.id = id;
		BlockInfo.BI.setProps(this);
		for (BlockProperty p : props)
			p.destroy();
		props = new TreeSet<>();
	}

	public Block(Chunk root) {
		super();
		this.root = root;
		id = 0;
		pos = new Point(-1, -1);
	}

	public Block(Chunk root, int id, Point pos) {
		super(pos, 1, 1);
		this.root = root;
		this.id = id;
		this.pos = pos;
		BlockInfo.BI.setProps(this);
	}

	public Block(Chunk root, BufferedReader br, Point pos) throws IOException {
		super(pos, 1, 1);
		build(root, br, pos);
	}

	BufferedReader build(Chunk root, BufferedReader br, Point pos) throws IOException {
		this.root = root;
		super.reset(new HitBox(pos, 1, 1));
		this.pos = pos;
		StringBuilder sb = new StringBuilder();
		char read;
		while (true) {
			read = (char) br.read();
			if (read == '\n') {
				continue;
			}
			if (read == ';') {
				break;
			}
			sb.append(read);
		}

		String[] s = sb.toString().split(",");
		id = Integer.parseInt(s[0].trim());
		if (id >= 0) {
			// ist die ID kleiner 0, so ist dies eine ModID und keine BlockID
			// diese wird daraufhin durch BI durch eine interne BlockID ersetzt
			BlockInfo.BI.setProps(this);
			String[] temp = Arrays.copyOfRange(s, 1, s.length);
			for (BlockProperty prop : props)
				temp = prop.build(temp);
		} else {
			BlockInfo.BI.setProps(this, Integer.parseInt(s[1].trim()));
			String[] temp = Arrays.copyOfRange(s, 1, s.length);
			for (BlockProperty prop : props)
				temp = prop.build(temp);
		}
		return br;
	}

	BufferedWriter save(BufferedWriter bw) throws IOException {
		bw.write(String.valueOf(id));
		for (BlockProperty prop : props)
			prop.save(bw);
		bw.write(";");
		return bw;
	}

	public Point getPos() {
		synchronized (pos) {
			return pos.getLocation();
		}
	}

	public void setPos(Point p) {
		synchronized (pos) {
			this.pos = p;
		}
	}

	public void addProperty(BlockProperty bp) {
		props.add(bp);
	}

	public void addProperties(BlockProperty bp[]) {
		props.addAll(Arrays.asList(bp));
	}

	public Chunk getChunk() {
		return root;
	}

	public int getID() {
		return id;
	}

	public void setTexture(BufferedImage bi) {
		texture = bi;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Icon getTexture() {
		return new ImageIcon(texture);
	}

	void destroy() {
		if (root != null) {
			props.forEach((p) -> p.destroy());
			props = new TreeSet<>();
		}
	}

	public void setID(int id) {
		if (this.id >= 0)
			throw new UnsupportedOperationException();
		this.id = id;
	}

	public boolean containsProperty(int id) {
		for (BlockProperty bp : props)
			if (bp.getPropID() == id)
				return true;
		return false;
	}

	@Override
	public void draw(Graphics g) {
		if (root != null) {
			synchronized (pos) {
				double s = root.getRoot().getScale();
				if (this.pos.getX() < root.getRoot().getCamPos().getX() - 1
						|| this.pos.getY() < root.getRoot().getCamPos().getY() - 1)
					return;
				if ((this.pos.getX() - root.getRoot().getCamPos().getX())*s > 2100
						|| (this.pos.getY() - root.getRoot().getCamPos().getY())*s > 2100)
					return;
				Point2D cp = root.getRoot().getCamPos();
				g.drawImage(texture, (int) ((pos.getX() - cp.getX()) * s), (int) ((pos.getY() - cp.getY()) * s),
						(int) (s + 1), (int) (s + 1), null);
			}
		} else {
			g.drawImage(texture, 0, 0, null);
		}
	}

}
