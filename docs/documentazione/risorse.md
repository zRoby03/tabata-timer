# Audio, logo e file generati

I binari non hanno righe di codice. [copertura.json](copertura.json) li elenca con percorso, dimensione e SHA-256; il verificatore controlla che siano identici alla base. Il loro processo di preparazione è invece codice testuale, spiegato riga per riga nell'indice.

## Nove suoni per piattaforma

| Risorse web | Controparti Android | Contenuto |
| --- | --- | --- |
| `countdown-work-1/2/3.wav` | `countdown_work_1/2/3.wav` | Note lavoro 440/370/330 Hz, una per numero, 120 ms |
| `countdown-rest-1/2/3.wav` | `countdown_rest_1/2/3.wav` | Note recupero 880/740/660 Hz, una per numero, 120 ms |
| `work-start.wav` | `work_start.wav` | Fischio da arbitro morbido, 500 ms |
| `rest-start.wav` | `rest_start.wav` | Sospiro, 500 ms |
| `workout-finish.wav` | `workout_finish.wav` | Tre ding a 0/300/600 ms, durata totale 900 ms |

I sei bip sono PCM mono 16 bit a 22.050 Hz. I tre segnali di transizione sono PCM mono a 24.000 Hz. `scripts/generate-horn.py` usa Python standard, genera i WAV delle due piattaforme e prepara anteprime in `output/audio`, escluse dal repository. Gli array dei nomi in `countdown.js`/`CountdownCues.kt` e `transitions.js`/`TransitionCues.kt` collegano eventi e campioni.

Il livello applicativo è 0,65 per bip/campanella, 0,85 per il sospiro e 1 per il fischio già preamplificato del fattore 1,2. Il nuovo fischio ha componente principale circa 1760 Hz, secondaria leggera 1880 Hz, trillo e inviluppo morbido. Il PCM non satura; la sensazione di volume rimane dipendente dall'hardware e dal volume multimediale.

Fischio, ding e bip sono sintetizzati dal codice. Il sospiro deriva dal riferimento YouTube indicato dall'utente, con intervallo 1,421375–1,921375 secondi, conversione mono, dissolvenze e normalizzazione. L'estratto necessario alla rigenerazione è già in `assets/audio/sigh-reference.wav`; provenienza e passaggi sono in [assets/audio/README.md](../../assets/audio/README.md). Il fatto che il file sia incluso non attribuisce una nuova licenza generale ai contenuti di terzi. L'app usa solo il campione locale e non scarica o riproduce il video.

Per preparare quell'estratto era stato installato `yt-dlp` 2026.10.26 in una cartella temporanea, non nell'app. Le normali build, il sito e la rigenerazione dai file presenti non richiedono quel download o un servizio a pagamento. I test analizzano durata, frequenza, attacco, picco e identità byte per byte delle copie web/Android.

`androidTest/res/raw/media_probe.wav` è soltanto il segnale del lettore esterno usato nei test di audio focus. Non è un decimo segnale dell'app finale.

## Marchio t.

La forma originaria è `assets/branding/t-dot-path.txt`. `generate-brand-icons.py` la usa per i vettori del sito e Android. `render-brand-icons.mjs` rasterizza le icone web in PNG 192 e 512. Lo script di rendering richiede Playwright nell'ambiente di sviluppo, senza aggiungerlo al bundle del sito.

I file Android `ic_launcher_foreground.xml`, `ic_launcher.xml`, `icon_colors.xml` e `ic_timer_notification.xml` definiscono launcher/adattamento e notifica. Il manifest web elenca i PNG. Cambiare il tracciato richiede rigenerazione e controllo visivo su maschere/dimensioni, non modifiche indipendenti disallineate sulle due piattaforme.

## Lingue generate

`locales/messages.json` è il catalogo modificabile. Ogni chiave italiana contiene traduzioni inglese/spagnolo ed eventuale variante italiana. `scripts/generate-locales.py` genera il modulo compatto web, gli ID Kotlin e le risorse XML. Non modificare solo un risultato generato: al comando successivo la modifica andrebbe perduta. Nessuna traduzione viene chiesta a servizi online durante l'uso.

Le tabelle riga per riga comprendono anche questi file generati. Rimangono senza migliaia di commenti all'interno: questo conserva formato, riutilizzabilità e chiara distinzione tra sorgente editoriale e output.

## Wrapper Gradle e schema Room

`gradle-wrapper.jar` è il bootstrap binario del wrapper Gradle. `gradle-wrapper.properties` blocca Gradle 9.4.1; `gradlew` e `gradlew.bat` lo avviano. Questi file non sono timer scritti per il progetto: si spiegano nei documenti affiancati senza alterare il codice del wrapper.

Il JSON in `android/app/schemas` è lo schema esportato da Room, non un database personale. Registra tabelle, colonne e versione. Va conservato per le future migrazioni; non si sostituisce con cancellazione automatica dei dati.

## File esclusi dal repository

APK/AAB, output, screenshot di verifica, ZIP, log, cache Gradle/Kotlin, `node_modules`, cartelle temporanee, impostazioni locali dell'SDK e chiavi di firma rimangono fuori da Git. I report non sensibili utili alla revisione sono stati copiati espressamente in `docs/verifiche`. Il repository contiene i sorgenti per ricostruire il progetto, non un archivio dei dati utente.
