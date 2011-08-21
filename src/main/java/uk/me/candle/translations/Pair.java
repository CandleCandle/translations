package uk.me.candle.translations;

import com.google.common.base.Objects;

/**
 *
 * @author andrew
 */
public class Pair<A, B> {
	private final A a;
	private final B b;

	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}

	public A getA() {
		return a;
	}

	public B getB() {
		return b;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		@SuppressWarnings("unchecked") // if the class are not equal, then the above 'if' will
		// result in the method returning.
		final Pair<A, B> other = (Pair<A, B>) obj;
		if (this.a != other.a && (this.a == null || !this.a.equals(other.a))) {
			return false;
		}
		if (this.b != other.b && (this.b == null || !this.b.equals(other.b))) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(a, b);
	}
}
