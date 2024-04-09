package ru.snake.telegram.remotecontrol.command;

import ru.snake.telegram.remotecontrol.Controller;

public class SetBuffer implements Command {

	private final String text;

	public SetBuffer(final String text) {
		this.text = text;
	}

	@Override
	public void execute(Controller controller) {
		controller.setClipboard(text);
	}

	@Override
	public String toString() {
		return "SetBuffer [text=" + text + "]";
	}

}
