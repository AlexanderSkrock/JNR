package cdl.data;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class PlayerInfo {

	private Path pTextures = Paths.get("textures", "player");

	public static final PlayerInfo PI = new PlayerInfo();

	private BufferedImage[] animations = new BufferedImage[10];
	private Map<Integer, String> colorsS = new HashMap<Integer, String>();
	private Map<Integer, String> colorsT = new HashMap<Integer, String>();

	private int shirts = -1;
	private int trousers = -1;

	private int leftKey = KeyEvent.VK_LEFT;
	private int rightKey = KeyEvent.VK_RIGHT;
	private int jumpKey = KeyEvent.VK_UP;

	private BufferedImage heart;
	
	public PlayerInfo() {
		shirts = pTextures.toFile().list().length - 1;
		trousers = Paths.get(pTextures.toAbsolutePath().toString(), pTextures.toFile().list()[0]).toFile()
				.list().length;

		for (int i = 0; i < shirts; i++) {
			colorsS.put(i, pTextures.toFile().list()[i]);
		}

		for (int i = 0; i < trousers; i++) {
			colorsT.put(i,
					Paths.get(pTextures.toAbsolutePath().toString(), pTextures.toFile().list()[0]).toFile().list()[i]);
		}

		Path player = Paths.get(pTextures.toAbsolutePath().toString(), colorsS.get(0), colorsT.get(0));
		for (int i = 0; i < animations.length; i++) {
			try {
				BufferedImage image;
				if (i == 10) {
					image = ImageIO.read(Paths.get(player.toAbsolutePath().toString(), "preview.png").toFile());

				} else {
					image = ImageIO
							.read(Paths.get(player.toAbsolutePath().toString(), String.valueOf(i) + ".png").toFile());
				}
				animations[i] = image;
			} catch (IOException e) {
				System.out.println("Error setting player graphic No. " + i + "!");
				e.printStackTrace();
			}
		}
		
		try {
			heart = ImageIO.read(Paths.get("textures", "misc", "heart.png").toFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public BufferedImage[][] getPreviewSprites() {
		if (shirts <= 0 || trousers <= 0)
			return null;
		BufferedImage[][] previews = new BufferedImage[shirts][trousers];
		for (int x = 0; x < shirts; x++) {
			for (int y = 0; y < trousers; y++) {
				try {
					Path pImage = Paths.get(pTextures.toAbsolutePath().toString(), colorsS.get(x), colorsT.get(y),
							"preview.png");
					previews[x][y] = ImageIO.read(pImage.toFile());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return previews;
	}

	public void setPlayer(int shirt, int trouser) {
		Path player = Paths.get(pTextures.toAbsolutePath().toString(), colorsS.get(shirt), colorsT.get(trouser));
		for (int i = 0; i < animations.length; i++) {
			try {
				BufferedImage image;
				image = ImageIO
						.read(Paths.get(player.toAbsolutePath().toString(), String.valueOf(i) + ".png").toFile());

				animations[i] = image;
			} catch (IOException e) {
				System.out.println("Error setting player graphic No. " + i + "!");
				e.printStackTrace();
			}
		}
	}

	public BufferedImage getSprite(int i) {
		if (i >= 0 && i < animations.length)
			return animations[i];
		return animations[10];
	}

	public BufferedImage[] getSprites() {
		return Arrays.copyOf(animations, animations.length);
	}

	public int getShirts() {
		return shirts;
	}

	public int getTrousers() {
		return trousers;
	}

	public int getLeftKey() {
		return leftKey;
	}

	public void setLeftKey(int key) {
		this.leftKey = key;
	}

	public int getRightKey() {
		return rightKey;
	}

	public void setRightKey(int key) {
		this.rightKey = key;
	}

	public int getJumpKey() {
		return jumpKey;
	}

	public void setJumpKey(int key) {
		this.jumpKey = key;
	}
	
	public BufferedImage getHeart() {
		return heart;
	}
}
