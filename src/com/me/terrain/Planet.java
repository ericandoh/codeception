package com.me.terrain;

import java.util.ArrayList;


public class Planet {
	
	//public static final int TOTAL_WIDTH_SQUARE = GridPanel.TOTAL_WIDTH_SQUARE;
	//public static final int TOTAL_HEIGHT_SQUARE = GridPanel.TOTAL_HEIGHT_SQUARE;
	
	private static int MIN_RADIUS = 100;
	private static int ADD_RADIUS = 100;
	private static int MAX_PLANETS = 4;
	private static int MIX_DIST = 3;				//When the difference of dist to biome point is less than this the point can belong to either biome
	private static int CHANGE_DIST = 1;				//When the difference of dist to biome point is less than this the point can belong to either
	
	private static int BIOME_SIZE = 1000;			//
	
	private int centerX, centerY, radius;
	protected ArrayList <BiomePoint> myBiomes;
	//protected GridPanel myMap;
	
	public Planet (ArrayList <Planet> currentPlanets, int w, int h) {
		System.out.println("Planet making...");
		//myMap = m;
		int i = 0;
		boolean isNotDone = true;
		boolean doesConflict;
		while (i<MAX_PLANETS && isNotDone) {
			radius = (int)((double)(ADD_RADIUS)*Math.random()+(double)MIN_RADIUS);
			centerX = (int)((double)((w-2*radius-2)*Math.random())+radius+1);
			centerY = (int)((double)((h-2*radius-2)*Math.random())+radius+1);
			if (currentPlanets.size()==0) {
				isNotDone = false;
				System.out.println("Planet at:"+(centerX)+"/"+(centerY));
			}
			else {
				doesConflict = false;
				for (int looper=0; looper < currentPlanets.size(); looper++) {
					if (conflictsWith(currentPlanets.get(looper))) {
						doesConflict = true;
					}
				}
				if (!doesConflict) {
					isNotDone = false;
					System.out.println("Planet at:"+(centerX)+"/"+(centerY));
				}
				else {
					System.out.println("Did conflict, remaking...");
				}
			}
			i++;
		}
		if (isNotDone) {
			centerX = -1;
			centerY = -1;
			radius = -1;
			System.out.println("Dam, its too filled up");
		}
		
		//make biomal points
		myBiomes = new ArrayList<BiomePoint>();
		//int overallType = (int)(Math.random()*1.0);
		int overallType = 0;
		int numBiome = (int)(Math.pow((double)radius, 2)/BIOME_SIZE + Math.random());
		int calcX, calcY;
		BiomePoint addPoint;
		BiomePointHandler handler = new BiomePointHandler();
		for (int looper2 = centerX - radius; looper2 <= centerX; looper2 += radius) {
			for (int looper3 = centerY - radius; looper3 <= centerY; looper3 += radius) {
				for (int looper4 = 0; looper4 < numBiome; looper4++) {
					calcX = looper2 + (int)(radius * Math.random());
					calcY = looper3 + (int)(radius * Math.random());
					addPoint = handler.returnBiomePoint(calcX, calcY, overallType);
					myBiomes.add(addPoint);
				}
			}
		}
	}
	public boolean conflictsWith(Planet other) {
		return Math.sqrt(Math.pow((double)(centerX-other.getX()), 2)
				+Math.pow((double)(centerY-other.getY()), 2))<=(double)(radius+other.getRadius());
	}
	public int getX() {
		return centerX;
	}
	public int getY() {
		return centerY;
	}
	public int getRadius() {
		return radius;
	}
	public void addPlanet(Square[] squares, Square[][] temp) {
		if (centerX == -1 || centerY == -1 || radius == -1)
			return;
		Square unit;
		double distance;
		for (int xloop = centerX-radius; xloop <= centerX+radius; xloop++){		//bounds = size of map file
			for (int yloop = centerY-radius; yloop <= centerY+radius; yloop++) {
				distance = Math.sqrt(Math.pow((double)(xloop-centerX), 2)+Math.pow((double)(yloop-centerY), 2));
				if (distance<radius) {
					unit = getNearestPoint(xloop, yloop).getSquare(squares);
					temp[xloop][yloop] = unit;
				}
			}
		}
	}
	public BiomePoint getNearestPoint (int x, int y) {
		BiomePoint closestBiomePoint = null;
		BiomePoint secondClosestBiomePoint = null;
		double nearestDist = 2*radius;
		double secondNearestDist = 2*radius + 1;
		int biomeX, biomeY;
		double dist;
		for (BiomePoint biome: myBiomes) {
			biomeX = biome.getX();
			biomeY = biome.getY();
			dist = Math.sqrt(Math.pow((double)(x-biomeX), 2)
					+Math.pow((double)(y-biomeY), 2));
			if (dist < nearestDist) {
				secondNearestDist = nearestDist;
				secondClosestBiomePoint = closestBiomePoint;
				nearestDist = dist;
				closestBiomePoint = biome;
			}
			else if (dist < secondNearestDist) {
				secondNearestDist = dist;
				secondClosestBiomePoint = biome;
			}
		}
		if (secondNearestDist - nearestDist <= CHANGE_DIST && Math.random() < 0.5) {
			return secondClosestBiomePoint;
		}
		else if (secondNearestDist - nearestDist <= MIX_DIST && 
				closestBiomePoint.canMix() && secondClosestBiomePoint.canMix() && Math.random() < 0.5) {
			return secondClosestBiomePoint;
		}
		return closestBiomePoint;
	}
}
