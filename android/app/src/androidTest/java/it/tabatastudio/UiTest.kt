package it.tabatastudio

import android.content.pm.ActivityInfo
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.After
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UiTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    @Before fun allowNotifications() {
        if (android.os.Build.VERSION.SDK_INT >= 33) android.os.ParcelFileDescriptor.AutoCloseInputStream(InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("pm grant it.tabatastudio android.permission.POST_NOTIFICATIONS")).use { it.readBytes() }
    }
    @Before fun workoutMode() {
        compose.activityRule.scenario.onActivity { activity ->
            val vm = androidx.lifecycle.ViewModelProvider(activity)[it.tabatastudio.ui.StudioViewModel::class.java]
            vm.chooseLanguage("it")
            if (vm.mode != "WORKOUT") vm.chooseMode(false)
            vm.navigate("Home")
        }
    }
    @After fun finishRuntime() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val runtime = (instrumentation.targetContext.applicationContext as TabataApplication).timerRuntime
        instrumentation.runOnMainSync { if (runtime.active) runtime.stop() }
        compose.waitUntil(10000) { !runtime.active && !runtime.hasUnsavedResult }
        instrumentation.runOnMainSync { runtime.clearFinished() }
    }
    private fun homeReady() { compose.waitUntil(20000) { compose.onAllNodesWithText("Avvia").fetchSemanticsNodes().isNotEmpty() } }
    private fun startDemo() {
        homeReady()
        compose.onNodeWithContentDescription("Lavoro").performTextReplacement("5")
        compose.onNodeWithContentDescription("Recupero").performTextReplacement("5")
        compose.onNodeWithContentDescription("Giri").performTextReplacement("2")
        compose.onNodeWithText("Avvia").performScrollTo().performClick()
        compose.onNodeWithText("Preparazione").assertExists()
        compose.onNodeWithText("20 secondi per prepararti").assertExists()
        compose.onNodeWithText("Salta →").performScrollTo().performClick()
    }
    @Test fun appearanceAndNumberedPresetsPersist() {
        homeReady()
        for (name in listOf("Tabata 1", "Tabata 2", "Tabata 3")) compose.onNodeWithText(name).assertExists()
        compose.onNodeWithContentDescription("Impostazioni").performClick()
        compose.onNodeWithText("Scuro").performScrollTo().performClick()
        compose.onNodeWithText("Giallo").performScrollTo().performClick()
        val prefs = compose.activity.getSharedPreferences("tabata-appearance", android.content.Context.MODE_PRIVATE)
        org.junit.Assert.assertTrue(prefs.getBoolean("dark", false))
        org.junit.Assert.assertEquals("yellow", prefs.getString("palette", ""))
        compose.activityRule.scenario.recreate()
        compose.onNodeWithText("Scuro").assertIsSelected()
        compose.onNodeWithText("Giallo").performScrollTo().assertIsSelected()
        for (name in listOf("Viola", "Blu", "Verde chiaro", "Verde scuro", "Azzurro", "Arancione", "Giallo", "Rosso", "Rosa")) {
            compose.onNodeWithText(name).performScrollTo().performClick().assertIsSelected()
        }
        compose.onNodeWithText("Chiaro").performScrollTo().performClick()
        compose.onNodeWithText("Viola").performScrollTo().performClick()
    }
    @Test fun saveNamedWorkoutAndOpenGuide() {
        homeReady()
        compose.onNodeWithText("Salva allenamento").performScrollTo().performClick()
        compose.onNodeWithText("Nome dell’allenamento").performTextReplacement("Il mio circuito locale")
        compose.onNodeWithText("Salva sul dispositivo").performClick()
        compose.waitUntil(10000) { compose.onAllNodesWithText("Il mio circuito locale").fetchSemanticsNodes().isNotEmpty() }
        compose.activityRule.scenario.recreate()
        compose.onNodeWithText("Il mio circuito locale").assertExists()
        compose.onNodeWithText("Guida").performClick()
        compose.onNodeWithText("Guida all’uso").assertExists()
        compose.onNodeWithText("Per conservare un allenamento").assertExists()
        compose.onNodeWithText("Workout e Studio").assertExists()
        compose.onNodeWithText("Tema e Colori").assertExists()
        compose.onNodeWithText("Parti in pochi tocchi").assertDoesNotExist()
        compose.onNodeWithText("Un ding: inizio del lavoro ordinario.", substring = true).assertExists()
    }
    @Test fun demoAndRotation() {
        startDemo()
        compose.activityRule.scenario.onActivity { it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE }
        compose.waitUntil(25000) { compose.onAllNodesWithText("Allenamento completato.").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Allenamento completato.").assertExists()
        compose.activityRule.scenario.onActivity { it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED }
    }
    @Test fun backgroundContinuesAndRecreationRetainsState() {
        startDemo()
        val runtime = (compose.activity.application as TabataApplication).timerRuntime
        val before = runtime.timer!!.elapsedMs
        compose.activityRule.scenario.moveToState(Lifecycle.State.CREATED)
        compose.waitUntil(6000) { runtime.timer!!.elapsedMs >= before + 1500 }
        org.junit.Assert.assertEquals("running", runtime.timer!!.state)
        compose.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        compose.activityRule.scenario.recreate()
        compose.onNodeWithText("In pausa").assertDoesNotExist()
        compose.onNodeWithText("Pausa").performScrollTo().performClick()
        compose.onNodeWithText("In pausa").assertExists()
        compose.onNodeWithText("Riprendi").performScrollTo().performClick()
        compose.onNodeWithText("Termina").performScrollTo().performClick()
        compose.onNode(hasText("Conferma") and hasClickAction()).performClick()
        compose.onNodeWithText("Sessione interrotta.").assertExists()
    }
    @Test fun quickSetupAndAdvancedEditorAreAccessible() {
        homeReady()
        compose.onNodeWithContentDescription("Lavoro").assertExists()
        compose.onNodeWithContentDescription("Recupero").assertExists()
        compose.onNodeWithContentDescription("Giri").assertExists()
        compose.onNodeWithText("Serie").assertDoesNotExist()
        compose.onNodeWithText("Allenamenti").performClick()
        compose.onNodeWithContentDescription("Opzioni per Tabata classico").performClick()
        compose.onNodeWithText("Modifica").performClick()
        compose.onNodeWithText("Modifica allenamento").assertExists()
    }
    @Test fun personalRecoveriesAndBlockPreview() {
        homeReady()
        lateinit var vm: it.tabatastudio.ui.StudioViewModel
        compose.activityRule.scenario.onActivity { activity ->
            vm = androidx.lifecycle.ViewModelProvider(activity)[it.tabatastudio.ui.StudioViewModel::class.java]
            val blocks = (1..3).map { n -> it.tabatastudio.domain.Block(
                id = "test_block_$n", title = "Circuito $n", cycles = if (n == 1) 2 else 1,
                restAfterBlockSec = n * 5, exercises = listOf(it.tabatastudio.domain.Exercise(
                    id = "test_ex_$n", title = "Movimento $n", workSec = 5, restBetweenRepsSec = 5))) }
            vm.edit(it.tabatastudio.domain.Workout(id = "test_editor", title = "Prova recuperi", timingVersion = 2, blocks = blocks))
        }
        compose.onAllNodesWithText("Recupero tra blocchi").assertCountEquals(2)
        compose.onAllNodesWithText("Recupero").assertCountEquals(3)
        compose.onNodeWithText("Recupero tra cicli").assertDoesNotExist()
        compose.onNodeWithText("Recupero tra esercizi").assertDoesNotExist()
        compose.onAllNodesWithText("↑ Blocco")[2].performScrollTo().performClick()
        org.junit.Assert.assertEquals(listOf(5, 15, 10), vm.draft!!.blocks.map { it.restAfterBlockSec })
        compose.onAllNodesWithText("Recupero tra blocchi").assertCountEquals(2)
        compose.onNodeWithText("Ciclo 2/2").performScrollTo().assertExists()
        compose.onNodeWithText("Recupero Movimento 1").performScrollTo().assertExists()
        compose.onNodeWithText("Recupero Blocco 1 → 2").performScrollTo().assertExists()
        compose.onNodeWithText("Due ding — inizio ciclo 2").performScrollTo().assertExists()
        compose.onNodeWithText("Tre ding — fine lavoro").performScrollTo().assertExists()
    }
    @Test fun studioModeMinutesSaveAndSeparation() {
        homeReady()
        compose.onNodeWithContentDescription("Modalità Studio").performClick().assertIsOn()
        compose.onNodeWithContentDescription("Concentrazione · Attività 1").performTextReplacement("1")
        compose.onNodeWithContentDescription("Pausa · Attività 1").assertExists()
        compose.onNodeWithContentDescription("Sessioni · Attività 1").performTextReplacement("2")
        compose.onNodeWithText("+ Aggiungi attività").performScrollTo().performClick()
        compose.onNodeWithContentDescription("Concentrazione · Attività 2").performScrollTo().performTextReplacement("3")
        compose.onNodeWithContentDescription("Sessioni · Attività 2").performScrollTo().performTextReplacement("1")
        compose.onNodeWithContentDescription("Pausa tra attività").performScrollTo().performTextReplacement("4")
        compose.onNodeWithText("Pausa lunga").assertDoesNotExist()
        compose.onNodeWithText("Serie").assertDoesNotExist()
        compose.onNodeWithText("Blocco 1").assertDoesNotExist()
        compose.activityRule.scenario.onActivity { activity ->
            val vm = androidx.lifecycle.ViewModelProvider(activity)[it.tabatastudio.ui.StudioViewModel::class.java]
            org.junit.Assert.assertEquals(2, vm.quickWorkout.activities!!.size)
            org.junit.Assert.assertEquals(240, vm.quickWorkout.restBetweenActivitiesSec)
            org.junit.Assert.assertEquals(840000L, it.tabatastudio.domain.plannedMs(it.tabatastudio.domain.compileTimeline(vm.quickWorkout)))
        }
        compose.onNodeWithContentDescription("Sessioni · Attività 1").assertExists()
        compose.onNodeWithText("Salva programma di studio").performScrollTo().performClick()
        compose.onNodeWithText("Nome del programma").performTextReplacement("Studio locale di matematica")
        compose.onNodeWithText("Salva sul dispositivo").performClick()
        compose.waitUntil(10000) { compose.onAllNodesWithText("Studio locale di matematica").fetchSemanticsNodes().isNotEmpty() }
        compose.activityRule.scenario.recreate()
        compose.onNodeWithContentDescription("Modalità Studio").assertIsOn()
        org.junit.Assert.assertEquals("STUDIO", compose.activity.getSharedPreferences("tabata-appearance", android.content.Context.MODE_PRIVATE).getString("mode", ""))
        compose.onNodeWithContentDescription("Modalità Studio").performClick().assertIsOff()
        compose.onNodeWithText("Allenamenti").performClick()
        compose.onNodeWithText("Studio locale di matematica").assertDoesNotExist()
    }
    @Test fun studioSubtitleIsExactInAllLanguages() {
        homeReady()
        compose.onNodeWithContentDescription("Modalità Studio").performClick()
        compose.onNodeWithText("Timer Pomodoro. Imposta i tempi e premi Avvia.").assertExists()
        compose.activityRule.scenario.onActivity { activity ->
            androidx.lifecycle.ViewModelProvider(activity)[it.tabatastudio.ui.StudioViewModel::class.java].chooseLanguage("en")
        }
        compose.onNodeWithText("Pomodoro timer. Set your times and press Start.").assertExists()
        compose.activityRule.scenario.onActivity { activity ->
            androidx.lifecycle.ViewModelProvider(activity)[it.tabatastudio.ui.StudioViewModel::class.java].chooseLanguage("es")
        }
        compose.onNodeWithText("Temporizador Pomodoro. Configura los tiempos y pulsa Iniciar.").assertExists()
    }
    @Test fun languagesPreserveArchiveTimerAndNotification() {
        homeReady()
        lateinit var vm: it.tabatastudio.ui.StudioViewModel
        compose.activityRule.scenario.onActivity { activity -> vm = androidx.lifecycle.ViewModelProvider(activity)[it.tabatastudio.ui.StudioViewModel::class.java] }
        val names = vm.data.workouts.map { it.id to it.title }
        compose.onNodeWithContentDescription("Impostazioni").performClick()
        compose.onNodeWithText("English").performScrollTo().performClick()
        compose.onNodeWithText("Settings").assertExists()
        compose.onNodeWithText("Español").performScrollTo().performClick()
        compose.onNodeWithText("Ajustes").assertExists()
        compose.onNodeWithText("Idioma").assertExists()
        org.junit.Assert.assertEquals("es", vm.language)
        org.junit.Assert.assertEquals("Descanso", it.tabatastudio.i18n.AppText.text("Recupero"))
        compose.onNodeWithText("English").performScrollTo().performClick()
        compose.onNodeWithText("Timer").performClick()
        compose.onNodeWithText("Start").performScrollTo().performClick()
        compose.onNodeWithText("Preparation").assertExists()
        compose.onNodeWithText("Pause").assertExists()
        val session = vm.timer!!.sessionId
        val runtime = (compose.activity.application as TabataApplication).timerRuntime
        compose.activityRule.scenario.onActivity { vm.chooseLanguage("es") }
        compose.onNodeWithText("Preparación").assertExists()
        org.junit.Assert.assertEquals(session, vm.timer!!.sessionId)
        org.junit.Assert.assertEquals(names, vm.data.workouts.map { it.id to it.title })
        val manager = compose.activity.getSystemService(android.app.NotificationManager::class.java)
        compose.waitUntil(10000) { manager.activeNotifications.any { it.notification.actions?.any { a -> a.title.toString() == "Finalizar" } == true } }
        compose.activityRule.scenario.recreate()
        compose.onNodeWithText("Preparación").assertExists()
        org.junit.Assert.assertEquals("es", vm.language)
        org.junit.Assert.assertEquals(session, runtime.timer!!.sessionId)
        compose.activityRule.scenario.onActivity { vm.chooseLanguage("it") }
    }

}
