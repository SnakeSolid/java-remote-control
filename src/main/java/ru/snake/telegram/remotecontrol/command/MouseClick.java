package ru.snake.telegram.remotecontrol.command;

import java.util.function.Consumer;

import ru.snake.telegram.remotecontrol.Controller;

public class MouseClick implements Command {

	private final Button button;

	private final int count;

	public MouseClick(final Button button, final int count) {
		this.button = button;
		this.count = count;
	}

	@Override
	public void execute(final Controller controller, final Consumer<String> result) {
		int identifier = button.asIdentifier();

		for (int index = 0; index < count; index += 1) {
			controller.mousePress(identifier);
			controller.mouseRelease(identifier);
		}
	}

	@Override
	public String toString() {
		return "MouseClick [button=" + button + ", count=" + count + "]";
	}

}
