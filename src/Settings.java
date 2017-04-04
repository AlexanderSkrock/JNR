import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cdl.data.PlayerInfo;
import cdl.misc.IPanel;

public class Settings extends JFrame {
	private static final long serialVersionUID = 8036927695992927060L;

	public static void start() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Settings frame = new Settings();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Settings() {
		setBounds(0, 0, 500, 600);
		setLocationRelativeTo(null);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				e.getWindow().dispose();
				Main.MAIN.setVisible(true);
			}
		});

		IPanel contentPane = new IPanel();
		contentPane.setLayout(new GridLayout(3, 3, 5, 5));
		setContentPane(contentPane);

		JPanel pControls = new JPanel();
		pControls.setLayout(new GridLayout(3, 3, 5, 5));

		JLabel lLeft = new JLabel("Left");
		JLabel lKeyLeft = new JLabel(KeyEvent.getKeyText(PlayerInfo.PI.getLeftKey()));
		JButton bKeyLeft = new JButton("Change");
		bKeyLeft.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				for (KeyListener kl : pControls.getKeyListeners()) {
					Settings.this.removeKeyListener(kl);
				}
				for (Component c : pControls.getComponents()) {
					if (c instanceof JButton)
						((JButton) c).setText("Change");
				}
				((JButton) ae.getSource()).setText("Press a key");
				
				Settings.this.addKeyListener(new KeyAdapter() {
					@Override
					public void keyPressed(KeyEvent ke) {
						PlayerInfo.PI.setLeftKey(ke.getKeyCode());
						lKeyLeft.setText(KeyEvent.getKeyText(ke.getKeyCode()));
						((JButton) ae.getSource()).setText("Change");
						Settings.this.removeKeyListener(this);
					}
				});
				Settings.this.requestFocus();
			}
		});
		pControls.add(lLeft);
		pControls.add(lKeyLeft);
		pControls.add(bKeyLeft);

		JLabel lRight = new JLabel("Right");
		JLabel lKeyRight = new JLabel(KeyEvent.getKeyText(PlayerInfo.PI.getRightKey()));
		JButton bKeyRight = new JButton("Change");
		bKeyRight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				for (KeyListener kl : pControls.getKeyListeners()) {
					Settings.this.removeKeyListener(kl);
				}
				for (Component c : pControls.getComponents()) {
					if (c instanceof JButton)
						((JButton) c).setText("Change");
				}
				((JButton) ae.getSource()).setText("Press a key");
				
				Settings.this.addKeyListener(new KeyAdapter() {
					@Override
					public void keyPressed(KeyEvent ke) {
						PlayerInfo.PI.setRightKey(ke.getKeyCode());
						lKeyRight.setText(KeyEvent.getKeyText(ke.getKeyCode()));
						((JButton) ae.getSource()).setText("Change");
						Settings.this.removeKeyListener(this);
					}
				});
				Settings.this.requestFocus();
			}
		});
		pControls.add(lRight);
		pControls.add(lKeyRight);
		pControls.add(bKeyRight);

		JLabel lJump = new JLabel("Jump");
		JLabel lKeyJump = new JLabel(KeyEvent.getKeyText(PlayerInfo.PI.getJumpKey()));
		JButton bKeyJump = new JButton("Change");
		bKeyJump.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				for (KeyListener kl : pControls.getKeyListeners()) {
					Settings.this.removeKeyListener(kl);
				}
				for (Component c : pControls.getComponents()) {
					if (c instanceof JButton)
						((JButton) c).setText("Change");
				}
				((JButton) ae.getSource()).setText("Press a key");

				Settings.this.addKeyListener(new KeyAdapter() {
					@Override
					public void keyPressed(KeyEvent ke) {
						PlayerInfo.PI.setJumpKey(ke.getKeyCode());
						lKeyJump.setText(KeyEvent.getKeyText(ke.getKeyCode()));
						((JButton) ae.getSource()).setText("Change");
						Settings.this.removeKeyListener(this);
					}
				});
				Settings.this.requestFocus();
			}
		});
		pControls.add(lJump);
		pControls.add(lKeyJump);
		pControls.add(bKeyJump);

		for (Component c : pControls.getComponents()) {
			if (c instanceof JLabel)
				((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
		}

		contentPane.add(pControls);
		
		JButton btnSetChar = new JButton("Choose a character");
		btnSetChar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				CharacterChooser cc = new CharacterChooser();
				cc.setVisible(true);
			}	
		});
		
		contentPane.add(btnSetChar);
	}
	
	public static class CharacterChooser extends JFrame {
		private static final long serialVersionUID = -5245878346502960032L;

		private int shirt = 1;
		private int trouser = 1;
		private int shirts = 5;
		private int trousers = 3;

		private BufferedImage[][] sprites = new BufferedImage[PlayerInfo.PI.getShirts()][PlayerInfo.PI.getTrousers()];

		public CharacterChooser() {
			sprites = PlayerInfo.PI.getPreviewSprites();

			setBounds(0, 0, 500, 300);
			setLocationRelativeTo(null);
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					e.getWindow().dispose();
					Main.MAIN.setVisible(true);
				}
			});

			JPanel contentPane = new JPanel();
			contentPane.setLayout(new BorderLayout(0, 0));
			setContentPane(contentPane);

			// GRAFIK IN DER MITTE
			JPanel c = new JPanel() {
				private static final long serialVersionUID = 358442481680305333L;

				public void paint(Graphics g) {
					BufferedImage before = sprites[shirt][trouser];

					AffineTransform at = new AffineTransform();
					double scale = (this.getWidth() / before.getWidth() < this.getHeight() / before.getHeight())
							? this.getWidth() / before.getWidth() : this.getHeight() / before.getHeight();
					at.scale(scale, scale);
					AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

					BufferedImage after = new BufferedImage((int) (before.getWidth() * at.getScaleX()),
							(int) (before.getHeight() * at.getScaleY()), BufferedImage.TYPE_INT_ARGB);
					after = scaleOp.filter(before, after);
					g.drawImage(after, (this.getWidth() - after.getWidth()) / 2,
							(this.getHeight() - after.getHeight()) / 2, after.getWidth(), after.getHeight(), null);// ,
					// getWidth(),
					// getHeight(),
					// null);
				}
			};

			// Buttons
			JButton btnSL = new JButton(); // Shirt - Left
			JButton btnTL = new JButton(); // Trouser - Left
			JButton btnSR = new JButton(); // Shirt - Right
			JButton btnTR = new JButton(); // Trouser - Right

			// LINKER CHOOSER
			JPanel cl = new JPanel(); // Linker Teil für die beiden linken
										// Buttons
			cl.setLayout(new GridLayout(2, 1));

			btnSL.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (shirt > 0) {
						shirt--;
						repaint();
						if (shirt == 0)
							btnSL.setEnabled(false);
						if (!btnSR.isEnabled())
							btnSR.setEnabled(true);
					}
				}
			});
			btnSL.setText("Vorheriges T-Shirt");

			btnTL.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (trouser > 0) {
						trouser--;
						repaint();
						if (trouser == 0)
							btnTL.setEnabled(false);
						if (!btnTR.isEnabled())
							btnTR.setEnabled(true);
					}
				}
			});
			btnTL.setText("Vorherige Hose");

			cl.add(btnSL);
			cl.add(btnTL);

			// RECHTER CHOOSER
			JPanel cr = new JPanel(); // Rechter Teil für die beiden rechten
										// Buttons
			cr.setLayout(new GridLayout(2, 1));

			btnSR.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (shirt < shirts - 1) {
						shirt++;
						repaint();
						if (shirt == shirts - 1)
							btnSR.setEnabled(false);
						if (!btnSL.isEnabled())
							btnSL.setEnabled(true);
					}
				}
			});
			btnSR.setText("Nächstes T-Shirt");

			btnTR.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (trouser < trousers - 1) {
						trouser++;
						repaint();
						if (trouser == trousers - 1)
							btnTR.setEnabled(false);
						if (!btnTL.isEnabled())
							btnTL.setEnabled(true);
					}
				}
			});
			btnTR.setText("Nächste Hose");

			cr.add(btnSR);
			cr.add(btnTR);

			JButton btnAccept = new JButton("Bestätigen");
			btnAccept.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					PlayerInfo.PI.setPlayer(shirt, trouser);
					CharacterChooser.this.dispose();
				}
			});

			contentPane.add(new JLabel("Wähle Deinen Charakter!", JLabel.CENTER), BorderLayout.NORTH);
			contentPane.add(cl, BorderLayout.WEST);
			contentPane.add(c, BorderLayout.CENTER);
			contentPane.add(cr, BorderLayout.EAST);
			contentPane.add(btnAccept, BorderLayout.SOUTH);
		}
	}
}
