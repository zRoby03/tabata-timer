# Tabata Timer — Guida all’uso

Revisione locale del 24 novembre 2026 · Web e Android.

Il nome generale è **Tabata Timer**. Lo switch accanto al logo sceglie **Workout** (secondi) o **Studio** (minuti) e ricorda l’ultima modalità. Allenamenti, programmi di studio, attività e statistiche sono separati; i dati precedenti restano in Workout. Tema e palette sono comuni.

In Studio aggiungi attività con Nome attività, Concentrazione, Sessioni e Pausa. Fuori dalle schede scegli una sola Pausa tra attività. Le pause valgono solo tra sessioni o attività consecutive, mai dopo l’ultima. Nessun blocco, serie, pausa lunga o finale. I 20 secondi di preparazione includono 3-2-1 e fischio. Salva programma di studio → nome del programma → Salva sul dispositivo lo conserva in Programmi di studio. [Esempi e compatibilità](workout-studio.md).

L’app funziona in locale, senza account, pubblicità, abbonamenti o sincronizzazione automatica. Le tre sezioni principali sono **Timer**, **Allenamenti** e **Attività**. Il pulsante **Guida** è sempre nell’intestazione; l’ingranaggio apre **Impostazioni**.

## Inizia in pochi tocchi

1. Apri **Timer**.
2. Scegli un’impostazione pronta oppure cambia **Lavoro**, **Recupero** e **Giri**.
3. Premi **Avvia**: hai **20 secondi per prepararti**, poi comincia l’allenamento.
4. Il suono scandisce gli ultimi **3, 2, 1 secondi di ogni fase**: un bip breve e immediato a ogni numero, alto nel lavoro e basso nel recupero.

Non c’è un comando Riscaldamento. La preparazione iniziale è automatica e separata dalla durata mostrata per l’allenamento. Per esempio, un allenamento indicato come 3:40 richiede 4:00 complessivi con la preparazione. I dati precedenti che contengono già un riscaldamento restano compatibili.

## Scegliere la lingua

Al primo avvio l’app sceglie italiano, inglese o spagnolo dalla lingua del dispositivo. In **Impostazioni → Lingua** puoi scegliere **Italiano**, **English** o **Español**. La scelta viene ricordata sul dispositivo anche dopo la chiusura e si applica a Workout e Studio. Se nessuna lingua del dispositivo è supportata viene usato l’italiano.

Nomi e descrizioni già salvati rimangono come li hai scritti. Cambiare lingua non modifica programmi, attività, statistiche, durate o timer in corso. La lingua, come tema e palette, è una preferenza locale e non è compresa nel backup JSON.

## Scegliere tema e palette

Apri **Impostazioni** con l’ingranaggio, poi **Aspetto**:

1. In **Tema** premi **Chiaro** oppure **Scuro**.
2. In **Palette colori** scegli **Viola**, **Blu**, **Verde chiaro**, **Verde scuro**, **Azzurro**, **Arancione**, **Giallo**, **Rosso** o **Rosa**.
3. L’interfaccia cambia subito. La scelta resta salvata sul dispositivo anche dopo la riapertura.

Le preferenze di aspetto sono separate dagli allenamenti: sul web vengono conservate anche quando gli allenamenti sono in modalità temporanea. Non attivano il salvataggio degli allenamenti e non sono incluse nei file JSON. Importare o cancellare l’archivio degli allenamenti conserva il tema scelto. Su giallo e altri colori luminosi il testo dei pulsanti diventa scuro per rimanere leggibile.

## Come salvare più allenamenti in locale

### Dal timer rapido — web e Android

1. Imposta i tempi desiderati in **Timer**, comprese eventuali **Altre opzioni**.
2. Premi **Salva allenamento**, subito sotto Avvia.
3. Scrivi un nome riconoscibile, per esempio “Gambe · 8 giri” oppure “Circuito del lunedì”.
4. Premi **Salva sul dispositivo**. L’app apre **Allenamenti**, dove compare il nome scelto.
5. Torna in **Timer**, cambia i valori e ripeti per creare un altro allenamento.
6. Per riutilizzarlo, apri **Allenamenti** e premi **Avvia** sulla sua scheda.

