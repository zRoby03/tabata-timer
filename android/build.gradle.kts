// DOC L0001: Esegue il passaggio esplicito «plugins {» nel flusso: Configurazione di compilazione e avvio Gradle.
plugins {
    // DOC L0002: Esegue il passaggio esplicito «id("com.android.application") version "9.2.1" apply false» nel flusso: Configurazione di compilazione e avvio Gradle.
    id("com.android.application") version "9.2.1" apply false
    // DOC L0003: Esegue il passaggio esplicito «id("org.jetbrains.kotlin.plugin.compose") version "2.2.10" apply false» nel flusso: Configurazione di compilazione e avvio Gradle.
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.10" apply false
    // DOC L0004: Esegue il passaggio esplicito «id("com.google.devtools.ksp") version "2.3.9" apply false» nel flusso: Configurazione di compilazione e avvio Gradle.
    id("com.google.devtools.ksp") version "2.3.9" apply false
// DOC L0005: Chiude il costrutto aperto alla riga 1.
}
