# Verifiche e criteri di accettazione

Revisione **locale del 24 novembre 2026**, branch `codex/workout-recuperi-audio`, base main `82d3580`. Nessun commit/push/deploy; checkout didattico e APK consegnato preservati. I numeri storici più sotto non vanno confusi con questa esecuzione.

## Esito attuale

| Controllo eseguito | Risultato |
| --- | --- |
| Node | 94/94, zero fallimenti, nessun test saltato |
| Kotlin/JVM | 48/48, zero fallimenti/errori/saltati |
| Android strumentale su Medium_Phone API 37 | Suite completa 17/17; nuovo caso editor/anteprima 1/1, totale 18 casi distinti. Ultima modifica focus: rieseguiti HornTest e BackgroundTimerTest (6/6) |
| assembleDebug | Riuscito, APK solo nella cartella build |
| lintDebug | 0 errori, 18 avvisi: 15 precedenti e 3 TypographyDashes nuovi sul testo richiesto “3-2-1” |
| Interoperabilità | Quattro export Kotlin riletti in JS; dati originali e sessioni storiche conservati, demo nuova 75000 ms |
| Browser-countdown | 13 WAV decodificati offline; 3-2-1, due finali identici, nessun bip nel defaticamento, coda da 1,92 s completa, stop/skip/mute |
| Browser-modes | 7 gruppi: Studio invariato, nomi pause, modalità separate, Guida, compatibilità e offline |
| Browser-languages | IT/EN/ES, nomi personali invariati, tre card Guida, nove palette, responsive e offline |
| Browser principale | 8 gruppi: timer 2:50, salvataggio, persistenza, pausa/ripresa, statistiche, importazione, offline e rete solo locale |
| Browser casi limite | 9 gruppi: input invalidi, uso temporaneo, annullamento, Guida, persistenza editor, viewport/target, quota e storage negato, import errato |
| Browser-simple | Attività Studio aggiunte/eliminate, ultima protetta, recuperi Workout personali e niente vecchi campi |
| Browser-workout-recuperi | Recuperi indipendenti, visibilità/reorder/copia/eliminazione, minimi 6/7, anteprima per blocco/ciclo, archivi intatti fino a Salva |

Le fixture condivise coprono recupero personale dopo ripetizione/esercizio/ciclo, sostituzione al confine anche con valori diversi, tre blocchi, zero consentito, finale senza pausa e demo completa. Il rapido 20/10 × 8 con due serie dura 470 secondi, più 20 di preparazione. La demo ha WORK 35, recuperi 30, defaticamento 10: pianificato 75, con preparazione 95; sola coda audio fino a 96,92.

I test dei dati verificano conversioni legacy su copia, avviso, export originale e cronologia invariati. Le anteprime e i segnali usano metadati, non stringhe tradotte. [Motivi dei valori attesi aggiornati](recuperi-audio-2026-11-24.md).

Android: prova reale SoundPool sui 13 campioni, finestra focus continua, rilascio e durata finale. Il lettore di prova nel secondo UID continua senza perdite complete/transitorie, consentendo MAY_DUCK; le prove a schermo spento/notifica restano attive. I test unitari iniettano focus negato e callback/scadenze; non simulano il volume percepito di Spotify/YouTube.

Prime esecuzioni: alcune aspettative erano ancora quelle della precedente logica (220 → 170 e 83 → 85); aggiornate dopo calcolo esplicito e aggiunta di fixture indipendenti. Tre test browser lanciati contemporaneamente hanno avuto timeout iniziali senza raggiungere la UI; rieseguiti singolarmente sono passati, senza correzioni al prodotto per quei timeout. Controllo visivo: eliminate classi CSS generiche che coloravano lo sfondo delle nuove righe, per mantenere testo leggibile.

I 18 avvisi Lint non sono soppressi: SDK/target e versioni disponibili (12), suggerimenti KTX (3), tipografia sul conteggio “3-2-1” (3). Nessun aggiornamento indiscriminato delle dipendenze. I tre avvisi tipografici sono mantenuti per rispettare il testo editoriale dell’utente, che non rappresenta un intervallo numerico.

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

Il comando interoperabilità richiede prima JUnit: genera gli export legacy, modalità, semplice e recovery-v2 in `android/app/build/interop/`. Non richiede un server.

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
node scripts/browser-workout-recuperi.mjs
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

## Cronologia delle verifiche precedenti

I risultati da qui in avanti riguardano revisioni precedenti, non descrivono le nuove regole audio/recuperi.

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
