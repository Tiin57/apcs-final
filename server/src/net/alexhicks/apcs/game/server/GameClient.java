package net.alexhicks.apcs.game.server;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import net.alexhicks.apcs.game.AccelRectangle;

public class GameClient extends Thread {

	private Socket socket;
	private PrintWriter writer;
	private boolean shouldEnd = false;
	private boolean isActive = true;
	private AccelRectangle player = new AccelRectangle();

	public GameClient(Socket socket) {
		this.socket = socket;
		this.writer = new PrintWriter(socket.getOutputStream(), true);
		this.player.x = MathUtils.random(0f, 800f);
		this.player.y = MathUtils.random(0f, 480f);
	}

	/**
	 * Checks for inactivity/AFK
	 *
	 * @return Whether the client is inactive
	 */
	public boolean isActive() {
		return isActive;
	}

	public void send(String data) {
		if (writer != null) {
			writer.println(data);
		}
	}

	/**
	 * c `x` `y`
	 * l
	 * @param data The line to handle
	 */
	private void handle(String data) {
		String[] tokens = data.split(" ");
		if (tokens.length == 0 || tokens[0].length() == 0) {
			return;
		}
		char c = tokens[0].charAt(0);
		if (c == 'c' && tokens.length == 3) {
			try {
				float x = Float.parseFloat(tokens[1]);
				float y = Float.parseFloat(tokens[2]);
				if (isCheating(x, this.player.x) || isCheating(y, this.player.y)) {
					return;
				}
				this.player.x = x;
				this.player.y = y;
			} catch (NumberFormatException ex) {
				ex.printStackTrace();
			}
		} else if (c == 'l') {
			this.end();
		}
	}

	private boolean isCheating(float a, float b) {
		return Math.abs(a - b) >= 4;
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
