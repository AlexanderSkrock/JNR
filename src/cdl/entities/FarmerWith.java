package cdl.entities;

import java.io.BufferedWriter;
import java.io.IOException;

import cdl.data.EntityInfo;
import cdl.movementManager.HitBox;
import cdl.space.World;

public class FarmerWith extends Farmer {

	public FarmerWith(World root, Position p) {
		super(root, p);
		this.reset(new HitBox(p, 1.6, 1.6));
		super.walking = EntityInfo.EI.getFarmerWithSlow();
		super.running = EntityInfo.EI.getFarmerWithFast();
	}

	@Override
	public void tick() {
		this.wl.lock();
		try {
			if (root.getPlayer() != null) {
				if (!angry) {
					if (this.getPosition().distance(root.getPlayer().getPosition()) < 7.5) {
						angry = true;
					}
				}
				super.tick();
			}
		} finally {
			wl.unlock();
		}
	}

	@Override
	public void save(BufferedWriter bw) throws IOException {
		bw.write("FarmerWithFork ");
		bw.write(Double.toString(getLeftBound() + getWidth() / 2));
		bw.write(";");
		bw.write(Double.toString(getTopBound() + getHeight() / 2));
		bw.newLine();
	}
	
	@Override
	public String toString(){
		return "Farmer with Fork";
	}
}
