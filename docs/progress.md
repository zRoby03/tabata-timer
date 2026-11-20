# Registro delle fasi

Riferimento: PDF operativo versione 1 del 12 novembre 2026, sei pagine. Le sue specifiche tecniche sono applicate al progetto richiesto; il testo «prompt da copiare» nel documento non è stato eseguito come comando o azione esterna. Tutto è rimasto nel workspace locale. Nessuna pubblicazione o attivazione di servizi.

## Fase 1 — Contratto dati e compilatore

Creati prima delle interfacce complete: `docs/schema-v1.md`, `fixtures/timeline-cases.json`, dominio JavaScript (`model.js`, `timeline.js`), dominio Kotlin (`Model.kt`, `Timeline.kt`) e relativi test. Definiti semantica dei recuperi, durata unica, limiti e comportamento della sessione. Configurato il progetto Android e compilata una prima schermata Compose senza Room.

Comandi dalla radice:

```powershell
node --test web/tests/*.test.js
cd android
.\gradlew.bat testDebugUnitTest assembleDebug
cd ..
```

Risultati iniziali: 16 test JavaScript e 4 JUnit passati; primo APK compilato. Entrambi i compilatori confermano 220 secondi. La seconda fixture copre tutti i livelli di recupero. Le fixture vengono poi lette automaticamente in entrambi i linguaggi nella fase interoperabilità. Limiti iniziali: nessun lifecycle o salvataggio testato in questa fase.

## Fase 2 — Web completo

Creati `web/index.html`, `styles.css`, `icon.svg`, manifest e service worker; moduli `main`, `editor`, `timer`, `storage`, `stats`, `charts`, `dom`, `io`. Il modulo IO è necessario già per caricare e validare lo snapshot locale; il collaudo incrociato con Android viene eseguito nella fase 4. Nessuna dipendenza web da npm o librerie UI.

Editor completo con blocchi/esercizi ordinabili e duplicabili, preset, conferme, anteprima; timer monotono con controlli, segnali sintetizzati e opzioni; cronologia, statistiche e tabelle dei grafici. Default temporaneo, opt-in localStorage e fallback. Aggiunti test timer/storage/statistiche e script browser.

```powershell
python -m http.server 5500 --bind 127.0.0.1 --directory web
# In un secondo PowerShell:
node --test web/tests/*.test.js
node scripts/browser-check.mjs
```

Risultati: 33 test JavaScript passati; Home/Editor senza overflow alle quattro larghezze; demo reale completa e pausa/ripresa; persistenza al reload; tre grafici accessibili; preview import annullabile; reload offline; richieste solo locali. Limite: le misure browser non rappresentano telefoni fisici.

## Fase 3 — Android Compose e Room

Aggiunti `data/Database.kt`, `ui/StudioViewModel.kt`, schermate Compose Home/Editor/Timer/Cronologia/Statistiche/Dati e `domain/TimerEngine.kt`, `Stats.kt`. Repository Room con tabelle workout, session_record, checkpoint e impostazione inizializzazione; import transazionale; nessun reset distruttivo. Conservazione ViewModel su rotazione, pausa su background e checkpoint per morte del processo.

Usate versioni bloccate e realmente risolte con l'Android Studio installato. KSP 2.3.2 inizialmente presente nella cache non era compatibile con i source set Kotlin integrati di AGP 9.2: sostituito con 2.3.9, senza disabilitare controlli di build.

```powershell
cd android
.\gradlew.bat testDebugUnitTest assembleDebug
.\gradlew.bat connectedDebugAndroidTest
cd ..
```

Risultati finali fase: APK generato, Room schema 1 esportato, test repository e UI su emulatore riusciti. Il primo runner UI non gestiva l'InputManager di API 37: aggiornato alle versioni stabili indicate nel setup, poi 5/5 test strumentali passati. Limiti: hardware fisico, vibrazione e condizioni OEM non verificati.

## Fase 4 — Import/export interoperabile

Completati `domain/Codec.kt`, `ui/DataScreen.kt`, JSON/CSV web, `fixtures/backup-web-v1.json`, `ContractTest.kt` e `scripts/interop-check.mjs`. Stesso formato v1, validazione rigorosa sintattica e semantica, limite file, preview e sostituzione solo confermata. I contatori di tutte le fasi sono estensioni opzionali v1; i backup senza di essi rimangono importabili.

```powershell
cd android
.\gradlew.bat testDebugUnitTest
cd ..
node scripts/interop-check.mjs
node scripts/browser-edge-cases.mjs
```

