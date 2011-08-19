package uk.me.candle.translations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Allows class definition from a byte array.
 */
public class BundleClassLoader extends ClassLoader {
	Set<Class<?>> classes = new HashSet<Class<?>>();
	private Map<String, Class<? extends Bundle>> classMap
			= new HashMap<String, Class<? extends Bundle>>();

	BundleClassLoader() { }

	BundleClassLoader(ClassLoader parent) {
		super(parent);
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
