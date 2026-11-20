package it.tabatastudio

import it.tabatastudio.i18n.AppText
import org.junit.Assert.*
import org.junit.Test

class LocaleTest {
    @Test fun deviceLanguageAndPreference() {
        assertEquals("en", AppText.initialLanguage(null, "en-US"))
        assertEquals("es", AppText.initialLanguage(null, "es-MX"))
        assertEquals("it", AppText.initialLanguage(null, "it-IT"))
        assertEquals("es", AppText.initialLanguage("es", "en-US"))
    }
    @Test fun unsupportedLanguageFallsBack() {
        assertEquals("it", AppText.initialLanguage(null, "de-DE"))
        assertEquals("en", AppText.initialLanguage("unknown", "en-US"))
    }
}
