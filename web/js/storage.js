export const STORAGE_KEY = 'tabata:v1:data';
export class LocalRepository {
  constructor(storage, onWarning = () => {}) { this.storage = storage; this.onWarning = onWarning; this.persistent = false; this.data = { workouts: [], sessions: [] }; }
  load(validate) {
    try {
      const raw = this.storage?.getItem(STORAGE_KEY);
      if (raw) { const valid = validate(raw); this.data = { workouts: valid.workouts, sessions: valid.sessions }; this.persistent = true; }
    } catch (e) { this.onWarning(`Archivio non disponibile: ${e.message}. Uso la memoria temporanea; l’archivio precedente è stato conservato.`); }
    return this.data;
  }
  save() {
    if (!this.persistent) return true;
    try { this.storage.setItem(STORAGE_KEY, JSON.stringify({ schemaVersion: 1, exportedAt: new Date().toISOString(), ...this.data })); return true; }
    catch { this.onWarning('Salvataggio non riuscito. I dati restano in memoria: esporta una copia JSON prima di chiudere.'); return false; }
  }
  enable() { const previous = this.persistent; this.persistent = true; if (!this.save()) { this.persistent = previous; return false; } return true; }
  disable() {
    try { this.storage?.removeItem(STORAGE_KEY); this.persistent = false; return true; }
    catch { this.onWarning('Impossibile rimuovere il salvataggio precedente. Usa le impostazioni del browser prima di passare alla modalità temporanea.'); return false; }
  }
  putWorkout(workout) { const i = this.data.workouts.findIndex(w => w.id === workout.id); if (i < 0) this.data.workouts.push(workout); else this.data.workouts[i] = workout; }
  upsertWorkout(workout) { this.putWorkout(workout); return this.save(); }
  saveWorkoutLocally(workout) { this.putWorkout(workout); return this.enable(); }
  addSession(session) { if (!this.data.sessions.some(s => s.id === session.id)) this.data.sessions.push(session); return this.save(); }
}
