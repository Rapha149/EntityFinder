package de.rapha149.entityfinder;

import java.math.BigInteger;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.querz.nbt.tag.ByteArrayTag;
import net.querz.nbt.tag.ByteTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.DoubleTag;
import net.querz.nbt.tag.FloatTag;
import net.querz.nbt.tag.IntArrayTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.LongArrayTag;
import net.querz.nbt.tag.LongTag;
import net.querz.nbt.tag.ShortTag;
import net.querz.nbt.tag.StringTag;
import net.querz.nbt.tag.Tag;

public class NBTComparer {

	public static boolean checkNBT(CompoundTag entity, JsonObject nbt) {
		JsonObject entityNBT = new JsonObject();
		readNBT(entityNBT, entity);
		return compare(nbt, entityNBT);
	}

	public static String checkNBTAndReturnJson(CompoundTag entity, JsonObject nbt) {
		JsonObject entityNBT = new JsonObject();
		readNBT(entityNBT, entity);
		if (compare(nbt, entityNBT))
			return entityNBT.toString();
		return null;
	}

	private static boolean compare(JsonObject nbt1, JsonObject nbt2) {
		for (String key : nbt1.keySet()) {
			if (!nbt2.has(key))
				return false;
			if (!compareValues(nbt1.get(key), nbt2.get(key)))
				return false;
		}

		return true;
	}

	private static boolean compareValues(JsonElement value1, JsonElement value2) {
		if (value1.isJsonPrimitive() && value2.isJsonPrimitive()) {
			JsonPrimitive primitive1 = value1.getAsJsonPrimitive();
			JsonPrimitive primitive2 = value2.getAsJsonPrimitive();
			if (!primitive1.equals(primitive2)) {
				if (primitive1.isBoolean() && primitive2.isNumber() && isIntegral(primitive2)) {
					if (primitive1.getAsBoolean() != (primitive2.getAsLong() == 1))
						return false;
				} else if (primitive2.isBoolean() && primitive1.isNumber() && isIntegral(primitive1)) {
					if (primitive2.getAsBoolean() != (primitive1.getAsLong() == 1))
						return false;
				} else
					return false;
			}
		}

		else if (value1.isJsonArray() && value2.isJsonArray()) {
			JsonArray array1 = value1.getAsJsonArray();
			JsonArray array2 = value2.getAsJsonArray();

			for (int i = 0; i < array1.size(); i++) {
				boolean existing = false;
				for (int j = 0; j < array2.size(); j++) {
					if (compareValues(array1.get(i), array2.get(j))) {
						existing = true;
						break;
					}
				}
				if (!existing)
					return false;
			}
		}

		else if (value1.isJsonObject() && value2.isJsonObject()) {
			if (!compare(value1.getAsJsonObject(), value2.getAsJsonObject()))
				return false;
		}

		else if (value1.isJsonNull() && value2.isJsonNull())
			return true;

		else
			return false;

		return true;
	}

	private static boolean isIntegral(JsonPrimitive primitive) {
		if (primitive.isNumber()) {
			Number number = primitive.getAsNumber();
			return number instanceof BigInteger || number instanceof Long || number instanceof Integer
					|| number instanceof Short || number instanceof Byte;
		}
		return false;
	}

	private static void readNBT(JsonObject nbt, CompoundTag tag) {
		for (Entry<String, Tag<?>> entry : tag.entrySet()) {
			Tag<?> value = entry.getValue();
			if (value instanceof StringTag)
				nbt.addProperty(entry.getKey(), ((StringTag) value).getValue());
			else if (value instanceof IntTag)
				nbt.addProperty(entry.getKey(), ((IntTag) value).asInt());
			else if (value instanceof ByteTag)
				nbt.addProperty(entry.getKey(), ((ByteTag) value).asByte());
			else if (value instanceof ShortTag)
				nbt.addProperty(entry.getKey(), ((ShortTag) value).asShort());
			else if (value instanceof LongTag)
				nbt.addProperty(entry.getKey(), ((LongTag) value).asLong());
			else if (value instanceof DoubleTag)
				nbt.addProperty(entry.getKey(), ((DoubleTag) value).asDouble());
			else if (value instanceof FloatTag)
				nbt.addProperty(entry.getKey(), ((FloatTag) value).asFloat());
			else if (value instanceof IntArrayTag) {
				JsonArray array = new JsonArray();
				for (int i : ((IntArrayTag) value).getValue())
					array.add(i);
				nbt.add(entry.getKey(), array);
			} else if (value instanceof ByteArrayTag) {
				JsonArray array = new JsonArray();
				for (byte b : ((ByteArrayTag) value).getValue())
					array.add(b);
				nbt.add(entry.getKey(), array);
			} else if (value instanceof LongArrayTag) {
				JsonArray array = new JsonArray();
				for (long l : ((LongArrayTag) value).getValue())
					array.add(l);
				nbt.add(entry.getKey(), array);
			} else if (value instanceof ListTag<?>) {
				JsonArray array = new JsonArray();
				readNBTListTag(array, (ListTag<?>) value);
				nbt.add(entry.getKey(), array);
			} else if (value instanceof CompoundTag) {
				JsonObject nbt1 = new JsonObject();
				readNBT(nbt1, (CompoundTag) value);
				nbt.add(entry.getKey(), nbt1);
			}
		}
	}

	private static void readNBTListTag(JsonArray array, ListTag<?> list) {
		for (int i = 0; i < list.size(); i++) {
			Tag<?> tag = list.get(i);
			if (tag instanceof StringTag)
				array.add(((StringTag) tag).getValue());
			else if (tag instanceof IntTag)
				array.add(((IntTag) tag).asInt());
			else if (tag instanceof ByteTag)
				array.add(((ByteTag) tag).asByte());
			else if (tag instanceof ShortTag)
				array.add(((ShortTag) tag).asShort());
			else if (tag instanceof LongTag)
				array.add(((LongTag) tag).asLong());
			else if (tag instanceof DoubleTag)
				array.add(((DoubleTag) tag).asDouble());
			else if (tag instanceof FloatTag)
				array.add(((FloatTag) tag).asFloat());
			else if (tag instanceof IntArrayTag) {
				JsonArray array1 = new JsonArray();
				for (int i1 : ((IntArrayTag) tag).getValue())
					array.add(i1);
				array.add(array1);
			} else if (tag instanceof ByteArrayTag) {
				JsonArray array1 = new JsonArray();
				for (byte b : ((ByteArrayTag) tag).getValue())
					array.add(b);
				array.add(array1);
			} else if (tag instanceof LongArrayTag) {
				JsonArray array1 = new JsonArray();
				for (long l : ((LongArrayTag) tag).getValue())
					array.add(l);
				array.add(array1);
			} else if (tag instanceof ListTag<?>) {
				JsonArray array1 = new JsonArray();
				readNBTListTag(array1, (ListTag<?>) tag);
				array.add(array1);
			} else if (tag instanceof CompoundTag) {
				JsonObject nbt = new JsonObject();
				readNBT(nbt, (CompoundTag) tag);
				array.add(nbt);
			}
		}
	}

}
