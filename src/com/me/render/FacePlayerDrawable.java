package com.me.render;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.me.entities.Position;

public class FacePlayerDrawable extends Drawable {
	protected float farDst;
	public FacePlayerDrawable(Position p, int i, float farDst) {
		super(p, i);
		this.farDst = farDst + 0.5f;
	}
	public FacePlayerDrawable(Position p, int i) {
		this(p, i, 0);
	}
	public void paintModel(GameRenderer r, ModelBatch mBatch, Lights lights) {
		if (instance == null) {
			if (!remakeImage(r))
				return;
		}
		float xdist = (float) (r.getCamera().position.x - pos.xpos);
		float ydist = (float) (r.getCamera().position.z - pos.ypos);
		//float xdist = r.getCamera().direction.x;
		//float ydist = r.getCamera().direction.z;
		double dir = Math.atan2(xdist, ydist);
		instance.transform.setToTranslation((float)pos.xpos, (float)(pos.zpos+farDst), (float)pos.ypos);
		instance.transform.rotate(0, 1, 0, (float)(dir * 180 / Math.PI));
		mBatch.render(instance, lights);
	}
	@Override
	public Model getModel(GameRenderer r) {
		return r.getFaceModel(id);
	}
}
