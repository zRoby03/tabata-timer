// DOC L0001: Assegna il file al namespace it.tabatastudio.i18n, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio.i18n

// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import android.app.Application. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.app.Application
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import android.content.Context. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.content.Context
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import android.content.res.Resources. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.content.res.Resources
// DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import androidx.core.content.edit. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.core.content.edit
// DOC L0007: Rende disponibili le dipendenze esplicite di questa riga: import android.content.res.Configuration. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.content.res.Configuration
// DOC L0008: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.runtime.getValue. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.runtime.getValue
// DOC L0009: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.runtime.mutableStateOf. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.runtime.mutableStateOf
// DOC L0010: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.runtime.setValue. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.runtime.setValue
// DOC L0011: Rende disponibili le dipendenze esplicite di questa riga: import java.util.Locale. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import java.util.Locale

/** Application language shared by Compose and notifications. Saved names are never translated. */
// DOC L0014: Dichiara il tipo o contenitore di questo modulo: Localizzazione nativa condivisa. I campi e i metodi sono descritti nelle righe successive.
object AppText {
    // DOC L0015: Esegue il passaggio esplicito «private lateinit var app: Application» nel flusso: Localizzazione nativa condivisa.
    private lateinit var app: Application
    // DOC L0016: slots: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    private val slots = Regex("\\{(\\d+)\\}")
    // DOC L0017: localizedResources: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    private val localizedResources = mutableMapOf<String, Resources>()
    // DOC L0018: language: lingua corrente, mantenuta separata dall’archivio.
    var language by mutableStateOf("it"); private set
    // DOC L0019: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. locale: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
    val locale: Locale get() = Locale.forLanguageTag(when (language) { "en" -> "en-GB"; "es" -> "es-ES"; else -> "it-IT" })
    // DOC L0020: initialLanguage: Sceglie lingua salvata, lingua del dispositivo o italiano, senza accettare codici sconosciuti.
    fun initialLanguage(saved: String?, device: String): String = saved?.takeIf { it in listOf("it", "en", "es") } ?: device.lowercase().substringBefore('-').substringBefore('_').takeIf { it in listOf("it", "en", "es") } ?: "it"
    // DOC L0021: initialize: Inizializza lingua e preferenze usando soltanto l’Application.
    fun initialize(application: Application) {
        // DOC L0022: Esegue il passaggio esplicito «app = application» nel flusso: Localizzazione nativa condivisa. Nel contesto di initialize.
        app = application
        // DOC L0023: deviceLocales: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di initialize.
        val deviceLocales = app.resources.configuration.locales
        // DOC L0024: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. device: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di initialize.
        val device = (0 until deviceLocales.size()).map { deviceLocales[it].language }.firstOrNull { it in listOf("it", "en", "es") } ?: Locale.getDefault().language
        // DOC L0025: Legge il valore locale e applica i controlli/fallback riportati nell’espressione. Nel contesto di initialize.
        language = initialLanguage(app.getSharedPreferences("tabata-appearance", Context.MODE_PRIVATE).getString("language", null), device)
    // DOC L0026: Chiude il costrutto aperto alla riga 21.
    }
    // DOC L0027: choose: Aggiorna la lingua nativa osservabile, la preferenza e la configurazione delle risorse.
    fun choose(value: String) {
        // DOC L0028: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di choose.
        if (value !in listOf("it", "en", "es")) return
        // DOC L0029: Esegue il passaggio esplicito «app.getSharedPreferences("tabata-appearance", Context.MODE_PRIVATE).edit { putString("language", value) }» nel flusso: Localizzazione nativa condivisa. Nel contesto di choose.
        app.getSharedPreferences("tabata-appearance", Context.MODE_PRIVATE).edit { putString("language", value) }
        // DOC L0030: Esegue il passaggio esplicito «language = value» nel flusso: Localizzazione nativa condivisa. Nel contesto di choose.
        language = value
    // DOC L0031: Chiude il costrutto aperto alla riga 27.
    }
    // DOC L0032: resources: Riusa Resources configurate per lingua senza conservare riferimenti a un’Activity.
    private fun resources(): Resources = localizedResources.getOrPut(language) { app.createConfigurationContext(Configuration(app.resources.configuration).apply { setLocale(AppText.locale) }).resources }
    // DOC L0033: patterns: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di resources.
    private val patterns by lazy {
        // DOC L0034: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di resources.
        messageIds.keys.filter { slots.containsMatchIn(it) && Regex("[A-Za-zÀ-ÿ]").containsMatchIn(it.replace(slots, "")) }.sortedByDescending { it.length }.map { source ->
            // DOC L0035: indices: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di resources.
            val indices = mutableListOf<Int>()
            // DOC L0036: pattern: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. start: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di resources.
            val pattern = StringBuilder("^"); var start = 0
            // DOC L0037: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di resources.
            slots.findAll(source).forEach { match ->
                // DOC L0038: Esegue il passaggio esplicito «pattern.append(Regex.escape(source.substring(start, match.range.first))).append("([\\s\\S]*?)"); indices.add(match.groupValues[1].toInt()); start = match.range.last + 1» nel flusso: Localizzazione nativa condivisa. Nel contesto di resources.
                pattern.append(Regex.escape(source.substring(start, match.range.first))).append("([\\s\\S]*?)"); indices.add(match.groupValues[1].toInt()); start = match.range.last + 1
            // DOC L0039: Chiude il costrutto aperto alla riga 37.
            }
            // DOC L0040: Esegue il passaggio esplicito «pattern.append(Regex.escape(source.substring(start))).append("$")» nel flusso: Localizzazione nativa condivisa. Nel contesto di resources.
            pattern.append(Regex.escape(source.substring(start))).append("$")
            // DOC L0041: Esegue il passaggio esplicito «Triple(source, Regex(pattern.toString()), indices)» nel flusso: Localizzazione nativa condivisa. Nel contesto di resources.
            Triple(source, Regex(pattern.toString()), indices)
        // DOC L0042: Chiude il costrutto aperto alla riga 34.
        }
    // DOC L0043: Chiude il costrutto aperto alla riga 33.
    }
    // DOC L0044: text: Cerca la risorsa tradotta o adatta un messaggio parametrico di sistema, conservando gli argomenti utente.
    fun text(source: String?, vararg values: Any?): String {
        // DOC L0045: input: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di text.
        val input = source ?: ""
        // DOC L0046: id: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di text.
        val id = messageIds[input]
        // DOC L0047: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di text.
        if (id != null) {
            // DOC L0048: args: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di text.
            val args = values.toMutableList()
            // DOC L0049: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Nel contesto di text.
            if (args.isNotEmpty() && (input.contains(": {0}") || input.startsWith("{0}:"))) args[0] = text(args[0]?.toString())
            // DOC L0050: Legge il valore locale e applica i controlli/fallback riportati nell’espressione. Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di text.
            return slots.replace(resources().getString(id)) { m -> args.getOrNull(m.groupValues[1].toInt())?.toString() ?: m.value }
        // DOC L0051: Chiude il costrutto aperto alla riga 47.
        }
        // DOC L0052: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di text.
        if (language != "it" && values.isEmpty()) for ((key, regex, indices) in patterns) {
            // DOC L0053: match: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di text.
            val match = regex.matchEntire(input) ?: continue
            // DOC L0054: args: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di text.
            val args = arrayOfNulls<String>(indices.maxOrNull()!! + 1)
            // DOC L0055: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di text.
            indices.forEachIndexed { n, index -> args[index] = match.groupValues[n + 1] }
            // DOC L0056: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di text.
            return text(key, *args)
        // DOC L0057: Chiude il costrutto aperto alla riga 52.
        }
        // DOC L0058: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di text.
        return input
    // DOC L0059: Chiude il costrutto aperto alla riga 44.
    }
// DOC L0060: Chiude il costrutto aperto alla riga 14.
}
