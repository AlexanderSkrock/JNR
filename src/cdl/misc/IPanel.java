package cdl.misc;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class IPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2036589468156606400L;
	private BufferedImage img = IMG;
	private final static Path image = Paths.get("textures", "misc", "grassland.png");
	public final static BufferedImage IMG = constructImg();

	public IPanel() {
		super();
	}

	public IPanel(LayoutManager arg0) {
		super(arg0);
	}

	public IPanel(boolean arg0) {
		super(arg0);
	}

	public IPanel(LayoutManager arg0, boolean arg1) {
		super(arg0, arg1);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(img, 0, 0, getWidth(), getHeight(), Color.WHITE, null);
	}

	private static BufferedImage constructImg() {
		BufferedImage pic = null;
			try {
				pic = (BufferedImage) ImageIO.read(image.toFile());
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Konnte HintergrundBild nicht laden", "ich habe eine Problem",
						JOptionPane.ERROR_MESSAGE);
				System.exit(-1);
			}
		return pic;
	}

}
