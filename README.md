# Tabata Timer

> **Branch documentazione.** Inizia dalla [guida al progetto spiegato](docs/documentazione/README.md) e dall'[indice riga per riga](docs/documentazione/indice-file.md). I commenti didattici sono verificati rispetto al codice originale; questo branch non è collegato alla pubblicazione del sito. Per i sorgenti senza annotazioni consulta [main](https://github.com/zRoby03/tabata-timer/tree/main).

**[Repository pubblico](https://github.com/zRoby03/tabata-timer)** · **[Branch documentazione: codice commentato e guide](https://github.com/zRoby03/tabata-timer/tree/documentazione/docs/documentazione)**. Il push GitHub non aggiorna automaticamente il sito.

Timer per **allenamento e studio, su web e Android**, in italiano, inglese e spagnolo, con un’interfaccia semplice e moderna. Funziona in locale: nessun account, backend, pubblicità, chiave API o abbonamento.

**Sito online: [Tabata Timer](https://tabata-timer-ek1.pages.dev)**, su Cloudflare Pages con sottodominio gratuito. APK conservato soltanto in locale. Per trasferire i salvataggi dal precedente indirizzo, esporta la copia JSON e importala sul nuovo sito. AdSense collegato e inviato a revisione; annunci ancora inattivi. [Pubblicazione e aggiornamenti](docs/pubblicazione.md) · [Cloudflare, AdSense e condivisione APK](docs/cloudflare-adsense-apk.md).

**[Guida a tutte le funzioni e al salvataggio locale](docs/guida-utente.md)** · [Installazione Windows](docs/setup-windows.md) · [Prova rapida](docs/demo.md) · [Verifiche](docs/testing.md)

## Tabata Timer

**Lingua automatica dal dispositivo**, con scelta Italiano / English / Español in Impostazioni → Lingua. La preferenza resta locale e vale per entrambe le modalità. Interfaccia, Guida, editor, timer, statistiche, errori e notifica Android sono tradotti; nomi, descrizioni e cronologia già salvati rimangono quelli dell’utente. [Lingue e manutenzione](docs/lingue.md) · [File pronti e passaggio al sito pubblico](docs/pubblicazione.md).

Rimossi i pulsanti demo di Workout e Studio, la riga sotto il timer sul salvataggio nel browser/telefono e il piè di pagina web. Salvataggio e informazioni sui dati restano nelle Impostazioni e nelle finestre di salvataggio. Il layout e il comportamento del timer sono conservati.


**Android continua in background e a schermo spento**, sia in Workout sia in Studio. La notifica permette Pausa, Riprendi, Salta e Termina. I segnali si mescolano a musica e video senza mettere in pausa il lettore; usano il volume multimediale. Consenti le notifiche al primo timer per vedere i controlli fuori dall’app. [Uso e dettagli del background](docs/android-background.md).

Lo switch a scorrimento in alto a sinistra, accanto al logo e a **Tabata Timer**, sceglie la modalità e ricorda l’ultima usata. Il layout resta lo stesso.

| | Tabata Workout | Tabata Studio |
| --- | --- | --- |
| Tempi | Secondi | Minuti interi |
| Campi | Lavoro, Recupero, Giri; blocchi con recupero comune | Attività con Nome attività, Concentrazione, Sessioni, Pausa |
| Preset | 20/10, 30/15, 40/20 | 25/5, 50/10, 45/15 |
| Raccolta | Allenamenti | Programmi di studio |
| Attività e statistiche | Solo Workout | Solo Studio, minuti di concentrazione |
| Preparazione | 0:20, 3-2-1 e fischio | 0:20, 3-2-1 e fischio |

Studio è un Pomodoro semplice: aggiungi o elimina attività, scegli il nome e i tempi di ciascuna. La Pausa vale soltanto tra le sessioni della stessa attività. Un solo campo **Pausa tra attività** vale tra attività consecutive. Non ci sono blocchi, serie, pause lunghe o finali. Le scelte rapide cambiano solo i tempi della prima attività. Il nome del programma si sceglie al salvataggio. Tema e palette sono comuni alle modalità; programmi, attività e statistiche sono separati. Per cambiare modalità termina il timer.

Workout mantiene blocchi, esercizi, cicli e ripetizioni. Ogni blocco ha un solo **Recupero tra esercizi**, applicato solo tra esercizi consecutivi; i recuperi tra ripetizioni restano distinti.

Gli archivi precedenti non vengono riscritti all’apertura: un avviso spiega le regole di conversione, applicate ad anteprima ed esecuzione. Solo il salvataggio esplicito sostituisce quel programma. [Regole precise e compatibilità](docs/workout-studio.md#archivi-precedenti).

Apri direttamente [Workout](http://127.0.0.1:5500/?mode=workout) o [Studio](http://127.0.0.1:5500/?mode=studio) con il server locale avviato. [Funzioni e salvataggi delle due modalità](docs/workout-studio.md).

Tre sezioni: **Timer**, **Allenamenti**, **Attività**. La schermata iniziale mostra solo Lavoro, Recupero e Giri, più Avvia e Salva allenamento. Serie e defaticamento sono in Altre opzioni. Il controllo Riscaldamento è stato rimosso.

- **Impostazioni → Aspetto:** tema Chiaro/Scuro e palette Viola, Blu, Verde chiaro, Verde scuro, Azzurro, Arancione, Giallo, Rosso e Rosa. Preferenze salvate localmente, separate dall’archivio.
- Tre scelte rapide: **Tabata 1** (20/10), **Tabata 2** (30/15), **Tabata 3** (40/20).
- Ogni avvio include **20 secondi di preparazione**, indicati separatamente dalla durata dell’allenamento.
- **Tre note singole in tre secondi** scandiscono **3, 2, 1**: un bip breve e immediato a ogni numero, con silenzio tra le note, basso per il lavoro e alto per preparazione, recuperi e defaticamento. Fischietto e sospiro durano entrambi 500 ms, rispettivamente all’inizio del lavoro e del recupero, con fischio al 120% e sospiro invariato all’85%. Il sospiro è un estratto del riferimento indicato dall’utente. Tre ding segnalano la fine dell’allenamento, prima dell’eventuale defaticamento. Tutti i suoni sono disattivabili. [Dettagli audio e provenienza](docs/audio-countdown.md).
- **Salva allenamento → nome → Salva sul dispositivo** conserva il timer nella raccolta Allenamenti. Sul web questa conferma attiva il salvataggio locale.
- L’editor personalizzato gestisce blocchi, cicli, esercizi, ripetizioni, recuperi e anteprima.
- Attività leggibili in schede, dettagli delle sessioni, statistiche 7/30/90 giorni e confronto per allenamento.
- Copia completa JSON v1 interoperabile, importazione con anteprima e conferma, esportazione delle attività CSV.
- **Guida** con tre soli riquadri: Workout e Studio, Per conservare un allenamento, Tema e Colori. Etichette e preset senza abbreviazioni di unità.

L’implementazione segue le cinque fasi della specifica PDF del 12 novembre 2026, con le successive modifiche richieste dall’utente. Cronologia del lavoro in [docs/progress.md](docs/progress.md).

## Avvio web

Da PowerShell, nella cartella del progetto:

```powershell
python -m http.server 5500 --bind 127.0.0.1 --directory web
```

Apri [Tabata Timer in locale](http://127.0.0.1:5500). Se necessario sostituisci `python` con `py`. Non aprire `index.html` con doppio clic: i moduli JavaScript e il service worker richiedono HTTP locale o HTTPS. Non occorrono npm, bundler o database.

Al primo utilizzo i dati sono **temporanei**. Premi **Salva allenamento**, scegli il nome e conferma **Salva sul dispositivo** per conservarli nel browser, oppure usa **Attiva il salvataggio** / **Impostazioni → Salva cronologia**. Dovrai usare lo stesso browser, profilo e indirizzo. `localhost` ha un archivio distinto da `127.0.0.1`.

Dopo il primo caricamento completo, il service worker conserva i soli file statici e permette l’uso offline. I dati personali hanno un archivio separato. In Studio usa **Salva programma di studio → nome del programma → Salva sul dispositivo**. Per ottenere anche un file nei download usa **Impostazioni → Esporta copia JSON**: include entrambe le modalità. Il CSV include solo le attività della modalità corrente.

## Android

Apri la cartella `android` in Android Studio, sincronizza Gradle e usa Run con emulatore o telefono (Android 8/API 26 o superiore). Non serve Play Store. [Procedura completa e versioni verificate](docs/setup-windows.md).

```powershell
cd android
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebug
```

APK debug: `android/app/build/outputs/apk/debug/app-debug.apk`. L’archivio locale usa Room/SQLite; attività e allenamenti salvati rimangono nell’app. Il manifest non richiede Internet e disabilita backup cloud e trasferimento automatico dei dati. Importazione ed esportazione usano il selettore file Android: scegli una cartella locale se vuoi tenere la copia sul telefono.

## Test

Node.js serve solo ai test, non all’utilizzo web:

```powershell
node --test web/tests/*.test.js
cd android
.\gradlew.bat testDebugUnitTest assembleDebug lintDebug
# Con emulatore o telefono di test collegato:
.\gradlew.bat connectedDebugAndroidTest
cd ..
node scripts/interop-check.mjs
```

I test strumentali aggiungono dati demo: usa un emulatore o dispositivo dedicato. Le fixture comuni verificano **220000 ms = 3:40**, oltre ai 20 s di preparazione. [Risultati e limiti](docs/testing.md) · [Formato JSON e formule](docs/schema-v1.md).

## Limiti

Sul web, chiudere o ricaricare durante una sessione non la riprende e non salva automaticamente una sessione parziale. Premi Termina per registrarla. Browser sospesi e schermo bloccato possono impedire audio, vibrazione e aggiornamenti.

Android mantiene il timer in un servizio in primo piano anche in background e a schermo spento. Arresto forzato o spegnimento del telefono interrompono l’esecuzione. La rotazione conserva il timer; dopo la chiusura del processo, il checkpoint viene registrato una sola volta come attività interrotta. Può mancare l’ultimo secondo attivo, o più in caso di errore di salvataggio. Una bozza editor non salvata non sopravvive alla chiusura del processo.

La pulizia del browser, la disinstallazione Android o la cancellazione dei dati possono eliminare l’archivio. Esporta periodicamente una copia JSON. I dati non passano automaticamente tra dispositivi. Le statistiche misurano tempo e continuità, senza valutare il miglioramento fisico.

**Il sito è pubblicato su Cloudflare Pages**, dopo l'autorizzazione dell'utente. Nessun servizio a pagamento attivato e nessun APK distribuito pubblicamente. Il server Python descritto sopra serve soltanto allo sviluppo locale.

## Consegna locale

I file di consegna locali sono `output/Tabata Timer.apk`, `output/Tabata Timer.zip` (sito) e `output/sorgenti/Tabata Timer.zip` (sorgenti). La cartella output è esclusa da GitHub: contiene anche risultati di revisioni precedenti. L'ultima build Android consegnata è la revisione interna 1.8.1; le modifiche web della successiva pubblicazione non ne hanno cambiato l'APK. I report selezionati sono in [docs/verifiche](docs/verifiche).

Per ricostruire lo ZIP Cloudflare: `python scripts/package-cloudflare.py`. Lo ZIP risultante è `output/cloudflare/Tabata Timer.zip`. Per ricompilare un APK di sviluppo usare Gradle come descritto sopra; non occorre scaricare un APK pubblico da questo repository. [Pubblicazione e stato AdSense](docs/pubblicazione.md).

## Nome, icona e fischio

Il nome mostrato nel browser, nelle schermate iniziali e all’installazione è soltanto **Tabata Timer**. Workout e Studio rimangono le due modalità. I vecchi parametri di versione e modalità vengono letti e rimossi dall’indirizzo visibile; la scelta resta nelle preferenze locali.

L’icona è la **t.**, comune a sito, Android, launcher e notifica; Android supporta maschere adattive e icone a tema. SVG, PNG web e vettori Android derivano dallo stesso tracciato locale (`assets/branding/t-dot-path.txt`). Il fischio di avvio è più basso e morbido, ispirato al fischietto da arbitro, sempre 500 ms; altri suoni e durate invariati.

Le revisioni tecniche rimangono interne per aggiornamenti, cache e verifiche. Il sito è pubblico; APK e chiavi di firma rimangono fuori da GitHub. Annunci AdSense ancora inattivi.
