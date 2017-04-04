import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cdl.entities.Entity;
import cdl.entities.Player;
import cdl.gameExceptions.GameFileCorruptionExeption;
import cdl.sounds.SoundPlayer;
import cdl.space.World;

public class Game extends JFrame {
	private static final long serialVersionUID = -8140766836735060008L;

	private World w;
	private JPanel worldPanel;

	/**
	 * Launch the application.
	 */
	public static void start(Path level) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Game frame = new Game(level);
					SoundPlayer.SP.changeMusicTo(SoundPlayer.GAME);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void startLevel(Path level) {
		try {
			BufferedReader br = Files.newBufferedReader(level);
			try {
				w = new World(br) {
					Boolean ended = false;

					@Override
					public void end() {
						synchronized (ended) {
							if (ended)
								return;
							ended = true;
						}
						super.end();
						Thread t = new Thread(() -> {
							int retVal = JOptionPane.showOptionDialog(null, "Choose an option!", "Menu",
									JOptionPane.CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
									new Object[] { "Try again", "Return to Menu", "Exit" }, 0);
							switch (retVal) {
							case (1):
								Main.MAIN.setVisible(false);
								break;
							case (2):
								System.exit(0);
								break;
							default:
								w.setCanvas(null);
								w.stop();
								startLevel(level);
							}
						});
						t.start();
					}
				};
			} catch (GameFileCorruptionExeption e) {
				e.printStackTrace();
			} finally {
				br.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.addKeyListener(w.getPlayer());
		w.getPlayer().resetKeys();
		w.setCanvas(worldPanel);
		w.start();
	}

	/**
	 * Create the frame.
	 */
	public Game(Path level) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setUndecorated(true);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		// setBounds(400, 200, 900, 500);
		setLocationRelativeTo(null);
		JPanel contentPane = new JPanel();
		// contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));

		setContentPane(contentPane);

		this.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					w.stop();
					int retVal = JOptionPane.showOptionDialog(Game.this, "Choose an option!", "Menu",
							JOptionPane.CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
							new Object[] { "Continue", "Return to Menu", "Exit" }, 0);
					switch (retVal) {
					case (1):
						Game.this.dispose();
						Main.MAIN.setVisible(true);
						break;
					case (2):
						System.exit(0);
						break;
					default:
						w.getPlayer().resetKeys();
						w.start();
					}
				}
			}
		});

		this.addWindowListener(new WindowAdapter() {

			public void windowClosed(WindowEvent e) {
				SoundPlayer.SP.changeMusicTo(SoundPlayer.MENU);
			}
		});

		worldPanel = new JPanel();

		// worldPanel.addMouseWheelListener(new MouseWheelListener() {
		// @Override
		// public void mouseWheelMoved(MouseWheelEvent e) {
		// int rotate = e.getWheelRotation();
		// System.out.println(w.getScale());
		// w.setScale(w.getScale() * (1 - (0.05 / rotate)));
		// }
		// });

		contentPane.add(worldPanel);

		startLevel(level);
	}
}
