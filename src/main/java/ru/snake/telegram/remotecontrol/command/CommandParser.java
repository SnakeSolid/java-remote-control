package ru.snake.telegram.remotecontrol.command;

import static org.jparsec.Parsers.or;
import static org.jparsec.Parsers.sequence;
import static org.jparsec.Scanners.IDENTIFIER;
import static org.jparsec.Scanners.INTEGER;
import static org.jparsec.Scanners.isChar;
import static org.jparsec.Scanners.string;
import static org.jparsec.pattern.Patterns.many;

import java.util.List;

import org.jparsec.Parser;
import org.jparsec.pattern.CharPredicates;

public class CommandParser {

	public static final Parser<Void> SPACE0 = isChar(' ').skipAtLeast(0);

	public static final Parser<Void> SPACE1 = isChar(' ').skipAtLeast(1);

	public static final Parser<Integer> NUMBER = INTEGER.map(Integer::parseInt);

	public static final Parser<Integer> SIGNED = or(
		sequence(isChar('+'), NUMBER, (a, b) -> +b),
		sequence(isChar('-'), NUMBER, (a, b) -> -b)
	);

	@SuppressWarnings("unchecked")
	private static final Parser<Key> KEY = or(
		isChar('[').map(k -> Key.from("[")),
		isChar(']').map(k -> Key.from("]")),
		isChar('.').map(k -> Key.from(".")),
		isChar(',').map(k -> Key.from(",")),
		isChar(';').map(k -> Key.from(";")),
		isChar('\\').map(k -> Key.from("\\")),
		isChar('+').map(k -> Key.from("+")),
		isChar('-').map(k -> Key.from("-")),
		isChar('*').map(k -> Key.from("*")),
		isChar('/').map(k -> Key.from("/")),
		isChar('=').map(k -> Key.from("=")),
		IDENTIFIER.map(k -> Key.from(k))
	);

	private static final Parser<Command> KEYS = sequence(
		string("keys"),
		SPACE1,
		KEY.sepBy1(SPACE1),
		(a, b, c) -> Command.pressKeys(c)
	);

	private static final Parser<Command> CLICK = sequence(
		string("click"),
		sequence(
			SPACE1,
			or(
				string("left").map(k -> Button.Left),
				string("right").map(k -> Button.Right),
				string("middle").map(k -> Button.Scroll),
				string("scroll").map(k -> Button.Scroll)
			),
			(a, b) -> b
		).optional(Button.Left),
		sequence(SPACE1, NUMBER, (a, b) -> b).optional(1),
		(a, button, count) -> Command.mouseClick(button, count)
	);

	private static final Parser<Command> DBLCLICK = sequence(
		string("dblclick"),
		sequence(
			SPACE1,
			or(
				string("left").map(k -> Button.Left),
				string("right").map(k -> Button.Right),
				string("middle").map(k -> Button.Scroll),
				string("scroll").map(k -> Button.Scroll)
			),
			(a, b) -> b
		).optional(Button.Left),
		(a, button) -> Command.mouseClick(button, 2)
	);

	private static final Parser<Command> MOVE = sequence(
		string("move"),
		SPACE1,
		or(
			sequence(NUMBER, SPACE1, NUMBER, (a, b, c) -> Command.moveAbsolute(a, c)),
			sequence(SIGNED, SPACE1, NUMBER, (a, b, c) -> Command.moveRelative(a, c)),
			sequence(NUMBER, SPACE1, SIGNED, (a, b, c) -> Command.moveRelative(a, c)),
			sequence(SIGNED, SPACE1, SIGNED, (a, b, c) -> Command.moveRelative(a, c))
		),
		(a, b, c) -> c
	);

	private static final Parser<Command> PASTE = sequence(
		string("paste"),
		SPACE1,
		many(CharPredicates.notChar('\n')).toScanner("text").source(),
		(a, b, c) -> Command.pasteText(c)
	);

	private static final Parser<Command> COMMAND = or(KEYS, CLICK, DBLCLICK, MOVE, PASTE);

	private static final Parser<List<Command>> COMMANDS = COMMAND.between(SPACE0, SPACE0).sepEndBy(isChar('\n'));

	public static List<Command> parse(final String content) {
		return COMMANDS.parse(content);
	}

}
