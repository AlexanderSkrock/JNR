import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import cdl.data.LevelInfo;
import cdl.misc.IPanel;
import cdl.sounds.SoundPlayer;

public class Main extends JFrame {
	private static final long serialVersionUID = -7269817541564645986L;

	public final static Main MAIN = new Main();
	private JPanel contentPane;

	private Main() {		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		setBounds(400, 200, 900, 500);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setLocationRelativeTo(null);
		setUndecorated(true);
		contentPane = new IPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		contentPane.setOpaque(true);

		JPanel centerPane = new JPanel();
		centerPane.setLayout(new GridLayout(4, 1, 0, 10));
		centerPane.setBorder(BorderFactory.createEmptyBorder(5,5, 5, 5));
		centerPane.setOpaque(true);

		JButton btnPlay = new JButton("Play");
		btnPlay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Main.this.dispose();
				LevelChooser lc = new LevelChooser();
				lc.setVisible(true);
			}
		});
		centerPane.add(btnPlay);

		JButton btnLevelEditor = new JButton("Level Editor");
		btnLevelEditor.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Main.this.setVisible(false);
				LevelEditor.start();
			}
		});
		centerPane.add(btnLevelEditor);

		JButton btnSettings = new JButton("Settings");
		btnSettings.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Main.this.setVisible(false);
				Settings.start();
			}
		});
		centerPane.add(btnSettings);

		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		centerPane.add(btnExit);

		contentPane.add(Box.createVerticalStrut(50), BorderLayout.NORTH);
		contentPane.add(Box.createHorizontalStrut(100), BorderLayout.WEST);
		contentPane.add(centerPane, BorderLayout.CENTER);
		contentPane.add(Box.createHorizontalStrut(100), BorderLayout.EAST);
		contentPane.add(Box.createVerticalStrut(50), BorderLayout.SOUTH);
	}

	public static class LevelChooser extends JFrame {
		private static final long serialVersionUID = -8208883026582959821L;

		private int level = -1;

		public LevelChooser() {
			setBounds(0, 0, 400, 500);
			setLocationRelativeTo(null);
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					e.getWindow().dispose();
					Main.MAIN.setVisible(true);;
				}
			});

			JPanel contentPane = new JPanel();
			contentPane.setLayout(new BorderLayout(0, 0));
			setContentPane(contentPane);

			String[] lvlNames = new String[LevelInfo.LI.getAmount()];
			for (int i = 0; i < lvlNames.length; i++) {
				lvlNames[i] = LevelInfo.LI.getName(i + 1);
			}
			JList<String> levels = new JList<String>(lvlNames);
			contentPane.add(new JScrollPane(levels), BorderLayout.CENTER);

			JButton btnAccept = new JButton("Bestätigen");
			btnAccept.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (levels.getSelectedValue() != null)
						level = levels.getSelectedIndex();
					if (level == -1)
						JOptionPane.showMessageDialog(LevelChooser.this, "Es wurde kein Level ausgewählt!", "",
								JOptionPane.ERROR_MESSAGE);
					else {
						LevelChooser.this.dispose();
						Game.start(LevelInfo.LI.getPath(level + 1));
					}
				}
			});

			JButton btnLoad = new JButton("Eigenes Level laden");
			btnLoad.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser fc = new JFileChooser(Paths.get("levels").toAbsolutePath().toString());
					int retVal = fc.showOpenDialog(null);
					Path p = null;
					if (JFileChooser.APPROVE_OPTION == retVal) {
						p = fc.getSelectedFile().toPath().toAbsolutePath();
					}
					if (p.toFile().exists()) {
						LevelChooser.this.dispose();
						Game.start(p);
					} else {
						JOptionPane.showMessageDialog(LevelChooser.this, "Es wurde kein Level ausgewählt!", "",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			});

			JPanel btns = new JPanel();
			btns.setLayout(new GridLayout(1, 2, 0, 5));
			btns.add(btnAccept);
			btns.add(btnLoad);

			contentPane.add(btns, BorderLayout.SOUTH);
		}
	}
}
