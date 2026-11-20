# Pubblicazione eseguita e repository GitHub

Questa è la ricostruzione dei passaggi eseguiti, aggiornata al 20 novembre 2026. Non è un nuovo deploy. Il sito corrente è [Tabata Timer](https://tabata-timer-ek1.pages.dev); il repository è [zRoby03/tabata-timer](https://github.com/zRoby03/tabata-timer).

## Prima pubblicazione e passaggio a Cloudflare

Il progetto era inizialmente locale. L'utente ha autorizzato la pubblicazione con «andiamo». La prima pubblicazione ha usato Sites e ha restituito [tabata-timer.paddedlotus8.chatgpt.site](https://tabata-timer.paddedlotus8.chatgpt.site). Il progetto di quella pubblicazione era separato in `sites/tabata-timer`, con identità di hosting propria. Non è un'identità Cloudflare e non è stata riciclata per il nuovo servizio.

Alla richiesta esplicita di Cloudflare si è preparato un pacchetto statico, si è usato l'account Cloudflare dell'utente e si è creato il progetto Pages **tabata-timer** tramite Direct Upload. Il servizio ha assegnato **tabata-timer-ek1.pages.dev**, con HTTPS e sottodominio gratuito. Non sono stati acquistati un dominio, un piano o uno storage. Il vecchio sito resta con la precedente versione per consentire anche l'esportazione dei dati.

Il report selezionato della pubblicazione Cloudflare è in [docs/verifiche/cloudflare-pubblicazione.json](../verifiche/cloudflare-pubblicazione.json). Il caricamento è distinto dal salvataggio Git: il repository non esisteva come sorgente di un deploy automatico e non è stato collegato successivamente a una pipeline.

## Che cosa contiene il pacchetto

`web/` contiene i 42 file pubblici della consegna: documenti HTML, CSS, moduli JS, manifest, icone, audio, service worker, `ads.txt`, Informazioni e Privacy. `scripts/package-cloudflare.py` prepara `output/cloudflare/Tabata Timer.zip` con questi file più `hosting/cloudflare/_headers`, per 43 file. `index.html` si trova nella radice dello ZIP, non in una cartella `web` annidata.

Lo script controlla integrità ZIP e corrispondenza dei file. Non include test, SDK, APK, account, credenziali, database o backup personali. Il sito non ha un comando di build da eseguire sul server: Cloudflare serve i file già pronti.

`_headers` aggiunge header delle risposte statiche e rivalidazione del service worker. La CSP è già nei documenti e limita le risorse. Il service worker è stato corretto per riconoscere gli URL puliti `/about` e `/privacy`, con o senza query, anche offline. Senza questa correzione una navigazione informativa poteva ricevere la pagina del timer dalla cache.

## Ripetere un aggiornamento autorizzato

Questi sono i passaggi usati; non sono stati eseguiti nuovamente per il branch didattico:

1. Nel ramo applicativo completare modifiche, test, controllo visivo e backup degli eventuali dati da trasferire.
2. Eseguire `python scripts/package-cloudflare.py` e controllare lo ZIP/report in `output/cloudflare`.
3. Nel pannello Cloudflare aprire Workers & Pages e il progetto esistente `tabata-timer`.
4. More actions → Create deployment → Production; caricare `Tabata Timer.zip`.
5. Usare Save and deploy e attendere l'esito Success. Non creare un altro progetto o acquistare un servizio per aggiornare quello esistente.
6. Aprire l'indirizzo pubblico e verificare timer, entrambe le modalità, tre lingue, salvataggi, pagine informative, layout mobile e offline dopo caricamento completo.

Direct Upload e integrazione Git sono modalità di creazione differenti del progetto Pages. Qui è rimasto Direct Upload. Nessun push su `main` o `documentazione` pubblica automaticamente. Il codice annotato serve allo studio e non richiede un deploy di produzione.

Il passaggio a un'altra origine non sposta localStorage: esportare il JSON dal vecchio indirizzo e importarlo nel nuovo con anteprima/conferma. L'esportazione comprende programmi e attività, non la cache del sito o le preferenze del browser.

## AdSense: operazioni fatte e stato osservato

L'utente ha fornito e autorizzato il contatto pubblico **RobertStruzzi, robert.struzzi03@gmail.com**. Non sono stati ricavati automaticamente altri contatti dagli account. Sono state aggiunte Informazioni e Privacy nelle tre lingue, accessibili dalle Impostazioni, e la riga autorizzata di `ads.txt`.

Nell'account AdSense sono stati aggiunti il sito, la richiesta di revisione e una sola unità display adattabile **Tabata Timer**. I riferimenti pubblici sono publisher `pub-9043574024513063`, client `ca-pub-9043574024513063`, slot `9553674851`. Questi sono identificatori pubblici dell'integrazione, non token di accesso. Lo snippet è conservato in `hosting/cloudflare/adsense-snippet.html`, fuori da `web` e dallo ZIP pubblico.

È stato pubblicato nell'account il messaggio europeo Google con italiano predefinito, inglese/spagnolo, logo e URL privacy, azioni Acconsento/Gestisci/Nega, rifiuto in tutte le regioni configurate, ottimizzazione OFF e interesse legittimo non attivo per default. Gli annunci automatici sono OFF. L'utente ha compilato direttamente in Google le informazioni del publisher; nel progetto non sono stati copiati documenti, indirizzi privati o dati di pagamento.

L'ultimo stato osservato era **In fase di preparazione / Revisione richiesta**, con `ads.txt` **Autorizzato** e conferma di completamento dei passi del publisher. Questo non costituisce approvazione del sito o avvio degli annunci. `hosting/cloudflare/adsense.json` registra `servingEnabled: false`. Il messaggio pubblicato nel pannello non viene mostrato sul sito senza l'integrazione dei relativi tag.

## Integrazione pubblicitaria ancora da completare

Dopo esito positivo Google, l'implementazione prevista è un solo spazio manuale su Informazioni, lontano dai controlli. Nessun annuncio nella schermata del conto alla rovescia, overlay, vignetta o ancoraggio. La CMP Google pubblicata deve essere collegata alle pagine e deve permettere di riaprire le scelte; non basta un banner artigianale. La disponibilità dell'API CMP non equivale a consenso già espresso.

Occorre adeguare la CSP della sola pagina interessata con una configurazione coerente con il codice Google: incollare lo snippet nell'attuale pagina non basta, perché gli script esterni sono bloccati. Per una pagina statica sono da valutare hash/strict-dynamic, oppure nonce per risposta se si introduce un'infrastruttura che lo generi; non un nonce fisso condiviso. Poi aggiornare l'informativa ai trattamenti effettivi e verificare consenso, rifiuto, revoca, richieste terze parti, CSP, mobile e assenza di annunci nel timer. Non eseguire clic di prova sugli annunci reali.

Questi passaggi sono documentati come **lavoro pendente**, non come codice già aggiunto. La descrizione non promette approvazione o ricavi. I riferimenti ufficiali consultati per la predisposizione sono elencati in [cloudflare-adsense-apk.md](../cloudflare-adsense-apk.md); requisiti e interfacce andranno ricontrollati quando si riprenderà l'attivazione.

## APK

Il sito e il repository non ospitano un download pubblico dell'APK. `output/Tabata Timer.apk` è rimasto locale per il collaudo. Pubblicare sorgenti Android non pubblica quel binario. Una futura distribuzione richiede la scelta del canale e una release firmata con chiave conservata fuori da Git, con gestione degli aggiornamenti e del trasferimento da build debug. Le alternative già analizzate sono nella guida Cloudflare/AdSense/APK, non sono state attivate in questa operazione.

## Creazione di GitHub e rami

Il connettore disponibile era collegato a `ch4tty25`; l'utente ha scelto invece **zRoby03**, repository pubblico e branch esattamente **documentazione**. È stata verificata la sessione browser dell'account richiesto e creato `tabata-timer` senza inizializzazione remota di file in conflitto. La visibilità pubblica era esplicitamente autorizzata.

Prima del commit sono stati rivisti `.gitignore` e `.gitattributes`, selezionati i report non sensibili e scanditi i sorgenti per credenziali. APK/AAB, chiavi di firma, variabili ambiente, impostazioni SDK, cache, log, output e cartelle temporanee sono esclusi. Non è stata aggiunta una licenza d'uso inventata: pubblico descrive la visibilità, non assegna da solo una licenza al codice o alle risorse.

I passaggi Git principali, dopo preparazione dei file, sono stati:

```powershell
git add .
git commit -m 'Importa Tabata Timer web e Android'
git remote add origin https://github.com/zRoby03/tabata-timer.git
git push -u origin main
git switch -c documentazione
# Aggiunta delle annotazioni, delle guide e dei tool; poi verifiche.
git add .
git commit -m 'Documenta sorgenti web, Kotlin e pubblicazione'
git push -u origin documentazione
```

L'autenticazione del push usa il gestore credenziali Git locale. Nessuna password o chiave è scritta negli URL remoti o nei comandi. Il commit di importazione originale è `5d18d6d633e8fd8c8241aed078246f1953b084ef`. Le verifiche del branch e il push finale sono riepilogati in [verifiche.md](verifiche.md).
