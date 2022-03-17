package game2022;

public class Player {
	private int id;
	private String name;
	private int xpos;
	private int ypos;
	private int points;
	private String direction;

	public Player(int id, String name, int xpos, int ypos, String direction) {
		this.id = id;
		this.name = name;
		this.xpos = xpos;
		this.ypos = ypos;
		this.direction = direction;
		this.points = 0;
	}

	public String toString() {
		String txt = "";
		txt += "--- Player ---";
		txt += "Name: " + this.name;
		txt += "Points: " + this.points;
		txt += "PosX: " + this.xpos;
		txt += "PosY: " + this.ypos;
		return txt;
	}

	public int getId() {
		return this.id;
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

	public void addPoints(int p) {
		points += p;
	}

}
