package com.me.panels;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.me.entities.Player;
import com.me.entities.Vehicle;
import com.me.render.GameRenderer;
import com.me.terrain.GameGrid;


public class RadarPanel extends Actor {
	private Player myPlayer;
	private GameGrid myGrid;
	private int totalWidth, totalHeight;
	private ShapeRenderer shapes;
	private PerspectiveCamera camera;				//used for getting position/direction
	private GameRenderer renderer;
	//private static final BufferedImage frame = ImageProcesser.getImage("frame1");
	public RadarPanel (Player p, GameGrid g, PerspectiveCamera cam, GameRenderer render) {
		super();
		myPlayer = p;
		myGrid = g;
		camera = cam;
		//totalWidth = w;
		//totalHeight = h;
		shapes = new ShapeRenderer();
		renderer = render;
		//setBackground(Color.black);
	}
	public void draw() {
		float mult;
		if (renderer.getFlatMode()) {
			mult = -1;
		}
		else {
			mult = 1;
		}
		totalHeight = (int)(camera.position.y * 3);
		totalWidth = (int)(camera.position.y * 3);
		//g.drawImage(frame, 0, 0, getWidth(), getHeight(), null);
		//g.setColor(Color.green);
		
		//(center of radar)
		
		//float playerX = (float) myPlayer.getXPos();
		//float playerY = (float) myPlayer.getYPos();
		
		float playerX = camera.position.x;
		float playerY = camera.position.z;
		
		double degree = Math.atan2(camera.direction.x, camera.direction.z) - Math.PI / 2;
		
		float scaleX, scaleY;
		double mag;
		double deg;
		
		shapes.begin(ShapeType.Filled);
		shapes.setColor(Color.WHITE);
		//shapes.circle((int)(myPlayer.getXPos() * getWidth() / totalWidth), (int)(myPlayer.getYPos() * getHeight() / totalHeight), 5);
		shapes.circle(getWidth() / 2, getHeight() / 2, 5);
		//for (Vehicle p: myGrid.getVehicle(playerX, playerY, totalWidth + totalHeight, null, false)) {
		for (Vehicle p : myGrid.getVehicleIgnoreHeight(playerX, playerY, totalWidth+totalHeight)) {
			scaleX = (float)((p.getXPos() - playerX) * getWidth() / totalWidth);
			scaleY = (float)((p.getYPos() - playerY) * getHeight() / totalHeight);
			deg = Math.atan2(scaleX, scaleY);
			deg -= degree;
			mag = Math.sqrt(Math.pow(scaleX, 2) + Math.pow(scaleY, 2));
			scaleX = (float) (mult*mag * Math.cos(deg)) + getWidth() / 2;
			scaleY = (float) (mult*mag * Math.sin(deg)) + getHeight() / 2;
			if (scaleX >= 0 && scaleX < getWidth() && scaleY >= 0 && scaleY < getHeight()) {
				if (p.getPlayer() == myPlayer) {
					shapes.setColor(Color.GREEN);
				}
				else {
					shapes.setColor(Color.RED);
				}
				shapes.circle(scaleX, scaleY, 3);
			}
		}
		shapes.end();
		//g.fillOval((int)(getWidth() * (myPlayer.getXPos()+GridPanel.MAX_SCREEN_WIDTH_SQUARE/2)/myGrid.getSquareWidth() / GridPanel.TOTAL_WIDTH_SQUARE),
		//		(int)(getHeight() * (myPlayer.getYPos()+GridPanel.MAX_SCREEN_HEIGHT_SQUARE)/myGrid.getSquareHeight() / GridPanel.TOTAL_HEIGHT_SQUARE), 5, 5);
	}
}
