import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
//git@github.com/GamingGears/JNR.git
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;

import cdl.data.BlockInfo;
import cdl.data.EntityInfo;
import cdl.entities.Destroyer;
import cdl.entities.FarmerWith;
import cdl.entities.FarmerWithout;
import cdl.entities.Player;
import cdl.gameExceptions.GameFileCorruptionExeption;
import cdl.movementManager.HitBox.Position;
import cdl.space.Block;
import cdl.space.World;

public class LevelEditor extends JFrame {
	private static final long serialVersionUID = -7269817541564645986L;

	private JPanel contentPane;
	private JTable blockTable;

	private World w = new World(1, 1, true);
	private JPanel worldPanel;
	private JTextField tfLevelName;
	private JScrollBar sBarWidth;
	private JScrollBar sBarHeight;
	private JTable entityTable;
	private JToggleButton tglbtnBlockEntity;
	private Boolean blöcke = Boolean.TRUE;

	/**
	 * Launch the application.
	 */
	public static void start() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LevelEditor frame = new LevelEditor();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public LevelEditor() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(400, 200, 900, 500);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setLocationRelativeTo(null);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				Main.MAIN.setVisible(true);
			}

			@Override
			public void windowClosing(WindowEvent e) {
				w.stop();
			}
		});

		this.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					int retVal = JOptionPane.showOptionDialog(LevelEditor.this, "Choose an option!", "Menu",
							JOptionPane.CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
							new Object[] { "Continue", "Return to Menu", "Exit" }, 0);
					switch (retVal) {
					case (1):
						w.stop();
						LevelEditor.this.dispose();
						break;
					case (2):
						System.exit(0);
						break;
					default:
						w.start();
					}
				}
			}
		});

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setPreferredSize(new Dimension(350, 5000));
		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		contentPane.add(scrollPane_1, BorderLayout.EAST);

		blockTable = new JTable(new BlockChoosingTableModel());
		scrollPane_1.setViewportView(blockTable);

		JPanel controllPanel = new JPanel();
		contentPane.add(controllPanel, BorderLayout.NORTH);

		JButton btnSaveWorld = new JButton("Save World");
		btnSaveWorld.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fcSave = new JFileChooser(Paths.get("levels").toAbsolutePath().toString());
				int retVal = fcSave.showSaveDialog(null);
				if (JFileChooser.APPROVE_OPTION == retVal) {
					Path p = fcSave.getSelectedFile().toPath().toAbsolutePath();
					try {
						BufferedWriter bw = Files.newBufferedWriter(p, StandardOpenOption.CREATE);
						bw.write("LevelName: " + tfLevelName.getText());
						bw.newLine();
						w.save(bw);
						bw.flush();
						bw.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		controllPanel.add(btnSaveWorld);

		JButton btnLoadWorld = new JButton("Load World");
		btnLoadWorld.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fcSave = new JFileChooser(Paths.get("levels").toAbsolutePath().toString());
				int retVal = fcSave.showOpenDialog(null);
				if (JFileChooser.APPROVE_OPTION == retVal) {
					Path p = fcSave.getSelectedFile().toPath().toAbsolutePath();
					try {
						BufferedReader br = Files.newBufferedReader(p);
						try {
							tfLevelName.setText(br.readLine().substring("LevelName: ".length()));
							w.build(br);
						} catch (GameFileCorruptionExeption e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						br.close();
						sBarWidth.setMaximum(w.getWidth());
						sBarHeight.setMaximum(w.getHeight());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				// TODO Auto-generated method stub
			}
		});
		controllPanel.add(btnLoadWorld);

		JLabel lblLevelname = new JLabel("LevelName:");
		controllPanel.add(lblLevelname);

		tfLevelName = new JTextField();
		controllPanel.add(tfLevelName);
		tfLevelName.setColumns(10);

		tglbtnBlockEntity = new JToggleButton("Bl\u00F6cke");
		tglbtnBlockEntity.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				synchronized (blöcke) {
					if (tglbtnBlockEntity.isSelected()) {
						System.out.println("Entities");
						tglbtnBlockEntity.setText("Entities");
						blöcke = Boolean.FALSE;
					} else {
						System.out.println("Blöcke");
						tglbtnBlockEntity.setText("Blöcke");
						blöcke = Boolean.TRUE;
					}
				}
			}
		});
		controllPanel.add(tglbtnBlockEntity);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));

		worldPanel = new JPanel();
		worldPanel.setMinimumSize(new Dimension(500, 500));
		worldPanel.addMouseMotionListener(new MouseAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				int xPos = (int) ((e.getX() / w.getScale()) + w.getCamPos().getX());
				int yPos = (int) ((e.getY() / w.getScale()) + w.getCamPos().getY());
				synchronized (blöcke) {
					if (blöcke) {
						if (blockTable.getSelectedRow() == -1)
							return;
						int id = (Integer) blockTable.getValueAt(blockTable.getSelectedRow(), 0);
						w.putBlock(id, xPos, yPos);
					}
				}
			}

		});
		worldPanel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				synchronized (blöcke) {
					if (blöcke) {
						int xPos = (int) ((e.getX() / w.getScale()) + w.getCamPos().getX());
						int yPos = (int) ((e.getY() / w.getScale()) + w.getCamPos().getY());
						if (blockTable.getSelectedRow() == -1)
							return;
						int id = (Integer) blockTable.getValueAt(blockTable.getSelectedRow(), 0);
						w.putBlock(id, xPos, yPos);
					} else {
						double xPos = (e.getX() / w.getScale()) + w.getCamPos().getX();
						double yPos = (e.getY() / w.getScale()) + w.getCamPos().getY();
						if (entityTable.getSelectedRow() == -1)
							return;
						if ("Player".equals(entityTable.getValueAt(entityTable.getSelectedRow(), 0))) {
							Player p = new Player(w, new Position(xPos, yPos));
							w.removeEntity(p);
							w.addEntity(p);
						} else if ("Farmer".equals(entityTable.getValueAt(entityTable.getSelectedRow(), 0))) {
							w.addEntity(new FarmerWithout(w, new Position(xPos, yPos), false));
						} else if ("fast Farmer".equals(entityTable.getValueAt(entityTable.getSelectedRow(), 0))) {
							w.addEntity(new FarmerWithout(w, new Position(xPos, yPos), true));
						} else if ("Farmer with Fork".equals(entityTable.getValueAt(entityTable.getSelectedRow(), 0))) {
							w.addEntity(new FarmerWith(w, new Position(xPos, yPos)));
						} else if ("Delete".equals(entityTable.getValueAt(entityTable.getSelectedRow(), 0))) {
							w.addEntity(new Destroyer(w, new Position(xPos, yPos)));
						}
					}
				}
			}
		});
		worldPanel.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				int rotate = e.getWheelRotation();
				System.out.println(w.getScale());
				w.setScale(w.getScale() * (1 - (0.05 / rotate)));
			}
		});
		panel.add(worldPanel, BorderLayout.CENTER);

		w.setCanvas(worldPanel);

		sBarHeight = new JScrollBar();
		sBarHeight.setMinimum(-1);
		sBarHeight.setMaximum(17);
		sBarHeight.setValue((sBarHeight.getMaximum() - sBarHeight.getModel().getExtent()) / 2);
		sBarHeight.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {

				Adjustable a = e.getAdjustable();

				if (a.getValue() == -1 && !e.getValueIsAdjusting()) {
					w.addTopRow();
					a.setValue(0);
					a.setMaximum(a.getMaximum() + 16);
				}
				if (a.getValue() == a.getMaximum() - sBarHeight.getModel().getExtent() && !e.getValueIsAdjusting()) {
					w.addRow();
					a.setMaximum(a.getMaximum() + 16);
					a.setValue(a.getMaximum() - sBarHeight.getModel().getExtent() - 1);
				}
				w.centerCamera(new Point(sBarWidth.getValue(), a.getValue()));

				System.out.println("x: " + a.getValue());
			}
		});

		panel.add(sBarHeight, BorderLayout.EAST);

		sBarWidth = new JScrollBar();
		sBarWidth.setMinimum(-1);
		sBarWidth.setMaximum(17);
		sBarWidth.setValue((sBarWidth.getMaximum() - sBarWidth.getModel().getExtent()) / 2);
		sBarWidth.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {

				Adjustable a = e.getAdjustable();

				if (a.getValue() == -1 && !e.getValueIsAdjusting()) {
					w.addLeftColumn();
					a.setValue(0);
					a.setMaximum(a.getMaximum() + 16);
				}
				if (a.getValue() == a.getMaximum() - sBarWidth.getModel().getExtent() && !e.getValueIsAdjusting()) {
					w.addColumn();
					a.setMaximum(a.getMaximum() + 16);
					a.setValue(a.getMaximum() - sBarWidth.getModel().getExtent() - 1);
				}
				w.centerCamera(new Point(a.getValue(), sBarHeight.getValue()));

				System.out.println("x: " + a.getValue());
			}
		});
		sBarWidth.setOrientation(JScrollBar.HORIZONTAL);
		panel.add(sBarWidth, BorderLayout.SOUTH);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(200, 5000));
		contentPane.add(scrollPane, BorderLayout.WEST);

		entityTable = new JTable(new EntityChoosingTableModel());
		entityTable.setRowHeight(64);
		scrollPane.setViewportView(entityTable);

		w.centerCamera(new Point(sBarWidth.getValue(), sBarHeight.getValue()));
		w.start();
		contentPane.repaint();

		requestFocus();
	}

	@SuppressWarnings("serial")
	private static class EntityChoosingTableModel extends AbstractTableModel {

		BufferedImage[] textures = new BufferedImage[5];
		String[] names = { "Player", "Farmer", "fast Farmer", "Farmer with Fork", "Delete" };

		public EntityChoosingTableModel() {
			try {
				Path p = Paths.get("textures", "player", "blue", "blue", "preview.png");
				if (Files.exists(p.toAbsolutePath())) {
					textures[0] = ImageIO.read(p.toAbsolutePath().toFile());
				}
				textures[1] = EntityInfo.EI.getFarmerWithoutSlow()[0];
				textures[2] = EntityInfo.EI.getFarmerWithoutFast()[0];
				textures[3] = EntityInfo.EI.getFarmerWithSlow()[0];
				// p = Paths.get("textures", "entities", "farmer", "no",
				// "walking", "0.png");
				// if (Files.exists(p.toAbsolutePath())) {
				// textures[1] = ImageIO.read(p.toAbsolutePath().toFile());
				// }
				// p = Paths.get("textures", "entities", "farmer", "no",
				// "running", "0.png");
				// if (Files.exists(p.toAbsolutePath())) {
				// textures[2] = ImageIO.read(p.toAbsolutePath().toFile());
				// }
				// p = Paths.get("textures", "entities", "farmer", "yes",
				// "walking", "0.png");
				// if (Files.exists(p.toAbsolutePath())) {
				// textures[3] = ImageIO.read(p.toAbsolutePath().toFile());
				// }
				p = Paths.get("textures", "misc", "none.png");
				if (Files.exists(p.toAbsolutePath())) {
					textures[4] = ImageIO.read(p.toAbsolutePath().toFile());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public Class<?> getColumnClass(int column) {
			switch (column) {
			case 0:
				return String.class;
			case 1:
				return Icon.class;
			default:
				return null;
			}
		}

		@Override
		public int getRowCount() {
			return 5;
		}

		@Override
		public Object getValueAt(int row, int column) {
			switch (column) {
			case 0:
				return names[row];
			case 1:
				return new ImageIcon(textures[row]);
			default:
				return null;
			}
		}

		@Override
		public String getColumnName(int column) {
			switch (column) {
			case 0:
				return "Name";
			case 1:
				return "Texture";
			default:
				return null;
			}
		}
	}

	@SuppressWarnings("serial")
	private static class BlockChoosingTableModel extends AbstractTableModel {

		List<Block> blocks = new ArrayList<>();

		public BlockChoosingTableModel() {
			Set<Integer> ids = BlockInfo.BI.getBlockIDs();
			for (int id : ids) {
				blocks.add(new Block(id));
			}
			blocks.sort((a, b) -> a.getID() - b.getID());
		}

		@Override
		public int getColumnCount() {
			return 3;
			// Block texture, Block name, Block ID, technical Block name
		}

		@Override
		public Class<?> getColumnClass(int column) {
			switch (column) {
			case 0:
				return Integer.class;
			case 1:
				return Icon.class;
			case 2:
				return String.class;
			default:
				return null;
			}
		}

		@Override
		public int getRowCount() {
			return blocks.size();
		}

		@Override
		public Object getValueAt(int row, int column) {
			switch (column) {
			case 0:
				return blocks.get(row).getID();
			case 1:
				return blocks.get(row).getTexture();
			case 2:
				return blocks.get(row).getName();
			default:
				return null;
			}
		}

		@Override
		public String getColumnName(int column) {
			switch (column) {
			case 0:
				return "ID";
			case 1:
				return "Texture";
			case 2:
				return "Name";
			default:
				return null;
			}
		}
	}
}
