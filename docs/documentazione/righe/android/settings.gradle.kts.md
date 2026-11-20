# android/settings.gradle.kts

Configurazione di compilazione e avvio Gradle.

Versioni, plugin e dipendenze devono restare riproducibili. Il wrapper avvia Gradle e non costituisce il motore del timer.
I numeri si riferiscono al sorgente originale; il collegamento apre esattamente quella riga. I commenti DOC nel branch sono aggiunte didattiche.

## Ogni riga

| Riga originale | Codice (estratto) | Spiegazione |
| ---: | --- | --- |
| [1](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/settings.gradle.kts#L1) | <code>pluginManagement { repositories { google(); mavenCentral(); gradlePluginPortal() } }</code> | Esegue il passaggio esplicito «pluginManagement { repositories { google(); mavenCentral(); gradlePluginPortal() } }» nel flusso: Configurazione di compilazione e avvio Gradle. |
| [2](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/settings.gradle.kts#L2) | <code>dependencyResolutionManagement {</code> | Esegue il passaggio esplicito «dependencyResolutionManagement {» nel flusso: Configurazione di compilazione e avvio Gradle. |
| [3](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/settings.gradle.kts#L3) | <code>repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)</code> | Esegue il passaggio esplicito «repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)» nel flusso: Configurazione di compilazione e avvio Gradle. |
| [4](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/settings.gradle.kts#L4) | <code>repositories { google(); mavenCentral() }</code> | Esegue il passaggio esplicito «repositories { google(); mavenCentral() }» nel flusso: Configurazione di compilazione e avvio Gradle. |
| [5](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/settings.gradle.kts#L5) | <code>}</code> | Chiude il costrutto aperto alla riga 2. |
| [6](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/settings.gradle.kts#L6) | <code>rootProject.name = &quot;TabataStudio&quot;</code> | Esegue il passaggio esplicito «rootProject.name = &quot;TabataStudio&quot;» nel flusso: Configurazione di compilazione e avvio Gradle. |
| [7](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/settings.gradle.kts#L7) | <code>include(&quot;:app&quot;)</code> | Esegue il passaggio esplicito «include(&quot;:app&quot;)» nel flusso: Configurazione di compilazione e avvio Gradle. |
