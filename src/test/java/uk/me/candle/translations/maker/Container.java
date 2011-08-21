package uk.me.candle.translations;

/**
 *
 * @author Andrew Wheat
 */
public class Container {
	private final String s;
	private final int i;

	public Container(String s, int i) {
		this.s = s;
		this.i = i;
	}

	@Override
	public String toString() {
		return "Container{" + "s=" + s + " i=" + i + '}';
	}
}