Risultati: 15 JUnit passati; percorso Web → Kotlin → Web con campi identici e timeline 220 s; import confermato/annullato/invalido verificato nel browser. CSV protegge i titoli interpretati come formule. Limite: il trasferimento manuale tramite selettore file di un telefono fisico resta da provare.

## Fase 5 — Rifiniture, accettazione locale e documentazione

Creati README, setup Windows, guida demo, matrice test, script `android-smoke.py` e report JSON in `output/`. Verificati screenshot web e Android; applicati target web minimi 44 px e reduced motion; avvisi accessibili; icona Android; backup automatici Android disattivati esplicitamente. Ottimizzato il polling: timer Android finito non aggiorna inutilmente la UI, checkpoint periodici solo durante esecuzione. Eliminata la ripetizione degli annunci finali nel timer web.

Il test browser quota ha rilevato che il successo dell'editor copriva un errore di scrittura: corretto e rieseguito con successo, mantenendo RAM e vecchio snapshot. Per Android, una sessione finale non ancora scritta resta esportabile e impedisce di avviarne una nuova fino al recupero del salvataggio.

```powershell
node --test web/tests/*.test.js
node scripts/browser-check.mjs
node scripts/browser-edge-cases.mjs
cd android
.\gradlew.bat testDebugUnitTest assembleDebug lintDebug connectedDebugAndroidTest
cd ..
node scripts/interop-check.mjs
python scripts/android-smoke.py
```

Risultati e requisiti non ancora verificabili senza hardware fisico sono elencati in `docs/testing.md`. Il force-stop reale sull'emulatore ha conservato 2 workout e recuperato esattamente una sessione: conteggio 0 → 1 → 1 ai due riavvii. Nessun permesso Internet nel manifest Android unito; nessuna rete esterna nel sito. Progetto avviabile in locale e APK debug disponibile. L'accettazione su telefoni reali resta esplicitamente aperta; non viene presentata come già superata.

## Revisione 1.1 — Percorso iniziale più semplice

Su richiesta successiva, consultate schede ufficiali App Store e ridisegnato il primo utilizzo in entrambe le versioni. Il timer rapido mostra tre valori (lavoro, recupero, giri) e Avvia. Serie e opzioni aggiuntive sono richiudibili. Le sezioni iniziali passano da cinque a tre: Timer, Allenamenti, Attività. Eliminata la grande introduzione promozionale; libreria con un'azione principale e menu ⋯; impostazioni del timer in un pannello secondario. Cronologia, grafici, editor completo e backup rimangono accessibili. Nessuna modifica allo schema persistente o ai dati esistenti.

Aggiunti `web/js/quick.js`, `web/tests/quick.test.js`, `domain/QuickWorkout.kt` e `ui/QuickTimerScreen.kt`; aggiornati navigazione, test browser, test UI, demo e README. Corretto anche il ritorno dall'editor: una bozza già scartata non causa ulteriori richieste di conferma all'avvio rapido.

Comandi eseguiti: `node --test web/tests/*.test.js`, entrambi gli script browser, `node scripts/interop-check.mjs`; Android `testDebugUnitTest assembleDebug lintDebug` e `connectedDebugAndroidTest`. Risultati: 35 test JavaScript, 16 JUnit e 6 strumentali riusciti; 8 gruppi browser principali e 8 casi limite, incluso avvio rapido senza editor e blocco dei valori invalidi. Screenshot web verificati a 390 px. I limiti su hardware fisico restano quelli della matrice test. Android aggiornato a versionName 1.1/versionCode 2, Room sempre schema 1. Dettagli e riferimenti in `docs/ux-semplice.md`.

## Revisione 1.2 — Preparazione e conto alla rovescia sonoro

Aggiunti 20 secondi prima di ogni avvio, anche dalla libreria e dalla demo, senza cambiare i workout salvati. Il conto iniziale può essere messo in pausa o saltato e non viene contato come lavoro/allenamento. UI ed anteprime segnalano separatamente i 20 secondi. La fase runtime PREPARE ha indice -1 e non entra nei contatori delle fasi del workout.

La trombetta locale scandisce gli ultimi 3, 2, 1 secondi di ciascuna fase; una nota finale segnala il workout completo. WAV da 7982 byte identico in web e Android, generato da `scripts/generate-horn.py`, riprodotto con Web Audio e SoundPool. Modulo puro CountdownCues in entrambi i linguaggi: niente ripetizioni dopo pausa/ripresa e niente raffiche di note arretrate. Modalità silenziosa e vibrazione opzionale conservate. Cache offline aggiornata con il suono; nessuna dipendenza o servizio aggiunto.

