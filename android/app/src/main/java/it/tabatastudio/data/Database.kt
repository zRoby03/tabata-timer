// DOC L0001: Assegna il file al namespace it.tabatastudio.data, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio.data

// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import android.content.Context. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.content.Context
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import androidx.room.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.room.*
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.domain.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.domain.*
// DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import kotlinx.coroutines.sync.Mutex. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import kotlinx.coroutines.sync.Mutex
// DOC L0007: Rende disponibili le dipendenze esplicite di questa riga: import kotlinx.coroutines.sync.withLock. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import kotlinx.coroutines.sync.withLock
// DOC L0008: Rende disponibili le dipendenze esplicite di questa riga: import org.json.JSONObject. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import org.json.JSONObject

// DOC L0010: Applica l’annotazione dichiarata: Compose identifica UI, Room genera accessi e schema, mentre le annotazioni Test selezionano i casi da eseguire.
@Entity(tableName = "workout")
// DOC L0011: Dichiara il tipo o contenitore di questo modulo: Persistenza Room/SQLite e repository transazionale. I campi e i metodi sono descritti nelle righe successive.
data class WorkoutEntity(@PrimaryKey val id: String, val json: String)
// DOC L0012: Applica l’annotazione dichiarata: Compose identifica UI, Room genera accessi e schema, mentre le annotazioni Test selezionano i casi da eseguire.
@Entity(tableName = "session_record")
// DOC L0013: Dichiara il tipo o contenitore di questo modulo: Persistenza Room/SQLite e repository transazionale. I campi e i metodi sono descritti nelle righe successive.
data class SessionEntity(@PrimaryKey val id: String, val json: String)
// DOC L0014: Applica l’annotazione dichiarata: Compose identifica UI, Room genera accessi e schema, mentre le annotazioni Test selezionano i casi da eseguire.
@Entity(tableName = "session_checkpoint")
// DOC L0015: Dichiara il tipo o contenitore di questo modulo: Persistenza Room/SQLite e repository transazionale. I campi e i metodi sono descritti nelle righe successive.
data class CheckpointEntity(@PrimaryKey val slot: Int = 1, val json: String)
// DOC L0016: Applica l’annotazione dichiarata: Compose identifica UI, Room genera accessi e schema, mentre le annotazioni Test selezionano i casi da eseguire.
@Entity(tableName = "setting")
// DOC L0017: Dichiara il tipo o contenitore di questo modulo: Persistenza Room/SQLite e repository transazionale. I campi e i metodi sono descritti nelle righe successive.
data class SettingEntity(@PrimaryKey val key: String, val value: String)

