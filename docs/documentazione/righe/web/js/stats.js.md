# web/js/stats.js

Statistiche aggregate delle sessioni web.

Raggruppa per settimane UTC e filtra intervallo e programma; considera tempo realmente svolto e completamento.
I numeri si riferiscono al sorgente originale; il collegamento apre esattamente quella riga. I commenti DOC nel branch sono aggiunte didattiche.
## Funzioni e callback
| Simbolo | Riga originale | Ruolo |
| --- | ---: | --- |
| completion | [1](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/stats.js#L1) | Calcola la percentuale di intervalli di lavoro completati rispetto a quelli pianificati. |
| aggregate | [2](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/stats.js#L2) | Filtra le sessioni per periodo e programma e calcola totali, media di completamento e settimane UTC. |
| aggregate.callback | [4](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/stats.js#L4) | Callback di sessions.filter; viene invocata dal chiamante nel contesto di aggregate. |
| aggregate.callback | [4](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/stats.js#L4) | Callback di sessions.filter(s =&gt; Date.parse(s.startedAt) &gt;= start &amp;&amp; Date.parse(s.startedAt) &lt;= end &amp;&amp; (!workoutId &#124;&#124; s.workoutId === workoutId)).sort; viene invocata dal chiamante nel contesto di aggregate. |
| aggregate.callback | [8](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/stats.js#L8) | Callback di [...buckets.values()].map; viene invocata dal chiamante nel contesto di aggregate. |
| aggregate.callback | [8](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/stats.js#L8) | Callback di filtered.reduce; viene invocata dal chiamante nel contesto di aggregate. |
| aggregate.callback | [8](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/stats.js#L8) | Callback di filtered.reduce; viene invocata dal chiamante nel contesto di aggregate. |
| monday | [10](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/stats.js#L10) | Determina il lunedì della settimana UTC per un raggruppamento uguale sui due client. |

## Ogni riga

| Riga originale | Codice (estratto) | Spiegazione |
| ---: | --- | --- |
| [1](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/stats.js#L1) | <code>export const completion = session =&gt; session.plannedWorkIntervals ? session.completedWorkIntervals / session.plannedWorkIntervals * 100 : 0;</code> | completion: Calcola la percentuale di intervalli di lavoro completati rispetto a quelli pianificati. |
| [2](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/stats.js#L2) | <code>export function aggregate(sessions, days = 30, workoutId = &#x27;&#x27;, now = new Date()) {</code> | aggregate: Filtra le sessioni per periodo e programma e calcola totali, media di completamento e settimane UTC. |
| [3](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/stats.js#L3) | <code>const end = now.getTime(), start = end - days * 86400000;</code> | end: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di aggregate. |
| [4](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/stats.js#L4) | <code>const filtered = sessions.filter(s =&gt; Date.parse(s.startedAt) &gt;= start &amp;&amp; Date.parse(s.startedAt) &lt;= end &amp;&amp; (!workoutId &#124;&#124; s.workoutId === workoutId)).sort((a, …</code> | filtered: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. |
| [5](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/stats.js#L5) | <code>const buckets = new Map();</code> | buckets: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di aggregate. |
| [6](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/stats.js#L6) | <code>for (let t = start; t &lt;= end; t += 86400000) { const d = monday(new Date(t)); if (!buckets.has(d)) buckets.set(d, { week: d, count: 0, workMs: 0, completion: 0 …</code> | Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di aggregate. |
| [7](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/stats.js#L7) | <code>for (const s of filtered) { const key = monday(new Date(s.startedAt)); const b = buckets.get(key) ?? { week: key, count: 0, workMs: 0, completion: 0 }; b.count+…</code> | Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di aggregate. |
| [8](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/stats.js#L8) | <code>return { sessions: filtered, weeks: [...buckets.values()].map(b =&gt; ({ ...b, completion: b.count ? b.completion / b.count : 0 })), count: filtered.length, workMs…</code> | Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Restituisce il valore calcolato o conclude il percorso della funzione. |
| [9](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/stats.js#L9) | <code>}</code> | Chiude il costrutto aperto alla riga 2. |
| [10](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/stats.js#L10) | <code>function monday(date) {</code> | monday: Determina il lunedì della settimana UTC per un raggruppamento uguale sui due client. |
| [11](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/stats.js#L11) | <code>const d = new Date(Date.UTC(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate()));</code> | d: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di monday. |
| [12](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/stats.js#L12) | <code>d.setUTCDate(d.getUTCDate() - (d.getUTCDay() + 6) % 7); return d.toISOString().slice(0, 10);</code> | Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di monday. |
| [13](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/stats.js#L13) | <code>}</code> | Chiude il costrutto aperto alla riga 10. |
