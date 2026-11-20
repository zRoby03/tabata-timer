# web/js/charts.js

Grafici SVG delle statistiche web.

Genera barre e descrizioni accessibili a partire dalle aggregazioni, senza librerie grafiche esterne.
I numeri si riferiscono al sorgente originale; il collegamento apre esattamente quella riga. I commenti DOC nel branch sono aggiunte didattiche.
## Funzioni e callback
| Simbolo | Riga originale | Ruolo |
| --- | ---: | --- |
| chart | [3](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/charts.js#L3) | Genera il grafico SVG accessibile per una metrica delle settimane aggregate. |
| chart.callback | [4](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/charts.js#L4) | Callback di weeks.map; viene invocata dal chiamante nel contesto di chart. |
| chart.callback | [6](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/charts.js#L6) | Callback di weeks.map; viene invocata dal chiamante nel contesto di chart. |
| chart.callback | [9](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/charts.js#L9) | Callback di weeks.map; viene invocata dal chiamante nel contesto di chart. |

## Ogni riga

| Riga originale | Codice (estratto) | Spiegazione |
| ---: | --- | --- |
| [1](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/charts.js#L1) | <code>import { t } from &#x27;./i18n.js&#x27;;</code> | Rende disponibili le dipendenze esplicite di questa riga: import { t } from &#x27;./i18n.js&#x27;;. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra. |
| [2](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/charts.js#L2) | <code>import { escapeHtml as h, decimal } from &#x27;./dom.js&#x27;;</code> | Rende disponibili le dipendenze esplicite di questa riga: import { escapeHtml as h, decimal } from &#x27;./dom.js&#x27;;. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra. |
| [3](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/charts.js#L3) | <code>export function chart(weeks, field, title, unit) {</code> | chart: Genera il grafico SVG accessibile per una metrica delle settimane aggregate. |
| [4](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/charts.js#L4) | <code>const max = Math.max(1, ...weeks.map(w =&gt; field === &#x27;workMs&#x27; ? w.workMs / 60000 : w[field]));</code> | Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. max: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. |
| [5](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/charts.js#L5) | <code>const width = 640, step = 560 / Math.max(1, weeks.length);</code> | width: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di chart. |
| [6](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/charts.js#L6) | <code>return `&lt;figure class=&quot;card chart&quot;&gt;&lt;figcaption&gt;${h(t(title))}&lt;/figcaption&gt;&lt;svg viewBox=&quot;0 0 ${width} 220&quot; role=&quot;img&quot; aria-label=&quot;${t(&quot;{0}. Valori nella tabella …</code> | Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Restituisce il valore calcolato o conclude il percorso della funzione. Genera o aggiorna HTML della UI; i dati utente devono passare attraverso l’escaping previsto dal modulo. |
| [7](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/charts.js#L7) | <code>const v = field === &#x27;workMs&#x27; ? w.workMs / 60000 : w[field], height = v / max * 135, x = 50 + i * step;</code> | v: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. |
| [8](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/charts.js#L8) | <code>return `&lt;rect x=&quot;${x}&quot; y=&quot;${180 - height}&quot; width=&quot;${Math.max(3, step - 8)}&quot; height=&quot;${height}&quot; rx=&quot;3&quot; fill=&quot;var(--accent-fill)&quot;/&gt;&lt;text x=&quot;${x + (step - 8) / 2}&quot;…</code> | Restituisce il valore calcolato o conclude il percorso della funzione. Genera o aggiorna HTML della UI; i dati utente devono passare attraverso l’escaping previsto dal modulo. |
| [9](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/charts.js#L9) | <code>}).join(&#x27;&#x27;)}&lt;/svg&gt;&lt;p class=&quot;muted&quot;&gt;${t(&quot;Colore principale: {0} per settimana (lunedì, UTC).&quot;, h(t(unit)))}&lt;/p&gt;&lt;details&gt;&lt;summary&gt;${t(&quot;Tabella dati · {0}&quot;, h(t(ti…</code> | Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. |
| [10](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/charts.js#L10) | <code>}</code> | Chiude il costrutto aperto alla riga 3. |
