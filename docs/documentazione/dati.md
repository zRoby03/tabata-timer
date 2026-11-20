# Dati, formule e compatibilità

Il contratto completo è in [schema-v1.md](../schema-v1.md). JSON e schema Room rimangono versione 1; la modalità e i campi opzionali permettono di leggere gli archivi precedenti senza cancellazioni indiscriminate. Questo documento distingue il modello conservato dalla copia usata per una nuova esecuzione.

## Programma e risultato sono oggetti diversi

Un programma descrive quello che si vuole eseguire: ID, titolo, modalità, blocchi/esercizi Workout oppure attività Studio, pause e metadati. Una sessione descrive quello che è successo: ID proprio, riferimento al programma, copia del titolo, date UTC, tempi e stato. Eliminare il programma non cancella la cronologia. Cambiarne il titolo non riscrive il titolo storico delle attività.

Il backup ha `schemaVersion`, `exportedAt`, `workouts` e `sessions`. Comprende entrambe le modalità. Un archivio storico senza `mode` è Workout; un valore sconosciuto viene rifiutato. Le viste e i CSV filtrano per modalità, non spostano materialmente le righe in un secondo database. Tema, palette e lingua non fanno parte di questa copia.

## Unità

Il modello persiste durate in secondi; le fasi le trasformano in millisecondi. Workout mostra direttamente i secondi. Studio mostra minuti interi e moltiplica per 60 al confine della UI. La rimozione delle etichette abbreviate non cambia questa conversione. Il timer continua a formattare lo stesso numero di millisecondi come prima.

## Formule delle pause

Per un'attività Studio con `s` sessioni, concentrazione `c` e pausa `p`, la durata è `s*c + max(0,s-1)*p`. Sommando le attività si aggiunge la pausa comune solo `n-1` volte, con `n` attività. La preparazione 20 secondi resta fuori da questa durata e viene mostrata separatamente.

Esempio Studio: attività 1 con concentrazione 1, sessioni 2 e pausa 2; attività 2 con concentrazione 3 e una sessione; pausa tra attività 4. La sequenza in minuti è `1 → 2 → 1 → 4 → 3`: totale 11:00, lavoro effettivo previsto 5:00. Non si aggiunge la pausa della seconda attività o un recupero finale.

Workout espande nell'ordine blocco → ciclo → esercizio → ripetizione. Il recupero tra ripetizioni è inserito solo prima di un'altra ripetizione dello stesso esercizio; quello comune del blocco solo prima dell'esercizio seguente; quello tra cicli solo prima di un altro ciclo; quello tra blocchi solo prima di un altro blocco. Il defaticamento opzionale è un'ultima fase propria. Il vecchio campo riscaldamento resta leggibile nel contratto, senza reintrodurre il pulsante rimosso.

Il caso rapido 20/10 per 8 giri contiene 8 lavori e **7** recuperi: 230 secondi, cioè 3:50, più 0:20 di preparazione. La fixture storica chiamata `riferimento-220` è un altro programma e verifica 220.000 millisecondi, senza preparazione.

## Cosa registrano i contatori

| Campo sessione | Significato |
| --- | --- |
| `plannedMs` | Somma delle fasi previste, senza preparazione |
| `elapsedMs` | Tempo attivo consumato nelle fasi pianificate, senza preparazione e pause manuali |
| `workMs` | Quota di tempo consumata in WORK |
| `completedWorkIntervals` | Numero di intervalli WORK unici terminati, senza duplicati dopo Precedente |
| `completedPhases` | Numero di fasi pianificate uniche terminate |
| `pauses` | Conteggio delle pause manuali |
| `status` | COMPLETED oppure INTERRUPTED secondo il percorso di conclusione |

