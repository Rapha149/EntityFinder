package de.rapha149.entityfinder;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import de.rapha149.entityfinder.Lang.Language;
import net.querz.mca.Chunk;
import net.querz.mca.LoadFlags;
import net.querz.mca.MCAFile;
import net.querz.mca.MCAUtil;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.DoubleTag;
import net.querz.nbt.tag.ListTag;

public class EntityFinder {

	private static FileFilter fileFilter = file -> file.getName().endsWith(".mca");

	private static boolean overworld;
	private static boolean nether;
	private static boolean end;

	private static int fileCount;
	private static int lastPercent = -1;
	private static String lastLine;

	public static void main(String[] args) {
		Language language = Language.ENGLISH;
		if (args.length > 0) {
			language = Language.of(args[0]);
			if (language == null) {
				StringBuilder sb = new StringBuilder();
				Language.getPossibleIdentifiers().forEach(
						(name, identifiers) -> sb.append("\n- " + name + ": " + String.join(", ", identifiers)));

				System.err
						.println("Unknown language identifier: " + args[0] + "\nPossible language identifiers: " + sb);
				System.exit(1);
				return;
			}
		}
		try {
			Lang.initialize(language);
		} catch (JsonIOException | JsonSyntaxException | IOException | URISyntaxException e) {
			e.printStackTrace();
			return;
		}

		File level = new File("level.dat");
		if (level.exists()) {
			File current;
			if ((current = new File("region")).exists() && current.isDirectory()
					&& current.listFiles(fileFilter).length > 0)
				overworld = true;
			if ((current = new File("Dim-1/region")).exists() && current.isDirectory()
					&& current.listFiles(fileFilter).length > 0)
				nether = true;
			if ((current = new File("Dim1/region")).exists() && current.isDirectory()
					&& current.listFiles(fileFilter).length > 0)
				end = true;

			if (!overworld && !nether && !end)
				System.out.println(Lang.NO_REGION_FILES);
			else {
				try {
					String levelName = ((CompoundTag) NBTUtil.read(level).getTag()).getCompoundTag("Data")
							.getString("LevelName");
					System.out.printf(Lang.SHOW_WORLD_NAME, levelName).println();

					System.out.println(Lang.FIND_OR_REMOVE);
					BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
					while (true) {
						try {
							int answer = Integer.parseInt(br.readLine());

							if (answer == 1) {
								System.out.println(Lang.ENTER_NBT_DATA);
								find(getNBT(br));
							} else if (answer == 2) {
								System.out.println(Lang.ENTER_NBT_DATA);
								remove(br, getNBT(br));
							} else {
								System.out.println(Lang.STATE_ONE_OR_TWO);
								continue;
							}

							System.exit(0);
							return;
						} catch (NumberFormatException e) {
							System.out.println(Lang.STATE_ONE_OR_TWO);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else
			System.out.println(Lang.NOT_A_WORLD_FOLDER);
	}
	
	private static JsonObject getNBT(BufferedReader br) throws IOException {
		while (true) {
			try {
				String line = br.readLine();
				if(Lang.isEntityName(line))
					line = "{id:\"" + line + "\"}";
				if(line.trim().isEmpty())
					line = "{}";

				JsonElement nbt = JsonParser.parseString(line);
				if (nbt.isJsonObject())
					return nbt.getAsJsonObject();
			} catch (JsonParseException e) {
			}

			System.out.println(Lang.WRONG_NBT_DATA);
		}
	}

	private static void find(JsonObject nbt) throws IOException {
		boolean exception = false;
		List<Entity> entities = new ArrayList<>();
		Map<String, File[]> worlds = new HashMap<>();
		if (overworld)
			worlds.put(Lang.OVERWORLD, new File(getRegionFolder(Lang.OVERWORLD)).listFiles(fileFilter));
		if (nether)
			worlds.put(Lang.NETHER, new File(getRegionFolder(Lang.NETHER)).listFiles(fileFilter));
		if (end)
			worlds.put(Lang.END, new File(getRegionFolder(Lang.END)).listFiles(fileFilter));

		int length = 0;
		for (File[] files : worlds.values())
			length += files.length;
		fileCount = length * 32 * 32;
		int count = 0;
		for (Entry<String, File[]> entry : worlds.entrySet()) {
			File[] files = entry.getValue();
			for (int i = 0; i < files.length; i++) {
				try {
					MCAFile file = MCAUtil.read(files[i], LoadFlags.ENTITIES);
					for (int j = 0; j < 32 * 32; j++) {
						count++;
						calcPercent(count);

						Chunk chunk = file.getChunk(j);
						if (chunk != null && chunk.getEntities() != null) {
							chunk.getEntities().forEach(entity -> {
								String json = NBTComparer.checkNBTAndReturnJson(entity, nbt);
								if (json != null) {
									ListTag<DoubleTag> pos = entity.getListTag("Pos").asDoubleTagList();
									double x = Math.round(pos.get(0).asDouble() * 100.0) / 100.0;
									double y = Math.round(pos.get(1).asDouble() * 100.0) / 100.0;
									double z = Math.round(pos.get(2).asDouble() * 100.0) / 100.0;

									entities.add(new Entity(entity.getString("id"), entry.getKey(), x, y, z, json));
								}
							});
						}
					}
				} catch (ClassCastException | EOFException e) {
					exception = true;
					continue;
				}
			}
		}

		calcPercent(fileCount);

		if (!entities.isEmpty()) {
			entities.sort((c1, c2) -> {
				int id = c1.id.compareTo(c2.id);
				long dx = c1.rx - c2.rx;
				long dy = c1.ry - c2.ry;
				long dz = c1.rz - c2.rz;

				if(id != 0)
					return id;
				if (dx != 0)
					return Long.signum(dx);
				if (dz != 0)
					return Long.signum(dy);
				return Long.signum(dy);
			});

			File file = new File(new SimpleDateFormat("'EntityFinder_'yyyy-MM-dd_HH.mm.ss'.txt'").format(new Date()));
			FileWriter writer = new FileWriter(file);
			for (Entity entity : entities)
				writer.write(entity.toString() + "\n");
			writer.flush();
			writer.close();

			System.out.printf(entities.size() == 1 ? Lang.ONE_ENTITY_FOUND : Lang.ENTITIES_FOUND, entities.size(),
					file.getName()).println();
		} else
			System.out.println(Lang.NO_ENTITIES_FOUND);

		if (exception)
			System.out.println(Lang.SOME_FILES_COULD_NOT_BE_READ);
	}

	private static void remove(BufferedReader br, JsonObject nbt) throws IOException {
		boolean exception = false;
		List<File[]> worlds = new ArrayList<>();
		Map<MCAFile, File> changed = new HashMap<>();
		if (overworld)
			worlds.add(new File(getRegionFolder(Lang.OVERWORLD)).listFiles(fileFilter));
		if (nether)
			worlds.add(new File(getRegionFolder(Lang.NETHER)).listFiles(fileFilter));
		if (end)
			worlds.add(new File(getRegionFolder(Lang.END)).listFiles(fileFilter));

		int length = 0;
		for (File[] files : worlds)
			length += files.length;
		fileCount = length * 32 * 32;
		int count = 0;

		int removed = 0;
		for (File[] files : worlds) {
			for (int i = 0; i < files.length; i++) {
				try {
					MCAFile file = MCAUtil.read(files[i]);

					for (int j = 0; j < 32 * 32; j++) {
						count++;
						calcPercent(count);
						Chunk chunk = file.getChunk(j);

						if (chunk != null && chunk.getEntities() != null) {
							Iterator<CompoundTag> iterator = chunk.getEntities().iterator();
							while (iterator.hasNext()) {
								CompoundTag entity = iterator.next();
								if (NBTComparer.checkNBT(entity, nbt)) {
									iterator.remove();
									if (!changed.containsKey(file))
										changed.put(file, files[i]);
									removed++;
								}
							}
						}
					}
				} catch (ClassCastException e) {
					exception = true;
					continue;
				}
			}
		}

		if (removed == 0)
			System.out.println(Lang.NO_ENTITIES_FOUND);
		else {
			System.out.printf(removed == 1 ? Lang.REMOVE_ONE_ENTITY : Lang.REMOVE_ENTITIES, removed).println();

			while (true) {
				String line = br.readLine();
				if (line.equalsIgnoreCase(Lang.YES)) {
					fileCount = changed.size();
					count = 0;
					lastLine = null;
					calcPercent(0);
					for (Entry<MCAFile, File> entry : changed.entrySet()) {
						MCAUtil.write(entry.getKey(), entry.getValue());
						count++;
						calcPercent(count);
					}
					
					System.out.printf(removed == 1 ? Lang.ONE_ENTITY_REMOVED : Lang.ENTITIES_REMOVED, removed)
							.println();
					return;
				}

				else if (line.equalsIgnoreCase(Lang.NO)) {
					System.out.println(Lang.CANCELLED);
					return;
				}

				else
					System.out.println(Lang.STATE_YES_OR_NO);
			}
		}

		if (exception)
			System.out.println(Lang.SOME_FILES_COULD_NOT_BE_READ);
	}

	private static void calcPercent(int completed) {
		int percent = Math.round((float) completed / (float) fileCount * 100f);
		if (percent != lastPercent) {
			if (lastLine != null)
				for (int i = 0; i < lastLine.length(); i++)
					System.out.print("\b \b");

			StringBuilder sb = new StringBuilder("[");
			for (int i = 0; i < percent; i++)
				sb.append("#");
			for (int i = 0; i < 100 - percent; i++)
				sb.append(".");
			sb.append("]");

			System.out.print(lastLine = percent + " %  " + sb + (percent == 100 ? "\n" : ""));
			lastPercent = percent;
		}
	}

	private static String getRegionFolder(String worldName) {
		if (worldName.equalsIgnoreCase(Lang.OVERWORLD))
			return "region";
		else if (worldName.equalsIgnoreCase(Lang.NETHER))
			return "Dim-1/region";
		else if (worldName.equalsIgnoreCase(Lang.END))
			return "Dim1/region";
		else
			return null;
	}

	private static class Entity {

		String id;
		String name;
		String world;
		double x;
		double y;
		double z;
		long rx;
		long ry;
		long rz;
		String nbt;

		Entity(String id, String world, double x, double y, double z, String nbt) {
			this.id = id;
			name = Lang.getEntityName(id);
			this.world = world;
			this.x = x;
			this.y = y;
			this.z = z;
			rx = (long) (x >= 0 ? Math.ceil(x) : Math.floor(x));
			ry = (long) (y >= 0 ? Math.ceil(y) : Math.floor(y));
			rz = (long) (z >= 0 ? Math.ceil(z) : Math.floor(z));
			this.nbt = nbt;
		}

		@Override
		public String toString() {
			return "[" + name + "] " + world + " / " + x + " " + y + " " + z + ": " + nbt + "\n";
		}
	}

}
