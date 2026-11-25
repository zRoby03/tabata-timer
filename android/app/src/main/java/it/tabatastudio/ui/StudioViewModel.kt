package it.tabatastudio.ui

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import it.tabatastudio.TabataApplication
import it.tabatastudio.data.*
import it.tabatastudio.domain.*
import kotlinx.coroutines.*

class StudioViewModel(application: Application) : AndroidViewModel(application) {
    val language: String get() = it.tabatastudio.i18n.AppText.language
    fun chooseLanguage(value: String) { it.tabatastudio.i18n.AppText.choose(value); it.tabatastudio.timer.TimerService.refreshResources() }
    private val runtime = (application as TabataApplication).timerRuntime
    private val appearance = application.getSharedPreferences("tabata-appearance", android.content.Context.MODE_PRIVATE)
    var darkTheme by mutableStateOf(runCatching { appearance.getBoolean("dark", false) }.getOrDefault(false)); private set
    var palette by mutableStateOf(runCatching { appearance.getString("palette", "purple") }.getOrNull()?.takeIf { value -> studioPalettes.any { it.id == value } } ?: "purple"); private set
    internal var style by mutableStateOf(StudioStyle.fromId(runCatching { appearance.getString("style", null) }.getOrNull()))
        private set
    internal fun chooseStyle(value: StudioStyle) {
        style = value
        appearance.edit().putString("style", value.id).apply()
    }
    fun chooseDarkTheme(value: Boolean) { darkTheme = value; appearance.edit().putBoolean("dark", value).apply() }
    fun choosePalette(value: String) { if (studioPalettes.none { it.id == value }) return; palette = value; appearance.edit().putString("palette", value).apply() }
    var mode by mutableStateOf(runCatching { appearance.getString("mode", "WORKOUT") }.getOrNull()?.takeIf { it in listOf("WORKOUT", "STUDIO") } ?: "WORKOUT"); private set
    val labels: ModeLabels get() = modeLabels(mode)
    val visibleData: Backup get() = data.forMode(mode)
    private val quickByMode = mutableMapOf("WORKOUT" to localizedNewWorkout(newQuickWorkout()), "STUDIO" to localizedNewWorkout(newQuickWorkout("STUDIO")))
    fun chooseMode(studio: Boolean) {
        if (active || runtime.hasUnsavedResult || quickSaveBusy) { message = "Termina la sessione prima di cambiare modalità."; return }
        val go = { mode = if (studio) "STUDIO" else "WORKOUT"; appearance.edit().putString("mode", mode).apply(); quickWorkout = quickByMode.getValue(mode); draft = null; dirty = false; runtime.clearFinished(); fullscreen = false; route = "Home" }
        if (dirty) confirm("Scartare le modifiche non salvate e cambiare modalità?", go) else go()
    }
    private val repository = (application as TabataApplication).repository
    var data by mutableStateOf(Backup(emptyList(), emptyList())); private set
    var ready by mutableStateOf(false); private set
    var route by mutableStateOf("Home"); private set
    var draft by mutableStateOf<Workout?>(null); private set
    var draftNotice by mutableStateOf(""); private set
    var quickWorkout by mutableStateOf(quickByMode.getValue(mode)); private set
    var dirty by mutableStateOf(false); private set
    var message: String
        get() = runtime.message
        set(value) { runtime.message = value }
    var confirmTitle by mutableStateOf(""); private set
    private var confirmAction: (() -> Unit)? = null
    var importPreview by mutableStateOf<Backup?>(null)
    var exportContent = ""
    var quickSaveDialog by mutableStateOf(false); private set
    var quickSaveName by mutableStateOf("")
    var quickSaveError by mutableStateOf(""); private set
    var quickSaveBusy by mutableStateOf(false); private set
    private var pendingQuickSave: Workout? = null
    val timer: TimerEngine? get() = runtime.timer
    val version: Int get() = runtime.version
    val starting: Boolean get() = runtime.starting
    var sound: Boolean
        get() = runtime.sound
        set(value) { runtime.chooseSound(value) }
    var vibration: Boolean
        get() = runtime.vibration
        set(value) { runtime.vibration = value }
    var keepAwake by mutableStateOf(true)
    var fullscreen by mutableStateOf(false)
    val active: Boolean get() = runtime.active
    init {
        viewModelScope.launch {
            runCatching {
                val recovered = runtime.initialized.await(); refresh(); ready = true
                runtime.timer?.let { current -> if (runtime.active || current.result != null) { mode = current.workout.mode; quickWorkout = quickByMode.getValue(mode); route = "Timer" } }
                if (recovered) message = "Recuperata un’attività interrotta dopo la chiusura dell’app."
            }.onFailure { message = "Archivio non disponibile: ${it.message}. Riapri l’app o controlla lo spazio libero." }
        }
        viewModelScope.launch {
            snapshotFlow { runtime.archiveRevision }.collect { refresh() }
        }
    }
    private suspend fun refresh() { data = repository.read() }
    private fun write(action: suspend () -> Unit) { viewModelScope.launch { try { action(); refresh() } catch (e: Exception) { message = "Operazione non riuscita: ${e.message}" } } }
    fun confirm(title: String, action: () -> Unit) { confirmTitle = title; confirmAction = action }
    fun dismissConfirm() { confirmTitle = ""; confirmAction = null }
    fun acceptConfirm() { val action = confirmAction; dismissConfirm(); action?.invoke() }
    fun navigate(destination: String) {
        val go = { if (destination == "Editor" && draft == null) draft = localizedNewWorkout(newWorkout(mode)); if (destination != "Timer") fullscreen = false; route = destination }
        if (route == "Editor" && dirty && destination != "Editor") confirm("Scartare le modifiche non salvate?") { dirty = false; draft = null; go() } else go()
    }
    fun edit(workout: Workout = localizedNewWorkout(newWorkout(mode))) { draftNotice = normalizationNotice(workout); draft = normalizeWorkout(workout); dirty = false; route = "Editor" }
    fun update(workout: Workout) { draft = workout; dirty = true }
    fun updateQuick(workout: Workout) { quickByMode[mode] = workout; quickWorkout = workout }
    fun quickPreset(preset: QuickPreset) {
        if (quickWorkout.studio) {
            updateQuick(quickWorkout.copy(title = preset.name, activities = quickWorkout.activities!!.mapIndexed { i, a -> if (i == 0) a.copy(concentrationSec = preset.workSec, pauseSec = preset.restSec) else a }))
            return
        }
        val b = quickWorkout.blocks[0]
        val e = b.exercises[0].copy(workSec = preset.workSec, restBetweenRepsSec = preset.restSec)
        updateQuick(quickWorkout.copy(title = preset.name, blocks = listOf(b.copy(exercises = listOf(e)))))
    }
    fun openQuickSave() {
        try { compileTimeline(quickWorkout) } catch (e: Exception) { message = e.message ?: "Controlla i campi"; return }
        pendingQuickSave = duplicateWorkout(quickWorkout)
        quickSaveName = quickWorkout.title; quickSaveError = ""; quickSaveDialog = true
    }
    fun dismissQuickSave() { if (!quickSaveBusy) { quickSaveDialog = false; pendingQuickSave = null } }
    fun confirmQuickSave() {
        if (quickSaveBusy) return
        val pending = pendingQuickSave ?: return
        val candidate = pending.copy(title = quickSaveName.trim())
        try { compileTimeline(candidate) } catch (e: Exception) { quickSaveError = e.message ?: "Inserisci un nome valido"; return }
        quickSaveBusy = true
        viewModelScope.launch {
            try {
                repository.save(candidate); refresh()
                updateQuick(quickWorkout.copy(title = candidate.title))
                quickSaveDialog = false; pendingQuickSave = null; route = "Allenamenti"
                message = "Salvato sul dispositivo. Lo trovi in ${labels.collection}."
            } catch (e: Exception) { quickSaveError = "Salvataggio non riuscito: ${e.message}. Puoi riprovare." }
            finally { quickSaveBusy = false }
        }
    }
    fun block(index: Int, transform: (Block) -> Block) { draft?.let { w -> update(w.copy(blocks = w.blocks.mapIndexed { i, b -> if (i == index) transform(b) else b })) } }
    fun exercise(bi: Int, ei: Int, transform: (Exercise) -> Exercise) { block(bi) { b -> b.copy(exercises = b.exercises.mapIndexed { i, e -> if (i == ei) transform(e) else e }) } }
    fun save() {
        val w = draft ?: return
        try { compileTimeline(w) } catch (e: Exception) { message = e.message ?: "Controlla i campi"; return }
        write { repository.save(w); dirty = false; draft = null; route = "Allenamenti"; message = "Allenamento salvato." }
    }
    fun duplicate(w: Workout) = write { repository.save(duplicateWorkout(w).copy(title = tr("{0} (copia)", w.title.take(110)))) }
    fun remove(w: Workout) = confirm("Eliminare ${w.title}? La cronologia rimane.") { write { repository.removeWorkout(w.id) } }
    fun removeSession(s: Session) = confirm("Eliminare questa sessione?") { write { repository.removeSession(s.id) } }
    fun addPresets() = write { presets(mode).forEach { repository.save(localizedNewWorkout(it)) } }
    fun start(w: Workout) {
        if (active) { message = "Termina prima la sessione in corso."; route = "Timer"; return }
        try { runtime.start(w); route = "Timer"; normalizationNotice(w).takeIf { it.isNotEmpty() }?.let { message = it } } catch (e: Exception) { message = e.message ?: "Timer non disponibile" }
    }
    fun chooseSound(enabled: Boolean) { runtime.chooseSound(enabled) }
    fun pauseResume() { runtime.pauseResume() }
    fun skip() { runtime.skip() }
    fun previous() { runtime.previous() }
    fun stop() = confirm("Terminare la sessione e registrarla come interrotta?") { runtime.stop() }
    fun loadImport(backup: Backup) {
        if (active || runtime.hasUnsavedResult) { message = "Termina la sessione e attendi il salvataggio prima di importare o cancellare dati."; return }
        importPreview = backup
    }
    fun applyImport() { val backup = importPreview ?: return; if (active || runtime.hasUnsavedResult) return; write { repository.replace(backup); importPreview = null; draft = null; dirty = false; runtime.clearFinished(); route = "Home"; message = "Archivio importato." } }
    fun backupForExport(): Backup { val session = timer?.result; return if (session != null && data.sessions.none { it.id == session.id }) data.copy(sessions = data.sessions + session) else data }
    fun clear() { if (active || runtime.hasUnsavedResult) { message = "Termina la sessione e attendi il salvataggio prima di importare o cancellare dati."; return }; confirm("Cancellare tutti gli allenamenti e tutte le attività? Questa operazione non può essere annullata.") { write { repository.replace(Backup(emptyList(), emptyList())); draft = null; dirty = false; runtime.clearFinished(); message = "Dati cancellati." } } }
}