**Annulla** chiude la finestra senza creare un allenamento. Salvare nuovamente dal Timer crea un nuovo elemento; per aggiornare quello esistente usa **⋯ → Modifica** sulla sua scheda e poi **Salva sul dispositivo**. I nomi possono coincidere: scegli nomi diversi per distinguerli.

### Allenamenti personalizzati

Apri **Allenamenti → Crea allenamento**, inserisci il nome, configura blocchi ed esercizi e controlla l’anteprima. Premi **Salva sul dispositivo**. L’editor non salva automaticamente mentre scrivi: se esci con modifiche non salvate, chiede conferma.

### Dove rimangono i dati

| Versione | Come vengono conservati | Come ritrovarli |
| --- | --- | --- |
| Web, prima del primo salvataggio | Solo nella memoria temporanea della pagina | Restano fino a chiusura o ricaricamento; non sono ancora conservati tra visite |
| Web, dopo **Salva sul dispositivo** | Nell’archivio locale del browser; si attiva anche il salvataggio delle attività | Usa lo stesso browser, profilo e indirizzo, per esempio sempre `http://127.0.0.1:5500` |
| Android | Nell’archivio privato dell’app sul telefono; le attività vengono salvate automaticamente | Riapri la stessa app e vai in Allenamenti o Attività |
| File JSON esportato | Nella cartella scelta da te o nei download del browser | Apri i file del dispositivo; per ricaricare i dati usa Importa copia JSON nell’app |

Sul web **Attiva il salvataggio** nel Timer, oppure **Impostazioni → Salva cronologia**, conserva anche le attività già svolte durante la visita. **Salva sul dispositivo** fa già questa scelta: non occorre premere un secondo pulsante.

Il salvataggio del browser non crea un file visibile in Documenti. `localhost` e `127.0.0.1`, porte, browser e profili diversi hanno archivi distinti. La navigazione privata o la pulizia dei dati del browser possono cancellare l’archivio. Su Android, disinstallare l’app o cancellarne i dati elimina l’archivio. Per conservare una copia indipendente, esporta il JSON.

Se il web segnala memoria piena o salvataggio non riuscito, la finestra resta aperta e non mostra un falso successo. Puoi riprovare senza creare duplicati, oppure chiuderla ed esportare subito una copia JSON dei dati ancora in memoria. Il precedente archivio salvato resta intatto.

## Tutte le funzioni

