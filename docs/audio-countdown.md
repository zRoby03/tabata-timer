# Segnali sonori di Tabata Timer

Ogni avvio include 20 secondi di preparazione, separati dall’allenamento. Alla fine di ogni fase suonano **tre note singole in tre secondi complessivi**: una sul 3, una sul 2, una sull’1. Ogni nota è un bip di circa 120 millisecondi, con attacco immediato e silenzio fino al numero successivo. Allo zero, il segnale della nuova fase distingue lavoro, recupero e conclusione dell’allenamento.

Le stesse regole valgono in Studio: lavoro significa concentrazione, recupero significa pausa e non è prevista una pausa finale. Anche Studio inizia con 20 secondi di preparazione, tre bip alti e fischio al primo turno.

## Segnali di cambio turno

| Quando | Suono | Durata |
| --- | --- | --- |
| Inizia ogni intervallo di lavoro | Fischio da arbitro dal timbro morbido | 500 ms |
| Inizia un recupero tra ripetizioni, esercizi, cicli o blocchi Workout, oppure tra sessioni e attività Studio | Sospiro tratto dal riferimento dell’utente | 500 ms |
| Inizia il defaticamento, oppure finisce la sequenza senza defaticamento | Campanellina con tre ding | 900 ms totali, colpi a 0 / 300 / 600 ms |

La campanella suona una sola volta per allenamento: con defaticamento viene emessa al suo inizio, senza essere ripetuta alla sua fine. Termina manuale non produce la campanella. Pausa e ripresa non ripetono il segnale di inizio turno; tornare alla fase precedente è invece una nuova visita. Eventuali segnali di inizio turno già trascorsi da oltre 500 ms non vengono recuperati in ritardo.

Una sola voce suona alla volta. Se una fase dura tre secondi o meno, il segnale iniziale ha precedenza sul bip che cadrebbe nello stesso istante; i bip successivi restano regolari. I tre ding durano meno di un secondo e terminano prima del bip successivo del defaticamento. Tutti i segnali rispettano Suoni e Vibrazione.

Il fischio passa dall’85% al **120% equivalente**, sempre 500 ms. SoundPool Android accetta guadagni tra 0 e 1: il WAV del solo fischio è quindi preamplificato di 1,2 e riprodotto con guadagno 1, identico sul web. Il picco PCM passa da 11000 a 13200 su 32767, senza saturazione. Il vecchio picco effettivo era 11000 × 0,85 = 9350; il nuovo è 13200. Sospiro all’85%, bip e campanella al 65%, campioni e durate invariati. Nessuna richiesta di focus o pausa/attenuazione della musica. Il livello percepito dipende dal volume multimediale del dispositivo e dal timbro.

Il fischio di avvio ora usa una risonanza principale di circa 1760 Hz, una secondaria leggera a 1880 Hz e un trillo delicato, con attacco di 12 ms e uscita graduale di 55 ms. Il vecchio componente principale a 2700 Hz e la componente a 3400 Hz sono stati rimossi: il segnale è meno acuto e più rotondo, mantenendo 500 ms, livello e riconoscibilità dell’avvio. Gli altri otto WAV sono verificati identici byte per byte. L’anteprima del solo segnale è in `output/audio/Fischio.wav`.

