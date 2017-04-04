package cdl.movementManager;

import java.util.EventListener;

import cdl.space.Chunk;

public interface MovementListener extends EventListener {
	public void movementOccured(MovementEvent evt);
	public Chunk getChunk();
}
