package ru.snake.telegram.remotecontrol;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Settings {

	private final String botToken;

	private final Set<Long> allowUsers;

	private final File scriptsPath;

	public Settings(final String botToken, final Set<Long> allowUsers, final File scriptsPath) {
		this.botToken = botToken;
		this.allowUsers = allowUsers;
		this.scriptsPath = scriptsPath;
	}

	public String getBotToken() {
		return botToken;
	}

	public Set<Long> getAllowUsers() {
		return allowUsers;
	}

	public File getScriptsPath() {
		return scriptsPath;
	}

	@Override
	public String toString() {
		return "Settings [botToken=" + botToken + ", allowUsers=" + allowUsers + ", scriptsPath=" + scriptsPath + "]";
	}

	public static Settings parse(String[] args) {
		Options options = new Options();
		Option tokenOption = Option.builder("t")
			.longOpt("bot-token")
			.desc("Telegram bot access token")
			.required()
			.hasArg()
			.argName("TOKEN")
			.type(String.class)
			.build();
		Option allowOption = Option.builder("a")
			.longOpt("allow-user")
			.desc("Allow access to user")
			.required()
			.hasArg()
			.argName("ID")
			.type(Long.class)
			.build();
		Option scriptsOption = Option.builder("s")
			.longOpt("scripts-path")
			.desc("Path to scripts file")
			.required()
			.hasArg()
			.argName("PATH")
			.type(File.class)
			.build();
		options.addOption(tokenOption);
		options.addOption(allowOption);
		options.addOption(scriptsOption);

		CommandLineParser parser = new DefaultParser();
		CommandLine commandLine;
		File scriptsPath;

		try {
			commandLine = parser.parse(options, args);
			scriptsPath = (File) commandLine.getParsedOptionValue(scriptsOption);
		} catch (ParseException e) {
			System.err.println(e.getMessage());

			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("remotecontrol", options);

			return null;
		}

		String botToken = commandLine.getOptionValue(tokenOption);
		Set<Long> allowUsers = new HashSet<>();

		for (String userId : commandLine.getOptionValues(allowOption)) {
			allowUsers.add(Long.parseLong(userId));
		}

		return new Settings(botToken, allowUsers, scriptsPath);
	}

}
