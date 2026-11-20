# web/js/study-fields.js

Campi condivisi delle attività Studio.

Lista piatta con nome, concentrazione, sessioni e pausa; una sola pausa globale separa le attività.
I numeri si riferiscono al sorgente originale; il collegamento apre esattamente quella riga. I commenti DOC nel branch sono aggiunte didattiche.
## Funzioni e callback
| Simbolo | Riga originale | Ruolo |
| --- | ---: | --- |
| studyFields | [5](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/study-fields.js#L5) | Genera le schede piatte Studio e la pausa globale tra attività, con valori utente escapati. |
| studyFields.field | [7](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/study-fields.js#L7) | Costruisce un campo dell’editor con percorso del modello e conversione dell’unità. |
| studyFields.callback | [8](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/study-fields.js#L8) | Callback di w.activities.map; viene invocata dal chiamante nel contesto di studyFields. |

## Ogni riga

| Riga originale | Codice (estratto) | Spiegazione |
| ---: | --- | --- |
| [1](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/study-fields.js#L1) | <code>import { t } from &#x27;./i18n.js&#x27;;</code> | Rende disponibili le dipendenze esplicite di questa riga: import { t } from &#x27;./i18n.js&#x27;;. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra. |
| [2](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/study-fields.js#L2) | <code>import { escapeHtml as h } from &#x27;./dom.js&#x27;;</code> | Rende disponibili le dipendenze esplicite di questa riga: import { escapeHtml as h } from &#x27;./dom.js&#x27;;. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra. |
| [3](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/study-fields.js#L3) | <code>∅</code> | Riga vuota: separa visivamente i passaggi, senza introdurre istruzioni o dati. |
| [4](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/study-fields.js#L4) | <code>// Both Studio forms edit the same flat model. Durations stay in seconds internally.</code> | Commento originale: conserva la motivazione o l’istruzione già presente nel sorgente. Both Studio forms edit the same flat model. Durations stay in seconds internally. |
| [5](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/study-fields.js#L5) | <code>export function studyFields(w, quick = false) {</code> | studyFields: Genera le schede piatte Studio e la pausa globale tra attività, con valori utente escapati. |
| [6](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/study-fields.js#L6) | <code>const attr = quick ? &#x27;data-quick-path&#x27; : &#x27;data-path&#x27;;</code> | attr: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di studyFields. |
| [7](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/study-fields.js#L7) | <code>const field = (label, path, value, index, factor = 1, min = 0, max = 3600) =&gt; `&lt;label&gt;${label}&lt;input ${attr}=&quot;${path}&quot; data-unit-factor=&quot;${factor}&quot; type=&quot;number…</code> | field: Costruisce un campo dell’editor con percorso del modello e conversione dell’unità. |
| [8](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/study-fields.js#L8) | <code>return `${w.activities.map((a, i) =&gt; `&lt;fieldset class=&quot;study-activity&quot;&gt;&lt;legend&gt;${t(&quot;Attività {0}&quot;, i + 1)}&lt;/legend&gt;&lt;label&gt;${t(&quot;Nome attività&quot;)}&lt;input ${attr}=&quot;a…</code> | Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Restituisce il valore calcolato o conclude il percorso della funzione. Genera o aggiorna HTML della UI; i dati utente devono passare attraverso l’escaping previsto dal modulo. |
| [9](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/study-fields.js#L9) | <code>}</code> | Chiude il costrutto aperto alla riga 5. |
