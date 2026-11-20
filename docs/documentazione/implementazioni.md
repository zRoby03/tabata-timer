# Implementazioni e scelte del progetto

La cronologia dettagliata delle revisioni è in [progress.md](../progress.md). Le cinque fasi iniziali hanno seguito il PDF fornito: contratto e compilatore, web, Android, interoperabilità, rifiniture e verifiche. Le richieste successive dell'utente hanno modificato alcuni comportamenti e hanno priorità sul progetto iniziale. Le frasi operative contenute nel PDF sono state trattate come specifiche del documento, non come autorizzazioni autonome a usare account o pubblicare.

Questa guida ricostruisce il risultato attuale. Nella cronologia, un comportamento superato non va interpretato come una funzionalità ancora presente: per esempio Android inizialmente si metteva in pausa in background, mentre ora continua tramite un servizio foreground.

## Le cinque fasi iniziali

| Fase | Cosa è stato necessario aggiungere | Come è stato verificato |
| --- | --- | --- |
| 1. Contratto e compilatore | Modelli JS/Kotlin, schema JSON v1, ID, limiti, fasi e fixture comuni prima dell'editor completo | Stessa durata della fixture di riferimento in entrambi i linguaggi; rifiuto di modelli non validi |
| 2. Web | HTML/CSS, moduli di viste/editor/timer, archivio temporaneo o locale, statistiche, cache offline | Test unitari e flussi browser, persistenza, responsive, import annullabile e offline |
| 3. Android | Compose, ViewModel, Room, repository e motore indipendente dall'Activity | Compilazione, test JVM, repository/UI su emulatore; versioni compatibili di KSP e runner |
| 4. Scambio dati | Codec JSON/CSV, anteprima e sostituzione confermata, fixture di andata/ritorno | Web → Kotlin → Web con programmi, sessioni e durate identici |
| 5. Rifiniture | Accessibilità, conferme, gestione quota, checkpoint, guide e script di controllo | Prove dei percorsi di errore e matrici di accettazione, con limiti hardware documentati |

KSP è stato portato a 2.3.9 per i source set Kotlin di AGP 9.2; il runner UI è stato aggiornato per l'emulatore API 37. Non sono stati disabilitati i controlli per nascondere incompatibilità. Le versioni finali sono riportate in [installazione](installazione-verifiche.md).

## Interfaccia iniziale più semplice

Sono stati separati il timer rapido e l'editor personalizzato. Workout espone subito Lavoro, Recupero e Giri, con Avvia e Salva allenamento. Serie, recupero tra serie e defaticamento sono in Altre opzioni. I tre preset hanno nomi semplici Tabata 1/2/3 e cambiano i tempi senza introdurre una procedura guidata.

Per farlo sono stati aggiunti `quick.js` e `QuickWorkout.kt`, poi i relativi componenti Compose. La conversione dal timer rapido al modello comune permette di usare la stessa validazione e timeline dell'editor: non esiste un secondo motore con regole differenti. Il pulsante Riscaldamento è stato rimosso; il campo legacy resta leggibile nei dati.

Le ultime richieste hanno tolto i pulsanti Prova demo di Workout e Studio, il messaggio sotto la scheda sul salvataggio locale e il footer con «I tuoi dati restano sul dispositivo». Le routine demo precedenti possono restare come codice non esposto o strumenti di test: non sono nuovi pulsanti dell'interfaccia. Nome visualizzato, manifest e titolo sono soltanto **Tabata Timer**. Numeri interni di build/cache continuano a distinguere aggiornamenti tecnici senza entrare nel nome pubblico.

## Preparazione e segnali

La preparazione di 20 secondi è stata inserita nel motore prima della timeline, con indice dedicato e contatori esclusi dalle statistiche. È condivisa dalle due modalità e compare separatamente dalla durata prevista. Test specifici coprono pausa, salto, ritardo che attraversa preparazione e lavoro, oltre al suono iniziale.

I suoni si sono evoluti su richiesta: dai motivetti a tre note singole e brevi sul 3-2-1. Le note basse appartengono alla fase lavoro che sta finendo; le alte a preparazione e recuperi. Un fischio di 500 ms segnala l'inizio del lavoro, un sospiro di 500 ms il recupero, tre ding la conclusione prima dell'eventuale defaticamento.

Per sincronizzarli senza raffiche sono stati aggiunti `CountdownTracker`/`CountdownCues` e `TransitionTracker`/`TransitionCues`, più WAV locali uguali sulle due piattaforme. I tracker distinguono numero, visita e ritardo; il gestore audio interrompe la sorgente precedente. Il successivo aumento del fischio al 120% è stato applicato al PCM con guadagno di riproduzione 1, compatibile con SoundPool. L'ultima modifica ha abbassato e ammorbidito il timbro da arbitro mantenendo 500 ms e gli altri otto campioni identici. [Dettagli delle risorse](risorse.md).

## Due modalità nello stesso layout

Il modello ha ricevuto `mode` con fallback Workout per gli archivi precedenti. `modes.js`/`Modes.kt` centralizzano testi, filtri e default. Lo switch in alto ricorda la scelta e impedisce cambi durante un timer o un salvataggio pendente. Raccolte, attività, statistiche e CSV filtrano per modalità; backup JSON conserva entrambe. Lingua, tema e palette sono comuni.

