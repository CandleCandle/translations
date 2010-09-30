package uk.me.candle.translations.example;

import java.util.Locale;
import org.junit.Test;
import uk.me.candle.translations.Bundle;
import uk.me.candle.translations.Bundle.LoadIgnoreExtra;
import uk.me.candle.translations.Bundle.LoadIgnoreMissing;
import uk.me.candle.translations.Bundle.LoadIgnoreParameterMisMatch;
import static org.junit.Assert.*;

/**
 *
 * @author Candle
 */
public class FooTest {
  @Test
  public void testFooBundle_en() throws Exception {
    // Load the bundle with the check flags enabled.
    // If there is an error, in either the abstract class
    // or the properties file has an error.
    Foo foo = Bundle.load(Foo.class, Locale.ENGLISH,
            LoadIgnoreMissing.NO,
            LoadIgnoreExtra.NO,
            LoadIgnoreParameterMisMatch.NO
            );
    assertNotNull(foo.bar());
  }
}