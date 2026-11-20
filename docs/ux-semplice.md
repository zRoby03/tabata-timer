# Interfaccia semplificata 1.8.0

Prima revisione del 17 novembre 2026, aggiornata il 20 novembre alla versione 1.8.0: percorso iniziale ispirato ai timer da allenamento dell'App Store, mantenendo le funzionalità previste dal progetto.

Riferimenti consultati: [SmartWOD Timer](https://apps.apple.com/us/app/smartwod-timer-wod-clock/id1248966041) per scelta rapida e numeri leggibili; [Interval Timer](https://apps.apple.com/us/app/interval-timer/id1610398390) per il percorso lavoro/recupero/giri. Le schede sono riferimenti di interazione, non asset da incorporare o servizi da collegare. Nessuna app è stata installata o acquistata e nessuna dipendenza è stata aggiunta al web.

## Percorso principale

1. Si apre il timer nell’ultima modalità usata. Lo switch in alto a sinistra, accanto a logo e nome Tabata Timer, sceglie Workout o Studio.
2. In Workout scegli Tabata 1 (20/10), Tabata 2 (30/15) o Tabata 3 (40/20), oppure cambia Lavoro, Recupero e Giri.
3. La durata totale si aggiorna dal compilatore esistente. Premi Avvia.
4. Durante l'allenamento restano in primo piano il tempo, Pausa/Riprendi e Salta. Termina richiede sempre conferma.

Altre opzioni contiene serie, recupero tra serie, defaticamento. Il controllo Riscaldamento è stato rimosso. **Salva allenamento** è subito sotto Avvia: apre una finestra per scegliere il nome e confermare **Salva sul dispositivo**. La conferma attiva la persistenza web; Annulla non crea dati. Altri controlli del timer contiene precedente, suoni, vibrazione e schermo intero/acceso. I limiti del background rimangono segnalati vicino al timer.

## Navigazione

| Sezione | Contenuto |
| --- | --- |
| Timer | Impostazione rapida o ritorno alla sessione ancora attiva |
| Allenamenti | Elenco compatto con Avvia; menu ⋯ per modifica, duplicazione, eliminazione; accesso all'editor completo |
| Attività | Cronologia e accesso a Statistiche |
| Impostazioni ⚙ | Salvataggio, JSON/CSV, importazione e gestione dati, in gruppi separati |
| Guida | Accesso dall’intestazione, tre riquadri: Workout e Studio, Per conservare un allenamento, Tema e Colori |

Nel web la barra è in basso su mobile. Nessun pannello editor viene mostrato al primo avvio. I dati esistenti e il JSON v1 restano compatibili. L'avvio rapido registra la sessione; Salva allenamento crea separatamente un elemento riutilizzabile nella libreria. Le impostazioni rapide restano nella pagina/ViewModel attuale: per conservarle tra riavvii occorre salvare l'allenamento.

Il timer rapido usa ID `quick-timer` anche per il confronto delle sessioni tra web e Android. Giri corrisponde alle ripetizioni dell'esercizio; Serie ai cicli del blocco. Un preset modifica lavoro/recupero e nome, conservando gli altri valori scelti. Otto lavori da 20 s con sette recuperi da 10 s durano 3:50, coerentemente con la regola che esclude il recupero finale. Il controllo 2 serie × 3 giri, 20/10 s e 60 s tra serie rimane 3:40.

## Scelte visive nella versione 1.3

Colori perla, bianco e lilla; caratteri di sistema senza download; cifre grandi e pulsanti facilmente raggiungibili. Il timer rapido resta una sola scheda centrale. Attività usa schede con tre dati principali e dettagli richiudibili; Impostazioni separa salvataggio, copie di sicurezza e cancellazione. Sul web mobile la navigazione è fissa in basso. Le finestre di salvataggio spiegano dove rimangono i dati. Su Android il colore nell’editor si sceglie da pulsanti con nomi leggibili, senza scrivere codici.

La revisione dei testi uniforma Avvia, Allenamento, Attività e Copia JSON, con date e numeri nel formato italiano. La guida completa è in [guida-utente.md](guida-utente.md) e nell’app.

## Verifica

Comandi invariati: `node --test web/tests/*.test.js`, `node scripts/browser-check.mjs`, `node scripts/browser-edge-cases.mjs`; in Android `./gradlew.bat testDebugUnitTest assembleDebug lintDebug connectedDebugAndroidTest`. Il percorso rapido ha test aggiuntivi sui totali, sui valori invalidi e sull'avvio senza editor; i test UI Android verificano anche l'accesso al menu degli allenamenti e all'editor avanzato. Risultati aggiornati in `docs/testing.md`.


Studio conserva la stessa disposizione e usa Concentrazione/Pausa in minuti, Sessioni e preset 25/5, 50/10 e 45/15. Preparazione iniziale di 20 secondi, 3-2-1 e fischio sono presenti anche qui. Le attività sono piatte e contengono solo nome, concentrazione, sessioni e pausa. Una sola Pausa tra attività vale per il programma; nessuna pausa dopo l’ultima sessione o attività. Blocchi, serie, pausa lunga e finale rimossi. Salva programma di studio chiede il nome del programma; Programmi di studio, Attività e Statistiche sono filtrati per Studio. Dati precedenti in Workout; aspetto comune. Il cambio modalità protegge le modifiche editor ed è disabilitato durante un timer. Dettagli in [workout-studio.md](workout-studio.md).

Da Android 1.6 il timer continua anche fuori dall’app e a schermo spento. La notifica offre Pausa/Riprendi, Salta e Termina senza aggiungere controlli alla schermata principale. I segnali si mescolano alla musica e seguono il volume multimediale. Il web conserva i limiti del browser.

Dalla 1.7.0 Workout ha un solo Recupero tra esercizi per blocco, esterno alle schede esercizio. Le abbreviazioni delle unità sono rimosse dai campi, dai preset e dalle anteprime; la Guida specifica secondi per Workout e minuti per Studio. La preparazione si mostra come + 0:20. I colori comprendono nove palette ordinate e i temi Chiaro/Scuro; Verde scuro e Rosa sono presenti su entrambe le piattaforme. La lettura degli archivi precedenti usa un adattatore puro e un avviso, senza sovrascrivere il programma finché l’utente non salva.

Dalla 1.8.0 sono rimossi pulsanti demo, riga sul salvataggio sotto al timer e footer web, secondo gli screenshot dell’utente. La scelta Italiano / English / Español è nelle Impostazioni; inizialmente segue la lingua del dispositivo. Il layout rimane lo stesso e il nome Tabata Timer non cambia. L’archivio personale resta nella forma originale.
