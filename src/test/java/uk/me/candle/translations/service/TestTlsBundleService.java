package uk.me.candle.translations.service;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.me.candle.translations.conf.DefaultBundleConfiguration;
import uk.me.candle.translations.maker.SimpleSmallBundle;
import uk.me.candle.translations.maker.TranslationBundle;

/**
 *
 * @author Andrew
 */
public class TestTlsBundleService {
	@Test
	public void testInitial() throws Exception {
		TlsBundleService bbs = new TlsBundleService(new DefaultBundleConfiguration(), Locale.ENGLISH);
		assertEquals("simple", bbs.get(SimpleSmallBundle.class).simple());
	}
	@Test
	public void testUseDefault() throws Exception {
		Locale.setDefault(Locale.ENGLISH);
		TlsBundleService bbs = new TlsBundleService(new DefaultBundleConfiguration());
		assertEquals("simple", bbs.get(SimpleSmallBundle.class).simple());
	}
	@Test
	public void testUseAllDefaults() throws Exception {
		Locale.setDefault(Locale.ENGLISH);
		TlsBundleService bbs = new TlsBundleService();
		assertEquals("simple", bbs.get(SimpleSmallBundle.class).simple());
	}
	@Test
	public void testUseOtherBundle() throws Exception {
		Locale.setDefault(Locale.ENGLISH);
		TlsBundleService bbs = new TlsBundleService(new DefaultBundleConfiguration());
		assertEquals("de simple", bbs.get(SimpleSmallBundle.class, Locale.GERMAN).simple());
	}
	@Test
	public void testUseTlsBundle() throws Exception {
		Locale.setDefault(Locale.ENGLISH);
		TlsBundleService bbs = new TlsBundleService(new DefaultBundleConfiguration());
		AtomicInteger integer = new AtomicInteger(5);
		Thread t = new Thread(new Runner(integer, bbs));
		t.start();
		assertEquals("simple", bbs.get(SimpleSmallBundle.class).simple());
		t.join();
		assertEquals(42, integer.get());
	}

	private static class Runner implements Runnable {
		private final AtomicInteger integer;
		private final TlsBundleService bbs;
		Runner(AtomicInteger integer, TlsBundleService bbs) {
			this.integer = integer;
			this.bbs = bbs;
		}
		@Override
		public void run() {
			bbs.setThreadLocale(Locale.GERMAN);
			assertEquals("de simple", bbs.get(SimpleSmallBundle.class, Locale.GERMAN).simple());
			integer.set(42);
		}
		
	}
}
