package cdl.data;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;

import cdl.space.Block;
import cdl.space.blockProperties.Damaging;
import cdl.space.blockProperties.Solid;
import cdl.space.blockProperties.SolidSlope;

public class BlockInfo {

	public static final Path data = Paths.get("propSheets", "BLOCKS.txt");
	public static final Path textures = Paths.get("textures", "blocks");
	public static final Path extData = Paths.get("custom", "BLOCKS.txt");
	public static final Path extTextures = Paths.get("custom", "textures");

	public static final BlockInfo BI = new BlockInfo();

	private Map<Integer, BProps> infos = new HashMap<>();
	private Map<Integer, BProps> extInfos = new HashMap<>();
	// used for modded Blocks
	private Map<IDPair, Integer> idLinking = new HashMap<>();
	private Random rdm = new Random();

	private BlockInfo() {
		List<String> lines = null;

		// reading internal data
		try {
			lines = Files.readAllLines(data);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		int line = 1;
		for (String l : lines) {
			String[] parts = l.split(":");
			if (parts.length != 4) {
				System.out.println("BLOCK.txt ist fehlerhaft in Zeile " + line);
				System.exit(1);
			}
			// ID lesen
			int id = Integer.parseInt(parts[0]);
			if (id < 0) {
				System.out.println("BLOCK.txt ist fehlerhaft in Zeile " + line);
				System.exit(1);
			}
			BProps bp = new BProps();
			// Namen laden
			bp.name = parts[1].trim();
			// Texturen laden
			String[] txts = parts[2].split(",");
			bp.img = new BufferedImage[txts.length];
			for (int i = 0; i < txts.length; i++) {
				try {
					bp.img[i] = ImageIO.read(Paths.get(textures.toString(), txts[i].trim()).toFile());
				} catch (IOException e) {
					System.out.println("BLOCK.txt ist fehlerhaft in Zeile " + line);
					System.out.println("Textur fehlt");
					e.printStackTrace();
					System.exit(1);
				}
			}
			// Properties lesen
			String[] props = parts[3].split(",");
			for (int i = 0; i < props.length; i++) {
				props[i] = props[i].trim().toUpperCase();
			}
			bp.props = props;
			// in Map speichern
			infos.put(id, bp);
			line++;
		}

		// reading external data

		/*
		 * try { Map<IDPair, BProps> temp = new HashMap<>(); lines = null;
		 * 
		 * lines = Files.readAllLines(extData);
		 * 
		 * line = 1; for (String l : lines) { String[] parts = l.split(":"); if
		 * (parts.length != 5) throw new
		 * IOException("BLOCK.txt ist fehlerhaft in Zeile " + line);
		 * 
		 * // IDs lesen int modID = (-Integer.parseInt(parts[0])) - 1; if (modID
		 * >= 0) throw new IOException("BLOCK.txt ist fehlerhaft in Zeile " +
		 * line);
		 * 
		 * int blockID = Integer.parseInt(parts[1]); if (blockID > 0) throw new
		 * IOException("BLOCK.txt ist fehlerhaft in Zeile " + line);
		 * 
		 * BProps bp = new BProps(); // Namen laden bp.name = parts[2].trim();
		 * // Texturen laden String[] txts = parts[3].split(","); bp.img = new
		 * BufferedImage[txts.length]; for (int i = 0; i < txts.length; i++)
		 * bp.img[i] = ImageIO.read(Paths.get(extTextures.toString(),
		 * txts[i].trim()).toFile());
		 * 
		 * // Properties lesen String[] props = parts[4].split(","); for (int i
		 * = 0; i < props.length; i++) { props[i] =
		 * props[i].trim().toUpperCase(); } // in Map speichern temp.put(new
		 * IDPair(modID, blockID), bp); line++; } Map<Integer, Integer> maxID =
		 * new TreeMap<>(); // finds the greatest // ID for each mod for (IDPair
		 * idp : temp.keySet()) { if (maxID.containsKey(idp.modID))
		 * maxID.put(idp.modID, Math.max(maxID.get(idp.modID), idp.bID)); else
		 * maxID.put(idp.modID, idp.bID); } int number = 0; int first = -1; //
		 * finding the starting ID for each mod for (Integer i : maxID.values())
		 * { if (first == -1) first = i; i -= first; i += number; number = i; }
		 * // linking the IDPairs to normal, internal IDs for (IDPair idp :
		 * temp.keySet()) { idLinking.put(idp, -(maxID.get(idp.modID) + idp.bID
		 * + 1)); } // putting extInfo together for (Entry<IDPair, Integer> e :
		 * idLinking.entrySet()) { extInfos.put(-e.getValue(),
		 * temp.get(e.getKey())); }
		 * 
		 * } catch (IOException e) { JOptionPane.showMessageDialog(null,
		 * e.toString(), "ModdingFehler", JOptionPane.WARNING_MESSAGE); }
		 */
	}

	public void setProps(Block b) {
		BProps props;
		if (b.getID() >= 0)
			props = infos.get(b.getID());
		else
			props = extInfos.get(b.getID());
		if (props != null) {
			if (props.img.length > 0)
				b.setTexture(props.img[rdm.nextInt(props.img.length)]);
			b.setName(props.name);
			if (props.props != null) {
				for (String prop : props.props) {

					if (prop.equals("SOLID")) {
						b.addProperty(new Solid(b));
						continue;
					} else if (prop.contains("SOLID_SLOPE")) {
						String orient = prop.substring(11, 13);
						b.addProperty(new SolidSlope(b, Integer.parseInt(orient) % 4));
						continue;
					} else if (prop.equals("DAMAGING")) {
						b.addProperty(new Damaging(b));
					} else if (prop.equals(""))
						continue;
					System.out.println("Unknown Property found at ID " + b.getID());
					System.exit(1);
				}
			} else
				System.out.println("null");
		} else {
			b.setName("null");
		}
	}

	public void setProps(Block b, int blockID) {
		if (b.getID() >= 0) {
			setProps(b);
			return;
		}
		b.setID(idLinking.get(new IDPair(b.getID(), blockID)));
		setProps(b);
	}

	public int getBlockCount() {
		return infos.size() + extInfos.size();
	}

	public Set<Integer> getBlockIDs() {
		Set<Integer> ret = new HashSet<>((infos.size() + extInfos.size()) / 10);
		ret.addAll(infos.keySet());
		ret.addAll(extInfos.keySet());
		return ret;
	}

	private class IDPair implements Comparable<IDPair> {
		int modID;
		int bID;

		IDPair(int mod_id, int block_id) {
			modID = mod_id;
			bID = block_id;
		}

		public boolean equals(Object o) {
			if (o instanceof IDPair) {
				IDPair temp = (IDPair) o;
				if (modID == temp.modID)
					if (bID == temp.bID)
						return true;
			}
			return false;
		}

		@Override
		public int hashCode() {
			return bID + modID;
		}

		@Override
		public int compareTo(IDPair comp) {
			if (modID > comp.modID)
				return 1;
			if (modID < comp.modID)
				return -1;
			if (bID > comp.bID)
				return 1;
			if (bID < comp.bID)
				return -1;
			return 0;
		}
	}

	private class BProps {
		String[] props = new String[0];
		BufferedImage[] img = new BufferedImage[0];
		String name;
	}
}
