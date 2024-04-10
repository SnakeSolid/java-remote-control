package ru.snake.telegram.remotecontrol.command;

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.function.Consumer;

import ru.snake.telegram.remotecontrol.Controller;

public class ShellExecute implements Command {

	private static final String SHELL;

	static {
		String shell = System.getenv("SHELL");

		if (shell == null) {
			shell = "cmd.exe";
		}

		SHELL = shell;
	}

	private final String command;

	public ShellExecute(final String command) {
		this.command = command;
	}

	@Override
	public void execute(Controller controller, Consumer<String> output) {
		try {
			Process process = new ProcessBuilder().command(SHELL)
				.redirectInput(Redirect.PIPE)
				.redirectOutput(Redirect.PIPE)
				.redirectError(Redirect.PIPE)
				.start();
			process.getOutputStream().write(command.getBytes());
			process.getOutputStream().close();
			process.waitFor();

			output.accept(new String(process.getErrorStream().readAllBytes()));
			output.accept(new String(process.getInputStream().readAllBytes()));
		} catch (IOException | InterruptedException e) {
			output.accept(e.getMessage());
		}
	}

	@Override
	public String toString() {
		return "ShellExecute [command=" + command + "]";
	}

}
