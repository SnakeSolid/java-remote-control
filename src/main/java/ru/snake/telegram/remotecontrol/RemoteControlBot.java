package ru.snake.telegram.remotecontrol;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.jparsec.error.ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import ru.snake.telegram.remotecontrol.command.Command;
import ru.snake.telegram.remotecontrol.command.CommandParser;
import ru.snake.telegram.remotecontrol.command.Key;
import ru.snake.telegram.remotecontrol.command.PressKeys;

public class RemoteControlBot implements LongPollingSingleThreadUpdateConsumer {

	private static final Logger LOG = LoggerFactory.getLogger(RemoteControlBot.class);

	private static final Command WAKEUP_COMMAND = new PressKeys(new Key[] { Key.from("ctrl") });

	private final TelegramClient telegramClient;

	private final Set<Long> whiteList;

	private final Controller controller;

	public RemoteControlBot(final String botToken, final Set<Long> whiteList, final Controller controller) {
		this.telegramClient = new OkHttpTelegramClient(botToken);
		this.whiteList = whiteList;
		this.controller = controller;
	}

	@Override
	public void consume(Update update) {
		if (update.hasMessage()) {
			Message message = update.getMessage();
			long userId = message.getFrom().getId();
			long chatId = message.getChatId();

			if (!whiteList.contains(userId)) {
				sendMessage(chatId, String.format("Access denied, your ID %d.", userId));
			} else if (update.getMessage().hasText()) {
				String text = message.getText();

				switch (text) {
				case "/start":
					break;

				case "/screenshot":
					screenshot(chatId);
					break;

				case "/wakeup":
					wakeup(chatId);
					break;

				case "/scripts":
					break;

				case "/clipboard":
					clipboard(chatId);
					break;

				default:
					execute(chatId, text);
					break;
				}
			}
		}
	}

	private void execute(long chatId, String text) {
		List<Command> commands;

		try {
			commands = CommandParser.parse(text.toLowerCase());
		} catch (ParserException e) {
			LOG.warn("Failed to parse script.", e);

			sendMessage(chatId, e.getMessage());

			return;
		}

		for (Command command : commands) {
			command.execute(controller);
		}

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			LOG.warn("Sleep failed.", e);
		}

		BufferedImage image = controller.showCursor();
		sendPhoto(chatId, image);
	}

	private void clipboard(long chatId) {
		Content content = controller.clipboardContent();

		if (content.getText() != null) {
			sendMessage(chatId, content.getText());
		} else if (content.getImage() != null) {
			sendPhoto(chatId, content.getImage());
		}
	}

	private void wakeup(long chatId) {
		WAKEUP_COMMAND.execute(controller);

		sendMessage(chatId, "Success.");
	}

	private void screenshot(long chatId) {
		BufferedImage image = controller.takeScreenshot();

		sendPhoto(chatId, image);
	}

	private void sendPhoto(long chatId, BufferedImage image) {
		File path;

		try {
			path = File.createTempFile("image-", null);
		} catch (IOException e) {
			LOG.warn("Failed to send photo.", e);

			return;
		}

		try {
			ImageIO.write(image, "png", path);
		} catch (IOException e) {
			LOG.warn("Failed to send photo.", e);

			return;
		}

		InputFile file = new InputFile(path);
		SendPhoto message = SendPhoto.builder().chatId(chatId).photo(file).build();

		try {
			telegramClient.execute(message);
		} catch (TelegramApiException e) {
			LOG.warn("Failed to send photo.", e);
		} finally {
			path.delete();
		}
	}

	private void sendMessage(long chatId, String text) {
		String escaped = text.replace(".", "\\.");
		SendMessage message = SendMessage.builder().chatId(chatId).parseMode("MarkdownV2").text(escaped).build();

		try {
			telegramClient.execute(message);
		} catch (TelegramApiException e) {
			LOG.warn("Failed to send message.", e);
		}
	}

}
