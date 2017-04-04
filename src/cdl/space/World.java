package cdl.space;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.event.EventListenerList;

import cdl.entities.Entity;
import cdl.entities.FarmerWith;
import cdl.entities.FarmerWithout;
import cdl.entities.Player;
import cdl.gameExceptions.GameFileCorruptionExeption;
import cdl.movementManager.HitBox.Position;
import cdl.movementManager.MovementEvent;
import cdl.movementManager.MovementListener;
import cdl.movementManager.Tickable;
import cdl.util.Matrix;

public class World implements Tickable {
	private List<Entity> entities = new ArrayList<>();// liste, falls es gleiche
														// gibt
	private Matrix<Chunk> chunks;

	public static final int TPS = 30;// ticks per second, for every Tick one
										// Frame

	public ExecutorService worldManager;
	public ScheduledExecutorService worldTicker;

	private List<Tickable> tkbls = new LinkedList<>();

	// only read
	private boolean editMode = false;

	private Boolean lost = false;
	// pixel per Block
	private Double scale = 1.8 * 32.;

	private final double gravitation = 5;

	private Point2D camPos = new Point2D.Double(0, 0);

	private Component canvas = null;

	public Set<MovementEvent> met = new HashSet<>(); // movementevents which
														// occured this tick
	EventListenerList ell = new EventListenerList();

	private BufferedImage gameover;

	public World(int width, int height, boolean editable) {
		this(width, height);
		editMode = editable;
	}

	public World(int width, int height) {
		chunks = new Matrix<>(width, height);
		int ms = width * height;
		for (int i = 0; i < ms; i++) {
			chunks.put(i % width, i / width, new Chunk(this, i % width, i / width));
		}
		tkbls.add(this);
	}

