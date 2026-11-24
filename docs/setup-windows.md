# Installazione Windows, passo per passo

## Revisione locale del 24 novembre 2026

Non sono necessari nuovi installer, dipendenze o servizi. Il lavoro corrente si trova nel worktree `tmp/workout-recuperi-audio`, branch locale `codex/workout-recuperi-audio`. Esegui i comandi dalla sua radice, non dal checkout didattico `documentazione`.

```powershell
Set-Location 'C:\Users\rober\Documents\ChatGPT\Tabata\tmp\workout-recuperi-audio'
python scripts/generate-horn.py
python scripts/generate-locales.py
python -m http.server 5500 --bind 127.0.0.1 --directory web
```

I generatori leggono solo risorse incluse: `assets/audio/` contiene estratto e tre DING, `locales/messages.json` e `locales/help.json` testi e struttura della Guida. Non richiedono Downloads, YouTube o cartella demo. La cache statica è `tabata-static-workout-audio-20261124`; l’archivio personale è separato. Ricarica il server locale per ricevere la nuova cache, senza cancellare dati del browser.

In un secondo PowerShell, dalla stessa radice:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
node --test web/tests/*.test.js
.\android\gradlew.bat -p android testDebugUnitTest assembleDebug lintDebug
node scripts/interop-check.mjs
# Solo su dispositivo/emulatore dedicato, boot completato:
.\android\gradlew.bat -p android connectedDebugAndroidTest
```

APK locale: `android/app/build/outputs/apk/debug/app-debug.apk` in questo worktree. `output/Tabata Timer.apk` già consegnato non viene sovrascritto. Per prova USB, dopo aver esportato un JSON, usa `adb install -r` sul nuovo APK se la firma coincide. Non disinstallare per conservare i dati; nessuna pubblicazione o distribuzione è compresa in questa revisione. [Verifiche](testing.md).

Ambiente di questa revisione: Windows, Node 24.22.3, JBR 21, SDK progetto 36, emulatore API 37. L’elenco seguente conserva anche istruzioni e versioni delle revisioni precedenti.

## 1. Sorgenti e prerequisiti

La radice contiene `web/`, `android/`, `fixtures/`, `scripts/` e `docs/`. Non ci sono `.env`, segreti o chiavi API.

Ambiente verificato il 18 novembre 2026: Windows, Python 3.14, Node 24.21.0, Android Studio 2026.1.1 con JBR 21.0.10, Android SDK 36, emulatore Medium_Phone Android 17/API 37. Python, Node e Android Studio erano già installati: nessun installer di sistema è stato eseguito. Le build hanno scaricato dipendenze gratuite da Google Maven, Maven Central e Gradle Plugin Portal.

## 2. Web

1. Se necessario installa Python da [python.org](https://www.python.org/downloads/), includendo il launcher Windows.
2. Apri PowerShell nella radice del progetto.
3. Verifica `python --version` (oppure `py --version`).
4. Avvia il server statico locale:

```powershell
python -m http.server 5500 --bind 127.0.0.1 --directory web
```

5. Apri [http://127.0.0.1:5500](http://127.0.0.1:5500). Il primo caricamento serve a memorizzare gli asset offline. L'uso non richiede Node.js o npm.
6. Scegli Workout o Studio nello switch vicino al logo. Imposta Lavoro/Recupero in secondi oppure Concentrazione/Pausa in minuti, poi i giri/sessioni e premi Avvia. I pulsanti demo sono stati rimossi da entrambe le modalità. Cronologia e statistiche si trovano in Attività.
7. Per conservare un timer premi **Salva allenamento** (Workout) o **Salva programma di studio** (Studio), scrivi il nome e conferma **Salva sul dispositivo**. Lo ritrovi in Allenamenti anche dopo la chiusura del browser. Questa conferma attiva anche il salvataggio delle attività. In alternativa, **Attiva il salvataggio** o **Impostazioni → Salva cronologia** conserva l’archivio corrente.
8. La guida completa è in [guida-utente.md](guida-utente.md) e nel pulsante Guida dell’app. Per interrompere il server: Ctrl+C nella sua finestra PowerShell.

Per usare lo stesso indirizzo da un telefono sulla LAN occorrerebbe un binding diverso e, per wake lock/service worker, un'origine sicura; questa configurazione non è stata attivata. Nessuna porta è stata pubblicata verso Internet.

## 3. Android

1. Se necessario installa [Android Studio](https://developer.android.com/studio) con Android SDK, Emulator e JDK integrato.
2. Apri **la cartella `android/` esistente**: non creare un secondo progetto sopra i file.
3. In Settings → Build Tools → Gradle scegli il JDK integrato JBR 21. Non usare il vecchio Java 8 di sistema.
4. In SDK Manager verifica Android SDK Platform 36 e Build Tools richiesti da Gradle. Accetta le licenze SDK richieste dall'IDE.
5. Attendi Gradle Sync. Il wrapper è incluso (`gradlew`, `gradlew.bat`, jar e properties); non serve un Gradle globale.
6. Crea o avvia un emulatore da Device Manager, oppure collega un telefono con USB debugging attivo.
7. Seleziona il modulo `app` e premi Run. Non occorre un account Google Play.

Versioni bloccate e risolte nelle build locali:

| Componente | Versione |
| --- | --- |
| Android Gradle Plugin | 9.2.1 |
| Gradle wrapper | 9.4.1 |
| Kotlin integrato AGP / Compose compiler | 2.2.10 |
| KSP | 2.3.9 |
| Compose BOM | 2025.08.00 |
| Activity Compose | 1.10.1 |
| Lifecycle ViewModel | 2.9.2 |
| Room | 2.8.4 |
| kotlinx.serialization JSON (validazione sintassi) | 1.8.1 |
| AndroidX Test runner / JUnit ext / Espresso | 1.7.0 / 1.3.0 / 3.7.0 |
| minSdk / compileSdk / targetSdk | 26 / 36 / 36 |

Le versioni sono conservate per riproducibilità, senza dipendenze dinamiche. Le note ufficiali [AGP 9.2](https://developer.android.com/build/releases/agp-9-2-0-release-notes) indicano Gradle 9.4.1 e Kotlin 2.2.10; [Compose compiler](https://developer.android.com/develop/ui/compose/setup-compose-dependencies-and-compiler), [KSP](https://github.com/google/ksp/releases/tag/2.3.9), [Room](https://developer.android.com/jetpack/androidx/releases/room) e [AndroidX Test](https://developer.android.com/jetpack/androidx/releases/test) sono i riferimenti usati. Lint segnala versioni successive disponibili: non è un errore di build.

Per PowerShell, se Java/SDK non sono già configurati (adatta i percorsi alla tua installazione):

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
cd android
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebug
.\gradlew.bat lintDebug
# Solo con dispositivo di test già avviato:
.\gradlew.bat connectedDebugAndroidTest
```

APK debug: `android/app/build/outputs/apk/debug/app-debug.apk`. Per installazione USB senza IDE:

```powershell
& "$env:ANDROID_HOME\platform-tools\adb.exe" install -r '.\app\build\outputs\apk\debug\app-debug.apk'
```

Room crea lo schema 1 al primo avvio. Il JSON di schema è in `android/app/schemas/`; non si usa `fallbackToDestructiveMigration`. Un futuro incremento di versione richiederà una migrazione esplicita e relativi test. Per la prima versione non esiste uno schema precedente da migrare.

Tema e colori si scelgono in **Impostazioni → Aspetto**. Queste preferenze vengono salvate sul dispositivo separatamente dagli allenamenti; non servono installazioni aggiuntive.

## 4. Backup manuale

1. Apri **Impostazioni → Esporta copia JSON**.
2. Sul web trovi il file nei download o nella destinazione scelta dal browser. Su Android scegli una cartella locale nel selettore file.
3. Conserva il file o trasferiscilo manualmente sull’altro dispositivo.
4. Scegli **Importa copia JSON** e controlla il numero di allenamenti e sessioni nell’anteprima.
5. Conferma **Sostituisci dati**, oppure annulla. Esporta prima una copia dell’archivio di destinazione per conservarlo.
6. Sul web temporaneo premi poi **Salva cronologia** per conservare l’importazione tra visite. Android e il web con salvataggio già attivo la conservano automaticamente.

CSV esporta solo le sessioni ed è destinato a fogli di calcolo; l'importazione supporta il backup JSON completo v1. I titoli CSV che iniziano con caratteri formula vengono protetti con un apostrofo.

## 5. Preparazione audio (origine storica 1.4.5)

Per l’uso dell’app non si installa nulla: tutti i WAV sono inclusi e funzionano offline. Per rigenerarli basta Python standard, dalla radice del progetto:

```powershell
python scripts/generate-horn.py
```

Il generatore legge anche `assets/audio/sigh-reference.wav`, l’estratto di 500 ms del video indicato dall’utente. Provenienza e trasformazioni sono in [assets/audio/README.md](../assets/audio/README.md).

Solo durante la preparazione di questo estratto è stato installato gratuitamente `yt-dlp` 2026.10.26 nella cartella temporanea del progetto, senza installazione globale:

```powershell
python -m pip install --disable-pip-version-check --target tmp\youtube-tools yt-dlp==2026.10.26
```

Il riferimento pubblico è stato acquisito in `tmp/audio-reference/`, convertito con Web Audio tramite Edge e il Playwright già incluso nell’ambiente di sviluppo, quindi ritagliato a 500 ms. Non sono stati installati FFmpeg o nuove librerie nell’app. Le cartelle temporanee sono escluse da Git e non servono per avviare, compilare o rigenerare i suoni del progetto. L’app non scarica audio e non usa servizi esterni.

## 6. Pubblicazione

Il sito è stato pubblicato, dopo autorizzazione, su Cloudflare Pages tramite caricamento diretto dello ZIP: [procedura e stato](pubblicazione.md). I sorgenti sono sul repository pubblico [zRoby03/tabata-timer](https://github.com/zRoby03/tabata-timer). Non è configurato un workflow di deploy GitHub e un push non pubblica il sito. Nessun servizio a pagamento o distribuzione pubblica dell'APK è stato attivato. La cronologia delle sezioni successive conserva i passaggi delle revisioni precedenti.


## Revisioni precedenti (cronologia, non istruzioni della build attuale)

## 7. Aggiornamento Android 1.6.0

Installa `output/Tabata-Timer-1.6.0-debug.apk` sopra la versione precedente, senza disinstallarla, oppure ricompila con i comandi già indicati. Non sono state installate nuove dipendenze: servizio, notifiche, wake lock e audio usano le API Android e le librerie già presenti. Al primo avvio di un timer su Android 13+ consenti le notifiche; se rifiuti, il timer continua ma il pannello può non mostrarne i controlli. Per riabilitarli: Impostazioni → Timer in background → Notifiche Android. [Funzionamento e prove](android-background.md).

Nessun accesso agli account Spotify/YouTube, abbonamento, allarme esatto o esenzione automatica dal risparmio energetico è richiesto. Il lettore audio usato nei test è un servizio separato nel solo APK di test, con un WAV locale; non fa parte dell’app consegnata.

## 8. Aggiornamento web e Android 1.7.0

Non occorrono installazioni o dipendenze aggiuntive. Il web rimane HTML/CSS/JavaScript; il motore Android usa le stesse librerie. Rigenera gli audio soltanto se modifichi i campioni: `python scripts/generate-horn.py` usa Python standard e l’estratto locale già incluso.

1. Esporta una copia JSON dalla versione precedente.
2. Web: avvia il server locale come sopra e ricarica nello stesso browser e indirizzo. La cache statica cambia a `tabata-static-v1.7.0`; i dati personali hanno una chiave separata e non vengono cancellati.
3. Android: installa `output/Tabata-Timer-1.7.0-debug.apk` sopra la versione precedente, senza disinstallare. Il package e la firma debug locale restano gli stessi. In alternativa ricompila con `testDebugUnitTest assembleDebug lintDebug`.
4. Per le prove strumentali attendi che l’emulatore abbia completato l’avvio, poi esegui `connectedDebugAndroidTest`.
5. Usa la stessa versione su web e Android per importare i nuovi backup Studio. I vecchi archivi sono letti senza riscrittura automatica; controlla l’avviso prima di salvarne una modifica.

Nessuna pubblicazione, distribuzione pubblica o servizio a pagamento è stato attivato.

## 9. Aggiornamento 1.8.0 e lingue

Non sono necessarie nuove installazioni o dipendenze. Il web resta buildless; Android usa le librerie già presenti. Per modificare le traduzioni cambia `locales/messages.json` e rigenera i file con `python scripts/generate-locales.py` prima di compilare.

Esporta un JSON prima dell’aggiornamento. Installa `output/Tabata-Timer-1.8.0-debug.apk` sopra la versione precedente, senza disinstallare, per conservare l’archivio. Sul web usa lo stesso browser e indirizzo e ricarica la pagina. La cache statica 1.8.0 è separata dai dati.

La lingua iniziale segue il dispositivo; Impostazioni → Lingua la cambia e la ricorda. I nomi già salvati restano originali. Il file `Tabata-Timer-1.8.0-solo-sito.zip` è pronto per un hosting statico: contiene `index.html` nella radice e tutte le risorse locali. Non contiene APK o archivi personali. Nessuna pubblicazione eseguita; si attende “andiamo”.

## Nome e icone aggiornati

Per l’utente i file correnti si chiamano `output/Tabata Timer.apk` e `output/Tabata Timer.zip` (sito); i sorgenti sono in `output/sorgenti/Tabata Timer.zip`. Installa l’APK sopra quello precedente senza disinstallare: il nome mostrato è Tabata Timer e l’icona è la t. Le revisioni tecniche rimangono nei metadati per consentire gli aggiornamenti.

Nessuna nuova installazione o dipendenza. Il tracciato dell’icona è incluso: `python scripts/generate-brand-icons.py` rigenera SVG e vettori Android, `python scripts/generate-horn.py` rigenera i suoni. I PNG per l’installazione web sono già presenti. Apri la pagina alla radice del server locale: non servono numeri di versione nel collegamento.
