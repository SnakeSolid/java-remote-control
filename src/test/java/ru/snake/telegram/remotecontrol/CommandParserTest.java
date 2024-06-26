package ru.snake.telegram.remotecontrol;

import java.util.List;

import org.jparsec.error.ParserException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ru.snake.telegram.remotecontrol.command.Command;
import ru.snake.telegram.remotecontrol.command.CommandParser;
import ru.snake.telegram.remotecontrol.command.MouseClick;
import ru.snake.telegram.remotecontrol.command.MoveAbsolute;
import ru.snake.telegram.remotecontrol.command.MoveRelative;
import ru.snake.telegram.remotecontrol.command.PressKeys;
import ru.snake.telegram.remotecontrol.command.Script;
import ru.snake.telegram.remotecontrol.command.SetBuffer;

public class CommandParserTest {

	@Test
	public void mustThrowError() {
		Assertions.assertThrows(ParserException.class, () -> CommandParser.parse("invalid command"));
	}

	@Test
	public void mustParseEmptyString() {
		List<Command> commands = CommandParser.parse("").getCommands();

		Assertions.assertEquals(0, commands.size());
	}

	@Test
	public void mustParseStriptNameIdent() {
		Script script = CommandParser.parse("Script:\n");

		Assertions.assertTrue(script.hasName());
		Assertions.assertEquals("Script", script.getName());
	}

	@Test
	public void mustParseStriptNameString() {
		Script script = CommandParser.parse("\"Script name\":\n");

		Assertions.assertTrue(script.hasName());
		Assertions.assertEquals("Script name", script.getName());
	}

	@Test
	public void mustParseKeys() {
		List<Command> commands = CommandParser.parse("keys ctrl a c").getCommands();

		Assertions.assertEquals(1, commands.size());
		Assertions.assertInstanceOf(PressKeys.class, commands.get(0));
	}

	@Test
	public void mustParseClick() {
		List<Command> commands = CommandParser.parse("click").getCommands();

		Assertions.assertEquals(1, commands.size());
		Assertions.assertInstanceOf(MouseClick.class, commands.get(0));
	}

	@Test
	public void mustParseComplexClick() {
		List<Command> commands = CommandParser.parse("click left 2").getCommands();

		Assertions.assertEquals(1, commands.size());
		Assertions.assertInstanceOf(MouseClick.class, commands.get(0));
	}

	@Test
	public void mustParseMoveAbsolute() {
		List<Command> commands = CommandParser.parse("move 50 50").getCommands();

		Assertions.assertEquals(1, commands.size());
		Assertions.assertInstanceOf(MoveAbsolute.class, commands.get(0));
	}

	@Test
	public void mustThrowInvalidValue() {
		Assertions.assertThrows(ParserException.class, () -> CommandParser.parse("move 101 0"));
		Assertions.assertThrows(ParserException.class, () -> CommandParser.parse("move 0 101"));
	}

	@Test
	public void mustParseMoveRelative1() {
		List<Command> commands = CommandParser.parse("move +50 -50").getCommands();

		Assertions.assertEquals(1, commands.size());
		Assertions.assertInstanceOf(MoveRelative.class, commands.get(0));
	}

	@Test
	public void mustParseMoveRelative2() {
		List<Command> commands = CommandParser.parse("move -50 +50").getCommands();

		Assertions.assertEquals(1, commands.size());
		Assertions.assertInstanceOf(MoveRelative.class, commands.get(0));
	}

	@Test
	public void mustParseBuffer() {
		List<Command> commands = CommandParser.parse("buffer test   ").getCommands();

		Assertions.assertEquals(1, commands.size());
		Assertions.assertInstanceOf(SetBuffer.class, commands.get(0));
	}

	@Test
	public void mustParseScript() {
		List<Command> commands = CommandParser
			.parse(" \"name\":\n keys ctrl a c \n click right\nmove 10 +5\nmove -5 0\n")
			.getCommands();

		Assertions.assertEquals(4, commands.size());
		Assertions.assertInstanceOf(PressKeys.class, commands.get(0));
		Assertions.assertInstanceOf(MouseClick.class, commands.get(1));
		Assertions.assertInstanceOf(MoveRelative.class, commands.get(2));
		Assertions.assertInstanceOf(MoveRelative.class, commands.get(3));
	}

	@Test
	public void mustParseCaseIgnoringCase() {
		List<Command> commands = CommandParser.parse("ClIcK").getCommands();

		Assertions.assertEquals(1, commands.size());
		Assertions.assertInstanceOf(MouseClick.class, commands.get(0));
	}

}
