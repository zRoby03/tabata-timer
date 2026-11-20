# Kotlin e Android

Il modulo `android/app` è un'app nativa. [L'indice completo](indice-file.md) rimanda a ogni dichiarazione e riga; questa guida spiega perché le classi sono divise così e come leggere Kotlin nel progetto.

## Il linguaggio nei sorgenti

| Costrutto | Significato concreto qui |
| --- | --- |
| `data class` | Contenitore di valori come Workout, Block, Exercise, StudyActivity e Session; fornisce `copy()` e confronto strutturale |
| `val` / `var` | Riferimento non riassegnabile / riassegnabile; `val` non rende automaticamente immutabile una lista mutabile |
| `Type?`, `?.`, `?:` | Valore opzionale, accesso solo se presente, valore alternativo; usati per timer non avviato, risultati e campi legacy |
| `copy(...)` | Nuovo modello con i soli campi cambiati; la bozza non deve modificare di nascosto il programma salvato |
| `let`, `takeIf`, `runCatching` | Elaborazione del valore presente, filtro condizionale e cattura dell'errore; non sono nuovi thread |
| `when` | Selezione tra stati, azioni o tipi di fase |
| `mapIndexed`, `forEachIndexed`, `repeat` | Iterazione con indice, necessaria anche per sapere quando non aggiungere una pausa |
| `suspend fun` | Funzione che può sospendere una coroutine; da sola non garantisce esecuzione su un thread separato |
| `viewModelScope.launch` | Avvio di lavoro legato al ciclo di vita del ViewModel |
| `mutableStateOf`, `by` | Stato osservabile con delega: una variazione può richiedere la ricomposizione della UI |
| `@Composable` | Funzione che descrive UI; non deve diventare il proprietario dell'orologio del timer |
| `Mutex.withLock`, `withTransaction` | Serializzazione nel processo e atomicità delle operazioni persistenti, responsabilità distinte |

## Avvio e proprietà delle risorse

`TabataApplication.onCreate()` inizializza lingua, database/repository e `TimerRuntime`. Sono riferimenti dell'applicazione, non di un'Activity. `MainActivity.onCreate()` monta Compose e tratta i permessi di notifica. Ruotare lo schermo può ricreare l'Activity: il motore appartiene al runtime dell'app e continua.

`StudioViewModel` espone lo stato delle schermate, osserva gli aggiornamenti dell'archivio e delega l'esecuzione al runtime. `draft` e `dirty` rappresentano una modifica non ancora salvata. `navigate()` e `chooseMode()` richiedono conferma quando una bozza verrebbe scartata; il cambio modalità è impedito durante sessioni o risultati non ancora salvati. Il dialogo di salvataggio rapido ha uno stato occupato per impedire doppie scritture e mantiene gli errori visibili per un nuovo tentativo.

La UI non salva il timer con un effetto che potrebbe ripetersi a ogni ricomposizione. Il runtime produce il risultato e il repository ne conserva l'ID. La UI legge stato e invia comandi.

## Schermate Compose

`StudioApp.kt` collega navigazione, switch, collezioni, timer, cronologia e dialoghi. `QuickTimerScreen.kt` contiene il timer iniziale; `EditorScreen.kt` l'editor Workout e il collegamento ai campi Studio; `StudyFields.kt` la lista piatta di attività. `StatsScreen.kt` presenta aggregati e grafico, `DataScreen.kt` importa/esporta attraverso il selettore Android, `HelpScreen.kt` conserva solo i tre riquadri richiesti.

I parametri valore/callback mantengono i componenti controllati dal chiamante. Per esempio, `StudyFields` riceve il programma e invia una copia aggiornata; non scrive direttamente nel database. `NumberField`, `StudyNumber` e `QuickNumber` separano input e trasformazione. La validazione completa rimane nel dominio prima di esecuzione e salvataggio.

`StudioTheme.kt` contiene le palette e deriva i colori Material per Chiaro/Scuro. `AppearanceSettings.kt` espone le scelte; il ViewModel verifica l'ID e salva nelle SharedPreferences. Queste preferenze sono condivise dalle due modalità e distinte da Room.

## Dominio puro

`Model.kt` dichiara modelli, ID, limiti e validazione. `Modes.kt` centralizza nomi, filtri e default delle modalità. `QuickWorkout.kt` costruisce i preset. `NormalizeWorkout.kt` converte una copia degli archivi precedenti secondo regole esplicite. `Timeline.kt` valida originale e copia normalizzata, espande le fasi e applica i confini delle pause. `Codec.kt` legge e scrive JSON, controlla sessioni e crea CSV. `Stats.kt` calcola aggregati.

`TimerEngine.kt` riceve orologio monotono e orologio civile sostituibili nei test. Lavora con millisecondi `Long` e fasi già compilate. L'indice `-1` rappresenta la preparazione, esclusa dai contatori di lavoro e durata pianificata. `tick()` recupera delta anche oltre più fasi, mentre insiemi degli indici completati impediscono duplicati. `snapshotSession()` genera uno stato di checkpoint; `finish()` finalizza un solo risultato. Pausa, ripresa, salto e precedente seguono le regole della versione JavaScript.

