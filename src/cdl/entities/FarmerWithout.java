package cdl.entities;

import java.io.BufferedWriter;
import java.io.IOException;

import cdl.data.EntityInfo;
import cdl.movementManager.HitBox;
import cdl.space.World;

public class FarmerWithout extends Farmer {

	public FarmerWithout(World root, Position p, boolean fast) {
		super(root, p);
		this.reset(new HitBox(p, 0.8, 1.6));
		super.walking = EntityInfo.EI.getFarmerWithoutSlow();
		super.running = EntityInfo.EI.getFarmerWithoutFast();
		this.angry = fast;
	}

	@Override
	public void save(BufferedWriter bw) throws IOException {
		if (angry)
			bw.write("FarmerNoFork ");
		else
			bw.write("FarmerNoForkFast ");
		bw.write(Double.toString(getLeftBound() + getWidth() / 2));
		bw.write(";");
		bw.write(Double.toString(getTopBound() + getHeight() / 2));
		bw.newLine();
	}
	
	@Override
	public String toString(){
		if(angry){
			return super.toString();
		}
		return "fast Farmer";
	}
}