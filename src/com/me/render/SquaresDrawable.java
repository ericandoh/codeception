package com.me.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.me.entities.Position;
import com.me.terrain.GameGrid;
import com.me.terrain.Square;


public class SquaresDrawable extends Drawable {
	public static final int SQUARE_LENGTH = GameGrid.SQUARE_LENGTH;
	public static final float BOUNDARY = Square.BOUNDARY;
	public static final float POS_OFFSET = 0.5f + BOUNDARY;
	public static final float STRETCH = SQUARE_LENGTH*(1+BOUNDARY*2f);
	
	protected Square[][] squares;
	protected int[][] selects;
	protected TextureRegion[][] texRegions;
	protected ModelInstance[][] modRegions;
	protected boolean centered;
	protected boolean needUpdate;
	protected boolean needUpdateAll;
	protected int needX, needY;
	public SquaresDrawable(Position p, Square i[][]) {
		super(p, 0);
		squares = i;
		centered = false;
		needUpdate = false;
		needUpdateAll = false;
		needX = 0;
		needY = 0;
	}
	public void paintModel(GameRenderer r, ModelBatch mBatch, Lights lights) {
		if (centered) {
			if (modRegions == null) {
				if (!remakeImage(r))
					return;
			}
			if (needUpdateAll) {
				if (!remakeImage(r))
					return;
			}
			if (needUpdate) {
				updateSquareModel(r);
			}
			boolean inRange;
			for (int x = 0; x < squares.length; x++) {
				for (int y = 0; y < squares[0].length; y++) {
					inRange = r.inRangeCamera((float)((pos.xpos+x)*SQUARE_LENGTH), 
							(float)((pos.ypos+y)*SQUARE_LENGTH));
					if (!inRange)
						continue;
					if (modRegions[x][y] == null)
						continue;
					mBatch.render(modRegions[x][y], lights);
				}
			}
		}
	}
	public void paintSprite(GameRenderer r, SpriteBatch sBatch, Lights lights) {
		if (texRegions == null) {
			if (!remakeImage(r))
				return;
		}
		for (int x = 0; x < squares.length; x++) {
			for (int y = 0; y < squares[0].length; y++) {
				id = squares[x][y].getID();
				if (id == 0)
					continue;
				sBatch.draw(texRegions[id][selects[x][y]], (float)(pos.xpos+x - POS_OFFSET)*SQUARE_LENGTH, 
						(float)(pos.ypos+y - POS_OFFSET)*SQUARE_LENGTH, 
						STRETCH, STRETCH);
			}
		}
	}
	public Model getModel(GameRenderer r) {
		return null;
	}
	@Override
	public boolean remakeImage(GameRenderer r) {
		needUpdateAll = false;
		
		texRegions = r.getTexSquares();
		selects = new int[squares.length][squares[0].length];
		for (int x = 0; x < squares.length; x++) {
			for (int y = 0; y < squares[0].length; y++) {
				id = squares[x][y].getID();
				if (id == 0) {
					selects[x][y] = 0;
					continue;
				}
				selects[x][y] = (int) (texRegions[id].length * Math.random());
			}
		}
		
		if (centered) {
			Model m;
			modRegions = new ModelInstance[squares.length][squares[0].length];
			for (int x = 0; x < squares.length; x++) {
				for (int y = 0; y < squares[0].length; y++) {
					id = squares[x][y].getID();
					if (id == 0)
						continue;
					m = r.getModSquares(id, selects[x][y]);
					if (m == null) {
						modRegions[x][y] = null;
						continue;
					}
					modRegions[x][y] = new ModelInstance(m);
					modRegions[x][y].transform.setToTranslation((float)(pos.xpos+x)*SQUARE_LENGTH, 0, 
							(float)(pos.ypos+y)*SQUARE_LENGTH);
				}
			}	
		}
		return true;
	}
	public void setCentered(boolean newCentered) {
		if (newCentered == centered)
			return;
		centered = newCentered;
	}
	public void updateSquare(int x, int y) {
		if (needUpdate)
			needUpdateAll = true;
		needX = x;
		needY = y;
		needUpdate = true;
		selects[x][y] = 0;
	}
	public void updateSquareModel(GameRenderer r) {
		needUpdate = false;
		id = squares[needX][needY].getID();
		if (id == 0)
			return;
		selects[needX][needY] = (int) (texRegions[id].length * Math.random());
		/*batch.draw(regions[id / 5][id % 5], xpos + x, ypos + y, 
				modw/2.0f, modh/2.0f, 
				modw, modh, 1f, 1f, direction, false);*/
		Model m1 = r.getModSquares(id, selects[needX][needY]);
		if (m1 == null) {
			modRegions[needX][needY] = null;
			return;
		}
		modRegions[needX][needY] = new ModelInstance(m1);
		/*modRegions[x][y].transform.setToTranslation((float)(pos.xpos+x - BOUNDARY)*SQUARE_LENGTH, 0, 
				(float)(pos.ypos+y - BOUNDARY)*SQUARE_LENGTH);*/
		modRegions[needX][needY].transform.setToTranslation((float)(pos.xpos+needX)*SQUARE_LENGTH, 0, 
				(float)(pos.ypos+needY)*SQUARE_LENGTH);
	}
}
