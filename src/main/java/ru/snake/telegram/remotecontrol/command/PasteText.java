package ru.snake.telegram.remotecontrol.command;

import ru.snake.telegram.remotecontrol.Controller;

public class PasteText implements Command {

	private final String text;

	public PasteText(final String text) {
		this.text = text;
	}

	@Override
	public void execute(Controller controller) {
		controller.setClipboard(text);
	}

	@Override
	public String toString() {
		return "PasteText [text=" + text + "]";
	}

}
