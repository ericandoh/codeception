package com.me.render;

import com.badlogic.gdx.graphics.g3d.Model;
import com.me.entities.Position;

public class EntityDrawable extends Drawable {
	private boolean destroyed;
	public EntityDrawable(Position p, int i, boolean d) {
		super(p, i);
		destroyed = d;
	}
	public Model getModel(GameRenderer r) {
		return r.getVehicleModel(id, destroyed);
	}
	public void updateDestroyed(boolean d) {
		destroyed = d;
	}
}
