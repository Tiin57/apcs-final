package net.alexhicks.apcs.game.server;

public class Start {

	public static void main(String[] args) {
		GameServer server = new GameServer("0.0.0.0", 8080);
		server.listen();
	}
}
