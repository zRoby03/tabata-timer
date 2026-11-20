// DOC L0001: Esegue il passaggio esplicito «plugins {» nel flusso: Configurazione di compilazione e avvio Gradle.
plugins {
    // DOC L0002: Esegue il passaggio esplicito «id("com.android.application")» nel flusso: Configurazione di compilazione e avvio Gradle.
    id("com.android.application")
    // DOC L0003: Esegue il passaggio esplicito «id("org.jetbrains.kotlin.plugin.compose")» nel flusso: Configurazione di compilazione e avvio Gradle.
    id("org.jetbrains.kotlin.plugin.compose")
    // DOC L0004: Esegue il passaggio esplicito «id("com.google.devtools.ksp")» nel flusso: Configurazione di compilazione e avvio Gradle.
    id("com.google.devtools.ksp")
// DOC L0005: Chiude il costrutto aperto alla riga 1.
}
// DOC L0006: Esegue il passaggio esplicito «android {» nel flusso: Configurazione di compilazione e avvio Gradle.
android {
    // DOC L0007: Esegue il passaggio esplicito «namespace = "it.tabatastudio"» nel flusso: Configurazione di compilazione e avvio Gradle.
    namespace = "it.tabatastudio"
    // DOC L0008: Esegue il passaggio esplicito «compileSdk = 36» nel flusso: Configurazione di compilazione e avvio Gradle.
    compileSdk = 36
    // DOC L0009: Esegue il passaggio esplicito «defaultConfig {» nel flusso: Configurazione di compilazione e avvio Gradle.
    defaultConfig {
        // DOC L0010: Esegue il passaggio esplicito «applicationId = "it.tabatastudio"» nel flusso: Configurazione di compilazione e avvio Gradle.
        applicationId = "it.tabatastudio"
        // DOC L0011: Esegue il passaggio esplicito «minSdk = 26» nel flusso: Configurazione di compilazione e avvio Gradle.
        minSdk = 26
        // DOC L0012: Esegue il passaggio esplicito «targetSdk = 36» nel flusso: Configurazione di compilazione e avvio Gradle.
        targetSdk = 36
        // DOC L0013: Esegue il passaggio esplicito «versionCode = 16» nel flusso: Configurazione di compilazione e avvio Gradle.
        versionCode = 16
        // DOC L0014: Esegue il passaggio esplicito «versionName = "1.8.1"» nel flusso: Configurazione di compilazione e avvio Gradle.
        versionName = "1.8.1"
        // DOC L0015: Esegue il passaggio esplicito «testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"» nel flusso: Configurazione di compilazione e avvio Gradle.
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    // DOC L0016: Chiude il costrutto aperto alla riga 9.
    }
    // DOC L0017: Esegue il passaggio esplicito «bundle { language { enableSplit = false } }» nel flusso: Configurazione di compilazione e avvio Gradle.
    bundle { language { enableSplit = false } }
    // DOC L0018: Esegue il passaggio esplicito «buildFeatures { compose = true }» nel flusso: Configurazione di compilazione e avvio Gradle.
    buildFeatures { compose = true }
    // DOC L0019: Esegue il passaggio esplicito «compileOptions {» nel flusso: Configurazione di compilazione e avvio Gradle.
    compileOptions {
        // DOC L0020: Esegue il passaggio esplicito «sourceCompatibility = JavaVersion.VERSION_17» nel flusso: Configurazione di compilazione e avvio Gradle.
        sourceCompatibility = JavaVersion.VERSION_17
        // DOC L0021: Esegue il passaggio esplicito «targetCompatibility = JavaVersion.VERSION_17» nel flusso: Configurazione di compilazione e avvio Gradle.
        targetCompatibility = JavaVersion.VERSION_17
    // DOC L0022: Chiude il costrutto aperto alla riga 19.
    }
    // DOC L0023: Esegue il passaggio esplicito «sourceSets.getByName("test").resources.srcDir("../../fixtures")» nel flusso: Configurazione di compilazione e avvio Gradle.
    sourceSets.getByName("test").resources.srcDir("../../fixtures")
// DOC L0024: Chiude il costrutto aperto alla riga 6.
}
// DOC L0025: Esegue il passaggio esplicito «dependencies {» nel flusso: Configurazione di compilazione e avvio Gradle.
dependencies {
    // DOC L0026: Esegue il passaggio esplicito «implementation(platform("androidx.compose:compose-bom:2025.08.00"))» nel flusso: Configurazione di compilazione e avvio Gradle.
    implementation(platform("androidx.compose:compose-bom:2025.08.00"))
    // DOC L0027: Esegue il passaggio esplicito «implementation("androidx.activity:activity-compose:1.10.1")» nel flusso: Configurazione di compilazione e avvio Gradle.
    implementation("androidx.activity:activity-compose:1.10.1")
    // DOC L0028: Esegue il passaggio esplicito «implementation("androidx.compose.material3:material3")» nel flusso: Configurazione di compilazione e avvio Gradle.
    implementation("androidx.compose.material3:material3")
    // DOC L0029: Esegue il passaggio esplicito «implementation("androidx.compose.ui:ui-tooling-preview")» nel flusso: Configurazione di compilazione e avvio Gradle.
    implementation("androidx.compose.ui:ui-tooling-preview")
    // DOC L0030: Esegue il passaggio esplicito «implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.2")» nel flusso: Configurazione di compilazione e avvio Gradle.
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.2")
    // DOC L0031: Esegue il passaggio esplicito «implementation("androidx.room:room-runtime:2.8.4")» nel flusso: Configurazione di compilazione e avvio Gradle.
    implementation("androidx.room:room-runtime:2.8.4")
    // DOC L0032: Esegue il passaggio esplicito «implementation("androidx.room:room-ktx:2.8.4")» nel flusso: Configurazione di compilazione e avvio Gradle.
    implementation("androidx.room:room-ktx:2.8.4")
    // DOC L0033: Esegue il passaggio esplicito «implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")» nel flusso: Configurazione di compilazione e avvio Gradle.
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    // DOC L0034: Esegue il passaggio esplicito «ksp("androidx.room:room-compiler:2.8.4")» nel flusso: Configurazione di compilazione e avvio Gradle.
    ksp("androidx.room:room-compiler:2.8.4")
    // DOC L0035: Esegue il passaggio esplicito «debugImplementation("androidx.compose.ui:ui-tooling")» nel flusso: Configurazione di compilazione e avvio Gradle.
    debugImplementation("androidx.compose.ui:ui-tooling")
    // DOC L0036: Esegue il passaggio esplicito «testImplementation("junit:junit:4.13.2")» nel flusso: Configurazione di compilazione e avvio Gradle.
    testImplementation("junit:junit:4.13.2")
    // DOC L0037: Esegue il passaggio esplicito «testImplementation("org.json:json:20240303")» nel flusso: Configurazione di compilazione e avvio Gradle.
    testImplementation("org.json:json:20240303")
    // DOC L0038: Esegue il passaggio esplicito «androidTestImplementation(platform("androidx.compose:compose-bom:2025.08.00"))» nel flusso: Configurazione di compilazione e avvio Gradle.
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.08.00"))
    // DOC L0039: Esegue il passaggio esplicito «androidTestImplementation("androidx.compose.ui:ui-test-junit4")» nel flusso: Configurazione di compilazione e avvio Gradle.
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    // DOC L0040: Esegue il passaggio esplicito «androidTestImplementation("androidx.test.ext:junit:1.3.0")» nel flusso: Configurazione di compilazione e avvio Gradle.
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    // DOC L0041: Esegue il passaggio esplicito «androidTestImplementation("androidx.test:runner:1.7.0")» nel flusso: Configurazione di compilazione e avvio Gradle.
    androidTestImplementation("androidx.test:runner:1.7.0")
    // DOC L0042: Esegue il passaggio esplicito «androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")» nel flusso: Configurazione di compilazione e avvio Gradle.
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    // DOC L0043: Esegue il passaggio esplicito «debugImplementation("androidx.compose.ui:ui-test-manifest")» nel flusso: Configurazione di compilazione e avvio Gradle.
    debugImplementation("androidx.compose.ui:ui-test-manifest")
// DOC L0044: Chiude il costrutto aperto alla riga 25.
}
// DOC L0045: Esegue il passaggio esplicito «ksp { arg("room.schemaLocation", "$projectDir/schemas") }» nel flusso: Configurazione di compilazione e avvio Gradle.
ksp { arg("room.schemaLocation", "$projectDir/schemas") }
