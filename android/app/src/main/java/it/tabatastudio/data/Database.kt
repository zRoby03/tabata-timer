package it.tabatastudio.data

import android.content.Context
import androidx.room.*
import it.tabatastudio.domain.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONObject

@Entity(tableName = "workout")
data class WorkoutEntity(@PrimaryKey val id: String, val json: String)
@Entity(tableName = "session_record")
data class SessionEntity(@PrimaryKey val id: String, val json: String)
@Entity(tableName = "session_checkpoint")
data class CheckpointEntity(@PrimaryKey val slot: Int = 1, val json: String)
@Entity(tableName = "setting")
data class SettingEntity(@PrimaryKey val key: String, val value: String)

@Dao
interface StudioDao {
    @Query("SELECT * FROM workout ORDER BY rowid") suspend fun workouts(): List<WorkoutEntity>
    @Query("SELECT * FROM session_record ORDER BY rowid") suspend fun sessions(): List<SessionEntity>
    @Upsert suspend fun saveWorkout(value: WorkoutEntity)
    @Upsert suspend fun saveSession(value: SessionEntity)
    @Upsert suspend fun checkpoint(value: CheckpointEntity)
    @Query("SELECT * FROM session_checkpoint WHERE slot=1") suspend fun checkpoint(): CheckpointEntity?
    @Query("DELETE FROM session_checkpoint") suspend fun clearCheckpoint()
    @Query("DELETE FROM workout") suspend fun clearWorkouts()
    @Query("DELETE FROM session_record") suspend fun clearSessions()
    @Query("DELETE FROM workout WHERE id=:id") suspend fun deleteWorkout(id: String)
    @Query("DELETE FROM session_record WHERE id=:id") suspend fun deleteSession(id: String)
    @Query("SELECT value FROM setting WHERE `key`='initialized'") suspend fun initialized(): String?
    @Upsert suspend fun setting(value: SettingEntity)
}
@Database(entities = [WorkoutEntity::class, SessionEntity::class, CheckpointEntity::class, SettingEntity::class], version = 1, exportSchema = true)
abstract class StudioDatabase : RoomDatabase() {
    abstract fun dao(): StudioDao
    companion object {
        fun open(context: Context): StudioDatabase = Room.databaseBuilder(context.applicationContext, StudioDatabase::class.java, "tabata-studio.db").build()
    }
}
class StudioRepository(private val db: StudioDatabase) {
    private val dao = db.dao()
    private val mutex = Mutex()
    suspend fun initialize(): Boolean = mutex.withLock {
        var recovered = false
        db.withTransaction {
            if (dao.initialized() == null) { presets().forEach { dao.saveWorkout(WorkoutEntity(it.id, workoutJson(it).toString())) }; dao.setting(SettingEntity("initialized", "1")) }
            dao.checkpoint()?.let { checkpoint -> val session = sessionFromJson(JSONObject(checkpoint.json)); dao.saveSession(SessionEntity(session.id, sessionJson(session.copy(status = "INTERRUPTED")).toString())); dao.clearCheckpoint(); recovered = true }
        }
        recovered
    }
    suspend fun read(): Backup = mutex.withLock { Backup(dao.workouts().map { workoutFromJson(JSONObject(it.json)) }, dao.sessions().map { sessionFromJson(JSONObject(it.json)) }) }
    suspend fun save(w: Workout) = mutex.withLock { compileTimeline(w); dao.saveWorkout(WorkoutEntity(w.id, workoutJson(w).toString())) }
    suspend fun removeWorkout(id: String) = mutex.withLock { dao.deleteWorkout(id) }
    suspend fun removeSession(id: String) = mutex.withLock { dao.deleteSession(id) }
    suspend fun checkpoint(s: Session) = mutex.withLock { dao.checkpoint(CheckpointEntity(json = sessionJson(s).toString())) }
    suspend fun finish(s: Session) = mutex.withLock { db.withTransaction { dao.saveSession(SessionEntity(s.id, sessionJson(s).toString())); dao.clearCheckpoint() } }
    suspend fun replace(backup: Backup) = mutex.withLock {
        db.withTransaction {
            dao.clearWorkouts(); dao.clearSessions(); dao.clearCheckpoint()
            backup.workouts.forEach { dao.saveWorkout(WorkoutEntity(it.id, workoutJson(it).toString())) }
            backup.sessions.forEach { dao.saveSession(SessionEntity(it.id, sessionJson(it).toString())) }
            dao.setting(SettingEntity("initialized", "1"))
        }
    }
}
