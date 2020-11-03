package de.rapha149.entityfinder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class Lang {

	private static String ENTITIES_LANG_FILE;
	private static JsonObject ENTITIES_LANG;

	public static String YES;
	public static String NO;

	public static String OVERWORLD;
	public static String NETHER;
	public static String END;

	public static String NOT_A_WORLD_FOLDER;
	public static String NO_REGION_FILES;
	public static String SHOW_WORLD_NAME;
	public static String ONE_FILE_COULD_NOT_BE_READ;
	public static String SOME_FILES_COULD_NOT_BE_READ;
	public static String WRONG_NBT_DATA;
	public static String NO_ENTITIES_FOUND;

	public static String FIND_OR_REMOVE;
	public static String STATE_ONE_OR_TWO;

	public static String ENTER_NBT_DATA;
	public static String ONE_ENTITY_FOUND;
	public static String ENTITIES_FOUND;

	public static String REMOVE_ONE_ENTITY;
	public static String REMOVE_ENTITIES;
	public static String STATE_YES_OR_NO;
	public static String CANCELLED;
	public static String ONE_ENTITY_REMOVED;
	public static String ENTITIES_REMOVED;

	public static void initialize(Language language)
			throws JsonIOException, JsonSyntaxException, IOException, URISyntaxException {
		switch (language) {
		case GERMAN:
			ENTITIES_LANG_FILE = "entities-de_de.json";

			YES = "Ja";
			NO = "Nein";

			OVERWORLD = "Overworld";
			NETHER = "Nether";
			END = "Ende";

			NOT_A_WORLD_FOLDER = "Das ist kein Welt-Ordner.";
			NO_REGION_FILES = "Keine Regionen-Dateien gefunden.";
			SHOW_WORLD_NAME = "Welt gefunden: %s";
			ONE_FILE_COULD_NOT_BE_READ = "Eine Datei konnten nicht ausgelesen werden.";
			SOME_FILES_COULD_NOT_BE_READ = "%s Dateien konnten nicht ausgelesen werden.";
			ENTER_NBT_DATA = "Bitte die NBT-Daten oder die ID der Entität zum Filtern angeben. (Nichts schreiben für alle Entitäten)";
			WRONG_NBT_DATA = "NBT-Daten konnten nicht gelesen werden.";
			NO_ENTITIES_FOUND = "Keine Entitäten gefunden.";

			FIND_OR_REMOVE = "Willst du Entitäten\n(1) suchen oder\n(2) entfernen?";
			STATE_ONE_OR_TWO = "Bitte die Zahl 1 oder 2 angeben.";

			ONE_ENTITY_FOUND = "%s Entität wurde gefunden. Output-Datei: \"%s\"";
			ENTITIES_FOUND = "%s Entitäten wurden gefunden. Output-Datei: \"%s\"";

			REMOVE_ONE_ENTITY = "Willst du wirklich %s Entität entfernen? (" + YES + "/" + NO + ")";
			REMOVE_ENTITIES = "Willst du wirklich %s Entitäten entfernen? (" + YES + "/" + NO + ")";
			STATE_YES_OR_NO = "Bitte " + YES + " oder " + NO + " angeben.";
			CANCELLED = "Abgebrochen.";
			ONE_ENTITY_REMOVED = "%s Entität wurde entfernt.";
			ENTITIES_REMOVED = "%s Entitäten wurden entfernt.";
			break;
		case ENGLISH:
			ENTITIES_LANG_FILE = "entities-en_us.json";

			YES = "Yes";
			NO = "No";

			OVERWORLD = "Overworld";
			NETHER = "Nether";
			END = "End";

			NOT_A_WORLD_FOLDER = "This is not a world folder.";
			NO_REGION_FILES = "No region files found.";
			SHOW_WORLD_NAME = "World found: %s";
			ONE_FILE_COULD_NOT_BE_READ = "One file could not be read.";
			SOME_FILES_COULD_NOT_BE_READ = "%s files could not be read.";
			ENTER_NBT_DATA = "Please enter the NBT data or the entity id for filtering. (Write nothing for all entities)";
			WRONG_NBT_DATA = "NBT data could not be read.";
			NO_ENTITIES_FOUND = "No entities found.";

			FIND_OR_REMOVE = "Do you want to\n(1) find or\n(2) remove\nEntities?";
			STATE_ONE_OR_TWO = "Please state the number 1 or 2.";

			ONE_ENTITY_FOUND = "%s entity found. Output file: \"%s\"";
			ENTITIES_FOUND = "%s entities found. Output file: \"%s\"";

			REMOVE_ONE_ENTITY = "Do you want to remove %s entity? (" + YES + "/" + NO + ")";
			REMOVE_ENTITIES = "Do you want to remove %s entities? (" + YES + "/" + NO + ")";
			STATE_YES_OR_NO = "State " + YES + " or " + NO + ".";
			CANCELLED = "Cancelled.";
			ONE_ENTITY_REMOVED = "%s entity was removed.";
			ENTITIES_REMOVED = "%s were removed.";
			break;
		}

		ENTITIES_LANG = JsonParser.parseReader(new InputStreamReader(
				ClassLoader.getSystemClassLoader().getResourceAsStream(ENTITIES_LANG_FILE), StandardCharsets.UTF_8))
				.getAsJsonObject();
	}

	public static boolean isEntityName(String id) {
		String type = id.startsWith(EntityFinder.ID_PREFIX) ? id.substring(EntityFinder.ID_PREFIX.length()) : id;
		return ENTITIES_LANG.has(type);
	}

	public static String getEntityName(String id) {
		String type = id.startsWith(EntityFinder.ID_PREFIX) ? id.substring(EntityFinder.ID_PREFIX.length()) : id;
		if (ENTITIES_LANG.has(type))
			return ENTITIES_LANG.get(type).getAsString();
		return type;
	}

	public static enum Language {
		GERMAN("German", "de", "ger", "german"), ENGLISH("English", "en", "english");

		private String name;
		private List<String> identifiers;

		private Language(String name, String... identifiers) {
			this.name = name;
			this.identifiers = Arrays.asList(identifiers);
		}

		public static Language of(String identifier) {
			for (Language language : values())
				if (language.identifiers.contains(identifier.toLowerCase()))
					return language;
			return null;
		}

		public static Map<String, List<String>> getPossibleIdentifiers() {
			Map<String, List<String>> identifiers = new HashMap<>();
			for (Language language : values())
				identifiers.put(language.name, language.identifiers);
			return identifiers;
		}
	}

}
