// DOC L0001: Assegna il file al namespace it.tabatastudio.domain, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio.domain

// DOC L0003: Fissa la preparazione a 20.000 millisecondi, separata dal lavoro pianificato. val: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
const val PREPARATION_MS = 20000L
// DOC L0004: preparation: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
private val preparation = Phase("PREPARE", null, null, PREPARATION_MS, "Preparazione")

// DOC L0006: Dichiara il tipo o contenitore di questo modulo: Macchina a stati del timer Android. I campi e i metodi sono descritti nelle righe successive.
class TimerEngine(val workout: Workout, private val now: () -> Long, private val wall: () -> String = ::isoNow) {
    // DOC L0007: phases: sequenza cronologica compilata dal programma.
    val phases = compileTimeline(workout)
    // DOC L0008: index: indice della fase: -1 identifica la preparazione iniziale.
    var index = -1; private set
    // DOC L0009: remainingMs: tempo residuo della fase in millisecondi.
    var remainingMs = PREPARATION_MS; private set
    // DOC L0010: phaseRun: identità della visita corrente, incrementata anche quando si ripete o salta una fase.
    var phaseRun = 0; private set
    // DOC L0011: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. currentPhase: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    val currentPhase: Phase? get() = if (index < 0) preparation else phases.getOrNull(index)
    // DOC L0012: nextPhase: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    val nextPhase: Phase? get() = phases.getOrNull(index + 1)
    // DOC L0013: state: stato idle, running, paused o finished della macchina del timer.
    var state = "idle"; private set
    // DOC L0014: elapsedMs: tempo attivo realmente consumato, senza preparazione né pause manuali.
    var elapsedMs = 0L; private set
    // DOC L0015: workMs: tempo realmente svolto nelle sole fasi WORK.
    var workMs = 0L; private set
    // DOC L0016: pauses: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    var pauses = 0; private set
    // DOC L0017: result: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    var result: Session? = null; private set
    // DOC L0018: completed: insieme degli intervalli WORK completati, così un replay non li duplica.
    private val completed = mutableSetOf<Int>()
    // DOC L0019: completedPhases: insieme delle fasi completate, distinto dagli intervalli di lavoro.
    private val completedPhases = mutableSetOf<Int>()
    // DOC L0020: last: ultimo riferimento dell’orologio monotono usato per misurare il delta.
    private var last = 0L
    // DOC L0021: sessionId: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    val sessionId = uid()
    // DOC L0022: startedAt: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    private var startedAt = ""
    // DOC L0023: start: Avvia un motore idle usando gli orologi iniettati, senza dipendere dall’Activity.
    fun start() { if (state == "idle") { startedAt = wall(); last = now(); state = "running" } }
    // DOC L0024: tick: Calcola il tempo realmente trascorso dall’orologio monotono e attraversa tutte le fasi superate, anche se il polling è arrivato in ritardo.
    fun tick() {
        // DOC L0025: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di tick.
        if (state != "running") return
        // DOC L0026: current: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. delta: tempo trascorso dall’ultimo aggiornamento, da distribuire sulle fasi. Nel contesto di tick.
        val current = now(); var delta = (current - last).coerceAtLeast(0); last = current
        // DOC L0027: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di tick.
        while (state == "running" && delta >= remainingMs) {
            // DOC L0028: Esegue il passaggio esplicito «consume(remainingMs); delta -= remainingMs» nel flusso: Macchina a stati del timer Android. Nel contesto di tick.
            consume(remainingMs); delta -= remainingMs
            // DOC L0029: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di tick.
            if (index >= 0) {
                // DOC L0030: Esegue il passaggio esplicito «completedPhases.add(index)» nel flusso: Macchina a stati del timer Android. Nel contesto di tick.
                completedPhases.add(index)
                // DOC L0031: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di tick.
                if (currentPhase?.type == "WORK") completed.add(index)
            // DOC L0032: Chiude il costrutto aperto alla riga 29.
            }
            // DOC L0033: Esegue il passaggio esplicito «advance()» nel flusso: Macchina a stati del timer Android. Nel contesto di tick.
            advance()
        // DOC L0034: Chiude il costrutto aperto alla riga 27.
        }
        // DOC L0035: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di tick.
        if (state == "running") { consume(delta); remainingMs -= delta }
    // DOC L0036: Chiude il costrutto aperto alla riga 24.
    }
    // DOC L0037: consume: Accredita tempo attivo e tempo WORK, escludendo la preparazione.
    private fun consume(ms: Long) {
        // DOC L0038: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di consume.
        if (index < 0) return // Preparation is outside the planned workout and its statistics.
        // DOC L0039: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di consume.
        elapsedMs += ms; if (currentPhase?.type == "WORK") workMs += ms
    // DOC L0040: Chiude il costrutto aperto alla riga 37.
    }
    // DOC L0041: advance: Incrementa indice e visita di fase; alla fine produce il risultato senza aggiungere un recupero finale.
    private fun advance() {
        // DOC L0042: Esegue il passaggio esplicito «index++; phaseRun++» nel flusso: Macchina a stati del timer Android. Nel contesto di advance.
        index++; phaseRun++
        // DOC L0043: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di advance.
        if (index >= phases.size) finish(if (completed.size == phases.count { it.type == "WORK" }) "COMPLETED" else "INTERRUPTED") else remainingMs = phases[index].durationMs
    // DOC L0044: Chiude il costrutto aperto alla riga 41.
    }
    // DOC L0045: pause: Aggiorna prima il tempo trascorso e poi sospende un motore ancora in esecuzione.
    fun pause() { tick(); if (state == "running") { state = "paused"; pauses++ } }
    // DOC L0046: resume: Riparte dal tempo residuo, sostituendo il riferimento monotono così la pausa non viene conteggiata.
    fun resume() { if (state == "paused") { last = now(); state = "running" } }
    // DOC L0047: skip: Salta la fase dopo aver aggiornato il timer, senza accreditarne il lavoro non svolto.
    fun skip() { tick(); if (state in listOf("running", "paused")) advance() }
    // DOC L0048: previous: Ripete la fase precedente creando una visita nuova senza duplicare gli intervalli già completati.
    fun previous() {
        // DOC L0049: Esegue il passaggio esplicito «tick()» nel flusso: Macchina a stati del timer Android. Nel contesto di previous.
        tick()
        // DOC L0050: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di previous.
        if (state in listOf("running", "paused")) {
            // DOC L0051: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di previous.
            if (index >= 0) index = (index - 1).coerceAtLeast(0)
            // DOC L0052: Esegue il passaggio esplicito «phaseRun++; remainingMs = currentPhase!!.durationMs; last = now()» nel flusso: Macchina a stati del timer Android. Nel contesto di previous.
            phaseRun++; remainingMs = currentPhase!!.durationMs; last = now()
        // DOC L0053: Chiude il costrutto aperto alla riga 50.
        }
    // DOC L0054: Chiude il costrutto aperto alla riga 48.
    }
    // DOC L0055: stop: Registra l’interruzione solo se la sessione era ancora attiva.
    fun stop() { tick(); if (state in listOf("running", "paused")) finish("INTERRUPTED") }
    // DOC L0056: snapshot: Crea il riepilogo corrente, usato per checkpoint e recupero di una sessione interrotta.
    fun snapshot(): Session = result ?: Session(sessionId, workout.id, workout.title, startedAt, wall(), plannedMs(phases), elapsedMs, workMs, completed.size, phases.count { it.type == "WORK" }, pauses, "INTERRUPTED", completedPhases.size, phases.size, workout.mode)
    // DOC L0057: finish: Conserva un solo risultato finale e azzera il residuo.
    private fun finish(status: String) { if (result != null) return; result = snapshot().copy(status = status); state = "finished"; remainingMs = 0 }
// DOC L0058: Chiude il costrutto aperto alla riga 6.
}
