package net.alexhicks.apcs.game.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.Array;

public class GameServer {

	private ServerSocket socket;
	private String ip;
	private int port;
	private Array<GameClient> clients = new Array<GameClient>();

	public GameServer(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public void listen() {
		ServerSocketHints hints = new ServerSocketHints();
		hints.acceptTimeout = 0;
		this.socket = Gdx.net.newServerSocket(Net.Protocol.TCP, port, hints);
		Socket s;
		SocketHints h = new SocketHints();
		while ((s = socket.accept(h)) != null) {
			clients.add(new GameClient(s));
			for (int i = 0; i < clients.size; i++) {
				GameClient c = clients.get(i);
				if (c.isAlive() && !c.isActive()) {
					c.end();
				} else if (!c.isAlive()) {
					clients.removeIndex(i);
				}
			}
		}
	}

	public void broadcast(String data) {
		for (GameClient c : clients) {
			c.send(data);
		}
	}

	public String getIP() {
		return this.ip;
	}

	public int getPort() {
		return this.port;
	}
}
