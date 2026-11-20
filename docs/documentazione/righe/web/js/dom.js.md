# web/js/dom.js

Utilità di escaping e formattazione locale.

I nomi utente sono testo: non devono essere interpretati come HTML o tradotti come messaggi di sistema.
I numeri si riferiscono al sorgente originale; il collegamento apre esattamente quella riga. I commenti DOC nel branch sono aggiunte didattiche.
## Funzioni e callback
| Simbolo | Riga originale | Ruolo |
| --- | ---: | --- |
| escapeHtml | [2](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/dom.js#L2) | Converte i caratteri speciali in entità HTML prima di mostrare testo inserito dall’utente. |
| escapeHtml.callback | [2](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/dom.js#L2) | Callback di String(value).replace; viene invocata dal chiamante nel contesto di escapeHtml. |
| dateLabel | [3](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/dom.js#L3) | Formatta una data con la lingua corrente per cronologia e tabelle. |
| decimal | [4](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/dom.js#L4) | Formatta un numero decimale nella lingua corrente. |

## Ogni riga

| Riga originale | Codice (estratto) | Spiegazione |
| ---: | --- | --- |
| [1](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/dom.js#L1) | <code>import { locale } from &#x27;./i18n.js&#x27;;</code> | Rende disponibili le dipendenze esplicite di questa riga: import { locale } from &#x27;./i18n.js&#x27;;. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra. |
| [2](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/dom.js#L2) | <code>export const escapeHtml = value =&gt; String(value).replace(/[&amp;&lt;&gt;&quot;&#x27;]/g, char =&gt; ({ &#x27;&amp;&#x27;: &#x27;&amp;amp;&#x27;, &#x27;&lt;&#x27;: &#x27;&amp;lt;&#x27;, &#x27;&gt;&#x27;: &#x27;&amp;gt;&#x27;, &#x27;&quot;&#x27;: &#x27;&amp;quot;&#x27;, &quot;&#x27;&quot;: &#x27;&amp;#39;&#x27; }[char]));</code> | escapeHtml: Converte i caratteri speciali in entità HTML prima di mostrare testo inserito dall’utente. |
| [3](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/dom.js#L3) | <code>export const dateLabel = value =&gt; new Date(value).toLocaleString(locale(), { dateStyle: &#x27;short&#x27;, timeStyle: &#x27;short&#x27; });</code> | dateLabel: Formatta una data con la lingua corrente per cronologia e tabelle. |
| [4](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/js/dom.js#L4) | <code>export const decimal = value =&gt; Number(value).toLocaleString(locale(), { minimumFractionDigits: 1, maximumFractionDigits: 1 });</code> | decimal: Formatta un numero decimale nella lingua corrente. |
