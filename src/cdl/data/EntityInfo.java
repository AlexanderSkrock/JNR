package cdl.data;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

public class EntityInfo {
	private Path pTextures = Paths.get("textures", "entities");
	private Path p;
	String[] files;

	public static final EntityInfo EI = new EntityInfo();

	// Bauer ohne Forke
	private Path pFarmerWithout = Paths.get("farmer", "no");
	private BufferedImage[] farmerWithoutSlow = new BufferedImage[4];
	private BufferedImage[] farmerWithoutFast = new BufferedImage[4];

	// Bauer mit Forke
	private Path pFarmerWith = Paths.get("farmer", "yes");
	private BufferedImage[] farmerWithSlow = new BufferedImage[4];
	private BufferedImage[] farmerWithFast = new BufferedImage[4];

	private EntityInfo() {

		// Bauer ohne Forke & langsam
		p = Paths.get(pTextures.toAbsolutePath().toString(), pFarmerWithout.toString(), "walking");
		files = p.toFile().list();
		farmerWithoutSlow = new BufferedImage[files.length];
		for (int i = 0; i < farmerWithoutSlow.length; i++) {
			try {
				farmerWithoutSlow[i] = ImageIO.read(Paths.get(p.toAbsolutePath().toString(), files[i]).toFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Bauer ohne Forke & schnell
		p = Paths.get(pTextures.toAbsolutePath().toString(), pFarmerWithout.toString(), "running");
		files = p.toFile().list();
//		farmerWithoutFast = new BufferedImage[files.length];
		for (int i = 0; i < farmerWithoutFast.length; i++) {
			try {
				farmerWithoutFast[i] = ImageIO.read(Paths.get(p.toAbsolutePath().toString(), files[i]).toFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Bauer mit Forke & langsam
		p = Paths.get(pTextures.toAbsolutePath().toString(), pFarmerWith.toString(), "walking");
		files = p.toFile().list();
//		farmerWithSlow = new BufferedImage[files.length];
		for (int i = 0; i < farmerWithSlow.length; i++) {
			try {
				farmerWithSlow[i] = ImageIO.read(Paths.get(p.toAbsolutePath().toString(), files[i]).toFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Bauer mit Forke & schnell
		p = Paths.get(pTextures.toAbsolutePath().toString(), pFarmerWith.toString(), "running");
		files = p.toFile().list();
//		farmerWithFast = new BufferedImage[files.length];
		for (int i = 0; i < farmerWithFast.length; i++) {
			try {
				farmerWithFast[i] = ImageIO.read(Paths.get(p.toAbsolutePath().toString(), files[i]).toFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public BufferedImage[] getFarmerWithoutSlow() {
		return farmerWithoutSlow;
	}
	
	public BufferedImage[] getFarmerWithoutFast() {
		return farmerWithoutFast;	
	}
	
	public BufferedImage[] getFarmerWithSlow() {
		return farmerWithSlow;		
	}
	
	public BufferedImage[] getFarmerWithFast() {
		return farmerWithFast;	
	}
}