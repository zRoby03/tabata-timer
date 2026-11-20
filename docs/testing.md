# Verifiche e criteri di accettazione

Verifiche locali aggiornate il 20 novembre 2026 alla versione 1.8.0, su web e Android. Nessuna pubblicazione o attivazione di servizi a pagamento. Le prove browser usano profili isolati; quelle Android un emulatore dedicato Medium_Phone Android 17/API 37.

La 1.8.0 rimuove i pulsanti demo e i testi sotto al timer/piè di pagina, e aggiunge italiano, inglese e spagnolo con rilevamento iniziale e selettore. Le prove complete browser verificano ogni lingua su editor, salvataggi, timer, statistiche, Guida e offline; nomi personali e archivi restano invariati.

## Risultati osservati

| Controllo | Esito |
| --- | --- |
| JavaScript node:test | 80/80, zero errori |
| Kotlin/JVM JUnit | 39/39, zero errori |
| Android suite completa | 16/16, zero errori o test saltati |
| APK debug 1.8.0, versionCode 15 | Build riuscita |
| lintDebug | 0 errori, 15 avvisi su SDK/versioni disponibili/suggerimenti API |
| Browser principale | 8 gruppi: timer, salvataggio, durata 3:40, pausa/ripresa, statistiche, importazione, offline, assenza di rete esterna |
| Browser casi limite | 9 gruppi: validazione, modalità temporanea, annullamento, Guida, persistenza editor, viewport equivalente al 200%, quota esaurita, storage negato, importazione errata |
| Aspetto | 9 palette in ordine richiesto; 18 combinazioni tema/palette; contrasto del testo almeno 4,5:1; persistenza e offline |
| Browser Workout/Studio | 7 gruppi: attività piatte e pause, layout a 320/390/768/1440, preparazione/audio, archivi e statistiche distinti, Guida esatta, conversione legacy senza riscrittura fino al salvataggio, preferenze e offline |
| Interazioni semplici | Aggiunta/eliminazione Studio, ultima attività protetta; un solo recupero Workout con tre esercizi, nessun campo per esercizio; anteprima coerente |
| Lingue | Tre flussi browser completi; catalogo comune e segnaposto verificati; Android cambia lingua a timer attivo, mantiene sessione e aggiorna notifica |
| Web Audio | PCM identici ad Android, fischio 500 ms ×1,2 con gain 1; sospiro 500 ms gain 0,85; altri suoni invariati; nessuna saturazione |
| Interoperabilità JS → Kotlin → JS | Vecchio Workout 220000 ms; vecchio Studio conservato e timeline aggiornata 14100000 ms; nuovo Workout 83000 ms; nuovo Studio 660000 ms |

Studio: testano pause soltanto tra le sessioni e tra le attività, con nomi, sessioni, conversione minuti/secondi, nessuna pausa finale e statistiche uguali alla timeline. Workout: tre esercizi con recuperi storici differenti usano il valore comune; restano i recuperi tra ripetizioni. Gli archivi originali e le sessioni storiche non vengono modificati dall’adattatore.

La prova UI Android modifica due attività, la pausa globale e salva il programma con nome; verifica riapertura e separazione dalla modalità Workout. Il database riapre e scambia la nuova struttura Studio. Guida e palette sono verificate anche nell’app. Il servizio in background non è stato modificato: tutti e quattro i test reali della revisione precedente sono stati rieseguiti con il nuovo modello e i nuovi audio.

I test di background verificano Pausa/Riprendi/Salta/Termina dalla notifica, wake lock rilasciato in pausa e alla fine, distruzione e riapertura dell’Activity, schermo spento, sessioni consecutive senza duplicati e un lettore multimediale in un altro UID. Quel lettore mantiene focus e riproduzione mentre AudioService registra l’avvio dei segnali SoundPool. Il callback pubblico dell’emulatore non espone SoundPool, quindi il test usa la diagnostica del server audio. Non sono stati aperti account Spotify/YouTube.

La prova reale force-stop sul nuovo APK conserva 2 allenamenti, registra una sola attività interrotta (0 → 1 → 1 ai riavvii) ed elimina il checkpoint. La verifica dell’aspetto conserva Scuro/Giallo dopo la chiusura del processo. Lo script di recupero prepara le notifiche sul solo emulatore dedicato, per evitare che il dialogo di permesso interferisca con i tap automatici.

Le prime esecuzioni hanno rilevato aspettative dei test ancora riferite a pausa lunga, sette palette, vecchie etichette e guadagno audio 0,85. I test sono stati aggiornati ai nuovi comportamenti, aggiungendo controlli delle durate e dei dati originali. Una prima partenza dei test Android ha coinciso con l’avvio incompleto dell’emulatore; la suite finale è stata eseguita dopo il boot e si è conclusa senza errori.

