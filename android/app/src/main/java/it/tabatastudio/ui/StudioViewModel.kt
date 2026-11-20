// DOC L0001: Assegna il file al namespace it.tabatastudio.ui, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio.ui

// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import android.app.Application. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.app.Application
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.runtime.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.runtime.*
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import androidx.lifecycle.AndroidViewModel. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.lifecycle.AndroidViewModel
// DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import androidx.lifecycle.viewModelScope. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.lifecycle.viewModelScope
// DOC L0007: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.TabataApplication. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.TabataApplication
// DOC L0008: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.data.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.data.*
// DOC L0009: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.domain.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.domain.*
// DOC L0010: Rende disponibili le dipendenze esplicite di questa riga: import kotlinx.coroutines.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import kotlinx.coroutines.*

// DOC L0012: Dichiara il tipo o contenitore di questo modulo: Coordinamento dello stato della UI Android. I campi e i metodi sono descritti nelle righe successive.
class StudioViewModel(application: Application) : AndroidViewModel(application) {
    // DOC L0013: language: lingua corrente, mantenuta separata dall’archivio.
    val language: String get() = it.tabatastudio.i18n.AppText.language
    // DOC L0014: chooseLanguage: Salva la lingua in AppText e aggiorna immediatamente anche le risorse della notifica attiva.
    fun chooseLanguage(value: String) { it.tabatastudio.i18n.AppText.choose(value); it.tabatastudio.timer.TimerService.refreshResources() }
    // DOC L0015: runtime: proprietario del motore Android per il processo, condiviso tra servizio e UI. Nel contesto di chooseLanguage.
    private val runtime = (application as TabataApplication).timerRuntime
    // DOC L0016: appearance: preferenze di aspetto separate da programmi e cronologia. Nel contesto di chooseLanguage.
    private val appearance = application.getSharedPreferences("tabata-appearance", android.content.Context.MODE_PRIVATE)
    // DOC L0017: Legge il valore locale e applica i controlli/fallback riportati nell’espressione. darkTheme: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di chooseLanguage.
    var darkTheme by mutableStateOf(runCatching { appearance.getBoolean("dark", false) }.getOrDefault(false)); private set
    // DOC L0018: Legge il valore locale e applica i controlli/fallback riportati nell’espressione. palette: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di chooseLanguage.
    var palette by mutableStateOf(runCatching { appearance.getString("palette", "purple") }.getOrNull()?.takeIf { value -> studioPalettes.any { it.id == value } } ?: "purple"); private set
    // DOC L0019: chooseDarkTheme: Aggiorna lo stato Compose del tema e persiste il booleano nelle SharedPreferences.
    fun chooseDarkTheme(value: Boolean) { darkTheme = value; appearance.edit().putBoolean("dark", value).apply() }
    // DOC L0020: choosePalette: Accetta soltanto un ID presente nelle nove palette, poi aggiorna UI e preferenza locale.
    fun choosePalette(value: String) { if (studioPalettes.none { it.id == value }) return; palette = value; appearance.edit().putString("palette", value).apply() }
    // DOC L0021: Legge il valore locale e applica i controlli/fallback riportati nell’espressione. mode: modalità WORKOUT o STUDIO, usata per filtrare programmi e attività. Nel contesto di choosePalette.
    var mode by mutableStateOf(runCatching { appearance.getString("mode", "WORKOUT") }.getOrNull()?.takeIf { it in listOf("WORKOUT", "STUDIO") } ?: "WORKOUT"); private set
    // DOC L0022: labels: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di choosePalette.
    val labels: ModeLabels get() = modeLabels(mode)
    // DOC L0023: visibleData: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di choosePalette.
    val visibleData: Backup get() = data.forMode(mode)
    // DOC L0024: quickByMode: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di choosePalette.
    private val quickByMode = mutableMapOf("WORKOUT" to localizedNewWorkout(newQuickWorkout()), "STUDIO" to localizedNewWorkout(newQuickWorkout("STUDIO")))
    // DOC L0025: chooseMode: Blocca il cambio durante timer o salvataggi pendenti; altrimenti conferma eventuali bozze da scartare e ricorda la modalità.
    fun chooseMode(studio: Boolean) {
        // DOC L0026: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di chooseMode.
        if (active || runtime.hasUnsavedResult || quickSaveBusy) { message = "Termina la sessione prima di cambiare modalità."; return }
        // DOC L0027: Scrive la preferenza o la copia locale prevista, senza inviarla a un server. Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. go: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di chooseMode.
        val go = { mode = if (studio) "STUDIO" else "WORKOUT"; appearance.edit().putString("mode", mode).apply(); quickWorkout = quickByMode.getValue(mode); draft = null; dirty = false; runtime.clearFinished(); fullscreen = false; route = "Home" }
        // DOC L0028: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di chooseMode.
        if (dirty) confirm("Scartare le modifiche non salvate e cambiare modalità?", go) else go()
    // DOC L0029: Chiude il costrutto aperto alla riga 25.
    }
    // DOC L0030: repository: accesso ai programmi, sessioni e checkpoint persistenti. Nel contesto di chooseMode.
    private val repository = (application as TabataApplication).repository
    // DOC L0031: data: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di chooseMode.
    var data by mutableStateOf(Backup(emptyList(), emptyList())); private set
    // DOC L0032: ready: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di chooseMode.
    var ready by mutableStateOf(false); private set
    // DOC L0033: route: schermata corrente della UI. Nel contesto di chooseMode.
    var route by mutableStateOf("Home"); private set
    // DOC L0034: draft: copia modificabile del programma nell’editor. Nel contesto di chooseMode.
    var draft by mutableStateOf<Workout?>(null); private set
    // DOC L0035: draftNotice: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di chooseMode.
    var draftNotice by mutableStateOf(""); private set
    // DOC L0036: quickWorkout: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di chooseMode.
    var quickWorkout by mutableStateOf(quickByMode.getValue(mode)); private set
    // DOC L0037: dirty: indicatore di modifiche non salvate che protegge navigazione e cambio modalità. Nel contesto di chooseMode.
    var dirty by mutableStateOf(false); private set
    // DOC L0038: message: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di chooseMode.
    var message: String
        // DOC L0039: Esegue il passaggio esplicito «get() = runtime.message» nel flusso: Coordinamento dello stato della UI Android. Nel contesto di chooseMode.
        get() = runtime.message
        // DOC L0040: Esegue il passaggio esplicito «set(value) { runtime.message = value }» nel flusso: Coordinamento dello stato della UI Android. Nel contesto di chooseMode.
        set(value) { runtime.message = value }
    // DOC L0041: confirmTitle: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di chooseMode.
    var confirmTitle by mutableStateOf(""); private set
    // DOC L0042: confirmAction: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di chooseMode.
    private var confirmAction: (() -> Unit)? = null
    // DOC L0043: importPreview: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di chooseMode.
    var importPreview by mutableStateOf<Backup?>(null)
    // DOC L0044: exportContent: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di chooseMode.
    var exportContent = ""
    // DOC L0045: quickSaveDialog: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di chooseMode.
    var quickSaveDialog by mutableStateOf(false); private set
    // DOC L0046: quickSaveName: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di chooseMode.
    var quickSaveName by mutableStateOf("")
    // DOC L0047: quickSaveError: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di chooseMode.
    var quickSaveError by mutableStateOf(""); private set
    // DOC L0048: quickSaveBusy: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di chooseMode.
    var quickSaveBusy by mutableStateOf(false); private set
    // DOC L0049: pendingQuickSave: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di chooseMode.
    private var pendingQuickSave: Workout? = null
    // DOC L0050: timer: motore corrente, opzionale quando nessuna sessione è stata avviata. Nel contesto di chooseMode.
    val timer: TimerEngine? get() = runtime.timer
    // DOC L0051: version: stato osservabile che fa aggiornare Compose quando il motore cambia. Nel contesto di chooseMode.
    val version: Int get() = runtime.version
    // DOC L0052: starting: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di chooseMode.
    val starting: Boolean get() = runtime.starting
    // DOC L0053: sound: preferenza che abilita i segnali, senza modificare i tempi. Nel contesto di chooseMode.
    var sound: Boolean
        // DOC L0054: Esegue il passaggio esplicito «get() = runtime.sound» nel flusso: Coordinamento dello stato della UI Android. Nel contesto di chooseMode.
        get() = runtime.sound
        // DOC L0055: Esegue il passaggio esplicito «set(value) { runtime.chooseSound(value) }» nel flusso: Coordinamento dello stato della UI Android. Nel contesto di chooseMode.
        set(value) { runtime.chooseSound(value) }
    // DOC L0056: vibration: preferenza per il segnale aptico, indipendente dal conteggio. Nel contesto di chooseMode.
    var vibration: Boolean
        // DOC L0057: Esegue il passaggio esplicito «get() = runtime.vibration» nel flusso: Coordinamento dello stato della UI Android. Nel contesto di chooseMode.
        get() = runtime.vibration
        // DOC L0058: Esegue il passaggio esplicito «set(value) { runtime.vibration = value }» nel flusso: Coordinamento dello stato della UI Android. Nel contesto di chooseMode.
        set(value) { runtime.vibration = value }
    // DOC L0059: keepAwake: preferenza per tenere acceso lo schermo quando supportato. Nel contesto di chooseMode.
    var keepAwake by mutableStateOf(true)
    // DOC L0060: fullscreen: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di chooseMode.
    var fullscreen by mutableStateOf(false)
    // DOC L0061: active: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di chooseMode.
    val active: Boolean get() = runtime.active
    // DOC L0062: Esegue il passaggio esplicito «init {» nel flusso: Coordinamento dello stato della UI Android. Nel contesto di chooseMode.
    init {
        // DOC L0063: Esegue il passaggio esplicito «viewModelScope.launch {» nel flusso: Coordinamento dello stato della UI Android. Nel contesto di chooseMode.
        viewModelScope.launch {
            // DOC L0064: Esegue il passaggio esplicito «runCatching {» nel flusso: Coordinamento dello stato della UI Android. Nel contesto di chooseMode.
            runCatching {
                // DOC L0065: recovered: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di chooseMode.
                val recovered = runtime.initialized.await(); refresh(); ready = true
                // DOC L0066: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di chooseMode.
                runtime.timer?.let { current -> if (runtime.active || current.result != null) { mode = current.workout.mode; quickWorkout = quickByMode.getValue(mode); route = "Timer" } }
                // DOC L0067: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di chooseMode.
                if (recovered) message = "Recuperata un’attività interrotta dopo la chiusura dell’app."
            // DOC L0068: Esegue il passaggio esplicito «}.onFailure { message = "Archivio non disponibile: ${it.message}. Riapri l’app o controlla lo spazio libero." }» nel flusso: Coordinamento dello stato della UI Android. Nel contesto di chooseMode.
            }.onFailure { message = "Archivio non disponibile: ${it.message}. Riapri l’app o controlla lo spazio libero." }
        // DOC L0069: Chiude il costrutto aperto alla riga 63.
        }
        // DOC L0070: Esegue il passaggio esplicito «viewModelScope.launch {» nel flusso: Coordinamento dello stato della UI Android. Nel contesto di chooseMode.
        viewModelScope.launch {
            // DOC L0071: Esegue il passaggio esplicito «snapshotFlow { runtime.archiveRevision }.collect { refresh() }» nel flusso: Coordinamento dello stato della UI Android. Nel contesto di chooseMode.
            snapshotFlow { runtime.archiveRevision }.collect { refresh() }
        // DOC L0072: Chiude il costrutto aperto alla riga 70.
        }
    // DOC L0073: Chiude il costrutto aperto alla riga 62.
    }
    // DOC L0074: refresh: Rilegge dal repository programmi e sessioni dopo un’operazione persistente.
    private suspend fun refresh() { data = repository.read() }
    // DOC L0075: write: Esegue un’operazione sospendibile nel viewModelScope, aggiorna l’archivio visibile e rende gli errori leggibili.
    private fun write(action: suspend () -> Unit) { viewModelScope.launch { try { action(); refresh() } catch (e: Exception) { message = "Operazione non riuscita: ${e.message}" } } }
    // DOC L0076: confirm: Memorizza titolo e azione da eseguire solo quando viene confermato il dialogo.
    fun confirm(title: String, action: () -> Unit) { confirmTitle = title; confirmAction = action }
    // DOC L0077: dismissConfirm: Chiude il dialogo ed elimina l’azione pendente, senza eseguirla.
    fun dismissConfirm() { confirmTitle = ""; confirmAction = null }
    // DOC L0078: acceptConfirm: Preleva l’azione, pulisce il dialogo e la esegue una sola volta.
    fun acceptConfirm() { val action = confirmAction; dismissConfirm(); action?.invoke() }
    // DOC L0079: navigate: Cambia schermata proteggendo il timer attivo e le bozze non salvate.
    fun navigate(destination: String) {
        // DOC L0080: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. go: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di navigate.
        val go = { if (destination == "Editor" && draft == null) draft = localizedNewWorkout(newWorkout(mode)); if (destination != "Timer") fullscreen = false; route = destination }
        // DOC L0081: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di navigate.
        if (route == "Editor" && dirty && destination != "Editor") confirm("Scartare le modifiche non salvate?") { dirty = false; draft = null; go() } else go()
    // DOC L0082: Chiude il costrutto aperto alla riga 79.
    }
    // DOC L0083: edit: Normalizza una copia compatibile del programma per l’editor, conservando l’originale finché non viene salvato.
    fun edit(workout: Workout = localizedNewWorkout(newWorkout(mode))) { draftNotice = normalizationNotice(workout); draft = normalizeWorkout(workout); dirty = false; route = "Editor" }
    // DOC L0084: update: Sostituisce la bozza dell’editor e la marca come modificata.
    fun update(workout: Workout) { draft = workout; dirty = true }
    // DOC L0085: updateQuick: Aggiorna il timer rapido della modalità corrente e la relativa copia nella mappa per modalità.
    fun updateQuick(workout: Workout) { quickByMode[mode] = workout; quickWorkout = workout }
    // DOC L0086: quickPreset: Applica lavoro/recupero al primo esercizio Workout o concentrazione/pausa alla prima attività Studio.
    fun quickPreset(preset: QuickPreset) {
        // DOC L0087: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di quickPreset.
        if (quickWorkout.studio) {
            // DOC L0088: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di quickPreset.
            updateQuick(quickWorkout.copy(title = preset.name, activities = quickWorkout.activities!!.mapIndexed { i, a -> if (i == 0) a.copy(concentrationSec = preset.workSec, pauseSec = preset.restSec) else a }))
            // DOC L0089: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di quickPreset.
            return
        // DOC L0090: Chiude il costrutto aperto alla riga 87.
        }
        // DOC L0091: b: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di quickPreset.
        val b = quickWorkout.blocks[0]
        // DOC L0092: e: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di quickPreset.
        val e = b.exercises[0].copy(workSec = preset.workSec, restBetweenRepsSec = preset.restSec)
        // DOC L0093: Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di quickPreset.
        updateQuick(quickWorkout.copy(title = preset.name, blocks = listOf(b.copy(exercises = listOf(e)))))
    // DOC L0094: Chiude il costrutto aperto alla riga 86.
    }
    // DOC L0095: openQuickSave: Valida la timeline, crea una copia con nuovi ID e apre la scelta del nome da salvare.
    fun openQuickSave() {
        // DOC L0096: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di openQuickSave.
        try { compileTimeline(quickWorkout) } catch (e: Exception) { message = e.message ?: "Controlla i campi"; return }
        // DOC L0097: Esegue il passaggio esplicito «pendingQuickSave = duplicateWorkout(quickWorkout)» nel flusso: Coordinamento dello stato della UI Android. Nel contesto di openQuickSave.
        pendingQuickSave = duplicateWorkout(quickWorkout)
        // DOC L0098: Esegue il passaggio esplicito «quickSaveName = quickWorkout.title; quickSaveError = ""; quickSaveDialog = true» nel flusso: Coordinamento dello stato della UI Android. Nel contesto di openQuickSave.
        quickSaveName = quickWorkout.title; quickSaveError = ""; quickSaveDialog = true
    // DOC L0099: Chiude il costrutto aperto alla riga 95.
    }
    // DOC L0100: dismissQuickSave: Annulla il dialogo di salvataggio solo se non è in corso una scrittura.
    fun dismissQuickSave() { if (!quickSaveBusy) { quickSaveDialog = false; pendingQuickSave = null } }
    // DOC L0101: confirmQuickSave: Valida il nome e la timeline, salva una volta nel repository e apre la raccolta della modalità.
    fun confirmQuickSave() {
        // DOC L0102: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di confirmQuickSave.
        if (quickSaveBusy) return
        // DOC L0103: pending: motore preparato che attende l’avvio del servizio foreground. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di confirmQuickSave.
        val pending = pendingQuickSave ?: return
        // DOC L0104: candidate: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di confirmQuickSave.
        val candidate = pending.copy(title = quickSaveName.trim())
        // DOC L0105: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di confirmQuickSave.
        try { compileTimeline(candidate) } catch (e: Exception) { quickSaveError = e.message ?: "Inserisci un nome valido"; return }
        // DOC L0106: Esegue il passaggio esplicito «quickSaveBusy = true» nel flusso: Coordinamento dello stato della UI Android. Nel contesto di confirmQuickSave.
        quickSaveBusy = true
        // DOC L0107: Esegue il passaggio esplicito «viewModelScope.launch {» nel flusso: Coordinamento dello stato della UI Android. Nel contesto di confirmQuickSave.
        viewModelScope.launch {
            // DOC L0108: Esegue il passaggio esplicito «try {» nel flusso: Coordinamento dello stato della UI Android. Nel contesto di confirmQuickSave.
            try {
                // DOC L0109: Esegue il passaggio esplicito «repository.save(candidate); refresh()» nel flusso: Coordinamento dello stato della UI Android. Nel contesto di confirmQuickSave.
                repository.save(candidate); refresh()
                // DOC L0110: Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di confirmQuickSave.
                updateQuick(quickWorkout.copy(title = candidate.title))
                // DOC L0111: Esegue il passaggio esplicito «quickSaveDialog = false; pendingQuickSave = null; route = "Allenamenti"» nel flusso: Coordinamento dello stato della UI Android. Nel contesto di confirmQuickSave.
                quickSaveDialog = false; pendingQuickSave = null; route = "Allenamenti"
                // DOC L0112: Esegue il passaggio esplicito «message = "Salvato sul dispositivo. Lo trovi in ${labels.collection}."» nel flusso: Coordinamento dello stato della UI Android. Nel contesto di confirmQuickSave.
                message = "Salvato sul dispositivo. Lo trovi in ${labels.collection}."
            // DOC L0113: Esegue il passaggio esplicito «} catch (e: Exception) { quickSaveError = "Salvataggio non riuscito: ${e.message}. Puoi riprovare." }» nel flusso: Coordinamento dello stato della UI Android. Nel contesto di confirmQuickSave.
            } catch (e: Exception) { quickSaveError = "Salvataggio non riuscito: ${e.message}. Puoi riprovare." }
            // DOC L0114: Esegue il passaggio esplicito «finally { quickSaveBusy = false }» nel flusso: Coordinamento dello stato della UI Android. Nel contesto di confirmQuickSave.
            finally { quickSaveBusy = false }
        // DOC L0115: Chiude il costrutto aperto alla riga 107.
        }
    // DOC L0116: Chiude il costrutto aperto alla riga 101.
    }
    // DOC L0117: block: Trasforma soltanto il blocco Workout con l’indice indicato, aggiornando la bozza.
    fun block(index: Int, transform: (Block) -> Block) { draft?.let { w -> update(w.copy(blocks = w.blocks.mapIndexed { i, b -> if (i == index) transform(b) else b })) } }
    // DOC L0118: exercise: Trasforma soltanto l’esercizio indicato nel suo blocco, usando copie immutabili.
    fun exercise(bi: Int, ei: Int, transform: (Exercise) -> Exercise) { block(bi) { b -> b.copy(exercises = b.exercises.mapIndexed { i, e -> if (i == ei) transform(e) else e }) } }
    // DOC L0119: save: Valida e salva la bozza, poi chiude l’editor e aggiorna la raccolta.
    fun save() {
        // DOC L0120: w: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di save.
        val w = draft ?: return
        // DOC L0121: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di save.
        try { compileTimeline(w) } catch (e: Exception) { message = e.message ?: "Controlla i campi"; return }
        // DOC L0122: Esegue il passaggio esplicito «write { repository.save(w); dirty = false; draft = null; route = "Allenamenti"; message = "Allenamento salvato." }» nel flusso: Coordinamento dello stato della UI Android. Nel contesto di save.
        write { repository.save(w); dirty = false; draft = null; route = "Allenamenti"; message = "Allenamento salvato." }
    // DOC L0123: Chiude il costrutto aperto alla riga 119.
    }
    // DOC L0124: duplicate: Crea e salva un duplicato con nuovi ID e suffisso del titolo tradotto.
    fun duplicate(w: Workout) = write { repository.save(duplicateWorkout(w).copy(title = tr("{0} (copia)", w.title.take(110)))) }
    // DOC L0125: remove: Chiede conferma prima di eliminare il programma, mantenendo le attività storiche.
    fun remove(w: Workout) = confirm("Eliminare ${w.title}? La cronologia rimane.") { write { repository.removeWorkout(w.id) } }
    // DOC L0126: removeSession: Chiede conferma prima di eliminare la sola attività selezionata.
    fun removeSession(s: Session) = confirm("Eliminare questa sessione?") { write { repository.removeSession(s.id) } }
    // DOC L0127: addPresets: Aggiunge i modelli iniziali della modalità corrente con titoli localizzati.
    fun addPresets() = write { presets(mode).forEach { repository.save(localizedNewWorkout(it)) } }
    // DOC L0128: start: Impedisce sessioni sovrapposte, delega l’avvio al runtime e apre la schermata Timer.
    fun start(w: Workout) {
        // DOC L0129: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di start.
        if (active) { message = "Termina prima la sessione in corso."; route = "Timer"; return }
        // DOC L0130: Esegue il passaggio esplicito «try { runtime.start(w); route = "Timer"; normalizationNotice(w).takeIf { it.isNotEmpty() }?.let { message = it } } catch (e: Exception) { message = e.message ?: "Timer non disponib» nel flusso: Coordinamento dello stato della UI Android. Nel contesto di start.
        try { runtime.start(w); route = "Timer"; normalizationNotice(w).takeIf { it.isNotEmpty() }?.let { message = it } } catch (e: Exception) { message = e.message ?: "Timer non disponibile" }
    // DOC L0131: Chiude il costrutto aperto alla riga 128.
    }
    // DOC L0132: demo: Conserva un percorso interno di prova precedente; i pulsanti demo non sono più esposti nella UI.
    fun demo() { if (mode == "STUDIO") { start(newWorkout(mode).copy(title = "Demo studio", activities = listOf(StudyActivity(concentrationSec = 60, sessions = 2, pauseSec = 60)))); return }; val w = Workout(title = "Demo · il tuo primo ritmo", blocks = listOf(Block(exercises = listOf(Exercise(title = "Muoviti al tuo ritmo", repetitions = 2, workSec = 5, restBetweenRepsSec = 2))))); start(w) }
    // DOC L0133: chooseSound: Delega al runtime la preferenza audio, condivisa con il servizio in esecuzione.
    fun chooseSound(enabled: Boolean) { runtime.chooseSound(enabled) }
    // DOC L0134: pauseResume: Delega al runtime la transizione tra pausa e ripresa.
    fun pauseResume() { runtime.pauseResume() }
    // DOC L0135: skip: Salta la fase dopo aver aggiornato il timer, senza accreditarne il lavoro non svolto.
    fun skip() { runtime.skip() }
    // DOC L0136: previous: Ripete la fase precedente creando una visita nuova senza duplicare gli intervalli già completati.
    fun previous() { runtime.previous() }
    // DOC L0137: stop: Chiede conferma prima di terminare il motore e registrarne il risultato interrotto.
    fun stop() = confirm("Terminare la sessione e registrarla come interrotta?") { runtime.stop() }
    // DOC L0138: loadImport: Rifiuta importazioni durante sessioni o salvataggi pendenti e prepara soltanto un’anteprima del JSON validato.
    fun loadImport(raw: String) {
        // DOC L0139: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di loadImport.
        if (active || runtime.hasUnsavedResult) { message = "Termina la sessione e attendi il salvataggio prima di importare o cancellare dati."; return }
        // DOC L0140: Esegue il passaggio esplicito «try { importPreview = parseBackup(raw) } catch (e: Exception) { message = "Importazione rifiutata: ${e.message}" }» nel flusso: Coordinamento dello stato della UI Android. Nel contesto di loadImport.
        try { importPreview = parseBackup(raw) } catch (e: Exception) { message = "Importazione rifiutata: ${e.message}" }
    // DOC L0141: Chiude il costrutto aperto alla riga 138.
    }
    // DOC L0142: applyImport: Sostituisce transazionalmente l’archivio confermato, poi pulisce bozza e risultato precedente.
    fun applyImport() { val backup = importPreview ?: return; if (active || runtime.hasUnsavedResult) return; write { repository.replace(backup); importPreview = null; draft = null; dirty = false; runtime.clearFinished(); route = "Home"; message = "Archivio importato." } }
    // DOC L0143: backupForExport: Include nel backup un risultato appena terminato ancora assente dalla copia UI, evitando duplicati per ID.
    fun backupForExport(): Backup { val session = timer?.result; return if (session != null && data.sessions.none { it.id == session.id }) data.copy(sessions = data.sessions + session) else data }
    // DOC L0144: clear: Controlla che il timer sia fermo e richiede conferma prima della sostituzione con un archivio vuoto.
    fun clear() { if (active || runtime.hasUnsavedResult) { message = "Termina la sessione e attendi il salvataggio prima di importare o cancellare dati."; return }; confirm("Cancellare tutti gli allenamenti e tutte le attività? Questa operazione non può essere annullata.") { write { repository.replace(Backup(emptyList(), emptyList())); draft = null; dirty = false; runtime.clearFinished(); message = "Dati cancellati." } } }
// DOC L0145: Chiude il costrutto aperto alla riga 12.
}