Verifica finale: 42 test JavaScript, 22 JUnit, 7 strumentali Android passati; build e Lint senza errori. I 16 gruppi browser esistenti sono passati e il nuovo `scripts/browser-countdown.mjs` ha verificato 20 s iniziali, vere sorgenti Web Audio alle tre altezze su preparazione/lavoro/recupero, nota finale, pausa e contatori invariati. Il test SoundPool sull'emulatore conferma caricamento del WAV e avvio dei tre toni; percezione su altoparlanti fisici, volume e Non disturbare restano da verificare su telefono. Comandi e dettagli in `docs/audio-countdown.md`. APK versionName 1.2/versionCode 3; JSON e Room sempre v1.

## Revisione 1.2.1 — Rimozione del controllo Riscaldamento

Rimosso su richiesta il campo Riscaldamento dalle opzioni rapide e dall'editor completo, sia web sia Android. I 20 secondi di preparazione iniziale rimangono parte dell'avvio. La modifica riguarda i controlli dell'interfaccia: eventuali fasi di riscaldamento già contenute nei dati salvati o importati restano compatibili e non vengono cancellate. Versione Android 1.2.1/versionCode 4 e cache web aggiornata. Eseguiti i test JavaScript e JVM, la build APK e i controlli browser del percorso principale e del conto alla rovescia.


## Revisione 1.3 — Layout moderno, testi chiari e salvataggio visibile

Su richiesta dell’utente, semplificati entrambi i layout e rivisti i testi italiani. Le tre sezioni rimangono Timer, Allenamenti e Attività. Avvia è l’azione principale; Salva allenamento è visibile subito sotto e apre nome e conferma Salva sul dispositivo. Sul web la conferma attiva la persistenza e mantiene aperta la finestra in caso di errore. I tentativi ripetuti usano lo stesso ID e non creano duplicati. Anche il salvataggio dell’editor conserva i dati sul dispositivo. Android usa lo stesso percorso, con guardia contro invii ripetuti durante la scrittura.

Riorganizzati Allenamenti, Attività, Statistiche e Impostazioni. Le attività mostrano tre metriche e dettagli richiudibili; le impostazioni sono divise per scopo. Aggiunta Guida nell’intestazione e nelle impostazioni, con istruzioni per tutte le funzioni e per le copie locali. La documentazione completa è in `docs/guida-utente.md`; aggiornati README, setup, demo e matrice test. Eliminati termini inglesi non necessari dall’interfaccia e uniformati date e decimali italiani. Android permette di scegliere il colore per nome nell’editor. I nuovi modelli non aggiungono un riscaldamento nascosto; quelli già presenti negli archivi rimangono compatibili.

Il web conserva caratteri di sistema e risorse locali, senza nuove dipendenze. Separati i moduli `help.js` e `views.js`, ripulito il foglio di stile e aggiornata la cache statica a v1.3.0. Preparazione di 20 s, trombetta 3-2-1, schema JSON v1 e Room schema 1 restano compatibili. Android versionName 1.3/versionCode 5.

Verifiche del 18 novembre 2026: 45/45 test JavaScript, 22/22 JUnit, 8/8 strumentali su emulatore; APK generato e Lint senza errori (13 avvisi documentati). Passati 8 gruppi browser principali, 9 casi limite e il controllo Web Audio. Controllati annullamento senza scritture, salvataggio con nome e ricaricamento, guida, quota esaurita senza perdita del precedente archivio e senza duplicati, importazione e uso offline. Screenshot verificati per web e Android; nessuno scorrimento orizzontale nelle viste web a 320/390/768/1440 px. Interoperabilità Web → Kotlin → Web riuscita. Il controllo di arresto forzato conserva 2 allenamenti e registra le sessioni 0 → 1 → 1, con checkpoint eliminato dopo il recupero. Un iniziale blocco dell’interfaccia di sistema dell’emulatore è stato risolto prima di ripetere con successo il controllo del processo.

Nessun installer di sistema, servizio a pagamento o pubblicazione aggiunto. Le verifiche su telefoni fisici rimangono quelle elencate in `docs/testing.md`.


## Revisione 1.4 — Tema, sette palette e trombetta distinta

Implementata la richiesta successiva: Impostazioni → Aspetto offre Chiaro/Scuro e le palette Viola, Blu, Verde chiaro, Azzurro, Arancione, Giallo forte e Rosso. Tema e palette si applicano subito a schermate, controlli, timer e grafici; i colori luminosi hanno un primo piano scuro leggibile. Il colore personalizzato dei singoli allenamenti rimane un attributo dell’allenamento. Le preferenze sono separate dall’archivio: chiave locale `tabata:v1:appearance` sul web, SharedPreferences privato su Android. Nessuna modifica a JSON v1 o Room schema 1, nessuna nuova dipendenza.

