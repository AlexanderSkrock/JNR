package cdl.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelInfo {

	private Path pData = Paths.get("propSheets", "LEVELS.txt");
	private Path pLevels = Paths.get("levels");

	public static final LevelInfo LI = new LevelInfo();
	
	private Map<Integer, Level> levels = new HashMap<Integer, Level>();

	private LevelInfo() {
		List<String> lines = null;

		try {
			lines = Files.readAllLines(pData);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		int line = 1;
		
		for (String l : lines) {
			String[] parts = l.split(":");
			if (parts.length != 3) {
				System.out.println("LEVELS.txt ist fehlerhaft in Zeile " + line);
				System.exit(1);
			}
			
			int id = Integer.parseInt(parts[0]);
			if (id <= 0) {
				System.out.println("LEVELS.txt ist fehlerhaft in Zeile " + line);
				System.exit(1);
			}
			
			Level level = new Level();
			level.setName(parts[1].trim());
			level.setFile(parts[2].trim() + ".txt");

			levels.put(id, level);
			line++;
		}
	}
	
	public  int getAmount() {
		return levels.size();
	}
	
	public  String getName(int i) {
		return levels.get(i).getName();
	}
	
	public Path getPath(int level) {
		return Paths.get(pLevels.toAbsolutePath().toString(), levels.get(level).getFile());
	}
	
	public static class Level {
		String file;
		String name;
		
		public String getFile() {
			return file;
		}
		
		public void setFile(String file) {
			this.file = file;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
	}
}
