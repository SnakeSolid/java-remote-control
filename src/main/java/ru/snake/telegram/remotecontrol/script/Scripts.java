package ru.snake.telegram.remotecontrol.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public class Scripts {

	private static final Logger LOG = LoggerFactory.getLogger(Scripts.class);

	private final File path;

	private final Yaml yaml;

	private final Map<String, String> scripts;

	public Scripts(final File path, final Yaml yaml, final Map<String, String> scripts) {
		this.path = path;
		this.yaml = yaml;
		this.scripts = scripts;
	}

	public Set<String> getNames() {
		return Collections.unmodifiableSet(scripts.keySet());
	}

	public String getScript(String name) {
		return scripts.get(name);
	}

	public static Scripts from(final File path) {
		Yaml yaml = new Yaml();
		Map<String, String> scripts = new TreeMap<>();

		if (path.exists()) {
			try (FileReader input = new FileReader(path); BufferedReader reader = new BufferedReader(input)) {
				scripts.putAll(yaml.loadAs(reader, Map.class));
			} catch (FileNotFoundException e) {
				LOG.error("Error creating controller.", e);

				return null;
			} catch (IOException e) {
				LOG.error("Error creating controller.", e);

				return null;
			}
		}

		return new Scripts(path, yaml, scripts);
	}

}
