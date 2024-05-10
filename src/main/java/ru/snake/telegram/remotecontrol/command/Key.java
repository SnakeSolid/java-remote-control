package ru.snake.telegram.remotecontrol.command;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class Key {

	private static final Map<String, Key> KEYS;

	static {
		KEYS = new HashMap<>();
		KEYS.put("shift", new Key(KeyEvent.VK_SHIFT, true));
		KEYS.put("ctrl", new Key(KeyEvent.VK_CONTROL, true));
		KEYS.put("alt", new Key(KeyEvent.VK_ALT, true));
		KEYS.put("win", new Key(KeyEvent.VK_WINDOWS, true));

		KEYS.put("enter", new Key(KeyEvent.VK_ENTER, false));
		KEYS.put("backspace", new Key(KeyEvent.VK_BACK_SPACE, false));
		KEYS.put("tab", new Key(KeyEvent.VK_TAB, false));
		KEYS.put("caps", new Key(KeyEvent.VK_CAPS_LOCK, false));
		KEYS.put("escape", new Key(KeyEvent.VK_ESCAPE, false));
		KEYS.put("space", new Key(KeyEvent.VK_SPACE, false));
		KEYS.put("pageup", new Key(KeyEvent.VK_PAGE_UP, false));
		KEYS.put("pagedown", new Key(KeyEvent.VK_PAGE_DOWN, false));
		KEYS.put("end", new Key(KeyEvent.VK_END, false));
		KEYS.put("home", new Key(KeyEvent.VK_HOME, false));
		KEYS.put("left", new Key(KeyEvent.VK_LEFT, false));
		KEYS.put("up", new Key(KeyEvent.VK_UP, false));
		KEYS.put("right", new Key(KeyEvent.VK_RIGHT, false));
		KEYS.put("down", new Key(KeyEvent.VK_DOWN, false));

		KEYS.put("0", new Key(KeyEvent.VK_0, false));
		KEYS.put("1", new Key(KeyEvent.VK_1, false));
		KEYS.put("2", new Key(KeyEvent.VK_2, false));
		KEYS.put("3", new Key(KeyEvent.VK_3, false));
		KEYS.put("4", new Key(KeyEvent.VK_4, false));
		KEYS.put("5", new Key(KeyEvent.VK_5, false));
		KEYS.put("6", new Key(KeyEvent.VK_6, false));
		KEYS.put("7", new Key(KeyEvent.VK_7, false));
		KEYS.put("8", new Key(KeyEvent.VK_8, false));
		KEYS.put("9", new Key(KeyEvent.VK_9, false));

		KEYS.put("a", new Key(KeyEvent.VK_A, false));
		KEYS.put("b", new Key(KeyEvent.VK_B, false));
		KEYS.put("c", new Key(KeyEvent.VK_C, false));
		KEYS.put("d", new Key(KeyEvent.VK_D, false));
		KEYS.put("e", new Key(KeyEvent.VK_E, false));
		KEYS.put("f", new Key(KeyEvent.VK_F, false));
		KEYS.put("g", new Key(KeyEvent.VK_G, false));
		KEYS.put("h", new Key(KeyEvent.VK_H, false));
		KEYS.put("i", new Key(KeyEvent.VK_I, false));
		KEYS.put("j", new Key(KeyEvent.VK_J, false));
		KEYS.put("k", new Key(KeyEvent.VK_K, false));
		KEYS.put("l", new Key(KeyEvent.VK_L, false));
		KEYS.put("m", new Key(KeyEvent.VK_M, false));
		KEYS.put("n", new Key(KeyEvent.VK_N, false));
		KEYS.put("o", new Key(KeyEvent.VK_O, false));
		KEYS.put("p", new Key(KeyEvent.VK_P, false));
		KEYS.put("q", new Key(KeyEvent.VK_Q, false));
		KEYS.put("r", new Key(KeyEvent.VK_R, false));
		KEYS.put("s", new Key(KeyEvent.VK_S, false));
		KEYS.put("t", new Key(KeyEvent.VK_T, false));
		KEYS.put("u", new Key(KeyEvent.VK_U, false));
		KEYS.put("v", new Key(KeyEvent.VK_V, false));
		KEYS.put("w", new Key(KeyEvent.VK_W, false));
		KEYS.put("x", new Key(KeyEvent.VK_X, false));
		KEYS.put("y", new Key(KeyEvent.VK_Y, false));
		KEYS.put("z", new Key(KeyEvent.VK_Z, false));

		KEYS.put("[", new Key(KeyEvent.VK_OPEN_BRACKET, false));
		KEYS.put("]", new Key(KeyEvent.VK_CLOSE_BRACKET, false));
		KEYS.put(".", new Key(KeyEvent.VK_PERIOD, false));
		KEYS.put(",", new Key(KeyEvent.VK_COMMA, false));
		KEYS.put(";", new Key(KeyEvent.VK_SEMICOLON, false));
		KEYS.put("\\", new Key(KeyEvent.VK_BACK_SLASH, false));
		KEYS.put("+", new Key(KeyEvent.VK_ADD, false));
		KEYS.put("-", new Key(KeyEvent.VK_MINUS, false));
		KEYS.put("*", new Key(KeyEvent.VK_MULTIPLY, false));
		KEYS.put("/", new Key(KeyEvent.VK_SLASH, false));
		KEYS.put("=", new Key(KeyEvent.VK_EQUALS, false));

		KEYS.put("delete", new Key(KeyEvent.VK_DELETE, false));
		KEYS.put("num", new Key(KeyEvent.VK_NUM_LOCK, false));
		KEYS.put("scroll", new Key(KeyEvent.VK_SCROLL_LOCK, false));

		KEYS.put("f1", new Key(KeyEvent.VK_F1, false));
		KEYS.put("f2", new Key(KeyEvent.VK_F2, false));
		KEYS.put("f3", new Key(KeyEvent.VK_F3, false));
		KEYS.put("f4", new Key(KeyEvent.VK_F4, false));
		KEYS.put("f5", new Key(KeyEvent.VK_F5, false));
		KEYS.put("f6", new Key(KeyEvent.VK_F6, false));
		KEYS.put("f7", new Key(KeyEvent.VK_F7, false));
		KEYS.put("f8", new Key(KeyEvent.VK_F8, false));
		KEYS.put("f9", new Key(KeyEvent.VK_F9, false));
		KEYS.put("f10", new Key(KeyEvent.VK_F10, false));
		KEYS.put("f11", new Key(KeyEvent.VK_F11, false));
		KEYS.put("f12", new Key(KeyEvent.VK_F12, false));

		KEYS.put("printscreen", new Key(KeyEvent.VK_PRINTSCREEN, false));
		KEYS.put("insert", new Key(KeyEvent.VK_INSERT, false));
		KEYS.put("backquote", new Key(KeyEvent.VK_BACK_QUOTE, false));
		KEYS.put("quote", new Key(KeyEvent.VK_QUOTE, false));
	}

	private final int event;

	private final boolean modifier;

	public Key(int event, boolean modifier) {
		this.event = event;
		this.modifier = modifier;
	}

	public int getEvent() {
		return event;
	}

	public boolean isModifier() {
		return modifier;
	}

	@Override
	public String toString() {
		return "Key [event=" + event + ", modifier=" + modifier + "]";
	}

	public static Key from(String text) {
		String name = text.toLowerCase();
		Key key = KEYS.get(name);

		if (key == null) {
			throw new IllegalArgumentException(String.format("Invalid key name: %s.", text));
		}

		return key;
	}

}
