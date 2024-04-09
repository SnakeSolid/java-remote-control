package ru.snake.telegram.remotecontrol;

import java.awt.image.BufferedImage;

public class Content {

	private final String text;

	private final BufferedImage image;

	private Content(final String text, final BufferedImage image) {
		this.text = text;
		this.image = image;
	}

	public String getText() {
		return text;
	}

	public BufferedImage getImage() {
		return image;
	}

	@Override
	public String toString() {
		return "Content [text=" + text + ", image=" + image + "]";
	}

	public static Content text(final String text) {
		return new Content(text, null);
	}

	public static Content image(final BufferedImage image) {
		return new Content(null, image);
	}

	public static Content dummy() {
		return new Content(null, null);
	}

}
