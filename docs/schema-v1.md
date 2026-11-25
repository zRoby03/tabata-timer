# Schema condiviso v1

Prima fase: contratto definito prima delle interfacce. Fixture comune: `fixtures/timeline-cases.json`, letta dai test JavaScript e Kotlin.

Backup: `{schemaVersion:1, exportedAt: ISO-8601 UTC, workouts: Workout[], sessions: Session[]}`. I campi aggiuntivi sono tollerati; versione diversa rifiutata. Import sostituisce l'intero archivio solo dopo validazione e conferma. ID unici per collezione e per elementi di ogni workout. Le sessioni possono riferirsi a workout cancellati.

Workout: `schemaVersion`, `id`, `title`, `description`, `color` opzionale (`#RRGGBB`, default lilla), `warmupSec`, `cooldownSec`, `blocks`.

Blocco: `id`, `title`, `cycles`, `restBetweenCyclesSec`, `restAfterBlockSec`, `exercises`.

Esercizio: `id`, `title`, `repetitions`, `workSec`, `restBetweenRepsSec`, `restAfterExerciseSec`.

Phase: `type`, `blockId` e `exerciseId` nullable, `durationMs`, `label`. Tipi correnti Workout: WARMUP, WORK, REST_REP, REST_EXERCISE, REST_REPEAT, REST_BLOCK, COOLDOWN. REST_CYCLE non viene più generata. Studio aggiunge REST_ACTIVITY; REST_LONG è solo storico. Solo WORK contribuisce al lavoro svolto. Le etichette sono presentazione, non identità.

Session: `id`, `workoutId`, `workoutTitle` opzionale per conservare il nome storico, `startedAt`, `endedAt` (ISO-8601 UTC), `plannedMs`, `elapsedMs`, `workMs`, `completedWorkIntervals`, `plannedWorkIntervals`, `pauses` (numero), `status` (COMPLETED o INTERRUPTED). `elapsedMs` è tempo attivo, esclude pause; `workMs` include lavoro parziale e ripetuto dopo precedente. Percentuale = intervalli lavoro completati distinti / pianificati * 100. Saltare non accredita lavoro o completamento. Tornare indietro consente ripetere una fase senza contare due volte il suo completamento. Lo stato COMPLETED richiede fine timeline e tutti gli intervalli lavoro conclusi; terminazione esplicita è INTERRUPTED.

Limiti di sicurezza condivisi: ID ASCII `[A-Za-z0-9_-]`, 1–128 caratteri; titoli 1–120 caratteri non vuoti; descrizioni fino a 2000. Interi: durate 0–3600 secondi (lavoro minimo 1), cicli/ripetizioni 1–100, blocchi 1–50, esercizi per blocco 1–100. Massimo 10.000 fasi e 24 ore per timeline. Backup massimo 5 MiB, 500 workout e 10.000 sessioni; nessun taglio silenzioso.

Ordine: warmup legacy, blocchi → cicli → esercizi → ripetizioni, cooldown. Il recupero personale vale tra WORK consecutivi nello stesso blocco, anche al cambio ciclo (REST_REPEAT). Al confine effettivo si usa solo restAfterBlockSec. Nessun recupero dopo l’ultimo WORK. Totale = somma durationMs; effetti audio esclusi. Riferimento aggiornato: `6*20 + 5*10 = 170 s = 2:50`, contro i precedenti 220. Ulteriori fixture equivalenti JS/Kotlin in `fixtures/workout-recovery-v2.json`.

Web: un solo snapshot atomico `tabata:v1:data`. Default RAM; persistenza scelta esplicitamente. Errore storage conserva RAM e vecchio snapshot, con avviso ed export disponibile. Disattivare persistenza rimuove esplicitamente lo snapshot su conferma; dopo il passaggio la modalità temporanea non scrive l’archivio; aspetto e ultima modalità usata sono preferenze separate.

Android: Room schema 1, tabelle workout e session_record con JSON v1 e chiave primaria ID; checkpoint separato per recuperare una sessione interrotta dal processo. Nessuna migrazione distruttiva. Aggiornamenti futuri richiedono Migration esplicita e test, non reset database.