Fischietto e campanella sono sintetizzati localmente con Python standard. Il sospiro usa un estratto di 500 ms del video [Sigh Sound Effect #soundeffects, Popcorn81 Extras](https://www.youtube.com/watch?v=1E-WbRI_R64), indicato dall’utente: da 1,421375 a 1,921375 s, convertito in mono, con brevi dissolvenze e normalizzazione del picco. Provenienza ed elaborazione sono documentate in [assets/audio/README.md](../assets/audio/README.md). I tre segnali sono PCM mono a 24000 Hz, identici tra web e Android. I nove campioni dell’app, compresi i bip, occupano 123348 byte per piattaforma, circa 121 KiB. L’app usa soltanto i file locali, senza contattare YouTube.

## Note alte e basse

| Fase | Sul 3 | Sul 2 | Sull’1 |
| --- | --- | --- | --- |
| Lavoro | 330 Hz, 120 ms | 370 Hz, 120 ms | 440 Hz, 120 ms |
| Preparazione, recuperi e defaticamento | 660 Hz, 120 ms | 740 Hz, 120 ms | 880 Hz, 120 ms |

I vecchi riscaldamenti eventualmente presenti negli archivi usano le note alte. Non c’è un controllo Riscaldamento nell’interfaccia.

Ogni file del conto alla rovescia contiene una sola nota breve, con attacco di 3 ms e chiusura morbida di 20 ms. La velocità di riproduzione è sempre 1, così la durata rimane breve anche per le note basse. I sei WAV sono PCM mono a 22050 Hz, 16 bit, identici tra web e Android; occupano complessivamente circa 32 kB per piattaforma. Per usare l’app o rigenerare i campioni non servono librerie aggiuntive o servizi online: l’estratto del sospiro è già incluso nel progetto.

Rigenerazione con Python standard:

```powershell
python scripts/generate-horn.py
```

Il comando genera anche due anteprime di esattamente tre secondi in `output/audio/countdown-work-short-3s.wav` e `output/audio/countdown-rest-short-3s.wav`, con le tre note effettivamente usate dal timer, collocate a 0, 1 e 2 secondi e intervallate dal silenzio. Le anteprime non vengono caricate dall’app.

## Comportamento

- `CountdownCues` emette una sola nota per secondo della visita corrente. Pausa e ripresa non ripetono la nota già suonata.
- Per fasi più brevi di tre secondi vengono suonate solo le note disponibili. Un ritardo non produce una raffica di note arretrate.
- Pausa, salto, precedente, termine e disattivazione dei suoni interrompono subito la nota. Il cambio automatico di fase interrompe quella precedente per evitare sovrapposizioni. Android continua i segnali tramite il servizio anche in background.
- Preparazione, lavoro svolto, durate e statistiche rimangono separati come prima. JSON e Room restano alla versione 1.
- Web Audio carica i nove file locali; il service worker li conserva per l’uso offline. SoundPool Android usa un solo flusso alla volta; i nove file sono disponibili anche offline.

## Verifica

I test controllano i PCM effettivi: un’unica nota per file, durata di 120 ms e attacco immediato, frequenza alta/bassa attesa, assenza di saturazione e identità dei dati tra le due piattaforme. I test di riproduzione controllano arresto e assenza di voci sovrapposte.

Il test browser offline riconosce la frequenza dell’audio realmente decodificato e registra istante di partenza, durata e guadagno di ogni sorgente: ogni bip dura 120 ms e le tre note partono a distanza di un secondo, non ci sono ripetizioni a metà secondo e i segnali di cambio fase sono riconosciuti e riprodotti una sola volta. Fischietto e sospiro durano 500 ms; il primo usa PCM × 1,2 e guadagno 1, il secondo guadagno 0,85; gli altri suoni usano 0,65. Verifica anche pausa, preparazione e conteggi delle sessioni. Il test SoundPool carica tutti i nove campioni e verifica la riproduzione sia delle note sia dei tre segnali.

```powershell
node --test web/tests/*.test.js
# Con Playwright configurato come in testing.md:
node scripts/browser-countdown.mjs
cd android
.\gradlew.bat testDebugUnitTest assembleDebug lintDebug
.\gradlew.bat connectedDebugAndroidTest
```

Report browser: `output/browser-countdown.json`. Rimangono da verificare timbro e volume sugli altoparlanti fisici. Browser sospesi e schermo bloccato possono impedire gli avvisi; Android continua in background e a schermo spento. SoundPool usa il flusso multimediale senza richiedere focus audio, così musica e video continuano.

I test dei cambi di fase controllano fischietto, sospiro, defaticamento facoltativo, completamento senza defaticamento, annullamento manuale, ritorno alla fase precedente, pause e ritardi. I test PCM verificano 500 ms per fischietto e sospiro, l’identità con l’estratto di riferimento e i tre colpi della campanella. Il browser verifica anche intervalli di un secondo, precedenza del fischietto e riproduzione completa della campanella a timer terminato.
