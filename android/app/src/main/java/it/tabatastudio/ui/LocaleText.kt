// DOC L0001: Assegna il file al namespace it.tabatastudio.ui, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio.ui

// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.i18n.AppText. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.i18n.AppText

// DOC L0005: tr: Delega la traduzione della UI ad AppText preservando gli argomenti dei segnaposto.
internal fun tr(source: String?, vararg values: Any?): String = AppText.text(source, *values)

// DOC L0007: localizedNewWorkout: Traduce soltanto le etichette di un programma appena creato, senza migrare i nomi degli archivi.
internal fun localizedNewWorkout(w: it.tabatastudio.domain.Workout): it.tabatastudio.domain.Workout = w.copy(
    // DOC L0008: Esegue il passaggio esplicito «title = tr(w.title), description = tr(w.description),» nel flusso: Adattatori di testo per la UI Compose. Nel contesto di localizedNewWorkout.
    title = tr(w.title), description = tr(w.description),
    // DOC L0009: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di localizedNewWorkout.
    blocks = w.blocks.map { localizedNewBlock(it) },
    // DOC L0010: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di localizedNewWorkout.
    activities = w.activities?.map { it.copy(title = tr(it.title)) }
// DOC L0011: Chiude un blocco, una chiamata o un valore composto; non aggiunge una nuova operazione.
)
// DOC L0012: localizedNewBlock: Localizza il titolo iniziale del blocco e i suoi esercizi nuovi.
internal fun localizedNewBlock(b: it.tabatastudio.domain.Block): it.tabatastudio.domain.Block = b.copy(title = tr(b.title), exercises = b.exercises.map { localizedNewExercise(it) })
// DOC L0013: localizedNewExercise: Localizza il titolo predefinito di un esercizio appena creato.
internal fun localizedNewExercise(e: it.tabatastudio.domain.Exercise): it.tabatastudio.domain.Exercise = e.copy(title = tr(e.title))
