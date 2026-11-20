# web/js/help.js

Guida integrata web con tre riquadri.

Contiene soltanto Workout e Studio, salvataggio e Tema e Colori, rispettando il testo richiesto.
I numeri si riferiscono al sorgente originale; il collegamento apre esattamente quella riga. I commenti DOC nel branch sono aggiunte didattiche.
## Funzioni e callback
| Simbolo | Riga originale | Ruolo |
| --- | ---: | --- |
| helpView | [2](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/help.js#L2) | Renderizza i tre riquadri concordati della Guida integrata. |

## Ogni riga

| Riga originale | Codice (estratto) | Spiegazione |
| ---: | --- | --- |
| [1](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/help.js#L1) | <code>import { t } from &#x27;./i18n.js&#x27;;</code> | Rende disponibili le dipendenze esplicite di questa riga: import { t } from &#x27;./i18n.js&#x27;;. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra. |
| [2](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/help.js#L2) | <code>export function helpView() {</code> | helpView: Renderizza i tre riquadri concordati della Guida integrata. |
| [3](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/help.js#L3) | <code>return `&lt;section class=&quot;help-page&quot;&gt;&lt;div class=&quot;page-heading&quot;&gt;&lt;h1&gt;${t(&quot;Guida all’uso&quot;)}&lt;/h1&gt;&lt;button data-action=&quot;home&quot;&gt;${t(&quot;Vai al timer&quot;)}&lt;/button&gt;&lt;/div&gt;&lt;div cl…</code> | Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione. Genera o aggiorna HTML della UI; i dati utente devono passare attraverso l’escaping previsto dal modulo. Nel contesto di helpView. |
| [4](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/help.js#L4) | <code>}</code> | Chiude il costrutto aperto alla riga 2. |