## TimerRuntime: un solo proprietario del motore

`start()` prepara la sessione e richiede il servizio foreground; `beginPending()` conclude l'avvio quando il servizio è pronto. Questo evita due motori contemporanei. L'inizializzazione del repository viene attesa prima di lavorare sull'archivio.

I comandi vengono applicati al timer corrente e notificano la UI attraverso stato osservabile. `tickAndSave()` è chiamata dal servizio: aggiorna il motore, emette i segnali ammessi, scrive checkpoint a intervalli limitati e salva il risultato. Il checkpoint non viene scritto a ogni fotogramma. Un errore di scrittura mantiene un risultato ancora da salvare e un messaggio; il sistema non lo considera già riuscito.

`attachAudio()` / `releaseAudio()` affidano l'audio al ciclo di vita del servizio. `chooseSound(false)` ferma subito il campione in corso. `clearFinished()` libera il risultato visualizzato solo quando è possibile farlo senza perdere una registrazione pendente.

## TimerService: background e controlli

Il manifest dichiara foreground service di tipo `specialUse`, permessi di foreground service, wake lock, vibrazione e notifiche. Non dichiara Internet. Il servizio entra in foreground con una notifica di bassa importanza e silenziosa: i segnali intenzionali sono riprodotti da SoundPool, non dal suono del canale notifiche.

Il ciclo coroutine aggiorna il timer circa ogni 100 ms durante l'esecuzione e rallenta quando è in pausa. Il tempo effettivo proviene comunque dal clock, non dalla somma dei ritardi della coroutine. Un `PARTIAL_WAKE_LOCK` mantiene disponibile la CPU durante il timer; non è un'esenzione dal risparmio energetico né uno schermo sempre acceso. La preferenza schermo è trattata separatamente.

Pausa/Riprendi, Salta e Termina sono azioni della notifica. I PendingIntent sono immutabili e includono l'identità della sessione: un vecchio comando non deve controllare quella successiva. `notification()` e `updateResources()` aggiornano fase, tempo e testi nella lingua selezionata. Non ricreano inutilmente la notifica a ogni tick.

`onDestroy()` annulla il ciclo, rilascia wake lock e audio e informa il runtime. `START_NOT_STICKY` evita di promettere la ripartenza automatica dopo un arresto del processo. La breve attesa finale permette al segnale di chiusura di terminare prima di rilasciare l'audio. Se Android arresta il processo o il telefono viene spento, al riavvio il checkpoint diventa un'attività interrotta, non una sessione completata inventata.

## Musica, video e segnali

`HornSignals.kt` carica gli stessi WAV del sito in SoundPool e gestisce una riproduzione alla volta. Non richiede audio focus al lettore multimediale: il segnale si somma a YouTube/Spotify senza mandare loro un comando pausa. Si usa il volume multimediale del telefono; il progetto non forza il volume di sistema. I tracker di countdown e transizioni ricordano visita e segnali già emessi, evitando ripetizioni al ritorno della UI o alla ripresa.

Questo è distinto da garantire qualsiasi comportamento su ogni produttore: arresto forzato, spegnimento, restrizioni manuali e volume azzerato rimangono condizioni esterne. I test di coesistenza usano un lettore locale nel solo APK di test, non accessi agli account Spotify o YouTube.

## Room e accesso ai file

`Database.kt` dichiara quattro tabelle: `workout`, `session_record`, `session_checkpoint`, `setting`. Le righe principali conservano l'ID e il JSON del contratto comune. Il DAO usa query parametrizzate e upsert; `StudioRepository` serializza le operazioni con un Mutex.

`initialize()` inserisce i preset soltanto senza marcatore, poi trasforma un eventuale checkpoint in una sessione interrotta e lo rimuove nella stessa transazione. `finish()` salva il risultato e cancella il checkpoint insieme. `replace()` applica l'importazione come sostituzione atomica, dopo la validazione e conferma nella UI. Non viene usata una migrazione distruttiva automatica.

Le esportazioni e importazioni usano Storage Access Framework: l'utente sceglie il documento, l'app riceve un URI e legge/scrive tramite ContentResolver. Non sono necessari permessi generali sulla memoria. Backup cloud e trasferimento automatico Android sono disabilitati nel manifest e nelle regole XML; l'esportazione JSON rimane la copia trasferibile esplicita.

## Traduzioni native

`AppText.kt` carica e ricorda la lingua, usa il contesto dell'applicazione e mantiene le risorse per la configurazione selezionata. `LocaleText.kt` collega i testi alle schermate. `MessageIds.kt` associa chiavi di sistema e ID `R.string`; i tre `messages.xml` sono generati dallo stesso catalogo del web. Il selettore modifica UI e notifica, senza rinominare un programma già salvato dall'utente.
