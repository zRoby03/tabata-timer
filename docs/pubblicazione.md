# Pubblicazione di Tabata Timer

Sito pubblico corrente: **https://tabata-timer-ek1.pages.dev**

Pubblicato il 20 novembre 2026, prima con Sites dopo l’autorizzazione “andiamo”, poi su Cloudflare Pages su richiesta esplicita. Accesso pubblico: chi riceve il link può aprire il timer. Il nome del progetto Cloudflare è `tabata-timer`; il sottodominio assegnato è `tabata-timer-ek1.pages.dev`. Non sono stati attivati servizi a pagamento e l’APK non è stato pubblicato.

Il precedente indirizzo https://tabata-timer.paddedlotus8.chatgpt.site resta disponibile con la versione precedente; non è stato eliminato o sovrascritto. I suoi salvataggi browser non passano automaticamente al nuovo indirizzo.

## File pronto

`output/Tabata Timer.zip` contiene `index.html` nella radice, CSS, moduli JavaScript, cataloghi linguistici, manifest, icone, service worker, audio, pagine Informazioni/Privacy e `ads.txt`: 42 file statici. `output/cloudflare/Tabata Timer.zip` aggiunge `_headers` per Cloudflare, per un totale di 43 file. Non comprende APK, test, SDK, archivi personali o credenziali. Non richiede database, backend o servizi di traduzione.

L’indirizzo pubblico usa HTTPS per cache offline e installazione web. La cartella `hosting/cloudflare` contiene gli header e i riferimenti pubblici AdSense. Il comando `python scripts/package-cloudflare.py` produce e verifica lo ZIP da caricare come nuovo deployment Production dello stesso progetto Cloudflare. Non serve installare Wrangler o collegare un repository Git.

Il report corrente è `output/cloudflare/publication.json`. `output/publication.json` conserva la prima pubblicazione Sites con il relativo commit. La cartella `sites/tabata-timer` e la sua identità `.openai/hosting.json` appartengono a quella prima pubblicazione e non sono state riutilizzate come identità Cloudflare. Nessun deploy automatico configurato.

## Conservare i dati passando dal locale al sito pubblico

1. Prima di cambiare indirizzo, nel sito locale apri Impostazioni → Esporta copia JSON. Il file comprende Workout, Studio e tutte le attività.
2. Conserva quel file nei tuoi documenti. Il solo sito ZIP non contiene i tuoi allenamenti.
3. Apri l’indirizzo pubblico, nello stesso dispositivo o in un altro.
4. Apri Impostazioni → Importa copia JSON, scegli il file, controlla l’anteprima e conferma Sostituisci dati. Prima esporta eventuali dati già presenti all’indirizzo pubblico da conservare.
5. Sul web conferma il salvataggio locale per ritrovare i dati alla riapertura. Per trasferirli su Android usa lo stesso JSON.

Gli archivi del browser sono separati per indirizzo: `127.0.0.1`, `localhost` e il dominio pubblico sono archivi distinti. La pubblicazione non cancella quello locale e non lo trasferisce automaticamente. Tema, palette e lingua sono preferenze del singolo dispositivo/indirizzo e si scelgono di nuovo dove necessario.

## Controllo successivo alla pubblicazione

Verificati sull’indirizzo pubblico HTTPS: lingua iniziale italiano/inglese/spagnolo, selettore persistente, salvataggio e riapertura, nomi personalizzati conservati, entrambe le modalità, preparazione, avvio e interruzione, statistiche, nove palette, tre riquadri Guida, assenza dei pulsanti demo e del footer, quattro larghezze da 320 a 1440 e riapertura offline dopo il caricamento completo.

Gli avvisi del timer web restano soggetti ai limiti dei browser sospesi; l’app Android usa il servizio in background già verificato. Durate, suoni e APK Android non sono cambiati per questo passaggio. Le pagine Informazioni e Privacy seguono lingua, tema e palette del sito e sono raggiungibili da Impostazioni. La Guida integrata conserva i tre riquadri richiesti. Corretto il recupero offline degli indirizzi senza estensione `/about` e `/privacy` usati da Cloudflare, anche con parametri lingua.

AdSense ha riconosciuto `ads.txt` come Autorizzato e il sito risulta in preparazione, con revisione richiesta. Creata una sola unità display; annunci automatici OFF. Il messaggio europeo Google è pubblicato nell’account, in italiano, inglese e spagnolo, con rifiuto in tutte le regioni e ottimizzazione del consenso disattivata. L’utente ha completato le informazioni del publisher: Google conferma “Hai completato tutti i passaggi” e “Abbiamo ricevuto le tue informazioni”. Il codice pubblicitario resta fuori da `web`: sul sito non vengono ancora caricati annunci o cookie pubblicitari. Restano l’esito della revisione Google e la successiva attivazione controllata del codice/CSP sulla sola pagina Informazioni, con verifica del consenso e della revoca. I dettagli sono in `docs/cloudflare-adsense-apk.md`.

Per aggiornare Cloudflare: modificare e verificare i sorgenti, eseguire `python scripts/package-cloudflare.py`, aprire il progetto esistente, More actions → Create deployment, selezionare Production, caricare lo ZIP e usare Save and deploy. Verificare l’esito Success e il sito pubblico. I file locali e l’APK restano separati dalla pubblicazione web.