## Comandi PowerShell

Dalla radice:

```powershell
node --test web/tests/*.test.js
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
cd android
.\gradlew.bat testDebugUnitTest assembleDebug lintDebug
.\gradlew.bat connectedDebugAndroidTest
cd ..
node scripts/interop-check.mjs
```

Il comando interoperabilità richiede prima JUnit: quel test genera `android/app/build/interop/android-export-v1.json` partendo dalla fixture web. Non richiede un server.

Per i test browser, avvia prima il server statico descritto nel README. Playwright è **opzionale e solo per QA**: durante lo sviluppo è stato usato il pacchetto già incluso nel runtime Codex, senza installare dipendenze nel progetto. `PLAYWRIGHT_MODULE` deve puntare al suo `index.mjs`; se Playwright è già disponibile al progetto, la variabile può essere omessa.

```powershell
# Esempio: adatta il percorso al Playwright già installato nella tua macchina.
$env:PLAYWRIGHT_MODULE = 'C:\percorso\playwright\index.mjs'
$env:TABATA_BROWSER = 'msedge'
node scripts/browser-check.mjs
node scripts/browser-edge-cases.mjs
node scripts/browser-countdown.mjs
node scripts/browser-appearance.mjs
node scripts/browser-modes.mjs
node scripts/browser-simple.mjs
node scripts/browser-languages.mjs
```

`TABATA_URL` può sostituire l'indirizzo predefinito `http://127.0.0.1:5500`. I test non pubblicano nulla. I contesti browser di test sono isolati, non toccano il profilo personale.

Il test reale del processo usa un **emulatore dedicato** con APK debug già installato, forza l'arresto di Tabata Studio e aggiunge una sessione interrotta. Non usarlo su un archivio personale senza backup:

```powershell
& "$env:ANDROID_HOME\platform-tools\adb.exe" install -r '.\android\app\build\outputs\apk\debug\app-debug.apk'
python scripts/android-smoke.py
```

Per verificare il tema Android dopo la chiusura del processo, soltanto su un emulatore dedicato con APK già installato:

```powershell
python scripts/android-appearance.py
python scripts/android-languages.py
```

Questo controllo imposta Scuro e Giallo, riavvia l’app e legge le preferenze locali. Non cambia l’archivio degli allenamenti.

## Verifiche ancora manuali

Non sono stati collegati telefoni fisici. Rimangono da verificare: resa e selettore file su dispositivi reali Android; browser mobile Chrome/Safari su telefono; suoni/vibrazione effettivamente percepiti, volume zero e Non disturbare; wake lock su hardware fisico; blocco fisico schermo su telefoni reali; TalkBack/screen reader; zoom browser nativo al 200% oltre al test del viewport equivalente; comportamento OEM e risparmio energetico prolungato.

Il motore web è verificato con salti di clock e ritardi di polling, ma non certificato per avvisi in un browser congelato dal sistema. La UI dichiara il limite. Android dalla 1.6.0 usa un servizio in primo piano e wake lock parziale: background e schermo spento sono coperti dalle nuove prove. Restano da verificare telefoni fisici e politiche energetiche OEM.

Il 20 novembre 2026 è stata autorizzata ed eseguita la pubblicazione; le verifiche sull’origine HTTPS pubblica sono descritte sotto. Non sono stati eseguiti test di migrazione da vecchi schemi Room perché questa è la prima versione (schema esportato 1); i futuri incrementi dovranno aggiungerli.

## Note di verifica della localizzazione

Il primo controllo Android ha rilevato che il motore regex Android richiede l’escape esplicito di entrambe le parentesi graffe dei segnaposto. Corretto e verificato sulle risorse reali, oltre ai test JVM. Il cambio lingua non ricrea il motore: la prova mantiene lo stesso ID di sessione durante cambio lingua e ricreazione dell’Activity. Distinti Pause/Pausar del controllo timer e Break/Pausa del campo Studio. Il marchio Tabata Timer resta identico in ogni lingua.

Le lingue sono incluse anche negli eventuali bundle Android, senza split/download a richiesta. La localizzazione conserva solo l’Application e risorse configurate, senza riferimenti ad Activity. Nessun controllo Lint disabilitato e nessuna nuova dipendenza. Il pacchetto web contiene i cataloghi offline e il pacchetto sorgenti comprende il catalogo comune e il generatore.

Esito finale 1.8.0: 80 test JS, 39 JVM e 16 Android, tutti superati. Lint: 0 errori, 15 avvisi preesistenti su SDK, versioni disponibili e suggerimenti API; nessun avviso nuovo di localizzazione lasciato aperto. Il controllo CLI conferma English/Español/Italiano dopo force-stop. Su APK installato: 2 workout conservati, sessioni 0 → 1 → 1, checkpoint rimosso. Un dialogo ANR di System UI dell’emulatore API 37 ha interrotto la prima prova CLI: chiuso il dialogo del sistema, la prova è stata ripetuta con successo. Non è un test su telefono fisico.

