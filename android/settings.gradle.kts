// DOC L0001: Esegue il passaggio esplicito «pluginManagement { repositories { google(); mavenCentral(); gradlePluginPortal() } }» nel flusso: Configurazione di compilazione e avvio Gradle.
pluginManagement { repositories { google(); mavenCentral(); gradlePluginPortal() } }
// DOC L0002: Esegue il passaggio esplicito «dependencyResolutionManagement {» nel flusso: Configurazione di compilazione e avvio Gradle.
dependencyResolutionManagement {
    // DOC L0003: Esegue il passaggio esplicito «repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)» nel flusso: Configurazione di compilazione e avvio Gradle.
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    // DOC L0004: Esegue il passaggio esplicito «repositories { google(); mavenCentral() }» nel flusso: Configurazione di compilazione e avvio Gradle.
    repositories { google(); mavenCentral() }
// DOC L0005: Chiude il costrutto aperto alla riga 2.
}
// DOC L0006: Esegue il passaggio esplicito «rootProject.name = "TabataStudio"» nel flusso: Configurazione di compilazione e avvio Gradle.
rootProject.name = "TabataStudio"
// DOC L0007: Esegue il passaggio esplicito «include(":app")» nel flusso: Configurazione di compilazione e avvio Gradle.
include(":app")
