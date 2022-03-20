package client.model;

public class Player {
	private int playerId;
	private String playerName;
	private String color;
	private int points;
	private int posX;
	private int posY;
	private String direction;
	private boolean ready;

	public Player(int playerId, String playerName, int posX, int posY, String direction, String color) {
		this.playerId = playerId;
		this.playerName = playerName;
		this.posX = posX;
		this.posY = posY;
		this.direction = direction;
		this.color = color;
		this.ready = false;
		this.points = 0;
	}

	public String toString() {
		String txt = "";
		txt += "--- Player ---\n";
		txt += "Id: " + this.playerId + "\n";
		txt += "Name: " + this.playerName + "\n";
		txt += "Points: " + this.points + "\n";
		txt += "PosX: " + this.posX + "\n";
		txt += "PosY: " + this.posY + "\n";
		txt += "Direction: " + this.direction + "\n";
		return txt;
	}

	public int getPlayerId() {
		return this.playerId;
	}

	public String getPlayerName() {
		return this.playerName;
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

	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
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
