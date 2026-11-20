package it.tabatastudio.domain

import java.time.*
import java.time.temporal.TemporalAdjusters

fun completion(s: Session): Double = s.completedWorkIntervals * 100.0 / s.plannedWorkIntervals
data class Week(val date: String, val count: Int, val workMs: Long, val completion: Double)
data class Statistics(val sessions: List<Session>, val weeks: List<Week>) {
    val workMs get() = sessions.sumOf { it.workMs }
    val completion get() = if (sessions.isEmpty()) 0.0 else sessions.map(::completion).average()
}
fun aggregate(sessions: List<Session>, days: Int, workoutId: String = "", now: Instant = Instant.now()): Statistics {
    val start = now.minusSeconds(days * 86400L)
    val filtered = sessions.filter { val d = Instant.parse(it.startedAt); d >= start && d <= now && (workoutId.isEmpty() || it.workoutId == workoutId) }.sortedBy { it.startedAt }
    fun monday(instant: Instant) = instant.atZone(ZoneOffset.UTC).toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val grouped = filtered.groupBy { monday(Instant.parse(it.startedAt)) }
    val weeks = generateSequence(monday(start)) { it.plusWeeks(1) }.takeWhile { it <= monday(now) }.map { d ->
        val list = grouped[d].orEmpty(); Week(d.toString(), list.size, list.sumOf { it.workMs }, if (list.isEmpty()) 0.0 else list.map(::completion).average())
    }.toList()
    return Statistics(filtered, weeks)
}
