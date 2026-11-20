// DOC L0001: Assegna il file al namespace it.tabatastudio, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio

// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import android.content.pm.ActivityInfo. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.content.pm.ActivityInfo
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.test.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.test.*
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.ui.test.junit4.createAndroidComposeRule. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.ui.test.junit4.createAndroidComposeRule
// DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import androidx.lifecycle.Lifecycle. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.lifecycle.Lifecycle
// DOC L0007: Rende disponibili le dipendenze esplicite di questa riga: import androidx.test.ext.junit.runners.AndroidJUnit4. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.test.ext.junit.runners.AndroidJUnit4
// DOC L0008: Rende disponibili le dipendenze esplicite di questa riga: import org.junit.Before. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import org.junit.Before
// DOC L0009: Rende disponibili le dipendenze esplicite di questa riga: import org.junit.After. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import org.junit.After
// DOC L0010: Rende disponibili le dipendenze esplicite di questa riga: import androidx.test.platform.app.InstrumentationRegistry. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.test.platform.app.InstrumentationRegistry
// DOC L0011: Rende disponibili le dipendenze esplicite di questa riga: import org.junit.Rule. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import org.junit.Rule
// DOC L0012: Rende disponibili le dipendenze esplicite di questa riga: import org.junit.Test. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import org.junit.Test
// DOC L0013: Rende disponibili le dipendenze esplicite di questa riga: import org.junit.runner.RunWith. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import org.junit.runner.RunWith

