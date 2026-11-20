// DOC L0001: completion: Calcola la percentuale di intervalli di lavoro completati rispetto a quelli pianificati.
export const completion = session => session.plannedWorkIntervals ? session.completedWorkIntervals / session.plannedWorkIntervals * 100 : 0;
// DOC L0002: aggregate: Filtra le sessioni per periodo e programma e calcola totali, media di completamento e settimane UTC.
export function aggregate(sessions, days = 30, workoutId = '', now = new Date()) {
  // DOC L0003: end: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di aggregate.
  const end = now.getTime(), start = end - days * 86400000;
  // DOC L0004: filtered: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga.
  const filtered = sessions.filter(s => Date.parse(s.startedAt) >= start && Date.parse(s.startedAt) <= end && (!workoutId || s.workoutId === workoutId)).sort((a, b) => a.startedAt.localeCompare(b.startedAt));
  // DOC L0005: buckets: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di aggregate.
  const buckets = new Map();
  // DOC L0006: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di aggregate.
  for (let t = start; t <= end; t += 86400000) { const d = monday(new Date(t)); if (!buckets.has(d)) buckets.set(d, { week: d, count: 0, workMs: 0, completion: 0 }); }
  // DOC L0007: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di aggregate.
  for (const s of filtered) { const key = monday(new Date(s.startedAt)); const b = buckets.get(key) ?? { week: key, count: 0, workMs: 0, completion: 0 }; b.count++; b.workMs += s.workMs; b.completion += completion(s); buckets.set(key, b); }
  // DOC L0008: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Restituisce il valore calcolato o conclude il percorso della funzione.
  return { sessions: filtered, weeks: [...buckets.values()].map(b => ({ ...b, completion: b.count ? b.completion / b.count : 0 })), count: filtered.length, workMs: filtered.reduce((sum, s) => sum + s.workMs, 0), completion: filtered.length ? filtered.reduce((sum, s) => sum + completion(s), 0) / filtered.length : 0 };
// DOC L0009: Chiude il costrutto aperto alla riga 2.
}
// DOC L0010: monday: Determina il lunedì della settimana UTC per un raggruppamento uguale sui due client.
function monday(date) {
  // DOC L0011: d: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di monday.
  const d = new Date(Date.UTC(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate()));
  // DOC L0012: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di monday.
  d.setUTCDate(d.getUTCDate() - (d.getUTCDay() + 6) % 7); return d.toISOString().slice(0, 10);
// DOC L0013: Chiude il costrutto aperto alla riga 10.
}