	public World(BufferedReader br) throws GameFileCorruptionExeption {
		build(br);
		try {
			gameover = ImageIO.read(Paths.get("textures", "misc", "gameover.png").toFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		tkbls.add(this);
	}

	public BufferedReader build(BufferedReader br) throws GameFileCorruptionExeption {
		try {
			entities = new ArrayList<>();
			String line = br.readLine();
			if (line.contains("LevelName:")) {
				line = br.readLine();
			}
			String[] s = line.split(";");
			chunks = new Matrix<Chunk>(Integer.parseInt(s[0].trim()), Integer.parseInt(s[1].trim()));
			System.out.println(chunks.Xsize());
			System.out.println(chunks.Ysize());
			for (int i = 0; i < chunks.Ysize(); i++)
				for (int j = 0; j < chunks.Xsize(); j++) {
					chunks.put(j, i, new Chunk(this, br, new Point(j, i)));
				}
			line = br.readLine();
			while (line != null) {
				if (line.contains("Player ")) {
					line = new String(line.substring("Player ".length()));
					s = line.split(";");
					this.addEntity(new Player(this, new Position(Double.parseDouble(s[0]), Double.parseDouble(s[1]))));
				} else if (line.contains("FarmerNoFork ")) {
					line = new String(line.substring("FarmerNoFork ".length()));
					s = line.split(";");
					this.addEntity(new FarmerWithout(this,
							new Position(Double.parseDouble(s[0]), Double.parseDouble(s[1])), false));
				} else if (line.contains("FarmerWithFork ")) {
					line = new String(line.substring("FarmerWithFork ".length()));
					s = line.split(";");
					this.addEntity(
							new FarmerWith(this, new Position(Double.parseDouble(s[0]), Double.parseDouble(s[1]))));
				} else if (line.contains("FarmerNoForkFast ")) {
					line = new String(line.substring("FarmerNoForkFast ".length()));
					s = line.split(";");
					this.addEntity(new FarmerWithout(this,
							new Position(Double.parseDouble(s[0]), Double.parseDouble(s[1])), true));
				}
				line = br.readLine();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			throw new GameFileCorruptionExeption("Fehler beim Einlesen der Spieldatei!");

		}
		return br;
	}

	public BufferedWriter save(BufferedWriter bw) throws IOException {
		bw.write(String.valueOf(chunks.Xsize()) + ";" + String.valueOf(chunks.Ysize()));
		bw.newLine();
		for (int i = 0; i < chunks.Ysize(); i++)
			for (int j = 0; j < chunks.Xsize(); j++) {
				bw.write("Chunk");
				if (!chunks.get(j, i).isEmpty()) {
					bw.newLine();
					chunks.get(j, i).save(bw);
				} else {
					bw.write(" empty");
					bw.newLine();
				}
			}
		synchronized (entities) {
			for (Entity e : entities)
				e.save(bw);
		}
		return bw;
	}

	public void addMovementListener(MovementListener ml) {
		ell.add(MovementListener.class, ml);
	}

	public void removeMovementListener(MovementListener ml) {
		ell.remove(MovementListener.class, ml);
	}

	public void notifyMovement(MovementEvent evt) {
		synchronized (met) {
			if (met.contains(evt)) {
				return;
			}
			met.add(evt);
			Chunk c = evt.getSource().getChunk();
			for (MovementListener l : ell.getListeners(MovementListener.class)) {
				double dist = l.getChunk().getPos().distance(c.getPos());
				if (dist < 1.5)
					l.movementOccured(evt);
			}
		}
	}

	public Chunk getChunk(int x, int y) {
		return chunks.get(x, y);
	}

	public long getTick() {
		synchronized (n) {
			return n;
		}
	}

	private Long n = 0l;

	@Override
	public void tick() {
		// System.out.println("tick: " + (n++));
		// long startTime = System.nanoTime();
		if (!editMode) {
			synchronized (met) {
				met.clear();
				synchronized (lost) {
					if (!lost) {
						List<Entity> ents = this.getEntities();
						try {
								for (Entity e : ents)
									e.tick();
						} catch (ConcurrentModificationException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		if (canvas != null) {
			draw(canvas);
		} else
			System.out.println("null");
		// System.out.println(((System.nanoTime() - startTime) / 1_000_000.) +
		// "ms");
	}

	public void start() {
		this.worldManager = Executors.newWorkStealingPool();
		this.worldTicker = Executors.newSingleThreadScheduledExecutor();
		worldTicker.scheduleAtFixedRate(() -> {
			synchronized (n) {
				n++;
			}
			for (Tickable t : getTickables())
				try {
					t.tick();
				} catch (Exception e) {
					e.printStackTrace();
				}

		}, 0, 1_000_000_000L / TPS, TimeUnit.NANOSECONDS);
	}

	public void stop() {
		this.worldTicker.shutdownNow();
		this.worldManager.shutdownNow();
	}

	public void end() {
		synchronized (lost) {
			this.lost = Boolean.TRUE;
		}
	}

	public double getScale() {
		synchronized (scale) {
			return scale;
		}
	}

	public void setScale(double scale) {
		synchronized (this.scale) {
			this.scale = scale;
		}
	}

	public Point2D getCamPos() {
		synchronized (camPos) {
			return new Point2D.Double(camPos.getX(), camPos.getY());
		}
	}

	public void centerCamera(Point2D p) {
		if (p == null)
			throw new IllegalArgumentException();
		synchronized (camPos) {
			synchronized (canvas) {
				synchronized (scale) {
					if (canvas != null)
						camPos.setLocation(p.getX() - canvas.getWidth() / (2 * scale),
								p.getY() - canvas.getHeight() / (2 * scale));
				}
			}
		}
	}

	public void setCamPos(Point2D p) {
		if (p == null)
			throw new IllegalArgumentException();
		synchronized (camPos) {
			camPos.setLocation(p.getX(), p.getY());
		}
	}

	public void setCanvas(Component g) {
		canvas = g;
	}

	public void addTopRow() {
		synchronized (chunks) {
			chunks.resize(chunks.Xsize(), chunks.Ysize() + 1);
			chunks.translate(0, 0, chunks.Xsize(), chunks.Ysize() - 1, 0, 1);
			for (int i = 0; i < chunks.Xsize(); i++) {
				chunks.put(i, 0, new Chunk(this, i, 0));
			}
			for (int i = 0; i < chunks.Xsize(); i++)
				for (int j = 0; j < chunks.Ysize(); j++)
					chunks.get(i, j).setPos(new Point(i, j));
		}
		synchronized (entities) {
			for (Entity e : entities) {
				e.move(0, 16);
			}
		}
	}

	public void addRow() {
		synchronized (chunks) {
			chunks.resize(chunks.Xsize(), chunks.Ysize() + 1);
			for (int i = 0; i < chunks.Xsize(); i++) {
				chunks.put(i, chunks.Ysize() - 1, new Chunk(this, i, chunks.Ysize() - 1));
			}
		}
	}

	public void addLeftColumn() {
		synchronized (chunks) {
			chunks.resize(chunks.Xsize() + 1, chunks.Ysize());
			chunks.translate(0, 0, chunks.Xsize() - 1, chunks.Ysize(), 1, 0);
			for (int i = 0; i < chunks.Ysize(); i++) {
				chunks.put(0, i, new Chunk(this, -1, i));
			}
			for (int i = 0; i < chunks.Xsize(); i++)
				for (int j = 0; j < chunks.Ysize(); j++)
					chunks.get(i, j).setPos(new Point(i, j));
		}
		synchronized (entities) {
			for (Entity e : entities) {
				e.move(16, 0);
			}
		}
	}

	public void addColumn() {
		synchronized (chunks) {
			chunks.resize(chunks.Xsize() + 1, chunks.Ysize());
			for (int i = 0; i < chunks.Ysize(); i++) {
				chunks.put(chunks.Xsize() - 1, i, new Chunk(this, chunks.Xsize() - 1, i));
			}
		}
	}

	public Graphics getCanGraphics() {
		synchronized (canvas) {
			return canvas.getGraphics();
		}
	}

	public int getWidth() {
		synchronized (chunks) {
			return chunks.Xsize() * 16;
		}
	}

	public int getHeight() {
		synchronized (chunks) {
			return chunks.Ysize() * 16;
		}
	}

	public void trim() {
		synchronized (chunks) {
			boolean trimmed;
			do {
				// leftColumn
				trimmed = false;
				boolean trimmable = true;
				if (chunks.Ysize() == 0)
					return;
				for (Chunk c : chunks.getColumn(0, new Chunk[chunks.Ysize()])) {
					trimmable &= c.isEmpty();
				}
				if (trimmable) {
					chunks.translate(0, 0, chunks.Xsize() - 1, chunks.Ysize(), -1, 0);
					chunks.resize(chunks.Xsize() - 1, chunks.Ysize());
					trimmed = true;
				}
			} while (trimmed);
			do {
				// rightColumn
				trimmed = false;
				boolean trimmable = true;
				if (chunks.Ysize() == 0)
					return;
				for (Chunk c : chunks.getColumn(chunks.Xsize() - 1, new Chunk[chunks.Ysize()])) {
					trimmable &= c.isEmpty();
				}
				if (trimmable) {
					chunks.resize(chunks.Xsize() - 1, chunks.Ysize());
					trimmed = true;
				}
			} while (trimmed);
			do {
				// topRow
				trimmed = false;
				boolean trimmable = true;
				if (chunks.Ysize() == 0)
					return;
				for (Chunk c : chunks.getColumn(0, new Chunk[chunks.Xsize()])) {
					trimmable &= c.isEmpty();
				}
				if (trimmable) {
					chunks.translate(0, 0, chunks.Xsize(), chunks.Ysize() - 1, 0, -1);
					chunks.resize(chunks.Xsize(), chunks.Ysize() - 1);
					trimmed = true;
				}
			} while (trimmed);
			do {
				// bottomRow
				trimmed = false;
				boolean trimmable = true;
				if (chunks.Ysize() == 0)
					return;
				for (Chunk c : chunks.getColumn(0, new Chunk[chunks.Xsize()])) {
					trimmable &= c.isEmpty();
				}
				if (trimmable) {
					chunks.resize(chunks.Xsize(), chunks.Ysize() - 1);
					trimmed = true;
				}
			} while (trimmed);
		}
	}

	protected void draw(Component canv) {
		// TODO add the BackgroundImage and so on
		if (canv == null)
			return;

		if (canv.getWidth() == 0 || canv.getHeight() == 0)
			return;

		BufferedImage temp = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
		Graphics g = temp.getGraphics();

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, temp.getWidth(), temp.getHeight());

		Point2D cp = new Point2D.Double();
		double scale;
		synchronized (this.scale) {
			scale = this.scale;
		}
		synchronized (camPos) {
			cp.setLocation(camPos);
		}
		synchronized (chunks) {
			for (Chunk c : chunks)
				c.draw(g);
		}
		synchronized (entities) {
			for (Entity e : entities)
				e.draw(g);
		}
		// draw grid
		if (editMode) {
			g.setColor(Color.black);
			double sizex = getWidth() * scale;
			double sizey = getHeight() * scale;
			for (double i = 0; i <= sizex; i += scale)
				g.drawLine((int) (i - cp.getX() * scale), (int) (-cp.getY() * scale), (int) (i - cp.getX() * scale),
						(int) (sizey - cp.getY() * scale));
			for (double i = 0; i <= sizey; i += scale)
				g.drawLine((int) (-cp.getX() * scale), (int) (i - cp.getY() * scale), (int) (sizex - cp.getX() * scale),
						(int) (i - cp.getY() * scale));

		}
		synchronized (lost) {
			if (lost) {
				AffineTransform at = new AffineTransform();
				double s = (canvas.getWidth() / gameover.getWidth() < canvas.getHeight() / gameover.getHeight())
						? canvas.getWidth() / gameover.getWidth() : canvas.getHeight() / gameover.getHeight();
				at.scale(s, s);
				AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

				BufferedImage after = new BufferedImage((int) (gameover.getWidth() * at.getScaleX()),
						(int) (gameover.getHeight() * at.getScaleY()), BufferedImage.TYPE_INT_ARGB);
				after = scaleOp.filter(gameover, after);
				g.drawImage(after, (canvas.getWidth() - after.getWidth()) / 2,
						(canvas.getHeight() - after.getHeight()) / 2, null);

				// g.setColor(Color.BLACK);
				// g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN,
				// 150));
				// g.drawString("GAME OVER", 0, canvas.getHeight() / 2);
			}
		}
		canv.getGraphics().drawImage(temp, 0, 0, canv.getWidth(), canv.getHeight(), null);
	}

	public double getGravitation() {
		return gravitation;
	}

	public void addTickable(Tickable t) {
		if (t != null)
			synchronized (tkbls) {
				tkbls.add(t);
			}
	}

	public void removeTickable(Tickable t) {
		if (t != null)
			synchronized (tkbls) {
				tkbls.remove(t);
			}
	}

	public void addEntity(Entity e) {
		if (e != null)
			synchronized (entities) {
				entities.add(e);
			}
	}

	public void removeEntity(Entity e) {
		if (e != null)
			synchronized (entities) {
				if (!(e instanceof Player)) {
					entities.remove(e);
				} else {
					for (int i = 0; i < entities.size(); i++) {
						if (e.equals(entities.get(i))) {
							entities.remove(i);
							i--;
						}
					}
				}
			}
	}

	public List<Entity> getEntities() {
		synchronized (entities) {
			return new ArrayList<Entity>(entities);
		}
	}

	public Player getPlayer() {
		synchronized (entities) {
			for (Entity e : entities)
				if (e instanceof Player)
					return (Player) e;
			return null;
		}
	}

	public Block getBlock(int x, int y) {
		synchronized (chunks) {
			return chunks.get(x / 16, y / 16).getBlock(x % 16, y % 16);
		}
	}

	public void putBlock(int id, int x, int y) {
		synchronized (chunks) {
			chunks.get(x / 16, y / 16).putBlock(id, x, y);
		}
	}

	public boolean isEditable() {
		return editMode;
	}

	public List<Tickable> getTickables() {
		synchronized (tkbls) {
			return new ArrayList<Tickable>(tkbls);
		}
	}
}
