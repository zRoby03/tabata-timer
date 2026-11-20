// DOC L0001: Assegna il file al namespace it.tabatastudio.domain, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio.domain

/** Pure read adapter: old records remain in the archive until the edited copy is saved. */
// DOC L0004: normalizeWorkout: Restituisce una copia compatibile con il modello semplice: non effettua scritture nell’archivio.
fun normalizeWorkout(w: Workout): Workout {
    // DOC L0005: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Restituisce il valore calcolato o conclude il percorso della funzione. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di normalizeWorkout.
    if (!w.studio) return w.copy(blocks = w.blocks.map { b -> b.copy(restBetweenExercisesSec = b.restBetweenExercisesSec ?: b.exercises.firstOrNull()?.restAfterExerciseSec ?: 15) })
    // DOC L0006: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di normalizeWorkout.
    if (w.activities != null) return w.copy(blocks = emptyList(), warmupSec = 0, cooldownSec = 0, longBreakSec = 0, longBreakEvery = 4)
    // DOC L0007: activities: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di normalizeWorkout.
    val activities = mutableListOf<StudyActivity>()
    // DOC L0008: boundaries: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di normalizeWorkout.
    val boundaries = mutableListOf<Int>()
    // DOC L0009: ids: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di normalizeWorkout.
    val ids = mutableSetOf(w.id)
    // DOC L0010: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di normalizeWorkout.
    w.blocks.forEachIndexed { bi, b ->
        // DOC L0011: Esegue il passaggio esplicito «repeat(b.cycles) { c ->» nel flusso: Adattamento esplicito dei programmi precedenti. Nel contesto di normalizeWorkout.
        repeat(b.cycles) { c ->
            // DOC L0012: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di normalizeWorkout.
            b.exercises.forEachIndexed { ei, e ->
                // DOC L0013: id: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di normalizeWorkout.
                var id = "legacy-${w.id.take(80)}-${activities.size + 1}"
                // DOC L0014: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di normalizeWorkout.
                while (id in ids) id += "_x"
                // DOC L0015: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di normalizeWorkout.
                require(activities.size < 5000) { "Troppe attività nel programma precedente" }
                // DOC L0016: Esegue il passaggio esplicito «ids.add(id)» nel flusso: Adattamento esplicito dei programmi precedenti. Nel contesto di normalizeWorkout.
                ids.add(id)
                // DOC L0017: Esegue il passaggio esplicito «activities.add(StudyActivity(id, e.title, e.workSec, e.repetitions, e.restBetweenRepsSec))» nel flusso: Adattamento esplicito dei programmi precedenti. Nel contesto di normalizeWorkout.
                activities.add(StudyActivity(id, e.title, e.workSec, e.repetitions, e.restBetweenRepsSec))
                // DOC L0018: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di normalizeWorkout.
                if (ei < b.exercises.lastIndex) boundaries.add(e.restAfterExerciseSec)
            // DOC L0019: Chiude il costrutto aperto alla riga 12.
            }
            // DOC L0020: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di normalizeWorkout.
            if (c < b.cycles - 1) boundaries.add(b.restBetweenCyclesSec)
        // DOC L0021: Chiude il costrutto aperto alla riga 11.
        }
        // DOC L0022: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di normalizeWorkout.
        if (bi < w.blocks.lastIndex) boundaries.add(b.restAfterBlockSec)
    // DOC L0023: Chiude il costrutto aperto alla riga 10.
    }
    // DOC L0024: Restituisce il valore calcolato o conclude il percorso della funzione. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di normalizeWorkout.
    return w.copy(blocks = emptyList(), activities = activities, restBetweenActivitiesSec = boundaries.firstOrNull() ?: 300, warmupSec = 0, cooldownSec = 0, longBreakSec = 0, longBreakEvery = 4)
// DOC L0025: Chiude il costrutto aperto alla riga 4.
}
// DOC L0026: normalizationNotice: Spiega all’utente la conversione dei campi precedenti e quando l’originale verrà sostituito.
fun normalizationNotice(w: Workout): String = when {
    // DOC L0027: Esegue il passaggio esplicito «w.studio && w.activities == null -> "Programma precedente: le serie diventano attività; pause lunghe e finali sono escluse. La pausa tra attività usa il primo valore precedente. L’» nel flusso: Adattamento esplicito dei programmi precedenti. Nel contesto di normalizationNotice.
    w.studio && w.activities == null -> "Programma precedente: le serie diventano attività; pause lunghe e finali sono escluse. La pausa tra attività usa il primo valore precedente. L’originale resta nell’archivio fino al salvataggio."
    // DOC L0028: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di normalizationNotice.
    !w.studio && w.blocks.any { b -> b.restBetweenExercisesSec == null && b.exercises.dropLast(1).map { it.restAfterExerciseSec }.distinct().size > 1 } -> "Allenamento precedente: il recupero tra esercizi è ora unico per blocco e usa il primo valore precedente. L’originale resta nell’archivio fino al salvataggio."
    // DOC L0029: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di normalizationNotice.
    else -> ""
// DOC L0030: Chiude il costrutto aperto alla riga 26.
}