| Funzione | Come si usa e che cosa fa |
| --- | --- |
| **Impostazioni rapide** | Tabata 1: 20 s lavoro / 10 s recupero; Tabata 2: 30/15; Tabata 3: 40/20. Cambiano i tempi del timer rapido, che puoi poi modificare. |
| **Lavoro** | Secondi attivi di ogni giro; deve essere almeno 1. |
| **Recupero** | Pausa tra i giri. Può essere 0; non viene aggiunta dopo l’ultimo giro. |
| **Giri** | Numero di intervalli di lavoro in una serie. |
| **Altre opzioni** | Apre Serie, Recupero tra serie, Defaticamento e accesso all’editor personalizzato. |
| **Serie** | Ripete l’intero gruppo di giri. Il recupero tra serie si applica solo tra due serie. |
| **Defaticamento** | Fase facoltativa alla fine dell’allenamento, con durata in secondi. |
| **Durata allenamento** | Si aggiorna quando cambi i valori. Comprende lavoro, recuperi previsti e defaticamento. I 20 s iniziali sono indicati a parte. I valori non validi bloccano Avvia e Salva. |
| **Preparazione** | Parte a ogni Avvia, anche dalla libreria. Si può mettere in pausa o saltare. Non aumenta lavoro, durata attiva o percentuali nelle statistiche. |
| **Pausa / Riprendi** | Ferma e riprende il conto alla rovescia. Il tempo in pausa viene escluso dal tempo attivo. |
| **Salta** | Passa alla fase successiva. Una fase saltata non risulta completata. |
| **Precedente** | In Altri controlli, riavvia la fase precedente. Il lavoro realmente ripetuto resta conteggiato come tempo svolto, senza duplicare gli intervalli completati. |
| **Termina** | Chiede conferma e registra l’attività come interrotta. Alla conclusione naturale viene registrata come completata. |
| **Tema e palette** | Impostazioni → Aspetto: tema Chiaro/Scuro e nove colori, con applicazione immediata e salvataggio locale. |
| **Conto alla rovescia** | Una nota breve sul 3, sul 2 e sull’1, basse nel lavoro e alte in preparazione/recupero. Nessun bip nel defaticamento Workout. Pausa, salto e mute interrompono subito il suono. Nessuna raffica arretrata dopo un ritardo. |
| **Inizio lavoro e recupero** | Workout: un ding ordinario; due ding al primo lavoro del nuovo ciclo/blocco, in sostituzione del singolo. Un sospiro nel blocco, due al cambio blocco. Studio conserva fischio e sospiro da 500 ms. [Dettagli](audio-countdown.md). |
| **Fine allenamento** | Workout: tre ding al termine naturale dell’ultimo lavoro e di nuovo alla fine del defaticamento, se attivo. Nessun altro segnale o bip nel defaticamento. Senza defaticamento un solo finale. Termina e salto della fase finale non producono un falso finale. Studio mantiene la campanella. |
| **Altri controlli** | Include Precedente, Suoni, Vibrazione, Schermo intero e Schermo acceso. Audio e vibrazione dipendono dal dispositivo e dai permessi disponibili. |
| **Allenamenti** | Mostra gli elementi salvati con durata e numero di intervalli. **Avvia** li esegue; **⋯** apre Modifica, Duplica ed Elimina. L’eliminazione richiede conferma. |
| **Allenamenti pronti** | Aggiunge alla raccolta modelli da usare o modificare. Sul web temporaneo, attiva il salvataggio per conservarli tra visite. |
| **Editor** | Workout: nome, descrizione, colore, defaticamento, blocchi/cicli ed esercizi con lavoro, ripetizioni e recupero personale. Recupero tra blocchi solo se segue un altro blocco. Studio: attività piatte e pausa tra attività. Duplicazione, riordino ed eliminazione con conferma. |
| **Anteprima** | Workout raggruppato per blocco e ciclo, recuperi con il nome dell’esercizio, cambio blocco senza doppia pausa, segnali come annotazioni. Studio: pausa con nome dell’attività e tipo di passaggio. Prime 150 fasi visibili; il timer le esegue tutte. |
| **Attività** | Mostra sessioni complete e interrotte, data, tempo attivo, lavoro svolto e percentuale di completamento. **Dettagli** aggiunge durata prevista, intervalli, fasi, pause ed eliminazione della singola attività. |
| **Statistiche** | Si apre da Attività. Filtra ultimi 7, 30 o 90 giorni e uno specifico allenamento. Mostra sessioni per settimana, minuti di lavoro e completamento medio. Grafici e tabelle riportano gli stessi valori. |
| **Confronto** | Le sessioni dello stesso allenamento si possono confrontare nel tempo. La percentuale misura gli intervalli completati, non la prestazione fisica. Le copie duplicate sono allenamenti distinti. |
| **Esporta copia JSON** | Crea una copia completa di allenamenti e attività, trasferibile tra web e Android. |
| **Importa copia JSON** | Legge e controlla il file, mostra un’anteprima e sostituisce l’archivio solo dopo **Sostituisci dati**. Annulla lascia tutto invariato. |
| **Esporta attività CSV** | Crea una tabella delle sessioni per un foglio di calcolo. Non contiene gli allenamenti completi e non è utilizzabile per il ripristino nell’app. |
| **Modalità temporanea — web** | In Impostazioni rimuove il salvataggio persistente dopo conferma. I dati correnti restano nella pagina fino a chiusura/ricaricamento. Per conservarli prima di uscire, attiva nuovamente il salvataggio o esporta JSON. |
| **Cancella tutti i dati** | In Impostazioni elimina l’archivio dopo conferma. Non cancella le copie JSON già esportate nei tuoi file. |
| **Guida** | Si apre dall’intestazione oppure da Impostazioni → Apri la guida; contiene istruzioni anche senza connessione. |

## Come creare un file locale di sicurezza

1. Apri **Impostazioni** con l’ingranaggio.
2. Premi **Esporta copia JSON**.
3. Sul web il browser scarica il file nella cartella dei download oppure chiede dove salvarlo, secondo le sue impostazioni. Su Android scegli una cartella locale, per esempio Download, e conferma nel selettore file.
4. Conserva il file con una data riconoscibile, per esempio `tabata-2026-11-18.json`.
5. Ripeti l’esportazione quando vuoi aggiornare la copia: il file esportato non si aggiorna automaticamente.

