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
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup.InlineKeyboardMarkupBuilder;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import ru.snake.telegram.remotecontrol.command.Command;
import ru.snake.telegram.remotecontrol.command.CommandParser;
import ru.snake.telegram.remotecontrol.command.Key;
import ru.snake.telegram.remotecontrol.command.PressKeys;
import ru.snake.telegram.remotecontrol.script.Scripts;

public class RemoteControlBot implements LongPollingSingleThreadUpdateConsumer {

	private static final Logger LOG = LoggerFactory.getLogger(RemoteControlBot.class);

	private static final Command WAKEUP_COMMAND = new PressKeys(new Key[] { Key.from("ctrl") });

	private final TelegramClient telegramClient;

	private final Set<Long> whiteList;

	private final Controller controller;

	private final Scripts scripts;

	public RemoteControlBot(
		final String botToken,
		final Set<Long> whiteList,
		final Controller controller,
		final Scripts scripts
	) {
		this.telegramClient = new OkHttpTelegramClient(botToken);
		this.whiteList = whiteList;
		this.controller = controller;
		this.scripts = scripts;
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
					scripts(chatId);
					break;

				case "/clipboard":
					clipboard(chatId);
					break;

				default:
					execute(chatId, text);
					break;
				}
			}
		} else if (update.hasCallbackQuery()) {
			CallbackQuery query = update.getCallbackQuery();
			String queryId = query.getId();
			long userId = query.getFrom().getId();
			long chatId = query.getMessage().getChatId();
			String data = query.getData();

			if (!whiteList.contains(userId)) {
				callbackAnswer(queryId, String.format("Access denied, your ID %d.", userId));
			} else if (data.startsWith(":")) {
				String name = data.substring(1);
				String script = scripts.getScript(name);
				execute(chatId, script);

				callbackAnswer(queryId);
			}
		}
	}

	private void scripts(long chatId) {
		try {
			scripts.update();
		} catch (IOException e) {
			LOG.warn("Failed to update scripts.", e);
		}

		StringBuilder builder = new StringBuilder();
		InlineKeyboardMarkupBuilder<?, ?> keyboardBuilder = InlineKeyboardMarkup.builder();
		InlineKeyboardRow row = new InlineKeyboardRow();
		int index = 1;

		for (String name : scripts.getNames()) {
			InlineKeyboardButton button = new InlineKeyboardButton(name);
			button.setCallbackData(String.format(":%s", name));
			row.add(button);

			builder.append(index);
			builder.append("\\. *");
			builder.append(name.strip());
			builder.append("*\n");

			if (index % 4 == 0) {
				keyboardBuilder.keyboardRow(row);
				row = new InlineKeyboardRow();
			}

			index += 1;
		}

		if (!row.isEmpty()) {
			keyboardBuilder.keyboardRow(row);
		}

		InlineKeyboardMarkup markup = keyboardBuilder.build();
		SendMessage message = SendMessage.builder()
			.chatId(chatId)
			.parseMode("MarkdownV2")
			.text(builder.toString())
			.replyMarkup(markup)
			.build();

		try {
			telegramClient.execute(message);
		} catch (TelegramApiException e) {
			LOG.warn("Failed to send message.", e);
		}
	}

	private void execute(long chatId, String text) {
		List<Command> commands;

		try {
			commands = CommandParser.parse(text.toLowerCase());
		} catch (ParserException e) {
			LOG.warn("Failed to parse script.", e);

			sendText(chatId, String.format("Parsing error: %s", e.getMessage()));

			return;
		}

		StringBuilder builder = new StringBuilder();

		for (Command command : commands) {
			command.execute(controller, message -> {
				builder.append(message);
				builder.append('\n');
			});
		}

		if (!builder.isEmpty()) {
			String output = builder.toString().strip();

			sendMessage(chatId, String.format("```\n%s\n```", output));
		} else {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				LOG.warn("Sleep failed.", e);
			}

			BufferedImage image = controller.showCursor();
			sendPhoto(chatId, image);
		}
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
		WAKEUP_COMMAND.execute(controller, message -> {
		});

		sendMessage(chatId, "Done.");
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

	private void callbackAnswer(String queryId) {
		AnswerCallbackQuery answer = AnswerCallbackQuery.builder().callbackQueryId(queryId).build();

		try {
			telegramClient.execute(answer);
		} catch (TelegramApiException e) {
			LOG.warn("Failed to send answer.", e);
		}
	}

	private void callbackAnswer(String queryId, String text) {
		AnswerCallbackQuery answer = AnswerCallbackQuery.builder().callbackQueryId(queryId).text(text).build();

		try {
			telegramClient.execute(answer);
		} catch (TelegramApiException e) {
			LOG.warn("Failed to send answer.", e);
		}
	}

	private void sendText(long chatId, String text) {
		SendMessage message = SendMessage.builder().chatId(chatId).text(text).build();

		try {
			telegramClient.execute(message);
		} catch (TelegramApiException e) {
			LOG.warn("Failed to send message.", e);
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
