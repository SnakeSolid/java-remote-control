package ru.snake.telegram.remotecontrol.command;

import java.util.List;

import ru.snake.telegram.remotecontrol.Controller;

public interface Command {

	public void execute(final Controller controller);

	public static Command moveAbsolute(final int x, final int y) {
		if (x < 0 || x > 100) {
			throw new IllegalArgumentException(String.format("X must be in range 0..100, but %d found.", x));
		} else if (y < 0 || y > 100) {
			throw new IllegalArgumentException(String.format("Y must be in range 0..100, but %d found.", y));
		}

		return new MoveAbsolute(x, y);
	}

	public static Command moveRelative(final int x, final int y) {
		return new MoveRelative(x, y);
	}

	public static Command mouseClick(final Button button, final int count) {
		return new MouseClick(button, count);
	}

	public static Command pressKeys(List<Key> list) {
		Key[] keys = new Key[list.size()];
		list.toArray(keys);

		return new PressKeys(keys);
	}

	public static Command pasteText(String text) {
		return new SetBuffer(text);
	}

}