Rinominati i tre pulsanti rapidi Tabata 1, Tabata 2 e Tabata 3, conservando 20/10, 30/15 e 40/20. La trombetta usa il registro acuto durante WORK e quello grave in recuperi e COOLDOWN; PREPARE conserva il registro intermedio. Rimangono conto 3-2-1, deduplicazione, pausa e suono disattivabile. File WAV originale unico, fattori identici nei due linguaggi. Aggiornati guida integrata, guida utente, README, demo e documentazione audio.

Verifiche: 52 test JavaScript, 24 Kotlin/JVM, 9 strumentali Android passati; assembleDebug e lintDebug riusciti, 0 errori e 13 avvisi. Browser: 8 gruppi principali, 9 casi limite, 5 gruppi nuovi sull’aspetto e prova Web Audio con lavoro, recupero e defaticamento. Verificati tutti i 14 abbinamenti tema/palette, contrasto ≥4,5:1, persistenza al reload, offline, assenza di scritture dell’archivio quando si cambia tema e fallback se le preferenze non possono essere salvate. Il controllo Android dedicato conferma Scuro/Giallo forte dopo force-stop; screenshot verificati. Interoperabilità Web → Kotlin → Web invariata. APK versionName 1.4/versionCode 6, cache web v1.4.0.

Nessuna pubblicazione o attivazione di servizi a pagamento. Restano da provare le differenze sonore su altoparlanti fisici e le condizioni hardware già elencate nella matrice test.


## Revisione 1.4.1 — Due motivetti originali

Il 19 novembre l’utente ha chiesto due motivetti effettivamente diversi. Aggiunti due file PCM originali: `horn-work.wav` (tre note in salita, ritmo breve/breve/lungo) e `horn-rest.wav` (due note più distese in discesa). Android usa gli equivalenti `horn_work.wav` e `horn_rest.wav`, byte per byte identici. Il generatore usa solo Python standard; nessun suono scaricato o nuova dipendenza. I tre campioni, incluso il segnale breve originale per preparazione e completamento, occupano meno di 51 kB per piattaforma.

Il motivetto appropriato suona a ciascuno degli ultimi secondi 3, 2, 1 della fase in corso. `hornSample` seleziona il file; la velocità dei due motivi rimane 1. Pausa, salto, precedente, termine o disattivazione audio interrompono il motivetto in corso; Android interrompe anche al passaggio in background. Conteggio, preparazione, durate, JSON e statistiche restano compatibili. Cache web v1.4.1 e Android versionName 1.4.1/versionCode 7.

Aggiornati guida integrata, README, guida utente, demo e documentazione audio. Verificati 55 test JavaScript, 24 JVM, percorso browser principale e prova Web Audio offline con tutti i campioni, pause e durate corrette. Build APK e Lint riusciti, con 0 errori e 13 avvisi preesistenti. Il test SoundPool controlla caricamento di tutti i file e riproduzione dei motivi. Passati tutti i 9 test strumentali Android, inclusa la riproduzione dei nuovi motivi.

Nessun servizio a pagamento o pubblicazione. Rimane da verificare la percezione dei motivi sugli altoparlanti dei dispositivi fisici.


## Revisione 1.4.2 — Una nota al secondo, tre secondi complessivi

Chiarita dall’utente la richiesta: note alte e basse distribuite sul 3, 2, 1, una sola nota al secondo. Rimossi i motivetti precedenti e la quarta nota allo zero. Generati sei WAV locali da un secondo ciascuno: tre toni alti per lavoro/preparazione, tre toni bassi per recuperi/defaticamento. Timbro sinusoidale morbido, velocità sempre 1 per conservare la durata. Su ciascuna fase la sequenza completa dura tre secondi; le fasi più brevi usano solo i secondi disponibili.

Il timer continua a scegliere una sola nota per secondo con deduplicazione di pausa/ripresa e gestione dei ritardi. Aggiunto arresto della nota al cambio automatico di fase; una sola voce attiva sul web e su SoundPool. Durate, preparazione di 20 s, statistiche e archivio rimangono compatibili. WAV identici tra piattaforme, circa 259 KiB totali. Il generatore crea anche due anteprime audio di tre secondi in `output/audio/`, senza aggiungerle al caricamento dell’app.

