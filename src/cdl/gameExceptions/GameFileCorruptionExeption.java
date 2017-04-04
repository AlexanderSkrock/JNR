package cdl.gameExceptions;

public class GameFileCorruptionExeption extends GameException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2821725153983760894L;

	public GameFileCorruptionExeption(String info) {
		super(info);
	}

	public GameFileCorruptionExeption() {
		super();
	}
}
