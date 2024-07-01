package ru.snake.telegram.remotecontrol;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Controller {

	private static final Logger LOG = LoggerFactory.getLogger(Controller.class);

	private static final int MAX_TEXT_LENGTH = 4_096;

	private static final int CURSOR_WIDTH = 1024;

	private static final int CURSOR_HEIGHT = 1024;

	private final BufferedImage cursor;

	private final Clipboard clipboard;

	private final Rectangle screenRect;

	private final Robot robot;

	private Point cursorPosition;

	private Controller(
		final BufferedImage cursor,
		final Clipboard clipboard,
		final Rectangle screenRect,
		final Robot robot
	) {
		this.cursor = cursor;
		this.clipboard = clipboard;
		this.screenRect = screenRect;
		this.robot = robot;
		this.cursorPosition = new Point(screenRect.width / 2, screenRect.height / 2);
	}

	public BufferedImage takeScreenshot() {
		BufferedImage image = robot.createScreenCapture(screenRect);

		return image;
	}

	public BufferedImage showCursor() {
		int cursorX = bounds(cursorPosition.x, CURSOR_WIDTH, 0, screenRect.width);
		int cursorY = bounds(cursorPosition.y, CURSOR_HEIGHT, 0, screenRect.height);
		int screenX = offset(cursorPosition.x, CURSOR_WIDTH, 0, screenRect.width);
		int screenY = offset(cursorPosition.y, CURSOR_HEIGHT, 0, screenRect.height);

		BufferedImage image = robot.createScreenCapture(new Rectangle(screenX, screenY, CURSOR_WIDTH, CURSOR_HEIGHT));
		Graphics2D graphics = image.createGraphics();
		graphics.setColor(Color.RED);
		graphics.drawImage(cursor, cursorX - cursor.getWidth() / 2, cursorY - cursor.getHeight() / 2, null);

		return image;
	}

	private static int bounds(int value, int width, int min, int max) {
		if (max - min < width) {
			return value;
		} else if (value < width / 2) {
			return value;
		} else if (value > max - width / 2) {
			return value + width - max;
		} else {
			return width / 2;
		}
	}

	private static int offset(int value, int width, int min, int max) {
		if (max - min < width) {
			return 0;
		} else if (value < width / 2) {
			return 0;
		} else if (value > max - width / 2) {
			return max - width;
		} else {
			return value - width / 2;
		}
	}

	public Content clipboardContent() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		try {
			Transferable contents = clipboard.getContents(null);

			if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				String string = (String) contents.getTransferData(DataFlavor.stringFlavor);
				String text = string.replace("`", "\\`");

				if (text.length() <= MAX_TEXT_LENGTH - 8) {
					return Content.text(String.format("```\n%s\n```", text));
				} else {
					return Content.text("Clipboard content is too long.");
				}
			} else if (contents.isDataFlavorSupported(DataFlavor.imageFlavor)) {
				BufferedImage image = (BufferedImage) contents.getTransferData(DataFlavor.imageFlavor);

				return Content.image(image);
			}
		} catch (UnsupportedFlavorException | IOException e) {
			LOG.warn("Failed to get clipboard data.", e);

			return Content.text(e.getMessage());
		}

		return Content.dummy();
	}

	public Rectangle getScreenRect() {
		return screenRect;
	}

	public Point getCursorPosition() {
		return cursorPosition;
	}

	public void setCursorPosition(int x, int y) {
		robot.mouseMove(x, y);
		cursorPosition = new Point(x, y);
	}

	public void mousePress(int button) {
		robot.mousePress(button);
	}

	public void mouseRelease(int button) {
		robot.mouseRelease(button);
	}

	public void keyPress(int key) {
		robot.keyPress(key);
	}

	public void keyRelease(int key) {
		robot.keyRelease(key);
	}

	public void setClipboard(String text) {
		StringSelection selection = new StringSelection(text);
		clipboard.setContents(selection, selection);
	}

	public void sleep(int millis) {
		robot.delay(millis);
	}

	@Override
	public String toString() {
		return "Controller [cursor=" + cursor + ", clipboard=" + clipboard + ", screeRect=" + screenRect + ", robot="
				+ robot + ", cursorPosition=" + cursorPosition + "]";
	}

	public static Controller create() throws AWTException, IOException {
		InputStream cursorResource = ClassLoader.getSystemResourceAsStream("cursor.png");
		BufferedImage cursor = ImageIO.read(cursorResource);
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		Dimension screenSize = toolkit.getScreenSize();
		Rectangle screenRect = new Rectangle(screenSize);
		Robot robot = new Robot();
		robot.setAutoDelay(10);

		return new Controller(cursor, clipboard, screenRect, robot);
	}

}