Aggiornati test, guida integrata e documentazione. Passati 55 test web, 24 JVM, percorso browser principale e prova audio offline: 15 note per cinque sequenze, una partenza ogni secondo, durata esatta di un secondo e nessun suono aggiuntivo allo zero. APK compilato e Lint senza errori (13 avvisi preesistenti). Passati anche tutti i 9 test strumentali Android. Cache web v1.4.2, Android versionName 1.4.2/versionCode 8. Nessuna pubblicazione o attivazione di servizi.


## Revisione 1.4.3 — Bip brevi e immediati

Su chiarimento dell’utente, i tre segnali devono essere istantanei e distanziati, anziché durare tutto il secondo. Accorciati tutti i sei PCM a 120 ms: attacco di 3 ms, chiusura morbida, note alte per lavoro/preparazione e basse per recuperi/defaticamento. La cadenza rimane una partenza sul 3, una sul 2 e una sull’1, con circa 880 ms di silenzio tra le note. Nessuna modifica al conteggio o agli archivi.

Rigenerate le anteprime di tre secondi con pause silenziose, aggiornati guida e test della durata e dell’attacco. Passati 55 test JavaScript e prova browser Web Audio offline, che verifica i file effettivamente decodificati e la durata di 120 ms. Passati 24 test JVM, build APK e Lint senza errori (13 avvisi preesistenti). Passato il test Android mirato alla riproduzione dei campioni (1/1). Cache v1.4.3, Android versionName 1.4.3/versionCode 9. Nessuna pubblicazione o nuovo servizio.


## Revisione 1.4.4 — Fischietto, sospiro e campanellina

Su richiesta, aggiunti fischietto sintetizzato di 250 ms all’inizio di WORK, sospiro/soffio di 150 ms all’inizio di ogni recupero e tre ding in 900 ms al termine della parte di allenamento. Con defaticamento, la campanella suona al suo inizio; senza, al termine dell’ultima fase. Non viene ripetuta dopo il defaticamento né su Termina manuale.

Aggiunto il modulo puro TransitionCues in JavaScript e Kotlin: una sola emissione per visita di fase, nessuna ripetizione dopo pausa/ripresa, niente raffica di segnali arretrati, campanella una sola volta per sessione. I bip 3-2-1 da 120 ms restano attivi; se il primo bip coincide con il segnale di avvio di una fase molto breve, prevale quest’ultimo. Una sola voce alla volta. Corretto l’arresto audio sullo stato finished per lasciare concludere i tre ding. Nessuna modifica al motore dei tempi, agli archivi o ai conteggi.

Campioni originali deterministici PCM a 24 kHz, identici tra web e Android, senza dipendenze o servizi; nove file totali sotto 95 kB. Cache web v1.4.4, Android versionName 1.4.4/versionCode 10. Aggiornata la documentazione d’uso.

Verificati 65 test JavaScript, 30 JVM, Web Audio offline con durate reali e prova completa browser; build APK e Lint riusciti. Verificati anche intervallo da un secondo, completamento senza defaticamento, campanella lasciata terminare e assenza di campanella dopo stop manuale. Passati tutti i 9 test strumentali Android, inclusi i nuovi campioni SoundPool. Nessuna pubblicazione o attivazione di servizi. La distinzione Studio/Workout resta oggetto della discussione successiva richiesta dall’utente.


## Revisione 1.4.5 — Registri invertiti e segnali da 500 ms

Su richiesta dell’utente, invertiti i bip: bassi nel lavoro, alti nel recupero e nel defaticamento; la preparazione mantiene i toni alti. Fischietto allungato a 500 ms. Sostituito il sospiro sintetico con 500 ms del riferimento YouTube indicato, ritagliato e convertito localmente; provenienza in assets/audio. Fischio e sospiro passano da volume applicativo 65% a 85%, interpretando «+due punti» come +20 punti percentuali; bip e campanella restano al 65%. Nove file identici tra web e Android, 123348 byte per piattaforma.

Verificati 66 test JavaScript, 31 JVM e tutti i 9 test Android allora presenti; build e Lint riusciti. Prova browser offline con durata, frequenze e guadagni reali. Documentata l’installazione temporanea gratuita di yt-dlp nella cartella tmp, senza dipendenze aggiunte all’app. Nessuna pubblicazione.

## Revisione 1.5.0 — Tabata Timer con Workout e Studio

Approvata dall’utente la tabella delle due modalità, con aggiunta dei 20 secondi di preparazione in Studio e switch a scorrimento in alto a sinistra accanto a logo e nome. Aggiunto il marchio Tabata Timer, layout condiviso, memoria dell’ultima modalità, tre preset per modalità e Studio in minuti interi. Studio offre concentrazione, pausa, sessioni, nome della materia/attività e pausa lunga facoltativa ogni quattro sessioni (frequenza modificabile). La pausa lunga sostituisce quella breve, mai dopo l’ultima sessione.

