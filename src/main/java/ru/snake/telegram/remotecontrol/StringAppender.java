package ru.snake.telegram.remotecontrol;

public class StringAppender {

	private final StringBuilder builder;

	public StringAppender() {
		this.builder = new StringBuilder();
	}

	public boolean isEmpty() {
		return builder.isEmpty();
	}

	public void appendLine(final String line) {
		builder.append(line);
		builder.append('\n');
	}

	@Override
	public String toString() {
		return builder.toString();
	}

}
