# web/js/site-links.js

Collegamenti localizzati alle pagine pubbliche.

Le etichette seguono la lingua comune; gli indirizzi dei file restano stabili.
I numeri si riferiscono al sorgente originale; il collegamento apre esattamente quella riga. I commenti DOC nel branch sono aggiunte didattiche.
## Funzioni e callback
| Simbolo | Riga originale | Ruolo |
| --- | ---: | --- |
| siteLinksView | [3](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/site-links.js#L3) | Produce i due collegamenti Informazioni e Privacy nella lingua corrente. |

## Ogni riga

| Riga originale | Codice (estratto) | Spiegazione |
| ---: | --- | --- |
| [1](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/site-links.js#L1) | <code>import { getLanguage } from &#x27;./i18n.js&#x27;;</code> | Rende disponibili le dipendenze esplicite di questa riga: import { getLanguage } from &#x27;./i18n.js&#x27;;. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra. |
| [2](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/site-links.js#L2) | <code>∅</code> | Riga vuota: separa visivamente i passaggi, senza introdurre istruzioni o dati. |
| [3](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/site-links.js#L3) | <code>export function siteLinksView() {</code> | siteLinksView: Produce i due collegamenti Informazioni e Privacy nella lingua corrente. |
| [4](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/site-links.js#L4) | <code>const labels = { it: [&#x27;Informazioni&#x27;, &#x27;Privacy e cookie&#x27;], en: [&#x27;About&#x27;, &#x27;Privacy and cookies&#x27;], es: [&#x27;Información&#x27;, &#x27;Privacidad y cookies&#x27;] }[getLanguage()];</code> | labels: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di siteLinksView. |
| [5](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/site-links.js#L5) | <code>return `&lt;p class=&quot;row site-links&quot;&gt;&lt;a href=&quot;./about.html&quot;&gt;${labels[0]}&lt;/a&gt;&lt;a href=&quot;./privacy.html&quot;&gt;${labels[1]}&lt;/a&gt;&lt;/p&gt;`;</code> | Restituisce il valore calcolato o conclude il percorso della funzione. Genera o aggiorna HTML della UI; i dati utente devono passare attraverso l’escaping previsto dal modulo. Nel contesto di siteLinksView. |
| [6](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/site-links.js#L6) | <code>}</code> | Chiude il costrutto aperto alla riga 3. |