Estensioni opzionali sessione v1: `completedPhases` e `plannedPhases` (sempre insieme), per conteggiare tutte le fasi concluse naturalmente, inclusi i recuperi. Interi 0–10000, completate non oltre pianificate e non sotto gli intervalli lavoro completati. I backup precedenti senza questi due campi rimangono validi. Le fasi saltate non sono conteggiate e le fasi ripetute non sono duplicate. Le pause sono interi 0–2147483647. Date con UTC `Z` e precisione fino ai millisecondi.

Da 1.2, ogni esecuzione inizia con un conto alla rovescia fisso di 20 s: fase virtuale `PREPARE`, indice runtime -1. Il compilatore workout e lo schema JSON restano invariati. `plannedMs`, `elapsedMs`, `workMs` e i contatori delle fasi misurano l'allenamento, escludendo questa preparazione. `startedAt` registra comunque il momento in cui si preme Avvia; `pauses` include anche le pause durante la preparazione. Un'interruzione prima dell'allenamento registra quindi una sessione INTERRUPTED a 0 ms attivi. La preparazione può essere messa in pausa o saltata; tornare alla prima fase del workout non la ripete. Il workout di riferimento ora dura 170 s, preceduto da 20 s di preparazione; eventuali sessioni storiche da 220 s restano immutate.

## Estensione Workout/Studio dalla versione 1.5.0

Workout e Session possono includere `mode`, con valore `WORKOUT` oppure `STUDIO`. Se assente, è interpretato come `WORKOUT`, preservando i dati precedenti senza riscriverli o cancellarli. Le sessioni conservano la propria modalità anche dopo l’eliminazione del programma. Le schermate filtrano programmi e attività per modalità prima di calcolare le statistiche.

Prima della 1.7.0, un programma Studio includeva anche `longBreakSec` (0–3600, default 0: disattivata) e `longBreakEvery` (2–100, default 4). In quella versione la pausa lunga sostituiva il recupero dopo ogni N intervalli di concentrazione; non viene aggiunta dopo l’ultimo. La fase generata è `REST_LONG`. Tutte le durate restano espresse in secondi nel JSON e in millisecondi nel motore; la UI Studio converte da/verso minuti interi. I suoi campi durata devono essere multipli di 60 secondi. La preparazione fissa resta 20000 ms, esterna alla timeline del programma e alle statistiche.

Il backup resta schema v1 e contiene entrambe le modalità; la prima estensione era leggibile dalla 1.5.0, mentre la struttura piatta descritta sotto richiede 1.7.0. Il CSV aggiunge `mode` ed esporta la modalità selezionata nell’interfaccia. Room resta alla versione 1 perché le tabelle e le colonne non cambiano: i dati applicativi sono memorizzati come JSON nelle tabelle esistenti. I test coprono lettura legacy, import/export misto, recupero del checkpoint Studio e interoperabilità.



## Runtime Android 1.6.0

Il servizio in primo piano mantiene il motore anche quando l’Activity è chiusa. Un solo writer produce checkpoint e risultato; ricreare l’interfaccia non richiama il recupero del checkpoint della sessione ancora attiva. Arresto forzato o morte del processo mantengono il recupero idempotente come INTERRUPTED. Nessuna modifica a tabelle Room o JSON.

## Estensione semplice dalla versione 1.7.0

Il contenitore del backup rimane v1 e Room conserva i JSON senza migrazione distruttiva. Le versioni precedenti vengono lette tramite un adattatore puro, senza aggiornare automaticamente le righe salvate.

Studio nuovo: `mode: "STUDIO"`, `blocks: []`, `warmupSec: 0`, `cooldownSec: 0`, `activities: [{id, title, concentrationSec, sessions, pauseSec}]`, `restBetweenActivitiesSec`. `title` della radice è il nome del programma; il titolo dell’attività è distinto. Le durate restano secondi interi multipli di 60 (0–3600; concentrazione almeno 60), sessioni 1–100, almeno una attività. ID validi e univoci. I limiti globali restano 10000 fasi e 24 ore.

