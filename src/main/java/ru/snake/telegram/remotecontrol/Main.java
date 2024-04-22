package ru.snake.telegram.remotecontrol;

import java.awt.AWTException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ru.snake.telegram.remotecontrol.script.Scripts;

public class Main {

	private static final Logger LOG = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		Settings settings = Settings.parse(args);

		if (settings == null) {
			return;
		}

		Scripts scripts = Scripts.from(settings.getScriptsPath());

		if (settings == null) {
			return;
		}

		Controller controller;

		try {
			controller = Controller.create();
		} catch (AWTException | IOException e) {
			LOG.error("Error creating controller.", e);

			return;
		}

		RemoteControlBot bot = new RemoteControlBot(
			settings.getBotToken(),
			settings.getAllowUsers(),
			controller,
			scripts
		);

		try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
			botsApplication.registerBot(settings.getBotToken(), bot);
			Thread.currentThread().join();
		} catch (TelegramApiException e) {
			LOG.error("Telegramm API error.", e);
		} catch (InterruptedException e) {
			LOG.error("Thread was interrupted.", e);
		} catch (Exception e) {
			LOG.error("Unknown error.", e);
		}
	}

}
