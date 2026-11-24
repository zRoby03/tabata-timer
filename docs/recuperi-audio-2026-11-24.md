# Aggiornamento locale dei recuperi e dell'audio

Base applicativa: main `82d3580`. Lavoro isolato sul branch locale `codex/workout-recuperi-audio`; nessun commit, push o deploy. Il branch didattico rimane intatto.

## Audit e variazioni attese delle fixture

Il codice precedente applicava il recupero comune tra esercizi e un recupero autonomo tra cicli. Ora ogni WORK usa il recupero personale dell'esercizio quando segue altro lavoro nello stesso blocco; tra blocchi il recupero del blocco lo sostituisce. Studio conserva le durate.

Queste variazioni sono calcolate prima di aggiornare i risultati attesi dei test:

| Esempio precedente | Prima | Nuova esecuzione | Motivo |
| --- | ---: | ---: | --- |
| Fixture `riferimento-220` | 220 s | 170 s | Sei lavori da 20 = 120; quattro recuperi interni da 10 più il recupero personale da 10 tra cicli = 50. Il vecchio recupero tra cicli da 60 viene sostituito da 10: −50. |
| Fixture `tutti-i-livelli` | 80 s | 187 s | Riscaldamento legacy 5; primo ciclo 125 (A: 5+5+5+5, B: 6+99); secondo ciclo e confine 39 (A: 20, B: 6, blocchi: 13); ultimo lavoro 8; defaticamento portato da 7 a 10. Totale 5+125+39+8+10. |
| Workout `backup-simple-v1` | 83 s | 85 s | Sei lavori da 10 = 60, cinque recuperi personali adattati da 3 a 5 = 25. I due vecchi recuperi comuni da 7 non vengono aggiunti. |
| Studio semplice | 660 s | 660 s | Cambiano solo i nomi delle pause. |
| Rapido 20/10 × 8, due serie | 520 s con pausa serie 60 | 470 s | Sedici lavori da 20 e quindici recuperi personali da 10. Preparazione separata +20. |

Le fixture di backup originali e le sessioni storiche restano inalterate, comprese le vecchie durate registrate. Si aggiornano solo le aspettative delle nuove esecuzioni; i test di andata/ritorno richiedono che il dato originale sia conservato.

## Compatibilità e validazione

`schemaVersion` e Room restano 1. L'opzionale `timingVersion: 2` identifica i programmi creati o salvati con le nuove regole e permette la validazione rigorosa dei nuovi input. Non è una nuova opzione visibile.

Una copia di un vecchio Workout senza marcatore riceve i nuovi minimi: lavoro e recuperi positivi almeno 5, confini fra blocchi almeno 5, defaticamento positivo almeno 10. Lo zero facoltativo resta zero. I campi storici rimangono nel dato; nessuna lettura riscrive l'intero archivio. Anteprima, editor e avvio mostrano un avviso per i programmi il cui comportamento cambia. Il salvataggio esplicito adotta la copia aggiornata.

## Audio e piattaforme

Workout usa i tre file DING originali importati in assets/audio e copiati identici su web/Android. Il doppio sospiro è 500 ms + 200 ms di silenzio + 500 ms. Studio mantiene i nove campioni precedenti. I tre ding da 1,92 s annunciano fine lavoro e, se attivo, fine defaticamento. Non ci sono bip dentro il defaticamento. I segnali non aggiungono fasi o tempo.

Android usa una breve lease rinnovabile di focus MAY_DUCK che copre 3-2-1 e segnale successivo. Rifiuto e perdita non interrompono il timer; non si forzano suoni né si recuperano avvisi scaduti. La lease viene rilasciata a fine finestra o subito su pausa, stop e disattivazione. I rilasci precedenti non possono scadere una lease estesa. La durata del servizio finale dipende dal campione effettivo, non dalla precedente campanella da 900 ms.

