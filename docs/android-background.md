# Android: background e priorità dei segnali

Workout e Studio proseguono passando a un’altra app e a schermo spento. Il foreground service mantiene il timer, il wake lock parziale il conteggio; la pausa rilascia il wake lock. Il clock usa elapsedRealtime e un solo runtime scrive checkpoint e risultato. Rotazione e ricreazione dell’Activity non creano un secondo timer.

## Uso

Installa la build di verifica sopra la precedente senza disinstallare, se la firma coincide. Esporta prima un JSON per sicurezza. Su Android 13+ consenti le notifiche per avere Pausa, Riprendi, Salta e Termina fuori dall’app; il rifiuto può nascondere i controlli, senza arrestare il servizio. Le azioni sono legate all’ID della sessione e non controllano sessioni successive. La notifica è silenziosa.

Alla fine naturale i tre ding possono terminare prima della rimozione del servizio. La grazia finale segue il campione effettivo da 1,92 secondi, senza aggiungerlo a durata o statistiche. Arresto manuale e distruzione rilasciano tutte le risorse. Dopo morte del processo, il checkpoint viene registrato una sola volta come attività interrotta; non viene riavviato automaticamente un timer.

## Musica e video

SoundPool e AudioFocusRequest usano gli stessi AudioAttributes: USAGE_MEDIA e CONTENT_TYPE_SONIFICATION. HornSignals centralizza AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK. Dal primo bip tiene una finestra fino al termine del segnale successivo, senza nuove richieste per ogni nota; un segnale isolato tiene il focus soltanto per la propria durata. Nel defaticamento il focus è libero dopo il finale del lavoro, fino al finale del defaticamento.

Una lease rinnovabile con clock monotono evita che una scadenza precedente rilasci una finestra estesa. Pausa, mute, salto, precedente, termine e distruzione cancellano playback e callback pendenti. Una transizione automatica arresta solo la vecchia voce, conservando la finestra comune del 3-2-1. Una sola voce alla volta.

Se la richiesta viene negata o il focus perso, il campione viene omesso/interrotto; nessuna riproduzione tardiva, timer e vibrazione indipendente continuano. Si riprova al prossimo segnale utile. Suoni disattivati non chiedono focus. Non si modificano volume globale, app musicali o loro account.

Android può attenuare un lettore compatibile e ripristinarlo al rilascio. Parlato, podcast e lettori che preferiscono la pausa possono comportarsi diversamente: non è un controllo universale di Spotify/YouTube. Da target API 35 la richiesta richiede app in primo piano o foreground service: il progetto target 36/min 26 rispetta il requisito usando il servizio già presente. [Documentazione ufficiale sul focus](https://developer.android.com/media/optimize/audio-focus).

Il servizio conserva tipo specialUse e motivazione nel manifest, senza privilegi o dipendenze nuovi. Permessi esistenti: FOREGROUND_SERVICE, FOREGROUND_SERVICE_SPECIAL_USE, WAKE_LOCK e POST_NOTIFICATIONS; nessun Internet, microfono o lettura musicale. Le API Spotify/YouTube non sono integrate. Le condizioni di riproduzione rimangono quelle dei rispettivi lettori.

## Prove e limiti

Le prove sull’emulatore Medium_Phone API 37 verificano notifica, schermo spento, controlli, pause, wake lock, ricreazione, sessioni consecutive, 13 campioni SoundPool e rilascio del focus. Un lettore nel diverso UID dell’APK di test continua a riprodurre un WAV locale e non riceve perdite permanenti/transitorie; può ricevere MAY_DUCK. Non è una prova fisica con Spotify/YouTube. I test JVM controllano separatamente richieste negate, estensione e rilascio della lease. [Report aggiornato](testing.md).

Restano da verificare telefoni reali, cuffie Bluetooth, musica/video/podcast, Non disturbare e politiche energetiche dei produttori. Arresto forzato, spegnimento e chiusura dal gestore Android interrompono il servizio. Il web non dispone di questo servizio e non promette la stessa priorità sonora.
