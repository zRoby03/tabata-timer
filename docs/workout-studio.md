# Workout e Studio

Lo switch accanto a Tabata Timer sceglie e ricorda la modalità. Termina il timer prima di cambiarla. Tema, palette e lingua sono comuni; programmi, attività e statistiche sono separati. Il backup JSON comprende entrambe le modalità.

## Workout: un recupero personale

Lavoro e recuperi si configurano in secondi, senza abbreviazioni nei campi. Ogni esercizio ha Lavoro, Ripetizioni e un solo Recupero. Questo recupero si applica dopo il suo lavoro quando segue un’altra ripetizione, un altro esercizio o un nuovo ciclo dello stesso blocco. Mantieni cicli e blocchi; non c’è un recupero comune tra esercizi né un recupero autonomo tra cicli.

Alla fine dell’ultimo ciclo di un blocco, se segue un altro blocco, si usa esclusivamente Recupero tra blocchi del blocco appena concluso. Sostituisce il recupero dell’ultimo esercizio; non si somma. Ogni confine ha il suo valore. Il campo compare soltanto quando esiste un blocco successivo e conserva il valore nascosto dopo riordino, eliminazione, duplicazione o aggiunta. Dopo l’ultimo lavoro non c’è un recupero automatico.

Lavoro, recupero personale e recupero tra blocchi partono da 5 secondi. Valori interi come 6 e 7 sono validi. L’interruttore Defaticamento mostra il relativo campo quando è attivo. La sua durata parte da 10 secondi. Un valore fuori limite impedisce avvio e salvataggio.

Il rapido conserva Lavoro, Recupero, Giri e, in Altre opzioni, Serie e Defaticamento. Il recupero personale vale anche tra serie. Esempio 20/10 × 8 con due serie: 16 × 20 + 15 × 10 = 470 secondi, più 20 di preparazione. La precedente pausa tra serie da 60 portava il totale a 520.

Anteprima: Blocco → Ciclo → esercizi con nome e recupero corrispondente. Esempio: Squat → Recupero Squat → Push-up → Recupero Push-up → ciclo successivo; all’ultimo ciclo il confine mostra solo Recupero Blocco 1 → 2. I segnali sono annotazioni, non fasi. [Audio](audio-countdown.md).

## Studio: Pomodoro semplice

Lista piatta di attività numerate con Nome attività, Concentrazione, Sessioni, Pausa. Puoi aggiungere o eliminare attività, lasciandone almeno una. Il nome del programma viene scelto al salvataggio. Concentrazione, Pausa e Pausa tra attività accettano da 1 a 60 minuti interi. Le durate sono salvate in secondi.

La pausa personale vale tra sessioni della stessa attività. La Pausa tra attività vale tra attività consecutive. Ogni passaggio applica la sua pausa e la sequenza finisce al termine dell’ultima sessione.

Esempio Lettura: concentrazione 1, sessioni 2, pausa 2; Ripasso: concentrazione 3 e una sessione; pausa globale 4. Anteprima: Lettura 1 → Pausa Lettura — tra sessioni 2 → Lettura 1 → Pausa Lettura → Ripasso — tra attività 4 → Ripasso 3. Totale 11:00, lavoro 5:00. Sono cambiati soltanto i nomi delle pause, non tempi o suoni Studio.

Studio 1/2/3 impostano 25/5, 50/10, 45/15 sulla prima attività, conservando nomi, sessioni e altre attività. Entrambe le modalità iniziano con 20 secondi di preparazione, separati dalla durata pianificata e dalle statistiche.

## Widget Android e mini timer su PC

Su Android tieni premuta un’area libera della schermata Home, apri **Widget** e trascina **Tabata Timer** sulla Home. Il widget è progettato per occupare quattro colonne e due righe. Su PC usa **Apri mini timer** nella pagina Timer. La finestra compatta può restare aperta quando chiudi la pagina principale.

- **Workout / Studio** seleziona la modalità. I pulsanti accanto al tempo cambiano la durata di un secondo in Workout e di un minuto in Studio. I minimi sono rispettivamente 5 secondi e 1 minuto intero.
- **Focus** avvia lavoro e recupero con 20 secondi di preparazione. I pulsanti sotto Play impostano da 1 a 20 giri o sessioni. **Break** avvia una singola pausa con i segnali della modalità scelta. Le pause singole sono escluse dalla cronologia delle attività.
- Premi il pulsante circolare per avviare, mettere in pausa o riprendere. **Termina** compare durante la pausa. Il widget Android controlla la stessa sessione dell’app e della notifica. Su PC una sola finestra per profilo può eseguire il timer quando il browser supporta Web Locks. I browser possono sospendere i suoni nelle finestre in background.

## Salvataggio e trasferimento

Timer → Salva allenamento / Salva programma di studio → nome → Salva sul dispositivo. Ritrovi il programma nella raccolta della modalità corrente. Nell’editor usa il pulsante finale. Modifica, Duplica ed Elimina sono nel menu della raccolta; eliminare un programma conserva la cronologia.

Web: stesso browser, profilo e indirizzo; la conferma di salvataggio attiva la persistenza. Android: database locale. Impostazioni → Esporta copia JSON crea un file; Importa copia JSON mostra un’anteprima e sostituisce l’archivio solo dopo conferma. Esporta prima i dati della destinazione se vuoi conservarli. Nessuna sincronizzazione automatica. CSV: solo attività della modalità corrente.

## Archivi precedenti

Leggere, esportare, aprire, duplicare o eseguire non riscrive il programma originale. L’adattatore normalizza una copia. Solo Salva sul dispositivo adotta il nuovo programma; la cronologia registrata non viene ricalcolata.

Workout legacy: lavoro e recuperi positivi 1–4 diventano 5 nella copia; un confine effettivo tra blocchi 0–4 diventa 5; defaticamento positivo 1–9 diventa 10. Zero personale/finale rimane zero. Il recupero personale usa `restBetweenRepsSec`; `restBetweenCyclesSec`, `restBetweenExercisesSec` e `restAfterExerciseSec` restano leggibili/esportabili nell’originale ma non aggiungono pause. L’avviso spiega cambiamenti e nuova durata prima dell’avvio. I programmi nuovi/salvati hanno il marcatore interno opzionale `timingVersion: 2`, per validare gli input senza confonderli con il legacy. JSON e Room restano versione 1.

Studio legacy con blocchi: attività appiattite nell’ordine originale e per ogni ciclo. Nomi, concentrazione, sessioni e pause interne conservati. La pausa comune usa il primo vecchio valore che separava realmente due attività, altrimenti 5 minuti. Pause lunghe/finali, frequenza e preparazione personalizzata non governano la nuova esecuzione. Avviso visibile; originale preservato fino al salvataggio. Queste conversioni Studio sono preesistenti: i nuovi minimi Workout non si applicano a Studio.

Quando modifichi un programma con pause precedentemente omesse, l’editor propone 5 secondi in Workout o 1 minuto in Studio. L’avviso indica che i minimi si applicano alla copia in modifica. Salva il programma per adottarli.

Per scambiare programmi nuovi usa la stessa revisione aggiornata su entrambe le piattaforme: un vecchio motore può ignorare il marcatore opzionale ed eseguire regole precedenti. [Contratto](schema-v1.md) · [Variazioni misurate](recuperi-audio-2026-11-24.md).
