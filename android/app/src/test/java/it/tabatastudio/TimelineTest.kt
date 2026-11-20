package it.tabatastudio

import it.tabatastudio.domain.*
import org.junit.Assert.*
import org.junit.Test

class TimelineTest {
    private val sample = Workout(id = "w", blocks = listOf(Block(id = "b", cycles = 2, exercises = listOf(Exercise(id = "e", repetitions = 3, workSec = 20)))))
    @Test fun reference220() { assertEquals(220000L, plannedMs(compileTimeline(sample))) }
    @Test fun noTrailingRest() {
        val one = sample.copy(blocks = listOf(sample.blocks[0].copy(cycles = 1, exercises = listOf(sample.blocks[0].exercises[0].copy(repetitions = 1)))))
        assertEquals(listOf("WORK"), compileTimeline(one).map { it.type })
    }
    @Test fun limits() {
        listOf(sample.copy(schemaVersion = 2), sample.copy(warmupSec = -1), sample.copy(blocks = emptyList()), sample.copy(blocks = listOf(sample.blocks[0].copy(cycles = 101))), sample.copy(blocks = listOf(sample.blocks[0].copy(id = "w")))).forEach { w ->
            assertThrows(IllegalArgumentException::class.java) { compileTimeline(w) }
        }
    }
    @Test fun expansionBounded() {
        val w = sample.copy(blocks = listOf(sample.blocks[0].copy(cycles = 100, exercises = listOf(sample.blocks[0].exercises[0].copy(repetitions = 100)))))
        assertThrows(IllegalArgumentException::class.java) { compileTimeline(w) }
    }
}
