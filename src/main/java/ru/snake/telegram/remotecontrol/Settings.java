package ru.snake.telegram.remotecontrol;

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

	public Settings(final String botToken, final Set<Long> allowUsers) {
		this.botToken = botToken;
		this.allowUsers = allowUsers;
	}

	public String getBotToken() {
		return botToken;
	}

	public Set<Long> getAllowUsers() {
		return allowUsers;
	}

	@Override
	public String toString() {
		return "Settings [botToken=" + botToken + ", allowUsers=" + allowUsers + "]";
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
		options.addOption(tokenOption);
		options.addOption(allowOption);

		CommandLineParser parser = new DefaultParser();
		CommandLine commandLine;

		try {
			commandLine = parser.parse(options, args);
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

		return new Settings(botToken, allowUsers);
	}

}
