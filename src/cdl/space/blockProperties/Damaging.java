package cdl.space.blockProperties;

import cdl.movementManager.MovementEvent;
import cdl.space.Block;

public class Damaging extends BlockProperty {

	private static final int PROP_ID = 0;

	public Damaging(Block b) {
		super(b);
	}

	@Override
	public void movementOccured(MovementEvent evt) {
		if (evt.getSource().isIntersecting(ref))
			evt.getSource().damage();
	}

	@Override
	public int getPropID() {
		return PROP_ID;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Damaging;
	}
}
