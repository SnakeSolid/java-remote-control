package ru.snake.telegram.remotecontrol.command;

import java.util.function.Consumer;

import ru.snake.telegram.remotecontrol.Controller;

public class SetBuffer implements Command {

	private final String text;

	public SetBuffer(final String text) {
		this.text = text;
	}

	@Override
	public void execute(final Controller controller, final Consumer<String> result) {
		controller.setClipboard(text);
	}

	@Override
	public String toString() {
		return "SetBuffer [text=" + text + "]";
	}

}
