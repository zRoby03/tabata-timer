# android/build.gradle.kts

Configurazione di compilazione e avvio Gradle.

Versioni, plugin e dipendenze devono restare riproducibili. Il wrapper avvia Gradle e non costituisce il motore del timer.
I numeri si riferiscono al sorgente originale; il collegamento apre esattamente quella riga. I commenti DOC nel branch sono aggiunte didattiche.

## Ogni riga

| Riga originale | Codice (estratto) | Spiegazione |
| ---: | --- | --- |
| [1](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/build.gradle.kts#L1) | <code>plugins {</code> | Esegue il passaggio esplicito «plugins {» nel flusso: Configurazione di compilazione e avvio Gradle. |
| [2](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/build.gradle.kts#L2) | <code>id(&quot;com.android.application&quot;) version &quot;9.2.1&quot; apply false</code> | Esegue il passaggio esplicito «id(&quot;com.android.application&quot;) version &quot;9.2.1&quot; apply false» nel flusso: Configurazione di compilazione e avvio Gradle. |
| [3](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/build.gradle.kts#L3) | <code>id(&quot;org.jetbrains.kotlin.plugin.compose&quot;) version &quot;2.2.10&quot; apply false</code> | Esegue il passaggio esplicito «id(&quot;org.jetbrains.kotlin.plugin.compose&quot;) version &quot;2.2.10&quot; apply false» nel flusso: Configurazione di compilazione e avvio Gradle. |
| [4](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/build.gradle.kts#L4) | <code>id(&quot;com.google.devtools.ksp&quot;) version &quot;2.3.9&quot; apply false</code> | Esegue il passaggio esplicito «id(&quot;com.google.devtools.ksp&quot;) version &quot;2.3.9&quot; apply false» nel flusso: Configurazione di compilazione e avvio Gradle. |
| [5](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/build.gradle.kts#L5) | <code>}</code> | Chiude il costrutto aperto alla riga 1. |
