package com.catchthing.catchthing.calculate;


public class Point {

	private int x;
	private int y;

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public double getDistance(Point p) {
		return Math.sqrt(
				Math.pow((p.getX() - this.getX()), 2.) +
						Math.pow((p.getY() - this.getY()), 2.));
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
