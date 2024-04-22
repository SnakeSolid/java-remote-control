package ru.snake.telegram.remotecontrol.command;

import java.util.List;
import java.util.Optional;

public class Script {

	private final Optional<String> name;

	private final List<Command> commands;

	public Script(final Optional<String> name, final List<Command> commands) {
		this.name = name;
		this.commands = commands;
	}

	public boolean hasName() {
		return name.isPresent();
	}

	public String getName() {
		return name.orElse("");
	}

	public List<Command> getCommands() {
		return commands;
	}

	@Override
	public String toString() {
		return "Script [name=" + name + ", commands=" + commands + "]";
	}

	public static Script from(final Optional<String> name, final List<Command> commands) {
		return new Script(name, commands);
	}

}
