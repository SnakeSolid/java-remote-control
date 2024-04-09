package ru.snake.telegram.remotecontrol.command;

import java.awt.event.InputEvent;

public enum Button {

	Left, Right, Scroll;

	public int asIdentifier() {
		switch (this) {
		case Left:
			return InputEvent.BUTTON1_DOWN_MASK;

		case Right:
			return InputEvent.BUTTON2_DOWN_MASK;

		case Scroll:
			return InputEvent.BUTTON3_DOWN_MASK;

		default:
			return InputEvent.BUTTON1_DOWN_MASK;
		}
	}

}
