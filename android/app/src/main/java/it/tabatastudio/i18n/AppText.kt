package it.tabatastudio.i18n

import android.app.Application
import android.content.Context
import android.content.res.Resources
import androidx.core.content.edit
import android.content.res.Configuration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Locale

/** Application language shared by Compose and notifications. Saved names are never translated. */
object AppText {
    private lateinit var app: Application
    private val slots = Regex("\\{(\\d+)\\}")
    private val localizedResources = mutableMapOf<String, Resources>()
    var language by mutableStateOf("it"); private set
    val locale: Locale get() = Locale.forLanguageTag(when (language) { "en" -> "en-GB"; "es" -> "es-ES"; else -> "it-IT" })
    fun initialLanguage(saved: String?, device: String): String = saved?.takeIf { it in listOf("it", "en", "es") } ?: device.lowercase().substringBefore('-').substringBefore('_').takeIf { it in listOf("it", "en", "es") } ?: "it"
    fun initialize(application: Application) {
        app = application
        val deviceLocales = app.resources.configuration.locales
        val device = (0 until deviceLocales.size()).map { deviceLocales[it].language }.firstOrNull { it in listOf("it", "en", "es") } ?: Locale.getDefault().language
        language = initialLanguage(app.getSharedPreferences("tabata-appearance", Context.MODE_PRIVATE).getString("language", null), device)
    }
    fun choose(value: String) {
        if (value !in listOf("it", "en", "es")) return
        app.getSharedPreferences("tabata-appearance", Context.MODE_PRIVATE).edit { putString("language", value) }
        language = value
    }
    private fun resources(): Resources = localizedResources.getOrPut(language) { app.createConfigurationContext(Configuration(app.resources.configuration).apply { setLocale(AppText.locale) }).resources }
    private val patterns by lazy {
        messageIds.keys.filter { slots.containsMatchIn(it) && Regex("[A-Za-zÀ-ÿ]").containsMatchIn(it.replace(slots, "")) }.sortedByDescending { it.length }.map { source ->
            val indices = mutableListOf<Int>()
            val pattern = StringBuilder("^"); var start = 0
            slots.findAll(source).forEach { match ->
                pattern.append(Regex.escape(source.substring(start, match.range.first))).append("([\\s\\S]*?)"); indices.add(match.groupValues[1].toInt()); start = match.range.last + 1
            }
            pattern.append(Regex.escape(source.substring(start))).append("$")
            Triple(source, Regex(pattern.toString()), indices)
        }
    }
    fun text(source: String?, vararg values: Any?): String {
        val input = source ?: ""
        val id = messageIds[input]
        if (id != null) {
            val args = values.toMutableList()
            if (args.isNotEmpty() && (input.contains(": {0}") || input.startsWith("{0}:"))) args[0] = text(args[0]?.toString())
            return slots.replace(resources().getString(id)) { m -> args.getOrNull(m.groupValues[1].toInt())?.toString() ?: m.value }
        }
        if (language != "it" && values.isEmpty()) for ((key, regex, indices) in patterns) {
            val match = regex.matchEntire(input) ?: continue
            val args = arrayOfNulls<String>(indices.maxOrNull()!! + 1)
            indices.forEachIndexed { n, index -> args[index] = match.groupValues[n + 1] }
            return text(key, *args)
        }
        return input
    }
}
