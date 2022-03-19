package client.model;

public class Player {
	private int id;
	private String name;
	private String color;
	private int points;
	private int xpos;
	private int ypos;
	private String direction;
	private boolean ready;

	public Player(int id, String name, int xpos, int ypos, String direction) {
		this.id = id;
		this.name = name;
		this.color = "white";
		this.xpos = xpos;
		this.ypos = ypos;
		this.direction = direction;
		this.points = 0;
		this.ready = false;
	}

	public String toString() {
		String txt = "";
		txt += "--- Player ---\n";
		txt += "Id: " + this.id + "\n";
		txt += "Name: " + this.name + "\n";
		txt += "Points: " + this.points + "\n";
		txt += "PosX: " + this.xpos + "\n";
		txt += "PosY: " + this.ypos + "\n";
		txt += "Direction: " + this.direction + "\n";
		return txt;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getColor() {
		return color;
	}

	public int getPoints() {
		return this.points;
	}

	public void addPoints(int p) {
		points += p;
	}

	public int getXpos() {
		return xpos;
	}

	public void setXpos(int xpos) {
		this.xpos = xpos;
	}

	public int getYpos() {
		return ypos;
	}

	public void setYpos(int ypos) {
		this.ypos = ypos;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public void setReady(boolean state) {
		this.ready = state;
	}

	public boolean isReady() {
		return this.ready;
	}

}
