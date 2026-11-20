package it.tabatastudio

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import it.tabatastudio.ui.HornSignals
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HornTest {
    @Test fun localNotesLoadAndPlayOncePerSecond() = runBlocking {
        val horn = HornSignals(ApplicationProvider.getApplicationContext())
        try {
            repeat(100) { if (!horn.isReady) delay(20) }
            assertTrue("Campione WAV locale non caricato", horn.isReady)
            for (phase in listOf("WORK", "COOLDOWN")) for (second in listOf(3, 2, 1)) {
                assertTrue("Riproduzione trombetta non avviata", horn.play(second, phase) > 0)
                delay(1050)
            }
            for (key in listOf("whistle", "sigh", "finish")) {
                assertTrue("Suono di cambio turno non avviato", horn.playCue(key) > 0)
                delay(1050)
            }
            assertEquals(0, horn.play(0, "WORK"))
        } finally { horn.release() }
    }
}