Studio genera WORK, REST_REP (tra sessioni) e REST_ACTIVITY (tra attività); non genera REST_LONG, REST_CYCLE, REST_BLOCK o COOLDOWN. L’ultima fase è sempre WORK. La preparazione fissa non fa parte della timeline o delle statistiche. `exerciseId` identifica l’attività, `blockId` è nullo.

Storicamente la 1.7.0 aggiungeva `blocks[].restBetweenExercisesSec` e adottava il primo recupero generale se assente. Questa regola è sostituita nella revisione locale descritta sotto: i campi legacy non governano la nuova timeline.

Studio legacy si riconosce dall’assenza di `activities`: ogni esercizio/ciclo viene appiattito in ordine. La prima pausa che separava attività diventa quella comune; pausa lunga, frequenza, warmup e cooldown non sono eseguiti. Questi campi restano nell’originale fino al salvataggio esplicito; nel nuovo JSON Studio non sono esportati i vecchi campi della pausa lunga. Storico e statistiche registrate non vengono ricalcolati. [Regole complete](workout-studio.md#archivi-precedenti).

La fixture `backup-modes-v1.json` resta legacy e verifica un round-trip fedele; la sua nuova timeline dura 235 minuti invece dei 245 registrati storicamente. `backup-simple-v1.json` verifica il formato nuovo su JS e Kotlin: Workout ora 85000 ms (prima 83000), Studio invariato 660000 ms. Un nuovo backup richiede la versione 1.7.0 o successiva su entrambi i dispositivi.

## Localizzazione dalla versione 1.8.0

Nessuna modifica allo schema. Identificatori, enumerazioni, colonne CSV e valori salvati restano interoperabili. La lingua vive in `tabata:language` sul web e `language` nelle SharedPreferences di aspetto Android. Non è esportata nel backup. Cambiare lingua non riscrive titoli, descrizioni, sessioni o durate; solo la presentazione e i valori iniziali dei nuovi elementi cambiano.

## Regole locali del 24 novembre 2026

JSON e Room restano 1. `timingVersion: 2` è un campo opzionale Workout per distinguere i nuovi input rigorosi dagli archivi precedenti. Il parser legacy conserva originali e campi rimossi. La normalizzazione pura marca soltanto la copia per anteprima/editor/esecuzione, con avviso quando cambia il comportamento; salvataggio esplicito per adottarla. Nessun ricalcolo della cronologia.

Nuovi minimi: WORK 5, recupero personale 0 oppure almeno 5, recupero effettivo tra blocchi almeno 5, cooldown 0 oppure almeno 10. Su copie legacy si adattano i positivi sotto soglia e i confini 0–4; Studio escluso. Limiti massimi e strutturali sopra invariati. Il recupero ordinario è `restBetweenRepsSec`; `restBetweenCyclesSec`, `restBetweenExercisesSec` e `restAfterExerciseSec` rimangono dati legacy ignorati nell’esecuzione Workout.

Metadati runtime non persistiti: indice/titolo blocco, indice/totale cicli, esercizio, ripetizione, chiave/argomenti etichetta e entryCue. JS conserva ripetizione anche in proprietà dedicate; Kotlin negli argomenti strutturati. phaseRun distingue le visite; naturalEndings identifica separatamente fine lavoro e cooldown. Nessuna decisione sonora analizza il testo tradotto. [Regole e avvisi](workout-studio.md#archivi-precedenti).

## Minimi nei campi di Timer ed editor

Workout richiede almeno 5 secondi per lavoro, recupero personale e recupero tra blocchi. Il defaticamento si attiva con un interruttore e accetta durate da 10 secondi. Quando è disattivato, il valore interno resta 0. In Studio concentrazione, pausa e pausa tra attività richiedono da 1 a 60 minuti interi. I conteggi di giri, serie e sessioni continuano a partire da 1.

Gli archivi v1 conservano le pause che in precedenza erano state omesse. Aprendo uno di questi programmi nell’editor, la copia in modifica propone il minimo per i campi a zero. Il programma archiviato viene aggiornato dopo il salvataggio esplicito.
