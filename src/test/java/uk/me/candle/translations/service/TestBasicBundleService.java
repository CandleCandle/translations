package uk.me.candle.translations.service;

import java.util.Locale;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.me.candle.translations.conf.DefaultBundleConfiguration;
import uk.me.candle.translations.maker.SimpleSmallBundle;

/**
 *
 * @author Andrew
 */
public class TestBasicBundleService {
	@Test
	public void testInitial() throws Exception {
		BasicBundleService bbs = new BasicBundleService(new DefaultBundleConfiguration(), Locale.ENGLISH);
		assertEquals("simple", bbs.get(SimpleSmallBundle.class).simple());
	}
	@Test
	public void testUseDefault() throws Exception {
		Locale.setDefault(Locale.ENGLISH);
		BasicBundleService bbs = new BasicBundleService(new DefaultBundleConfiguration());
		assertEquals("simple", bbs.get(SimpleSmallBundle.class).simple());
	}
	@Test
	public void testUseOtherBundle() throws Exception {
		Locale.setDefault(Locale.ENGLISH);
		BasicBundleService bbs = new BasicBundleService(new DefaultBundleConfiguration());
		assertEquals("de simple", bbs.get(SimpleSmallBundle.class, Locale.GERMAN).simple());
	}
}
