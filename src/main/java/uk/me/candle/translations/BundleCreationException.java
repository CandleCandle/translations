package uk.me.candle.translations;

/**
 *
 * @author Andrew
 */
public class BundleCreationException extends Exception {
	private static final long serialVersionUID = 1457853680L;
	public BundleCreationException(String message, Throwable cause) {
		super(message, cause);
	}
	public BundleCreationException(String message) {
		super(message);
	}
}
