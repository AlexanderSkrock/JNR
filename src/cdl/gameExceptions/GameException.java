package cdl.gameExceptions;

public abstract class GameException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5370826512547630420L;

	public GameException(String info) {
		super(info);
	}

	public GameException() {
		super();
	}

}