Studio è stato poi semplificato in un Pomodoro con **lista piatta**: Attività 1/2/3, Nome attività, Concentrazione, Sessioni e Pausa. Un solo Pausa tra attività sta fuori dalle schede. Sono stati aggiunti `StudyActivity`, `activities`, `restBetweenActivitiesSec`, `study-fields.js` e `StudyFields.kt`. La compilazione inserisce pause interne solo tra sessioni e pause globali solo tra attività. Blocchi/cicli/serie, pause lunghe/frequenza e pausa finale non sono configurabili in Studio. Il titolo dell'intero programma resta nel salvataggio.

Workout conserva blocchi ed esercizi. Il recupero tra esercizi è stato spostato sul blocco con `restBetweenExercisesSec`: un campo comune invece di uno per scheda. Recuperi tra ripetizioni e altre impostazioni rimangono nel modello. Il compilatore controlla il confine dell'ultimo esercizio per ogni ciclo. L'adattatore legacy spiega la scelta del primo recupero precedente quando mancava il nuovo campo comune.

Le abbreviazioni visibili min/sec sono state rimosse da etichette, preset e anteprime senza toccare conversioni o durate. Le parole estese richieste rimangono nella Guida. I test delle modalità controllano sia il modello sia la sequenza e la conservazione dei dati precedenti.

## Aspetto e Guida

`appearance.js`, `StudioTheme.kt` e `AppearanceSettings.kt` hanno separato preferenze e dati. L'ordine finale è Viola, Blu, Verde chiaro, Verde scuro, Azzurro, Arancione, Giallo, Rosso, Rosa, con Chiaro/Scuro. Sono stati verificati contrasto del testo almeno 4,5:1 sulle coppie previste e fallback per preferenze sconosciute o storage negato.

La Guida contiene soltanto Workout e Studio, Per conservare un allenamento, Tema e Colori. Il testo italiano richiesto del primo riquadro è conservato con i capoversi; inglese e spagnolo ne sono traduzioni. Le istruzioni sul salvataggio nominano le due raccolte correttamente. Informazioni/Privacy hanno percorsi separati nelle Impostazioni.

## Android in background insieme a musica/video

Mantenere un ViewModel non basta per lavorare a schermo spento. È stato aggiunto `TimerRuntime` come proprietario del motore dell'app e `TimerService` come esecutore foreground, con notifica, azioni associate alla sessione, coroutine, wake lock CPU e gestione del ciclo di vita. SoundPool è stato legato al servizio e non richiede audio focus.

Il manifest dichiara servizio e permessi necessari, senza Internet, allarmi esatti o accessi agli account multimediali. Il repository scrive checkpoint e conclusione transazionale. Il recupero dopo morte del processo produce un'attività interrotta una volta sola. Test strumentali coprono schermo spento, notifica, vecchie azioni, rotazione, chiusura del processo e coesistenza con un lettore esterno di prova. [Approfondimento Kotlin](kotlin.md) e [verifiche precedenti](../android-background.md).

## Italiano, inglese e spagnolo

È stato aggiunto un catalogo sorgente unico `locales/messages.json` e `generate-locales.py`, che produce `messages.js`, `MessageIds.kt` e le risorse Android delle tre lingue. `i18n.js` e `AppText.kt` gestiscono la preferenza, il fallback dal dispositivo e il selettore nelle Impostazioni. La notifica usa la stessa lingua corrente.

Le chiavi di sistema, i placeholder e le etichette generate sono tradotti esplicitamente. Nomi e descrizioni scritti dall'utente, ID e tempi non vengono modificati. I test verificano completezza catalogo, placeholder corrispondenti, lingua sconosciuta, storage bloccato e conservazione dei nomi personalizzati.

## Logo e nome

Il tracciato `assets/branding/t-dot-path.txt` è la sorgente comune della t. `generate-brand-icons.py` produce SVG/vettori e `render-brand-icons.mjs` produce PNG web; i launcher Android includono maschere adattive e icona a tema. Il manifest, il launcher, la notifica e il sito condividono il marchio. Il titolo non include versioni; applicationId Android e numeri interni rimangono identificatori tecnici.

## Hosting e predisposizione pubblicitaria

Dopo l'autorizzazione «andiamo», il sito è stato pubblicato prima con Sites e poi su Cloudflare Pages tramite caricamento diretto dello ZIP. Sono stati aggiunti script di pacchettizzazione e `_headers`. Per Cloudflare si è corretto anche l'offline delle pagine senza estensione. Non è stata aggiunta un'infrastruttura backend o una pipeline di deploy GitHub.

Per AdSense sono stati aggiunti `ads.txt`, articoli Informazioni/Privacy nelle tre lingue, collegamenti dalle Impostazioni e configurazione documentale fuori dal web. Account, unità manuale e messaggio CMP sono stati predisposti; il sito è stato inviato a revisione. **Il tag pubblicitario non è caricato nel sito**. La pubblicazione del messaggio nell'account non equivale al consenso funzionante nelle pagine. [Procedura e stato](pubblicazione-github.md).

## Repository pubblico e branch didattico

Su richiesta è stato creato `zRoby03/tabata-timer`, pubblico, usando l'account indicato dall'utente anziché il connettore collegato a un altro account. Prima del push sono stati esclusi output, APK, cache, SDK, impostazioni locali e chiavi. I report selezionati sono stati copiati in `docs/verifiche` soltanto dopo controllo del contenuto.

Il branch `documentazione` aggiunge commenti nei punti sintatticamente sicuri e documenti per ogni riga; Acorn è una dipendenza di sviluppo del solo strumento, non del sito. Il confronto col commit originale e i test dell'app impediscono di confondere la documentazione con una nuova implementazione del timer. Nessun push di questo branch aggiorna il sito o distribuisce l'APK.
