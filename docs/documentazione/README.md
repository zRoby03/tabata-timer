# Tabata Timer: il progetto spiegato

Questo è il branch didattico **documentazione** del repository pubblico [zRoby03/tabata-timer](https://github.com/zRoby03/tabata-timer). La base del codice applicativo è il commit [5d18d6d](https://github.com/zRoby03/tabata-timer/tree/5d18d6d633e8fd8c8241aed078246f1953b084ef). `main` conserva i sorgenti dell'app senza le annotazioni aggiunte qui. La pubblicazione del sito rimane indipendente dal push su GitHub.

Il sito effettivamente pubblicato è [Tabata Timer su Cloudflare](https://tabata-timer-ek1.pages.dev). Questo branch non è stato caricato su Cloudflare, non distribuisce un APK e non attiva annunci.

## Come leggere la documentazione

1. [Architettura e percorso di un comando](architettura.md): quali parti esistono e come collaborano.
2. [HTML, CSS e JavaScript](web.md): schermate, eventi, motore, audio, dati locali e offline.
3. [Kotlin e Android](kotlin.md): tipi, Compose, ViewModel, Room e servizio in background.
4. [Dati e compatibilità](dati.md): salvataggi, backup, formule e conversione degli archivi precedenti.
5. [Implementazioni e scelte](implementazioni.md): richiesta, modifica necessaria e verifica, funzione per funzione attraverso i collegamenti ai sorgenti.
6. [Installazione e verifiche ripetibili](installazione-verifiche.md): strumenti, comandi e controlli del branch commentato.
7. [Pubblicazione eseguita e GitHub](pubblicazione-github.md): primo hosting, Cloudflare, AdSense e cosa rimane da integrare.
8. [Audio, logo e file generati](risorse.md): origine, trasformazioni e differenza tra sorgente e risultato.
9. [Indice completo dei file](indice-file.md): accesso alla spiegazione di **ogni riga** e al catalogo delle funzioni/callback di ciascun modulo.

## Che cosa significa «ogni riga»

Ogni riga fisica dei file di codice, configurazione, test, script e cataloghi della base ha una voce nella tabella del documento affiancato. Sono comprese righe vuote, parentesi, import, dichiarazioni e dati generati. Ogni funzione JavaScript nominata, metodo, callback JavaScript e dichiarazione `fun` Kotlin viene indicizzata con il suo ruolo; i metodi Java e le funzioni Python sono inclusi. Le lambda Kotlin sono spiegate nelle righe del corpo, senza attribuire loro un nome inventato.

I commenti `DOC L0123` sono inseriti direttamente prima delle righe di codice quando la sintassi lo consente. JSON, cataloghi generati, wrapper Gradle, stringhe multilinea e template HTML dentro JavaScript sono spiegati nel documento affiancato: inserirvi commenti potrebbe alterare il dato o rompere il formato. I documenti già scritti in `docs/` costituiscono spiegazioni, non codice da ricommentare. I binari hanno dimensioni, impronte e provenienza, senza finte righe testuali.

Il numero `L0123` si riferisce alla **riga originale**, non alla posizione dopo l'aggiunta dei commenti. I link delle tabelle puntano al commit immutabile di base; `copertura.json` conserva la corrispondenza con le righe del branch commentato. L'estratto di codice in tabella è limitato a 160 caratteri per leggibilità: il collegamento apre la riga intera.

Le descrizioni sono prodotte con analisi sintattica e un catalogo editoriale dei moduli, delle funzioni e dei campi, in [tools/documentation](../../tools/documentation). I passaggi centrali sono approfonditi nelle guide sopra. I cataloghi più lunghi sono suddivisi in pagine collegate per facilitarne la lettura su GitHub. Il generatore non modifica istruzioni, stringhe, tempi o risorse. [Il controllo automatico](verifica-commenti.json) verifica che, togliendo i soli commenti DOC, si ottenga il sorgente originale; confronta inoltre AST JavaScript/Python e token Kotlin. I test dell'app verificano il comportamento.

## Da dove iniziare nel codice

| Obiettivo | Web | Android |
| --- | --- | --- |
| Capire Avvia | [main.js](righe/web/js/main.js.md) | [StudioViewModel.kt](righe/android/app/src/main/java/it/tabatastudio/ui/StudioViewModel.kt.md) |
| Capire la durata | [timeline.js](righe/web/js/timeline.js.md) | [Timeline.kt](righe/android/app/src/main/java/it/tabatastudio/domain/Timeline.kt.md) |
| Capire pausa e ripresa | [timer.js](righe/web/js/timer.js.md) | [TimerEngine.kt](righe/android/app/src/main/java/it/tabatastudio/domain/TimerEngine.kt.md) |
| Capire il background | Limiti descritti in [web.md](web.md) | [TimerService.kt](righe/android/app/src/main/java/it/tabatastudio/timer/TimerService.kt.md) |
| Capire i salvataggi | [storage.js](righe/web/js/storage.js.md) | [Database.kt](righe/android/app/src/main/java/it/tabatastudio/data/Database.kt.md) |
| Cambiare un testo | [locales/messages.json](righe/locales/messages.json.md) | Stesso catalogo, poi generazione risorse |
| Preparare lo ZIP pubblico | [package-cloudflare.py](righe/scripts/package-cloudflare.py.md) | L'APK ha un processo distinto |

I risultati del controllo di questo branch sono in [verifiche del branch](verifiche.md). I report precedenti sono conservati in [docs/verifiche](../verifiche), con il loro contesto storico; non vengono presentati come nuove prove eseguite oggi.