Il ducking dipende dal sistema e dal lettore, in particolare con contenuti parlati o player che preferiscono la pausa: [riferimento Android](https://developer.android.com/media/optimize/audio-focus). I test con un lettore di prova non sostituiscono prove fisiche con Spotify/YouTube e diversi produttori.

La Guida viene generata dalla struttura condivisa locales/help.json e dal catalogo unico. L'italiano aggiornato dall'utente è la fonte dei tre riquadri; non si reinseriscono in inglese/spagnolo i paragrafi eliminati.

## Consegna e verifiche

Il checkout corrente è `C:\Users\rober\Documents\ChatGPT\Tabata\tmp\workout-recuperi-audio`, branch locale `codex/workout-recuperi-audio`. Il checkout originale è pulito sul branch didattico `documentazione` (`b0d3c6a`); main resta `82d3580`. Non sono stati eseguiti commit, push, merge, rebase, deploy, pubblicazioni o servizi a pagamento. Il sito attivo rimane la revisione già pubblicata.

Risultati: 94 Node e 48 JVM passati; suite Android completa 17/17 più nuovo caso editor 1/1 (18 casi distinti). Sull’ultima protezione delle callback focus sono rieseguiti i 6 casi SoundPool/background. Build debug e lint riusciti. Lint: 0 errori, 18 avvisi (15 precedenti e 3 TypographyDashes relativi al testo editoriale “3-2-1”). Nessun controllo Lint soppresso e nessuna dipendenza aggiunta. Sette suite browser pertinenti superate: check, edge-cases, countdown, modes, simple, languages e workout-recuperi. Interoperabilità: quattro export Kotlin riletti in JS, inclusi programma e sessione della demo.

Le prime aspettative obsolete dei test sono state corrette dopo i calcoli riportati sopra. Tre avvii browser concorrenti hanno avuto timeout prima della UI; la ripetizione singola è passata. Le prove finali non hanno fallimenti aperti o test saltati. I report completi sono locali in output e nei report Gradle; [riepilogo e comandi](testing.md).

Esempio visivo verificato: Blocco 1, Ciclo 1/2, Squat 0:07 → Recupero Squat 0:10; Ciclo 2/2, Due ding, Squat 0:07 → Due sospiri, Recupero Blocco 1 → 2 0:06. Quindi Push-up 0:05 → Recupero Blocco 2 → 3 0:10 → Plank 0:05, Tre ding. Totale 0:50 più preparazione 0:20. Non appare un recupero personale aggiuntivo ai confini.

La demo di riferimento è riprodotta dai motori reali: preparazione 20, lavoro 35, recuperi 30, defaticamento 10. Pianificato 75, da Avvia 95; suono finale fino a 96,92 senza credito temporale extra. Il doppio ding sostituisce il singolo; dopo fine lavoro e fine defaticamento viene riprodotto lo stesso WAV completo.

### APK locale e integrità

Nuovo APK debug: `android/app/build/outputs/apk/debug/app-debug.apk` nel worktree sopra. Non è pubblicato né distribuito. Per provarlo vedi [installazione](setup-windows.md). Il vecchio `C:\Users\rober\Documents\ChatGPT\Tabata\output\Tabata Timer.apk` non è stato sovrascritto; SHA-256 verificato invariato:
`4D93B0E5B8CDC3632F16BD5355165DCB0873090ABD92DDD1003347567CD2CC7D`.

Tutti i 9 WAV precedenti sono identici al main iniziale. I 3 DING coincidono con Downloads e con le copie Android/web. Nessun picco PCM satura. I generatori usano asset inclusi e le stringhe sorgente condivise; non dipendono da cartelle temporanee o dalla demo.

### Cosa resta da provare su hardware fisico

Non è stato collegato un telefono fisico. Verificare resa dei DING/sospiro e livelli reali, musica Spotify, video YouTube, podcast/parlato, cuffie/Bluetooth, volume zero/Non disturbare, schermo spento prolungato e risparmio energetico del produttore. La riproduzione e il focus del lettore di prova sull’emulatore non certificano l’attenuazione percepita delle app reali. Il web non promette priorità multimediale o segnali quando il browser viene sospeso. Non sono stati eseguiti test sul sito pubblico o una nuova distribuzione.

## File e responsabilità

| Moduli modificati o aggiunti | Motivo |
| --- | --- |
| web/js/model.js; Android domain/Model.kt, NormalizeWorkout.kt, Modes.kt, QuickWorkout.kt, Codec.kt | Minimi, marcatore interno, conversione pura con avviso e compatibilità v1 |
| web/js/timeline.js; Android domain/Timeline.kt | Unica regola di recupero personale, sostituzione tra blocchi, metadati e nomi pause Studio |
| web/js/timer.js, countdown.js, transitions.js; corrispondenti TimerEngine, CountdownCues, TransitionCues Kotlin | Visite, naturali finali distinti/idempotenti, ritardi, nessun bip nel defaticamento |
| web/js/editor.js, quick.js, preview.js, main.js, i18n.js, styles.css | Campi rimossi, recuperi personali e confini dinamici, anteprima raggruppata e timer localizzato |
| Android ui/EditorScreen.kt, QuickTimerScreen.kt, TimelinePreview.kt, StudioApp.kt, LocaleText.kt | Stessi controlli, validazione e presentazione web; Studio resta piatto |
| Android domain/AudioFocusLease.kt, ui/HornSignals.kt | Focus MAY_DUCK a finestra, perdita/rifiuto, callback legate alla richiesta, caricamento 13 campioni |
| Android timer/TimerRuntime.kt, TimerService.kt | Comandi cancellano suoni/focus, vibrazione indipendente, coda finale completa e background conservato |
| assets/audio/*.wav; scripts/generate-horn.py; quattro nuovi WAV web e quattro Android | Originali riproducibili e doppio sospiro, vecchio audio intatto |
| locales/help.json, locales/messages.json; scripts/generate-locales.py | Guida italiana autorevole e traduzioni fedeli, unica fonte condivisa |
| web/js/help.js, help-content.js, messages.js; Android HelpScreen.kt, HelpContent.kt, MessageIds.kt e values*/messages.xml | Struttura tre riquadri e risorse generate IT/EN/ES |
| web/sw.js | Nuovi moduli e 13 WAV offline, nuova cache distinta dai dati |
| fixtures/timeline-cases.json, workout-recovery-v2.json; test JS/Kotlin e script interop | Calcoli indipendenti, stessi casi tra piattaforme, dati originali e sessioni conservati |
| scripts/browser-{check,edge-cases,countdown,languages,modes,simple,workout-recuperi}.mjs; test Android UiTest, HornTest, BackgroundTimerTest | Verifica reale controlli, anteprime, lingue, audio, offline, focus, background/notifica |
| README, assets/audio/README, docs/audio-countdown, workout-studio, android-background, schema-v1, setup-windows, testing, guida-utente e questo report | Funzioni attuali, provenienza WAV, installazione, compatibilità, test e limiti |

Non è cambiato lo schema Room, il contratto delle sessioni, il manifest, le dipendenze, il marchio, le palette o la logica Studio. Il sorgente del branch didattico resta intatto. Nessuna sessione storica è stata ricalcolata.
