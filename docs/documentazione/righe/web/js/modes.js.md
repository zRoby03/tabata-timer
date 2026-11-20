# web/js/modes.js

Separazione Workout/Studio e conversione delle unità.

Un archivio senza mode viene interpretato come Workout; Studio usa fattore 60 per mostrare minuti mantenendo secondi interni.
I numeri si riferiscono al sorgente originale; il collegamento apre esattamente quella riga. I commenti DOC nel branch sono aggiunte didattiche.
## Funzioni e callback
| Simbolo | Riga originale | Ruolo |
| --- | ---: | --- |
| modeOf | [5](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/modes.js#L5) | Legge la modalità; i dati privi del campo mode appartengono a Workout. |
| isStudio | [6](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/modes.js#L6) | Determina se il programma appartiene alla modalità Studio. |
| unitFactor | [7](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/modes.js#L7) | Restituisce 60 per i minuti Studio e 1 per i secondi Workout. |
| modeData | [8](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/modes.js#L8) | Crea una vista dell’archivio contenente soltanto programmi e sessioni della modalità richiesta. |
| modeData.callback | [8](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/modes.js#L8) | Callback di data.workouts.filter; viene invocata dal chiamante nel contesto di modeData. |
| modeData.callback | [8](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/modes.js#L8) | Callback di data.sessions.filter; viene invocata dal chiamante nel contesto di modeData. |
| loadMode | [9](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/modes.js#L9) | Legge l’ultima modalità dal browser con fallback a Workout anche se lo storage è negato. |
| saveMode | [10](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/modes.js#L10) | Ricorda l’ultima modalità senza rendere inutilizzabile il timer se lo storage fallisce. |

## Ogni riga

| Riga originale | Codice (estratto) | Spiegazione |
| ---: | --- | --- |
| [1](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/modes.js#L1) | <code>export const MODES = {</code> | MODES: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. |
| [2](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/modes.js#L2) | <code>WORKOUT: { title: &#x27;Tabata Workout&#x27;, subtitle: &#x27;Allenamento a intervalli&#x27;, collection: &#x27;Allenamenti&#x27;, noun: &#x27;allenamento&#x27;, focus: &#x27;Lavoro&#x27;, rest: &#x27;Recupero&#x27;, rou…</code> | Esegue il passaggio esplicito «WORKOUT: { title: &#x27;Tabata Workout&#x27;, subtitle: &#x27;Allenamento a intervalli&#x27;, collection: &#x27;Allenamenti&#x27;, noun: &#x27;allenamento&#x27;, focus: &#x27;Lavoro&#x27;, rest: &#x27;Recupero&#x27;, rounds: &#x27;Giri&#x27;, final:» nel flusso: Separazione Workout/Studio e conversione delle unità. |
| [3](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/modes.js#L3) | <code>STUDIO: { title: &#x27;Tabata Studio&#x27;, subtitle: &#x27;Timer Pomodoro per studio e lavoro&#x27;, collection: &#x27;Programmi di studio&#x27;, noun: &#x27;programma di studio&#x27;, focus: &#x27;Concen…</code> | Esegue il passaggio esplicito «STUDIO: { title: &#x27;Tabata Studio&#x27;, subtitle: &#x27;Timer Pomodoro per studio e lavoro&#x27;, collection: &#x27;Programmi di studio&#x27;, noun: &#x27;programma di studio&#x27;, focus: &#x27;Concentrazione&#x27;, rest: &#x27;Pa» nel flusso: Separazione Workout/Studio e conversione delle unità. |
| [4](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/modes.js#L4) | <code>};</code> | Chiude il costrutto aperto alla riga 1. |
| [5](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/modes.js#L5) | <code>export const modeOf = value =&gt; value?.mode ?? &#x27;WORKOUT&#x27;;</code> | modeOf: Legge la modalità; i dati privi del campo mode appartengono a Workout. |
| [6](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/modes.js#L6) | <code>export const isStudio = value =&gt; modeOf(value) === &#x27;STUDIO&#x27;;</code> | isStudio: Determina se il programma appartiene alla modalità Studio. |
| [7](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/modes.js#L7) | <code>export const unitFactor = value =&gt; isStudio(value) ? 60 : 1;</code> | unitFactor: Restituisce 60 per i minuti Studio e 1 per i secondi Workout. |
| [8](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/modes.js#L8) | <code>export const modeData = (data, mode) =&gt; ({ workouts: data.workouts.filter(w =&gt; modeOf(w) === mode), sessions: data.sessions.filter(s =&gt; modeOf(s) === mode) });</code> | modeData: Crea una vista dell’archivio contenente soltanto programmi e sessioni della modalità richiesta. |
| [9](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/modes.js#L9) | <code>export function loadMode(storage) { try { return storage?.getItem(&#x27;tabata:mode&#x27;) === &#x27;STUDIO&#x27; ? &#x27;STUDIO&#x27; : &#x27;WORKOUT&#x27;; } catch { return &#x27;WORKOUT&#x27;; } }</code> | loadMode: Legge l’ultima modalità dal browser con fallback a Workout anche se lo storage è negato. |
| [10](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/modes.js#L10) | <code>export function saveMode(storage, mode) { try { storage?.setItem(&#x27;tabata:mode&#x27;, mode); } catch { /* The current visit still works when storage is unavailable. *…</code> | saveMode: Ricorda l’ultima modalità senza rendere inutilizzabile il timer se lo storage fallisce. |