Salta non accredita lavoro non svolto; Termina registra un'interruzione. Ripetere con Precedente può aumentare il tempo reale svolto senza duplicare un intervallo completato. Le statistiche leggono queste registrazioni e applicano periodo/modalità; non ricostruiscono una vecchia esecuzione usando il programma oggi modificato. Il raggruppamento dei giorni usa le regole UTC espresse nei moduli statistiche e nei test.

## Archivi precedenti

La sequenza è `valida originale → normalizza una copia → valida copia → compila`. Aprire raccolta, editor o anteprima non sovrascrive il programma originale. Eseguire o duplicare non applica una migrazione distruttiva globale. Solo il salvataggio esplicito della modifica aggiorna quel programma.

| Campo/struttura precedente | Trattamento attuale |
| --- | --- |
| Studio con blocchi/esercizi | Esercizi trasformati in attività nello stesso ordine, ripetuti per i cicli del blocco |
| Titoli, lavoro e ripetizioni Studio | Trasferiti rispettivamente a nome, concentrazione e sessioni |
| Recupero interno esercizio Studio | Pausa tra sessioni dell'attività |
| Pause ai confini Studio | Primo valore che separava realmente due attività usato come pausa comune; in assenza di confini, default 5 minuti |
| Pausa lunga, frequenza, preparazione personalizzata e pausa finale Studio | Escluse dalla nuova copia eseguibile; la preparazione fissa resta 20 secondi |
| Recuperi per esercizio Workout senza campo comune | Valore del primo esercizio scelto per il blocco; avviso quando i valori precedenti erano diversi |
| `restAfterExerciseSec` precedente | Rimane leggibile/esportabile; l'esecuzione normalizzata usa il recupero comune del blocco |
| Sessioni storiche | Conservano tempi e risultato registrati, senza ricalcolo con le nuove regole |

Gli avvisi spiegano che cosa cambia nella nuova esecuzione. L'esportazione prima della modifica permette di conservare anche una copia indipendente del vecchio formato. Un vecchio APK precedente al supporto Studio piatto non è un destinatario compatibile dei nuovi programmi; aggiornare entrambe le piattaforme prima dello scambio.

## Salvare e ritrovare

Workout: Salva allenamento → nome → Salva sul dispositivo → raccolta Allenamenti. Studio: Salva programma di studio → nome → Salva sul dispositivo → Programmi di studio. Il nome dell'attività interna resta distinto dal nome dell'intero programma.

Sul web l'archivio persistente è `localStorage` della stessa origine, browser e profilo. `localhost`, `127.0.0.1`, vecchio Sites e Cloudflare sono archivi separati. Cancellare i dati del browser o usare navigazione privata può rimuoverli. Android usa Room nella sandbox dell'app; disinstallazione o cancellazione dati rimuovono quell'archivio. Non si tratta di una copia in un account online.

## Esportazione e importazione

Esporta copia JSON crea un file trasferibile: salvarlo nei propri documenti, inviarlo manualmente all'altro dispositivo e aprire Importa copia JSON. L'importazione controlla formato, versione, dimensione, limiti, ID, date e contatori; mostra un'anteprima e sostituisce l'archivio solo dopo conferma. Prima di sostituire un archivio di destinazione da conservare, esportarlo. Non è una fusione automatica.

Su Android la sostituzione è transazionale. Sul web i dati validi rimangono in memoria anche se una successiva scrittura fallisce; il messaggio invita a esportare prima di chiudere. Una copia corrotta letta all'avvio non viene cancellata silenziosamente. Il CSV serve alla lettura in un foglio di calcolo e non sostituisce il backup JSON importabile.

## Recupero Android

Il checkpoint contiene una fotografia della sessione, non il ripristino completo del timer da un punto esatto. Dopo chiusura del processo, `initialize()` lo registra una sola volta come INTERRUPTED e lo cancella in transazione. Può mancare l'ultimo intervallo tra checkpoint, normalmente circa un secondo, o più in caso di errore di scrittura. Il web non implementa questo checkpoint e non riprende una pagina chiusa durante il timer.
