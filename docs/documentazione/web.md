# HTML, CSS e JavaScript

L'ingresso è [web/index.html](righe/web/index.html.md). Carica CSS e `js/main.js` come modulo. Non contiene React, bundler, font remoti o chiamate a un backend. Gli import dichiarano esplicitamente le dipendenze tra i file. [L'indice](indice-file.md) contiene la lettura completa anche di test e script browser.

## Documento e layout

`index.html` definisce titolo pubblico, logo t., switch Workout/Studio, navigazione e contenitore dell'app. Attributi `data-i18n` indicano i testi di sistema traducibili. I campi utente non vengono tradotti. `styles.css` definisce spaziature, schede, bottoni, layout responsive e variabili CSS. `appearance.js` normalizza, carica e applica tema/palette; un ID sconosciuto torna al default senza intaccare gli allenamenti.

L'HTML pubblico contiene una Content Security Policy. Finché l'integrazione pubblicitaria non è completata, gli script esterni sono bloccati. `about.html` e `privacy.html` sono articoli statici con contenuti nelle tre lingue; `public-pages.js` sceglie lingua/aspetto e gestisce i collegamenti di ritorno. Il contenuto originale di questi articoli non è un nuovo riquadro nella Guida integrata.

## Il coordinatore main.js

`main.js` conserva i riferimenti di stato della pagina: repository, modalità, timer, bozza e schermata. `navigate()` protegge le modifiche non salvate. `render()` sceglie la vista; gli handler trasformano il modello e richiamano il rendering. `paintTimer()` aggiorna la rappresentazione del timer durante l'esecuzione. `start()` verifica il programma, prepara le risorse audio nel gesto dell'utente e avvia il motore. `settle()` registra il risultato concluso senza inserirlo due volte.

Gli handler sono delegati al contenitore con attributi delle azioni e dei campi. Le funzioni delle viste restituiscono HTML; non tengono copie autonome dell'archivio. Il contenuto libero passa da `escapeHtml()` prima di entrare in HTML. Nel documento riga per riga le callback sono identificate anche dal chiamante e, quando presente, dal nome dell'evento.

| Modulo | Comportamento |
| --- | --- |
| `quick.js` | Preset rapidi, lavoro/recupero/giri Workout e collegamento alle opzioni aggiuntive |
| `study-fields.js` | Schede piatte Studio, aggiunta/rimozione e pausa comune tra attività |
| `editor.js` | Titolo del programma, blocchi/esercizi Workout, attività Studio e anteprima |
| `views.js` | Raccolta, cronologia, statistiche e Impostazioni |
| `help.js` | I tre riquadri richiesti della Guida |
| `charts.js` | Barre delle statistiche a partire da dati già aggregati |
| `dom.js` | Escaping e formattazione di date/decimali |
| `site-links.js` | Accesso alle pagine Informazioni e Privacy dalle Impostazioni |

## Dal modello alla sequenza

`model.js` crea ID, modelli iniziali, copie e preset, verifica tipi/limiti/ID, e offre l'adattatore dei programmi precedenti. `modes.js` centralizza modalità, testi delle raccolte e fattore delle unità. `timeline.js` produce una lista ordinata di oggetti fase con tipo, durata in millisecondi, etichetta e riferimenti a blocco/esercizio o attività.

I controlli sul numero dell'elemento stabiliscono se inserire una pausa. Per esempio, con `session < sessions - 1` la pausa interna Studio non compare dopo l'ultima sessione. Il limite analogo sulla lista attività impedisce una pausa finale globale. Le durate zero vengono saltate. La compilazione rifiuta più di 10.000 fasi o oltre 24 ore complessive. Anteprima e motore chiamano la stessa funzione: non esistono due formule indipendenti da aggiornare.

## Il motore indipendente dal browser

`TimerEngine` in `timer.js` accetta orologi sostituibili nei test. `tick()` misura `current - last`, limita delta negativi a zero e consuma prima interamente le fasi scadute, poi l'eventuale resto sulla fase corrente. `consume()` esclude l'indice di preparazione e incrementa il lavoro solo per tipo WORK. Gli insiemi degli indici completati evitano di contare nuovamente un intervallo quando si usa Precedente.

`pause()` richiama prima `tick()`, così il tempo fino al clic non va perso. `resume()` reimposta `last`, evitando di conteggiare la sosta. `skip()` avanza senza trasformare i millisecondi saltati in lavoro. `finish()` restituisce lo stesso risultato se richiamata nuovamente: questo facilita un salvataggio idempotente. Lo stato temporale non dipende da quanto velocemente viene disegnata la pagina.

## Audio

`TimerSignals` gestisce Web Audio. `unlock()` crea o riprende l'AudioContext dopo un gesto e decodifica i WAV locali una volta; nessun file viene scaricato da YouTube durante l'uso. `play()` ferma la sorgente precedente, crea sorgente e gain, collega la destinazione e pulisce i nodi a fine suono. `horn()` sceglie il campione corretto per numero e fase.

`CountdownTracker` e `TransitionTracker`, nei rispettivi moduli, ricordano gli avvisi già emessi. Un risveglio tardivo non deve far suonare tutti i vecchi 3-2-1 in raffica. Pausa, disattivazione audio e Termina fermano le sorgenti. Il fischio ha l'amplificazione già nel campione e usa gain 1: applicare di nuovo 1,2 raddoppierebbe l'aumento rispetto all'intenzione. [Risorse](risorse.md) spiega durate, livelli e provenienza.

## Archivio e scambio dati

`LocalRepository` in `storage.js` parte in modalità temporanea e conserva `{workouts, sessions}` in memoria. `load()` tenta di leggere e validare la copia sotto la chiave `tabata:v1:data`. Se è corrotta, segnala il problema e non la cancella. `enable()` prova la scrittura prima di considerare attiva la persistenza. `saveWorkoutLocally()` inserisce/aggiorna il programma e attiva il salvataggio; `addSession()` evita duplicati per ID. Un errore di quota lascia i dati in RAM e invita a esportarli prima di chiudere.

`io.js` verifica backup, limiti, date, identificativi e risultati. `makeBackup()` include entrambe le modalità. `sessionsCsv()` esporta la cronologia richiesta e neutralizza i titoli che potrebbero essere interpretati come formule dai fogli di calcolo. `download()` crea un Blob e il relativo link di download, senza invio a un server. [Dati](dati.md) dettaglia il contratto.

## Traduzioni

`i18n.js` sceglie prima la preferenza salvata valida, poi la lingua del dispositivo e infine l'italiano. Gestisce `it`, `en` ed `es`. `t()` traduce chiavi di sistema e sostituisce placeholder; non traduce nomi del programma o delle attività. `translateDocument()` applica le etichette dichiarate nel documento. Il catalogo sorgente è `locales/messages.json`, non il file compatto generato `messages.js`. Gli ID e i valori numerici non devono essere tradotti.

## Offline e limiti reali

`sw.js` precachea i file statici dell'app e rimuove le vecchie cache dell'applicazione. Non è il database della cronologia. La gestione delle navigazioni riconosce `/about`, `/about.html`, `/privacy`, `/privacy.html` e query lingua: una pagina informativa non deve ricevere per errore l'HTML del timer quando manca rete.

L'orologio monotono corregge il conteggio dopo un rallentamento, ma non concede al browser un servizio Android. Una scheda sospesa può perdere segnali audio; chiudere o ricaricare una sessione web non la riprende automaticamente. Il wake lock web riguarda lo schermo quando supportato, non mantiene la CPU come il servizio nativo. Questi limiti rimangono anche installando la PWA.
