package gen;

import java.util.Collection;
import java.util.HashMap;

public class Storage {

	// this mocks a database.
	private static HashMap<String, Generator> contents = new HashMap<String, Generator>();

	public static void create(String name) {
		contents.put(name, new Generator(name));
	}

	public static Generator read(String name) {
		return contents.get(name);
	}

	public static void delete(String name) {
		contents.remove(name);
	}

	public static Collection<Generator> findAll() {
		return contents.values();
	}


	static {
		Storage.create("demogen");
	}

}
