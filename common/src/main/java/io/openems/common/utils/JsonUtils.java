package io.openems.common.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map.Entry;
import java.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import io.openems.common.exceptions.OpenemsException;

public class JsonUtils {
	public static JsonArray getAsJsonArray(JsonElement jElement) throws OpenemsException {
		if (!jElement.isJsonArray()) {
			throw new OpenemsException("This is not a JsonArray: " + jElement);
		}
		return jElement.getAsJsonArray();
	};

	public static JsonArray getAsJsonArray(JsonElement jElement, String memberName) throws OpenemsException {
		JsonElement jSubElement = getSubElement(jElement, memberName);
		if (!jSubElement.isJsonArray()) {
			throw new OpenemsException("Element [" + memberName + "] is not a JsonArray: " + jSubElement);
		}
		return jSubElement.getAsJsonArray();
	};

	public static Optional<JsonArray> getAsOptionalJsonArray(JsonElement jElement, String memberName) {
		try {
			return Optional.of(getAsJsonArray(jElement, memberName));
		} catch (OpenemsException e) {
			return Optional.empty();
		}
	}
	
	public static JsonObject getAsJsonObject(JsonElement jElement) throws OpenemsException {
		if (!jElement.isJsonObject()) {
			throw new OpenemsException("This is not a JsonObject: " + jElement);
		}
		return jElement.getAsJsonObject();
	};

	public static JsonObject getAsJsonObject(JsonElement jElement, String memberName) throws OpenemsException {
		JsonElement jsubElement = getSubElement(jElement, memberName);
		if (!jsubElement.isJsonObject()) {
			throw new OpenemsException("Element [" + memberName + "] is not a JsonObject: " + jsubElement);
		}
		return jsubElement.getAsJsonObject();
	};

	public static Optional<JsonObject> getAsOptionalJsonObject(JsonElement jElement, String memberName) {
		try {
			return Optional.of(getAsJsonObject(jElement, memberName));
		} catch (OpenemsException e) {
			return Optional.empty();
		}
	}
	
	public static JsonPrimitive getAsPrimitive(JsonElement jElement, String memberName) throws OpenemsException {
		JsonElement jSubElement = getSubElement(jElement, memberName);
		return getAsPrimitive(jSubElement);
	}

	public static JsonPrimitive getAsPrimitive(JsonElement jElement) throws OpenemsException {
		if (!jElement.isJsonPrimitive()) {
			throw new OpenemsException("This is not a JsonPrimitive: " + jElement);
		}
		return jElement.getAsJsonPrimitive();
	}

	public static String getAsString(JsonElement jElement) throws OpenemsException {
		JsonPrimitive jPrimitive = getAsPrimitive(jElement);
		if (!jPrimitive.isString()) {
			throw new OpenemsException("This is not a String: " + jPrimitive);
		}
		return jPrimitive.getAsString();
	}

	public static boolean getAsBoolean(JsonElement jElement) throws OpenemsException {
		JsonPrimitive jPrimitive = getAsPrimitive(jElement);
		if (!jPrimitive.isBoolean()) {
			throw new OpenemsException("This is not a Boolean: " + jPrimitive);
		}
		return jPrimitive.getAsBoolean();
	}

	public static String getAsString(JsonElement jElement, String memberName) throws OpenemsException {
		JsonPrimitive jPrimitive = getAsPrimitive(jElement, memberName);
		if (!jPrimitive.isString()) {
			throw new OpenemsException("Element [" + memberName + "] is not a String: " + jPrimitive);
		}
		return jPrimitive.getAsString();
	}

	public static int getAsInt(JsonElement jElement, String memberName) throws OpenemsException {
		JsonPrimitive jPrimitive = getAsPrimitive(jElement, memberName);
		if (jPrimitive.isNumber()) {
			return jPrimitive.getAsInt();
		} else if (jPrimitive.isString()) {
			String string = jPrimitive.getAsString();
			return Integer.parseInt(string);
		}
		throw new OpenemsException("Element [" + memberName + "] is not an Integer: " + jPrimitive);
	}

	public static boolean getAsBoolean(JsonElement jElement, String memberName) throws OpenemsException {
		JsonPrimitive jPrimitive = getAsPrimitive(jElement, memberName);
		if (!jPrimitive.isBoolean()) {
			throw new OpenemsException("Element [" + memberName + "] is not a Boolean: " + jPrimitive);
		}
		return jPrimitive.getAsBoolean();
	}

	public static ZonedDateTime getAsZonedDateTime(JsonElement jElement, String memberName, ZoneId timezone)
			throws OpenemsException {
		String[] date = JsonUtils.getAsString(jElement, memberName).split("-");
		try {
			int year = Integer.valueOf(date[0]);
			int month = Integer.valueOf(date[1]);
			int day = Integer.valueOf(date[2]);
			return ZonedDateTime.of(year, month, day, 0, 0, 0, 0, timezone);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new OpenemsException("Element [" + memberName + "] is not a Date: " + jElement + ". Error: " + e);
		}
	}

	public static JsonElement getSubElement(JsonElement jElement, String memberName) throws OpenemsException {
		JsonObject jObject = getAsJsonObject(jElement);
		if (!jObject.has(memberName)) {
			throw new OpenemsException("Element [" + memberName + "] is not a Subelement of: " + jElement);
		}
		return jObject.get(memberName);
	}

	/**
	 * Merges the second Object into the first object
	 * 
	 * @param j1
	 * @param j2
	 * @return
	 */
	public static JsonObject merge(JsonObject j1, JsonObject j2) {
		// TODO be smarter: merge down the tree
		for(Entry<String, JsonElement> entry : j2.entrySet()) {
			j1.add(entry.getKey(), entry.getValue());
		}
		return j1;
	}
	
	public static Optional<JsonObject> merge(Optional<JsonObject> j1Opt, Optional<JsonObject> j2Opt) {
		if(j1Opt.isPresent() && j2Opt.isPresent()) {
			return Optional.of(JsonUtils.merge(j1Opt.get(), j2Opt.get()));
		}
		if(j1Opt.isPresent()) {
			return j1Opt;
		}
		return j2Opt;
	}
}
