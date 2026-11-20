export const completion = session => session.plannedWorkIntervals ? session.completedWorkIntervals / session.plannedWorkIntervals * 100 : 0;
export function aggregate(sessions, days = 30, workoutId = '', now = new Date()) {
  const end = now.getTime(), start = end - days * 86400000;
  const filtered = sessions.filter(s => Date.parse(s.startedAt) >= start && Date.parse(s.startedAt) <= end && (!workoutId || s.workoutId === workoutId)).sort((a, b) => a.startedAt.localeCompare(b.startedAt));
  const buckets = new Map();
  for (let t = start; t <= end; t += 86400000) { const d = monday(new Date(t)); if (!buckets.has(d)) buckets.set(d, { week: d, count: 0, workMs: 0, completion: 0 }); }
  for (const s of filtered) { const key = monday(new Date(s.startedAt)); const b = buckets.get(key) ?? { week: key, count: 0, workMs: 0, completion: 0 }; b.count++; b.workMs += s.workMs; b.completion += completion(s); buckets.set(key, b); }
  return { sessions: filtered, weeks: [...buckets.values()].map(b => ({ ...b, completion: b.count ? b.completion / b.count : 0 })), count: filtered.length, workMs: filtered.reduce((sum, s) => sum + s.workMs, 0), completion: filtered.length ? filtered.reduce((sum, s) => sum + completion(s), 0) / filtered.length : 0 };
}
function monday(date) {
  const d = new Date(Date.UTC(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate()));
  d.setUTCDate(d.getUTCDate() - (d.getUTCDay() + 6) % 7); return d.toISOString().slice(0, 10);
}
