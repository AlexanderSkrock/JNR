import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cdl.entities.Entity;
import cdl.entities.Player;
import cdl.misc.IPanel;
import cdl.sounds.SoundPlayer;

public class Intro extends JFrame {
	private static final long serialVersionUID = -4380891973741139536L;

	private JPanel pane;
	private List<String> lines;

	private Runnable task = () -> {
		int i = 0;
		Schleife:
		while (!Thread.interrupted()) {
			try {
				Thread.sleep(25);
			} catch (InterruptedException e) {
				break Schleife;
			}
			try {
				BufferedImage bi;
				try {
					bi = new BufferedImage(pane.getWidth(), pane.getHeight(), BufferedImage.TYPE_4BYTE_ABGR_PRE);
				} catch (IllegalArgumentException e) {
					continue;
				}
				int ln = 0;
				Graphics g = bi.getGraphics();
				g.drawImage(IPanel.IMG, 0, 0, bi.getWidth(), bi.getHeight(), null);
				g.setFont(new Font(Font.SERIF, Font.PLAIN, 40));
				g.setColor(Color.BLACK);
				for (String line : lines) {
					g.drawString(line, 80, pane.getHeight() + 50 * ln - i * 2);
					ln++;
				}
				i++;
				pane.getGraphics().drawImage(bi, 0, 0, null);
			} catch (NullPointerException e) {
			}
		}
		

		Main.MAIN.setVisible(true);
		Intro.this.dispose();
	};

	public static void main(String[] args) {
		SoundPlayer.SP.playMusic(SoundPlayer.MENU);
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				Intro intro = new Intro();
				intro.setVisible(true);
			}
		});
	}

	public Intro() {
		try {
			lines = Files.readAllLines(Paths.get("misc", "story.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.setUndecorated(true);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pane = new JPanel();
		this.setContentPane(pane);
		Thread t = new Thread(task);
		t.start();

		this.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					t.interrupt();
				}
			}
		});

	}

}