// DOC L0019: Applica l’annotazione dichiarata: Compose identifica UI, Room genera accessi e schema, mentre le annotazioni Test selezionano i casi da eseguire.
@Dao
// DOC L0020: Dichiara il tipo o contenitore di questo modulo: Persistenza Room/SQLite e repository transazionale. I campi e i metodi sono descritti nelle righe successive.
interface StudioDao {
    // DOC L0021: workouts: Legge le righe workout in ordine di inserimento; il repository decodifica poi ciascun JSON.
    @Query("SELECT * FROM workout ORDER BY rowid") suspend fun workouts(): List<WorkoutEntity>
    // DOC L0022: sessions: Legge la cronologia session_record in ordine di inserimento, mantenendola distinta dai programmi.
    @Query("SELECT * FROM session_record ORDER BY rowid") suspend fun sessions(): List<SessionEntity>
    // DOC L0023: saveWorkout: Esegue un upsert della riga programma: un ID esistente viene aggiornato senza duplicarlo.
    @Upsert suspend fun saveWorkout(value: WorkoutEntity)
    // DOC L0024: saveSession: Esegue un upsert del risultato per ID, così un secondo tentativo di salvataggio resta idempotente.
    @Upsert suspend fun saveSession(value: SessionEntity)
    // DOC L0025: checkpoint: Sovrascrive l’unico checkpoint, oppure legge quello esistente nel DAO.
    @Upsert suspend fun checkpoint(value: CheckpointEntity)
    // DOC L0026: checkpoint: Sovrascrive l’unico checkpoint, oppure legge quello esistente nel DAO.
    @Query("SELECT * FROM session_checkpoint WHERE slot=1") suspend fun checkpoint(): CheckpointEntity?
    // DOC L0027: clearCheckpoint: Svuota il solo checkpoint, dopo il recupero o il completamento atomico della sessione.
    @Query("DELETE FROM session_checkpoint") suspend fun clearCheckpoint()
    // DOC L0028: clearWorkouts: Svuota la tabella programmi dentro il percorso esplicito di sostituzione dell’archivio.
    @Query("DELETE FROM workout") suspend fun clearWorkouts()
    // DOC L0029: clearSessions: Svuota la tabella cronologia dentro il percorso esplicito di sostituzione dell’archivio.
    @Query("DELETE FROM session_record") suspend fun clearSessions()
    // DOC L0030: deleteWorkout: Elimina soltanto il programma indicato; non cancella la cronologia che ne conserva il titolo.
    @Query("DELETE FROM workout WHERE id=:id") suspend fun deleteWorkout(id: String)
    // DOC L0031: deleteSession: Elimina soltanto la sessione il cui ID è passato come parametro SQL Room.
    @Query("DELETE FROM session_record WHERE id=:id") suspend fun deleteSession(id: String)
    // DOC L0032: initialized: Legge il marcatore di inizializzazione, per inserire i preset soltanto al primo avvio.
    @Query("SELECT value FROM setting WHERE `key`='initialized'") suspend fun initialized(): String?
    // DOC L0033: setting: Salva il marcatore o l’impostazione nella tabella setting tramite upsert.
    @Upsert suspend fun setting(value: SettingEntity)
// DOC L0034: Chiude il costrutto aperto alla riga 20.
}
// DOC L0035: Applica l’annotazione dichiarata: Compose identifica UI, Room genera accessi e schema, mentre le annotazioni Test selezionano i casi da eseguire.
@Database(entities = [WorkoutEntity::class, SessionEntity::class, CheckpointEntity::class, SettingEntity::class], version = 1, exportSchema = true)
// DOC L0036: Dichiara il tipo o contenitore di questo modulo: Persistenza Room/SQLite e repository transazionale. I campi e i metodi sono descritti nelle righe successive.
abstract class StudioDatabase : RoomDatabase() {
    // DOC L0037: dao: Espone il DAO generato da Room per le query dichiarate.
    abstract fun dao(): StudioDao
    // DOC L0038: Esegue il passaggio esplicito «companion object {» nel flusso: Persistenza Room/SQLite e repository transazionale. Nel contesto di dao.
    companion object {
        // DOC L0039: open: Apre il database Room usando applicationContext, senza trattenere l’Activity.
        fun open(context: Context): StudioDatabase = Room.databaseBuilder(context.applicationContext, StudioDatabase::class.java, "tabata-studio.db").build()
    // DOC L0040: Chiude il costrutto aperto alla riga 38.
    }
// DOC L0041: Chiude il costrutto aperto alla riga 36.
}
// DOC L0042: Dichiara il tipo o contenitore di questo modulo: Persistenza Room/SQLite e repository transazionale. I campi e i metodi sono descritti nelle righe successive.
class StudioRepository(private val db: StudioDatabase) {
    // DOC L0043: dao: interfaccia Room generata per leggere e scrivere le tabelle. Nel contesto di open.
    private val dao = db.dao()
    // DOC L0044: mutex: esclusione reciproca tra operazioni del repository. Nel contesto di open.
    private val mutex = Mutex()
    // DOC L0045: initialize: Inizializza i preset una volta e recupera un checkpoint come sessione interrotta in una transazione.
    suspend fun initialize(): Boolean = mutex.withLock {
        // DOC L0046: recovered: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di initialize.
        var recovered = false
        // DOC L0047: Raggruppa le scritture Room in una transazione: o vengono applicate tutte, o nessuna. Nel contesto di initialize.
        db.withTransaction {
            // DOC L0048: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di initialize.
            if (dao.initialized() == null) { presets().forEach { dao.saveWorkout(WorkoutEntity(it.id, workoutJson(it).toString())) }; dao.setting(SettingEntity("initialized", "1")) }
            // DOC L0049: Lavora su una copia per mantenere distinta la bozza dal valore precedente. Nel contesto di initialize.
            dao.checkpoint()?.let { checkpoint -> val session = sessionFromJson(JSONObject(checkpoint.json)); dao.saveSession(SessionEntity(session.id, sessionJson(session.copy(status = "INTERRUPTED")).toString())); dao.clearCheckpoint(); recovered = true }
        // DOC L0050: Chiude il costrutto aperto alla riga 47.
        }
        // DOC L0051: Esegue il passaggio esplicito «recovered» nel flusso: Persistenza Room/SQLite e repository transazionale. Nel contesto di initialize.
        recovered
    // DOC L0052: Chiude il costrutto aperto alla riga 45.
    }
    // DOC L0053: read: Legge da Room programmi e sessioni e li deserializza nel modello condiviso.
    suspend fun read(): Backup = mutex.withLock { Backup(dao.workouts().map { workoutFromJson(JSONObject(it.json)) }, dao.sessions().map { sessionFromJson(JSONObject(it.json)) }) }
    // DOC L0054: save: Valida la timeline e scrive il programma nel database sotto mutex.
    suspend fun save(w: Workout) = mutex.withLock { compileTimeline(w); dao.saveWorkout(WorkoutEntity(w.id, workoutJson(w).toString())) }
    // DOC L0055: removeWorkout: Serializza la richiesta di eliminazione di un programma e la passa al DAO.
    suspend fun removeWorkout(id: String) = mutex.withLock { dao.deleteWorkout(id) }
    // DOC L0056: removeSession: Serializza la richiesta di eliminazione di una sessione e la passa al DAO.
    suspend fun removeSession(id: String) = mutex.withLock { dao.deleteSession(id) }
    // DOC L0057: checkpoint: Sovrascrive l’unico checkpoint, oppure legge quello esistente nel DAO.
    suspend fun checkpoint(s: Session) = mutex.withLock { dao.checkpoint(CheckpointEntity(json = sessionJson(s).toString())) }
    // DOC L0058: finish: Salva il risultato finale e cancella il checkpoint nella stessa transazione.
    suspend fun finish(s: Session) = mutex.withLock { db.withTransaction { dao.saveSession(SessionEntity(s.id, sessionJson(s).toString())); dao.clearCheckpoint() } }
    // DOC L0059: replace: Sostituisce programmi e sessioni atomicamente dopo la conferma dell’importazione o della cancellazione.
    suspend fun replace(backup: Backup) = mutex.withLock {
        // DOC L0060: Raggruppa le scritture Room in una transazione: o vengono applicate tutte, o nessuna. Nel contesto di replace.
        db.withTransaction {
            // DOC L0061: Esegue il passaggio esplicito «dao.clearWorkouts(); dao.clearSessions(); dao.clearCheckpoint()» nel flusso: Persistenza Room/SQLite e repository transazionale. Nel contesto di replace.
            dao.clearWorkouts(); dao.clearSessions(); dao.clearCheckpoint()
            // DOC L0062: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di replace.
            backup.workouts.forEach { dao.saveWorkout(WorkoutEntity(it.id, workoutJson(it).toString())) }
            // DOC L0063: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di replace.
            backup.sessions.forEach { dao.saveSession(SessionEntity(it.id, sessionJson(it).toString())) }
            // DOC L0064: Esegue il passaggio esplicito «dao.setting(SettingEntity("initialized", "1"))» nel flusso: Persistenza Room/SQLite e repository transazionale. Nel contesto di replace.
            dao.setting(SettingEntity("initialized", "1"))
        // DOC L0065: Chiude il costrutto aperto alla riga 60.
        }
    // DOC L0066: Chiude il costrutto aperto alla riga 59.
    }
// DOC L0067: Chiude il costrutto aperto alla riga 42.
}
