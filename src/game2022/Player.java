package game2022;

public class Player {
	private int id;
	private String name;
	private int xpos;
	private int ypos;
	private int point;
	private String direction;

	public Player(int id, String name, int xpos, int ypos, String direction) {
		this.id = id;
		this.name = name;
		this.xpos = xpos;
		this.ypos = ypos;
		this.direction = direction;
		this.point = 0;
	}

	public String toString() {
		return name + ":   " + point;
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
		point += p;
	}

}
