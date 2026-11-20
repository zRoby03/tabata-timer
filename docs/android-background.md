# Android: background, schermo spento e musica — versione 1.7.0

Workout e Studio continuano quando passi a un’altra app, chiudi l’interfaccia o spegni lo schermo. Un servizio in primo piano mantiene il timer; un blocco parziale della CPU consente di scandire fasi e ultimi tre secondi a schermo spento. Lo schermo può restare spento. Il blocco della CPU viene rilasciato in pausa, al termine e quando il servizio viene distrutto.

## Utilizzo

1. Installa l’APK 1.7.0 sopra la versione precedente, senza disinstallare: conserva i dati locali.
2. Avvia un timer in Workout o Studio. Su Android 13 e successivi, al primo avvio consenti le notifiche per vedere i controlli fuori dall’app.
3. Apri YouTube, Spotify o un altro lettore, oppure spegni lo schermo. Il timer continua. Non servono accessi a questi servizi nell’app Tabata.
4. La notifica mostra fase e tempo rimanente. **Pausa**, **Riprendi**, **Salta** e **Termina** funzionano anche senza riaprire l’interfaccia. Tocca il corpo della notifica per tornare al timer.
5. **Termina** dalla notifica registra subito la sessione interrotta. Al completamento naturale la sessione viene salvata una sola volta, i tre ding possono concludersi e la notifica viene rimossa.

Se rifiuti le notifiche, il servizio continua ma Android può nascondere i controlli dal pannello notifiche. Puoi riabilitarli in **Impostazioni → Timer in background → Notifiche Android**. Il rifiuto non viene seguito da richieste ripetute a ogni timer. [Comportamento documentato da Android](https://developer.android.com/develop/ui/compose/notifications/notification-permission).

## Audio insieme a musica e video

I nove campioni e i volumi restano quelli concordati: bip da 120 ms, bassi in lavoro/concentrazione e alti in preparazione/pause; fischio da 500 ms al 120% equivalente e sospiro da 500 ms invariato all’85%; tre ding finali.

SoundPool riproduce brevi effetti sul flusso multimediale, anche nelle cuffie instradate da Android. L’app non richiede il focus audio: non invia comandi di pausa o riduzione del volume al lettore. La notifica è silenziosa e non aggiunge suoni di sistema a ogni aggiornamento. Il volume multimediale del dispositivo regola anche i segnali, disattivabili con Suoni. Android può [mescolare l’audio di più app](https://developer.android.com/media/optimize/audio-focus).

Le condizioni di riproduzione di YouTube o Spotify restano quelle offerte dalle rispettive app: Tabata non accede agli account né modifica le limitazioni dei loro piani.

## Struttura tecnica

- `TabataApplication` condivide repository e runtime tra interfaccia e servizio. La chiusura o ricreazione dell’Activity non crea un secondo motore o un secondo checkpoint.
- `TimerRuntime` possiede motore, segnali e salvataggi; `TimerService` è l’unico componente che aggiorna il timer. Il clock usa `elapsedRealtime`, indipendente dall’orario di sistema.
- Il servizio viene avviato dall’azione dell’utente, con tipo `specialUse` e motivazione dichiarata nel manifest. Non usa il tipo a durata limitata `shortService` né finge una riproduzione multimediale continua. [Tipi dei servizi Android](https://developer.android.com/develop/background-work/services/fgs/service-types).
- La notifica offre azioni legate all’ID della sessione: comandi rimasti da una sessione precedente non controllano quella nuova. Il servizio non è esportato ad altre app.
- Il wake lock parziale, con timeout, viene acquisito solo durante il conteggio e la breve chiusura audio; la pausa lo rilascia. Non si richiedono esenzioni automatiche dal risparmio energetico. [Wake lock Android](https://developer.android.com/develop/background-work/background-tasks/awake/wakelock).
- Permessi aggiunti: `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_SPECIAL_USE`, `WAKE_LOCK`, `POST_NOTIFICATIONS`. Nessun permesso Internet, lettura della libreria musicale, microfono o accesso ai lettori esterni.
- Room e JSON rimangono v1. L’arresto forzato dell’app o del processo termina il servizio; al riavvio il checkpoint viene recuperato una sola volta come attività interrotta. Non si crea una nuova sessione automaticamente.

## Verifiche eseguite

Su Medium_Phone Android 17/API 37 sono passati tutti i 15 test strumentali: quattro nuovi casi coprono notifica, pausa/ripresa/salto/termine, wake lock rilasciato, Studio a schermo spento, distruzione e riapertura dell’interfaccia, timer consecutivi e audio simultaneo. Un lettore nel diverso UID dell’APK di test riproduce un WAV locale e mantiene il focus audio; AudioService conferma gli avvii dei segnali del timer. I 35 test JVM e i 72 Node sono passati; interoperabilità JSON legacy/Studio confermata. Build APK e Lint completati, 0 errori.

Il permesso notifiche viene concesso soltanto sull’emulatore di test. Il test media non richiede account, download o abbonamenti. Il suo servizio e il permesso mediaPlayback non sono nel manifest dell’app consegnata. Lo script `scripts/android-smoke.py`, su emulatore dedicato, verifica l’arresto reale del processo e il recupero del checkpoint senza duplicati; salva anche una schermata della notifica.

## Limiti delle verifiche

Il web conserva i limiti del browser e non acquisisce questo servizio Android. La resa audio su altoparlanti, cuffie e telefoni fisici va verificata sul dispositivo dell’utente; le politiche energetiche dei produttori possono limitare i servizi. Spegnere il telefono, usare Arresto forzato o fermare l’app dal gestore Android interrompe l’esecuzione. Non sono stati attivati servizi a pagamento o eseguiti deploy.