// DOC L0015: Applica l’annotazione dichiarata: Compose identifica UI, Room genera accessi e schema, mentre le annotazioni Test selezionano i casi da eseguire.
@RunWith(AndroidJUnit4::class)
// DOC L0016: Dichiara il tipo o contenitore di questo modulo: Prove di regressione in UiTest.kt. I campi e i metodi sono descritti nelle righe successive.
class UiTest {
    // DOC L0017: Applica l’annotazione dichiarata: Compose identifica UI, Room genera accessi e schema, mentre le annotazioni Test selezionano i casi da eseguire.
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    // DOC L0018: allowNotifications: Prepara o verifica il caso di regressione «allow Notifications»; le asserzioni nel corpo specificano l’esito atteso.
    @Before fun allowNotifications() {
        // DOC L0019: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di allowNotifications.
        if (android.os.Build.VERSION.SDK_INT >= 33) android.os.ParcelFileDescriptor.AutoCloseInputStream(InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("pm grant it.tabatastudio android.permission.POST_NOTIFICATIONS")).use { it.readBytes() }
    // DOC L0020: Chiude il costrutto aperto alla riga 18.
    }
    // DOC L0021: workoutMode: Prepara o verifica il caso di regressione «workout Mode»; le asserzioni nel corpo specificano l’esito atteso.
    @Before fun workoutMode() {
        // DOC L0022: Esegue il passaggio esplicito «compose.activityRule.scenario.onActivity { activity ->» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di workoutMode.
        compose.activityRule.scenario.onActivity { activity ->
            // DOC L0023: vm: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di workoutMode.
            val vm = androidx.lifecycle.ViewModelProvider(activity)[it.tabatastudio.ui.StudioViewModel::class.java]
            // DOC L0024: Esegue il passaggio esplicito «vm.chooseLanguage("it")» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di workoutMode.
            vm.chooseLanguage("it")
            // DOC L0025: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di workoutMode.
            if (vm.mode != "WORKOUT") vm.chooseMode(false)
            // DOC L0026: Esegue il passaggio esplicito «vm.navigate("Home")» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di workoutMode.
            vm.navigate("Home")
        // DOC L0027: Chiude il costrutto aperto alla riga 22.
        }
    // DOC L0028: Chiude il costrutto aperto alla riga 21.
    }
    // DOC L0029: finishRuntime: Prepara o verifica il caso di regressione «finish Runtime»; le asserzioni nel corpo specificano l’esito atteso.
    @After fun finishRuntime() {
        // DOC L0030: instrumentation: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di finishRuntime.
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        // DOC L0031: runtime: proprietario del motore Android per il processo, condiviso tra servizio e UI. Nel contesto di finishRuntime.
        val runtime = (instrumentation.targetContext.applicationContext as TabataApplication).timerRuntime
        // DOC L0032: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di finishRuntime.
        instrumentation.runOnMainSync { if (runtime.active) runtime.stop() }
        // DOC L0033: Esegue il passaggio esplicito «compose.waitUntil(10000) { !runtime.active && !runtime.hasUnsavedResult }» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di finishRuntime.
        compose.waitUntil(10000) { !runtime.active && !runtime.hasUnsavedResult }
        // DOC L0034: Esegue il passaggio esplicito «instrumentation.runOnMainSync { runtime.clearFinished() }» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di finishRuntime.
        instrumentation.runOnMainSync { runtime.clearFinished() }
    // DOC L0035: Chiude il costrutto aperto alla riga 29.
    }
    // DOC L0036: homeReady: Prepara o verifica il caso di regressione «home Ready»; le asserzioni nel corpo specificano l’esito atteso.
    private fun homeReady() { compose.waitUntil(20000) { compose.onAllNodesWithText("Avvia").fetchSemanticsNodes().isNotEmpty() } }
    // DOC L0037: startDemo: Prepara o verifica il caso di regressione «start Demo»; le asserzioni nel corpo specificano l’esito atteso.
    private fun startDemo() {
        // DOC L0038: Esegue il passaggio esplicito «homeReady()» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di startDemo.
        homeReady()
        // DOC L0039: Esegue il passaggio esplicito «compose.onNodeWithContentDescription("Lavoro").performTextReplacement("5")» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di startDemo.
        compose.onNodeWithContentDescription("Lavoro").performTextReplacement("5")
        // DOC L0040: Esegue il passaggio esplicito «compose.onNodeWithContentDescription("Recupero").performTextReplacement("2")» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di startDemo.
        compose.onNodeWithContentDescription("Recupero").performTextReplacement("2")
        // DOC L0041: Esegue il passaggio esplicito «compose.onNodeWithContentDescription("Giri").performTextReplacement("2")» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di startDemo.
        compose.onNodeWithContentDescription("Giri").performTextReplacement("2")
        // DOC L0042: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di startDemo.
        compose.onNodeWithText("Avvia").performScrollTo().performClick()
        // DOC L0043: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di startDemo.
        compose.onNodeWithText("Preparazione").assertExists()
        // DOC L0044: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di startDemo.
        compose.onNodeWithText("20 secondi per prepararti").assertExists()
        // DOC L0045: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di startDemo.
        compose.onNodeWithText("Salta →").performScrollTo().performClick()
    // DOC L0046: Chiude il costrutto aperto alla riga 37.
    }
    // DOC L0047: appearanceAndNumberedPresetsPersist: Prepara o verifica il caso di regressione «appearance And Numbered Presets Persist»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun appearanceAndNumberedPresetsPersist() {
        // DOC L0048: Esegue il passaggio esplicito «homeReady()» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di appearanceAndNumberedPresetsPersist.
        homeReady()
        // DOC L0049: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di appearanceAndNumberedPresetsPersist.
        for (name in listOf("Tabata 1", "Tabata 2", "Tabata 3")) compose.onNodeWithText(name).assertExists()
        // DOC L0050: Esegue il passaggio esplicito «compose.onNodeWithContentDescription("Impostazioni").performClick()» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di appearanceAndNumberedPresetsPersist.
        compose.onNodeWithContentDescription("Impostazioni").performClick()
        // DOC L0051: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di appearanceAndNumberedPresetsPersist.
        compose.onNodeWithText("Scuro").performScrollTo().performClick()
        // DOC L0052: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di appearanceAndNumberedPresetsPersist.
        compose.onNodeWithText("Giallo").performScrollTo().performClick()
        // DOC L0053: prefs: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di appearanceAndNumberedPresetsPersist.
        val prefs = compose.activity.getSharedPreferences("tabata-appearance", android.content.Context.MODE_PRIVATE)
        // DOC L0054: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Legge il valore locale e applica i controlli/fallback riportati nell’espressione. Nel contesto di appearanceAndNumberedPresetsPersist.
        org.junit.Assert.assertTrue(prefs.getBoolean("dark", false))
        // DOC L0055: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Legge il valore locale e applica i controlli/fallback riportati nell’espressione. Nel contesto di appearanceAndNumberedPresetsPersist.
        org.junit.Assert.assertEquals("yellow", prefs.getString("palette", ""))
        // DOC L0056: Esegue il passaggio esplicito «compose.activityRule.scenario.recreate()» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di appearanceAndNumberedPresetsPersist.
        compose.activityRule.scenario.recreate()
        // DOC L0057: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di appearanceAndNumberedPresetsPersist.
        compose.onNodeWithText("Scuro").assertIsSelected()
        // DOC L0058: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di appearanceAndNumberedPresetsPersist.
        compose.onNodeWithText("Giallo").performScrollTo().assertIsSelected()
        // DOC L0059: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di appearanceAndNumberedPresetsPersist.
        for (name in listOf("Viola", "Blu", "Verde chiaro", "Verde scuro", "Azzurro", "Arancione", "Giallo", "Rosso", "Rosa")) {
            // DOC L0060: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di appearanceAndNumberedPresetsPersist.
            compose.onNodeWithText(name).performScrollTo().performClick().assertIsSelected()
        // DOC L0061: Chiude il costrutto aperto alla riga 59.
        }
        // DOC L0062: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di appearanceAndNumberedPresetsPersist.
        compose.onNodeWithText("Chiaro").performScrollTo().performClick()
        // DOC L0063: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di appearanceAndNumberedPresetsPersist.
        compose.onNodeWithText("Viola").performScrollTo().performClick()
    // DOC L0064: Chiude il costrutto aperto alla riga 47.
    }
    // DOC L0065: saveNamedWorkoutAndOpenGuide: Prepara o verifica il caso di regressione «save Named Workout And Open Guide»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun saveNamedWorkoutAndOpenGuide() {
        // DOC L0066: Esegue il passaggio esplicito «homeReady()» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di saveNamedWorkoutAndOpenGuide.
        homeReady()
        // DOC L0067: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di saveNamedWorkoutAndOpenGuide.
        compose.onNodeWithText("Salva allenamento").performScrollTo().performClick()
        // DOC L0068: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di saveNamedWorkoutAndOpenGuide.
        compose.onNodeWithText("Nome dell’allenamento").performTextReplacement("Il mio circuito locale")
        // DOC L0069: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di saveNamedWorkoutAndOpenGuide.
        compose.onNodeWithText("Salva sul dispositivo").performClick()
        // DOC L0070: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di saveNamedWorkoutAndOpenGuide.
        compose.waitUntil(10000) { compose.onAllNodesWithText("Il mio circuito locale").fetchSemanticsNodes().isNotEmpty() }
        // DOC L0071: Esegue il passaggio esplicito «compose.activityRule.scenario.recreate()» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di saveNamedWorkoutAndOpenGuide.
        compose.activityRule.scenario.recreate()
        // DOC L0072: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di saveNamedWorkoutAndOpenGuide.
        compose.onNodeWithText("Il mio circuito locale").assertExists()
        // DOC L0073: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di saveNamedWorkoutAndOpenGuide.
        compose.onNodeWithText("Guida").performClick()
        // DOC L0074: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di saveNamedWorkoutAndOpenGuide.
        compose.onNodeWithText("Guida all’uso").assertExists()
        // DOC L0075: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di saveNamedWorkoutAndOpenGuide.
        compose.onNodeWithText("Per conservare un allenamento").assertExists()
        // DOC L0076: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di saveNamedWorkoutAndOpenGuide.
        compose.onNodeWithText("Workout e Studio").assertExists()
        // DOC L0077: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di saveNamedWorkoutAndOpenGuide.
        compose.onNodeWithText("Tema e Colori").assertExists()
        // DOC L0078: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di saveNamedWorkoutAndOpenGuide.
        compose.onNodeWithText("Parti in pochi tocchi").assertDoesNotExist()
        // DOC L0079: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di saveNamedWorkoutAndOpenGuide.
        compose.onNodeWithText("In Altre opzioni puoi impostare pause e serie personalizzate a tuo piacimento ed ad ogni tua necessità.").assertExists()
    // DOC L0080: Chiude il costrutto aperto alla riga 65.
    }
    // DOC L0081: demoAndRotation: Prepara o verifica il caso di regressione «demo And Rotation»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun demoAndRotation() {
        // DOC L0082: Esegue il passaggio esplicito «startDemo()» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di demoAndRotation.
        startDemo()
        // DOC L0083: Esegue il passaggio esplicito «compose.activityRule.scenario.onActivity { it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE }» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di demoAndRotation.
        compose.activityRule.scenario.onActivity { it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE }
        // DOC L0084: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di demoAndRotation.
        compose.waitUntil(25000) { compose.onAllNodesWithText("Allenamento completato.").fetchSemanticsNodes().isNotEmpty() }
        // DOC L0085: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di demoAndRotation.
        compose.onNodeWithText("Allenamento completato.").assertExists()
        // DOC L0086: Esegue il passaggio esplicito «compose.activityRule.scenario.onActivity { it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED }» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di demoAndRotation.
        compose.activityRule.scenario.onActivity { it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED }
    // DOC L0087: Chiude il costrutto aperto alla riga 81.
    }
    // DOC L0088: backgroundContinuesAndRecreationRetainsState: Prepara o verifica il caso di regressione «background Continues And Recreation Retains State»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun backgroundContinuesAndRecreationRetainsState() {
        // DOC L0089: Esegue il passaggio esplicito «startDemo()» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di backgroundContinuesAndRecreationRetainsState.
        startDemo()
        // DOC L0090: runtime: proprietario del motore Android per il processo, condiviso tra servizio e UI. Nel contesto di backgroundContinuesAndRecreationRetainsState.
        val runtime = (compose.activity.application as TabataApplication).timerRuntime
        // DOC L0091: before: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di backgroundContinuesAndRecreationRetainsState.
        val before = runtime.timer!!.elapsedMs
        // DOC L0092: Esegue il passaggio esplicito «compose.activityRule.scenario.moveToState(Lifecycle.State.CREATED)» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di backgroundContinuesAndRecreationRetainsState.
        compose.activityRule.scenario.moveToState(Lifecycle.State.CREATED)
        // DOC L0093: Esegue il passaggio esplicito «compose.waitUntil(6000) { runtime.timer!!.elapsedMs >= before + 1500 }» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di backgroundContinuesAndRecreationRetainsState.
        compose.waitUntil(6000) { runtime.timer!!.elapsedMs >= before + 1500 }
        // DOC L0094: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di backgroundContinuesAndRecreationRetainsState.
        org.junit.Assert.assertEquals("running", runtime.timer!!.state)
        // DOC L0095: Esegue il passaggio esplicito «compose.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di backgroundContinuesAndRecreationRetainsState.
        compose.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        // DOC L0096: Esegue il passaggio esplicito «compose.activityRule.scenario.recreate()» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di backgroundContinuesAndRecreationRetainsState.
        compose.activityRule.scenario.recreate()
        // DOC L0097: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di backgroundContinuesAndRecreationRetainsState.
        compose.onNodeWithText("In pausa").assertDoesNotExist()
        // DOC L0098: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di backgroundContinuesAndRecreationRetainsState.
        compose.onNodeWithText("Pausa").performScrollTo().performClick()
        // DOC L0099: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di backgroundContinuesAndRecreationRetainsState.
        compose.onNodeWithText("In pausa").assertExists()
        // DOC L0100: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di backgroundContinuesAndRecreationRetainsState.
        compose.onNodeWithText("Riprendi").performScrollTo().performClick()
        // DOC L0101: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di backgroundContinuesAndRecreationRetainsState.
        compose.onNodeWithText("Termina").performScrollTo().performClick()
        // DOC L0102: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di backgroundContinuesAndRecreationRetainsState.
        compose.onNode(hasText("Conferma") and hasClickAction()).performClick()
        // DOC L0103: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di backgroundContinuesAndRecreationRetainsState.
        compose.onNodeWithText("Sessione interrotta.").assertExists()
    // DOC L0104: Chiude il costrutto aperto alla riga 88.
    }
    // DOC L0105: quickSetupAndAdvancedEditorAreAccessible: Prepara o verifica il caso di regressione «quick Setup And Advanced Editor Are Accessible»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun quickSetupAndAdvancedEditorAreAccessible() {
        // DOC L0106: Esegue il passaggio esplicito «homeReady()» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di quickSetupAndAdvancedEditorAreAccessible.
        homeReady()
        // DOC L0107: Esegue il passaggio esplicito «compose.onNodeWithContentDescription("Lavoro").assertExists()» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di quickSetupAndAdvancedEditorAreAccessible.
        compose.onNodeWithContentDescription("Lavoro").assertExists()
        // DOC L0108: Esegue il passaggio esplicito «compose.onNodeWithContentDescription("Recupero").assertExists()» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di quickSetupAndAdvancedEditorAreAccessible.
        compose.onNodeWithContentDescription("Recupero").assertExists()
        // DOC L0109: Esegue il passaggio esplicito «compose.onNodeWithContentDescription("Giri").assertExists()» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di quickSetupAndAdvancedEditorAreAccessible.
        compose.onNodeWithContentDescription("Giri").assertExists()
        // DOC L0110: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di quickSetupAndAdvancedEditorAreAccessible.
        compose.onNodeWithText("Serie").assertDoesNotExist()
        // DOC L0111: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di quickSetupAndAdvancedEditorAreAccessible.
        compose.onNodeWithText("Allenamenti").performClick()
        // DOC L0112: Esegue il passaggio esplicito «compose.onNodeWithContentDescription("Opzioni per Tabata classico").performClick()» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di quickSetupAndAdvancedEditorAreAccessible.
        compose.onNodeWithContentDescription("Opzioni per Tabata classico").performClick()
        // DOC L0113: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di quickSetupAndAdvancedEditorAreAccessible.
        compose.onNodeWithText("Modifica").performClick()
        // DOC L0114: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di quickSetupAndAdvancedEditorAreAccessible.
        compose.onNodeWithText("Modifica allenamento").assertExists()
    // DOC L0115: Chiude il costrutto aperto alla riga 105.
    }
    // DOC L0116: studioModeMinutesSaveAndSeparation: Prepara o verifica il caso di regressione «studio Mode Minutes Save And Separation»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun studioModeMinutesSaveAndSeparation() {
        // DOC L0117: Esegue il passaggio esplicito «homeReady()» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di studioModeMinutesSaveAndSeparation.
        homeReady()
        // DOC L0118: Esegue il passaggio esplicito «compose.onNodeWithContentDescription("Modalità Studio").performClick().assertIsOn()» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di studioModeMinutesSaveAndSeparation.
        compose.onNodeWithContentDescription("Modalità Studio").performClick().assertIsOn()
        // DOC L0119: Esegue il passaggio esplicito «compose.onNodeWithContentDescription("Concentrazione · Attività 1").performTextReplacement("1")» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di studioModeMinutesSaveAndSeparation.
        compose.onNodeWithContentDescription("Concentrazione · Attività 1").performTextReplacement("1")
        // DOC L0120: Esegue il passaggio esplicito «compose.onNodeWithContentDescription("Pausa · Attività 1").assertExists()» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di studioModeMinutesSaveAndSeparation.
        compose.onNodeWithContentDescription("Pausa · Attività 1").assertExists()
        // DOC L0121: Esegue il passaggio esplicito «compose.onNodeWithContentDescription("Sessioni · Attività 1").performTextReplacement("2")» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di studioModeMinutesSaveAndSeparation.
        compose.onNodeWithContentDescription("Sessioni · Attività 1").performTextReplacement("2")
        // DOC L0122: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di studioModeMinutesSaveAndSeparation.
        compose.onNodeWithText("+ Aggiungi attività").performScrollTo().performClick()
        // DOC L0123: Esegue il passaggio esplicito «compose.onNodeWithContentDescription("Concentrazione · Attività 2").performScrollTo().performTextReplacement("3")» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di studioModeMinutesSaveAndSeparation.
        compose.onNodeWithContentDescription("Concentrazione · Attività 2").performScrollTo().performTextReplacement("3")
        // DOC L0124: Esegue il passaggio esplicito «compose.onNodeWithContentDescription("Sessioni · Attività 2").performScrollTo().performTextReplacement("1")» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di studioModeMinutesSaveAndSeparation.
        compose.onNodeWithContentDescription("Sessioni · Attività 2").performScrollTo().performTextReplacement("1")
        // DOC L0125: Esegue il passaggio esplicito «compose.onNodeWithContentDescription("Pausa tra attività").performScrollTo().performTextReplacement("4")» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di studioModeMinutesSaveAndSeparation.
        compose.onNodeWithContentDescription("Pausa tra attività").performScrollTo().performTextReplacement("4")
        // DOC L0126: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di studioModeMinutesSaveAndSeparation.
        compose.onNodeWithText("Pausa lunga").assertDoesNotExist()
        // DOC L0127: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di studioModeMinutesSaveAndSeparation.
        compose.onNodeWithText("Serie").assertDoesNotExist()
        // DOC L0128: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di studioModeMinutesSaveAndSeparation.
        compose.onNodeWithText("Blocco 1").assertDoesNotExist()
        // DOC L0129: Esegue il passaggio esplicito «compose.activityRule.scenario.onActivity { activity ->» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di studioModeMinutesSaveAndSeparation.
        compose.activityRule.scenario.onActivity { activity ->
            // DOC L0130: vm: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di studioModeMinutesSaveAndSeparation.
            val vm = androidx.lifecycle.ViewModelProvider(activity)[it.tabatastudio.ui.StudioViewModel::class.java]
            // DOC L0131: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di studioModeMinutesSaveAndSeparation.
            org.junit.Assert.assertEquals(2, vm.quickWorkout.activities!!.size)
            // DOC L0132: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di studioModeMinutesSaveAndSeparation.
            org.junit.Assert.assertEquals(240, vm.quickWorkout.restBetweenActivitiesSec)
            // DOC L0133: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di studioModeMinutesSaveAndSeparation.
            org.junit.Assert.assertEquals(840000L, it.tabatastudio.domain.plannedMs(it.tabatastudio.domain.compileTimeline(vm.quickWorkout)))
        // DOC L0134: Chiude il costrutto aperto alla riga 129.
        }
        // DOC L0135: Esegue il passaggio esplicito «compose.onNodeWithContentDescription("Sessioni · Attività 1").assertExists()» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di studioModeMinutesSaveAndSeparation.
        compose.onNodeWithContentDescription("Sessioni · Attività 1").assertExists()
        // DOC L0136: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di studioModeMinutesSaveAndSeparation.
        compose.onNodeWithText("Salva programma di studio").performScrollTo().performClick()
        // DOC L0137: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di studioModeMinutesSaveAndSeparation.
        compose.onNodeWithText("Nome del programma").performTextReplacement("Studio locale di matematica")
        // DOC L0138: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di studioModeMinutesSaveAndSeparation.
        compose.onNodeWithText("Salva sul dispositivo").performClick()
        // DOC L0139: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di studioModeMinutesSaveAndSeparation.
        compose.waitUntil(10000) { compose.onAllNodesWithText("Studio locale di matematica").fetchSemanticsNodes().isNotEmpty() }
        // DOC L0140: Esegue il passaggio esplicito «compose.activityRule.scenario.recreate()» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di studioModeMinutesSaveAndSeparation.
        compose.activityRule.scenario.recreate()
        // DOC L0141: Esegue il passaggio esplicito «compose.onNodeWithContentDescription("Modalità Studio").assertIsOn()» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di studioModeMinutesSaveAndSeparation.
        compose.onNodeWithContentDescription("Modalità Studio").assertIsOn()
        // DOC L0142: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Legge il valore locale e applica i controlli/fallback riportati nell’espressione. Nel contesto di studioModeMinutesSaveAndSeparation.
        org.junit.Assert.assertEquals("STUDIO", compose.activity.getSharedPreferences("tabata-appearance", android.content.Context.MODE_PRIVATE).getString("mode", ""))
        // DOC L0143: Esegue il passaggio esplicito «compose.onNodeWithContentDescription("Modalità Studio").performClick().assertIsOff()» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di studioModeMinutesSaveAndSeparation.
        compose.onNodeWithContentDescription("Modalità Studio").performClick().assertIsOff()
        // DOC L0144: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di studioModeMinutesSaveAndSeparation.
        compose.onNodeWithText("Allenamenti").performClick()
        // DOC L0145: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di studioModeMinutesSaveAndSeparation.
        compose.onNodeWithText("Studio locale di matematica").assertDoesNotExist()
    // DOC L0146: Chiude il costrutto aperto alla riga 116.
    }
    // DOC L0147: languagesPreserveArchiveTimerAndNotification: Prepara o verifica il caso di regressione «languages Preserve Archive Timer And Notification»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun languagesPreserveArchiveTimerAndNotification() {
        // DOC L0148: Esegue il passaggio esplicito «homeReady()» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di languagesPreserveArchiveTimerAndNotification.
        homeReady()
        // DOC L0149: Esegue il passaggio esplicito «lateinit var vm: it.tabatastudio.ui.StudioViewModel» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di languagesPreserveArchiveTimerAndNotification.
        lateinit var vm: it.tabatastudio.ui.StudioViewModel
        // DOC L0150: Esegue il passaggio esplicito «compose.activityRule.scenario.onActivity { activity -> vm = androidx.lifecycle.ViewModelProvider(activity)[it.tabatastudio.ui.StudioViewModel::class.java] }» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di languagesPreserveArchiveTimerAndNotification.
        compose.activityRule.scenario.onActivity { activity -> vm = androidx.lifecycle.ViewModelProvider(activity)[it.tabatastudio.ui.StudioViewModel::class.java] }
        // DOC L0151: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. names: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di languagesPreserveArchiveTimerAndNotification.
        val names = vm.data.workouts.map { it.id to it.title }
        // DOC L0152: Esegue il passaggio esplicito «compose.onNodeWithContentDescription("Impostazioni").performClick()» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di languagesPreserveArchiveTimerAndNotification.
        compose.onNodeWithContentDescription("Impostazioni").performClick()
        // DOC L0153: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di languagesPreserveArchiveTimerAndNotification.
        compose.onNodeWithText("English").performScrollTo().performClick()
        // DOC L0154: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di languagesPreserveArchiveTimerAndNotification.
        compose.onNodeWithText("Settings").assertExists()
        // DOC L0155: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di languagesPreserveArchiveTimerAndNotification.
        compose.onNodeWithText("Español").performScrollTo().performClick()
        // DOC L0156: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di languagesPreserveArchiveTimerAndNotification.
        compose.onNodeWithText("Ajustes").assertExists()
        // DOC L0157: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di languagesPreserveArchiveTimerAndNotification.
        compose.onNodeWithText("Idioma").assertExists()
        // DOC L0158: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di languagesPreserveArchiveTimerAndNotification.
        org.junit.Assert.assertEquals("es", vm.language)
        // DOC L0159: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di languagesPreserveArchiveTimerAndNotification.
        org.junit.Assert.assertEquals("Descanso", it.tabatastudio.i18n.AppText.text("Recupero"))
        // DOC L0160: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di languagesPreserveArchiveTimerAndNotification.
        compose.onNodeWithText("English").performScrollTo().performClick()
        // DOC L0161: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di languagesPreserveArchiveTimerAndNotification.
        compose.onNodeWithText("Timer").performClick()
        // DOC L0162: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di languagesPreserveArchiveTimerAndNotification.
        compose.onNodeWithText("Start").performScrollTo().performClick()
        // DOC L0163: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di languagesPreserveArchiveTimerAndNotification.
        compose.onNodeWithText("Preparation").assertExists()
        // DOC L0164: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di languagesPreserveArchiveTimerAndNotification.
        compose.onNodeWithText("Pause").assertExists()
        // DOC L0165: session: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di languagesPreserveArchiveTimerAndNotification.
        val session = vm.timer!!.sessionId
        // DOC L0166: runtime: proprietario del motore Android per il processo, condiviso tra servizio e UI. Nel contesto di languagesPreserveArchiveTimerAndNotification.
        val runtime = (compose.activity.application as TabataApplication).timerRuntime
        // DOC L0167: Esegue il passaggio esplicito «compose.activityRule.scenario.onActivity { vm.chooseLanguage("es") }» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di languagesPreserveArchiveTimerAndNotification.
        compose.activityRule.scenario.onActivity { vm.chooseLanguage("es") }
        // DOC L0168: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di languagesPreserveArchiveTimerAndNotification.
        compose.onNodeWithText("Preparación").assertExists()
        // DOC L0169: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di languagesPreserveArchiveTimerAndNotification.
        org.junit.Assert.assertEquals(session, vm.timer!!.sessionId)
        // DOC L0170: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di languagesPreserveArchiveTimerAndNotification.
        org.junit.Assert.assertEquals(names, vm.data.workouts.map { it.id to it.title })
        // DOC L0171: manager: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di languagesPreserveArchiveTimerAndNotification.
        val manager = compose.activity.getSystemService(android.app.NotificationManager::class.java)
        // DOC L0172: Esegue il passaggio esplicito «compose.waitUntil(10000) { manager.activeNotifications.any { it.notification.actions?.any { a -> a.title.toString() == "Finalizar" } == true } }» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di languagesPreserveArchiveTimerAndNotification.
        compose.waitUntil(10000) { manager.activeNotifications.any { it.notification.actions?.any { a -> a.title.toString() == "Finalizar" } == true } }
        // DOC L0173: Esegue il passaggio esplicito «compose.activityRule.scenario.recreate()» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di languagesPreserveArchiveTimerAndNotification.
        compose.activityRule.scenario.recreate()
        // DOC L0174: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di languagesPreserveArchiveTimerAndNotification.
        compose.onNodeWithText("Preparación").assertExists()
        // DOC L0175: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di languagesPreserveArchiveTimerAndNotification.
        org.junit.Assert.assertEquals("es", vm.language)
        // DOC L0176: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di languagesPreserveArchiveTimerAndNotification.
        org.junit.Assert.assertEquals(session, runtime.timer!!.sessionId)
        // DOC L0177: Esegue il passaggio esplicito «compose.activityRule.scenario.onActivity { vm.chooseLanguage("it") }» nel flusso: Prove di regressione in UiTest.kt. Nel contesto di languagesPreserveArchiveTimerAndNotification.
        compose.activityRule.scenario.onActivity { vm.chooseLanguage("it") }
    // DOC L0178: Chiude il costrutto aperto alla riga 147.
    }

// DOC L0180: Chiude il costrutto aperto alla riga 16.
}
