package net.alexhicks.apcs.game.server;

import com.badlogic.gdx.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class GameClient extends Thread {

	private Socket socket;
	private PrintWriter writer;
	private boolean shouldEnd = false;
	private boolean isInactive = false;

	public GameClient(Socket socket) {
		this.socket = socket;
		this.writer = new PrintWriter(socket.getOutputStream(), true);
	}

	/**
	 * Checks for inactivity/AFK
	 *
	 * @return Whether the client is inactive
	 */
	public boolean isActive() {
		return isInactive;
	}

	public void send(String data) {
		if (writer != null) {
			writer.println(data);
		}
	}
	
	/*
	c `x` `y`
	s `score`
	*/
	private void handle(String data) {
		
	}

	@Override
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line;
			while ((line = in.readLine()) != null && !shouldEnd) {
				handle(line);
			}
			writer.close();
			in.close();
			socket.dispose();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void end() {
		this.shouldEnd = true;
	}
}
