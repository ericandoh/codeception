package com.me.render;

import com.badlogic.gdx.graphics.g3d.Model;
import com.me.entities.Position;

public class ShadowRotatableDrawable extends Drawable{
	public ShadowRotatableDrawable(Position p, int i) {
		super(p, i);
	}
	@Override
	public Model getModel(GameRenderer r) {
		return r.getShadowModel(id);
	}
}