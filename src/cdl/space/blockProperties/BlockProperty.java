package cdl.space.blockProperties;

import java.io.BufferedWriter;

import cdl.movementManager.MovementListener;
import cdl.space.Block;
import cdl.space.Chunk;

public abstract class BlockProperty implements MovementListener, Comparable<BlockProperty> {

	protected Block ref;

	public static final int DAMAGING = 0, SOLID = 1, SOLID_SLOPE = 2;

	protected BlockProperty(Block b) {
		ref = b;
		try {
			getChunk().addMovementListener(this);
		} catch (NullPointerException e) {
		}
	}

	public Chunk getChunk() {
		return ref.getChunk();
	}

	@Override
	public int compareTo(BlockProperty bp) {
		return getPropID() - bp.getPropID();
	}

	public void destroy() {
		try {
			ref.getChunk().removeMovementListener(this);
		} catch (NullPointerException e) {
		}
	}

	public abstract int getPropID();

	public String[] build(String[] strings) {
		return strings;
	}

	public BufferedWriter save(BufferedWriter bw) {
		return bw;
	}
}
