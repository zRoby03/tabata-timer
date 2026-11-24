package it.tabatastudio

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import it.tabatastudio.ui.HornSignals
import it.tabatastudio.domain.cueDurationMs
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HornTest {
    @get:Rule val activityRule = ActivityScenarioRule(MainActivity::class.java)
    private fun main(action: () -> Unit) = InstrumentationRegistry.getInstrumentation().runOnMainSync(action)
    private fun ready(horn: HornSignals) {
        val deadline=android.os.SystemClock.elapsedRealtime()+10000
        while(!horn.isReady && android.os.SystemClock.elapsedRealtime()<deadline)Thread.sleep(20)
        assertTrue("Set di 13 WAV non caricato",horn.isReady)
    }
    @Test fun localNotesAndAllTransitionSamplesLoadPlayAndRelease() {
        lateinit var horn: HornSignals
        main { horn=HornSignals(ApplicationProvider.getApplicationContext()) }
        try {
            ready(horn)
            val keys=listOf("work_3","work_2","work_1","rest_3","rest_2","rest_1","whistle","sigh","finish","ding1","ding2","ding3","double_sigh")
            for(key in keys){
                var stream=0
                main { stream=horn.playCue(key) }
                assertTrue("Campione non avviato: $key",stream>0)
                Thread.sleep(cueDurationMs(key)+150)
                assertFalse("Focus non rilasciato: $key",horn.isFocusHeld)
            }
            assertEquals(0,horn.play(0,"WORK"))
        } finally {main {horn.release()}}
    }
    @Test fun countdownFocusPersistsAcrossSilenceAndStopCancelsLease() {
        lateinit var horn: HornSignals
        main {horn=HornSignals(ApplicationProvider.getApplicationContext())}
        try {
            ready(horn)
            var stream=0
            main {stream=horn.playCue("rest_3",4485)}
            assertTrue(stream>0);Thread.sleep(250)
            assertTrue(horn.isFocusHeld)
            main {horn.stop()};assertFalse(horn.isFocusHeld)
            main {stream=horn.playCue("ding3")};assertTrue(stream>0);assertTrue(horn.isFocusHeld)
            Thread.sleep(2150);assertFalse(horn.isFocusHeld);assertEquals(0L,horn.remainingPlaybackMs)
        } finally {main {horn.release()}}
    }
}
