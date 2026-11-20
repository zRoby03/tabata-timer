import { object, text, id, integer } from './model.js';
import { compileTimeline } from './timeline.js';
import { MODES, modeOf } from './modes.js';
export const MAX_BACKUP_BYTES = 5 * 1024 * 1024;
function timestamp(value, path) {
  if (typeof value !== 'string' || !/^\d{4}-\d\d-\d\dT\d\d:\d\d:\d\d(?:\.\d{1,3})?Z$/.test(value) || !Number.isFinite(Date.parse(value)) || new Date(value).toISOString().slice(0, 19) !== value.slice(0, 19)) throw new Error(`${path}: data UTC non valida`);
}
export function validateSession(s) {
  object(s, 'Sessione'); id(s.id, 'session.id'); id(s.workoutId, 'workoutId');
  if (s.mode !== undefined && !Object.hasOwn(MODES, s.mode)) throw new Error('Modalità sessione non valida');
  if (s.workoutTitle !== undefined) text(s.workoutTitle, 'Nome storico', 120, true);
  timestamp(s.startedAt, 'Inizio'); timestamp(s.endedAt, 'Fine');
  if (Date.parse(s.endedAt) < Date.parse(s.startedAt)) throw new Error('Fine precedente all’inizio');
  integer(s.plannedMs, 'Pianificato', 1, 86400000); integer(s.elapsedMs, 'Effettivo', 0, Number.MAX_SAFE_INTEGER); integer(s.workMs, 'Lavoro', 0, s.elapsedMs);
  integer(s.plannedWorkIntervals, 'Intervalli pianificati', 1, 10000); integer(s.completedWorkIntervals, 'Intervalli completati', 0, s.plannedWorkIntervals); integer(s.pauses, 'Pause', 0, 2147483647);
  if (s.completedPhases !== undefined || s.plannedPhases !== undefined) { integer(s.plannedPhases, 'Fasi pianificate', s.plannedWorkIntervals, 10000); integer(s.completedPhases, 'Fasi completate', s.completedWorkIntervals, s.plannedPhases); }
  if (!['COMPLETED', 'INTERRUPTED'].includes(s.status) || (s.status === 'COMPLETED' && s.completedWorkIntervals !== s.plannedWorkIntervals)) throw new Error('Stato sessione non valido');
  return s;
}
export function parseBackup(raw) {
  if (typeof raw !== 'string' || new TextEncoder().encode(raw).length > MAX_BACKUP_BYTES) throw new Error('Backup troppo grande: massimo 5 MiB');
  let value; try { value = JSON.parse(raw); } catch { throw new Error('JSON non valido'); }
  object(value, 'Backup'); if (value.schemaVersion !== 1) throw new Error('Versione backup non supportata'); timestamp(value.exportedAt, 'Esportazione');
  if (!Array.isArray(value.workouts) || value.workouts.length > 500 || !Array.isArray(value.sessions) || value.sessions.length > 10000) throw new Error('Collezioni backup non valide o troppo grandi');
  for (const [items, validate] of [[value.workouts, compileTimeline], [value.sessions, validateSession]]) {
    const ids = new Set(); for (const item of items) { validate(item); if (ids.has(item.id)) throw new Error('ID duplicato nel backup'); ids.add(item.id); }
  }
  return value;
}
export const makeBackup = data => JSON.stringify({ schemaVersion: 1, exportedAt: new Date().toISOString(), workouts: data.workouts, sessions: data.sessions }, null, 2);
export function sessionsCsv(sessions) {
  const fields = ['id', 'mode', 'workoutId', 'workoutTitle', 'startedAt', 'endedAt', 'status', 'plannedMs', 'elapsedMs', 'workMs', 'completedWorkIntervals', 'plannedWorkIntervals', 'completedPhases', 'plannedPhases', 'pauses'];
  // Apostrophe guards spreadsheet formula injection when users choose to open CSV in a spreadsheet.
  const cell = value => `"${String(value ?? '').replace(/^[=+@\-\t\r]/, "'$&").replaceAll('"', '""')}"`;
  return '\uFEFF' + [fields.map(cell).join(','), ...sessions.map(s => fields.map(f => cell(f === 'mode' ? modeOf(s) : s[f])).join(','))].join('\r\n');
}
export function download(content, filename, type) {
  const url = URL.createObjectURL(new Blob([content], { type })), a = document.createElement('a'); a.href = url; a.download = filename; a.click(); setTimeout(() => URL.revokeObjectURL(url), 1000);
}