Per una copia solo locale, scegli una cartella del computer o del telefono. Non è richiesto alcun servizio cloud. Il file contiene l’intero archivio, quindi può includere più allenamenti e le rispettive attività.

### Ripristinare o trasferire il JSON

1. Se vuoi conservare i dati già presenti nella destinazione, esportali prima in un altro file.
2. Apri **Impostazioni → Importa copia JSON** e scegli il file.
3. Controlla il numero di allenamenti e sessioni nell’anteprima.
4. Premi **Sostituisci dati**. Questa operazione sostituisce l’archivio; non unisce due raccolte.
5. Sul web, se sei ancora in modalità temporanea, premi **Salva cronologia** per conservare l’importazione tra visite. Se il salvataggio è già attivo, la sostituzione viene salvata. Android salva automaticamente l’archivio importato.

Non ci sono sincronizzazione automatica o collegamenti tra dispositivi: puoi trasferire manualmente il file con il metodo che preferisci.

## Uso offline e comportamento alla chiusura

- **Web:** dopo il primo caricamento completo vengono conservati i file dell’app per l’uso offline. Gli allenamenti sono conservati separatamente solo quando attivi il salvataggio. Mantieni la pagina visibile: il sistema può sospendere il browser e impedire i suoni. Ricaricare o chiudere durante una sessione non la riprende; per registrarla premi prima Termina.
- **Android:** l’app funziona senza Internet. Il timer continua quando passi a un’altra app o spegni lo schermo, anche mentre ascolti musica o video. La notifica offre Pausa, Riprendi, Salta e Termina; al primo timer consenti le notifiche. I segnali chiedono una breve attenuazione MAY_DUCK; il comportamento dipende dal lettore, soprattutto per il parlato. Il volume globale non viene modificato. [Guida del background](android-background.md). Ruotare lo schermo conserva la sessione. Se il sistema termina il processo, al successivo avvio la sessione viene registrata una sola volta come interrotta fino all’ultimo punto salvato; può mancare circa l’ultimo secondo, o più in caso di errore di memoria.

Per avvio web, installazione Android e comandi di verifica, vedi [Installazione Windows](setup-windows.md). Nessun nuovo servizio o dipendenza è necessario. Questa revisione è locale e non cambia il sito già attivo.

## Modello semplice aggiornato al 24 novembre 2026

Studio usa attività piatte; Workout conserva blocchi e cicli. Ogni esercizio ha il proprio Recupero; tra blocchi il recupero del blocco sostituisce quello dell’ultimo esercizio. Non c’è un recupero autonomo tra cicli. L’anteprima e l’esecuzione usano gli stessi valori. La Guida integrata ha soltanto Workout e Studio, Per conservare un allenamento, Tema e Colori. Le nove palette hanno questo ordine: Viola, Blu, Verde chiaro, Verde scuro, Azzurro, Arancione, Giallo, Rosso, Rosa. Temi Chiaro/Scuro e preferenze restano locali.

Gli archivi precedenti restano disponibili: quando serve, un avviso spiega la conversione dei campi rimossi. Aprire o usare un programma non lo sovrascrive; salvarne una modifica adotta la nuova struttura. La cronologia esistente non cambia. [Regole di conversione](workout-studio.md#archivi-precedenti).

## Interfaccia essenziale della versione 1.8.0

I pulsanti demo sono stati rimossi in entrambe le modalità. Sono stati tolti anche il testo sul salvataggio sotto al timer e il piè di pagina web. Per conservare un programma continua a usare Salva allenamento / Salva programma di studio → nome → Salva sul dispositivo. Le impostazioni conservano le informazioni su salvataggio, copie JSON e modalità temporanea.

Minimi Workout: Lavoro 5, Recupero 0 oppure almeno 5, recupero effettivo tra blocchi almeno 5, Defaticamento 0 oppure almeno 10. I valori 6/7 sono validi. I vecchi programmi mostrano un avviso e una copia adattata; l’originale rimane esportabile fino al salvataggio esplicito. Studio mantiene i tempi in minuti.
