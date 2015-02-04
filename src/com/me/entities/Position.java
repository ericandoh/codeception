package com.me.entities;

public class Position {
	public double xpos, ypos;
	public double direction;
	public double zpos;
	public Position(double x, double y) {
		xpos = x;
		ypos = y;
		zpos = 0;
		direction = 0;
	}
	public Position(double x, double y, double d) {
		xpos = x;
		ypos = y;
		zpos = 0;
		direction = d;
	}
	public Position(double x, double y, double z, double d) {
		xpos = x;
		ypos = y;
		zpos = z;
		direction = d;
	}
}
