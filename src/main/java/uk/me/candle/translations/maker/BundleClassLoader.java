package uk.me.candle.translations.maker;

import java.util.HashMap;
import java.util.Map;
import uk.me.candle.translations.Bundle;

/**
 * Allows class definition from a byte array.
 */
public final class BundleClassLoader extends ClassLoader {
	private Map<String, Class<? extends Bundle>> classMap
			= new HashMap<String, Class<? extends Bundle>>();

	public BundleClassLoader() { }

	public boolean isClassDefined(String key) {
		return classMap.containsKey(key);
	}

	@SuppressWarnings("unchecked")
	Class<?> defineClass(String name, byte[] b) {
		if(classMap.containsKey(name)) {
			return classMap.get(name);
		}
		Class<?> clz = defineClass(name, b, 0, b.length);
		classMap.put(name, (Class<? extends Bundle>)clz);
		return clz;
	}
}
