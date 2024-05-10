package ru.snake.telegram.remotecontrol.command;

import static org.jparsec.Parsers.or;
import static org.jparsec.Parsers.sequence;
import static org.jparsec.Scanners.DOUBLE_QUOTE_STRING;
import static org.jparsec.Scanners.IDENTIFIER;
import static org.jparsec.Scanners.INTEGER;
import static org.jparsec.Scanners.isChar;
import static org.jparsec.Scanners.stringCaseInsensitive;
import static org.jparsec.pattern.Patterns.many;

import java.util.List;
import java.util.Optional;

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
		INTEGER.map(k -> Key.from(k)),
		IDENTIFIER.map(k -> Key.from(k))
	);

	private static final Parser<Command> KEYS = sequence(
		stringCaseInsensitive("keys"),
		SPACE1,
		KEY.sepBy1(SPACE1),
		(a, b, c) -> Command.pressKeys(c)
	);

	private static final Parser<Command> CLICK = sequence(
		stringCaseInsensitive("click"),
		sequence(
			SPACE1,
			or(
				stringCaseInsensitive("left").map(k -> Button.Left),
				stringCaseInsensitive("right").map(k -> Button.Right),
				stringCaseInsensitive("middle").map(k -> Button.Scroll),
				stringCaseInsensitive("scroll").map(k -> Button.Scroll)
			),
			(a, b) -> b
		).optional(Button.Left),
		sequence(SPACE1, NUMBER, (a, b) -> b).optional(1),
		(a, button, count) -> Command.mouseClick(button, count)
	);

	private static final Parser<Command> MOVE = sequence(
		stringCaseInsensitive("move"),
		SPACE1,
		or(
			sequence(NUMBER, SPACE1, NUMBER, (a, b, c) -> Command.moveAbsolute(a, c)),
			sequence(SIGNED, SPACE1, NUMBER, (a, b, c) -> Command.moveRelative(a, c)),
			sequence(NUMBER, SPACE1, SIGNED, (a, b, c) -> Command.moveRelative(a, c)),
			sequence(SIGNED, SPACE1, SIGNED, (a, b, c) -> Command.moveRelative(a, c))
		),
		(a, b, c) -> c
	);

	private static final Parser<Command> BUFFER = sequence(
		stringCaseInsensitive("buffer"),
		SPACE1,
		many(CharPredicates.notChar('\n')).toScanner("text").source(),
		(a, b, c) -> Command.pasteText(c.strip())
	);

	private static final Parser<Command> EXEC = sequence(
		stringCaseInsensitive("exec"),
		SPACE1,
		many(CharPredicates.notChar('\n')).toScanner("text").source(),
		(a, b, c) -> Command.shellExecute(c.strip())
	);

	private static final Parser<Command> COMMAND = or(KEYS, CLICK, MOVE, BUFFER, EXEC);

	private static final Parser<List<Command>> COMMANDS = COMMAND.between(SPACE0, SPACE0).sepEndBy(isChar('\n'));

	private static final Parser<String> STRING = DOUBLE_QUOTE_STRING.map(s -> s.substring(1, s.length() - 1));

	private static final Parser<Optional<String>> NAME = sequence(
		SPACE0,
		or(IDENTIFIER, STRING),
		SPACE0,
		isChar(':'),
		SPACE0,
		isChar('\n'),
		(a, b, c, d, e, f) -> b
	).asOptional();

	private static final Parser<Script> SCRIPT = sequence(NAME, COMMANDS, Script::from);

	public static Script parse(final String content) {
		return SCRIPT.parse(content);
	}

}
