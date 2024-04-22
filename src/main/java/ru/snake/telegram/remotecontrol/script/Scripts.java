package ru.snake.telegram.remotecontrol.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import ru.snake.telegram.remotecontrol.command.Command;
import ru.snake.telegram.remotecontrol.command.CommandParser;
import ru.snake.telegram.remotecontrol.command.Script;

public class Scripts {

	private static final Logger LOG = LoggerFactory.getLogger(Scripts.class);

	private final File path;

	private final Yaml yaml;

	private final Map<String, List<Command>> scripts;

	private long updateTime;

	public Scripts(final File path, final Yaml yaml, final Map<String, List<Command>> scripts) {
		this.path = path;
		this.yaml = yaml;
		this.scripts = scripts;
		this.updateTime = path.lastModified();
	}

	public Set<String> getNames() {
		return Collections.unmodifiableSet(scripts.keySet());
	}

	public List<Command> getCommands(String name) {
		return scripts.get(name);
	}

	public void insertCommands(String name, List<Command> commands) {
		scripts.put(name, commands);
	}

	public void update() throws IOException {
		if (updateTime < path.lastModified()) {
			Map<String, List<Command>> content = load(path, yaml);

			scripts.clear();
			scripts.putAll(content);

			updateTime = path.lastModified();
		}
	}

	@Override
	public String toString() {
		return "Scripts [path=" + path + ", yaml=" + yaml + ", scripts=" + scripts + ", updateTime=" + updateTime + "]";
	}

	public static Scripts from(final File path) {
		Yaml yaml = new Yaml();
		Map<String, List<Command>> scripts = new TreeMap<>();

		if (path.exists()) {
			try {
				scripts = load(path, yaml);
			} catch (IOException e) {
				LOG.error("Failed to load scripts file.", e);

				return null;
			}
		}

		return new Scripts(path, yaml, scripts);
	}

	private static Map<String, List<Command>> load(final File path, final Yaml yaml) throws IOException {
		try (FileReader input = new FileReader(path); BufferedReader reader = new BufferedReader(input)) {
			List<String> scripts = yaml.loadAs(reader, List.class);
			Map<String, List<Command>> result = new LinkedHashMap<>();

			for (String text : scripts) {
				Script script = CommandParser.parse(text);

				if (script.hasName()) {
					result.put(script.getName(), script.getCommands());
				}
			}

			return result;
		}
	}

}