Mode WORKOUT/STUDIO nei programmi e nelle sessioni, default WORKOUT per gli archivi precedenti. Raccolte, attività, filtri, statistiche e CSV separati. Tema e palette comuni. Backup JSON completo con entrambe le modalità; Room e schema JSON restano v1 con campi aggiuntivi. Una sessione attiva impedisce il cambio modalità; l’editor protegge le modifiche non salvate. Aggiornate guide integrate, installazione, demo e guida dei salvataggi.

Passati 72 test web, 35 JVM e 11 strumentali Android. Build APK 1.5.0/versionCode 12 e Lint riusciti (0 errori, 13 avvisi preesistenti). Ripetuti i quattro controlli browser precedenti e aggiunto browser-modes: minuti, editor, pausa lunga, preparazione con audio, completamento, attività/statistiche distinte, memorizzazione e offline, layout a quattro larghezze. Interoperabilità verificata anche con fixture Studio di 245 minuti. Modalità Studio conservata dopo force-stop e riapertura dell’app. Restano i limiti dichiarati del background e la verifica su telefoni fisici. Nessun servizio a pagamento, deploy o pubblicità attivati.

Consegna locale: ZIP dei sorgenti verificato con CRC, APK debug nominato per versione, riepilogo JSON delle verifiche e SHA256SUMS. Esclusi dallo ZIP cache, SDK, percorsi locali e riferimento video completo. Nessun deploy eseguito.


## Revisione Android 1.6.0 — Background e riproduzione musicale

Su richiesta dell’utente, eliminata la pausa automatica quando si lascia l’app. Aggiunti TabataApplication, TimerRuntime e TimerService: un motore condiviso, un solo writer dei checkpoint, servizio foreground specialUse dichiarato per timer avviato dall’utente, notifica silenziosa con Pausa/Riprendi/Salta/Termina e riapertura del timer. Il wake lock parziale tiene attiva la CPU durante il conteggio e viene rilasciato in pausa e alla fine. La chiusura dell’Activity non ferma l’esecuzione. La breve chiusura del servizio lascia terminare la campanella. Gestita la ripresa dopo arresto del servizio e l’avvio di un nuovo timer mentre il precedente si chiude.

SoundPool usa il flusso multimediale e non richiede focus audio: i segnali si mescolano a musica e video senza mettere in pausa o abbassare il lettore. Nessun MediaSession o lettore fittizio nell’app. Permessi aggiunti: foreground service/specialUse, wake lock e notifiche; nessun Internet o accesso ai media. Richiesta notifiche al primo timer Android 13+, con funzionamento mantenuto se rifiutata e pulsante nelle impostazioni per riabilitarle. Aggiornate guide e testi dell’app.

Verificati 72 test Node, 35 JVM e tutti i 15 strumentali Android. Quattro nuovi test reali coprono notifica, risorse, schermo spento, chiusura/riapertura UI, timer consecutivi e audio con un lettore in altro UID. Il lettore resta in riproduzione con focus invariato; gli eventi AudioService provano l’avvio dei segnali. Il callback pubblico API 37 non esponeva SoundPool ed è stato sostituito nel test con la diagnostica del server audio. Nessun controllo Lint disabilitato; aggirato un crash dell’analizzatore usando addOnNewIntentListener. Build 1.6.0/versionCode 13 e Lint riusciti (0 errori, 15 avvisi su SDK/dipendenze/suggerimenti). Nessuna nuova dipendenza o installazione di sistema; il WAV del lettore di prova è stato generato localmente con Python standard. Web invariato. Nessun deploy o servizio a pagamento.

Prova finale di arresto reale su APK 1.6.0: 2 allenamenti conservati, sessioni 1 → 2 → 2 ai riavvii, checkpoint eliminato e nessun duplicato. Verificata visivamente la notifica. Consegna APK e sorgenti 1.6.0, riepilogo JSON e SHA-256 aggiornati.

## Revisione web e Android 1.7.0 — Pomodoro semplice e recupero comune

Applicata l’ultima specifica dell’utente: nove palette, compresa Rosa, in ordine identico sulle piattaforme; Guida ridotta ai tre riquadri indicati, con i quattro capoversi esatti. Nome del programma distinto dal Nome attività. Tolte le abbreviazioni delle unità da campi, preset e anteprime.

