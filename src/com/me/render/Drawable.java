package com.me.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.me.entities.Position;


public abstract class Drawable {
	protected Position pos;
	//protected TextureRegion region;
	//protected int length;
	protected int id;
	protected ModelInstance instance;
	//protected float yoffset;
	public Drawable(Position p, int i) {
		id = i;
		pos = p;
		//length = 1;
	}
	public void paintSprite(GameRenderer r, SpriteBatch sBatch, Lights lights) {
		//do nothing
	}
	public void paintModel(GameRenderer r, ModelBatch mBatch, Lights lights) {
		if (instance == null) {
			if (!remakeImage(r))
				return;
		}
		instance.transform.setToTranslation((float)pos.xpos, (float)pos.zpos, (float)pos.ypos);
		instance.transform.rotate(0, 1, 0, (float)(-pos.direction * 180 / Math.PI));
		mBatch.render(instance, lights);
	}
	public boolean remakeImage(GameRenderer r) {
		Model m = getModel(r);
		if (m != null) {
			instance = new ModelInstance(m);
			//yoffset = instance.calculateBoundingBox(new BoundingBox()).getDimensions().y / 2;
			return true;
		}
		return false;
	}
	public abstract Model getModel(GameRenderer r);
}