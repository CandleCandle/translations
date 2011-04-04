package uk.me.candle.translations;

/**
 * Allows class definition from a byte array.
 */
class BundleClassLoader extends ClassLoader {
	public Class<?> defineClass(String name, byte[] b) {
		return defineClass(name, b, 0, b.length);
	}
}
