package net.alexhicks.apcs.game;

public enum Textures {

	DOT("dot.png"),
	OPENSANS("OpenSans.ttf"),
	PLAYER("player.png");

	private String file;

	Textures(String file) {
		this.file = file;
	}

	public String getLocation() {
		return this.file;
	}
}