Studio usa attività piatte (nome, concentrazione, sessioni, pausa) e una pausa globale tra attività, senza blocchi, cicli, serie, pausa lunga o finale. Pause mai dopo l’ultimo elemento. Condivisi i campi tra schermata iniziale ed editor. Workout mantiene le opzioni esistenti con un solo recupero comune per blocco tra esercizi; recuperi tra ripetizioni invariati.

Aggiunto un adattatore puro per archivi precedenti: nessuna riscrittura all’apertura. Studio espande esercizi/cicli in attività e usa la prima pausa tra attività come valore comune; elimina le opzioni rimosse soltanto dalla copia operativa, con avviso. Workout adotta il recupero del primo esercizio e avvisa quando i valori storici differivano. Solo il salvataggio esplicito sostituisce il programma; sessioni storiche inalterate. Backup JSON e Room restano v1; nuova fixture condivisa per il formato semplice.

Fischio di 500 ms al 120% equivalente: WAV preamplificato 1,2 e guadagno di riproduzione 1, rispettando il limite SoundPool. Picco 13200/32767, senza clipping. Sospiro, bip, campanella, volumi e gestione della musica invariati. Nessuna nuova dipendenza o installazione necessaria. Preparazione di 20 secondi, countdown, servizio Android, notifica e focus audio conservati.

Passati 75 test JS, 37 JVM e 15 strumentali; build 1.7.0/versionCode 14 e Lint riusciti (0 errori, 15 avvisi). Eseguiti sei script browser, inclusi offline e layout a quattro larghezze, 18 combinazioni di aspetto, Guida, flusso salvataggio e conversione legacy. Interoperabilità verificata sui tre backup condivisi. Installazione, schema, funzioni, audio e prove documentati. Nessun deploy, servizio a pagamento o distribuzione pubblica.

Prova finale dell’APK installato: force-stop e due riaperture conservano 2 workout, sessioni 1 → 2 → 2, nessun checkpoint residuo. Preferenze Scuro/Giallo conservate dopo force-stop. Dopo i test Gradle l’APK è stato reinstallato sull’emulatore dedicato; lo script prepara il permesso notifiche prima dei tap, evitando il dialogo di primo avvio. Consegna locale con APK, sito statico e sorgenti 1.7.0, riepilogo delle verifiche e impronte SHA-256.

## Revisione web e Android 1.8.0 — 20 novembre 2026

Applicate le tre rimozioni indicate negli screenshot: pulsanti demo Workout/Studio, riga sul salvataggio sotto al timer e footer web. Conservati i percorsi di salvataggio e le informazioni nelle Impostazioni. Nessuna modifica a tempi o suoni.

Aggiunte italiano, inglese e spagnolo: lingua iniziale dal dispositivo, selettore nelle Impostazioni e preferenza locale separata dall’archivio. Catalogo comune in locales/messages.json, generatore Python standard e risorse native/buildless. Tradotti UI, Guida, editor, timer, anteprime, statistiche, errori, accessibilità e notifica Android. Nomi e descrizioni salvati non vengono riscritti; nuovi elementi usano la lingua corrente. Il cambio lingua durante il timer mantiene la sessione. Nessuna dipendenza di traduzione o servizio esterno.

Conferma dell’utente: preparare tutto, poi attendere il comando “andiamo”. Nessun deploy, pubblicazione, distribuzione pubblica di APK o servizio a pagamento attivato.

Verifica finale 1.8.0: 80 JS, 39 JVM, 16 strumentali Android superati, sette script browser superati, tre lingue persistenti dopo force-stop. Build versionCode 15 e Lint riusciti: 0 errori, 15 avvisi preesistenti. Rimosso un riferimento generico a Context dall’helper linguistico usando Application e risorse configurate; disabilitati gli split lingua del bundle per includere sempre le tre lingue offline. Nessun Lint soppresso. Recupero processo: 2 programmi conservati, attività 0 → 1 → 1, nessun checkpoint residuo. Verificati visivamente browser e Android in spagnolo. Pacchetti APK, sorgenti e solo sito con checksum e controllo integrità ZIP. Pubblicazione ancora in attesa di “andiamo”.

## Fischio, icona e nome Tabata Timer — 20 novembre 2026

Sostituito il vecchio fischio con un segnale originale da 500 ms meno acuto: risonanza principale circa 1760 Hz, risonanza secondaria lieve, aria filtrata e trillo morbido. Conservati livello applicativo, volume multimediale, preparazione, countdown, segnali di recupero e campanella. Gli altri otto WAV sono identici ai precedenti; campioni Android/web identici e picco senza saturazione. Test acustico aggiunto su frequenza e bordi del segnale, anteprima WAV disponibile.

