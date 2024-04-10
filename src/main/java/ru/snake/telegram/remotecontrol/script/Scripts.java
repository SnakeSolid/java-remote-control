package ru.snake.telegram.remotecontrol.script;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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

	private long updateTime;

	public Scripts(final File path, final Yaml yaml, final Map<String, String> scripts) {
		this.path = path;
		this.yaml = yaml;
		this.scripts = scripts;
		this.updateTime = path.lastModified();
	}

	public Set<String> getNames() {
		return Collections.unmodifiableSet(scripts.keySet());
	}

	public String getScript(String name) {
		return scripts.get(name);
	}

	public void update() throws FileNotFoundException, IOException {
		if (updateTime < path.lastModified()) {
			Map<String, String> content = load(path, yaml);

			scripts.clear();
			scripts.putAll(content);

			updateTime = path.lastModified();
		}
	}

	public void save() throws IOException {
		save(path, yaml, scripts);

		updateTime = path.lastModified();
	}

	public static Scripts from(final File path) {
		Yaml yaml = new Yaml();
		Map<String, String> scripts = new TreeMap<>();

		if (path.exists()) {
			try {
				scripts = load(path, yaml);
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

	private static Map<String, String> load(final File path, final Yaml yaml)
			throws FileNotFoundException, IOException {
		try (FileReader input = new FileReader(path); BufferedReader reader = new BufferedReader(input)) {
			return yaml.loadAs(reader, Map.class);
		}
	}

	private static void save(final File path, final Yaml yaml, final Map<String, String> scripts) throws IOException {
		try (FileWriter output = new FileWriter(path); BufferedWriter writer = new BufferedWriter(output)) {
			yaml.dump(scripts, writer);
		}
	}

}
