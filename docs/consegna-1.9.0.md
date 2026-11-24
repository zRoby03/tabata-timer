# Consegna Tabata Timer 1.9.0

Revisione interna Android 1.9.0, versionCode 17. Il nome pubblico resta Tabata Timer. Sorgenti sul branch `codex/workout-recuperi-audio` del repository `zRoby03/tabata-timer`; `documentazione` resta il ramo didattico della revisione precedente. L’autorizzazione successiva al report locale comprende commit, push e preparazione del nuovo APK. Non comprende un nuovo deployment web o una pubblicazione su Google Play/GitHub Releases.

## APK da installare

File di consegna: `output/Tabata Timer.apk` nella cartella originale del progetto. Copia con versione: `output/Tabata-Timer-1.9.0.apk`. Il precedente APK rimane disponibile in `output/Tabata-Timer-1.8.1-debug.apk` e viene verificato prima di aggiornare il file di consegna. APK, chiavi e cartella output restano esclusi da Git.

La nuova app usa il build type **release**, con debug disabilitato, e conserva applicationId `it.tabatastudio`, Room v1 e la firma dell’APK già inviato. Questo consente l’aggiornamento diretto senza disinstallazione e conserva i dati locali. La firma esistente è il certificato locale **Android Debug**, mantenuto deliberatamente per compatibilità: questa consegna è destinata all’installazione diretta e non è un pacchetto già predisposto per Google Play. Nessuna nuova chiave di produzione è stata creata o caricata nel repository.

SHA-256 pubblico del certificato conservato: `e48adbb6e73ab44cc1645addbba4e5f8f6d3d82f28d28dd24e5d54709ec3dece`.

Sul telefono esporta prima una copia JSON, poi apri il nuovo APK e conferma Aggiorna. Se Android lo richiede, consenti l’installazione al gestore file usato. Non disinstallare l’app precedente. Conserva la copia JSON anche dopo l’aggiornamento.

## Ricostruzione su questa macchina

Dalla radice del checkout applicativo, con Python, JBR e SDK già presenti:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
python scripts/generate-horn.py
python scripts/generate-locales.py
node --test web/tests/*.test.js
.\android\gradlew.bat -p android testDebugUnitTest assembleDebug assembleRelease lintRelease
node scripts/interop-check.mjs
```

`assembleRelease` produce `android/app/build/outputs/apk/release/app-release-unsigned.apk`. Per conservare la firma già usata, viene allineato e firmato localmente; il file chiave rimane fuori dal repository:

```powershell
$buildTools = Join-Path $env:ANDROID_HOME 'build-tools\36.0.0'
$unsignedApk = 'android\app\build\outputs\apk\release\app-release-unsigned.apk'
$alignedApk = 'output\Tabata-Timer-1.9.0-aligned.apk'
$signedApk = 'output\Tabata-Timer-1.9.0.apk'
& "$buildTools\zipalign.exe" -P 16 -f 4 $unsignedApk $alignedApk
& "$buildTools\apksigner.bat" sign --ks "$env:USERPROFILE\.android\debug.keystore" --ks-key-alias androiddebugkey --ks-pass pass:android --key-pass pass:android --out $signedApk $alignedApk
& "$buildTools\apksigner.bat" verify --verbose --print-certs $signedApk
& "$buildTools\zipalign.exe" -c -P 16 -v 4 $signedApk
& "$buildTools\aapt.exe" dump badging $signedApk
```

La password standard mostrata è quella del solo keystore di sviluppo preesistente. Non usare questi comandi come configurazione di una futura firma Google Play. Le verifiche controllano firma uguale al precedente APK, versione 17/1.9.0, nome pubblico, assenza del flag debuggable e allineamento. Prima di sostituire il file di consegna si controlla la copia precedente tramite SHA-256.

Per installare la copia locale su un dispositivo di prova collegato:

```powershell
& "$env:ANDROID_HOME\platform-tools\adb.exe" install -r 'output\Tabata-Timer-1.9.0.apk'
```

Non occorrono nuove dipendenze, installer o servizi. L’APK non viene incluso nel sito o nei sorgenti Git. Il push del branch non esegue un deployment Cloudflare: il sito attivo conserva la revisione precedente.

## Funzioni e verifiche

La consegna comprende recuperi personali Workout, sostituzione al cambio blocco, anteprime, DING originali, focus breve Android e Guida IT/EN/ES. [Regole e test funzionali](recuperi-audio-2026-11-24.md), [installazione](setup-windows.md), [verifiche e limiti](testing.md).

I report della consegna e gli hash dell’APK sono conservati localmente in `output/verification-v1.9.0.json` e `output/SHA256SUMS-v1.9.0.txt`. Le verifiche del telefono fisico con Spotify, YouTube, podcast e cuffie restano separate dai test automatici e dall’emulatore.

## Risultati della consegna

Ripetuti 94 test Node e 48 JVM, tutti passati; quattro scambi JSON Kotlin/JS verificati. Build debug e release riuscite, lintRelease 0 errori e 18 avvisi (gli stessi già documentati). I 18 casi Android funzionali appartengono alla revisione appena verificata; per questa consegna è stata eseguita in aggiunta una prova di aggiornamento reale dell’APK release sopra il precedente.

Su emulatore dedicato: versione precedente 1.8.1 installata, archivio con 3 allenamenti e 1 sessione storica, aggiornamento con `install -r` alla release 1.9.0 senza disinstallazione, avvio e confronto dei JSON conservati. Tutte le righe restano identiche, compresi i dati legacy; lingua inglese preservata. APK verificato con nome Tabata Timer, firma uguale, versione 17/1.9.0, debug disattivato e allineamento corretto.

L’emulatore non consente adb root e la release non consente run-as: per ispezionare il database lasciato dalla release, è stato installato temporaneamente il debug della stessa versione senza avviarlo, letto il database e reinstallata la release. Nessuna pulizia dei dati o disinstallazione. Un primo dialogo ANR di System UI è stato chiuso; un controllo QA inizialmente cercava soltanto Avvia invece del pulsante inglese Start. I tentativi sono riportati nel report locale, senza modifiche al prodotto per quei problemi di prova.

SHA-256 del nuovo APK: `46b11332f4834036d54b702c8192d989c35f6f6b3401b3aa8b27142017b1e752`.
Dimensione: 8346268 byte. Il vecchio APK 1.8.1 rimane nella copia con versione e conserva l’hash `4d93b0e5b8cdc3632f16bd5355165dcb0873090abd92ddd1003347567cd2cc7d`.
