package ru.snake.telegram.remotecontrol.command;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.function.Consumer;

import ru.snake.telegram.remotecontrol.Controller;

public class MoveRelative implements Command {

	private final int x;

	private final int y;

	public MoveRelative(final int x, final int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void execute(final Controller controller, final Consumer<String> result) {
		Rectangle screenRect = controller.getScreenRect();
		Point position = controller.getCursorPosition();
		int x = clip(position.x + this.x, 0, screenRect.width);
		int y = clip(position.y + this.y, 0, screenRect.height);

		controller.setCursorPosition(x, y);
	}

	private static int clip(int x, int min, int max) {
		if (x < min) {
			return min;
		} else if (x > max) {
			return max;
		} else {
			return x;
		}
	}

	@Override
	public String toString() {
		return "MoveRelative [x=" + x + ", y=" + y + "]";
	}

}
