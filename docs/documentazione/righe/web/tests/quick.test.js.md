# web/tests/quick.test.js

Prove di regressione in quick.test.js.

Le asserzioni verificano il comportamento; fixture, clock e servizi di prova non sono funzionalità aggiunte al prodotto.
I numeri si riferiscono al sorgente originale; il collegamento apre esattamente quella riga. I commenti DOC nel branch sono aggiunte didattiche.
## Funzioni e callback
| Simbolo | Riga originale | Ruolo |
| --- | ---: | --- |
| callback | [6](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/tests/quick.test.js#L6) | Callback di test per l’evento o caso «timer rapido: otto lavori e sette recuperi, senza recupero finale»; viene invocata dal chiamante nel flusso del modulo. |
| callback | [9](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/tests/quick.test.js#L9) | Callback di phases.filter; viene invocata dal chiamante nel flusso del modulo. |
| callback | [13](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/tests/quick.test.js#L13) | Callback di test per l’evento o caso «opzioni rapide riusano cicli e recuperi del modello condiviso»; viene invocata dal chiamante nel flusso del modulo. |

## Ogni riga

| Riga originale | Codice (estratto) | Spiegazione |
| ---: | --- | --- |
| [1](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/tests/quick.test.js#L1) | <code>import { test } from &#x27;node:test&#x27;;</code> | Rende disponibili le dipendenze esplicite di questa riga: import { test } from &#x27;node:test&#x27;;. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra. |
| [2](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/tests/quick.test.js#L2) | <code>import assert from &#x27;node:assert/strict&#x27;;</code> | Rende disponibili le dipendenze esplicite di questa riga: import assert from &#x27;node:assert/strict&#x27;;. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra. |
| [3](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/tests/quick.test.js#L3) | <code>import { newQuickWorkout } from &#x27;../js/quick.js&#x27;;</code> | Rende disponibili le dipendenze esplicite di questa riga: import { newQuickWorkout } from &#x27;../js/quick.js&#x27;;. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra. |
| [4](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/tests/quick.test.js#L4) | <code>import { compileTimeline, plannedMs } from &#x27;../js/timeline.js&#x27;;</code> | Rende disponibili le dipendenze esplicite di questa riga: import { compileTimeline, plannedMs } from &#x27;../js/timeline.js&#x27;;. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra. |
| [5](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/tests/quick.test.js#L5) | <code>∅</code> | Riga vuota: separa visivamente i passaggi, senza introdurre istruzioni o dati. |
| [6](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/tests/quick.test.js#L6) | <code>test(&#x27;timer rapido: otto lavori e sette recuperi, senza recupero finale&#x27;, () =&gt; {</code> | Esegue il passaggio esplicito «test(&#x27;timer rapido: otto lavori e sette recuperi, senza recupero finale&#x27;, () =&gt; {» nel flusso: Prove di regressione in quick.test.js. |
| [7](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/tests/quick.test.js#L7) | <code>const phases = compileTimeline(newQuickWorkout());</code> | phases: sequenza cronologica compilata dal programma. |
| [8](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/tests/quick.test.js#L8) | <code>assert.equal(plannedMs(phases), 230000);</code> | Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. |
| [9](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/tests/quick.test.js#L9) | <code>assert.equal(phases.filter(p =&gt; p.type === &#x27;WORK&#x27;).length, 8);</code> | Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. |
| [10](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/tests/quick.test.js#L10) | <code>assert.equal(phases.at(-1).type, &#x27;WORK&#x27;);</code> | Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. |
| [11](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/tests/quick.test.js#L11) | <code>});</code> | Chiude il costrutto aperto alla riga 6. |
| [12](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/tests/quick.test.js#L12) | <code>∅</code> | Riga vuota: separa visivamente i passaggi, senza introdurre istruzioni o dati. |
| [13](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/tests/quick.test.js#L13) | <code>test(&#x27;opzioni rapide riusano cicli e recuperi del modello condiviso&#x27;, () =&gt; {</code> | Esegue il passaggio esplicito «test(&#x27;opzioni rapide riusano cicli e recuperi del modello condiviso&#x27;, () =&gt; {» nel flusso: Prove di regressione in quick.test.js. |
| [14](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/tests/quick.test.js#L14) | <code>const w = newQuickWorkout();</code> | w: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. |
| [15](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/tests/quick.test.js#L15) | <code>w.blocks[0].cycles = 2;</code> | Esegue il passaggio esplicito «w.blocks[0].cycles = 2;» nel flusso: Prove di regressione in quick.test.js. |
| [16](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/tests/quick.test.js#L16) | <code>w.blocks[0].exercises[0].repetitions = 3;</code> | Esegue il passaggio esplicito «w.blocks[0].exercises[0].repetitions = 3;» nel flusso: Prove di regressione in quick.test.js. |
| [17](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/tests/quick.test.js#L17) | <code>assert.equal(plannedMs(compileTimeline(w)), 220000);</code> | Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. |
| [18](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/web/tests/quick.test.js#L18) | <code>});</code> | Chiude il costrutto aperto alla riga 13. |