## Verifica del marchio e del nuovo fischio

Nuova verifica Node sulla risonanza del fischio: circa 1758 Hz stimati, sotto 2100 Hz, bordi più morbidi, sempre 500 ms e senza saturazione. Gli altri otto audio risultano identici ai precedenti e i nove campioni coincidono tra web e Android. Il test browser offline prova la sequenza audio reale con i medesimi volumi. Il test del nome prova Tabata Timer in titolo e pagina, entrambe le modalità e tutte le lingue; i vecchi parametri v/mode sono gestiti e rimossi dall’URL visibile. Manifest web con nome completo e icone SVG/PNG, Android con icona adattiva e monocromatica.

La prima compilazione incrementale dopo lo spostamento delle risorse dell’icona manteneva riferimenti obsoleti: ricompilazione pulita delle sole cartelle build verificate nel workspace. Il primo avvio strumentale ha coinciso con l’emulatore non ancora pronto; ripetuto a boot completato. Nessun dato utente cancellato e nessuna pubblicazione.

Esito della revisione del marchio/fischio: 81 test JS, 39 JVM e 16 Android superati; build pulita riuscita, Lint 0 errori e 15 avvisi preesistenti. Ripetuti browser-branding, browser-countdown e browser-languages: nome, indirizzo pulito, audio offline e tre lingue. Il launcher Android è stato verificato visivamente sull’APK installato e il package conferma l’etichetta Tabata Timer. Icone web SVG/PNG e pacchetti ZIP verificati; nessuna pubblicazione. I file per l’utente sono Tabata Timer.apk e Tabata Timer.zip.

## Pubblicazione autorizzata — 20 novembre 2026

Ripetuti gli 81 test JavaScript, tutti superati. Browser-branding e browser-languages superati sul pacchetto statico destinato a Sites. Il controllo ZIP ha rilevato una copia di main.js precedente all’ultima pulizia dell’URL: rigenerato lo ZIP e confrontati tutti i 37 file con i sorgenti. L’archivio Sites contiene questi stessi file e il manifest di hosting; nessun APK o dato personale incluso. APK Android invariato, verificato tramite SHA-256; le prove native già completate restano valide, senza una nuova compilazione per la sola pubblicazione.

Deploy Sites pubblico riuscito: https://tabata-timer.paddedlotus8.chatgpt.site. Ripetuto browser-languages.mjs sull’origine pubblica, con contesti browser nuovi e senza autenticazione: tre lingue, persistenza del selettore e dei programmi, nomi utente, Workout/Studio, preparazione, timer, statistiche, Guida, palette, rimozioni richieste, viewport 320/390/768/1440 e offline superati. Nessun errore JavaScript. Prova eseguita con Edge desktop automatizzato; non sostituisce il collaudo su telefoni fisici.

Report: output/publication.json e output/browser-publication-languages.json. Nessun nuovo pacchetto da installare, servizio a pagamento o distribuzione pubblica dell’APK.

## Cloudflare e pagine pubbliche

Superati 82 test JS: aggiunta una regressione che esegue il fetch handler reale del service worker offline e verifica `/about`, `/privacy`, le varianti `.html` e le query lingua. Questi indirizzi non devono ripiegare sul timer. Validati i riferimenti locali, le tre sezioni linguistiche per pagina e il contenuto di ads.txt fornito dall’account. ZIP Cloudflare verificato file per file: 42 file web e `_headers`; nessun APK o credenziale.

Controllati nel browser il dominio Cloudflare pubblico, entrambe le modalità, tre lingue e nuove pagine Informazioni/Privacy, link dalle Impostazioni e rimozioni richieste. I dati del timer e le durate non sono stati modificati. APK e audio Android invariati; non sono stati ripetuti i test nativi per questo aggiornamento solo web.

AdSense: verifica pubblica ads.txt Autorizzato, richiesta di revisione inviata e profilo publisher completato dall’utente. Messaggio europeo pubblicato nell’account con tre lingue e rifiuto in tutte le regioni, una sola unità display creata. Nessun test su annunci reali: lo snippet è conservato fuori da web, in attesa dell’approvazione e della successiva integrazione. Il messaggio pubblicato nel pannello non è ancora un banner caricato sul sito.

Il client HTTP Python riceve Cloudflare 1010, quindi non si dichiara riuscito il confronto dei file remoti via Python. Il sito è stato verificato nel browser e il crawler Google ha autorizzato ads.txt. Non modificate protezioni per aggirare il blocco. Report in output/cloudflare/publication.json; immagini di verifica in output/screenshots.