Creato un tracciato comune della t. usata nel marchio. Aggiornati favicon, icona installabile web con PNG 192/512, icona launcher Android adattiva/rotonda/a tema e icona notifica. Nome app, schermata iniziale, titolo browser e manifest web uniformati a Tabata Timer. Workout e Studio restano modalità; nessun dato personale rinominato. Parametri tecnici rimossi dall’URL visibile; revisioni solo interne. Preparati nomi dei file utente senza numeri di versione. Nessuna pubblicazione o servizio a pagamento.

Verifiche completate: 81 JS, 39 JVM e 16 Android, build pulita e Lint 0 errori/15 avvisi preesistenti; nessuna nuova dipendenza o Lint soppresso. Audio reale offline verificato; gli otto suoni non modificati sono byte-identici. Nome e URL verificati nelle tre lingue e in entrambe le modalità; icona t. osservata nel launcher Android dopo installazione dell’APK. File utente senza numeri, sorgenti e sito con integrità ZIP e checksum; revisioni archiviate solo per tracciabilità interna. Nessuna pubblicazione.

## Pubblicazione di Tabata Timer — 20 novembre 2026

Ricevuta autorizzazione esplicita “andiamo”. Confermata l’assenza della frase sul dispositivo, dei due pulsanti demo e della riga sotto al timer. Riutilizzata l’identità Sites già registrata per Tabata Timer, con accesso pubblico e nessuna versione precedente pubblicata. Sincronizzati i 37 file statici correnti, senza test, APK o dati personali. Rigenerato lo ZIP del sito dopo aver rilevato un main.js non allineato.

Ripetuti con successo 81 test JS e i controlli browser del nome e delle tre lingue. Pubblicato il commit verificato tramite archivio Sites; deploy riuscito all’indirizzo https://tabata-timer.paddedlotus8.chatgpt.site. Ripetuti i flussi completi nelle tre lingue sul sito pubblico, compresi salvataggi, Workout/Studio, assenza demo/footer, quattro larghezze e offline; nessun errore JavaScript. Aggiornati documentazione, report e pacchetti locali. Android invariato e APK solo locale. Nessun servizio a pagamento attivato.

## Cloudflare e avvio AdSense — 20 novembre 2026

Su richiesta, creato il progetto Cloudflare Pages tabata-timer con Direct Upload sul piano gratuito. Sottodominio assegnato: tabata-timer-ek1.pages.dev. Nessun dominio acquistato, Worker, database, R2 o piano a pagamento attivato. Lo ZIP dedicato contiene 42 file web e un file header. Aggiornamenti caricati nello stesso progetto Production, senza pubblicare l’APK.

Aggiunte pagine Informazioni e Privacy nelle tre lingue, collegate dalle Impostazioni e coerenti con tema e palette. Contatto pubblico approvato dall’utente. Guida integrata invariata nei suoi tre riquadri. Cloudflare serve HTML senza estensione: adattato il service worker per ripristinare la pagina corretta offline anche con query lingua, con una nuova prova di regressione. Superati tutti gli 82 test JS. I limiti del timer web in background restano gli stessi; Android non modificato.

AdSense: sito aggiunto, ads.txt pubblico riconosciuto come Autorizzato, revisione richiesta. Creata l’unità display 9553674851 con il nome Tabata Timer; annunci automatici OFF. Pubblicato il messaggio europeo Google nelle tre lingue con rifiuto in tutte le regioni, logo e privacy URL; interesse legittimo non preabilitato e ottimizzazione consenso OFF. Lo snippet resta fuori dal sito: annunci e cookie pubblicitari non attivi. Richiesto al gestore di completare le informazioni obbligatorie in Google. Approvazione e successiva integrazione del banner/consenso/CSP ancora da completare; nessun ricavo o approvazione promesso.

Verificate in browser le modalità e le lingue, le nuove pagine e le rimozioni. Il controllo HTTP diretto con Python ha ricevuto Cloudflare 1010 per la firma del client, mentre il browser e la verifica ads.txt di Google funzionano; non sono state indebolite protezioni per forzarlo. Documentati i passaggi gratuiti e le alternative APK/Google Play, con i requisiti Android aggiornati al novembre 2026.

Il gestore ha completato le informazioni del publisher direttamente nel pannello Google. Verificata la conferma “Hai completato tutti i passaggi” e “Abbiamo ricevuto le tue informazioni”. Il sito resta in preparazione, con revisione richiesta e ads.txt Autorizzato. Confermati la pubblicazione del consenso nell’account e l’ultimo deploy Cloudflare riuscito. Salvate prove visive del sito e degli stati Google; nessun dato finanziario acquisito o inserito nei sorgenti.
