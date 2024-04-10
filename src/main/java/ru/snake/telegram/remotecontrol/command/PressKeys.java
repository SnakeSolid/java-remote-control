package ru.snake.telegram.remotecontrol.command;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

import ru.snake.telegram.remotecontrol.Controller;

public class PressKeys implements Command {

	private final Key[] keys;

	public PressKeys(Key[] keys) {
		this.keys = keys;
	}

	@Override
	public void execute(final Controller controller, final Consumer<String> result) {
		Set<Integer> modifiers = new LinkedHashSet<>();

		for (Key key : keys) {
			int event = key.getEvent();
			boolean modifier = key.isModifier();

			controller.keyPress(event);

			if (modifier) {
				modifiers.add(event);
			} else {
				controller.keyRelease(event);
			}
		}

		for (int event : modifiers) {
			controller.keyRelease(event);
		}
	}

	@Override
	public String toString() {
		return "PressKeys [keys=" + Arrays.toString(keys) + "]";
	}

}
