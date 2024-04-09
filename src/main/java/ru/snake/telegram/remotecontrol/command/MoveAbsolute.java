package ru.snake.telegram.remotecontrol.command;

import java.awt.Rectangle;

import ru.snake.telegram.remotecontrol.Controller;

public class MoveAbsolute implements Command {

	private final int x;

	private final int y;

	public MoveAbsolute(final int x, final int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void execute(Controller controller) {
		Rectangle screenRect = controller.getScreenRect();
		int cursorX = x * screenRect.width / 100;
		int cursorY = y * screenRect.height / 100;

		controller.setCursorPosition(cursorX, cursorY);
	}

	@Override
	public String toString() {
		return "MoveAbsolute [x=" + x + ", y=" + y + "]";
	}

}
