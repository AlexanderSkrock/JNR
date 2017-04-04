package cdl.entities;

import java.awt.Graphics;

import cdl.space.World;

public class Destroyer extends Entity {

	public Destroyer(World root, Position p) {
		super(root, p);
		for (Entity e : root.getEntities()) {
			if (this.isIntersecting(e)) {
				System.out.println("destroy");
				e.destroy();		
			}
		}
	}

	@Override
	public void draw(Graphics g) {
		return;
	}
}
