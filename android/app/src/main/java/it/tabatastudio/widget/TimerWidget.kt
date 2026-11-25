package it.tabatastudio.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.edit
import it.tabatastudio.MainActivity
import it.tabatastudio.R
import it.tabatastudio.TabataApplication
import it.tabatastudio.domain.*
import it.tabatastudio.i18n.AppText
import it.tabatastudio.timer.TimerService
import it.tabatastudio.ui.studioPalettes

private const val MAX_ROUNDS = 20
private const val ACTION = "it.tabatastudio.widget.CONTROL"
private const val COMMAND = "command"

internal data class WidgetTimes(val mode: String, val focus: Int, val rest: Int, val rounds: Int) {
    val step get() = if (mode == "STUDIO") STUDIO_MIN_SECONDS else 1
    val minimum get() = if (mode == "STUDIO") STUDIO_MIN_SECONDS else WORKOUT_MIN_SECONDS

    fun adjust(breakSelected: Boolean, direction: Int): WidgetTimes {
        val current = if (breakSelected) rest else focus
        val adjusted = (current + direction * step).coerceIn(minimum, 3600)
        return if (breakSelected) copy(rest = adjusted) else copy(focus = adjusted)
    }

    fun workout(): Workout {
        val base = newQuickWorkout(mode).copy(title = AppText.text("Timer rapido"))
        if (base.studio) return base.copy(activities = listOf(
            base.activities!!.first().copy(title = AppText.text("Concentrazione"), concentrationSec = focus, sessions = rounds, pauseSec = rest)
        ))
        val block = base.blocks.first()
        val exercise = block.exercises.first().copy(title = AppText.text("Lavoro"), workSec = focus, restBetweenRepsSec = rest, repetitions = rounds)
        return base.copy(blocks = listOf(block.copy(exercises = listOf(exercise))))
    }
}

private class WidgetSettings(context: Context, private val id: Int) {
    private val prefs = context.getSharedPreferences("tabata-widget-$id", Context.MODE_PRIVATE)
    var mode: String
        get() = prefs.getString("mode", "WORKOUT") ?: "WORKOUT"
        set(value) { prefs.edit { putString("mode", value) } }
    var breakSelected: Boolean
        get() = prefs.getBoolean("break", false)
        set(value) { prefs.edit { putBoolean("break", value) } }
    fun times(mode: String = this.mode): WidgetTimes {
        val studio = mode == "STUDIO"
        return WidgetTimes(mode, prefs.getInt("$mode-focus", if (studio) 1500 else 20),
            prefs.getInt("$mode-rest", if (studio) 300 else 10), prefs.getInt("$mode-rounds", if (studio) 4 else 8))
    }
    fun save(times: WidgetTimes) = prefs.edit {
        putInt("${times.mode}-focus", times.focus)
        putInt("${times.mode}-rest", times.rest)
        putInt("${times.mode}-rounds", times.rounds)
    }
}

class TimerWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        val app = context.applicationContext as TabataApplication
        ids.forEach { manager.updateAppWidget(it, views(app, manager, it)) }
    }

    override fun onAppWidgetOptionsChanged(context: Context, manager: AppWidgetManager, id: Int, options: Bundle) {
        onUpdate(context, manager, intArrayOf(id))
    }

    override fun onDeleted(context: Context, ids: IntArray) {
        ids.forEach { context.deleteSharedPreferences("tabata-widget-$it") }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION) {
            super.onReceive(context, intent)
            return
        }
        val app = context.applicationContext as TabataApplication
        val manager = AppWidgetManager.getInstance(app)
        val id = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        if (id !in manager.getAppWidgetIds(ComponentName(app, TimerWidget::class.java))) return
        val command = intent.getStringExtra(COMMAND)
        val settings = WidgetSettings(app, id)
        val runtime = app.timerRuntime
        if (runtime.active) {
            val engine = runtime.timer
            if (engine != null && intent.getStringExtra(TimerService.SESSION) == engine.sessionId) when (command) {
                "pause" -> runtime.pause()
                "resume" -> runtime.resume()
                "stop" -> if (engine.state == "paused") runtime.stop()
            }
        } else when (command) {
            "workout" -> settings.mode = "WORKOUT"
            "studio" -> settings.mode = "STUDIO"
            "focus" -> settings.breakSelected = false
            "break" -> settings.breakSelected = true
            "less", "more" -> settings.save(settings.times().adjust(settings.breakSelected, if (command == "more") 1 else -1))
            "round-less", "round-more" -> {
                val times = settings.times()
                val direction = if (command == "round-more") 1 else -1
                settings.save(times.copy(rounds = (times.rounds + direction).coerceIn(1, MAX_ROUNDS)))
            }
            "start" -> {
                val times = settings.times()
                val workout = times.workout().let { if (settings.breakSelected) it.copy(title = AppText.text("Pausa singola")) else it }
                runCatching {
                    runtime.start(workout, times.rest.takeIf { settings.breakSelected })
                }.onFailure {
                    Toast.makeText(app, AppText.text("Apri Tabata Timer per avviare la sessione."), Toast.LENGTH_SHORT).show()
                }
            }
        }
        refresh(app, force = true)
    }

    companion object {
        private var lastFrame: String? = null
        fun refresh(app: TabataApplication, force: Boolean = false) {
            val runtime = app.timerRuntime
            val engine = runtime.timer
            val seconds = ((engine?.remainingMs ?: 0L) + 999) / 1000
            val frame = "${engine?.sessionId}:${engine?.state}:${engine?.phaseRun}:$seconds:${runtime.starting}:${AppText.language}"
            if (!force && frame == lastFrame) return
            lastFrame = frame
            val manager = AppWidgetManager.getInstance(app)
            manager.getAppWidgetIds(ComponentName(app, TimerWidget::class.java)).forEach {
                manager.updateAppWidget(it, views(app, manager, it))
            }
        }

        private fun views(app: TabataApplication, manager: AppWidgetManager, id: Int): RemoteViews {
            val settings = WidgetSettings(app, id)
            val runtime = app.timerRuntime
            val engine = runtime.timer?.takeIf { it.state == "running" || it.state == "paused" }
            val locked = runtime.active
            val paused = engine?.state == "paused"
            val mode = engine?.workout?.mode ?: settings.mode
            val times = settings.times(mode)
            val stage = engine?.currentPhase
            val breakSelected = if (engine != null) stage?.type?.startsWith("REST") == true else settings.breakSelected
            val time = formatTime(engine?.remainingMs ?: (if (breakSelected) times.rest else times.focus) * 1000L)
            val (before, after) = time.substringBeforeLast(':') to time.substringAfterLast(':')
            val rounds = if (engine != null && !engine.standaloneBreak) {
                val current = engine.completedWorkIntervals + if (stage?.type == "WORK") 1 else 0
                "$current/${engine.workIntervals}"
            } else times.rounds.toString()
            val minWidth = manager.getAppWidgetOptions(id).getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
            val textSize = if (before.length > 2) 18f else if (minWidth in 1..275) 25f else 30f
            val paletteId = app.getSharedPreferences("tabata-appearance", Context.MODE_PRIVATE).getString("palette", "purple")
            val palette = studioPalettes.firstOrNull { it.id == paletteId } ?: studioPalettes.first()
            val accent = palette.darkFill.toArgb()
            val ink = palette.darkOnFill.toArgb()
            return RemoteViews(app.packageName, R.layout.timer_widget).apply {
                fun text(view: Int, value: String) = setTextViewText(view, value)
                fun command(view: Int, value: String?, label: String, session: String? = null) {
                    setContentDescription(view, AppText.text(label))
                    val pending = value?.let {
                        val intent = Intent(app, TimerWidget::class.java).setAction(ACTION)
                            .setData(Uri.parse("tabata://widget/$id/$it/${session.orEmpty()}"))
                            .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id).putExtra(COMMAND, it)
                        if (session != null) intent.putExtra(TimerService.SESSION, session)
                        PendingIntent.getBroadcast(app, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                    }
                    setOnClickPendingIntent(view, pending)
                }
                fun selected(view: Int, active: Boolean, neutral: Boolean = false) {
                    setInt(view, "setBackgroundResource", if (active) R.drawable.timer_widget_selected else 0)
                    if (Build.VERSION.SDK_INT >= 31) {
                        val fill = if (neutral) Color.rgb(215, 222, 234) else accent
                        setColorStateList(view, "setBackgroundTintList", ColorStateList.valueOf(fill))
                    }
                    setTextColor(view, if (active) if (Build.VERSION.SDK_INT >= 31 && !neutral) ink else Color.rgb(32, 38, 50) else Color.WHITE)
                }
                val activity = PendingIntent.getActivity(app, 0,
                    Intent(app, MainActivity::class.java).setAction(TimerService.OPEN_TIMER)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                setOnClickPendingIntent(R.id.widget_logo, activity)
                setOnClickPendingIntent(R.id.widget_title, activity)
                setContentDescription(R.id.widget_logo, "Tabata Timer")
                if (Build.VERSION.SDK_INT >= 31) setColorStateList(R.id.widget_logo, "setBackgroundTintList", ColorStateList.valueOf(accent))
                if (Build.VERSION.SDK_INT >= 31) setTextColor(R.id.widget_logo, ink)
                text(R.id.widget_stop, AppText.text("Termina"))
                setViewVisibility(R.id.widget_stop, if (paused) View.VISIBLE else View.GONE)
                command(R.id.widget_stop, if (paused) "stop" else null, "Termina", engine?.sessionId)
                selected(R.id.widget_workout, mode == "WORKOUT", true)
                selected(R.id.widget_studio, mode == "STUDIO", true)
                selected(R.id.widget_focus, !breakSelected)
                selected(R.id.widget_break, breakSelected)
                command(R.id.widget_workout, if (locked) null else "workout", "Workout")
                command(R.id.widget_studio, if (locked) null else "studio", "Studio")
                command(R.id.widget_focus, if (locked) null else "focus", "Focus")
                command(R.id.widget_break, if (locked) null else "break", "Break")
                val running = engine?.state == "running"
                text(R.id.widget_play, if (running) "Ⅱ" else "▶")
                setTextColor(R.id.widget_play, accent)
                command(R.id.widget_play, when {
                    running -> "pause"
                    paused -> "resume"
                    locked -> null
                    else -> "start"
                }, when { running -> "Pausa (timer)"; paused -> "Riprendi"; else -> "Avvia" }, engine?.sessionId)
                text(R.id.widget_count, rounds)
                setTextViewTextSize(R.id.widget_count, TypedValue.COMPLEX_UNIT_SP, if (rounds.length > 4) 9f else 12f)
                command(R.id.widget_count_less, if (locked || times.rounds == 1) null else "round-less", "Riduci ripetizioni")
                command(R.id.widget_count_more, if (locked || times.rounds == MAX_ROUNDS) null else "round-more", "Aumenta ripetizioni")
                val configured = if (breakSelected) times.rest else times.focus
                command(R.id.widget_time_less, if (locked || configured == times.minimum) null else "less", "Riduci durata")
                command(R.id.widget_time_more, if (locked || configured == 3600) null else "more", "Aumenta durata")
                text(R.id.widget_time_before, before)
                text(R.id.widget_time_after, after)
                listOf(R.id.widget_time_before, R.id.widget_time_colon, R.id.widget_time_after).forEach {
                    setTextViewTextSize(it, TypedValue.COMPLEX_UNIT_SP, textSize)
                }
                text(R.id.widget_status, AppText.text(when {
                    paused -> "In pausa"
                    runtime.starting -> "Avvio del timer…"
                    stage != null -> stage.labelKey.takeIf { stage.type == "PREPARE" } ?: if (breakSelected) modeLabels(mode).rest else if (stage.type == "COOLDOWN") modeLabels(mode).final else modeLabels(mode).focus
                    else -> ""
                }))
            }
        }
    }
}
