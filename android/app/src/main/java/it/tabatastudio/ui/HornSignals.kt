// DOC L0001: Assegna il file al namespace it.tabatastudio.ui, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio.ui

// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import android.content.Context. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.content.Context
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import android.media.AudioAttributes. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.media.AudioAttributes
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import android.media.SoundPool. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.media.SoundPool
// DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.R. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.R
// DOC L0007: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.domain.hornSample. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.domain.hornSample
// DOC L0008: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.domain.cueVolume. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.domain.cueVolume

// DOC L0010: Dichiara il tipo o contenitore di questo modulo: Riproduzione nativa dei nove WAV locali. I campi e i metodi sono descritti nelle righe successive.
class HornSignals(context: Context) {
    // DOC L0011: Prepara o riproduce il segnale audio; il motore del tempo rimane indipendente dalla riproduzione. pool: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    private val pool = SoundPool.Builder().setMaxStreams(1)
        // Short cues mix into the media stream. Never request focus, pause or duck another player.
        // DOC L0013: Esegue il passaggio esplicito «.setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build())» nel flusso: Riproduzione nativa dei nove WAV locali.
        .setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build())
        // DOC L0014: Esegue il passaggio esplicito «.build()» nel flusso: Riproduzione nativa dei nove WAV locali.
        .build()
    // DOC L0015: Applica l’annotazione dichiarata: Compose identifica UI, Room genera accessi e schema, mentre le annotazioni Test selezionano i casi da eseguire.
    @Volatile var isReady = false
        // DOC L0016: Esegue il passaggio esplicito «private set» nel flusso: Riproduzione nativa dei nove WAV locali.
        private set
    // DOC L0017: samples: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    private val samples = mutableMapOf<String, Int>()
    // DOC L0018: loaded: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    private val loaded = mutableSetOf<Int>()
    // DOC L0019: stream: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    private var stream = 0

    // DOC L0021: Esegue il passaggio esplicito «init {» nel flusso: Riproduzione nativa dei nove WAV locali.
    init {
        // DOC L0022: Esegue il passaggio esplicito «pool.setOnLoadCompleteListener { _, sample, status ->» nel flusso: Riproduzione nativa dei nove WAV locali.
        pool.setOnLoadCompleteListener { _, sample, status ->
            // DOC L0023: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
            if (status == 0) loaded.add(sample)
            // DOC L0024: Esegue il passaggio esplicito «isReady = loaded.size == 9» nel flusso: Riproduzione nativa dei nove WAV locali.
            isReady = loaded.size == 9
        // DOC L0025: Chiude il costrutto aperto alla riga 22.
        }
        // DOC L0026: Esegue il passaggio esplicito «mapOf(» nel flusso: Riproduzione nativa dei nove WAV locali.
        mapOf(
            // DOC L0027: Esegue il passaggio esplicito «"work_3" to R.raw.countdown_work_3, "work_2" to R.raw.countdown_work_2, "work_1" to R.raw.countdown_work_1,» nel flusso: Riproduzione nativa dei nove WAV locali.
            "work_3" to R.raw.countdown_work_3, "work_2" to R.raw.countdown_work_2, "work_1" to R.raw.countdown_work_1,
            // DOC L0028: Esegue il passaggio esplicito «"rest_3" to R.raw.countdown_rest_3, "rest_2" to R.raw.countdown_rest_2, "rest_1" to R.raw.countdown_rest_1,» nel flusso: Riproduzione nativa dei nove WAV locali.
            "rest_3" to R.raw.countdown_rest_3, "rest_2" to R.raw.countdown_rest_2, "rest_1" to R.raw.countdown_rest_1,
            // DOC L0029: Esegue il passaggio esplicito «"whistle" to R.raw.work_start, "sigh" to R.raw.rest_start, "finish" to R.raw.workout_finish» nel flusso: Riproduzione nativa dei nove WAV locali.
            "whistle" to R.raw.work_start, "sigh" to R.raw.rest_start, "finish" to R.raw.workout_finish
        // DOC L0030: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
        ).forEach { (key, resource) -> samples[key] = pool.load(context, resource, 1) }
    // DOC L0031: Chiude il costrutto aperto alla riga 21.
    }
    // DOC L0032: play: Seleziona il campione 3, 2, 1 e lo riproduce attraverso SoundPool.
    fun play(second: Int, phase: String = "PREPARE"): Int {
        // DOC L0033: key: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di play.
        val key = hornSample(second, phase) ?: return 0
        // DOC L0034: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di play.
        return playCue(key)
    // DOC L0035: Chiude il costrutto aperto alla riga 32.
    }
    // DOC L0036: playCue: Riproduce il campione richiesto quando caricato, con una sola voce e il guadagno previsto.
    fun playCue(key: String): Int {
        // DOC L0037: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di playCue.
        if (!isReady) return 0
        // DOC L0038: sample: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di playCue.
        val sample = samples[key] ?: return 0
        // DOC L0039: Esegue il passaggio esplicito «stop()» nel flusso: Riproduzione nativa dei nove WAV locali. Nel contesto di playCue.
        stop()
        // DOC L0040: volume: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di playCue.
        val volume = cueVolume(key)
        // DOC L0041: Prepara o riproduce il segnale audio; il motore del tempo rimane indipendente dalla riproduzione. Nel contesto di playCue.
        stream = pool.play(sample, volume, volume, 1, 0, 1f)
        // DOC L0042: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di playCue.
        return stream
    // DOC L0043: Chiude il costrutto aperto alla riga 36.
    }
    // DOC L0044: stop: Ferma lo stream SoundPool attivo senza rilasciare il pool.
    fun stop() { if (stream != 0) pool.stop(stream); stream = 0 }
    // DOC L0045: release: Ferma il segnale e libera definitivamente SoundPool.
    fun release() { stop(); isReady = false; pool.release() }
// DOC L0046: Chiude il costrutto aperto alla riga 10.
}
